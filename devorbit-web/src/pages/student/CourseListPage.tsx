import { useEffect, useState } from 'react'
import { apiGet, apiStudentPost } from '../../lib/api'
import { getStudentToken } from '../../lib/auth'
import { CourseCard } from '../../components/student/CourseCard'
import type { CourseSummary } from '../../types/api'

export function CourseListPage() {
  const [courses, setCourses] = useState<CourseSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [savedId, setSavedId] = useState<number | null>(null)

  useEffect(() => {
    apiGet<CourseSummary[]>('/api/courses')
      .then(setCourses)
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  async function saveCourse(courseId: number) {
    const token = getStudentToken()
    if (!token) {
      window.location.href = '/student/login'
      return
    }
    await apiStudentPost('/api/student/bookmarks', token, { targetType: 'COURSE', targetId: courseId })
    setSavedId(courseId)
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 body-sm text-steel">
          <svg className="h-5 w-5 animate-spin text-brand-green" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Loading courses...
        </div>
      </div>
    )
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[40px]">
        <h1 className="display-sm text-ink mb-2">Course Catalog</h1>
        <p className="body-md text-steel max-w-[600px]">Browse the UIT course catalog and explore linked repositories.</p>
      </div>

      <div className="grid gap-[24px] sm:grid-cols-2 lg:grid-cols-3">
        {courses.map((c) => (
          <div key={c.id} className="relative group">
            <CourseCard course={c} />
            <button
              type="button"
              onClick={(e) => {
                e.preventDefault();
                void saveCourse(c.id);
              }}
              className="absolute right-4 top-4 btn-secondary !py-1 !px-2 !text-xs !bg-surface z-10"
            >
              {savedId === c.id ? 'Saved' : 'Save'}
            </button>
          </div>
        ))}
      </div>

      {courses.length === 0 && (
        <div className="card-base py-16 text-center text-steel bg-transparent border-dashed border-hairline-soft mt-8">
          <p className="body-md">No courses available yet.</p>
        </div>
      )}
    </div>
  )
}
