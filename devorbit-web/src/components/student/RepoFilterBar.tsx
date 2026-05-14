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
  )
}
