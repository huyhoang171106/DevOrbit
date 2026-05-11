import { useMemo, useCallback, useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import ForceGraph2D from 'react-force-graph-2d'
import { useKnowledgeGraph } from '../../../hooks/useKnowledgeGraph'
import { apiGet } from '../../../lib/api'
import { GalaxyOverlay } from './ui/GalaxyOverlay'
import { useGalaxyStore } from './store/useGalaxyStore'
import { FolderOpen, Activity } from 'lucide-react'
import type { GraphLink, CourseSummary } from '../../../types/api'

const CARD_WIDTH = 140
const CARD_HEIGHT = 65
const SMALL_CARD_WIDTH = 120
const SMALL_CARD_HEIGHT = 48
const CARD_RADIUS = 4
const COLUMN_WIDTH = 240
const VERTICAL_STEP = 110
const SMALL_VERTICAL_STEP = 80

const ELECTIVE_GROUP_COLORS: Record<string, string> = {
  TC_CSN: '#d97706',
  TC_CN: '#7c3aed',
  TC_CN_PM: '#0891b2',
  TC_CN_GAME: '#db2777',
  TC_TN: '#65a30d',
}

const ELECTIVE_LEGEND = [
  { code: 'TC_CSN', name: 'Tự chọn Cơ sở ngành', color: '#d97706' },
  { code: 'TC_CN', name: 'Tự chọn Chuyên ngành', color: '#7c3aed' },
  { code: 'TC_CN_PM', name: 'Tự chọn PM', color: '#0891b2' },
  { code: 'TC_CN_GAME', name: 'Tự chọn Game', color: '#db2777' },
  { code: 'TC_TN', name: 'Chuyên đề TN', color: '#65a30d' },
]

const ELECTIVE_GROUP_BG: Record<string, string> = {
  TC_CSN: '#fffbeb',
  TC_CN: '#f5f3ff',
  TC_CN_PM: '#ecfeff',
  TC_CN_GAME: '#fdf2f8',
  TC_TN: '#f7fee7',
}

function computeBlocked(failed: Set<number>, links: GraphLink[]): Set<number> {
  if (failed.size === 0) return new Set()
  const blocked = new Set<number>(failed)
  const queue = [...failed]
  let idx = 0
  while (idx < queue.length) {
    const current = queue[idx++]
    for (const l of links) {
      if (l.type !== 'PREREQUISITE') continue
      if (l.source !== current) continue
      if (!blocked.has(l.target)) {
        blocked.add(l.target)
        queue.push(l.target)
      }
    }
  }
  return blocked
}

export default function GalaxyPage() {
  const fgRef = useRef<any>(null)
  const navigate = useNavigate()
  const { data, isLoading, error } = useKnowledgeGraph()
  const isSimulationMode = useGalaxyStore((s) => s.isSimulationMode)
  const failedNodes = useGalaxyStore((s) => s.failedNodes)
  const toggleFailedNode = useGalaxyStore((s) => s.toggleFailedNode)
  const setBlockedNodes = useGalaxyStore((s) => s.setBlockedNodes)
  const blockedNodes = useGalaxyStore((s) => s.blockedNodes)

  // --- Expanded elective group state ---
  const [expandedGroup, setExpandedGroup] = useState<string | null>(null)
  const [expandedNodeId, setExpandedNodeId] = useState<number | string | null>(null)
  const [expandedNodeRef, setExpandedNodeRef] = useState<any>(null)
  const [groupElectives, setGroupElectives] = useState<CourseSummary[]>([])
  const [popoverPos, setPopoverPos] = useState<{ x: number, y: number } | null>(null)

  // Fetch expanded group courses
  useEffect(() => {
    if (expandedGroup) {
      apiGet<CourseSummary[]>(`/api/courses/elective-group/${expandedGroup}`)
        .then(setGroupElectives)
        .catch(() => setGroupElectives([]))
    } else {
      setGroupElectives([])
    }
  }, [expandedGroup])

  const graphData = useMemo(() => {
    if (!data) return { nodes: [], links: [] }

    // All nodes that have a semester (mandatory courses + elective group nodes)
    const semesterNodes = data.nodes.filter(n => n.semester != null)

    // Sort for consistent vertical order
    const sortedSemester = [...semesterNodes].sort((a, b) => a.code.localeCompare(b.code))

    // Build base nodes: mandatory + elective group nodes in fixed grid
    const processedNodes = sortedSemester.map((node) => {
      const semester = node.semester ?? 1
      const nodesInSemester = sortedSemester.filter(n => n.semester === semester)
      // Find position within same-semester nodes, counting group nodes
      let nodeIndex = 0
      for (const n of nodesInSemester) {
        if (n.id === node.id) break
        nodeIndex++
      }
      const totalInSemester = nodesInSemester.length

      return {
        ...node,
        fx: (semester - 4.5) * COLUMN_WIDTH,
        fy: (nodeIndex - (totalInSemester - 1) / 2) * VERTICAL_STEP,
      }
    })

    // REMOVED: individual elective nodes generation
    
    const allNodeIds = new Set(processedNodes.map(n => n.id))

    // Resolve links: don't filter mandatory-only, include group nodes and expanded electives
    const resolvedLinks = data.links
      .filter(l => {
        const src = typeof l.source === 'number' ? l.source : (l.source as any)?.id
        const tgt = typeof l.target === 'number' ? l.target : (l.target as any)?.id
        return allNodeIds.has(src) && allNodeIds.has(tgt)
      })
      .map(l => ({ ...l }))

    return { nodes: processedNodes, links: resolvedLinks }
  }, [data, expandedGroup, groupElectives])

  useEffect(() => {
    if (data) {
      setBlockedNodes(computeBlocked(failedNodes, data.links))
    }
  }, [failedNodes, data, setBlockedNodes])

  const updatePopoverPos = useCallback(() => {
    if (!expandedNodeRef || !fgRef.current) return
    // Use the reference to the node object directly as its x,y are updated by the engine
    const coords = fgRef.current.graph2ScreenCoords(expandedNodeRef.x, expandedNodeRef.y)
    if (coords && (coords.x !== 0 || coords.y !== 0)) {
      setPopoverPos(coords)
    }
  }, [expandedNodeRef])

  const handleNodeClick = useCallback((node: any) => {
    if (isSimulationMode) {
      toggleFailedNode(node.id)
      return
    }

    const isElectiveGroup = node.electiveGroup != null && (node.semester != null || node.id < 0)
    const isExpandedElective = node.electiveGroup != null && !isElectiveGroup

    if (isElectiveGroup) {
      if (expandedNodeId === node.id) {
        setExpandedGroup(null)
        setExpandedNodeId(null)
        setExpandedNodeRef(null)
        setPopoverPos(null)
      } else {
        setExpandedGroup(node.electiveGroup)
        setExpandedNodeId(node.id)
        setExpandedNodeRef(node)
      }
    } else if (isExpandedElective) {
      navigate(`/courses/${node.id}`)
    } else {
      navigate(`/courses/${node.id}`)
    }
  }, [isSimulationMode, navigate, toggleFailedNode, expandedNodeId])

  useEffect(() => {
    if (expandedNodeId) {
      // Initial position
      setTimeout(updatePopoverPos, 50)
    }
  }, [expandedNodeId, updatePopoverPos])

  // Keyboard escape to collapse expanded group
  useEffect(() => {
    if (!expandedGroup) return
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setExpandedGroup(null)
    }
    window.addEventListener('keydown', handler)
    return () => window.removeEventListener('keydown', handler)
  }, [expandedGroup])

  if (isLoading) {
    return (
      <div className="h-[80vh] flex items-center justify-center bg-white">
        <div className="h-6 w-6 border border-clay-border border-t-clay-primary animate-spin" />
      </div>
    )
  }

  if (error || !data) return null

  return (
    <div className="relative w-full h-[calc(100vh-64px)] bg-[#fcfcfd] overflow-hidden font-sans">
      <GalaxyOverlay 
        expandedGroup={expandedGroup} 
        groupElectives={groupElectives}
        onCollapse={() => {
          setExpandedGroup(null)
          setExpandedNodeId(null)
          setPopoverPos(null)
        }} 
      />

      {/* Floating Elective Card */}
      {expandedGroup && popoverPos && (
        <div 
          className="absolute z-[100] pointer-events-auto animate-in zoom-in-95 fade-in duration-200"
          style={{ 
            left: popoverPos.x, 
            top: popoverPos.y + 45,
            transform: 'translateX(-50%)'
          }}
        >
          <div className="bg-white border-2 border-clay-primary/20 shadow-2xl min-w-[320px] max-w-[400px] overflow-hidden rounded-xl">
            <div className="bg-clay-surface border-b border-clay-border px-4 py-2.5 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <FolderOpen className="w-3.5 h-3.5 text-clay-primary" />
                <span className="text-[10px] font-bold uppercase tracking-wider text-clay-text">
                  {ELECTIVE_LEGEND.find(e => e.code === expandedGroup)?.name || 'Danh sách môn'}
                </span>
              </div>
              <span className="text-[10px] font-bold text-clay-primary px-1.5 py-0.5 bg-clay-primary/10 rounded-full">
                {groupElectives.length} môn
              </span>
            </div>
            
            <div className="max-h-[300px] overflow-y-auto p-2 space-y-1 custom-scrollbar min-h-[100px]">
              {groupElectives.length > 0 ? (
                groupElectives.map(ec => (
                  <button
                    key={ec.id}
                    onClick={() => navigate(`/courses/${ec.id}`)}
                    className="w-full text-left px-3 py-2.5 hover:bg-clay-primary/5 transition-colors border border-transparent hover:border-clay-primary/20 group rounded-lg"
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-[11px] font-bold text-clay-primary">{ec.code}</span>
                      <span className="text-[9px] text-clay-text-muted opacity-0 group-hover:opacity-100 transition-opacity">Chi tiết →</span>
                    </div>
                    <div className="text-[11px] text-clay-text mt-0.5 truncate font-medium">
                      {ec.name}
                    </div>
                  </button>
                ))
              ) : (
                <div className="py-8 text-center">
                  <div className="inline-flex items-center justify-center w-8 h-8 rounded-full bg-clay-surface mb-2">
                    <Activity className="w-4 h-4 text-clay-text-muted animate-pulse" />
                  </div>
                  <p className="text-[11px] text-clay-text-muted italic">Đang tải danh sách môn học...</p>
                </div>
              )}
            </div>
            
            <div className="bg-clay-surface px-4 py-2 border-t border-clay-border flex justify-center">
               <button 
                onClick={() => { setExpandedGroup(null); setExpandedNodeId(null); }}
                className="text-[9px] font-bold text-clay-text-muted hover:text-rose-600 uppercase tracking-widest transition-colors"
               >
                 Đóng lại
               </button>
            </div>
          </div>
          {/* Arrow pointing up */}
          <div className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-full w-0 h-0 border-l-[10px] border-l-transparent border-r-[10px] border-r-transparent border-b-[10px] border-b-clay-primary/20" />
          <div className="absolute top-[2px] left-1/2 -translate-x-1/2 -translate-y-full w-0 h-0 border-l-[8px] border-l-transparent border-r-[8px] border-r-transparent border-b-[8px] border-b-white" />
        </div>
      )}

      <ForceGraph2D
        ref={fgRef}
        graphData={graphData}
        backgroundColor="#fcfcfd"
        nodeRelSize={1}
        cooldownTicks={0}

        onRenderFramePre={(ctx, globalScale) => {
          ctx.save()
          for (let i = 1; i <= 8; i++) {
            const x = (i - 4.5) * COLUMN_WIDTH

            ctx.beginPath()
            ctx.setLineDash([5, 5])
            ctx.strokeStyle = 'rgba(0, 0, 0, 0.04)'
            ctx.lineWidth = 1 / globalScale
            ctx.moveTo(x - COLUMN_WIDTH / 2, -1000)
            ctx.lineTo(x - COLUMN_WIDTH / 2, 1000)
            ctx.stroke()

            const fontSize = 14 / globalScale
            ctx.font = `black ${fontSize}px Inter, sans-serif`
            ctx.fillStyle = 'rgba(0, 0, 0, 0.2)'
            ctx.textAlign = 'center'
            ctx.textBaseline = 'bottom'
            ctx.setLineDash([])
            ctx.fillText(`HỌC KỲ ${i}`, x, -500)
          }
          ctx.restore()
        }}

        nodeCanvasObject={(node: any, ctx, globalScale) => {
          const isFailed = failedNodes.has(node.id)
          const isBlocked = blockedNodes.has(node.id) && !isFailed
          const isElectiveGroup = node.electiveGroup != null && (node.semester != null || node.id < 0)
          const isExpandedElective = node.electiveGroup != null && !isElectiveGroup

          let borderColor = '#cbd5e1'
          let bgColor = '#ffffff'
          let textColor = '#1e293b'

          if (isExpandedElective) {
            // Expanded elective course card
            const color = ELECTIVE_GROUP_COLORS[node.electiveGroup] || '#64748b'
            borderColor = `${color}44`
            bgColor = ELECTIVE_GROUP_BG[node.electiveGroup] || '#f8fafc'
            textColor = '#334155'

            const w = SMALL_CARD_WIDTH
            const h = SMALL_CARD_HEIGHT
            const x = node.x - w / 2
            const y = node.y - h / 2

            ctx.shadowColor = 'rgba(0,0,0,0.03)'
            ctx.shadowBlur = 4 / globalScale
            ctx.shadowOffsetY = 1 / globalScale
            ctx.fillStyle = bgColor
            ctx.strokeStyle = borderColor
            ctx.lineWidth = 1 / globalScale
            const r = 3
            ctx.beginPath()
            ctx.moveTo(x + r, y)
            ctx.lineTo(x + w - r, y)
            ctx.quadraticCurveTo(x + w, y, x + w, y + r)
            ctx.lineTo(x + w, y + h - r)
            ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h)
            ctx.lineTo(x + r, y + h)
            ctx.quadraticCurveTo(x, y + h, x, y + h - r)
            ctx.lineTo(x, y + r)
            ctx.quadraticCurveTo(x, y, x + r, y)
            ctx.closePath()
            ctx.fill()
            ctx.stroke()
            ctx.shadowColor = 'transparent'

            const fs = 9 / globalScale
            ctx.textAlign = 'center'
            ctx.textBaseline = 'middle'
            ctx.font = `800 ${fs}px Inter, sans-serif`
            ctx.fillStyle = color
            ctx.fillText(node.code, node.x, node.y - 8)

            ctx.font = `${fs * 0.8}px Inter, sans-serif`
            ctx.fillStyle = '#64748b'
            const name = node.name || ''
            const maxChars = 16
            ctx.fillText(name.length > maxChars ? name.substring(0, maxChars) + '…' : name, node.x, node.y + 8)
            return
          }

          if (isElectiveGroup) {
            // Elective group node
            const color = ELECTIVE_GROUP_COLORS[node.code] || '#64748b'
            borderColor = color
            bgColor = ELECTIVE_GROUP_BG[node.code] || '#fff'
            textColor = color

            const w = CARD_WIDTH
            const h = CARD_HEIGHT
            const x = node.x - w / 2
            const y = node.y - h / 2

            // Dashed border for group
            ctx.shadowColor = 'rgba(0,0,0,0.04)'
            ctx.shadowBlur = 6 / globalScale
            ctx.shadowOffsetY = 2 / globalScale
            ctx.fillStyle = bgColor
            ctx.strokeStyle = borderColor
            ctx.lineWidth = 1.5 / globalScale
            ctx.setLineDash([4 / globalScale, 3 / globalScale])
            const r = CARD_RADIUS
            ctx.beginPath()
            ctx.moveTo(x + r, y)
            ctx.lineTo(x + w - r, y)
            ctx.quadraticCurveTo(x + w, y, x + w, y + r)
            ctx.lineTo(x + w, y + h - r)
            ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h)
            ctx.lineTo(x + r, y + h)
            ctx.quadraticCurveTo(x, y + h, x, y + h - r)
            ctx.lineTo(x, y + r)
            ctx.quadraticCurveTo(x, y, x + r, y)
            ctx.closePath()
            ctx.fill()
            ctx.stroke()
            ctx.setLineDash([])
            ctx.shadowColor = 'transparent'

            // Group code
            const fs = 11 / globalScale
            ctx.textAlign = 'center'
            ctx.textBaseline = 'middle'
            ctx.font = `900 ${fs}px Inter, sans-serif`
            ctx.fillStyle = textColor
            ctx.fillText(node.code, node.x, node.y - 12)

            // Group name truncated
            ctx.font = `${fs * 0.8}px Inter, sans-serif`
            ctx.fillStyle = '#64748b'
            const name = node.name || ''
            const maxChars = 20
            ctx.fillText(name.length > maxChars ? name.substring(0, maxChars) + '…' : name, node.x, node.y + 6)

            // "Chọn TC" badge
            if (!isFailed && !isBlocked) {
              const badgeSize = 8 / globalScale
              ctx.font = `800 ${badgeSize}px Inter, sans-serif`
              ctx.fillStyle = color
              ctx.globalAlpha = 0.7
              ctx.fillText('Môn tự chọn', node.x, node.y + 24)
              ctx.globalAlpha = 1
            }

            // Expanded indicator
            if (expandedNodeId === node.id) {
              ctx.beginPath()
              ctx.arc(node.x + w / 2 - 10, y + 6, 5 / globalScale, 0, Math.PI * 2)
              ctx.fillStyle = color
              ctx.globalAlpha = 0.3
              ctx.fill()
              ctx.globalAlpha = 1
              ctx.font = `700 ${7 / globalScale}px Inter, sans-serif`
              ctx.fillStyle = color
              ctx.fillText('−', node.x + w / 2 - 10, y + 7)
            }

            return
          }

          // --- Mandatory course card (existing logic) ---
          if (isFailed) { borderColor = '#ef4444'; bgColor = '#fef2f2'; textColor = '#991b1b' }
          else if (isBlocked) { borderColor = '#fca5a5'; bgColor = '#fff1f2'; textColor = '#991b1b' }

          const x = node.x - CARD_WIDTH / 2
          const y = node.y - CARD_HEIGHT / 2

          ctx.shadowColor = 'rgba(0, 0, 0, 0.04)'
          ctx.shadowBlur = 6 / globalScale
          ctx.shadowOffsetY = 2 / globalScale

          ctx.fillStyle = bgColor
          ctx.strokeStyle = borderColor
          ctx.lineWidth = 1 / globalScale

          const r = CARD_RADIUS
          ctx.beginPath()
          ctx.moveTo(x + r, y)
          ctx.lineTo(x + CARD_WIDTH - r, y)
          ctx.quadraticCurveTo(x + CARD_WIDTH, y, x + CARD_WIDTH, y + r)
          ctx.lineTo(x + CARD_WIDTH, y + CARD_HEIGHT - r)
          ctx.quadraticCurveTo(x + CARD_WIDTH, y + CARD_HEIGHT, x + CARD_WIDTH - r, y + CARD_HEIGHT)
          ctx.lineTo(x + r, y + CARD_HEIGHT)
          ctx.quadraticCurveTo(x, y + CARD_HEIGHT, x, y + CARD_HEIGHT - r)
          ctx.lineTo(x, y + r)
          ctx.quadraticCurveTo(x, y, x + r, y)
          ctx.closePath()
          ctx.fill()
          ctx.stroke()
          ctx.shadowColor = 'transparent'

          const fontSize = 11 / globalScale
          ctx.textAlign = 'center'
          ctx.textBaseline = 'middle'

          ctx.font = `900 ${fontSize}px Inter, sans-serif`
          ctx.fillStyle = textColor
          ctx.fillText(node.code, node.x, node.y - 12)

          ctx.font = `${fontSize * 0.8}px Inter, sans-serif`
          ctx.fillStyle = isFailed || isBlocked ? textColor : '#64748b'
          const n = node.name || ''
          const mc = 20
          ctx.fillText(n.length > mc ? n.substring(0, mc) + '…' : n, node.x, node.y + 12)
        }}

        nodePointerAreaPaint={(node: any, color, ctx) => {
          const isElectiveGroup = node.electiveGroup != null && (node.semester != null || node.id < 0)
          const isExpandedElective = node.electiveGroup != null && !isElectiveGroup
          const w = isExpandedElective ? SMALL_CARD_WIDTH : isElectiveGroup ? CARD_WIDTH : CARD_WIDTH
          const h = isExpandedElective ? SMALL_CARD_HEIGHT : isElectiveGroup ? CARD_HEIGHT : CARD_HEIGHT
          ctx.fillStyle = color
          ctx.fillRect(node.x - w / 2, node.y - h / 2, w, h)
        }}

        linkCanvasObject={(link: any, ctx, globalScale) => {
          const start = link.source
          const end = link.target
          if (!start || !end || typeof start !== 'object' || typeof end !== 'object') return

          const isAffected = blockedNodes.has(start.id) || blockedNodes.has(end.id)

          // Link color: if either endpoint is elective-group-related, use a fainter style
          const isGroupRelated =
            (start.electiveGroup && start.electiveGroup === start.code) ||
            (end.electiveGroup && end.electiveGroup === end.code) ||
            (start.electiveGroup && start.id < 0) ||
            (end.electiveGroup && end.id < 0)

          ctx.beginPath()
          ctx.moveTo(start.x, start.y)
          ctx.lineTo(end.x, end.y)

          ctx.strokeStyle = isAffected ? '#fca5a5' : isGroupRelated ? '#d4d4d8' : '#e2e8f0'
          ctx.lineWidth = (isAffected ? 1.5 : isGroupRelated ? 0.5 : 1) / globalScale
          ctx.stroke()

          if (globalScale > 0.5 && !isGroupRelated) {
            const arrowSize = 4 / globalScale
            const angle = Math.atan2(end.y - start.y, end.x - start.x)
            const arrowX = end.x - (CARD_WIDTH / 2) * Math.cos(angle)
            const arrowY = end.y - (CARD_HEIGHT / 2) * Math.sin(angle)
            ctx.beginPath()
            ctx.moveTo(arrowX, arrowY)
            ctx.lineTo(arrowX - arrowSize * Math.cos(angle - Math.PI / 6), arrowY - arrowSize * Math.sin(angle - Math.PI / 6))
            ctx.lineTo(arrowX - arrowSize * Math.cos(angle + Math.PI / 6), arrowY - arrowSize * Math.sin(angle + Math.PI / 6))
            ctx.closePath()
            ctx.fillStyle = isAffected ? '#fca5a5' : '#e2e8f0'
            ctx.fill()
          }
        }}

        linkDirectionalParticles={1}
        linkDirectionalParticleWidth={(link: any) => {
          const isAffected = blockedNodes.has((link.source as any)?.id) || blockedNodes.has((link.target as any)?.id)
          return isAffected ? 2 : 0
        }}
        linkDirectionalParticleSpeed={0.01}
        linkDirectionalParticleColor="#f43f5e"

        onNodeClick={handleNodeClick}
        onZoom={updatePopoverPos}
        onPan={updatePopoverPos}
        onEngineTick={updatePopoverPos}
        onBackgroundClick={() => {
          setExpandedGroup(null)
          setExpandedNodeId(null)
          setPopoverPos(null)
        }}
        onEngineStop={() => {
          if (fgRef.current) {
            fgRef.current.zoomToFit(400, 100)
          }
        }}
      />
    </div>
  )
}
