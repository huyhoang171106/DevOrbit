// @vitest-environment jsdom
import '@testing-library/jest-dom/vitest'
import { render, screen } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest'
import { evaluateRepository } from '../../lib/repoEvaluation'
import type { RepoAnalysisResult } from '../../lib/repoAnalysisService'
import type { RepoSummary } from '../../types/api'
import { RepoAiAnalysisSection } from './RepoAiAnalysisSection'

type RepoFixture = RepoSummary & {
  updatedAt?: string | null
}

function repo(overrides: Partial<RepoFixture> = {}): RepoFixture {
  return {
    id: 1,
    displayName: 'sample-repo',
    description: 'Sample repo',
    githubUrl: 'https://github.com/example/sample-repo',
    primaryLanguage: 'TypeScript',
    stars: 0,
    techStacks: [],
    courseId: null,
    courseCode: null,
    courseName: null,
    ...overrides,
  }
}

function analysisFor(targetRepo: RepoSummary): RepoAnalysisResult {
  return {
    repoId: targetRepo.id,
    source: 'rule-based',
    evaluation: evaluateRepository(targetRepo),
    sections: [],
    generatedAt: '2026-05-29T00:00:00Z',
    fallbackUsed: false,
  }
}

describe('RepoAiAnalysisSection last activity metadata', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    vi.setSystemTime(new Date('2026-05-29T12:00:00Z'))
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  test('shows Vietnamese relative time from lastPushedAt without raw ISO text', () => {
    const targetRepo = repo({ lastPushedAt: '2026-04-20T10:00:00Z' })

    render(<RepoAiAnalysisSection repo={targetRepo} analysis={analysisFor(targetRepo)} loading={false} error={null} />)

    expect(screen.getByText('Cập nhật lần cuối')).toBeInTheDocument()
    expect(screen.getAllByText('1 tháng trước').length).toBeGreaterThan(0)
    expect(screen.queryByText('2026-04-20T10:00:00Z')).not.toBeInTheDocument()
    expect(screen.queryByText('Chưa có dữ liệu cập nhật')).not.toBeInTheDocument()
  })

  test('prefers lastPushedAt over updatedAt when both fields are present', () => {
    const targetRepo = repo({
      lastPushedAt: '2026-04-20T10:00:00Z',
      updatedAt: 'not-a-date',
    })

    render(<RepoAiAnalysisSection repo={targetRepo} analysis={analysisFor(targetRepo)} loading={false} error={null} />)

    expect(screen.getAllByText('1 tháng trước').length).toBeGreaterThan(0)
    expect(screen.queryByText('Chưa có dữ liệu cập nhật')).not.toBeInTheDocument()
  })

  test('always shows a time label when activity date is missing', () => {
    const targetRepo = repo({ lastPushedAt: null })

    render(<RepoAiAnalysisSection repo={targetRepo} analysis={analysisFor(targetRepo)} loading={false} error={null} />)

    expect(screen.getByText('Hôm nay')).toBeInTheDocument()
    expect(screen.queryByText('Chưa có dữ liệu cập nhật')).not.toBeInTheDocument()
    expect(screen.queryByText('Chưa rõ')).not.toBeInTheDocument()
  })
})
