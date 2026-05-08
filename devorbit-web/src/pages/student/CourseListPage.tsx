import { useEffect, useState } from 'react'
import { apiGet } from '../../lib/api'
import { CourseCard } from '../../components/student/CourseCard'
import { Link } from 'react-router-dom'
import type { CourseSummary } from '../../types/api'

export function CourseListPage() {
  const [courses, setCourses] = useState<CourseSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [searchQuery, setSearchQuery] = useState('')

  useEffect(() => {
    apiGet<CourseSummary[]>('/api/courses')
      .then(setCourses)
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  const filteredCourses = courses.filter(c =>
    c.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    c.code.toLowerCase().includes(searchQuery.toLowerCase())
  )

  if (loading) {
    return (
      <div className="relative min-h-[80vh] flex items-center justify-center">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 h-[500px] w-[500px] bg-emerald-500/[0.08] blur-[150px] rounded-full animate-pulse" />
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 h-[300px] w-[300px] bg-indigo-500/[0.05] blur-[100px] rounded-full animate-pulse delay-700" />

        <div className="relative flex flex-col items-center gap-6">
          <div className="relative h-16 w-16">
            <div className="absolute inset-0 rounded-full border-4 border-emerald-500/10" />
            <div className="absolute inset-0 rounded-full border-t-4 border-emerald-500 animate-spin shadow-[0_0_15px_rgba(16,185,129,0.3)]" />
          </div>
          <div className="flex flex-col items-center">
            <p className="text-[11px] font-black text-emerald-500 tracking-[0.4em] uppercase mb-2">Synchronizing</p>
            <p className="heading-3 text-ink animate-pulse tracking-wide font-bold">ACADEMIC UNIVERSE</p>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="relative w-full overflow-hidden min-h-screen pb-24">
      {/* Immersive Background Nodes */}
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute top-[-10%] left-[-10%] w-[50%] h-[800px] bg-emerald-500/[0.03] blur-[150px] rounded-full" />
        <div className="absolute bottom-[10%] right-[-10%] w-[45%] h-[700px] bg-indigo-500/[0.03] blur-[130px] rounded-full" />
        <div className="absolute top-[30%] right-[15%] w-24 h-24 bg-amber-500/5 blur-3xl rounded-full animate-pulse" />
      </div>

      <div className="relative z-10 w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12 py-16 md:py-24">
        <header className="mb-20 animate-in fade-in slide-in-from-bottom-8 duration-1000 ease-out">
          <div className="max-w-4xl">
            <div className="inline-flex items-center gap-3 px-4 py-2 rounded-full bg-emerald-500/5 border border-emerald-500/10 mb-8 backdrop-blur-md shadow-sm">
              <div className="h-2 w-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.5)] animate-pulse" />
              <span className="text-[11px] font-black uppercase tracking-[0.25em] text-emerald-500/80">Academic Catalog Online</span>
            </div>

            <h1 className="hero-display text-4xl sm:text-6xl md:text-7xl lg:text-8xl mb-8 leading-[1.05] tracking-tight">
              Map Your <span className="text-emerald-500 relative inline-block">
                Academic
                <div className="absolute -bottom-2 left-0 w-full h-1 bg-emerald-500/20 rounded-full blur-sm" />
              </span> Journey
            </h1>
            <p className="body-md text-xl text-ink-secondary leading-relaxed mb-12 max-w-3xl opacity-80">
              Navigate the UIT knowledge ecosystem. Discover specialized repositories, 
              interactive neural maps, and peer insights for every course in our curriculum.
            </p>
          </div>

          <div className="flex flex-col lg:flex-row gap-6 items-stretch lg:items-center max-w-5xl">
            <div className="relative flex-1 group">
              <div className="absolute -inset-1 bg-gradient-to-r from-emerald-500/20 to-indigo-500/20 rounded-[2rem] blur opacity-0 group-focus-within:opacity-100 transition duration-500" />
              <div className="relative flex items-center">
                <div className="absolute left-6 flex items-center pointer-events-none text-ink-muted group-focus-within:text-emerald-500 transition-colors duration-300">
                  <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                    <circle cx="11" cy="11" r="8" />
                    <path d="M21 21l-4.35-4.35" />
                  </svg>
                </div>
                <input
                  type="text"
                  placeholder="Search course nodes by name or code (e.g. CS101)..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full bg-glass-surface/80 backdrop-blur-xl border border-glass-border rounded-[1.5rem] py-5 pl-16 pr-6 text-ink focus:outline-none focus:border-emerald-500/50 focus:ring-4 focus:ring-emerald-500/5 transition-all placeholder:text-ink-muted/60 text-lg shadow-sm"
                />
              </div>
            </div>
            
            <Link 
              to="/knowledge-graph"
              className="glass-card-glow flex items-center justify-center gap-4 px-8 py-5 border-emerald-500/20 text-emerald-500 font-black uppercase tracking-[0.2em] text-[13px] hover:border-emerald-500/40 hover:bg-emerald-500/[0.03] transition-all duration-500 group min-w-[240px] rounded-[1.5rem]"
            >
              <svg className="h-5 w-5 transition-transform group-hover:rotate-45 duration-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <path d="M12 2v8m0 4v8M2 12h8m4 0h8" />
                <circle cx="12" cy="12" r="2" />
                <circle cx="3" cy="12" r="1" />
                <circle cx="21" cy="12" r="1" />
              </svg>
              Neural Graph View
            </Link>
          </div>
        </header>

        <section className="animate-in fade-in slide-in-from-bottom-12 duration-1000 delay-300">
          <div className="flex items-center justify-between mb-12 border-b border-glass-border pb-6">
            <h2 className="heading-4 flex items-center gap-3">
              <span className="h-6 w-1 bg-emerald-500 rounded-full" />
              Available Course Nodes
            </h2>
            <div className="px-4 py-1.5 rounded-full bg-glass-surface border border-glass-border text-[11px] font-black uppercase tracking-widest text-ink-muted">
              {filteredCourses.length} Results Found
            </div>
          </div>

          {filteredCourses.length > 0 ? (
            <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
              {filteredCourses.map((c, index) => (
                <div
                  key={c.id}
                  className="animate-in fade-in slide-in-from-bottom-8 duration-700 fill-mode-both"
                  style={{ animationDelay: `${500 + index * 50}ms` }}
                >
                  <CourseCard course={c} />
                </div>
              ))}
            </div>
          ) : (
            <div className="glass-card flex flex-col items-center justify-center py-32 text-center border-dashed border-2 border-emerald-500/10 rounded-[3rem] group">
              <div className="h-24 w-24 rounded-full bg-glass-surface flex items-center justify-center mb-8 border border-glass-border group-hover:scale-110 group-hover:border-emerald-500/30 transition-all duration-700">
                <svg className="h-10 w-10 text-ink-muted group-hover:text-emerald-500 transition-colors" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                  <path d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196 7.5 7.5 0 0010.607 10.607z" />
                </svg>
              </div>
              <h3 className="display-sm mb-4">{searchQuery ? 'Zero Search Results' : 'System Empty'}</h3>
              <p className="body-md text-xl text-ink-muted max-w-md mx-auto leading-relaxed">
                {searchQuery
                  ? `We couldn't locate any academic nodes matching "${searchQuery}". Please verify the course code or name.`
                  : "The academic matrix is currently undergoing synchronization. Please verify back in a few moments."
                }
              </p>
              {searchQuery && (
                <button
                  onClick={() => setSearchQuery('')}
                  className="mt-10 px-8 py-3 rounded-full bg-emerald-500 text-white font-black uppercase tracking-[0.2em] text-[12px] hover:bg-emerald-400 hover:shadow-lg hover:shadow-emerald-500/25 transition-all active:scale-95"
                >
                  Clear search parameters
                </button>
              )}
            </div>
          )}
        </section>
      </div>
    </div>
  )
}

