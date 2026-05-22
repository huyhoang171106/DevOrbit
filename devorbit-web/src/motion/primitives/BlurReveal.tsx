'use client'

import { motion } from 'framer-motion'
import type { HTMLMotionProps } from 'framer-motion'
import { useReducedMotion } from '../hooks/useReducedMotion'

interface BlurRevealProps extends HTMLMotionProps<'div'> {
  children: React.ReactNode
  /** Blur amount in px. Default 12 */
  blur?: number
  /** Duration in seconds. Default 0.8 */
  duration?: number
  /** Delay in seconds. Default 0 */
  delay?: number
  /** Offset in px. Default 0 */
  y?: number
  /** Trigger once. Default true */
  once?: boolean
}

/**
 * Cinematic blur-to-clear reveal — like a lens focusing.
 * Perfect for hero text, headings, and premium reveals.
 * GPU accelerated.
 */
export function BlurReveal({
  children,
  blur = 12,
  duration = 0.8,
  delay = 0,
  y: offsetY = 0,
  once = true,
  className,
  ...props
}: BlurRevealProps) {
  const prefersReduced = useReducedMotion()

  if (prefersReduced) {
    return <div className={className}>{children}</div>
  }

  return (
    <motion.div
      className={className}
      initial={{ opacity: 0, filter: `blur(${blur}px)`, y: offsetY }}
      whileInView={{
        opacity: 1,
        filter: 'blur(0px)',
        y: 0,
        transition: { duration, delay, ease: [0.25, 0.1, 0.25, 1] },
      }}
      viewport={{ once, margin: '-60px' }}
      {...props}
    >
      {children}
    </motion.div>
  )
}
