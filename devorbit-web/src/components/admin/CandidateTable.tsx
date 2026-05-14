import type { RepoCandidate } from '../../types/api'

type CandidateTableProps = {
  candidates: RepoCandidate[]
  onApprove: (id: number) => void
  onReject: (id: number) => void
}

export function CandidateTable({ candidates, onApprove, onReject }: CandidateTableProps) {
  return (
    <div className="glass-card overflow-x-auto border border-clay-border p-0">
      <table className="min-w-full text-sm">
        <thead>
          <tr className="border-b border-clay-border bg-glass-surface-raised">
            <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4 w-[280px]">Kho mã nguồn</th>
            <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4 w-[100px]">Môn học</th>
            <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4 w-[350px]">URL</th>
            <th className="table-header text-left font-medium text-clay-text-muted py-3 px-4 w-[120px]">Trạng thái</th>
            <th className="table-header text-right font-medium text-clay-text-muted py-3 px-4 w-[180px]">Thao tác</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-clay-border bg-clay-bg">
          {candidates.length === 0 && (
            <tr>
              <td colSpan={5} className="px-4 py-10 text-center body-sm text-clay-text-muted">
                <svg className="mx-auto mb-2 h-8 w-8 text-clay-text-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                  <path d="M9 12l2 2 4-4" />
                  <circle cx="12" cy="12" r="10" />
                </svg>
                No candidates found.
              </td>
            </tr>
          )}
          {candidates.map((c) => (
            <tr key={c.id} className="transition-colors hover:bg-glass-surface-raised">
              <td className="table-cell py-3 px-4 max-w-[280px]">
                <div className="font-medium text-clay-text body-sm break-words">{c.githubName}</div>
                <div className="mt-2 flex flex-wrap items-center gap-2 text-xs text-clay-text-muted">
                  {c.primaryLanguage && <span className="badge-tag bg-clay-surface text-clay-text-muted">{c.primaryLanguage}</span>}
                  <span className="inline-flex items-center gap-1 text-clay-text-muted">
                    <svg className="h-3 w-3 text-clay-text-muted" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                    </svg>
                    {c.stars}
                  </span>
                  <span className="text-clay-text-muted">{c.forks} forks</span>
                </div>
                {c.reviewNote && (
                  <div className="mt-3 p-3 bg-gradient-to-br from-clay-primary/10 via-clay-primary/5 to-transparent border border-clay-primary/20 rounded-xl text-[12px] leading-relaxed text-clay-text shadow-sm backdrop-blur-sm relative overflow-hidden group">
                    <div className="absolute top-0 right-0 p-1 opacity-10 group-hover:opacity-20 transition-opacity">
                      <svg className="h-8 w-8 text-clay-primary" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2L14.5 9L22 12L14.5 15L12 22L9.5 15L2 12L9.5 9L12 2Z" />
                      </svg>
                    </div>
                    <div className="flex items-center gap-2 mb-1.5 font-bold text-clay-primary uppercase tracking-widest text-[9px]">
                      <span className="flex h-4 w-4 items-center justify-center rounded-full bg-clay-primary text-white scale-90">
                        <svg className="h-2.5 w-2.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                          <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" />
                        </svg>
                      </span>
                      AI Analysis Agent
                    </div>
                    <div className="relative z-10 font-medium italic">
                      {c.reviewNote}
                    </div>
                  </div>
                )}
              </td>
              <td className="table-cell py-3 px-4">
                {c.courseCode ? (
                  <span className="text-xs font-semibold text-clay-primary bg-clay-primary/5 px-2 py-1 rounded border border-clay-primary/10">
                    {c.courseCode}
                  </span>
                ) : (
                  <span className="text-xs text-clay-text-muted italic">N/A</span>
                )}
              </td>
              <td className="table-cell py-3 px-4 max-w-[350px]">
                <a
                  href={c.githubUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-clay-primary transition-colors hover:text-clay-primary/80 inline-flex items-center gap-1 body-sm font-medium break-all"
                >
                  <span>{c.githubUrl.replace('https://github.com/', '')}</span>
                  <svg className="h-3 w-3 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6M15 3h6v6M10 14L21 3" />
                  </svg>
                </a>
              </td>
              <td className="table-cell py-3 px-4">
                <div className="flex flex-col gap-1">
                  {c.status === 'APPROVED' && <span className="badge-tag w-fit bg-clay-primary/10 text-clay-primary border-clay-primary/20">{c.status}</span>}
                  {c.status === 'REJECTED' && <span className="badge-tag w-fit bg-red-500/10 text-red-400 border-red-500/30">{c.status}</span>}
                  {c.status === 'NEW' && <span className="badge-tag w-fit bg-clay-surface text-clay-text-muted">{c.status}</span>}
                  {c.lastPushedAt && <div className="text-[11px] text-clay-text-muted">Pushed {new Date(c.lastPushedAt).toLocaleDateString()}</div>}
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
                      className="btn-secondary !py-1.5 !px-3 !text-xs !bg-clay-surface !text-red-400 hover:!bg-red-500/10 border border-red-500/30"
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
