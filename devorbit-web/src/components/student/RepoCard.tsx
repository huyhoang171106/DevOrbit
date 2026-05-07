import type { RepoSummary } from '../../types/api'

const LANGUAGE_COLORS: Record<string, string> = {
  TypeScript: '#3178C6',
  JavaScript: '#F7DF1E',
  Python: '#3572A5',
  Java: '#B07219',
  Go: '#00ADD8',
  Rust: '#DEA584',
  'C++': '#F34B7D',
  'C#': '#178600',
  Ruby: '#701516',
  PHP: '#4F5D95',
  Swift: '#F05138',
  Kotlin: '#A97BFF',
  Dart: '#00B4AB',
  TSQL: '#E38C00',
  HTML: '#E34F26',
  CSS: '#563D7C',
  Shell: '#89E051',
}

export function RepoCard({ repo }: { repo: RepoSummary }) {
  const langColor = LANGUAGE_COLORS[repo.primaryLanguage] ?? '#6b7280'

  return (
    <div className="glass-card group cursor-pointer border-opacity-50 hover:border-emerald-500/30 hover:bg-glass-surface-hover hover:shadow-[0_20px_50px_rgba(16,185,129,0.1)] transition-all duration-500">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0 flex-1">
          <a
            href={repo.githubUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-2 heading-5 text-ink hover:text-emerald-400 transition-colors duration-500 cursor-pointer"
          >
            <span className="truncate">{repo.displayName}</span>
            <svg className="h-4 w-4 flex-shrink-0 text-ink-secondary transition-colors duration-500 group-hover:text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6M15 3h6v6M10 14L21 3" />
            </svg>
          </a>
          <p className="mt-2 body-sm text-ink-secondary line-clamp-2 leading-relaxed opacity-80 group-hover:opacity-100 transition-opacity duration-500">
            {repo.description}
          </p>
        </div>

        {repo.stars !== null && (
          <span className="flex flex-shrink-0 items-center gap-1.5 bg-amber-500/10 px-3 py-1.5 body-sm text-amber-400 border border-amber-500/20 rounded-xl transition-all duration-500 group-hover:bg-amber-500/15 group-hover:border-amber-500/30 group-hover:shadow-sm group-hover:shadow-amber-500/10">
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
            </svg>
            {repo.stars}
          </span>
        )}
      </div>

      <div className="mt-5 flex flex-wrap items-center gap-2">
        {repo.primaryLanguage && (
          <span className="inline-flex items-center gap-1.5 rounded-md border border-glass-border bg-glass-surface px-2.5 py-1 text-[12px] font-medium text-ink-secondary">
            <span className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: langColor }} />
            {repo.primaryLanguage}
          </span>
        )}
        {repo.techStacks.map((stack) => (
          <span key={stack} className="badge-tag transition-all duration-500 group-hover:border-emerald-500/20 group-hover:text-emerald-500/80">
            {stack}
          </span>
        ))}
      </div>

      <div className="mt-5 pt-4 flex items-center justify-between border-t border-glass-border group-hover:border-emerald-500/20 transition-colors duration-500">
        <span className="text-[12px] font-bold uppercase tracking-widest text-ink-muted group-hover:text-emerald-500/80 transition-colors duration-500">
          View Repository
        </span>
        <div className="h-8 w-8 rounded-full bg-glass-surface flex items-center justify-center transition-all duration-500 group-hover:bg-emerald-500 group-hover:text-white">
          <svg className="h-4 w-4 transition-transform duration-500 group-hover:translate-x-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
            <path d="M5 12h14M12 5l7 7-7 7" />
          </svg>
        </div>
      </div>
    </div>
  )
}
