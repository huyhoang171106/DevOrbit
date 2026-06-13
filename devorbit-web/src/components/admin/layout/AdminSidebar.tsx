import { NavLink } from 'react-router-dom'
import {
  BookOpen, UsersThree, ChatDots, Camera, Tag,
  GitBranch, Star, Link, GearSix, CaretDown, CaretLeft, CaretRight, Cube,
  SignOut, UserCircle,
} from '@phosphor-icons/react'
import { getSidebarCollapsed, setSidebarCollapsed, getAdminToken } from '../../../lib/adminAuth'
import { clearAdminToken } from '../../../lib/auth'
import { useNavigate } from 'react-router-dom'
import { useState, useMemo } from 'react'

function decodeUsername(token: string): string {
  try {
    const payload = token.split('.')[1]
    const decoded = JSON.parse(atob(payload))
    return decoded.sub || decoded.username || 'Admin'
  } catch {
    return 'Admin'
  }
}

const NAV_GROUPS = [
  {
    label: 'Quản lý môn học',
    icon: GearSix,
    children: [
      { to: '/admin/courses', icon: BookOpen, label: 'Môn học' },
      { to: '/admin/repos', icon: GitBranch, label: 'Repos' },
      { to: '/admin/reviews', icon: Star, label: 'Đánh giá' },
      { to: '/admin/relationships', icon: Link, label: 'Quan hệ' },
    ],
  },
]

const NAV_ITEMS = [
  { to: '/admin/students', icon: UsersThree, label: 'Sinh viên' },
  { to: '/admin/community', icon: UsersThree, label: 'Cộng đồng' },
  { to: '/admin/chat', icon: ChatDots, label: 'AI Chat' },
  { to: '/admin/photobooth', icon: Camera, label: 'Photobooth' },
  { to: '/admin/techstack', icon: Tag, label: 'Tech Stack' },
]

export function AdminSidebar() {
  const navigate = useNavigate()
  const [collapsed, setCollapsed] = useState(getSidebarCollapsed)
  const [expandedGroup, setExpandedGroup] = useState<string | null>(null)
  const [showLogout, setShowLogout] = useState(false)

  const username = useMemo(() => {
    const token = getAdminToken()
    return token ? decodeUsername(token) : 'Admin'
  }, [])

  const toggle = () => {
    setCollapsed(!collapsed)
    setSidebarCollapsed(!collapsed)
  }

  const toggleGroup = (label: string) => {
    setExpandedGroup(expandedGroup === label ? null : label)
  }

  const groupOpen = expandedGroup !== null

  const handleLogout = () => {
    clearAdminToken()
    navigate('/admin/login')
  }

  return (
    <aside
      className={`glow-border fixed left-0 top-0 h-full z-40 flex flex-col border-r border-orbit-border bg-orbit-surface/80 backdrop-blur-xl transition-all duration-200 ${
        collapsed ? 'w-[64px]' : 'w-[240px]'
      }`}
    >
      {/* Logo */}
      <div className="h-16 flex items-center justify-center border-b border-orbit-border">
        <NavLink to="/admin" end className="flex items-center gap-2.5 group">
          <div className="relative h-9 w-9 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center overflow-hidden shrink-0 group-hover:border-orbit-accent/40 transition-colors duration-500">
            <Cube className="h-5 w-5 text-orbit-accent" weight="duotone" />
            <div className="absolute inset-0 bg-gradient-to-br from-orbit-accent/5 to-transparent" />
          </div>
          {!collapsed && (
            <span className="font-heading text-lg font-black text-orbit-text tracking-tight group-hover:text-orbit-accent transition-colors duration-500">DevOrbit</span>
          )}
        </NavLink>
      </div>

      {/* Collapse button */}
      <button
        onClick={toggle}
        className="absolute right-0 top-1/2 -translate-y-1/2 translate-x-1/2 h-8 w-8 rounded-full bg-orbit-elevated border border-orbit-border flex items-center justify-center text-ink-muted hover:text-orbit-accent hover:border-orbit-accent/30 transition-all duration-200 shadow-diffusion z-20"
      >
        {collapsed ? <CaretRight size={14} weight="bold" /> : <CaretLeft size={14} weight="bold" />}
      </button>

      {/* Navigation */}
      <nav className="flex-1 py-5 overflow-y-auto overflow-x-hidden scrollbar-none space-y-0.5">
        {NAV_GROUPS.map((group) => (
          <div key={group.label}>
            <button
              onClick={() => toggleGroup(group.label)}
              className={`w-full flex items-center gap-3 px-4 py-3 text-base font-heading font-semibold transition-all duration-200 ${
                collapsed ? 'justify-center mx-auto w-12 h-12 px-0' : 'mx-4'
              } ${groupOpen ? 'text-orbit-accent' : 'text-ink-secondary hover:text-orbit-accent'}`}
            >
              <group.icon size={20} weight={groupOpen ? 'fill' : 'regular'} />
              {!collapsed && (
                <>
                  <span className="flex-1 text-left">{group.label}</span>
                  <CaretDown
                    size={14}
                    weight="bold"
                    className={`text-ink-muted transition-transform duration-200 ${
                      groupOpen ? 'rotate-180' : ''
                    }`}
                  />
                </>
              )}
            </button>
            {groupOpen && !collapsed && (
              <div className="ml-5 mt-0.5 mb-1">
                {group.children.map((child) => (
                  <NavLink
                    key={child.to}
                    to={child.to}
                    className={({ isActive }) =>
                      `relative flex items-center gap-3 px-4 py-2.5 ml-3 mr-4 rounded-xl text-base font-heading transition-all duration-200 ${
                        isActive
                          ? 'bg-orbit-accent/10 text-orbit-accent shadow-glow'
                          : 'text-ink-secondary hover:text-orbit-accent hover:bg-orbit-surface/50'
                      }`
                    }
                  >
                    {({ isActive }) => (
                      <>
                        {isActive && (
                          <span className="absolute left-0 top-1/2 -translate-y-1/2 w-0.5 h-5 bg-orbit-accent rounded-full" />
                        )}
                        <child.icon size={20} weight={isActive ? 'fill' : 'regular'} />
                        <span>{child.label}</span>
                      </>
                    )}
                  </NavLink>
                ))}
              </div>
            )}
          </div>
        ))}

        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `relative flex items-center gap-3 px-4 py-3 rounded-xl text-base font-heading font-semibold transition-all duration-200 ${
                isActive
                  ? 'bg-orbit-accent/10 text-orbit-accent shadow-glow'
                  : 'text-ink-secondary hover:text-orbit-accent hover:bg-orbit-surface/50'
              } ${collapsed ? 'justify-center mx-auto w-12 h-12 px-0' : 'mx-4'}`
            }
          >
            {({ isActive }) => (
              <>
                {isActive && (
                  <span className="absolute left-0 top-1/2 -translate-y-1/2 w-0.5 h-5 bg-orbit-accent rounded-full" />
                )}
                <item.icon size={20} weight={isActive ? 'fill' : 'regular'} />
                {!collapsed && <span>{item.label}</span>}
              </>
            )}
          </NavLink>
        ))}
      </nav>

      {/* Account section */}
      <div className="border-t border-orbit-border">
        <button
          onClick={() => setShowLogout(!showLogout)}
          className={`w-full flex items-center gap-3 px-4 py-3.5 transition-colors ${
            collapsed ? 'justify-center mx-auto w-12 h-12 px-0' : 'mx-4'
          } text-ink-secondary hover:text-orbit-text hover:bg-orbit-surface/50 rounded-xl`}
        >
          <UserCircle size={20} className="text-orbit-accent shrink-0" weight="duotone" />
          {!collapsed && (
            <span className="text-sm font-medium text-ink-primary">{username}</span>
          )}
        </button>
        {showLogout && !collapsed && (
          <div className="px-5 pb-4">
            <button
              onClick={handleLogout}
              className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium text-red-400 hover:bg-red-500/10 transition-colors"
            >
              <SignOut size={18} />
              <span>Đăng xuất</span>
            </button>
          </div>
        )}
      </div>
    </aside>
  )
}
