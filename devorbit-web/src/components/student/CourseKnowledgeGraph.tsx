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
      <div className="border border-clay-border p-6">
        <div className="h-4 w-24 bg-clay-border mb-6" />
        <div className="space-y-3">
          {[1, 2, 3].map(i => (
            <div key={i} className="h-12 bg-clay-surface" />
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
    <div className="border border-clay-border">
      {/* Header */}
      <div className="px-6 py-4 border-b border-clay-border flex items-center justify-between bg-clay-surface/50">
        <h3 className="text-[11px] font-bold uppercase tracking-[0.1em] text-clay-primary">
          Bản đồ kiến thức
        </h3>
        <div className="h-5 w-5 text-clay-primary/40">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
            <path d="M9 6l6 6-6 6" />
          </svg>
        </div>
      </div>

      {/* Body */}
      <div className="p-6 space-y-8">
        {prerequisites.length > 0 && (
          <Section
            label="Môn tiên quyết"
            items={prerequisites}
            courseId={courseId}
          />
        )}

        {corequisite.length > 0 && (
          <Section
            label="Môn song hành"
            items={corequisite}
            courseId={courseId}
          />
        )}

        {complementary.length > 0 && (
          <Section
            label="Môn bổ trợ / Gợi ý"
            items={complementary}
            courseId={courseId}
          />
        )}
      </div>

      {/* Footer */}
      <div className="px-6 py-4 border-t border-clay-border bg-clay-surface/30">
        <p className="text-[10px] text-clay-text-muted leading-relaxed italic">
          Các mối liên hệ được xác lập dựa trên chương trình đào tạo SE 2025 và lộ trình học tập khuyến nghị.
        </p>
      </div>
    </div>
  )
}

function Section({
  label,
  items,
  courseId,
}: {
  label: string
  items: CourseRelationshipResponse[]
  courseId: number
}) {
  return (
    <div>
      <h4 className="text-[10px] font-semibold uppercase tracking-[0.08em] text-clay-text-muted mb-3">
        {label}
      </h4>
      <div className="divide-y divide-clay-border border border-clay-border">
        {items.map(r => (
          <Link
            key={r.id}
            to={`/courses/${r.courseId === courseId ? r.relatedCourseId : r.courseId}`}
            className="flex items-center justify-between px-4 py-3 hover:bg-clay-surface transition-colors group"
          >
            <div className="min-w-0">
              <div className="flex items-center gap-2 mb-0.5">
                <span className="text-[12px] font-bold text-clay-text group-hover:text-clay-primary transition-colors">
                  {r.courseId === courseId ? r.relatedCourseCode : r.courseCode}
                </span>
                {r.relationType === 'COMPLEMENTARY' && (
                  <span className="text-[9px] px-1.5 py-0.5 rounded-full bg-indigo-500/10 text-indigo-500 font-bold uppercase tracking-tighter">
                    Gợi ý
                  </span>
                )}
              </div>
              <div className="text-[11px] text-clay-text-muted truncate font-medium">
                {r.courseId === courseId ? r.relatedCourseName : r.courseName}
              </div>
              {(r.courseId === courseId ? r.relatedCourseNameEn : r.courseNameEn) && (
                <div className="text-[10px] text-ink-muted/60 truncate italic">
                  {r.courseId === courseId ? r.relatedCourseNameEn : r.courseNameEn}
                </div>
              )}
            </div>
            <svg className="h-3 w-3 text-ink-muted shrink-0 ml-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M9 18l6-6-6-6" />
            </svg>
          </Link>
        ))}
      </div>
    </div>
  )
}
