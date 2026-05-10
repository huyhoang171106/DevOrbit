import { useMemo } from 'react'
import { useGalaxyStore } from '../store/useGalaxyStore'
import { Wormhole } from '../entities/Wormhole'
import type { GalaxyEdge, DomainGalaxy } from '../layout/types'

type Props = {
  edges: GalaxyEdge[]
  galaxies: DomainGalaxy[]
}

export function WormholeSystem({ edges, galaxies }: Props) {
  const hoveredNodeId = useGalaxyStore((s) => s.hoveredNodeId)

  const { nodePosMap, nodeDomainMap } = useMemo(() => {
    const posMap = new Map<number, [number, number, number]>()
    const domainMap = new Map<number, string>()
    for (const g of galaxies) {
      for (const n of g.nodes) {
        posMap.set(n.id, [n.x, n.y, n.z])
        domainMap.set(n.id, g.color)
      }
    }
    return { nodePosMap: posMap, nodeDomainMap: domainMap }
  }, [galaxies])

  const validEdges = useMemo(() => {
    const result: Array<{
      edge: GalaxyEdge
      sourcePos: [number, number, number]
      targetPos: [number, number, number]
      color: string
    }> = []
    const seen = new Set<string>()

    for (const edge of edges) {
      if (edge.type !== 'PREREQUISITE') {
        const key = `${Math.min(edge.source, edge.target)}-${Math.max(edge.source, edge.target)}`
        if (seen.has(key)) continue
        seen.add(key)
      }

      const srcPos = nodePosMap.get(edge.source)
      const tgtPos = nodePosMap.get(edge.target)
      if (!srcPos || !tgtPos) continue

      const color = nodeDomainMap.get(edge.source) ?? '#64748b'
      result.push({ edge, sourcePos: srcPos, targetPos: tgtPos, color })
    }

    return result
  }, [edges, nodePosMap, nodeDomainMap])

  if (validEdges.length === 0) return null

  return (
    <group>
      {validEdges.map(({ edge, sourcePos, targetPos, color }) => (
        <Wormhole
          key={`${edge.source}-${edge.target}`}
          sourcePos={sourcePos}
          targetPos={targetPos}
          edge={edge}
          color={color}
          isHovered={hoveredNodeId === edge.source || hoveredNodeId === edge.target}
        />
      ))}
    </group>
  )
}
