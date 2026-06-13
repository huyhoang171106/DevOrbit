import { useLocation } from 'react-router-dom'
import { NotificationDropdown } from '../shared/NotificationDropdown'

const ROUTE_LABELS: Record<string, string> = {
  '/admin': 'Bảng điều khiển',
  '/admin/courses': 'Môn học',
  '/admin/repos': 'Repos',
  '/admin/students': 'Sinh viên',
  '/admin/reviews': 'Đánh giá',
  '/admin/community': 'Cộng đồng',
  '/admin/chat': 'AI Chat',
  '/admin/relationships': 'Quan hệ',
  '/admin/photobooth': 'Photobooth',
  '/admin/techstack': 'Tech Stack',
}

export function AdminTopbar() {
  const location = useLocation()
  const label = ROUTE_LABELS[location.pathname] || 'Admin'

  return (
    <header className="sticky top-0 z-30 h-16 flex items-center justify-between px-8 border-b border-orbit-border/50 bg-orbit-bg/80 backdrop-blur-md">
      <div className="flex items-center gap-3">
        <span className="h-1.5 w-1.5 rounded-full bg-orbit-accent animate-breathing" />
        <h2 className="font-heading font-bold text-lg text-orbit-text">{label}</h2>
      </div>
      <div className="flex items-center gap-4">
        <NotificationDropdown />
      </div>
    </header>
  )
}
