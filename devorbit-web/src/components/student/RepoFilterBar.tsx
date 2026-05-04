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
            : 'bg-white/[0.04] text-slate-400 border border-white/[0.04] hover:bg-white/[0.08] hover:text-slate-200'
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
              : 'bg-white/[0.04] text-slate-400 border border-white/[0.04] hover:bg-white/[0.08] hover:text-slate-200'
          }`}
        >
          {stack}
        </button>
      ))}
    </div>
  )
}
