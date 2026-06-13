import { describe, expect, test } from 'vitest'
import { searchCourses, searchRepos, tokenizeSearchQuery } from './repoSearch'
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
})
