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
        className={`px-5 py-2 text-[11px] font-semibold uppercase tracking-[0.1em] transition-all border cursor-pointer ${active === null
            ? 'bg-clay-primary text-white border-clay-primary'
            : 'bg-white text-ink-muted border-clay-border hover:border-clay-primary hover:text-clay-text'
          }`}
      >
        All Nodes
      </button>
      {techStacks.map((stack) => (
        <button
          key={stack}
          onClick={() => handleClick(stack)}
          className={`px-5 py-2 text-[11px] font-semibold uppercase tracking-[0.1em] transition-all border cursor-pointer ${active === stack
              ? 'bg-clay-primary text-white border-clay-primary'
              : 'bg-white text-ink-muted border-clay-border hover:border-clay-primary hover:text-clay-text'
            }`}
        >
          {stack}
        </button>
      ))}
    </div>
  )
}
