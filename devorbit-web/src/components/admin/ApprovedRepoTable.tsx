import type { RepoSummary } from '../../types/api'

type ApprovedRepoTableProps = {
  repos: RepoSummary[]
  onEdit: (repo: RepoSummary) => void
  onDeactivate: (id: number) => void
}

export function ApprovedRepoTable({ repos, onEdit, onDeactivate }: ApprovedRepoTableProps) {
  return (
    <div className="card-base overflow-hidden p-0 border-hairline">
      <table className="min-w-full text-sm">
        <thead>
          <tr className="border-b border-hairline bg-surface">
            <th className="text-left font-medium text-steel py-3 px-4 dark:text-muted">Name</th>
            <th className="text-left font-medium text-steel py-3 px-4 dark:text-muted">Language</th>
            <th className="text-left font-medium text-steel py-3 px-4 dark:text-muted">Stars</th>
            <th className="text-left font-medium text-steel py-3 px-4 dark:text-muted">Tech Stacks</th>
            <th className="text-right font-medium text-steel py-3 px-4 dark:text-muted">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-hairline bg-canvas">
          {repos.length === 0 && (
            <tr>
              <td colSpan={5} className="px-4 py-10 text-center text-steel body-sm">
                <svg className="mx-auto mb-2 h-8 w-8 text-steel/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                  <path d="M22 12A10 10 0 1 1 12 2a10 10 0 0 1 10 10z" />
                  <path d="M8 14s1.5 2 4 2 4-2 4-2" />
                  <path d="M9 9h.01M15 9h.01" />
                </svg>
                No approved repos.
              </td>
            </tr>
          )}
          {repos.map((r) => (
            <tr key={r.id} className="transition-colors hover:bg-surface-soft dark:hover:bg-charcoal/50">
              <td className="text-ink py-3 px-4 font-medium">{r.displayName}</td>
              <td className="py-3 px-4">
                {r.primaryLanguage && (
                  <span className="inline-flex items-center justify-center bg-surface-soft text-steel rounded-sm px-1.5 py-0.5 dark:bg-charcoal dark:text-muted">{r.primaryLanguage}</span>
                )}
              </td>
              <td className="py-3 px-4">
                <span className="flex flex-shrink-0 items-center gap-1 bg-surface-soft px-2 py-1 body-sm text-charcoal border border-hairline rounded-md w-fit dark:bg-charcoal dark:text-on-dark-muted">
                  <svg className="h-3.5 w-3.5 text-testimonial-orange" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                  </svg>
                  {r.stars}
                </span>
              </td>
              <td className="py-3 px-4">
                <div className="flex flex-wrap gap-1">
                  {r.techStacks.map((s) => (
                    <span key={s} className="badge-tag">{s}</span>
                  ))}
                </div>
              </td>
              <td className="text-right py-3 px-4">
                <div className="flex items-center justify-end gap-2">
                  <button onClick={() => onEdit(r)} className="btn-secondary !py-1 !px-2 !text-xs">
                    Edit
                  </button>
                  <button onClick={() => onDeactivate(r.id)} className="btn-secondary !py-1 !px-2 !text-xs !text-danger-11 !border-danger-6 hover:!bg-danger-3 dark:hover:!bg-danger-11 dark:hover:!text-on-dark">
                    Deactivate
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
