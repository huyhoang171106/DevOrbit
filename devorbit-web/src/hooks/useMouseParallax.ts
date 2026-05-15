import { useEffect } from 'react'
import { useMotionValue, useSpring } from 'framer-motion'

export function useMouseParallax() {
  const rawX = useMotionValue(0)
  const rawY = useMotionValue(0)

  const x = useSpring(rawX, { stiffness: 300, damping: 28 })
  const y = useSpring(rawY, { stiffness: 300, damping: 28 })

  useEffect(() => {
    const handleMouse = (e: MouseEvent) => {
      const nx = (e.clientX / window.innerWidth) * 2 - 1
      const ny = (e.clientY / window.innerHeight) * 2 - 1
      rawX.set(nx)
      rawY.set(ny)
    }
    window.addEventListener('mousemove', handleMouse, { passive: true })
    return () => window.removeEventListener('mousemove', handleMouse)
  }, [])

  return { x, y }
}
