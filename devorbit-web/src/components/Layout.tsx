import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'

import { ThemeToggle } from './ThemeToggle'

const navLinks = [
  { to: '/courses', label: 'Courses' },
  { to: '/knowledge-graph', label: 'Knowledge Graph' },
]

export function Layout({ children }: { children: React.ReactNode }) {
  const location = useLocation()
  const [mobileOpen, setMobileOpen] = useState(false)

  return (
    <div className="relative min-h-screen flex flex-col bg-cosmic-base">
      {/* Top Navigation */}
      <nav className="sticky top-0 z-50 w-full border-b border-glass-border bg-cosmic-base/80 backdrop-blur-[20px] h-[64px]">
        <div className="mx-auto flex h-full max-w-[1440px] items-center justify-between px-8">
          <div className="flex items-center gap-8">
            <Link to="/" className="flex items-center gap-2 text-[18px] font-bold font-heading text-ink transition-opacity hover:opacity-80">
              <svg className="h-6 w-6 text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="5" />
                <circle cx="12" cy="12" r="8" strokeDasharray="3 3" opacity="0.4" />
                <circle cx="12" cy="12" r="11" strokeDasharray="2 4" opacity="0.2" />
              </svg>
              DevOrbit
            </Link>

            {/* Desktop nav */}
            <div className="hidden md:flex items-center gap-4">
              {navLinks.map((link) => (
                <Link
                  key={link.to}
                  to={link.to}
                  className={`text-[14px] font-medium transition-colors ${
                    location.pathname === link.to || location.pathname.startsWith(link.to + '/')
                      ? 'text-ink'
                      : 'text-ink-muted hover:text-ink'
                  }`}
                >
                  {link.label}
                </Link>
              ))}
            </div>
          </div>

          <div className="hidden md:flex items-center gap-4">
            <ThemeToggle />
            <Link to="/admin/login" className="text-[14px] font-medium text-ink-muted hover:text-ink transition-colors">
              Admin
            </Link>
            <Link to="/courses" className="btn-primary">
              Get Started
            </Link>
          </div>

          {/* Mobile hamburger */}
          <button
            type="button"
            className="md:hidden p-2 text-ink-muted hover:text-ink transition-colors"
            onClick={() => setMobileOpen(!mobileOpen)}
            aria-label="Toggle menu"
          >
            <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
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
          <div className="md:hidden border-b border-glass-border bg-cosmic-base px-6 py-4 space-y-4">
            {navLinks.map((link) => (
              <Link
                key={link.to}
                to={link.to}
                onClick={() => setMobileOpen(false)}
                className={`block text-[14px] font-medium transition-colors ${
                  location.pathname === link.to || location.pathname.startsWith(link.to + '/')
                    ? 'text-ink'
                    : 'text-ink-muted hover:text-ink'
                }`}
              >
                {link.label}
              </Link>
            ))}
            <div className="pt-4 flex flex-col gap-3">
               <div className="flex items-center justify-between">
                 <span className="text-[14px] font-medium text-ink-muted">Appearance</span>
                 <ThemeToggle />
               </div>
               <Link
                  to="/admin/login"
                  onClick={() => setMobileOpen(false)}
                  className="text-[14px] font-medium text-ink-muted hover:text-ink transition-colors"
                >
                  Admin
               </Link>
               <Link
                  to="/courses"
                  onClick={() => setMobileOpen(false)}
                  className="btn-primary w-full"
                >
                  Get Started
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
      <footer className="border-t border-glass-border py-[64px] px-[32px]">
        <div className="mx-auto max-w-[1440px] flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2 text-[16px] font-medium font-heading text-ink">
            DevOrbit
          </div>
          <p className="text-[14px] text-ink-muted">
            UIT Knowledge Repository Explorer &mdash; Built for students, by students.
          </p>
        </div>
      </footer>
    </div>
  )
}
