import { BookOpen, CheckCircle, GraduationCap, MagicWand, RocketLaunch, Stack, WarningCircle } from '@phosphor-icons/react'
import type { Icon } from '@phosphor-icons/react'
import type { RepoAnalysisResult } from '../../lib/repoAnalysisService'
import type { RepoAiAnalysisSection as RepoAiAnalysisSectionModel, RepoAiAnalysisTone } from '../../lib/repoAiAnalysis'

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

const groupTitles: Record<string, string> = {
  content: 'Phân tích nội dung',
  learning: 'Chiến lược học tập',
  action: 'Việc nên làm tiếp theo',
  warnings: 'Cảnh báo/chú ý',
}

const groupDescriptions: Record<string, string> = {
  content: 'Repo đang nói về gì, stack chính là gì, và có phù hợp với môn học/deadline không.',
  learning: 'Cách đọc repo theo thứ tự để biến source code thành kiến thức dùng được.',
  action: 'Các bước thực tế nên làm sau khi xem metadata và phân tích ban đầu.',
  warnings: 'Những điểm còn thiếu trong dữ liệu public, cần kiểm tra trực tiếp trên GitHub.',
}

const groupKeys: Record<string, RepoAiAnalysisSectionModel['key'][]> = {
  content: ['overview', 'technology', 'fit'],
  learning: ['reviewFirst', 'strategy'],
  action: ['nextSteps'],
  warnings: ['warnings'],
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

  const sectionsByKey = new Map(analysis.sections.map((section) => [section.key, section]))

  return (
    <section className="space-y-8">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-5">
        <div>
          <span className="section-label mb-4 inline-flex">
            <MagicWand className="h-3 w-3" weight="fill" />
            AI analysis
          </span>
          <h2 className="heading-3 text-orbit-text">Phân tích repository</h2>
          <p className="mt-3 max-w-2xl text-[14px] leading-relaxed text-orbit-text-secondary">
            Nội dung dưới đây được tạo từ metadata thật của repo hiện có trong DevOrbit. Nếu thiếu README, topics hoặc file tree, phần cảnh báo sẽ nói rõ thay vì suy đoán.
          </p>
        </div>
        <div className="flex items-center gap-3 rounded-2xl border border-orbit-border bg-orbit-surface px-4 py-3">
          <span className="h-2 w-2 rounded-full bg-orbit-accent" />
          <span className="text-[10px] font-black uppercase tracking-[0.16em] text-orbit-text-muted">
            {analysis.source === 'rule-based' ? 'Rule-based' : 'AI provider'}
          </span>
        </div>
      </div>

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

      {Object.entries(groupKeys).map(([groupKey, keys]) => {
        const sections = keys.map((key) => sectionsByKey.get(key)).filter(Boolean) as RepoAiAnalysisSectionModel[]
        if (sections.length === 0) return null

        return (
          <div key={groupKey} className="space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div>
                <h3 className="text-[15px] font-black text-orbit-text uppercase tracking-[0.08em]">
                  {groupTitles[groupKey]}
                </h3>
                <p className="mt-1 text-[12px] leading-relaxed text-orbit-text-muted">
                  {groupDescriptions[groupKey]}
                </p>
              </div>
              <span className="text-[10px] font-black uppercase tracking-[0.15em] text-orbit-text-muted/70">
                {sections.length} mục
              </span>
            </div>

            <div className={`grid gap-5 ${sections.length === 1 ? 'md:grid-cols-1' : 'md:grid-cols-2'}`}>
              {sections.map((section) => (
                <AnalysisCard key={section.key} section={section} />
              ))}
            </div>
          </div>
        )
      })}
    </section>
  )
}

function AnalysisCard({ section }: { section: RepoAiAnalysisSectionModel }) {
  const IconComponent = sectionIcons[section.key] ?? MagicWand
  const tone = toneClasses[section.tone]
  const isWarning = section.tone === 'rose'

  return (
    <article
      className={`orbit-card-glow p-6 md:p-7 transition-all duration-500 ${tone.border} ${
        isWarning ? 'md:col-span-1 bg-rose-500/5' : ''
      }`}
    >
      <div className="flex items-start gap-4 mb-5">
        <div className={`h-11 w-11 rounded-2xl border flex items-center justify-center shrink-0 ${tone.iconBox}`}>
          <IconComponent className={`h-5 w-5 ${tone.icon}`} weight="duotone" />
        </div>
        <div className="min-w-0">
          <h4 className="text-[17px] font-black text-orbit-text leading-snug">
            {section.title}
          </h4>
          <div className={`mt-2 h-1 w-12 rounded-full ${isWarning ? 'bg-rose-400/60' : 'bg-orbit-accent/50'}`} />
        </div>
      </div>

      <p className="text-[14px] leading-[1.75] text-orbit-text-secondary">
        {section.content}
      </p>

      {section.items && section.items.length > 0 && (
        <ul className="mt-5 divide-y divide-orbit-border/40">
          {section.items.map((item) => (
            <li key={item} className="flex gap-3 py-3 first:pt-0 last:pb-0 text-[13px] leading-relaxed text-orbit-text-secondary">
              <span className={`mt-2 h-1.5 w-1.5 rounded-full shrink-0 ${isWarning ? 'bg-rose-400' : 'bg-orbit-accent'}`} />
              <span>{item}</span>
            </li>
          ))}
        </ul>
      )}
    </article>
  )
}
