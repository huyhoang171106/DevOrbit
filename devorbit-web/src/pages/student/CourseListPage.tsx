import { useEffect, useState, useMemo, useRef, useCallback } from 'react'
import { apiGet } from '../../lib/api'
import { CourseCard } from '../../components/student/CourseCard'
import { Link } from 'react-router-dom'
import type { CourseSummary, RepoSummary } from '../../types/api'
import { searchCourses, searchRepos } from '../../lib/repoSearch'
import { MagnifyingGlass, Graph, Funnel, X, GraduationCap, BookOpen, CaretLeft, CaretRight, Code } from '@phosphor-icons/react'
import { BlurReveal, FadeReveal, StaggerReveal, StaggerItem, SectionTransition, ParallaxLayer } from '../../motion'

const PAGE_SIZE = 30
const MAX_REPO_RESULTS = 12

export function CourseListPage() {
  const [courses, setCourses] = useState<CourseSummary[]>([])
  const [allRepos, setAllRepos] = useState<RepoSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [reposLoading, setReposLoading] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [debouncedQuery, setDebouncedQuery] = useState('')
  const searchTimer = useRef<ReturnType<typeof setTimeout> | null>(null)
  const reposFetched = useRef(false)

  // Debounce search by 200ms to avoid re-filtering on every keystroke
  const handleSearchChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value
    setSearchQuery(val)
    if (searchTimer.current) clearTimeout(searchTimer.current)
    searchTimer.current = setTimeout(() => setDebouncedQuery(val), 200)
  }, [])

  useEffect(() => () => { if (searchTimer.current) clearTimeout(searchTimer.current) }, [])
  const [error, setError] = useState<string | null>(null)
  const [page, setPage] = useState(0)

  useEffect(() => {
    apiGet<CourseSummary[]>('/api/courses')
      .then(setCourses)
      .catch((err) => {
        console.error(err)
        setError('Không thể tải danh sách môn học. Vui lòng thử lại sau.')
      })
      .finally(() => setLoading(false))
  }, [])

  // Fetch all repos lazily when user first searches
  useEffect(() => {
    if (debouncedQuery.trim() && !reposFetched.current) {
      reposFetched.current = true
      setReposLoading(true)
      apiGet<RepoSummary[]>('/api/discovery/repos')
        .then(setAllRepos)
        .catch(() => {})
        .finally(() => setReposLoading(false))
    }
  }, [debouncedQuery])

  const matchedCourses = useMemo(() => {
    if (!debouncedQuery.trim()) return courses
    return searchCourses(courses, debouncedQuery)
  }, [courses, debouncedQuery])

  const matchedRepos = useMemo(() => {
    if (!debouncedQuery.trim() || allRepos.length === 0) return []
    return searchRepos(allRepos, debouncedQuery)
  }, [allRepos, debouncedQuery])

  const hasSearch = debouncedQuery.trim().length > 0
  const showRepos = hasSearch && matchedRepos.length > 0
  const showCourses = matchedCourses.length > 0
  const hasAnyResult = showCourses || showRepos
  const searchResultCount = matchedCourses.length + matchedRepos.length

  const sortedCourses = useMemo(() =>
    [...matchedCourses].sort((a, b) => b.repoCount - a.repoCount),
    [matchedCourses]
  )

  // Reset page on search
  useEffect(() => { setPage(0) }, [searchQuery])

  const totalPages = Math.ceil(sortedCourses.length / PAGE_SIZE)
  const paged = sortedCourses.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE)

  if (loading) {
    return (
      <div className="relative min-h-[80vh] flex items-center justify-center">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[500px] h-[500px] bg-orbit-accent/5 blur-[150px] rounded-full animate-pulse-soft" />
        <div className="relative flex flex-col items-center gap-8">
          <div className="relative h-16 w-16">
            <div className="absolute inset-0 rounded-full border-2 border-orbit-accent/10" />
            <div className="absolute inset-0 rounded-full border-t-2 border-orbit-accent animate-spin shadow-[0_0_20px_rgba(52,211,153,0.2)]" />
          </div>
          <div className="flex flex-col items-center gap-2">
            <p className="text-[11px] font-black text-orbit-accent tracking-[0.3em] uppercase">Đang đồng bộ</p>
            <p className="text-[15px] font-bold text-orbit-text-secondary animate-pulse-soft">Vũ trụ học thuật</p>
          </div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="relative min-h-[70vh] flex items-center justify-center px-6">
        <div className="orbit-card p-12 md:p-16 max-w-lg text-center">
          <div className="h-16 w-16 rounded-2xl bg-rose-500/10 border border-rose-500/20 flex items-center justify-center mx-auto mb-8">
            <X className="h-8 w-8 text-rose-500" weight="duotone" />
          </div>
          <h2 className="heading-4 mb-4 text-orbit-text">Không thể tải dữ liệu</h2>
          <p className="body-md mb-8">{error}</p>
          <button onClick={() => window.location.reload()} className="btn-primary">
            Thử lại
          </button>
        </div>
      </div>
    )
  }

  return (
    <SectionTransition atmosphere="light" className="relative w-full min-h-screen pb-32">
      {/* Background ambient with parallax */}
      <ParallaxLayer speed={0.15} range={100}>
        <div className="fixed inset-0 pointer-events-none z-0">
          <div className="absolute top-[10%] left-[-10%] w-[40%] h-[600px] bg-orbit-accent/5 blur-[180px] rounded-full" />
          <div className="absolute bottom-[20%] right-[-10%] w-[35%] h-[500px] bg-emerald-500/3 blur-[150px] rounded-full" />
        </div>
      </ParallaxLayer>

      <div className="relative z-10 w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12 py-16 md:py-24">
        {/* ─── HEADER ─── */}
        <header className="mb-20">
          <div className="max-w-3xl">
            <BlurReveal blur={8} duration={0.7}>
              <span className="section-label mb-8 inline-flex">
                <GraduationCap className="h-3 w-3" weight="fill" />
                Danh mục môn học trực tuyến
              </span>
            </BlurReveal>

            <BlurReveal blur={6} delay={0.1} y={0}>
              <h1 className="display-lg mt-6 mb-6 leading-[1.05]">
                Thư viện{' '}
                <span className="text-orbit-accent relative inline-block">
                  Repo
                  <span className="absolute -bottom-2 left-0 w-full h-1 bg-orbit-accent/20 rounded-full blur-[2px]" />
                </span>{' '}
                học tập
              </h1>
            </BlurReveal>

            <FadeReveal y={16} delay={0.2}>
              <p className="body-lg text-[17px] md:text-[18px] leading-relaxed max-w-2xl mb-10">
                Khám phá hệ&nbsp;sinh thái kiến&nbsp;thức SE - UIT. Tìm kiếm các repository chuyên&nbsp;sâu,
                sơ&nbsp;đồ mạng&nbsp;lưới tương&nbsp;tác và kinh&nbsp;nghiệm từ bạn bè cho mọi môn&nbsp;học trong chương&nbsp;trình.
              </p>
            </FadeReveal>
          </div>

          {/* Search + CTA row */}
          <div className="flex flex-col lg:flex-row gap-5 items-stretch lg:items-center">
            <div className="relative flex-1 max-w-xl group">
              <div className="absolute -inset-1 bg-gradient-to-r from-orbit-accent/20 to-emerald-500/20 rounded-3xl blur opacity-0 group-focus-within:opacity-100 transition duration-500" />
              <div className="relative flex items-center">
                <MagnifyingGlass className="absolute left-5 h-5 w-5 text-orbit-text-muted group-focus-within:text-orbit-accent transition-colors duration-300" weight="regular" />
                <input
                  type="text"
                  placeholder="Tìm kiếm môn học và repo theo tên, mã, ngôn ngữ..."
                  value={searchQuery}
                  onChange={handleSearchChange}
                  className="w-full bg-orbit-surface/80 backdrop-blur-xl border border-orbit-border rounded-3xl py-5 pl-14 pr-14 text-orbit-text placeholder:text-orbit-text-muted/50 focus:outline-none focus:border-orbit-accent/40 focus:ring-4 focus:ring-orbit-accent/5 transition-all text-[15px]"
                />
                {searchQuery && (
                  <button
                    onClick={() => { setSearchQuery(''); setDebouncedQuery(''); }}
                    className="absolute right-5 h-8 w-8 rounded-full bg-orbit-elevated border border-orbit-border flex items-center justify-center text-orbit-text-muted hover:text-orbit-text hover:border-orbit-accent/30 transition-all"
                  >
                    <X className="h-4 w-4" weight="bold" />
                  </button>
                )}
              </div>
            </div>

            <Link
              to="/knowledge-graph"
              className="btn-secondary group shrink-0 text-[12px] px-8 py-5 will-change-transform"
            >
              <Graph className="h-5 w-5" weight="regular" />
              Sơ đồ kiến thức
            </Link>
          </div>
        </header>

        {/* ─── RESULT COUNT ─── */}
        <div className="flex items-center justify-between mb-12 pb-6 border-b border-orbit-border/50">
          <div className="flex items-center gap-3">
            <span className="h-6 w-1 bg-orbit-accent rounded-full" />
            <h2 className="text-[15px] font-bold text-orbit-text">{hasSearch ? 'Kết quả tìm kiếm' : 'Danh sách môn học'}</h2>
          </div>
          <div className="px-4 py-2 rounded-full bg-orbit-surface border border-orbit-border text-[10px] font-black uppercase tracking-widest text-orbit-text-muted tabular-nums">
            <Funnel className="h-3 w-3 inline-block mr-2" weight="regular" />
            {hasSearch ? `${searchResultCount} kết quả` : `${sortedCourses.length} môn học`}
          </div>
        </div>

        {/* ─── COURSE SECTION ─── */}
        {showCourses && (
          <div className="mb-14">
            <div className="flex items-center gap-3 mb-8">
              <GraduationCap className="h-5 w-5 text-orbit-accent" weight="duotone" />
              <h3 className="text-[14px] font-black text-orbit-text uppercase tracking-[0.12em]">Môn học phù hợp</h3>
              <span className="px-2.5 py-0.5 rounded-full bg-orbit-accent/10 border border-orbit-accent/15 text-[10px] font-bold text-orbit-accent">{matchedCourses.length}</span>
            </div>
            <StaggerReveal stagger={0.04} y={20}>
              <div className="grid gap-6 md:grid-cols-2 xl:grid-cols-3">
                {paged.map((c) => (
                  <StaggerItem key={c.id}>
                    <CourseCard course={c} />
                  </StaggerItem>
                ))}
              </div>
            </StaggerReveal>

            {/* ─── PAGINATION ─── */}
            {totalPages > 1 && (
              <div className="mt-12 flex items-center justify-center gap-3">
                <button
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="btn-secondary px-4 py-3 disabled:opacity-30"
                >
                  <CaretLeft className="h-4 w-4" weight="bold" />
                </button>

                {Array.from({ length: Math.min(totalPages, 7) }, (_, i) => {
                  let pageNum: number
                  if (totalPages <= 7) {
                    pageNum = i
                  } else if (page < 4) {
                    pageNum = i
                  } else if (page > totalPages - 5) {
                    pageNum = totalPages - 7 + i
                  } else {
                    pageNum = page - 3 + i
                  }
                  return (
                    <button
                      key={pageNum}
                      onClick={() => setPage(pageNum)}
                      className={`px-4 py-3 rounded-xl text-[12px] font-bold transition-all ${
                        pageNum === page
                          ? 'bg-orbit-accent text-white shadow-glow'
                          : 'bg-orbit-surface border border-orbit-border text-orbit-text-muted hover:text-orbit-text'
                      }`}
                    >
                      {pageNum + 1}
                    </button>
                  )
                })}

                <button
                  onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                  disabled={page >= totalPages - 1}
                  className="btn-secondary px-4 py-3 disabled:opacity-30"
                >
                  <CaretRight className="h-4 w-4" weight="bold" />
                </button>
              </div>
            )}
          </div>
        )}

        {/* ─── REPO SECTION ─── */}
        {showRepos && (
          <div className="mb-14">
            <div className="flex items-center gap-3 mb-8">
              <Code className="h-5 w-5 text-indigo-400" weight="duotone" />
              <h3 className="text-[14px] font-black text-orbit-text uppercase tracking-[0.12em]">Repo phù hợp</h3>
              <span className="px-2.5 py-0.5 rounded-full bg-indigo-500/10 border border-indigo-500/15 text-[10px] font-bold text-indigo-400">{matchedRepos.length}</span>
            </div>
            <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
              {matchedRepos.slice(0, MAX_REPO_RESULTS).map((r) => (
                <RepoSearchCard key={r.id} repo={r} />
              ))}
            </div>
            {reposLoading && (
              <div className="mt-6 flex items-center justify-center gap-3 text-orbit-text-muted">
                <div className="h-3 w-3 rounded-full border border-orbit-accent/30 border-t-transparent animate-spin" />
                <span className="text-[12px]">Đang tải thêm dữ liệu repo...</span>
              </div>
            )}
          </div>
        )}

        {/* ─── EMPTY STATE ─── */}
        {!hasAnyResult && !loading && (
          <div className="orbit-card p-16 md:p-24 text-center border-dashed border-2 border-orbit-accent/10">
            <div className="h-20 w-20 rounded-2xl bg-orbit-surface border border-orbit-border flex items-center justify-center mx-auto mb-8">
              {hasSearch ? <MagnifyingGlass className="h-10 w-10 text-orbit-text-muted" weight="light" /> : <BookOpen className="h-10 w-10 text-orbit-text-muted" weight="light" />}
            </div>
            <h3 className="heading-4 mb-4 text-orbit-text">
              {hasSearch ? 'Không tìm thấy kết quả' : 'Hệ thống trống'}
            </h3>
            <p className="body-md text-[14px] max-w-md mx-auto leading-relaxed mb-8">
              {hasSearch
                ? `Chúng tôi không tìm thấy môn học hoặc repo nào khớp với "${searchQuery}".`
                : 'Ma trận học thuật hiện đang được đồng bộ hóa. Vui lòng quay lại sau vài phút.'
              }
            </p>
            {hasSearch && (
              <button
                onClick={() => setSearchQuery('')}
                className="btn-primary text-[12px]"
              >
                <X className="h-4 w-4" weight="bold" />
                Xoá bộ lọc
              </button>
            )}
          </div>
        )}
      </div>
    </SectionTransition>
  )
}

function RepoSearchCard({ repo }: { repo: RepoSummary }) {
  const stacks = repo.techStacks?.slice(0, 3) ?? []
  const courseLabel = repo.courseCode && repo.courseName
    ? `${repo.courseCode} — ${repo.courseName}`
    : repo.courseCode || repo.courseName || null

  return (
    <Link
      to={`/courses/${repo.courseId}/repos/${repo.id}`}
      className="orbit-card p-5 border-orbit-border bg-orbit-surface/70 hover:border-orbit-accent/25 hover:bg-orbit-accent/[0.03] transition-all duration-300 group block"
    >
      <div className="flex items-start gap-4">
        <div className="h-10 w-10 rounded-xl bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center shrink-0 group-hover:bg-indigo-500/15 transition-colors">
          <Code className="h-5 w-5 text-indigo-400" weight="duotone" />
        </div>
        <div className="min-w-0 flex-1">
          <p className="text-[14px] font-bold text-orbit-text truncate group-hover:text-orbit-accent transition-colors">
            {repo.displayName}
          </p>
          {courseLabel && (
            <p className="mt-1 text-[11px] text-orbit-text-muted truncate">
              Thuộc môn: {courseLabel}
            </p>
          )}
          {repo.description && (
            <p className="mt-2 text-[12px] leading-relaxed text-orbit-text-secondary line-clamp-2">
              {repo.description}
            </p>
          )}
          <div className="mt-3 flex flex-wrap items-center gap-2">
            {repo.primaryLanguage && (
              <span className="px-2.5 py-1 rounded-full bg-orbit-accent/8 border border-orbit-accent/12 text-[10px] font-bold text-orbit-accent">
                {repo.primaryLanguage}
              </span>
            )}
            {stacks.map((s) => (
              <span key={s} className="px-2.5 py-1 rounded-full bg-orbit-surface border border-orbit-border text-[10px] font-bold text-orbit-text-muted">
                {s}
              </span>
            ))}
          </div>
        </div>
      </div>
    </Link>
  )
}
