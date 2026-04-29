import type { RepoCandidate } from '../../types/api'

type CandidateTableProps = {
  candidates: RepoCandidate[]
  onApprove: (id: number) => void
  onReject: (id: number) => void
}

export function CandidateTable({ candidates, onApprove, onReject }: CandidateTableProps) {
  return (
    <div className="overflow-x-auto rounded-lg border bg-white shadow-sm">
      <table className="min-w-full text-sm">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-4 py-3 text-left font-medium text-gray-600">Owner</th>
            <th className="px-4 py-3 text-left font-medium text-gray-600">Repository</th>
            <th className="px-4 py-3 text-left font-medium text-gray-600">URL</th>
            <th className="px-4 py-3 text-left font-medium text-gray-600">Status</th>
            <th className="px-4 py-3 text-left font-medium text-gray-600">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y">
          {candidates.length === 0 && (
            <tr>
              <td colSpan={5} className="px-4 py-8 text-center text-gray-500">
                No candidates found.
              </td>
            </tr>
          )}
          {candidates.map((c) => (
            <tr key={c.id} className="even:bg-gray-50">
              <td className="px-4 py-3">{c.githubOwner}</td>
              <td className="px-4 py-3 font-medium">{c.githubName}</td>
              <td className="px-4 py-3">
                <a
                  href={c.githubUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-600 hover:underline"
                >
                  {c.githubUrl}
                </a>
              </td>
              <td className="px-4 py-3">
                <span
                  className={`inline-block rounded-full px-2 py-0.5 text-xs font-medium ${
                    c.status === 'APPROVED'
                      ? 'bg-green-100 text-green-700'
                      : c.status === 'REJECTED'
                        ? 'bg-red-100 text-red-700'
                        : 'bg-yellow-100 text-yellow-700'
                  }`}
                >
                  {c.status}
                </span>
              </td>
              <td className="px-4 py-3">
                {c.status === 'PENDING' && (
                  <div className="flex gap-2">
                    <button
                      onClick={() => onApprove(c.id)}
                      className="rounded bg-green-600 px-3 py-1 text-xs font-medium text-white hover:bg-green-700"
                    >
                      Approve
                    </button>
                    <button
                      onClick={() => onReject(c.id)}
                      className="rounded bg-red-600 px-3 py-1 text-xs font-medium text-white hover:bg-red-700"
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
