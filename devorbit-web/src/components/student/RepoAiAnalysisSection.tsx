import { GraduationCap, MagicWand } from '@phosphor-icons/react'
import type { AiResponse } from '../../types/api'
import { buildRepoAiAnalysisCards } from '../../lib/repoAiAnalysis'

type RepoAiAnalysisSectionProps = {
  summary: AiResponse | null
  advice: AiResponse | null
}

export function RepoAiAnalysisSection({ summary, advice }: RepoAiAnalysisSectionProps) {
  const cards = buildRepoAiAnalysisCards(summary, advice)

  if (cards.length === 0) return null

  return (
    <div className="grid md:grid-cols-2 gap-6">
      {cards.map((card) => {
        const isAccent = card.tone === 'accent'
        const Icon = isAccent ? MagicWand : GraduationCap

        return (
          <div
            key={card.key}
            className={`orbit-card-glow p-8 md:p-10 transition-all duration-500 ${
              isAccent
                ? 'border-orbit-accent/10 hover:border-orbit-accent/30'
                : 'border-indigo-500/10 hover:border-indigo-500/30'
            }`}
          >
            <div
              className={`h-12 w-12 rounded-2xl border flex items-center justify-center mb-8 ${
                isAccent
                  ? 'bg-orbit-accent/5 border-orbit-accent/10'
                  : 'bg-indigo-500/5 border-indigo-500/10'
              }`}
            >
              <Icon className={`h-6 w-6 ${isAccent ? 'text-orbit-accent' : 'text-indigo-400'}`} weight="duotone" />
            </div>
            <h3 className="heading-4 mb-6 text-orbit-text flex items-center gap-3">
              {card.title}
            </h3>
            <p
              className={`body-md text-[14px] leading-[1.8] text-orbit-text-secondary whitespace-pre-line ${
                card.italic ? 'italic' : ''
              }`}
            >
              {card.content}
            </p>
          </div>
        )
      })}
    </div>
  )
}
