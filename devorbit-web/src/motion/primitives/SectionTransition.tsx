'use client'

import { useRef, useEffect, useCallback } from 'react'
import { useReducedMotion } from '../hooks/useReducedMotion'

interface SectionTransitionProps {
  children: React.ReactNode
  className?: string
  /** Optional ambient effect class for background changes */
  atmosphere?: 'light' | 'deep' | 'glow' | 'none'
}

/**
 * Cinematic section wrapper that provides:
 * - scroll-linked atmosphere changes via data attributes
 * - section lifecycle events for GSAP ScrollTriggers
 * - performance isolation (content-visibility)
 */
export function SectionTransition({
  children,
  className = '',
  atmosphere = 'none',
}: SectionTransitionProps) {
  const ref = useRef<HTMLElement>(null)
  const prefersReduced = useReducedMotion()

  const updateAtmosphere = useCallback((isInView: boolean) => {
    if (atmosphere === 'none') return
    if (!ref.current) return
    ref.current.dataset.atmosphere = isInView ? atmosphere : 'none'
  }, [atmosphere])

  useEffect(() => {
    const el = ref.current
    if (!el || prefersReduced) return

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          updateAtmosphere(true)
          el.dataset.sectionActive = 'true'
        } else {
          updateAtmosphere(false)
          el.dataset.sectionActive = 'false'
        }
      },
      { threshold: [0, 0.3, 0.7, 1] }
    )

    observer.observe(el)

    return () => observer.disconnect()
  }, [prefersReduced, updateAtmosphere])

  return (
    <section
      ref={ref}
      className={`relative w-full overflow-hidden ${className}`}
      data-atmosphere={atmosphere === 'none' ? undefined : 'none'}
      data-section-active="false"
      style={{ contentVisibility: 'auto', containIntrinsicSize: 'auto 600px' }}
    >
      {children}
    </section>
  )
}
