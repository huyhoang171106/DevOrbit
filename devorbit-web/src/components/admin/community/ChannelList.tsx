import type { ChatChannelResponse } from '../../../types/api'

interface ChannelListProps {
  channels: ChatChannelResponse[]
  selectedChannel: ChatChannelResponse | null
  onSelect: (channel: ChatChannelResponse) => void
}

export function ChannelList({ channels, selectedChannel, onSelect }: ChannelListProps) {
  return (
    <div className="glass-card overflow-hidden">
      <div className="px-4 py-3 border-b border-orbit-border">
        <h3 className="text-sm font-semibold text-ink-primary">Kênh</h3>
      </div>
      <div className="divide-y divide-clay-border/50">
        {channels.length === 0 && (
          <div className="px-4 py-6 text-center text-xs text-ink-secondary">Không có kênh</div>
        )}
        {channels.map((channel) => (
          <button
            key={channel.id}
            onClick={() => onSelect(channel)}
            className={`w-full text-left px-4 py-3 transition-colors ${
              selectedChannel?.id === channel.id
                ? 'bg-orbit-accent/10 text-orbit-accent'
                : 'text-ink-secondary hover:bg-orbit-surface/30 hover:text-ink-primary'
            }`}
          >
            <span className="text-sm font-medium">{channel.name}</span>
            <span className="block text-xs text-ink-muted mt-0.5">{channel.type}</span>
          </button>
        ))}
      </div>
    </div>
  )
}
