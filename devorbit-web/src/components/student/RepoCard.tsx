import { Link } from 'react-router-dom'
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
    <Link
      to={`/repos/${repo.id}`}
      className="clay-card-hover group flex flex-col h-full bg-clay-surface !p-7 border-clay-border"
    >
      <div className="flex items-start justify-between gap-4 mb-5">
        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-3 mb-3">
            <div className="h-10 w-10 rounded-xl bg-clay-primary flex items-center justify-center border-[3px] border-clay-border shadow-[3px_3px_0px_0px_var(--color-clay-shadow-outer)] group-hover:bg-clay-secondary transition-colors shrink-0">
              <svg className="h-5 w-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" />
              </svg>
            </div>
            <h3 className="text-lg font-black text-clay-text uppercase tracking-normal truncate leading-snug">
              {repo.displayName}
            </h3>
          </div>

          <p className="text-[16px] font-bold text-clay-text leading-snug">
            {repo.description || "Danh mục mã nguồn chuyên sâu cho môn học."}
          </p>
        </div>

        {repo.stars !== null && repo.stars > 0 && (
          <div className="shrink-0">
            <span className="flex items-center gap-1.5 bg-clay-surface px-3 py-1.5 rounded-xl border-[3px] border-clay-border shadow-[4px_4px_0px_0px_var(--color-clay-shadow-outer)] text-clay-text">
              <svg className="h-3.5 w-3.5 fill-clay-text" viewBox="0 0 24 24">
                <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
              </svg>
              <span className="text-[12px] font-black tracking-tighter">{repo.stars}</span>
            </span>
          </div>
        )}
      </div>

      <div className="mt-auto flex flex-wrap items-center gap-3">
        {repo.primaryLanguage && (
          <span className="inline-flex items-center gap-2 rounded-lg bg-clay-surface px-3 py-1.5 border-[2px] border-clay-border">
            <span className="h-2 w-2 rounded-full border-[1px] border-clay-border" style={{ backgroundColor: langColor }} />
            <span className="text-[10px] font-black uppercase tracking-widest text-clay-text">{repo.primaryLanguage}</span>
          </span>
        )}
        {repo.techStacks?.slice(0, 3).map((stack) => (
          <span key={stack} className="inline-flex items-center rounded-lg bg-clay-accent text-white px-3 py-1.5 border-[2px] border-clay-border text-[10px] uppercase font-black tracking-widest shadow-[2px_2px_0px_0px_var(--color-clay-shadow-outer)]">
            {stack}
          </span>
        ))}
        {repo.techStacks?.length > 3 && (
          <span className="text-[10px] font-black text-ink-muted uppercase tracking-tighter">+{repo.techStacks.length - 3} more</span>
        )}
      </div>

      {/* Chunky indicator of activity */}
      <div className="absolute top-0 right-0 p-2 opacity-0 group-hover:opacity-100 transition-all duration-300 translate-x-2 -translate-y-2">
        <div className="h-4 w-4 rounded-full bg-clay-primary border-[2px] border-clay-border shadow-[2px_2px_0px_0px_var(--color-clay-shadow-outer)]" />
      </div>
    </Link>
  )
}
