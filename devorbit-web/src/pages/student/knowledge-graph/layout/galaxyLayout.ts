import type { GraphNode, GraphLink } from '../../../../types/api'
import { DOMAIN_CONFIG, type DomainId, type GalaxyData, type GalaxyNode, type DomainGalaxy, type GalaxyEdge } from './types'

const GALAXY_RADIUS = 28
const RING_RADII = [3, 6, 9, 12, 15, 18]
const Z_WARP = 1.5

function getDomain(code: string): DomainId {
  const prefix = code.substring(0, 2).toUpperCase()
  if (['IT'].includes(prefix)) return 'IT'
  if (['CS'].includes(prefix)) return 'CS'
  if (['SE'].includes(prefix)) return 'SE'
  if (['IS'].includes(prefix)) return 'IS'
  if (['NT'].includes(prefix)) return 'NT'
  if (['MA'].includes(prefix)) return 'MA'
  return 'OTHER'
}

function computeGalaxyPositions(galaxyCount: number): [number, number, number][] {
  const positions: [number, number, number][] = []
  const goldenAngle = Math.PI * (3 - Math.sqrt(5))
  for (let i = 0; i < galaxyCount; i++) {
    const y = (i - (galaxyCount - 1) / 2) * 1.5
    const angle = i * goldenAngle
    positions.push([
      Math.cos(angle) * GALAXY_RADIUS,
      y,
      Math.sin(angle) * GALAXY_RADIUS,
    ])
  }
  return positions
}

function spiralize(
  nodes: GalaxyNode[],
  galaxyCenter: [number, number, number],
  _color: string,
): GalaxyNode[] {
  const byLevel = new Map<number, GalaxyNode[]>()
  for (const n of nodes) {
    const l = byLevel.get(n.level) ?? []
    l.push(n)
    byLevel.set(n.level, l)
  }

  const sortedLevels = [...byLevel.keys()].sort((a, b) => a - b)
  const result: GalaxyNode[] = []

  for (let li = 0; li < sortedLevels.length; li++) {
    const level = sortedLevels[li]
    const levelNodes = byLevel.get(level)!
    const radius = RING_RADII[Math.min(li, RING_RADII.length - 1)]
    const count = levelNodes.length

    levelNodes.forEach((node, i) => {
      const angleOffset = li * 0.5 * Math.PI
      const angle = angleOffset + (i / count) * 2 * Math.PI
      const jitter = (Math.random() - 0.5) * 0.8

      node.x = galaxyCenter[0] + Math.cos(angle) * (radius + jitter)
      node.z = galaxyCenter[2] + Math.sin(angle) * (radius + jitter)
      node.y = galaxyCenter[1] + Math.sin(angle * 3) * Z_WARP + (Math.random() - 0.5) * 0.5

      result.push(node)
    })
  }

  return result
}

export function computeGalaxyData(
  nodes: GraphNode[],
  links: GraphLink[],
): GalaxyData {
  const grouped = new Map<DomainId, GalaxyNode[]>()
  for (const n of nodes) {
    const domain = getDomain(n.code)
    const arr = grouped.get(domain) ?? []
    arr.push({
      id: n.id,
      name: n.name,
      code: n.code,
      level: n.level,
      impactScore: n.impactScore,
      size: Math.max(0.4, n.val / 10),
      domain,
      x: 0, y: 0, z: 0,
    })
    grouped.set(domain, arr)
  }

  const domainIds = Object.keys(DOMAIN_CONFIG) as DomainId[]
  const presentDomains = domainIds.filter(d => (grouped.get(d)?.length ?? 0) > 0)
  const positions = computeGalaxyPositions(presentDomains.length)

  const galaxies: DomainGalaxy[] = presentDomains.map((id, i) => {
    const center = positions[i]
    const rawNodes = grouped.get(id) ?? []
    return {
      id,
      name: DOMAIN_CONFIG[id].name,
      color: DOMAIN_CONFIG[id].color,
      nodes: spiralize(rawNodes, center, DOMAIN_CONFIG[id].color),
      center,
    }
  })

  const nodeMap = new Map<number, GalaxyNode>()
  for (const g of galaxies) { for (const n of g.nodes) { nodeMap.set(n.id, n) } }

  const edges: GalaxyEdge[] = links
    .filter(l => nodeMap.has(l.source) && nodeMap.has(l.target))
    .map(l => {
      const srcDomain = getDomain(nodeMap.get(l.source)!.code)
      const tgtDomain = getDomain(nodeMap.get(l.target)!.code)
      return {
        source: l.source,
        target: l.target,
        type: l.type,
        isInterGalaxy: srcDomain !== tgtDomain,
      }
    })

  return { galaxies, edges }
}
