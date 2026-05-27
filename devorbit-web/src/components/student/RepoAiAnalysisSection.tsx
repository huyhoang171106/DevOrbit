import { useState } from 'react'
import { BookOpen, CaretDown, CheckCircle, GraduationCap, MagicWand, RocketLaunch, Stack, WarningCircle } from '@phosphor-icons/react'
import type { Icon } from '@phosphor-icons/react'
import type { RepoAnalysisResult } from '../../lib/repoAnalysisService'
import type { RepoAiAnalysisSection as RepoAiAnalysisSectionModel, RepoAiAnalysisTone } from '../../lib/repoAiAnalysis'

type RepoAiAnalysisSectionProps = {
  analysis: RepoAnalysisResult | null
  loading: boolean
  error: string | null
}

type AiSummary = {
  summary: string
  firstAction: string
  actions: string[]
  badges: Array<{
    label: string
    value: string
    tone: RepoAiAnalysisTone
  }>
  warning?: string
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
  readmeInsights: BookOpen,
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
  content: ['overview', 'readmeInsights', 'technology', 'fit'],
  learning: ['reviewFirst', 'strategy'],
  action: ['nextSteps'],
  warnings: ['warnings'],
}

const badgeToneClasses: Record<RepoAiAnalysisTone, string> = {
  accent: 'border-orbit-accent/20 bg-orbit-accent/10 text-orbit-accent',
  indigo: 'border-indigo-500/20 bg-indigo-500/10 text-indigo-300',
  amber: 'border-amber-500/20 bg-amber-500/10 text-amber-300',
  rose: 'border-rose-500/20 bg-rose-500/10 text-rose-300',
}

export function RepoAiAnalysisSection({ analysis, loading, error }: RepoAiAnalysisSectionProps) {
  const [detailsOpen, setDetailsOpen] = useState(false)

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
  const summary = buildAiSummary(sectionsByKey)
  const detailGroups = Object.entries(groupKeys)
    .map(([groupKey, keys]) => ({
      groupKey,
      sections: keys.map((key) => sectionsByKey.get(key)).filter(Boolean) as RepoAiAnalysisSectionModel[],
    }))
    .filter((group) => group.sections.length > 0)
  const detailSectionCount = detailGroups.reduce((total, group) => total + group.sections.length, 0)

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

      <AiSummaryCard summary={summary} />

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

      <div className="orbit-card p-5 md:p-6 border-orbit-border bg-orbit-surface/60">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h3 className="text-[15px] font-black text-orbit-text">Phân tích chi tiết</h3>
            <p className="mt-1 text-[12px] leading-relaxed text-orbit-text-muted">
              README insights, công nghệ, mức độ phù hợp, chiến lược học và cảnh báo vẫn được giữ đầy đủ ở đây.
            </p>
          </div>
          <button
            type="button"
            onClick={() => setDetailsOpen((open) => !open)}
            className="inline-flex items-center justify-center gap-2 rounded-2xl border border-orbit-border bg-orbit-elevated px-4 py-3 text-[11px] font-black uppercase tracking-[0.12em] text-orbit-text-secondary transition-all hover:border-orbit-accent/30 hover:text-orbit-text"
            aria-expanded={detailsOpen}
          >
            {detailsOpen ? 'Ẩn phân tích chi tiết' : 'Xem phân tích chi tiết'}
            <CaretDown className={`h-4 w-4 transition-transform ${detailsOpen ? 'rotate-180' : ''}`} weight="bold" />
          </button>
        </div>
        <div className="mt-4 flex flex-wrap gap-2 text-[10px] font-black uppercase tracking-[0.14em] text-orbit-text-muted/70">
          <span className="rounded-full border border-orbit-border px-3 py-1.5">{detailSectionCount} mục chi tiết</span>
          <span className="rounded-full border border-orbit-border px-3 py-1.5">{detailGroups.length} nhóm</span>
        </div>
      </div>

      {detailsOpen && (
        <div className="space-y-8">
          {detailGroups.map(({ groupKey, sections }) => (
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
          ))}
        </div>
      )}
    </section>
  )
}

function AiSummaryCard({ summary }: { summary: AiSummary }) {
  return (
    <article className="orbit-card-glow p-6 md:p-8 border-orbit-accent/20 bg-orbit-accent/5">
      <div className="flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between">
        <div className="min-w-0 flex-1">
          <div className="mb-4 flex items-center gap-3">
            <div className="h-11 w-11 rounded-2xl border border-orbit-accent/20 bg-orbit-accent/10 flex items-center justify-center shrink-0">
              <MagicWand className="h-5 w-5 text-orbit-accent" weight="duotone" />
            </div>
            <div>
              <p className="text-[10px] font-black uppercase tracking-[0.18em] text-orbit-accent">AI Summary</p>
              <h3 className="mt-1 text-[20px] font-black text-orbit-text leading-tight">Nên hiểu repo này thế nào?</h3>
            </div>
          </div>

          <p className="max-w-3xl text-[15px] leading-[1.75] text-orbit-text-secondary">
            {summary.summary}
          </p>

          <div className="mt-5 flex flex-wrap gap-2">
            {summary.badges.map((badge) => (
              <span
                key={`${badge.label}-${badge.value}`}
                className={`inline-flex items-center gap-2 rounded-2xl border px-3.5 py-2 text-[10px] font-black uppercase tracking-[0.12em] ${badgeToneClasses[badge.tone]}`}
              >
                <span className="text-orbit-text-muted">{badge.label}</span>
                <span>{badge.value}</span>
              </span>
            ))}
          </div>

          {summary.warning && (
            <div className="mt-5 flex items-start gap-3 rounded-2xl border border-amber-500/20 bg-amber-500/5 p-4">
              <WarningCircle className="mt-0.5 h-5 w-5 shrink-0 text-amber-400" weight="duotone" />
              <p className="text-[13px] leading-relaxed text-orbit-text-secondary">{summary.warning}</p>
            </div>
          )}
        </div>

        <div className="w-full lg:max-w-sm rounded-2xl border border-orbit-border bg-orbit-surface/70 p-5">
          <div className="mb-4 flex items-center gap-2">
            <RocketLaunch className="h-4 w-4 text-orbit-accent" weight="duotone" />
            <h4 className="text-[13px] font-black uppercase tracking-[0.12em] text-orbit-text">Việc nên làm ngay</h4>
          </div>
          <p className="mb-4 text-[13px] leading-relaxed text-orbit-text-secondary">
            {summary.firstAction}
          </p>
          <ul className="space-y-3">
            {summary.actions.slice(1).map((action) => (
              <li key={action} className="flex gap-3 text-[13px] leading-relaxed text-orbit-text-secondary">
                <span className="mt-2 h-1.5 w-1.5 rounded-full bg-orbit-accent shrink-0" />
                <span>{action}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </article>
  )
}

function AnalysisCard({ section }: { section: RepoAiAnalysisSectionModel }) {
  const IconComponent = sectionIcons[section.key] ?? MagicWand
  const tone = toneClasses[section.tone]
  const isWarning = section.tone === 'rose'
  const items = section.key === 'nextSteps' ? section.items?.slice(0, 3) : section.items

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

      {items && items.length > 0 && (
        <ul className="mt-5 divide-y divide-orbit-border/40">
          {items.map((item) => (
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

function buildAiSummary(sectionsByKey: Map<RepoAiAnalysisSectionModel['key'], RepoAiAnalysisSectionModel>): AiSummary {
  const overview = sectionsByKey.get('overview')
  const fit = sectionsByKey.get('fit')
  const nextSteps = sectionsByKey.get('nextSteps')
  const warnings = sectionsByKey.get('warnings')
  const summary = summarizeText(overview?.content ?? 'Chưa đủ dữ liệu để phân tích sâu.')
  const actions = prioritizeActions(nextSteps?.items ?? [])
  const firstAction = actions[0] ?? 'Đọc README hoặc mở GitHub để kiểm tra source chính trước.'
  const fitLevel = extractValue(fit?.items, /Mức độ phù hợp học tập:\s*([^.(]+)/i)
  const completeness = extractValue(overview?.items, /Mức độ đầy đủ thông tin:\s*([^(]+)/i)
  const warning = warnings?.items?.[0]
  const badges: AiSummary['badges'] = [
    {
      label: 'Phù hợp',
      value: fitLevel ?? 'chưa rõ',
      tone: fitLevel === 'cao' ? 'accent' : fitLevel === 'thấp' ? 'rose' : 'amber',
    },
    {
      label: 'Dữ liệu',
      value: completeness ?? 'ít',
      tone: completeness === 'đầy đủ' ? 'accent' : completeness === 'ít' ? 'rose' : 'amber',
    },
  ]

  if (warning) {
    badges.push({
      label: 'Cảnh báo',
      value: 'cần kiểm tra',
      tone: 'amber',
    })
  }

  return {
    summary,
    firstAction,
    actions,
    badges: badges.slice(0, 3),
    warning,
  }
}

function prioritizeActions(items: string[]): string[] {
  const priority = [
    /readme/i,
    /source|src/i,
    /setup|build|run|config|dependency|package|pom|gradle|docker/i,
    /clone|fork/i,
    /docs?/i,
  ]
  const sorted = [...items].sort((a, b) => scoreAction(b, priority) - scoreAction(a, priority))
  return sorted.slice(0, 3)
}

function scoreAction(action: string, priority: RegExp[]): number {
  const index = priority.findIndex((pattern) => pattern.test(action))
  return index === -1 ? 0 : priority.length - index
}

function extractValue(items: string[] | undefined, pattern: RegExp): string | null {
  if (!items) return null
  for (const item of items) {
    const match = item.match(pattern)
    if (match?.[1]) return match[1].trim().toLowerCase()
  }
  return null
}

function summarizeText(value: string): string {
  if (value.includes('Chưa đủ dữ liệu')) return 'Chưa đủ dữ liệu để phân tích sâu. Hãy kiểm tra README, source chính và metadata còn thiếu trước khi dùng repo cho deadline.'
  if (value.length <= 280) return value
  return `${value.slice(0, 279).trim()}…`
}
