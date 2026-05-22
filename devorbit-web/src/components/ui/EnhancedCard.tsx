'use client'

import { type ReactNode, useRef, useState } from 'react'
import { motion } from 'framer-motion'
import { useReducedMotion } from '../../motion/hooks/useReducedMotion'

interface EnhancedCardProps {
  children: ReactNode
  className?: string
  /** Enable glow effect that follows mouse. Default false */
  glow?: boolean
  /** Glow color. Default '#34d399' */
  glowColor?: string
  href?: string
  onClick?: () => void
  /** Scale on hover. Default 1.01 */
  hoverScale?: number
  /** No padding. Default false */
  noPadding?: boolean
}

/**
 * Premium glass card with optional mouse-follow glow border.
 * Uses GPU transforms only. Respects reduced motion.
 */
export function EnhancedCard({
  children,
  className = '',
  glow = false,
  glowColor = '#34d399',
  href,
  onClick,
  hoverScale = 1.01,
  noPadding = false,
}: EnhancedCardProps) {
  const ref = useRef<HTMLDivElement>(null)
  const prefersReduced = useReducedMotion()
  const [mousePos, setMousePos] = useState({ x: 0.5, y: 0.5 })
  const [isHovered, setIsHovered] = useState(false)

  const handleMouseMove = (e: React.MouseEvent) => {
    if (prefersReduced || !glow) return
    const el = ref.current
    if (!el) return
    const rect = el.getBoundingClientRect()
    setMousePos({
      x: (e.clientX - rect.left) / rect.width,
      y: (e.clientY - rect.top) / rect.height,
    })
  }

  const Component = href ? motion.a : motion.div
  const extraProps = href ? { href } : {}

  return (
    <Component
      ref={ref as any}
      className={`relative overflow-hidden orbit-card transition-all duration-500 ${
        onClick || href ? 'cursor-pointer hover:-translate-y-0.5' : ''
      } ${noPadding ? 'p-0' : ''} ${className}`}
      onMouseMove={handleMouseMove}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => { setIsHovered(false); setMousePos({ x: 0.5, y: 0.5 }) }}
      onClick={onClick}
      whileHover={prefersReduced ? undefined : { scale: hoverScale }}
      transition={{ type: 'spring', stiffness: 300, damping: 25 }}
      {...extraProps}
    >
      {/* Glow border effect */}
      {glow && !prefersReduced && (
        <motion.div
          className="absolute inset-0 rounded-4xl pointer-events-none"
          style={{
            background: `radial-gradient(circle at ${mousePos.x * 100}% ${mousePos.y * 100}%, ${glowColor}15, transparent 60%)`,
            opacity: isHovered ? 1 : 0,
            transition: 'opacity 0.4s ease',
          }}
        />
      )}

      {/* Content */}
      <div className="relative z-10">{children}</div>
    </Component>
  )
}
