import { useState } from 'react'

type RepoFilterBarProps = {
  techStacks: string[]
  onFilter: (techStack: string | null) => void
}

export function RepoFilterBar({ techStacks, onFilter }: RepoFilterBarProps) {
  const [active, setActive] = useState<string | null>(null)

  function handleClick(stack: string | null) {
    setActive(stack)
    onFilter(stack)
  }

  return (
    <div className="mb-6 flex flex-wrap gap-2">
      <button
        onClick={() => handleClick(null)}
        className={`rounded-xl px-4 py-1.5 text-sm font-medium transition-all duration-200 cursor-pointer ${
          active === null
            ? 'bg-amber-400/15 text-amber-400 border border-amber-400/20 shadow-sm shadow-amber-400/10'
            : 'bg-glass-surface text-ink-muted border border-glass-border hover:bg-glass-surface-hover hover:text-ink'
        }`}
      >
        All
      </button>
      {techStacks.map((stack) => (
        <button
          key={stack}
          onClick={() => handleClick(stack)}
          className={`rounded-xl px-4 py-1.5 text-sm font-medium transition-all duration-200 cursor-pointer ${
            active === stack
              ? 'bg-amber-400/15 text-amber-400 border border-amber-400/20 shadow-sm shadow-amber-400/10'
              : 'bg-glass-surface text-ink-muted border border-glass-border hover:bg-glass-surface-hover hover:text-ink'
          }`}
        >
          {stack}
        </button>
      ))}
    </div>
  )
}
