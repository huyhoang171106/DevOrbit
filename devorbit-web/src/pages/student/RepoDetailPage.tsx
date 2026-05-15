import { useEffect, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { apiGet, apiStudentPost } from '../../lib/api'
import { isStudentAuthenticated } from '../../lib/auth'
import type { RepoSummary, AiResponse } from '../../types/api'
import { ArrowLeft, Code, Star, MagicWand, GraduationCap, ArrowSquareOut, WarningCircle, GithubLogo, Bookmark, BookmarkSimple } from '@phosphor-icons/react'

export function RepoDetailPage() {
  const { repoId } = useParams<{ repoId: string }>()
  const navigate = useNavigate()
  const [repo, setRepo] = useState<RepoSummary | null>(null)
  const [summary, setSummary] = useState<AiResponse | null>(null)
  const [advice, setAdvice] = useState<AiResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [bookmarked, setBookmarked] = useState(false)
  const [bookmarking, setBookmarking] = useState(false)

  async function toggleBookmark() {
    if (!isStudentAuthenticated()) {
      navigate('/student/login')
      return
    }
    if (!repo || bookmarking) return
    setBookmarking(true)
    try {
      if (!bookmarked) {
        await apiStudentPost('/api/student/bookmarks', {
          targetType: 'REPO',
          targetId: repo.id,
          title: repo.displayName,
          subtitle: repo.description?.slice(0, 100),
          url: `/repos/${repo.id}`,
        })
        setBookmarked(true)
      }
    } catch {
      // silently fail
    } finally {
      setBookmarking(false)
    }
  }

  useEffect(() => {
    if (!repoId) return
    setLoading(true)

    Promise.all([
      apiGet<RepoSummary>(`/api/repos/${repoId}`),
      apiGet<AiResponse>(`/api/ai/repo/${repoId}/summary`).catch(() => null),
      apiGet<AiResponse>(`/api/ai/repo/${repoId}/advice`).catch(() => null),
    ])
      .then(([repoData, summaryData, adviceData]) => {
        setRepo(repoData)
        setSummary(summaryData)
        setAdvice(adviceData)
      })
      .catch((err) => {
        console.error(err)
        setError('Không thể tải dữ liệu repository.')
      })
      .finally(() => setLoading(false))
  }, [repoId])

  if (loading) {
    return (
      <div className="relative min-h-[80vh] flex items-center justify-center">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[400px] h-[400px] bg-orbit-accent/5 blur-[120px] rounded-full animate-pulse-soft" />
        <div className="relative flex flex-col items-center gap-6">
          <div className="relative h-16 w-16">
            <div className="absolute inset-0 rounded-full border-2 border-orbit-accent/10" />
            <div className="absolute inset-0 rounded-full border-t-2 border-orbit-accent animate-spin shadow-[0_0_20px_rgba(52,211,153,0.2)]" />
          </div>
          <div className="flex flex-col items-center gap-2">
            <p className="text-[11px] font-black text-orbit-accent tracking-[0.3em] uppercase">Đang phân tích</p>
            <p className="text-[14px] font-bold text-orbit-text-secondary animate-pulse-soft">Đồng bộ hóa tài nguyên</p>
          </div>
        </div>
      </div>
    )
  }

  if (error || !repo) {
    return (
      <div className="flex flex-col items-center justify-center py-32 text-center px-6">
        <div className="orbit-card p-12 md:p-16 max-w-md">
          <div className="h-16 w-16 rounded-2xl bg-rose-500/10 border border-rose-500/20 flex items-center justify-center mx-auto mb-8">
            <WarningCircle className="h-8 w-8 text-rose-500" weight="duotone" />
          </div>
          <h2 className="heading-4 mb-4 text-orbit-text">Không tìm thấy tài nguyên</h2>
          <p className="body-md text-[14px] mb-8">
            {error || 'Tài nguyên học thuật không tồn tại trong hệ thống.'}
          </p>
          <Link to="/courses" className="btn-primary">
            <ArrowLeft className="h-4 w-4" weight="bold" />
            Quay lại danh mục
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="relative w-full min-h-screen pb-32">
      {/* Background */}
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute top-[-10%] right-[-10%] w-[45%] h-[600px] bg-orbit-accent/5 blur-[150px] rounded-full" />
        <div className="absolute bottom-[10%] left-[-10%] w-[35%] h-[500px] bg-indigo-500/3 blur-[120px] rounded-full" />
      </div>

      <div className="relative z-10 max-w-[1000px] mx-auto px-6 pt-12 md:pt-20">
        {/* Back link */}
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ type: 'spring', stiffness: 100, damping: 20 }}
          className="mb-10"
        >
          <Link
            to={repo.courseId ? `/courses/${repo.courseId}` : '/courses'}
            className="inline-flex items-center gap-3 text-[11px] font-bold uppercase tracking-[0.15em] text-orbit-text-muted hover:text-orbit-accent transition-all group"
          >
            <div className="h-8 w-8 rounded-2xl border border-orbit-border flex items-center justify-center group-hover:border-orbit-accent/30 group-hover:bg-orbit-accent/5 transition-all">
              <ArrowLeft className="h-4 w-4 transition-transform group-hover:-translate-x-0.5" weight="bold" />
            </div>
            Quay lại
          </Link>
        </motion.div>

        <motion.div
          className="space-y-8"
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ type: 'spring', stiffness: 100, damping: 20 }}
        >
          {/* Main card */}
          <div className="orbit-card-glow p-8 md:p-12">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-8 mb-10">
              <div className="space-y-5">
                <span className="badge inline-flex">
                  <Code className="h-3 w-3" weight="fill" />
                  Repository
                </span>
                <h1 className="display-md">{repo.displayName}</h1>
              </div>

              {repo.stars !== null && repo.stars > 0 && (
                <div className="flex flex-col items-end">
                  <div className="flex items-center gap-2.5 px-5 py-3 rounded-2xl bg-amber-500/5 border border-amber-500/20">
                    <Star className="h-5 w-5 text-amber-400" weight="fill" />
                    <span className="text-xl font-black text-amber-300 tabular-nums">{repo.stars}</span>
                  </div>
                  <span className="text-[10px] font-black uppercase tracking-[0.15em] text-amber-500/60 mt-2">Lượt yêu thích</span>
                </div>
              )}
            </div>

            <div className="max-w-3xl mb-10">
              <p className="body-lg text-[16px] leading-relaxed text-orbit-text-secondary">
                {repo.description || 'Triển khai toàn diện các khái niệm môn học và các mô-đun học thuật tiêu chuẩn.'}
              </p>
            </div>

            <div className="flex flex-wrap items-center gap-3 mb-10">
              {repo.primaryLanguage && (
                <span className="inline-flex items-center gap-2 px-4 py-2 rounded-2xl bg-orbit-surface border border-orbit-border">
                  <span className="h-2 w-2 rounded-full bg-orbit-accent shadow-[0_0_5px_rgba(52,211,153,0.5)]" />
                  <span className="text-[11px] font-bold text-orbit-text-secondary">{repo.primaryLanguage}</span>
                </span>
              )}
              {repo.techStacks.map((stack) => (
                <span
                  key={stack}
                  className="px-4 py-2 rounded-2xl bg-orbit-accent-subtle border border-orbit-accent/20 text-[10px] font-bold uppercase tracking-widest text-orbit-accent/80"
                >
                  {stack}
                </span>
              ))}
            </div>

            <div className="flex flex-wrap items-center gap-3">
              <a
                href={repo.githubUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="btn-primary text-[12px] inline-flex"
              >
                <GithubLogo className="h-4 w-4" weight="fill" />
                Truy cập mã nguồn
                <ArrowSquareOut className="h-4 w-4" weight="bold" />
              </a>
              <button
                onClick={toggleBookmark}
                disabled={bookmarking}
                className={`btn-secondary text-[12px] inline-flex ${bookmarked ? 'border-emerald-500/30 bg-emerald-500/5' : ''}`}
              >
                {bookmarked ? (
                  <BookmarkSimple className="h-4 w-4 text-emerald-400" weight="fill" />
                ) : (
                  <Bookmark className="h-4 w-4" weight="regular" />
                )}
                {bookmarked ? 'Đã đánh dấu' : 'Đánh dấu'}
              </button>
            </div>
          </div>

          {/* AI Cards */}
          <div className="grid md:grid-cols-2 gap-6">
            {summary && (
              <div className="orbit-card-glow p-8 md:p-10 border-orbit-accent/10 hover:border-orbit-accent/30 transition-all duration-500">
                <div className="h-12 w-12 rounded-2xl bg-orbit-accent/5 border border-orbit-accent/10 flex items-center justify-center mb-8">
                  <MagicWand className="h-6 w-6 text-orbit-accent" weight="duotone" />
                </div>
                <h3 className="heading-4 mb-6 text-orbit-text flex items-center gap-3">
                  Phân tích nội dung
                </h3>
                <p className="body-md text-[14px] leading-[1.8] text-orbit-text-secondary">
                  {summary.content}
                </p>
              </div>
            )}

            {advice && (
              <div className="orbit-card-glow p-8 md:p-10 border-indigo-500/10 hover:border-indigo-500/30 transition-all duration-500">
                <div className="h-12 w-12 rounded-2xl bg-indigo-500/5 border border-indigo-500/10 flex items-center justify-center mb-8">
                  <GraduationCap className="h-6 w-6 text-indigo-400" weight="duotone" />
                </div>
                <h3 className="heading-4 mb-6 text-orbit-text flex items-center gap-3">
                  Chiến lược học tập
                </h3>
                <p className="body-md text-[14px] leading-[1.8] text-orbit-text-secondary italic">
                  {advice.content}
                </p>
              </div>
            )}
          </div>
        </motion.div>
      </div>
    </div>
  )
}
