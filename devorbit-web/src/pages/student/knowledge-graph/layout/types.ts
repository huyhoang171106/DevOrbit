import type { GraphLink } from '../../../../types/api'

export type DomainId = 'IT' | 'CS' | 'SE' | 'IS' | 'NT' | 'MA' | 'OTHER'

export type GalaxyNode = {
  id: number
  name: string
  code: string
  level: number
  impactScore: number
  size: number
  domain: DomainId
  x: number
  y: number
  z: number
}

export type DomainGalaxy = {
  id: DomainId
  name: string
  color: string
  nodes: GalaxyNode[]
  center: [number, number, number]
}

export type GalaxyEdge = {
  source: number
  target: number
  type: GraphLink['type']
  isInterGalaxy: boolean
}

export type GalaxyData = {
  galaxies: DomainGalaxy[]
  edges: GalaxyEdge[]
}

export const DOMAIN_CONFIG: Record<DomainId, { name: string; color: string }> = {
  IT: { name: 'Công Nghệ Phần Mềm', color: '#10b981' },
  CS: { name: 'Khoa Học Máy Tính', color: '#6366f1' },
  SE: { name: 'Kỹ Thuật Phần Mềm', color: '#8b5cf6' },
  IS: { name: 'Hệ Thống Thông Tin', color: '#f59e0b' },
  NT: { name: 'Mạng Máy Tính', color: '#f43f5e' },
  MA: { name: 'Toán', color: '#22d3ee' },
  OTHER: { name: 'Khác', color: '#64748b' },
}
