import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { DiscoveryFeed } from '../../components/student/DiscoveryFeed'
import { Compass, Graph, Cube, MagicWand, Rocket, BookOpen, Code, Sparkle, ArrowRight, GraduationCap } from '@phosphor-icons/react'

const staggerContainer = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: { staggerChildren: 0.12, delayChildren: 0.2 },
  },
}

const fadeUp = {
  hidden: { opacity: 0, y: 30 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { type: 'spring' as const, stiffness: 100, damping: 20 },
  },
}

export function HomePage() {
  return (
    <div className="w-full">
      {/* ─── HERO: Split-screen asymmetric ─── */}
      <section className="relative w-full overflow-hidden min-h-[100dvh] flex items-center border-b border-orbit-border">
        {/* Background orbs */}
        <div className="absolute inset-0 pointer-events-none z-0">
          <div className="absolute top-[-20%] right-[-10%] w-[70%] h-[80%] bg-orbit-accent/8 blur-[250px] rounded-full" />
          <div className="absolute bottom-[-10%] left-[-10%] w-[40%] h-[50%] bg-emerald-500/5 blur-[200px] rounded-full" />
          {/* Grid texture */}
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
                  Đại học Công nghệ Thông tin
                </span>
              </motion.div>

              <motion.h1
                variants={fadeUp}
                className="hero-display mb-8"
              >
                Kết nối{' '}
                <span className="text-orbit-accent relative inline-block">
                  Môn học
                  <span className="absolute -bottom-2 left-0 w-full h-1 bg-orbit-accent/30 rounded-full blur-[2px]" />
                </span>
                <br />
                Kho Mã nguồn
              </motion.h1>

              <motion.p
                variants={fadeUp}
                className="body-lg text-[18px] md:text-[20px] mb-12 max-w-[600px] leading-relaxed"
              >
                DevOrbit giúp sinh viên UIT tiếp cận các đồ án mẫu, dự án GitHub
                và công nghệ lõi theo từng môn học một cách trực quan và khoa học.
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
                  <div className="absolute inset-0 rounded-[2.5rem] bg-gradient-to-br from-orbit-surface to-orbit-elevated border border-orbit-accent/20 p-10 flex flex-col justify-between shadow-glow-lg">
                    <div className="flex items-center gap-4">
                      <div className="h-14 w-14 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center">
                        <Cube className="h-8 w-8 text-orbit-accent" weight="duotone" />
                      </div>
                      <div>
                        <div className="text-[10px] font-black uppercase tracking-[0.2em] text-orbit-accent mb-1">Hệ sinh thái</div>
                        <div className="text-lg font-bold text-orbit-text">DevOrbit v2.0</div>
                      </div>
                    </div>

                    <div className="space-y-5">
                      {[
                        { icon: BookOpen, label: 'Danh mục môn học', count: '120+' },
                        { icon: Code, label: 'Kho mã nguồn', count: '450+' },
                        { icon: GraduationCap, label: 'Sinh viên sử dụng', count: '2.5K+' },
                      ].map((item, i) => (
                        <div key={i} className="flex items-center justify-between py-3 border-b border-orbit-border/50 last:border-0">
                          <div className="flex items-center gap-3">
                            <item.icon className="h-4 w-4 text-orbit-accent" weight="duotone" />
                            <span className="text-[13px] font-medium text-orbit-text-secondary">{item.label}</span>
                          </div>
                          <span className="text-[15px] font-black text-orbit-text tabular-nums">{item.count}</span>
                        </div>
                      ))}
                    </div>

                    <div className="flex gap-3 pt-4">
                      <div className="h-2 w-2 rounded-full bg-orbit-accent animate-breathing" />
                      <div className="h-2 w-2 rounded-full bg-orbit-accent/40 animate-breathing" style={{ animationDelay: '0.5s' }} />
                      <div className="h-2 w-2 rounded-full bg-orbit-accent/20 animate-breathing" style={{ animationDelay: '1s' }} />
                    </div>
                  </div>
                </div>
              </div>
            </motion.div>
          </motion.div>
        </div>
      </section>

      {/* ─── AI TUTOR: Bento Section ─── */}
      <section className="relative w-full overflow-hidden py-28 md:py-36">
        <div className="w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12">
          <motion.div
            className="max-w-xl mb-16"
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: '-100px' }}
            transition={{ type: 'spring', stiffness: 100, damping: 20 }}
          >
            <span className="section-label mb-6 inline-flex">
              <MagicWand className="h-3 w-3" weight="fill" />
              Sức mạnh trí tuệ nhân tạo
            </span>
            <h2 className="display-lg mt-6 mb-6">
              Tóm tắt &<br />
              <span className="text-orbit-accent">Cố vấn học tập</span>
            </h2>
            <p className="body-lg text-[17px] leading-relaxed">
              DevOrbit sử dụng AI để phân tích từng repository. Nhận ngay bản tóm tắt dự án,
              lộ trình học tập và lời khuyên từ AI Tutor để làm chủ kiến thức.
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
                        "Dựa trên kiến trúc Spring Boot này, bạn nên tập trung vào SecurityConfig
                        để hiểu cách xử lý JWT..."
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
                    Đang phân tích 3 repositories
                  </span>
                </div>
              </div>
            </motion.div>

            {/* Tóm tắt Card */}
            <motion.div
              variants={fadeUp}
              className="orbit-card-glow p-10 md:p-12 flex flex-col items-start justify-between group"
            >
              <div className="h-16 w-16 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center mb-8 group-hover:scale-105 transition-transform duration-500">
                <Code className="h-8 w-8 text-orbit-accent" weight="duotone" />
              </div>
              <div>
                <h3 className="heading-4 mb-3">Tóm tắt tự động</h3>
                <p className="body-md text-[14px] leading-relaxed">
                  Hiểu các dự án phức tạp trong vài giây với phân tích AI chuyên sâu.
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
              className="md:col-span-3 orbit-card-glow p-10 md:p-12 flex flex-col md:flex-row items-start md:items-center justify-between gap-8 group"
            >
              <div className="flex items-start gap-6">
                <div className="h-16 w-16 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform duration-500">
                  <Rocket className="h-8 w-8 text-orbit-accent" weight="duotone" />
                </div>
                <div>
                  <h3 className="heading-4 mb-3">Lộ trình học tập</h3>
                  <p className="body-md text-[14px] leading-relaxed max-w-xl">
                    Hướng dẫn học tập từ mã nguồn thực tế, đề xuất môn học và kỹ năng
                    theo định hướng nghề nghiệp của bạn.
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

      {/* ─── STATS: Minimal metrics strip ─── */}
      <section className="border-t border-b border-orbit-border py-16 md:py-20">
        <div className="w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8 md:gap-16">
            {[
              { value: '120+', label: 'Môn học' },
              { value: '450+', label: 'Dự án mã nguồn' },
              { value: '2.500+', label: 'Sinh viên' },
              { value: '96%', label: 'Hài lòng' },
            ].map((stat, i) => (
              <motion.div
                key={i}
                className="text-center md:text-left"
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.1, type: 'spring', stiffness: 100, damping: 20 }}
              >
                <div className="text-[36px] md:text-[48px] font-black text-orbit-text tabular-nums leading-none mb-2">
                  {stat.value}
                </div>
                <div className="text-[13px] font-medium text-orbit-text-muted uppercase tracking-wider">
                  {stat.label}
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* ─── DISCOVERY FEED ─── */}
      <section className="relative py-28 md:py-36">
        <div className="w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12">
          <motion.div
            className="max-w-xl mb-16"
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: '-100px' }}
            transition={{ type: 'spring', stiffness: 100, damping: 20 }}
          >
            <span className="section-label-muted mb-6 inline-flex">
              <Compass className="h-3 w-3" weight="regular" />
              Dòng thời gian mới nhất
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
