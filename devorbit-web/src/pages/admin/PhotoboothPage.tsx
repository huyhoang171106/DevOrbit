import { useState, useCallback, useEffect } from 'react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { PhotoboothFrameGrid } from '../../components/admin/photobooth/PhotoboothFrameGrid'
import { FrameUploadDialog } from '../../components/admin/photobooth/FrameUploadDialog'
import { FrameSlotEditor } from '../../components/admin/photobooth/FrameSlotEditor'
import { frameService } from '../../lib/frames/frameService'
import { reloadFrames, normalizeStoredFrameSlots } from '../../lib/frames/frameDefinitions'
import { useRequireAuth } from '../../lib/hooks'
import type { StoredFrame } from '../../types/frames'

function emptySlots(count: number) {
  const h = Math.round(2000 / count)
  return Array.from({ length: count }, (_, i) => ({
    id: `slot${i + 1}`, x: 0, y: i * h, width: 2000, height: h, borderRadius: 0,
  }))
}

export function PhotoboothPage() {
  useRequireAuth()
  const [frames, setFrames] = useState<StoredFrame[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [editingSlots, setEditingSlots] = useState<StoredFrame | null>(null)
  const [showUpload, setShowUpload] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const list = await frameService.list()
      const normalized = await Promise.all(list.map(normalizeStoredFrameSlots))
      setFrames(normalized)
    } catch (e: any) {
      setError(e?.message || 'Tải frame thất bại')
    }
    setLoading(false)
  }, [])

  useEffect(() => { load() }, [load])

  const handleDelete = async (id: string) => {
    if (!confirm('Xoá frame này?')) return
    setError(null)
    try {
      const ok = await frameService.delete(id)
      if (!ok) { setError('Xoá thất bại'); return }
      reloadFrames()
      load()
    } catch (e: any) {
      setError(e?.message || 'Xoá thất bại')
    }
  }

  const handleRename = async (frame: StoredFrame, newDisplayName: string) => {
    setError(null)
    try {
      const ok = await frameService.upsert({ ...frame, displayName: newDisplayName })
      if (!ok) { setError('Đổi tên thất bại'); return }
      reloadFrames()
      setFrames((prev) => prev.map((f) => (f.id === frame.id ? { ...f, displayName: newDisplayName } : f)))
    } catch (e: any) {
      setError(e?.message || 'Đổi tên thất bại')
    }
  }

  const handleSaveSlots = async (updated: StoredFrame) => {
    setError(null)
    try {
      const ok = await frameService.upsert(updated)
      if (!ok) { setError('Lưu ô thất bại'); setEditingSlots(null); return }
      const saved = await frameService.get(updated.id)
      if (saved) {
        const normalized = await normalizeStoredFrameSlots(saved)
        setFrames((prev) => prev.map((f) => (f.id === normalized.id ? normalized : f)))
      }
    } catch (e: any) {
      setError(e?.message || 'Lưu ô thất bại')
    }
    setEditingSlots(null)
  }

  const handleUpload = async (data: { displayName: string; photoCount: number }, file: File | null) => {
    setError(null)
    try {
      const id = data.displayName.trim().toLowerCase().replace(/\s+/g, '-')
      const newFrame: StoredFrame = {
        id,
        name: data.displayName.trim(),
        displayName: data.displayName.trim(),
        photoCount: data.photoCount as 1 | 2 | 3 | 4 | 6,
        description: `Frame ${data.photoCount} ảnh`,
        slots: emptySlots(data.photoCount),
        overlayImage: '',
        filter: 'normal',
        backgroundColor: '#ffffff',
      }
      let imageUrl = ''
      if (file) {
        const url = await frameService.uploadImage(id, file)
        if (!url) { setError('Tải lên thất bại'); setShowUpload(false); return }
        imageUrl = url
      }
      newFrame.overlayImage = imageUrl
      const ok = await frameService.upsert(newFrame)
      if (!ok) { setError('Lưu frame thất bại'); setShowUpload(false); return }
    } catch (e: any) {
      setError(e?.message || 'Tải lên thất bại')
    }
    setShowUpload(false)
    load()
  }

  return (
    <AdminPageLayout
      title="Photobooth Frames"
      description="Cấu hình frame và vị trí các ô"
      action={
        <button onClick={() => setShowUpload(true)} className="btn-primary self-start">+ Tải Frame</button>
      }
    >
      {loading && <AdminSpinner text="Đang tải..." />}
      {error && <AdminErrorBanner message={error} onRetry={load} />}

      {!loading && (
        <PhotoboothFrameGrid
          frames={frames}
          onEditSlots={setEditingSlots}
          onDelete={handleDelete}
          onRename={handleRename}
        />
      )}

      {editingSlots && (
        <FrameSlotEditor
          frame={editingSlots}
          onSave={handleSaveSlots}
          onClose={() => setEditingSlots(null)}
        />
      )}

      {showUpload && (
        <FrameUploadDialog
          onUpload={handleUpload}
          onClose={() => setShowUpload(false)}
        />
      )}
    </AdminPageLayout>
  )
}
