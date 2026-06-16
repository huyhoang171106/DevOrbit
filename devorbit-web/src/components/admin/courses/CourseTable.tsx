import { useState, useMemo } from 'react'
import { CaretUp, CaretDown } from '@phosphor-icons/react'
import type { CourseSummary } from '../../../types/api'
import { LOAI_MON_HOC_LABELS } from './CourseFormDialog'

interface CourseTableProps {
  courses: CourseSummary[]
  onEdit: (course: CourseSummary) => void
  onDelete: (id: number) => void
}

type SortKey = 'code' | 'credits' | 'loaiMonHoc'

export function CourseTable({ courses, onEdit, onDelete }: CourseTableProps) {
  const [sortKey, setSortKey] = useState<SortKey | null>(null)
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc')

  const sorted = useMemo(() => {
    if (!sortKey) return courses
    return [...courses].sort((a, b) => {
      const aVal = a[sortKey as keyof CourseSummary]
      const bVal = b[sortKey as keyof CourseSummary]
      if (aVal == null) return 1
      if (bVal == null) return -1
      const cmp = typeof aVal === 'number'
        ? (aVal as number) - (bVal as number)
        : String(aVal).localeCompare(String(bVal))
      return sortDir === 'asc' ? cmp : -cmp
    })
  }, [courses, sortKey, sortDir])

  function toggleSort(key: SortKey) {
    if (sortKey === key) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'))
    } else {
      setSortKey(key)
      setSortDir('asc')
    }
  }

  function SortHeader({ sortable, children }: { sortable: SortKey; children: React.ReactNode }) {
    const active = sortKey === sortable
    return (
      <th
        className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase cursor-pointer select-none hover:text-orbit-accent transition-colors"
        onClick={() => toggleSort(sortable)}
      >
        <span className="inline-flex items-center gap-1">
          {children}
          {active ? (
            sortDir === 'asc'
              ? <CaretUp className="h-3 w-3" weight="bold" />
              : <CaretDown className="h-3 w-3" weight="bold" />
          ) : (
            <div className="h-3 w-3" />
          )}
        </span>
      </th>
    )
  }

  if (courses.length === 0) {
    return (
      <div className="glass-card p-8 text-center">
        <p className="text-ink-secondary">Không có môn học</p>
      </div>
    )
  }

  return (
    <div className="glass-card overflow-hidden border border-orbit-border">
      <table className="w-full">
        <thead>
          <tr className="border-b border-orbit-border bg-orbit-surface/50">
            <SortHeader sortable="code">Mã MH</SortHeader>
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Tên môn học</th>
            <SortHeader sortable="credits">TC</SortHeader>
            <SortHeader sortable="loaiMonHoc">Loại</SortHeader>
            <th className="px-4 py-3 text-center text-xs font-medium text-orbit-text uppercase">Thao tác</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-clay-border">
          {sorted.map((course) => (
            <tr key={course.id} className="transition-colors hover:bg-orbit-surface/30">
              <td className="px-4 py-3 text-sm font-medium text-ink-primary text-center">{course.code}</td>
              <td className="px-4 py-3 text-sm text-ink-primary text-center">{course.name}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary text-center">{course.credits ?? '-'}</td>
              <td className="px-4 py-3 text-sm text-ink-secondary text-center">{LOAI_MON_HOC_LABELS[course.loaiMonHoc] ?? course.loaiMonHoc ?? '-'}</td>
              <td className="px-4 py-3 text-sm text-center">
                <div className="flex items-center justify-center gap-2">
                  <button onClick={() => onEdit(course)} className="btn-ghost text-xs px-3 py-1.5">Sửa</button>
                  <button onClick={() => onDelete(course.id)} className="btn-ghost text-xs px-3 py-1.5 text-red-400 hover:text-red-300">Xoá</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
