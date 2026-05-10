import { useMemo, useCallback, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useKnowledgeGraph } from '../../../hooks/useKnowledgeGraph'
import { computeGalaxyData } from './layout/galaxyLayout'
import type { GalaxyEdge } from './layout/types'
import { GalaxyCanvas } from './GalaxyCanvas'
import { ConstellationSystem } from './systems/ConstellationSystem'
import { WormholeSystem } from './systems/WormholeSystem'
import { OrbitRings } from './effects/OrbitRings'
import { GalaxyOverlay } from './ui/GalaxyOverlay'
import { TimeTravelSlider } from './ui/TimeTravelSlider'
import { useGalaxyStore } from './store/useGalaxyStore'
import { PlanetPositionProvider } from './context/PlanetPositionContext'

function computeBlocked(failed: Set<number>, edges: GalaxyEdge[]): Set<number> {
  if (failed.size === 0) return new Set()
  const blocked = new Set<number>(failed)
  const queue = [...failed]
  let idx = 0
  while (idx < queue.length) {
    const current = queue[idx++]
    for (const e of edges) {
      if (e.type !== 'PREREQUISITE') continue
      if (e.source !== current) continue
      if (!blocked.has(e.target)) {
        blocked.add(e.target)
        queue.push(e.target)
      }
    }
  }
  return blocked
}

export default function GalaxyPage() {
  const navigate = useNavigate()
  const { data, isLoading, error } = useKnowledgeGraph()
  const isSimulationMode = useGalaxyStore((s) => s.isSimulationMode)
  const failedNodes = useGalaxyStore((s) => s.failedNodes)
  const toggleFailedNode = useGalaxyStore((s) => s.toggleFailedNode)
  const setBlockedNodes = useGalaxyStore((s) => s.setBlockedNodes)
  const setWeekMarkers = useGalaxyStore((s) => s.setWeekMarkers)

  const galaxyData = useMemo(() => {
    if (!data) return null
    return computeGalaxyData(data.nodes, data.links)
  }, [data])

  useEffect(() => {
    if (galaxyData) {
      setBlockedNodes(computeBlocked(failedNodes, galaxyData.edges))
    }
  }, [failedNodes, galaxyData, setBlockedNodes])

  useEffect(() => {
    if (!data) return
    const levelCounts = new Map<number, number>()
    for (const n of data.nodes) {
      levelCounts.set(n.level, (levelCounts.get(n.level) ?? 0) + 1)
    }
    const markers = Array.from(levelCounts.entries())
      .sort((a, b) => a[0] - b[0])
      .map(([week, count]) => ({ week, count }))
    setWeekMarkers(markers)
  }, [data, setWeekMarkers])

  const handlePlanetClick = useCallback((id: number) => {
    if (isSimulationMode) {
      toggleFailedNode(id)
    } else {
      navigate(`/courses/${id}`)
    }
  }, [isSimulationMode, navigate, toggleFailedNode])

  if (isLoading) {
    return (
      <div className="relative h-[80vh] flex items-center justify-center overflow-hidden bg-[#05070a]">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 h-[600px] w-[600px] bg-emerald-500/[0.05] blur-[150px] rounded-full animate-pulse" />
        <div className="relative flex flex-col items-center gap-6">
          <div className="relative h-20 w-20">
            <div className="absolute inset-0 rounded-full border-4 border-emerald-500/10" />
            <div className="absolute inset-0 rounded-full border-t-4 border-emerald-500 animate-spin shadow-[0_0_20px_rgba(16,185,129,0.4)]" />
          </div>
          <div className="flex flex-col items-center">
            <p className="text-[12px] font-black text-emerald-500 tracking-[0.4em] uppercase mb-2">Đang thiết lập</p>
            <p className="heading-3 text-clay-text animate-pulse tracking-widest font-bold">DẢI NGÂN HÀ TRI THỨC</p>
          </div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="bg-[#05070a] flex flex-col items-center justify-center h-screen text-ink-secondary gap-4">
        <h2 className="text-clay-text">Không thể tải galaxy kiến thức</h2>
        <p>Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại.</p>
        <button onClick={() => window.location.reload()} className="btn-secondary text-[11px] px-6 py-2.5 uppercase tracking-[0.2em]">
          Thử lại
        </button>
      </div>
    )
  }

  if (!galaxyData) return null

  return (
    <div className="relative w-full h-[calc(100vh-80px)] bg-[#05070a] overflow-hidden">
      <GalaxyOverlay />
      <TimeTravelSlider />

      <div className="absolute bottom-8 right-8 z-20 flex flex-col items-end gap-3 pointer-events-none animate-in fade-in slide-in-from-right-8 duration-1000">
        <div className="glass-card px-5 py-3 text-[10px] font-black uppercase tracking-[0.2em] text-ink-muted border-glass-border backdrop-blur-md">
          Kéo: Xoay • Cuộn: Zoom • Nhấn: Xem môn học
        </div>
      </div>

      <GalaxyCanvas>
        <PlanetPositionProvider>
          <WormholeSystem edges={galaxyData.edges} galaxies={galaxyData.galaxies} />
          <OrbitRings galaxies={galaxyData.galaxies} />
          <ConstellationSystem galaxies={galaxyData.galaxies} onPlanetClick={handlePlanetClick} />
        </PlanetPositionProvider>
      </GalaxyCanvas>
    </div>
  )
}
