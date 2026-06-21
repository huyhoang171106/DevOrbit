import { useRef, useEffect } from 'react'
import { ReactLenis } from 'lenis/react'
import { gsap } from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

interface LenisInstance {
  on: (event: string, callback: () => void) => void
  off: (event: string, callback: () => void) => void
  raf: (time: number) => void
}

interface Props {
  children: React.ReactNode
  /** LERP ratio for smoothness. Lower = smoother. Default 0.08 */
  lerp?: number
}

export function SmoothScrollProvider({ children, lerp = 0.08 }: Props) {
  const tickerRef = useRef<((time: number) => void) | null>(null)
  const scrollRef = useRef<(() => void) | null>(null)
  const disposedRef = useRef(false)

  const prefersReducedMotion =
    typeof window !== 'undefined'
      ? window.matchMedia('(prefers-reduced-motion: reduce)').matches
      : false

  useEffect(() => {
    let cancelled = false
    disposedRef.current = false

    const discover = setInterval(() => {
      if (cancelled) return
      const inst = (window as unknown as { __lenis?: LenisInstance }).__lenis
      if (!inst) return

      clearInterval(discover)

      const onScroll = () => ScrollTrigger.update()
      scrollRef.current = onScroll
      inst.on('scroll', onScroll)

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

      if (tickerRef.current) {
        gsap.ticker.remove(tickerRef.current)
        tickerRef.current = null
      }

      const globalRef = window as unknown as { __lenis?: LenisInstance }
      const inst = globalRef.__lenis
      if (inst) {
        if (scrollRef.current) {
          inst.off('scroll', scrollRef.current)
          scrollRef.current = null
        }
        if (globalRef.__lenis === inst) {
          globalRef.__lenis = undefined
        }
      }
    }
  }, [])

  if (prefersReducedMotion) return <>{children}</>

  return (
    <ReactLenis
      root
      options={{
        lerp,
        wheelMultiplier: 0.8,
        touchMultiplier: 1.2,
      }}
    >
      {children}
    </ReactLenis>
  )
}
