import type { CourseReviewAdmin } from '../../../types/admin'

interface CourseReviewTableProps {
  reviews: CourseReviewAdmin[]
  onDelete: (id: number) => void
}

export function CourseReviewTable({ reviews, onDelete }: CourseReviewTableProps) {
  if (reviews.length === 0) {
    return (
      <div className="glass-card p-8 text-center">
        <p className="text-ink-secondary">Không có đánh giá môn học</p>
      </div>
    )
  }

  return (
    <div className="glass-card overflow-hidden border border-orbit-border">
      <table className="w-full">
        <thead>
          <tr className="border-b border-orbit-border bg-orbit-surface/50">
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Sinh viên</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Môn học</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Đánh giá</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Nhận xét</th>
            <th className="px-4 py-3 text-left text-xs font-medium text-ink-secondary uppercase">Ngày</th>
            <th className="px-4 py-3 text-right text-xs font-medium text-ink-secondary uppercase">Thao tác</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-clay-border">
          {reviews.map((review) => (
            <tr key={review.id} className="transition-colors hover:bg-orbit-surface/30">
              <td className="px-4 py-3 text-sm text-ink-primary">{review.studentName}</td>
              <td className="px-4 py-3 text-sm text-ink-primary">{review.courseName}</td>
              <td className="px-4 py-3 text-sm">
                <span className="text-yellow-400">{'★'.repeat(review.rating)}{'☆'.repeat(5 - review.rating)}</span>
              </td>
              <td className="px-4 py-3 text-sm text-ink-secondary max-w-xs truncate">{review.comment}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary">{new Date(review.createdAt).toLocaleDateString()}</td>
              <td className="px-4 py-3 text-sm text-right">
                <button onClick={() => onDelete(review.id)} className="btn-ghost text-xs text-red-400">Xoá</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
