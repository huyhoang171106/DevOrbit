import { useState, useEffect } from 'react'
import type { YoutubePlaylistRequest } from '../../types/api'

type Props = {
  open: boolean
  onClose: () => void
  onSubmit: (data: YoutubePlaylistRequest) => void
  initial?: YoutubePlaylistRequest
}

const empty: YoutubePlaylistRequest = { title: '', url: '', description: '', channelName: '' }

export function YoutubePlaylistDialog({ open, onClose, onSubmit, initial }: Props) {
  const [form, setForm] = useState<YoutubePlaylistRequest>(initial ?? empty)

  useEffect(() => { setForm(initial ?? empty) }, [initial, open])

  if (!open) return null

  function handleChange(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
    const { name, value } = e.target
    setForm((p) => ({ ...p, [name]: value }))
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    onSubmit(form)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm">
      <div className="glass-card w-full max-w-lg p-6 shadow-2xl">
        <div className="flex items-center justify-between mb-4">
          <h2 className="heading-5 text-clay-text">
            {initial ? 'Sửa danh sách' : 'Thêm danh sách'}
          </h2>
          <button type="button" onClick={onClose} className="rounded-lg p-1 text-clay-text-muted hover:bg-glass-surface-raised hover:text-clay-text transition-colors">
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label">Tiêu đề *</label>
            <input name="title" value={form.title} onChange={handleChange} className="input-field" required />
          </div>
          <div>
            <label className="label">Đường dẫn *</label>
            <input name="url" value={form.url} onChange={handleChange} className="input-field" required placeholder="https://youtube.com/playlist?list=..." />
          </div>
          <div>
            <label className="label">Tên kênh</label>
            <input name="channelName" value={form.channelName ?? ''} onChange={handleChange} className="input-field" />
          </div>
          <div>
            <label className="label">Mô tả</label>
            <textarea name="description" value={form.description ?? ''} onChange={handleChange} rows={3} className="input-field" />
          </div>
          <div className="flex justify-end gap-3 pt-4">
            <button type="button" onClick={onClose} className="btn-secondary">Hủy</button>
            <button type="submit" className="btn-primary">{initial ? 'Cập nhật' : 'Thêm'}</button>
          </div>
        </form>
      </div>
    </div>
  )
}
