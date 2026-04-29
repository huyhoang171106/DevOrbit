import type { RepoSummary } from '../../types/api'

export function RepoCard({ repo }: { repo: RepoSummary }) {
  return (
    <div className="glass-card group p-5">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0 flex-1">
          <a
            href={repo.githubUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center gap-2 text-lg font-semibold text-slate-100 transition-colors hover:text-amber-400"
          >
            {repo.displayName}
            <svg className="h-4 w-4 flex-shrink-0 text-slate-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6M15 3h6v6M10 14L21 3" />
            </svg>
          </a>
          <p className="mt-1 text-sm text-slate-400 line-clamp-2">{repo.description}</p>
        </div>
        <span className="flex flex-shrink-0 items-center gap-1 text-sm text-slate-400">
          <svg className="h-4 w-4 text-amber-400" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
          </svg>
          {repo.stars}
        </span>
      </div>
      <div className="mt-4 flex flex-wrap items-center gap-2">
        {repo.primaryLanguage && (
          <span className="badge-language">{repo.primaryLanguage}</span>
        )}
        {repo.techStacks.map((stack) => (
          <span key={stack} className="badge-stack">{stack}</span>
        ))}
      </div>
    </div>
  )
}
