'use client'

import { motion, type Variants } from 'framer-motion'
import type { HTMLMotionProps } from 'framer-motion'
import { useReducedMotion } from '../hooks/useReducedMotion'

interface StaggerRevealProps extends HTMLMotionProps<'div'> {
  children: React.ReactNode
  /** Stagger delay between children. Default 0.06 */
  stagger?: number
  /** Delay before first child. Default 0 */
  delay?: number
  /** Distance in px. Default 24 */
  y?: number
  /** Once vs every time. Default true */
  once?: boolean
}

/**
 * Stagger-reveal container for lists, grids, and sequential content.
 * Each child fades up in sequence.
 */
export function StaggerReveal({
  children,
  stagger = 0.06,
  delay = 0,
  y = 24,
  once = true,
  className,
  ...props
}: StaggerRevealProps) {
  const prefersReduced = useReducedMotion()

  const variants: Variants = {
    hidden: { opacity: 0, y: prefersReduced ? 0 : y },
    visible: {
      opacity: 1,
      y: 0,
      transition: {
        staggerChildren: stagger,
        delayChildren: delay,
      },
    },
  }

  return (
    <motion.div
      className={className}
      variants={variants}
      initial="hidden"
      whileInView="visible"
      viewport={{ once, margin: '-60px' }}
      {...props}
    >
      {children}
    </motion.div>
  )
}

/**
 * Individual stagger item. Wraps each child in the stagger.
 */
export function StaggerItem({
  children,
  className,
  ...props
}: HTMLMotionProps<'div'> & { children: React.ReactNode }) {
  const prefersReduced = useReducedMotion()

  return (
    <motion.div
      className={className}
      variants={{
        hidden: { opacity: 0, y: prefersReduced ? 0 : 24 },
        visible: {
          opacity: 1,
          y: 0,
          transition: {
            duration: 0.6,
            ease: [0.25, 0.1, 0.25, 1],
          },
        },
      }}
      {...props}
    >
      {children}
    </motion.div>
  )
}
