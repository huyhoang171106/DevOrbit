import { useRef, useState, useMemo } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import ForceGraph2D from 'react-force-graph-2d'
import { useKnowledgeGraph } from '../../../hooks/useKnowledgeGraph'
import { useCourseList } from '../../../hooks/useCourseList'
import type { GraphNode } from '../../../types/api'
import {
  Graph,
  Play,
  ArrowClockwise,
  WarningCircle,
  X,
  Compass,
  MagnifyingGlass,
  ArrowsOut,
  ArrowsIn,
  Kanban,
  CaretDown,
} from '@phosphor-icons/react'
import { KanbanBoard } from '../../../components/student/KanbanBoard'
import {
  GRADUATION_PATHS,
  SPECIALTY_COURSES,
  ELECTIVE_CREDITS,
  COURSE_NAMES,
  CO_SO_NGANH_COURSES,
  CO_SO_NGANH_MIN_TC,
  CHUYEN_NGANH_MIN_TC,
  ELECTIVE_SECTION_GROUPS,
  type ElectiveSubGroup,
} from '../../../components/student/CareerOrientationData'
import {
  getFixedSemesterCodes,
  getCurriculumCredits,
  getCurriculumPrereqs,
} from '../../../components/student/curriculumData'

type ViewMode = 'graph' | 'kanban'

// ─── Helpers ───

/** Set of mandatory course codes that are always in the curriculum (no warning needed) */
const MANDATORY_SEMESTER_CODES = new Set(getFixedSemesterCodes().map(c => c.toUpperCase()))

function getSelectedCredits(codes: Set<string>): number {
  let total = 0
  for (const code of codes) total += ELECTIVE_CREDITS[code] ?? 3
  return total
}

function getCsnCredits(codes: Set<string>): number {
  let total = 0
  for (const c of CO_SO_NGANH_COURSES) if (codes.has(c)) total += ELECTIVE_CREDITS[c] ?? 3
  return total
}

function getCnCredits(codes: Set<string>): number {
  let total = 0
  for (const code of codes) {
    if (!CO_SO_NGANH_COURSES.includes(code)) total += ELECTIVE_CREDITS[code] ?? 3
  }
  return total
}

// ─── Elective course group section with sub-groups ───

function ElectiveSubGroupSection({
  subgroup,
  selected,
  onToggle,
}: {
  subgroup: ElectiveSubGroup
  selected: Set<string>
  onToggle: (code: string) => void
}) {
  return (
    <div className="mb-2">
      <p className="text-[9px] font-semibold tracking-[0.08em] text-orbit-text-muted/70 mb-1 px-1">
        {subgroup.title}
      </p>
      <div className="grid grid-cols-1 gap-0.5">
        {subgroup.codes.map(code => {
          const checked = selected.has(code)
          const credits = ELECTIVE_CREDITS[code] ?? 3
          const name = COURSE_NAMES[code]
          return (
            <button
              key={code}
              onClick={() => onToggle(code)}
              className={`flex items-center gap-3 w-full px-3 py-1.5 rounded-xl text-left transition-all ${
                checked
                  ? 'bg-orbit-accent/8'
                  : 'hover:bg-orbit-surface/50'
              }`}
            >
              <div className={`h-3 w-3 shrink-0 rounded-[4px] border transition-all flex items-center justify-center ${
                checked
                  ? 'bg-orbit-accent border-orbit-accent'
                  : 'border-orbit-border/40'
              }`}>
                {checked && (
                  <svg width="6" height="6" viewBox="0 0 8 8" fill="none">
                    <path d="M1.5 4L3.5 6L6.5 2" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" className="text-orbit-bg"/>
                  </svg>
                )}
              </div>
              <span className={`text-[10px] font-medium flex-1 ${checked ? 'text-orbit-text' : 'text-orbit-text-muted'}`}>
                {code}
              </span>
              {name && (
                <span className="text-[8px] text-orbit-text-muted/60 flex-1 truncate max-w-[120px]">
                  {name}
                </span>
              )}
              <span className="text-[8px] text-orbit-text-muted shrink-0">
                {credits}TC
              </span>
            </button>
          )
        })}
      </div>
    </div>
  )
}

// ─── Main ───

export default function GalaxyPage() {
  const fgRef = useRef<any>(null)
  const navigate = useNavigate()
  const { data, isLoading, error } = useKnowledgeGraph()

  // Also fetch full course list to include elective courses not in the graph
  const { data: allCourses } = useCourseList()

  // View mode
  const [viewMode, setViewMode] = useState<ViewMode>('kanban')

  // Force graph state
  const [zoomLevel, setZoomLevel] = useState(1)
  const [showLegend, setShowLegend] = useState(true)
  const [isSimulationMode, setIsSimulationMode] = useState(false)
  const [failedNodes, setFailedNodes] = useState<Set<number>>(new Set())
  const [graphReady, setGraphReady] = useState(false)

  // Career orientation
  const [selectedElectiveCodes, setSelectedElectiveCodes] = useState<Set<string>>(new Set())
  const [showElectivePicker, setShowElectivePicker] = useState(false)

  // Prerequisite warning state
  const [prereqWarning, setPrereqWarning] = useState<{
    code: string
    name: string
    missing: { code: string; name: string }[]
  } | null>(null)

  // ─── Elective toggle handler with prerequisite check ───
  const handleToggleElective = (code: string) => {
    const isAdding = !selectedElectiveCodes.has(code)
    if (isAdding) {
      // Check prerequisites
      const prereqs = getCurriculumPrereqs(code)
      if (prereqs.length > 0) {
        const missingPrereqs = prereqs.filter(p => {
          const pUpper = p.toUpperCase()
          // Skip if the prereq is a mandatory course (always in curriculum)
          if (MANDATORY_SEMESTER_CODES.has(pUpper)) return false
          // Skip if the prereq is already selected as an elective
          if (selectedElectiveCodes.has(pUpper)) return false
          // Also skip if the prereq itself is already being toggled in this operation
          return true
        })
        if (missingPrereqs.length > 0) {
          // Show warning popup
          setPrereqWarning({
            code: code.toUpperCase(),
            name: COURSE_NAMES[code.toUpperCase()] ?? code,
            missing: missingPrereqs.map(p => ({
              code: p.toUpperCase(),
              name: COURSE_NAMES[p.toUpperCase()] ?? p,
            })),
          })
          return // Don't add the course yet
        }
      }
    }
    // Safe to toggle
    const next = new Set(selectedElectiveCodes)
    next.has(code) ? next.delete(code) : next.add(code)
    setSelectedElectiveCodes(next)
  }

  // Recursive blocking logic (graph mode)
  const blockedNodes = useMemo(() => {
    if (!data || failedNodes.size === 0) return new Set<number>()

    const blocked = new Set<number>(failedNodes)
    const queue = Array.from(failedNodes)

    while (queue.length > 0) {
      const currentId = queue.shift()!
      const downstream = data.links
        .filter(link => link.source === currentId && link.type === 'PREREQUISITE')
        .map(link => link.target)

      for (const targetId of downstream) {
        if (!blocked.has(targetId)) {
          blocked.add(targetId)
          queue.push(targetId)
        }
      }
    }
    return blocked
  }, [data, failedNodes])

  const delayEstimate = useMemo(() => {
    if (!data || blockedNodes.size === 0) return 0
    const blockedNodeList = data.nodes.filter(n => blockedNodes.has(n.id))
    const maxLevel = Math.max(...blockedNodeList.map(n => n.level))
    const minLevel = Math.min(...blockedNodeList.map(n => n.level))
    return Math.max(1, Math.floor((maxLevel - minLevel) / 1.5) + 1)
  }, [data, blockedNodes])

  // Merge graph nodes with career-recommended courses from full course list
  const kanbanNodes = useMemo<GraphNode[]>(() => {
    if (!data) return []
    if (!allCourses) return data.nodes

    const graphCodes = new Set(data.nodes.map(n => n.code.toUpperCase()))

    // Always-needed codes: selected electives + graduation + fixed-semester curriculum courses
    const alwaysNeeded = new Set<string>(selectedElectiveCodes)
    // Graduation course codes
    for (const gp of GRADUATION_PATHS) {
      for (const c of gp.courses) alwaysNeeded.add(c.code)
    }
    for (const sc of SPECIALTY_COURSES) alwaysNeeded.add(sc.code)
    // Fixed-semester curriculum courses (SE502, SE503, etc.)
    for (const code of getFixedSemesterCodes()) alwaysNeeded.add(code.toUpperCase())

    const missingCodes = new Set<string>()
    for (const code of alwaysNeeded) {
      if (!graphCodes.has(code)) missingCodes.add(code)
    }

    if (missingCodes.size === 0) return data.nodes

    // Create synthetic nodes for missing courses
    const extraNodes: GraphNode[] = []
    for (const course of allCourses) {
      if (missingCodes.has(course.code.toUpperCase())) {
        extraNodes.push({
          id: course.id,
          name: course.name,
          code: course.code,
          description: null,
          val: course.credits ?? getCurriculumCredits(course.code.toUpperCase()) ?? 6,
          level: 0,
          impactScore: 0,
          semester: course.semester ?? null,
          electiveGroup: 'CAREER_RECOMMENDED',
        })
      }
    }

    return [...data.nodes, ...extraNodes]
  }, [data, selectedElectiveCodes, allCourses])

  // Build credit map: courseId → credits from API + DAA curriculum data
  const creditMap = useMemo(() => {
    const map = new Map<number, number>()
    if (allCourses) {
      for (const c of allCourses) {
        map.set(c.id, c.credits ?? 3)
      }
    }
    // Fallback to DAA curriculum for courses not in API response
    for (const n of kanbanNodes) {
      if (!map.has(n.id)) {
        const daaCredits = getCurriculumCredits(n.code.toUpperCase())
        if (daaCredits !== undefined) map.set(n.id, daaCredits)
      }
    }
    return map
  }, [allCourses, kanbanNodes])

  const toggleSimulation = () => {
    setIsSimulationMode(!isSimulationMode)
    if (isSimulationMode) setFailedNodes(new Set())
  }

  const resetSimulation = () => setFailedNodes(new Set())

  const handleZoomIn = () => {
    if (fgRef.current) {
      const newZoom = Math.min(zoomLevel * 1.4, 8)
      setZoomLevel(newZoom)
      fgRef.current.zoom(newZoom, 400)
    }
  }

  const handleZoomOut = () => {
    if (fgRef.current) {
      const newZoom = Math.max(zoomLevel / 1.4, 0.3)
      setZoomLevel(newZoom)
      fgRef.current.zoom(newZoom, 400)
    }
  }

  const handleResetZoom = () => {
    if (fgRef.current && (data?.nodes.length ?? 0) > 0) {
      setZoomLevel(1.8)
      fgRef.current.zoomToFit(400, 80)
    }
  }

  if (isLoading) {
    return (
      <div className="h-[calc(100vh-72px)] flex items-center justify-center bg-orbit-bg">
        <div className="flex flex-col items-center gap-6">
          <div className="relative h-12 w-12">
            <div className="absolute inset-0 rounded-full border-2 border-orbit-accent/10" />
            <div className="absolute inset-0 rounded-full border-t-2 border-orbit-accent animate-spin shadow-[0_0_20px_rgba(52,211,153,0.2)]" />
          </div>
          <p className="text-[11px] font-black text-orbit-accent tracking-[0.3em] uppercase">Đang thiết lập</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-[calc(100vh-72px)] px-6">
        <div className="orbit-card p-12 md:p-16 max-w-md text-center">
          <div className="h-16 w-16 rounded-2xl bg-rose-500/10 border border-rose-500/20 flex items-center justify-center mx-auto mb-8">
            <WarningCircle className="h-8 w-8 text-rose-500" weight="duotone" />
          </div>
          <h2 className="heading-4 mb-4 text-orbit-text">Không thể tải sơ đồ kiến thức</h2>
          <p className="body-md mb-8">{error instanceof Error ? error.message : 'Đã xảy ra lỗi không xác định'}</p>
          <button onClick={() => window.location.reload()} className="btn-primary">
            Thử lại
          </button>
        </div>
      </div>
    )
  }

  const graphData = data ?? { nodes: [], links: [] }

  return (
    <div className="relative w-full h-[calc(100vh-72px)] bg-orbit-bg overflow-hidden flex flex-col">
      {/* ─── TOP TOOLBAR ─── */}
      <div className="shrink-0 flex items-center justify-between px-6 py-3 border-b border-orbit-border/30 bg-orbit-bg/90 backdrop-blur-xl z-30">
        {/* Left: View mode toggle */}
        <div className="flex items-center gap-2">
          <div className="flex rounded-2xl border border-orbit-border/50 overflow-hidden">
            <button
              onClick={() => setViewMode('kanban')}
              className={`flex items-center gap-2 px-4 py-2 text-[10px] font-bold uppercase tracking-[0.12em] transition-all ${
                viewMode === 'kanban'
                  ? 'bg-orbit-accent text-zinc-950'
                  : 'text-orbit-text-muted hover:text-orbit-text hover:bg-orbit-surface'
              }`}
            >
              <Kanban className="h-3.5 w-3.5" weight={viewMode === 'kanban' ? 'fill' : 'regular'} />
              Kanban
            </button>
            <button
              onClick={() => setViewMode('graph')}
              className={`flex items-center gap-2 px-4 py-2 text-[10px] font-bold uppercase tracking-[0.12em] transition-all ${
                viewMode === 'graph'
                  ? 'bg-orbit-accent text-zinc-950'
                  : 'text-orbit-text-muted hover:text-orbit-text hover:bg-orbit-surface'
              }`}
            >
              <Graph className="h-3.5 w-3.5" weight={viewMode === 'graph' ? 'fill' : 'regular'} />
              Đồ thị
            </button>
          </div>

          {/* Elective course selector */}
          <div className="relative ml-4">
            <button
              onClick={() => setShowElectivePicker(!showElectivePicker)}
              className={`flex items-center gap-2 px-4 py-2 rounded-2xl border text-[10px] font-bold uppercase tracking-[0.1em] transition-all ${
                selectedElectiveCodes.size > 0
                  ? 'border-orbit-accent/40 text-orbit-accent bg-orbit-accent/5 hover:bg-orbit-accent/15'
                  : 'border-orbit-border/50 text-orbit-text-muted hover:text-orbit-text hover:border-orbit-accent/30'
              }`}
            >
              <Compass className="h-3.5 w-3.5" weight="regular" />
              {selectedElectiveCodes.size > 0
                ? `${selectedElectiveCodes.size} môn đã chọn`
                : 'Môn tự chọn'}
              <CaretDown className="h-3 w-3" weight="bold" />
            </button>

            <AnimatePresence>
              {showElectivePicker && (
                <>
                  <div className="fixed inset-0 z-40" onClick={() => setShowElectivePicker(false)} />
                  <motion.div
                    initial={{ opacity: 0, y: -8, scale: 0.96 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    exit={{ opacity: 0, y: -8, scale: 0.96 }}
                    transition={{ type: 'spring', stiffness: 200, damping: 25 }}
                    className="absolute top-full left-0 mt-2 w-[480px] z-50 origin-top-left"
                  >
                    <div className="orbit-glass-strong p-4 rounded-3xl max-h-[520px] overflow-y-auto">
                      <div className="flex items-center justify-between mb-3 px-1">
                        <p className="text-[9px] font-black uppercase tracking-[0.15em] text-orbit-text-muted">
                          Chọn môn tự chọn
                        </p>
                        {selectedElectiveCodes.size > 0 && (
                          <button
                            onClick={() => setSelectedElectiveCodes(new Set())}
                            className="text-[9px] font-semibold text-rose-400 hover:text-rose-300 transition-all"
                          >
                            Xoá tất cả
                          </button>
                        )}
                      </div>

                      {ELECTIVE_SECTION_GROUPS.map(group => (
                        <div key={group.title} className="mb-4">
                          <div className="flex items-center justify-between px-1 mb-2">
                            <p className="text-[9px] font-black uppercase tracking-[0.12em] text-orbit-text-muted">
                              {group.title}
                              {group.minTC > 0 && <span className="text-orbit-accent ml-1">(≥{group.minTC}TC)</span>}
                            </p>
                            <button
                              onClick={() => {
                                const next = new Set(selectedElectiveCodes)
                                for (const c of group.allCodes) next.add(c)
                                setSelectedElectiveCodes(next)
                              }}
                              className="text-[8px] font-bold text-orbit-accent hover:text-orbit-accent/80 transition-all tracking-[0.08em]"
                            >
                              Chọn tất cả
                            </button>
                          </div>
                          <div className="space-y-1.5">
                            {group.subgroups.map(sg => (
                              <ElectiveSubGroupSection
                                key={sg.title}
                                subgroup={sg}
                                selected={selectedElectiveCodes}
                                onToggle={handleToggleElective}
                              />
                            ))}
                          </div>
                        </div>
                      ))}

                      {/* Credit total */}
                      <div className="mt-4 pt-3 border-t border-orbit-border/15 px-1 space-y-2">
                        {/* Cơ sở ngành */}
                        <div className="flex items-center justify-between">
                          <span className="text-[10px] text-orbit-text-muted">Cơ sở ngành</span>
                          <span className={`text-[12px] font-black tabular-nums ${
                            getCsnCredits(selectedElectiveCodes) >= CO_SO_NGANH_MIN_TC
                              ? 'text-emerald-400'
                              : 'text-amber-400'
                          }`}>
                            {getCsnCredits(selectedElectiveCodes)} / {CO_SO_NGANH_MIN_TC} TC
                          </span>
                        </div>
                        {/* Chuyên ngành */}
                        <div className="flex items-center justify-between">
                          <span className="text-[10px] text-orbit-text-muted">Chuyên ngành</span>
                          <span className={`text-[12px] font-black tabular-nums ${
                            getCnCredits(selectedElectiveCodes) >= CHUYEN_NGANH_MIN_TC
                              ? 'text-emerald-400'
                              : 'text-amber-400'
                          }`}>
                            {getCnCredits(selectedElectiveCodes)} / {CHUYEN_NGANH_MIN_TC} TC
                          </span>
                        </div>
                        {/* Tổng */}
                        <div className="flex items-center justify-between pt-1 border-t border-orbit-border/10">
                          <span className="text-[10px] font-bold text-orbit-text">Tổng tự chọn</span>
                          <span className="text-[14px] font-black tabular-nums text-orbit-accent">
                            {getSelectedCredits(selectedElectiveCodes)} TC
                          </span>
                        </div>
                      </div>
                    </div>
                  </motion.div>
                </>
              )}
            </AnimatePresence>
          </div>
        </div>

        {/* Right: nav link */}
        <Link
          to="/courses"
          className="flex items-center gap-2 px-4 py-2 rounded-2xl border border-orbit-border/30 text-[10px] font-semibold text-orbit-text-muted hover:text-orbit-text hover:border-orbit-accent/30 transition-all"
        >
          <MagnifyingGlass className="h-3.5 w-3.5" weight="regular" />
          Danh mục môn học
        </Link>
      </div>

      {/* ─── CONTENT AREA ─── */}
      <div className="flex-1 relative overflow-hidden">
        <AnimatePresence mode="wait">
          {viewMode === 'kanban' ? (
            /* ─── KANBAN BOARD ─── */
            <motion.div
              key="kanban"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.2 }}
              className="absolute inset-0"
            >
              <KanbanBoard
                nodes={kanbanNodes}
                links={graphData.links}
                selectedElectiveCodes={selectedElectiveCodes}
                creditMap={creditMap}
              />
            </motion.div>
          ) : (
            /* ─── FORCE GRAPH ─── */
            <motion.div
              key="graph"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.2 }}
              className="absolute inset-0"
            >
              {/* Grid background */}
              <div
                className="absolute inset-0 opacity-[0.02] pointer-events-none"
                style={{ backgroundImage: 'radial-gradient(rgba(52, 211, 153, 0.3) 1px, transparent 1px)', backgroundSize: '48px 48px' }}
              />

              {/* ─── LEGEND PANEL ─── */}
              <AnimatePresence>
                {showLegend && (
                  <motion.div
                    initial={{ opacity: 0, x: -20, scale: 0.98 }}
                    animate={{ opacity: 1, x: 0, scale: 1 }}
                    exit={{ opacity: 0, x: -20, scale: 0.98 }}
                    transition={{ type: 'spring', stiffness: 100, damping: 20 }}
                    className="absolute top-6 left-6 z-20 max-w-xs"
                  >
                    <div className="orbit-glass-strong p-6">
                      <div className="flex items-center justify-between mb-5">
                        <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-orbit-accent-subtle border border-orbit-accent/20">
                          <span className="h-1.5 w-1.5 rounded-full bg-orbit-accent animate-breathing" />
                          <span className="text-[9px] font-black uppercase tracking-[0.15em] text-orbit-accent">Trực tuyến</span>
                        </div>
                        <button
                          onClick={() => setShowLegend(false)}
                          className="h-7 w-7 rounded-lg bg-orbit-surface border border-orbit-border flex items-center justify-center text-orbit-text-muted hover:text-orbit-text transition-colors"
                        >
                          <X className="h-3.5 w-3.5" weight="bold" />
                        </button>
                      </div>

                      <h2 className="text-[18px] font-black text-orbit-text tracking-tight mb-2">Hệ sinh thái học thuật</h2>
                      <p className="text-[12px] text-orbit-text-secondary leading-relaxed mb-6">
                        Mạng lưới các môn học tại UIT. Phân tích mối liên hệ và lộ trình học tập.
                      </p>

                      <div className="grid grid-cols-2 gap-x-4 gap-y-2.5 mb-6">
                        {[
                          { color: '#22d3ee', label: 'Cơ sở' },
                          { color: '#34d399', label: 'Cơ sở ngành' },
                          { color: '#6366f1', label: 'Trung cấp' },
                          { color: '#8b5cf6', label: 'Nâng cao' },
                          { color: '#f59e0b', label: 'Chuyên ngành' },
                          { color: '#f43f5e', label: 'Chuyên sâu' },
                        ].map(item => (
                          <div key={item.label} className="flex items-center gap-2.5">
                            <div className="h-2 w-3 rounded-sm" style={{ backgroundColor: item.color }} />
                            <span className="text-[10px] font-medium text-orbit-text-secondary">{item.label}</span>
                          </div>
                        ))}
                      </div>

                      <div className="space-y-2.5 pt-4 border-t border-orbit-border/50">
                        <div className="flex items-center gap-3">
                          <div className="h-px w-8 bg-orbit-accent/80" />
                          <div className="flex flex-col">
                            <span className="text-[10px] font-semibold text-orbit-accent uppercase tracking-wider">Môn tiên quyết</span>
                            <span className="text-[8px] text-orbit-text-muted">Mối liên hệ bắt buộc</span>
                          </div>
                        </div>
                        <div className="flex items-center gap-3">
                          <div className="h-px w-8 border-t border-dashed border-orbit-text-muted/50" />
                          <div className="flex flex-col">
                            <span className="text-[10px] font-semibold text-orbit-text-secondary uppercase tracking-wider">Bổ sung</span>
                            <span className="text-[8px] text-orbit-text-muted">Được khuyến nghị</span>
                          </div>
                        </div>
                      </div>

                      <div className="mt-6 flex flex-col gap-2.5">
                        <button
                          onClick={toggleSimulation}
                          className={`flex items-center justify-center gap-2.5 w-full px-5 py-3 rounded-2xl font-bold uppercase tracking-[0.1em] text-[11px] transition-all border ${
                            isSimulationMode
                              ? 'bg-rose-500/10 text-rose-400 border-rose-500/30 hover:bg-rose-500/20'
                              : 'bg-orbit-accent/10 text-orbit-accent border-orbit-accent/30 hover:bg-orbit-accent/20'
                          }`}
                        >
                          {isSimulationMode ? <X className="h-4 w-4" weight="bold" /> : <Play className="h-4 w-4" weight="fill" />}
                          {isSimulationMode ? 'Thoát giả lập' : 'Chế độ giả lập'}
                        </button>
                      </div>
                    </div>

                    {isSimulationMode && (
                      <motion.div
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="mt-3 orbit-glass-strong p-5"
                      >
                        <div className="flex items-center justify-between mb-4">
                          <span className="text-[10px] font-black uppercase tracking-widest text-orbit-text-muted">Chỉ số giả lập</span>
                          <button
                            onClick={resetSimulation}
                            className="flex items-center gap-1.5 text-[10px] font-semibold text-orbit-text-muted hover:text-orbit-accent transition-colors"
                          >
                            <ArrowClockwise className="h-3 w-3" weight="bold" />
                            Reset
                          </button>
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                          <div className="flex flex-col gap-1">
                            <span className="text-[28px] font-black text-orbit-text tabular-nums leading-none">{blockedNodes.size}</span>
                            <span className="text-[9px] font-medium uppercase tracking-widest text-orbit-text-muted">Môn học bị ảnh hưởng</span>
                          </div>
                          <div className="flex flex-col gap-1">
                            <div className="flex items-center gap-2">
                              <span className="text-[28px] font-black text-rose-400 tabular-nums leading-none">+{delayEstimate}</span>
                              <span className="text-[10px] text-rose-400 font-bold uppercase">Kỳ</span>
                            </div>
                            <span className="text-[9px] font-medium uppercase tracking-widest text-orbit-text-muted">Độ trễ tốt nghiệp</span>
                          </div>
                        </div>

                        {failedNodes.size > 0 && (
                          <div className="flex items-start gap-3 mt-4 p-3 rounded-2xl bg-rose-500/10 border border-rose-500/20">
                            <WarningCircle className="h-4 w-4 text-rose-400 shrink-0 mt-0.5" weight="fill" />
                            <p className="text-[11px] text-rose-300 leading-relaxed font-medium">
                              Rủi ro cao. Mô hình thất bại hiện tại làm gián đoạn các môn tiên quyết quan trọng.
                            </p>
                          </div>
                        )}

                        <p className="text-[10px] text-orbit-text-muted italic text-center mt-4">
                          Nhấn vào môn học để giả định không hoàn thành và xem ảnh hưởng lan truyền.
                        </p>
                      </motion.div>
                    )}
                  </motion.div>
                )}
              </AnimatePresence>

              {!showLegend && (
                <button
                  onClick={() => setShowLegend(true)}
                  className="absolute top-6 left-6 z-20 orbit-glass-strong px-4 py-3 flex items-center gap-3 hover:border-orbit-accent/30 transition-all"
                >
                  <Graph className="h-4 w-4 text-orbit-accent" weight="fill" />
                  <span className="text-[11px] font-bold text-orbit-text">Chú thích</span>
                </button>
              )}

              <div className="absolute bottom-6 right-6 z-20 flex flex-col gap-2">
                <div className="orbit-glass-strong flex flex-col rounded-2xl overflow-hidden">
                  <button onClick={handleZoomIn} className="p-3 text-orbit-text-secondary hover:text-orbit-accent hover:bg-orbit-accent/5 transition-all border-b border-orbit-border/50" aria-label="Phóng to">
                    <MagnifyingGlass className="h-4 w-4" weight="bold" />
                  </button>
                  <button onClick={handleZoomOut} className="p-3 text-orbit-text-secondary hover:text-orbit-accent hover:bg-orbit-accent/5 transition-all border-b border-orbit-border/50" aria-label="Thu nhỏ">
                    <ArrowsIn className="h-4 w-4" weight="bold" />
                  </button>
                  <button onClick={handleResetZoom} className="p-3 text-orbit-text-secondary hover:text-orbit-accent hover:bg-orbit-accent/5 transition-all" aria-label="Vừa khung">
                    <ArrowsOut className="h-4 w-4" weight="bold" />
                  </button>
                </div>
              </div>

              <div className="absolute bottom-6 left-1/2 -translate-x-1/2 z-20">
                <div className="orbit-glass-strong px-5 py-2.5 text-[10px] font-medium text-orbit-text-muted tracking-wide">
                  <span className="hidden sm:inline">Cuộn: Phóng to &bull; Kéo: Di chuyển &bull; Nhấn: </span>
                  {isSimulationMode ? 'Giả lập thất bại' : 'Xem môn học'}
                </div>
              </div>

              <ForceGraph2D
                ref={fgRef}
                graphData={graphData}
                backgroundColor="transparent"
                nodeLabel={(node: any) => `
                  <div style="background: rgba(9, 9, 11, 0.96); border: 1px solid rgba(52, 211, 153, 0.3); border-radius: 20px; padding: 16px; box-shadow: 0 20px 40px rgba(0,0,0,0.6); backdrop-filter: blur(20px); max-width: 320px;">
                    <div style="color: #34d399; font-weight: 900; font-size: 10px; margin-bottom: 6px; letter-spacing: 0.15em; text-transform: uppercase;">${node.code}</div>
                    <div style="color: #fff; font-weight: 700; font-size: 14px; margin-bottom: 10px;">${node.name}</div>
                    ${node.description ? `<div style="color: rgba(255,255,255,0.6); font-size: 12px; line-height: 1.5; border-top: 1px solid rgba(255,255,255,0.08); padding-top: 10px;">${node.description}</div>` : ''}
                  </div>
                `}
                nodeColor={() => '#34d399'}
                nodeRelSize={1.2}
                nodeCanvasObject={(node: any, ctx, globalScale) => {
                  const label = node.code
                  const fontSize = 12 / globalScale
                  ctx.font = `${fontSize}px "Geist Mono", monospace`
                  if (!Number.isFinite(node.x) || !Number.isFinite(node.y)) return
                  if (!Number.isFinite(node.val) || node.val <= 0) return

                  const isFailed = failedNodes.has(node.id)
                  const isBlocked = blockedNodes.has(node.id) && !isFailed
                  const impact = node.impactScore || 0
                  const impactFactor = impact / 10
                  const level = node.level || 0

                  let nodeColor = '#34d399'
                  let glowColor = 'rgba(52, 211, 153, 0.3)'
                  let glowMuted = 'rgba(52, 211, 153, 0.08)'

                  if (isFailed) {
                    const pulse = (Math.sin(Date.now() / 200) + 1) / 2
                    nodeColor = pulse > 0.5 ? '#f43f5e' : '#e11d48'
                    glowColor = `rgba(244, 63, 94, ${0.3 + pulse * 0.4})`
                    glowMuted = `rgba(244, 63, 94, ${0.08 + pulse * 0.1})`
                  } else if (isBlocked) {
                    const pulse = (Math.sin(Date.now() / 400) + 1) / 2
                    nodeColor = '#fb7185'
                    glowColor = `rgba(251, 113, 133, ${0.15 + pulse * 0.15})`
                    glowMuted = 'rgba(251, 113, 133, 0.05)'
                  } else if (isSimulationMode && impact > 7) {
                    nodeColor = '#f59e0b'
                    glowColor = 'rgba(245, 158, 11, 0.5)'
                    glowMuted = 'rgba(245, 158, 11, 0.15)'
                  } else {
                    switch (level) {
                      case 0: nodeColor = '#22d3ee'; glowColor = 'rgba(34, 211, 238, 0.3)'; glowMuted = 'rgba(34, 211, 238, 0.08)'; break
                      case 1: nodeColor = '#34d399'; glowColor = 'rgba(52, 211, 153, 0.3)'; glowMuted = 'rgba(52, 211, 153, 0.08)'; break
                      case 2: nodeColor = '#6366f1'; glowColor = 'rgba(99, 102, 241, 0.3)'; glowMuted = 'rgba(99, 102, 241, 0.08)'; break
                      case 3: nodeColor = '#8b5cf6'; glowColor = 'rgba(139, 92, 246, 0.3)'; glowMuted = 'rgba(139, 92, 246, 0.08)'; break
                      case 4: nodeColor = '#f59e0b'; glowColor = 'rgba(245, 158, 11, 0.3)'; glowMuted = 'rgba(245, 158, 11, 0.08)'; break
                      default: nodeColor = '#f43f5e'; glowColor = 'rgba(244, 63, 94, 0.3)'; glowMuted = 'rgba(244, 63, 94, 0.08)'
                    }
                  }

                  const glowSize = (node.val + 2) + (isSimulationMode ? impactFactor * 6 : 0)
                  const gradient = ctx.createRadialGradient(node.x, node.y, 0, node.x, node.y, glowSize)
                  gradient.addColorStop(0, glowColor)
                  gradient.addColorStop(0.5, glowMuted)
                  gradient.addColorStop(1, 'rgba(0, 0, 0, 0)')
                  ctx.beginPath()
                  ctx.arc(node.x, node.y, glowSize, 0, 2 * Math.PI, false)
                  ctx.fillStyle = gradient
                  ctx.fill()

                  const radius = (node.val / 2) + (isSimulationMode ? impactFactor * 2 : 0)
                  ctx.beginPath()
                  ctx.arc(node.x, node.y, radius, 0, 2 * Math.PI, false)
                  ctx.fillStyle = nodeColor
                  ctx.fill()

                  ctx.strokeStyle = (isSimulationMode && impact > 8) ? '#fff' : nodeColor
                  ctx.lineWidth = (isSimulationMode && impact > 8) ? 2.5 / globalScale : 1.5 / globalScale
                  ctx.stroke()

                  if (globalScale > 1.8) {
                    ctx.textAlign = 'center'
                    ctx.textBaseline = 'middle'
                    ctx.fillStyle = 'rgba(255, 255, 255, 0.85)'
                    ctx.fillText(label, node.x, node.y + (node.val / 2) + fontSize + 6)
                  }
                }}
                linkWidth={1.5}
                linkColor={(link: any) => {
                  if (blockedNodes.has(link.source?.id) || blockedNodes.has(link.target?.id)) return 'rgba(244, 63, 94, 0.5)'
                  return link.type === 'PREREQUISITE' ? 'rgba(52, 211, 153, 0.5)' : 'rgba(99, 102, 241, 0.4)'
                }}
                linkDirectionalArrowLength={4}
                linkDirectionalArrowRelPos={1}
                linkDirectionalParticles={3}
                linkDirectionalParticleSpeed={(link: any) => {
                  if (blockedNodes.has(link.source?.id) || blockedNodes.has(link.target?.id)) return 0.035
                  return 0.012
                }}
                linkDirectionalParticleWidth={2}
                linkDirectionalParticleColor={(link: any) => {
                  if (blockedNodes.has(link.source?.id) || blockedNodes.has(link.target?.id)) return '#f43f5e'
                  return link.type === 'PREREQUISITE' ? '#34d399' : '#6366f1'
                }}
                linkCurvature={0.08}
                linkLineDash={(link: any) => link.type === 'COMPLEMENTARY' ? [5, 4] : []}
                onNodeClick={(node: any) => {
                  if (isSimulationMode) {
                    setFailedNodes(prev => {
                      const next = new Set(prev)
                      if (next.has(node.id)) next.delete(node.id)
                      else next.add(node.id)
                      return next
                    })
                  } else {
                    navigate(`/courses/${node.id}`)
                  }
                }}
                cooldownTicks={50}
                d3AlphaDecay={0.02}
                d3VelocityDecay={0.35}
                onEngineStop={() => {
                  if (graphData.nodes.length > 0 && fgRef.current && !graphReady) {
                    setGraphReady(true)
                    fgRef.current.zoomToFit(400, 100)
                    setZoomLevel(1.8)
                  }
                }}
              />
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* ─── PREREQUISITE WARNING POPUP ─── */}
      <AnimatePresence>
        {prereqWarning && (
          <>
            <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-sm" onClick={() => setPrereqWarning(null)} />
            <div className="fixed inset-0 z-50 flex items-center justify-center pointer-events-none">
              <motion.div
                initial={{ opacity: 0, scale: 0.92 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.92 }}
                transition={{ type: 'spring', stiffness: 200, damping: 25 }}
                className="pointer-events-auto w-[420px]"
              >
              <div className="orbit-glass-strong p-6 rounded-[2.5rem]">
                {/* Icon */}
                <div className="flex items-center justify-center mb-4">
                  <div className="h-12 w-12 rounded-2xl bg-amber-400/10 flex items-center justify-center">
                    <WarningCircle className="h-6 w-6 text-amber-400" weight="fill" />
                  </div>
                </div>

                {/* Title */}
                <p className="text-[13px] font-black text-orbit-text text-center mb-1">
                  Môn học chưa đủ điều kiện
                </p>
                <p className="text-[10px] text-orbit-text-muted text-center mb-5">
                  {prereqWarning.name} ({prereqWarning.code}) yêu cầu hoàn thành các môn sau:
                </p>

                {/* Missing prereqs */}
                <div className="space-y-2 mb-6">
                  {prereqWarning.missing.map(m => (
                    <div key={m.code} className="flex items-center gap-3 px-4 py-2.5 rounded-2xl bg-rose-400/5 border border-rose-400/15">
                      <div className="h-2 w-2 rounded-full bg-rose-400 shrink-0" />
                      <div>
                        <span className="text-[10px] font-bold text-rose-400">{m.code}</span>
                        <span className="text-[9px] text-rose-400/70 ml-2">{m.name}</span>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Actions */}
                <div className="flex items-center gap-3">
                  <button
                    onClick={() => setPrereqWarning(null)}
                    className="flex-1 py-2.5 rounded-2xl border border-orbit-border/40 text-[10px] font-bold text-orbit-text-muted hover:text-orbit-text transition-all"
                  >
                    Huỷ
                  </button>
                  <button
                    onClick={() => {
                      // Force-add the course despite warning
                      const next = new Set(selectedElectiveCodes)
                      next.add(prereqWarning.code)
                      setSelectedElectiveCodes(next)
                      setPrereqWarning(null)
                    }}
                    className="flex-1 py-2.5 rounded-2xl bg-amber-400/15 border border-amber-400/30 text-[10px] font-bold text-amber-400 hover:bg-amber-400/25 transition-all"
                  >
                    Vẫn chọn
                  </button>
                </div>
              </div>
            </motion.div>
            </div>
          </>
        )}
      </AnimatePresence>
    </div>
  )
}
