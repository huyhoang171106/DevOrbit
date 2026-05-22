'use client'

import { useEffect } from 'react'
import { useMotionValue, useSpring } from 'framer-motion'

/**
 * Maps device orientation / mouse position to smooth tilt values.
 * Creates a subtle spatial depth effect for parallax layers.
 */
export function useDeviceTilt() {
  const rawX = useMotionValue(0)
  const rawY = useMotionValue(0)

  const tiltX = useSpring(rawX, { stiffness: 200, damping: 30 })
  const tiltY = useSpring(rawY, { stiffness: 200, damping: 30 })

  useEffect(() => {
    const handleMouse = (e: MouseEvent) => {
      const nx = (e.clientX / window.innerWidth) * 2 - 1
      const ny = (e.clientY / window.innerHeight) * 2 - 1
      rawX.set(nx * 8) // max ±8 degrees
      rawY.set(ny * -8)
    }

    const handleOrientation = (e: DeviceOrientationEvent) => {
      if (e.gamma !== null && e.beta !== null) {
        const nx = Math.max(-1, Math.min(1, (e.gamma || 0) / 45))
        const ny = Math.max(-1, Math.min(1, ((e.beta || 0) - 45) / 45))
        rawX.set(nx * 5)
        rawY.set(ny * 5)
      }
    }

    // Use rAF for smooth reading
    // (motion values are set directly, springs handle interpolation)

    window.addEventListener('mousemove', handleMouse, { passive: true })
    window.addEventListener('deviceorientation', handleOrientation, { passive: true })

    return () => {
      window.removeEventListener('mousemove', handleMouse)
      window.removeEventListener('deviceorientation', handleOrientation)
    }
  }, [rawX, rawY])

  return { tiltX, tiltY }
}
