import { useState, useRef, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Bell, Check, Sparkle, UserPlus, ChatCircleDots, ChatTeardropText } from '@phosphor-icons/react'
import { useNotifications } from '../../../hooks/useNotifications'

function notificationIcon(type: string) {
  switch (type) {
    case 'REVIEW_COURSE':
    case 'REVIEW_REPO':
      return <Sparkle className="h-4 w-4 text-orbit-accent" weight="fill" />
    case 'STUDENT_REGISTER':
      return <UserPlus className="h-4 w-4 text-sky-400" weight="fill" />
    case 'COMMUNITY_CHAT':
      return <ChatCircleDots className="h-4 w-4 text-violet-400" weight="fill" />
    case 'AI_CHAT':
      return <ChatTeardropText className="h-4 w-4 text-amber-400" weight="fill" />
    default:
      return <Bell className="h-4 w-4 text-zinc-400" weight="fill" />
  }
}

export function NotificationDropdown() {
  const navigate = useNavigate()
  const { notifications, unreadCount, markAsRead, markAllAsRead } = useNotifications()
  const [open, setOpen] = useState(false)
  const ref = useRef<HTMLDivElement>(null)

  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        setOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClick)
    return () => document.removeEventListener('mousedown', handleClick)
  }, [])

  const handleNotificationClick = (n: typeof notifications[0]) => {
    if (!n.isRead) markAsRead(n.id)
    if (n.targetUrl) navigate(n.targetUrl)
    setOpen(false)
  }

  return (
    <div ref={ref} className="relative">
      <button
        onClick={() => setOpen(!open)}
        className="relative h-9 w-9 rounded-xl bg-orbit-surface border border-orbit-border flex items-center justify-center text-ink-secondary hover:text-orbit-accent hover:border-orbit-accent/30 transition-all duration-200"
        aria-label="Thông báo"
      >
        <Bell size={18} />
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 h-4 min-w-[16px] flex items-center justify-center px-1 rounded-full bg-rose-500 text-[9px] font-black text-white leading-none">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {open && (
        <div className="absolute right-0 top-full mt-2 w-[380px] max-h-[480px] rounded-2xl border border-orbit-border/50 bg-orbit-surface shadow-diffusion overflow-hidden z-50">
          <div className="flex items-center justify-between px-4 py-3 border-b border-orbit-border/30">
            <span className="text-[13px] font-bold text-orbit-text">Thông báo</span>
            {unreadCount > 0 && (
              <button
                onClick={markAllAsRead}
                className="flex items-center gap-1 text-[11px] font-semibold text-orbit-accent hover:text-emerald-300 transition-colors"
              >
                <Check className="h-3 w-3" weight="bold" />
                Đã đọc tất cả
              </button>
            )}
          </div>

          <div className="overflow-y-auto max-h-[380px]">
            {notifications.length === 0 ? (
              <div className="px-4 py-8 text-center">
                <Bell className="h-8 w-8 mx-auto mb-2 text-zinc-500" weight="light" />
                <p className="text-[13px] text-zinc-500">Chưa có thông báo</p>
              </div>
            ) : (
              notifications.map((n) => (
                <button
                  key={n.id}
                  onClick={() => handleNotificationClick(n)}
                  className={`w-full flex items-start gap-3 px-4 py-3 text-left transition-colors hover:bg-orbit-surface/50 ${
                    !n.isRead ? 'bg-orbit-accent/[0.02] border-l-2 border-orbit-accent' : 'border-l-2 border-transparent'
                  }`}
                >
                  <div className="mt-0.5 shrink-0">
                    {notificationIcon(n.type)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className={`text-[13px] leading-relaxed ${n.isRead ? 'text-zinc-400' : 'text-zinc-200'}`}>
                      {n.message}
                    </p>
                    <p className="text-[10px] text-zinc-500 mt-1">
                      {new Date(n.createdAt).toLocaleDateString('vi-VN', {
                        hour: '2-digit',
                        minute: '2-digit',
                        day: 'numeric',
                        month: 'numeric',
                      })}
                    </p>
                  </div>
                  {!n.isRead && (
                    <span className="h-2 w-2 rounded-full bg-orbit-accent shrink-0 mt-1.5" />
                  )}
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  )
}
