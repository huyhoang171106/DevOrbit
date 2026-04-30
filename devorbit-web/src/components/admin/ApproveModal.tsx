import { useState } from 'react'
import type { RepoCandidate } from '../../types/api'

type ApproveModalProps = {
  candidate: RepoCandidate
  onConfirm: (description: string, techStacks: string[], reviewNote: string) => void
  onClose: () => void
}

export function ApproveModal({ candidate, onConfirm, onClose }: ApproveModalProps) {
  const [description, setDescription] = useState('')
  const [techStacks, setTechStacks] = useState('')
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
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
      <div className="w-full max-w-lg rounded-2xl border border-white/10 bg-slate-900 p-6 shadow-2xl">
        <h2 className="text-lg font-semibold text-slate-100 mb-1">
          Approve Repository
        </h2>
        <p className="text-sm text-slate-400 mb-5">
          {candidate.githubOwner}/{candidate.githubName}
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">
              Description
            </label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
              className="w-full rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-100 placeholder-slate-500 focus:border-cyan-500/50 focus:outline-none focus:ring-2 focus:ring-cyan-500/20"
              placeholder="A brief description of this repository..."
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">
              Tech Stacks
            </label>
            <input
              type="text"
              value={techStacks}
              onChange={(e) => setTechStacks(e.target.value)}
              className="w-full rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-100 placeholder-slate-500 focus:border-cyan-500/50 focus:outline-none focus:ring-2 focus:ring-cyan-500/20"
              placeholder="e.g. React, Spring Boot, PostgreSQL"
            />
            <p className="mt-1 text-xs text-slate-500">Separate with commas</p>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">
              Review Note
            </label>
            <input
              type="text"
              value={reviewNote}
              onChange={(e) => setReviewNote(e.target.value)}
              className="w-full rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-100 placeholder-slate-500 focus:border-cyan-500/50 focus:outline-none focus:ring-2 focus:ring-cyan-500/20"
              placeholder="Optional note about this candidate"
            />
          </div>

          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="rounded-xl px-4 py-2 text-sm font-medium text-slate-400 transition-colors hover:bg-white/5 hover:text-slate-200"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="rounded-xl bg-green-500/10 px-5 py-2 text-sm font-medium text-green-400 transition-all duration-200 hover:bg-green-500/20 hover:text-green-300"
            >
              Confirm Approval
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}