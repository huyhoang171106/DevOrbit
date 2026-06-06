import { useState, useEffect, useRef } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { BookmarkSimple, UsersThree, SignOut, User } from '@phosphor-icons/react'
import { apiStudentGet } from '../../lib/api'
import { isStudentAuthenticated, clearStudentToken } from '../../lib/auth'
import type { StudentProfileResponse } from '../../types/api'

const AVATAR_COLORS = [
  'bg-emerald-500',
  'bg-sky-500',
  'bg-violet-500',
  'bg-rose-500',
  'bg-amber-500',
  'bg-cyan-500',
  'bg-pink-500',
  'bg-indigo-500',
]

function hashColor(name: string): string {
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length]
}

function initials(name: string): string {
  return name
    .split(' ')
    .map((w) => w.charAt(0))
    .join('')
    .toUpperCase()
    .slice(0, 2)
}

export function ProfileDropdown() {
  const navigate = useNavigate()
  const [open, setOpen] = useState(false)
  const [profile, setProfile] = useState<StudentProfileResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const ref = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (!isStudentAuthenticated()) {
      setLoading(false)
      return
    }
    apiStudentGet<StudentProfileResponse>('/api/student/me')
      .then(setProfile)
      .catch(() => clearStudentToken())
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        setOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  function handleLogout() {
    clearStudentToken()
    setOpen(false)
    navigate('/')
  }

  if (loading) return null
  if (!profile) {
    return (
      <Link
        to="/student/login"
        className="btn-primary text-[12px] px-6 py-3"
      >
        <User className="h-4 w-4" weight="bold" />
        Đăng nhập
      </Link>
    )
  }

  const avatarColor = hashColor(profile.fullName)
  const avatarInitials = initials(profile.fullName)

  return (
    <div ref={ref} className="relative">
      <button
        type="button"
        onClick={() => setOpen(!open)}
        className={`h-10 w-10 rounded-full ${avatarColor} flex items-center justify-center text-[14px] font-bold text-white ring-2 ring-transparent hover:ring-white/20 transition-[ring] duration-200 cursor-pointer`}
        title={profile.fullName}
      >
        {avatarInitials}
      </button>

      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0, scale: 0.95, y: -4 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: -4 }}
            transition={{ type: 'spring', stiffness: 300, damping: 30 }}
            className="absolute right-0 top-full mt-2 w-72 rounded-2xl border border-orbit-border bg-orbit-bg/95 backdrop-blur-xl shadow-2xl overflow-hidden z-50"
          >
            <div className="p-5 pb-3 border-b border-orbit-border">
              <div className="flex items-center gap-4">
                <div className={`h-12 w-12 rounded-full ${avatarColor} flex items-center justify-center text-[18px] font-bold text-white flex-shrink-0`}>
                  {avatarInitials}
                </div>
                <div className="min-w-0">
                  <p className="text-[15px] font-bold text-orbit-text truncate">
                    {profile.fullName}
                  </p>
                  <p className="text-[13px] text-orbit-text-secondary font-mono">
                    {profile.studentCode}
                  </p>
                </div>
              </div>
              <p className="mt-2 text-[12px] text-orbit-text-muted truncate">
                {profile.email}
              </p>
            </div>

            <div className="p-2">
              <Link
                to="/student/profile"
                onClick={() => setOpen(false)}
                className="flex items-center gap-3 px-4 py-2.5 text-[14px] font-medium text-orbit-text-secondary hover:text-orbit-text hover:bg-orbit-surface rounded-xl transition-[color,background-color] duration-200"
              >
                <User className="h-4 w-4" weight="regular" />
                Trang cá nhân
              </Link>
              <Link
                to="/student/bookmarks"
                onClick={() => setOpen(false)}
                className="flex items-center gap-3 px-4 py-2.5 text-[14px] font-medium text-orbit-text-secondary hover:text-orbit-text hover:bg-orbit-surface rounded-xl transition-[color,background-color] duration-200"
              >
                <BookmarkSimple className="h-4 w-4" weight="regular" />
                Bookmarks
              </Link>
              <Link
                to="/community"
                onClick={() => setOpen(false)}
                className="flex items-center gap-3 px-4 py-2.5 text-[14px] font-medium text-orbit-text-secondary hover:text-orbit-text hover:bg-orbit-surface rounded-xl transition-[color,background-color] duration-200"
              >
                <UsersThree className="h-4 w-4" weight="regular" />
                Cộng đồng
              </Link>
            </div>

            <div className="border-t border-orbit-border p-2">
              <button
                type="button"
                onClick={handleLogout}
                className="flex w-full items-center gap-3 px-4 py-2.5 text-[14px] font-medium text-red-400 hover:bg-red-500/10 rounded-xl transition-[color,background-color] duration-200 cursor-pointer"
              >
                <SignOut className="h-4 w-4" weight="regular" />
                Đăng xuất
              </button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}
