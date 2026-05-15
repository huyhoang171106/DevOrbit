import { useEffect, useState } from 'react'
import { apiGet } from '../../lib/api'
import { CourseCard } from '../../components/student/CourseCard'
import { Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import type { CourseSummary } from '../../types/api'
import { MagnifyingGlass, Graph, Funnel, X, GraduationCap, BookOpen } from '@phosphor-icons/react'

export function CourseListPage() {
  const [courses, setCourses] = useState<CourseSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [searchQuery, setSearchQuery] = useState('')
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    apiGet<CourseSummary[]>('/api/courses')
      .then(setCourses)
      .catch((err) => {
        console.error(err)
        setError('Không thể tải danh sách môn học. Vui lòng thử lại sau.')
      })
      .finally(() => setLoading(false))
  }, [])

  const filteredCourses = courses.filter(c =>
    c.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    c.code.toLowerCase().includes(searchQuery.toLowerCase())
  )

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
    <div className="relative w-full min-h-screen pb-32">
      {/* Background ambient */}
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute top-[10%] left-[-10%] w-[40%] h-[600px] bg-orbit-accent/5 blur-[180px] rounded-full" />
        <div className="absolute bottom-[20%] right-[-10%] w-[35%] h-[500px] bg-emerald-500/3 blur-[150px] rounded-full" />
      </div>

      <div className="relative z-10 w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12 py-16 md:py-24">
        {/* ─── HEADER: Left-aligned, asymmetric ─── */}
        <header className="mb-20">
          <motion.div
            className="max-w-3xl"
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ type: 'spring', stiffness: 100, damping: 20 }}
          >
            <span className="section-label mb-8 inline-flex">
              <GraduationCap className="h-3 w-3" weight="fill" />
              Danh mục môn học trực tuyến
            </span>

            <h1 className="display-lg mt-6 mb-6 leading-[1.05]">
              Lộ trình{' '}
              <span className="text-orbit-accent relative inline-block">
                Học tập
                <span className="absolute -bottom-2 left-0 w-full h-1 bg-orbit-accent/20 rounded-full blur-[2px]" />
              </span>{' '}
              của Bạn
            </h1>

            <p className="body-lg text-[17px] md:text-[18px] leading-relaxed max-w-2xl mb-10">
               Khám phá hệ&nbsp;sinh thái kiến&nbsp;thức SE - UIT. Tìm kiếm các repository chuyên&nbsp;sâu,
              sơ&nbsp;đồ mạng&nbsp;lưới tương&nbsp;tác và kinh&nbsp;nghiệm từ bạn bè cho mọi môn&nbsp;học trong chương&nbsp;trình.
            </p>
          </motion.div>

          {/* Search + CTA row */}
          <motion.div
            className="flex flex-col lg:flex-row gap-5 items-stretch lg:items-center"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2, type: 'spring', stiffness: 100, damping: 20 }}
          >
            <div className="relative flex-1 max-w-xl group">
              <div className="absolute -inset-1 bg-gradient-to-r from-orbit-accent/20 to-emerald-500/20 rounded-3xl blur opacity-0 group-focus-within:opacity-100 transition duration-500" />
              <div className="relative flex items-center">
                <MagnifyingGlass className="absolute left-5 h-5 w-5 text-orbit-text-muted group-focus-within:text-orbit-accent transition-colors duration-300" weight="regular" />
                <input
                  type="text"
                  placeholder="Tìm kiếm môn học theo tên hoặc mã..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full bg-orbit-surface/80 backdrop-blur-xl border border-orbit-border rounded-3xl py-5 pl-14 pr-14 text-orbit-text placeholder:text-orbit-text-muted/50 focus:outline-none focus:border-orbit-accent/40 focus:ring-4 focus:ring-orbit-accent/5 transition-all text-[15px]"
                />
                {searchQuery && (
                  <button
                    onClick={() => setSearchQuery('')}
                    className="absolute right-5 h-8 w-8 rounded-full bg-orbit-elevated border border-orbit-border flex items-center justify-center text-orbit-text-muted hover:text-orbit-text hover:border-orbit-accent/30 transition-all"
                  >
                    <X className="h-4 w-4" weight="bold" />
                  </button>
                )}
              </div>
            </div>

            <Link
              to="/knowledge-graph"
              className="btn-secondary group shrink-0 text-[12px] px-8 py-5"
            >
              <Graph className="h-5 w-5" weight="regular" />
              Sơ đồ kiến thức
            </Link>
          </motion.div>
        </header>

        {/* ─── RESULT COUNT ─── */}
        <motion.div
          className="flex items-center justify-between mb-12 pb-6 border-b border-orbit-border/50"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.3 }}
        >
          <div className="flex items-center gap-3">
            <span className="h-6 w-1 bg-orbit-accent rounded-full" />
            <h2 className="text-[15px] font-bold text-orbit-text">Danh sách môn học</h2>
          </div>
          <div className="px-4 py-2 rounded-full bg-orbit-surface border border-orbit-border text-[10px] font-black uppercase tracking-widest text-orbit-text-muted tabular-nums">
            <Funnel className="h-3 w-3 inline-block mr-2" weight="regular" />
            {filteredCourses.length} kết quả
          </div>
        </motion.div>

        {/* ─── COURSE GRID (2-col asymmetric + 3-col on xl) ─── */}
        <AnimatePresence mode="wait">
          {filteredCourses.length > 0 ? (
            <motion.div
              key="grid"
              className="grid gap-6 md:grid-cols-2 xl:grid-cols-3"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
            >
              {filteredCourses.map((c, index) => (
                <div
                  key={c.id}
                  className={index === 0 ? 'md:col-span-2 xl:col-span-1' : ''}
                >
                  <CourseCard course={c} index={index} />
                </div>
              ))}
            </motion.div>
          ) : (
            <motion.div
              key="empty"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="orbit-card p-16 md:p-24 text-center border-dashed border-2 border-orbit-accent/10"
            >
              <div className="h-20 w-20 rounded-2xl bg-orbit-surface border border-orbit-border flex items-center justify-center mx-auto mb-8">
                <BookOpen className="h-10 w-10 text-orbit-text-muted" weight="light" />
              </div>
              <h3 className="heading-4 mb-4 text-orbit-text">
                {searchQuery ? 'Không tìm thấy kết quả' : 'Hệ thống trống'}
              </h3>
              <p className="body-md text-[14px] max-w-md mx-auto leading-relaxed mb-8">
                {searchQuery
                  ? `Chúng tôi không tìm thấy môn học nào khớp với "${searchQuery}".`
                  : 'Ma trận học thuật hiện đang được đồng bộ hóa. Vui lòng quay lại sau vài phút.'
                }
              </p>
              {searchQuery && (
                <button
                  onClick={() => setSearchQuery('')}
                  className="btn-primary text-[12px]"
                >
                  <X className="h-4 w-4" weight="bold" />
                  Xoá bộ lọc
                </button>
              )}
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  )
}
