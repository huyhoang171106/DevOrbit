import { useRef, useMemo } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'

const COUNT = 6000
const RANGE = 200

export function Starfield() {
  const ref = useRef<THREE.Points>(null)
  const [positions, colors] = useMemo(() => {
    const pos = new Float32Array(COUNT * 3)
    const col = new Float32Array(COUNT * 3)
    for (let i = 0; i < COUNT; i++) {
      const i3 = i * 3
      pos[i3] = (Math.random() - 0.5) * RANGE
      pos[i3 + 1] = (Math.random() - 0.5) * RANGE
      pos[i3 + 2] = (Math.random() - 0.5) * RANGE
      const brightness = 0.3 + Math.random() * 0.7
      col[i3] = brightness
      col[i3 + 1] = brightness
      col[i3 + 2] = brightness + Math.random() * 0.2
    }
    return [pos, col]
  }, [])

  useFrame((_, delta) => {
    if (ref.current) ref.current.rotation.y += delta * 0.01
  })

  return (
    <points ref={ref}>
      <bufferGeometry>
        <bufferAttribute attach="attributes-position" args={[positions, 3]} />
        <bufferAttribute attach="attributes-color" args={[colors, 3]} />
      </bufferGeometry>
      <pointsMaterial size={0.5} vertexColors sizeAttenuation />
    </points>
  )
}
