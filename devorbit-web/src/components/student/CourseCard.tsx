import { Link } from 'react-router-dom'
import type { CourseSummary } from '../../types/api'

export function CourseCard({ course }: { course: CourseSummary }) {
  return (
    <Link
      to={`/courses/${course.id}`}
      className="glass-card group block p-5"
    >
      <span className="badge-language">{course.code}</span>
      <h3 className="mt-3 text-lg font-semibold text-slate-100 transition-colors group-hover:text-amber-400">
        {course.name}
      </h3>
      <div className="mt-3 flex items-center gap-1 text-sm text-slate-500 group-hover:text-amber-400/60">
        <span>Explore repos</span>
        <svg className="h-4 w-4 transition-transform group-hover:translate-x-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M5 12h14M12 5l7 7-7 7" />
        </svg>
      </div>
    </Link>
  )
}
