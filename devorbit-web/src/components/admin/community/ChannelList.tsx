import { useState } from 'react'
import { MagnifyingGlass, CaretDown } from '@phosphor-icons/react'
import type { ChatChannelResponse } from '../../../types/api'

interface ChannelListProps {
  channels: ChatChannelResponse[]
  selectedChannel: ChatChannelResponse | null
  onSelect: (channel: ChatChannelResponse) => void
}

const GROUP_CONFIG: Record<string, { label: string; color: string }> = {
  GENERAL: { label: 'Chung', color: 'bg-violet-500/10 text-violet-400 border-violet-500/20' },
  COURSE: { label: 'Môn học', color: 'bg-blue-500/10 text-blue-400 border-blue-500/20' },
  TECH_STACK: { label: 'Tech Stack', color: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20' },
}

const GROUP_ORDER = ['GENERAL', 'COURSE', 'TECH_STACK']

export function ChannelList({ channels, selectedChannel, onSelect }: ChannelListProps) {
  const [search, setSearch] = useState('')
  const [collapsed, setCollapsed] = useState<Record<string, boolean>>({
    GENERAL: false,
    COURSE: true,
    TECH_STACK: true,
  })

  const q = search.trim().toLowerCase()
  const filtered = q
    ? channels.filter((c) => c.name.toLowerCase().includes(q))
    : channels

  const grouped = q
    ? null
    : Object.fromEntries(
        GROUP_ORDER.map((type) => [type, filtered.filter((c) => c.type === type)])
      )

  return (
    <div className="glass-card overflow-hidden">
      <div className="px-4 py-3 border-b border-orbit-border">
        <h3 className="text-sm font-semibold text-orbit-text">Kênh</h3>
      </div>

      {/* Search */}
      <div className="px-4 py-2 border-b border-orbit-border/30">
        <div className="relative">
          <MagnifyingGlass className="absolute left-3 top-1/2 -translate-y-1/2 h-3.5 w-3.5 text-zinc-500" weight="regular" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Tìm kênh..."
            className="w-full bg-orbit-bg border border-orbit-border rounded-xl pl-8 pr-3 py-1.5 text-[12px] text-orbit-text outline-none placeholder:text-zinc-500 focus:border-orbit-accent/60 transition-colors"
          />
        </div>
      </div>

      {filtered.length === 0 && (
        <div className="px-4 py-6 text-center text-xs text-zinc-500">Không có kênh</div>
      )}

      {/* Search mode: flat list with type badges */}
      {q ? (
        <div className="divide-y divide-zinc-800/30">
          {filtered.map((channel) => (
            <button
              key={channel.id}
              onClick={() => onSelect(channel)}
              className={`w-full text-left px-4 py-3 transition-colors ${
                selectedChannel?.id === channel.id
                  ? 'bg-orbit-accent/10 text-orbit-accent'
                  : 'text-zinc-400 hover:bg-orbit-surface/30 hover:text-zinc-200'
              }`}
            >
              <div className="flex items-center justify-between gap-2">
                <span className="text-sm font-medium truncate">{channel.name}</span>
                <span className={`shrink-0 text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full border ${
                  GROUP_CONFIG[channel.type]?.color || ''
                }`}>
                  {GROUP_CONFIG[channel.type]?.label || channel.type}
                </span>
              </div>
            </button>
          ))}
        </div>
      ) : (
        /* Normal mode: grouped + collapsible */
        <div className="divide-y divide-zinc-800/30">
          {GROUP_ORDER.map((type) => {
            const group = grouped![type]
            if (!group || group.length === 0) return null
            const config = GROUP_CONFIG[type]
            const isCollapsed = collapsed[type]

            return (
              <div key={type}>
                <button
                  onClick={() => setCollapsed((prev) => ({ ...prev, [type]: !prev[type] }))}
                  className="w-full flex items-center justify-between px-4 py-2.5 text-[11px] font-bold uppercase tracking-wider text-zinc-500 hover:text-zinc-300 transition-colors"
                >
                  <span>{config?.label || type}</span>
                  <div className="flex items-center gap-2">
                    <span className="text-[10px] font-normal lowercase tracking-normal text-zinc-600">{group.length}</span>
                    <CaretDown className={`h-3 w-3 transition-transform duration-200 ${isCollapsed ? '-rotate-90' : ''}`} weight="bold" />
                  </div>
                </button>
                {!isCollapsed && (
                  <div className="pb-1">
                    {group.map((channel) => (
                      <button
                        key={channel.id}
                        onClick={() => onSelect(channel)}
                        className={`w-full text-left px-4 py-2.5 transition-colors ${
                          selectedChannel?.id === channel.id
                            ? 'bg-orbit-accent/10 text-orbit-accent'
                            : 'text-zinc-400 hover:bg-orbit-surface/30 hover:text-zinc-200'
                        }`}
                      >
                        <span className="text-sm font-medium">{channel.name}</span>
                      </button>
                    ))}
                  </div>
                )}
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
