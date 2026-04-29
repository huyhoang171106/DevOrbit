import { Link } from 'react-router-dom'
import type { CourseSummary } from '../../types/api'

export function CourseCard({ course }: { course: CourseSummary }) {
  return (
    <Link
      to={`/courses/${course.id}`}
      className="block rounded-lg border bg-white p-4 shadow-sm transition hover:shadow-md"
    >
      <span className="text-xs font-semibold text-blue-600">{course.code}</span>
      <h3 className="mt-1 text-lg font-medium">{course.name}</h3>
    </Link>
  )
}
