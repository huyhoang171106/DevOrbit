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
    <div className="relative min-h-screen flex flex-col bg-slate-50 dark:bg-[#070b15]">
      {/* Top Navigation */}
      <nav className="sticky top-0 z-50 w-full border-b border-black/10 bg-slate-50/80 dark:border-white/10 dark:bg-[#070b15]/80 backdrop-blur-[20px] h-[64px]">
        <div className="mx-auto flex h-full max-w-[1440px] items-center justify-between px-8">
          <div className="flex items-center gap-8">
            <Link to="/" className="flex items-center gap-2 text-[18px] font-bold font-heading text-slate-900 dark:text-slate-100 transition-opacity hover:opacity-80">
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
                      ? 'text-slate-900 dark:text-slate-100'
                      : 'text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100'
                  }`}
                >
                  {link.label}
                </Link>
              ))}
            </div>
          </div>

          <div className="hidden md:flex items-center gap-4">
            <ThemeToggle />
            <Link to="/admin/login" className="text-[14px] font-medium text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100 transition-colors">
              Admin
            </Link>
            <Link to="/courses" className="btn-primary">
              Get Started
            </Link>
          </div>

          {/* Mobile hamburger */}
          <button
            type="button"
            className="md:hidden p-2 text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100 transition-colors"
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
          <div className="md:hidden border-b border-black/10 dark:border-white/10 bg-slate-50 dark:bg-[#070b15] px-6 py-4 space-y-4">
            {navLinks.map((link) => (
              <Link
                key={link.to}
                to={link.to}
                onClick={() => setMobileOpen(false)}
                className={`block text-[14px] font-medium transition-colors ${
                  location.pathname === link.to || location.pathname.startsWith(link.to + '/')
                    ? 'text-slate-900 dark:text-slate-100'
                    : 'text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100'
                }`}
              >
                {link.label}
              </Link>
            ))}
            <div className="pt-4 flex flex-col gap-3">
               <div className="flex items-center justify-between">
                 <span className="text-[14px] font-medium text-slate-500 dark:text-slate-400">Appearance</span>
                 <ThemeToggle />
               </div>
               <Link
                  to="/admin/login"
                  onClick={() => setMobileOpen(false)}
                  className="text-[14px] font-medium text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100 transition-colors"
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
      <footer className="border-t border-black/10 dark:border-white/10 py-[64px] px-[32px]">
        <div className="mx-auto max-w-[1440px] flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2 text-[16px] font-medium font-heading text-slate-900 dark:text-slate-100">
            DevOrbit
          </div>
          <p className="text-[14px] text-slate-500 dark:text-slate-400">
            UIT Knowledge Repository Explorer &mdash; Built for students, by students.
          </p>
        </div>
      </footer>
    </div>
  )
}
