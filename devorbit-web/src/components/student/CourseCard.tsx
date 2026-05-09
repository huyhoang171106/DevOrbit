import { Link } from 'react-router-dom'
import type { CourseSummary } from '../../types/api'
import { getCourseColor, colorMap } from '../../lib/colors'

export function CourseCard({ course }: { course: CourseSummary }) {
  const themeColor = getCourseColor(course.code)
  const colors = colorMap[themeColor]

  return (
    <Link
      to={`/courses/${course.id}`}
      className={`clay-card-hover group relative flex flex-col h-full cursor-pointer bg-clay-surface !p-8 border-clay-border shadow-[10px_10px_0px_0px_var(--color-clay-shadow-outer)] hover:shadow-none hover:translate-x-1 hover:translate-y-1 transition-all duration-300`}
    >
      <div className="relative z-10 flex justify-between items-start mb-10">
        <div className={`h-16 w-16 rounded-2xl ${colors.bg} flex items-center justify-center border-[3px] border-clay-border shadow-[4px_4px_0px_0px_var(--color-clay-shadow-outer)] group-hover:bg-opacity-90 transition-all`}>
          <svg className="h-8 w-8 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18c-2.305 0-4.408.867-6 2.292m0-14.25v14.25" />
          </svg>
        </div>
        <div className="flex flex-col items-end gap-1">
          <span className={`inline-flex items-center gap-2 rounded-xl bg-clay-surface border-[3px] border-clay-border px-4 py-2 text-[12px] font-black tracking-widest text-clay-text shadow-[4px_4px_0px_0px_var(--color-clay-shadow-outer)]`}>
            {course.code}
          </span>
        </div>
      </div>

      <div className="relative z-10 space-y-4 mb-10">
        <h3 className={`text-[28px] font-extrabold text-clay-text uppercase italic leading-snug group-hover:text-clay-primary transition-colors`}>
          {course.name}
        </h3>

        <p className="text-[15px] font-bold text-clay-text-muted leading-relaxed">
          Khám phá các kho mã nguồn chuyên biệt và bản đồ tri thức cho môn học này.
        </p>
      </div>

      <div className="mt-auto relative z-10 pt-6 flex items-center justify-between border-t-[3px] border-clay-border/30">
        <div className="flex items-center gap-4">
          <span className={`inline-flex items-center gap-2 text-[13px] font-black uppercase tracking-widest text-clay-text`}>
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
              <path d="M20 7H4a2 2 0 00-2 2v10a2 2 0 002 2h16a2 2 0 002-2V9a2 2 0 00-2-2z" />
              <path d="M16 21V5a2 2 0 00-2-2h-4a2 2 0 00-2 2v16" />
            </svg>
            {course.repoCount} Dự án
          </span>
        </div>

        <div className="flex items-center gap-3">
          <span className="text-[11px] font-black uppercase tracking-tighter text-ink-muted group-hover:text-clay-primary">Xem chi tiết</span>
          <div className={`h-10 w-10 rounded-xl ${colors.bg} text-white flex items-center justify-center border-[3px] border-clay-border shadow-[4px_4px_0px_0px_var(--color-clay-shadow-outer)] group-hover:shadow-none transition-all`}>
            <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
              <path d="M5 12h14M12 5l7 7-7 7" />
            </svg>
          </div>
        </div>
      </div>
    </Link>
  )
}
