// @vitest-environment jsdom

import '@testing-library/jest-dom/vitest'
import { cleanup, render, screen } from '@testing-library/react'
import type { ComponentProps } from 'react'
import { afterEach, describe, expect, test, vi } from 'vitest'
import { ChatMessage } from '../AiChatWidget'
import type { WebSearchResult, SubjectQaStreamStage } from '../../../hooks/useSubjectQa'

Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation((query: string) => ({
    matches: true,
    media: query,
    onchange: null,
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    addListener: vi.fn(),
    removeListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})

afterEach(() => {
  cleanup()
})

function renderSearchResultMessage(overrides: Partial<ComponentProps<typeof ChatMessage>['message']> = {}) {
  const searchResults: WebSearchResult[] = [
    {
      url: 'https://svuit.org/mmtt/docs/MonHocTuChon/CNPM/SE104.html',
      title: 'SE104 - Nhập môn công nghệ phần mềm',
      description: 'Tài liệu môn học SE104 của SVUIT.',
      position: 1,
      highlights: ['SE104 là môn nhập môn công nghệ phần mềm.'],
      publishedDate: '2026-06-01',
      author: 'SVUIT',
      sourceProvider: 'exa',
    },
  ]

  const message: ComponentProps<typeof ChatMessage>['message'] = {
    id: 'ai-1',
    sender: 'ai',
    content: 'Đây là câu trả lời có nguồn web.',
    searchResults,
    sources: [],
    ...overrides,
  }

  render(
    <ChatMessage
      message={message}
      isStreaming={true}
      copiedId={null}
      onCopy={vi.fn()}
    />,
  )

  return { searchResults }
}

describe('AiChatWidget search result cards', () => {
  test('shows search results even while the answer is still streaming', () => {
    renderSearchResultMessage()

    expect(screen.getByText('Kết quả tìm kiếm:')).toBeInTheDocument()
    expect(screen.getByText('SE104 - Nhập môn công nghệ phần mềm')).toBeInTheDocument()
    expect(screen.getByText('svuit.org')).toBeInTheDocument()
    expect(screen.getByText('exa')).toBeInTheDocument()
  })
})

describe('AiChatWidget streaming status rows', () => {
  test('renders status rows while streaming', () => {
    const statusEvents = [
      { id: 's1', stage: 'session' as SubjectQaStreamStage, message: 'Đang mở phiên chat' },
      { id: 's2', stage: 'analyze' as SubjectQaStreamStage, message: 'Tìm thấy mã môn: SE104' },
      { id: 's3', stage: 'rag' as SubjectQaStreamStage, message: 'Đang tìm trong Knowledge RAG' },
    ]

    const message: ComponentProps<typeof ChatMessage>['message'] = {
      id: 'ai-stream-1',
      sender: 'ai',
      content: '',
      sources: [],
      searchResults: [],
      statusEvents,
    }

    render(
      <ChatMessage
        message={message}
        isStreaming={true}
        copiedId={null}
        onCopy={vi.fn()}
      />,
    )

    expect(screen.getByText('Đang mở phiên chat')).toBeInTheDocument()
    expect(screen.getByText('Tìm thấy mã môn: SE104')).toBeInTheDocument()
    expect(screen.getByText('Đang tìm trong Knowledge RAG')).toBeInTheDocument()
  })

  test('renders accumulated markdown without fake streaming', () => {
    const message: ComponentProps<typeof ChatMessage>['message'] = {
      id: 'ai-2',
      sender: 'ai',
      content: 'Xin **chào** bạn!',
      sources: [],
      searchResults: [],
      statusEvents: [],
    }

    render(
      <ChatMessage
        message={message}
        isStreaming={true}
        copiedId={null}
        onCopy={vi.fn()}
      />,
    )

    // Bold text should be rendered immediately
    expect(screen.getByText('chào')).toBeInTheDocument()
    expect(screen.getByText('Xin')).toBeInTheDocument()
    // The blinking cursor should be visible
    const cursor = document.querySelector('.animate-pulse')
    expect(cursor).toBeInTheDocument()
  })
})
