import type { NoteResponse } from '../../../types/api'

interface NoteTableProps {
  notes: NoteResponse[]
  onView: (note: NoteResponse) => void
  onDelete: (id: number) => void
}

export function NoteTable({ notes, onView, onDelete }: NoteTableProps) {
  if (notes.length === 0) {
    return (
      <div className="glass-card p-8 text-center">
        <p className="text-ink-secondary">Không có ghi chú</p>
      </div>
    )
  }

  return (
    <div className="glass-card overflow-hidden border border-orbit-border">
      <table className="w-full">
        <thead>
          <tr className="border-b border-orbit-border bg-orbit-surface/50">
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Tiêu đề</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Sinh viên</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Đối tượng</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Đoạn mã</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Ngày</th>
            <th className="px-4 py-3 text-right text-xs font-medium text-ink-secondary uppercase">Thao tác</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-clay-border">
          {notes.map((note) => (
            <tr key={note.id} className="transition-colors hover:bg-orbit-surface/30">
              <td className="px-4 py-3 text-sm font-medium text-ink-primary">{note.title}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary">{note.studentName}</td>
              <td className="px-4 py-3 text-sm">
                <span className="inline-flex px-2 py-0.5 rounded text-xs bg-orbit-accent/10 text-orbit-accent">
                  {note.targetType}
                </span>
              </td>
              <td className="px-4 py-3 text-sm text-ink-secondary">{note.snippets?.length ?? 0}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary">{new Date(note.createdAt).toLocaleDateString()}</td>
              <td className="px-4 py-3 text-sm text-right">
                <div className="flex justify-end gap-2">
                  <button onClick={() => onView(note)} className="btn-ghost text-xs">Xem</button>
                  <button onClick={() => onDelete(note.id)} className="btn-ghost text-xs text-red-400">Xoá</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
