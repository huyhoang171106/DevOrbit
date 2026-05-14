import { useRef, type ReactNode } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'
import type { GalaxyNode } from '../layout/types'
import { usePlanetPositions } from '../context/PlanetPositionContext'

type Props = {
  node: GalaxyNode
  galaxyCenter: [number, number, number]
  children: ReactNode
}

export function OrbitalGroup({ node, galaxyCenter, children }: Props) {
  const groupRef = useRef<THREE.Group>(null)
  const ctx = usePlanetPositions()

  const [cx, , cz] = galaxyCenter
  const dx = node.x - cx
  const dz = node.z - cz
  const radius = Math.sqrt(dx * dx + dz * dz)
  const initialAngle = Math.atan2(dz, dx)
  const angleRef = useRef(initialAngle)
  const orbitSpeed = 0.03 + node.level * 0.02

  const setPosition = ctx ? ctx.registerPlanet(node.id) : null
  const vec = useRef(new THREE.Vector3())

  useFrame((_, delta) => {
    if (!groupRef.current) return
    angleRef.current += delta * orbitSpeed
    const angle = angleRef.current
    const x = cx + radius * Math.cos(angle)
    const z = cz + radius * Math.sin(angle)

    groupRef.current.position.set(x, node.y, z)

    if (setPosition) {
      vec.current.set(x, node.y, z)
      setPosition(vec.current)
    }
  })

  return <group ref={groupRef}>{children}</group>
}
