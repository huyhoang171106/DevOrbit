import { useState } from 'react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { NoteTable } from '../../components/admin/notes/NoteTable'
import { NoteDetailDialog } from '../../components/admin/NoteDetailDialog'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { getAdminToken } from '../../lib/auth'
import type { NoteResponse } from '../../types/api'

export function NotesPage() {
  const token = getAdminToken()
  const [selectedNote, setSelectedNote] = useState<NoteResponse | null>(null)
  const [detailOpen, setDetailOpen] = useState(false)

  const { data: notes, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getNotes(t),
    [],
  )

  const handleDelete = async (id: number) => {
    if (!token || !confirm('Xoá ghi chú này?')) return
    try {
      await adminApi.deleteNote(token, id)
      if (selectedNote?.id === id) { setDetailOpen(false); setSelectedNote(null) }
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  return (
    <AdminPageLayout title="Ghi chú" description="Xem và quản lý ghi chú của sinh viên">
      {loading && <AdminSpinner text="Đang tải ghi chú..." />}
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}
      {notes && (
        <NoteTable
          notes={notes}
          onView={(n) => { setSelectedNote(n); setDetailOpen(true) }}
          onDelete={handleDelete}
        />
      )}
      <NoteDetailDialog
        open={detailOpen}
        note={selectedNote}
        onClose={() => { setDetailOpen(false); setSelectedNote(null) }}
      />
    </AdminPageLayout>
  )
}
