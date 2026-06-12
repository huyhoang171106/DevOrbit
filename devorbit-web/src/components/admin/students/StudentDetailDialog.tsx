import type { AdminStudent } from '../../../types/admin'

interface StudentDetailDialogProps {
  open: boolean
  student: AdminStudent | null
  onClose: () => void
}

export function StudentDetailDialog({ open, student, onClose }: StudentDetailDialogProps) {
  if (!open || !student) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="glass-card w-full max-w-md p-6 shadow-2xl">
        <div className="flex items-center justify-between mb-6">
          <h2 className="heading-5 text-ink-primary">Thông tin sinh viên</h2>
          <button onClick={onClose} className="text-ink-secondary hover:text-ink-primary transition-colors">
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div className="space-y-3">
          <div className="flex justify-between py-2 border-b border-orbit-border/50">
            <span className="text-sm text-ink-secondary">Mã số</span>
            <span className="text-sm text-ink-primary font-medium">{student.studentCode}</span>
          </div>
          <div className="flex justify-between py-2 border-b border-orbit-border/50">
            <span className="text-sm text-ink-secondary">Họ tên</span>
            <span className="text-sm text-ink-primary">{student.fullName}</span>
          </div>
          <div className="flex justify-between py-2 border-b border-orbit-border/50">
            <span className="text-sm text-ink-secondary">Email</span>
            <span className="text-sm text-ink-primary">{student.email}</span>
          </div>
          <div className="flex justify-between py-2 border-b border-orbit-border/50">
            <span className="text-sm text-ink-secondary">Trạng thái</span>
            <span className={`text-sm font-medium ${student.active ? 'text-green-400' : 'text-red-400'}`}>
              {student.active ? 'Hoạt động' : 'Ngừng hoạt động'}
            </span>
          </div>
          <div className="flex justify-between py-2">
            <span className="text-sm text-ink-secondary">Xác thực email</span>
            <span className={`text-sm font-medium ${student.emailVerified ? 'text-green-400' : 'text-ink-muted'}`}>
              {student.emailVerified ? 'Có' : 'Không'}
            </span>
          </div>
        </div>
        <div className="flex justify-end mt-6">
          <button onClick={onClose} className="btn-primary text-sm">Đóng</button>
        </div>
      </div>
    </div>
  )
}
