import { useState } from 'react'
import { CaretRight } from '@phosphor-icons/react'
import { adminApi } from '../../../lib/adminApi'
import { AdminSpinner } from '../shared/AdminSpinner'
import { getAdminToken } from '../../../lib/auth'
import type { RoadmapResponse, PhaseResponse, ItemResponse, RoadmapRequest, PhaseRequest, ItemRequest } from '../../../types/api'
import { RoadmapDialog } from './RoadmapDialog'
import { PhaseDialog } from './PhaseDialog'
import { ItemDialog } from './ItemDialog'

interface RoadmapTreeProps {
  roadmaps: RoadmapResponse[]
  onRefetch: () => void
}

export function RoadmapTree({ roadmaps, onRefetch }: RoadmapTreeProps) {
  const token = getAdminToken()
  const [expandedRoadmaps, setExpandedRoadmaps] = useState<Set<number>>(new Set())
  const [expandedPhases, setExpandedPhases] = useState<Set<number>>(new Set())
  const [phasesMap, setPhasesMap] = useState<Record<number, PhaseResponse[]>>({})
  const [itemsMap, setItemsMap] = useState<Record<number, ItemResponse[]>>({})
  const [loadingPhases, setLoadingPhases] = useState<Set<number>>(new Set())
  const [loadingItems, setLoadingItems] = useState<Set<number>>(new Set())

  // Dialog states
  const [roadmapDialog, setRoadmapDialog] = useState<{ open: boolean; initial?: Partial<RoadmapRequest> | null; id?: number }>({ open: false })
  const [phaseDialog, setPhaseDialog] = useState<{ open: boolean; initial?: Partial<PhaseRequest> | null; roadmapId?: number; id?: number }>({ open: false })
  const [itemDialog, setItemDialog] = useState<{ open: boolean; initial?: Partial<ItemRequest> | null; phaseId?: number; id?: number }>({ open: false })

  const toggleRoadmap = async (roadmapId: number) => {
    const newExpanded = new Set(expandedRoadmaps)
    if (newExpanded.has(roadmapId)) {
      newExpanded.delete(roadmapId)
      setExpandedRoadmaps(newExpanded)
      return
    }
    newExpanded.add(roadmapId)
    setExpandedRoadmaps(newExpanded)

    if (!phasesMap[roadmapId]) {
      setLoadingPhases((prev) => new Set(prev).add(roadmapId))
      try {
        const phases = await adminApi.getPhases(token!, roadmapId)
        setPhasesMap((prev) => ({ ...prev, [roadmapId]: phases }))
      } finally {
        setLoadingPhases((prev) => { const next = new Set(prev); next.delete(roadmapId); return next })
      }
    }
  }

  const togglePhase = async (phaseId: number) => {
    const newExpanded = new Set(expandedPhases)
    if (newExpanded.has(phaseId)) {
      newExpanded.delete(phaseId)
      setExpandedPhases(newExpanded)
      return
    }
    newExpanded.add(phaseId)
    setExpandedPhases(newExpanded)

    if (!itemsMap[phaseId]) {
      setLoadingItems((prev) => new Set(prev).add(phaseId))
      try {
        const items = await adminApi.getItems(token!, phaseId)
        setItemsMap((prev) => ({ ...prev, [phaseId]: items }))
      } finally {
        setLoadingItems((prev) => { const next = new Set(prev); next.delete(phaseId); return next })
      }
    }
  }

  const handleRoadmapSubmit = async (data: RoadmapRequest) => {
    if (roadmapDialog.id) {
      await adminApi.updateRoadmap(token!, roadmapDialog.id, data)
    } else {
      await adminApi.createRoadmap(token!, data)
    }
    setRoadmapDialog({ open: false })
    onRefetch()
  }

  const handlePhaseSubmit = async (data: PhaseRequest) => {
    if (phaseDialog.id) {
      await adminApi.updatePhase(token!, phaseDialog.id, data)
    } else {
      await adminApi.createPhase(token!, phaseDialog.roadmapId!, data)
    }
    setPhaseDialog({ open: false })
    setPhasesMap({})
    onRefetch()
  }

  const handleItemSubmit = async (data: ItemRequest) => {
    if (itemDialog.id) {
      await adminApi.updateItem(token!, itemDialog.id, data)
    } else {
      await adminApi.createItem(token!, itemDialog.phaseId!, data)
    }
    setItemDialog({ open: false })
    setItemsMap({})
    onRefetch()
  }

  const deleteRoadmap = async (id: number) => {
    if (!confirm('Delete this roadmap?')) return
    await adminApi.deleteRoadmap(token!, id)
    setPhasesMap((prev) => { const next = { ...prev }; delete next[id]; return next })
    onRefetch()
  }

  const deletePhase = async (phaseId: number) => {
    if (!confirm('Delete this phase?')) return
    await adminApi.deletePhase(token!, phaseId)
    setPhasesMap({})
    onRefetch()
  }

  const deleteItem = async (itemId: number) => {
    if (!confirm('Delete this item?')) return
    await adminApi.deleteItem(token!, itemId)
    setItemsMap({})
    onRefetch()
  }

  return (
    <div className="space-y-3">
      {roadmaps.map((roadmap) => (
        <div key={roadmap.id} className="glass-card overflow-hidden">
          <div
            className="flex items-center justify-between px-5 py-4 cursor-pointer hover:bg-orbit-surface/30 transition-colors"
            onClick={() => toggleRoadmap(roadmap.id)}
          >
            <div className="flex items-center gap-3">
              <CaretRight size={14} className={`text-ink-secondary transition-transform duration-200 ${expandedRoadmaps.has(roadmap.id) ? 'rotate-90' : ''}`} />
              <div>
                <span className="text-sm font-medium text-ink-primary">{roadmap.title}</span>
                <span className="ml-2 text-xs text-ink-secondary">by {roadmap.studentName}</span>
              </div>
            </div>
            <div className="flex gap-2" onClick={(e) => e.stopPropagation()}>
              <button
                onClick={() => setRoadmapDialog({ open: true, initial: { title: roadmap.title, description: roadmap.description ?? undefined, studentId: roadmap.studentId, isPublic: roadmap.isPublic }, id: roadmap.id })}
                className="btn-ghost text-xs"
              >
                Edit
              </button>
              <button
                onClick={() => setPhaseDialog({ open: true, roadmapId: roadmap.id })}
                className="btn-ghost text-xs"
              >
                + Phase
              </button>
              <button onClick={() => deleteRoadmap(roadmap.id)} className="btn-ghost text-xs text-red-400">Delete</button>
            </div>
          </div>

          {expandedRoadmaps.has(roadmap.id) && (
            <div className="border-t border-orbit-border">
              {loadingPhases.has(roadmap.id) ? (
                <div className="p-4"><AdminSpinner text="Loading phases..." /></div>
              ) : (
                (phasesMap[roadmap.id] ?? []).map((phase) => (
                  <div key={phase.id}>
                    <div
                      className="flex items-center justify-between px-8 py-3 cursor-pointer hover:bg-orbit-surface/20 transition-colors"
                      onClick={() => togglePhase(phase.id)}
                    >
                      <div className="flex items-center gap-3">
                        <CaretRight size={12} className={`text-ink-muted transition-transform duration-200 ${expandedPhases.has(phase.id) ? 'rotate-90' : ''}`} />
                        <span className="text-sm text-ink-primary">{phase.title}</span>
                        <span className="text-xs text-ink-muted">order {phase.sortOrder}</span>
                      </div>
                      <div className="flex gap-2" onClick={(e) => e.stopPropagation()}>
                        <button
                          onClick={() => setPhaseDialog({ open: true, initial: { title: phase.title, description: phase.description ?? undefined, sortOrder: phase.sortOrder }, roadmapId: roadmap.id, id: phase.id })}
                          className="btn-ghost text-xs"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => setItemDialog({ open: true, phaseId: phase.id })}
                          className="btn-ghost text-xs"
                        >
                          + Item
                        </button>
                        <button onClick={() => deletePhase(phase.id)} className="btn-ghost text-xs text-red-400">Delete</button>
                      </div>
                    </div>

                    {expandedPhases.has(phase.id) && (
                      <div className="border-t border-orbit-border/50">
                        {loadingItems.has(phase.id) ? (
                          <div className="p-3 pl-12"><AdminSpinner text="Loading items..." /></div>
                        ) : (
                          (itemsMap[phase.id] ?? []).length === 0 ? (
                            <div className="px-12 py-3 text-xs text-ink-muted">No items</div>
                          ) : (
                            (itemsMap[phase.id] ?? []).map((item) => (
                              <div key={item.id} className="flex items-center justify-between px-12 py-2.5 hover:bg-orbit-surface/10 transition-colors">
                                <div className="flex items-center gap-2">
                                  <span className="text-xs text-orbit-accent">&#8226;</span>
                                  <span className="text-sm text-ink-primary">{item.title || `${item.targetType} #${item.targetId}`}</span>
                                  <span className="text-xs px-1.5 py-0.5 rounded bg-orbit-accent/10 text-orbit-accent">{item.targetType}</span>
                                  <span className="text-xs text-ink-muted">order {item.sortOrder}</span>
                                </div>
                                <div className="flex gap-2">
                                  <button
                                    onClick={(e) => { e.stopPropagation(); setItemDialog({ open: true, initial: { targetType: item.targetType, targetId: item.targetId, title: item.title ?? undefined, note: item.note ?? undefined, sortOrder: item.sortOrder }, phaseId: phase.id, id: item.id }) }}
                                    className="btn-ghost text-xs"
                                  >
                                    Edit
                                  </button>
                                  <button onClick={(e) => { e.stopPropagation(); deleteItem(item.id) }} className="btn-ghost text-xs text-red-400">Delete</button>
                                </div>
                              </div>
                            ))
                          )
                        )}
                      </div>
                    )}
                  </div>
                ))
              )}
              {!loadingPhases.has(roadmap.id) && (phasesMap[roadmap.id] ?? []).length === 0 && (
                <div className="px-8 py-3 text-xs text-ink-muted">No phases yet</div>
              )}
            </div>
          )}
        </div>
      ))}

      <RoadmapDialog
        open={roadmapDialog.open}
        onClose={() => setRoadmapDialog({ open: false })}
        onSubmit={handleRoadmapSubmit}
        initial={roadmapDialog.initial}
      />
      <PhaseDialog
        open={phaseDialog.open}
        onClose={() => setPhaseDialog({ open: false })}
        onSubmit={handlePhaseSubmit}
        initial={phaseDialog.initial}
      />
      <ItemDialog
        open={itemDialog.open}
        onClose={() => setItemDialog({ open: false })}
        onSubmit={handleItemSubmit}
        initial={itemDialog.initial}
      />
    </div>
  )
}
