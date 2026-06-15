import type { ChatSessionAdmin } from '../../../types/admin'

interface ChatSessionTableProps {
  sessions: ChatSessionAdmin[]
  selectedSessionId: string | null
  onSelect: (session: ChatSessionAdmin) => void
}

export function ChatSessionTable({ sessions, selectedSessionId, onSelect }: ChatSessionTableProps) {
  if (sessions.length === 0) {
    return (
      <div className="glass-card p-8 text-center">
        <p className="text-ink-secondary">Không có phiên chat</p>
      </div>
    )
  }

  return (
    <div className="glass-card" onWheel={(e) => e.stopPropagation()}>
      <div className="px-4 py-3 border-b border-orbit-border">
        <h3 className="text-sm font-semibold text-ink-primary">Phiên</h3>
      </div>
      <div className="divide-y divide-clay-border/50 max-h-[600px] overflow-y-auto overscroll-contain" style={{ scrollbarWidth: 'thin', scrollbarColor: '#52525b transparent' }}>
        {sessions.map((session) => (
          <button
            key={session.id}
            onClick={() => onSelect(session)}
            className={`w-full text-left px-4 py-3 transition-colors ${
              selectedSessionId === session.id
                ? 'bg-orbit-accent/10 text-orbit-accent'
                : 'text-ink-secondary hover:bg-orbit-surface/30 hover:text-ink-primary'
            }`}
          >
            <span className="text-sm font-medium block truncate">{session.title}</span>
            <span className="text-xs text-ink-muted">
              {session.studentName} &middot; {session.messageCount} tin nhắn &middot; {new Date(session.createdAt).toLocaleDateString()}
            </span>
          </button>
        ))}
      </div>
    </div>
  )
}
