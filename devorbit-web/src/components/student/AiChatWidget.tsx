import { useState, useRef, useEffect, useCallback, useMemo } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Sparkle, ArrowUp, X, ChatTeardropText, Spinner as SpinnerIcon, Link as LinkIcon, Trash, Copy, Check, CheckCircle } from '@phosphor-icons/react'
import {
    useSubjectQa,
    streamSubjectQa,
    type WebSearchResult,
    type SubjectQaStreamStage,
} from '../../hooks/useSubjectQa'

// ─── Types ───

export interface AiChatStatusEvent {
    id: string
    stage: SubjectQaStreamStage
    message: string
}

export interface AiChatMessage {
    id: string
    sender: 'student' | 'ai'
    content: string
    sources?: string[]
    searchResults?: WebSearchResult[]
    statusEvents?: AiChatStatusEvent[]
}

// ─── Constants ───

const SUGGESTIONS = [
    'SE104 hoc sao cho tot?',
    'MA006 hoc phan nay co tai lieu gi?',
    'IS201 co repo nao nen xem khong?',
] as const

// ─── Helpers ───

function getSourceLabel(url: string): string {
    try {
        return new URL(url).hostname.replace(/^www\./, '')
    } catch {
        return url
    }
}

/** Sanitize a URL: allow only http/https/mailto. Null if unsafe. */
function sanitizeUrl(url: string): string | null {
    if (!url) return null
    const lower = url.toLowerCase()
    if (lower.startsWith('http://') || lower.startsWith('https://') || lower.startsWith('mailto:')) {
        return url
    }
    return null
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

            // Code block (``` ... ```)
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

            // Heading (## ...)
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

            // Unordered list
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

            // Ordered list
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

            // Horizontal rule
            if (/^(-{3,}|_{3,}|\*{3,})$/.test(trimmed)) {
                result.push(<hr key={i} className="border-zinc-800 my-4" />)
                continue
            }

            // Paragraph with inline tokens + links
            const parts = trimmed.split(/(\[([^\]]+)\]\(([^)]+)\))/g)
            const elements: React.ReactNode[] = []
            for (let j = 0; j < parts.length; j++) {
                if (j % 4 === 0) {
                    // Text part
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

function InlineRenderer({ text, prefix }: { text: string; prefix: string }) {
    const tokens = useMemo(() => parseInline(text), [text])
    return <>{inlineToReact(tokens, prefix)}</>
}

// ─── StreamingText (removed — replaced by live delta rendering) ───

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

interface SearchResultsListProps {
    results?: WebSearchResult[]
}

function getResultDomain(url: string): string {
    try {
        const host = new URL(url).hostname.replace(/^www\./, '')
        return host.length > 25 ? host.slice(0, 22) + '...' : host
    } catch {
        return url
    }
}

function SearchResultsList({ results }: SearchResultsListProps) {
    if (!results || results.length === 0) return null

    return (
        <div className="mt-3 pt-3 border-t border-zinc-800/40">
            <p className="text-[11px] font-bold text-zinc-400 mb-2 uppercase tracking-wider">Kết quả tìm kiếm:</p>
            <div className="space-y-2">
                {results.map((r, i) => (
                    <a
                        key={`${r.url}-${i}`}
                        href={sanitizeUrl(r.url) || r.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="block bg-zinc-900/30 border border-zinc-800/40 hover:border-zinc-700/60 rounded-xl p-3 transition-colors group"
                    >
                        <div className="flex items-start gap-2">
                            <div className="flex-1 min-w-0">
                                <p className="text-[13px] font-medium text-zinc-200 group-hover:text-orbit-accent transition-colors truncate">
                                    {r.title || 'Không có tiêu đề'}
                                </p>
                                <p className="text-[11px] text-zinc-500 mt-0.5 truncate">{getResultDomain(r.url)}</p>
                                {r.description && (
                                    <p className="text-[12px] text-zinc-400 mt-1 line-clamp-2">{r.description}</p>
                                )}
                                {r.sourceProvider && (
                                    <span className="inline-block mt-1 text-[10px] font-bold text-zinc-500 uppercase tracking-wider">
                                        {r.sourceProvider}
                                    </span>
                                )}
                            </div>
                            <LinkIcon
                                className="h-4 w-4 text-zinc-600 group-hover:text-orbit-accent/70 shrink-0 mt-0.5"
                                aria-hidden="true"
                            />
                        </div>
                    </a>
                ))}
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
        let keyIndex = 0
        while ((match = pattern.exec(content)) !== null) {
            if (match.index > lastIndex) {
                parts.push(<span key={keyIndex++}>{content.slice(lastIndex, match.index)}</span>)
            }
            parts.push(
                <span
                    key={keyIndex++}
                    className="inline-block bg-orbit-accent/10 border border-orbit-accent/20 text-orbit-accent rounded-lg px-2 py-0.5 text-[12px] font-bold mx-0.5"
                >
                    {match[1]}
                </span>,
            )
            lastIndex = match.index + match[0].length
        }
        if (lastIndex < content.length) {
            parts.push(<span key={keyIndex++}>{content.slice(lastIndex)}</span>)
        }
        return parts
    }, [content])

    return <div className="whitespace-pre-line break-words">{elements}</div>
}

// ─── StatusProgress ───

interface StatusProgressProps {
    statusEvents: AiChatStatusEvent[]
    isStreaming: boolean
}

function StatusProgress({ statusEvents, isStreaming }: StatusProgressProps) {
    if (!statusEvents || statusEvents.length === 0) return null

    return (
        <div className="mb-3 space-y-1.5">
            {statusEvents.map((evt) => {
                const isLast = evt === statusEvents[statusEvents.length - 1]
                const isActive = isLast && isStreaming && evt.stage !== 'error' && evt.stage !== 'done'
                return (
                    <div key={evt.id} className="flex items-center gap-2 text-[12px]">
                        {isActive ? (
                            <SpinnerIcon className="h-3.5 w-3.5 animate-spin text-orbit-accent shrink-0" aria-hidden="true" />
                        ) : evt.stage === 'error' ? (
                            <X className="h-3.5 w-3.5 text-rose-400 shrink-0" aria-hidden="true" />
                        ) : (
                            <CheckCircle className="h-3.5 w-3.5 text-emerald-400 shrink-0" aria-hidden="true" />
                        )}
                        <span className={isActive ? 'text-zinc-300' : evt.stage === 'error' ? 'text-rose-300' : 'text-zinc-400'}>
                            {evt.message}
                        </span>
                    </div>
                )
            })}
        </div>
    )
}

// ─── ChatMessage ───

interface ChatMessageProps {
    message: AiChatMessage
    isStreaming: boolean
    copiedId: string | null
    onCopy: (id: string) => void
}

export function ChatMessage({
    message,
    isStreaming,
    copiedId,
    onCopy,
}: ChatMessageProps) {
    const isAi = message.sender === 'ai'
    const showSources = isAi && message.sources && message.sources.length > 0
    const showSearchResults = isAi && message.searchResults && message.searchResults.length > 0

    return (
        <div
            className={`flex flex-col ${isAi ? 'items-start' : 'items-end'}`}
            role="listitem"
        >
            <div
                className={`max-w-[88%] px-4 py-3 rounded-2xl text-[14px] leading-relaxed ${
                    isAi
                        ? 'bg-zinc-900/50 border border-zinc-800/60 text-zinc-100 rounded-tl-none'
                        : 'bg-orbit-accent/10 border border-orbit-accent/20 text-orbit-accent rounded-tr-none'
                }`}
            >
                {isAi && message.statusEvents && message.statusEvents.length > 0 && (
                    <StatusProgress statusEvents={message.statusEvents} isStreaming={isStreaming} />
                )}

                {isAi ? (
                    message.content.length > 0 ? (
                        <div className="whitespace-pre-wrap break-words">
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
                        <div className="flex items-center gap-2 text-[12px] text-zinc-400" role="status" aria-label="AI đang trả lời">
                            <SpinnerIcon className="h-4 w-4 animate-spin text-orbit-accent" aria-hidden="true" />
                            <span>Đang xử lý...</span>
                        </div>
                    ) : null
                ) : (
                    <div className="whitespace-pre-line break-words">
                        <CourseBadgeRenderer content={message.content} />
                    </div>
                )}

                {showSearchResults && <SearchResultsList results={message.searchResults!} />}
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
}

// ─── AiChatWidget (main) ───

export function AiChatWidget() {
    const [isOpen, setIsOpen] = useState(false)
    const [input, setInput] = useState('')
    const [sessionId, setSessionId] = useState<string | undefined>(() => {
        return localStorage.getItem('orbit_chat_session_id') || undefined
    })
    const [messages, setMessages] = useState<AiChatMessage[]>(() => {
        const saved = localStorage.getItem('orbit_chat_messages')
        return saved ? JSON.parse(saved) : [
            {
                id: 'welcome',
                sender: 'ai',
                content: 'Chào bạn! Mình là Cố vấn Học tập AI của DevOrbit. Bạn cần hỏi điều gì về môn học, đề cương, cách tính điểm hay tham khảo đồ án mẫu UIT không?',
            },
        ]
    })
    const [streamingMsgId, setStreamingMsgId] = useState<string | null>(null)
    const [copiedId, setCopiedId] = useState<string | null>(null)
    const abortRef = useRef<AbortController | null>(null)

    const scrollRef = useRef<HTMLDivElement>(null)
    const isNearBottomRef = useRef(true)

    const chatMutation = useSubjectQa()

    // Persist chat history
    useEffect(() => {
        localStorage.setItem('orbit_chat_messages', JSON.stringify(messages))
        if (sessionId) {
            localStorage.setItem('orbit_chat_session_id', sessionId)
        }
    }, [messages, sessionId])

    // Track scroll position to decide auto-scroll
    const handleScroll = useCallback(() => {
        const el = scrollRef.current
        if (!el) return
        const threshold = 150
        isNearBottomRef.current = el.scrollHeight - el.scrollTop - el.clientHeight < threshold
    }, [])

    // Auto-scroll when new messages arrive, but only if user was near bottom
    useEffect(() => {
        if (isNearBottomRef.current) {
            scrollRef.current?.scrollTo({
                top: scrollRef.current.scrollHeight,
                behavior: 'smooth',
            })
        }
    }, [messages, streamingMsgId])

    // Copy timeout
    useEffect(() => {
        if (copiedId === null) return
        const timer = setTimeout(() => setCopiedId(null), 2000)
        return () => clearTimeout(timer)
    }, [copiedId])

    const handleCopy = useCallback((id: string) => {
        setCopiedId(id)
    }, [])

    const handleSend = async (text: string) => {
        if (!text.trim() || streamingMsgId !== null) return

        const userMsg: AiChatMessage = {
            id: `user-${Date.now()}`,
            sender: 'student',
            content: text,
        }

        const aiId = `ai-${Date.now()}`
        const aiMsg: AiChatMessage = {
            id: aiId,
            sender: 'ai',
            content: '',
            sources: [],
            searchResults: [],
            statusEvents: [],
        }

        setMessages((prev) => [...prev, userMsg, aiMsg])
        setStreamingMsgId(aiId)
        setInput('')

        const abort = new AbortController()
        abortRef.current = abort

        try {
            await streamSubjectQa(
                { message: text, sessionId },
                {
                    onStatus: (evt) => {
                        setMessages((prev) =>
                            prev.map((msg) => {
                                if (msg.id !== aiId) return msg
                                const existing = msg.statusEvents ?? []
                                // Replace duplicate stage events
                                const lastIdx = existing.length - 1
                                if (lastIdx >= 0 && existing[lastIdx].stage === evt.stage) {
                                    const updated = [...existing]
                                    updated[lastIdx] = {
                                        id: `status-${Date.now()}-${evt.stage}`,
                                        stage: evt.stage,
                                        message: evt.message,
                                    }
                                    return { ...msg, statusEvents: updated }
                                }
                                return {
                                    ...msg,
                                    statusEvents: [
                                        ...existing,
                                        {
                                            id: `status-${Date.now()}-${evt.stage}`,
                                            stage: evt.stage,
                                            message: evt.message,
                                        },
                                    ],
                                }
                            }),
                        )
                    },
                    onSearchResult: (result) => {
                        setMessages((prev) =>
                            prev.map((msg) => {
                                if (msg.id !== aiId) return msg
                                const existing = msg.searchResults ?? []
                                // Deduplicate by URL
                                if (existing.some((r) => r.url === result.url)) return msg
                                return { ...msg, searchResults: [...existing, result] }
                            }),
                        )
                    },
                    onDelta: (content) => {
                        setMessages((prev) =>
                            prev.map((msg) => {
                                if (msg.id !== aiId) return msg
                                return { ...msg, content: msg.content + content }
                            }),
                        )
                    },
                    onComplete: (response) => {
                        if (response.sessionId && response.sessionId !== sessionId) {
                            setSessionId(response.sessionId)
                        }
                        setMessages((prev) =>
                            prev.map((msg) => {
                                if (msg.id !== aiId) return msg
                                return {
                                    ...msg,
                                    content: msg.content || response.answer,
                                    sources: response.sources,
                                    searchResults: mergeSearchResults(msg.searchResults ?? [], response.searchResults ?? []),
                                }
                            }),
                        )
                        setStreamingMsgId(null)
                        abortRef.current = null
                    },
                    onError: (message) => {
                        setMessages((prev) =>
                            prev.map((msg) => {
                                if (msg.id !== aiId) return msg
                                return {
                                    ...msg,
                                    content: '⚠️ Đã xảy ra lỗi khi kết nối với trợ lý AI. Vui lòng thử lại sau.',
                                    statusEvents: [
                                        ...(msg.statusEvents ?? []),
                                        {
                                            id: `status-${Date.now()}-error`,
                                            stage: 'error',
                                            message,
                                        },
                                    ],
                                }
                            }),
                        )
                        setStreamingMsgId(null)
                        abortRef.current = null
                    },
                },
                abort.signal,
            )
        } catch (error) {
            // Check if this is a browser streaming support fallback
            if (error instanceof Error && error.message === 'Streaming is not supported in this browser') {
                // Fallback to one-shot
                try {
                    const res = await chatMutation.mutateAsync({ message: text, sessionId })
                    if (res.sessionId && res.sessionId !== sessionId) {
                        setSessionId(res.sessionId)
                    }
                    setMessages((prev) =>
                        prev.map((msg) => {
                            if (msg.id !== aiId) return msg
                            return {
                                ...msg,
                                content: res.answer,
                                sources: res.sources,
                                searchResults: res.searchResults ?? [],
                            }
                        }),
                    )
                } catch (fallbackError) {
                    if (fallbackError instanceof Error && fallbackError.message.includes('400')) {
                        setSessionId(undefined)
                        localStorage.removeItem('orbit_chat_session_id')
                    }
                    setMessages((prev) =>
                        prev.map((msg) => {
                            if (msg.id !== aiId) return msg
                            return {
                                ...msg,
                                content: '⚠️ Đã xảy ra lỗi khi kết nối với trợ lý AI. Vui lòng thử lại sau.',
                            }
                        }),
                    )
                }
            } else if (error instanceof Error && error.name === 'AbortError') {
                // User cancelled, do nothing
            } else {
                // Generic error
                setMessages((prev) =>
                    prev.map((msg) => {
                        if (msg.id !== aiId) return msg
                        return {
                            ...msg,
                            content: '⚠️ Đã xảy ra lỗi khi kết nối với trợ lý AI. Vui lòng thử lại sau.',
                        }
                    }),
                )
            }
            setStreamingMsgId(null)
            abortRef.current = null
        }
    }

    const clearHistory = () => {
        if (abortRef.current) {
            abortRef.current.abort()
            abortRef.current = null
        }
        setMessages([
            {
                id: 'welcome',
                sender: 'ai',
                content: 'Chào bạn! Mình là Cố vấn Học tập AI của DevOrbit. Bạn cần hỏi điều gì về môn học, đề cương, cách tính điểm hay tham khảo đồ án mẫu UIT không?',
            },
        ])
        setSessionId(undefined)
        setStreamingMsgId(null)
        localStorage.removeItem('orbit_chat_messages')
        localStorage.removeItem('orbit_chat_session_id')
    }

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault()
        handleSend(input)
    }

    const isMessageStreaming = (msgId: string): boolean => {
        return streamingMsgId === msgId
    }

    return (
        <div className="fixed bottom-6 right-6 z-50 flex flex-col items-end">
            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.95, y: 15 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.95, y: 15 }}
                        transition={{ type: 'spring', stiffness: 350, damping: 30 }}
                        className="w-[90vw] sm:w-[420px] h-[600px] max-h-[80vh] mb-4 flex flex-col rounded-[2rem] border border-zinc-800/50 bg-zinc-950/80 backdrop-blur-xl shadow-[0_20px_50px_rgba(0,0,0,0.5)] overflow-hidden"
                        role="dialog"
                        aria-label="DevOrbit AI Chat"
                        aria-modal="true"
                    >
                        {/* ─── Header ─── */}
                        <div className="flex items-center justify-between px-6 py-4 border-b border-zinc-800/40 bg-zinc-900/10 shrink-0">
                            <div className="flex items-center gap-3">
                                <div className="h-9 w-9 rounded-xl bg-orbit-accent/15 border border-orbit-accent/20 flex items-center justify-center relative">
                                    <Sparkle className="h-5 w-5 text-orbit-accent animate-pulse" weight="fill" aria-hidden="true" />
                                    <span className="absolute bottom-0 right-0 h-2 w-2 rounded-full bg-emerald-400 border border-zinc-950" />
                                </div>
                                <div>
                                    <h4 className="text-[14px] font-bold text-zinc-100">DevOrbit AI</h4>
                                    <p className="text-[11px] text-zinc-400">Trợ lý Cố vấn Học tập UIT</p>
                                </div>
                            </div>
                            <div className="flex items-center gap-2">
                                <button
                                    onClick={clearHistory}
                                    title="Xóa lịch sử chat"
                                    aria-label="Xóa lịch sử chat"
                                    className="p-1.5 rounded-lg text-zinc-400 hover:text-rose-400 hover:bg-zinc-800/40 transition-colors"
                                >
                                    <Trash className="h-4.5 w-4.5" aria-hidden="true" />
                                </button>
                                <button
                                    onClick={() => setIsOpen(false)}
                                    aria-label="Đóng chat"
                                    className="p-1.5 rounded-lg text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800/40 transition-colors"
                                >
                                    <X className="h-5 w-5" aria-hidden="true" />
                                </button>
                            </div>
                        </div>

                        {/* ─── Messages ─── */}
                        <div
                            ref={scrollRef}
                            onScroll={handleScroll}
                            className="flex-1 overflow-y-auto p-6 space-y-4 scrollbar-thin min-h-0"
                            role="list"
                            aria-label="Tin nhắn chat"
                        >
                            {messages.map((msg) => (
                                <ChatMessage
                                    key={msg.id}
                                    message={msg}
                                    isStreaming={isMessageStreaming(msg.id)}
                                    copiedId={copiedId}
                                    onCopy={handleCopy}
                                />
                            ))}
                        </div>

                        {/* ─── Suggestion Chips ─── */}
                        {messages.length === 1 && (
                            <div className="px-6 py-2 flex flex-col gap-1.5 shrink-0">
                                <span className="text-[10px] font-bold text-zinc-400">Gợi ý câu hỏi:</span>
                                <div className="flex flex-wrap gap-1.5 max-h-[85px] overflow-y-auto">
                                    {SUGGESTIONS.map((s, i) => (
                                        <button
                                            key={i}
                                            onClick={() => handleSend(s)}
                                            disabled={streamingMsgId !== null}
                                            className="text-[11px] text-left text-zinc-300 hover:text-orbit-accent bg-zinc-900/40 border border-zinc-800 hover:border-orbit-accent/30 rounded-xl px-3 py-1.5 transition-colors duration-200 disabled:opacity-50"
                                            aria-label={`Gợi ý: ${s}`}
                                        >
                                            {s}
                                        </button>
                                    ))}
                                </div>
                            </div>
                        )}

                        {/* ─── Input ─── */}
                        <div className="p-4 border-t border-zinc-800/40 bg-zinc-900/10 shrink-0">
                            <form onSubmit={handleSubmit} className="relative flex items-center">
                                <input
                                    type="text"
                                    value={input}
                                    onChange={(e) => setInput(e.target.value)}
                                    placeholder="Hỏi về môn học, đề cương, cách ôn thi..."
                                    disabled={streamingMsgId !== null}
                                    aria-label="Tin nhắn của bạn"
                                    className="w-full bg-zinc-900 border border-zinc-800 hover:border-zinc-700 focus:border-orbit-accent/50 focus:ring-1 focus:ring-orbit-accent/50 rounded-2xl pl-4 pr-12 py-3 text-[14px] text-zinc-100 placeholder:text-zinc-500 transition-[border-color,box-shadow] outline-none disabled:opacity-50"
                                />
                                <button
                                    type="submit"
                                    disabled={!input.trim() || streamingMsgId !== null}
                                    aria-label="Gửi tin nhắn"
                                    className="absolute right-2.5 h-8 w-8 rounded-xl bg-orbit-accent hover:bg-orbit-accent/90 disabled:bg-zinc-800 text-zinc-950 disabled:text-zinc-500 flex items-center justify-center transition-all focus:outline-none"
                                >
                                    <ArrowUp className="h-4.5 w-4.5" weight="bold" aria-hidden="true" />
                                </button>
                            </form>
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>

            {/* ─── FAB Toggle ─── */}
            <motion.button
                onClick={() => setIsOpen(!isOpen)}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                aria-label={isOpen ? 'Đóng chat' : 'Mở chat AI'}
                className={`h-14 w-14 rounded-full shadow-2xl flex items-center justify-center border transition-all duration-300 ${
                    isOpen
                        ? 'bg-zinc-900 border-zinc-800 text-zinc-100 hover:bg-zinc-800'
                        : 'bg-orbit-accent border-orbit-accent/20 text-zinc-950 hover:shadow-[0_0_20px_rgba(52,211,153,0.4)]'
                }`}
            >
                {isOpen ? (
                    <X className="h-6 w-6" weight="bold" aria-hidden="true" />
                ) : (
                    <ChatTeardropText className="h-6 w-6" weight="fill" aria-hidden="true" />
                )}
            </motion.button>
        </div>
    )
}

// ─── Helpers ───

function mergeSearchResults(
    existing: WebSearchResult[],
    incoming: WebSearchResult[],
): WebSearchResult[] {
    const seen = new Set(existing.map((r) => r.url))
    const merged = [...existing]
    for (const r of incoming) {
        if (!seen.has(r.url)) {
            seen.add(r.url)
            merged.push(r)
        }
    }
    return merged
}
