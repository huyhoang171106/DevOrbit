import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Activity, Play, RotateCcw, AlertTriangle, Sparkles, ChevronRight, Loader2 } from 'lucide-react'
import { useGalaxyStore } from '../store/useGalaxyStore'
import { useAiRoadmap, type RoadmapRecommendation } from '../../../../hooks/useAiRoadmap'

export function GalaxyOverlay() {
  const navigate = useNavigate()
  const isSimulationMode = useGalaxyStore((s) => s.isSimulationMode)
  const failedNodes = useGalaxyStore((s) => s.failedNodes)
  const blockedNodes = useGalaxyStore((s) => s.blockedNodes)
  const toggleSimulation = useGalaxyStore((s) => s.toggleSimulation)
  const resetFailedNodes = useGalaxyStore((s) => s.resetFailedNodes)
  const setAiRecommendedNodes = useGalaxyStore((s) => s.setAiRecommendedNodes)

  // AI Roadmap State
  const [goals, setGoals] = useState('')
  const [career, setCareer] = useState('')
  const [aiError, setAiError] = useState<string | null>(null)
  const { mutate: generateRoadmap, data: aiResult, isPending: isAiLoading, error: aiMutationError } = useAiRoadmap()

  // Clear error when inputs change
  useEffect(() => { setAiError(null) }, [goals, career])

  // Capture mutation error
  useEffect(() => {
    if (aiMutationError) {
      setAiError(aiMutationError.message)
    }
  }, [aiMutationError])

  // Update store when results arrive
  useEffect(() => {
    if (aiResult) {
      const ids = new Set<number>(aiResult.recommendedCourses.map((c: RoadmapRecommendation) => c.courseId))
      setAiRecommendedNodes(ids)
    }
  }, [aiResult, setAiRecommendedNodes])

  return (
    <div className="absolute top-6 left-6 z-20 pointer-events-none max-w-[320px] h-[calc(100vh-100px)] flex flex-col gap-4 animate-in fade-in slide-in-from-left-4 duration-700">
      <div className="bg-white border border-clay-border p-6 shadow-sm overflow-y-auto custom-scrollbar pointer-events-auto">
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
            <span className="text-[10px] text-clay-text-muted">Môn học hiện tại</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="h-3 w-3 shrink-0 border border-clay-primary bg-clay-primary/10" />
            <span className="text-[10px] text-clay-text-muted">Môn học đang chọn</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-6 border-t border-dashed border-slate-300" />
            <span className="text-[10px] text-clay-text-muted">Mối quan hệ tiên quyết</span>
          </div>
        </div>

        {/* AI Roadmap Generator */}
        <div className="mb-6 space-y-4">
          <div className="flex items-center gap-2">
            <Sparkles className="w-4 h-4 text-clay-primary" />
            <span className="text-[11px] font-bold uppercase tracking-widest text-clay-text">AI Roadmap Generator</span>
          </div>
          
          <div className="space-y-2">
            <label className="text-[10px] font-bold text-clay-text-muted uppercase">Bạn muốn học gì?</label>
            <input 
              value={goals}
              onChange={(e) => setGoals(e.target.value)}
              placeholder="Vd: React, Spring Boot, AI..."
              className="w-full px-3 py-2 bg-clay-surface border border-clay-border text-[11px] outline-none focus:border-clay-primary transition-colors"
            />
          </div>

          <div className="space-y-2">
            <label className="text-[10px] font-bold text-clay-text-muted uppercase">Định hướng nghề nghiệp?</label>
            <input 
              value={career}
              onChange={(e) => setCareer(e.target.value)}
              placeholder="Vd: Full-stack Web, Data Engineer..."
              className="w-full px-3 py-2 bg-clay-surface border border-clay-border text-[11px] outline-none focus:border-clay-primary transition-colors"
            />
          </div>

          {/* Error display */}
          {aiError && (
            <div className="p-3 bg-rose-50 border border-rose-200 rounded">
              <p className="text-[10px] text-rose-700 leading-tight">
                {aiError}
              </p>
              <button 
                onClick={() => generateRoadmap({ learningGoals: goals, careerPath: career })}
                className="mt-2 text-[9px] font-bold text-rose-600 underline hover:text-rose-800"
              >
                Thử lại
              </button>
            </div>
          )}

          <button
            onClick={() => {
              setAiError(null)
              generateRoadmap({ learningGoals: goals, careerPath: career })
            }}
            disabled={isAiLoading || !goals || !career}
            className="w-full py-2.5 bg-clay-primary text-white text-[10px] font-bold uppercase tracking-widest hover:bg-clay-primary/90 transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
          >
            {isAiLoading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Sparkles className="w-3.5 h-3.5" />}
            Tạo lộ trình cá nhân
          </button>
        </div>

        {/* AI Results */}
        {aiResult && (
          <div className="mb-6 animate-in fade-in slide-in-from-top-2 duration-500">
            <div className="p-4 bg-clay-primary/5 border border-clay-primary/20 rounded-lg mb-4">
              <p className="text-[11px] text-clay-text leading-relaxed italic">
                "{aiResult.summary}"
              </p>
            </div>
            
            <div className="space-y-2">
              <span className="text-[10px] font-bold text-clay-text-muted uppercase mb-2 block">Môn học đề xuất:</span>
              {aiResult.recommendedCourses.map((rc: RoadmapRecommendation) => (
                <div 
                  key={rc.courseId}
                  className="p-3 bg-white border border-clay-border hover:border-clay-primary transition-all group"
                >
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-[10px] font-bold text-clay-primary">{rc.courseCode}</span>
                    <button 
                      onClick={() => navigate(`/courses/${rc.courseId}`)}
                      className="text-[9px] text-clay-text-muted hover:text-clay-primary"
                    >
                      Chi tiết <ChevronRight className="w-2.5 h-2.5 inline" />
                    </button>
                  </div>
                  <h4 className="text-[11px] font-bold text-clay-text mb-1">{rc.courseName}</h4>
                  <p className="text-[10px] text-clay-text-muted leading-tight">{rc.reasoning}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        <div className="space-y-3 pt-6 border-t border-clay-border">
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
