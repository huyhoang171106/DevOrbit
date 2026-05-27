import { useMemo } from 'react'
import { Line } from '@react-three/drei'
import type { DomainGalaxy } from '../layout/types'

type Props = {
  galaxies: DomainGalaxy[]
}

export function OrbitRings({ galaxies }: Props) {
  const rings = useMemo(() => {
    const result: Array<{ points: [number, number, number][]; color: string }> = []

    for (const g of galaxies) {
      const radii = new Set<number>()
      for (const n of g.nodes) {
        const dx = n.x - g.center[0]
        const dz = n.z - g.center[2]
        const r = Math.round(Math.sqrt(dx * dx + dz * dz))
        if (r > 0) radii.add(r)
      }

      for (const r of radii) {
        const points: [number, number, number][] = Array.from({ length: 72 }, (_, j) => {
          const angle = (j / 72) * Math.PI * 2
          return [
            g.center[0] + r * Math.cos(angle),
            g.center[1],
            g.center[2] + r * Math.sin(angle),
          ]
        })
        result.push({ points, color: g.color })
      }
    }

    return result
  }, [galaxies])

  return (
    <group>
      {rings.map(({ points, color }, i) => (
        <Line key={i} points={points} color={color} transparent opacity={0.06} lineWidth={1} />
      ))}
    </group>
  )
}
