import type { RepoReviewAdmin } from '../../../types/admin'

interface RepoReviewTableProps {
  reviews: RepoReviewAdmin[]
  onDelete: (id: number) => void
}

export function RepoReviewTable({ reviews, onDelete }: RepoReviewTableProps) {
  if (reviews.length === 0) {
    return (
      <div className="glass-card p-8 text-center">
        <p className="text-ink-secondary">Không có đánh giá repo</p>
      </div>
    )
  }

  return (
    <div className="glass-card overflow-hidden border border-orbit-border hover:border-orbit-accent/20 shadow-glow transition-all duration-500">
      <table className="w-full">
        <thead>
          <tr className="border-b border-orbit-border bg-orbit-surface/50">
            <th className="px-4 py-3 text-center text-xs font-black tracking-[0.12em] text-orbit-text uppercase">Sinh viên</th>
            <th className="px-4 py-3 text-center text-xs font-black tracking-[0.12em] text-orbit-text uppercase">Repo</th>
            <th className="px-4 py-3 text-center text-xs font-black tracking-[0.12em] text-orbit-text uppercase">Đánh giá</th>
            <th className="px-4 py-3 text-center text-xs font-black tracking-[0.12em] text-orbit-text uppercase">Nhận xét</th>
            <th className="px-4 py-3 text-center text-xs font-black tracking-[0.12em] text-orbit-text uppercase">Ngày</th>
            <th className="px-4 py-3 text-center text-xs font-black tracking-[0.12em] text-orbit-text uppercase">Thao tác</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-orbit-border">
          {reviews.map((review) => (
            <tr key={review.id} className="transition-colors hover:bg-orbit-surface/30">
              <td className="px-4 py-3 text-sm text-orbit-text text-center">{review.studentName}</td>
              <td className="px-4 py-3 text-sm text-orbit-text text-center">{review.repoName}</td>
              <td className="px-4 py-3 text-sm text-center">
                <span className="text-yellow-400">{'★'.repeat(review.rating)}{'☆'.repeat(5 - review.rating)}</span>
              </td>
              <td className="px-4 py-3 text-sm text-ink-secondary text-center max-w-sm break-words">{review.comment}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary text-center">{new Date(review.createdAt).toLocaleDateString()}</td>
              <td className="px-4 py-3 text-sm text-center">
                <div className="flex items-center justify-center gap-2">
                  <button onClick={() => onDelete(review.id)} className="btn-ghost text-xs text-red-400">Xoá</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
