import { BookOpen, CheckCircle, GraduationCap, MagicWand, RocketLaunch, Stack, WarningCircle } from '@phosphor-icons/react'
import type { Icon } from '@phosphor-icons/react'
import type { RepoSummary } from '../../types/api'
import { buildRepoAiAnalysisSections, type RepoAiAnalysisTone } from '../../lib/repoAiAnalysis'

type RepoAiAnalysisSectionProps = {
  repo: RepoSummary
}

const toneClasses: Record<RepoAiAnalysisTone, { border: string; icon: string; iconBox: string }> = {
  accent: {
    border: 'border-orbit-accent/10 hover:border-orbit-accent/30',
    icon: 'text-orbit-accent',
    iconBox: 'bg-orbit-accent/5 border-orbit-accent/10',
  },
  indigo: {
    border: 'border-indigo-500/10 hover:border-indigo-500/30',
    icon: 'text-indigo-400',
    iconBox: 'bg-indigo-500/5 border-indigo-500/10',
  },
  amber: {
    border: 'border-amber-500/10 hover:border-amber-500/30',
    icon: 'text-amber-400',
    iconBox: 'bg-amber-500/5 border-amber-500/10',
  },
  rose: {
    border: 'border-rose-500/10 hover:border-rose-500/30',
    icon: 'text-rose-400',
    iconBox: 'bg-rose-500/5 border-rose-500/10',
  },
}

const sectionIcons: Record<string, Icon> = {
  overview: MagicWand,
  technology: Stack,
  fit: CheckCircle,
  reviewFirst: BookOpen,
  strategy: GraduationCap,
  nextSteps: RocketLaunch,
  warnings: WarningCircle,
}

export function RepoAiAnalysisSection({ repo }: RepoAiAnalysisSectionProps) {
  const sections = buildRepoAiAnalysisSections(repo)

  return (
    <section className="grid md:grid-cols-2 gap-6">
      {sections.map((section) => {
        const IconComponent = sectionIcons[section.key] ?? MagicWand
        const tone = toneClasses[section.tone]

        return (
          <article
            key={section.key}
            className={`orbit-card-glow p-7 md:p-8 transition-all duration-500 ${tone.border}`}
          >
            <div className={`h-11 w-11 rounded-2xl border flex items-center justify-center mb-6 ${tone.iconBox}`}>
              <IconComponent className={`h-5 w-5 ${tone.icon}`} weight="duotone" />
            </div>
            <h3 className="text-[18px] font-black mb-4 text-orbit-text">
              {section.title}
            </h3>
            <p className="body-md text-[14px] leading-[1.8] text-orbit-text-secondary">
              {section.content}
            </p>
            {section.items && section.items.length > 0 && (
              <ul className="mt-5 space-y-3">
                {section.items.map((item) => (
                  <li key={item} className="flex gap-3 text-[13px] leading-relaxed text-orbit-text-secondary">
                    <span className={`mt-2 h-1.5 w-1.5 rounded-full shrink-0 ${section.tone === 'rose' ? 'bg-rose-400' : 'bg-orbit-accent'}`} />
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            )}
          </article>
        )
      })}
    </section>
  )
}
