interface AdminPageLayoutProps {
  title?: string
  description?: string
  action?: React.ReactNode
  children: React.ReactNode
}

export function AdminPageLayout({ description, action, children }: AdminPageLayoutProps) {
  return (
    <div className="w-full max-w-[1280px] mx-auto px-8 py-8">
      {(description || action) && (
        <div className="flex items-start justify-between mb-8">
          <div>
            {description && <p className="body-sm text-ink-secondary">{description}</p>}
          </div>
          {action && (
            <div className="shrink-0">{action}</div>
          )}
        </div>
      )}
      <div className="space-y-6">
        {children}
      </div>
    </div>
  )
}
