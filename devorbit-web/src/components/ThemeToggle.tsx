export function ThemeToggle() {
  return (
    <div className="flex h-9 w-9 items-center justify-center rounded-xl border border-glass-border bg-glass-surface" aria-label="Dark mode">
      <svg className="h-4 w-4 text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z" />
      </svg>
    </div>
  )
}
