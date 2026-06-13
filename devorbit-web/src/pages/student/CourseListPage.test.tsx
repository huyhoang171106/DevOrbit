// @vitest-environment jsdom

import '@testing-library/jest-dom/vitest'
import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import { useState, type ReactNode } from 'react'
import { MemoryRouter } from 'react-router-dom'
import { afterEach, describe, expect, test, vi } from 'vitest'
import type { CourseSummary } from '../../types/api'
import { CourseListPage } from './CourseListPage'

const courses: CourseSummary[] = Array.from({ length: 31 }, (_, index) => ({
  id: index + 1,
  code: `COURSE-${String(index + 1).padStart(2, '0')}`,
  name: `Course ${index + 1}`,
  repoCount: 31 - index,
}))

vi.mock('../../hooks/useCourseList', () => ({
  useCourseList: () => ({
    data: courses,
    isLoading: false,
    error: null,
  }),
}))

vi.mock('../../motion/primitives/StaggerReveal', () => ({
  StaggerReveal: ({ children }: { children: ReactNode }) => {
    const [revealedChildren] = useState(children)
    return <div>{revealedChildren}</div>
  },
  StaggerItem: ({ children }: { children: ReactNode }) => <div>{children}</div>,
}))

vi.mock('../../motion/primitives/BlurReveal', () => ({
  BlurReveal: ({ children }: { children: ReactNode }) => <div>{children}</div>,
}))

vi.mock('../../motion/primitives/FadeReveal', () => ({
  FadeReveal: ({ children }: { children: ReactNode }) => <div>{children}</div>,
}))

vi.mock('../../motion/primitives/SectionTransition', () => ({
  SectionTransition: ({ children }: { children: ReactNode }) => <div>{children}</div>,
}))

vi.mock('../../motion/primitives/ParallaxLayer', () => ({
  ParallaxLayer: ({ children }: { children: ReactNode }) => <div>{children}</div>,
}))

afterEach(() => {
  cleanup()
})

describe('CourseListPage pagination', () => {
  test('reveals course cards after moving beyond the first page', () => {
    render(
      <MemoryRouter>
        <CourseListPage />
      </MemoryRouter>,
    )

    expect(screen.getByText('COURSE-01')).toBeInTheDocument()
    expect(screen.queryByText('COURSE-31')).not.toBeInTheDocument()

    fireEvent.click(screen.getByRole('button', { name: '2' }))

    expect(screen.queryByText('COURSE-01')).not.toBeInTheDocument()
    expect(screen.getByText('COURSE-31')).toBeInTheDocument()
  })
})
