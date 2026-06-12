import type { CourseSummary } from '../../../types/api'

interface CourseTableProps {
  courses: CourseSummary[]
  onEdit: (course: CourseSummary) => void
  onDelete: (id: number) => void
  onManageResources: (course: CourseSummary) => void
  selectedCourseId?: number | null
}

export function CourseTable({ courses, onEdit, onDelete, onManageResources, selectedCourseId }: CourseTableProps) {
  if (courses.length === 0) {
    return (
      <div className="glass-card p-8 text-center">
        <p className="text-ink-secondary">Không có môn học</p>
      </div>
    )
  }

  return (
    <div className="glass-card overflow-hidden border border-orbit-border">
      <table className="w-full">
        <thead>
          <tr className="border-b border-orbit-border bg-orbit-surface/50">
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Mã MH</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Tên môn học</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">TC</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Loại</th>
            <th className="px-4 py-3 text-right text-xs font-medium text-ink-secondary uppercase">Thao tác</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-clay-border">
          {courses.map((course) => (
            <tr
              key={course.id}
              className={`transition-colors hover:bg-orbit-surface/30 cursor-pointer ${
                selectedCourseId === course.id ? 'bg-orbit-accent/5' : ''
              }`}
              onClick={() => onManageResources(course)}
            >
              <td className="px-4 py-3 text-sm font-medium text-ink-primary">{course.code}</td>
              <td className="px-4 py-3 text-sm text-ink-primary">{course.name}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary">{course.credits ?? '-'}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary">{course.loaiMonHoc ?? '-'}</td>
              <td className="px-4 py-3 text-sm text-right">
                <div className="flex items-center justify-end gap-2">
                  <button
                    onClick={(e) => { e.stopPropagation(); onManageResources(course) }}
                    className={`btn-ghost text-xs px-3 py-1.5 ${
                      selectedCourseId === course.id ? 'text-orbit-accent' : ''
                    }`}
                  >
                    Tài nguyên
                  </button>
                  <button
                    onClick={(e) => { e.stopPropagation(); onEdit(course) }}
                    className="btn-ghost text-xs px-3 py-1.5"
                  >
                    Sửa
                  </button>
                  <button
                    onClick={(e) => { e.stopPropagation(); onDelete(course.id) }}
                    className="btn-ghost text-xs px-3 py-1.5 text-red-400 hover:text-red-300"
                  >
                    Xoá
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
