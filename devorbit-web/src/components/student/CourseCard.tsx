import { Link } from 'react-router-dom'
import type { CourseSummary } from '../../types/api'
import { getCourseColor, colorMap } from '../../lib/colors'

export function CourseCard({ course }: { course: CourseSummary }) {
  const themeColor = getCourseColor(course.code)
  const colors = colorMap[themeColor]

  return (
    <Link
      to={`/courses/${course.id}`}
      className={`glass-card-glow group relative flex flex-col h-full cursor-pointer overflow-hidden border-opacity-30 ${colors.borderHover} hover:bg-glass-surface-hover hover:shadow-2xl transition-all duration-700 p-7`}
    >
      {/* Decorative gradient corner */}
      <div className={`absolute top-0 right-0 w-32 h-32 ${colors.bgLight} blur-3xl rounded-full -translate-y-1/2 translate-x-1/2 group-hover:bg-opacity-20 transition-all duration-700`} />
      
      <div className="relative z-10 flex justify-between items-start mb-8">
        <div className={`h-12 w-12 rounded-2xl ${colors.bgLight} flex items-center justify-center border ${colors.border} ${colors.bgHover} group-hover:scale-110 group-hover:rotate-3 transition-all duration-500 shadow-sm ${colors.shadow}`}>
          <svg className={`h-6 w-6 ${colors.text}`} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75">
            <path strokeLinecap="round" strokeLinejoin="round" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18c-2.305 0-4.408.867-6 2.292m0-14.25v14.25" />
          </svg>
        </div>
        <div className="flex flex-col items-end gap-1">
          <span className={`inline-flex items-center gap-1.5 rounded-full ${colors.bgLight} border ${colors.border} px-3.5 py-1.5 text-[10px] font-black tracking-widest ${colors.text} group-hover:bg-opacity-20 transition-all duration-500`}>
            <span className={`h-1.5 w-1.5 rounded-full ${colors.bg} animate-pulse`} />
            {course.code}
          </span>
        </div>
      </div>

      <div className="relative z-10 space-y-4 mb-8">
        <h3 className={`heading-3 text-ink ${colors.text} opacity-90 group-hover:opacity-100 transition-colors duration-500 line-clamp-2 leading-tight`}>
          {course.name}
        </h3>

        <p className="body-sm text-ink-secondary line-clamp-2 opacity-70 group-hover:opacity-100 transition-opacity duration-500 leading-relaxed">
          Unlock specialized repositories and interconnected knowledge maps for this subject area.
        </p>
      </div>

      <div className="mt-auto relative z-10 pt-5 flex items-center justify-between border-t border-glass-border ${colors.borderHover} transition-colors duration-500">
        <div className="flex items-center gap-4">
          <span className={`inline-flex items-center gap-1.5 text-[11px] font-bold uppercase tracking-[0.15em] text-ink-muted ${colors.text} group-hover:opacity-100 transition-colors`}>
            <svg className="h-3.5 w-3.5 opacity-60" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M20 7H4a2 2 0 00-2 2v10a2 2 0 002 2h16a2 2 0 002-2V9a2 2 0 00-2-2z" />
              <path d="M16 21V5a2 2 0 00-2-2h-4a2 2 0 00-2 2v16" />
            </svg>
            {course.repoCount} {course.repoCount === 1 ? 'Source' : 'Sources'}
          </span>
        </div>
        
        <div className={`flex items-center gap-2 ${colors.text} opacity-0 group-hover:opacity-100 -translate-x-2 group-hover:translate-x-0 transition-all duration-500`}>
          <span className="text-[10px] font-black uppercase tracking-tighter">Explore</span>
          <div className={`h-8 w-8 rounded-full ${colors.bg} text-white flex items-center justify-center shadow-lg shadow-emerald-500/25 scale-90 group-hover:scale-100 transition-all duration-500`}>
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
              <path d="M5 12h14M12 5l7 7-7 7" />
            </svg>
          </div>
        </div>
      </div>
    </Link>
  )
}



