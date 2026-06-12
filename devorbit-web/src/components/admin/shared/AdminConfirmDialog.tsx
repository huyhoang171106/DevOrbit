interface AdminConfirmDialogProps {
  open: boolean
  title: string
  message: string
  confirmLabel?: string
  variant?: 'danger' | 'primary'
  onConfirm: () => void
  onCancel: () => void
  loading?: boolean
}

export function AdminConfirmDialog({
  open, title, message, confirmLabel = 'Xác nhận', variant = 'danger',
  onConfirm, onCancel, loading = false
}: AdminConfirmDialogProps) {
  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm">
      <div className="orbit-card max-w-md w-full mx-4 p-6">
        <div className="flex items-center gap-4 mb-4">
          <div className={`h-10 w-10 rounded-2xl flex items-center justify-center shrink-0 ${
            variant === 'danger' ? 'bg-red-500/10 border border-red-500/20' : 'bg-orbit-accent/10 border border-orbit-accent/20'
          }`}>
            {variant === 'danger' ? (
              <svg className="h-5 w-5 text-red-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M12 9v4M12 17h.01" />
                <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
              </svg>
            ) : (
              <svg className="h-5 w-5 text-orbit-accent" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M12 5v14M5 12h14" />
              </svg>
            )}
          </div>
          <div>
            <h3 className="font-heading font-bold text-lg text-orbit-text">{title}</h3>
            <p className="text-sm text-ink-secondary mt-0.5">{message}</p>
          </div>
        </div>
        <div className="flex justify-end gap-3 mt-6">
          <button onClick={onCancel} className="btn-ghost text-sm px-5 py-2.5" disabled={loading}>
            Huỷ
          </button>
          <button
            onClick={onConfirm}
            disabled={loading}
            className={`text-sm px-5 py-2.5 rounded-xl font-semibold transition-all duration-200 ${
              variant === 'danger'
                ? 'bg-red-500/20 text-red-400 hover:bg-red-500/30'
                : 'btn-primary text-sm px-5 py-2.5'
            }`}
          >
            {loading ? (
              <span className="flex items-center gap-2">
                <span className="h-3 w-3 border-2 border-current border-t-transparent rounded-full animate-spin" />
                Đang xử lý...
              </span>
            ) : confirmLabel}
          </button>
        </div>
      </div>
    </div>
  )
}
