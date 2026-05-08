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
      className="glass-card-glow group relative flex flex-col h-full cursor-pointer border-opacity-30 hover:border-emerald-500/40 hover:bg-glass-surface-hover hover:shadow-[0_15px_40px_-12px_rgba(16,185,129,0.12)] transition-all duration-500 p-6"
    >
      <div className="relative z-10 flex items-start justify-between gap-4 mb-4">
        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-2.5 mb-2">
            <div className="h-7 w-7 rounded-lg bg-emerald-500/5 flex items-center justify-center border border-emerald-500/10 group-hover:border-emerald-500/30 group-hover:bg-emerald-500/10 transition-all duration-500 shrink-0">
              <svg className="h-3.5 w-3.5 text-emerald-500/70 group-hover:text-emerald-500 transition-colors" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" />
              </svg>
            </div>
            <h3 className="text-base font-bold text-ink group-hover:text-emerald-500 transition-colors duration-500 truncate leading-none">
              {repo.displayName}
            </h3>
          </div>
          
          <p className="text-xs text-ink-secondary line-clamp-2 leading-relaxed opacity-80 group-hover:opacity-100 transition-opacity duration-500 min-h-[2.5rem]">
            {repo.description || "No description provided for this academic resource node."}
          </p>
        </div>

        {repo.stars !== null && repo.stars > 0 && (
          <div className="shrink-0">
            <span className="flex items-center gap-1 bg-amber-500/5 px-2 py-1 rounded-lg border border-amber-500/10 text-amber-500 group-hover:bg-amber-500/10 transition-all">
              <svg className="h-3 w-3 fill-current" viewBox="0 0 24 24">
                <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
              </svg>
              <span className="text-[10px] font-black tracking-tighter">{repo.stars}</span>
            </span>
          </div>
        )}
      </div>

      <div className="relative z-10 mt-auto flex flex-wrap items-center gap-2">
        {repo.primaryLanguage && (
          <span className="inline-flex items-center gap-1.5 rounded-lg bg-glass-surface/50 px-2.5 py-1 border border-glass-border">
            <span className="h-1.5 w-1.5 rounded-full shadow-[0_0_5px_currentColor]" style={{ color: langColor, backgroundColor: 'currentColor' }} />
            <span className="text-[10px] font-black uppercase tracking-wider text-ink-secondary">{repo.primaryLanguage}</span>
          </span>
        )}
        {repo.techStacks?.slice(0, 3).map((stack) => (
          <span key={stack} className="inline-flex items-center rounded-lg bg-emerald-500/5 px-2.5 py-1 border border-emerald-500/10 text-[9px] uppercase font-black tracking-[0.1em] text-emerald-500/70 group-hover:text-emerald-500 transition-all">
            {stack}
          </span>
        ))}
        {repo.techStacks?.length > 3 && (
          <span className="text-[9px] font-black text-ink-muted/60 uppercase tracking-tighter">+{repo.techStacks.length - 3}</span>
        )}
      </div>

      {/* Subtle indicator of activity */}
      <div className="absolute top-0 right-0 p-1 opacity-0 group-hover:opacity-100 transition-all duration-500 translate-x-1 -translate-y-1">
        <div className="h-2 w-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.8)]" />
      </div>
    </Link>
  )
}


