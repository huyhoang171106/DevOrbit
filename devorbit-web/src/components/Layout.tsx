import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'

const navLinks = [
  { to: '/courses', label: 'Môn Học' },
  { to: '/knowledge-graph', label: 'Sơ Đồ Kiến Thức' },
  { to: '/photograph', label: 'Photobooth' },
]

export function Layout({ children }: { children: React.ReactNode }) {
  const location = useLocation()
  const [mobileOpen, setMobileOpen] = useState(false)

  return (
    <div className="relative min-h-screen flex flex-col bg-clay-bg selection:bg-clay-primary selection:text-white">
      {/* Top Navigation */}
      <nav className="sticky top-0 z-50 w-full border-b-[3px] border-clay-border bg-clay-surface h-[90px] flex items-center">
        <div className="mx-auto flex w-full max-w-[1440px] items-center justify-between px-8">
          <div className="flex items-center gap-16">
            <Link to="/" className="flex items-center gap-4 text-[28px] font-black font-heading text-clay-text transition-all hover:scale-105 active:scale-95 group">
              <div className="bg-clay-primary p-2.5 rounded-2xl border-[3px] border-clay-border shadow-[5px_5px_0px_0px_var(--color-clay-shadow-outer)] group-hover:shadow-none group-hover:translate-x-1 group-hover:translate-y-1 transition-all">
                <svg className="h-7 w-7 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3.5">
                  <circle cx="12" cy="12" r="5" />
                  <path d="M12 2v4M12 18v4M2 12h4M18 12h4" />
                </svg>
              </div>
              <span className="uppercase tracking-tighter italic">DevOrbit</span>
            </Link>

            {/* Desktop nav */}
            <div className="hidden md:flex items-center gap-4">
              {navLinks.map((link) => (
                <Link
                  key={link.to}
                  to={link.to}
                  className={`px-5 py-2.5 text-[15px] font-black uppercase tracking-wider transition-all rounded-xl border-[3px] ${location.pathname === link.to || location.pathname.startsWith(link.to + '/')
                      ? 'bg-clay-primary text-white border-clay-border shadow-[4px_4px_0px_0px_var(--color-clay-shadow-outer)]'
                      : 'text-clay-text border-transparent hover:border-clay-border hover:bg-glass-surface-hover'
                    }`}
                >
                  {link.label}
                </Link>
              ))}
            </div>
          </div>

          <div className="hidden md:flex items-center gap-8">
            <Link to="/admin/login" className="text-[14px] font-black uppercase tracking-widest text-ink-muted hover:text-clay-text transition-colors">
              Quản trị
            </Link>
            <Link to="/courses" className="btn-primary px-8 py-4">
              Khám phá
            </Link>
          </div>

          {/* Mobile hamburger */}
          <button
            type="button"
            className="md:hidden p-3 bg-clay-surface border-[3px] border-clay-border rounded-xl shadow-[4px_4px_0px_0px_var(--color-clay-shadow-outer)] active:shadow-none active:translate-x-1 active:translate-y-1 transition-all"
            onClick={() => setMobileOpen(!mobileOpen)}
            aria-label="Toggle menu"
          >
            <svg className="h-6 w-6 text-clay-text" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
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
          <div className="absolute top-[80px] left-0 w-full border-b-[3px] border-clay-border bg-clay-bg px-6 py-8 space-y-4 animate-in slide-in-from-top duration-300">
            {navLinks.map((link) => (
              <Link
                key={link.to}
                to={link.to}
                onClick={() => setMobileOpen(false)}
                className={`block px-6 py-4 text-[16px] font-black uppercase tracking-widest rounded-2xl border-[3px] ${location.pathname === link.to || location.pathname.startsWith(link.to + '/')
                    ? 'bg-clay-primary text-white border-clay-border shadow-[6px_6px_0px_0px_var(--color-clay-shadow-outer)]'
                    : 'bg-clay-surface text-clay-text border-clay-border shadow-[4px_4px_0px_0px_var(--color-clay-shadow-outer)]'
                  }`}
              >
                {link.label}
              </Link>
            ))}
            <div className="pt-6 flex flex-col gap-4">
              <Link
                to="/admin/login"
                onClick={() => setMobileOpen(false)}
                className="block px-6 py-4 text-[16px] font-black uppercase tracking-widest text-center text-ink-muted"
              >
                Truy cập Quản trị
              </Link>
              <Link
                to="/courses"
                onClick={() => setMobileOpen(false)}
                className="btn-primary w-full text-center py-5"
              >
                Khám phá ngay
              </Link>
            </div>
          </div>
        )}
      </nav>

      {/* Main content */}
      <main className="relative z-10 flex-1 w-full">
        {children}
      </main>

      {/* Footer */}
      <footer className="bg-clay-surface border-t-[3px] border-clay-border py-[100px] px-[32px]">
        <div className="mx-auto max-w-[1440px] flex flex-col md:flex-row items-center justify-between gap-16">
          <div className="flex items-center gap-4 text-[36px] font-black font-heading text-clay-text uppercase italic">
            <div className="bg-clay-primary w-12 h-12 rounded-xl border-[3px] border-clay-border shadow-[5px_5px_0px_0px_var(--color-clay-shadow-outer)] flex items-center justify-center">
              <svg className="h-7 w-7 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3.5">
                <circle cx="12" cy="12" r="5" />
                <path d="M12 2v4M12 18v4M2 12h4M18 12h4" />
              </svg>
            </div>
            DevOrbit
          </div>
          <div className="flex flex-col items-center md:items-end gap-3">
            <p className="text-[18px] font-black text-clay-text text-center md:text-right uppercase tracking-wide">
              Cổng tra cứu mã nguồn sinh viên UIT
            </p>
            <p className="text-[14px] font-bold text-clay-text-muted uppercase tracking-[0.2em]">
              Phát triển bởi cộng đồng sinh viên &copy; 2026
            </p>
          </div>
        </div>
      </footer>
    </div>
  )
}
