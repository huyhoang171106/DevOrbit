import { useState } from 'react'
import { apiAdminGet, apiAdminDelete } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { NoteDetailDialog } from '../../components/admin/NoteDetailDialog'
import type { NoteResponse } from '../../types/api'

const targetLabels: Record<string, string> = {
  COURSE: 'Môn học',
  REPO: 'Kho',
  NONE: '—',
}

function truncate(str: string, n: number) {
  return str.length > n ? str.slice(0, n) + '...' : str
}

export function AdminNotesPage() {
  useRequireAuth()
  const token = getAdminToken()!
  const [selectedNote, setSelectedNote] = useState<NoteResponse | null>(null)

  const { data: notes, loading, refetch } = useApiFetch(
    () => apiAdminGet<NoteResponse[]>('/api/admin/notes', token),
    [token],
  )

  async function handleDelete(id: number) {
    if (!confirm('Xóa ghi chú này? Hành động này không thể hoàn tác')) return
    try {
      await apiAdminDelete(`/api/admin/notes/${id}`, token)
      if (selectedNote?.id === id) setSelectedNote(null)
      refetch()
    } catch (e) { console.error(e) }
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px]">
        <h1 className="display-sm text-clay-text mb-1">Ghi chú sinh viên</h1>
        <p className="body-sm text-clay-text-muted">Xem và quản lý ghi chú của sinh viên</p>
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-[96px]">
          <div className="flex items-center gap-3 body-sm text-clay-text-muted">
            <svg className="h-5 w-5 animate-spin text-emerald-400" viewBox="0 0 24 24" fill="none">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
            </svg>
            Đang tải ghi chú...
          </div>
        </div>
      ) : (
        <div className="glass-card overflow-hidden border border-clay-border p-0">
          <table className="min-w-full text-sm">
            <thead>
              <tr className="border-b border-clay-border bg-glass-surface-raised">
                <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4">Tiêu đề</th>
                <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4">Sinh viên</th>
                <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4">Đối tượng</th>
                <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4">Đoạn mã</th>
                <th className="table-header text-right font-medium text-clay-text-muted py-3 px-4">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-clay-border bg-clay-bg">
              {(notes ?? []).length === 0 && (
                <tr>
                  <td colSpan={5} className="px-4 py-10 text-center body-sm text-clay-text-muted">Không tìm thấy ghi chú nào</td>
                </tr>
              )}
              {(notes ?? []).map((n) => (
                <tr key={n.id} className="transition-colors hover:bg-glass-surface-raised">
                  <td className="table-cell py-3 px-4">
                    <button
                      onClick={() => setSelectedNote(n)}
                      className="text-left text-clay-text hover:text-emerald-400 transition-colors font-medium body-sm"
                    >
                      {truncate(n.title, 60)}
                    </button>
                  </td>
                  <td className="table-cell text-clay-text-muted body-sm py-3 px-4">{n.studentCode}</td>
                  <td className="table-cell py-3 px-4">
                    <span className="code-sm text-clay-text-muted bg-clay-surface border border-clay-border px-1.5 py-0.5 rounded-md">{targetLabels[n.targetType] ?? n.targetType}{n.targetId ? ` #${n.targetId}` : ''}</span>
                  </td>
                  <td className="table-cell text-clay-text-muted body-sm py-3 px-4">{n.snippets?.length ?? 0}</td>
                  <td className="table-cell text-right py-3 px-4">
                    <div className="flex justify-end gap-2">
                      <button onClick={() => setSelectedNote(n)} className="btn-secondary !py-1 !px-3 !bg-clay-surface !text-xs !text-clay-text-muted hover:!text-clay-text border border-clay-border">Xem</button>
                      <button onClick={() => handleDelete(n.id)} className="btn-secondary !py-1 !px-3 !bg-clay-surface !text-xs !text-red-400 hover:!bg-red-500/10 border border-red-500/30">Xóa</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <NoteDetailDialog
        open={selectedNote !== null}
        note={selectedNote}
        onClose={() => setSelectedNote(null)}
      />
    </div>
  )
}
