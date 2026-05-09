import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { apiGet } from '../../lib/api'
import { RepoCard } from '../../components/student/RepoCard'
import { RepoFilterBar } from '../../components/student/RepoFilterBar'
import { CourseKnowledgeGraph } from '../../components/student/CourseKnowledgeGraph'
import type { RepoSummary, CourseDetail } from '../../types/api'

export function CourseDetailPage() {
  const { courseId } = useParams<{ courseId: string }>()
  const [course, setCourse] = useState<CourseDetail | null>(null)
  const [repos, setRepos] = useState<RepoSummary[]>([])
  const [filtered, setFiltered] = useState<RepoSummary[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!courseId) return

    setLoading(true)
    apiGet<CourseDetail>(`/api/courses/${courseId}`)
      .then((data) => {
        setCourse(data)
        setRepos(data.repos || [])
        setFiltered(data.repos || [])
      })
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [courseId])

  const allStacks = [...new Set(repos.flatMap((r) => r.techStacks))]

  function handleFilter(stack: string | null) {
    if (!stack) {
      setFiltered(repos)
    } else {
      setFiltered(repos.filter((r) => r.techStacks.includes(stack)))
    }
  }

  if (loading) {
    return (
      <div className="relative min-h-[80vh] flex items-center justify-center">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 h-96 w-96 rounded-full bg-emerald-500/10 blur-[120px] animate-pulse" />
        <div className="relative flex flex-col items-center gap-6">
          <div className="relative h-16 w-16">
            <div className="absolute inset-0 rounded-full border-4 border-emerald-500/10" />
            <div className="absolute inset-0 rounded-full border-t-4 border-emerald-500 animate-spin shadow-[0_0_15px_rgba(16,185,129,0.5)]" />
          </div>
          <div className="flex flex-col items-center">
            <p className="text-[12px] font-black text-emerald-500 tracking-[0.3em] uppercase mb-2">Đang xử lý</p>
            <p className="heading-4 text-clay-text animate-pulse tracking-wide">DỮ LIỆU MÔN HỌC</p>
          </div>
        </div>
      </div>
    )
  }

  if (!course) {
    return (
      <div className="flex flex-col items-center justify-center py-32 text-center">
        <div className="h-24 w-24 rounded-full bg-red-500/10 flex items-center justify-center mb-8 border border-red-500/20">
          <svg className="h-10 w-10 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
          </svg>
        </div>
        <h2 className="display-sm text-clay-text mb-4">Môn học đã biến mất vào hư không</h2>
        <p className="body-md text-ink-muted mb-8 max-w-md">Chúng tôi không thể tìm thấy dữ liệu về môn học này trong hệ thống.</p>
        <Link to="/courses" className="btn-primary px-8">Quay lại danh sách</Link>
      </div>
    )
  }

  return (
    <div className="relative w-full min-h-screen pb-24">
      {/* Dynamic Background Elements */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden z-0">
        <div className="absolute top-0 right-0 w-[60%] h-[700px] bg-emerald-500/[0.03] blur-[150px] rounded-full -translate-y-1/2 translate-x-1/4" />
        <div className="absolute top-1/3 left-0 w-[40%] h-[600px] bg-indigo-500/[0.03] blur-[130px] rounded-full -translate-x-1/4" />
        <div className="absolute bottom-0 right-1/4 w-[30%] h-[400px] bg-amber-500/[0.02] blur-[100px] rounded-full translate-y-1/2" />
      </div>

      <div className="relative z-10 w-full max-w-[1300px] mx-auto px-6 md:px-10 lg:px-12 py-10">
        {/* Navigation Breadcrumb */}
        <nav className="mb-12 flex items-center gap-4 animate-in fade-in slide-in-from-left-4 duration-700">
          <Link
            to="/courses"
            className="group flex items-center gap-3 py-2 px-4 rounded-full bg-glass-surface border border-glass-border hover:border-emerald-500/30 hover:bg-glass-surface-hover transition-all duration-300"
          >
            <svg className="h-4 w-4 text-ink-muted group-hover:text-emerald-500 transition-colors group-hover:-translate-x-0.5 duration-300" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <path d="M19 12H5M12 19l-7-7 7-7" />
            </svg>
            <span className="text-[11px] font-black uppercase tracking-[0.2em] text-ink-secondary group-hover:text-clay-text transition-colors">Danh mục</span>
          </Link>
          <div className="h-1 w-1 rounded-full bg-ink-muted/30" />
          <span className="text-[11px] font-bold text-emerald-500 uppercase tracking-widest">{course.code}</span>
        </nav>

        {/* Enhanced Hero Section */}
        <header className="mb-20 animate-in fade-in slide-in-from-bottom-8 duration-1000 ease-out">
          <div className="flex flex-col lg:flex-row lg:items-start justify-between gap-12 lg:gap-16">
            <div className="flex-1 max-w-4xl">
              <div className="inline-flex items-center gap-3 px-4 py-2 rounded-2xl bg-emerald-500/5 border border-emerald-500/10 mb-8 backdrop-blur-md">
                <div className="flex -space-x-2">
                  <div className="h-2 w-2 rounded-full bg-emerald-400 shadow-[0_0_8px_rgba(52,211,153,0.8)] animate-pulse" />
                </div>
                <span className="text-[10px] font-black uppercase tracking-[0.25em] text-emerald-500/80">Dữ liệu cốt lõi đang hoạt động</span>
              </div>

              <h1 className="hero-display text-4xl sm:text-5xl md:text-6xl lg:text-7xl mb-6 leading-[1.1] tracking-tight">
                {course.name}
              </h1>

              {course.nameEn && (
                <p className="text-xl md:text-2xl font-heading text-ink-muted mb-8 italic leading-relaxed">
                  {course.nameEn}
                </p>
              )}

              {course.description && (
                <div className="relative pl-8 border-l-2 border-emerald-500/20 py-1">
                  <p className="body-md text-lg text-ink-secondary leading-relaxed max-w-3xl">
                    {course.description}
                  </p>
                </div>
              )}
            </div>

            <div className="flex flex-row lg:flex-col flex-wrap gap-4 min-w-[200px]">
              <div className="glass-card-glow flex-1 lg:flex-none px-8 py-6 border-emerald-500/10 hover:border-emerald-500/30 transition-all duration-500 flex flex-col items-center justify-center min-w-[140px]">
                <div className="text-[10px] font-black uppercase tracking-[0.2em] text-emerald-500 mb-2">Số tín chỉ</div>
                <div className="heading-1 text-clay-text group-hover:scale-110 transition-transform">{course.credits}</div>
                <div className="mt-2 h-1 w-8 bg-emerald-500/20 rounded-full overflow-hidden">
                  <div className="h-full bg-emerald-500 rounded-full w-2/3 shadow-[0_0_10px_rgba(16,185,129,0.5)]" />
                </div>
              </div>
              <div className="glass-card-glow flex-1 lg:flex-none px-8 py-6 border-indigo-500/10 hover:border-indigo-500/30 transition-all duration-500 flex flex-col items-center justify-center min-w-[140px]">
                <div className="text-[10px] font-black uppercase tracking-[0.2em] text-indigo-400 mb-2">Nguồn tài liệu</div>
                <div className="heading-1 text-clay-text">{repos.length}</div>
                <div className="mt-2 flex gap-1">
                  {[1, 2, 3].map(i => <div key={i} className="h-1 w-2 rounded-full bg-indigo-500/30" />)}
                </div>
              </div>
            </div>
          </div>

          {/* Precision Stats Grid */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-5 mt-16">
            {[
              { label: 'Thời lượng Lý thuyết', value: `${course.theoryHours || 0} Tiết`, icon: 'book', color: 'emerald' },
              { label: 'Thời lượng Thực hành', value: `${course.practiceHours || 0} Tiết`, icon: 'code', color: 'indigo' },
              { label: 'Loại học phần', value: course.subjectType || 'Chuyên ngành', icon: 'tag', color: 'amber' },
              { label: 'Đơn vị quản lý', value: course.managementUnit || 'Khoa', icon: 'building', color: 'violet' }
            ].map((stat, i) => (
              <div
                key={i}
                className="glass-card p-5 flex items-center gap-5 border-opacity-20 hover:border-opacity-60 hover:bg-glass-surface-hover hover:-translate-y-1 transition-all duration-500 group cursor-default"
              >
                <div className={`h-12 w-12 rounded-2xl bg-${stat.color}-500/5 flex items-center justify-center text-ink-muted group-hover:text-${stat.color}-500 group-hover:bg-${stat.color}-500/10 transition-all duration-500 border border-transparent group-hover:border-${stat.color}-500/20 shadow-sm shadow-transparent group-hover:shadow-${stat.color}-500/5`}>
                  {stat.icon === 'book' && <svg className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24"><path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>}
                  {stat.icon === 'code' && <svg className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24"><path d="M17.25 6.75L22.5 12l-5.25 5.25m-10.5 0L1.5 12l5.25-5.25m7.5-3l-4.5 16.5" /></svg>}
                  {stat.icon === 'tag' && <svg className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24"><path d="M9.568 3H5.25A2.25 2.25 0 003 5.25v4.318c0 .597.237 1.17.659 1.591l9.581 9.581a1.125 1.125 0 001.591 0l4.318-4.318a1.125 1.125 0 000-1.591l-9.581-9.581c-.421-.421-.994-.659-1.591-.659zM6.375 7.125a.75.75 0 11-1.5 0 .75.75 0 011.5 0z" /></svg>}
                  {stat.icon === 'building' && <svg className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24"><path d="M2.25 21h19.5m-18-18v18m10.5-18v18m6-13.5V21M6.75 6.75h.75m-.75 3h.75m-.75 3h.75m3-6h.75m-.75 3h.75m-.75 3h.75M6.75 21v-3.375c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21M3 3h12m-.75 4.5H21m-3.75 3.75h.008v.008h-.008v-.008zm0 3h.008v.008h-.008v-.008zm0 3h.008v.008h-.008v-.008z" /></svg>}
                </div>
                <div>
                  <div className="text-[10px] font-black uppercase tracking-[0.2em] text-ink-muted mb-0.5">{stat.label}</div>
                  <div className="text-sm font-bold text-clay-text group-hover:text-emerald-500 transition-colors duration-300">{stat.value}</div>
                </div>
              </div>
            ))}
          </div>
        </header>

        <div className="grid lg:grid-cols-12 gap-12">
          {/* Main Content: Repositories */}
          <div className="lg:col-span-8 space-y-12">
            <section className="animate-in fade-in slide-in-from-bottom-8 duration-700 delay-300">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-6 mb-10">
                <h2 className="heading-3 flex items-center gap-4">
                  <span className="h-10 w-1.5 bg-gradient-to-b from-emerald-400 to-emerald-600 rounded-full shadow-[0_0_10px_rgba(16,185,129,0.3)]" />
                  Ma trận kiến thức
                </h2>
                <div className="px-4 py-2 rounded-xl bg-glass-surface border border-glass-border flex items-center gap-3">
                  <div className="h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
                  <span className="text-[11px] font-black uppercase tracking-widest text-ink-muted">
                    Tìm thấy {filtered.length} kết quả
                  </span>
                </div>
              </div>

              {allStacks.length > 0 && (
                <div className="mb-10 p-1 rounded-2xl bg-glass-surface/50 border border-glass-border/50 backdrop-blur-sm">
                  <RepoFilterBar techStacks={allStacks} onFilter={handleFilter} />
                </div>
              )}

              <div className="grid sm:grid-cols-2 gap-6">
                {filtered.map((r, index) => (
                  <div
                    key={r.id}
                    className="animate-in fade-in slide-in-from-bottom-6 duration-700 fill-mode-both"
                    style={{ animationDelay: `${400 + index * 100}ms` }}
                  >
                    <RepoCard repo={r} />
                  </div>
                ))}
              </div>

              {filtered.length === 0 && (
                <div className="glass-card py-24 text-center border-dashed border-2 border-emerald-500/10 mt-8 group">
                  <div className="h-20 w-20 rounded-full bg-glass-surface flex items-center justify-center mx-auto mb-8 border border-glass-border group-hover:scale-110 group-hover:border-emerald-500/30 transition-all duration-500">
                    <svg className="h-10 w-10 text-ink-muted group-hover:text-emerald-500 transition-colors" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                      <path d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196 7.5 7.5 0 0010.607 10.607z" />
                    </svg>
                  </div>
                  <h3 className="heading-3 text-clay-text mb-3">Không tìm thấy kết quả</h3>
                  <p className="body-md text-ink-muted max-w-sm mx-auto leading-relaxed">
                    Các tham số hiện tại không trả về kết quả nào. Hãy điều chỉnh bộ lọc để tiếp tục khám phá.
                  </p>
                </div>
              )}
            </section>
          </div>

          {/* Sidebar: Knowledge Graph & Metadata */}
          <aside className="lg:col-span-4 space-y-10 animate-in fade-in slide-in-from-right-8 duration-700 delay-500">
            <section className="sticky top-10 space-y-8">
              <div className="relative group">
                <div className="absolute -inset-1 bg-gradient-to-r from-emerald-500/20 to-indigo-500/20 rounded-3xl blur opacity-0 group-hover:opacity-100 transition duration-1000" />
                <div className="relative">
                  <CourseKnowledgeGraph courseId={Number(courseId)} />
                </div>
              </div>

              {/* Enhanced Info Card */}
              <div className="glass-card-glow p-8 bg-emerald-500/[0.01] border-emerald-500/10 hover:border-emerald-500/30 transition-all duration-700 group">
                <div className="flex items-center gap-4 mb-6">
                  <div className="h-10 w-10 rounded-xl bg-emerald-500/10 flex items-center justify-center text-emerald-500 border border-emerald-500/20 group-hover:bg-emerald-500/20 transition-all">
                    <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0012 18.75c-1.03 0-1.9-.4-2.59-1.177l-.547-.547z" />
                    </svg>
                  </div>
                  <h4 className="heading-5 text-emerald-500 uppercase tracking-[0.1em]">Góc nhìn học thuật</h4>
                </div>

                <p className="body-sm leading-[1.8] text-ink-secondary italic relative">
                  <span className="absolute -top-4 -left-2 text-4xl text-emerald-500/20 font-serif">"</span>
                  Thu hẹp khoảng cách giữa lý thuyết kiến trúc và triển khai thực tế. Phân tích các repository để hiểu cách áp dụng các kiến thức cốt lõi vào thực tiễn theo tiêu chuẩn công nghiệp.
                </p>

                <div className="mt-8 pt-8 border-t border-glass-border flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="h-2 w-2 rounded-full bg-emerald-500" />
                    <span className="text-[10px] font-black uppercase tracking-widest text-ink-muted">Nguồn tin cậy</span>
                  </div>
                  <svg className="h-5 w-5 text-ink-muted opacity-30" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" />
                  </svg>
                </div>
              </div>

              {/* Quick Actions */}
              <div className="flex flex-col gap-3">
                <button className="w-full btn-secondary py-4 group justify-start px-6">
                  <svg className="h-4 w-4 mr-3 text-ink-muted group-hover:text-indigo-400 transition-colors" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M17.593 3.322c1.1.128 1.907 1.077 1.907 2.185V21L12 17.25 4.5 21V5.507c0-1.108.806-2.057 1.907-2.185a48.507 48.507 0 0111.186 0z" />
                  </svg>
                  Đánh dấu môn học
                </button>
                <button className="w-full btn-secondary py-4 group justify-start px-6">
                  <svg className="h-4 w-4 mr-3 text-ink-muted group-hover:text-emerald-400 transition-colors" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M7.217 10.907a2.25 2.25 0 100 2.186m0-2.186c.18.324.283.696.283 1.093s-.103.77-.283 1.093m0-2.186l9.566-5.314m-9.566 7.5l9.566 5.314m0 0a2.25 2.25 0 103.935 2.186 2.25 2.25 0 00-3.935-2.186zm0-12.814a2.25 2.25 0 103.933-2.185 2.25 2.25 0 00-3.933 2.185z" />
                  </svg>
                  Chia sẻ Repository
                </button>
              </div>
            </section>
          </aside>
        </div>
      </div>
    </div>
  )
}
