import { useState } from 'react'
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
  const [selectedChannel, setSelectedChannel] = useState<ChatChannelResponse | null>(null)

  const { data: channels, loading: loadingChannels, error: channelsError, refetch: refetchChannels } = useAdminFetch(
    (t) => adminApi.getChannels(t),
    [],
  )

  const { data: messages, loading: loadingMessages, error: messagesError, refetch: refetchMessages } = useAdminFetch(
    (t) => adminApi.getCommunityMessages(t),
    [],
  )

  const filteredMessages = selectedChannel
    ? (messages ?? []).filter((m) => m.channelName === selectedChannel.name)
    : (messages ?? [])

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
          {loadingMessages && <AdminSpinner text="Đang tải tin nhắn..." />}
          {messagesError && <AdminErrorBanner message={messagesError} onRetry={refetchMessages} />}
          {!loadingMessages && (
            <MessageTable messages={filteredMessages} onDelete={handleDelete} />
          )}
        </div>
      </div>
    </AdminPageLayout>
  )
}
