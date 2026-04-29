import type { RepoSummary } from '../../types/api'

type ApprovedRepoTableProps = {
  repos: RepoSummary[]
  onDeactivate: (id: number) => void
}

export function ApprovedRepoTable({ repos, onDeactivate }: ApprovedRepoTableProps) {
  return (
    <div className="glass-card overflow-hidden">
      <table className="min-w-full text-sm">
        <thead>
          <tr className="border-b border-white/5">
            <th className="table-header">Name</th>
            <th className="table-header">Language</th>
            <th className="table-header">Stars</th>
            <th className="table-header">Tech Stacks</th>
            <th className="table-header">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-white/5">
          {repos.length === 0 && (
            <tr>
              <td colSpan={5} className="px-4 py-8 text-center text-slate-500">
                No approved repos.
              </td>
            </tr>
          )}
          {repos.map((r, i) => (
            <tr key={r.id} className={i % 2 === 1 ? 'bg-white/[0.02]' : ''}>
              <td className="table-cell font-medium text-slate-100">{r.displayName}</td>
              <td className="table-cell">
                {r.primaryLanguage && (
                  <span className="badge-language">{r.primaryLanguage}</span>
                )}
              </td>
              <td className="table-cell">
                <span className="flex items-center gap-1 text-slate-300">
                  <svg className="h-3.5 w-3.5 text-amber-400" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                  </svg>
                  {r.stars}
                </span>
              </td>
              <td className="table-cell">
                <div className="flex flex-wrap gap-1">
                  {r.techStacks.map((s) => (
                    <span key={s} className="badge-stack">{s}</span>
                  ))}
                </div>
              </td>
              <td className="table-cell">
                <button
                  onClick={() => onDeactivate(r.id)}
                  className="btn-danger px-3 py-1 text-xs"
                >
                  Deactivate
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
