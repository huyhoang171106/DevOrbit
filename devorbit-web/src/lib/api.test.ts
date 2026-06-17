// @vitest-environment jsdom

import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { apiStudentGet, apiStudentPost, apiStudentUpload, buildApiUrl } from './api'
import { getStudentToken, saveStudentToken } from './auth'

describe('api helpers', () => {
  const preventRedirect = (event: Event) => event.preventDefault()

  beforeEach(() => {
    localStorage.clear()
    vi.restoreAllMocks()
    window.addEventListener('devorbit:auth-redirect', preventRedirect)
  })

  afterEach(() => {
    window.removeEventListener('devorbit:auth-redirect', preventRedirect)
  })

  it('clears stale student token on 401 responses', async () => {
    saveStudentToken('stale-token')
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(
      new Response(JSON.stringify({ error: 'expired' }), { status: 401 }),
    ))

    await expect(apiStudentGet('/api/student/me')).rejects.toThrow('expired')

    expect(getStudentToken()).toBeNull()
  })

  it('uploads student files with the student token and API base URL handling', async () => {
    saveStudentToken('student-token')
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(
      new Response(JSON.stringify({ id: 1 }), { status: 200 }),
    ))
    const formData = new FormData()
    formData.append('file', new Blob(['avatar']), 'avatar.png')

    await apiStudentUpload('/api/student/me/avatar/upload', formData)

    expect(fetch).toHaveBeenCalledWith(buildApiUrl('', '/api/student/me/avatar/upload'), {
      method: 'POST',
      headers: { Authorization: 'Bearer student-token' },
      body: formData,
    })
  })

  it('sends student tokens and clears stale credentials for protected AI endpoints', async () => {
    saveStudentToken('student-token')
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(
      new Response(JSON.stringify({ error: 'expired' }), { status: 401 }),
    ))

    await expect(apiStudentPost('/api/ai/generate-roadmap', { learningGoals: 'AI', careerPath: 'Backend' }))
      .rejects.toThrow('expired')

    expect(fetch).toHaveBeenCalledWith(buildApiUrl('', '/api/ai/generate-roadmap'), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer student-token',
      },
      body: JSON.stringify({ learningGoals: 'AI', careerPath: 'Backend' }),
    })
    expect(getStudentToken()).toBeNull()
  })
})
