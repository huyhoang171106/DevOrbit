'use client'

import { type ReactNode } from 'react'
import { motion } from 'framer-motion'
import { useMagneticHover } from '../../motion/hooks/useMagneticHover'
import { useReducedMotion } from '../../motion/hooks/useReducedMotion'

interface MagneticButtonProps {
  children: ReactNode
  variant?: 'primary' | 'secondary' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  icon?: ReactNode
  href?: string
  onClick?: () => void
  className?: string
  disabled?: boolean
  type?: 'button' | 'submit'
}

const variantStyles = {
  primary:
    'bg-orbit-accent text-zinc-950 font-bold shadow-glow hover:shadow-glow-lg hover:bg-emerald-300',
  secondary:
    'bg-transparent text-orbit-text border border-orbit-border hover:border-orbit-accent/40 hover:bg-orbit-accent-subtle hover:text-orbit-accent',
  ghost:
    'text-orbit-text-secondary hover:bg-orbit-surface hover:text-orbit-text',
}

const sizeStyles = {
  sm: 'px-5 py-3 text-[11px] gap-2 rounded-2xl',
  md: 'px-8 py-4 text-[13px] gap-3 rounded-3xl',
  lg: 'px-12 py-6 text-[14px] gap-4 rounded-4xl',
}

/**
 * Premium magnetic button with hover parallax and spring scale.
 * GPU accelerated — uses transform and opacity only.
 * Respects reduced motion.
 */
export function MagneticButton({
  children,
  variant = 'primary',
  size = 'md',
  icon,
  href,
  onClick,
  className = '',
  disabled = false,
  type = 'button',
}: MagneticButtonProps) {
  const prefersReduced = useReducedMotion()
  const { ref, x, y, scale, handlers } = useMagneticHover<HTMLAnchorElement | HTMLButtonElement>({
    strength: 0.3,
    stiffness: 250,
    damping: 20,
  })

  const baseClass = `relative inline-flex items-center justify-center uppercase tracking-wider font-bold transition-colors duration-300 ${variantStyles[variant]} ${sizeStyles[size]} ${className}`

  const content = (
    <motion.span
      className="relative z-10 inline-flex items-center gap-2"
      style={prefersReduced ? undefined : { x, y, scale }}
    >
      {icon && <span className="shrink-0">{icon}</span>}
      {children}
    </motion.span>
  )

  if (href) {
    return (
      <motion.a
        ref={ref as any}
        href={href}
        className={baseClass}
        style={prefersReduced ? undefined : { scale }}
        {...(!prefersReduced ? handlers : {})}
        whileTap={prefersReduced ? undefined : { scale: 0.97 }}
        aria-disabled={disabled}
      >
        {content}
      </motion.a>
    )
  }

  return (
    <motion.button
      ref={ref as any}
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={baseClass}
      style={prefersReduced ? undefined : { scale }}
      {...(!prefersReduced ? handlers : {})}
      whileTap={prefersReduced ? undefined : { scale: 0.97 }}
    >
      {content}
    </motion.button>
  )
}
