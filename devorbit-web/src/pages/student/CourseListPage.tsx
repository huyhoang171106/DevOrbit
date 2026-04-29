import { useEffect, useState } from 'react'
import { apiGet } from '../../lib/api'
import { CourseCard } from '../../components/student/CourseCard'
import type { CourseSummary } from '../../types/api'

export function CourseListPage() {
  const [courses, setCourses] = useState<CourseSummary[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    apiGet<CourseSummary[]>('/api/courses')
      .then(setCourses)
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  if (loading) return (
    <div className="flex items-center justify-center py-20">
      <div className="text-sm text-slate-500 animate-pulse">Loading courses...</div>
    </div>
  )

  return (
    <div>
      <div className="mb-8 flex items-center gap-3">
        <svg className="h-8 w-8 text-amber-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
          <circle cx="12" cy="12" r="5" />
          <circle cx="12" cy="12" r="8" strokeDasharray="3 3" opacity="0.4" />
          <circle cx="12" cy="12" r="11" strokeDasharray="2 4" opacity="0.2" />
        </svg>
        <h1 className="page-title">Courses</h1>
      </div>
      <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
        {courses.map((c) => (
          <CourseCard key={c.id} course={c} />
        ))}
      </div>
    </div>
  )
}
