import { useEffect, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { apiGet, apiStudentPost } from '../../lib/api'
import { isStudentAuthenticated } from '../../lib/auth'
import { RepoCard } from '../../components/student/RepoCard'
import { RepoFilterBar } from '../../components/student/RepoFilterBar'
import { CourseKnowledgeGraph } from '../../components/student/CourseKnowledgeGraph'
import type { RepoSummary, CourseDetail } from '../../types/api'
import { ArrowLeft, GraduationCap, BookOpen, Code, Tag, Building, Clock, Bookmark, BookmarkSimple, ShareNetwork, Sparkle, Stack, WarningCircle } from '@phosphor-icons/react'

const stagger = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.05, delayChildren: 0.03 },
  },
}

const fadeUp = {
  hidden: { opacity: 0, y: 24 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { type: 'spring' as const, stiffness: 300, damping: 30 },
  },
}

export function CourseDetailPage() {
  const { courseId } = useParams<{ courseId: string }>()
  const navigate = useNavigate()
  const [course, setCourse] = useState<CourseDetail | null>(null)
  const [repos, setRepos] = useState<RepoSummary[]>([])
  const [filtered, setFiltered] = useState<RepoSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [bookmarked, setBookmarked] = useState(false)
  const [bookmarking, setBookmarking] = useState(false)

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

  async function toggleBookmark() {
    if (!isStudentAuthenticated()) {
      navigate('/student/login')
      return
    }
    if (!course || bookmarking) return
    setBookmarking(true)
    try {
      if (bookmarked) {
        // We don't have the bookmark id here — skip for now, use the add-only flow
        setBookmarked(false)
      } else {
        await apiStudentPost('/api/student/bookmarks', {
          targetType: 'COURSE',
          targetId: course.id,
          title: course.name,
          subtitle: course.code,
          url: `/courses/${course.id}`,
        })
        setBookmarked(true)
      }
    } catch {
      // silently fail
    } finally {
      setBookmarking(false)
    }
  }

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
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[400px] h-[400px] bg-orbit-accent/5 blur-[40px] rounded-full animate-pulse-soft" />
        <div className="relative flex flex-col items-center gap-6">
          <div className="relative h-16 w-16">
            <div className="absolute inset-0 rounded-full border-2 border-orbit-accent/10" />
            <div className="absolute inset-0 rounded-full border-t-2 border-orbit-accent animate-spin shadow-[0_0_20px_rgba(52,211,153,0.2)]" />
          </div>
          <div className="flex flex-col items-center">
            <p className="text-[11px] font-black text-orbit-accent tracking-[0.3em] uppercase mb-2">Đang xử lý</p>
            <p className="text-[14px] font-bold text-orbit-text-secondary animate-pulse-soft">Dữ liệu môn học</p>
          </div>
        </div>
      </div>
    )
  }

  if (!course) {
    return (
      <div className="flex flex-col items-center justify-center py-32 text-center px-6">
        <div className="orbit-card p-12 md:p-16 max-w-md">
          <div className="h-16 w-16 rounded-2xl bg-rose-500/10 border border-rose-500/20 flex items-center justify-center mx-auto mb-8">
            <WarningCircle className="h-8 w-8 text-rose-500" weight="duotone" />
          </div>
          <h2 className="heading-4 mb-4 text-orbit-text">Môn học không tồn tại</h2>
          <p className="body-md mb-8">Chúng tôi không thể tìm thấy dữ liệu về môn học này trong hệ thống.</p>
          <Link to="/courses" className="btn-primary">
            <ArrowLeft className="h-4 w-4" weight="bold" />
            Quay lại danh sách
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="relative w-full min-h-screen pb-32 gpu">
      {/* Ambient background */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden z-0">
        <div className="absolute top-0 right-0 w-[50%] h-[600px] bg-orbit-accent/5 blur-[60px] rounded-full -translate-y-1/4 translate-x-1/4" />
        <div className="absolute bottom-0 left-0 w-[35%] h-[400px] bg-emerald-500/3 blur-[40px] rounded-full" />
      </div>

      <div className="relative z-10 w-full max-w-[1300px] mx-auto px-6 md:px-10 lg:px-12 py-10">
        {/* ─── Breadcrumb ─── */}
        <motion.nav
          className="mb-12 flex items-center gap-4"
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ type: 'spring', stiffness: 300, damping: 30 }}
        >
          <Link
            to="/courses"
            className="group flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-orbit-surface border border-orbit-border hover:border-orbit-accent/30 hover:bg-orbit-accent/5 transition-[border-color,background-color] duration-300"
          >
            <ArrowLeft className="h-4 w-4 text-orbit-text-muted group-hover:text-orbit-accent transition-colors group-hover:-translate-x-0.5 duration-300" weight="bold" />
            <span className="text-[11px] font-semibold uppercase tracking-[0.15em] text-orbit-text-secondary group-hover:text-orbit-text transition-colors">Danh mục</span>
          </Link>
          <span className="h-1 w-1 rounded-full bg-orbit-text-muted/30" />
          <span className="text-[11px] font-bold text-orbit-accent uppercase tracking-widest tabular-nums">{course.code}</span>
        </motion.nav>

        {/* ─── HERO ─── */}
        <motion.header
          className="mb-20"
          variants={stagger}
          initial="hidden"
          animate="visible"
        >
          <div className="flex flex-col lg:flex-row lg:items-start justify-between gap-12 lg:gap-16">
            <div className="flex-1 max-w-3xl">
              <motion.div variants={fadeUp}>
                <span className="section-label mb-8 inline-flex">
                  <GraduationCap className="h-3 w-3" weight="fill" />
                  Dữ liệu cốt lõi
                </span>
              </motion.div>

              <motion.h1
                variants={fadeUp}
                className="display-lg mb-6 leading-[1.05]"
              >
                {course.name}
              </motion.h1>

              {course.nameEn && (
                <motion.p
                  variants={fadeUp}
                  className="body-lg text-[17px] md:text-[19px] text-orbit-text-muted mb-8 italic leading-relaxed"
                >
                  {course.nameEn}
                </motion.p>
              )}

              {course.description && (
                <motion.div
                  variants={fadeUp}
                  className="relative pl-8 border-l-2 border-orbit-accent/20 py-1"
                >
                  <p className="body-md text-[15px] leading-relaxed max-w-2xl">
                    {course.description}
                  </p>
                </motion.div>
              )}
            </div>

            <motion.div
              variants={fadeUp}
              className="flex flex-row lg:flex-col flex-wrap gap-4 min-w-[180px]"
            >
              <div className="orbit-card-glow flex-1 lg:flex-none px-8 py-6 flex flex-col items-center justify-center min-w-[130px]">
                <div className="text-[10px] font-black uppercase tracking-[0.2em] text-orbit-accent mb-2">Số tín chỉ</div>
                <div className="text-[40px] font-black text-orbit-text tabular-nums">{course.credits}</div>
                <div className="mt-3 h-1 w-12 bg-orbit-accent/20 rounded-full overflow-hidden">
                  <div className="h-full bg-orbit-accent rounded-full w-2/3" />
                </div>
              </div>
              <div className="orbit-card-glow flex-1 lg:flex-none px-8 py-6 flex flex-col items-center justify-center min-w-[130px] border-indigo-500/10">
                <div className="text-[10px] font-black uppercase tracking-[0.2em] text-indigo-400 mb-2">Nguồn tài liệu</div>
                <div className="text-[40px] font-black text-orbit-text tabular-nums">{repos.length}</div>
                <div className="mt-3 flex gap-1">
                  {[1, 2, 3].map(i => <div key={i} className="h-1 w-2 rounded-full bg-indigo-500/30" />)}
                </div>
              </div>
            </motion.div>
          </div>

          {/* ─── Stats Grid ─── */}
          <motion.div
            variants={fadeUp}
            className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-12"
          >
            {[
              { label: 'Lý thuyết', value: `${course.theoryHours || 0} Tiết`, icon: BookOpen, color: 'text-orbit-accent' },
              { label: 'Thực hành', value: `${course.practiceHours || 0} Tiết`, icon: Code, color: 'text-indigo-400' },
              { label: 'Loại học phần', value: course.subjectType || 'Chuyên ngành', icon: Tag, color: 'text-amber-400' },
              { label: 'Đơn vị quản lý', value: course.managementUnit || 'Khoa', icon: Building, color: 'text-violet-400' },
            ].map((stat, i) => {
              const Icon = stat.icon
              return (
                <div
                  key={i}
                  className="orbit-card p-5 flex items-center gap-5 hover:border-orbit-border transition-[border-color,opacity] duration-500 group cursor-default"
                >
                  <div className={`h-12 w-12 rounded-2xl bg-orbit-surface border border-orbit-border flex items-center justify-center group-hover:border-orbit-accent/20 group-hover:bg-orbit-accent/5 transition-[border-color,background-color] duration-500`}>
                    <Icon className={`h-6 w-6 text-orbit-text-muted group-hover:${stat.color} transition-colors duration-500`} weight="duotone" />
                  </div>
                  <div>
                    <div className="text-[10px] font-black uppercase tracking-[0.15em] text-orbit-text-muted mb-0.5">{stat.label}</div>
                    <div className="text-[14px] font-bold text-orbit-text">{stat.value}</div>
                  </div>
                </div>
              )
            })}
          </motion.div>

          {/* ─── Metadata ─── */}
          {(course.equivalentMH || course.codeOld) && (
            <motion.div
              variants={fadeUp}
              className="mt-6 flex flex-wrap gap-3"
            >
              {course.codeOld && (
                <div className="px-4 py-2 rounded-2xl bg-amber-500/5 border border-amber-500/20 flex items-center gap-3">
                  <Clock className="h-3.5 w-3.5 text-amber-400" weight="regular" />
                  <span className="text-[10px] font-black uppercase tracking-wider text-amber-400/80">Mã cũ:</span>
                  <span className="text-[11px] font-bold text-amber-300">{course.codeOld}</span>
                </div>
              )}
              {course.equivalentMH && (
                <div className="px-4 py-2 rounded-2xl bg-indigo-500/5 border border-indigo-500/20 flex items-center gap-3">
                  <Code className="h-3.5 w-3.5 text-indigo-400" weight="regular" />
                  <span className="text-[10px] font-black uppercase tracking-wider text-indigo-400/80">Tương đương:</span>
                  <span className="text-[11px] font-bold text-indigo-300">{course.equivalentMH}</span>
                </div>
              )}
            </motion.div>
          )}
        </motion.header>

        {/* ─── Main Content + Sidebar ─── */}
        <div className="grid lg:grid-cols-12 gap-12">
          {/* Main: Repositories */}
          <div className="lg:col-span-8 space-y-10">
            <motion.section
              initial={{ opacity: 0, y: 30 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3, type: 'spring', stiffness: 300, damping: 30 }}
            >
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-6 mb-10">
                <h2 className="heading-3 flex items-center gap-4">
                  <span className="h-10 w-1.5 bg-gradient-to-b from-orbit-accent to-emerald-600 rounded-full shadow-[0_0_10px_rgba(52,211,153,0.3)]" />
                  Ma trận kiến thức
                </h2>
                <div className="px-4 py-2 rounded-2xl bg-orbit-surface border border-orbit-border flex items-center gap-3">
                  <span className="h-2 w-2 rounded-full bg-orbit-accent animate-breathing" />
                  <span className="text-[10px] font-black uppercase tracking-widest text-orbit-text-muted tabular-nums">
                    {filtered.length} kết quả
                  </span>
                </div>
              </div>

              {allStacks.length > 0 && (
                <div className="mb-8 p-1.5 rounded-3xl bg-orbit-surface/60 border border-orbit-border/60 backdrop-blur-sm">
                  <RepoFilterBar techStacks={allStacks} onFilter={handleFilter} />
                </div>
              )}

              <div className="grid sm:grid-cols-2 gap-5">
                {filtered.map((r, index) => (
                  <motion.div
                    key={r.id}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{
                      type: 'spring',
                      stiffness: 300, damping: 30,
                      delay: 0.4 + index * 0.08,
                    }}
                  >
                    <RepoCard repo={r} />
                  </motion.div>
                ))}
              </div>

              {filtered.length === 0 && (
                <div className="orbit-card py-24 text-center border-dashed border-2 border-orbit-accent/10">
                  <div className="h-16 w-16 rounded-2xl bg-orbit-surface border border-orbit-border flex items-center justify-center mx-auto mb-6">
                    <Stack className="h-8 w-8 text-orbit-text-muted" weight="light" />
                  </div>
                  <h3 className="heading-4 mb-3 text-orbit-text">Không tìm thấy kết quả</h3>
                  <p className="body-md text-[14px] max-w-sm mx-auto leading-relaxed">
                    Các tham số hiện tại không trả về kết quả nào. Hãy điều chỉnh bộ lọc để tiếp tục khám phá.
                  </p>
                </div>
              )}
            </motion.section>
          </div>

          {/* Sidebar */}
          <aside className="lg:col-span-4 space-y-8">
            <motion.div
              className="sticky top-24 space-y-8"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.4, type: 'spring', stiffness: 300, damping: 30 }}
            >
              {/* Knowledge Graph sidebar */}
              <div className="relative group">
                <div className="absolute -inset-1 bg-gradient-to-r from-orbit-accent/20 to-indigo-500/20 rounded-4xl blur opacity-0 group-hover:opacity-100 transition duration-1000" />
                <div className="relative">
                  <CourseKnowledgeGraph courseId={Number(courseId)} />
                </div>
              </div>

              {/* Insight card */}
              <div className="orbit-card-glow p-8 group">
                <div className="flex items-center gap-4 mb-6">
                  <div className="h-10 w-10 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center">
                    <Sparkle className="h-5 w-5 text-orbit-accent" weight="duotone" />
                  </div>
                  <h4 className="text-[13px] font-bold text-orbit-accent uppercase tracking-[0.1em]">Góc nhìn học thuật</h4>
                </div>

                <p className="body-sm text-[13px] leading-[1.8] text-orbit-text-secondary italic relative">
                  <span className="absolute -top-3 -left-2 text-3xl text-orbit-accent/20 font-serif leading-none">"</span>
                  Thu hẹp khoảng cách giữa lý thuyết kiến trúc và triển khai thực tế. Phân tích các repository để hiểu cách áp dụng các kiến thức cốt lõi vào thực tiễn theo tiêu chuẩn công nghiệp.
                </p>

                <div className="mt-8 pt-6 border-t border-orbit-border/50 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <span className="h-2 w-2 rounded-full bg-orbit-accent" />
                    <span className="text-[10px] font-black uppercase tracking-widest text-orbit-text-muted">Nguồn tin cậy</span>
                  </div>
                </div>
              </div>

              {/* Quick Actions */}
              <div className="flex flex-col gap-3">
                <button
                  onClick={toggleBookmark}
                  disabled={bookmarking}
                  className={`btn-secondary justify-start px-6 py-4 text-[12px] group ${bookmarked ? 'border-emerald-500/30 bg-emerald-500/5' : ''}`}
                >
                  {bookmarked ? (
                    <BookmarkSimple className="h-4 w-4 text-emerald-400" weight="fill" />
                  ) : (
                    <Bookmark className="h-4 w-4 text-orbit-text-muted group-hover:text-orbit-accent transition-colors" weight="regular" />
                  )}
                  {bookmarked ? 'Đã đánh dấu' : 'Đánh dấu môn học'}
                </button>
                <button className="btn-secondary justify-start px-6 py-4 text-[12px] group">
                  <ShareNetwork className="h-4 w-4 text-orbit-text-muted group-hover:text-orbit-accent transition-colors" weight="regular" />
                  Chia sẻ Repository
                </button>
              </div>
            </motion.div>
          </aside>
        </div>
      </div>
    </div>
  )
}
