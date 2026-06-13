import { describe, expect, test } from 'vitest'
import { hasExactCourseMatch, searchCourses, searchRepos, tokenizeSearchQuery } from './repoSearch'
import type { CourseSummary, RepoSummary } from '../types/api'

function course(overrides: Partial<CourseSummary> = {}): CourseSummary {
  return {
    id: 1,
    code: 'SE104',
    name: 'Nhap mon Cong nghe phan mem',
    description: 'Course about software engineering basics',
    repoCount: 1,
    ...overrides,
  }
}

function repo(overrides: Partial<RepoSummary> = {}): RepoSummary {
  return {
    id: 1,
    displayName: 'spring-course-api',
    description: 'REST API for course management',
    githubUrl: 'https://github.com/example/spring-course-api',
    primaryLanguage: 'Java',
    stars: 12,
    techStacks: ['Spring Boot', 'PostgreSQL'],
    courseId: 1,
    courseCode: 'SE104',
    courseName: 'Nhap mon Cong nghe phan mem',
    ...overrides,
  }
}

describe('tokenizeSearchQuery', () => {
  test('normalizes accents, trims whitespace, and removes empty tokens', () => {
    expect(tokenizeSearchQuery('  Công   nghệ   phần mềm  ')).toEqual(['cong', 'nghe', 'phan', 'mem'])
  })
})

describe('repo search', () => {
  test('matches Vietnamese course names with or without accents', () => {
    const courses = [
      course({ id: 1, name: 'Nhập môn Công nghệ phần mềm' }),
      course({ id: 2, code: 'IT001', name: 'Nhập môn lập trình' }),
      course({ id: 3, code: 'IT002', name: 'Lập trình hướng đối tượng' }),
    ]

    expect(searchCourses(courses, 'nhập môn').map((result) => result.id)).toEqual([1, 2])
    expect(searchCourses(courses, 'nhap mon').map((result) => result.id)).toEqual([1, 2])
  })

  test('returns relevant results while each keyword is being typed', () => {
    const courses = [
      course({ id: 1, name: 'Nhập môn Công nghệ phần mềm' }),
      course({ id: 2, code: 'IT001', name: 'Nhập môn lập trình' }),
      course({ id: 3, code: 'IT002', name: 'Lập trình hướng đối tượng' }),
    ]

    expect(searchCourses(courses, 'nhập').map((result) => result.id)).toEqual([1, 2])
    expect(searchCourses(courses, 'nhập m').map((result) => result.id)).toEqual([1, 2])
  })

  test('keeps direct phrase matches focused when a phrase exists', () => {
    const results = searchCourses([
      course({ id: 1, name: 'Nhập môn Công nghệ phần mềm' }),
      course({ id: 2, code: 'IT001', name: 'Nhập học môn Công nghệ phần mềm' }),
    ], 'nhập môn')

    expect(results.map((result) => result.id)).toEqual([1])
  })

  test('ranks an exact course title first without hiding longer phrase matches', () => {
    const courses = [
      course({ id: 1, name: 'Nhập môn Công nghệ phần mềm' }),
      course({ id: 2, code: 'IT001', name: 'Nhập môn lập trình' }),
      course({ id: 3, code: 'SE100', name: 'Công nghệ phần mềm' }),
      course({ id: 4, code: 'SE200', name: 'Nhập môn Công nghệ phần mềm nâng cao' }),
    ]

    expect(hasExactCourseMatch(courses, 'nhap mon cong nghe phan mem')).toBe(true)
    expect(searchCourses(courses, 'nhập môn công nghệ phần mềm').map((result) => result.id)).toEqual([1, 4])
  })

  test('shows every course containing a phrase even when one course name is exact', () => {
    const courses = [
      course({ id: 1, code: 'IT001', name: 'Lập trình' }),
      course({ id: 2, code: 'IT002', name: 'Lập trình hướng đối tượng' }),
      course({ id: 3, code: 'NT001', name: 'Kỹ thuật lập trình' }),
      course({ id: 4, code: 'SE100', name: 'Công nghệ phần mềm' }),
    ]

    expect(searchCourses(courses, 'lập trình').map((result) => result.id)).toEqual([1, 2, 3])
  })

  test('keeps discovery broad until the complete title is entered', () => {
    const courses = [
      course({ id: 1, name: 'Nhập môn Công nghệ phần mềm' }),
      course({ id: 2, code: 'IT001', name: 'Nhập môn lập trình' }),
    ]

    expect(hasExactCourseMatch(courses, 'nhập môn')).toBe(false)
    expect(searchCourses(courses, 'nhập môn').map((result) => result.id)).toEqual([1, 2])
  })

  test('does not match courses from descriptions or expanded meanings', () => {
    const courses = [
      course({ id: 1, name: 'Nhập môn Công nghệ phần mềm' }),
      course({
        id: 2,
        code: 'CE101',
        name: 'Thiết kế luận lý số',
        description: 'Kiến thức nhập môn và nền tảng cơ bản',
      }),
    ]

    expect(searchCourses(courses, 'nhập môn').map((result) => result.id)).toEqual([1])
  })

  test('matches phrase words at word boundaries instead of inside unrelated words', () => {
    const courses = [
      course({ id: 1, code: 'ENG01', name: 'Anh văn 1' }),
      course({ id: 2, code: 'ML001', name: 'Phát triển và vận hành hệ thống máy học' }),
    ]

    expect(searchCourses(courses, 'anh văn').map((result) => result.id)).toEqual([1])
  })

  test('falls back to every directly matching keyword when no full phrase exists', () => {
    const courses = [
      course({ id: 1, code: 'SE401', name: 'Đồ án chuyên ngành' }),
      course({ id: 2, code: 'IT002', name: 'Lập trình Java' }),
      course({ id: 3, code: 'ML001', name: 'Phát triển và vận hành hệ thống máy học' }),
      course({ id: 4, code: 'IT004', name: 'Lập trình Javascript' }),
      course({ id: 5, code: 'SE405', name: 'Đổi mới sáng tạo' }),
    ]

    expect(searchCourses(courses, 'đồ án java').map((result) => result.id)).toEqual([1, 2])
  })

  test('uses prefix matching only while no complete keyword matches', () => {
    const courses = [
      course({ id: 1, code: 'IT002', name: 'Lập trình Java' }),
      course({ id: 2, code: 'IT004', name: 'Lập trình Javascript' }),
    ]

    expect(searchCourses(courses, 'jav').map((result) => result.id)).toEqual([1, 2])
    expect(searchCourses(courses, 'java').map((result) => result.id)).toEqual([1])
  })

  test('keeps phrase course search matching tokens in order', () => {
    const results = searchCourses([
      course({ id: 1, name: 'Nhap mon Cong nghe phan mem' }),
      course({ id: 2, code: 'IT001', name: 'Nhap mon lap trinh' }),
    ], 'cong nghe')

    expect(results.map((result) => result.id)).toEqual([1])
  })

  test('keeps phrase repo search matching repository names', () => {
    const results = searchRepos([
      repo({ id: 1, displayName: 'react-app-starter' }),
      repo({ id: 2, displayName: 'database-notes', readmeExcerpt: 'SQL lecture notes.' }),
    ], 'react app')

    expect(results.map((result) => result.id)).toEqual([1])
  })

  test('does not match repositories from descriptions, README, or file trees', () => {
    const results = searchRepos([
      repo({
        id: 1,
        displayName: 'digital-logic',
        description: 'Python exercises',
        readmeExcerpt: 'Python tutorial',
        fileTree: 'python/main.py',
        primaryLanguage: 'Verilog',
        techStacks: ['FPGA'],
      }),
    ], 'python')

    expect(results).toEqual([])
  })

  test('returns all matching repositories instead of truncating results', () => {
    const repos = Array.from({ length: 25 }, (_, index) => repo({
      id: index + 1,
      displayName: `python-learning-${index + 1}`,
      primaryLanguage: 'Python',
      techStacks: ['Python'],
    }))

    expect(searchRepos(repos, 'python')).toHaveLength(25)
  })
})
