import { useRef, useMemo } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'
import type { GalaxyEdge } from '../layout/types'

type Props = {
  sourcePos: [number, number, number]
  targetPos: [number, number, number]
  edge: GalaxyEdge
  color: string
  isHovered: boolean
}

const PARTICLE_COUNT = 10

export function Wormhole({ sourcePos, targetPos, edge, color, isHovered }: Props) {
  const meshRef = useRef<THREE.Mesh>(null)
  const pointsRef = useRef<THREE.Points>(null)
  const phasesRef = useRef(new Float32Array(PARTICLE_COUNT).map((_, i) => i / PARTICLE_COUNT))
  const curveRef = useRef<THREE.CatmullRomCurve3>(null!)

  const { tubeGeo, particleGeo } = useMemo(() => {
    const src = new THREE.Vector3(...sourcePos)
    const tgt = new THREE.Vector3(...targetPos)
    const mid = new THREE.Vector3().lerpVectors(src, tgt, 0.5)
    const dir = new THREE.Vector3().subVectors(tgt, src).normalize()
    const up = new THREE.Vector3(0, 1, 0)
    const perp = new THREE.Vector3().crossVectors(dir, up)
    if (perp.length() < 0.01) perp.set(1, 0, 0)
    perp.normalize()
    mid.addScaledVector(perp, src.distanceTo(tgt) * 0.15)

    const curve = new THREE.CatmullRomCurve3([src, mid, tgt])
    curveRef.current = curve

    const radius = edge.isInterGalaxy ? 0.18 : 0.10
    const tube = new THREE.TubeGeometry(curve, 32, radius, 6, false)

    const positions = new Float32Array(PARTICLE_COUNT * 3)
    for (let i = 0; i < PARTICLE_COUNT; i++) {
      const pt = curve.getPoint(i / PARTICLE_COUNT)
      positions[i * 3] = pt.x
      positions[i * 3 + 1] = pt.y
      positions[i * 3 + 2] = pt.z
    }
    const particle = new THREE.BufferGeometry()
    particle.setAttribute('position', new THREE.BufferAttribute(positions, 3))

    return { tubeGeo: tube, particleGeo: particle }
  }, [sourcePos, targetPos, edge.isInterGalaxy])

  useFrame((_, delta) => {
    const curve = curveRef.current
    const posAttr = pointsRef.current?.geometry.attributes.position
    if (!curve || !posAttr) return

    const speed = isHovered ? 0.4 : 0.15
    const phases = phasesRef.current
    const array = posAttr.array as Float32Array
    for (let i = 0; i < PARTICLE_COUNT; i++) {
      let t = phases[i] + delta * speed
      if (t > 1) t -= 1
      phases[i] = t
      const pt = curve.getPoint(t)
      array[i * 3] = pt.x
      array[i * 3 + 1] = pt.y
      array[i * 3 + 2] = pt.z
    }
    posAttr.needsUpdate = true
  })

  const opacity = isHovered ? 1.0 : 0.7

  return (
    <group>
      <mesh ref={meshRef} geometry={tubeGeo}>
        <meshBasicMaterial color={color} transparent opacity={opacity} depthWrite={false} />
      </mesh>

      {edge.isInterGalaxy && (
        <mesh geometry={tubeGeo}>
          <meshBasicMaterial
            color={color}
            transparent
            opacity={opacity * 0.25}
            blending={THREE.AdditiveBlending}
            depthWrite={false}
          />
        </mesh>
      )}

      <points ref={pointsRef} geometry={particleGeo}>
        <pointsMaterial
          size={0.25}
          color={color}
          transparent
          opacity={0.85}
          blending={THREE.AdditiveBlending}
          sizeAttenuation
          depthWrite={false}
        />
      </points>
    </group>
  )
}
