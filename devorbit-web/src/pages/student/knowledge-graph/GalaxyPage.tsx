import { useState, useMemo } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useKnowledgeGraph } from '../../../hooks/useKnowledgeGraph'
import { useCourseList } from '../../../hooks/useCourseList'
import type { GraphNode } from '../../../types/api'
import { Compass, WarningCircle } from '@phosphor-icons/react'
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
} from '../../../components/student/CareerOrientationData'
import {
  getFixedSemesterCodes,
  getCurriculumCredits,
  getCurriculumPrereqs,
} from '../../../components/student/curriculumData'

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

// ─── Main ───

export default function GalaxyPage() {
  const { data, isLoading, error } = useKnowledgeGraph()

  // Also fetch full course list to include elective courses not in the graph
  const { data: allCourses } = useCourseList()

  // Career orientation
  const [selectedElectiveCodes, setSelectedElectiveCodes] = useState<Set<string>>(new Set())

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

  if (isLoading) {
    return (
      <div className="h-[calc(100vh-72px)] flex items-center justify-center">
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
    <div className="relative w-full min-h-screen flex flex-col">
      {/* ─── PAGE HEADING ─── */}
      <div className="w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12 pt-16 md:pt-24 pb-2">
        <motion.span
          initial={{ opacity: 0, y: 6 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="section-label mb-8 inline-flex"
        >
          <Compass className="h-3 w-3" weight="fill" />
          Lộ&nbsp;trình học&nbsp;tập trực&nbsp;tuyến
        </motion.span>
        <div className="flex items-end justify-between gap-4">
          <motion.h1
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, delay: 0.05 }}
            className="display-lg leading-[1.05] mt-3"
          >
            <span className="text-orbit-accent">Lộ&nbsp;trình</span>{' '}
            <span className="text-orbit-text">học&nbsp;tập</span>
          </motion.h1>
        </div>
        <motion.p
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4, delay: 0.1 }}
          className="body-lg text-[16px] md:text-[17px] leading-relaxed max-w-2xl mt-4 text-orbit-text-secondary"
        >
          Lên kế hoạch học&nbsp;tập 4 năm tại UIT. Sắp xếp môn&nbsp;học theo từng học&nbsp;kỳ, chọn môn tự&nbsp;chọn và theo dõi lộ&nbsp;trình tốt&nbsp;nghiệp.
        </motion.p>
      </div>

      {/* ─── CONTENT ─── */}
      <div className="w-full max-w-[1440px] mx-auto px-6 md:px-10 lg:px-12 flex-1">
        <KanbanBoard
          nodes={kanbanNodes}
          links={graphData.links}
          selectedElectiveCodes={selectedElectiveCodes}
          creditMap={creditMap}
        />

        {/* ─── Elective picker — inline cards ─── */}
        <div className="px-6 py-8 space-y-6 pb-24">
          <div className="flex items-center justify-between">
            <h3 className="text-[17px] font-bold text-orbit-text flex items-center gap-3">
              <Compass className="h-5 w-5 text-orbit-accent" weight="duotone" />
              Môn tự chọn
            </h3>
            {selectedElectiveCodes.size > 0 && (
              <button
                onClick={() => setSelectedElectiveCodes(new Set())}
                className="text-[14px] font-semibold text-rose-400 hover:text-rose-300 transition-all"
              >
                Xoá tất cả
              </button>
            )}
          </div>

          <div className="grid gap-4">
            {ELECTIVE_SECTION_GROUPS.map(group => (
              <div
                key={group.title}
                className="rounded-2xl border border-orbit-border/30 bg-orbit-surface/40 p-5"
              >
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-3">
                    <span className="text-[15px] font-bold uppercase tracking-[0.1em] text-orbit-text">
                      {group.title}
                    </span>
                    {group.minTC > 0 && (
                      <span className={`text-[14px] font-bold tabular-nums ${
                        (group.title === 'Cơ sở ngành'
                          ? getCsnCredits(selectedElectiveCodes)
                          : getCnCredits(selectedElectiveCodes)
                        ) >= group.minTC
                          ? 'text-emerald-400'
                          : 'text-amber-400'
                      }`}>
                        {(() => {
                          const current = group.title === 'Cơ sở ngành'
                            ? getCsnCredits(selectedElectiveCodes)
                            : getCnCredits(selectedElectiveCodes)
                          return `${current} / ${group.minTC} TC`
                        })()}
                      </span>
                    )}
                  </div>
                  <button
                    onClick={() => {
                      const next = new Set(selectedElectiveCodes)
                      for (const c of group.allCodes) next.add(c)
                      setSelectedElectiveCodes(next)
                    }}
                    className="text-[13px] font-bold text-orbit-accent hover:text-orbit-accent/80 transition-all"
                  >
                    Chọn tất cả
                  </button>
                </div>

                <div className="flex flex-wrap gap-3">
                  {group.subgroups.map(sg => (
                    sg.codes.map(code => {
                      const checked = selectedElectiveCodes.has(code)
                      const credits = ELECTIVE_CREDITS[code] ?? 3
                      const name = COURSE_NAMES[code]
                      return (
                        <button
                          key={code}
                          onClick={() => handleToggleElective(code)}
                          className={`flex flex-col items-start gap-0.5 px-4 py-2.5 rounded-xl border text-left transition-all ${
                            checked
                              ? 'border-orbit-accent/50 bg-orbit-accent/10'
                              : 'border-orbit-border/20 bg-orbit-elevated/40 hover:border-orbit-accent/30 hover:bg-orbit-elevated/60'
                          }`}
                        >
                          <div className="flex items-center gap-2.5">
                            <span className={`text-[15px] font-bold ${
                              checked ? 'text-orbit-accent' : 'text-orbit-text'
                            }`}>
                              {code}
                            </span>
                            <span className={`text-[13px] ${
                              checked ? 'text-orbit-accent' : 'text-orbit-text-secondary'
                            }`}>
                              {credits}TC
                            </span>
                          </div>
                          {name && (
                            <span className={`text-[13px] leading-tight ${
                              checked ? 'text-orbit-accent/80' : 'text-orbit-text-secondary'
                            }`}>
                              {name}
                            </span>
                          )}
                        </button>
                      )
                    })
                  ))}
                </div>
              </div>
            ))}
          </div>

          {/* Credit total */}
          <div className="flex items-center justify-center gap-6 py-3 px-5 rounded-2xl border border-orbit-border/20 bg-orbit-elevated/30">
            <div className="flex items-center gap-2">
              <span className="text-[14px] text-orbit-text-muted">Cơ sở ngành</span>
              <span className={`text-[16px] font-black tabular-nums ${
                getCsnCredits(selectedElectiveCodes) >= CO_SO_NGANH_MIN_TC
                  ? 'text-emerald-400'
                  : 'text-amber-400'
              }`}>
                {getCsnCredits(selectedElectiveCodes)} / {CO_SO_NGANH_MIN_TC} TC
              </span>
            </div>
            <span className="text-orbit-border/30">|</span>
            <div className="flex items-center gap-2">
              <span className="text-[14px] text-orbit-text-muted">Chuyên ngành</span>
              <span className={`text-[16px] font-black tabular-nums ${
                getCnCredits(selectedElectiveCodes) >= CHUYEN_NGANH_MIN_TC
                  ? 'text-emerald-400'
                  : 'text-amber-400'
              }`}>
                {getCnCredits(selectedElectiveCodes)} / {CHUYEN_NGANH_MIN_TC} TC
              </span>
            </div>
            <span className="text-orbit-border/30">|</span>
            <div className="flex items-center gap-2">
              <span className="text-[14px] font-bold text-orbit-text">Tổng</span>
              <span className="text-[18px] font-black tabular-nums text-orbit-accent">
                {getSelectedCredits(selectedElectiveCodes)} TC
              </span>
            </div>
          </div>
        </div>
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
