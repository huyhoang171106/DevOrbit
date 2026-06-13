import { useEffect, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { m as motion } from 'framer-motion'
import { apiGet, apiStudentPost } from '../../lib/api'
import { isStudentAuthenticated } from '../../lib/auth'
import { RepoAiAnalysisSection } from '../../components/student/RepoAiAnalysisSection'
import { ReviewSection } from '../../components/student/ReviewSection'
import { VoteButtons } from '../../components/student/VoteButtons'
import { useRepoSocialInfo } from '../../hooks/useCommunity'
import type { RepoSummary } from '../../types/api'
import { analyzeRepository, type RepoAnalysisResult } from '../../lib/repoAnalysisService'
import { ArrowLeft, Code, Star, ArrowSquareOut, WarningCircle, GithubLogo, Bookmark, BookmarkSimple } from '@phosphor-icons/react'

type GithubRepositoryMetadata = {
  default_branch?: string | null
  pushed_at?: string | null
  updated_at?: string | null
}

type GithubCommitResponse = Array<{
  commit?: {
    committer?: { date?: string | null } | null
    author?: { date?: string | null } | null
  } | null
}>

const LAST_PUSHED_AT_CACHE_PREFIX = 'devorbit:lastPushedAt:'

export function RepoDetailPage() {
  const { repoId } = useParams<{ repoId: string }>()
  const navigate = useNavigate()
  const [repo, setRepo] = useState<RepoSummary | null>(null)
  const [analysis, setAnalysis] = useState<RepoAnalysisResult | null>(null)
  const [analysisLoading, setAnalysisLoading] = useState(false)
  const [analysisError, setAnalysisError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [bookmarked, setBookmarked] = useState(false)
  const [bookmarking, setBookmarking] = useState(false)
  const [bookmarkError, setBookmarkError] = useState<string | null>(null)

  async function toggleBookmark() {
    if (!isStudentAuthenticated()) {
      navigate('/student/login')
      return
    }
    if (!repo || bookmarking) return
    setBookmarkError(null)
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
    } catch (e) {
      setBookmarkError(e instanceof Error ? e.message : 'Đánh dấu thất bại, vui lòng thử lại')
    } finally {
      setBookmarking(false)
    }
  }

  const { data: socialInfo, refetch: refetchSocial } = useRepoSocialInfo(Number(repoId))

  useEffect(() => {
    if (!repoId) return
    setLoading(true)
    setAnalysis(null)
    setAnalysisError(null)

    apiGet<RepoSummary>(`/api/repos/${repoId}`)
      .then(async (repoData) => {
        const hydratedRepo = await hydrateLastPushedAt(repoData)
        setRepo(hydratedRepo)
        setLoading(false)
        setAnalysisLoading(true)
        analyzeRepository(hydratedRepo)
          .then((result) => {
            setAnalysis(result)
            setAnalysisError(result.errorMessage ?? null)
          })
          .catch((analysisErr) => {
            console.error(analysisErr)
            setAnalysisError('Không thể tạo phân tích repository từ dữ liệu hiện có.')
          })
          .finally(() => setAnalysisLoading(false))
      })
      .catch((err) => {
        console.error(err)
        setError('Không thể tải dữ liệu repository.')
        setLoading(false)
      })
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
          transition={{ type: 'spring', stiffness: 300, damping: 30 }}
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
          transition={{ type: 'spring', stiffness: 300, damping: 30 }}
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

              {repo.stars !== null && (
                <div className="flex flex-col items-end">
                  <div className={`flex items-center gap-2.5 px-5 py-3 rounded-2xl border ${repo.stars > 0 ? 'bg-amber-500/5 border-amber-500/20' : 'bg-orbit-surface border-orbit-border'}`}>
                    <Star className={`h-5 w-5 ${repo.stars > 0 ? 'text-amber-400' : 'text-orbit-text-muted/50'}`} weight="fill" />
                    <span className={`text-xl font-black tabular-nums ${repo.stars > 0 ? 'text-amber-300' : 'text-orbit-text-muted'}`}>
                      {repo.stars.toLocaleString('en-US')}
                    </span>
                  </div>
                  <span className="text-[10px] font-black uppercase tracking-[0.15em] text-orbit-text-muted/50 mt-2">Lượt yêu thích</span>
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
              {bookmarkError && (
                <p className="text-[11px] text-rose-400 font-medium">{bookmarkError}</p>
              )}
            </div>
          </div>

          {/* Social: Vote + Reviews */}
          {socialInfo && (
            <div className="orbit-card p-6 md:p-8">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-[13px] font-bold text-orbit-text">Bình chọn & Đánh giá</h3>
                {/* TODO: Use socialInfo.userVote for initialVote when backend supports it */}
                <VoteButtons
                  repoId={Number(repoId)}
                  initialScore={socialInfo.voteScore}
                  initialVote={0}
                  onVoteChanged={() => refetchSocial()}
                />
              </div>
              <ReviewSection
                averageRating={socialInfo.averageRating}
                reviews={socialInfo.reviews}
                targetType="REPO"
                targetId={Number(repoId)}
                onReviewChanged={() => refetchSocial()}
              />
            </div>
          )}

          <RepoAiAnalysisSection repo={repo} analysis={analysis} loading={analysisLoading} error={analysisError} />
        </motion.div>
      </div>
    </div>
  )
}

export async function hydrateLastPushedAt(repo: RepoSummary): Promise<RepoSummary> {
  if (repo.lastPushedAt) {
    writeCachedLastPushedAt(repo, repo.lastPushedAt)
    return repo
  }

  const cachedLastPushedAt = readCachedLastPushedAt(repo)
  if (cachedLastPushedAt) return { ...repo, lastPushedAt: cachedLastPushedAt }

  const slug = parseGithubSlug(repo.githubUrl)
  if (!slug) return repo

  try {
    const metadata = await fetchGithubJson<GithubRepositoryMetadata>(
      `https://api.github.com/repos/${slug.owner}/${slug.name}`,
    )
    const latestCommitDate = await fetchLatestGithubCommitDate(slug.owner, slug.name, metadata.default_branch).catch(() => null)
    const lastPushedAt = latestCommitDate || metadata.pushed_at || metadata.updated_at || null
    if (!lastPushedAt) return repo
    writeCachedLastPushedAt(repo, lastPushedAt)
    return { ...repo, lastPushedAt }
  } catch {
    return repo
  }
}

async function fetchLatestGithubCommitDate(owner: string, name: string, defaultBranch?: string | null): Promise<string | null> {
  const branchQuery = defaultBranch ? `?sha=${encodeURIComponent(defaultBranch)}&per_page=1` : '?per_page=1'
  const commits = await fetchGithubJson<GithubCommitResponse>(
    `https://api.github.com/repos/${owner}/${name}/commits${branchQuery}`,
  )
  const commit = commits[0]?.commit
  return commit?.committer?.date || commit?.author?.date || null
}

async function fetchGithubJson<T>(url: string): Promise<T> {
  const response = await fetch(url, {
    headers: {
      Accept: 'application/vnd.github+json',
    },
  })
  if (!response.ok) throw new Error(`GitHub request failed: ${response.status}`)
  return response.json() as Promise<T>
}

export function parseGithubSlug(url: string): { owner: string; name: string } | null {
  const match = url.match(/github\.com\/([^/]+)\/([^/?#]+)/i)
  if (!match) return null
  return {
    owner: match[1],
    name: match[2].replace(/\.git$/i, ''),
  }
}

function readCachedLastPushedAt(repo: RepoSummary): string | null {
  try {
    const value = window.localStorage.getItem(lastPushedAtCacheKey(repo))
    return isValidDateString(value) ? value : null
  } catch {
    return null
  }
}

function writeCachedLastPushedAt(repo: RepoSummary, value: string): void {
  if (!isValidDateString(value)) return
  try {
    window.localStorage.setItem(lastPushedAtCacheKey(repo), value)
  } catch {
    // Browser storage can be disabled; API/DB data remains the source of truth.
  }
}

function lastPushedAtCacheKey(repo: RepoSummary): string {
  return `${LAST_PUSHED_AT_CACHE_PREFIX}${repo.id}:${repo.githubUrl}`
}

function isValidDateString(value: string | null | undefined): value is string {
  return Boolean(value && !Number.isNaN(new Date(value).getTime()))
}
