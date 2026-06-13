import { createContext, useContext, useState, useRef, useEffect, useCallback, type ReactNode } from 'react'
import {
  useSubjectQa,
  streamSubjectQa,
  type WebSearchResult,
} from '../../hooks/useSubjectQa'

// ─── Shared Types ───

export interface AiChatStatusEvent {
  id: string
  stage: string
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

// ─── Context Value ───

interface ChatContextValue {
  isOpen: boolean
  messages: AiChatMessage[]
  input: string
  setInput: (val: string) => void
  streamingMsgId: string | null
  copiedId: string | null
  scrollRef: React.RefObject<HTMLDivElement | null>
  inputRef: React.RefObject<HTMLInputElement | null>
  handleSend: (text: string) => Promise<void>
  handleSubmit: (e: React.FormEvent) => void
  handleCopy: (id: string) => void
  clearHistory: () => void
  handleScroll: () => void
  isMessageStreaming: (msgId: string) => boolean
  setIsOpen: (val: boolean) => void
}

const ChatContext = createContext<ChatContextValue | null>(null)

export function useChat(): ChatContextValue {
  const ctx = useContext(ChatContext)
  if (!ctx) throw new Error('useChat must be used within ChatProvider')
  return ctx
}

// ─── Provider ───

export function ChatProvider({ children }: { children: ReactNode }) {
  const [isOpen, setIsOpen] = useState(false)
  const [input, setInput] = useState('')
  const sessionIdRef = useRef<string | undefined>(localStorage.getItem('orbit_chat_session_id') || undefined)
  const [messages, setMessages] = useState<AiChatMessage[]>(() => {
    try {
      const saved = localStorage.getItem('orbit_chat_messages')
      if (saved) return JSON.parse(saved)
    } catch { /* ignore */ }
    return [
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
  const inputRef = useRef<HTMLInputElement>(null)
  const scrollRef = useRef<HTMLDivElement>(null)
  const isNearBottomRef = useRef(true)

  const chatMutation = useSubjectQa()

  // Clean abort controller on unmount
  useEffect(() => {
    return () => {
      abortRef.current?.abort()
    }
  }, [])

  // Persist chat history
  useEffect(() => {
    if (streamingMsgId === null) {
      localStorage.setItem('orbit_chat_messages', JSON.stringify(messages))
    }
  }, [messages, streamingMsgId])

  const updateSessionId = useCallback((nextSessionId: string | undefined) => {
    sessionIdRef.current = nextSessionId
    if (nextSessionId) {
      localStorage.setItem('orbit_chat_session_id', nextSessionId)
    } else {
      localStorage.removeItem('orbit_chat_session_id')
    }
  }, [])

  // Track scroll position
  const handleScroll = useCallback(() => {
    const el = scrollRef.current
    if (!el) return
    const threshold = 150
    isNearBottomRef.current = el.scrollHeight - el.scrollTop - el.clientHeight < threshold
  }, [])

  // Auto-scroll when new messages arrive
  useEffect(() => {
    if (isNearBottomRef.current) {
      const isStreaming = streamingMsgId !== null
      scrollRef.current?.scrollTo({
        top: scrollRef.current.scrollHeight,
        behavior: isStreaming ? 'auto' : 'smooth',
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

  const handleSend = useCallback(async (text: string) => {
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
        { message: text, sessionId: sessionIdRef.current },
        {
          onStatus: (evt) => {
            setMessages((prev) =>
              prev.map((msg) => {
                if (msg.id !== aiId) return msg
                const existing = msg.statusEvents ?? []
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
            if (response.sessionId && response.sessionId !== sessionIdRef.current) {
              updateSessionId(response.sessionId)
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
                const prefix = msg.content ? msg.content + '\n\n' : ''
                return {
                  ...msg,
                  content: prefix + '⚠️ Đã xảy ra lỗi khi kết nối với trợ lý AI. Vui lòng thử lại sau.',
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
      if (error instanceof Error && error.message === 'Streaming is not supported in this browser') {
        try {
          const res = await chatMutation.mutateAsync({ message: text, sessionId: sessionIdRef.current })
          if (res.sessionId && res.sessionId !== sessionIdRef.current) {
            updateSessionId(res.sessionId)
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
            updateSessionId(undefined)
          }
          setMessages((prev) =>
            prev.map((msg) => {
              if (msg.id !== aiId) return msg
              const prefix = msg.content ? msg.content + '\n\n' : ''
              return {
                ...msg,
                content: prefix + '⚠️ Đã xảy ra lỗi khi kết nối với trợ lý AI. Vui lòng thử lại sau.',
              }
            }),
          )
        }
      } else if (error instanceof Error && error.name === 'AbortError') {
        // User cancelled, do nothing
      } else {
        setMessages((prev) =>
          prev.map((msg) => {
            if (msg.id !== aiId) return msg
            const prefix = msg.content ? msg.content + '\n\n' : ''
            return {
              ...msg,
              content: prefix + '⚠️ Đã xảy ra lỗi khi kết nối với trợ lý AI. Vui lòng thử lại sau.',
            }
          }),
        )
      }
      setStreamingMsgId(null)
      abortRef.current = null
    }
  }, [chatMutation, streamingMsgId, updateSessionId])

  const clearHistory = useCallback(() => {
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
    updateSessionId(undefined)
    setStreamingMsgId(null)
    localStorage.removeItem('orbit_chat_messages')
    localStorage.removeItem('orbit_chat_session_id')
  }, [updateSessionId])

  const handleSubmit = useCallback((e: React.FormEvent) => {
    e.preventDefault()
    handleSend(input)
  }, [handleSend, input])

  const isMessageStreaming = useCallback((msgId: string): boolean => {
    return streamingMsgId === msgId
  }, [streamingMsgId])

  const value: ChatContextValue = {
    messages,
    input,
    setInput,
    isOpen,
    setIsOpen,
    streamingMsgId,
    copiedId,
    scrollRef,
    inputRef,
    handleSend,
    handleSubmit,
    handleCopy,
    clearHistory,
    handleScroll,
    isMessageStreaming,
  }

  return (
    <ChatContext.Provider value={value}>
      {children}
    </ChatContext.Provider>
  )
}

// ─── Helper ───

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
