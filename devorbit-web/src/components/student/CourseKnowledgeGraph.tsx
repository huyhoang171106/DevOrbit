import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiGet } from '../../lib/api'
import type { CourseRelationshipResponse } from '../../types/api'

export function CourseKnowledgeGraph({ courseId }: { courseId: number }) {
  const [relationships, setRelationships] = useState<CourseRelationshipResponse[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    apiGet<CourseRelationshipResponse[]>(`/api/courses/relationships/course/${courseId}`)
      .then(setRelationships)
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [courseId])

  if (loading) {
    return (
      <div className="glass-card-glow p-8 animate-pulse border-emerald-500/10">
        <div className="h-4 w-32 bg-emerald-500/10 rounded mb-8" />
        <div className="space-y-6">
          {[1, 2, 3].map(i => (
            <div key={i} className="h-14 w-full bg-glass-surface rounded-2xl border border-glass-border/50" />
          ))}
        </div>
      </div>
    )
  }

  if (relationships.length === 0) return null

  const prerequisites = relationships.filter(r => r.relationType === 'PREREQUISITE')
  const complementary = relationships.filter(r => r.relationType === 'COMPLEMENTARY')
  const corequisite = relationships.filter(r => r.relationType === 'COREQUISITE')

  return (
    <div className="glass-card-glow overflow-hidden border-emerald-500/20 group">
      <div className="bg-gradient-to-r from-emerald-500/10 via-emerald-500/5 to-transparent px-8 py-5 border-b border-emerald-500/10 flex items-center justify-between">
        <h3 className="text-[10px] font-black uppercase tracking-[0.3em] text-emerald-500 flex items-center gap-3">
          <div className="h-2 w-2 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.5)] animate-pulse" />
          Neural Knowledge Map
        </h3>
        <div className="h-8 w-8 rounded-lg bg-glass-surface border border-glass-border flex items-center justify-center text-ink-muted group-hover:text-emerald-500 transition-colors">
          <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M7.5 3.75g.562.562 0 011.125 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L7.5 3.75z" />
          </svg>
        </div>
      </div>

      <div className="p-8 space-y-10">
        {prerequisites.length > 0 && (
          <div className="relative">
            <div className="absolute -left-4 top-1 bottom-1 w-[2px] bg-emerald-500/20 rounded-full" />
            <h4 className="text-[9px] font-black uppercase tracking-[0.25em] text-emerald-500/70 mb-5 flex items-center gap-2">
              <span className="h-1.5 w-1.5 rounded-full bg-emerald-500 shadow-[0_0_5px_rgba(16,185,129,0.5)]" />
              Required Pre-Nodes
            </h4>
            <div className="grid gap-3">
              {prerequisites.map(r => (
                <Link
                  key={r.id}
                  to={`/courses/${r.courseId === courseId ? r.relatedCourseId : r.courseId}`}
                  className="flex items-center justify-between p-4 rounded-2xl bg-glass-surface border border-glass-border hover:border-emerald-500/40 hover:bg-emerald-500/[0.03] hover:shadow-[0_8px_20px_-8px_rgba(16,185,129,0.15)] transition-all duration-300 group/item cursor-pointer"
                >
                  <div className="min-w-0">
                    <div className="text-xs font-black text-clay-text group-hover/item:text-emerald-500 transition-colors mb-0.5 tracking-tight">
                      {r.courseId === courseId ? r.relatedCourseCode : r.courseCode}
                    </div>
                    <div className="text-[10px] text-ink-secondary truncate font-medium group-hover/item:text-clay-text transition-colors">
                      {r.courseId === courseId ? r.relatedCourseName : r.courseName}
                    </div>
                  </div>
                  <div className="h-7 w-7 rounded-full bg-glass-surface border border-glass-border flex items-center justify-center text-ink-muted group-hover/item:bg-emerald-500 group-hover/item:text-white group-hover/item:border-emerald-500 transition-all duration-300">
                    <svg className="h-3 w-3 transition-transform group-hover/item:translate-x-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                      <path d="M9 18l6-6-6-6" />
                    </svg>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        )}

        {corequisite.length > 0 && (
          <div className="relative">
            <div className="absolute -left-4 top-1 bottom-1 w-[2px] bg-indigo-500/20 rounded-full" />
            <h4 className="text-[9px] font-black uppercase tracking-[0.25em] text-indigo-400/70 mb-5 flex items-center gap-2">
              <span className="h-1.5 w-1.5 rounded-full bg-indigo-500 shadow-[0_0_5px_rgba(99,102,241,0.5)]" />
              Parallel Sync Nodes
            </h4>
            <div className="grid gap-3">
              {corequisite.map(r => (
                <Link
                  key={r.id}
                  to={`/courses/${r.courseId === courseId ? r.relatedCourseId : r.courseId}`}
                  className="flex items-center justify-between p-4 rounded-2xl bg-glass-surface border border-glass-border hover:border-indigo-500/40 hover:bg-indigo-500/[0.03] hover:shadow-[0_8px_20px_-8px_rgba(99,102,241,0.15)] transition-all duration-300 group/item cursor-pointer"
                >
                  <div className="min-w-0">
                    <div className="text-xs font-black text-clay-text group-hover/item:text-indigo-400 transition-colors mb-0.5 tracking-tight">
                      {r.courseId === courseId ? r.relatedCourseCode : r.courseCode}
                    </div>
                    <div className="text-[10px] text-ink-secondary truncate font-medium group-hover/item:text-clay-text transition-colors">
                      {r.courseId === courseId ? r.relatedCourseName : r.courseName}
                    </div>
                  </div>
                  <div className="h-7 w-7 rounded-full bg-glass-surface border border-glass-border flex items-center justify-center text-ink-muted group-hover/item:bg-indigo-500 group-hover/item:text-white group-hover/item:border-indigo-500 transition-all duration-300">
                    <svg className="h-3 w-3 transition-transform group-hover/item:translate-x-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                      <path d="M9 18l6-6-6-6" />
                    </svg>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        )}

        {complementary.length > 0 && (
          <div className="relative">
            <div className="absolute -left-4 top-1 bottom-1 w-[2px] bg-amber-500/20 rounded-full" />
            <h4 className="text-[9px] font-black uppercase tracking-[0.25em] text-amber-500/70 mb-5 flex items-center gap-2">
              <span className="h-1.5 w-1.5 rounded-full bg-amber-500 shadow-[0_0_5px_rgba(245,158,11,0.5)]" />
              Expansion Nodes
            </h4>
            <div className="grid gap-3">
              {complementary.map(r => (
                <Link
                  key={r.id}
                  to={`/courses/${r.courseId === courseId ? r.relatedCourseId : r.courseId}`}
                  className="flex items-center justify-between p-4 rounded-2xl bg-glass-surface border border-glass-border hover:border-amber-500/40 hover:bg-amber-500/[0.03] hover:shadow-[0_8px_20px_-8px_rgba(245,158,11,0.15)] transition-all duration-300 group/item cursor-pointer"
                >
                  <div className="min-w-0">
                    <div className="text-xs font-black text-clay-text group-hover/item:text-amber-500 transition-colors mb-0.5 tracking-tight">
                      {r.courseId === courseId ? r.relatedCourseCode : r.courseCode}
                    </div>
                    <div className="text-[10px] text-ink-secondary truncate font-medium group-hover/item:text-clay-text transition-colors">
                      {r.courseId === courseId ? r.relatedCourseName : r.courseName}
                    </div>
                  </div>
                  <div className="h-7 w-7 rounded-full bg-glass-surface border border-glass-border flex items-center justify-center text-ink-muted group-hover/item:bg-amber-500 group-hover/item:text-white group-hover/item:border-amber-500 transition-all duration-300">
                    <svg className="h-3 w-3 transition-transform group-hover/item:translate-x-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                      <path d="M9 18l6-6-6-6" />
                    </svg>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        )}
      </div>

      <div className="bg-glass-surface/50 p-6 border-t border-glass-border/40">
        <p className="text-[10px] text-ink-muted leading-relaxed font-medium italic">
          Neural connections represent the structural dependencies within the broader academic curriculum. Explore nodes to map your learning path.
        </p>
      </div>
    </div>
  )
}
