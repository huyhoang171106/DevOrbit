import { useEffect, useMemo, useState } from 'react'
import { Calculator, Copy, Plus, RotateCcw, Trash2 } from 'lucide-react'
import { apiGet } from '../../lib/api'
import type { CourseSummary } from '../../types/api'

type CourseInput = {
  id: number
  name: string
  credits: string
  grade10: string
}

type CourseResult = {
  credits: number
  grade10: number
  name: string
}

type SemesterMap = Record<number, number | null>
type CalculationMode = 'semester' | 'cumulative' | 'goal'
type GoalStatus = 'needs-input' | 'no-term-credits' | 'not-feasible' | 'already-above-target' | 'difficult' | 'feasible'
type CourseValidation = {
  creditsReason: string | null
  gradeReason: string | null
  valid: boolean
}

const initialCourses: CourseInput[] = [
  { id: 1, name: '', credits: '3', grade10: '' },
  { id: 2, name: '', credits: '3', grade10: '' },
]

const semesters = [1, 2, 3, 4, 5, 6, 7, 8]
const savedRoadmapKey = 'devorbit_kanban_semester_map'

function validateCourse(course: CourseInput): CourseValidation {
  const creditsText = course.credits.trim()
  const gradeText = course.grade10.trim()
  const credits = Number(creditsText)
  const grade10 = Number(gradeText)
  const creditsReason = creditsText === ''
    ? 'Nhập tín chỉ'
    : !Number.isFinite(credits) || credits <= 0
      ? 'Tín chỉ phải lớn hơn 0'
      : null
  const gradeReason = gradeText === ''
    ? 'Nhập điểm'
    : !Number.isFinite(grade10) || grade10 < 0 || grade10 > 10
      ? 'Điểm phải từ 0 đến 10'
      : null

  return {
    creditsReason,
    gradeReason,
    valid: creditsReason === null && gradeReason === null,
  }
}

function parseCourse(course: CourseInput): CourseResult | null {
  if (!validateCourse(course).valid) return null

  const credits = Number(course.credits)
  const grade10 = Number(course.grade10)

  if (!Number.isFinite(credits) || !Number.isFinite(grade10)) return null
  if (credits <= 0 || grade10 < 0 || grade10 > 10) return null

  return {
    credits,
    grade10,
    name: course.name.trim() || 'Môn chưa đặt tên',
  }
}

function classifyGrade10(average10: number): string {
  if (average10 >= 9) return 'Xuất sắc'
  if (average10 >= 8) return 'Giỏi'
  if (average10 >= 7) return 'Khá'
  if (average10 >= 5) return 'Trung bình'
  return 'Cần cải thiện'
}

function formatNumber(value: number): string {
  return value.toFixed(2)
}

function clampGrade10(value: number): number {
  return Math.min(10, Math.max(0, value))
}

function goalStatusLabel(status: GoalStatus): string {
  if (status === 'not-feasible') return 'Mục tiêu không khả thi trong kỳ này'
  if (status === 'already-above-target') return 'Đã vượt mục tiêu'
  if (status === 'difficult') return 'Mục tiêu khó nhưng còn khả thi'
  if (status === 'feasible') return 'Mục tiêu khả thi'
  if (status === 'no-term-credits') return 'Chưa có tín chỉ kỳ này'
  return 'Cần thêm dữ liệu'
}

function goalGuidance(status: GoalStatus): string {
  if (status === 'not-feasible') return 'Kỳ này cần cao hơn 10 điểm trung bình, nên mục tiêu này chưa khả thi với số tín chỉ hiện tại.'
  if (status === 'already-above-target') return 'GPA hiện tại đã đủ cao, kỳ này không cần áp lực điểm thêm để giữ mục tiêu đã nhập.'
  if (status === 'difficult') return 'Mức điểm cần đạt khá cao nhưng vẫn nằm trong thang 10.'
  if (status === 'feasible') return 'Mức điểm cần đạt nằm trong thang 10 và có thể dùng để lập kế hoạch học tập.'
  if (status === 'no-term-credits') return 'Thêm môn hoặc nhập tín chỉ kỳ này hợp lệ để tính ngược GPA mục tiêu.'
  return 'Nhập GPA hiện tại, tín chỉ đã tích lũy và GPA mục tiêu trong khoảng 0 đến 10.'
}

function courseToInput(course: CourseSummary, index: number): CourseInput {
  return {
    id: course.id || index + 1,
    name: `${course.code} - ${course.name}`,
    credits: String(course.credits ?? 3),
    grade10: '',
  }
}

function readSavedSemesterMap(): SemesterMap | null {
  try {
    const saved = localStorage.getItem(savedRoadmapKey)
    if (!saved) return null
    return JSON.parse(saved) as SemesterMap
  } catch {
    return null
  }
}

function getSemesterCourses(catalogue: CourseSummary[], semester: number, semesterMap: SemesterMap | null): CourseSummary[] {
  return catalogue
    .filter((course) => {
      const roadmapSemester = semesterMap?.[course.id]
      const effectiveSemester = roadmapSemester === undefined ? course.semester : roadmapSemester
      return effectiveSemester === semester && Number(course.credits ?? 0) > 0
    })
    .sort((a, b) => a.code.localeCompare(b.code))
}

export function GpaCalculatorPage() {
  const [courses, setCourses] = useState<CourseInput[]>(initialCourses)
  const [catalogue, setCatalogue] = useState<CourseSummary[]>([])
  const [savedSemesterMap, setSavedSemesterMap] = useState<SemesterMap | null>(() => readSavedSemesterMap())
  const [selectedSemester, setSelectedSemester] = useState('1')
  const [calculationMode, setCalculationMode] = useState<CalculationMode>('semester')
  const [currentGpa, setCurrentGpa] = useState('')
  const [completedCredits, setCompletedCredits] = useState('')
  const [targetGpa, setTargetGpa] = useState('')
  const [presetStatus, setPresetStatus] = useState('Đang tải danh sách môn học...')

  useEffect(() => {
    let active = true

    apiGet<CourseSummary[]>('/api/courses')
      .then((data) => {
        if (!active) return
        setCatalogue(data)
        setPresetStatus(data.length > 0 ? 'Chọn học kỳ để tự điền môn học từ dữ liệu DevOrbit.' : 'Chưa có dữ liệu môn học để tạo preset.')
      })
      .catch(() => {
        if (!active) return
        setPresetStatus('Không tải được preset. Bạn vẫn có thể nhập môn thủ công.')
      })

    return () => {
      active = false
    }
  }, [])

  const semesterCourses = useMemo(() => {
    return getSemesterCourses(catalogue, Number(selectedSemester), savedSemesterMap)
  }, [catalogue, savedSemesterMap, selectedSemester])

  const summary = useMemo(() => {
    const parsedCourses = courses.map((course) => ({
      course: parseCourse(course),
      validation: validateCourse(course),
    }))
    const validCourses = parsedCourses.map((item) => item.course).filter((course): course is CourseResult => course !== null)
    const ignoredRows = parsedCourses.filter((item) => !item.validation.valid).length
    const totalCredits = validCourses.reduce((sum, course) => sum + course.credits, 0)

    if (totalCredits === 0) {
      return {
        totalCredits: 0,
        average10: 0,
        classification: 'Nhập tín chỉ và điểm hợp lệ',
        ignoredRows,
      }
    }

    const weighted10 = validCourses.reduce((sum, course) => sum + course.grade10 * course.credits, 0)
    const average10 = weighted10 / totalCredits

    return {
      totalCredits,
      average10,
      classification: classifyGrade10(average10),
      ignoredRows,
    }
  }, [courses])

  const cumulativeSummary = useMemo(() => {
    const existingGpa = Number(currentGpa)
    const existingCredits = Number(completedCredits)
    const termCredits = summary.totalCredits
    const totalCreditsAfterTerm = Number.isFinite(existingCredits) && existingCredits >= 0
      ? existingCredits + termCredits
      : termCredits

    if (
      !Number.isFinite(existingGpa)
      || !Number.isFinite(existingCredits)
      || existingGpa < 0
      || existingGpa > 10
      || existingCredits < 0
      || termCredits <= 0
      || totalCreditsAfterTerm <= 0
    ) {
      return {
        projectedGpa: 0,
        totalCreditsAfterTerm,
        valid: false,
      }
    }

    return {
      projectedGpa: ((existingGpa * existingCredits) + (summary.average10 * termCredits)) / totalCreditsAfterTerm,
      totalCreditsAfterTerm,
      valid: true,
    }
  }, [completedCredits, currentGpa, summary.average10, summary.totalCredits])

  const goalSummary = useMemo(() => {
    const existingGpa = Number(currentGpa)
    const existingCredits = Number(completedCredits)
    const desiredGpa = Number(targetGpa)
    const validCourses = courses.map(parseCourse).filter((course): course is CourseResult => course !== null)
    const termCredits = validCourses.reduce((sum, course) => sum + course.credits, 0)
    const totalCreditsAfterTerm = Number.isFinite(existingCredits) && existingCredits >= 0
      ? existingCredits + termCredits
      : termCredits

    if (termCredits <= 0) {
      return {
        status: 'no-term-credits' as GoalStatus,
        requiredTermGpa: 0,
        totalCreditsAfterTerm,
        courseTargets: [],
        valid: false,
      }
    }

    if (
      !Number.isFinite(existingGpa)
      || !Number.isFinite(existingCredits)
      || !Number.isFinite(desiredGpa)
      || existingGpa < 0
      || existingGpa > 10
      || existingCredits < 0
      || desiredGpa < 0
      || desiredGpa > 10
      || totalCreditsAfterTerm <= 0
    ) {
      return {
        status: 'needs-input' as GoalStatus,
        requiredTermGpa: 0,
        totalCreditsAfterTerm,
        courseTargets: [],
        valid: false,
      }
    }

    const requiredTermGpa = ((desiredGpa * totalCreditsAfterTerm) - (existingGpa * existingCredits)) / termCredits
    const status: GoalStatus = requiredTermGpa > 10
      ? 'not-feasible'
      : requiredTermGpa < 0
        ? 'already-above-target'
        : requiredTermGpa >= 8.5
          ? 'difficult'
          : 'feasible'

    return {
      status,
      requiredTermGpa,
      totalCreditsAfterTerm,
      courseTargets: validCourses.map((course) => ({
        name: course.name,
        credits: course.credits,
        targetGrade: clampGrade10(requiredTermGpa),
      })),
      valid: true,
    }
  }, [completedCredits, courses, currentGpa, targetGpa])

  const updateCourse = (id: number, field: keyof Omit<CourseInput, 'id'>, value: string) => {
    setCourses((current) =>
      current.map((course) =>
        course.id === id ? { ...course, [field]: value } : course,
      ),
    )
  }

  const addCourse = () => {
    setCourses((current) => [
      ...current,
      { id: Math.max(...current.map((course) => course.id)) + 1, name: '', credits: '3', grade10: '' },
    ])
  }

  const addFiveCourses = () => {
    setCourses((current) => {
      const startId = Math.max(...current.map((course) => course.id)) + 1
      return [
        ...current,
        ...Array.from({ length: 5 }, (_, index) => ({
          id: startId + index,
          name: '',
          credits: '3',
          grade10: '',
        })),
      ]
    })
  }

  const clearCourses = () => {
    setCourses([{ id: 1, name: '', credits: '', grade10: '' }])
  }

  const resetCourses = () => {
    setCourses(initialCourses)
  }

  const duplicateCourse = (id: number) => {
    setCourses((current) => {
      const index = current.findIndex((course) => course.id === id)
      if (index === -1) return current
      const withDuplicate = [
        ...current.slice(0, index + 1),
        { ...current[index] },
        ...current.slice(index + 1),
      ]
      return withDuplicate.map((course, courseIndex) => ({ ...course, id: courseIndex + 1 }))
    })
  }

  const applySemesterPreset = () => {
    const latestSemesterMap = readSavedSemesterMap()
    const latestSemesterCourses = getSemesterCourses(catalogue, Number(selectedSemester), latestSemesterMap)
    setSavedSemesterMap(latestSemesterMap)
    if (latestSemesterCourses.length === 0) return
    setCourses(latestSemesterCourses.map(courseToInput))
  }

  const removeCourse = (id: number) => {
    setCourses((current) => current.length === 1 ? current : current.filter((course) => course.id !== id))
  }

  return (
    <section className="min-h-[calc(100vh-72px)] bg-orbit-bg text-orbit-text">
      <div className="mx-auto grid w-full max-w-[1440px] gap-8 px-6 py-10 md:grid-cols-[minmax(0,1fr)_360px] md:px-10 lg:px-14">
        <div>
          <div className="mb-8 flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-[8px] border border-orbit-accent/25 bg-orbit-accent/10 text-orbit-accent">
              <Calculator className="h-5 w-5" aria-hidden="true" />
            </div>
            <div>
              <p className="text-[11px] font-black uppercase tracking-[0.18em] text-orbit-accent">Công cụ học tập UIT</p>
              <h1 className="font-heading text-[44px] font-black leading-tight tracking-normal text-orbit-text">
                Tính GPA
              </h1>
            </div>
          </div>

          <p className="mb-8 max-w-3xl text-[16px] leading-7 text-orbit-text-secondary">
            Nhập tín chỉ và điểm hệ 10 của từng môn. Công cụ chỉ tính GPA hệ 10 theo trọng số tín chỉ, phù hợp để sinh viên ước lượng nhanh kết quả học kỳ.
          </p>

          <div className="mb-6 flex flex-wrap gap-3">
            <div className="inline-flex rounded-[8px] border border-orbit-border bg-orbit-surface p-1 shadow-diffusion">
              <button
                type="button"
                onClick={() => setCalculationMode('semester')}
                className={`h-10 rounded-[6px] px-4 text-[13px] font-bold transition-colors ${
                  calculationMode === 'semester'
                    ? 'bg-orbit-accent text-zinc-950'
                    : 'text-orbit-text-secondary hover:text-orbit-text'
                }`}
              >
                Tính GPA học kỳ
              </button>
              <button
                type="button"
                onClick={() => setCalculationMode('cumulative')}
                className={`h-10 rounded-[6px] px-4 text-[13px] font-bold transition-colors ${
                  calculationMode === 'cumulative'
                    ? 'bg-orbit-accent text-zinc-950'
                    : 'text-orbit-text-secondary hover:text-orbit-text'
                }`}
              >
                Ước lượng GPA tích lũy
              </button>
              <button
                type="button"
                onClick={() => setCalculationMode('goal')}
                className={`h-10 rounded-[6px] px-4 text-[13px] font-bold transition-colors ${
                  calculationMode === 'goal'
                    ? 'bg-orbit-accent text-zinc-950'
                    : 'text-orbit-text-secondary hover:text-orbit-text'
                }`}
              >
                Mục tiêu GPA
              </button>
            </div>
          </div>

          {(calculationMode === 'cumulative' || calculationMode === 'goal') && (
            <div className="mb-6 rounded-[8px] border border-orbit-border bg-orbit-surface p-4 shadow-diffusion">
              <div className={`grid gap-4 ${calculationMode === 'goal' ? 'md:grid-cols-3' : 'md:grid-cols-2'}`}>
                <div>
                  <label className="mb-2 block text-[12px] font-bold uppercase tracking-[0.12em] text-orbit-text-muted" htmlFor="current-gpa">
                    GPA hiện tại
                  </label>
                  <input
                    id="current-gpa"
                    type="number"
                    min="0"
                    max="10"
                    step="0.01"
                    value={currentGpa}
                    onChange={(event) => setCurrentGpa(event.target.value)}
                    placeholder="7.50"
                    className="h-11 w-full rounded-[8px] border border-orbit-border bg-orbit-bg px-3 text-[14px] text-orbit-text outline-none transition-colors placeholder:text-orbit-text-muted focus:border-orbit-accent/60"
                  />
                </div>
                <div>
                  <label className="mb-2 block text-[12px] font-bold uppercase tracking-[0.12em] text-orbit-text-muted" htmlFor="completed-credits">
                    Tín chỉ đã tích lũy
                  </label>
                  <input
                    id="completed-credits"
                    type="number"
                    min="0"
                    step="1"
                    value={completedCredits}
                    onChange={(event) => setCompletedCredits(event.target.value)}
                    placeholder="60"
                    className="h-11 w-full rounded-[8px] border border-orbit-border bg-orbit-bg px-3 text-[14px] text-orbit-text outline-none transition-colors placeholder:text-orbit-text-muted focus:border-orbit-accent/60"
                  />
                </div>
                {calculationMode === 'goal' && (
                  <div>
                    <label className="mb-2 block text-[12px] font-bold uppercase tracking-[0.12em] text-orbit-text-muted" htmlFor="target-gpa">
                      GPA mục tiêu
                    </label>
                    <input
                      id="target-gpa"
                      type="number"
                      min="0"
                      max="10"
                      step="0.01"
                      value={targetGpa}
                      onChange={(event) => setTargetGpa(event.target.value)}
                      placeholder="8.00"
                      className="h-11 w-full rounded-[8px] border border-orbit-border bg-orbit-bg px-3 text-[14px] text-orbit-text outline-none transition-colors placeholder:text-orbit-text-muted focus:border-orbit-accent/60"
                    />
                  </div>
                )}
              </div>
            </div>
          )}

          <div className="mb-6 rounded-[8px] border border-orbit-border bg-orbit-surface p-4 shadow-diffusion">
            <div className="grid gap-3 md:grid-cols-[minmax(0,1fr)_180px_180px] md:items-end">
              <div>
                <p className="text-[12px] font-black uppercase tracking-[0.14em] text-orbit-accent">Preset theo học kỳ</p>
                <p className="mt-2 text-[13px] leading-6 text-orbit-text-secondary">{presetStatus}</p>
              </div>
              <div>
                <label className="mb-2 block text-[12px] font-bold uppercase tracking-[0.12em] text-orbit-text-muted" htmlFor="semester-preset">
                  Chọn học kỳ
                </label>
                <select
                  id="semester-preset"
                  value={selectedSemester}
                  onChange={(event) => setSelectedSemester(event.target.value)}
                  className="h-11 w-full rounded-[8px] border border-orbit-border bg-orbit-bg px-3 text-[14px] text-orbit-text outline-none transition-colors focus:border-orbit-accent/60"
                >
                  {semesters.map((semester) => (
                    <option key={semester} value={semester}>Học kỳ {semester}</option>
                  ))}
                </select>
              </div>
              <button
                type="button"
                onClick={applySemesterPreset}
                disabled={semesterCourses.length === 0}
                className="inline-flex h-11 items-center justify-center rounded-[8px] bg-orbit-accent px-5 text-[13px] font-bold uppercase tracking-[0.12em] text-zinc-950 transition-colors hover:bg-emerald-300 disabled:cursor-not-allowed disabled:opacity-50"
              >
                Áp dụng học kỳ
              </button>
            </div>
          </div>

          <div className="overflow-hidden rounded-[8px] border border-orbit-border bg-orbit-surface shadow-diffusion">
            <div className="grid grid-cols-[minmax(180px,1fr)_96px_128px_104px] gap-3 border-b border-orbit-border bg-orbit-elevated/40 px-4 py-3 text-[12px] font-black uppercase tracking-[0.12em] text-orbit-text-muted">
              <span>Môn học</span>
              <span>Tín chỉ</span>
              <span>Điểm hệ 10</span>
              <span />
            </div>

            <div className="divide-y divide-orbit-border">
              {courses.map((course, index) => {
                const validation = validateCourse(course)
                const warningInputClass = 'border-amber-300/60 bg-amber-300/10 focus:border-amber-200'
                const defaultInputClass = 'border-orbit-border bg-orbit-bg focus:border-orbit-accent/60'
                return (
                  <div
                    key={course.id}
                    className={`grid grid-cols-[minmax(180px,1fr)_96px_128px_104px] gap-3 px-4 py-4 ${
                      validation.valid ? '' : 'bg-amber-300/[0.03]'
                    }`}
                  >
                    <div>
                      <label className="sr-only" htmlFor={`course-name-${course.id}`}>Tên môn {index + 1}</label>
                      <input
                        id={`course-name-${course.id}`}
                        value={course.name}
                        onChange={(event) => updateCourse(course.id, 'name', event.target.value)}
                        placeholder={`Môn ${index + 1}`}
                        className="h-11 w-full rounded-[8px] border border-orbit-border bg-orbit-bg px-3 text-[14px] text-orbit-text outline-none transition-colors placeholder:text-orbit-text-muted focus:border-orbit-accent/60"
                      />
                    </div>

                    <div>
                      <label className="sr-only" htmlFor={`course-credits-${course.id}`}>Tín chỉ {index + 1}</label>
                      <input
                        id={`course-credits-${course.id}`}
                        type="number"
                        min="0"
                        step="0.5"
                        value={course.credits}
                        onChange={(event) => updateCourse(course.id, 'credits', event.target.value)}
                        className={`h-11 w-full rounded-[8px] border px-3 text-[14px] text-orbit-text outline-none transition-colors ${
                          validation.creditsReason ? warningInputClass : defaultInputClass
                        }`}
                      />
                      {validation.creditsReason && (
                        <p className="mt-1 text-[11px] font-semibold leading-4 text-amber-200">{validation.creditsReason}</p>
                      )}
                    </div>

                    <div>
                      <label className="sr-only" htmlFor={`course-grade-${course.id}`}>Điểm hệ 10 môn {index + 1}</label>
                      <input
                        id={`course-grade-${course.id}`}
                        type="number"
                        min="0"
                        max="10"
                        step="0.1"
                        value={course.grade10}
                        onChange={(event) => updateCourse(course.id, 'grade10', event.target.value)}
                        className={`h-11 w-full rounded-[8px] border px-3 text-[14px] text-orbit-text outline-none transition-colors ${
                          validation.gradeReason ? warningInputClass : defaultInputClass
                        }`}
                      />
                      {validation.gradeReason && (
                        <p className="mt-1 text-[11px] font-semibold leading-4 text-amber-200">{validation.gradeReason}</p>
                      )}
                    </div>

                    <div className="flex gap-2">
                      <button
                        type="button"
                        onClick={() => duplicateCourse(course.id)}
                        className="flex h-11 w-11 items-center justify-center rounded-[8px] border border-orbit-border text-orbit-text-muted transition-colors hover:border-orbit-accent/60 hover:text-orbit-accent"
                        aria-label="Nhân bản dòng"
                      >
                        <Copy className="h-4 w-4" aria-hidden="true" />
                      </button>
                      <button
                        type="button"
                        onClick={() => removeCourse(course.id)}
                        className="flex h-11 w-11 items-center justify-center rounded-[8px] border border-orbit-border text-orbit-text-muted transition-colors hover:border-rose-300/60 hover:text-rose-300 disabled:cursor-not-allowed disabled:opacity-40"
                        disabled={courses.length === 1}
                        aria-label="Xóa môn"
                      >
                        <Trash2 className="h-4 w-4" aria-hidden="true" />
                      </button>
                    </div>
                  </div>
                )
              })}
            </div>
          </div>

          <div className="mt-5 flex flex-wrap gap-3">
            <button
              type="button"
              onClick={addCourse}
              className="inline-flex h-11 items-center gap-2 rounded-[8px] bg-orbit-accent px-5 text-[13px] font-bold uppercase tracking-[0.12em] text-zinc-950 transition-colors hover:bg-emerald-300"
            >
              <Plus className="h-4 w-4" aria-hidden="true" />
              Thêm môn
            </button>
            <button
              type="button"
              onClick={addFiveCourses}
              className="inline-flex h-11 items-center gap-2 rounded-[8px] border border-orbit-border px-4 text-[13px] font-bold uppercase tracking-[0.12em] text-orbit-text-secondary transition-colors hover:border-orbit-accent/60 hover:text-orbit-text"
            >
              <Plus className="h-4 w-4" aria-hidden="true" />
              Thêm 5 môn
            </button>
            <button
              type="button"
              onClick={clearCourses}
              className="inline-flex h-11 items-center gap-2 rounded-[8px] border border-orbit-border px-4 text-[13px] font-bold uppercase tracking-[0.12em] text-orbit-text-secondary transition-colors hover:border-rose-300/60 hover:text-rose-300"
            >
              <Trash2 className="h-4 w-4" aria-hidden="true" />
              Xóa tất cả
            </button>
            <button
              type="button"
              onClick={resetCourses}
              className="inline-flex h-11 items-center gap-2 rounded-[8px] border border-orbit-border px-4 text-[13px] font-bold uppercase tracking-[0.12em] text-orbit-text-secondary transition-colors hover:border-orbit-accent/60 hover:text-orbit-text"
            >
              <RotateCcw className="h-4 w-4" aria-hidden="true" />
              Reset mẫu
            </button>
          </div>
        </div>

        <aside className="h-fit rounded-[8px] border border-orbit-border bg-orbit-surface p-6 shadow-diffusion md:sticky md:top-24">
          <p className="text-[12px] font-black uppercase tracking-[0.16em] text-orbit-text-muted">Kết quả tạm tính</p>

          <div className="mt-6 rounded-[8px] border border-orbit-accent/20 bg-orbit-accent/10 p-5">
            <p className="text-[13px] font-bold text-orbit-accent">GPA học kỳ</p>
            <p className="mt-2 font-heading text-[56px] font-black leading-none text-orbit-text">
              {formatNumber(summary.average10)}
            </p>
          </div>

          <dl className="mt-6 space-y-4">
            <div className="flex items-center justify-between border-b border-orbit-border pb-4">
              <dt className="text-[14px] text-orbit-text-secondary">Tín chỉ kỳ này</dt>
              <dd className="text-[15px] font-bold text-orbit-text">{summary.totalCredits} tín chỉ</dd>
            </div>
            <div className="flex items-center justify-between">
              <dt className="text-[14px] text-orbit-text-secondary">Xếp loại</dt>
              <dd className="text-right text-[15px] font-bold text-orbit-text">{summary.classification}</dd>
            </div>
            {calculationMode === 'cumulative' && (
              <>
                <div className="flex items-center justify-between border-t border-orbit-border pt-4">
                  <dt className="text-[14px] text-orbit-text-secondary">GPA tích lũy dự kiến</dt>
                  <dd className="text-[15px] font-bold text-orbit-text">
                    {cumulativeSummary.valid ? formatNumber(cumulativeSummary.projectedGpa) : '--'}
                  </dd>
                </div>
                <div className="flex items-center justify-between">
                  <dt className="text-[14px] text-orbit-text-secondary">Tổng tín chỉ sau kỳ này</dt>
                  <dd className="text-[15px] font-bold text-orbit-text">{cumulativeSummary.totalCreditsAfterTerm} tín chỉ</dd>
                </div>
              </>
            )}
            {calculationMode === 'goal' && (
              <>
                <div className="flex items-center justify-between border-t border-orbit-border pt-4">
                  <dt className="text-[14px] text-orbit-text-secondary">Kỳ này cần trung bình</dt>
                  <dd className="text-[15px] font-bold text-orbit-text">
                    {goalSummary.valid ? formatNumber(clampGrade10(goalSummary.requiredTermGpa)) : '--'}
                  </dd>
                </div>
                <div className="flex items-center justify-between">
                  <dt className="text-[14px] text-orbit-text-secondary">Trạng thái mục tiêu</dt>
                  <dd className="text-right text-[15px] font-bold text-orbit-text">{goalStatusLabel(goalSummary.status)}</dd>
                </div>
                <div className="flex items-center justify-between">
                  <dt className="text-[14px] text-orbit-text-secondary">Tổng tín chỉ sau kỳ này</dt>
                  <dd className="text-[15px] font-bold text-orbit-text">{goalSummary.totalCreditsAfterTerm} tín chỉ</dd>
                </div>
              </>
            )}
          </dl>

          {summary.ignoredRows > 0 && (
            <p className="mt-4 rounded-[8px] border border-amber-300/30 bg-amber-300/10 p-3 text-[13px] leading-6 text-amber-100">
              Đang bỏ qua {summary.ignoredRows} dòng chưa hợp lệ.
            </p>
          )}

          {calculationMode === 'cumulative' && !cumulativeSummary.valid && (
            <p className="mt-4 rounded-[8px] border border-orbit-border bg-orbit-bg p-3 text-[13px] leading-6 text-orbit-text-secondary">
              Nhập GPA hiện tại, tín chỉ đã tích lũy và điểm kỳ này hợp lệ để ước lượng GPA mới.
            </p>
          )}

          {calculationMode === 'goal' && (
            <div className="mt-4 rounded-[8px] border border-orbit-border bg-orbit-bg p-3 text-[13px] leading-6 text-orbit-text-secondary">
              <p>{goalGuidance(goalSummary.status)}</p>
              {goalSummary.courseTargets.length > 0 && (
                <div className="mt-4 space-y-2">
                  <p className="text-[12px] font-black uppercase tracking-[0.12em] text-orbit-accent">Gợi ý từng môn</p>
                  {goalSummary.courseTargets.map((course, index) => (
                    <div key={`${course.name}-${index}`} className="flex items-center justify-between gap-3 border-t border-orbit-border pt-2">
                      <span className="min-w-0 text-orbit-text-secondary">{course.name} ({course.credits} tín chỉ)</span>
                      <span className="shrink-0 font-bold text-orbit-text">{formatNumber(course.targetGrade)}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          <div className="mt-6 rounded-[8px] border border-orbit-border bg-orbit-bg p-4 text-[13px] leading-6 text-orbit-text-secondary">
            Công thức: tổng của <span className="font-semibold text-orbit-text">điểm x tín chỉ</span> chia cho tổng tín chỉ hợp lệ. Với mục tiêu GPA, app tính ngược điểm trung bình kỳ này cần đạt. Kết quả chỉ mang tính tham khảo.
          </div>
        </aside>
      </div>
    </section>
  )
}
