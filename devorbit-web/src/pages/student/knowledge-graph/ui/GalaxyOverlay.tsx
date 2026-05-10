import { useNavigate } from 'react-router-dom'
import { Activity, Play, RotateCcw, AlertTriangle, Clock } from 'lucide-react'
import { useGalaxyStore } from '../store/useGalaxyStore'
import { DOMAIN_CONFIG, type DomainId } from '../layout/types'

export function GalaxyOverlay() {
  const navigate = useNavigate()
  const isSimulationMode = useGalaxyStore((s) => s.isSimulationMode)
  const isTimeTravelMode = useGalaxyStore((s) => s.isTimeTravelMode)
  const failedNodes = useGalaxyStore((s) => s.failedNodes)
  const blockedNodes = useGalaxyStore((s) => s.blockedNodes)
  const toggleSimulation = useGalaxyStore((s) => s.toggleSimulation)
  const toggleTimeTravel = useGalaxyStore((s) => s.toggleTimeTravel)
  const resetFailedNodes = useGalaxyStore((s) => s.resetFailedNodes)

  return (
    <div className="absolute top-8 left-8 z-20 pointer-events-none max-w-md animate-in fade-in slide-in-from-left-8 duration-1000">
      <div className="glass-card-glow p-8 border-emerald-500/20 backdrop-blur-2xl">
        <div className="inline-flex items-center gap-3 px-3 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/20 mb-6">
          <div className="h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
          <span className="text-[10px] font-black uppercase tracking-[0.2em] text-emerald-500">
            Galaxy kiến thức 3D
          </span>
        </div>

        <h1 className="display-sm text-white mb-4 leading-tight">Hệ sinh thái học thuật</h1>
        <p className="body-sm text-ink-secondary mb-8 leading-relaxed">
          Dải ngân hà tri thức — mỗi thiên hà là một lĩnh vực, mỗi hành tinh là một môn học.
        </p>

        {/* domain legend */}
        <div className="grid grid-cols-2 gap-3 mb-8">
          {(Object.keys(DOMAIN_CONFIG) as DomainId[]).map((id) => {
            if (id === 'OTHER') return null
            const cfg = DOMAIN_CONFIG[id]
            return (
              <div key={id} className="flex items-center gap-3">
                <div
                  className="h-1.5 w-4 rounded-full"
                  style={{ backgroundColor: cfg.color, boxShadow: `0 0 8px ${cfg.color}` }}
                />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">
                  {cfg.name}
                </span>
              </div>
            )
          })}
        </div>

        {/* controls */}
        <div className="space-y-4 pt-6 border-t border-white/5 pointer-events-auto">
          {/* simulation button */}
          <button
            onClick={() => { toggleSimulation() }}
            className={`flex items-center justify-center gap-3 w-full px-6 py-3 rounded-xl font-black uppercase tracking-[0.2em] text-[11px] transition-all duration-500 border ${
              isSimulationMode
                ? 'bg-rose-500 border-rose-400 text-white shadow-[0_0_30px_rgba(244,63,94,0.4)]'
                : 'bg-emerald-500 border-emerald-400 text-white shadow-[0_0_30px_rgba(16,185,129,0.4)]'
            }`}
          >
            {isSimulationMode ? <Activity className="w-4 h-4" /> : <Play className="w-4 h-4" />}
            {isSimulationMode ? 'Thoát giả lập' : 'Bật chế độ giả lập'}
          </button>

          {/* time travel button */}
          <button
            onClick={() => { toggleTimeTravel() }}
            className={`flex items-center justify-center gap-3 w-full px-6 py-3 rounded-xl font-black uppercase tracking-[0.2em] text-[11px] transition-all duration-500 border ${
              isTimeTravelMode
                ? 'bg-cyan-500 border-cyan-400 text-white shadow-[0_0_30px_rgba(6,182,212,0.4)]'
                : 'bg-white/5 border-white/10 text-ink-muted hover:text-white hover:border-white/20'
            }`}
          >
            <Clock className="w-4 h-4" />
            {isTimeTravelMode ? 'Thoát du hành' : 'Du hành thời gian'}
          </button>

          {isSimulationMode && (
            <div className="space-y-4 animate-in fade-in slide-in-from-top-4 duration-500">
              <div className="p-5 rounded-2xl bg-white/[0.03] border border-white/5 backdrop-blur-sm">
                <div className="flex items-center justify-between mb-4">
                  <span className="text-[10px] font-black uppercase tracking-widest text-ink-muted">
                    Chỉ số giả lập
                  </span>
                  <button
                    onClick={resetFailedNodes}
                    className="text-ink-muted hover:text-white transition-colors"
                  >
                    <RotateCcw className="w-3 h-3" />
                  </button>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-[20px] font-bold text-rose-500">{failedNodes.size}</span>
                  <span className="text-[8px] font-black uppercase tracking-widest text-ink-muted">
                    Thất bại
                  </span>
                </div>
                <div className="flex items-center gap-2 mt-2">
                  <span className="text-[20px] font-bold text-rose-300">{blockedNodes.size}</span>
                  <span className="text-[8px] font-black uppercase tracking-widest text-ink-muted">
                    Bị chặn
                  </span>
                </div>
              </div>

              {failedNodes.size > 0 && (
                <div className="flex items-start gap-3 p-4 rounded-xl bg-rose-500/10 border border-rose-500/20">
                  <AlertTriangle className="w-4 h-4 text-rose-500 shrink-0 mt-0.5" />
                  <p className="text-[10px] text-rose-200 leading-relaxed font-medium">
                    Click hành tinh để giả lập failure — quan sát cascade blocking.
                  </p>
                </div>
              )}
            </div>
          )}

          <button
            onClick={() => navigate('/courses')}
            className="btn-secondary text-[11px] px-6 py-2.5 uppercase tracking-[0.2em] w-full text-center"
          >
            Xem danh mục
          </button>
        </div>
      </div>
    </div>
  )
}
