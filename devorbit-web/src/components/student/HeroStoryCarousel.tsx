import { useState, useEffect, useRef, useCallback } from 'react'
import { motion, AnimatePresence, useMotionValue, useSpring, useTransform } from 'framer-motion'
import { gsap } from 'gsap'
import { Compass, Graph, MagicWand, Rocket, Cube } from '@phosphor-icons/react'
import type { Icon } from '@phosphor-icons/react'
import { useReducedMotion } from '../../motion'

/* ─── Scene definitions — pure storytelling, no fake stats ─── */

interface Scene {
  icon: Icon
  title: string
  description: string
  color: string
}

const scenes: Scene[] = [
  { icon: Compass,    title: 'Khám phá vũ trụ\nmôn học',          description: 'Duyệt qua hơn 80 môn học trong chương trình SE-UIT. Mỗi môn là một vì sao — DevOrbit giúp bạn định vị và khám phá từng chòm sao tri thức.',                          color: '#34d399' },
  { icon: Graph,      title: 'Kết nối\nmạng lưới tri thức',       description: 'Mỗi môn học đều có mối liên hệ. Sơ đồ kiến thức trực quan giúp bạn thấy rõ điều kiện tiên quyết, lộ trình và sự phát triển xuyên suốt 8 học kỳ.',                        color: '#818cf8' },
  { icon: MagicWand,  title: 'AI đồng hành\ncùng bạn',            description: 'Trí tuệ nhân tạo phân tích repository mẫu theo từng môn học. Nhận đề xuất lộ trình, tóm tắt kiến trúc và lời khuyên từ AI Tutor.',                                              color: '#a78bfa' },
  { icon: Rocket,     title: 'Hệ thống thích\nứng theo bạn',      description: 'DevOrbit ghi nhận tiến độ học tập, đề xuất môn học phù hợp với định hướng nghề nghiệp và đồng hành cùng bạn đến khi tốt nghiệp.',                                            color: '#f59e0b' },
]

/* ─── Single scene slide with word-by-word reveal ─── */

function SceneSlide({ scene, index }: { scene: Scene; index: number }) {
  const words = scene.description.split(' ')
  return (
    <motion.div
      key={index}
      initial={{ opacity: 0, y: 24, scale: 0.96 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      exit={{ opacity: 0, y: -24, scale: 1.03 }}
      transition={{ type: 'spring', stiffness: 220, damping: 26, mass: 0.8 }}
      className="flex flex-col items-center text-center px-2"
    >
      {/* Icon */}
      <div className="relative mb-5">
        <motion.div
          className="h-14 w-14 rounded-2xl flex items-center justify-center border"
          style={{ backgroundColor: `${scene.color}12`, borderColor: `${scene.color}25` }}
        >
          <scene.icon className="h-7 w-7" style={{ color: scene.color }} weight="duotone" />
        </motion.div>
        <motion.div
          className="absolute inset-0 rounded-2xl blur-xl pointer-events-none"
          style={{ backgroundColor: scene.color }}
          animate={{ scale: [1, 1.6, 1], opacity: [0.08, 0.18, 0.08] }}
          transition={{ duration: 3, repeat: Infinity, ease: 'easeInOut' }}
        />
      </div>

      {/* Title */}
      <h3 className="text-[24px] md:text-[26px] font-black leading-[1.15] tracking-[-0.02em] text-orbit-text whitespace-pre-line">
        {scene.title}
      </h3>

      {/* Description — staggered word reveal */}
      <p className="mt-4 max-w-[320px] text-[15px] md:text-[16px] leading-relaxed text-orbit-text-secondary">
        {words.map((word, i) => (
          <span key={i}>
            <motion.span
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.15 + i * 0.025, duration: 0.35, ease: 'easeOut' }}
              className="inline-block"
            >
              {word}
            </motion.span>
            {i < words.length - 1 && '\u00A0'}
          </span>
        ))}
      </p>
    </motion.div>
  )
}

/* ─── Floating particles ─── */

function Particles({ color, count = 6 }: { color: string; count?: number }) {
  const reduced = useReducedMotion()
  if (reduced) return null
  return (
    <div className="absolute inset-0 pointer-events-none overflow-hidden rounded-[2.5rem]">
      {Array.from({ length: count }).map((_, i) => (
        <motion.div
          key={i}
          className="absolute rounded-full"
          style={{
            left: `${15 + i * 14}%`,
            top: `${20 + (i % 4) * 20}%`,
            width: 2 + (i % 3), height: 2 + (i % 3),
            backgroundColor: color, opacity: 0.08 + i * 0.02,
          }}
          animate={{ y: [0, -12 - i * 3, 0], opacity: [0, 0.25 + i * 0.03, 0] }}
          transition={{ duration: 2.5 + i * 0.4, repeat: Infinity, delay: i * 0.35, ease: 'easeInOut' }}
        />
      ))}
    </div>
  )
}

/* ─── Main component ─── */

export function HeroStoryCarousel() {
  const reduced = useReducedMotion()
  const [active, setActive] = useState(0)
  const timer = useRef<ReturnType<typeof setInterval> | null>(null)
  const orb1 = useRef<HTMLDivElement>(null)
  const orb2 = useRef<HTMLDivElement>(null)
  const scene = scenes[active]

  // Auto-rotate
  useEffect(() => {
    timer.current = setInterval(() => setActive(p => (p + 1) % scenes.length), 4500)
    return () => { if (timer.current) clearInterval(timer.current) }
  }, [])

  const goTo = useCallback((i: number) => {
    setActive(i)
    if (timer.current) { clearInterval(timer.current); timer.current = setInterval(() => setActive(p => (p + 1) % scenes.length), 4500) }
  }, [])

  // GSAP gradient orbs
  useEffect(() => {
    const ctx = gsap.context(() => {
      ;[orb1, orb2].forEach((ref, ri) => {
        if (ref.current) gsap.to(ref.current, { y: 20 + Math.random() * 20, x: -10 + Math.random() * 20, scale: 0.9 + Math.random() * 0.2, duration: 5 + Math.random() * 3, repeat: -1, yoyo: true, ease: 'sine.inOut', delay: ri * 1.2 })
      })
    })
    return () => ctx.revert()
  }, [])

  // Mouse parallax tilt
  const mx = useMotionValue(0.5)
  const my = useMotionValue(0.5)
  const sx = useSpring(mx, { stiffness: 200, damping: 25 })
  const sy = useSpring(my, { stiffness: 200, damping: 25 })
  const rx = useTransform(sy, [0, 1], reduced ? [0, 0] : [6, -6])
  const ry = useTransform(sx, [0, 1], reduced ? [0, 0] : [-6, 6])

  const onMove = useCallback((e: React.MouseEvent) => {
    if (reduced) return
    const r = e.currentTarget.getBoundingClientRect()
    mx.set((e.clientX - r.left) / r.width)
    my.set((e.clientY - r.top) / r.height)
  }, [reduced, mx, my])
  const onLeave = useCallback(() => { mx.set(0.5); my.set(0.5) }, [mx, my])

  return (
    <div className="relative aspect-square max-w-[500px] mx-auto">
      {/* Outer glow */}
      <motion.div
        className="absolute inset-0 blur-[120px] rounded-full pointer-events-none"
        animate={{ backgroundColor: `${scene.color}12` }}
        transition={{ duration: 1.2, ease: 'easeInOut' }}
      />

      {/* Card with tilt */}
      <motion.div
        className="relative h-full w-full"
        style={{ rotateX: rx, rotateY: ry, transformPerspective: 1200 }}
        onMouseMove={onMove} onMouseLeave={onLeave}
      >
        {/* Back card */}
        <div className="absolute top-4 left-4 right-0 bottom-0 rounded-[2.5rem] bg-orbit-elevated border border-orbit-border" />

        {/* Front card */}
        <div className="absolute inset-0 rounded-[2.5rem] bg-gradient-to-br from-orbit-surface to-orbit-elevated p-10 flex flex-col overflow-hidden shadow-glow-lg">
          {/* Atmospheric layers */}
          <div className="absolute inset-0 pointer-events-none overflow-hidden rounded-[2.5rem]">
            <div ref={orb1} className="absolute -top-20 -right-16 w-80 h-80 rounded-full" style={{ background: `radial-gradient(circle at 30% 30%, ${scene.color}14, transparent 70%)` }} />
            <div ref={orb2} className="absolute -bottom-24 -left-20 w-96 h-96 rounded-full" style={{ background: `radial-gradient(circle at 70% 70%, ${scene.color}08, transparent 70%)` }} />
          </div>

          {/* Light sweep */}
          {!reduced && (
            <div className="absolute inset-0 pointer-events-none overflow-hidden rounded-[2.5rem]">
              <motion.div
                className="absolute top-0 h-full w-[40%]" style={{ background: 'linear-gradient(90deg, transparent, rgba(255,255,255,0.04), transparent)', left: '-40%' }}
                animate={{ x: ['0%', '350%'] }}
                transition={{ duration: 5, repeat: Infinity, repeatDelay: 3, ease: 'easeInOut' }}
              />
            </div>
          )}

          <Particles color={scene.color} count={8} />

          {/* Border glow */}
          <motion.div
            className="absolute inset-0 rounded-[2.5rem] pointer-events-none"
            animate={{ borderColor: [`${scene.color}15`, `${scene.color}35`, `${scene.color}15`], boxShadow: [`inset 0 0 30px ${scene.color}08`, `inset 0 0 40px ${scene.color}15`, `inset 0 0 30px ${scene.color}08`] }}
            transition={{ duration: 4, repeat: Infinity, ease: 'easeInOut' }}
            style={{ borderWidth: 1, borderStyle: 'solid' }}
          />

          {/* Content */}
          <div className="relative z-10 flex flex-col h-full">
            {/* Header */}
            <div className="flex items-center gap-4 shrink-0">
              <motion.div
                className="h-14 w-14 rounded-2xl flex items-center justify-center border"
                style={{ backgroundColor: `${scene.color}12`, borderColor: `${scene.color}25` }}
                animate={{ boxShadow: [`0 0 0px ${scene.color}00`, `0 0 20px ${scene.color}20`, `0 0 0px ${scene.color}00`] }}
                transition={{ duration: 3, repeat: Infinity, ease: 'easeInOut' }}
              >
                <Cube className="h-8 w-8" style={{ color: scene.color }} weight="duotone" />
              </motion.div>
              <div>
                <motion.div
                  className="text-[10px] font-black uppercase tracking-[0.2em] mb-1"
                  style={{ color: scene.color }}
                  animate={{ opacity: [0.7, 1, 0.7] }}
                  transition={{ duration: 3, repeat: Infinity, ease: 'easeInOut' }}
                >
                  DevOrbit
                </motion.div>
                <div className="text-lg font-bold text-orbit-text">Hành trình khám phá</div>
              </div>
            </div>

            {/* Scene */}
            <div className="flex-1 flex items-center justify-center">
              <div className="w-full">
                <AnimatePresence mode="wait">
                  <SceneSlide key={active} scene={scene} index={active} />
                </AnimatePresence>
              </div>
            </div>

            {/* Dots */}
            <div className="flex items-center justify-center gap-2 shrink-0 mt-auto pt-5">
              {scenes.map((s, i) => (
                <button
                  key={i} onClick={() => goTo(i)}
                  className="rounded-full transition-all duration-500 focus:outline-none"
                  style={{
                    width: i === active ? 24 : 8, height: 8,
                    backgroundColor: i === active ? s.color : 'rgba(113,113,122,0.25)',
                    boxShadow: i === active ? `0 0 12px ${s.color}50` : 'none',
                  }}
                  aria-label={`Scene ${i + 1}`}
                />
              ))}
            </div>
          </div>
        </div>
      </motion.div>
    </div>
  )
}
