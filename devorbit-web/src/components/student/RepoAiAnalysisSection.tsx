import { useState } from 'react'
import type { ReactNode } from 'react'
import { BookOpen, CaretDown, CheckCircle, Code, Compass, Gauge, Hash, Info, MagicWand, RocketLaunch, ShieldCheck, WarningCircle } from '@phosphor-icons/react'
import type { RepoAnalysisResult } from '../../lib/repoAnalysisService'
import { formatVietnameseRelativeDate, type RepoEvaluationResult, type UsefulnessRating } from '../../lib/repoEvaluation'
import type { RepoSummary } from '../../types/api'

type RepoAiAnalysisSectionProps = {
  repo: RepoSummary
  analysis: RepoAnalysisResult | null
  loading: boolean
  error: string | null
}

type RepoDisplayMetadata = RepoSummary & {
  forks?: number | null
  updatedAt?: string | null
  lastPushedAt?: string | null
  license?: string | { name?: string | null; spdxId?: string | null } | null
}

const ratingToneClasses: Record<UsefulnessRating, string> = {
  highly_recommended: 'border-emerald-500/25 bg-emerald-500/10 text-emerald-300',
  recommended: 'border-emerald-500/25 bg-emerald-500/10 text-emerald-300',
  selective: 'border-amber-500/25 bg-amber-500/10 text-amber-300',
  quick_reference: 'border-amber-500/25 bg-amber-500/10 text-amber-300',
  low_priority: 'border-rose-500/25 bg-rose-500/10 text-rose-300',
  insufficient_data: 'border-rose-500/25 bg-rose-500/10 text-rose-300',
}

export function RepoAiAnalysisSection({ repo, analysis, loading, error }: RepoAiAnalysisSectionProps) {
  const [detailsOpen, setDetailsOpen] = useState(false)
  const [fileTreeOpen, setFileTreeOpen] = useState(false)

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
              DevOrbit đang đọc dữ liệu repo để tạo phần tổng quan ngắn gọn.
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
              {error || 'Chưa đủ dữ liệu để phân tích. Hãy mở GitHub để kiểm tra README và source trực tiếp.'}
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
            Phân tích repo
          </span>
          <p className="mt-3 max-w-2xl text-[14px] leading-relaxed text-orbit-text-secondary">
            Nhìn nhanh repo này dùng để làm gì, có đáng xem không và cần kiểm tra gì.
          </p>
        </div>
      </div>

      <OverviewCard repo={repo} evaluation={evaluation} />
      <FileTreePreview files={evaluation.signals.filePaths} open={fileTreeOpen} onToggle={() => setFileTreeOpen((open) => !open)} />

      {(error || analysis.errorMessage || analysis.fallbackUsed) && (
        <div className="orbit-card p-5 border-amber-500/20 bg-amber-500/5">
          <div className="flex items-start gap-3">
            <WarningCircle className="h-5 w-5 text-amber-400 shrink-0 mt-0.5" weight="duotone" />
            <p className="text-[13px] leading-relaxed text-orbit-text-secondary">
              {error || analysis.errorMessage || 'Không thể tạo phân tích từ provider, DevOrbit đang dùng bộ phân tích repo cục bộ.'}
            </p>
          </div>
        </div>
      )}

      <div className="orbit-card p-5 md:p-6 border-orbit-border bg-orbit-surface/60">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h3 className="text-[15px] font-black text-orbit-text">Mở rộng cho bạn</h3>
            <p className="mt-1 text-[12px] leading-relaxed text-orbit-text-muted">
              Gợi ý cách học, cách khai thác và những điểm cần cẩn thận khi dùng repo này.
            </p>
          </div>
          <button
            type="button"
            onClick={() => setDetailsOpen((open) => !open)}
            className="inline-flex items-center justify-center gap-2 rounded-2xl border border-orbit-border bg-orbit-elevated px-4 py-3 text-[11px] font-black uppercase tracking-[0.12em] text-orbit-text-secondary transition-all hover:border-orbit-accent/30 hover:text-orbit-text"
            aria-expanded={detailsOpen}
          >
            {detailsOpen ? 'Thu gọn' : 'Mở rộng'}
            <CaretDown className={`h-4 w-4 transition-transform ${detailsOpen ? 'rotate-180' : ''}`} weight="bold" />
          </button>
        </div>
      </div>

      {detailsOpen && (
        <div className="grid gap-5 lg:grid-cols-2">
          <div className="lg:col-span-2">
            <StrengthWeaknessCard strengths={evaluation.strengths} weaknesses={evaluation.weaknesses} />
          </div>
          <NextActionsCard actions={evaluation.nextActions} />
          <LearningStrategyCard strategies={evaluation.learningStrategy} />
          <div className="lg:col-span-2">
            <CautionCard cautions={evaluation.cautionNotes} />
          </div>
        </div>
      )}
    </section>
  )
}

function OverviewCard({ repo, evaluation }: { repo: RepoSummary; evaluation: RepoEvaluationResult }) {
  const metadata = repo as RepoDisplayMetadata
  const owner = getRepoOwner(repo.githubUrl)
  const license = getLicenseLabel(metadata.license)
  const updatedAt = formatVietnameseRelativeDate(metadata.lastPushedAt ?? metadata.updatedAt)
  const forks = typeof metadata.forks === 'number' ? metadata.forks : null
  const scoreTone = ratingToneClasses[evaluation.usefulnessRating]
  const groupLabel = evaluation.repoType === 'exam_review' ? 'Tài liệu ôn tập / đề thi' : evaluation.courseGroupLabel
  const safety = buildSafetySummary(evaluation)
  const coreTopics = evaluation.coreTopics.length > 0 ? evaluation.coreTopics : ['Chưa đủ dữ liệu']
  const tools = evaluation.techTools.length > 0 ? evaluation.techTools : ['Chưa rõ']
  const coursePrefix = repo.courseCode ? `${repo.courseCode} — ` : ''
  const { readmeText, hasReadme } = evaluation.signals
  const hasRealReadme = Boolean(readmeText) && readmeText!.trim().length > 3

  const assessmentParts: string[] = []
  if (evaluation.quickBullets.length > 0) assessmentParts.push(evaluation.quickBullets[0])
  if (evaluation.coreTopics.length > 0) assessmentParts.push(`Trọng tâm: ${evaluation.coreTopics.slice(0, 3).join(', ')}`)
  assessmentParts.push(evaluation.recommendation)

  return (
    <article className="orbit-card-glow p-6 md:p-8 border-orbit-accent/20 bg-orbit-accent/5">
      <div className="mb-6 flex flex-col gap-5 lg:flex-row lg:items-start lg:justify-between">
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-3">
            <MagicWand className="h-4 w-4 text-orbit-accent" weight="fill" />
            <p className="text-[12px] font-black uppercase tracking-[0.18em] text-orbit-accent">Tổng quan nhanh</p>
          </div>

          <h3 className="mt-3 text-[20px] md:text-[24px] font-black leading-tight text-orbit-text break-words">
            {coursePrefix}{evaluation.signals.name}
          </h3>

          <span className="mt-2 inline-flex items-center gap-1.5 rounded-full border border-orbit-accent/20 bg-orbit-accent/8 px-3 py-1 text-[11px] font-bold text-orbit-accent">
            {evaluation.repoTypeLabel}
          </span>

          <div className="mt-5 rounded-xl border border-orbit-accent/15 bg-orbit-accent/[0.04] p-4 space-y-3">
            <div className="flex gap-3">
              <Info className="h-5 w-5 text-orbit-accent shrink-0 mt-0.5" weight="duotone" />
              <div className="min-w-0 flex-1">
                <p className="text-[14px] font-semibold leading-relaxed text-orbit-text">{evaluation.quickSummary}</p>

                {hasRealReadme && (
                  <div className="mt-4 rounded-lg border border-orbit-border/60 bg-orbit-surface/40 p-3.5">
                    <div className="flex items-center gap-2 mb-2.5">
                      <BookOpen className="h-3.5 w-3.5 text-orbit-accent" weight="duotone" />
                      <p className="text-[11px] font-black uppercase tracking-[0.16em] text-orbit-text-muted">README nói gì?</p>
                    </div>
                    <ul className="space-y-1.5">
                      {buildReadmeInsightBullets(evaluation).map((item) => (
                        <Bullet key={item}>{item}</Bullet>
                      ))}
                    </ul>
                  </div>
                )}

                {!hasRealReadme && hasReadme && (
                  <p className="mt-3 text-[12px] italic text-orbit-text-muted">
                    README có tồn tại nhưng chưa đủ nội dung để tóm tắt.
                  </p>
                )}

                <p className="mt-3 text-[13px] leading-relaxed text-orbit-text-secondary">
                  <span className="font-bold text-orbit-text">Đánh giá nhanh: </span>
                  {assessmentParts.join(' • ')}
                </p>
              </div>
            </div>
          </div>
        </div>

        <div className={`shrink-0 w-fit rounded-2xl border px-5 py-4 ${scoreTone}`}>
          <p className="text-[10px] font-black uppercase tracking-[0.14em]">Điểm đáng xem</p>
          <p className="mt-1 text-3xl font-black tabular-nums leading-none">{evaluation.usefulnessScore}</p>
          <p className="mt-1 text-[11px] font-black uppercase tracking-[0.12em]">{evaluation.usefulnessLabel}</p>
        </div>
      </div>

      <div className="rounded-xl border border-orbit-border/80 bg-orbit-surface/40 p-5 space-y-5">
        <SectionBlock title="Thông tin repo" icon={<Info className="h-3.5 w-3.5" weight="duotone" />}>
          <div className="grid gap-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
            <MetaItem label="Owner" value={owner || 'Chưa rõ'} />
            <MetaItem label="Ngôn ngữ chính" value={repo.primaryLanguage || 'Chưa rõ'} />
            <MetaItem label="License" value={license || 'Chưa rõ license'} />
            <MetaItem label="Số sao" value={repo.stars !== null ? repo.stars.toLocaleString('vi-VN') : 'Chưa rõ'} />
            <MetaItem label="Fork" value={forks !== null ? forks.toLocaleString('vi-VN') : 'Chưa có dữ liệu fork'} />
            <MetaItem label="Cập nhật lần cuối" value={updatedAt || 'Chưa có dữ liệu cập nhật'} />
            <MetaItem label="Kiểu repo" value={evaluation.repoIdentity} />
            <MetaItem label="Phân nhóm" value={groupLabel} />
          </div>
        </SectionBlock>

        <SectionDivider />

        <SectionBlock title="Công nghệ / công cụ" icon={<Code className="h-3.5 w-3.5" weight="duotone" />}>
          <TagList items={tools.slice(0, 6)} />
          <p className="mt-2 text-[13px] font-bold leading-relaxed text-orbit-text-secondary">
            {evaluation.bestFor}
          </p>
        </SectionBlock>

        <SectionDivider />

        <SectionBlock title="Chủ đề chính" icon={<Hash className="h-3.5 w-3.5" weight="duotone" />}>
          <TagList items={coreTopics.slice(0, 7)} />
          <p className="mt-2 text-[13px] font-bold leading-relaxed text-orbit-text-secondary">
            {evaluation.groupHighlights.slice(0, 2).join(' ')}
          </p>
        </SectionBlock>

        <SectionDivider />

        <div className="grid gap-4 md:grid-cols-2">
          <SectionBlock title="Mức độ sẵn sàng" icon={<Gauge className="h-3.5 w-3.5" weight="duotone" />}>
            <p className="text-[15px] font-black leading-tight text-orbit-text">{evaluation.readyToUseLabel}</p>
            <p className="mt-1 text-[20px] font-black tracking-[0.08em] text-orbit-accent">{renderStars(evaluation.readyToUseStars)}</p>
            <p className="mt-2 text-[13px] font-bold leading-relaxed text-orbit-text-secondary">{evaluation.readyToUseNote}</p>
          </SectionBlock>

          <SectionBlock title="Độ an toàn" icon={<ShieldCheck className="h-3.5 w-3.5" weight="duotone" />}>
            <div className="flex flex-wrap items-center gap-3">
              <ShieldCheck className={`h-5 w-5 ${safety.tone}`} weight="duotone" />
              <p className="text-[15px] font-black text-orbit-text">{renderStars(safety.stars)} ({safety.score}/5) — Rủi ro {safety.risk}</p>
            </div>
            <p className="mt-2 text-[13px] font-bold leading-relaxed text-orbit-text-secondary">{safety.reason}</p>
            <p className="mt-1 text-[12px] leading-relaxed text-orbit-text-muted">{safety.advice}</p>
          </SectionBlock>
        </div>
      </div>

      <div className="mt-5 grid gap-5 lg:grid-cols-[1fr_1fr]">
        <InfoPanel title="Nên dùng để" icon={<Compass className="h-3.5 w-3.5" weight="duotone" />}>
          <p className="text-[13px] font-bold leading-relaxed text-orbit-text-secondary">{evaluation.recommendation}</p>
          <ul className="mt-3 space-y-2">
            {evaluation.suitableUse.slice(0, 3).map((item) => (
              <Bullet key={item}>{item}</Bullet>
            ))}
          </ul>
        </InfoPanel>

        <InfoPanel title="Cần kiểm tra" icon={<WarningCircle className="h-3.5 w-3.5" weight="duotone" />}>
          <div className="flex flex-wrap gap-2">
            {evaluation.checksBeforeUsing.slice(0, 4).map((item) => (
              <span key={item} className="rounded-full border border-amber-500/20 bg-amber-500/10 px-3 py-1.5 text-[12px] font-bold text-amber-200">
                {item}
              </span>
            ))}
          </div>
        </InfoPanel>
      </div>
    </article>
  )
}

function buildReadmeInsightBullets(evaluation: RepoEvaluationResult): string[] {
  const signals = evaluation.signals
  const rawText = signals.readmeText ?? ''
  const text = normalizeText(rawText)
  const items: string[] = []

  // 1. What the repo is used for — từ README, không phải description
  const firstSentence = rawText.split(/[.!?\n]/).find((s) => s.trim().length > 20)
  if (firstSentence) {
    items.push(`README mô tả: ${firstSentence.trim()}.`)
  } else {
    const purpose = evaluation.repoType === 'programming_exercise'
      ? `Repo phục vụ ${evaluation.repoIdentity.toLowerCase()}${evaluation.weapons !== 'Chưa rõ' ? ` với ${evaluation.weapons}` : ''}`
      : evaluation.repoType === 'project_practice'
        ? 'Repo nghiêng về project có source/setup cần kiểm tra khi clone'
        : evaluation.repoType === 'study_material'
          ? 'Repo là nguồn tài liệu học hoặc note/slide để đọc theo chủ đề'
          : evaluation.repoType === 'exam_review'
            ? 'Repo dùng cho ôn tập, đề thi hoặc đối chiếu lời giải'
            : `Repo thuộc dạng ${evaluation.repoTypeLabel.toLowerCase()}`
    items.push(`${purpose}.`)
  }

  // 2. README hướng dẫn gì
  if (containsAny(text, ['setup', 'install', 'run', 'docker', 'npm', 'mvn', 'gradle', 'usage'])) {
    items.push('README có hướng dẫn setup/chạy hoặc cách sử dụng.')
  } else if (containsAny(text, ['lab', 'assignment', 'exercise', 'chapter', 'week', 'tuan', 'chuong'])) {
    items.push('Nội dung được chia theo lab/bài/chapter, dễ đọc theo từng phần.')
  } else if (evaluation.coreTopics.length > 0) {
    items.push(`Trọng tâm README: ${evaluation.coreTopics.slice(0, 4).join(', ')}.`)
  } else {
    items.push('Nên đối chiếu README với cây thư mục để biết repo gồm những phần nào.')
  }

  // 3. Cảnh báo / hướng dẫn học
  if (containsAny(text, ['caution', 'warning', 'note', 'important', 'chú ý', 'lưu ý', 'cảnh báo'])) {
    items.push('README có cảnh báo hoặc lưu ý quan trọng cần đọc trước khi dùng.')
  } else if (containsAny(text, ['setup', 'install', 'run', 'docker', 'npm', 'mvn', 'gradle', 'usage'])) {
    items.push('Nên đọc kỹ hướng dẫn setup trước khi clone chạy local.')
  } else if (evaluation.repoType === 'programming_exercise') {
    items.push('Nên kiểm tra đề bài và test case trước khi dùng làm lời giải mẫu.')
  } else {
    items.push('Nên bắt đầu từ README, sau đó mở các folder/file chính để xác nhận phạm vi.')
  }

  return items.slice(0, 3)
}

function FileTreePreview({ files, open, onToggle }: { files: string[]; open: boolean; onToggle: () => void }) {
  if (files.length === 0) return null
  const visibleFiles = files.slice(0, open ? 24 : 8)
  return (
    <div className="orbit-card p-5 border-orbit-border bg-orbit-surface/60">
      <button type="button" onClick={onToggle} className="flex w-full items-center justify-between gap-3 text-left" aria-expanded={open}>
        <span>
          <span className="block text-[13px] font-black text-orbit-text">Cây thư mục</span>
          <span className="mt-1 block text-[12px] text-orbit-text-muted">{files.length} path được dùng để nhìn nhanh cấu trúc repo</span>
        </span>
        <CaretDown className={`h-4 w-4 shrink-0 text-orbit-text-muted transition-transform ${open ? 'rotate-180' : ''}`} weight="bold" />
      </button>
      <pre className="mt-4 max-h-72 overflow-auto rounded-xl border border-orbit-border bg-orbit-elevated/70 p-4 text-[12px] leading-6 text-orbit-text-secondary">
        {visibleFiles.map((file) => `- ${file}`).join('\n')}
        {files.length > visibleFiles.length ? `\n... ${files.length - visibleFiles.length} file/folder khác` : ''}
      </pre>
    </div>
  )
}

function StrengthWeaknessCard({ strengths, weaknesses }: { strengths: string[]; weaknesses: string[] }) {
  return (
    <article className="orbit-card p-6 border-orbit-border bg-orbit-surface/70">
      <div className="mb-4 flex items-center gap-3">
        <CheckCircle className="h-5 w-5 text-orbit-accent" weight="duotone" />
        <h3 className="text-[15px] font-black text-orbit-text">Điểm mạnh / Điểm yếu</h3>
      </div>
      <p className="mb-4 text-[12px] leading-relaxed text-orbit-text-muted">
        Những ưu điểm và hạn chế của repo dựa trên dữ liệu hiện có.
      </p>
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
      <div className="mb-4 flex items-center gap-3">
        <RocketLaunch className="h-5 w-5 text-orbit-accent" weight="duotone" />
        <h3 className="text-[15px] font-black text-orbit-text">Hành động tiếp theo</h3>
      </div>
      <p className="mb-3 text-[12px] leading-relaxed text-orbit-text-muted">
        Các bước cụ thể để bắt đầu với repo này.
      </p>
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

function LearningStrategyCard({ strategies }: { strategies: string[] }) {
  return (
    <article className="orbit-card p-6 border-orbit-border bg-orbit-surface/70">
      <div className="mb-4 flex items-center gap-3">
        <BookOpen className="h-5 w-5 text-indigo-400" weight="duotone" />
        <h3 className="text-[15px] font-black text-orbit-text">Chiến lược học tập với repo</h3>
      </div>
      <p className="mb-3 text-[12px] leading-relaxed text-orbit-text-muted">
        Cách khai thác repo này hiệu quả theo đúng loại nội dung.
      </p>
      <ul className="space-y-3">
        {strategies.map((item) => (
          <Bullet key={item}>{item}</Bullet>
        ))}
      </ul>
    </article>
  )
}

function CautionCard({ cautions }: { cautions: string[] }) {
  return (
    <article className="orbit-card p-6 border-orbit-border bg-orbit-surface/70">
      <div className="mb-4 flex items-center gap-3">
        <WarningCircle className="h-5 w-5 text-amber-300" weight="duotone" />
        <h3 className="text-[15px] font-black text-orbit-text">Cần cẩn thận</h3>
      </div>
      <p className="mb-3 text-[12px] leading-relaxed text-orbit-text-muted">
        Những điểm dễ sai hoặc rủi ro khi dùng repo này.
      </p>
      <ul className="space-y-3">
        {cautions.map((item) => (
          <Bullet key={item}>{item}</Bullet>
        ))}
      </ul>
    </article>
  )
}

function InfoPanel({ title, children, icon }: { title: string; children: ReactNode; icon?: ReactNode }) {
  return (
    <div className="rounded-xl border border-orbit-border/80 bg-orbit-surface/55 p-4">
      <div className="flex items-center gap-2 mb-3">
        {icon && <span className="text-orbit-accent shrink-0">{icon}</span>}
        <p className="text-[11px] font-black uppercase tracking-[0.16em] text-orbit-text-secondary">{title}</p>
      </div>
      {children}
    </div>
  )
}

function MetaItem({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-lg border border-orbit-border/60 bg-orbit-surface/40 px-3 py-2.5 min-h-[3.25rem] flex flex-col justify-center">
      <p className="text-[10px] font-black uppercase tracking-[0.12em] text-orbit-text-muted mb-0.5">{label}</p>
      <p className="text-[13px] font-bold text-orbit-text-secondary break-words whitespace-normal leading-snug">{value}</p>
    </div>
  )
}

function SectionBlock({ title, children, icon }: { title: string; children: ReactNode; icon?: ReactNode }) {
  return (
    <div>
      <div className="flex items-center gap-2 mb-3">
        {icon && <span className="text-orbit-accent shrink-0">{icon}</span>}
        <p className="text-[11px] font-black uppercase tracking-[0.16em] text-orbit-text-secondary">{title}</p>
      </div>
      {children}
    </div>
  )
}

function SectionDivider() {
  return <div className="border-t border-orbit-border/60" />
}

function TagList({ items }: { items: string[] }) {
  return (
    <div className="flex flex-wrap gap-2">
      {items.map((item) => (
        <span key={item} className="rounded-full border border-orbit-border bg-orbit-surface/70 px-3 py-1.5 text-[12px] font-bold text-orbit-text-secondary">
          {item}
        </span>
      ))}
    </div>
  )
}

function Bullet({ children }: { children: ReactNode }) {
  return (
    <li className="flex gap-3 text-[13px] leading-relaxed text-orbit-text-secondary">
      <span className="mt-2 h-1.5 w-1.5 rounded-full bg-orbit-accent shrink-0" />
      <span>{children}</span>
    </li>
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
          <Bullet key={item}>{item}</Bullet>
        ))}
      </ul>
    </div>
  )
}

function buildSafetySummary(evaluation: RepoEvaluationResult) {
  const text = normalizeText([evaluation.signals.readmeText, evaluation.signals.description, ...evaluation.signals.filePaths].join(' '))
  const hasExecutableRisk = containsAny(text, ['subprocess', 'os.system', 'exec(', 'eval(', 'shell', 'socket', 'request', 'scrape', 'crawl', 'network', 'chmod', 'sudo'])
  const hasCodeOrTool = evaluation.signals.hasSourceCode || evaluation.repoType === 'project_practice'
  const missingGuidance = !evaluation.signals.hasReadme || !evaluation.signals.hasTests

  if (!hasCodeOrTool) {
    return {
      stars: 4,
      score: '4.0',
      risk: 'Thấp',
      tone: 'text-orbit-accent',
      reason: 'Chưa thấy yếu tố code/tool/script có rủi ro nổi bật từ dữ liệu hiện có.',
      advice: 'Phù hợp để tham khảo, vẫn nên kiểm tra nguồn tài liệu và phạm vi trước khi dùng.',
    }
  }

  if (hasExecutableRisk) {
    return {
      stars: 3,
      score: '3.0',
      risk: 'Trung bình',
      tone: 'text-amber-300',
      reason: 'Có dấu hiệu thao tác hệ thống, network hoặc script cần đọc kỹ trước khi chạy.',
      advice: 'Không chạy trực tiếp với quyền cao nếu chưa đọc source và hiểu lệnh thực thi.',
    }
  }

  if (missingGuidance) {
    return {
      stars: 3,
      score: '3.5',
      risk: 'Trung bình',
      tone: 'text-amber-300',
      reason: 'Có code/source nhưng hướng dẫn hoặc test chưa thật rõ.',
      advice: 'Phù hợp để tham khảo, vẫn nên kiểm tra đề bài/test case hoặc hướng dẫn chạy trước khi dùng.',
    }
  }

  return {
    stars: 4,
    score: '4.2',
    risk: 'Thấp',
    tone: 'text-orbit-accent',
    reason: 'Dữ liệu hiện có cho thấy repo tương đối rõ mục tiêu và cấu trúc.',
    advice: 'Có thể dùng để học/tham khảo, vẫn nên đọc README và kiểm tra license trước khi tái sử dụng.',
  }
}

function getRepoOwner(url: string): string | null {
  const match = url.match(/github\.com\/([^/]+)\/([^/?#]+)/i)
  return match ? match[1] : null
}

function getLicenseLabel(license: RepoDisplayMetadata['license']): string | null {
  if (!license) return null
  if (typeof license === 'string') return license
  return license.spdxId || license.name || null
}

function normalizeText(value: string | null): string {
  return (value ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
}

function containsAny(text: string, keywords: string[]): boolean {
  return keywords.some((keyword) => text.includes(keyword))
}

function renderStars(value: number): string {
  const filled = Math.max(1, Math.min(5, value))
  return `${'★'.repeat(filled)}${'☆'.repeat(5 - filled)}`
}
