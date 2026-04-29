import type { RepoSummary } from '../../types/api'

export function RepoCard({ repo }: { repo: RepoSummary }) {
  return (
    <div className="rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex items-start justify-between">
        <div>
          <a
            href={repo.githubUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="text-lg font-semibold text-blue-600 hover:underline"
          >
            {repo.displayName}
          </a>
          <p className="mt-1 text-sm text-gray-600">{repo.description}</p>
        </div>
        <span className="text-sm text-gray-500">&#9733; {repo.stars}</span>
      </div>
      <div className="mt-3 flex flex-wrap items-center gap-2">
        {repo.primaryLanguage && (
          <span className="rounded bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-700">
            {repo.primaryLanguage}
          </span>
        )}
        {repo.techStacks.map((stack) => (
          <span
            key={stack}
            className="rounded bg-blue-50 px-2 py-0.5 text-xs font-medium text-blue-700"
          >
            {stack}
          </span>
        ))}
      </div>
    </div>
  )
}
