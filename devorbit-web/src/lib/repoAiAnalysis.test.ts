import { describe, expect, test } from 'vitest'
import { buildRepoAiAnalysisSections } from './repoAiAnalysis'
import type { RepoSummary } from '../types/api'

function repo(overrides: Partial<RepoSummary> = {}): RepoSummary {
  return {
    id: 1,
    displayName: 'spring-course-api',
    description: 'REST API for course management',
    githubUrl: 'https://github.com/example/spring-course-api',
    primaryLanguage: 'Java',
    stars: 12,
    techStacks: ['Spring Boot', 'PostgreSQL'],
    courseId: 10,
    courseCode: 'SE104',
    courseName: 'Nhập môn công nghệ phần mềm',
    ...overrides,
  }
}

describe('buildRepoAiAnalysisSections', () => {
  test('builds required sections from repository metadata', () => {
    const sections = buildRepoAiAnalysisSections(repo())

    expect(sections.map((section) => section.key)).toEqual([
      'overview',
      'technology',
      'fit',
      'reviewFirst',
      'strategy',
      'nextSteps',
      'warnings',
    ])
    expect(sections.find((section) => section.key === 'overview')?.content).toContain('backend hoặc full-stack')
    expect(sections.find((section) => section.key === 'technology')?.items).toContain('Tech stack liên quan: Spring Boot, PostgreSQL.')
    expect(sections.find((section) => section.key === 'fit')?.content).toContain('SE104')
  })

  test('warns clearly when repository data is sparse', () => {
    const sections = buildRepoAiAnalysisSections(repo({
      description: '',
      primaryLanguage: '',
      techStacks: [],
      courseId: null,
      courseCode: null,
      courseName: null,
      stars: null,
    }))

    const warnings = sections.find((section) => section.key === 'warnings')
    expect(warnings?.items).toEqual(expect.arrayContaining([
      'Thiếu description nên chưa xác định chắc repo đang giải quyết bài toán gì.',
      'Thiếu primaryLanguage nên chưa thể suy luận chắc cách build/chạy.',
      'Thiếu techStacks nên phân tích công nghệ chỉ dựa trên metadata tối thiểu.',
      'Thiếu courseCode/courseName nên chưa đánh giá được mức độ khớp với môn học UIT cụ thể.',
      'Public repo detail hiện chưa có README excerpt, topics, forks hoặc last pushed date.',
    ]))
  })
})
