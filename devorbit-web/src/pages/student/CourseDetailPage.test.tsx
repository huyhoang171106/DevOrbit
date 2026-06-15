// @vitest-environment jsdom

import '@testing-library/jest-dom/vitest'
import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import { useState, type ReactNode } from 'react'
import { MemoryRouter, Route, Routes, useLocation } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest'
import { apiGet } from '../../lib/api'
import type { CourseDetail } from '../../types/api'
import { CourseDetailPage } from './CourseDetailPage'

const course: CourseDetail = {
  id: 1,
  code: 'IT003',
  name: 'Cau truc du lieu va giai thuat',
  nameEn: null,
  description: null,
  credits: 4,
  theoryHours: 45,
  practiceHours: 30,
  subjectType: 'Bat buoc',
  isOpen: true,
  managementUnit: 'Khoa Khoa hoc May tinh',
  codeOld: null,
  equivalentMH: null,
  prerequisiteMH: null,
  previousMH: null,
  repos: [
    {
      id: 1,
      displayName: 'Java algorithms',
      description: '',
      githubUrl: 'https://github.com/example/java-algorithms',
      primaryLanguage: 'Java',
      stars: 0,
      techStacks: ['Java'],
      courseId: 1,
      courseCode: 'IT003',
      courseName: 'Cau truc du lieu va giai thuat',
    },
    ...Array.from({ length: 4 }, (_, index) => ({
      id: index + 2,
      displayName: `Python algorithms ${index + 1}`,
      description: '',
      githubUrl: `https://github.com/example/python-algorithms-${index + 1}`,
      primaryLanguage: 'Python',
      stars: 0,
      techStacks: ['Python'],
      courseId: 1,
      courseCode: 'IT003',
      courseName: 'Cau truc du lieu va giai thuat',
    })),
  ],
}

let revealMounts = 0

vi.mock('../../lib/api', () => ({
  apiGet: vi.fn(),
  apiStudentPost: vi.fn(),
  apiStudentGet: vi.fn(),
  apiStudentDelete: vi.fn(),
}))

vi.mock('../../lib/auth', () => ({
  isStudentAuthenticated: () => false,
}))

vi.mock('../../hooks/useCommunity', () => ({
  useCourseReviews: () => ({
    data: null,
    isLoading: false,
    refetch: vi.fn(),
  }),
}))

vi.mock('../../components/student/CourseKnowledgeGraph', () => ({
  CourseKnowledgeGraph: () => null,
}))

vi.mock('../../components/student/ReviewSection', () => ({
  ReviewSection: () => null,
}))

vi.mock('../../components/student/RepoCard', () => ({
  RepoCard: ({ repo }: { repo: { displayName: string } }) => <div>{repo.displayName}</div>,
}))

vi.mock('../../motion/primitives/StaggerReveal', () => ({
  StaggerReveal: ({ children }: { children: ReactNode }) => {
    const [mountId] = useState(() => ++revealMounts)
    return <div data-testid="repo-reveal" data-mount-id={mountId}>{children}</div>
  },
  StaggerItem: ({ children }: { children: ReactNode }) => <div>{children}</div>,
}))

vi.mock('../../motion/primitives/SectionTransition', () => ({
  SectionTransition: ({ children }: { children: ReactNode }) => <div>{children}</div>,
}))

vi.mock('../../motion/primitives/ParallaxLayer', () => ({
  ParallaxLayer: ({ children }: { children: ReactNode }) => <div>{children}</div>,
}))

beforeEach(() => {
  revealMounts = 0
  vi.mocked(apiGet).mockResolvedValue(course)
  localStorage.setItem('devorbit-course-1', JSON.stringify({ data: course, ts: Date.now() }))
})

afterEach(() => {
  cleanup()
  localStorage.clear()
})

function LocationProbe() {
  const location = useLocation()
  return <output aria-label="current-location">{`${location.pathname}${location.search}`}</output>
}

describe('CourseDetailPage repository filters', () => {
  test('reveals every repository immediately after changing tech stack filters', () => {
    render(
      <MemoryRouter initialEntries={['/courses/1']}>
        <Routes>
          <Route path="/courses/:courseId" element={<CourseDetailPage />} />
        </Routes>
      </MemoryRouter>,
    )

    expect(screen.getByTestId('repo-reveal')).toHaveAttribute('data-mount-id', '1')

    fireEvent.click(screen.getByRole('button', { name: 'Java' }))
    expect(screen.getByText('Java algorithms')).toBeInTheDocument()
    expect(screen.queryByText('Python algorithms 1')).not.toBeInTheDocument()
    expect(screen.getByTestId('repo-reveal')).toHaveAttribute('data-mount-id', '2')

    fireEvent.click(screen.getByRole('button', { name: 'Python' }))
    expect(screen.queryByText('Java algorithms')).not.toBeInTheDocument()
    expect(screen.getAllByText(/Python algorithms/)).toHaveLength(4)
    expect(screen.getByTestId('repo-reveal')).toHaveAttribute('data-mount-id', '3')
  })

  test('uses the originating course-list page for the category link', () => {
    render(
      <MemoryRouter initialEntries={[{
        pathname: '/courses/1',
        state: { courseListPath: '/courses?page=2' },
      }]}>
        <LocationProbe />
        <Routes>
          <Route path="/courses/:courseId" element={<CourseDetailPage />} />
          <Route path="/courses" element={<div>Course list</div>} />
        </Routes>
      </MemoryRouter>,
    )

    fireEvent.click(screen.getByRole('link', { name: 'Danh mục' }))

    expect(screen.getByText('Course list')).toBeInTheDocument()
    expect(screen.getByLabelText('current-location')).toHaveTextContent('/courses?page=2')
  })
})
