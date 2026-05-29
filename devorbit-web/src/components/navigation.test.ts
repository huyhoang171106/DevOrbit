import { describe, expect, test } from 'vitest'
import { navLinks } from './navigation'

describe('navigation links', () => {
  test('includes the GPA calculator route', () => {
    expect(navLinks).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          label: 'Tính GPA',
          to: '/gpa-calculator',
        }),
      ]),
    )
  })
})
