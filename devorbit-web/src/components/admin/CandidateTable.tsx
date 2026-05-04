import type { RepoCandidate } from '../../types/api'

type CandidateTableProps = {
  candidates: RepoCandidate[]
  onApprove: (id: number) => void
  onReject: (id: number) => void
}

export function CandidateTable({ candidates, onApprove, onReject }: CandidateTableProps) {
  return (
    <div className="card-base overflow-x-auto border border-hairline p-0">
      <table className="min-w-full text-sm">
        <thead>
          <tr className="border-b border-hairline bg-surface-soft">
            <th className="table-header text-left font-medium text-steel py-3 px-4 w-[280px]">Repository</th>
            <th className="table-header text-left font-medium text-steel py-3 px-4 w-[100px]">Subject</th>
            <th className="table-header text-left font-medium text-steel py-3 px-4 w-[350px]">URL</th>
            <th className="table-header text-left font-medium text-steel py-3 px-4 w-[120px]">Status</th>
            <th className="table-header text-right font-medium text-steel py-3 px-4 w-[180px]">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-hairline bg-canvas">
          {candidates.length === 0 && (
            <tr>
              <td colSpan={5} className="px-4 py-10 text-center body-sm text-steel">
                <svg className="mx-auto mb-2 h-8 w-8 text-steel" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                  <path d="M9 12l2 2 4-4" />
                  <circle cx="12" cy="12" r="10" />
                </svg>
                No candidates found.
              </td>
            </tr>
          )}
          {candidates.map((c) => (
            <tr key={c.id} className="transition-colors hover:bg-surface-soft">
              <td className="table-cell py-3 px-4 max-w-[280px]">
                <div className="font-medium text-ink body-sm break-words">{c.githubName}</div>
                <div className="mt-2 flex flex-wrap items-center gap-2 text-xs text-steel">
                  {c.primaryLanguage && <span className="badge-tag bg-surface text-steel">{c.primaryLanguage}</span>}
                  <span className="inline-flex items-center gap-1 text-steel">
                    <svg className="h-3 w-3 text-steel" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                    </svg>
                    {c.stars}
                  </span>
                  <span className="text-steel">{c.forks} forks</span>
                </div>
              </td>
              <td className="table-cell py-3 px-4">
                {c.courseCode ? (
                  <span className="text-xs font-semibold text-brand-green bg-brand-green/5 px-2 py-1 rounded border border-brand-green/10">
                    {c.courseCode}
                  </span>
                ) : (
                  <span className="text-xs text-muted italic">N/A</span>
                )}
              </td>
              <td className="table-cell py-3 px-4 max-w-[350px]">
                <a
                  href={c.githubUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-brand-green transition-colors hover:text-brand-green/80 inline-flex items-center gap-1 body-sm font-medium break-all"
                >
                  <span>{c.githubUrl.replace('https://github.com/', '')}</span>
                  <svg className="h-3 w-3 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6M15 3h6v6M10 14L21 3" />
                  </svg>
                </a>
              </td>
              <td className="table-cell py-3 px-4">
                <div className="flex flex-col gap-1">
                  {c.status === 'APPROVED' && <span className="badge-tag w-fit bg-brand-green/10 text-brand-green border-brand-green/20">{c.status}</span>}
                  {c.status === 'REJECTED' && <span className="badge-tag w-fit bg-danger-3 text-danger-11 border-danger-6">{c.status}</span>}
                  {c.status === 'NEW' && <span className="badge-tag w-fit bg-surface text-steel">{c.status}</span>}
                  {c.lastPushedAt && <div className="text-[11px] text-steel">Pushed {new Date(c.lastPushedAt).toLocaleDateString()}</div>}
                </div>
              </td>
              <td className="table-cell text-right py-3 px-4">
                {c.status === 'NEW' && (
                  <div className="flex items-center justify-end gap-2">
                    <button
                      onClick={() => onApprove(c.id)}
                      className="btn-primary !py-1.5 !px-3 !text-xs"
                    >
                      <svg className="h-3.5 w-3.5 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M20 6L9 17l-5-5" />
                      </svg>
                      Approve
                    </button>
                    <button
                      onClick={() => onReject(c.id)}
                      className="btn-secondary !py-1.5 !px-3 !text-xs !bg-surface !text-danger-11 hover:!bg-danger-3 border border-danger-6"
                    >
                      <svg className="h-3.5 w-3.5 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M18 6L6 18M6 6l12 12" />
                      </svg>
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
