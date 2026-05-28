'use client'

import { useEffect, useRef } from 'react'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { useReducedMotion } from '../hooks/useReducedMotion'

gsap.registerPlugin(ScrollTrigger)

interface GsapScrollConfig {
  /** CSS selector or ref for trigger element */
  trigger: string | HTMLElement
  /** Animation type */
  type: 'fadeIn' | 'parallax' | 'scaleIn' | 'clipReveal' | 'custom'
  /** Timeline construction fn for 'custom' type */
  buildTimeline?: (tl: gsap.core.Timeline, trigger: HTMLElement) => void
  /** Start position. Default 'top 85%' */
  start?: string
  /** End position. Default 'top 15%' */
  end?: string
  /** Pin the section during animation. Default false */
  pin?: boolean
  /** Markers for debugging. Default false */
  markers?: boolean
  /** Scrub (link to scroll). Default true */
  scrub?: boolean | number
  /** Toggle actions. Default 'play none none none' */
  toggleActions?: string
  /** Horizontal offset for parallax. Default 0 */
  x?: number
  /** Vertical offset for parallax. Default 0 */
  y?: number
  /** Opacity target. Default 1 */
  opacity?: number
  /** Scale target. Default 1 */
  scale?: number
}

/**
 * Registers a GSAP ScrollTrigger animation.
 * Automatically cleans up on unmount.
 * Respects reduced motion.
 */
export function useGsapScroll(config: GsapScrollConfig) {
  const prefersReduced = useReducedMotion()
  const animRef = useRef<gsap.core.Tween | gsap.core.Timeline | null>(null)
  const triggerRef = useRef<ScrollTrigger | null>(null)

  useEffect(() => {
    if (prefersReduced) return

    const triggerEl =
      typeof config.trigger === 'string'
        ? document.querySelector(config.trigger)
        : config.trigger

    if (!triggerEl) return

    const safeStart = config.start ?? 'top 85%'
    const safeEnd = config.end ?? 'top 15%'

    if (config.type === 'custom' && config.buildTimeline) {
      const tl = gsap.timeline({
        scrollTrigger: {
          trigger: triggerEl,
          start: safeStart,
          end: safeEnd,
          scrub: config.scrub ?? true,
          pin: config.pin ?? false,
          markers: config.markers ?? false,
          toggleActions: config.toggleActions ?? 'play none none none',
          invalidateOnRefresh: true,
        },
      })
      config.buildTimeline(tl, triggerEl as HTMLElement)
      animRef.current = tl
      triggerRef.current = tl.scrollTrigger!
    } else {
      const vars: gsap.TweenVars = {
        scrollTrigger: {
          trigger: triggerEl,
          start: safeStart,
          end: safeEnd,
          scrub: config.scrub ?? true,
          pin: config.pin ?? false,
          markers: config.markers ?? false,
          toggleActions: config.toggleActions ?? 'play none none none',
          invalidateOnRefresh: true,
        },
      }

      switch (config.type) {
        case 'fadeIn':
          Object.assign(vars, {
            opacity: config.opacity ?? 1,
            y: config.y ?? 0,
          })
          gsap.set(triggerEl, { opacity: 0, y: config.y ?? 60 })
          break
        case 'parallax':
          Object.assign(vars, {
            y: config.y ?? 0,
            ease: 'none',
          })
          break
        case 'scaleIn':
          Object.assign(vars, {
            opacity: config.opacity ?? 1,
            scale: config.scale ?? 1,
          })
          gsap.set(triggerEl, { opacity: 0, scale: 0.9 })
          break
        case 'clipReveal':
          // Use clip-path reveal — set initial state
          gsap.set(triggerEl, { clipPath: 'inset(0 100% 0 0)' })
          Object.assign(vars, {
            clipPath: 'inset(0 0% 0 0)',
            ease: 'none',
          })
          break
      }

      animRef.current = gsap.to(triggerEl, vars)
      // @ts-expect-error ScrollTrigger instance from gsap.to
      triggerRef.current = animRef.current.scrollTrigger
    }

    return () => {
      if (triggerRef.current) {
        triggerRef.current.kill()
      }
      if (animRef.current) {
        animRef.current.kill()
      }
      ScrollTrigger.refresh()
    }
  }, [prefersReduced])
}

/**
 * Manually refresh ScrollTrigger (call after layout changes).
 */
export function refreshGsapScroll() {
  ScrollTrigger.refresh()
}
