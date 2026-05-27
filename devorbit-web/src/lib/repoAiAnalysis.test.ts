import { describe, expect, test } from 'vitest'
import { buildRepoAiAnalysisCards } from './repoAiAnalysis'

describe('buildRepoAiAnalysisCards', () => {
  test('builds cleaned cards for summary and advice responses', () => {
    const cards = buildRepoAiAnalysisCards(
      { type: 'SUMMARY', content: '📌 **Tổng quan**\n\nRepository Java' },
      { type: 'TUTOR_ADVICE', content: '🎯 **Bước 1:**\n- Đọc `README.md`' },
    )

    expect(cards).toEqual([
      {
        key: 'summary',
        title: 'Phân tích nội dung',
        content: 'Tổng quan\n\nRepository Java',
        tone: 'accent',
      },
      {
        key: 'advice',
        title: 'Chiến lược học tập',
        content: 'Bước 1:\n- Đọc README.md',
        tone: 'indigo',
        italic: true,
      },
    ])
  })

  test('returns no cards when AI responses are missing', () => {
    expect(buildRepoAiAnalysisCards(null, null)).toEqual([])
  })
})
