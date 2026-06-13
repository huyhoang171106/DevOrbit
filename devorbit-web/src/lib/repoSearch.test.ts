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

  test('ranks exact phrase matches before related keyword matches', () => {
    const results = searchCourses([
      course({ id: 1, name: 'Nhập môn Công nghệ phần mềm' }),
      course({ id: 2, code: 'IT001', name: 'Nhập học môn Công nghệ phần mềm' }),
    ], 'nhập môn')

    expect(results.map((result) => result.id)).toEqual([1, 2])
    expect(results[0]._score).toBeGreaterThan(results[1]._score)
  })

  test('locks a complete course title to exact matches only', () => {
    const courses = [
      course({ id: 1, name: 'Nhập môn Công nghệ phần mềm' }),
      course({ id: 2, code: 'IT001', name: 'Nhập môn lập trình' }),
      course({ id: 3, code: 'SE100', name: 'Công nghệ phần mềm' }),
    ]

    expect(hasExactCourseMatch(courses, 'nhap mon cong nghe phan mem')).toBe(true)
    expect(searchCourses(courses, 'nhập môn công nghệ phần mềm').map((result) => result.id)).toEqual([1])
  })

  test('keeps discovery broad until the complete title is entered', () => {
    const courses = [
      course({ id: 1, name: 'Nhập môn Công nghệ phần mềm' }),
      course({ id: 2, code: 'IT001', name: 'Nhập môn lập trình' }),
    ]

    expect(hasExactCourseMatch(courses, 'nhập môn')).toBe(false)
    expect(searchCourses(courses, 'nhập môn').map((result) => result.id)).toEqual([1, 2])
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
