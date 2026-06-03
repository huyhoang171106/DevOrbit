import { useRef, useMemo } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'
import { usePlanetPositions } from '../context/PlanetPositionContext'

type Props = {
  nodeId: number
  color: string
  trailLength?: number
}

export function PlanetTrail({ nodeId, color, trailLength = 25 }: Props) {
  const pointsRef = useRef<THREE.Points>(null)
  const headRef = useRef(0)
  const ctx = usePlanetPositions()

  const geometry = useMemo(() => {
    const positions = new Float32Array(trailLength * 3)
    const geo = new THREE.BufferGeometry()
    geo.setAttribute('position', new THREE.BufferAttribute(positions, 3))
    geo.setDrawRange(0, trailLength)
    return geo
  }, [trailLength])

  useFrame(() => {
    if (!ctx) return
    const pos = ctx.getPosition(nodeId)
    if (!pos) return

    const attr = geometry.attributes.position
    const array = attr.array as Float32Array
    const head = headRef.current

    array[head * 3] = pos.x
    array[head * 3 + 1] = pos.y
    array[head * 3 + 2] = pos.z

    headRef.current = (head + 1) % trailLength
    attr.needsUpdate = true
  })

  return (
    <points ref={pointsRef} geometry={geometry}>
      <pointsMaterial
        size={0.12}
        color={color}
        transparent
        opacity={0.6}
        blending={THREE.AdditiveBlending}
        sizeAttenuation
        depthWrite={false}
      />
    </points>
  )
}
