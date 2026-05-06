import type { NoteResponse } from '../../types/api'

type Props = {
  open: boolean
  note: NoteResponse | null
  onClose: () => void
}

const targetLabels: Record<string, string> = {
  COURSE: 'Course',
  REPO: 'Repository',
  NONE: 'None',
}

export function NoteDetailDialog({ open, note, onClose }: Props) {
  if (!open || !note) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-sm">
      <div className="glass-card w-full max-w-2xl p-6 max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between mb-4">
          <h2 className="heading-5 text-ink">{note.title}</h2>
          <button type="button" onClick={onClose} className="rounded-lg p-1 text-ink-secondary hover:bg-glass-surface-raised hover:text-ink transition-colors">
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>

        <div className="flex flex-wrap gap-3 mb-6 text-xs text-ink-secondary">
          <span className="bg-glass-surface border border-glass-border px-2 py-1 rounded-md">{note.studentCode} - {note.studentName}</span>
          <span className="bg-glass-surface border border-glass-border px-2 py-1 rounded-md">Target: {targetLabels[note.targetType] ?? note.targetType}{note.targetId ? ` #${note.targetId}` : ''}</span>
          <span className="bg-glass-surface border border-glass-border px-2 py-1 rounded-md">{new Date(note.createdAt).toLocaleDateString()}</span>
        </div>

        <div className="mb-6">
          <h3 className="label mb-2">Content</h3>
          <div className="rounded-xl bg-glass-surface border border-glass-border p-4 body-sm text-ink-secondary whitespace-pre-wrap font-mono leading-relaxed">
            {note.contentMarkdown || <span className="text-ink-secondary/50 italic">No content</span>}
          </div>
        </div>

        {note.snippets.length > 0 && (
          <div>
            <h3 className="label mb-3">Code Snippets ({note.snippets.length})</h3>
            <div className="space-y-4">
              {note.snippets.map((s) => (
                <div key={s.id} className="rounded-xl bg-glass-surface border border-glass-border overflow-hidden">
                  {s.caption && (
                     <div className="px-4 py-2 text-xs text-ink-secondary border-b border-glass-border bg-glass-surface-raised font-medium">
                      {s.caption}
                    </div>
                  )}
                  <div className="px-4 py-3">
                    <div className="text-[10px] uppercase tracking-wider text-ink-secondary/70 mb-1.5 font-medium">{s.language}</div>
                    <pre className="text-xs text-ink font-mono overflow-x-auto whitespace-pre-wrap">{s.code}</pre>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        <div className="flex justify-end pt-6 mt-2">
          <button type="button" onClick={onClose} className="btn-secondary px-6">Close</button>
        </div>
      </div>
    </div>
  )
}
