import { describe, expect, test, vi } from 'vitest'
import { buildRepoAiAnalysisSections } from './repoAiAnalysis'
import type { RepoSummary } from '../types/api'

type RepoSummaryWithOptionalMetadata = RepoSummary & {
  topics?: string[] | string | null
  forks?: number | null
  updatedAt?: string | null
  lastPushedAt?: string | null
  readmeExcerpt?: string | null
  deadline?: string | null
}

function repo(overrides: Partial<RepoSummaryWithOptionalMetadata> = {}): RepoSummaryWithOptionalMetadata {
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
      'readmeInsights',
      'reviewFirst',
      'strategy',
      'nextSteps',
      'warnings',
    ])
    expect(sections.find((section) => section.key === 'overview')?.content).toContain('REST API for course management')
    expect(sections.find((section) => section.key === 'technology')?.items).toContain('Tech stack đang có: Spring Boot, PostgreSQL.')
    expect(sections.find((section) => section.key === 'fit')?.items).toContain('Mức độ phù hợp học tập: vừa.')
    expect(sections.find((section) => section.key === 'readmeInsights')?.content).toContain('Chưa có README để phân tích sâu')
  })

  test('uses optional readme and topics when they are available', () => {
    const sections = buildRepoAiAnalysisSections(repo({
      topics: ['course-management', 'spring-security'],
      forks: 4,
      updatedAt: '2026-05-20T10:00:00Z',
      readmeExcerpt: 'Course API helps students manage courses and enrollments. Setup: mvn spring-boot:run. See src/controllers and pom.xml for the main flow.',
    }))

    expect(sections.find((section) => section.key === 'overview')?.items).toContain(
      'Topics/tags gợi ý phạm vi: course-management, spring-security.',
    )
    expect(sections.find((section) => section.key === 'fit')?.items).toContain('Mức độ phù hợp học tập: cao.')
    expect(sections.find((section) => section.key === 'reviewFirst')?.items?.[0]).toContain('README: Course API helps students')
    expect(sections.find((section) => section.key === 'readmeInsights')?.content).toContain('Course API helps students manage courses')
    expect(sections.find((section) => section.key === 'readmeInsights')?.items).toContain('Lệnh setup/run phát hiện: mvn spring-boot:run.')
    expect(sections.find((section) => section.key === 'readmeInsights')?.items).toContain('README nhắc tới phần nên đọc/kiểm tra: thư mục src, pom.xml, controller/API layer.')
    expect(sections.find((section) => section.key === 'nextSteps')?.items).toContain('Forks hiện có: 4; có thể tham khảo mức độ được tái sử dụng.')
  })

  test('prefers lastPushedAt over invalid updatedAt for activity signals', () => {
    vi.useFakeTimers()
    vi.setSystemTime(new Date('2026-05-20T10:00:00Z'))

    const sections = buildRepoAiAnalysisSections(repo({
      updatedAt: 'not-a-date',
      lastPushedAt: '2026-04-20T10:00:00Z',
    }))

    expect(sections.find((section) => section.key === 'reviewFirst')?.items).toContain(
      'Lịch sử cập nhật gần nhất: 1 tháng trước.',
    )
    expect(sections.find((section) => section.key === 'warnings')?.items ?? []).not.toContain(
      'Chưa có updatedAt/lastPushedAt nên chưa biết repo còn được duy trì gần đây hay không.',
    )

    vi.useRealTimers()
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
    expect(sections.find((section) => section.key === 'overview')?.content).toContain('Chưa đủ dữ liệu để phân tích sâu')
    expect(sections.find((section) => section.key === 'fit')?.items).toContain('Mức độ phù hợp học tập: thấp.')
    expect(warnings?.items).toEqual(expect.arrayContaining([
      'Thiếu mô tả nên chưa xác định chắc repo đang giải quyết bài toán gì.',
      'Chưa rõ công nghệ chính vì thiếu primaryLanguage.',
      'Thiếu techStacks nên cần mở source/config để xác nhận framework và dependency.',
      'Thiếu topics/tags nên khó nhận diện domain hoặc mục tiêu repo từ metadata.',
      'Thiếu README excerpt; hãy đọc README trên GitHub trước khi dùng repo cho deadline.',
      'Thiếu courseCode/courseName nên chưa đánh giá được mức độ khớp với môn học UIT cụ thể.',
      'Chưa có forks để tham khảo mức độ được tái sử dụng.',
      'Chưa có updatedAt/lastPushedAt nên chưa biết repo còn được duy trì gần đây hay không.',
    ]))
  })
})
