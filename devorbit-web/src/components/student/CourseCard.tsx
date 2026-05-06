import { Link } from 'react-router-dom'
import type { CourseSummary } from '../../types/api'

export function CourseCard({ course }: { course: CourseSummary }) {
  return (
    <Link
      to={`/courses/${course.id}`}
      className="glass-card flex flex-col group h-full cursor-pointer hover:shadow-[0_4px_12px_rgba(0,0,0,0.08)] transition-shadow"
    >
      <div className="mb-4">
        <span className="badge-tag">{course.code}</span>
      </div>
      <h3 className="heading-5 text-ink mb-2 line-clamp-2">
        {course.name}
      </h3>
      <div className="mt-auto pt-4 flex items-center gap-1 body-sm-medium text-emerald-400">
        <span>Explore repos</span>
        <svg className="h-4 w-4 transition-transform group-hover:translate-x-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M5 12h14M12 5l7 7-7 7" />
        </svg>
      </div>
    </Link>
  )
}
