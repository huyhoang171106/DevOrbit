import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { ChannelList } from '../../components/admin/community/ChannelList'
import { MessageTable } from '../../components/admin/community/MessageTable'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { getAdminToken } from '../../lib/auth'
import type { ChatChannelResponse } from '../../types/api'

export function CommunityPage() {
  const token = getAdminToken()
  const [searchParams] = useSearchParams()
  const [selectedChannel, setSelectedChannel] = useState<ChatChannelResponse | null>(null)
  const [search, setSearch] = useState('')

  const { data: channels, loading: loadingChannels, error: channelsError, refetch: refetchChannels } = useAdminFetch(
    (t) => adminApi.getChannels(t),
    [],
  )

  useEffect(() => {
    const ch = searchParams.get('ch')
    if (ch && channels) {
      const match = channels.find((c) => c.id === Number(ch))
      if (match) setSelectedChannel(match)
    }
  }, [searchParams, channels])

  const { data: messages, loading: loadingMessages, error: messagesError, refetch: refetchMessages } = useAdminFetch(
    (t) => adminApi.getCommunityMessages(t),
    [],
  )

  const filteredMessages = selectedChannel
    ? (messages ?? []).filter((m) => m.channelName === selectedChannel.name)
    : (messages ?? [])

  const q = search.trim().toLowerCase()
  const searchedMessages = q
    ? filteredMessages.filter((m) => m.content.toLowerCase().includes(q) || m.studentName.toLowerCase().includes(q))
    : filteredMessages

  const handleDelete = async (id: number) => {
    if (!token || !confirm('Xoá tin nhắn này?')) return
    try {
      await adminApi.deleteCommunityMessage(token, id)
      refetchMessages()
    } catch (e) {
      console.error(e)
    }
  }

  if (loadingChannels) return <AdminSpinner text="Đang tải kênh..." />

  return (
    <AdminPageLayout title="Cộng đồng" description="Giám sát tin nhắn cộng đồng">
      {channelsError && <AdminErrorBanner message={channelsError} onRetry={refetchChannels} />}
      <div className="flex gap-6">
        <div className="w-80 flex-shrink-0">
          <ChannelList
            channels={channels ?? []}
            selectedChannel={selectedChannel}
            onSelect={setSelectedChannel}
          />
        </div>
        <div className="flex-1">
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Tìm tin nhắn..."
            className="w-full mb-4 bg-orbit-bg border border-orbit-border rounded-2xl px-3 py-2 text-[13px] text-orbit-text outline-none focus:border-orbit-accent/60 placeholder:text-orbit-text-muted"
          />
          {loadingMessages && <AdminSpinner text="Đang tải tin nhắn..." />}
          {messagesError && <AdminErrorBanner message={messagesError} onRetry={refetchMessages} />}
          {!loadingMessages && (
            <MessageTable messages={searchedMessages} onDelete={handleDelete} />
          )}
        </div>
      </div>
    </AdminPageLayout>
  )
}
