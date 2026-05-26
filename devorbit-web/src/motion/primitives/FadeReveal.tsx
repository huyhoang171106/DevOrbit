'use client'

import { motion } from 'framer-motion'
import type { HTMLMotionProps } from 'framer-motion'
import { useReducedMotion } from '../hooks/useReducedMotion'

interface FadeRevealProps extends HTMLMotionProps<'div'> {
  children: React.ReactNode
  /** Offset in px. Default 40 */
  y?: number
  /** Duration in seconds. Default 0.7 */
  duration?: number
  /** Delay in seconds. Default 0 */
  delay?: number
  /** Ease function. Default [0.25, 0.1, 0.25, 1] */
  ease?: [number, number, number, number]
  /** Trigger once vs every time. Default true */
  once?: boolean
  /** Root margin for IntersectionObserver. Default '-80px' */
  margin?: string
  /** Optical center — scale fade origin. Default 1 */
  scale?: number
}

/**
 * Premium fade-up reveal on scroll.
 * GPU accelerated — animates transform and opacity only.
 * Respects reduced motion.
 */
export function FadeReveal({
  children,
  y = 40,
  duration = 0.7,
  delay = 0,
  ease = [0.25, 0.1, 0.25, 1],
  once = true,
  margin = '-80px',
  scale: fadeScale = 1,
  className,
  ...props
}: FadeRevealProps) {
  const prefersReduced = useReducedMotion()

  if (prefersReduced) {
    return <div className={className}>{children}</div>
  }

  return (
    <motion.div
      className={className}
      initial={{ opacity: 0, y, scale: fadeScale }}
      whileInView={{
        opacity: 1,
        y: 0,
        scale: 1,
        transition: { duration, delay, ease },
      }}
      viewport={{ once, margin }}
      {...props}
    >
      {children}
    </motion.div>
  )
}
