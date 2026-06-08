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

  test('includes the community route', () => {
    expect(navLinks).toEqual(
      expect.arrayContaining([
        expect.objectContaining({
          label: 'Cộng đồng',
          to: '/community',
        }),
      ]),
    )
  })
})
