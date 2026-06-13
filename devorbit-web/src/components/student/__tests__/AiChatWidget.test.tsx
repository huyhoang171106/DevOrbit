// @vitest-environment jsdom

import '@testing-library/jest-dom/vitest'
import { cleanup, render, screen } from '@testing-library/react'
import type { ComponentProps } from 'react'
import { afterEach, describe, expect, test, vi } from 'vitest'
import { ChatMessage } from '../AiChatWidget'
import type { WebSearchResult, SubjectQaStreamStage } from '../../../hooks/useSubjectQa'
import type { RoadmapResponse } from '../../../hooks/useAiRoadmap'

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

function renderRoadmapMessage() {
  const roadmap: RoadmapResponse = {
    summary: '## Mục tiêu\n\n- Học các môn nền tảng trước.\n- Ưu tiên môn phù hợp mục tiêu backend.',
    recommendedCourses: [
      {
        courseId: 330,
        courseCode: 'SE330',
        courseName: 'Ngôn ngữ lập trình Java',
        reasoning: 'Môn nền tảng để đi sâu vào Java backend.',
        description: 'Java course',
        isMandatory: false,
        semester: 4,
        credits: 3,
      },
      {
        courseId: 325,
        courseCode: 'SE325',
        courseName: 'Chuyên đề J2EE',
        reasoning: 'Môn đi sâu vào Java enterprise.',
        description: 'Enterprise Java course',
        isMandatory: false,
        semester: 5,
        credits: 3,
      },
    ],
    graduationTracks: [
      {
        type: 'THESIS',
        name: 'Khóa luận tốt nghiệp',
        description: 'Theo hướng nghiên cứu và tổng hợp.',
        credits: 10,
        requirements: 'Hoàn tất các học phần bắt buộc.',
        recommendation: 'Phù hợp nếu muốn đi sâu nghiên cứu.',
        recommended: true,
        courseCodes: ['SE505'],
      },
    ],
    electivePools: [
      {
        poolId: 'chuyen-nganh',
        poolName: 'Chuyên ngành',
        targetTC: 16,
        currentTC: 9,
        candidates: [],
      },
    ],
  }

  const message: ComponentProps<typeof ChatMessage>['message'] = {
    id: 'ai-roadmap-1',
    sender: 'ai',
    content: 'Mình đã dựng lộ trình học tập theo mục tiêu bạn nhập.',
    sources: [],
    searchResults: [],
    roadmap,
  }

  render(
    <ChatMessage
      message={message}
      isStreaming={false}
      copiedId={null}
      onCopy={vi.fn()}
    />,
  )
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

describe('AiChatWidget roadmap preview', () => {
  test('shows structured roadmap payload inside the chat bubble', () => {
    renderRoadmapMessage()

    expect(screen.getByText('Lộ trình học tập')).toBeInTheDocument()
    expect(screen.getByText('Node lộ trình')).toBeInTheDocument()
    expect(screen.getByText('Học kỳ 4')).toBeInTheDocument()
    expect(screen.getByText('Học kỳ 5')).toBeInTheDocument()
    expect(screen.getByText('SE330')).toBeInTheDocument()
    expect(screen.getByText('Ngôn ngữ lập trình Java')).toBeInTheDocument()
    expect(screen.getByText('Hướng tốt nghiệp')).toBeInTheDocument()
    expect(screen.getByText('Khóa luận tốt nghiệp')).toBeInTheDocument()
    expect(screen.getByText('Nhóm tự chọn')).toBeInTheDocument()
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
