import { useState, useEffect } from 'react'
import type { YoutubePlaylistRequest, ArticleRequest, TutorialRequest } from '../../../types/api'

type ResourceType = 'youtube' | 'article' | 'tutorial'
type ResourceData = YoutubePlaylistRequest | ArticleRequest | TutorialRequest

interface ResourceDialogProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: ResourceData) => void
  resourceType: ResourceType
  initial?: ResourceData | null
  loading?: boolean
}

const emptyForm: Record<ResourceType, ResourceData> = {
  youtube: { title: '', url: '', description: '', channelName: '' },
  article: { title: '', url: '', author: '', description: '' },
  tutorial: { title: '', url: '', type: '', description: '' },
}

const TYPE_LABELS: Record<ResourceType, string> = {
  youtube: 'YouTube Playlist',
  article: 'Bài viết',
  tutorial: 'Hướng dẫn',
}

export function ResourceDialog({ open, onClose, onSubmit, resourceType, initial, loading }: ResourceDialogProps) {
  const [form, setForm] = useState<ResourceData>(emptyForm[resourceType])

  useEffect(() => {
    setForm(initial ?? emptyForm[resourceType])
  }, [initial, resourceType, open])

  if (!open) return null

  const handleChange = (field: string, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    onSubmit(form)
  }

  const title = initial ? 'Sửa' : 'Thêm'
  const typeLabel = TYPE_LABELS[resourceType]

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="glass-card w-full max-w-lg p-6 shadow-2xl">
        <div className="flex items-center justify-between mb-6">
          <h2 className="heading-5 text-ink-primary">{title} {typeLabel}</h2>
          <button onClick={onClose} className="text-ink-secondary hover:text-ink-primary transition-colors">
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label">Tiêu đề *</label>
            <input
              type="text"
              value={(form as Record<string, string>).title ?? ''}
              onChange={(e) => handleChange('title', e.target.value)}
              className="input-field"
              required
            />
          </div>
          <div>
            <label className="label">URL *</label>
            <input
              type="url"
              value={(form as Record<string, string>).url ?? ''}
              onChange={(e) => handleChange('url', e.target.value)}
              className="input-field"
              required
            />
          </div>
          {resourceType === 'youtube' && (
            <div>
              <label className="label">Tên kênh</label>
              <input
                type="text"
                value={(form as YoutubePlaylistRequest).channelName ?? ''}
                onChange={(e) => handleChange('channelName', e.target.value)}
                className="input-field"
              />
            </div>
          )}
          {resourceType === 'article' && (
            <div>
              <label className="label">Tác giả</label>
              <input
                type="text"
                value={(form as ArticleRequest).author ?? ''}
                onChange={(e) => handleChange('author', e.target.value)}
                className="input-field"
              />
            </div>
          )}
          {resourceType === 'tutorial' && (
            <div>
              <label className="label">Nền tảng</label>
              <input
                type="text"
                value={(form as TutorialRequest).type ?? ''}
                onChange={(e) => handleChange('type', e.target.value)}
                className="input-field"
                placeholder="VD: YouTube, Udemy, Documentation"
              />
            </div>
          )}
          <div>
            <label className="label">Mô tả</label>
            <textarea
              value={(form as Record<string, string>).description ?? ''}
              onChange={(e) => handleChange('description', e.target.value)}
              className="input-field"
              rows={2}
            />
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost text-sm" disabled={loading}>Huỷ</button>
            <button type="submit" className="btn-primary text-sm" disabled={loading}>
              {loading ? 'Đang lưu...' : initial ? 'Cập nhật' : 'Thêm'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
