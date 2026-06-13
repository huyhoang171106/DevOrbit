
import { useRef, useEffect } from 'react'
import { useThree, useFrame } from '@react-three/fiber'
import * as THREE from 'three'

interface CameraControllerProps {
  speed?: number
  radius?: number
  height?: number
  target?: [number, number, number]
  mouseParallax?: boolean
}

/**
 * Cinematic orbiting camera following a gentle Lissajous curve.
 * No per-frame allocations — temp vectors are reused via refs.
 * Respects prefers-reduced-motion.
 */
export function CameraController({
  speed = 0.08,
  radius = 45,
  height = 0,
  target: lookAt = [0, 0, 0],
  mouseParallax = true,
}: CameraControllerProps) {
  const { camera } = useThree()
  const mouseRef = useRef({ x: 0, y: 0 })
  const timeRef = useRef(0)
  const prefersReduced = useRef(false)
  const targetPos = useRef<THREE.Vector3 | null>(null)
  if (targetPos.current === null) {
    targetPos.current = new THREE.Vector3()
  }

  useEffect(() => {
    const mq = window.matchMedia('(prefers-reduced-motion: reduce)')
    prefersReduced.current = mq.matches

    const handleMouse = (e: MouseEvent) => {
      if (!mouseParallax || prefersReduced.current) return
      mouseRef.current.x = (e.clientX / window.innerWidth - 0.5) * 0.3
      mouseRef.current.y = (e.clientY / window.innerHeight - 0.5) * 0.3
    }

    window.addEventListener('mousemove', handleMouse, { passive: true })
    return () => window.removeEventListener('mousemove', handleMouse)
  }, [mouseParallax])

  useFrame((_, delta) => {
    if (prefersReduced.current) return
    timeRef.current += delta * speed

    const t = timeRef.current
    const tp = targetPos.current
    if (!tp) return
    const mx = mouseRef.current.x * 8
    const my = mouseRef.current.y * 4

    // Lissajous curve — no allocations
    tp.set(
      Math.sin(t * 0.7) * radius + mx,
      Math.sin(t * 0.3) * (radius * 0.25) + height + my,
      Math.cos(t * 0.5) * radius,
    )

    camera.position.lerp(tp, 0.02)
    camera.lookAt(lookAt[0], lookAt[1], lookAt[2])
  })

  return null
}
