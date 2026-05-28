'use client'

import { useEffect, useRef } from 'react'
import { useMotionValue, useSpring, useTransform } from 'framer-motion'

interface ParallaxOptions {
  /** Parallax speed multiplier. Default 0.4 */
  speed?: number
  /** Spring stiffness. Default 200 */
  stiffness?: number
  /** Spring damping. Default 20 */
  damping?: number
}

/**
 * Parallax motion value tied to page scroll.
 * Uses single listener: Lenis if available, otherwise window scroll.
 * Does NOT register duplicate listeners.
 */
export function useParallaxScroll(options: ParallaxOptions = {}) {
  const { speed = 0.4, stiffness = 200, damping = 20 } = options
  const scrollY = useMotionValue(0)
  const smoothScrollY = useSpring(scrollY, { stiffness, damping })
  const y = useTransform(smoothScrollY, (v) => v * speed)
  const ref = useRef<HTMLDivElement>(null)
  const lenisSub = useRef<(() => void) | null>(null)

  useEffect(() => {
    const lenis = (window as any).__lenis

    if (lenis) {
      // Single Lenis listener only
      const onScroll = (e: any) => scrollY.set(e.scroll)
      lenis.on('scroll', onScroll)
      lenisSub.current = () => lenis.off('scroll', onScroll)
      // Set initial value
      scrollY.set(lenis.scroll ?? window.scrollY)
    } else {
      // Fallback to window scroll
      const onScroll = () => scrollY.set(window.scrollY)
      scrollY.set(window.scrollY)
      window.addEventListener('scroll', onScroll, { passive: true })
      lenisSub.current = () => window.removeEventListener('scroll', onScroll)
    }

    return () => {
      lenisSub.current?.()
      lenisSub.current = null
    }
  }, [scrollY])

  return { ref, y, scrollY: smoothScrollY }
}
