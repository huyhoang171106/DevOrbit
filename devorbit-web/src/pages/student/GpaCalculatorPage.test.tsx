// @vitest-environment jsdom

import '@testing-library/jest-dom/vitest'
import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, test, vi } from 'vitest'
import { GpaCalculatorPage } from './GpaCalculatorPage'

function input(id: string): HTMLInputElement {
  const element = document.querySelector<HTMLInputElement>(`#${id}`)
  if (!element) throw new Error(`Missing input ${id}`)
  return element
}

function select(id: string): HTMLSelectElement {
  const element = document.querySelector<HTMLSelectElement>(`#${id}`)
  if (!element) throw new Error(`Missing select ${id}`)
  return element
}

function buttonByText(pattern: RegExp): HTMLButtonElement {
  const element = screen.getAllByRole('button').find((button) => pattern.test(button.textContent ?? ''))
  if (!element) throw new Error(`Missing button ${pattern}`)
  return element as HTMLButtonElement
}

const mojibakePattern = new RegExp([
  '\\u00c2.',
  '\\u00c3.',
  '\\u00c4.',
  '\\u00c5.',
  '\\u00c6.',
  '\\u00e1\\u00ba.',
  '\\u00e1\\u00bb.',
  '\\ufffd',
].join('|'))

afterEach(() => {
  cleanup()
  vi.restoreAllMocks()
  localStorage.clear()
})

beforeEach(() => {
  vi.spyOn(globalThis, 'fetch').mockResolvedValue({
    ok: true,
    json: async () => [],
  } as Response)
})

describe('GpaCalculatorPage', () => {
  test('calculates weighted GPA on the 10-point scale only', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(input('course-name-1'), { target: { value: 'Nhap mon lap trinh' } })
    fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
    fireEvent.change(input('course-grade-1'), { target: { value: '8.5' } })
    fireEvent.change(input('course-name-2'), { target: { value: 'Cau truc du lieu' } })
    fireEvent.change(input('course-credits-2'), { target: { value: '3' } })
    fireEvent.change(input('course-grade-2'), { target: { value: '7' } })

    expect(screen.queryByText(/he 4/i)).not.toBeInTheDocument()
    expect(screen.getByText('7.86')).toBeInTheDocument()
    expect(screen.getByText(/7.*t/i)).toBeInTheDocument()
    expect(document.body.textContent).not.toMatch(mojibakePattern)
  })

  test('adds and removes course rows', () => {
    render(<GpaCalculatorPage />)

    fireEvent.click(buttonByText(/Thêm môn/))
    expect(input('course-name-3')).toBeInTheDocument()

    fireEvent.click(screen.getAllByRole('button', { name: /x/i })[2])
    expect(document.querySelector('#course-name-3')).not.toBeInTheDocument()
  })

  test('shows validation guidance when there are no valid credits', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(input('course-credits-1'), { target: { value: '0' } })
    fireEvent.change(input('course-credits-2'), { target: { value: '0' } })

    expect(screen.getAllByText(/h.*p l/i).length).toBeGreaterThan(0)
  })

  test('loads semester course presets from the course catalogue', async () => {
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => [
        { id: 1, code: 'MA006', name: 'Giai tich', repoCount: 0, semester: 1, credits: 4 },
        { id: 2, code: 'IT001', name: 'Nhap mon lap trinh', repoCount: 0, semester: 1, credits: 4 },
        { id: 3, code: 'PE0231', name: 'Giao duc the chat 1', repoCount: 0, semester: 1, credits: 0 },
        { id: 4, code: 'IT002', name: 'Lap trinh huong doi tuong', repoCount: 0, semester: 2, credits: 4 },
      ],
    } as Response)

    render(<GpaCalculatorPage />)

    await screen.findByText(/DevOrbit|preset/i)
    fireEvent.change(select('semester-preset'), { target: { value: '1' } })
    fireEvent.click(buttonByText(/Áp dụng học kỳ/))

    expect(screen.getByDisplayValue('MA006 - Giai tich')).toBeInTheDocument()
    expect(screen.getByDisplayValue('IT001 - Nhap mon lap trinh')).toBeInTheDocument()
    expect(screen.queryByDisplayValue('PE0231 - Giao duc the chat 1')).not.toBeInTheDocument()
    expect(screen.getAllByDisplayValue('4')).toHaveLength(2)
  })

  test('uses saved learning roadmap semester assignments before catalogue semesters', async () => {
    localStorage.setItem('devorbit_kanban_semester_map', JSON.stringify({ 1: 1, 4: 1, 5: 2 }))
    vi.mocked(fetch).mockResolvedValueOnce({
      ok: true,
      json: async () => [
        { id: 1, code: 'MA006', name: 'Giai tich', repoCount: 0, semester: 1, credits: 4 },
        { id: 4, code: 'IT002', name: 'Lap trinh huong doi tuong', repoCount: 0, semester: 2, credits: 4 },
        { id: 5, code: 'IT003', name: 'Cau truc du lieu va giai thuat', repoCount: 0, semester: 1, credits: 4 },
      ],
    } as Response)

    render(<GpaCalculatorPage />)

    await screen.findByText(/DevOrbit|preset/i)
    fireEvent.change(select('semester-preset'), { target: { value: '1' } })
    fireEvent.click(buttonByText(/Áp dụng học kỳ/))

    expect(screen.getByDisplayValue('MA006 - Giai tich')).toBeInTheDocument()
    expect(screen.getByDisplayValue('IT002 - Lap trinh huong doi tuong')).toBeInTheDocument()
    expect(screen.queryByDisplayValue('IT003 - Cau truc du lieu va giai thuat')).not.toBeInTheDocument()
  })

  test('estimates cumulative GPA from current GPA and completed credits', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
    fireEvent.change(input('course-grade-1'), { target: { value: '8.5' } })
    fireEvent.change(input('course-credits-2'), { target: { value: '3' } })
    fireEvent.change(input('course-grade-2'), { target: { value: '7' } })

    fireEvent.click(buttonByText(/tích lũy/i))
    fireEvent.change(input('current-gpa'), { target: { value: '7' } })
    fireEvent.change(input('completed-credits'), { target: { value: '20' } })

    expect(screen.getByText(/GPA tích lũy dự kiến/i)).toBeInTheDocument()
    expect(screen.getByText('7.22')).toBeInTheDocument()
    expect(screen.getByText(/27.*t/i)).toBeInTheDocument()
  })
})
