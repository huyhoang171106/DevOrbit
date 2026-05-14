import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { Cube, Graph, Compass, BookOpen, User, Camera } from '@phosphor-icons/react'

const navLinks = [
  { to: '/courses', label: 'Môn Học', icon: BookOpen },
  { to: '/knowledge-graph', label: 'Sơ Đồ Kiến Thức', icon: Graph },
  { to: '/student/photobooth', label: 'Photobooth', icon: Camera },
]

export function Layout({ children }: { children: React.ReactNode }) {
  const location = useLocation()
  const [mobileOpen, setMobileOpen] = useState(false)

  const isActive = (to: string) =>
    location.pathname === to || location.pathname.startsWith(to + '/')

  return (
    <div className="relative min-h-screen flex flex-col bg-orbit-bg selection:bg-orbit-accent selection:text-zinc-950">
      {/* Ambient background glow */}
      <div className="fixed inset-0 pointer-events-none z-0 overflow-hidden">
        <div className="absolute -top-[40%] -right-[20%] w-[60%] h-[60%] bg-orbit-accent/5 blur-[200px] rounded-full" />
        <div className="absolute -bottom-[30%] -left-[20%] w-[50%] h-[50%] bg-emerald-500/3 blur-[150px] rounded-full" />
      </div>

      {/* Top Navigation */}
      <nav className="sticky top-0 z-50 w-full border-b border-orbit-border bg-orbit-bg/80 backdrop-blur-2xl">
        <div className="mx-auto flex w-full max-w-[1440px] items-center justify-between px-6 md:px-10 h-[72px]">
          {/* Logo */}
          <Link
            to="/"
            className="relative flex items-center gap-3 font-heading text-xl font-black text-orbit-text tracking-tight group"
          >
            <div className="relative h-9 w-9 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center overflow-hidden group-hover:border-orbit-accent/40 transition-all duration-500">
              <Cube className="h-5 w-5 text-orbit-accent" weight="duotone" />
              <div className="absolute inset-0 bg-gradient-to-br from-orbit-accent/5 to-transparent" />
            </div>
            <span className="tracking-tight">DevOrbit</span>
          </Link>

          {/* Desktop nav */}
          <div className="hidden md:flex items-center h-[72px] gap-1">
            {navLinks.map((link) => {
              const Icon = link.icon
              return (
                <Link
                  key={link.to}
                  to={link.to}
                  className={`relative flex items-center gap-2.5 px-5 text-[13px] font-semibold transition-all duration-300 h-full group
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
                      transition={{ type: 'spring', stiffness: 100, damping: 20 }}
                    />
                  )}
                </Link>
              )
            })}
          </div>

          <div className="hidden md:flex items-center gap-4">
            <Link
              to="/admin/login"
              className="flex items-center gap-2 text-[13px] font-medium text-orbit-text-muted hover:text-orbit-text-secondary transition-colors px-4 py-2 rounded-2xl hover:bg-orbit-surface"
            >
              <User className="h-4 w-4" weight="regular" />
              Quản trị
            </Link>
            <Link to="/courses" className="btn-primary text-[12px] px-6 py-3">
              <Compass className="h-4 w-4" weight="bold" />
              Khám phá
            </Link>
          </div>

          {/* Mobile hamburger */}
          <button
            type="button"
            className="md:hidden relative h-10 w-10 rounded-xl bg-orbit-surface border border-orbit-border flex items-center justify-center text-orbit-text-secondary hover:text-orbit-text hover:border-orbit-accent/30 transition-all duration-300"
            onClick={() => setMobileOpen(!mobileOpen)}
            aria-label={mobileOpen ? 'Đóng menu' : 'Mở menu'}
          >
            <div className="relative h-4 w-4 flex flex-col items-center justify-center gap-[3px]">
              <span className={`block h-[2px] w-full bg-current rounded-full transition-all duration-300 ${mobileOpen ? 'rotate-45 translate-y-[2.5px]' : ''}`} />
              <span className={`block h-[2px] w-full bg-current rounded-full transition-all duration-300 ${mobileOpen ? 'opacity-0' : ''}`} />
              <span className={`block h-[2px] w-full bg-current rounded-full transition-all duration-300 ${mobileOpen ? '-rotate-45 -translate-y-[2.5px]' : ''}`} />
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
              transition={{ type: 'spring', stiffness: 100, damping: 20 }}
              className="overflow-hidden border-b border-orbit-border bg-orbit-bg/95 backdrop-blur-2xl"
            >
              <div className="px-6 py-6 space-y-1">
                {navLinks.map((link) => {
                  const Icon = link.icon
                  return (
                    <Link
                      key={link.to}
                      to={link.to}
                      onClick={() => setMobileOpen(false)}
                      className={`flex items-center gap-4 px-5 py-4 text-[15px] font-semibold rounded-2xl transition-all duration-200
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
                <div className="pt-4 mt-4 border-t border-orbit-border space-y-3">
                  <Link
                    to="/admin/login"
                    onClick={() => setMobileOpen(false)}
                    className="flex items-center gap-3 px-5 py-4 text-[14px] font-medium text-orbit-text-muted rounded-2xl hover:bg-orbit-surface transition-colors"
                  >
                    <User className="h-4 w-4" weight="regular" />
                    Quản trị
                  </Link>
                  <Link
                    to="/courses"
                    onClick={() => setMobileOpen(false)}
                    className="btn-primary w-full justify-center py-4"
                  >
                    <Compass className="h-4 w-4" weight="bold" />
                    Khám phá ngay
                  </Link>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </nav>

      {/* Main content */}
      <main className="relative z-10 flex-1 w-full">
        {children}
      </main>

      {/* Footer */}
      {!location.pathname.startsWith('/knowledge-graph') && (
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
    </div>
  )
}
