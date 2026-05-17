import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { apiGet } from '../../lib/api'
import type { CourseRelationshipResponse } from '../../types/api'
import { Graph, ArrowRight } from '@phosphor-icons/react'

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
      <div className="orbit-card-glow p-8">
        <div className="flex items-center gap-3 mb-6">
          <div className="h-3 w-3 rounded-full bg-orbit-accent animate-breathing" />
          <div className="h-3 w-24 skeleton rounded-full" />
        </div>
        <div className="space-y-3">
          {[1, 2, 3].map(i => (
            <div key={i} className="h-16 skeleton rounded-2xl" />
          ))}
        </div>
      </div>
    )
  }

  if (relationships.length === 0) return null

  const prerequisites = relationships.filter(r => r.relationType === 'PREREQUISITE')
  const complementary = relationships.filter(r => r.relationType === 'COMPLEMENTARY')
  const corequisite = relationships.filter(r => r.relationType === 'COREQUISITE')

  const sections = [
    { label: 'Môn tiên quyết', items: prerequisites, color: 'text-orbit-accent', border: 'border-orbit-accent/30', dot: 'bg-orbit-accent' },
    { label: 'Môn song hành', items: corequisite, color: 'text-amber-400', border: 'border-amber-500/30', dot: 'bg-amber-400' },
    { label: 'Môn bổ trợ / Gợi ý', items: complementary, color: 'text-indigo-400', border: 'border-indigo-500/30', dot: 'bg-indigo-400' },
  ].filter(s => s.items.length > 0)

  return (
    <motion.div
      className="orbit-card-glow p-0 overflow-hidden"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ type: 'spring', stiffness: 300, damping: 30 }}
    >
      {/* Header */}
      <div className="px-8 py-5 border-b border-orbit-border/50 flex items-center justify-between">
        <h3 className="text-[11px] font-black uppercase tracking-[0.15em] text-orbit-accent flex items-center gap-2.5">
          <Graph className="h-4 w-4" weight="fill" />
          Bản đồ kiến thức
        </h3>
        <div className="h-6 w-6 rounded-lg bg-orbit-surface border border-orbit-border flex items-center justify-center">
          <ArrowRight className="h-3 w-3 text-orbit-text-muted" weight="bold" />
        </div>
      </div>

      {/* Body */}
      <div className="p-6 space-y-6">
        <AnimatePresence>
          {sections.map((section, si) => (
            <motion.div
              key={section.label}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: si * 0.1 }}
            >
              <h4 className={`text-[10px] font-bold uppercase tracking-[0.12em] ${section.color} mb-3 flex items-center gap-2`}>
                <span className={`h-1.5 w-1.5 rounded-full ${section.dot}`} />
                {section.label}
              </h4>
              <div className="space-y-1">
                {section.items.map(r => (
                  <Link
                    key={r.id}
                    to={`/courses/${r.courseId === courseId ? r.relatedCourseId : r.courseId}`}
                    className="flex items-center justify-between px-4 py-3 rounded-2xl bg-orbit-surface/50 border border-orbit-border/50 hover:bg-orbit-accent/5 hover:border-orbit-accent/20 transition-[background-color,border-color] duration-300 group"
                  >
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2.5 mb-0.5">
                        <span className="text-[12px] font-bold text-orbit-text group-hover:text-orbit-accent transition-colors tabular-nums">
                          {r.courseId === courseId ? r.relatedCourseCode : r.courseCode}
                        </span>
                        {r.relationType === 'COMPLEMENTARY' && (
                          <span className="text-[9px] px-2 py-0.5 rounded-full bg-indigo-500/10 text-indigo-400 font-bold uppercase tracking-tighter border border-indigo-500/20">
                            Gợi ý
                          </span>
                        )}
                      </div>
                      <div className="text-[11px] text-orbit-text-muted truncate font-medium">
                        {r.courseId === courseId ? r.relatedCourseName : r.courseName}
                      </div>
                      {(r.courseId === courseId ? r.relatedCourseNameEn : r.courseNameEn) && (
                        <div className="text-[10px] text-orbit-text-muted/50 truncate italic">
                          {r.courseId === courseId ? r.relatedCourseNameEn : r.courseNameEn}
                        </div>
                      )}
                    </div>
                    <ArrowRight className="h-3 w-3 text-orbit-text-muted shrink-0 ml-4 group-hover:text-orbit-accent group-hover:translate-x-0.5 transition-[color,transform]" weight="bold" />
                  </Link>
                ))}
              </div>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

      {/* Footer */}
      <div className="px-8 py-4 border-t border-orbit-border/50 bg-orbit-surface/30">
        <p className="text-[10px] text-orbit-text-muted leading-relaxed italic">
          Các mối liên hệ được xác lập dựa trên chương trình đào tạo SE 2025 và lộ trình học tập khuyến nghị.
        </p>
      </div>
    </motion.div>
  )
}
