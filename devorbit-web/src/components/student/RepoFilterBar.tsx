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
    <div className="mb-4 flex flex-wrap gap-2">
      <button
        onClick={() => handleClick(null)}
        className={`rounded-full px-3 py-1 text-sm font-medium ${
          active === null ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
        }`}
      >
        All
      </button>
      {techStacks.map((stack) => (
        <button
          key={stack}
          onClick={() => handleClick(stack)}
          className={`rounded-full px-3 py-1 text-sm font-medium ${
            active === stack ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          }`}
        >
          {stack}
        </button>
      ))}
    </div>
  )
}
