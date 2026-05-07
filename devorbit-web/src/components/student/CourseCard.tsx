import { Link } from 'react-router-dom'
import type { CourseSummary } from '../../types/api'

export function CourseCard({ course }: { course: CourseSummary }) {
  return (
    <Link
      to={`/courses/${course.id}`}
      className="glass-card flex flex-col group h-full cursor-pointer border-opacity-50 hover:border-emerald-500/30 hover:bg-glass-surface-hover hover:shadow-[0_20px_50px_rgba(16,185,129,0.1)] transition-all duration-500 p-6"
    >
      <div className="flex justify-between items-start mb-6">
        <div className="h-10 w-10 rounded-xl bg-emerald-500/10 flex items-center justify-center border border-emerald-500/20 group-hover:bg-emerald-500/20 transition-colors duration-500">
          <svg className="h-5 w-5 text-emerald-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
          </svg>
        </div>
        <span className="badge-tag !bg-emerald-500/5 !border-emerald-500/10 !text-emerald-500/80 font-bold tracking-wider">
          {course.code}
        </span>
      </div>

      <h3 className="heading-4 text-ink mb-3 line-clamp-2 group-hover:text-emerald-500 transition-colors duration-500">
        {course.name}
      </h3>
      
      <p className="body-sm text-ink-secondary mb-6 line-clamp-2 opacity-80 group-hover:opacity-100 transition-opacity">
        Explore detailed repositories, interactive roadmaps, and academic resources for this course.
      </p>

      <div className="mt-auto pt-4 flex items-center justify-between border-t border-glass-border group-hover:border-emerald-500/20 transition-colors duration-500">
        <span className="text-[12px] font-bold uppercase tracking-widest text-ink-muted group-hover:text-emerald-500/80 transition-colors">
          View Details
        </span>
        <div className="h-8 w-8 rounded-full bg-glass-surface flex items-center justify-center transition-all duration-500 group-hover:bg-emerald-500 group-hover:text-white">
          <svg className="h-4 w-4 transition-transform duration-500 group-hover:translate-x-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
            <path d="M5 12h14M12 5l7 7-7 7" />
          </svg>
        </div>
      </div>
    </Link>
  )
}

