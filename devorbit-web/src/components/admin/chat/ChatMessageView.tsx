import type { ChatMessageAdmin } from '../../../types/admin'

interface ChatMessageViewProps {
  messages: ChatMessageAdmin[]
  loading?: boolean
}

export function ChatMessageView({ messages, loading }: ChatMessageViewProps) {
  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="w-8 h-8 border-2 border-orbit-border border-t-clay-accent rounded-full animate-spin" />
      </div>
    )
  }

  if (messages.length === 0) {
    return (
      <div className="glass-card p-8 text-center">
        <p className="text-ink-secondary">Chọn một phiên để xem hội thoại</p>
      </div>
    )
  }

  return (
    <div className="glass-card overflow-hidden">
      <div className="px-4 py-3 border-b border-orbit-border">
        <h3 className="text-sm font-semibold text-ink-primary">Hội thoại</h3>
      </div>
      <div className="p-4 space-y-4 max-h-[600px] overflow-y-auto">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex ${msg.sender === 'AI' ? 'justify-start' : 'justify-end'}`}
          >
            <div
              className={`max-w-[70%] rounded-lg px-4 py-2.5 text-sm ${
                msg.sender === 'AI'
                  ? 'bg-orbit-accent/10 text-ink-primary'
                  : 'bg-orbit-surface text-ink-primary'
              }`}
            >
              <p className="text-xs text-ink-muted mb-1">
                {msg.sender === 'AI' ? 'AI Tutor' : 'Sinh viên'} &middot; {new Date(msg.createdAt).toLocaleString()}
              </p>
              <p className="whitespace-pre-wrap">{msg.content}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
