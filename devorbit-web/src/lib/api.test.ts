import { describe, expect, test } from 'vitest'
import { buildApiUrl } from './api'

describe('buildApiUrl', () => {
  test('does not duplicate api prefix when base and path both include it', () => {
    expect(buildApiUrl('/api', '/api/discovery/top-stacks')).toBe('/api/discovery/top-stacks')
  })

  test('joins non-duplicated base and path with one slash', () => {
    expect(buildApiUrl('http://localhost:8080', '/api/courses')).toBe('http://localhost:8080/api/courses')
  })
})
