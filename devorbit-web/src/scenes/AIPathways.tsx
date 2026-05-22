'use client'

import { useRef, useMemo, useEffect } from 'react'
import { useFrame } from '@react-three/fiber'
import * as THREE from 'three'

interface AIPathwaysProps {
  count?: number
  color?: string
  width?: number
  opacity?: number
}

/**
 * Glowing AI pathway curves that pulse through 3D space.
 * Each path is a CatmullRomCurve3 with animated dash.
 * Creates a sense of intelligent data flow.
 */
export function AIPathways({
  count = 15,
  color = '#34d399',
  width = 0.3,
  opacity = 0.4,
}: AIPathwaysProps) {
  const groupRef = useRef<THREE.Group>(null)
  const timeRef = useRef(0)
  const prefersReduced = useRef(false)

  useEffect(() => {
    const mq = window.matchMedia('(prefers-reduced-motion: reduce)')
    prefersReduced.current = mq.matches
  }, [])

  const curves = useMemo(() => {
    const result: THREE.CatmullRomCurve3[] = []
    for (let i = 0; i < count; i++) {
      const points: THREE.Vector3[] = []
      const numPoints = 4 + Math.floor(Math.random() * 3)

      for (let j = 0; j < numPoints; j++) {
        const t = j / (numPoints - 1)
        const r = 10 + t * 25
        const angle = t * Math.PI * 2 * (1 + Math.random()) + (i / count) * Math.PI * 2
        const yOffset = (Math.random() - 0.5) * 15
        points.push(
          new THREE.Vector3(
            r * Math.cos(angle),
            Math.sin(t * Math.PI * 3) * 5 + yOffset * (1 - t * 0.5),
            r * Math.sin(angle)
          )
        )
      }

      result.push(new THREE.CatmullRomCurve3(points))
    }
    return result
  }, [count])

  const tubeGeometries = useMemo(() => {
    return curves.map((curve) => {
      const tubularSegments = 120
      const radialSegments = 4
      const tubeGeo = new THREE.TubeGeometry(curve, tubularSegments, width, radialSegments, true)
      return tubeGeo
    })
  }, [curves, width])

  useFrame((_, delta) => {
    if (prefersReduced.current) return
    if (!groupRef.current) return

    timeRef.current += delta
    const children = groupRef.current.children

    for (let i = 0; i < children.length; i++) {
      const mesh = children[i] as THREE.Mesh
      if (mesh.material) {
        const mat = mesh.material as THREE.MeshBasicMaterial
        // Pulsing opacity
        mat.opacity = opacity * (0.5 + 0.5 * Math.sin(timeRef.current * 0.5 + i * 0.8))
      }
    }
  })

  return (
    <group ref={groupRef}>
      {tubeGeometries.map((geo, i) => (
        <mesh key={i} geometry={geo}>
          <meshBasicMaterial
            color={color}
            transparent
            opacity={opacity}
            blending={THREE.AdditiveBlending}
            depthWrite={false}
            wireframe={false}
          />
        </mesh>
      ))}
    </group>
  )
}
