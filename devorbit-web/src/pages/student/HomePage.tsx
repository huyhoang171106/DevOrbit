import { useState, useEffect, useRef, useMemo } from 'react'
import { Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { DiscoveryFeed } from '../../components/student/DiscoveryFeed'
import { Compass, Graph, Cube, MagicWand, Rocket, BookOpen, Code, Sparkle, ArrowRight, GraduationCap, Smiley } from '@phosphor-icons/react'

const staggerContainer = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.06, delayChildren: 0.05 },
  },
}

const fadeUp = {
  hidden: { opacity: 0, y: 30 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { type: 'spring' as const, stiffness: 300, damping: 30 },
  },
}

export function HomePage() {
  const [slideIndex, setSlideIndex] = useState(0)
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null)

  const slides = useMemo(() => [
    { icon: BookOpen, label: 'Danh mục môn học', count: '80+' },
    { icon: Code, label: 'Kho mã nguồn', count: '450+' },
    { icon: GraduationCap, label: 'Sinh viên sử dụng', count: '100+' },
    { icon: Smiley, label: 'Mức độ hài lòng', count: '96%' },
  ], [])

  useEffect(() => {
    intervalRef.current = setInterval(() => {
      setSlideIndex(prev => (prev + 1) % slides.length)
    }, 2000)
    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current)
    }
  }, [])

  return (
    <div className="w-full">
      {/* ─── HERO: Split-screen asymmetric ─── */}
      <section className="relative w-full overflow-hidden min-h-[100dvh] flex items-center border-b border-orbit-border gpu">
        {/* Background orbs */}
        <div className="absolute inset-0 pointer-events-none z-0">
          <div className="absolute top-[-20%] right-[-10%] w-[70%] h-[80%] bg-orbit-accent/8 blur-[80px] rounded-full" />
          <div className="absolute bottom-[-10%] left-[-10%] w-[40%] h-[50%] bg-emerald-500/5 blur-[60px] rounded-full" />
          <div
            className="absolute inset-0 opacity-[0.015]"
            style={{ backgroundImage: 'radial-gradient(rgba(52, 211, 153, 0.5) 1px, transparent 1px)', backgroundSize: '48px 48px' }}
          />
        </div>

        <div className="relative z-10 w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12 py-24 md:py-32">
          <motion.div
            className="grid lg:grid-cols-12 gap-16 lg:gap-24 items-center"
            variants={staggerContainer}
            initial="hidden"
            animate="visible"
          >
            {/* Left: Content */}
            <div className="lg:col-span-7 max-w-2xl">
              <motion.div variants={fadeUp} className="mb-8">
                <span className="section-label">
                  <Sparkle className="h-3 w-3" weight="fill" />
                  Trường Đại học Công nghệ Thông tin
                </span>
              </motion.div>

              <motion.h1
                variants={fadeUp}
                className="hero-display mb-8 leading-[1.1]"
              >
                Kết nối{' '}
                <span className="text-orbit-accent relative inline-block">
                  Môn học
                  <span className="absolute -bottom-1 left-0 w-full h-1 bg-orbit-accent/30 rounded-full blur-[2px]" />
                </span>
                <br />
                <span className="inline-block mt-1">
                  Kho Mã nguồn
                </span>
              </motion.h1>

              <motion.p
                variants={fadeUp}
                className="body-lg text-[18px] md:text-[20px] mb-12 max-w-[600px] leading-relaxed"
              >
                DevOrbit giúp sinh&nbsp;viên Khoa Công&nbsp;nghệ Phần&nbsp;Mềm - UIT tiếp&nbsp;cận các đồ&nbsp;án mẫu, dự&nbsp;án GitHub
                và công&nbsp;nghệ lõi theo từng môn&nbsp;học một cách trực&nbsp;quan và khoa&nbsp;học.
              </motion.p>

              <motion.div
                variants={fadeUp}
                className="flex flex-col sm:flex-row gap-5"
              >
                <Link to="/courses" className="btn-primary text-[13px] px-10 py-5">
                  <Rocket className="h-5 w-5" weight="fill" />
                  Bắt đầu ngay
                </Link>
                <Link
                  to="/knowledge-graph"
                  className="btn-secondary text-[13px] px-10 py-5 group"
                >
                  <Graph className="h-5 w-5" weight="regular" />
                  Sơ đồ kiến thức
                  <ArrowRight className="h-4 w-4 group-hover:translate-x-1 transition-transform" weight="bold" />
                </Link>
              </motion.div>
            </div>

            {/* Right: Feature Cube */}
            <motion.div
              variants={fadeUp}
              className="lg:col-span-5 relative"
            >
              <div className="relative aspect-square max-w-[500px] mx-auto">
                {/* Glow behind */}
                <div className="absolute inset-0 bg-orbit-accent/10 blur-[100px] rounded-full" />

                {/* 3D-like card stack */}
                <div className="relative h-full w-full">
                  {/* Back card */}
                  <div className="absolute top-4 left-4 right-0 bottom-0 rounded-[2.5rem] bg-orbit-elevated border border-orbit-border" />

                  {/* Front card */}
                  <div className="absolute inset-0 rounded-[2.5rem] bg-gradient-to-br from-orbit-surface to-orbit-elevated border border-orbit-accent/20 p-10 flex flex-col shadow-glow-lg overflow-hidden">
                    {/* Floating decorative bubbles */}
                    <motion.div
                      className="absolute -top-16 -right-16 w-72 h-72 rounded-full pointer-events-none"
                      style={{ background: 'radial-gradient(circle at 30% 30%, rgba(52, 211, 153, 0.08), rgba(255, 255, 255, 0.02) 70%)' }}
                      animate={{ y: [0, -35, 0, 35, 0] }}
                      transition={{ duration: 7, repeat: Infinity, ease: 'easeInOut' }}
                    />
                    <motion.div
                      className="absolute -bottom-20 -left-20 w-96 h-96 rounded-full pointer-events-none"
                      style={{ background: 'radial-gradient(circle at 70% 70%, rgba(16, 185, 129, 0.06), rgba(255, 255, 255, 0.01) 70%)' }}
                      animate={{ y: [0, 30, 0, -30, 0] }}
                      transition={{ duration: 9, repeat: Infinity, ease: 'easeInOut' }}
                    />
                    <div className="flex items-center gap-4 shrink-0">
                      <div className="h-14 w-14 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center">
                        <Cube className="h-8 w-8 text-orbit-accent" weight="duotone" />
                      </div>
                      <div>
                        <div className="text-[10px] font-black uppercase tracking-[0.2em] text-orbit-accent mb-1">Hệ sinh thái</div>
                        <div className="text-lg font-bold text-orbit-text">DevOrbit v2.0</div>
                      </div>
                    </div>

                    {/* ─── Stats Carousel (centered) ─── */}
                    <div className="flex-1 flex flex-col items-center justify-center">
                      <div className="relative w-full overflow-hidden flex items-center justify-center">
                        <AnimatePresence mode="wait">
                          <motion.div
                            key={slideIndex}
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            transition={{ duration: 0.35 }}
                            className="flex flex-col items-center justify-center"
                          >
                            {(() => {
                              const Icon = slides[slideIndex].icon
                              return <Icon className="h-9 w-9 text-orbit-accent mb-3" weight="duotone" />
                            })()}
                            <span className="text-3xl font-bold text-orbit-text text-center leading-tight">{slides[slideIndex].label}</span>
                            <span className="text-[44px] font-black text-orbit-text tabular-nums leading-none mt-3">{slides[slideIndex].count}</span>
                          </motion.div>
                        </AnimatePresence>
                      </div>
                    </div>

                    {/* Slide indicator (bottom) */}
                    <div className="flex items-center justify-center gap-2 shrink-0 mt-auto pt-6">
                      {slides.map((_slide, i) => (
                        <div
                          key={i}
                          className={`h-2 rounded-full transition-[border-color,background-color] duration-300 ${
                            i === slideIndex ? 'w-6 bg-orbit-accent' : 'w-2 bg-orbit-border/30'
                          }`}
                        />
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            </motion.div>
          </motion.div>
        </div>
      </section>

      {/* ─── AI TUTOR: Bento Section ─── */}
      <section className="relative w-full overflow-hidden pt-28 md:pt-36 pb-16 md:pb-20" style={{ contentVisibility: "auto" }}>
        <div className="w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12">
          <motion.div
            className="max-w-xl mb-16"
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: '-100px' }}
            transition={{ type: 'spring', stiffness: 300, damping: 30 }}
          >
            <span className="section-label mb-6 inline-flex">
              <MagicWand className="h-3 w-3" weight="fill" />
              Sức&nbsp;mạnh trí&nbsp;tuệ nhân&nbsp;tạo
            </span>
            <h2 className="display-lg mt-6 mb-6">
              Tóm tắt &<br />
              <span className="text-orbit-accent">Cố vấn học tập</span>
            </h2>
            <p className="body-lg text-[17px] leading-relaxed">
               DevOrbit sử dụng AI để phân&nbsp;tích từng repository. Nhận ngay bản tóm&nbsp;tắt dự&nbsp;án,
              lộ&nbsp;trình học&nbsp;tập và lời khuyên từ AI Tutor để làm chủ kiến&nbsp;thức.
            </p>
          </motion.div>

          <motion.div
            className="grid md:grid-cols-3 gap-6"
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true, margin: '-100px' }}
            variants={staggerContainer}
          >
            {/* AI Tutor Card - wide */}
            <motion.div
              variants={fadeUp}
              className="md:col-span-2 orbit-card-glow p-10 md:p-12 relative overflow-hidden group"
            >
              <div className="absolute top-0 right-0 w-64 h-64 bg-orbit-accent/5 blur-[100px] rounded-full -translate-y-1/2 translate-x-1/2 group-hover:bg-orbit-accent/10 transition-all duration-1000" />
              <div className="relative">
                <div className="flex items-center gap-4 mb-8">
                  <div className="h-14 w-14 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center">
                    <MagicWand className="h-7 w-7 text-orbit-accent" weight="duotone" />
                  </div>
                  <div>
                    <div className="text-[10px] font-black uppercase tracking-[0.2em] text-orbit-accent mb-1">AI Tutor</div>
                    <div className="text-[15px] font-bold text-orbit-text">Trợ lý học tập thông minh</div>
                  </div>
                </div>
                <div className="p-8 rounded-3xl bg-orbit-bg border border-orbit-border/60">
                  <div className="flex items-start gap-4">
                    <div className="h-10 w-10 rounded-xl bg-orbit-accent/10 flex items-center justify-center shrink-0 mt-1">
                      <Sparkle className="h-5 w-5 text-orbit-accent" weight="fill" />
                    </div>
                    <div>
                      <div className="flex items-center gap-3 mb-3">
                        <span className="text-[11px] font-bold text-orbit-accent uppercase tracking-wider">AI Tutor</span>
                        <span className="text-[10px] text-orbit-text-muted">Vừa xong</span>
                      </div>
                      <p className="body-md text-[14px] italic leading-relaxed text-orbit-text-secondary">
                        "Dựa trên kiến&nbsp;trúc Spring Boot này, bạn nên tập&nbsp;trung vào SecurityConfig
                        để hiểu cách xử&nbsp;lý JWT..."
                      </p>
                      <div className="mt-4 flex gap-2">
                        <span className="text-[10px] px-3 py-1.5 rounded-full bg-orbit-accent-subtle border border-orbit-accent/20 text-orbit-accent font-semibold">Spring Boot</span>
                        <span className="text-[10px] px-3 py-1.5 rounded-full bg-orbit-surface border border-orbit-border text-orbit-text-muted font-semibold">JWT</span>
                        <span className="text-[10px] px-3 py-1.5 rounded-full bg-orbit-surface border border-orbit-border text-orbit-text-muted font-semibold">Security</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="flex items-center gap-4 mt-6 text-[12px]">
                  <span className="flex items-center gap-2 text-orbit-text-muted">
                    <span className="h-1.5 w-1.5 rounded-full bg-orbit-accent animate-breathing" />
                    Đang phân&nbsp;tích 3 repositories
                  </span>
                </div>
              </div>
            </motion.div>

            {/* Tóm tắt Card */}
            <motion.div
              variants={fadeUp}
              className="orbit-card-glow p-10 md:p-12 flex flex-col items-start justify-between group will-change-transform"
            >
              <div className="h-16 w-16 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center mb-8 group-hover:scale-105 transition-transform duration-500">
                <Code className="h-8 w-8 text-orbit-accent" weight="duotone" />
              </div>
              <div>
                <h3 className="heading-4 mb-3">Tóm&nbsp;tắt tự&nbsp;động</h3>
                <p className="body-md text-[14px] leading-relaxed">
                  Hiểu các dự&nbsp;án phức&nbsp;tạp trong vài giây với phân&nbsp;tích AI chuyên&nbsp;sâu.
                </p>
              </div>
              <div className="mt-8 flex gap-2">
                {['Java', 'TS', 'Py'].map((tag, i) => (
                  <span key={i} className="text-[10px] px-3 py-1.5 rounded-full bg-orbit-surface border border-orbit-border text-orbit-text-muted font-semibold">{tag}</span>
                ))}
              </div>
            </motion.div>

            {/* Lộ trình Card */}
            <motion.div
              variants={fadeUp}
              className="md:col-span-3 orbit-card-glow p-10 md:p-12 flex flex-col will-change-transform md:flex-row items-start md:items-center justify-between gap-8 group"
            >
              <div className="flex items-start gap-6">
                <div className="h-16 w-16 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform duration-500">
                  <Rocket className="h-8 w-8 text-orbit-accent" weight="duotone" />
                </div>
                <div>
                  <h3 className="heading-4 mb-3">Lộ&nbsp;trình học&nbsp;tập</h3>
                  <p className="body-md text-[14px] leading-relaxed max-w-xl">
                    Hướng dẫn học&nbsp;tập từ mã&nbsp;nguồn thực&nbsp;tế, đề&nbsp;xuất môn&nbsp;học và kỹ&nbsp;năng
                    theo định&nbsp;hướng nghề&nbsp;nghiệp của bạn.
                  </p>
                </div>
              </div>
              <Link to="/courses" className="btn-secondary text-[12px] shrink-0">
                Xem lộ trình
                <ArrowRight className="h-4 w-4" weight="bold" />
              </Link>
            </motion.div>
          </motion.div>
        </div>
      </section>

      {/* ─── DISCOVERY FEED ─── */}
      <section className="relative py-28 md:py-36" style={{ contentVisibility: "auto" }}>
        <div className="w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12">
          <motion.div
            className="max-w-xl mb-16"
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: '-100px' }}
            transition={{ type: 'spring', stiffness: 300, damping: 30 }}
          >
            <span className="section-label-muted mb-6 inline-flex">
              <Compass className="h-3 w-3" weight="regular" />
              Dòng thời&nbsp;gian mới&nbsp;nhất
            </span>
            <h2 className="display-lg mt-6 mb-6">
              Khám phá{' '}
              <span className="text-orbit-accent">mới nhất</span>
            </h2>
          </motion.div>

          <DiscoveryFeed />
        </div>
      </section>
    </div>
  )
}
