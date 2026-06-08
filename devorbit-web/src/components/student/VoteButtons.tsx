import { useState } from 'react'
import { CaretUp, CaretDown, Spinner } from '@phosphor-icons/react'
import { isStudentAuthenticated } from '../../lib/auth'
import { apiStudentPost } from '../../lib/api'
import { useNavigate } from 'react-router-dom'

type VoteButtonsProps = {
  repoId: number
  initialScore: number
  initialVote: number
  onVoteChanged: () => void
}

export function VoteButtons({ repoId, initialScore, initialVote, onVoteChanged }: VoteButtonsProps) {
  const navigate = useNavigate()
  const [vote, setVote] = useState(initialVote)
  const [score, setScore] = useState(initialScore)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleVote = async (value: number) => {
    if (!isStudentAuthenticated()) {
      navigate('/student/login')
      return
    }
    setError(null)
    const newVote = vote === value ? 0 : value
    setLoading(true)
    try {
      const res = await apiStudentPost<{ voteScore: number; voteValue: number }>(
        `/api/student/repos/${repoId}/vote`, { voteValue: newVote }
      )
      setVote(res.voteValue)
      setScore(res.voteScore)
      onVoteChanged()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Bình chọn thất bại, vui lòng thử lại')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex flex-col items-center gap-1">
    <div className="flex items-center gap-1">
      <button
        onClick={() => handleVote(1)}
        disabled={loading}
        className={`h-8 w-8 rounded-lg flex items-center justify-center transition-colors ${vote === 1 ? 'bg-orbit-accent/10 text-orbit-accent' : 'text-orbit-text-muted hover:text-orbit-accent hover:bg-orbit-accent/5'}`}
      >
        {loading ? <Spinner className="h-4 w-4 animate-spin" /> : <CaretUp className="h-5 w-5" weight={vote === 1 ? 'fill' : 'regular'} />}
      </button>
      <span className={`text-[13px] font-black tabular-nums min-w-[24px] text-center ${score > 0 ? 'text-orbit-accent' : score < 0 ? 'text-rose-400' : 'text-orbit-text-muted'}`}>
        {score}
      </span>
      <button
        onClick={() => handleVote(-1)}
        disabled={loading}
        className={`h-8 w-8 rounded-lg flex items-center justify-center transition-colors ${vote === -1 ? 'bg-rose-500/10 text-rose-400' : 'text-orbit-text-muted hover:text-rose-400 hover:bg-rose-500/5'}`}
      >
        <CaretDown className="h-5 w-5" weight={vote === -1 ? 'fill' : 'regular'} />
      </button>
    </div>
    {error && <p className="text-[10px] text-rose-400 font-medium text-center">{error}</p>}
  </div>
  )
}
