import { useState, useMemo } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { m as motion, AnimatePresence, useScroll, useTransform, LayoutGroup } from 'framer-motion'
import { ParticleNetwork } from './ParticleNetwork'
import { Cube, UserCircle, SignOut, BookmarkSimple, UsersThree, User } from '@phosphor-icons/react'
import { ScrollProgressIndicator } from '../motion/primitives/ScrollProgressIndicator'
import { navLinks } from './navigation'
import { AiChatWidget } from './student/AiChatWidget'
import { ChatProvider } from './student/ChatContext'
import { ProfileDropdown } from './student/ProfileDropdown'
import { isStudentAuthenticated, clearStudentToken } from '../lib/auth'

export function Layout({ children }: { children: React.ReactNode }) {
  const location = useLocation()
  const [mobileOpen, setMobileOpen] = useState(false)

  const isActive = (to: string) =>
    location.pathname === to || location.pathname.startsWith(to + '/')

  // Scroll-driven nav background opacity
  const { scrollY } = useScroll()
  const navBorderColor = useTransform(scrollY, [0, 100], ['rgba(39,39,42,0.3)', 'rgba(39,39,42,0.8)'])
  const navBackgroundOpacity = useTransform(scrollY, [0, 80], [0.3, 1])

  const isAdmin = location.pathname.startsWith('/admin')

  const showParticles = useMemo(() => !isAdmin, [isAdmin])

  return (
    <ChatProvider>
    <div className="relative min-h-screen flex flex-col bg-orbit-bg selection:bg-orbit-accent selection:text-zinc-950">
      {showParticles && <ParticleNetwork />}

      {!isAdmin && (
        <ScrollProgressIndicator position="right" showLabel={false} />
      )}

      {!isAdmin && <motion.nav
        className="sticky top-0 z-50 w-full border-b gpu"
        style={{
          borderColor: navBorderColor,
        }}
      >
        {/* Background layer with animated opacity */}
        <motion.div
          className="absolute inset-0 -z-10"
          style={{
            background: 'rgba(9,9,11,0.95)',
            opacity: navBackgroundOpacity,
          }}
        />
        <div className="mx-auto flex w-full max-w-[1440px] items-center justify-between px-6 md:px-10 h-[72px]">
          {/* Logo */}
          <Link
            to="/"
            className="relative flex items-center gap-3 font-heading text-xl font-black text-orbit-text tracking-tight group"
          >
            <div className="relative h-9 w-9 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center overflow-hidden group-hover:border-orbit-accent/40 transition-[border-color] duration-500">
              <Cube className="h-5 w-5 text-orbit-accent" weight="duotone" />
              <div className="absolute inset-0 bg-gradient-to-br from-orbit-accent/5 to-transparent" />
            </div>
            <span className="tracking-tight">DevOrbit</span>
          </Link>

          {/* Desktop nav */}
          <LayoutGroup>
          <div className="hidden md:flex items-center h-[72px] gap-1">
            {navLinks.map((link) => {
              const Icon = link.icon
              return (
                <Link
                  key={link.to}
                  to={link.to}
                  className={`relative flex items-center gap-2.5 px-5 text-[15px] font-bold transition-[color] duration-300 h-full group
                    ${isActive(link.to)
                      ? 'text-orbit-accent'
                      : 'text-orbit-text-secondary hover:text-orbit-text'
                    }`}
                >
                  <Icon className="h-4 w-4" weight={isActive(link.to) ? 'fill' : 'regular'} />
                  {link.label}
                  {isActive(link.to) && (
                    <motion.div
                      layoutId="nav-indicator"
                      className="absolute bottom-0 left-4 right-4 h-[2px] bg-orbit-accent rounded-full"
                      transition={{ type: 'spring', stiffness: 300, damping: 30 }}
                    />
                  )}
                </Link>
              )
            })}
          </div>
          </LayoutGroup>

          <div className="hidden md:flex items-center gap-4">
            {isStudentAuthenticated() ? (
              <ProfileDropdown />
            ) : (
              <Link
                to="/student/login"
                className="btn-primary text-[12px] px-6 py-3"
              >
                <UserCircle className="h-4 w-4" weight="bold" />
                Đăng nhập
              </Link>
            )}
          </div>

          {/* Mobile hamburger */}
          <button
            type="button"
            className="md:hidden relative h-10 w-10 rounded-xl bg-orbit-surface border border-orbit-border flex items-center justify-center text-orbit-text-secondary hover:text-orbit-text hover:border-orbit-accent/30 transition-[color,border-color] duration-300"
            onClick={() => setMobileOpen(!mobileOpen)}
            aria-label={mobileOpen ? 'Đóng menu' : 'Mở menu'}
          >
            <div className="relative h-4 w-4 flex flex-col items-center justify-center gap-[3px]">
              <span className={`block h-[2px] w-full bg-current rounded-full transition-[transform,opacity] duration-300 ${mobileOpen ? 'rotate-45 translate-y-[2.5px]' : ''}`} />
              <span className={`block h-[2px] w-full bg-current rounded-full transition-[transform,opacity] duration-300 ${mobileOpen ? 'opacity-0' : ''}`} />
              <span className={`block h-[2px] w-full bg-current rounded-full transition-[transform,opacity] duration-300 ${mobileOpen ? '-rotate-45 -translate-y-[2.5px]' : ''}`} />
            </div>
          </button>
        </div>

        {/* Mobile menu */}
        <AnimatePresence>
          {mobileOpen && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              transition={{ type: 'spring', stiffness: 300, damping: 30 }}
              className="overflow-hidden border-b border-orbit-border bg-orbit-bg/95 backdrop-blur-lg"
            >
              <div className="px-6 py-6 space-y-1">
                {navLinks.map((link) => {
                  const Icon = link.icon
                  return (
                    <Link
                      key={link.to}
                      to={link.to}
                      onClick={() => setMobileOpen(false)}
                      className={`flex items-center gap-4 px-5 py-4 text-[16px] font-bold rounded-2xl transition-[background-color,color,border-color] duration-200
                        ${isActive(link.to)
                          ? 'text-orbit-accent bg-orbit-accent/5 border border-orbit-accent/20'
                          : 'text-orbit-text-secondary hover:text-orbit-text hover:bg-orbit-surface border border-transparent'
                        }`}
                    >
                      <Icon className="h-5 w-5" weight={isActive(link.to) ? 'fill' : 'regular'} />
                      {link.label}
                    </Link>
                  )
                })}
                <div className="pt-4 mt-4 border-t border-orbit-border space-y-1">
                  {isStudentAuthenticated() ? (
                    <>
                      <Link
                        to="/student/profile"
                        onClick={() => setMobileOpen(false)}
                        className="flex items-center gap-4 px-5 py-4 text-[16px] font-bold rounded-2xl text-orbit-text-secondary hover:text-orbit-text hover:bg-orbit-surface border border-transparent transition-[background-color,color,border-color] duration-200"
                      >
                        <User className="h-5 w-5" weight="regular" />
                        Trang cá nhân
                      </Link>
                      <Link
                        to="/student/bookmarks"
                        onClick={() => setMobileOpen(false)}
                        className="flex items-center gap-4 px-5 py-4 text-[16px] font-bold rounded-2xl text-orbit-text-secondary hover:text-orbit-text hover:bg-orbit-surface border border-transparent transition-[background-color,color,border-color] duration-200"
                      >
                        <BookmarkSimple className="h-5 w-5" weight="regular" />
                        Đã đánh dấu
                      </Link>
                      <Link
                        to="/community"
                        onClick={() => setMobileOpen(false)}
                        className="flex items-center gap-4 px-5 py-4 text-[16px] font-bold rounded-2xl text-orbit-text-secondary hover:text-orbit-text hover:bg-orbit-surface border border-transparent transition-[background-color,color,border-color] duration-200"
                      >
                        <UsersThree className="h-5 w-5" weight="regular" />
                        Cộng đồng
                      </Link>
                      <button
                        type="button"
                        onClick={() => { clearStudentToken(); setMobileOpen(false); }}
                        className="flex w-full items-center gap-4 px-5 py-4 text-[16px] font-bold rounded-2xl text-red-400 hover:bg-red-500/10 border border-transparent transition-[background-color,color,border-color] duration-200 cursor-pointer"
                      >
                        <SignOut className="h-5 w-5" weight="regular" />
                        Đăng xuất
                      </button>
                    </>
                  ) : (
                    <Link
                      to="/student/login"
                      onClick={() => setMobileOpen(false)}
                      className="btn-primary w-full justify-center py-4"
                    >
                      <UserCircle className="h-4 w-4" weight="bold" />
                      Đăng nhập
                    </Link>
                  )}
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </motion.nav>}

      {/* Main content */}
      <main className="relative z-10 flex-1 w-full min-h-0">
        {children}
      </main>

      {!isAdmin && !location.pathname.startsWith('/knowledge-graph') && !location.pathname.startsWith('/ai-tutor') && !location.pathname.startsWith('/community') && (
        <footer className="relative z-10 border-t border-orbit-border bg-orbit-bg">
          <div className="mx-auto max-w-[1440px] px-6 md:px-10 py-16">
            <div className="flex flex-col md:flex-row items-center justify-between gap-8">
              <div className="flex items-center gap-3 font-heading text-lg font-black text-orbit-text tracking-tight">
                <div className="h-9 w-9 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center">
                  <Cube className="h-5 w-5 text-orbit-accent" weight="duotone" />
                </div>
                DevOrbit
              </div>
              <div className="flex flex-col items-center md:items-end gap-1">
                <p className="text-[14px] font-medium text-orbit-text-secondary text-center md:text-right">
                  Cổng tra cứu mã nguồn sinh viên UIT
                </p>
                <p className="text-[12px] text-orbit-text-muted">
                  Phát triển bởi cộng đồng sinh viên &copy; 2026
                </p>
              </div>
            </div>
          </div>
        </footer>
      )}

      {!isAdmin && <AiChatWidget />}
    </div>
    </ChatProvider>
  )
}
