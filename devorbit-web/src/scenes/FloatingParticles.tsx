'use client'

import { useRef, useMemo, useEffect } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'
import { usePerformanceProfile } from '../performance'

interface FloatingParticlesProps {
  count?: number
  radius?: number
  color?: string
  size?: number
  speed?: number
  mouseRadius?: number
}

/**
 * Floating particles in a toroidal cloud.
 * Profile-driven particle count. No per-frame allocations.
 * Mouse drift on desktop only (pointer:fine).
 * Reduced-motion → no particles.
 */
export function FloatingParticles({
  count = 2000,
  radius = 30,
  color = '#34d399',
  size = 0.15,
  speed = 0.15,
  mouseRadius = 3,
}: FloatingParticlesProps) {
  const ref = useRef<THREE.Points>(null)
  const mouseRef = useRef({ x: 0, y: 0 })
  const timeRef = useRef(0)
  const prof = usePerformanceProfile()

  const scaledCount = Math.max(50, Math.floor(count * prof.particleMultiplier))
  const hasMouse = typeof window !== 'undefined'
    && window.matchMedia('(pointer: fine)').matches
    && prof.tier !== 'low'

  // Mouse tracking — effect runs once
  useEffect(() => {
    if (!hasMouse) return
    const handleMouse = (e: MouseEvent) => {
      mouseRef.current.x = (e.clientX / window.innerWidth - 0.5) * 2
      mouseRef.current.y = (e.clientY / window.innerHeight - 0.5) * 2
    }
    window.addEventListener('mousemove', handleMouse, { passive: true })
    return () => window.removeEventListener('mousemove', handleMouse)
  }, [hasMouse])

  const [positions, colors, sizes, velocities] = useMemo(() => {
    const pos = new Float32Array(scaledCount * 3)
    const col = new Float32Array(scaledCount * 3)
    const siz = new Float32Array(scaledCount)
    const vel = new Float32Array(scaledCount * 3)

    const baseColor = new THREE.Color(color)
    const altColor = new THREE.Color('#0ea5e9')

    for (let i = 0; i < scaledCount; i++) {
      const i3 = i * 3
      const theta = Math.random() * Math.PI * 2
      const phi = Math.acos(2 * Math.random() - 1)
      const r = radius * (0.5 + Math.random() * 0.5)
      const torusR = r * 0.6
      const torusAngle = Math.random() * Math.PI * 2

      pos[i3] = (r + torusR * Math.cos(torusAngle)) * Math.sin(theta) * Math.cos(phi)
      pos[i3 + 1] = torusR * Math.sin(torusAngle) * 0.8
      pos[i3 + 2] = (r + torusR * Math.cos(torusAngle)) * Math.sin(phi)

      const blend = Math.random()
      const c = baseColor.clone().lerp(altColor, blend * 0.4)
      col[i3] = c.r
      col[i3 + 1] = c.g
      col[i3 + 2] = c.b
      siz[i] = size * (0.5 + Math.random() * 1.0)
      vel[i3] = (Math.random() - 0.5) * 0.02
      vel[i3 + 1] = (Math.random() - 0.5) * 0.02
      vel[i3 + 2] = (Math.random() - 0.5) * 0.02
    }
    return [pos, col, siz, vel]
  }, [scaledCount, radius, color, size])

  useFrame((_, delta) => {
    if (!ref.current || prof.prefersReducedMotion) return

    timeRef.current += delta * speed
    const posAttr = ref.current.geometry.attributes.position
    const arr = posAttr.array as Float32Array
    const mx = mouseRef.current.x * mouseRadius * prof.particleMultiplier
    const my = mouseRef.current.y * mouseRadius * prof.particleMultiplier
    const t = timeRef.current

    for (let i = 0; i < scaledCount; i++) {
      const i3 = i * 3
      arr[i3] += Math.sin(t + i * 0.01) * 0.003 + velocities[i3]
      arr[i3 + 1] += Math.cos(t * 0.7 + i * 0.015) * 0.003 + velocities[i3 + 1]
      arr[i3 + 2] += Math.sin(t * 0.5 + i * 0.02) * 0.003 + velocities[i3 + 2]

      if (hasMouse && (mx !== 0 || my !== 0)) {
        arr[i3] += (mx - arr[i3] * 0.01) * 0.0005
        arr[i3 + 1] += (my - arr[i3 + 1] * 0.01) * 0.0005
      }

      const dx = arr[i3], dy = arr[i3 + 1], dz = arr[i3 + 2]
      const dist = Math.sqrt(dx * dx + dy * dy + dz * dz)
      if (dist > radius * 1.5) {
        arr[i3] *= 0.99
        arr[i3 + 1] *= 0.99
        arr[i3 + 2] *= 0.99
      }
    }

    posAttr.needsUpdate = true
  })

  if (prof.prefersReducedMotion) return null

  return (
    <points ref={ref}>
      <bufferGeometry>
        <bufferAttribute attach="attributes-position" args={[positions, 3]} />
        <bufferAttribute attach="attributes-color" args={[colors, 3]} />
        <bufferAttribute attach="attributes-size" args={[sizes, 1]} />
      </bufferGeometry>
      <pointsMaterial
        size={size}
        vertexColors
        transparent
        opacity={0.85}
        blending={THREE.AdditiveBlending}
        depthWrite={false}
        sizeAttenuation
      />
    </points>
  )
}
