
import { useRef, useMemo } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'
import { usePerformanceProfile } from '../performance/usePerformanceProfile'

interface KnowledgeGalaxyProps {
  starCount?: number
  radius?: number
  arms?: number
  rotationSpeed?: number
  color1?: string
  color2?: string
}

/**
 * Logarithmic spiral galaxy.
 * - Group rotation only (no per-frame size attribute mutation — major perf win)
 * - Profile-driven star count
 * - Reduced-motion → static
 */
export function KnowledgeGalaxy({
  starCount = 500,
  radius = 40,
  arms = 4,
  rotationSpeed = 0.02,
  color1 = '#34d399',
  color2 = '#818cf8',
}: KnowledgeGalaxyProps) {
  const ref = useRef<THREE.Points>(null)
  const prof = usePerformanceProfile()

  const scaledCount = Math.max(50, Math.floor(starCount * prof.particleMultiplier))

  const rotationSpeedScaled = rotationSpeed * (prof.tier === 'high' ? 1 : 0.5)

  const [positions, colors] = useMemo(() => {
    const pos = new Float32Array(scaledCount * 3)
    const col = new Float32Array(scaledCount * 3)

    const c1 = new THREE.Color(color1)
    const c2 = new THREE.Color(color2)
    const coreColor = new THREE.Color('#ffffff')

    for (let i = 0; i < scaledCount; i++) {
      const i3 = i * 3
      const isCore = i < scaledCount * 0.15

      if (isCore) {
        const r = radius * 0.15 * Math.pow(Math.random(), 2)
        const theta = Math.random() * Math.PI * 2
        const phi = Math.acos(2 * Math.random() - 1)
        pos[i3] = r * Math.sin(theta) * Math.cos(phi)
        pos[i3 + 1] = r * Math.sin(phi) * 0.3
        pos[i3 + 2] = r * Math.cos(theta) * Math.cos(phi)

        const brightness = 0.6 + Math.random() * 0.4
        col[i3] = coreColor.r * brightness
        col[i3 + 1] = coreColor.g * brightness
        col[i3 + 2] = coreColor.b * brightness
      } else {
        const armIndex = Math.floor(Math.random() * arms)
        const armAngle = (armIndex / arms) * Math.PI * 2
        const spiralT = 0.15 + Math.random() * 0.85
        const r = spiralT * radius
        const scatter = (1 - spiralT) * 3 + 0.5
        const theta = spiralT * 3 * Math.PI + armAngle + (Math.random() - 0.5) * scatter * 0.3

        pos[i3] = r * Math.cos(theta)
        pos[i3 + 1] = (Math.random() - 0.5) * 2 * (1 - spiralT * 0.5)
        pos[i3 + 2] = r * Math.sin(theta)

        const blend = spiralT
        const c = c1.clone().lerp(c2, blend)
        const brightness = 0.3 + (1 - spiralT) * 0.7
        col[i3] = c.r * brightness
        col[i3 + 1] = c.g * brightness
        col[i3 + 2] = c.b * brightness
      }
    }

    return [pos, col]
  }, [scaledCount, radius, arms, color1, color2])

  useFrame((_, delta) => {
    if (!ref.current || prof.prefersReducedMotion) return
    ref.current.rotation.y += delta * rotationSpeedScaled
  })

  if (prof.prefersReducedMotion) return null

  return (
    <points ref={ref}>
      <bufferGeometry>
        <bufferAttribute attach="attributes-position" args={[positions, 3]} />
        <bufferAttribute attach="attributes-color" args={[colors, 3]} />
      </bufferGeometry>
      <pointsMaterial
        size={0.2}
        vertexColors
        transparent
        opacity={0.8}
        blending={THREE.AdditiveBlending}
        depthWrite={false}
        sizeAttenuation
      />
    </points>
  )
}
