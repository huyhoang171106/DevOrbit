import type { RepoSummary } from '../../types/api'

type ApprovedRepoTableProps = {
  repos: RepoSummary[]
  onDeactivate: (id: number) => void
}

export function ApprovedRepoTable({ repos, onDeactivate }: ApprovedRepoTableProps) {
  return (
    <div className="overflow-x-auto rounded-lg border bg-white shadow-sm">
      <table className="min-w-full text-sm">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-4 py-3 text-left font-medium text-gray-600">Name</th>
            <th className="px-4 py-3 text-left font-medium text-gray-600">Language</th>
            <th className="px-4 py-3 text-left font-medium text-gray-600">Stars</th>
            <th className="px-4 py-3 text-left font-medium text-gray-600">Tech Stacks</th>
            <th className="px-4 py-3 text-left font-medium text-gray-600">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y">
          {repos.length === 0 && (
            <tr>
              <td colSpan={5} className="px-4 py-8 text-center text-gray-500">
                No approved repos.
              </td>
            </tr>
          )}
          {repos.map((r) => (
            <tr key={r.id} className="even:bg-gray-50">
              <td className="px-4 py-3 font-medium">{r.displayName}</td>
              <td className="px-4 py-3">
                <span className="rounded bg-gray-100 px-2 py-0.5 text-xs font-medium">
                  {r.primaryLanguage}
                </span>
              </td>
              <td className="px-4 py-3">&#9733; {r.stars}</td>
              <td className="px-4 py-3">
                <div className="flex flex-wrap gap-1">
                  {r.techStacks.map((s) => (
                    <span key={s} className="rounded bg-blue-50 px-2 py-0.5 text-xs font-medium text-blue-700">
                      {s}
                    </span>
                  ))}
                </div>
              </td>
              <td className="px-4 py-3">
                <button
                  onClick={() => onDeactivate(r.id)}
                  className="rounded bg-red-600 px-3 py-1 text-xs font-medium text-white hover:bg-red-700"
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
