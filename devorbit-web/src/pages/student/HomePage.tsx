import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { DiscoveryFeed } from '../../components/student/DiscoveryFeed'
import { HeroStoryCarousel } from '../../components/student/HeroStoryCarousel'
import { Compass, Graph, MagicWand, Rocket, Sparkle, ArrowRight, Globe, Star, Code } from '@phosphor-icons/react'
import {
  FadeReveal,
  BlurReveal,
  StaggerReveal,
  StaggerItem,
  SectionTransition,
  ParallaxLayer,
  ScrollProgressIndicator,
} from '../../motion'

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

  return (
    <div className="w-full">
      {/* Scroll progress on non-knowledge-graph pages */}
      <ScrollProgressIndicator position="right" showLabel={false} />

      {/* ─── HERO: Split-screen asymmetric ─── */}
      <SectionTransition atmosphere="glow" className="relative w-full overflow-hidden min-h-[100dvh] flex items-center border-b border-orbit-border gpu">
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

            {/* Right: Hero Story Carousel (cinematic scene narrative) */}
            <motion.div variants={fadeUp} className="lg:col-span-5 relative">
              <HeroStoryCarousel />
            </motion.div>
          </motion.div>
        </div>
      </SectionTransition>

      {/* ─── HOW IT WORKS: Cinematic Story Section ─── */}
      <SectionTransition atmosphere="deep" className="relative w-full overflow-hidden py-36 md:py-48 border-b border-orbit-border/40">
        {/* Background depth */}
        <div className="absolute inset-0 pointer-events-none">
          <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-orbit-accent/3 blur-[150px] rounded-full" />
          <div className="absolute top-0 right-0 w-[300px] h-[300px] bg-indigo-500/3 blur-[120px] rounded-full" />
          <div className="absolute bottom-0 left-0 w-[400px] h-[400px] bg-emerald-500/2 blur-[100px] rounded-full" />
        </div>

        <div className="relative z-10 w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12">
          <BlurReveal blur={10} duration={0.8}>
            <div className="flex items-center gap-3 mb-8">
              <span className="section-label">
                <Star className="h-3 w-3" weight="fill" />
                Hành trình học tập
              </span>
            </div>
            <h2 className="display-lg mb-8">
              Khám phá{' '}
              <span className="text-orbit-accent">tri thức</span>
              {' '}theo cách của bạn
            </h2>
          </BlurReveal>

          <FadeReveal y={20} delay={0.2}>
            <p className="body-lg text-[17px] md:text-[18px] max-w-2xl mb-20 leading-relaxed">
              DevOrbit biến việc học lập trình thành một hành trình khám phá vũ trụ kiến thức,
              nơi mỗi môn học là một vì sao và mỗi dự án là một chòm sao.
            </p>
          </FadeReveal>

          <StaggerReveal stagger={0.1} y={30}>
            {/* Step 1 */}
            <StaggerItem>
              <div className="grid md:grid-cols-12 gap-8 md:gap-16 items-center mb-28 md:mb-36 last:mb-0">
                <div className="md:col-span-5 order-2 md:order-1">
                  <div className="flex items-center gap-3 mb-4">
                    <span className="h-8 w-8 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center text-[12px] font-black text-orbit-accent">01</span>
                    <span className="text-[10px] font-black uppercase tracking-[0.25em] text-orbit-accent">Khám phá</span>
                  </div>
                  <h3 className="heading-2 mb-4">Duyệt qua vũ trụ môn học</h3>
                  <p className="body-lg text-[15px] md:text-[16px] leading-relaxed text-orbit-text-secondary">
                    Khám phá hơn 80+ môn học trong chương trình Công nghệ Phần mềm.
                    Mỗi môn học đều được phân tích chi tiết với các repository mẫu,
                    tài nguyên học tập và lộ trình phát triển kỹ năng.
                  </p>
                </div>
                <div className="md:col-span-7 order-1 md:order-2">
                  <ParallaxLayer speed={0.2} range={60}>
                    <div className="orbit-card-glow p-10 md:p-14 relative overflow-hidden">
                      <div className="absolute -top-20 -right-20 w-60 h-60 bg-orbit-accent/8 blur-[80px] rounded-full" />
                      <div className="relative">
                        <div className="flex items-center gap-4 text-[15px] font-bold text-orbit-text mb-8">
                          <Globe className="h-6 w-6 text-orbit-accent" weight="duotone" />
                          Hệ thống môn học SE-UIT
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                          {[
                            'SE104 - Nhập môn CNPM',
                            'SE100 - PT PM HĐT',
                            'SE109 - DevOps',
                            'SE401 - Mẫu thiết kế',
                          ].map((item, i) => (
                            <div key={i} className="px-5 py-4 rounded-2xl bg-orbit-bg border border-orbit-border/50 flex items-center gap-3">
                              <div className="h-2 w-2 rounded-full bg-orbit-accent/60" />
                              <span className="text-[13px] font-semibold text-orbit-text-secondary">{item}</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    </div>
                  </ParallaxLayer>
                </div>
              </div>
            </StaggerItem>

            {/* Step 2 */}
            <StaggerItem>
              <div className="grid md:grid-cols-12 gap-8 md:gap-16 items-center mb-28 md:mb-36 last:mb-0">
                <div className="md:col-span-7">
                  <ParallaxLayer speed={-0.15} range={60}>
                    <div className="orbit-card-glow p-10 md:p-14 relative overflow-hidden">
                      <div className="absolute -bottom-20 -left-20 w-60 h-60 bg-indigo-500/8 blur-[80px] rounded-full" />
                      <div className="relative">
                        <div className="flex items-center gap-4 text-[15px] font-bold text-orbit-text mb-8">
                          <Graph className="h-6 w-6 text-indigo-400" weight="duotone" />
                          Mạng lưới kiến thức
                        </div>
                        <div className="space-y-3">
                          {[
                            { from: 'SE104', to: 'SE100', label: 'Môn học trước' },
                            { from: 'SE104', to: 'SE109', label: 'Môn học trước' },
                            { from: 'SE100', to: 'SE401', label: 'Môn học trước' },
                          ].map((item, i) => (
                            <div key={i} className="flex items-center gap-3 px-5 py-3 rounded-2xl bg-orbit-bg border border-orbit-border/50">
                              <span className="text-[12px] font-bold text-emerald-400">{item.from}</span>
                              <ArrowRight className="h-3.5 w-3.5 text-orbit-text-muted" weight="bold" />
                              <span className="text-[12px] font-bold text-indigo-400">{item.to}</span>
                              <span className="ml-auto text-[10px] text-orbit-text-muted">{item.label}</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    </div>
                  </ParallaxLayer>
                </div>
                <div className="md:col-span-5">
                  <div className="flex items-center gap-3 mb-4">
                    <span className="h-8 w-8 rounded-xl bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center text-[12px] font-black text-indigo-400">02</span>
                    <span className="text-[10px] font-black uppercase tracking-[0.25em] text-indigo-400">Kết nối</span>
                  </div>
                  <h3 className="heading-2 mb-4">Xây dựng lộ trình học tập</h3>
                  <p className="body-lg text-[15px] md:text-[16px] leading-relaxed text-orbit-text-secondary">
                    Hiểu rõ mối quan hệ giữa các môn học thông qua sơ đồ kiến thức trực quan.
                    Theo dõi điều kiện tiên quyết, khối lượng tín chỉ và lộ trình tốt nghiệp
                    phù hợp với định hướng nghề nghiệp.
                  </p>
                </div>
              </div>
            </StaggerItem>

            {/* Step 3 */}
            <StaggerItem>
              <div className="grid md:grid-cols-12 gap-8 md:gap-16 items-center">
                <div className="md:col-span-5 order-2 md:order-1">
                  <div className="flex items-center gap-3 mb-4">
                    <span className="h-8 w-8 rounded-xl bg-violet-500/10 border border-violet-500/20 flex items-center justify-center text-[12px] font-black text-violet-400">03</span>
                    <span className="text-[10px] font-black uppercase tracking-[0.25em] text-violet-400">Hoàn thiện</span>
                  </div>
                  <h3 className="heading-2 mb-4">Phát triển cùng AI</h3>
                  <p className="body-lg text-[15px] md:text-[16px] leading-relaxed text-orbit-text-secondary">
                    Sử dụng trí tuệ nhân tạo để phân tích repository, đề xuất lộ trình học tập
                    và nhận lời khuyên chuyên sâu từ AI Tutor — trợ lý học tập thông minh
                    đồng hành cùng bạn suốt hành trình.
                  </p>
                </div>
                <div className="md:col-span-7 order-1 md:order-2">
                  <ParallaxLayer speed={0.1} range={60}>
                    <div className="orbit-card-glow p-10 md:p-14 relative overflow-hidden">
                      <div className="absolute -top-20 -right-20 w-60 h-60 bg-violet-500/8 blur-[80px] rounded-full" />
                      <div className="relative">
                        <div className="flex items-center gap-4 text-[15px] font-bold text-orbit-text mb-8">
                          <Sparkle className="h-6 w-6 text-violet-400" weight="duotone" />
                          AI Learning Assistant
                        </div>
                        <div className="p-6 rounded-3xl bg-orbit-bg border border-orbit-border/50">
                          <div className="flex items-start gap-4">
                            <div className="h-10 w-10 rounded-xl bg-violet-500/10 flex items-center justify-center shrink-0">
                              <MagicWand className="h-5 w-5 text-violet-400" weight="fill" />
                            </div>
                            <div>
                              <p className="text-[13px] italic text-orbit-text-secondary leading-relaxed">
                                "Dựa trên kiến trúc Spring Boot và các repository mẫu,
                                bạn có thể bắt đầu với module IoC Container trước khi
                                đi sâu vào Spring Security và JWT Authentication."
                              </p>
                              <div className="flex items-center gap-2 mt-4">
                                <span className="h-1.5 w-1.5 rounded-full bg-violet-400 animate-breathing" />
                                <span className="text-[10px] text-orbit-text-muted">AI Tutor đề xuất</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </ParallaxLayer>
                </div>
              </div>
            </StaggerItem>
          </StaggerReveal>
        </div>
      </SectionTransition>

      {/* ─── AI TUTOR: Bento Section ─── */}
      <SectionTransition atmosphere="glow" className="relative w-full overflow-hidden pt-28 md:pt-36 pb-16 md:pb-20">
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
      </SectionTransition>

      {/* ─── DISCOVERY FEED ─── */}
      <SectionTransition atmosphere="deep" className="relative py-28 md:py-36">
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
      </SectionTransition>
    </div>
  )
}
