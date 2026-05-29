import { describe, expect, test } from 'vitest'
import { analyzeRepository } from './repoAnalysisService'
import type { RepositoryAnalysisProvider } from './repoAnalysisService'
import type { RepoSummary } from '../types/api'

const repo: RepoSummary = {
  id: 1,
  displayName: 'react-student-portal',
  description: 'Student portal UI',
  githubUrl: 'https://github.com/example/react-student-portal',
  primaryLanguage: 'TypeScript',
  stars: 5,
  techStacks: ['React', 'Vite'],
  courseId: 2,
  courseCode: 'SE104',
  courseName: 'Nhap mon cong nghe phan mem',
}

describe('analyzeRepository', () => {
  test('uses rule-based analysis when no AI provider is configured', async () => {
    const result = await analyzeRepository(repo)

    expect(result.source).toBe('rule-based')
    expect(result.fallbackUsed).toBe(false)
    expect(result.sections.map((section) => section.key)).toContain('overview')
  })

  test('falls back to rule-based analysis when a provider fails', async () => {
    const provider: RepositoryAnalysisProvider = {
      analyzeRepository: async () => {
        throw new Error('provider unavailable')
      },
    }

    const result = await analyzeRepository(repo, { provider })

    expect(result.source).toBe('rule-based')
    expect(result.fallbackUsed).toBe(true)
    expect(result.errorMessage).toBe('provider unavailable')
    expect(result.sections.length).toBeGreaterThan(0)
  })
})
