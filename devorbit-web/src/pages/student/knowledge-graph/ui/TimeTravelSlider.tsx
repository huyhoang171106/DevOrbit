import { useGalaxyStore } from '../store/useGalaxyStore'

export function TimeTravelSlider() {
  const isTimeTravelMode = useGalaxyStore((s) => s.isTimeTravelMode)
  const currentSemester = useGalaxyStore((s) => s.currentSemester)
  const setCurrentSemester = useGalaxyStore((s) => s.setCurrentSemester)
  const weekMarkers = useGalaxyStore((s) => s.weekMarkers)

  if (!isTimeTravelMode) return null

  const maxWeek = Math.max(...weekMarkers.map((m) => m.week), 15)

  return (
    <div className="absolute bottom-8 left-1/2 -translate-x-1/2 z-30 pointer-events-auto animate-in fade-in slide-in-from-bottom-8 duration-700">
      <div className="glass-card px-6 py-4 border-emerald-500/20 backdrop-blur-2xl min-w-[320px]">
        <div className="flex items-center justify-between mb-3">
          <span className="text-[10px] font-black uppercase tracking-[0.2em] text-emerald-400">
            Du hành thời gian
          </span>
          <span className="text-[11px] text-ink-muted font-bold">
            Tuần {currentSemester}/{maxWeek}
          </span>
        </div>

        <input
          type="range"
          min={0}
          max={maxWeek}
          value={currentSemester}
          onChange={(e) => setCurrentSemester(Number(e.target.value))}
          className="w-full accent-emerald-500"
        />

        <div className="flex justify-between mt-2 px-0.5">
          {weekMarkers.map((m) => (
            <div key={m.week} className="flex flex-col items-center gap-0.5">
              <div
                className={`h-1.5 w-1.5 rounded-full ${
                  m.week <= currentSemester ? 'bg-emerald-500' : 'bg-white/10'
                }`}
              />
              <span className="text-[7px] text-ink-muted">{m.week}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
