import type { CommunityMessageAdmin } from '../../../types/admin'

interface MessageTableProps {
  messages: CommunityMessageAdmin[]
  onDelete: (id: number) => void
}

export function MessageTable({ messages, onDelete }: MessageTableProps) {
  if (messages.length === 0) {
    return (
      <div className="glass-card p-8 text-center">
        <p className="text-ink-secondary">Không có tin nhắn trong kênh này</p>
      </div>
    )
  }

  return (
    <div className="glass-card overflow-hidden border border-orbit-border">
      <table className="w-full">
        <thead>
          <tr className="border-b border-orbit-border bg-orbit-surface/50">
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Sinh viên</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Nội dung</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Ngày</th>
            <th className="px-4 py-3 text-right text-xs font-medium text-ink-secondary uppercase">Thao tác</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-clay-border">
          {messages.map((msg) => (
            <tr key={msg.id} className="transition-colors hover:bg-orbit-surface/30">
              <td className="px-4 py-3 text-sm text-ink-primary">{msg.studentName}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary max-w-md truncate">{msg.content}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary">{new Date(msg.createdAt).toLocaleDateString()}</td>
              <td className="px-4 py-3 text-sm text-right">
                <button onClick={() => onDelete(msg.id)} className="btn-ghost text-xs text-red-400">Xoá</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
