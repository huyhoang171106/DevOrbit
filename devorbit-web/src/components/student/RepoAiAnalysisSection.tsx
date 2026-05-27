import { useState } from 'react'
import { BookOpen, CaretDown, CheckCircle, MagicWand, RocketLaunch, WarningCircle } from '@phosphor-icons/react'
import type { RepoAnalysisResult } from '../../lib/repoAnalysisService'
import type { RepoAiAnalysisSection as RepoAiAnalysisSectionModel, RepoAiAnalysisTone } from '../../lib/repoAiAnalysis'

type RepoAiAnalysisSectionProps = {
  analysis: RepoAnalysisResult | null
  loading: boolean
  error: string | null
}

type DecisionLabel = 'Nên xem' | 'Xem có chọn lọc' | 'Chỉ tham khảo' | 'Không đủ dữ liệu'

type DecisionView = {
  conclusion: string
  decision: DecisionLabel
  tone: RepoAiAnalysisTone
  reason: string
  strengths: string[]
  weaknesses: string[]
  actions: string[]
  details: Array<{
    title: string
    items: string[]
  }>
}

const decisionToneClasses: Record<RepoAiAnalysisTone, string> = {
  accent: 'border-orbit-accent/25 bg-orbit-accent/10 text-orbit-accent',
  indigo: 'border-indigo-500/20 bg-indigo-500/10 text-indigo-300',
  amber: 'border-amber-500/25 bg-amber-500/10 text-amber-300',
  rose: 'border-rose-500/25 bg-rose-500/10 text-rose-300',
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
              {error || 'Chưa đủ dữ liệu để phân tích sâu. Hãy mở GitHub để kiểm tra README và source trực tiếp.'}
            </p>
          </div>
        </div>
      </section>
    )
  }

  const sectionsByKey = new Map(analysis.sections.map((section) => [section.key, section]))
  const decision = buildDecisionView(sectionsByKey)

  return (
    <section className="space-y-7">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-5">
        <div>
          <span className="section-label mb-4 inline-flex">
            <MagicWand className="h-3 w-3" weight="fill" />
            AI analysis
          </span>
          <h2 className="heading-3 text-orbit-text">Phân tích repository</h2>
          <p className="mt-3 max-w-2xl text-[14px] leading-relaxed text-orbit-text-secondary">
            Đánh giá nhanh để quyết định repo này có đáng mở, đáng học, hay chỉ nên tham khảo.
          </p>
        </div>
        <div className="flex items-center gap-3 rounded-2xl border border-orbit-border bg-orbit-surface px-4 py-3">
          <span className="h-2 w-2 rounded-full bg-orbit-accent" />
          <span className="text-[10px] font-black uppercase tracking-[0.16em] text-orbit-text-muted">
            {analysis.source === 'rule-based' ? 'Rule-based' : 'AI provider'}
          </span>
        </div>
      </div>

      <QuickDecisionCard decision={decision} />

      {(error || analysis.errorMessage || analysis.fallbackUsed) && (
        <div className="orbit-card p-5 border-amber-500/20 bg-amber-500/5">
          <div className="flex items-start gap-3">
            <WarningCircle className="h-5 w-5 text-amber-400 shrink-0 mt-0.5" weight="duotone" />
            <p className="text-[13px] leading-relaxed text-orbit-text-secondary">
              {error || analysis.errorMessage || 'Không thể tạo phân tích chi tiết, đang dùng phân tích cơ bản từ dữ liệu repo hiện có.'}
            </p>
          </div>
        </div>
      )}

      <div className="grid gap-5 lg:grid-cols-[1fr_0.9fr]">
        <StrengthWeaknessCard strengths={decision.strengths} weaknesses={decision.weaknesses} />
        <NextActionsCard actions={decision.actions} />
      </div>

      <div className="orbit-card p-5 md:p-6 border-orbit-border bg-orbit-surface/60">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h3 className="text-[15px] font-black text-orbit-text">Phân tích chi tiết</h3>
            <p className="mt-1 text-[12px] leading-relaxed text-orbit-text-muted">
              Mở khi cần xem lý do phía sau đánh giá nhanh.
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
        <div className="grid gap-5 lg:grid-cols-3">
          {decision.details.map((group) => (
            <DetailGroupCard key={group.title} title={group.title} items={group.items} />
          ))}
        </div>
      )}
    </section>
  )
}

function QuickDecisionCard({ decision }: { decision: DecisionView }) {
  return (
    <article className="orbit-card-glow p-6 md:p-8 border-orbit-accent/20 bg-orbit-accent/5">
      <div className="mb-5 flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div className="flex items-center gap-3">
          <div className="h-11 w-11 rounded-2xl border border-orbit-accent/20 bg-orbit-accent/10 flex items-center justify-center shrink-0">
            <MagicWand className="h-5 w-5 text-orbit-accent" weight="duotone" />
          </div>
          <div>
            <p className="text-[10px] font-black uppercase tracking-[0.18em] text-orbit-accent">Đánh giá nhanh</p>
            <h3 className="mt-1 text-[20px] font-black text-orbit-text leading-tight">Repo này có đáng xem không?</h3>
          </div>
        </div>
        <span className={`w-fit rounded-2xl border px-4 py-2 text-[11px] font-black uppercase tracking-[0.12em] ${decisionToneClasses[decision.tone]}`}>
          {decision.decision}
        </span>
      </div>

      <p className="text-[16px] font-bold leading-relaxed text-orbit-text">
        {decision.conclusion}
      </p>
      <p className="mt-3 max-w-3xl text-[14px] leading-relaxed text-orbit-text-secondary">
        {decision.reason}
      </p>
    </article>
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
        <DecisionList title="Điểm mạnh" items={strengths} tone="accent" />
        <DecisionList title="Điểm yếu" items={weaknesses} tone="amber" />
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

function DecisionList({ title, items, tone }: { title: string; items: string[]; tone: 'accent' | 'amber' }) {
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

function buildDecisionView(sectionsByKey: Map<RepoAiAnalysisSectionModel['key'], RepoAiAnalysisSectionModel>): DecisionView {
  const overview = sectionsByKey.get('overview')
  const technology = sectionsByKey.get('technology')
  const fit = sectionsByKey.get('fit')
  const readme = sectionsByKey.get('readmeInsights')
  const reviewFirst = sectionsByKey.get('reviewFirst')
  const warnings = sectionsByKey.get('warnings')
  const fitLevel = extractLevel(fit?.items, ['cao', 'vừa', 'thấp'])
  const completeness = extractLevel(overview?.items, ['đầy đủ', 'trung bình', 'ít'])
  const warningItems = prioritizeWarnings(warnings?.items ?? [])
  const hasReadme = Boolean(readme && !containsAny(readme.content, ['Chưa có README', 'thiếu README']))
  const decision = chooseDecision(fitLevel, completeness, hasReadme)
  const strengths = buildStrengths(overview, technology, fit, readme)
  const weaknesses = buildWeaknesses(warningItems, hasReadme)
  const actions = buildActions(hasReadme, technology, completeness)

  return {
    ...decision,
    strengths,
    weaknesses,
    actions,
    details: [
      {
        title: 'Repo nói về gì?',
        items: compactItems([
          hasConcreteOverview(overview) ? 'Repo có mô tả đủ để xác định hướng đọc ban đầu.' : 'Chưa rõ mục tiêu repo; cần kiểm tra README hoặc source.',
          hasReadme ? summarizeReadme(readme) : null,
          technology?.items?.[0] ? simplifyTechItem(technology.items[0]) : null,
        ], 3),
      },
      {
        title: 'Có áp dụng được không?',
        items: compactItems([
          buildFitDetail(fitLevel),
          hasReadme ? 'Có thể cân nhắc dùng cho học tập nếu README hướng dẫn chạy rõ.' : 'Chưa nên dùng làm nguồn chính cho deadline khi thiếu README/setup.',
          technology?.items?.[1] ? simplifyLearningPrep(technology.items[1]) : null,
        ], 3),
      },
      {
        title: 'Cần kiểm tra trước khi tin',
        items: compactItems([
          ...warningItems.slice(0, 3).map(simplifyWarning),
          warningItems.length === 0 ? reviewFirst?.items?.[0] : null,
        ], 3),
      },
    ],
  }
}

function chooseDecision(
  fitLevel: string | null,
  completeness: string | null,
  hasReadme: boolean,
): Pick<DecisionView, 'conclusion' | 'decision' | 'tone' | 'reason'> {
  if (completeness === 'ít') {
    return {
      decision: 'Không đủ dữ liệu',
      tone: 'rose',
      conclusion: 'Chưa đủ dữ liệu để đánh giá repo này là nguồn học đáng tin.',
      reason: 'Nên mở GitHub kiểm tra README, source chính và cách chạy trước. Nếu chưa xác minh được setup, chỉ dùng repo để tham khảo ý tưởng.',
    }
  }

  if (fitLevel === 'cao' && hasReadme) {
    return {
      decision: 'Nên xem',
      tone: 'accent',
      conclusion: 'Repo này đáng xem vì có đủ tín hiệu để bắt đầu học hoặc tham khảo code.',
      reason: 'Vẫn cần kiểm tra repo có chạy được không trước khi dùng cho deadline. Nếu README/setup rõ, đây có thể là nguồn tham khảo tốt.',
    }
  }

  if (fitLevel === 'thấp') {
    return {
      decision: 'Chỉ tham khảo',
      tone: 'amber',
      conclusion: 'Repo này chỉ nên dùng để lấy ý tưởng, chưa nên xem là nguồn chính.',
      reason: 'Dữ liệu hiện có chưa đủ mạnh để tin vào chất lượng hoặc khả năng chạy. Trước khi dùng cho deadline, cần xác minh README và source.',
    }
  }

  return {
    decision: 'Xem có chọn lọc',
    tone: 'amber',
    conclusion: 'Repo này có thể hữu ích, nhưng cần kiểm tra kỹ trước khi dùng.',
    reason: hasReadme
      ? 'Có một số tín hiệu tốt, nhưng vẫn nên xác nhận setup, source chính và phạm vi project trước khi dựa vào repo.'
      : 'Thiếu README hoặc tín hiệu sâu, nên chỉ nên đọc chọn lọc và không dùng làm nguồn chính khi chưa kiểm chứng.',
  }
}

function buildStrengths(
  overview: RepoAiAnalysisSectionModel | undefined,
  technology: RepoAiAnalysisSectionModel | undefined,
  fit: RepoAiAnalysisSectionModel | undefined,
  readme: RepoAiAnalysisSectionModel | undefined,
): string[] {
  return compactItems([
    hasConcreteOverview(overview) ? 'Có mô tả giúp hiểu repo đang giải quyết việc gì.' : null,
    technology?.items?.[0] && !technology.items[0].includes('Chưa') ? simplifyTechItem(technology.items[0]) : null,
    readme && !readme.content.includes('Chưa có README') ? 'Có README để kiểm tra mục tiêu hoặc cách chạy.' : null,
    fit?.items?.[1]?.includes('môn') ? 'Có ngữ cảnh môn học để đối chiếu khi học.' : null,
  ], 2, ['Chưa thấy điểm mạnh rõ từ dữ liệu hiện có.'])
}

function buildWeaknesses(warnings: string[], hasReadme: boolean): string[] {
  return compactItems([
    !hasReadme ? 'Thiếu README nên chưa biết cách chạy/setup.' : null,
    ...warnings.map(simplifyWarning),
  ], 2, ['Chưa thấy rủi ro lớn ngoài việc cần kiểm tra trực tiếp trên GitHub.'])
}

function buildActions(
  hasReadme: boolean,
  technology: RepoAiAnalysisSectionModel | undefined,
  completeness: string | null,
): string[] {
  const configHint = technology?.items?.find((item) => /File nên kiểm tra|package|pom|gradle|config|dependency/i.test(item))
  return [
    hasReadme ? 'Đọc README để xác nhận mục tiêu, setup và cách chạy.' : 'Kiểm tra repo có README không; nếu thiếu, đọc description và source trước.',
    configHint ? simplifyConfigHint(configHint) : 'Mở source chính hoặc file build/config để xác nhận project chạy được.',
    completeness === 'đầy đủ'
      ? 'Nếu setup rõ, clone/fork để chạy thử trước khi dùng cho deadline.'
      : 'Chỉ dùng repo làm tham khảo nếu chưa xác minh được setup.',
  ]
}

function compactItems(items: Array<string | null | undefined>, limit: number, fallback: string[] = []): string[] {
  const seen = new Set<string>()
  const result: string[] = []
  for (const item of items) {
    const normalized = item?.trim()
    if (!normalized) continue
    const key = normalized.toLowerCase()
    if (seen.has(key)) continue
    seen.add(key)
    result.push(normalized)
    if (result.length >= limit) break
  }
  return result.length > 0 ? result : fallback
}

function extractLevel(items: string[] | undefined, levels: string[]): string | null {
  const text = (items ?? []).join(' ').toLowerCase()
  return levels.find((level) => text.includes(level)) ?? null
}

function hasConcreteOverview(section: RepoAiAnalysisSectionModel | undefined): boolean {
  if (!section) return false
  return !containsAny(section.content, ['Chưa đủ dữ liệu', 'thiếu description'])
}

function summarizeReadme(section: RepoAiAnalysisSectionModel | undefined): string | null {
  if (!section || section.content.includes('Chưa có README')) return null
  if (/setup|run|mvn|npm|docker|gradle|pip|python/i.test(section.content)) {
    return 'README có tín hiệu về setup hoặc cách chạy.'
  }
  return 'README có thêm ngữ cảnh để hiểu mục tiêu repo.'
}

function buildFitDetail(fitLevel: string | null): string {
  if (fitLevel === 'cao') return 'Phù hợp để học hoặc tham khảo code nếu chạy được.'
  if (fitLevel === 'vừa') return 'Phù hợp để xem chọn lọc, chưa nên dùng nguyên làm nguồn chính.'
  if (fitLevel === 'thấp') return 'Chỉ nên dùng để lấy ý tưởng ban đầu.'
  return 'Chưa đủ dữ liệu để kết luận mức độ áp dụng.'
}

function prioritizeWarnings(items: string[]): string[] {
  const priority = [/README/i, /mô tả|description/i, /techStacks|công nghệ|primaryLanguage/i, /topics/i, /course/i, /updatedAt|forks/i]
  return [...items].sort((a, b) => warningScore(b, priority) - warningScore(a, priority))
}

function warningScore(item: string, priority: RegExp[]): number {
  const index = priority.findIndex((pattern) => pattern.test(item))
  return index === -1 ? 0 : priority.length - index
}

function simplifyWarning(item: string): string {
  if (/README/i.test(item)) return 'Thiếu README nên chưa biết cách chạy/setup.'
  if (/description|mô tả/i.test(item)) return 'Thiếu mô tả nên chưa rõ mục tiêu repo.'
  if (/techStacks|primaryLanguage|công nghệ/i.test(item)) return 'Chưa rõ stack nên cần mở source/config để kiểm tra.'
  if (/topics/i.test(item)) return 'Thiếu topics nên khó xác định phạm vi project.'
  if (/course|courseCode|courseName/i.test(item)) return 'Chưa rõ repo gắn với môn học nào.'
  if (/updatedAt|lastPushedAt/i.test(item)) return 'Chưa biết repo còn được cập nhật gần đây không.'
  if (/forks/i.test(item)) return 'Chưa có forks để tham khảo mức độ được dùng lại.'
  return stripPrefix(item)
}

function simplifyTechItem(item: string): string {
  return stripPrefix(item)
    .replace('Tech stack đang có:', 'Stack chính:')
    .replace('Ngôn ngữ chính được GitHub ghi nhận là', 'Ngôn ngữ chính:')
}

function simplifyLearningPrep(item: string): string {
  return stripPrefix(item).replace('Người học nên chuẩn bị:', 'Cần chuẩn bị:')
}

function simplifyConfigHint(item: string): string {
  return stripPrefix(item).replace('File nên kiểm tra sớm:', 'Mở file cấu hình/build:')
}

function stripPrefix(value: string): string {
  return value.replace(/\s+/g, ' ').trim()
}

function containsAny(value: string, terms: string[]): boolean {
  return terms.some((term) => value.toLowerCase().includes(term.toLowerCase()))
}
