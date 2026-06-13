import type { AdminStudent } from '../../../types/admin'

interface StudentTableProps {
  students: AdminStudent[]
  onToggleActive: (student: AdminStudent) => void
  onViewDetail: (student: AdminStudent) => void
}

export function StudentTable({ students, onToggleActive, onViewDetail }: StudentTableProps) {
  if (students.length === 0) {
    return (
      <div className="glass-card p-8 text-center">
        <p className="text-ink-secondary">Không tìm thấy sinh viên</p>
      </div>
    )
  }

  return (
    <div className="glass-card overflow-hidden border border-orbit-border">
      <table className="w-full">
        <thead>
          <tr className="border-b border-orbit-border bg-orbit-surface/50">
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Tên đăng nhập</th>
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Họ tên</th>
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Email</th>
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Trạng thái</th>
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Thao tác</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-orbit-border">
          {students.map((student) => (
            <tr key={student.id} className="transition-colors hover:bg-orbit-surface/30">
              <td className="px-4 py-3 text-sm font-medium text-orbit-text text-center">{student.studentCode}</td>
              <td className="px-4 py-3 text-sm text-orbit-text text-center">{student.fullName}</td>
              <td className="px-4 py-3 text-sm text-orbit-text text-center">{student.email}</td>
              <td className="px-4 py-3 text-sm text-center">
                <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                  student.active
                    ? 'bg-green-500/10 text-green-400'
                    : 'bg-red-500/10 text-red-400'
                }`}>
                  {student.active ? 'Hoạt động' : 'Vô hiệu'}
                </span>
              </td>
              <td className="px-4 py-3 text-sm text-center">
                <div className="flex justify-center gap-2">
                  <button
                    onClick={() => onViewDetail(student)}
                    className="btn-ghost text-xs"
                  >
                    Xem
                  </button>
                  <button
                    onClick={() => onToggleActive(student)}
                    className={`btn-ghost text-xs ${
                      student.active ? 'text-red-400' : 'text-green-400'
                    }`}
                  >
                    {student.active ? 'Vô hiệu' : 'Kích hoạt'}
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
