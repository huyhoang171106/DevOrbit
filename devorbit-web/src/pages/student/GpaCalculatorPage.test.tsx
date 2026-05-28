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

function textExactly(value: string) {
  return (_content: string, element: Element | null) => element?.textContent === value
}

function switchToGoalMode() {
  fireEvent.click(buttonByText(/Mục tiêu GPA|Muc tieu GPA/i))
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

const draftKey = 'devorbit_gpa_calculator_draft_v1'

afterEach(() => {
  cleanup()
  vi.restoreAllMocks()
  vi.unstubAllGlobals()
  localStorage.clear()
})

beforeEach(() => {
  Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: vi.fn().mockImplementation((query: string) => ({
      matches: false,
      media: query,
      onchange: null,
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      addListener: vi.fn(),
      removeListener: vi.fn(),
      dispatchEvent: vi.fn(),
    })),
  })
  vi.stubGlobal('IntersectionObserver', class {
    observe = vi.fn()
    unobserve = vi.fn()
    disconnect = vi.fn()
  })
  vi.spyOn(globalThis, 'fetch').mockResolvedValue({
    ok: true,
    json: async () => [],
  } as Response)
})

describe('GpaCalculatorPage', () => {
  test('renders the shared student background atmosphere', () => {
    render(<GpaCalculatorPage />)

    const section = document.querySelector('section')
    const atmosphere = document.querySelector('[data-testid="gpa-shared-atmosphere"]')
    expect(section).toHaveAttribute('data-atmosphere', 'none')
    expect(atmosphere).toHaveClass('fixed', 'inset-0', 'pointer-events-none', 'z-0')
    expect(atmosphere?.querySelector('.bg-orbit-accent\\/5')).toBeInTheDocument()
    expect(atmosphere?.querySelector('.bg-emerald-500\\/3')).toBeInTheDocument()
    expect(document.querySelector('[data-testid="gpa-atmosphere"]')).not.toBeInTheDocument()
  })

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

    fireEvent.click(buttonByText(/Thêm môn|Them mon/i))
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
    fireEvent.click(buttonByText(/Áp dụng học kỳ|Ap dung hoc ky/i))

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
    fireEvent.click(buttonByText(/Áp dụng học kỳ|Ap dung hoc ky/i))

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

    fireEvent.click(buttonByText(/tích lũy|tich luy/i))
    fireEvent.change(input('current-gpa'), { target: { value: '7' } })
    fireEvent.change(input('completed-credits'), { target: { value: '20' } })

    expect(screen.getByText(/GPA tích lũy dự kiến|GPA tich luy du kien/i)).toBeInTheDocument()
    expect(screen.getByText('7.22')).toBeInTheDocument()
    expect(screen.getByText(/27.*t/i)).toBeInTheDocument()
  })

  test('calculates the required current-term GPA for a target cumulative GPA', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(input('course-name-1'), { target: { value: 'Nhap mon lap trinh' } })
    fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
    fireEvent.change(input('course-grade-1'), { target: { value: '8' } })
    fireEvent.change(input('course-name-2'), { target: { value: 'Cau truc du lieu' } })
    fireEvent.change(input('course-credits-2'), { target: { value: '3' } })
    fireEvent.change(input('course-grade-2'), { target: { value: '8' } })

    switchToGoalMode()
    fireEvent.change(input('current-gpa'), { target: { value: '7' } })
    fireEvent.change(input('completed-credits'), { target: { value: '20' } })
    fireEvent.change(input('target-gpa'), { target: { value: '7.5' } })

    expect(screen.getByText(/Kỳ này cần trung bình|Ky nay can trung binh/i)).toBeInTheDocument()
    expect(screen.getAllByText('8.93').length).toBeGreaterThan(0)
    expect(screen.getByText(/Mục tiêu khó nhưng còn khả thi|Muc tieu kho nhung con kha thi/i)).toBeInTheDocument()
    expect(screen.getByText(/Nhap mon lap trinh/i)).toBeInTheDocument()
    expect(screen.getByText(/Cau truc du lieu/i)).toBeInTheDocument()
  })

  test('marks the target as not feasible when required term GPA is above 10', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(input('course-credits-1'), { target: { value: '3' } })
    fireEvent.change(input('course-grade-1'), { target: { value: '8' } })
    fireEvent.change(input('course-credits-2'), { target: { value: '3' } })
    fireEvent.change(input('course-grade-2'), { target: { value: '8' } })

    switchToGoalMode()
    fireEvent.change(input('current-gpa'), { target: { value: '5' } })
    fireEvent.change(input('completed-credits'), { target: { value: '100' } })
    fireEvent.change(input('target-gpa'), { target: { value: '8' } })

    expect(screen.getByText('58.00')).toBeInTheDocument()
    expect(screen.getByText(/không khả thi|khong kha thi/i)).toBeInTheDocument()
    expect(screen.getByText(/cần cao hơn 10|can cao hon 10/i)).toBeInTheDocument()
  })

  test('shows already above target when no extra GPA pressure is required', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
    fireEvent.change(input('course-grade-1'), { target: { value: '8' } })
    fireEvent.change(input('course-credits-2'), { target: { value: '3' } })
    fireEvent.change(input('course-grade-2'), { target: { value: '8' } })

    switchToGoalMode()
    fireEvent.change(input('current-gpa'), { target: { value: '9' } })
    fireEvent.change(input('completed-credits'), { target: { value: '80' } })
    fireEvent.change(input('target-gpa'), { target: { value: '7' } })

    expect(screen.getByText(/đã vượt mục tiêu|da vuot muc tieu/i)).toBeInTheDocument()
    expect(screen.getByText(/không cần áp lực điểm thêm|khong can ap luc diem them/i)).toBeInTheDocument()
  })

  test('shows goal guidance when target inputs or term credits are invalid', () => {
    render(<GpaCalculatorPage />)

    switchToGoalMode()
    fireEvent.change(input('current-gpa'), { target: { value: '7' } })
    fireEvent.change(input('completed-credits'), { target: { value: '20' } })
    fireEvent.change(input('target-gpa'), { target: { value: '7.5' } })

    expect(screen.getByText(/Thêm môn hoặc nhập tín chỉ kỳ này hợp lệ|Them mon hoac nhap tin chi ky nay hop le/i)).toBeInTheDocument()
  })

  test('shows row-level validation reasons for invalid course inputs', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(input('course-credits-1'), { target: { value: '' } })
    fireEvent.change(input('course-grade-1'), { target: { value: '' } })
    fireEvent.change(input('course-credits-2'), { target: { value: '-1' } })
    fireEvent.change(input('course-grade-2'), { target: { value: '11' } })

    expect(screen.getByText(textExactly('Nhập tín chỉ'))).toBeInTheDocument()
    expect(screen.getByText(textExactly('Nhập điểm'))).toBeInTheDocument()
    expect(screen.getByText(textExactly('Tín chỉ phải lớn hơn 0'))).toBeInTheDocument()
    expect(screen.getByText(textExactly('Điểm phải từ 0 đến 10'))).toBeInTheDocument()
  })

  test('shows how many invalid rows are ignored in the summary', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
    fireEvent.change(input('course-grade-1'), { target: { value: '8' } })
    fireEvent.change(input('course-credits-2'), { target: { value: '3' } })
    fireEvent.change(input('course-grade-2'), { target: { value: '12' } })

    expect(screen.getByText(/Đang bỏ qua 1 dòng chưa hợp lệ|Dang bo qua 1 dong chua hop le/i)).toBeInTheDocument()
    expect(screen.getByText('8.00')).toBeInTheDocument()
  })

  test('duplicates a course row directly below the source row', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(input('course-name-1'), { target: { value: 'Nhap mon lap trinh' } })
    fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
    fireEvent.change(input('course-grade-1'), { target: { value: '8.5' } })
    fireEvent.click(screen.getAllByRole('button', { name: /Nhân bản dòng|Nhan ban dong/i })[0])

    expect(input('course-name-2').value).toBe('Nhap mon lap trinh')
    expect(input('course-credits-2').value).toBe('4')
    expect(input('course-grade-2').value).toBe('8.5')
  })

  test('supports clear all, add five courses, and reset default quick actions', () => {
    render(<GpaCalculatorPage />)

    fireEvent.click(buttonByText(/Thêm 5 môn|Them 5 mon/i))
    expect(input('course-name-7')).toBeInTheDocument()

    fireEvent.click(buttonByText(/Xóa tất cả|Xoa tat ca/i))
    expect(input('course-name-1')).toBeInTheDocument()
    expect(document.querySelector('#course-name-2')).not.toBeInTheDocument()

    fireEvent.click(buttonByText(/Reset mẫu|Reset mau/i))
    expect(input('course-name-1')).toBeInTheDocument()
    expect(input('course-name-2')).toBeInTheDocument()
    expect(document.querySelector('#course-name-3')).not.toBeInTheDocument()
  })

  test('restores a saved browser draft on reload', () => {
    localStorage.setItem(draftKey, JSON.stringify({
      courses: [
        { id: 1, name: 'Nhap mon lap trinh', credits: '4', grade10: '8.5' },
        { id: 2, name: 'Cau truc du lieu', credits: '3', grade10: '7.5' },
      ],
      calculationMode: 'goal',
      currentGpa: '7',
      completedCredits: '20',
      targetGpa: '7.8',
      selectedSemester: '3',
      updatedAt: '2026-05-27T03:00:00.000Z',
    }))

    render(<GpaCalculatorPage />)

    expect(input('course-name-1').value).toBe('Nhap mon lap trinh')
    expect(input('course-credits-1').value).toBe('4')
    expect(input('course-grade-2').value).toBe('7.5')
    expect(input('current-gpa').value).toBe('7')
    expect(input('completed-credits').value).toBe('20')
    expect(input('target-gpa').value).toBe('7.8')
    expect(select('semester-preset').value).toBe('3')
    expect(screen.getByText(/Đã lưu tạm trên trình duyệt|Da luu tam tren trinh duyet/i)).toBeInTheDocument()
  })

  test('autosaves calculator changes to localStorage', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(input('course-name-1'), { target: { value: 'Nhap mon lap trinh' } })
    fireEvent.change(input('course-credits-1'), { target: { value: '4' } })
    fireEvent.click(buttonByText(/Mục tiêu GPA|Muc tieu GPA/i))
    fireEvent.change(input('target-gpa'), { target: { value: '8' } })

    const saved = JSON.parse(localStorage.getItem(draftKey) ?? '{}')
    expect(saved.courses[0]).toMatchObject({ name: 'Nhap mon lap trinh', credits: '4' })
    expect(saved.calculationMode).toBe('goal')
    expect(saved.targetGpa).toBe('8')
    expect(typeof saved.updatedAt).toBe('string')
  })

  test('ignores a corrupt browser draft without crashing', () => {
    localStorage.setItem(draftKey, '{bad-json')

    render(<GpaCalculatorPage />)

    expect(input('course-name-1').value).toBe('')
    expect(input('course-credits-1').value).toBe('3')
    expect(screen.getByRole('heading', { name: /Tính GPA|Tinh GPA/i })).toBeInTheDocument()
  })

  test('does not show the saved draft clearing action yet', () => {
    render(<GpaCalculatorPage />)

    expect(screen.getByText(/Đã lưu tạm trên trình duyệt|Da luu tam tren trinh duyet/i)).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: /Xóa bản lưu|Xoa ban luu/i })).not.toBeInTheDocument()
  })
})
