import { useEffect, useState } from 'react'
import { apiGet } from '../../lib/api'
import { CourseCard } from '../../components/student/CourseCard'
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
      <div className="relative min-h-[60vh] flex items-center justify-center">
        <div className="absolute top-1/4 left-1/4 h-64 w-64 rounded-full bg-emerald-500/10 blur-[100px] animate-pulse" />
        <div className="absolute bottom-1/4 right-1/4 h-64 w-64 rounded-full bg-indigo-500/10 blur-[100px] animate-pulse" />

        <div className="relative flex flex-col items-center gap-4">
          <div className="relative h-12 w-12">
            <div className="absolute inset-0 rounded-full border-2 border-emerald-500/20" />
            <div className="absolute inset-0 rounded-full border-t-2 border-emerald-500 animate-spin" />
          </div>
          <p className="body-sm-medium text-ink-secondary animate-pulse tracking-wide">INITIALIZING ACADEMIC CORE...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="relative w-full overflow-hidden">
      <div className="absolute top-0 left-[-10%] w-[40%] h-[500px] bg-emerald-500/5 blur-[120px] rounded-full pointer-events-none" />
      <div className="absolute top-[200px] right-[-10%] w-[35%] h-[400px] bg-indigo-500/5 blur-[100px] rounded-full pointer-events-none" />

      <div className="relative z-10 w-full max-w-[1440px] mx-auto px-6 md:px-12 py-16 md:py-24">
        <header className="mb-16">
          <div className="max-w-2xl">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/20 mb-6">
              <div className="h-1.5 w-1.5 rounded-full bg-emerald-500 animate-pulse" />
              <span className="text-[11px] font-bold uppercase tracking-wider text-emerald-500">Academic Catalog</span>
            </div>

            <h1 className="hero-display text-4xl md:text-6xl mb-6 leading-tight">
              Explore Your <span className="text-emerald-500">Academic</span> Universe
            </h1>
            <p className="body-md text-lg text-ink-secondary leading-relaxed mb-10">
              Navigate through the UIT course catalog. Discover knowledge repositories,
              connected roadmaps, and community insights for every subject.
            </p>
          </div>

          <div className="relative max-w-xl group">
            <div className="absolute inset-y-0 left-4 flex items-center pointer-events-none text-ink-muted group-focus-within:text-emerald-500 transition-colors">
              <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="11" cy="11" r="8" />
                <path d="M21 21l-4.35-4.35" />
              </svg>
            </div>
            <input
              type="text"
              placeholder="Search courses by name or code (e.g. CS101)..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-glass-surface border border-glass-border rounded-2xl py-4 pl-12 pr-4 text-ink focus:outline-none focus:border-emerald-500/50 focus:ring-4 focus:ring-emerald-500/5 transition-all placeholder:text-ink-muted"
            />
          </div>
        </header>

        {filteredCourses.length > 0 ? (
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {filteredCourses.map((c, index) => (
              <div
                key={c.id}
                className="relative group animate-in fade-in slide-in-from-bottom-4 duration-500 fill-mode-both"
                style={{ animationDelay: `${index * 50}ms` }}
              >
                <CourseCard course={c} />
              </div>
            ))}
          </div>
        ) : (
          <div className="glass-card flex flex-col items-center justify-center py-24 text-center border-dashed">
            <div className="h-16 w-16 rounded-full bg-glass-surface flex items-center justify-center mb-6">
              <svg className="h-8 w-8 text-ink-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
            </div>
            <h3 className="heading-3 mb-2">{searchQuery ? 'No matching courses' : 'No courses found'}</h3>
            <p className="body-md max-w-sm">
              {searchQuery
                ? `We couldn't find any courses matching "${searchQuery}". Try a different search term.`
                : "We couldn't find any courses in the catalog right now. Check back later or contact an administrator."
              }
            </p>
            {searchQuery && (
              <button
                onClick={() => setSearchQuery('')}
                className="mt-6 text-emerald-500 font-bold hover:underline"
              >
                Clear search
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
