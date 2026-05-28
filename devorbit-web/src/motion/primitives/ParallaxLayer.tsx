'use client'

import { motion, useScroll, useTransform } from 'framer-motion'
import { useRef } from 'react'
import { useReducedMotion } from '../hooks/useReducedMotion'

interface ParallaxLayerProps {
  children: React.ReactNode
  /** Speed: 0.5 = half scroll speed, -0.5 = reverse. Default 0.3 */
  speed?: number
  /** Offset range in px. Default 150 */
  range?: number
  className?: string
}

/**
 * A parallax layer that moves at a different speed than scroll.
 * Optimized — uses useScroll + useTransform from Framer Motion (no rAF).
 */
export function ParallaxLayer({
  children,
  speed = 0.3,
  range = 150,
  className,
}: ParallaxLayerProps) {
  const ref = useRef<HTMLDivElement>(null)
  const prefersReduced = useReducedMotion()

  const { scrollYProgress } = useScroll({
    target: ref,
    offset: ['start end', 'end start'],
  })

  const y = useTransform(
    scrollYProgress,
    [0, 1],
    prefersReduced ? [0, 0] : [range * speed, -range * speed]
  )

  return (
    <motion.div ref={ref} className={className} style={{ y }}>
      {children}
    </motion.div>
  )
}
