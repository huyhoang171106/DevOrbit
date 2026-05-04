import { useState } from 'react'
import { apiAdminGet, apiAdminDelete } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { NoteDetailDialog } from '../../components/admin/NoteDetailDialog'
import type { NoteResponse } from '../../types/api'

const targetLabels: Record<string, string> = {
  COURSE: 'Course',
  REPO: 'Repository',
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
    if (!confirm('Delete this note? This action cannot be undone.')) return
    try {
      await apiAdminDelete(`/api/admin/notes/${id}`, token)
      if (selectedNote?.id === id) setSelectedNote(null)
      refetch()
    } catch (e) { console.error(e) }
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px]">
        <h1 className="display-sm text-ink mb-1">Student Notes</h1>
        <p className="body-sm text-steel">Browse and manage notes created by students.</p>
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-[96px]">
          <div className="flex items-center gap-3 body-sm text-steel">
            <svg className="h-5 w-5 animate-spin text-brand-green" viewBox="0 0 24 24" fill="none">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
            </svg>
            Loading notes...
          </div>
        </div>
      ) : (
        <div className="card-base overflow-hidden border border-hairline p-0">
          <table className="min-w-full text-sm">
            <thead>
              <tr className="border-b border-hairline bg-surface-soft">
                <th className="table-header text-left font-medium text-steel py-3 px-4">Title</th>
                <th className="table-header text-left font-medium text-steel py-3 px-4">Student</th>
                <th className="table-header text-left font-medium text-steel py-3 px-4">Target</th>
                <th className="table-header text-left font-medium text-steel py-3 px-4">Snippets</th>
                <th className="table-header text-right font-medium text-steel py-3 px-4">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-hairline bg-canvas">
              {(notes ?? []).length === 0 && (
                <tr>
                  <td colSpan={5} className="px-4 py-10 text-center body-sm text-steel">No notes found.</td>
                </tr>
              )}
              {(notes ?? []).map((n) => (
                <tr key={n.id} className="transition-colors hover:bg-surface-soft">
                  <td className="table-cell py-3 px-4">
                    <button
                      onClick={() => setSelectedNote(n)}
                      className="text-left text-ink hover:text-brand-green transition-colors font-medium body-sm"
                    >
                      {truncate(n.title, 60)}
                    </button>
                  </td>
                  <td className="table-cell text-steel body-sm py-3 px-4">{n.studentCode}</td>
                  <td className="table-cell py-3 px-4">
                    <span className="code-sm text-steel bg-surface border border-hairline-soft px-1.5 py-0.5 rounded-md">{targetLabels[n.targetType] ?? n.targetType}{n.targetId ? ` #${n.targetId}` : ''}</span>
                  </td>
                  <td className="table-cell text-steel body-sm py-3 px-4">{n.snippets?.length ?? 0}</td>
                  <td className="table-cell text-right py-3 px-4">
                    <div className="flex justify-end gap-2">
                      <button onClick={() => setSelectedNote(n)} className="btn-secondary !py-1 !px-3 !bg-surface !text-xs !text-steel hover:!text-ink border border-hairline">View</button>
                      <button onClick={() => handleDelete(n.id)} className="btn-secondary !py-1 !px-3 !bg-surface !text-xs !text-danger-11 hover:!bg-danger-3 border border-danger-6">Delete</button>
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
