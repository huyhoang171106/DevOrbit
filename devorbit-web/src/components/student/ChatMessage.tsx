import { useState, useMemo, useEffect, useCallback, memo } from 'react'
import {
  Copy, Check, CheckCircle, CaretDown, Globe, Database, BookOpen,
  MagnifyingGlass, Sparkle, Spinner as SpinnerIcon, Link as LinkIcon, X, ChatTeardropText,
} from '@phosphor-icons/react'
import type { AiChatMessage, AiChatStatusEvent } from './ChatContext'
import type { WebSearchResult } from '../../hooks/useSubjectQa'
import type { RoadmapResponse } from '../../hooks/useAiRoadmap'

// ─── Helpers ───

function getSourceLabel(url: string): string {
  try {
    return new URL(url).hostname.replace(/^www\./, '')
  } catch {
    return url
  }
}

function sanitizeUrl(url: string): string | null {
  if (!url) return null
  const lower = url.toLowerCase()
  if (lower.startsWith('http://') || lower.startsWith('https://') || lower.startsWith('mailto:')) {
    return url
  }
  return null
}

type RoadmapCourse = NonNullable<RoadmapResponse['recommendedCourses']>[number]

interface RoadmapSemesterNode {
  key: string
  label: string
  semester: number | null
  courses: RoadmapCourse[]
  credits: number
}

function groupRoadmapCoursesBySemester(courses: readonly RoadmapCourse[]): RoadmapSemesterNode[] {
  const groups = new Map<string, RoadmapSemesterNode>()

  for (const course of courses) {
    const semester = typeof course.semester === 'number' && Number.isFinite(course.semester) ? course.semester : null
    const key = semester != null ? `semester-${semester}` : 'semester-unknown'
    const existing = groups.get(key)
    const nextGroup: RoadmapSemesterNode = existing ?? {
      key,
      semester,
      label: semester != null ? `Học kỳ ${semester}` : 'Chưa xếp học kỳ',
      courses: [],
      credits: 0,
    }

    nextGroup.courses.push(course)
    nextGroup.credits += course.credits ?? 0
    groups.set(key, nextGroup)
  }

  return Array.from(groups.values())
    .sort((a, b) => {
      if (a.semester == null && b.semester == null) return a.label.localeCompare(b.label)
      if (a.semester == null) return 1
      if (b.semester == null) return -1
      return a.semester - b.semester
    })
    .map((group) => ({
      ...group,
      courses: group.courses.toSorted((a, b) => {
        const mandatoryA = a.isMandatory ? 0 : 1
        const mandatoryB = b.isMandatory ? 0 : 1
        if (mandatoryA !== mandatoryB) return mandatoryA - mandatoryB
        const codeA = a.courseCode ?? a.courseName ?? ''
        const codeB = b.courseCode ?? b.courseName ?? ''
        return codeA.localeCompare(codeB)
      }),
    }))
}

// ─── Inline Markdown Tokeniser ───

type InlineToken =
  | { type: 'text'; text: string }
  | { type: 'bold'; text: string }
  | { type: 'italic'; text: string }
  | { type: 'code'; text: string }
  | { type: 'link'; text: string; url: string }

function parseInline(text: string): InlineToken[] {
  const tokens: InlineToken[] = []
  const regex = /(\*\*(.+?)\*\*)|(\*(.+?)\*)|(`([^`]+)`)|(\[([^\]]+)\]\(([^)]+)\))/g
  let lastIndex = 0
  let match: RegExpExecArray | null
  while ((match = regex.exec(text)) !== null) {
    if (match.index > lastIndex) {
      tokens.push({ type: 'text', text: text.slice(lastIndex, match.index) })
    }
    if (match[1]) {
      tokens.push({ type: 'bold', text: match[2] })
    } else if (match[3]) {
      tokens.push({ type: 'italic', text: match[4] })
    } else if (match[5]) {
      tokens.push({ type: 'code', text: match[6] })
    } else if (match[7]) {
      const url = sanitizeUrl(match[9])
      tokens.push({ type: 'link', text: match[8], url: url || match[9] })
    }
    lastIndex = match.index + match[0].length
  }
  if (lastIndex < text.length) {
    tokens.push({ type: 'text', text: text.slice(lastIndex) })
  }
  return tokens
}

function inlineToReact(tokens: InlineToken[], keyPrefix: string): React.ReactNode[] {
  return tokens.map((token, i) => {
    const key = `${keyPrefix}-${i}`
    switch (token.type) {
      case 'bold':
        return <strong key={key}>{token.text}</strong>
      case 'italic':
        return <em key={key}>{token.text}</em>
      case 'code':
        return (
          <code
            key={key}
            className="bg-zinc-800/60 text-orbit-accent px-1.5 py-0.5 rounded-md text-[13px] font-mono"
          >
            {token.text}
          </code>
        )
      case 'link': {
        const safeUrl = sanitizeUrl(token.url)
        if (!safeUrl) return <span key={key}>{token.text}</span>
        return (
          <a
            key={key}
            href={safeUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="text-orbit-accent underline decoration-orbit-accent/30 hover:decoration-orbit-accent transition-colors break-all"
          >
            {token.text}
          </a>
        )
      }
      default:
        return <span key={key}>{token.text}</span>
    }
  })
}

function InlineRenderer({ text, prefix }: { text: string; prefix: string }) {
  const tokens = useMemo(() => parseInline(text), [text])
  return <>{inlineToReact(tokens, prefix)}</>
}

// ─── MarkdownRenderer ───

interface MarkdownRendererProps {
  text: string
}

function MarkdownRenderer({ text }: MarkdownRendererProps) {
  const blocks = useMemo(() => {
    const raw = text.split(/(\n{2,})/).filter(Boolean)
    const result: React.ReactNode[] = []
    let linkCount = 0
    for (let i = 0; i < raw.length; i++) {
      const block = raw[i]
      if (/^\s*$/.test(block)) continue

      const trimmed = block.trim()

      const codeMatch = trimmed.match(/^```(\w*)\n([\s\S]*?)\n```$/)
      if (codeMatch) {
        const [, , code] = codeMatch
        result.push(
          <pre key={i} className="bg-zinc-900/80 border border-zinc-800/60 rounded-xl p-4 my-3 overflow-x-auto text-[13px] leading-relaxed font-mono text-zinc-200">
            <code>{code}</code>
          </pre>,
        )
        continue
      }

      if (/^#{1,3}\s/.test(trimmed)) {
        const level = trimmed.startsWith('###') ? 3 : trimmed.startsWith('##') ? 2 : 1
        const headingText = trimmed.replace(/^#{1,3}\s+/, '')
        const Tag = level === 1 ? 'h3' : level === 2 ? 'h4' : 'h5'
        const cls = level === 1
          ? 'text-[16px] font-bold text-zinc-100 mt-5 mb-2'
          : level === 2
            ? 'text-[15px] font-semibold text-zinc-200 mt-4 mb-1.5'
            : 'text-[14px] font-semibold text-zinc-300 mt-3 mb-1'
        result.push(
          <Tag key={i} className={cls}>{headingText}</Tag>,
        )
        continue
      }

      if (/^[-*]\s/.test(trimmed)) {
        const items = trimmed.split('\n').filter(l => /^[-*]\s/.test(l)).map(l => l.replace(/^[-*]\s+/, ''))
        result.push(
          <ul key={i} className="list-disc list-inside space-y-1 my-2 text-[14px] text-zinc-200">
            {items.map((item, j) => (
              <li key={j} className="leading-relaxed"><InlineRenderer text={item} prefix={`ul-${i}-${j}`} /></li>
            ))}
          </ul>,
        )
        continue
      }

      if (/^\d+[.)]\s/.test(trimmed)) {
        const items = trimmed.split('\n').filter(l => /^\d+[.)]\s/.test(l)).map(l => l.replace(/^\d+[.)]\s+/, ''))
        result.push(
          <ol key={i} className="list-decimal list-inside space-y-1 my-2 text-[14px] text-zinc-200">
            {items.map((item, j) => (
              <li key={j} className="leading-relaxed"><InlineRenderer text={item} prefix={`ol-${i}-${j}`} /></li>
            ))}
          </ol>,
        )
        continue
      }

      if (/^(-{3,}|_{3,}|\*{3,})$/.test(trimmed)) {
        result.push(<hr key={i} className="border-zinc-800 my-4" />)
        continue
      }

      const parts = trimmed.split(/(\[([^\]]+)\]\(([^)]+)\))/g)
      const elements: React.ReactNode[] = []
      for (let j = 0; j < parts.length; j++) {
        if (j % 4 === 0) {
          const inlineTokens = parseInline(parts[j])
          elements.push(...inlineToReact(inlineTokens, `p-${i}-${j}`))
        } else if (j % 4 === 2) {
          const text = parts[j]
          const url = sanitizeUrl(parts[j + 1])
          if (url) {
            linkCount++
            elements.push(
              <a
                key={`link-${i}-${j}`}
                href={url}
                target="_blank"
                rel="noopener noreferrer"
                className="text-orbit-accent underline decoration-orbit-accent/30 hover:decoration-orbit-accent transition-colors break-all"
              >
                {text}
              </a>,
            )
          } else {
            elements.push(<span key={`link-${i}-${j}`}>{text}</span>)
          }
          j += 2
        }
      }

      if (elements.length > 0) {
        result.push(
          <p key={i} className="text-[14px] leading-relaxed text-zinc-200 my-1.5">
            {elements}
          </p>,
        )
      }
    }
    return result
  }, [text])

  return <div className="space-y-0">{blocks}</div>
}

// ─── CopyButton ───

interface CopyButtonProps {
  text: string
  messageId: string
  copiedId: string | null
  onCopy: (id: string) => void
}

function CopyButton({ text, messageId, copiedId, onCopy }: CopyButtonProps) {
  const handleCopy = useCallback(async () => {
    try {
      await navigator.clipboard.writeText(text)
      onCopy(messageId)
    } catch {
      // Clipboard unavailable
    }
  }, [text, messageId, onCopy])

  const isCopied = copiedId === messageId

  return (
    <button
      onClick={handleCopy}
      className="mt-3 flex items-center gap-1.5 text-[11px] text-zinc-500 hover:text-orbit-accent transition-colors group"
      aria-label={isCopied ? 'Đã sao chép' : 'Sao chép nội dung'}
    >
      {isCopied ? (
        <>
          <Check className="h-3.5 w-3.5" aria-hidden="true" />
          <span>Đã sao chép</span>
        </>
      ) : (
        <>
          <Copy className="h-3.5 w-3.5" aria-hidden="true" />
          <span>Sao chép</span>
        </>
      )}
    </button>
  )
}

// ─── SourcesList ───

interface SourcesListProps {
  sources: string[]
}

function SourcesList({ sources }: SourcesListProps) {
  return (
    <div className="mt-3 pt-3 border-t border-zinc-800/40">
      <p className="text-[11px] font-bold text-zinc-400 mb-2 uppercase tracking-wider">Nguồn tham khảo</p>
      <div className="flex flex-wrap gap-1.5">
        {sources.map((url, i) => (
          <a
            key={i}
            href={sanitizeUrl(url) || url}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-1 text-[11px] text-zinc-400 hover:text-orbit-accent bg-zinc-900/40 border border-zinc-800/50 hover:border-orbit-accent/30 rounded-lg px-2 py-1 transition-colors"
          >
            <LinkIcon className="h-3 w-3 shrink-0" aria-hidden="true" />
            <span className="truncate max-w-[180px]">{getSourceLabel(url)}</span>
          </a>
        ))}
      </div>
    </div>
  )
}

// ─── SearchResultsList ───

interface SearchResultsListProps {
  results?: WebSearchResult[]
  isSearching?: boolean
}

function getResultDomain(url: string): string {
  try {
    const host = new URL(url).hostname.replace(/^www\./, '')
    return host.length > 25 ? host.slice(0, 22) + '...' : host
  } catch {
    return url
  }
}

function isWebSearching(events?: AiChatStatusEvent[]): boolean {
  if (!events || events.length === 0) return false
  const last = events[events.length - 1]
  return last.stage === 'web_search' || last.stage === 'web_read'
}

function SearchResultsList({ results, isSearching }: SearchResultsListProps) {
  if ((!results || results.length === 0) && !isSearching) return null

  return (
    <div className="mt-3.5 pb-3.5 border-b border-zinc-800/40">
      <div className="flex items-center gap-2 text-zinc-400 mb-2">
        <div className="relative flex items-center justify-center">
          {isSearching ? (
            <>
              <Globe className="h-4 w-4 text-orbit-accent animate-pulse shrink-0" aria-hidden="true" />
              <span className="absolute inline-flex h-full w-full rounded-full bg-orbit-accent/30 animate-ping opacity-75"></span>
            </>
          ) : (
            <Globe className="h-4 w-4 text-emerald-400 shrink-0" aria-hidden="true" />
          )}
        </div>
        <p className="text-[11px] font-bold uppercase tracking-wider text-zinc-400 flex items-center gap-1.5">
          Kết quả tìm kiếm:
          {isSearching && (
            <span className="text-[10px] lowercase font-normal text-zinc-500 italic">
              (đang cập nhật...)
            </span>
          )}
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-2.5">
        {results?.map((r, i) => (
          <a
            key={`${r.url}-${i}`}
            href={sanitizeUrl(r.url) || r.url}
            target="_blank"
            rel="noopener noreferrer"
            className="flex flex-col justify-between bg-zinc-900/40 hover:bg-zinc-800/40 border border-zinc-800/60 hover:border-orbit-accent/40 rounded-xl p-3 transition-all duration-200 group text-left relative overflow-hidden"
          >
            <div className="flex items-center gap-2 min-w-0">
              <div className="w-5 h-5 rounded-lg bg-zinc-950 flex items-center justify-center shrink-0 border border-zinc-800 overflow-hidden">
                <img
                  src={`https://www.google.com/s2/favicons?sz=32&domain=${getResultDomain(r.url)}`}
                  alt=""
                  className="w-3.5 h-3.5 object-contain"
                  onError={(e) => {
                    e.currentTarget.style.display = 'none'
                    const fallback = e.currentTarget.nextElementSibling as HTMLElement
                    if (fallback) fallback.style.display = 'block'
                  }}
                />
                <LinkIcon className="h-3 w-3 text-zinc-500 shrink-0" style={{ display: 'none' }} />
              </div>
              <span className="text-[11px] font-medium text-zinc-400 group-hover:text-zinc-200 transition-colors truncate">
                {getResultDomain(r.url)}
              </span>
            </div>

            <div className="mt-2 flex-1 min-w-0">
              <p className="text-[13px] font-semibold text-zinc-200 group-hover:text-orbit-accent leading-snug transition-colors line-clamp-2">
                {r.title || 'Không có tiêu đề'}
              </p>
              {r.description && (
                <p className="text-[11px] text-zinc-500 mt-1 line-clamp-1 group-hover:text-zinc-400 transition-colors">
                  {r.description}
                </p>
              )}
            </div>

            <div className="flex items-center justify-between mt-2.5 pt-2 border-t border-zinc-800/30 text-[10px] text-zinc-500">
              <span className="bg-zinc-900/85 px-1.5 py-0.5 rounded-md font-mono border border-zinc-850 text-[9px] font-bold text-zinc-400 group-hover:text-orbit-accent group-hover:border-orbit-accent/20 transition-all font-bold">
                {i + 1}
              </span>
              {r.sourceProvider && (
                <span className="bg-zinc-900/60 px-1.5 py-0.5 rounded text-[9px] font-bold uppercase tracking-wider text-zinc-500">
                  {r.sourceProvider}
                </span>
              )}
            </div>
          </a>
        ))}

        {isSearching && (!results || results.length < 3) &&
          Array.from({ length: 3 - (results?.length || 0) }).map((_, idx) => (
            <div
              key={`skeleton-${idx}`}
              className="flex flex-col justify-between bg-zinc-900/20 border border-zinc-800/40 rounded-xl p-3 min-h-[92px] animate-pulse"
            >
              <div className="flex items-center gap-2">
                <div className="w-5 h-5 rounded-lg bg-zinc-800 shrink-0" />
                <div className="h-3 w-16 bg-zinc-800 rounded" />
              </div>
              <div className="h-4 w-5/6 bg-zinc-800 rounded mt-2.5" />
              <div className="h-3 w-2/3 bg-zinc-800 rounded mt-1.5" />
            </div>
          ))
        }
      </div>
    </div>
  )
}

// ─── CourseBadgeRenderer ───

interface CourseBadgeRendererProps {
  content: string
}

function CourseBadgeRenderer({ content }: CourseBadgeRendererProps) {
  const elements = useMemo(() => {
    const pattern = /\b([A-Z]{2,4}\d{3,4})\b/g
    const parts: React.ReactNode[] = []
    let lastIndex = 0
    let match: RegExpExecArray | null
    while ((match = pattern.exec(content)) !== null) {
      if (match.index > lastIndex) {
        parts.push(<span key={`text-${lastIndex}-${match.index}`}>{content.slice(lastIndex, match.index)}</span>)
      }
      parts.push(
        <span
          key={`course-${match.index}-${match[0]}`}
          className="inline-block bg-orbit-accent/10 border border-orbit-accent/20 text-orbit-accent rounded-lg px-2 py-0.5 text-[12px] font-bold mx-0.5"
        >
          {match[1]}
        </span>,
      )
      lastIndex = match.index + match[0].length
    }
    if (lastIndex < content.length) {
      parts.push(<span key={`text-${lastIndex}-${content.length}`}>{content.slice(lastIndex)}</span>)
    }
    return parts
  }, [content])

  return <div className="whitespace-pre-line break-words">{elements}</div>
}

interface RoadmapPreviewProps {
  roadmap: RoadmapResponse
}

function RoadmapPreview({ roadmap }: RoadmapPreviewProps) {
  const roadmapCourses = roadmap.recommendedCourses ?? []
  const semesterNodes = groupRoadmapCoursesBySemester(roadmapCourses)
  const totalCredits = roadmapCourses.reduce((sum, course) => sum + (course.credits ?? 0), 0)
  const mandatoryCount = roadmapCourses.filter((course) => course.isMandatory).length
  const recommendedTrack = roadmap.graduationTracks?.find((track) => track.recommended)
  const electivePools = roadmap.electivePools ?? []

  return (
    <div className="mt-3 pt-3 border-t border-zinc-800/40 space-y-3">
      <div className="flex items-center gap-2 text-zinc-400">
        <BookOpen className="h-4 w-4 text-orbit-accent shrink-0" aria-hidden="true" />
        <p className="text-[11px] font-bold uppercase tracking-wider">Lộ trình học tập</p>
      </div>

      <div className="flex flex-wrap gap-2 text-[11px] text-zinc-400">
        <span className="rounded-full border border-zinc-800/60 bg-zinc-900/40 px-2 py-1">
          {semesterNodes.length} node
        </span>
        <span className="rounded-full border border-zinc-800/60 bg-zinc-900/40 px-2 py-1">
          {roadmapCourses.length} môn
        </span>
        <span className="rounded-full border border-zinc-800/60 bg-zinc-900/40 px-2 py-1">
          {totalCredits} TC
        </span>
        <span className="rounded-full border border-zinc-800/60 bg-zinc-900/40 px-2 py-1">
          {mandatoryCount} bắt buộc
        </span>
      </div>

      {semesterNodes.length > 0 && (
        <div className="space-y-2">
          <p className="text-[11px] font-bold uppercase tracking-wider text-zinc-500">Node lộ trình</p>
          <div className="relative pl-4">
            <div className="absolute left-[10px] top-2 bottom-2 w-px bg-zinc-800/70" aria-hidden="true" />
            <div className="space-y-3">
              {semesterNodes.map((group) => (
                <div key={group.key} className="relative pl-6">
                  <span
                    className="absolute left-[3px] top-4 h-3.5 w-3.5 rounded-full border border-orbit-accent bg-zinc-950 shadow-[0_0_0_4px_rgba(14,165,233,0.08)]"
                    aria-hidden="true"
                  />
                  <div className="rounded-2xl border border-zinc-800/50 bg-zinc-950/25 p-3">
                    <div className="flex items-start justify-between gap-2">
                      <div>
                        <p className="text-[12px] font-bold uppercase tracking-wider text-zinc-300">
                          {group.label}
                        </p>
                        <p className="mt-0.5 text-[11px] text-zinc-500">
                          {group.courses.length} môn · {group.credits} TC
                        </p>
                      </div>
                      <span className="rounded-full border border-zinc-800/60 bg-zinc-900/40 px-2 py-1 text-[11px] text-zinc-300">
                        {group.semester != null ? `HK${group.semester}` : 'Chưa xếp HK'}
                      </span>
                    </div>

                    <div className="mt-3 space-y-2">
                      {group.courses.map((course, index) => (
                        <div
                          key={`${group.key}-${course.courseCode ?? course.courseName ?? course.courseId ?? index}`}
                          className="flex gap-2 rounded-xl border border-zinc-800/50 bg-zinc-900/40 px-3 py-2"
                        >
                          <span
                            className={`mt-1.5 h-2.5 w-2.5 shrink-0 rounded-full ${
                              course.isMandatory ? 'bg-emerald-400' : 'bg-orbit-accent'
                            }`}
                            aria-hidden="true"
                          />
                          <div className="min-w-0 flex-1">
                            <div className="flex flex-wrap items-center gap-1.5 text-[13px] leading-snug">
                              <span className="font-semibold text-zinc-100">
                                {course.courseCode ?? '---'}
                              </span>
                              <span className="text-zinc-400">·</span>
                              <span className="text-zinc-200">{course.courseName ?? 'Môn học'}</span>
                              <span className="rounded-full border border-zinc-800/60 px-1.5 py-0.5 text-[10px] text-zinc-400">
                                {course.credits ?? 0} TC
                              </span>
                              <span className="rounded-full border border-zinc-800/60 px-1.5 py-0.5 text-[10px] text-zinc-400">
                                {course.isMandatory ? 'Bắt buộc' : 'Tự chọn'}
                              </span>
                            </div>
                            {course.reasoning && (
                              <div className="mt-1 text-[12px] leading-relaxed text-zinc-400">
                                {course.reasoning}
                              </div>
                            )}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {roadmap.summary && (
        <div className="space-y-1 rounded-2xl border border-zinc-800/50 bg-zinc-950/25 p-3">
          <p className="text-[11px] font-bold uppercase tracking-wider text-zinc-500">Tóm tắt</p>
          <div className="space-y-1 text-[13px] leading-relaxed text-zinc-200">
            <MarkdownRenderer text={roadmap.summary} />
          </div>
        </div>
      )}

      {recommendedTrack && (
        <div className="flex flex-wrap items-center gap-1.5">
          <span className="text-[10px] font-bold uppercase tracking-wider text-zinc-500">
            Hướng tốt nghiệp
          </span>
          <span className="rounded-full border border-zinc-800/60 bg-zinc-900/40 px-2 py-1 text-[11px] text-zinc-200">
            {recommendedTrack.name}
          </span>
        </div>
      )}

      {electivePools.length > 0 && (
        <div className="flex flex-wrap items-center gap-1.5">
          <Database className="h-3.5 w-3.5 shrink-0 text-zinc-500" aria-hidden="true" />
          <span className="text-[10px] font-bold uppercase tracking-wider text-zinc-500">Nhóm tự chọn</span>
          {electivePools.map((pool) => (
            <span
              key={pool.poolId}
              className="rounded-full border border-zinc-800/60 bg-zinc-900/40 px-2 py-1 text-[11px] text-zinc-300"
            >
              {pool.poolName}
            </span>
          ))}
        </div>
      )}
    </div>
  )
}

// ─── StatusProgress ───

interface StatusProgressProps {
  statusEvents: AiChatStatusEvent[]
  isStreaming: boolean
}

function getStageDetailedDescription(stage: string): string {
  switch (stage) {
    case 'session':
      return 'Khởi tạo session ID và kết nối SSE stream.'
    case 'analyze':
      return 'LLM phân tích từ khóa ý định, nhận dạng mã môn học.'
    case 'devorbit_context':
      return 'Đọc cấu trúc môn học, đề cương và tài liệu từ cơ sở dữ liệu DevOrbit.'
    case 'rag':
      return 'Truy vấn embedding tương đồng vector từ DevOrbit Vector DB.'
    case 'web_search':
      return 'Tìm tài liệu đề thi & kinh nghiệm học tập từ diễn đàn UIT.'
    case 'web_read':
      return 'Đọc & trích xuất nội dung bài viết bằng Firecrawl.'
    case 'answer':
      return 'Tổng hợp ngữ cảnh đa nguồn (RAG + Web + Database) và tạo câu trả lời.'
    case 'done':
      return 'Hoàn tất sinh câu trả lời và kết thúc.'
    case 'error':
      return 'Tiến trình bị gián đoạn. Sử dụng câu trả lời fallback.'
    default:
      return ''
  }
}

function getStageIcon(stage: string, isActive: boolean) {
  if (stage === 'error') {
    return <X className="h-2.5 w-2.5 text-rose-400 shrink-0" aria-hidden="true" />
  }

  switch (stage) {
    case 'session':
      return <Sparkle className="h-2.5 w-2.5 text-zinc-500 shrink-0" aria-hidden="true" />
    case 'analyze':
      return <MagnifyingGlass className="h-2.5 w-2.5 text-zinc-400 shrink-0" aria-hidden="true" />
    case 'devorbit_context':
    case 'rag':
      return <Database className="h-2.5 w-2.5 text-orbit-accent shrink-0" aria-hidden="true" />
    case 'web_search':
      return <Globe className={`h-2.5 w-2.5 text-sky-400 shrink-0 ${isActive ? 'animate-pulse' : ''}`} aria-hidden="true" />
    case 'web_read':
      return <BookOpen className={`h-2.5 w-2.5 text-emerald-400 shrink-0 ${isActive ? 'animate-pulse' : ''}`} aria-hidden="true" />
    case 'answer':
      return <ChatTeardropText className={`h-2.5 w-2.5 text-amber-400 shrink-0 ${isActive ? 'animate-pulse' : ''}`} aria-hidden="true" />
    case 'done':
      return <CheckCircle className="h-2.5 w-2.5 text-emerald-500 shrink-0" aria-hidden="true" />
    default:
      return <Sparkle className="h-2.5 w-2.5 text-zinc-400 shrink-0" aria-hidden="true" />
  }
}

function StatusProgress({ statusEvents, isStreaming }: StatusProgressProps) {
  const [isExpanded, setIsExpanded] = useState(isStreaming)

  useEffect(() => {
    if (isStreaming) {
      setIsExpanded(true)
    }
  }, [isStreaming, statusEvents.length])

  if (!statusEvents || statusEvents.length === 0) return null

  return (
    <div className="bg-zinc-950/20 border border-zinc-800/35 rounded-xl p-2.5 mb-2.5 text-[12.5px] text-zinc-300">
      <button
        onClick={() => setIsExpanded(!isExpanded)}
        className="flex items-center justify-between w-full text-zinc-400 hover:text-zinc-200 transition-colors focus:outline-none"
        aria-expanded={isExpanded}
        aria-label="Xem chi tiết quá trình RAG"
      >
        <div className="flex items-center gap-2 text-[11px] font-bold uppercase tracking-wider text-left min-w-0">
          {isStreaming ? (
            <div className="relative flex h-2 w-2 shrink-0">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-orbit-accent opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-orbit-accent"></span>
            </div>
          ) : (
            <CheckCircle className="h-3.5 w-3.5 text-emerald-400 shrink-0" aria-hidden="true" />
          )}
          <span className="text-zinc-300 truncate">
            {isStreaming
              ? 'Đang tìm kiếm & RAG...'
              : 'Đã hoàn thành phân tích & RAG'}
          </span>
        </div>
        <div className="flex items-center gap-1.5 shrink-0 ml-2">
          <span className="text-[9px] text-zinc-400 bg-zinc-900/40 px-1.5 py-0.5 rounded border border-zinc-800/30 font-medium">
            {statusEvents.length} bước
          </span>
          <CaretDown className={`h-3 w-3 text-zinc-400 transition-transform duration-300 ${isExpanded ? 'rotate-180' : ''}`} />
        </div>
      </button>

      <div
        className={`transition-all duration-300 ease-in-out overflow-hidden ${
          isExpanded ? 'mt-2.5 opacity-100 max-h-[500px] pointer-events-auto' : 'opacity-0 max-h-0 pointer-events-none'
        }`}
      >
        <div className="relative pl-3.5 border-l border-zinc-800/40 ml-1.5 space-y-2.5 pt-1">
          {statusEvents.map((evt, idx) => {
            const isLast = idx === statusEvents.length - 1
            const isActive = isLast && isStreaming && evt.stage !== 'error' && evt.stage !== 'done'
            const isCompleted = !isStreaming || idx < statusEvents.length - 1 || evt.stage === 'done'

            return (
              <div key={evt.id} className="relative flex gap-2.5 items-start group">
                <div className={`absolute -left-[10px] mt-0.5 flex items-center justify-center rounded-full border z-10 w-5 h-5 transition-all duration-200 ${
                  evt.stage === 'error'
                    ? 'bg-rose-950/40 border-rose-500/30 text-rose-400'
                    : isCompleted
                      ? 'bg-emerald-950/25 border-emerald-500/20 text-emerald-400'
                      : isActive
                        ? 'bg-zinc-900 border-orbit-accent/40 text-orbit-accent shadow-[0_0_8px_rgba(52,211,153,0.15)]'
                        : 'bg-zinc-950 border-zinc-800 text-zinc-600'
                }`}>
                  {getStageIcon(evt.stage, isActive)}
                </div>

                <div className="flex-1 min-w-0 pl-[14px]">
                  <div className="flex items-center gap-2">
                    <span className={`text-[12px] transition-colors ${
                      isActive
                        ? 'text-orbit-accent font-medium'
                        : evt.stage === 'error'
                          ? 'text-rose-400 font-medium'
                          : 'text-zinc-300'
                    }`}>
                      {evt.message}
                    </span>
                  </div>
                  <p className={`text-[11px] text-zinc-500 transition-all duration-300 ease-in-out overflow-hidden leading-normal ${
                    (isActive || evt.stage === 'error')
                      ? 'mt-0.5 opacity-100 max-h-12'
                      : 'max-h-0 opacity-0 group-hover:max-h-12 group-hover:opacity-100 group-hover:mt-0.5'
                  }`}>
                    {getStageDetailedDescription(evt.stage)}
                  </p>
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}

// ─── ChatMessage (memo-ized) ───

interface ChatMessageProps {
  message: AiChatMessage
  isStreaming: boolean
  copiedId: string | null
  onCopy: (id: string) => void
}

export const ChatMessage = memo(function ChatMessage({
  message,
  isStreaming,
  copiedId,
  onCopy,
}: ChatMessageProps) {
  const isAi = message.sender === 'ai'
  const showSources = isAi && message.sources && message.sources.length > 0
  const showSearchResults = isAi && ((message.searchResults && message.searchResults.length > 0) || (isStreaming && isWebSearching(message.statusEvents)))
  const showRoadmap = isAi && Boolean(message.roadmap)

  return (
    <div
      className={`flex flex-col ${isAi ? 'items-start' : 'items-end'}`}
      role="listitem"
    >
      <div
        className={`max-w-[88%] w-full px-4 py-3 rounded-2xl text-[14px] leading-relaxed ${
          isAi
            ? 'bg-zinc-900/50 border border-zinc-800/60 text-zinc-100 rounded-tl-none'
            : 'bg-orbit-accent/10 border border-orbit-accent/20 text-orbit-accent rounded-tr-none'
        }`}
      >
        {isAi && message.statusEvents && message.statusEvents.length > 0 && (
          <StatusProgress statusEvents={message.statusEvents} isStreaming={isStreaming} />
        )}

        {showSearchResults && (
          <SearchResultsList
            results={message.searchResults}
            isSearching={isStreaming && isWebSearching(message.statusEvents)}
          />
        )}

        {isAi ? (
          message.content.length > 0 ? (
            <div className="whitespace-pre-wrap break-words mt-3">
              <MarkdownRenderer text={message.content} />
              {isStreaming && (
                <span
                  className="inline-flex items-center ml-0.5 text-orbit-accent"
                  aria-hidden="true"
                >
                  <span className="h-4 w-[2px] bg-orbit-accent animate-pulse" />
                </span>
              )}
            </div>
          ) : isStreaming ? (
            <div className="flex items-center gap-2 text-[12px] text-zinc-400 mt-2" role="status" aria-label="AI đang trả lời">
              <SpinnerIcon className="h-4 w-4 animate-spin text-orbit-accent" aria-hidden="true" />
              <span>Đang soạn câu trả lời...</span>
            </div>
          ) : null
        ) : (
          <div className="whitespace-pre-line break-words">
            <CourseBadgeRenderer content={message.content} />
          </div>
        )}

        {showRoadmap && message.roadmap && <RoadmapPreview roadmap={message.roadmap} />}

        {showSources && <SourcesList sources={message.sources!} />}

        {isAi && !isStreaming && message.content.length > 0 && (
          <CopyButton
            text={message.content}
            messageId={message.id}
            copiedId={copiedId}
            onCopy={onCopy}
          />
        )}
      </div>
    </div>
  )
})
