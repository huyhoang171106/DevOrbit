import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'

const navLinks = [
  { to: '/courses', label: 'Môn Học' },
  { to: '/knowledge-graph', label: 'Sơ Đồ Kiến Thức' },
  { to: '/student/photobooth', label: 'Photobooth' },
]

export function Layout({ children }: { children: React.ReactNode }) {
  const location = useLocation()
  const [mobileOpen, setMobileOpen] = useState(false)

  const isActive = (to: string) =>
    location.pathname === to || location.pathname.startsWith(to + '/')

  return (
    <div className="relative min-h-screen flex flex-col bg-clay-bg selection:bg-clay-primary selection:text-white">
      {/* Top Navigation */}
      <nav className="sticky top-0 z-50 w-full border-b border-clay-border bg-white">
        <div className="mx-auto flex w-full max-w-[1440px] items-center justify-between px-8 h-[64px]">
          <div className="flex items-center gap-12">
            <Link to="/" className="flex items-center gap-3 font-heading text-xl font-bold text-clay-text tracking-tight">
              <div className="bg-clay-primary p-2 text-white rounded-none">
                <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                  <circle cx="12" cy="12" r="5" />
                  <path d="M12 2v4M12 18v4M2 12h4M18 12h4" />
                </svg>
              </div>
              DevOrbit
            </Link>

            {/* Desktop nav */}
            <div className="hidden md:flex items-center h-[64px] gap-1">
              {navLinks.map((link) => (
                <Link
                  key={link.to}
                  to={link.to}
                  className={`relative flex items-center px-4 text-[14px] font-medium transition-colors h-full
                    ${isActive(link.to)
                      ? 'text-clay-primary after:absolute after:bottom-0 after:left-0 after:w-full after:h-[2px] after:bg-clay-primary'
                      : 'text-[#4b5563] hover:text-clay-text'
                    }`}
                >
                  {link.label}
                </Link>
              ))}
            </div>
          </div>

          <div className="hidden md:flex items-center gap-6">
            <Link to="/admin/login" className="text-[13px] font-medium text-ink-muted hover:text-clay-text transition-colors">
              Quản trị
            </Link>
            <Link to="/courses" className="btn-primary">
              Khám phá
            </Link>
          </div>

          {/* Mobile hamburger */}
          <button
            type="button"
            className="md:hidden p-2 border border-clay-border bg-white rounded-none"
            onClick={() => setMobileOpen(!mobileOpen)}
            aria-label="Toggle menu"
          >
            <svg className="h-5 w-5 text-clay-text" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              {mobileOpen ? (
                <path d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
        </div>

        {/* Mobile menu */}
        {mobileOpen && (
          <div className="absolute top-[64px] left-0 w-full border-b border-clay-border bg-white px-6 py-6 space-y-1 animate-in slide-in-from-top duration-300">
            {navLinks.map((link) => (
              <Link
                key={link.to}
                to={link.to}
                onClick={() => setMobileOpen(false)}
                className={`block px-4 py-3 text-[15px] font-medium border-l-2 transition-colors
                  ${isActive(link.to)
                    ? 'text-clay-primary border-l-clay-primary bg-[#eff6ff]'
                    : 'text-[#4b5563] border-l-transparent hover:text-clay-text hover:bg-clay-surface'
                  }`}
              >
                {link.label}
              </Link>
            ))}
            <div className="pt-4 mt-4 border-t border-clay-border space-y-3">
              <Link
                to="/admin/login"
                onClick={() => setMobileOpen(false)}
                className="block px-4 py-3 text-[14px] font-medium text-center text-ink-muted"
              >
                Quản trị
              </Link>
              <Link
                to="/courses"
                onClick={() => setMobileOpen(false)}
                className="btn-primary w-full text-center py-3"
              >
                Khám phá ngay
              </Link>
            </div>
          </div>
        )}
      </nav>

      {/* Main content */}
      <main className="flex-1 w-full">
        {children}
      </main>

      {/* Footer - Hidden on Knowledge Graph to allow full-screen interaction */}
      {!location.pathname.startsWith('/knowledge-graph') && (
        <footer className="bg-clay-surface border-t border-clay-border py-16 px-8">
          <div className="mx-auto max-w-[1440px] flex flex-col md:flex-row items-center justify-between gap-8">
            <div className="flex items-center gap-3 font-heading text-xl font-bold text-clay-text tracking-tight">
              <div className="bg-clay-primary p-2 text-white rounded-none">
                <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                  <circle cx="12" cy="12" r="5" />
                  <path d="M12 2v4M12 18v4M2 12h4M18 12h4" />
                </svg>
              </div>
              DevOrbit
            </div>
            <div className="flex flex-col items-center md:items-end gap-1.5">
              <p className="text-[15px] font-medium text-clay-text text-center md:text-right">
                Cổng tra cứu mã nguồn sinh viên UIT
              </p>
              <p className="text-[12px] text-clay-text-muted">
                Phát triển bởi cộng đồng sinh viên &copy; 2026
              </p>
            </div>
          </div>
        </footer>
      )}
    </div>
  )
}
