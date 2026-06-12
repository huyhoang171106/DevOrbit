import { useState } from 'react'

interface FrameUploadDialogProps {
  onUpload: (frame: { displayName: string; photoCount: number }, file: File | null) => void
  onClose: () => void
}

export function FrameUploadDialog({ onUpload, onClose }: FrameUploadDialogProps) {
  const [displayName, setDisplayName] = useState('')
  const [photoCount, setPhotoCount] = useState(1)
  const [file, setFile] = useState<File | null>(null)

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    onUpload({ displayName: displayName.trim() || 'Frame mới', photoCount }, file)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="glass-card w-full max-w-md p-6 shadow-2xl">
        <div className="flex items-center justify-between mb-6">
          <h2 className="heading-5 text-ink-primary">Tải lên Frame mới</h2>
          <button onClick={onClose} className="text-ink-secondary hover:text-ink-primary transition-colors">
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label">Tên hiển thị</label>
            <input
              type="text"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              className="input-field"
              placeholder="Frame của tôi"
            />
          </div>
          <div>
            <label className="label">Số ảnh</label>
            <input
              type="number"
              value={photoCount}
              onChange={(e) => setPhotoCount(Math.max(1, Number(e.target.value)))}
              className="input-field"
              min={1}
              max={6}
            />
          </div>
          <div>
            <label className="label">Ảnh nền (PNG)</label>
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setFile(e.target.files?.[0] ?? null)}
              className="text-sm text-ink-secondary file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-medium file:bg-orbit-accent/10 file:text-orbit-accent hover:file:bg-orbit-accent/20"
            />
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost text-sm">Huỷ</button>
            <button type="submit" className="btn-primary text-sm">Tải lên</button>
          </div>
        </form>
      </div>
    </div>
  )
}
