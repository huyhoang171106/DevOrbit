import { Link } from 'react-router-dom'

export function HomePage() {
  return (
    <div className="flex min-h-[70vh] flex-col items-center justify-center text-center">
      <div className="relative mb-8">
        <svg className="h-20 w-20 text-amber-400/80 animate-float" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
          <circle cx="12" cy="12" r="5" />
          <circle cx="12" cy="12" r="8" strokeDasharray="3 3" opacity="0.4" />
          <circle cx="12" cy="12" r="11" strokeDasharray="2 4" opacity="0.2" />
        </svg>
      </div>
      <h1 className="page-title mb-3">
        <span className="bg-gradient-to-r from-amber-400 via-amber-300 to-amber-400 bg-clip-text text-transparent">
          DevOrbit
        </span>
      </h1>
      <p className="mb-8 max-w-md text-slate-400">
        Discover course-related GitHub repositories, explore tech stacks, and find the
        perfect resources for your learning journey.
      </p>
      <Link to="/courses" className="btn-primary text-base">
        Browse Courses
      </Link>
    </div>
  )
}
