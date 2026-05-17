import { Link } from 'react-router-dom'
import type { RepoSummary } from '../../types/api'
import { Code, Star } from '@phosphor-icons/react'

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
      className="group relative flex flex-col h-full orbit-card overflow-hidden"
    >
      {/* Hover glow */}
      <div className="absolute -top-20 -right-20 w-40 h-40 bg-orbit-accent/5 blur-[80px] rounded-full opacity-0 group-hover:opacity-100 transition-opacity duration-700" />

      <div className="relative z-10 flex-1 flex flex-col">
        <div className="flex items-start justify-between gap-4 mb-5">
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-3 mb-4">
              <div className="h-10 w-10 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform duration-500">
                <Code className="h-5 w-5 text-orbit-accent" weight="duotone" />
              </div>
              <h3 className="text-[16px] font-bold text-orbit-text leading-snug truncate group-hover:text-orbit-accent transition-colors duration-300">
                {repo.displayName}
              </h3>
            </div>

            <p className="body-sm text-[13px] leading-relaxed text-orbit-text-secondary line-clamp-2">
              {repo.description || 'Danh mục mã nguồn chuyên sâu cho môn học.'}
            </p>
          </div>

          {repo.stars !== null && repo.stars > 0 && (
            <div className="shrink-0">
              <span className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-orbit-surface border border-orbit-border text-[11px] font-bold text-orbit-text-muted tabular-nums">
                <Star className="h-3.5 w-3.5 text-amber-400" weight="fill" />
                {repo.stars}
              </span>
            </div>
          )}
        </div>

        <div className="mt-auto flex flex-wrap items-center gap-2 pt-6 border-t border-orbit-border/30">
          {repo.primaryLanguage && (
            <span className="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-orbit-surface border border-orbit-border">
              <span className="h-2 w-2 rounded-full" style={{ backgroundColor: langColor }} />
              <span className="text-[10px] font-semibold text-orbit-text-muted">{repo.primaryLanguage}</span>
            </span>
          )}
          {repo.techStacks?.slice(0, 3).map((stack) => (
            <span
              key={stack}
              className="inline-flex items-center px-3 py-1.5 rounded-xl bg-orbit-accent-subtle border border-orbit-accent/20 text-orbit-accent text-[10px] font-semibold"
            >
              {stack}
            </span>
          ))}
          {repo.techStacks?.length > 3 && (
            <span className="text-[10px] font-semibold text-orbit-text-muted tracking-tight">
              +{repo.techStacks.length - 3} khác
            </span>
          )}
        </div>
      </div>
    </Link>
  )
}
