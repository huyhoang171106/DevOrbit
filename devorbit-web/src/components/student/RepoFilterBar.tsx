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
    <div className="flex flex-wrap items-center gap-2">
      <button
        onClick={() => handleClick(null)}
        className={`px-5 py-2 rounded-xl text-[11px] font-black uppercase tracking-[0.15em] transition-all duration-300 cursor-pointer ${active === null
            ? 'bg-emerald-500 text-white shadow-lg shadow-emerald-500/25 border border-emerald-400'
            : 'bg-glass-surface text-ink-muted border border-glass-border hover:border-emerald-500/30 hover:text-clay-text'
          }`}
      >
        All Nodes
      </button>
      {techStacks.map((stack) => (
        <button
          key={stack}
          onClick={() => handleClick(stack)}
          className={`px-5 py-2 rounded-xl text-[11px] font-black uppercase tracking-[0.15em] transition-all duration-300 cursor-pointer ${active === stack
              ? 'bg-emerald-500 text-white shadow-lg shadow-emerald-500/25 border border-emerald-400'
              : 'bg-glass-surface text-ink-muted border border-glass-border hover:border-emerald-500/30 hover:text-clay-text'
            }`}
        >
          {stack}
        </button>
      ))}
    </div>
  )
}
