import { useDroppable } from '@dnd-kit/core'
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable'
import { GraphNode } from '../../types/api'
import { KanbanCard } from './KanbanCard'

type KanbanColumnProps = {
  semester: number | null
  label: string
  color: string
  nodes: GraphNode[]
  prereqMap: Map<number, number[]>
  recommendedIds: Set<number>
  semesterMap: Record<number, number | null>
  nodeMap: Map<number, GraphNode>
  creditMap: Map<number, number>
  onCardClick: (id: number) => void
}

const MAX_CREDITS_PER_SEMESTER = 24
const WARN_CREDITS = 20

export function KanbanColumn({
  semester,
  label,
  color,
  nodes,
  prereqMap,
  recommendedIds,
  semesterMap,
  nodeMap,
  creditMap,
  onCardClick,
}: KanbanColumnProps) {
  const columnId = `column-${semester === null ? 'null' : semester}`
  const { setNodeRef, isOver } = useDroppable({ id: columnId })

  const nodeIds = nodes.map(n => n.id)

  // Total credits in this column (only for real semesters)
  const isUnassigned = semester === null
  const totalCredits = nodes.reduce((sum, n) => sum + (creditMap.get(n.id) ?? Math.round(n.val)), 0)
  const creditRatio = isUnassigned ? 0 : totalCredits / MAX_CREDITS_PER_SEMESTER
  const isOverloaded = !isUnassigned && totalCredits > MAX_CREDITS_PER_SEMESTER
  const isWarning = !isUnassigned && totalCredits > WARN_CREDITS && !isOverloaded

  return (
    <div
      className={`flex flex-col flex-shrink-0 w-[300px] rounded-3xl transition-all duration-300 ${
        isOver ? 'bg-orbit-accent/5 border-orbit-accent/30' : ''
      }`}
      style={{
        border: `1px solid ${isOver ? `${color}60` : 'rgba(255,255,255,0.06)'}`,
        background: isOver
          ? `linear-gradient(135deg, ${color}06, transparent)`
          : 'rgba(24, 24, 27, 0.4)',
      }}
    >
      {/* Column header */}
      <div className="px-4 pt-4 pb-3 border-b border-white/5 space-y-2">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5 min-w-0">
            <div
              className="h-2.5 w-2.5 rounded-full shrink-0"
              style={{ backgroundColor: color, boxShadow: `0 0 6px ${color}60` }}
            />
            <span className="text-[14px] font-black uppercase tracking-[0.12em] text-orbit-text truncate">
              {label}
            </span>
          </div>
          <div className="flex items-center gap-1.5 shrink-0 ml-2">
            {/* Credit count — hidden for unassigned */}
            {!isUnassigned && (
              <>
                <span
                  className={`text-[12px] font-black tabular-nums ${
                    isOverloaded ? 'text-rose-400' : isWarning ? 'text-amber-400' : 'text-zinc-400'
                  }`}
                >
                  {totalCredits}
                </span>
                <span className="text-[11px] text-zinc-600 font-bold">/ {MAX_CREDITS_PER_SEMESTER} TC</span>
              </>
            )}
          </div>
        </div>

        {/* Capacity bar — hidden for unassigned */}
        {!isUnassigned && (
          <div className="h-1.5 rounded-full bg-zinc-800/60 overflow-hidden">
            <div
              className="h-full rounded-full transition-all duration-500 ease-out"
              style={{
                width: `${Math.min(creditRatio * 100, 100)}%`,
                backgroundColor: isOverloaded
                  ? '#f43f5e'
                  : isWarning
                    ? '#f59e0b'
                    : color,
                boxShadow: isOverloaded
                  ? '0 0 8px rgba(244,63,94,0.4)'
                  : isWarning
                    ? '0 0 8px rgba(245,158,11,0.3)'
                    : `0 0 6px ${color}40`,
              }}
            />
          </div>
        )}

        {/* Course count */}
        <div className="flex items-center justify-between">
          <span className="text-[12px] text-zinc-500 font-medium">
            {nodes.length} môn
          </span>
          {isOverloaded && (
            <span className="text-[11px] text-rose-400 font-bold uppercase tracking-wider">
              Quá tải
            </span>
          )}
        </div>
      </div>

      {/* Drop zone */}
      <div
        ref={setNodeRef}
        className="flex-1 p-3 space-y-2.5 min-h-[100px]"
      >
        {nodes.length === 0 ? (
          <div className="flex items-center justify-center h-20 rounded-2xl border border-dashed border-white/5">
            <span className="text-[13px] font-medium text-zinc-600">
              Kéo thả môn học vào đây
            </span>
          </div>
        ) : (
          <SortableContext items={nodeIds} strategy={verticalListSortingStrategy}>
            {nodes.map((node) => (
              <KanbanCard
                key={node.id}
                node={node}
                isRecommended={recommendedIds.has(node.id)}
                prerequisites={prereqMap.get(node.id) ?? []}
                semesterMap={semesterMap}
                color={color}
                nodeMap={nodeMap}
                creditMap={creditMap}
                maxSemesterCredits={MAX_CREDITS_PER_SEMESTER}
                onClick={() => onCardClick(node.id)}
              />
            ))}
          </SortableContext>
        )}
      </div>
    </div>
  )
}

export { MAX_CREDITS_PER_SEMESTER }

