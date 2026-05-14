import { useMemo, useCallback, useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import ForceGraph2D from 'react-force-graph-2d'
import { AlertTriangle } from 'lucide-react'
import { useKnowledgeGraph } from '../../../hooks/useKnowledgeGraph'
import { GalaxyOverlay } from './ui/GalaxyOverlay'
import { useGalaxyStore } from './store/useGalaxyStore'
import type { GraphLink } from '../../../types/api'

const CARD_WIDTH = 140
const CARD_HEIGHT = 65
const COLUMN_WIDTH = 240
const VERTICAL_STEP = 80

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
  const aiElectiveNodes = useGalaxyStore((s) => s.aiElectiveNodes)
  const aiElectiveNodeData = useGalaxyStore((s) => s.aiElectiveNodeData)

  const graphData = useMemo(() => {
    if (!data || !data.nodes) return { nodes: [], links: [] }

    const validNodes = data.nodes.map(n => ({
      ...n,
      semester: n.semester ?? 1
    }))

    const sortedNodes = [...validNodes].sort((a, b) => {
      if (a.semester !== b.semester) return a.semester - b.semester
      // Push ENG courses to the bottom of each semester
      const aIsEng = a.code?.startsWith("ENG")
      const bIsEng = b.code?.startsWith("ENG")
      if (aIsEng && !bIsEng) return 1
      if (!aIsEng && bIsEng) return -1
      return a.code.localeCompare(b.code)
    })

    const processedNodes = sortedNodes.map((node) => {
      const nodesInSemester = sortedNodes.filter(n => n.semester === node.semester)
      const nodeIndex = nodesInSemester.findIndex(n => n.id === node.id)
      const totalInSemester = nodesInSemester.length

      return {
        ...node,
        fx: (node.semester - 4.5) * COLUMN_WIDTH,
        fy: (nodeIndex - (totalInSemester - 1) / 2) * VERTICAL_STEP,
      }
    })

    // Add elective nodes below main nodes in their semester columns
    if (aiElectiveNodeData.length > 0) {
      const maxSemesterY: Record<number, number> = {}
      processedNodes.forEach(n => {
        const sem = n.semester ?? 1
        if (!maxSemesterY[sem] || n.fy > maxSemesterY[sem]) maxSemesterY[sem] = n.fy
      })

      // Group electives by semester so per-semester indices are correct
      const electivesBySemester: Record<number, typeof aiElectiveNodeData> = {}
      aiElectiveNodeData.forEach(en => {
        const sem = en.semester || 1
        if (!electivesBySemester[sem]) electivesBySemester[sem] = []
        electivesBySemester[sem].push(en)
      })

      const electiveNodes: any[] = []
      Object.entries(electivesBySemester)
        .sort(([a], [b]) => Number(a) - Number(b))
        .forEach(([semStr, electives]) => {
          const sem = Number(semStr)
          const baseY = maxSemesterY[sem] ?? 0
          electives.forEach((en, i) => {
            electiveNodes.push({
              id: en.id,
              code: en.code,
              name: en.name,
              semester: sem,
              description: en.description || '',
              val: 12.0,
              level: 0,
              impactScore: 0.0,
              electiveGroup: null,
              fx: (sem - 4.5) * COLUMN_WIDTH,
              fy: baseY + (i + 1) * VERTICAL_STEP,
            })
          })
        })
      processedNodes.push(...electiveNodes)
    }

    const allNodeIds = new Set(processedNodes.map(n => n.id))

    const resolvedLinks = (data.links || [])
      .filter(l => {
        const src = typeof l.source === 'object' ? (l.source as any).id : l.source
        const tgt = typeof l.target === 'object' ? (l.target as any).id : l.target
        return src != null && tgt != null && allNodeIds.has(src) && allNodeIds.has(tgt)
      })
      .map(l => ({
        source: typeof l.source === 'object' ? (l.source as any).id : l.source,
        target: typeof l.target === 'object' ? (l.target as any).id : l.target,
        type: l.type
      }))

    return { nodes: processedNodes, links: resolvedLinks }
  }, [data, aiElectiveNodeData])

  useEffect(() => {
    if (data && data.links) {
      setBlockedNodes(computeBlocked(failedNodes, data.links))
    }
  }, [failedNodes, data, setBlockedNodes])

  const [selectedNodeId, setSelectedNodeId] = useState<number | string | null>(null)
  const lastClickTime = useRef<number>(0)
  const clickTimer = useRef<any>(null)

  const handleNodeClick = useCallback((node: any) => {
    if (isSimulationMode) {
      toggleFailedNode(node.id)
      return
    }

    const now = Date.now()
    if (now - lastClickTime.current < 300) {
      if (clickTimer.current) {
        clearTimeout(clickTimer.current)
        clickTimer.current = null
      }
      navigate(`/courses/${node.id}`)
    } else {
      if (clickTimer.current) clearTimeout(clickTimer.current)
      
      clickTimer.current = setTimeout(() => {
        if (selectedNodeId === node.id) {
          setSelectedNodeId(null)
        } else {
          setSelectedNodeId(node.id)
        }
        clickTimer.current = null
      }, 300)
    }
    lastClickTime.current = now
  }, [isSimulationMode, navigate, toggleFailedNode, selectedNodeId])

  if (isLoading) {
    return (
      <div className="h-[80vh] flex items-center justify-center bg-white">
        <div className="h-6 w-6 border border-clay-border border-t-clay-primary animate-spin" />
      </div>
    )
  }

  if (error) {
    return (
      <div className="h-[80vh] flex flex-col items-center justify-center bg-white p-6 text-center">
        <AlertTriangle className="w-12 h-12 text-rose-500 mb-4" />
        <h2 className="text-lg font-bold text-clay-text mb-2">Không thể tải sơ đồ kiến thức</h2>
        <p className="text-sm text-clay-text-muted mb-6 max-w-md">
          Đã có lỗi xảy ra khi kết nối với máy chủ. Vui lòng kiểm tra kết nối mạng hoặc thử lại sau.
        </p>
        <button 
          onClick={() => window.location.reload()}
          className="px-6 py-2 bg-clay-primary text-white text-xs font-bold uppercase tracking-widest hover:bg-clay-primary/90 transition-colors"
        >
          Tải lại trang
        </button>
      </div>
    )
  }

  if (!data) return null

  return (
    <div className="relative w-full h-[calc(100vh-64px)] bg-[#fcfcfd] overflow-hidden font-sans">
      <GalaxyOverlay />

      {/* Legend Overlay */}
      <div className="absolute bottom-6 right-6 bg-white p-4 border border-slate-200 shadow-sm pointer-events-none z-10">
        <div className="flex flex-col gap-3 text-sm text-slate-700">
          <div className="flex items-center gap-3">
            <div className="w-12 h-[2px] bg-slate-800 relative">
              <div className="absolute right-[-4px] top-[-4px] border-t-[5px] border-t-transparent border-l-[6px] border-l-slate-800 border-b-[5px] border-b-transparent"></div>
            </div>
            <span>Môn học trước</span>
          </div>
          <div className="flex items-center gap-3">
            <div className="w-12 h-[4px] border-y border-slate-800 bg-white relative">
              <div className="absolute right-[-6px] top-[-5px] w-0 h-0 border-t-[6px] border-t-transparent border-l-[8px] border-l-white border-b-[6px] border-b-transparent z-10"></div>
              <div className="absolute right-[-8px] top-[-6px] w-0 h-0 border-t-[7px] border-t-transparent border-l-[10px] border-l-slate-800 border-b-[7px] border-b-transparent"></div>
            </div>
            <span>Môn học tiên quyết</span>
          </div>
        </div>
      </div>

      <ForceGraph2D
        ref={fgRef}
        graphData={graphData}
        backgroundColor="#fcfcfd"
        nodeRelSize={1}
        cooldownTicks={0}

        onRenderFramePre={(ctx, globalScale) => {
          ctx.save()
          // Top horizontal axis line spanning all semesters
          ctx.beginPath()
          ctx.moveTo(-4 * COLUMN_WIDTH, -550)
          ctx.lineTo(4.5 * COLUMN_WIDTH, -550)
          ctx.strokeStyle = '#1e293b'
          ctx.lineWidth = 1 / globalScale
          ctx.stroke()
          
          // Arrow at the end of the top axis
          ctx.beginPath()
          ctx.moveTo(4.5 * COLUMN_WIDTH, -550)
          ctx.lineTo(4.5 * COLUMN_WIDTH - 10 / globalScale, -550 - 5 / globalScale)
          ctx.lineTo(4.5 * COLUMN_WIDTH - 10 / globalScale, -550 + 5 / globalScale)
          ctx.fillStyle = '#1e293b'
          ctx.fill()

          for (let i = 1; i <= 8; i++) {
            const x = (i - 4.5) * COLUMN_WIDTH

            // Semester vertical dividing line
            ctx.beginPath()
            ctx.setLineDash([5, 5])
            ctx.strokeStyle = 'rgba(0, 0, 0, 0.04)'
            ctx.lineWidth = 1 / globalScale
            ctx.moveTo(x - COLUMN_WIDTH / 2, -500)
            ctx.lineTo(x - COLUMN_WIDTH / 2, 1000)
            ctx.stroke()

            const fontSize = 14
            ctx.font = `black ${fontSize}px Inter, sans-serif`
            ctx.fillStyle = '#1e293b'
            ctx.textAlign = 'center'
            ctx.textBaseline = 'bottom'
            ctx.setLineDash([])
            ctx.fillText(`HK${i}`, x, -560)
          }
          ctx.restore()
        }}

        nodeCanvasObject={(node: any, ctx, globalScale) => {
          const isFailed = failedNodes.has(node.id)
          const isBlocked = blockedNodes.has(node.id) && !isFailed
          const isSelected = selectedNodeId === node.id
          const isAiRecommended = aiElectiveNodes.has(node.id)
          
          let borderColor = isSelected ? '#10b981' : (isAiRecommended ? '#7c3aed' : '#334155')
          let bgColor = isSelected ? '#ecfdf5' : (isAiRecommended ? '#f5f3ff' : '#ffffff')
          let textColor = isSelected ? '#065f46' : (isAiRecommended ? '#5b21b6' : '#1e293b')

          if (isFailed) { borderColor = '#ef4444'; bgColor = '#fef2f2'; textColor = '#991b1b' }
          else if (isBlocked) { borderColor = '#fca5a5'; bgColor = '#fff1f2'; textColor = '#991b1b' }

          const x = node.x - CARD_WIDTH / 2
          const y = node.y - CARD_HEIGHT / 2

          ctx.fillStyle = bgColor
          ctx.strokeStyle = borderColor
          ctx.lineWidth = (isSelected || isAiRecommended ? 2 : 1) / globalScale

          ctx.beginPath()
          ctx.roundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 4)
          ctx.fill()
          ctx.stroke()

          const fs = 11
          ctx.textAlign = 'center'
          ctx.textBaseline = 'middle'

          // Course Code
          ctx.font = `600 ${fs}px Inter, sans-serif`
          ctx.fillStyle = textColor
          ctx.fillText(node.code, node.x, node.y - 12)

          if (isAiRecommended && !isSelected) {
            ctx.font = `${fs}px Inter, sans-serif`
            ctx.fillText('✨', node.x + CARD_WIDTH / 2 - 12, node.y - CARD_HEIGHT / 2 + 12)
          }

          // Course Name
          ctx.font = `400 ${fs * 0.9}px Inter, sans-serif`
          ctx.fillStyle = isFailed || isBlocked ? textColor : '#334155'
          const n = node.name || ''
          
          // Multi-line wrap for course name
          const words = n.split(' ')
          let line = ''
          let currentY = node.y + 4
          for (let i = 0; i < words.length; i++) {
            const testLine = line + words[i] + ' '
            const metrics = ctx.measureText(testLine)
            if (metrics.width > CARD_WIDTH - 8 && i > 0) {
              ctx.fillText(line.trim(), node.x, currentY)
              line = words[i] + ' '
              currentY += fs * 1.2
            } else {
              line = testLine
            }
          }
          ctx.fillText(line.trim(), node.x, currentY)
        }}

        linkCanvasObject={(link: any, ctx, globalScale) => {
          const start = link.source
          const end = link.target
          if (!start || !end || typeof start !== 'object' || typeof end !== 'object') return

          const isAffected = blockedNodes.has(start.id) || blockedNodes.has(end.id)
          const isPrerequisite = link.type === 'PREREQUISITE'
          
          const startX = start.x + CARD_WIDTH / 2
          const startY = start.y
          const endX = end.x - CARD_WIDTH / 2
          const endY = end.y

          const baseColor = isAffected ? '#fca5a5' : '#1e293b'

          const drawPath = (offsetY: number = 0) => {
            ctx.beginPath()
            ctx.moveTo(startX, startY + offsetY)
            
            if (Math.abs(start.x - end.x) < 10) {
               ctx.lineTo(startX + 20/globalScale, startY + offsetY)
               ctx.lineTo(startX + 20/globalScale, endY + offsetY)
               ctx.lineTo(endX, endY + offsetY)
            } else {
              const midX = startX + (endX - startX) / 2
              ctx.lineTo(midX, startY + offsetY)
              ctx.lineTo(midX, endY + offsetY)
              ctx.lineTo(endX, endY + offsetY)
            }
          }

          if (isPrerequisite) {
            ctx.lineWidth = 3 / globalScale
            ctx.strokeStyle = baseColor
            drawPath()
            ctx.stroke()

            ctx.lineWidth = 1 / globalScale
            ctx.strokeStyle = '#fcfcfd'
            drawPath()
            ctx.stroke()
            
            if (globalScale > 0.2) {
              const arrowSize = 8 / globalScale
              ctx.beginPath()
              ctx.moveTo(endX + arrowSize, endY)
              ctx.lineTo(endX, endY - arrowSize/1.5)
              ctx.lineTo(endX, endY + arrowSize/1.5)
              ctx.closePath()
              ctx.fillStyle = '#fcfcfd'
              ctx.fill()
              ctx.lineWidth = 1 / globalScale
              ctx.strokeStyle = baseColor
              ctx.stroke()
            }
          } else {
            ctx.lineWidth = 1 / globalScale
            ctx.strokeStyle = baseColor
            drawPath()
            ctx.stroke()
            
            if (globalScale > 0.2) {
              const arrowSize = 6 / globalScale
              ctx.beginPath()
              ctx.moveTo(endX, endY)
              ctx.lineTo(endX - arrowSize, endY - arrowSize/1.5)
              ctx.lineTo(endX - arrowSize, endY + arrowSize/1.5)
              ctx.closePath()
              ctx.fillStyle = baseColor
              ctx.fill()
            }
          }
        }}

        linkDirectionalParticles={0}

        onNodeClick={handleNodeClick}
        onEngineStop={() => {
          if (fgRef.current) {
            fgRef.current.zoomToFit(400, 100)
          }
        }}
      />
    </div>
  )
}
