import { Link, useNavigate } from 'react-router-dom'
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
  const navigate = useNavigate()

  return (
    <div className="w-full">
      {/* Scroll progress on non-knowledge-graph pages */}
      <ScrollProgressIndicator position="right" showLabel={false} />

      {/* â”€â”€â”€ HERO: Split-screen asymmetric â”€â”€â”€ */}
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

        <div className="relative z-10 w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12 py-12 sm:py-24 md:py-32">
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
                  TrÆ°á»ng Äáº¡i há»c CÃ´ng nghá»‡ ThÃ´ng tin
                </span>
              </motion.div>

              <motion.h1
                variants={fadeUp}
                className="hero-display mb-8 leading-[1.1]"
              >
                Káº¿t ná»‘i{' '}
                <span className="text-orbit-accent relative inline-block">
                  MÃ´n há»c
                  <span className="absolute -bottom-1 left-0 w-full h-1 bg-orbit-accent/30 rounded-full blur-[2px]" />
                </span>
                <br />
                <span className="inline-block mt-1">
                  Kho MÃ£ nguá»“n
                </span>
              </motion.h1>

              <motion.p
                variants={fadeUp}
                className="body-lg text-[18px] md:text-[20px] mb-12 max-w-[600px] leading-relaxed"
              >
                DevOrbit giÃºp sinh&nbsp;viÃªn Khoa CÃ´ng&nbsp;nghá»‡ Pháº§n&nbsp;Má»m - UIT tiáº¿p&nbsp;cáº­n cÃ¡c Ä‘á»“&nbsp;Ã¡n máº«u, dá»±&nbsp;Ã¡n GitHub
                vÃ  cÃ´ng&nbsp;nghá»‡ lÃµi theo tá»«ng mÃ´n&nbsp;há»c má»™t cÃ¡ch trá»±c&nbsp;quan vÃ  khoa&nbsp;há»c.
              </motion.p>

              <motion.div
                variants={fadeUp}
                className="flex flex-col sm:flex-row gap-5"
              >
                <Link to="/courses" className="btn-primary text-[13px] px-10 py-5">
                  <Rocket className="h-5 w-5" weight="fill" />
                  Báº¯t Ä‘áº§u ngay
                </Link>
                <Link
                  to="/knowledge-graph"
                  className="btn-secondary text-[13px] px-10 py-5 group"
                >
                  <Graph className="h-5 w-5" weight="regular" />
                  SÆ¡ Ä‘á»“ kiáº¿n thá»©c
                  <ArrowRight className="h-4 w-4 group-hover:translate-x-1 transition-transform" weight="bold" />
                </Link>
              </motion.div>
            </div>

            {/* Right: Hero Story Carousel (cinematic scene narrative) */}
            <motion.div variants={fadeUp} className="lg:col-span-5 relative w-full overflow-hidden">
              <HeroStoryCarousel />
            </motion.div>
          </motion.div>
        </div>
      </SectionTransition>

      {/* â”€â”€â”€ HOW IT WORKS: Cinematic Story Section â”€â”€â”€ */}
      <SectionTransition atmosphere="deep" className="relative w-full overflow-hidden py-16 sm:py-36 md:py-48 border-b border-orbit-border/40">
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
                HÃ nh trÃ¬nh há»c táº­p
              </span>
            </div>
            <h2 className="display-lg mb-8">
              KhÃ¡m phÃ¡{' '}
              <span className="text-orbit-accent">tri thá»©c</span>
              {' '}theo cÃ¡ch cá»§a báº¡n
            </h2>
          </BlurReveal>

          <FadeReveal y={20} delay={0.2}>
            <p className="body-lg text-[17px] md:text-[18px] max-w-2xl mb-20 leading-relaxed">
              DevOrbit biáº¿n viá»‡c há»c láº­p trÃ¬nh thÃ nh má»™t hÃ nh trÃ¬nh khÃ¡m phÃ¡ vÅ© trá»¥ kiáº¿n thá»©c,
              nÆ¡i má»—i mÃ´n há»c lÃ  má»™t vÃ¬ sao vÃ  má»—i dá»± Ã¡n lÃ  má»™t chÃ²m sao.
            </p>
          </FadeReveal>

          <StaggerReveal stagger={0.1} y={30}>
            {/* Step 1 */}
            <StaggerItem>
              <div className="grid md:grid-cols-12 gap-8 md:gap-16 items-center mb-28 md:mb-36 last:mb-0">
                <div className="md:col-span-5 order-2 md:order-1">
                  <div className="flex items-center gap-3 mb-4">
                    <span className="h-8 w-8 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center text-[12px] font-black text-orbit-accent">01</span>
                    <span className="text-[10px] font-black uppercase tracking-[0.25em] text-orbit-accent">KhÃ¡m phÃ¡</span>
                  </div>
                  <h3 className="heading-2 mb-4">Duyá»‡t qua vÅ© trá»¥ mÃ´n há»c</h3>
                  <p className="body-lg text-[15px] md:text-[16px] leading-relaxed text-orbit-text-secondary">
                    KhÃ¡m phÃ¡ hÆ¡n 80+ mÃ´n há»c trong chÆ°Æ¡ng trÃ¬nh CÃ´ng nghá»‡ Pháº§n má»m.
                    Má»—i mÃ´n há»c Ä‘á»u Ä‘Æ°á»£c phÃ¢n tÃ­ch chi tiáº¿t vá»›i cÃ¡c repository máº«u,
                    tÃ i nguyÃªn há»c táº­p vÃ  lá»™ trÃ¬nh phÃ¡t triá»ƒn ká»¹ nÄƒng.
                  </p>
                </div>
                <div className="md:col-span-7 order-1 md:order-2">
                  <ParallaxLayer speed={0.2} range={60}>
                    <div className="orbit-card-glow p-10 md:p-14 relative overflow-hidden">
                      <div className="absolute -top-20 -right-20 w-60 h-60 bg-orbit-accent/8 blur-[80px] rounded-full" />
                      <div className="relative">
                        <div className="flex items-center gap-4 text-[15px] font-bold text-orbit-text mb-8">
                          <Globe className="h-6 w-6 text-orbit-accent" weight="duotone" />
                          Há»‡ thá»‘ng mÃ´n há»c SE-UIT
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                          {[
                            'SE104 - Nháº­p mÃ´n CNPM',
                            'SE100 - PT PM HÄT',
                            'SE109 - DevOps',
                            'SE401 - Máº«u thiáº¿t káº¿',
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
                          Máº¡ng lÆ°á»›i kiáº¿n thá»©c
                        </div>
                        <div className="space-y-3">
                          {[
                            { from: 'SE104', to: 'SE100', label: 'MÃ´n há»c trÆ°á»›c' },
                            { from: 'SE104', to: 'SE109', label: 'MÃ´n há»c trÆ°á»›c' },
                            { from: 'SE100', to: 'SE401', label: 'MÃ´n há»c trÆ°á»›c' },
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
                    <span className="text-[10px] font-black uppercase tracking-[0.25em] text-indigo-400">Káº¿t ná»‘i</span>
                  </div>
                  <h3 className="heading-2 mb-4">XÃ¢y dá»±ng lá»™ trÃ¬nh há»c táº­p</h3>
                  <p className="body-lg text-[15px] md:text-[16px] leading-relaxed text-orbit-text-secondary">
                    Hiá»ƒu rÃµ má»‘i quan há»‡ giá»¯a cÃ¡c mÃ´n há»c thÃ´ng qua sÆ¡ Ä‘á»“ kiáº¿n thá»©c trá»±c quan.
                    Theo dÃµi Ä‘iá»u kiá»‡n tiÃªn quyáº¿t, khá»‘i lÆ°á»£ng tÃ­n chá»‰ vÃ  lá»™ trÃ¬nh tá»‘t nghiá»‡p
                    phÃ¹ há»£p vá»›i Ä‘á»‹nh hÆ°á»›ng nghá» nghiá»‡p.
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
                    <span className="text-[10px] font-black uppercase tracking-[0.25em] text-violet-400">HoÃ n thiá»‡n</span>
                  </div>
                  <h3 className="heading-2 mb-4">PhÃ¡t triá»ƒn cÃ¹ng AI</h3>
                  <p className="body-lg text-[15px] md:text-[16px] leading-relaxed text-orbit-text-secondary">
                    Sá»­ dá»¥ng trÃ­ tuá»‡ nhÃ¢n táº¡o Ä‘á»ƒ phÃ¢n tÃ­ch repository, Ä‘á» xuáº¥t lá»™ trÃ¬nh há»c táº­p
                    vÃ  nháº­n lá»i khuyÃªn chuyÃªn sÃ¢u tá»« AI Tutor â€” trá»£ lÃ½ há»c táº­p thÃ´ng minh
                    Ä‘á»“ng hÃ nh cÃ¹ng báº¡n suá»‘t hÃ nh trÃ¬nh.
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
                                "Dá»±a trÃªn kiáº¿n trÃºc Spring Boot vÃ  cÃ¡c repository máº«u,
                                báº¡n cÃ³ thá»ƒ báº¯t Ä‘áº§u vá»›i module IoC Container trÆ°á»›c khi
                                Ä‘i sÃ¢u vÃ o Spring Security vÃ  JWT Authentication."
                              </p>
                              <div className="flex items-center gap-2 mt-4">
                                <span className="h-1.5 w-1.5 rounded-full bg-violet-400 animate-breathing" />
                                <span className="text-[10px] text-orbit-text-muted">AI Tutor Ä‘á» xuáº¥t</span>
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

      {/* â”€â”€â”€ AI TUTOR: Bento Section â”€â”€â”€ */}
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
              Sá»©c&nbsp;máº¡nh trÃ­&nbsp;tuá»‡ nhÃ¢n&nbsp;táº¡o
            </span>
            <h2 className="display-lg mt-6 mb-6">
              TÃ³m táº¯t &<br />
              <span className="text-orbit-accent">Cá»‘ váº¥n há»c táº­p</span>
            </h2>
            <p className="body-lg text-[17px] leading-relaxed">
               DevOrbit sá»­ dá»¥ng AI Ä‘á»ƒ phÃ¢n&nbsp;tÃ­ch tá»«ng repository. Nháº­n ngay báº£n tÃ³m&nbsp;táº¯t dá»±&nbsp;Ã¡n,
              lá»™&nbsp;trÃ¬nh há»c&nbsp;táº­p vÃ  lá»i khuyÃªn tá»« AI Tutor Ä‘á»ƒ lÃ m chá»§ kiáº¿n&nbsp;thá»©c.
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
              onClick={() => navigate('/ai-tutor')}
              variants={fadeUp}
              className="md:col-span-2 orbit-card-glow p-10 md:p-12 relative overflow-hidden group cursor-pointer"
            >
              <div className="absolute top-0 right-0 w-64 h-64 bg-orbit-accent/5 blur-[100px] rounded-full -translate-y-1/2 translate-x-1/2 group-hover:bg-orbit-accent/10 transition-all duration-1000" />
              <div className="relative">
                <div className="flex items-center gap-4 mb-8">
                  <div className="h-14 w-14 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center">
                    <MagicWand className="h-7 w-7 text-orbit-accent" weight="duotone" />
                  </div>
                  <div>
                    <div className="text-[10px] font-black uppercase tracking-[0.2em] text-orbit-accent mb-1">AI Tutor</div>
                    <div className="text-[15px] font-bold text-orbit-text">Trá»£ lÃ½ há»c táº­p thÃ´ng minh</div>
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
                        <span className="text-[10px] text-orbit-text-muted">Vá»«a xong</span>
                      </div>
                      <p className="body-md text-[14px] italic leading-relaxed text-orbit-text-secondary">
                        "Dá»±a trÃªn kiáº¿n&nbsp;trÃºc Spring Boot nÃ y, báº¡n nÃªn táº­p&nbsp;trung vÃ o SecurityConfig
                        Ä‘á»ƒ hiá»ƒu cÃ¡ch xá»­&nbsp;lÃ½ JWT..."
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
                    Äang phÃ¢n&nbsp;tÃ­ch 3 repositories
                  </span>
                </div>
              </div>
            </motion.div>

            {/* TÃ³m táº¯t Card */}
            <motion.div
              variants={fadeUp}
              className="orbit-card-glow p-10 md:p-12 flex flex-col items-start justify-between group will-change-transform"
            >
              <div className="h-16 w-16 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center mb-8 group-hover:scale-105 transition-transform duration-500">
                <Code className="h-8 w-8 text-orbit-accent" weight="duotone" />
              </div>
              <div>
                <h3 className="heading-4 mb-3">TÃ³m&nbsp;táº¯t tá»±&nbsp;Ä‘á»™ng</h3>
                <p className="body-md text-[14px] leading-relaxed">
                  Hiá»ƒu cÃ¡c dá»±&nbsp;Ã¡n phá»©c&nbsp;táº¡p trong vÃ i giÃ¢y vá»›i phÃ¢n&nbsp;tÃ­ch AI chuyÃªn&nbsp;sÃ¢u.
                </p>
              </div>
              <div className="mt-8 flex gap-2">
                {['Java', 'TS', 'Py'].map((tag, i) => (
                  <span key={i} className="text-[10px] px-3 py-1.5 rounded-full bg-orbit-surface border border-orbit-border text-orbit-text-muted font-semibold">{tag}</span>
                ))}
              </div>
            </motion.div>

            {/* Lá»™ trÃ¬nh Card */}
            <motion.div
              variants={fadeUp}
              className="md:col-span-3 orbit-card-glow p-10 md:p-12 flex flex-col will-change-transform md:flex-row items-start md:items-center justify-between gap-8 group"
            >
              <div className="flex items-start gap-6">
                <div className="h-16 w-16 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform duration-500">
                  <Rocket className="h-8 w-8 text-orbit-accent" weight="duotone" />
                </div>
                <div>
                  <h3 className="heading-4 mb-3">Lá»™&nbsp;trÃ¬nh há»c&nbsp;táº­p</h3>
                  <p className="body-md text-[14px] leading-relaxed max-w-xl">
                    HÆ°á»›ng dáº«n há»c&nbsp;táº­p tá»« mÃ£&nbsp;nguá»“n thá»±c&nbsp;táº¿, Ä‘á»&nbsp;xuáº¥t mÃ´n&nbsp;há»c vÃ  ká»¹&nbsp;nÄƒng
                    theo Ä‘á»‹nh&nbsp;hÆ°á»›ng nghá»&nbsp;nghiá»‡p cá»§a báº¡n.
                  </p>
                </div>
              </div>
              <Link to="/courses" className="btn-secondary text-[12px] shrink-0">
                Xem lá»™ trÃ¬nh
                <ArrowRight className="h-4 w-4" weight="bold" />
              </Link>
            </motion.div>
          </motion.div>
        </div>
      </SectionTransition>

      {/* â”€â”€â”€ DISCOVERY FEED â”€â”€â”€ */}
      <SectionTransition atmosphere="deep" className="relative py-16 sm:py-28 md:py-36">
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
              DÃ²ng thá»i&nbsp;gian má»›i&nbsp;nháº¥t
            </span>
            <h2 className="display-lg mt-6 mb-6">
              KhÃ¡m phÃ¡{' '}
              <span className="text-orbit-accent">má»›i nháº¥t</span>
            </h2>
          </motion.div>

          <DiscoveryFeed />
        </div>
      </SectionTransition>
    </div>
  )
}


