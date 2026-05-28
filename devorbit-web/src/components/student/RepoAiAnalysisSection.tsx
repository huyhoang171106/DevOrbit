import { useState } from 'react'
import type { ReactNode } from 'react'
import { BookOpen, CaretDown, CheckCircle, MagicWand, RocketLaunch, WarningCircle } from '@phosphor-icons/react'
import type { RepoAnalysisResult } from '../../lib/repoAnalysisService'
import type { ConfidenceLabel, RepoEvaluationResult, UsefulnessRating } from '../../lib/repoEvaluation'

type RepoAiAnalysisSectionProps = {
  analysis: RepoAnalysisResult | null
  loading: boolean
  error: string | null
}

const ratingToneClasses: Record<UsefulnessRating, string> = {
  highly_recommended: 'border-orbit-accent/25 bg-orbit-accent/10 text-orbit-accent',
  recommended: 'border-orbit-accent/25 bg-orbit-accent/10 text-orbit-accent',
  selective: 'border-amber-500/25 bg-amber-500/10 text-amber-300',
  quick_reference: 'border-indigo-500/20 bg-indigo-500/10 text-indigo-300',
  low_priority: 'border-rose-500/25 bg-rose-500/10 text-rose-300',
  insufficient_data: 'border-rose-500/25 bg-rose-500/10 text-rose-300',
}

const confidenceClasses: Record<ConfidenceLabel, string> = {
  high: 'border-orbit-accent/20 bg-orbit-accent/10 text-orbit-accent',
  medium: 'border-amber-500/20 bg-amber-500/10 text-amber-300',
  low: 'border-rose-500/20 bg-rose-500/10 text-rose-300',
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
              DevOrbit đang đọc dữ liệu repo để đưa ra đánh giá ngắn gọn, dễ quyết định.
            </p>
          </div>
        </div>
      </section>
    )
  }

  if (!analysis?.evaluation) {
    return (
      <section className="orbit-card-glow p-8 border-amber-500/10">
        <div className="flex items-start gap-4">
          <div className="h-11 w-11 rounded-2xl border border-amber-500/10 bg-amber-500/5 flex items-center justify-center shrink-0">
            <WarningCircle className="h-5 w-5 text-amber-400" weight="duotone" />
          </div>
          <div>
            <h3 className="text-[18px] font-black text-orbit-text">Chưa có phân tích</h3>
            <p className="mt-2 text-[13px] leading-relaxed text-orbit-text-secondary">
              {error || 'Chưa đủ dữ liệu để phân tích sâu. Hãy mở GitHub để kiểm tra README và source trực tiếp.'}
            </p>
          </div>
        </div>
      </section>
    )
  }

  const evaluation = analysis.evaluation

  return (
    <section className="space-y-7">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-5">
        <div>
          <span className="section-label mb-4 inline-flex">
            <MagicWand className="h-3 w-3" weight="fill" />
            Repo evaluation
          </span>
          <h2 className="heading-3 text-orbit-text">Đánh giá repository</h2>
          <p className="mt-3 max-w-2xl text-[14px] leading-relaxed text-orbit-text-secondary">
            Nắm nhanh repo này thuộc loại gì, đáng xem ở mức nào, và cần kiểm tra gì trước khi dùng.
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-3">
          <Pill className={confidenceClasses[evaluation.confidence]}>
            Độ tin cậy: {evaluation.confidenceLabel}
          </Pill>
          <Pill className="border-orbit-border bg-orbit-surface text-orbit-text-muted">
            {analysis.source === 'rule-based' ? 'Rule-based' : 'AI provider'}
          </Pill>
        </div>
      </div>

      <QuickCaptureCard evaluation={evaluation} />

      {evaluation.confidence === 'low' && (
        <div className="orbit-card p-5 border-amber-500/20 bg-amber-500/5">
          <div className="flex items-start gap-3">
            <WarningCircle className="h-5 w-5 text-amber-400 shrink-0 mt-0.5" weight="duotone" />
            <p className="text-[13px] leading-relaxed text-orbit-text-secondary">
              Đánh giá này dựa trên dữ liệu giới hạn, nên kiểm tra trực tiếp repo trước khi dùng.
            </p>
          </div>
        </div>
      )}

      {(error || analysis.errorMessage || analysis.fallbackUsed) && (
        <div className="orbit-card p-5 border-amber-500/20 bg-amber-500/5">
          <div className="flex items-start gap-3">
            <WarningCircle className="h-5 w-5 text-amber-400 shrink-0 mt-0.5" weight="duotone" />
            <p className="text-[13px] leading-relaxed text-orbit-text-secondary">
              {error || analysis.errorMessage || 'Không thể tạo phân tích từ provider, đang dùng phân tích rule-based từ dữ liệu repo hiện có.'}
            </p>
          </div>
        </div>
      )}

      <div className="grid gap-5 lg:grid-cols-[1fr_0.9fr]">
        <StrengthWeaknessCard strengths={evaluation.strengths} weaknesses={evaluation.weaknesses} />
        <NextActionsCard actions={evaluation.nextActions} />
      </div>

      <div className="orbit-card p-5 md:p-6 border-orbit-border bg-orbit-surface/60">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h3 className="text-[15px] font-black text-orbit-text">Phân tích chi tiết</h3>
            <p className="mt-1 text-[12px] leading-relaxed text-orbit-text-muted">
              Mở khi cần xem repo nói về gì, có áp dụng được không và app đã dựa vào tín hiệu nào.
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
      </div>

      {detailsOpen && (
        <div className="grid gap-5 lg:grid-cols-2">
          {evaluation.sections.map((section) => (
            <DetailGroupCard key={section.title} title={section.title} items={section.items} />
          ))}
        </div>
      )}
    </section>
  )
}

function QuickCaptureCard({ evaluation }: { evaluation: RepoEvaluationResult }) {
  return (
    <article className="orbit-card-glow p-6 md:p-8 border-orbit-accent/20 bg-orbit-accent/5">
      <div className="mb-6 flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
        <div className="flex items-center gap-3">
          <div className="h-11 w-11 rounded-2xl border border-orbit-accent/20 bg-orbit-accent/10 flex items-center justify-center shrink-0">
            <MagicWand className="h-5 w-5 text-orbit-accent" weight="duotone" />
          </div>
          <div>
            <p className="text-[10px] font-black uppercase tracking-[0.18em] text-orbit-accent">Đánh giá nhanh</p>
            <h3 className="mt-1 text-[20px] font-black text-orbit-text leading-tight">Repo này có đáng xem không?</h3>
          </div>
        </div>
        <Pill className={ratingToneClasses[evaluation.usefulnessRating]}>
          {evaluation.usefulnessLabel}
        </Pill>
      </div>

      <div className="grid gap-3 md:grid-cols-2">
        <FactCard label="Loại repo" value={evaluation.repoTypeLabel} />
        <FactCard label="Phù hợp nhất" value={evaluation.bestFor} />
        <FactCard label="Mức độ đáng xem" value={evaluation.usefulnessLabel} />
        <FactCard label="Cần kiểm tra" value={evaluation.checksBeforeUsing.slice(0, 3).join(', ')} />
      </div>

      <div className="mt-6 space-y-3">
        <p className="text-[15px] font-black leading-relaxed text-orbit-text">
          {evaluation.mainReason}
        </p>
        <p className="max-w-3xl text-[14px] leading-relaxed text-orbit-text-secondary">
          {evaluation.quickSummary}
        </p>
      </div>
    </article>
  )
}

function FactCard({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-2xl border border-orbit-border bg-orbit-surface/80 p-4">
      <p className="text-[10px] font-black uppercase tracking-[0.16em] text-orbit-text-muted">{label}</p>
      <p className="mt-2 text-[13px] font-bold leading-relaxed text-orbit-text-secondary">{value}</p>
    </div>
  )
}

function StrengthWeaknessCard({ strengths, weaknesses }: { strengths: string[]; weaknesses: string[] }) {
  return (
    <article className="orbit-card p-6 border-orbit-border bg-orbit-surface/70">
      <div className="mb-5 flex items-center gap-3">
        <CheckCircle className="h-5 w-5 text-orbit-accent" weight="duotone" />
        <h3 className="text-[15px] font-black text-orbit-text">Điểm mạnh / Điểm yếu</h3>
      </div>
      <div className="grid gap-5 sm:grid-cols-2">
        <EvaluationList title="Điểm mạnh" items={strengths} tone="accent" />
        <EvaluationList title="Điểm yếu" items={weaknesses} tone="amber" />
      </div>
    </article>
  )
}

function NextActionsCard({ actions }: { actions: string[] }) {
  return (
    <article className="orbit-card p-6 border-orbit-border bg-orbit-surface/70">
      <div className="mb-5 flex items-center gap-3">
        <RocketLaunch className="h-5 w-5 text-orbit-accent" weight="duotone" />
        <h3 className="text-[15px] font-black text-orbit-text">Hành động tiếp theo</h3>
      </div>
      <ol className="space-y-4">
        {actions.map((action, index) => (
          <li key={action} className="flex gap-3 text-[13px] leading-relaxed text-orbit-text-secondary">
            <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full border border-orbit-accent/20 bg-orbit-accent/10 text-[11px] font-black text-orbit-accent">
              {index + 1}
            </span>
            <span>{action}</span>
          </li>
        ))}
      </ol>
    </article>
  )
}

function DetailGroupCard({ title, items }: { title: string; items: string[] }) {
  return (
    <article className="orbit-card-glow p-6 border-orbit-border">
      <div className="mb-5 flex items-center gap-3">
        <BookOpen className="h-5 w-5 text-orbit-accent" weight="duotone" />
        <h3 className="text-[15px] font-black text-orbit-text">{title}</h3>
      </div>
      <ul className="space-y-3">
        {items.map((item) => (
          <li key={item} className="flex gap-3 text-[13px] leading-relaxed text-orbit-text-secondary">
            <span className="mt-2 h-1.5 w-1.5 rounded-full bg-orbit-accent shrink-0" />
            <span>{item}</span>
          </li>
        ))}
      </ul>
    </article>
  )
}

function EvaluationList({ title, items, tone }: { title: string; items: string[]; tone: 'accent' | 'amber' }) {
  return (
    <div>
      <h4 className={`mb-3 text-[11px] font-black uppercase tracking-[0.14em] ${tone === 'accent' ? 'text-orbit-accent' : 'text-amber-300'}`}>
        {title}
      </h4>
      <ul className="space-y-3">
        {items.map((item) => (
          <li key={item} className="flex gap-3 text-[13px] leading-relaxed text-orbit-text-secondary">
            <span className={`mt-2 h-1.5 w-1.5 rounded-full shrink-0 ${tone === 'accent' ? 'bg-orbit-accent' : 'bg-amber-400'}`} />
            <span>{item}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}

function Pill({ children, className }: { children: ReactNode; className: string }) {
  return (
    <span className={`w-fit rounded-2xl border px-4 py-2 text-[11px] font-black uppercase tracking-[0.12em] ${className}`}>
      {children}
    </span>
  )
}
