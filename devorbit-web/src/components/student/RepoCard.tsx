import type { RepoSummary } from '../../types/api'

export function RepoCard({ repo }: { repo: RepoSummary }) {
  return (
    <div className="glass-card group cursor-default hover:shadow-[0_4px_12px_rgba(0,0,0,0.08)] transition-shadow">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0 flex-1">
          <a
            href={repo.githubUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-2 heading-5 text-ink hover:text-emerald-400 transition-colors cursor-pointer"
          >
            <span className="truncate">{repo.displayName}</span>
            <svg className="h-4 w-4 flex-shrink-0 text-ink-secondary" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6M15 3h6v6M10 14L21 3" />
            </svg>
          </a>
          <p className="mt-2 body-sm text-ink-secondary line-clamp-2 leading-relaxed">{repo.description}</p>
        </div>
        <span className="flex flex-shrink-0 items-center gap-1 bg-glass-surface px-2 py-1 body-sm text-ink border border-glass-border rounded-md">
          <svg className="h-3.5 w-3.5 text-amber-500" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
          </svg>
          {repo.stars}
        </span>
      </div>
      <div className="mt-4 flex flex-wrap items-center gap-2">
        {repo.primaryLanguage && (
          <span className="inline-flex items-center justify-center bg-glass-surface text-ink-secondary code-sm rounded-sm px-1.5 py-0.5">{repo.primaryLanguage}</span>
        )}
        {repo.techStacks.map((stack) => (
          <span key={stack} className="badge-tag">{stack}</span>
        ))}
      </div>
    </div>
  )
}
