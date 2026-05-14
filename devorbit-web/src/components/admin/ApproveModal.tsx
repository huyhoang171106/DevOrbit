import { useState } from 'react'
import type { RepoCandidate } from '../../types/api'

type ApproveModalProps = {
  candidate: RepoCandidate
  onConfirm: (description: string, techStacks: string[], reviewNote: string) => void
  onClose: () => void
}

export function ApproveModal({ candidate, onConfirm, onClose }: ApproveModalProps) {
  const [description, setDescription] = useState(candidate.description ?? '')
  const [techStacks, setTechStacks] = useState([candidate.primaryLanguage, candidate.topics].filter(Boolean).join(', '))
  const [reviewNote, setReviewNote] = useState('')

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    const stacks = techStacks
      .split(',')
      .map((s) => s.trim())
      .filter(Boolean)
    onConfirm(description, stacks, reviewNote)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm">
      <div className="glass-card w-full max-w-lg p-6 shadow-2xl">
        <div className="flex items-center justify-between mb-4">
          <h2 className="heading-5 text-clay-text">
            Approve Repository
          </h2>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg p-1 text-clay-text-muted hover:bg-glass-surface-raised hover:text-clay-text transition-colors"
          >
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <p className="body-sm text-clay-text-muted mb-5">
          {candidate.githubOwner}/{candidate.githubName}
        </p>

        <div className="mb-5 rounded-xl border border-clay-border bg-clay-surface p-4 text-xs text-clay-text-muted">
          <div className="flex items-center gap-3">
            <span className="inline-flex items-center gap-1 text-clay-text-muted">
              <svg className="h-3.5 w-3.5 text-clay-text-muted" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
              </svg>
              {candidate.stars}
            </span>
            <span>{candidate.forks} forks</span>
            {candidate.lastPushedAt && <span>· pushed {new Date(candidate.lastPushedAt).toLocaleDateString()}</span>}
          </div>
          {candidate.readmeExcerpt && (
            <p className="mt-3 line-clamp-4 text-clay-text-muted leading-relaxed">{candidate.readmeExcerpt}</p>
          )}
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label">Description</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
              className="input-field"
              placeholder="A brief description of this repository..."
            />
          </div>

          <div>
            <label className="label">Tech Stacks</label>
            <input
              type="text"
              value={techStacks}
              onChange={(e) => setTechStacks(e.target.value)}
              className="input-field"
              placeholder="e.g. React, Spring Boot, PostgreSQL"
            />
            <p className="mt-1.5 text-xs text-clay-text-muted">Separate with commas</p>
          </div>

          <div>
            <label className="label">Review Note</label>
            <input
              type="text"
              value={reviewNote}
              onChange={(e) => setReviewNote(e.target.value)}
              className="input-field"
              placeholder="Optional note about this candidate"
            />
          </div>

          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn-primary"
            >
              <svg className="h-4 w-4 mr-2 inline" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M20 6L9 17l-5-5" />
              </svg>
              Confirm Approval
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
