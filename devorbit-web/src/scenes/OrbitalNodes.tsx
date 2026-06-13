
import { useRef, useMemo } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'
import { Line } from '@react-three/drei'
import { usePerformanceProfile } from '../performance/usePerformanceProfile'

interface OrbitalNodesProps {
  count?: number
  maxRadius?: number
  minRadius?: number
  colors?: string[]
  showOrbits?: boolean
  nodeSize?: number
}

interface OrbitalNodeProps {
  node: { radius: number; color: string }
  nodeSize: number
  texture?: THREE.Texture
  hasGlow: boolean
}

function OrbitalNode({ node, nodeSize, texture, hasGlow }: OrbitalNodeProps) {
  return (
    <mesh position={[node.radius, 0, 0]}>
      <sphereGeometry args={[nodeSize, 8, 8]} />
      <meshBasicMaterial color={node.color} transparent opacity={0.7} />
      {hasGlow && texture && (
        <sprite scale={[nodeSize * 3, nodeSize * 3, 1]}>
          <spriteMaterial
            map={texture}
            transparent
            opacity={0.3}
            blending={THREE.AdditiveBlending}
            depthWrite={false}
          />
        </sprite>
      )}
    </mesh>
  )
}

/**
 * Orbital course nodes — each on its own ring.
 * - Shared glow textures per color (no per-node canvas allocation)
 * - Profile-driven node count
 * - Sprite glow hidden on low-tier devices
 * - Reduced-motion → static
 */
export function OrbitalNodes({
  count = 30,
  maxRadius = 35,
  minRadius = 5,
  colors = ['#34d399', '#0ea5e9', '#818cf8', '#f59e0b', '#ec4899'],
  showOrbits = true,
  nodeSize = 0.3,
}: OrbitalNodesProps) {
  const groupRef = useRef<THREE.Group>(null)
  const timeRef = useRef(0)
  const prof = usePerformanceProfile()

  const scaledCount = Math.max(4, Math.floor(count * prof.particleMultiplier))
  const hasGlow = prof.tier !== 'low'

  // Shared glow textures per color (computed once)
  const glowTextures = useMemo(() => {
    const map = new Map<string, THREE.Texture>()
    for (const c of colors) {
      const canvas = document.createElement('canvas')
      canvas.width = 32
      canvas.height = 32
      const ctx = canvas.getContext('2d')
      if (ctx) {
        const g = ctx.createRadialGradient(16, 16, 0, 16, 16, 16)
        g.addColorStop(0, `${c}60`)
        g.addColorStop(1, 'rgba(0,0,0,0)')
        ctx.fillStyle = g
        ctx.fillRect(0, 0, 32, 32)
      }
      const tex = new THREE.CanvasTexture(canvas)
      tex.needsUpdate = true
      map.set(c, tex)
    }
    return map
  }, [colors])

  const nodes = useMemo(() => {
    const result: { radius: number; speed: number; phase: number; color: string }[] = []
    for (let i = 0; i < scaledCount; i++) {
      result.push({
        radius: minRadius + (maxRadius - minRadius) * (i / scaledCount),
        speed: 0.1 + Math.random() * 0.3,
        phase: Math.random() * Math.PI * 2,
        color: colors[i % colors.length],
      })
    }
    return result
  }, [scaledCount, minRadius, maxRadius, colors])

  // Orbit ring paths
  const orbitRings = useMemo(() => {
    if (!showOrbits || !hasGlow) return []
    return nodes.map((node) => {
      const segments = 48
      const points: [number, number, number][] = []
      for (let i = 0; i <= segments; i++) {
        const angle = (i / segments) * Math.PI * 2
        points.push([node.radius * Math.cos(angle), 0, node.radius * Math.sin(angle)])
      }
      return { points, color: node.color }
    })
  }, [nodes, showOrbits, hasGlow])

  useFrame((_, delta) => {
    if (!groupRef.current || prof.prefersReducedMotion) return
    timeRef.current += delta
    const children = groupRef.current.children

    for (let i = 0; i < nodes.length; i++) {
      const mesh = children[i * 2] as THREE.Mesh
      if (mesh) {
        const angle = timeRef.current * nodes[i].speed + nodes[i].phase
        mesh.position.x = nodes[i].radius * Math.cos(angle)
        mesh.position.z = nodes[i].radius * Math.sin(angle)
      }
    }
  })

  if (prof.prefersReducedMotion) return null

  return (
    <group ref={groupRef}>
      {/* Orbit rings */}
      {orbitRings.map((ring, i) => (
        <Line
          key={`ring-${i}`}
          points={ring.points}
          color={ring.color}
          transparent
          opacity={0.06}
          lineWidth={0.5}
        />
      ))}

      {/* Orbiting nodes */}
      {nodes.map((node, i) => (
        <OrbitalNode
          key={`node-${i}`}
          node={node}
          nodeSize={nodeSize}
          texture={glowTextures.get(node.color)}
          hasGlow={hasGlow}
        />
      ))}
    </group>
  )
}
