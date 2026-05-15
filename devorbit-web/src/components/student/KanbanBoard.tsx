import { useState, useMemo, useCallback, useEffect } from 'react'
import { motion } from 'framer-motion'
import {
  DndContext,
  DragOverlay,
  closestCorners,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
  DragStartEvent,
  DragEndEvent,
  DragOverEvent,
} from '@dnd-kit/core'
import { useNavigate } from 'react-router-dom'
import { GraphNode, GraphLink } from '../../types/api'
import { KanbanColumn, MAX_CREDITS_PER_SEMESTER } from './KanbanColumn'
import { KanbanCard } from './KanbanCard'
import {
  SEMESTER_LABELS,
  SEMESTER_COLORS,
  GRADUATION_PATHS,
  GraduationPath,
  SPECIALTY_COURSES,
  ELECTIVE_CREDITS,
  CO_SO_NGANH_COURSES,
} from './CareerOrientationData'
import {
  Compass,
  Sparkle,
  FloppyDisk,
  ClockCounterClockwise,
  CheckCircle,
  CaretDown,
  GraduationCap,
  X,
} from '@phosphor-icons/react'
import {
  getFixedSemester,
  getCurriculumPrereqs,
  getCurriculumCredits,
} from './curriculumData'

type EnglishLevel = 'all' | 'eng2' | 'eng3' | 'passed'

function getEngSkipCodes(level: EnglishLevel): Set<string> {
  switch (level) {
    case 'eng2': return new Set(['ENG01'])
    case 'eng3': return new Set(['ENG01', 'ENG02'])
    case 'passed': return new Set(['ENG01', 'ENG02', 'ENG03'])
    default: return new Set()
  }
}

const ENG_LEVEL_LABELS: Record<EnglishLevel, string> = {
  all: 'Bắt đầu từ AV1',
  eng2: 'Bắt đầu từ AV2',
  eng3: 'Bắt đầu từ AV3',
  passed: 'Đã vượt qua AV',
}

type SemesterMap = Record<number, number | null>

type KanbanBoardProps = {
  nodes: GraphNode[]
  links: GraphLink[]
  selectedElectiveCodes: Set<string>
  creditMap: Map<number, number>
}

export function KanbanBoard({ nodes, links, selectedElectiveCodes, creditMap }: KanbanBoardProps) {
  const navigate = useNavigate()

  // Initialize semester assignments from graph data (graduation courses start unassigned)
  const [semesterMap, setSemesterMap] = useState<SemesterMap>(() => {
    const map: SemesterMap = {}
    const gradSet = new Set(['SE505', 'SE506', 'SE507'])
    for (const n of nodes) {
      if (gradSet.has(n.code.toUpperCase()) || SPECIALTY_COURSES.some(s => s.code === n.code.toUpperCase())) {
        map[n.id] = null
      } else {
        map[n.id] = n.semester ?? null
      }
    }
    return map
  })

  // Sync semesterMap when new nodes appear — exclude graduation courses from HK8 by default
  useEffect(() => {
    setSemesterMap(prev => {
      const next = { ...prev }
      let changed = false
      const gradSet = new Set(['SE505', 'SE506', 'SE507'])
      for (const n of nodes) {
        if (!(n.id in next)) {
          // Graduation and specialty courses start unassigned until path is chosen
          if (gradSet.has(n.code.toUpperCase()) || SPECIALTY_COURSES.some(s => s.code === n.code.toUpperCase())) {
            next[n.id] = null
          } else {
            next[n.id] = n.semester ?? null
          }
          changed = true
        }
      }
      return changed ? next : prev
    })
  }, [nodes])

  const [activeId, setActiveId] = useState<number | null>(null)
  const [activeColumn, setActiveColumn] = useState<number | null>(null)
  const [savedMapHash, setSavedMapHash] = useState<string | null>(null)
  const [saveMessage, setSaveMessage] = useState<string | null>(null)
  const [englishLevel, setEnglishLevel] = useState<EnglishLevel>('all')
  const [showEnglishMenu, setShowEnglishMenu] = useState(false)
  const [showGuide, setShowGuide] = useState(true)
  const [graduationPath, setGraduationPath] = useState<GraduationPath | null>(null)
  const [showGradMenu, setShowGradMenu] = useState(false)
  const [specialtyChoice, setSpecialtyChoice] = useState<string | null>(null)

  // Load saved assignments from localStorage on mount
  useEffect(() => {
    const saved = localStorage.getItem('devorbit_kanban_semester_map')
    if (saved) {
      try {
        const parsed = JSON.parse(saved) as SemesterMap
        setSemesterMap(parsed)
        setSavedMapHash(hashSemesterMap(parsed))
      } catch { /* ignore */ }
    }
  }, [])

  const recommendedIds = useMemo(
    () => new Set(
      nodes.filter(n => selectedElectiveCodes.has(n.code.toUpperCase())).map(n => n.id)
    ),
    [selectedElectiveCodes, nodes],
  )

  // Build prerequisite map
  const prereqMap = useMemo(() => {
    const map = new Map<number, number[]>()
    for (const link of links) {
      if (link.type === 'PREREQUISITE') {
        const target = typeof link.target === 'number' ? link.target : (link.target as Record<string, number>).id
        const source = typeof link.source === 'number' ? link.source : (link.source as Record<string, number>).id
        if (!map.has(target)) map.set(target, [])
        map.get(target)!.push(source)
      }
    }
    return map
  }, [links])

  // Build node lookup map
  const nodeMap = useMemo(() => {
    const map = new Map<number, GraphNode>()
    for (const n of nodes) map.set(n.id, n)
    return map
  }, [nodes])

  // Group nodes by semester
  const columns = useMemo(() => {
    // Build set of codes to hide when no graduation path selected
    const hiddenCodes = new Set<string>()
    if (!graduationPath) {
      for (const c of ['SE505', 'SE506', 'SE507']) hiddenCodes.add(c)
      for (const sc of SPECIALTY_COURSES) hiddenCodes.add(sc.code)
    }

    const groups: { semester: number | null; nodes: GraphNode[] }[] = []
    for (let s = 1; s <= 8; s++) {
      const columnNodes = nodes.filter(n =>
        semesterMap[n.id] === s && !hiddenCodes.has(n.code.toUpperCase())
      )
      groups.push({ semester: s, nodes: columnNodes })
    }
    const unassigned = nodes.filter(n =>
      semesterMap[n.id] === null && !hiddenCodes.has(n.code.toUpperCase())
    )
    if (unassigned.length > 0) groups.push({ semester: null, nodes: unassigned })
    return groups
  }, [nodes, semesterMap, graduationPath])

  // Total credits (only counts what's visible in semester columns, not hidden grad courses)
  const totalCredits = useMemo(() => {
    let total = 0
    for (const col of columns) {
      if (col.semester === null) continue // skip Chưa xếp
      for (const n of col.nodes) {
        total += creditMap.get(n.id) ?? getCurriculumCredits(n.code.toUpperCase()) ?? 3
      }
    }
    // ENG credits count regardless of English level
    if (englishLevel !== 'all') {
      const skipCodes = getEngSkipCodes(englishLevel)
      for (const engCode of ['ENG01', 'ENG02', 'ENG03']) {
        if (!skipCodes.has(engCode)) continue
        const engNode = nodes.find(n => n.code.toUpperCase() === engCode)
        if (engNode) {
          if (semesterMap[engNode.id] === null) {
            total += creditMap.get(engNode.id) ?? getCurriculumCredits(engCode) ?? 4
          }
        }
      }
    }
    return total
  }, [columns, nodes, semesterMap, creditMap, englishLevel])

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 8 } }),
    useSensor(KeyboardSensor),
  )

  const handleDragStart = useCallback((event: DragStartEvent) => {
    setActiveId(event.active.id as number)
    for (const [id, sem] of Object.entries(semesterMap)) {
      if (Number(id) === event.active.id) {
        setActiveColumn(sem)
        break
      }
    }
  }, [semesterMap])

  const handleDragOver = useCallback((event: DragOverEvent) => {
    const { active, over } = event
    if (!over || active.id === over.id) return

    const activeIdNum = active.id as number
    const activeSem = semesterMap[activeIdNum]

    let targetSem: number | null = null
    if (typeof over.id === 'string' && over.id.startsWith('column-')) {
      targetSem = over.id === 'column-null' ? null : Number(over.id.replace('column-', ''))
    } else {
      targetSem = semesterMap[over.id as number] ?? null
    }

    if (targetSem !== undefined && activeSem !== targetSem) {
      setSemesterMap(prev => ({ ...prev, [activeIdNum]: targetSem }))
    }
  }, [semesterMap])

  const handleDragEnd = useCallback((event: DragEndEvent) => {
    const { active, over } = event
    if (!over) { setActiveId(null); setActiveColumn(null); return }

    const activeIdNum = active.id as number
    let targetSemester: number | null = null
    if (typeof over.id === 'string' && over.id.startsWith('column-')) {
      targetSemester = over.id === 'column-null' ? null : Number(over.id.replace('column-', ''))
    } else {
      targetSemester = semesterMap[over.id as number] ?? null
    }

    if (targetSemester !== undefined) {
      setSemesterMap(prev => ({ ...prev, [activeIdNum]: targetSemester }))
    }
    setActiveId(null)
    setActiveColumn(null)
  }, [semesterMap])

  const activeNode = useMemo(
    () => (activeId ? nodes.find(n => n.id === activeId) ?? null : null),
    [nodes, activeId],
  )

  // ─── Auto-arrange algorithm (curriculum-aware) ───

  const handleAutoArrange = useCallback(() => {
    // Build code → id lookup from graph nodes
    const codeToId = new Map<string, number>()
    for (const n of nodes) codeToId.set(n.code.toUpperCase(), n.id)

    // Step 1: Build adjacency graph from force-graph links + DAA "before" relationships
    const inDegree = new Map<number, number>()
    const adjacency = new Map<number, number[]>()
    const allIds = nodes.map(n => n.id)

    for (const id of allIds) {
      inDegree.set(id, 0)
      adjacency.set(id, [])
    }

    // Force-graph prerequisite links
    for (const link of links) {
      if (link.type !== 'PREREQUISITE') continue
      const source = typeof link.source === 'number' ? link.source : (link.source as Record<string, number>).id
      const target = typeof link.target === 'number' ? link.target : (link.target as Record<string, number>).id
      if (!adjacency.has(source) || !adjacency.has(target)) continue
      adjacency.get(source)!.push(target)
      inDegree.set(target, (inDegree.get(target) ?? 0) + 1)
    }

    // DAA "before" course relationships (for courses not in the JSON curriculum)
    for (const n of nodes) {
      const beforeCodes = getCurriculumPrereqs(n.code.toUpperCase())
      if (beforeCodes.length > 0) {
        for (const bCode of beforeCodes) {
          const bId = codeToId.get(bCode.toUpperCase())
          if (bId !== undefined && adjacency.has(bId) && adjacency.has(n.id)) {
            // Add edge: beforeCourse → this course
            adjacency.get(bId)!.push(n.id)
            inDegree.set(n.id, (inDegree.get(n.id) ?? 0) + 1)
          }
        }
      }
    }

    // Step 2: Topological sort
    const queue: number[] = []
    const topoOrder: number[] = []
    const levelMap = new Map<number, number>()

    for (const [id, deg] of inDegree) {
      if (deg === 0) queue.push(id)
    }
    while (queue.length > 0) {
      const id = queue.shift()!
      topoOrder.push(id)
      const currentLevel = levelMap.get(id) ?? 0
      for (const neighbor of adjacency.get(id) ?? []) {
        if ((inDegree.get(neighbor) ?? 0) > 0) {
          const newLevel = currentLevel + 1
          levelMap.set(neighbor, Math.max(levelMap.get(neighbor) ?? 0, newLevel))
          inDegree.set(neighbor, (inDegree.get(neighbor) ?? 1) - 1)
          if ((inDegree.get(neighbor) ?? 0) === 0) queue.push(neighbor)
        }
      }
    }

    // Step 3: Assign fixed-semester courses first (from KTPM curriculum JSON)
    const assignedSemester = new Map<number, number>()
    const semesterCredits = new Map<number, number>()
    for (let s = 1; s <= 8; s++) semesterCredits.set(s, 0)

    // Graduation course codes to manage
    const gradCodeSet = new Set(['SE505', 'SE506', 'SE507'])
    const pathCodes = graduationPath ? new Set(graduationPath.courses.map(c => c.code)) : new Set<string>()

    for (const n of nodes) {
      const code = n.code.toUpperCase()
      const fixedSem = getFixedSemester(code)
      const credits = creditMap.get(n.id) ?? Math.round(n.val ?? 6)

      // Skip non-matching graduation courses (handled later)
      if (gradCodeSet.has(code)) {
        if (!graduationPath || !pathCodes.has(code)) continue
      }
      // Skip specialty courses unless chosen in combined path
      if (SPECIALTY_COURSES.some(s => s.code === code)) {
        if (graduationPath?.id !== 'combined' || code !== specialtyChoice) continue
      }

      if (fixedSem !== undefined && fixedSem >= 1 && fixedSem <= 8) {
        assignedSemester.set(n.id, fixedSem)
        semesterCredits.set(fixedSem, (semesterCredits.get(fixedSem) ?? 0) + credits)
      }
    }

    // Career codes set for early-placement preference
    const careerCodeSet = selectedElectiveCodes

    // ENG courses to skip based on English level
    const engSkipSet = getEngSkipCodes(englishLevel)

    // Step 4: Process remaining (flexible) courses in topological order
    for (const id of topoOrder) {
      if (assignedSemester.has(id)) continue // already fixed by curriculum

      const node = nodeMap.get(id)
      if (!node) continue

      // Skip ENG courses based on English level selection
      if (engSkipSet.has(node.code.toUpperCase())) continue

      const isCareerNode = careerCodeSet.has(node.code.toUpperCase())
      const isSSCourse = /^SS\d/.test(node.code.toUpperCase())
      const nodeLevel = levelMap.get(id) ?? 0
      const credits = creditMap.get(id) ?? Math.round(node.val ?? 6)

      // Determine minimum semester:
      // - SS courses (SS003-SS010): flexible, HK2+
      // - Career-recommended electives: HK4+
      // - Other flexible courses: based on topological level
      let minSem: number
      if (isSSCourse) {
        minSem = 2
      } else if (isCareerNode) {
        minSem = 4
      } else {
        minSem = Math.max(1, Math.min(nodeLevel + 1, 8))
      }

      let placed = false

      for (let s = minSem; s <= 8; s++) {
        const current = semesterCredits.get(s) ?? 0
        if (current + credits <= MAX_CREDITS_PER_SEMESTER) {
          // Check if all graph prerequisites are in earlier semesters
          const prereqs = prereqMap.get(id) ?? []
          const prereqsSatisfied = prereqs.every(pId => {
            const pSem = assignedSemester.get(pId)
            return pSem !== undefined && pSem < s
          })

          if (prereqsSatisfied) {
            assignedSemester.set(id, s)
            semesterCredits.set(s, current + credits)
            placed = true
            break
          }
        }
      }

      // Fallback: place wherever there's room (over-capacity by up to +4)
      if (!placed) {
        for (let s = minSem; s <= 8; s++) {
          const current = semesterCredits.get(s) ?? 0
          if (current + credits <= MAX_CREDITS_PER_SEMESTER + 4) {
            assignedSemester.set(id, s)
            semesterCredits.set(s, current + credits)
            placed = true
            break
          }
        }
      }

      // Last resort: first possible semester
      if (!placed) {
        for (let s = minSem; s <= 8; s++) {
          assignedSemester.set(id, s)
          semesterCredits.set(s, (semesterCredits.get(s) ?? 0) + credits)
          placed = true
          break
        }
      }

      // Ultra-last resort: push into semester 1
      if (!placed) {
        for (let s = 1; s <= 8; s++) {
          assignedSemester.set(id, s)
          semesterCredits.set(s, (semesterCredits.get(s) ?? 0) + credits)
          break
        }
      }
    }

    // Step 5: Apply to state
    const next: SemesterMap = {}

    for (const n of nodes) {
      const code = n.code.toUpperCase()

      // Skip ENG courses based on English level
      if (engSkipSet.has(code)) {
        next[n.id] = null
        continue
      }

      // Graduation courses: only kept if matching selected path
      if (gradCodeSet.has(code)) {
        if (!graduationPath || !pathCodes.has(code)) {
          next[n.id] = null
          continue
        }
      }
      // Specialty courses: only kept if chosen in combined path
      if (SPECIALTY_COURSES.some(s => s.code === code)) {
        if (graduationPath?.id !== 'combined' || code !== specialtyChoice) {
          next[n.id] = null
          continue
        }
      }

      next[n.id] = assignedSemester.get(n.id) ?? n.semester ?? null
    }

    // For combined path, ensure specialty course is placed in HK8
    if (graduationPath?.id === 'combined' && specialtyChoice) {
      const specNode = nodes.find(n => n.code.toUpperCase() === specialtyChoice)
      if (specNode) {
        next[specNode.id] = 8
      }
    }

    setSemesterMap(next)
  }, [nodes, links, creditMap, prereqMap, nodeMap, selectedElectiveCodes, englishLevel, graduationPath, specialtyChoice])

  // ─── Save / Load ───

  const handleSave = useCallback(() => {
    const hash = hashSemesterMap(semesterMap)
    localStorage.setItem('devorbit_kanban_semester_map', JSON.stringify(semesterMap))
    setSavedMapHash(hash)
    setSaveMessage('Đã lưu lộ trình')
    setTimeout(() => setSaveMessage(null), 2000)
  }, [semesterMap])

  const handleLoad = useCallback(() => {
    const saved = localStorage.getItem('devorbit_kanban_semester_map')
    if (!saved) {
      setSaveMessage('Chưa có lộ trình đã lưu')
      setTimeout(() => setSaveMessage(null), 2000)
      return
    }
    try {
      const parsed = JSON.parse(saved) as SemesterMap
      setSemesterMap(parsed)
      setSavedMapHash(hashSemesterMap(parsed))
      setSaveMessage('Đã tải lộ trình')
      setTimeout(() => setSaveMessage(null), 2000)
    } catch { /* ignore */ }
  }, [])

  const hasUnsavedChanges = savedMapHash !== null && savedMapHash !== hashSemesterMap(semesterMap)

  return (
    <div className="flex flex-col min-h-full">
      {/* ─── Elective credits banner ─── */}
      {selectedElectiveCodes.size > 0 && (
        <div className="shrink-0 px-6 pt-4 flex justify-center">
          <motion.div
            initial={{ opacity: 0, x: -10 }}
            animate={{ opacity: 1, x: 0 }}
            className="inline-flex items-center gap-3 px-5 py-2.5 rounded-2xl border border-orbit-accent/15 bg-orbit-accent/3"
          >
            <Compass className="h-4 w-4 shrink-0 text-orbit-accent" weight="duotone" />
            <span className="text-[13px] text-orbit-text-muted">Cơ sở ngành</span>
            <span className={`text-[14px] font-bold tabular-nums ${
              (() => {
                let c = 0
                for (const code of selectedElectiveCodes) if (CO_SO_NGANH_COURSES.includes(code)) c += ELECTIVE_CREDITS[code] ?? 3
                return c >= 12
              })() ? 'text-emerald-400' : 'text-amber-400'
            }`}>
              {(() => {
                let c = 0
                for (const code of selectedElectiveCodes) if (CO_SO_NGANH_COURSES.includes(code)) c += ELECTIVE_CREDITS[code] ?? 3
                return c
              })()} / 12 TC
            </span>
            <span className="text-[13px] text-orbit-text-muted">·</span>
            <span className="text-[13px] text-orbit-text-muted">Chuyên ngành</span>
            <span className={`text-[14px] font-bold tabular-nums ${
              (() => {
                let c = 0
                for (const code of selectedElectiveCodes) if (!CO_SO_NGANH_COURSES.includes(code)) c += ELECTIVE_CREDITS[code] ?? 3
                return c >= 16
              })() ? 'text-emerald-400' : 'text-amber-400'
            }`}>
              {(() => {
                let c = 0
                for (const code of selectedElectiveCodes) if (!CO_SO_NGANH_COURSES.includes(code)) c += ELECTIVE_CREDITS[code] ?? 3
                return c
              })()} / 16 TC
            </span>
          </motion.div>
        </div>
      )}

      {/* ─── AV + TN selectors ─── */}
      <div className="shrink-0 px-6 pt-5 pb-3 flex items-end justify-center gap-8">
        {/* English level selector */}
        <div className="relative">
          <label className="text-[11px] font-semibold text-orbit-text-muted mb-2 block text-center">
            Chọn trình độ anh văn đầu vào
          </label>
          <button
            onClick={() => setShowEnglishMenu(!showEnglishMenu)}
            className="flex items-center gap-3 px-5 py-3 min-w-[260px] rounded-2xl border border-orbit-border/30 bg-orbit-surface/60 text-[14px] font-medium text-orbit-text hover:border-orbit-accent/40 hover:bg-orbit-accent/[0.02] transition-all"
          >
            <span className="flex-1 text-left">
              {ENG_LEVEL_LABELS[englishLevel]}
            </span>
            <CaretDown className="h-4 w-4 text-orbit-text-muted shrink-0" weight="bold" />
          </button>
          {showEnglishMenu && (
            <>
              <div className="fixed inset-0 z-10" onClick={() => setShowEnglishMenu(false)} />
              <motion.div
                initial={{ opacity: 0, y: -4, scale: 0.96 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                className="absolute left-0 top-full mt-2 w-full min-w-[220px] rounded-2xl border border-orbit-border/20 bg-orbit-surface shadow-diffusion z-20 overflow-hidden"
              >
                {(['all', 'eng2', 'eng3', 'passed'] as EnglishLevel[]).map((level) => (
                  <button
                    key={level}
                    onClick={() => { setEnglishLevel(level); setShowEnglishMenu(false) }}
                    className={`w-full flex items-center gap-3 px-4 py-3 text-left text-[14px] font-medium transition-all hover:bg-orbit-accent/5 ${
                      englishLevel === level
                        ? 'text-orbit-accent bg-orbit-accent/5'
                        : 'text-orbit-text'
                    }`}
                  >
                    {englishLevel === level && (
                      <CheckCircle className="h-4 w-4 shrink-0 text-orbit-accent" weight="fill" />
                    )}
                    <span className={englishLevel !== level ? 'ml-[28px]' : ''}>
                      {ENG_LEVEL_LABELS[level]}
                    </span>
                  </button>
                ))}
              </motion.div>
            </>
          )}
        </div>

        {/* Graduation path selector */}
        <div className="relative">
          <label className="text-[11px] font-semibold text-orbit-text-muted mb-2 block text-center">
            Chọn hình thức tốt nghiệp
          </label>
          <button
            onClick={() => setShowGradMenu(!showGradMenu)}
            className="flex items-center gap-3 px-5 py-3 min-w-[260px] rounded-2xl border border-orbit-border/30 bg-orbit-surface/60 text-[14px] font-medium text-orbit-text hover:border-orbit-accent/40 hover:bg-orbit-accent/[0.02] transition-all"
          >
            <span className="flex-1 text-left">
              {graduationPath ? graduationPath.name : 'Chưa chọn'}
            </span>
            <CaretDown className="h-4 w-4 text-orbit-text-muted shrink-0" weight="bold" />
          </button>
          {showGradMenu && (
            <>
              <div className="fixed inset-0 z-10" onClick={() => setShowGradMenu(false)} />
              <motion.div
                initial={{ opacity: 0, y: -4, scale: 0.96 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                className="absolute left-0 top-full mt-2 w-full min-w-[300px] rounded-2xl border border-orbit-border/20 bg-orbit-surface shadow-diffusion z-20 overflow-hidden"
              >
                <p className="text-[11px] font-black uppercase tracking-[0.15em] text-orbit-text-muted px-4 pt-3 pb-1">
                  Hình thức tốt nghiệp
                </p>
                {GRADUATION_PATHS.map((path) => {
                  const isActive = graduationPath?.id === path.id
                  return (
                    <button
                      key={path.id}
                      onClick={() => {
                        setGraduationPath(isActive ? null : path)
                        if (!isActive) setSpecialtyChoice(null)
                      }}
                      className={`w-full flex items-start gap-3 px-4 py-3 text-left transition-all hover:bg-orbit-accent/5 ${
                        isActive ? 'bg-orbit-accent/5' : ''
                      }`}
                    >
                      <div className="mt-0.5">
                        {isActive ? (
                          <CheckCircle className="h-4 w-4 text-orbit-accent" weight="fill" />
                        ) : (
                          <div className="h-4 w-4 rounded-full border border-orbit-border/40" />
                        )}
                      </div>
                      <div className="min-w-0 flex-1">
                        <div className={`text-[14px] font-bold ${isActive ? 'text-orbit-accent' : 'text-orbit-text'}`}>
                          {path.name}
                        </div>
                        <div className="text-[12px] text-orbit-text-muted mt-0.5 leading-relaxed">
                          {path.description}
                        </div>
                      </div>
                    </button>
                  )
                })}

                {/* Specialty sub-selector for combined path */}
                {graduationPath?.id === 'combined' && (
                  <div className="border-t border-orbit-border/10 mt-1 pt-2 pb-3 px-4">
                    <p className="text-[11px] font-bold uppercase tracking-[0.1em] text-orbit-text-muted mb-2">
                      Chọn môn chuyên đề (4TC)
                    </p>
                    <div className="grid grid-cols-1 gap-1">
                      {SPECIALTY_COURSES.map((spec) => {
                        const isChosen = specialtyChoice === spec.code
                        return (
                          <button
                            key={spec.code}
                            onClick={() => setSpecialtyChoice(isChosen ? null : spec.code)}
                            className={`flex items-center gap-2 px-3 py-2 rounded-xl text-left text-[13px] font-medium transition-all ${
                              isChosen
                                ? 'bg-orbit-accent/10 text-orbit-accent'
                                : 'text-orbit-text-muted hover:text-orbit-text hover:bg-orbit-surface'
                            }`}
                          >
                            {isChosen ? (
                              <CheckCircle className="h-3.5 w-3.5 shrink-0 text-orbit-accent" weight="fill" />
                            ) : (
                              <div className="h-3.5 w-3.5 shrink-0 rounded-full border border-orbit-border/30" />
                            )}
                            <span className="truncate">{spec.name}</span>
                            <span className="text-[11px] text-orbit-text-muted shrink-0 ml-auto">
                              {spec.credits}TC
                            </span>
                          </button>
                        )
                      })}
                    </div>
                  </div>
                )}
              </motion.div>
            </>
          )}
        </div>

      </div>

      {/* ─── Usage guide ─── */}
      {showGuide && (
        <div className="shrink-0 px-6 pb-1 flex justify-center">
          <motion.div
            initial={{ opacity: 0, y: -6 }}
            animate={{ opacity: 1, y: 0 }}
            className="relative flex flex-col items-center gap-1.5 px-5 py-3 rounded-2xl bg-orbit-accent/[0.03] border border-orbit-accent/10"
          >
            <div className="flex items-center gap-3 text-[12px] text-orbit-text-secondary leading-relaxed">
              <span className="flex items-center gap-1.5">
                <span className="h-2 w-2 rounded-full bg-orbit-accent/60" />
                Kéo thả môn qua học kỳ
              </span>
              <span className="text-orbit-border/30">|</span>
              <span className="flex items-center gap-1.5">
                <Compass className="h-3.5 w-3.5 text-orbit-accent/60" weight="regular" />
                Chọn môn tự chọn theo ý thích
              </span>
              <span className="text-orbit-border/30">|</span>
              <span className="flex items-center gap-1.5">
                <GraduationCap className="h-3.5 w-3.5 text-orbit-accent/60" weight="regular" />
                Chọn hình thức tốt nghiệp
              </span>
              <span className="text-orbit-border/30">|</span>
              <span className="flex items-center gap-1.5">
                <Sparkle className="h-3.5 w-3.5 text-orbit-accent/60" weight="fill" />
                Tự động sắp xếp lộ trình
              </span>
            </div>
            <p className="text-[10px] text-orbit-text-muted italic tracking-wide">
              Hãy cùng nhau sắp xếp lộ&nbsp;trình học&nbsp;tập 4 năm tại UIT nhé!
            </p>
            <button
              onClick={() => setShowGuide(false)}
              className="absolute top-2 right-2 h-5 w-5 rounded-lg flex items-center justify-center text-orbit-text-muted hover:text-orbit-text hover:bg-orbit-elevated/50 transition-all"
            >
              <X className="h-3 w-3" weight="bold" />
            </button>
          </motion.div>
        </div>
      )}

      {/* ─── Total credits bar ─── */}
      <div className="shrink-0 px-6 pb-4 flex items-center gap-4">
        <div className="flex items-center gap-3 py-2 px-4 rounded-2xl bg-orbit-surface/60 border border-orbit-border/10">
          <span className="text-[12px] font-bold uppercase tracking-[0.15em] text-orbit-text-muted">
            Tổng TC đã xếp
          </span>
          <span className="text-[25px] font-black text-orbit-text leading-none">
            {totalCredits}
          </span>
          <span className="text-[12px] text-orbit-text-muted">TC</span>
        </div>
        {englishLevel !== 'all' && (
          <div className="flex items-center gap-2 py-2 px-3 rounded-2xl bg-amber-400/5 border border-amber-400/10">
            <span className="text-[12px] font-medium text-amber-400/80">
              (+{(() => {
                const skipCodes = getEngSkipCodes(englishLevel)
                let engTotal = 0
                for (const code of skipCodes) {
                  const node = nodes.find(n => n.code.toUpperCase() === code)
                  engTotal += creditMap.get(node?.id ?? -1) ?? getCurriculumCredits(code) ?? 4
                }
                return engTotal
              })()} TC Anh văn đã tính)
            </span>
          </div>
        )}
      </div>

      {/* ─── Kanban scrollable area ─── */}
      <div className="flex-1 overflow-x-auto px-6 py-3 pb-24">
        <DndContext
          sensors={sensors}
          collisionDetection={closestCorners}
          onDragStart={handleDragStart}
          onDragOver={handleDragOver}
          onDragEnd={handleDragEnd}
        >
          <div className="flex gap-5 h-full min-w-max pb-4">
            {columns.map((col) => (
              <KanbanColumn
                key={col.semester === null ? 'unassigned' : `sem-${col.semester}`}
                semester={col.semester}
                label={
                  col.semester !== null
                    ? SEMESTER_LABELS[col.semester] ?? `Kỳ ${col.semester}`
                    : 'Chưa xếp'
                }
                color={col.semester !== null ? SEMESTER_COLORS[col.semester] : '#71717a'}
                nodes={col.nodes}
                prereqMap={prereqMap}
                recommendedIds={recommendedIds}
                semesterMap={semesterMap}
                nodeMap={nodeMap}
                creditMap={creditMap}
                onCardClick={(id) => navigate(`/courses/${id}`)}
              />
            ))}
          </div>

          <DragOverlay>
            {activeNode && (
              <div className="opacity-90 rotate-[2deg]">
                <KanbanCard
                  node={activeNode}
                  isRecommended={recommendedIds.has(activeNode.id)}
                  prerequisites={prereqMap.get(activeNode.id) ?? []}
                  semesterMap={semesterMap}
                  color={activeColumn !== null ? SEMESTER_COLORS[activeColumn] ?? '#71717a' : '#71717a'}
                  nodeMap={nodeMap}
                  creditMap={creditMap}
                  maxSemesterCredits={MAX_CREDITS_PER_SEMESTER}
                />
              </div>
            )}
          </DragOverlay>
        </DndContext>
      </div>

      {/* ─── Bottom bar: Save / Restore / Auto-arrange ─── */}
      <div className="fixed bottom-0 left-0 right-0 z-20 px-6 py-4 flex items-center justify-center gap-4 border-t border-orbit-border/10 bg-orbit-bg/90 backdrop-blur-xl">
        <button
          onClick={handleSave}
          className={`flex items-center gap-3 px-6 py-3 rounded-2xl border text-[13px] font-bold uppercase tracking-[0.12em] transition-all shadow-sm ${
            hasUnsavedChanges
              ? 'border-orbit-accent/50 text-orbit-accent bg-orbit-accent/10 hover:bg-orbit-accent/20 shadow-orbit-accent/5'
              : 'border-orbit-border/40 text-orbit-text-secondary bg-orbit-elevated/60 hover:text-orbit-text hover:border-orbit-border/70 hover:bg-orbit-elevated'
          }`}
        >
          <FloppyDisk className="h-5 w-5" weight="regular" />
          Lưu lộ trình
        </button>
        <button
          onClick={handleLoad}
          className="flex items-center gap-3 px-6 py-3 rounded-2xl border border-orbit-border/40 text-[13px] font-bold uppercase tracking-[0.12em] text-orbit-text-secondary bg-orbit-elevated/60 hover:text-orbit-text hover:border-orbit-border/70 hover:bg-orbit-elevated transition-all shadow-sm"
        >
          <ClockCounterClockwise className="h-5 w-5" weight="regular" />
          Khôi phục
        </button>
        <button
          onClick={handleAutoArrange}
          className="flex items-center gap-3 px-6 py-3 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/30 text-[13px] font-bold uppercase tracking-[0.12em] text-orbit-accent hover:bg-orbit-accent/20 transition-all shadow-sm"
        >
          <Sparkle className="h-5 w-5" weight="fill" />
          Tự động sắp xếp
        </button>

        {saveMessage && (
          <motion.span
            initial={{ opacity: 0, x: 10 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: 10 }}
            className="text-[13px] font-medium text-orbit-accent"
          >
            {saveMessage}
          </motion.span>
        )}
      </div>
    </div>
  )
}

function hashSemesterMap(map: SemesterMap): string {
  const keys = Object.keys(map).sort()
  let h = 0
  for (const k of keys) {
    h = ((h << 5) - h + Number(k) * 31 + (map[Number(k)] ?? -1)) | 0
  }
  return h.toString(36)
}

