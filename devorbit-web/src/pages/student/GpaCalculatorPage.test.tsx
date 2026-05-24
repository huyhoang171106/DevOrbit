// @vitest-environment jsdom

import '@testing-library/jest-dom/vitest'
import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import { afterEach, describe, expect, test } from 'vitest'
import { GpaCalculatorPage } from './GpaCalculatorPage'

afterEach(() => {
  cleanup()
})

describe('GpaCalculatorPage', () => {
  test('calculates weighted GPA on the 10-point scale only', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(screen.getByLabelText(/Tên môn 1/i), { target: { value: 'Nhập môn lập trình' } })
    fireEvent.change(screen.getByLabelText(/Tín chỉ 1/i), { target: { value: '4' } })
    fireEvent.change(screen.getByLabelText(/Điểm hệ 10 môn 1/i), { target: { value: '8.5' } })
    fireEvent.change(screen.getByLabelText(/Tên môn 2/i), { target: { value: 'Cấu trúc dữ liệu' } })
    fireEvent.change(screen.getByLabelText(/Tín chỉ 2/i), { target: { value: '3' } })
    fireEvent.change(screen.getByLabelText(/Điểm hệ 10 môn 2/i), { target: { value: '7' } })

    expect(screen.queryByText(/hệ 4/i)).not.toBeInTheDocument()
    expect(screen.getByText('GPA hệ 10')).toBeInTheDocument()
    expect(screen.getByText('7.86')).toBeInTheDocument()
    expect(screen.getByText('7 tín chỉ')).toBeInTheDocument()
    expect(screen.getByText('Khá')).toBeInTheDocument()
  })

  test('adds and removes course rows', () => {
    render(<GpaCalculatorPage />)

    fireEvent.click(screen.getByRole('button', { name: /Thêm môn/i }))
    expect(screen.getByLabelText(/Tên môn 3/i)).toBeInTheDocument()

    fireEvent.click(screen.getAllByRole('button', { name: /Xóa môn/i })[2])
    expect(screen.queryByLabelText(/Tên môn 3/i)).not.toBeInTheDocument()
  })

  test('shows validation guidance when there are no valid credits', () => {
    render(<GpaCalculatorPage />)

    fireEvent.change(screen.getByLabelText(/Tín chỉ 1/i), { target: { value: '0' } })
    fireEvent.change(screen.getByLabelText(/Tín chỉ 2/i), { target: { value: '0' } })

    expect(screen.getByText(/Nhập tín chỉ và điểm hợp lệ/i)).toBeInTheDocument()
  })
})
