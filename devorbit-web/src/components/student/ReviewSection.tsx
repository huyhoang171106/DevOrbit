import { useState } from 'react'
import { Star, Spinner } from '@phosphor-icons/react'
import { isStudentAuthenticated } from '../../lib/auth'
import { apiStudentPost } from '../../lib/api'
import { useNavigate } from 'react-router-dom'
import type { ReviewResponse } from '../../types/api'

type ReviewSectionProps = {
  averageRating: number
  reviews: ReviewResponse[]
  targetType: 'COURSE' | 'REPO'
  targetId: number
  onReviewChanged: () => void
  loading?: boolean
}

function StarRating({
  value,
  onChange,
  interactive,
  size,
}: {
  value: number
  onChange?: (v: number) => void
  interactive?: boolean
  size?: string
}) {
  const [hovered, setHovered] = useState(0)
  const starSize = size ?? 'h-5 w-5'

  return (
    <div className="flex items-center gap-0.5">
      {[1, 2, 3, 4, 5].map((star) => {
        const fill = star <= (hovered || value) ? '#34d399' : '#27272a'
        return (
          <button
            key={star}
            type="button"
            disabled={!interactive}
            onClick={() => onChange?.(star)}
            onMouseEnter={() => interactive && setHovered(star)}
            onMouseLeave={() => setHovered(0)}
            className={`${interactive ? 'cursor-pointer hover:scale-110' : 'cursor-default'} transition-transform`}
          >
            <Star className={starSize} weight={star <= (hovered || value) ? 'fill' : 'regular'} style={{ color: fill }} />
          </button>
        )
      })}
    </div>
  )
}

function ReviewForm({
  targetType,
  targetId,
  onSubmitDone,
}: {
  targetType: string
  targetId: number
  onSubmitDone: () => void
}) {
  const [rating, setRating] = useState(5)
  const [comment, setComment] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState<string | null>(null)

  const handleSubmit = async () => {
    setSubmitError(null)
    setSubmitting(true)
    try {
      const endpoint = targetType === 'COURSE'
        ? `/api/student/courses/${targetId}/review`
        : `/api/student/repos/${targetId}/review`
      await apiStudentPost(endpoint, { rating, comment })
      onSubmitDone()
      setComment('')
    } catch (e) {
      setSubmitError(e instanceof Error ? e.message : 'Gửi đánh giá thất bại, vui lòng thử lại')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <span className="text-[12px] font-bold text-orbit-text">Đánh giá của bạn</span>
        <StarRating value={rating} onChange={setRating} interactive size="h-4 w-4" />
      </div>
      <textarea
        value={comment}
        onChange={(e) => setComment(e.target.value)}
        placeholder="Chia sẻ nhận xét của bạn..."
        rows={3}
        maxLength={5000}
        className="w-full bg-orbit-surface border border-orbit-border rounded-xl px-3 py-2 text-[13px] text-orbit-text placeholder:text-orbit-text-muted outline-none focus:border-orbit-accent/40 transition-colors resize-none"
      />
      {submitError && (
        <p className="text-[11px] text-rose-400 font-medium">{submitError}</p>
      )}
      <button
        onClick={handleSubmit}
        disabled={submitting}
        className="w-full py-2 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 text-[12px] font-bold text-orbit-accent hover:bg-orbit-accent/20 transition-colors disabled:opacity-30 disabled:cursor-not-allowed flex items-center justify-center gap-2"
      >
        {submitting && <Spinner className="h-3.5 w-3.5 animate-spin" />}
        Gửi đánh giá
      </button>
    </div>
  )
}

export function ReviewSection({ averageRating, reviews, targetType, targetId, onReviewChanged, loading }: ReviewSectionProps) {
  const authenticated = isStudentAuthenticated()
  const navigate = useNavigate()
  const [showForm, setShowForm] = useState(false)

  return (
    <div className="orbit-card p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-[13px] font-bold text-orbit-text flex items-center gap-2">
          <Star className="h-4 w-4 text-orbit-accent" weight="fill" />
          Đánh giá
        </h3>
        <div className="flex items-center gap-2">
          <span className="text-[22px] font-black text-orbit-text tabular-nums">
            {averageRating > 0 ? averageRating.toFixed(1) : '—'}
          </span>
          <div className="flex flex-col items-start">
            <StarRating value={Math.round(averageRating)} size="h-3 w-3" />
            <span className="text-[9px] text-orbit-text-muted">{reviews.length} lượt</span>
          </div>
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center py-6">
          <Spinner className="h-5 w-5 text-orbit-accent animate-spin" />
        </div>
      ) : (
        <div className="space-y-4">
          {reviews.length > 0 && (
            <div className="space-y-3 max-h-[300px] overflow-y-auto scrollbar-thin">
              {reviews.map((review) => (
                <div key={review.id} className="flex gap-3">
                  <div className="shrink-0 h-8 w-8 rounded-full bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center">
                    <span className="text-[10px] font-bold text-orbit-accent">
                      {review.studentName.charAt(0).toUpperCase()}
                    </span>
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="text-[12px] font-bold text-orbit-text">{review.studentName}</span>
                      <StarRating value={review.rating} size="h-3 w-3" />
                      <span className="text-[9px] text-orbit-text-muted">
                        {new Date(review.updatedAt).toLocaleDateString('vi-VN')}
                      </span>
                    </div>
                    {review.comment && (
                      <p className="text-[13px] text-orbit-text-secondary mt-0.5 leading-relaxed">{review.comment}</p>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}

          {authenticated ? (
            showForm ? (
              <ReviewForm
                targetType={targetType}
                targetId={targetId}
                onSubmitDone={() => { setShowForm(false); onReviewChanged() }}
              />
            ) : (
              <button
                onClick={() => setShowForm(true)}
                className="w-full py-2 rounded-xl border border-dashed border-orbit-border text-[12px] font-bold text-orbit-text-muted hover:text-orbit-accent hover:border-orbit-accent/30 transition-colors"
              >
                + Viết đánh giá
              </button>
            )
          ) : (
            <button
              onClick={() => navigate('/student/login')}
              className="w-full py-2 rounded-xl border border-dashed border-orbit-border text-[12px] font-bold text-orbit-text-muted hover:text-orbit-accent hover:border-orbit-accent/30 transition-colors"
            >
              Đăng nhập để đánh giá
            </button>
          )}
        </div>
      )}
    </div>
  )
}
