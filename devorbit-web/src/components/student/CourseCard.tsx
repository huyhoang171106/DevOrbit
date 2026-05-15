import { Link } from 'react-router-dom'
import type { CourseSummary } from '../../types/api'
import { getCourseColor, colorMap } from '../../lib/colors'
import { BookOpen, ArrowRight, Stack } from '@phosphor-icons/react'

export function CourseCard({ course }: { course: CourseSummary; index?: number }) {
  const themeColor = getCourseColor(course.code)
  const colors = colorMap[themeColor as keyof typeof colorMap]

  return (
    <Link
      to={`/courses/${course.id}`}
      className="group relative flex flex-col h-full orbit-card cursor-pointer overflow-hidden"
    >
      <div className="absolute -top-20 -right-20 w-40 h-40 bg-orbit-accent/5 blur-[80px] rounded-full opacity-0 group-hover:opacity-100 transition-opacity duration-700" />

      <div className="relative z-10 flex-1 flex flex-col">
        <div className="flex justify-between items-start mb-8">
          <div className={`h-14 w-14 rounded-2xl ${colors.bg} flex items-center justify-center shadow-glow transition-transform duration-500 group-hover:scale-105`}>
            <BookOpen className="h-7 w-7 text-white" weight="duotone" />
          </div>
          <span className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-orbit-surface border border-orbit-border text-[11px] font-black tracking-widest text-orbit-text-secondary tabular-nums">
            {course.code}
          </span>
        </div>

        <div className="space-y-4 flex-1">
          <h3 className="text-[22px] md:text-[24px] font-black text-orbit-text leading-tight tracking-tight group-hover:text-orbit-accent transition-colors duration-300">
            {course.name}
          </h3>
          <p className="body-sm text-[13px] leading-relaxed text-orbit-text-secondary">
            Khám phá các kho mã nguồn chuyên biệt và bản đồ tri thức cho môn học này.
          </p>
        </div>

        <div className="mt-10 pt-6 border-t border-orbit-border/50 flex items-center justify-between">
          <span className="flex items-center gap-2.5 text-[12px] font-semibold text-orbit-text-muted">
            <Stack className="h-4 w-4" weight="regular" />
            {course.repoCount} Dự án
          </span>
          <div className="flex items-center gap-2 text-[11px] font-bold text-orbit-accent opacity-0 group-hover:opacity-100 transition-all duration-300 translate-x-2 group-hover:translate-x-0">
            Xem chi tiết
            <ArrowRight className="h-3.5 w-3.5" weight="bold" />
          </div>
        </div>
      </div>
    </Link>
  )
}
