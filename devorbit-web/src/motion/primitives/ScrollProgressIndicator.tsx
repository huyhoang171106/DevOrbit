
import { m as motion, useScroll, useSpring, useTransform } from 'framer-motion'
import { useReducedMotion } from '../hooks/useReducedMotion'

interface ScrollProgressIndicatorProps {
  /** Position. Default 'right' */
  position?: 'right' | 'left'
  /** Show label below. Default false */
  showLabel?: boolean
  /** Color. Default orbit-accent */
  color?: string
}

/**
 * Elegant scroll progress indicator.
 * Minimal — subtle line that fills as you scroll.
 * Only visible after scrolling past the first viewport.
 */
export function ScrollProgressIndicator({
  position = 'right',
  showLabel = false,
  color = 'bg-orbit-accent',
}: ScrollProgressIndicatorProps) {
  const prefersReduced = useReducedMotion()

  const { scrollYProgress } = useScroll()
  const smoothProgress = useSpring(scrollYProgress, {
    stiffness: 100,
    damping: 30,
  })

  const opacity = useTransform(scrollYProgress, [0, 0.05, 0.95, 1], [0, 1, 1, 0])
  const scaleY = useTransform(smoothProgress, [0, 1], [0, 1])
  const labelOpacity = useTransform(
    scrollYProgress,
    [0, 0.08, 0.92, 1],
    [0, 0.7, 0.7, 0]
  )
  const labelText = useTransform(smoothProgress, (v) => `${Math.round(v * 100)}%`)

  const positionClass = position === 'right' ? 'right-6' : 'left-6'

  if (prefersReduced) return null

  return (
    <motion.div
      className={`fixed ${positionClass} top-1/2 -translate-y-1/2 z-50 flex flex-col items-center gap-3 pointer-events-none`}
      style={{ opacity }}
    >
      {/* Track */}
      <div className="relative h-40 w-[2px] rounded-full bg-orbit-border/30 overflow-hidden">
        {/* Fill */}
        <motion.div
          className={`absolute bottom-0 left-0 w-full rounded-full ${color}`}
          style={{ scaleY, originY: 1, height: '100%' }}
        />
        {/* Glow */}
        <motion.div
          className={`absolute bottom-0 left-1/2 -translate-x-1/2 w-[6px] h-[6px] rounded-full ${color} blur-sm`}
          style={{ scaleY, originY: 1 }}
        />
      </div>

      {/* Percentage label */}
      {showLabel && (
        <motion.span
          className="text-[9px] font-black text-orbit-text-muted tabular-nums tracking-wider"
          style={{ opacity: labelOpacity }}
        >
          <motion.span>{labelText}</motion.span>
        </motion.span>
      )}
    </motion.div>
  )
}
