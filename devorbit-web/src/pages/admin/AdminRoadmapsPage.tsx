import { useState } from 'react'
import { apiAdminGet, apiAdminPost, apiAdminPut, apiAdminDelete } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { RoadmapDialog } from '../../components/admin/RoadmapDialog'
import { PhaseDialog } from '../../components/admin/PhaseDialog'
import { ItemDialog } from '../../components/admin/ItemDialog'
import type {
  RoadmapResponse, RoadmapRequest,
  PhaseResponse, PhaseRequest,
  ItemResponse, ItemRequest,
} from '../../types/api'

export function AdminRoadmapsPage() {
  useRequireAuth()
  const token = getAdminToken()!

  // Roadmap CRUD
  const [rmDialog, setRmDialog] = useState(false)
  const [rmEdit, setRmEdit] = useState<RoadmapRequest | undefined>(undefined)
  const [rmEditId, setRmEditId] = useState<number | null>(null)
  const { data: roadmaps, loading, refetch } = useApiFetch(
    () => apiAdminGet<RoadmapResponse[]>('/api/admin/roadmaps', token),
    [token],
  )

  // Phase state (per roadmap)
  const [expandedRoadmap, setExpandedRoadmap] = useState<number | null>(null)
  const [phases, setPhases] = useState<Record<number, PhaseResponse[]>>({})
  const [phLoading, setPhLoading] = useState<Record<number, boolean>>({})
  const [phDialog, setPhDialog] = useState(false)
  const [phRoadmapId, setPhRoadmapId] = useState<number | null>(null)
  const [phEdit, setPhEdit] = useState<PhaseRequest | undefined>(undefined)
  const [phEditId, setPhEditId] = useState<number | null>(null)

  // Item state (per phase)
  const [expandedPhase, setExpandedPhase] = useState<Record<number, boolean>>({})
  const [items, setItems] = useState<Record<number, ItemResponse[]>>({})
  const [itLoading, setItLoading] = useState<Record<number, boolean>>({})
  const [itDialog, setItDialog] = useState(false)
  const [itPhaseId, setItPhaseId] = useState<number | null>(null)
  const [itEdit, setItEdit] = useState<ItemRequest | undefined>(undefined)
  const [itEditId, setItEditId] = useState<number | null>(null)

  // Fetch phases for a roadmap
  async function toggleExpandRoadmap(rmId: number) {
    if (expandedRoadmap === rmId) {
      setExpandedRoadmap(null)
      return
    }
    setExpandedRoadmap(rmId)
    if (!phases[rmId]) {
      setPhLoading((p) => ({ ...p, [rmId]: true }))
      try {
        const data = await apiAdminGet<PhaseResponse[]>(`/api/admin/roadmaps/${rmId}/phases`, token)
        setPhases((p) => ({ ...p, [rmId]: data }))
      } finally {
        setPhLoading((p) => ({ ...p, [rmId]: false }))
      }
    }
  }

  // Fetch items for a phase
  async function toggleExpandPhase(phaseId: number) {
    setExpandedPhase((p) => ({ ...p, [phaseId]: !p[phaseId] }))
    if (!items[phaseId]) {
      setItLoading((p) => ({ ...p, [phaseId]: true }))
      try {
        const data = await apiAdminGet<ItemResponse[]>(`/api/admin/roadmaps/phases/${phaseId}/items`, token)
        setItems((p) => ({ ...p, [phaseId]: data }))
      } finally {
        setItLoading((p) => ({ ...p, [phaseId]: false }))
      }
    }
  }

  // Refresh phases after mutation
  async function refreshPhases(rmId: number) {
    const data = await apiAdminGet<PhaseResponse[]>(`/api/admin/roadmaps/${rmId}/phases`, token)
    setPhases((p) => ({ ...p, [rmId]: data }))
  }

  // Refresh items after mutation
  async function refreshItems(phaseId: number) {
    const data = await apiAdminGet<ItemResponse[]>(`/api/admin/roadmaps/phases/${phaseId}/items`, token)
    setItems((p) => ({ ...p, [phaseId]: data }))
  }

  // --- Roadmap handlers ---
  async function handleRmSubmit(data: RoadmapRequest) {
    try {
      if (rmEditId) {
        await apiAdminPut(`/api/admin/roadmaps/${rmEditId}`, token, data)
      } else {
        await apiAdminPost('/api/admin/roadmaps', token, data)
      }
      setRmDialog(false); setRmEdit(undefined); setRmEditId(null); refetch()
    } catch (e) { console.error(e) }
  }

  async function handleRmDelete(id: number) {
    if (!confirm('Xóa lộ trình này và toàn bộ dữ liệu liên quan?')) return
    try {
      await apiAdminDelete(`/api/admin/roadmaps/${id}`, token)
      setPhases((p) => { const n = { ...p }; delete n[id]; return n })
      refetch()
    } catch (e) { console.error(e) }
  }

  function openRmEdit(rm: RoadmapResponse) {
    setRmEdit({
      studentId: rm.studentId,
      title: rm.title,
      description: rm.description ?? '',
      markdownContent: rm.markdownContent ?? '',
      isPublic: rm.isPublic,
    })
    setRmEditId(rm.id)
    setRmDialog(true)
  }

  // --- Phase handlers ---
  async function handlePhSubmit(data: PhaseRequest) {
    try {
      if (phEditId) {
        await apiAdminPut(`/api/admin/roadmaps/phases/${phEditId}`, token, data)
      } else if (phRoadmapId) {
        await apiAdminPost(`/api/admin/roadmaps/${phRoadmapId}/phases`, token, data)
      }
      setPhDialog(false); setPhEdit(undefined); setPhEditId(null); setPhRoadmapId(null)
      if (phRoadmapId) refreshPhases(phRoadmapId)
      else if (phEditId && expandedRoadmap) refreshPhases(expandedRoadmap)
    } catch (e) { console.error(e) }
  }

  async function handlePhDelete(phaseId: number, rmId: number) {
    if (!confirm('Xóa giai đoạn này và toàn bộ mục?')) return
    try {
      await apiAdminDelete(`/api/admin/roadmaps/phases/${phaseId}`, token)
      setItems((p) => { const n = { ...p }; delete n[phaseId]; return n })
      refreshPhases(rmId)
    } catch (e) { console.error(e) }
  }

  // --- Item handlers ---
  async function handleItSubmit(data: ItemRequest) {
    try {
      if (itEditId) {
        await apiAdminPut(`/api/admin/roadmaps/items/${itEditId}`, token, data)
      } else if (itPhaseId) {
        await apiAdminPost(`/api/admin/roadmaps/phases/${itPhaseId}/items`, token, data)
      }
      setItDialog(false); setItEdit(undefined); setItEditId(null); setItPhaseId(null)
      if (itPhaseId) refreshItems(itPhaseId)
      else if (itEditId) {
        const phaseId = Object.entries(items).find(([, v]) => v.some((i) => i.id === itEditId))?.[0]
        if (phaseId) refreshItems(Number(phaseId))
      }
    } catch (e) { console.error(e) }
  }

  async function handleItDelete(itemId: number) {
    if (!confirm('Xóa mục này?')) return
    try {
      await apiAdminDelete(`/api/admin/roadmaps/items/${itemId}`, token)
      const phaseId = Object.entries(items).find(([, v]) => v.some((i) => i.id === itemId))?.[0]
      if (phaseId) refreshItems(Number(phaseId))
    } catch (e) { console.error(e) }
  }

  // --- Shared ---
  function Spinner() {
    return (
      <div className="flex items-center gap-2 text-xs text-clay-text-muted py-4 px-4">
        <svg className="h-4 w-4 animate-spin text-emerald-400" viewBox="0 0 24 24" fill="none">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
        </svg>
        Đang tải...
      </div>
    )
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px] flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="display-sm text-clay-text mb-1">Lộ trình học tập</h1>
          <p className="body-sm text-clay-text-muted">Quản lý lộ trình học tập của sinh viên</p>
        </div>
        <button onClick={() => { setRmEdit(undefined); setRmEditId(null); setRmDialog(true) }} className="btn-primary self-start text-sm px-4 py-2">
          <svg className="mr-2 h-4 w-4 inline" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 5v14M5 12h14" /></svg>
          Tạo lộ trình
        </button>
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-[96px]">
          <div className="flex items-center gap-3 body-sm text-clay-text-muted">
            <svg className="h-5 w-5 animate-spin text-emerald-400" viewBox="0 0 24 24" fill="none">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
            </svg>
            Đang tải lộ trình...
          </div>
        </div>
      ) : (
        <div className="space-y-[16px]">
          {(roadmaps ?? []).length === 0 && (
            <div className="glass-card p-[32px] text-center body-sm text-clay-text-muted">Chưa có lộ trình nào</div>
          )}
          {(roadmaps ?? []).map((rm) => (
            <div key={rm.id} className="glass-card overflow-hidden p-0">
              {/* Roadmap header */}
              <div className="p-4 flex items-center justify-between hover:bg-glass-surface-raised transition-colors cursor-pointer" onClick={() => toggleExpandRoadmap(rm.id)}>
                <div className="flex items-center gap-3 flex-1 min-w-0">
                  <button
                    className="text-clay-text-muted hover:text-clay-text transition-colors"
                  >
                    <svg className={`h-4 w-4 transition-transform ${expandedRoadmap === rm.id ? 'rotate-90' : ''}`} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M9 18l6-6-6-6" />
                    </svg>
                  </button>
                  <div className="min-w-0">
                    <h3 className="heading-4 text-clay-text">{rm.title}</h3>
                    <p className="body-sm text-clay-text-muted mt-[4px]">
                      {rm.studentCode} - {rm.studentName} &middot; {rm.isPublic ? 'Công khai' : 'Riêng tư'}
                      &middot; Updated {new Date(rm.updatedAt).toLocaleDateString()}
                    </p>
                  </div>
                </div>
                <div className="flex gap-2 shrink-0" onClick={(e) => e.stopPropagation()}>
                  <button onClick={() => openRmEdit(rm)} className="btn-secondary !py-1 !px-3 !bg-clay-surface !text-xs">Sửa</button>
                  <button onClick={() => handleRmDelete(rm.id)} className="btn-secondary !py-1 !px-3 !bg-clay-surface !text-xs !text-red-400 hover:!bg-red-500/10 border border-red-500/30">Xóa</button>
                </div>
              </div>

              {/* Phases (expanded) */}
              {expandedRoadmap === rm.id && (
                <div className="border-t border-clay-border bg-clay-surface">
                  {phLoading[rm.id] ? <Spinner /> : (
                    <div>
                      {/* Phase list */}
                      {(phases[rm.id] ?? []).length === 0 && (
                        <p className="px-[32px] py-[16px] body-sm text-clay-text-muted">Chưa có giai đoạn nào</p>
                      )}
                      {(phases[rm.id] ?? []).map((ph) => (
                        <div key={ph.id}>
                          {/* Phase row */}
                          <div className="pl-[48px] pr-4 py-3 flex items-center justify-between border-b border-clay-border hover:bg-glass-surface-raised cursor-pointer" onClick={() => toggleExpandPhase(ph.id)}>
                            <div className="flex items-center gap-3 flex-1 min-w-0">
                              <button
                                className="text-clay-text-muted hover:text-clay-text transition-colors"
                              >
                                <svg className={`h-3.5 w-3.5 transition-transform ${expandedPhase[ph.id] ? 'rotate-90' : ''}`} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                  <path d="M9 18l6-6-6-6" />
                                </svg>
                              </button>
                              <span className="code-sm text-clay-text-muted bg-clay-surface border border-clay-border px-1.5 py-0.5 rounded">#{ph.sortOrder}</span>
                              <div className="min-w-0">
                                <span className="body-md font-medium text-clay-text">{ph.title}</span>
                                {ph.description && <p className="body-sm text-clay-text-muted truncate mt-0.5">{ph.description}</p>}
                              </div>
                            </div>
                            <div className="flex gap-2 shrink-0" onClick={(e) => e.stopPropagation()}>
                              <button onClick={() => { setPhEdit({ title: ph.title, description: ph.description ?? '', sortOrder: ph.sortOrder }); setPhEditId(ph.id); setPhRoadmapId(null); setPhDialog(true) }} className="btn-secondary !py-0.5 !px-2 !bg-clay-bg !text-xs">Sửa</button>
                              <button onClick={() => handlePhDelete(ph.id, rm.id)} className="btn-secondary !py-0.5 !px-2 !bg-clay-bg !text-xs !text-red-400 hover:!bg-red-500/10 border border-red-500/30">Xóa</button>
                            </div>
                          </div>

                          {/* Items (expanded under phase) */}
                          {expandedPhase[ph.id] && (
                            <div className="bg-clay-bg border-b border-clay-border">
                              {itLoading[ph.id] ? <Spinner /> : (
                                <div className="py-2">
                                  {(items[ph.id] ?? []).length === 0 && (
                                    <p className="pl-[72px] pr-8 py-2 body-sm text-clay-text-muted">Chưa có mục nào</p>
                                  )}
                                  {(items[ph.id] ?? []).map((it) => (
                                    <div key={it.id} className="pl-[72px] pr-4 py-2 flex items-center justify-between hover:bg-glass-surface-raised transition-colors">
                                      <div className="flex items-center gap-3 min-w-0">
                                        <span className="code-sm text-clay-text-muted bg-clay-surface border border-clay-border px-1.5 py-0.5 rounded">#{it.sortOrder}</span>
                                        <span className="badge-tag uppercase">
                                          {it.targetType}
                                        </span>
                                        <span className="code-sm text-clay-text-muted">ID: {it.targetId}</span>
                                        {it.title && <span className="body-sm text-clay-text truncate max-w-[200px]">{it.title}</span>}
                                        {it.note && <span className="body-sm text-clay-text-muted italic truncate max-w-[150px]">{it.note}</span>}
                                      </div>
                                      <div className="flex gap-2 shrink-0">
                                        <button onClick={() => { setItEdit({ targetType: it.targetType, targetId: it.targetId, title: it.title ?? '', note: it.note ?? '', sortOrder: it.sortOrder }); setItEditId(it.id); setItPhaseId(null); setItDialog(true) }} className="btn-secondary !py-0.5 !px-2 !bg-clay-surface !text-xs">Sửa</button>
                                        <button onClick={() => handleItDelete(it.id)} className="btn-secondary !py-0.5 !px-2 !bg-clay-surface !text-xs !text-red-400 hover:!bg-red-500/10 border border-red-500/30">Xóa</button>
                                      </div>
                                    </div>
                                  ))}
                                  {/* Add item button */}
                                  <div className="pl-[72px] pr-4 py-2">
                                    <button onClick={() => { setItEdit(undefined); setItEditId(null); setItPhaseId(ph.id); setItDialog(true) }} className="body-sm text-emerald-400 hover:text-emerald-400/80 transition-colors font-medium">
                                      + Thêm mục
                                    </button>
                                  </div>
                                </div>
                              )}
                            </div>
                          )}
                        </div>
                      ))}
                      {/* Add phase button */}
                      <div className="pl-[48px] pr-4 py-3">
                        <button onClick={() => { setPhEdit(undefined); setPhEditId(null); setPhRoadmapId(rm.id); setPhDialog(true) }} className="body-sm text-emerald-400 hover:text-emerald-400/80 transition-colors font-medium">
                          + Thêm giai đoạn
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Dialogs */}
      <RoadmapDialog open={rmDialog} onClose={() => { setRmDialog(false); setRmEdit(undefined); setRmEditId(null) }} onSubmit={handleRmSubmit} initial={rmEdit} />
      <PhaseDialog open={phDialog} onClose={() => { setPhDialog(false); setPhEdit(undefined); setPhEditId(null); setPhRoadmapId(null) }} onSubmit={handlePhSubmit} initial={phEdit} />
      <ItemDialog open={itDialog} onClose={() => { setItDialog(false); setItEdit(undefined); setItEditId(null); setItPhaseId(null) }} onSubmit={handleItSubmit} initial={itEdit} />
    </div>
  )
}
