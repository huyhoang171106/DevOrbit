import { describe, expect, test } from 'vitest'
import { cleanText, normalizeStringList } from './repoText'

describe('cleanText', () => {
  test('collapses whitespace and returns null for empty input', () => {
    expect(cleanText('  React   course\nAPI  ')).toBe('React course API')
    expect(cleanText('   ')).toBeNull()
    expect(cleanText(null)).toBeNull()
  })
})

describe('normalizeStringList', () => {
  test('deduplicates cleaned array values while preserving first occurrence order', () => {
    expect(normalizeStringList([' React ', 'Spring', 'React', '', ' spring '])).toEqual([
      'React',
      'Spring',
      'spring',
    ])
  })

  test('splits delimited strings and removes empty values', () => {
    expect(normalizeStringList('React, Spring Boot; PostgreSQL|')).toEqual([
      'React',
      'Spring Boot',
      'PostgreSQL',
    ])
  })
})
