import { useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { GraphNode } from '../../types/api'
import { getCourseColor, colorMap } from '../../lib/colors'

type KanbanCardProps = {
  node: GraphNode
  isRecommended: boolean
  prerequisites: number[]
  semesterMap: Record<number, number | null>
  color: string
  nodeMap: Map<number, GraphNode>
  creditMap: Map<number, number>
  maxSemesterCredits: number
  onClick?: () => void
}

export function KanbanCard({
  node,
  isRecommended,
  prerequisites,
  semesterMap,
  color,
  nodeMap,
  creditMap,
  maxSemesterCredits,
  onClick,
}: KanbanCardProps) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: node.id })

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.4 : 1,
    cursor: isDragging ? 'grabbing' : 'grab',
  }

  const courseColor = getCourseColor(node.code)
  const cc = colorMap[courseColor]
  const credits = creditMap.get(node.id) ?? Math.round(node.val)

  // Check for misplaced prerequisites
  const nodeSem = semesterMap[node.id]
  const misplacedPrereqs = prerequisites
    .map(id => ({ id, sem: semesterMap[id] }))
    .filter(p => p.sem !== null && nodeSem !== null && p.sem !== undefined && nodeSem !== undefined && p.sem > nodeSem)

  const hasWarning = misplacedPrereqs.length > 0
  const isOverloaded = nodeSem !== null && nodeSem !== undefined && credits > maxSemesterCredits

  // Resolve prerequisite node codes
  const prereqNames = prerequisites
    .map(id => nodeMap.get(id))
    .filter((n): n is GraphNode => !!n)
    .slice(0, 3)

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      onClick={onClick}
      className={`relative group rounded-2xl p-3.5 transition-all duration-200 border ${
        hasWarning
          ? 'border-rose-500/60 bg-rose-500/5'
          : isRecommended
            ? `${cc.bgLight} ${cc.borderHover}`
            : 'bg-zinc-900/60 border-zinc-800/60 hover:border-zinc-700/60'
      } ${isDragging ? 'shadow-2xl scale-105 z-50' : ''} ${
        onClick ? 'cursor-pointer' : ''
      }`}
    >
      {/* Warning toast */}
      {hasWarning && (
        <div className="absolute -top-2 -right-2 z-10">
          <div className="px-2 py-0.5 rounded-full bg-rose-500/20 border border-rose-500/30 text-[10px] font-black text-rose-400 uppercase tracking-wider whitespace-nowrap">
            Môn tiên quyết sai kỳ
          </div>
        </div>
      )}

      {/* Top row: code + credits */}
      <div className="flex items-center justify-between gap-2 mb-1.5">
        <span
          className={`text-[13px] font-black uppercase tracking-wider ${
            isRecommended ? cc.textLight : hasWarning ? 'text-rose-300' : 'text-zinc-400'
          }`}
        >
          {node.code}
        </span>
        <span
          className={`px-2 py-0.5 rounded-md text-[12px] font-bold tabular-nums ${
            isOverloaded
              ? 'bg-amber-500/15 text-amber-400'
              : 'bg-zinc-800 text-zinc-400'
          }`}
        >
          {credits} TC
        </span>
      </div>

      {/* Course name */}
      <p
        className={`text-[15px] font-semibold leading-snug tracking-tight mb-1.5 ${
          isRecommended ? 'text-orbit-text' : hasWarning ? 'text-rose-200' : 'text-zinc-300'
        } line-clamp-2`}
      >
        {node.name}
      </p>

      {/* Prerequisite display */}
      {prereqNames.length > 0 && (
        <div className="flex flex-wrap gap-1 mb-2">
          {prereqNames.map(p => (
            <span
              key={p.id}
              className="px-1.5 py-0.5 rounded-md bg-zinc-800/50 border border-zinc-700/30 text-[11px] font-medium text-zinc-500 truncate max-w-[100px]"
              title={p.name}
            >
              {p.code}
            </span>
          ))}
          {prerequisites.length > 3 && (
            <span className="px-1.5 py-0.5 text-[11px] text-zinc-600">
              +{prerequisites.length - 3}
            </span>
          )}
        </div>
      )}

      {/* Bottom row */}
      <div className="flex items-center justify-between pt-2 border-t border-white/5">
        {isRecommended && (
          <span
            className="text-[11px] font-black uppercase tracking-[0.15em]"
            style={{ color }}
          >
            Đề xuất
          </span>
        )}
        {hasWarning && (
          <span className="text-[11px] font-bold text-rose-400/70">
            Cần {misplacedPrereqs.length} môn trước
          </span>
        )}
        <div className="flex items-center gap-1 ml-auto">
          {Array.from({ length: Math.min(node.level + 1, 3) }).map((_, i) => (
            <div
              key={i}
              className="h-1 w-3 rounded-full transition-colors"
              style={{
                backgroundColor: i === 0 ? (hasWarning ? '#f43f5e' : color) : `${color}30`,
              }}
            />
          ))}
        </div>
      </div>

      {/* Drag handle */}
      <div className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity">
        <div className="flex flex-col gap-0.5 p-1">
          <div className="flex gap-0.5">
            <div className="h-0.5 w-0.5 rounded-full bg-zinc-500" />
            <div className="h-0.5 w-0.5 rounded-full bg-zinc-500" />
          </div>
          <div className="flex gap-0.5">
            <div className="h-0.5 w-0.5 rounded-full bg-zinc-500" />
            <div className="h-0.5 w-0.5 rounded-full bg-zinc-500" />
          </div>
        </div>
      </div>
    </div>
  )
}

