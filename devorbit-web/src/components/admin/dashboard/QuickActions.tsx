import { Link } from 'react-router-dom'
import { BookOpen, Star, Chats, ArrowRight } from '@phosphor-icons/react'

const ACTIONS = [
  { to: '/admin/courses', icon: BookOpen, label: 'Quản lý môn học', desc: 'Thêm, sửa, xoá môn học và tài nguyên', gradient: 'from-emerald-500/10 to-emerald-500/5' },
  { to: '/admin/reviews', icon: Star, label: 'Duyệt đánh giá', desc: 'Xem và quản lý phản hồi từ sinh viên', gradient: 'from-amber-500/10 to-amber-500/5' },
  { to: '/admin/community', icon: Chats, label: 'Cộng đồng', desc: 'Giám sát tin nhắn cộng đồng', gradient: 'from-blue-500/10 to-blue-500/5' },
]

export function QuickActions() {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
      {ACTIONS.map((action) => (
        <Link
          key={action.to}
          to={action.to}
          className={`orbit-card bg-gradient-to-br ${action.gradient} hover:scale-[1.02] transition-all duration-200 group`}
        >
          <div className="flex items-start justify-between">
            <action.icon size={28} className="text-orbit-accent" />
            <ArrowRight size={16} className="text-ink-muted group-hover:text-orbit-accent group-hover:translate-x-1 transition-all" />
          </div>
          <div className="mt-4">
            <span className="text-sm font-semibold text-ink-primary group-hover:text-orbit-accent transition-colors">
              {action.label}
            </span>
            <p className="mt-1 text-xs text-ink-secondary">{action.desc}</p>
          </div>
        </Link>
      ))}
    </div>
  )
}
