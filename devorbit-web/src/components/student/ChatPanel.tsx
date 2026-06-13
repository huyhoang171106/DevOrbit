import { useNavigate } from 'react-router-dom'
import { Sparkle, ArrowUp, Minus, ArrowsOut, ArrowsClockwise } from '@phosphor-icons/react'
import { ChatMessage } from './ChatMessage'
import { useChat } from './ChatContext'

const SUGGESTIONS = [
  'SE104 hoc sao cho tot?',
  'MA006 hoc phan nay co tai lieu gi?',
  'IS201 co repo nao nen xem khong?',
] as const

interface ChatPanelProps {
  onClose?: () => void
  /** When true, render without floating-style constraints */
  fullPage?: boolean
}

export function ChatPanel({ onClose, fullPage }: ChatPanelProps) {
  const navigate = useNavigate()
  const {
    messages,
    input,
    setInput,
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
  } = useChat()

  const header = (
    <div className="flex items-center justify-between px-6 py-4 border-b border-zinc-800/40 bg-zinc-900/10 shrink-0">
      <div className="flex items-center gap-3">
        <div className="h-9 w-9 rounded-xl bg-orbit-accent/15 border border-orbit-accent/20 flex items-center justify-center relative">
          <Sparkle className="h-5 w-5 text-orbit-accent animate-pulse" weight="fill" aria-hidden="true" />
          <span className="absolute bottom-0 right-0 h-2 w-2 rounded-full bg-emerald-400 border border-zinc-950" />
        </div>
        <div>
          <h4 className="text-[14px] font-bold text-zinc-100">DevOrbit AI</h4>
          <p className="text-[11px] text-zinc-400">Trợ lý học tập thông minh</p>
        </div>
      </div>
      <div className="flex items-center gap-2">
        <button
          onClick={clearHistory}
          title="Đoạn chat mới"
          aria-label="Đoạn chat mới"
          className="p-1.5 rounded-lg text-zinc-400 hover:text-orbit-accent hover:bg-zinc-800/40 transition-colors"
        >
          <ArrowsClockwise className="h-4.5 w-4.5" aria-hidden="true" />
        </button>
        <button
          onClick={() => navigate('/ai-tutor')}
          title="Mở toàn màn hình"
          aria-label="Mở toàn màn hình"
          className="p-1.5 rounded-lg text-zinc-400 hover:text-orbit-accent hover:bg-zinc-800/40 transition-colors"
        >
          <ArrowsOut className="h-5 w-5" aria-hidden="true" />
        </button>
        {onClose && (
          <button
            onClick={onClose}
            aria-label="Thu gọn"
            title="Thu gọn"
            className="p-1.5 rounded-lg text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800/40 transition-colors"
          >
            <Minus className="h-5 w-5" aria-hidden="true" />
          </button>
        )}
      </div>
    </div>
  )

  const messagesList = (
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
  )

  const suggestionChips = messages.length === 1 ? (
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
  ) : null

  const inputArea = (
    <div className="p-4 border-t border-zinc-800/40 bg-zinc-900/10 shrink-0">
      <form onSubmit={handleSubmit} className="relative flex items-center">
        <input
          ref={inputRef}
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
  )

  if (fullPage) {
    return (
      <div data-lenis-prevent className="w-full h-full flex flex-col rounded-[2rem] border border-zinc-800/50 bg-zinc-950/80 backdrop-blur-xl shadow-[0_20px_50px_rgba(0,0,0,0.5)] overflow-hidden">
        {messagesList}
        {suggestionChips}
        {inputArea}
      </div>
    )
  }

  return (
    <>
      {header}
      {messagesList}
      {suggestionChips}
      {inputArea}
    </>
  )
}
