'use client'

import { useRef, useState, useEffect } from 'react'

interface ScrollProgressOptions {
  /** Offset relative to viewport bottom (0-1). Default 0.85 */
  offset?: number
  /** Throttle interval in ms. Default 100 (~10fps) */
  throttle?: number
}

/**
 * Returns scroll progress (0-1) for a target element.
 * State updates throttled to ~10fps to avoid per-frame React rerenders.
 * Uses IntersectionObserver to enable/disable tracking.
 */
export function useScrollProgress<T extends HTMLElement = HTMLDivElement>(
  options: ScrollProgressOptions = {}
) {
  const { offset = 0.85, throttle = 100 } = options
  const ref = useRef<T>(null)
  const [progress, setProgress] = useState(0)
  const [isInView, setIsInView] = useState(false)
  const lastUpdate = useRef(0)
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null)

  useEffect(() => {
    const el = ref.current
    if (!el) return

    const update = () => {
      const now = performance.now()
      if (now - lastUpdate.current < throttle) return
      lastUpdate.current = now

      const rect = el!.getBoundingClientRect()
      const winH = window.innerHeight
      const triggerPoint = winH * offset
      const total = rect.height + winH
      const current = winH - rect.top + triggerPoint
      setProgress(Math.max(0, Math.min(1, current / total)))
    }

    const observer = new IntersectionObserver(
      ([entry]) => {
        setIsInView(entry.isIntersecting)
        if (entry.isIntersecting) {
          update()
          intervalRef.current = setInterval(update, throttle)
        } else {
          if (intervalRef.current) {
            clearInterval(intervalRef.current)
            intervalRef.current = null
          }
        }
      },
      { threshold: [0, 0.5, 1] }
    )

    observer.observe(el)

    return () => {
      observer.disconnect()
      if (intervalRef.current) {
        clearInterval(intervalRef.current)
        intervalRef.current = null
      }
    }
  }, [offset, throttle])

  return { ref, progress, isInView }
}
