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
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Sinh viên</th>
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Nội dung</th>
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Ngày</th>
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Thao tác</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-clay-border">
          {messages.map((msg) => (
            <tr key={msg.id} className="transition-colors hover:bg-orbit-surface/30">
              <td className="px-4 py-3 text-sm text-ink-primary text-center">{msg.studentName}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary max-w-md truncate">{msg.content}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary text-center">{new Date(msg.createdAt).toLocaleDateString()}</td>
              <td className="px-4 py-3 text-sm text-center">
                <div className="flex items-center justify-center">
                  <button onClick={() => onDelete(msg.id)} className="btn-ghost text-xs text-red-400">Xoá</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
