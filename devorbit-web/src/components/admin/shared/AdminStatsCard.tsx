interface AdminStatsCardProps {
  label: string
  value: number | string
  icon: React.ReactNode
  trend?: { value: number; label: string }
}

export function AdminStatsCard({ label, value, icon, trend }: AdminStatsCardProps) {
  return (
    <div className="orbit-card p-6">
      <div className="flex items-center justify-between">
        <div className="space-y-2">
          <p className="text-[10px] font-black text-ink-secondary uppercase tracking-[0.2em]">{label}</p>
          <p className="text-3xl font-bold text-orbit-text tabular-nums">{value}</p>
          {trend && (
            <p className="text-xs text-ink-secondary">
              <span className={trend.value >= 0 ? 'text-emerald-400' : 'text-red-400'}>
                {trend.value >= 0 ? '+' : ''}{trend.value}
              </span>
              {' '}{trend.label}
            </p>
          )}
        </div>
        <div className="h-12 w-12 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center text-orbit-accent">
          {icon}
        </div>
      </div>
    </div>
  )
}
