import type { AiResponse } from '../types/api'
import { cleanAiContent } from './contentCleaner'

export type RepoAiAnalysisCard = {
  key: 'summary' | 'advice'
  title: string
  content: string
  tone: 'accent' | 'indigo'
  italic?: boolean
}

export function buildRepoAiAnalysisCards(
  summary: AiResponse | null,
  advice: AiResponse | null,
): RepoAiAnalysisCard[] {
  const cards: Array<RepoAiAnalysisCard | null> = [
    summary && {
      key: 'summary',
      title: 'Phân tích nội dung',
      content: cleanAiContent(summary.content),
      tone: 'accent',
    },
    advice && {
      key: 'advice',
      title: 'Chiến lược học tập',
      content: cleanAiContent(advice.content),
      tone: 'indigo',
      italic: true,
    },
  ]

  return cards.filter((card): card is RepoAiAnalysisCard => Boolean(card && card.content))
}
