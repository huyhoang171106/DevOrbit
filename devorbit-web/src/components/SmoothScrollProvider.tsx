import { useRef, useEffect } from 'react'
import { ReactLenis } from 'lenis/react'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

interface Props {
  children: React.ReactNode
  /** LERP ratio for smoothness. Lower = smoother. Default 0.08 */
  lerp?: number
}

/**
 * Smooth scroll + GSAP ScrollTrigger integration.
 * - Lenis handles smooth scrolling (its own RAF loop)
 * - GSAP ScrollTrigger is synced on every Lenis scroll event
 * - Proper cleanup prevents orphaned tickers under StrictMode
 * - Exposes `window.__lenis` for global access by motion hooks
 * - Respects prefers-reduced-motion
 */
export function SmoothScrollProvider({ children, lerp = 0.08 }: Props) {
  const tickerRef = useRef<((time: number) => void) | null>(null)
  const scrollRef = useRef<(() => void) | null>(null)
  const disposedRef = useRef(false)

  const prefersReducedMotion =
    typeof window !== 'undefined'
      ? window.matchMedia('(prefers-reduced-motion: reduce)').matches
      : false

  // Single-effect: discover Lenis instance → connect GSAP → cleanup
  useEffect(() => {
    let cancelled = false
    disposedRef.current = false

    const discover = setInterval(() => {
      if (cancelled) return
      const inst = (window as any).__lenis
      if (!inst) return

      clearInterval(discover)

      // 1. Sync ScrollTrigger on every Lenis scroll
      const onScroll = () => ScrollTrigger.update()
      scrollRef.current = onScroll
      inst.on('scroll', onScroll)

      // 2. Drive Lenis from GSAP ticker (single coordinated loop)
      const ticker = (time: number) => inst.raf(time * 1000)
      tickerRef.current = ticker
      gsap.ticker.add(ticker)
    }, 100)

    const safety = setTimeout(() => clearInterval(discover), 5000)

    return () => {
      cancelled = true
      disposedRef.current = true
      clearInterval(discover)
      clearTimeout(safety)

      // Remove GSAP ticker
      if (tickerRef.current) {
        gsap.ticker.remove(tickerRef.current)
        tickerRef.current = null
      }

      // Remove scroll listener from Lenis and clear global reference
      const inst = (window as any).__lenis
      if (inst) {
        if (scrollRef.current) {
          inst.off('scroll', scrollRef.current)
          scrollRef.current = null
        }
        if ((window as any).__lenis === inst) {
          ;(window as any).__lenis = null
        }
      }
    }
  }, [])

  if (prefersReducedMotion) return <>{children}</>

  return (
    <ReactLenis
      root
      ref={(el: any) => {
        // Capture Lenis instance as soon as it mounts
        if (el && typeof el === 'object') {
          const instance = el.__lenis ?? el.lenis ?? el
          if (instance && instance.on) {
            ;(window as any).__lenis = instance
          }
        }
      }}
      options={{
        lerp,
        smoothWheel: true,
        syncTouch: false,
        wheelMultiplier: 0.8,
        touchMultiplier: 1.2,
        infinite: false,
      }}
    >
      {children}
    </ReactLenis>
  )
}
