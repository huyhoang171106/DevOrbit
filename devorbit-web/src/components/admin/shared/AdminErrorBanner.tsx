export function AdminErrorBanner({ message, onRetry }: { message: string; onRetry?: () => void }) {
  return (
    <div className="orbit-card border-red-500/20 bg-red-500/5">
      <div className="flex items-start gap-4">
        <div className="h-8 w-8 rounded-xl bg-red-500/10 border border-red-500/20 flex items-center justify-center shrink-0 mt-0.5">
          <svg className="h-4 w-4 text-red-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10" />
            <path d="M12 8v4M12 16h.01" />
          </svg>
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-sm text-red-400 break-words">{message}</p>
          {onRetry && (
            <button onClick={onRetry} className="mt-3 text-xs font-semibold text-orbit-accent hover:text-orbit-accent/80 transition-colors uppercase tracking-wider">
              Thử lại
            </button>
          )}
        </div>
      </div>
    </div>
  )
}
