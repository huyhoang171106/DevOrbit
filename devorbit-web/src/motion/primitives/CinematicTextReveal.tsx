'use client'

import { motion } from 'framer-motion'
import { useReducedMotion } from '../hooks/useReducedMotion'

interface CinematicTextRevealProps {
  children: string
  as?: 'h1' | 'h2' | 'h3' | 'p' | 'span'
  className?: string
  /** Delay per character stagger. Default 0.03 */
  stagger?: number
  /** Direction: 'up' | 'down'. Default 'up' */
  direction?: 'up' | 'down'
  /** Trigger once. Default true */
  once?: boolean
}

/**
 * Character-by-character cinematic text reveal.
 * Each word fades up in sequence with a subtle transition.
 * Perfect for hero headlines and section titles.
 */
export function CinematicTextReveal({
  children,
  as: Tag = 'h1',
  className,
  stagger = 0.03,
  direction = 'up',
  once = true,
}: CinematicTextRevealProps) {
  const prefersReduced = useReducedMotion()

  if (prefersReduced) {
    return <Tag className={className}>{children}</Tag>
  }

  const words = children.split(' ')
  const y = direction === 'up' ? 40 : -40

  return (
    <Tag className={className}>
      {words.map((word, i) => (
        <span key={i} className="relative inline-block overflow-hidden">
          <motion.span
            className="inline-block"
            initial={{ opacity: 0, y }}
            whileInView={{
              opacity: 1,
              y: 0,
              transition: {
                duration: 0.6,
                delay: i * stagger,
                ease: [0.25, 0.1, 0.25, 1],
              },
            }}
            viewport={{ once, margin: '-40px' }}
          >
            {word}
            {i < words.length - 1 && '\u00A0'}
          </motion.span>
        </span>
      ))}
    </Tag>
  )
}
