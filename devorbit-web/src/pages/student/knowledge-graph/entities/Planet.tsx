import { useRef, useMemo } from 'react'
import { useFrame } from '@react-three/fiber'
import { MeshDistortMaterial } from '@react-three/drei'
import * as THREE from 'three'
import { useGalaxyStore } from '../store/useGalaxyStore'
import type { GalaxyNode } from '../layout/types'

type PlanetProps = {
  node: GalaxyNode
  color: string
  onClick: (id: number) => void
  opacity?: number
}

export function Planet({ node, color, onClick, opacity = 1 }: PlanetProps) {
  const meshRef = useRef<THREE.Mesh>(null)
  const ringRef = useRef<THREE.Mesh>(null)
  const focusNodeId = useGalaxyStore((s) => s.focusNodeId)
  const hoveredNodeId = useGalaxyStore((s) => s.hoveredNodeId)
  const setHoveredNode = useGalaxyStore((s) => s.setHoveredNode)
  const failedNodes = useGalaxyStore((s) => s.failedNodes)
  const blockedNodes = useGalaxyStore((s) => s.blockedNodes)
  const isTimeTravelMode = useGalaxyStore((s) => s.isTimeTravelMode)
  const currentSemester = useGalaxyStore((s) => s.currentSemester)

  const isFailed = failedNodes.has(node.id)
  const isBlocked = blockedNodes.has(node.id) && !isFailed
  const isFocused = focusNodeId === node.id
  const isHovered = hoveredNodeId === node.id
  const scale = isFocused ? 1.6 : isHovered ? 1.3 : 1

  const ttOpacity = useMemo(() => {
    if (!isTimeTravelMode) return opacity
    if (node.level > currentSemester) return 0
    if (node.level === currentSemester) return opacity * 0.4
    return opacity
  }, [isTimeTravelMode, currentSemester, node.level, opacity])

  if (ttOpacity <= 0) return null

  const { displayColor, emissiveIntensity, glowOpacity } = useMemo(() => {
    if (isFailed) {
      return { displayColor: '#f43f5e', emissiveIntensity: 3, glowOpacity: 0.4 }
    }
    if (isBlocked) {
      return { displayColor: '#fb7185', emissiveIntensity: 1.5, glowOpacity: 0.2 }
    }
    const base = 0.15 + (node.impactScore / 10) * 0.85
    return { displayColor: color, emissiveIntensity: base * 2, glowOpacity: 0.08 + base * 0.12 }
  }, [isFailed, isBlocked, color, node.impactScore])

  useFrame((state) => {
    if (!meshRef.current || !ringRef.current) return
    const t = state.clock.getElapsedTime()
    meshRef.current.position.y = Math.sin(t * 0.5 + node.id) * 0.15
    ringRef.current.rotation.z = t * 0.2
    ringRef.current.rotation.x = Math.sin(t * 0.1) * 0.3
  })

  return (
    <group scale={scale}>
      {/* orbit ring */}
      <mesh ref={ringRef} rotation={[Math.PI / 2, 0, 0]}>
        <ringGeometry args={[node.size + 0.2, node.size + 0.35, 32]} />
        <meshBasicMaterial
          color={displayColor}
          transparent
          opacity={(isFailed ? 0.4 : 0.15) * ttOpacity}
          side={THREE.DoubleSide}
          depthWrite={false}
        />
      </mesh>

      {/* planet body */}
      <mesh
        ref={meshRef}
        onClick={(e) => { e.stopPropagation(); onClick(node.id) }}
        onPointerEnter={() => setHoveredNode(node.id)}
        onPointerLeave={() => setHoveredNode(null)}
      >
        <sphereGeometry args={[node.size, 32, 32]} />
        <MeshDistortMaterial
          color={displayColor}
          emissive={displayColor}
          emissiveIntensity={emissiveIntensity}
          speed={isFailed ? 6 : 2 + node.impactScore * 0.3}
          distort={isFailed ? 0.5 : 0.15 + emissiveIntensity * 0.15}
          radius={1}
          transparent
          opacity={ttOpacity}
        />
      </mesh>

      {/* glow */}
      <sprite scale={[node.size * (isFailed ? 6 : 4), node.size * (isFailed ? 6 : 4), 1]}>
        <spriteMaterial
          transparent
          opacity={glowOpacity * ttOpacity}
          color={displayColor}
          blending={THREE.AdditiveBlending}
          depthWrite={false}
        />
      </sprite>
    </group>
  )
}
