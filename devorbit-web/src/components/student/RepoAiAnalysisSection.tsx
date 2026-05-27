import { BookOpen, CheckCircle, GraduationCap, MagicWand, RocketLaunch, Stack, WarningCircle } from '@phosphor-icons/react'
import type { Icon } from '@phosphor-icons/react'
import type { RepoAnalysisResult } from '../../lib/repoAnalysisService'
import type { RepoAiAnalysisTone } from '../../lib/repoAiAnalysis'

type RepoAiAnalysisSectionProps = {
  analysis: RepoAnalysisResult | null
  loading: boolean
  error: string | null
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

export function RepoAiAnalysisSection({ analysis, loading, error }: RepoAiAnalysisSectionProps) {
  if (loading) {
    return (
      <section className="orbit-card-glow p-8 border-orbit-accent/10">
        <div className="flex items-center gap-4">
          <div className="h-11 w-11 rounded-2xl border border-orbit-accent/10 bg-orbit-accent/5 flex items-center justify-center">
            <MagicWand className="h-5 w-5 text-orbit-accent animate-pulse" weight="duotone" />
          </div>
          <div>
            <h3 className="text-[18px] font-black text-orbit-text">Đang chuẩn bị phân tích</h3>
            <p className="mt-2 text-[13px] leading-relaxed text-orbit-text-secondary">
              DevOrbit đang đọc metadata repository và tạo phân tích phù hợp cho sinh viên KTPM UIT.
            </p>
          </div>
        </div>
      </section>
    )
  }

  if (!analysis || analysis.sections.length === 0) {
    return (
      <section className="orbit-card-glow p-8 border-amber-500/10">
        <div className="flex items-start gap-4">
          <div className="h-11 w-11 rounded-2xl border border-amber-500/10 bg-amber-500/5 flex items-center justify-center shrink-0">
            <WarningCircle className="h-5 w-5 text-amber-400" weight="duotone" />
          </div>
          <div>
            <h3 className="text-[18px] font-black text-orbit-text">Chưa có phân tích</h3>
            <p className="mt-2 text-[13px] leading-relaxed text-orbit-text-secondary">
              {error || 'Chưa đủ dữ liệu để tạo phân tích repository. Hãy mở GitHub để kiểm tra README và source trực tiếp.'}
            </p>
          </div>
        </div>
      </section>
    )
  }

  return (
    <section className="space-y-5">
      {(error || analysis.errorMessage || analysis.fallbackUsed) && (
        <div className="orbit-card p-5 border-amber-500/20 bg-amber-500/5">
          <div className="flex items-start gap-3">
            <WarningCircle className="h-5 w-5 text-amber-400 shrink-0 mt-0.5" weight="duotone" />
            <p className="text-[13px] leading-relaxed text-orbit-text-secondary">
              {error || analysis.errorMessage || 'AI provider chưa khả dụng, đang dùng phân tích rule-based từ dữ liệu repo hiện có.'}
            </p>
          </div>
        </div>
      )}

      <div className="grid md:grid-cols-2 gap-6">
      {analysis.sections.map((section) => {
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
      </div>
    </section>
  )
}
