import { useMemo, useState } from 'react'
import { Calculator, Plus, Trash2 } from 'lucide-react'

type CourseInput = {
  id: number
  name: string
  credits: string
  grade10: string
}

type CourseResult = {
  credits: number
  grade10: number
}

const initialCourses: CourseInput[] = [
  { id: 1, name: '', credits: '3', grade10: '' },
  { id: 2, name: '', credits: '3', grade10: '' },
]

function parseCourse(course: CourseInput): CourseResult | null {
  const credits = Number(course.credits)
  const grade10 = Number(course.grade10)

  if (!Number.isFinite(credits) || !Number.isFinite(grade10)) return null
  if (credits <= 0 || grade10 < 0 || grade10 > 10) return null

  return {
    credits,
    grade10,
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

export function GpaCalculatorPage() {
  const [courses, setCourses] = useState<CourseInput[]>(initialCourses)

  const summary = useMemo(() => {
    const validCourses = courses.map(parseCourse).filter((course): course is CourseResult => course !== null)
    const totalCredits = validCourses.reduce((sum, course) => sum + course.credits, 0)

    if (totalCredits === 0) {
      return {
        totalCredits: 0,
        average10: 0,
        classification: 'Nhập tín chỉ và điểm hợp lệ',
      }
    }

    const weighted10 = validCourses.reduce((sum, course) => sum + course.grade10 * course.credits, 0)
    const average10 = weighted10 / totalCredits

    return {
      totalCredits,
      average10,
      classification: classifyGrade10(average10),
    }
  }, [courses])

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

          <div className="overflow-hidden rounded-[8px] border border-orbit-border bg-orbit-surface shadow-diffusion">
            <div className="grid grid-cols-[minmax(180px,1fr)_96px_128px_56px] gap-3 border-b border-orbit-border bg-orbit-elevated/40 px-4 py-3 text-[12px] font-black uppercase tracking-[0.12em] text-orbit-text-muted">
              <span>Môn học</span>
              <span>Tín chỉ</span>
              <span>Điểm hệ 10</span>
              <span />
            </div>

            <div className="divide-y divide-orbit-border">
              {courses.map((course, index) => (
                <div key={course.id} className="grid grid-cols-[minmax(180px,1fr)_96px_128px_56px] gap-3 px-4 py-4">
                  <label className="sr-only" htmlFor={`course-name-${course.id}`}>Tên môn {index + 1}</label>
                  <input
                    id={`course-name-${course.id}`}
                    value={course.name}
                    onChange={(event) => updateCourse(course.id, 'name', event.target.value)}
                    placeholder={`Môn ${index + 1}`}
                    className="h-11 rounded-[8px] border border-orbit-border bg-orbit-bg px-3 text-[14px] text-orbit-text outline-none transition-colors placeholder:text-orbit-text-muted focus:border-orbit-accent/60"
                  />

                  <label className="sr-only" htmlFor={`course-credits-${course.id}`}>Tín chỉ {index + 1}</label>
                  <input
                    id={`course-credits-${course.id}`}
                    type="number"
                    min="0"
                    step="0.5"
                    value={course.credits}
                    onChange={(event) => updateCourse(course.id, 'credits', event.target.value)}
                    className="h-11 rounded-[8px] border border-orbit-border bg-orbit-bg px-3 text-[14px] text-orbit-text outline-none transition-colors focus:border-orbit-accent/60"
                  />

                  <label className="sr-only" htmlFor={`course-grade-${course.id}`}>Điểm hệ 10 môn {index + 1}</label>
                  <input
                    id={`course-grade-${course.id}`}
                    type="number"
                    min="0"
                    max="10"
                    step="0.1"
                    value={course.grade10}
                    onChange={(event) => updateCourse(course.id, 'grade10', event.target.value)}
                    className="h-11 rounded-[8px] border border-orbit-border bg-orbit-bg px-3 text-[14px] text-orbit-text outline-none transition-colors focus:border-orbit-accent/60"
                  />

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
              ))}
            </div>
          </div>

          <button
            type="button"
            onClick={addCourse}
            className="mt-5 inline-flex h-11 items-center gap-2 rounded-[8px] bg-orbit-accent px-5 text-[13px] font-bold uppercase tracking-[0.12em] text-zinc-950 transition-colors hover:bg-emerald-300"
          >
            <Plus className="h-4 w-4" aria-hidden="true" />
            Thêm môn
          </button>
        </div>

        <aside className="h-fit rounded-[8px] border border-orbit-border bg-orbit-surface p-6 shadow-diffusion md:sticky md:top-24">
          <p className="text-[12px] font-black uppercase tracking-[0.16em] text-orbit-text-muted">Kết quả tạm tính</p>

          <div className="mt-6 rounded-[8px] border border-orbit-accent/20 bg-orbit-accent/10 p-5">
            <p className="text-[13px] font-bold text-orbit-accent">GPA hệ 10</p>
            <p className="mt-2 font-heading text-[56px] font-black leading-none text-orbit-text">
              {formatNumber(summary.average10)}
            </p>
          </div>

          <dl className="mt-6 space-y-4">
            <div className="flex items-center justify-between border-b border-orbit-border pb-4">
              <dt className="text-[14px] text-orbit-text-secondary">Tổng tín chỉ</dt>
              <dd className="text-[15px] font-bold text-orbit-text">{summary.totalCredits} tín chỉ</dd>
            </div>
            <div className="flex items-center justify-between">
              <dt className="text-[14px] text-orbit-text-secondary">Xếp loại</dt>
              <dd className="text-right text-[15px] font-bold text-orbit-text">{summary.classification}</dd>
            </div>
          </dl>

          <div className="mt-6 rounded-[8px] border border-orbit-border bg-orbit-bg p-4 text-[13px] leading-6 text-orbit-text-secondary">
            Công thức: tổng của <span className="font-semibold text-orbit-text">điểm x tín chỉ</span> chia cho tổng tín chỉ hợp lệ. Kết quả chỉ mang tính tham khảo.
          </div>
        </aside>
      </div>
    </section>
  )
}
