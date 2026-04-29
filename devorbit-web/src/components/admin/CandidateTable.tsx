import type { RepoCandidate } from '../../types/api'

type CandidateTableProps = {
  candidates: RepoCandidate[]
  onApprove: (id: number) => void
  onReject: (id: number) => void
}

export function CandidateTable({ candidates, onApprove, onReject }: CandidateTableProps) {
  return (
    <div className="glass-card overflow-hidden">
      <table className="min-w-full text-sm">
        <thead>
          <tr className="border-b border-white/5">
            <th className="table-header">Owner</th>
            <th className="table-header">Repository</th>
            <th className="table-header">URL</th>
            <th className="table-header">Status</th>
            <th className="table-header">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-white/5">
          {candidates.length === 0 && (
            <tr>
              <td colSpan={5} className="px-4 py-8 text-center text-slate-500">
                No candidates found.
              </td>
            </tr>
          )}
          {candidates.map((c, i) => (
            <tr key={c.id} className={i % 2 === 1 ? 'bg-white/[0.02]' : ''}>
              <td className="table-cell text-slate-300">{c.githubOwner}</td>
              <td className="table-cell font-medium text-slate-100">{c.githubName}</td>
              <td className="table-cell">
                <a
                  href={c.githubUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-cyan-400 transition-colors hover:text-cyan-300"
                >
                  {c.githubUrl}
                </a>
              </td>
              <td className="table-cell">
                {c.status === 'APPROVED' && <span className="badge-approved">{c.status}</span>}
                {c.status === 'REJECTED' && <span className="badge-rejected">{c.status}</span>}
                {c.status === 'PENDING' && <span className="badge-new">{c.status}</span>}
              </td>
              <td className="table-cell">
                {c.status === 'PENDING' && (
                  <div className="flex gap-2">
                    <button
                      onClick={() => onApprove(c.id)}
                      className="rounded-xl bg-green-500/10 px-3 py-1 text-xs font-medium text-green-400 transition-all duration-200 hover:bg-green-500/20 hover:text-green-300"
                    >
                      Approve
                    </button>
                    <button
                      onClick={() => onReject(c.id)}
                      className="btn-danger px-3 py-1 text-xs"
                    >
                      Reject
                    </button>
                  </div>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
