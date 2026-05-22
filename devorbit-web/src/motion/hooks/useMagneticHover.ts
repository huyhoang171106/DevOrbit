'use client'

import { useRef, useCallback } from 'react'
import { useMotionValue, useSpring } from 'framer-motion'

interface MagneticOptions {
  /** Magnetic pull strength multiplier. Default 0.4 */
  strength?: number
  /** Spring stiffness. Default 300 */
  stiffness?: number
  /** Spring damping. Default 25 */
  damping?: number
}

/**
 * Premium magnetic hover effect for buttons, cards, and interactive elements.
 * Returns motion values + event handlers. Apply to any element.
 */
export function useMagneticHover<T extends HTMLElement = HTMLElement>(
  options: MagneticOptions = {}
) {
  const { strength = 0.4, stiffness = 300, damping = 25 } = options
  const ref = useRef<T>(null)

  const rawX = useMotionValue(0)
  const rawY = useMotionValue(0)
  const x = useSpring(rawX, { stiffness, damping })
  const y = useSpring(rawY, { stiffness, damping })
  const scale = useSpring(1, { stiffness: 400, damping: 20 })

  const handleMouseMove = useCallback(
    (e: React.MouseEvent) => {
      const el = ref.current
      if (!el) return
      const rect = el.getBoundingClientRect()
      const cx = rect.left + rect.width / 2
      const cy = rect.top + rect.height / 2
      const dx = e.clientX - cx
      const dy = e.clientY - cy
      rawX.set(dx * strength)
      rawY.set(dy * strength)
    },
    [strength, rawX, rawY]
  )

  const handleMouseLeave = useCallback(() => {
    rawX.set(0)
    rawY.set(0)
    scale.set(1)
  }, [rawX, rawY, scale])

  const handleMouseEnter = useCallback(() => {
    scale.set(1.02)
  }, [scale])

  return {
    ref,
    x,
    y,
    scale,
    handlers: {
      onMouseMove: handleMouseMove,
      onMouseLeave: handleMouseLeave,
      onMouseEnter: handleMouseEnter,
    },
  }
}
