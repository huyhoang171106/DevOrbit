import { useState, useRef, useEffect } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import {
  ChatCircleDots,
  Hash,
  BookOpen,
  Code,
  UsersThree,
  PaperPlaneRight,
  MagnifyingGlass,
  DotsThree,
  Spinner,
  CheckCircle,
  XCircle,
} from '@phosphor-icons/react'
import { FadeReveal } from '../../motion'
import { useChannels, useChannelMessages } from '../../hooks/useCommunity'
import { isStudentAuthenticated, getStudentToken } from '../../lib/auth'
import { apiStudentPost, apiStudentDelete } from '../../lib/api'
import { useNavigate } from 'react-router-dom'
import type { ChatChannelResponse, ChatMessageResponse } from '../../types/api'

const CHANNEL_GROUP_LABELS: Record<string, string> = {
  GENERAL: 'Chung',
  COURSE: 'Môn học',
  TECH_STACK: 'Tech Stack',
}

const CHANNEL_GROUP_COLORS: Record<string, string> = {
  GENERAL: 'text-purple-400',
  COURSE: 'text-blue-400',
  TECH_STACK: 'text-emerald-400',
}

function ChannelList({
  channels,
  activeId,
  onSelect,
  loading,
}: {
  channels: ChatChannelResponse[]
  activeId: number | null
  onSelect: (ch: ChatChannelResponse) => void
  loading: boolean
}) {
  const groups = channels.reduce<Record<string, ChatChannelResponse[]>>((acc, ch) => {
    const g = ch.type
    if (!acc[g]) acc[g] = []
    acc[g].push(ch)
    return acc
  }, {})

  const typeOrder = ['GENERAL', 'COURSE', 'TECH_STACK']

  return (
    <div className="h-full flex flex-col overflow-hidden">
      <div className="px-4 py-4 border-b border-orbit-border">
        <h2 className="font-heading text-sm font-bold text-orbit-text tracking-wide">Kênh</h2>
      </div>
      <div className="flex-1 overflow-y-auto scrollbar-thin">
        {loading ? (
          <div className="flex justify-center py-8">
            <Spinner className="h-5 w-5 text-orbit-accent animate-spin" />
          </div>
        ) : (
          typeOrder.map((type) => {
            const list = groups[type]
            if (!list || list.length === 0) return null
            return (
              <div key={type} className="px-2 pt-3 pb-1">
                <p className={`px-2 text-[11px] font-bold tracking-wider uppercase mb-1 ${CHANNEL_GROUP_COLORS[type] ?? 'text-orbit-text-muted'}`}>
                  {CHANNEL_GROUP_LABELS[type] ?? type}
                </p>
                {list.map((ch) => {
                  const isActive = ch.id === activeId
                  return (
                    <button
                      key={ch.id}
                      onClick={() => onSelect(ch)}
                      className={`w-full flex items-center gap-2.5 px-3 py-2 rounded-lg text-left text-sm transition-all duration-200 ${isActive ? 'bg-orbit-accent/10 text-orbit-accent shadow-[inset_2px_0_0_rgba(52,211,153,0.5)]' : 'text-orbit-text-secondary hover:text-orbit-text hover:bg-orbit-surface-hover'}`}
                    >
                      <Hash className="h-4 w-4 shrink-0 opacity-60" weight="bold" />
                      <span className="truncate font-medium">{ch.name}</span>
                    </button>
                  )
                })}
              </div>
            )
          })
        )}
      </div>
    </div>
  )
}

function ChatArea({
  channel,
  messages,
  loadingMessages,
  page,
  totalPages,
  onLoadMore,
  onSend,
  authenticated,
}: {
  channel: ChatChannelResponse | null
  messages: ChatMessageResponse[]
  loadingMessages: boolean
  page: number
  totalPages: number
  onLoadMore: () => void
  onSend: (content: string) => void
  authenticated: boolean
}) {
  const [input, setInput] = useState('')
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const containerRef = useRef<HTMLDivElement>(null)
  const prevMessagesLength = useRef(0)
  const [autoScroll, setAutoScroll] = useState(true)

  useEffect(() => {
    if (autoScroll && messages.length > prevMessagesLength.current) {
      messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
    }
    prevMessagesLength.current = messages.length
  }, [messages.length, autoScroll])

  const handleScroll = () => {
    if (!containerRef.current) return
    const el = containerRef.current
    const atBottom = el.scrollHeight - el.scrollTop - el.clientHeight < 100
    setAutoScroll(atBottom)

    if (el.scrollTop < 50 && page < totalPages - 1 && !loadingMessages) {
      onLoadMore()
    }
  }

  const handleSend = () => {
    const trimmed = input.trim()
    if (!trimmed || !authenticated) return
    onSend(trimmed)
    setInput('')
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSend()
    }
  }

  if (!channel) {
    return (
      <div className="h-full flex flex-col items-center justify-center text-orbit-text-secondary">
        <ChatCircleDots className="h-16 w-16 mb-4 opacity-20" />
        <p className="text-sm font-medium">Chọn một kênh để bắt đầu trò chuyện</p>
      </div>
    )
  }

  return (
    <div className="h-full flex flex-col">
      <div className="shrink-0 px-6 py-4 border-b border-orbit-border flex items-center gap-3">
        <Hash className="h-5 w-5 text-orbit-accent" weight="bold" />
        <div>
          <h2 className="font-heading text-sm font-bold text-orbit-text">{channel.name}</h2>
          <p className="text-[11px] text-orbit-text-muted">{CHANNEL_GROUP_LABELS[channel.type]}</p>
        </div>
      </div>

      <div
        ref={containerRef}
        onScroll={handleScroll}
        className="flex-1 overflow-y-auto scrollbar-thin px-6 py-4 space-y-3"
      >
        {page < totalPages - 1 && (
          <div className="flex justify-center py-2">
            {loadingMessages ? (
              <Spinner className="h-4 w-4 text-orbit-accent animate-spin" />
            ) : (
              <button
                onClick={onLoadMore}
                className="text-[11px] text-orbit-accent hover:underline font-medium"
              >
                Tải tin nhắn cũ hơn
              </button>
            )}
          </div>
        )}

        {messages.length === 0 && !loadingMessages && (
          <div className="flex flex-col items-center justify-center h-full text-orbit-text-secondary">
            <p className="text-sm">Chưa có tin nhắn nào</p>
            <p className="text-[12px] mt-1">Hãy gửi tin nhắn đầu tiên!</p>
          </div>
        )}

        {messages.map((msg) => (
          <div key={msg.id} className="flex gap-3 group">
            <div className="shrink-0 h-8 w-8 rounded-full bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center">
              <span className="text-[11px] font-bold text-orbit-accent">
                {msg.senderName.charAt(0).toUpperCase()}
              </span>
            </div>
            <div className="flex-1 min-w-0">
              <div className="flex items-baseline gap-2">
                <span className="text-[13px] font-bold text-orbit-text">{msg.senderName}</span>
                <span className="text-[10px] text-orbit-text-muted">
                  {new Date(msg.createdAt).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}
                </span>
              </div>
              <p className="text-[14px] text-orbit-text-secondary mt-0.5 leading-relaxed whitespace-pre-wrap break-words">
                {msg.content}
              </p>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <div className="shrink-0 px-4 py-3 border-t border-orbit-border">
        {authenticated ? (
          <div className="flex items-end gap-2">
            <div className="flex-1 relative">
              <input
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder={`Gửi tin nhắn trong #${channel.name}`}
                className="w-full bg-orbit-surface border border-orbit-border rounded-xl px-4 py-2.5 text-sm text-orbit-text placeholder:text-orbit-text-muted outline-none focus:border-orbit-accent/40 transition-colors"
                maxLength={1000}
              />
            </div>
            <button
              onClick={handleSend}
              disabled={!input.trim()}
              className="shrink-0 h-10 w-10 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center hover:bg-orbit-accent/20 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
            >
              <PaperPlaneRight className="h-5 w-5 text-orbit-accent" weight="fill" />
            </button>
          </div>
        ) : (
          <div className="text-center py-3">
            <p className="text-[13px] text-orbit-text-muted">
              <span className="text-orbit-accent font-medium cursor-pointer hover:underline">Đăng nhập</span> để tham gia trò chuyện
            </p>
          </div>
        )}
      </div>
    </div>
  )
}

function OnlineMembers() {
  return (
    <div className="h-full flex flex-col overflow-hidden">
      <div className="px-4 py-4 border-b border-orbit-border">
        <h2 className="font-heading text-sm font-bold text-orbit-text tracking-wide">Đang hoạt động</h2>
      </div>
      <div className="flex-1 overflow-y-auto scrollbar-thin px-4 py-4">
        <div className="flex flex-col items-center justify-center h-full text-orbit-text-secondary">
          <UsersThree className="h-10 w-10 mb-3 opacity-20" />
          <p className="text-[13px] text-center font-medium">Tính năng đang phát triển</p>
          <p className="text-[11px] text-center mt-1">Danh sách thành viên online sẽ sớm xuất hiện</p>
        </div>
      </div>
    </div>
  )
}

export function CommunityPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const authenticated = isStudentAuthenticated()

  const { data: channels = [], isLoading: channelsLoading } = useChannels()
  const [activeChannel, setActiveChannel] = useState<ChatChannelResponse | null>(null)
  const [page, setPage] = useState(0)
  const [allMessages, setAllMessages] = useState<ChatMessageResponse[]>([])
  const [totalPages, setTotalPages] = useState(1)

  const { data: fetchedMessages, isLoading: messagesLoading, isFetching: messagesFetching } =
    useChannelMessages(activeChannel?.id ?? null, page)

  useEffect(() => {
    if (channels.length > 0 && !activeChannel) {
      setActiveChannel(channels[0])
    }
  }, [channels, activeChannel])

  useEffect(() => {
    if (fetchedMessages) {
      if (page === 0) {
        setAllMessages(fetchedMessages)
      } else {
        setAllMessages((prev) => [...fetchedMessages, ...prev])
      }

      const totalItems = fetchedMessages.length === 0 && page > 0
        ? page * 50
        : (page + 1) * 50 + (fetchedMessages.length < 50 ? 0 : 50)
      setTotalPages(Math.ceil(totalItems / 50))
    }
  }, [fetchedMessages, page])

  const handleChannelSelect = (ch: ChatChannelResponse) => {
    if (ch.id !== activeChannel?.id) {
      setActiveChannel(ch)
      setPage(0)
      setAllMessages([])
      setTotalPages(1)
    }
  }

  const handleLoadMore = () => {
    if (page < totalPages - 1) {
      setPage((p) => p + 1)
    }
  }

  const handleSendMessage = (content: string) => {
    if (!activeChannel) return
    const currentChannel = activeChannel

    apiStudentPost(`/api/student/community/channels/${currentChannel.channelId}/messages`, { content })
      .then(() => {
        setPage(0)
        setAllMessages([])
        queryClient.invalidateQueries({ queryKey: ['community', 'messages', currentChannel.id, 0] })
      })
      .catch(() => {})
  }

  const handleLoginClick = () => {
    navigate('/student/login')
  }

  return (
    <div className="w-full h-[calc(100vh-73px)]">
      <div className="h-full max-w-[1440px] mx-auto flex border border-orbit-border rounded-2xl overflow-hidden bg-orbit-surface/80 backdrop-blur-sm">
        <div className="w-[260px] shrink-0 border-r border-orbit-border hidden md:block">
          <ChannelList
            channels={channels}
            activeId={activeChannel?.id ?? null}
            onSelect={handleChannelSelect}
            loading={channelsLoading}
          />
        </div>

        <div className="flex-1 min-w-0">
          <ChatArea
            channel={activeChannel}
            messages={allMessages}
            loadingMessages={messagesLoading || messagesFetching}
            page={page}
            totalPages={totalPages}
            onLoadMore={handleLoadMore}
            onSend={handleSendMessage}
            authenticated={authenticated}
          />
        </div>

        <div className="w-[260px] shrink-0 border-l border-orbit-border hidden xl:block">
          <OnlineMembers />
        </div>
      </div>
    </div>
  )
}
