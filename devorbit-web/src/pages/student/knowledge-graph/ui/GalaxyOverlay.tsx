import { useNavigate } from 'react-router-dom'
import { Activity, Play, RotateCcw, AlertTriangle, FolderOpen, X } from 'lucide-react'
import { useGalaxyStore } from '../store/useGalaxyStore'
import type { CourseSummary } from '../../../types/api'

type Props = {
  expandedGroup: string | null
  onCollapse: () => void
  groupElectives: CourseSummary[]
}

const ELECTIVE_LEGEND: { code: string; name: string; color: string }[] = [
  { code: 'TC_CSN', name: 'Tự chọn Cơ sở ngành', color: '#d97706' },
  { code: 'TC_CN', name: 'Tự chọn Chuyên ngành', color: '#7c3aed' },
  { code: 'TC_TN', name: 'Chuyên đề TN', color: '#65a30d' },
]

export function GalaxyOverlay({ expandedGroup, onCollapse, groupElectives }: Props) {
  const navigate = useNavigate()
  const isSimulationMode = useGalaxyStore((s) => s.isSimulationMode)
  const failedNodes = useGalaxyStore((s) => s.failedNodes)
  const blockedNodes = useGalaxyStore((s) => s.blockedNodes)
  const toggleSimulation = useGalaxyStore((s) => s.toggleSimulation)
  const resetFailedNodes = useGalaxyStore((s) => s.resetFailedNodes)

  return (
    <div className="absolute top-6 left-6 z-20 pointer-events-none max-w-[280px] animate-in fade-in slide-in-from-left-4 duration-700">
      <div className="bg-white border border-clay-border p-6 shadow-sm">
        <div className="flex items-center gap-2 mb-4">
          <div className="h-1 w-4 bg-clay-primary" />
          <span className="text-[10px] font-bold uppercase tracking-[0.2em] text-clay-primary">
            Sơ đồ kiến thức 2D
          </span>
        </div>

        <h1 className="text-[20px] font-bold text-clay-text mb-2 leading-tight">Lộ trình học tập</h1>
        <p className="text-[12px] text-clay-text-muted mb-6 leading-relaxed">
          Mạng lưới các môn học được sắp xếp theo trình tự học tập. Phân tích các mối liên hệ và rủi ro.
        </p>

        {/* Legend */}
        <div className="space-y-2 pb-5 mb-5 border-b border-clay-border">
          <div className="text-[9px] font-bold uppercase tracking-widest text-clay-text-muted mb-2">Chú thích</div>
          <div className="flex items-center gap-2">
            <div className="h-3 w-3 shrink-0 border border-slate-300 bg-white" />
            <span className="text-[10px] text-clay-text-muted">Môn bắt buộc</span>
          </div>
          {ELECTIVE_LEGEND.map(item => (
            <div key={item.code} className="flex items-center gap-2">
              <div className="h-3 w-3 shrink-0 border border-dashed" style={{ borderColor: item.color, backgroundColor: `${item.color}15` }} />
              <span className="text-[10px] text-clay-text-muted">{item.name}</span>
            </div>
          ))}
          <div className="flex items-center gap-2">
            <div className="w-6 border-t border-dashed border-slate-300" />
            <span className="text-[10px] text-clay-text-muted">Liên kết bổ trợ</span>
          </div>
        </div>

        {/* Expanded group indicator and list */}
        {expandedGroup && (
          <div className="mb-4 pointer-events-auto bg-white border border-clay-border overflow-hidden animate-in slide-in-from-top-2 duration-300 shadow-lg">
            <div className="flex items-center justify-between p-3 bg-clay-surface border-b border-clay-border">
              <div className="flex items-center gap-2 min-w-0">
                <FolderOpen className="w-3.5 h-3.5 text-clay-primary shrink-0" />
                <span className="text-[10px] font-bold text-clay-text truncate">
                  {ELECTIVE_LEGEND.find(e => e.code === expandedGroup)?.name || expandedGroup}
                </span>
              </div>
              <button onClick={onCollapse} className="p-1 hover:bg-clay-border rounded transition-colors">
                <X className="w-3 h-3 text-clay-text-muted" />
              </button>
            </div>
            
            <div className="max-h-[240px] overflow-y-auto custom-scrollbar p-1">
              {groupElectives.length > 0 ? (
                groupElectives.map(ec => (
                  <button
                    key={ec.id}
                    onClick={() => navigate(`/courses/${ec.id}`)}
                    className="w-full text-left px-3 py-2 hover:bg-clay-surface transition-colors group flex flex-col"
                  >
                    <span className="text-[10px] font-bold text-clay-primary leading-tight">{ec.code}</span>
                    <span className="text-[10px] text-clay-text truncate leading-tight mt-0.5">{ec.name}</span>
                  </button>
                ))
              ) : (
                <div className="py-6 text-center">
                  <Activity className="w-4 h-4 text-clay-text-muted animate-pulse mx-auto mb-2" />
                  <p className="text-[9px] text-clay-text-muted italic">Đang tải danh sách...</p>
                </div>
              )}
            </div>
          </div>
        )}

        <div className="space-y-3 pt-0 border-t-0 pointer-events-auto">
          <button
            onClick={() => toggleSimulation()}
            className={`flex items-center justify-center gap-2 w-full px-4 py-2.5 font-bold uppercase tracking-[0.1em] text-[10px] transition-all duration-300 border ${
              isSimulationMode
                ? 'bg-rose-50 border-rose-200 text-rose-600'
                : 'bg-clay-surface border-clay-border text-clay-text hover:bg-white hover:border-clay-primary'
            }`}
          >
            {isSimulationMode ? <Activity className="w-3.5 h-3.5" /> : <Play className="w-3.5 h-3.5" />}
            {isSimulationMode ? 'Thoát giả lập' : 'Chế độ giả lập'}
          </button>

          {isSimulationMode && (
            <div className="space-y-3 pt-3 animate-in fade-in slide-in-from-top-2">
              <div className="p-4 bg-clay-surface border border-clay-border">
                <div className="flex items-center justify-between mb-3">
                  <span className="text-[9px] font-bold uppercase tracking-widest text-clay-text-muted">
                    Phân tích rủi ro
                  </span>
                  <button onClick={resetFailedNodes} className="text-clay-text-muted hover:text-clay-text">
                    <RotateCcw className="w-3 h-3" />
                  </button>
                </div>

                <div className="flex items-baseline gap-2">
                  <span className="text-xl font-bold text-rose-600">{failedNodes.size}</span>
                  <span className="text-[9px] font-bold text-clay-text-muted uppercase">Thất bại</span>
                </div>
                <div className="flex items-baseline gap-2 mt-1">
                  <span className="text-xl font-bold text-rose-400">{blockedNodes.size}</span>
                  <span className="text-[9px] font-bold text-clay-text-muted uppercase">Bị chặn</span>
                </div>
              </div>

              <div className="flex items-start gap-2 p-3 bg-rose-50 border border-rose-100">
                <AlertTriangle className="w-3 h-3 text-rose-500 shrink-0 mt-0.5" />
                <p className="text-[9px] text-rose-700 leading-tight">
                  Chọn môn học để xem ảnh hưởng lan truyền.
                </p>
              </div>
            </div>
          )}

          <button
            onClick={() => navigate('/courses')}
            className="w-full text-center py-2 text-[10px] font-bold text-clay-text-muted hover:text-clay-primary transition-colors uppercase tracking-[0.1em]"
          >
            Danh sách môn học
          </button>
        </div>
      </div>
    </div>
  )
}
