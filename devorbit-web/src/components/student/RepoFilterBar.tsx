import { useState } from 'react'
import { FunnelSimple, Star, ArrowUpRight } from '@phosphor-icons/react'

export type SortOption = 'default' | 'mostStars' | 'mostReviews'

type RepoFilterBarProps = {
  techStacks: string[]
  onFilter: (techStack: string | null) => void
  sortBy?: SortOption
  onSortChange?: (sort: SortOption) => void
}

const SORT_OPTIONS: { value: SortOption; label: string; icon: typeof Star }[] = [
  { value: 'default', label: 'Mới nhất', icon: ArrowUpRight },
  { value: 'mostStars', label: 'Nhiều sao', icon: Star },
  { value: 'mostReviews', label: 'Nhiều đánh giá', icon: Star },
]

export function RepoFilterBar({ techStacks, onFilter, sortBy = 'default', onSortChange }: RepoFilterBarProps) {
  const [active, setActive] = useState<string | null>(null)

  function handleClick(stack: string | null) {
    setActive(stack)
    onFilter(stack)
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-wrap items-center gap-2">
        <button
          onClick={() => handleClick(null)}
          className={`px-5 py-2.5 text-[10px] font-bold uppercase tracking-[0.12em] rounded-2xl transition-all duration-300 border cursor-pointer ${
            active === null
              ? 'bg-orbit-accent text-zinc-950 border-orbit-accent shadow-glow'
              : 'bg-transparent text-orbit-text-muted border-orbit-border hover:border-orbit-accent/40 hover:text-orbit-text'
          }`}
        >
          Tất cả
        </button>
        {techStacks.map((stack) => (
          <button
            key={stack}
            onClick={() => handleClick(stack)}
            className={`px-5 py-2.5 text-[10px] font-bold uppercase tracking-[0.12em] rounded-2xl transition-all duration-300 border cursor-pointer ${
              active === stack
                ? 'bg-orbit-accent text-zinc-950 border-orbit-accent shadow-glow'
                : 'bg-transparent text-orbit-text-muted border-orbit-border hover:border-orbit-accent/40 hover:text-orbit-text'
            }`}
          >
            {stack}
          </button>
        ))}
      </div>

      {onSortChange && (
        <div className="flex items-center gap-2">
          <FunnelSimple className="h-3.5 w-3.5 text-orbit-text-muted" weight="bold" />
          <span className="text-[10px] font-bold uppercase tracking-[0.12em] text-orbit-text-muted mr-1">Sắp xếp:</span>
          {SORT_OPTIONS.map(({ value, label, icon: Icon }) => (
            <button
              key={value}
              onClick={() => onSortChange(value)}
              className={`inline-flex items-center gap-1.5 px-4 py-2 text-[10px] font-bold uppercase tracking-[0.1em] rounded-xl transition-all duration-300 border cursor-pointer ${
                sortBy === value
                  ? 'bg-orbit-accent/10 text-orbit-accent border-orbit-accent/30'
                  : 'bg-transparent text-orbit-text-muted border-orbit-border/50 hover:border-orbit-accent/30 hover:text-orbit-text-secondary'
              }`}
            >
              <Icon className="h-3 w-3" weight="bold" />
              {label}
            </button>
          ))}
        </div>
      )}
    </div>
  )
}
