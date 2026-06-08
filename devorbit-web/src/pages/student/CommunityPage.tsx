import { useState, useRef, useEffect, useCallback } from 'react'
import {
  ChatCircleDots,
  Hash,
  UsersThree,
  PaperPlaneRight,
  Spinner,
  MagnifyingGlass,
  CaretDown,
  SignIn,
} from '@phosphor-icons/react'
import { useChannels, useChannelMessages } from '../../hooks/useCommunity'
import { useCommunitySocket } from '../../hooks/useCommunitySocket'
import { isStudentAuthenticated } from '../../lib/auth'
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

const CHANNEL_INITIAL_LIMIT: Record<string, number> = {
  GENERAL: Infinity,
  COURSE: 0,
  TECH_STACK: 0,
}

function ConfirmDialog({ show, title, message, onConfirm, onCancel }: {
  show: boolean; title: string; message: string; onConfirm: () => void; onCancel: () => void
}) {
  if (!show) return null
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm" onClick={onCancel}>
      <div className="bg-orbit-surface border border-orbit-border rounded-2xl p-6 max-w-sm w-full mx-4 shadow-2xl" onClick={(e) => e.stopPropagation()}>
        <h3 className="font-heading text-sm font-bold text-orbit-text mb-2">{title}</h3>
        <p className="text-[13px] text-orbit-text-secondary mb-6 leading-relaxed">{message}</p>
        <div className="flex gap-3 justify-end">
          <button onClick={onCancel} className="px-4 py-2 rounded-xl text-[12px] font-bold text-orbit-text-muted hover:text-orbit-text border border-orbit-border hover:border-orbit-accent/30 transition-colors">Hủy</button>
          <button onClick={onConfirm} className="px-4 py-2 rounded-xl text-[12px] font-bold text-white bg-rose-500/80 hover:bg-rose-500 transition-colors">Xác nhận</button>
        </div>
      </div>
    </div>
  )
}

const SUBSCRIBED_KEY = 'devorbit-subscribed-channels'

function getSubscribedIds(): Set<number> {
  try {
    const raw = localStorage.getItem(SUBSCRIBED_KEY)
    return new Set(raw ? JSON.parse(raw) : [])
  } catch {
    return new Set()
  }
}

function addSubscribedId(id: number) {
  const ids = getSubscribedIds()
  ids.add(id)
  localStorage.setItem(SUBSCRIBED_KEY, JSON.stringify([...ids]))
}

function removeSubscribedId(id: number) {
  const ids = getSubscribedIds()
  ids.delete(id)
  localStorage.setItem(SUBSCRIBED_KEY, JSON.stringify([...ids]))
}

function ChannelList({
  channels,
  activeId,
  onSelect,
  onUnsubscribe,
  loading,
  search,
  onSearchChange,
  subscribedIds,
}: {
  channels: ChatChannelResponse[]
  activeId: number | null
  onSelect: (ch: ChatChannelResponse) => void
  onUnsubscribe: (id: number, name: string) => void
  loading: boolean
  search: string
  onSearchChange: (v: string) => void
  subscribedIds: Set<number>
}) {
  const [collapsed, setCollapsed] = useState<Record<string, boolean>>({
    GENERAL: false,
    COURSE: true,
    TECH_STACK: true,
  })
  const isSearching = search.length > 0

  const filtered = isSearching
    ? channels.filter((ch) => ch.name.toLowerCase().includes(search.toLowerCase()))
    : channels

  const groups = filtered.reduce<Record<string, ChatChannelResponse[]>>((acc, ch) => {
    const g = ch.type
    if (!acc[g]) acc[g] = []
    acc[g].push(ch)
    return acc
  }, {})

  const typeOrder = ['GENERAL', 'COURSE', 'TECH_STACK']

  const toggleCollapse = (type: string) => {
    setCollapsed((prev) => ({ ...prev, [type]: !prev[type] }))
  }

  return (
    <div className="h-full flex flex-col overflow-hidden min-h-0">
      <div className="shrink-0 px-4 py-3 border-b border-orbit-border space-y-3">
        <h2 className="font-heading text-sm font-bold text-orbit-text tracking-wide">Kênh</h2>
        <div className="relative">
          <MagnifyingGlass className="absolute left-2.5 top-1/2 -translate-y-1/2 h-3.5 w-3.5 text-orbit-text-muted" weight="bold" />
          <input
            value={search}
            onChange={(e) => onSearchChange(e.target.value)}
            placeholder="Tìm kênh..."
            className="w-full bg-orbit-surface border border-orbit-border rounded-lg pl-8 pr-3 py-1.5 text-[12px] text-orbit-text placeholder:text-orbit-text-muted outline-none focus:border-orbit-accent/40 transition-colors"
          />
        </div>
      </div>
      <div className="flex-1 overflow-y-auto scrollbar-thin min-h-0">
        {loading ? (
          <div className="flex justify-center py-8">
            <Spinner className="h-5 w-5 text-orbit-accent animate-spin" />
          </div>
        ) : isSearching ? (
          <div className="px-2 pt-2 pb-1 space-y-0.5">
            {typeOrder.map((type) => {
              const list = groups[type]
              if (!list || list.length === 0) return null
              return list.map((ch) => {
                const isActive = ch.id === activeId
                return (
                  <div key={ch.id} className="group relative flex items-center">
                    <button onClick={() => onSelect(ch)}
                      className={`flex-1 flex items-center gap-2.5 px-3 py-1.5 rounded-lg text-left text-sm transition-all duration-200 ${isActive ? 'bg-orbit-accent/10 text-orbit-accent shadow-[inset_2px_0_0_rgba(52,211,153,0.5)]' : 'text-orbit-text-secondary hover:text-orbit-text hover:bg-orbit-surface-hover'}`}
                    >
                      <Hash className="h-4 w-4 shrink-0 opacity-60" weight="bold" />
                      <span className="font-medium min-w-0">{ch.name}</span>
                      <span className={`shrink-0 ml-auto text-[9px] font-bold ${CHANNEL_GROUP_COLORS[type]}`}>{CHANNEL_GROUP_LABELS[type]}</span>
                    </button>
                    {subscribedIds.has(ch.id) && ch.type !== 'GENERAL' && (
                      <button
                        onClick={(e) => { e.stopPropagation(); onUnsubscribe(ch.id, ch.name) }}
                        className="shrink-0 mr-1 h-5 w-5 rounded flex items-center justify-center opacity-0 group-hover:opacity-100 hover:bg-rose-500/20 transition-opacity"
                        title="Bỏ khỏi danh sách"
                      >
                        <span className="text-[10px] font-bold text-rose-400 leading-none">✕</span>
                      </button>
                    )}
                  </div>
                )
              })
            })}
          </div>
        ) : (
          typeOrder.map((type) => {
            const list = groups[type]
            const total = channels.filter((ch) => ch.type === type).length
            if (!list || list.length === 0) return null
            const isCollapsed = collapsed[type]
            const limit = CHANNEL_INITIAL_LIMIT[type]
            const subscribed = list.filter((ch) => subscribedIds.has(ch.id))
            const regular = list.filter((ch) => !subscribedIds.has(ch.id)).slice(0, limit)
            const visible = isCollapsed ? subscribed : [...subscribed, ...regular]
            const hiddenCount = total - subscribed.length - regular.length
            return (
              <div key={type} className="px-2 pt-3 pb-1">
                <button
                  onClick={() => toggleCollapse(type)}
                  className="w-full flex items-center gap-1.5 px-2 mb-1 group"
                >
                  <CaretDown
                    className={`h-3 w-3 text-orbit-text-muted transition-transform duration-200 ${isCollapsed ? '-rotate-90' : ''}`}
                    weight="bold"
                  />
                  <p className={`text-[11px] font-bold tracking-wider uppercase ${CHANNEL_GROUP_COLORS[type] ?? 'text-orbit-text-muted'}`}>
                    {CHANNEL_GROUP_LABELS[type] ?? type}
                  </p>
                  <span className="text-[9px] text-orbit-text-muted ml-1">
                    ({total}{subscribed.length > 0 ? `, đã tham gia ${subscribed.length}` : ''})
                  </span>
                </button>
                {(!isCollapsed || subscribed.length > 0) && (
                  <div className="space-y-0.5">
                    {visible.map((ch) => {
                      const isActive = ch.id === activeId
                      const isSubscribed = subscribedIds.has(ch.id)
                      return (
                        <div key={ch.id} className="group relative flex items-center">
                          <button onClick={() => onSelect(ch)}
                            className={`flex-1 flex items-center gap-2.5 px-3 py-1.5 rounded-lg text-left text-sm transition-all duration-200 ${isActive ? 'bg-orbit-accent/10 text-orbit-accent shadow-[inset_2px_0_0_rgba(52,211,153,0.5)]' : 'text-orbit-text-secondary hover:text-orbit-text hover:bg-orbit-surface-hover'}`}
                          >
                            <Hash className="h-4 w-4 shrink-0 opacity-60" weight="bold" />
                            <span className="font-medium min-w-0">{ch.name}</span>
                          </button>
                          {isSubscribed && ch.type !== 'GENERAL' && (
                            <button
                              onClick={(e) => { e.stopPropagation(); onUnsubscribe(ch.id, ch.name) }}
                              className="shrink-0 mr-1 h-5 w-5 rounded flex items-center justify-center opacity-0 group-hover:opacity-100 hover:bg-rose-500/20 transition-opacity"
                              title="Bỏ khỏi danh sách"
                            >
                              <span className="text-[10px] font-bold text-rose-400 leading-none">✕</span>
                            </button>
                          )}
                        </div>
                      )
                    })}
                    {hiddenCount > 0 && !isCollapsed && (
                      <p className="px-3 py-1 text-[10px] text-orbit-text-muted italic">
                        + {hiddenCount} kênh khác — tìm kiếm để xem
                      </p>
                    )}
                  </div>
                )}
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

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
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
    <div className="h-full flex flex-col min-h-0">
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
              <textarea
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder={`Gửi tin nhắn trong #${channel.name}`}
                rows={1}
                className="w-full bg-orbit-surface border border-orbit-border rounded-xl px-4 py-2.5 text-sm text-orbit-text placeholder:text-orbit-text-muted outline-none focus:border-orbit-accent/40 transition-colors resize-none"
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
    <div className="h-full flex flex-col overflow-hidden min-h-0">
      <div className="shrink-0 px-4 py-4 border-b border-orbit-border">
        <h2 className="font-heading text-sm font-bold text-orbit-text tracking-wide">Đang hoạt động</h2>
      </div>
      <div className="flex-1 overflow-y-auto scrollbar-thin min-h-0 px-4 py-4">
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
  const authenticated = isStudentAuthenticated()

  const { data: channels = [], isLoading: channelsLoading } = useChannels()
  const [activeChannel, setActiveChannel] = useState<ChatChannelResponse | null>(null)
  const [page, setPage] = useState(0)
  const [allMessages, setAllMessages] = useState<ChatMessageResponse[]>([])
  const [totalPages, setTotalPages] = useState(1)

  const [search, setSearch] = useState('')
  const [subscribedIds, setSubscribedIds] = useState<Set<number>>(() => getSubscribedIds())
  const [confirm, setConfirm] = useState<{ id: number; name: string } | null>(null)

  const { data: fetchedPage, isLoading: messagesLoading, isFetching: messagesFetching } =
    useChannelMessages(activeChannel?.id ?? null, page)

  const activeChannelRef = useRef(activeChannel)
  activeChannelRef.current = activeChannel

  const handleRealtimeMessage = useCallback((msg: ChatMessageResponse) => {
    const current = activeChannelRef.current
    if (current && msg.channelId === current.id) {
      setAllMessages((prev) => [...prev, msg])
    }
  }, [])

  const { sendMessage } = useCommunitySocket({
    channelId: activeChannel?.id ?? null,
    enabled: authenticated,
    onMessage: handleRealtimeMessage,
  })

  useEffect(() => {
    if (channels.length > 0) {
      if (!activeChannel) {
        const general = channels.find((ch) => ch.type === 'GENERAL') || channels[0]
        setActiveChannel(general)
      } else if (activeChannel.id < 0) {
        const real = channels.find((ch) => ch.channelId === activeChannel.channelId)
        if (real) setActiveChannel(real)
      }
    }
  }, [channels, activeChannel])

  useEffect(() => {
    if (fetchedPage) {
      const { content, totalPages } = fetchedPage
      const ordered = [...content].reverse()
      if (page === 0) {
        setAllMessages(ordered)
      } else {
        setAllMessages((prev) => [...ordered, ...prev])
      }
      setTotalPages(totalPages)
    }
  }, [fetchedPage, page])

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

  const handleUnsubscribe = (id: number, name: string) => {
    setConfirm({ id, name })
  }

  const confirmUnsubscribe = () => {
    if (!confirm) return
    removeSubscribedId(confirm.id)
    setSubscribedIds(getSubscribedIds())
    if (activeChannel?.id === confirm.id) {
      const general = channels.find((ch) => ch.type === 'GENERAL') || channels[0]
      setActiveChannel(general)
      setPage(0)
      setAllMessages([])
      setTotalPages(1)
    }
    setConfirm(null)
  }

  const handleSendMessage = (content: string) => {
    if (!activeChannel) return
    sendMessage(activeChannel.id, content)
    addSubscribedId(activeChannel.id)
    setSubscribedIds(getSubscribedIds())
    setSearch('')
  }

  return (
    <>
      <ConfirmDialog
        show={confirm !== null}
        title="Bỏ kênh"
        message={confirm ? `Bỏ "${confirm.name}" khỏi danh sách? Bạn vẫn có thể tìm lại qua ô tìm kiếm.` : ''}
        onConfirm={confirmUnsubscribe}
        onCancel={() => setConfirm(null)}
      />
      {!authenticated ? (
        <div className="w-full h-[calc(100vh-73px)] min-h-0 flex items-center justify-center">
          <div className="flex flex-col items-center text-center max-w-md px-6">
            <div className="h-16 w-16 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center mb-6">
              <UsersThree className="h-8 w-8 text-orbit-accent" weight="duotone" />
            </div>
            <h1 className="font-heading text-2xl font-black text-orbit-text tracking-tight mb-3">Cộng đồng</h1>
            <p className="text-[14px] text-orbit-text-secondary mb-8 leading-relaxed">
              Đăng nhập để tham gia thảo luận, đánh giá môn học và kết nối với sinh viên UIT.
            </p>
            <button
              onClick={() => navigate('/student/login')}
              className="btn-primary text-[12px] px-8 py-3 flex items-center gap-2"
            >
              <SignIn className="h-4 w-4" weight="bold" />
              Đăng nhập
            </button>
          </div>
        </div>
      ) : (
        <div className="w-full h-[calc(100vh-73px)] min-h-0">
          <div className="h-full min-h-0 max-w-[1440px] mx-auto flex border border-orbit-border rounded-2xl overflow-hidden bg-orbit-surface/80 backdrop-blur-sm">
            <div className="w-[260px] shrink-0 border-r border-orbit-border hidden md:block min-h-0">
              <ChannelList
                channels={channels}
                activeId={activeChannel?.id ?? null}
                onSelect={handleChannelSelect}
                onUnsubscribe={handleUnsubscribe}
                loading={channelsLoading}
                search={search}
                onSearchChange={setSearch}
                subscribedIds={subscribedIds}
              />
            </div>

            <div className="flex-1 min-w-0 min-h-0">
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

            <div className="w-[260px] shrink-0 border-l border-orbit-border hidden xl:block min-h-0">
              <OnlineMembers />
            </div>
          </div>
        </div>
      )}
    </>
  )
}
