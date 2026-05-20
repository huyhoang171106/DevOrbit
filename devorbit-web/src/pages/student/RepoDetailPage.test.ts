// @vitest-environment jsdom
import { afterEach, describe, expect, test, vi } from 'vitest'
import { hydrateLastPushedAt, parseGithubSlug } from './RepoDetailPage'
import type { RepoSummary } from '../../types/api'

function repo(overrides: Partial<RepoSummary> = {}): RepoSummary {
  return {
    id: 1,
    displayName: 'sample',
    description: 'Sample repo',
    githubUrl: 'https://github.com/example/sample',
    primaryLanguage: 'TypeScript',
    stars: 0,
    techStacks: [],
    courseId: null,
    courseCode: null,
    courseName: null,
    ...overrides,
  }
}

describe('RepoDetailPage GitHub activity fallback', () => {
  afterEach(() => {
    vi.restoreAllMocks()
    window.localStorage.clear()
  })

  test('parses GitHub owner and repo from URL', () => {
    expect(parseGithubSlug('https://github.com/example/sample.git')).toEqual({
      owner: 'example',
      name: 'sample',
    })
  })

  test('keeps API lastPushedAt when backend already provides it', async () => {
    const fetchSpy = vi.spyOn(globalThis, 'fetch')
    const hydrated = await hydrateLastPushedAt(repo({ lastPushedAt: '2026-04-20T10:00:00Z' }))

    expect(hydrated.lastPushedAt).toBe('2026-04-20T10:00:00Z')
    expect(fetchSpy).not.toHaveBeenCalled()
  })

  test('fetches latest commit date when API response has no lastPushedAt', async () => {
    vi.spyOn(globalThis, 'fetch')
      .mockResolvedValueOnce(response({ default_branch: 'main', pushed_at: '2026-03-01T00:00:00Z' }))
      .mockResolvedValueOnce(response([
        { commit: { committer: { date: '2026-04-20T10:00:00Z' }, author: { date: '2026-04-19T10:00:00Z' } } },
      ]))

    const hydrated = await hydrateLastPushedAt(repo())

    expect(hydrated.lastPushedAt).toBe('2026-04-20T10:00:00Z')
  })

  test('falls back to pushed_at when commits API fails', async () => {
    vi.spyOn(globalThis, 'fetch')
      .mockResolvedValueOnce(response({ default_branch: 'main', pushed_at: '2026-03-01T00:00:00Z' }))
      .mockResolvedValueOnce(response({ message: 'rate limit' }, false))

    const hydrated = await hydrateLastPushedAt(repo())

    expect(hydrated.lastPushedAt).toBe('2026-03-01T00:00:00Z')
  })

  test('uses cached GitHub activity date after reload when API still has no lastPushedAt', async () => {
    vi.spyOn(globalThis, 'fetch')
      .mockResolvedValueOnce(response({ default_branch: 'main', pushed_at: '2026-03-01T00:00:00Z' }))
      .mockResolvedValueOnce(response([
        { commit: { committer: { date: '2026-04-20T10:00:00Z' }, author: { date: '2026-04-19T10:00:00Z' } } },
      ]))

    const firstLoad = await hydrateLastPushedAt(repo())
    expect(firstLoad.lastPushedAt).toBe('2026-04-20T10:00:00Z')

    vi.restoreAllMocks()
    const fetchSpy = vi.spyOn(globalThis, 'fetch')

    const reloaded = await hydrateLastPushedAt(repo())

    expect(reloaded.lastPushedAt).toBe('2026-04-20T10:00:00Z')
    expect(fetchSpy).not.toHaveBeenCalled()
  })
})

function response(body: unknown, ok = true): Response {
  return {
    ok,
    status: ok ? 200 : 403,
    json: () => Promise.resolve(body),
  } as Response
}

