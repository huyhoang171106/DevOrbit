import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'

import { ThemeToggle } from './ThemeToggle'

const navLinks = [
  { to: '/courses', label: 'Courses' },
  { to: '/student/bookmarks', label: 'My Learning' },
]

export function Layout({ children }: { children: React.ReactNode }) {
  const location = useLocation()
  const [mobileOpen, setMobileOpen] = useState(false)

  return (
    <div className="relative min-h-screen flex flex-col bg-canvas text-charcoal font-inter dark:bg-canvas-dark dark:text-on-dark">
      {/* Top Navigation */}
      <nav className="sticky top-0 z-50 w-full bg-canvas border-b border-hairline-soft h-[64px] dark:bg-canvas-dark dark:border-hairline-dark">
        <div className="mx-auto flex h-full max-w-[1440px] items-center justify-between px-8">
          <div className="flex items-center gap-8">
            <Link to="/" className="flex items-center gap-2 text-[18px] font-semibold text-ink transition-opacity hover:opacity-80 dark:text-on-dark">
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
                      ? 'text-ink dark:text-on-dark'
                      : 'text-steel hover:text-ink dark:text-muted dark:hover:text-on-dark'
                  }`}
                >
                  {link.label}
                </Link>
              ))}
            </div>
          </div>

          <div className="hidden md:flex items-center gap-4">
            <ThemeToggle />
            <Link to="/admin/login" className="text-[14px] font-medium text-steel hover:text-ink transition-colors dark:text-muted dark:hover:text-on-dark">
              Admin
            </Link>
            <Link to="/courses" className="btn-primary">
              Get Started
            </Link>
          </div>

          {/* Mobile hamburger */}
          <button
            type="button"
            className="md:hidden p-2 text-steel hover:text-ink transition-colors"
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
          <div className="md:hidden border-b border-hairline-soft bg-canvas px-6 py-4 space-y-4 dark:border-hairline-dark dark:bg-canvas-dark">
            {navLinks.map((link) => (
              <Link
                key={link.to}
                to={link.to}
                onClick={() => setMobileOpen(false)}
                className={`block text-[14px] font-medium transition-colors ${
                  location.pathname === link.to || location.pathname.startsWith(link.to + '/')
                    ? 'text-ink dark:text-on-dark'
                    : 'text-steel hover:text-ink dark:text-muted dark:hover:text-on-dark'
                }`}
              >
                {link.label}
              </Link>
            ))}
            <div className="pt-4 flex flex-col gap-3">
               <div className="flex items-center justify-between">
                 <span className="text-[14px] font-medium text-steel dark:text-muted">Appearance</span>
                 <ThemeToggle />
               </div>
               <Link 
                  to="/admin/login" 
                  onClick={() => setMobileOpen(false)}
                  className="text-[14px] font-medium text-steel hover:text-ink transition-colors dark:text-muted dark:hover:text-on-dark"
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
      <footer className="relative z-10 bg-canvas border-t border-hairline py-[64px] px-[32px] dark:bg-canvas-dark dark:border-hairline-dark">
        <div className="mx-auto max-w-[1440px] flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2 text-[16px] font-medium text-ink dark:text-on-dark">
            DevOrbit
          </div>
          <p className="text-[14px] text-steel dark:text-muted">
            UIT Knowledge Repository Explorer &mdash; Built for students, by students.
          </p>
        </div>
      </footer>
    </div>
  )
}
