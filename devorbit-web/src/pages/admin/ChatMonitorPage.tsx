import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { ChatSessionTable } from '../../components/admin/chat/ChatSessionTable'
import { ChatMessageView } from '../../components/admin/chat/ChatMessageView'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import type { ChatSessionAdmin } from '../../types/admin'

export function ChatMonitorPage() {
  const [selectedSession, setSelectedSession] = useState<ChatSessionAdmin | null>(null)
  const [searchParams] = useSearchParams()

  const { data: sessions, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getChatSessions(t),
    [],
  )

  useEffect(() => {
    const sessionId = searchParams.get('sessionId')
    if (sessionId && sessions && sessions.length > 0) {
      const match = sessions.find((s) => s.id === sessionId)
      if (match) setSelectedSession(match)
    }
  }, [searchParams, sessions])

  const { data: messages, loading: loadingMessages } = useAdminFetch(
    (t) => selectedSession ? adminApi.getChatMessages(t, selectedSession.id) : Promise.resolve([]),
    [selectedSession?.id],
  )

  return (
    <AdminPageLayout title="AI Chat" description="Giám sát hội thoại AI Tutor">
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}
      <div className="flex gap-6">
        <div className="w-80 flex-shrink-0">
          {loading ? <AdminSpinner text="Đang tải phiên..." /> : (
            <ChatSessionTable
              sessions={sessions ?? []}
              selectedSessionId={selectedSession?.id ?? null}
              onSelect={setSelectedSession}
            />
          )}
        </div>
        <div className="flex-1">
          <ChatMessageView messages={messages ?? []} loading={loadingMessages} />
        </div>
      </div>
    </AdminPageLayout>
  )
}
