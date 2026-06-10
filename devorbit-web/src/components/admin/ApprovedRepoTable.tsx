import type { RepoSummary } from '../../types/api'

type ApprovedRepoTableProps = {
  repos: RepoSummary[]
  onEdit: (repo: RepoSummary) => void
  onDeactivate: (id: number) => void
}

export function ApprovedRepoTable({ repos, onEdit, onDeactivate }: ApprovedRepoTableProps) {
  return (
    <div className="glass-card overflow-hidden p-0 border-clay-border">
      <table className="min-w-full text-sm">
        <thead>
          <tr className="border-b border-clay-border bg-clay-surface">
            <th className="text-left font-medium text-clay-text-muted py-3 px-4">Tên</th>
            <th className="text-left font-medium text-clay-text-muted py-3 px-4">Môn học</th>
            <th className="text-left font-medium text-clay-text-muted py-3 px-4">Ngôn ngữ</th>
            <th className="text-left font-medium text-clay-text-muted py-3 px-4">Sao</th>
            <th className="text-left font-medium text-clay-text-muted py-3 px-4">Công nghệ</th>
            <th className="text-right font-medium text-clay-text-muted py-3 px-4">Thao tác</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-clay-border bg-clay-bg">
          {repos.length === 0 && (
            <tr>
              <td colSpan={6} className="px-4 py-10 text-center text-clay-text-muted body-sm">
                <svg className="mx-auto mb-2 h-8 w-8 text-clay-text-muted/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                  <path d="M22 12A10 10 0 1 1 12 2a10 10 0 0 1 10 10z" />
                  <path d="M8 14s1.5 2 4 2 4-2 4-2" />
                  <path d="M9 9h.01M15 9h.01" />
                </svg>
                Chưa có kho nào được duyệt
              </td>
            </tr>
          )}
          {repos.map((r) => (
            <tr key={r.id} className="transition-colors hover:bg-glass-surface-raised">
              <td className="text-clay-text py-3 px-4 font-medium">{r.displayName}</td>
              <td className="py-3 px-4">
                <span className="text-clay-text-muted body-sm">{r.courseCode ?? r.courseName ?? '-'}</span>
              </td>
              <td className="py-3 px-4">
                {r.primaryLanguage && (
                  <span className="inline-flex items-center justify-center bg-glass-surface-raised text-clay-text-muted rounded-sm px-1.5 py-0.5">{r.primaryLanguage}</span>
                )}
              </td>
              <td className="py-3 px-4">
                <span className="flex flex-shrink-0 items-center gap-1 bg-glass-surface-raised px-2 py-1 body-sm text-clay-text border border-clay-border rounded-md w-fit">
                  <svg className="h-3.5 w-3.5 text-amber-500" viewBox="0 0 24 24" fill="currentColor">
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
                  <button onClick={() => onDeactivate(r.id)} className="btn-secondary !py-1 !px-2 !text-xs !text-red-400 !border-red-500/30 hover:!bg-red-500/10">
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
