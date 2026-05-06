import { useState, useEffect } from 'react'
import type { ArticleRequest } from '../../types/api'

type Props = {
  open: boolean
  onClose: () => void
  onSubmit: (data: ArticleRequest) => void
  initial?: ArticleRequest
}

const empty: ArticleRequest = { title: '', url: '', author: '', description: '' }

export function ArticleDialog({ open, onClose, onSubmit, initial }: Props) {
  const [form, setForm] = useState<ArticleRequest>(initial ?? empty)

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
          <h2 className="heading-5 text-ink">
            {initial ? 'Edit Article' : 'Add Article'}
          </h2>
          <button type="button" onClick={onClose} className="rounded-lg p-1 text-ink-secondary hover:bg-glass-surface-raised hover:text-ink transition-colors">
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label">Title *</label>
            <input name="title" value={form.title} onChange={handleChange} className="input-field" required />
          </div>
          <div>
            <label className="label">URL *</label>
            <input name="url" value={form.url} onChange={handleChange} className="input-field" required />
          </div>
          <div>
            <label className="label">Author</label>
            <input name="author" value={form.author ?? ''} onChange={handleChange} className="input-field" />
          </div>
          <div>
            <label className="label">Description</label>
            <textarea name="description" value={form.description ?? ''} onChange={handleChange} rows={3} className="input-field" />
          </div>
          <div className="flex justify-end gap-3 pt-4">
            <button type="button" onClick={onClose} className="btn-secondary">Cancel</button>
            <button type="submit" className="btn-primary">{initial ? 'Update' : 'Create'}</button>
          </div>
        </form>
      </div>
    </div>
  )
}
