// @vitest-environment jsdom

import '@testing-library/jest-dom/vitest'
import { act, cleanup, fireEvent, render, screen } from '@testing-library/react'
import { useState, type ReactNode } from 'react'
import { Link, MemoryRouter, Route, Routes, useLocation, useNavigate } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest'
import type { CourseSummary, RepoSummary } from '../../types/api'
import { CourseListPage } from './CourseListPage'

const courses: CourseSummary[] = Array.from({ length: 31 }, (_, index) => ({
  id: index + 1,
  code: `COURSE-${String(index + 1).padStart(2, '0')}`,
  name: index === 0 ? 'Nhập môn Công nghệ phần mềm' : `Course ${index + 1}`,
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

const apiGetMock = vi.hoisted(() => vi.fn())

vi.mock('../../lib/api', () => ({
  apiGet: apiGetMock,
}))

function CourseDetailStub() {
  const location = useLocation()
  const navigate = useNavigate()
  const courseListPath = typeof location.state?.courseListPath === 'string'
    ? location.state.courseListPath
    : '/courses'

  return (
    <>
      <button onClick={() => navigate(-1)}>Back to courses</button>
      <Link to={courseListPath}>Danh mục</Link>
    </>
  )
}

function LocationProbe() {
  const location = useLocation()
  return <output aria-label="current-location">{`${location.pathname}${location.search}`}</output>
}

beforeEach(() => {
  apiGetMock.mockResolvedValue([])
})

afterEach(() => {
  cleanup()
  vi.useRealTimers()
  apiGetMock.mockReset()
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

  test('restores the selected page after opening a course and navigating back', () => {
    render(
      <MemoryRouter initialEntries={['/courses?page=2']}>
        <LocationProbe />
        <Routes>
          <Route path="/courses" element={<CourseListPage />} />
          <Route path="/courses/:courseId" element={<CourseDetailStub />} />
        </Routes>
      </MemoryRouter>,
    )

    expect(screen.getByText('COURSE-31')).toBeInTheDocument()
    expect(screen.getByLabelText('current-location')).toHaveTextContent('/courses?page=2')

    fireEvent.click(screen.getByText('COURSE-31'))
    fireEvent.click(screen.getByRole('button', { name: 'Back to courses' }))

    expect(screen.getByText('COURSE-31')).toBeInTheDocument()
    expect(screen.getByLabelText('current-location')).toHaveTextContent('/courses?page=2')
  })

  test('restores the selected page from the course detail category link', () => {
    render(
      <MemoryRouter initialEntries={['/courses?page=2']}>
        <LocationProbe />
        <Routes>
          <Route path="/courses" element={<CourseListPage />} />
          <Route path="/courses/:courseId" element={<CourseDetailStub />} />
        </Routes>
      </MemoryRouter>,
    )

    fireEvent.click(screen.getByText('COURSE-31'))
    fireEvent.click(screen.getByRole('link', { name: 'Danh mục' }))

    expect(screen.getByText('COURSE-31')).toBeInTheDocument()
    expect(screen.getByLabelText('current-location')).toHaveTextContent('/courses?page=2')
  })

  test('submits the complete query immediately with Enter', () => {
    render(
      <MemoryRouter>
        <CourseListPage />
      </MemoryRouter>,
    )

    const input = screen.getByRole('searchbox')
    fireEvent.change(input, { target: { value: 'nhập môn' } })
    fireEvent.keyDown(input, { key: 'Enter' })

    expect(screen.getByText('COURSE-01')).toBeInTheDocument()
    expect(screen.queryByText('COURSE-02')).not.toBeInTheDocument()
  })

  test('shows only the exact course after the complete title is entered', () => {
    render(
      <MemoryRouter>
        <CourseListPage />
      </MemoryRouter>,
    )

    const input = screen.getByRole('searchbox')
    fireEvent.change(input, { target: { value: 'nhap mon cong nghe phan mem' } })
    fireEvent.keyDown(input, { key: 'Enter' })

    expect(screen.getByText('COURSE-01')).toBeInTheDocument()
    expect(screen.queryByText('COURSE-02')).not.toBeInTheDocument()
    expect(screen.getByText('1 kết quả')).toBeInTheDocument()
  })

  test('updates relevant results while the user is typing', () => {
    vi.useFakeTimers()
    render(
      <MemoryRouter>
        <CourseListPage />
      </MemoryRouter>,
    )

    fireEvent.change(screen.getByRole('searchbox'), { target: { value: 'nhập' } })
    act(() => {
      vi.advanceTimersByTime(200)
    })

    expect(screen.getByText('COURSE-01')).toBeInTheDocument()
    expect(screen.queryByText('COURSE-02')).not.toBeInTheDocument()
  })

  test('opens the repo detail route from repository search results', async () => {
    const repos: RepoSummary[] = [
      {
        id: 77,
        displayName: 'devorbit-sample-repo',
        description: 'Sample repository for course work',
        githubUrl: 'https://github.com/example/devorbit-sample-repo',
        primaryLanguage: 'TypeScript',
        stars: 12,
        techStacks: ['React'],
        courseId: 1,
        courseCode: 'COURSE-01',
        courseName: 'Nhap mon Cong nghe phan mem',
      },
    ]
    apiGetMock.mockResolvedValueOnce(repos)

    render(
      <MemoryRouter initialEntries={['/courses']}>
        <LocationProbe />
        <Routes>
          <Route path="/courses" element={<CourseListPage />} />
          <Route path="/repos/:repoId" element={<div>Repo detail loaded</div>} />
        </Routes>
      </MemoryRouter>,
    )

    fireEvent.change(screen.getByRole('searchbox'), { target: { value: 'devorbit sample' } })

    const repoLink = await screen.findByRole('link', { name: /devorbit-sample-repo/i })
    expect(repoLink).toHaveAttribute('href', '/repos/77')

    fireEvent.click(repoLink)

    expect(screen.getByLabelText('current-location')).toHaveTextContent('/repos/77')
    expect(screen.getByText('Repo detail loaded')).toBeInTheDocument()
  })
})
