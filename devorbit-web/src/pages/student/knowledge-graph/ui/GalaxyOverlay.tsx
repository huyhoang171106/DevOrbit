import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Activity, Play, RotateCcw, AlertTriangle, Sparkles, Loader2, GraduationCap, Building2, FileText, ChevronDown, ChevronRight, Circle, CheckCircle2 } from 'lucide-react'
import { useGalaxyStore } from '../store/useGalaxyStore'
import { useAiRoadmap, type RoadmapRecommendation, type GraduationTrack, type ElectivePool, type ElectiveCandidate } from '../../../../hooks/useAiRoadmap'

export function GalaxyOverlay() {
  const navigate = useNavigate()
  const isSimulationMode = useGalaxyStore((s) => s.isSimulationMode)
  const failedNodes = useGalaxyStore((s) => s.failedNodes)
  const blockedNodes = useGalaxyStore((s) => s.blockedNodes)
  const toggleSimulation = useGalaxyStore((s) => s.toggleSimulation)
  const resetFailedNodes = useGalaxyStore((s) => s.resetFailedNodes)
  const setAiRecommendedNodes = useGalaxyStore((s) => s.setAiRecommendedNodes)
  const setAiElectiveNodes = useGalaxyStore((s) => s.setAiElectiveNodes)
  const setAiElectiveNodeData = useGalaxyStore((s) => s.setAiElectiveNodeData)
  const selectedGraduationTrack = useGalaxyStore((s) => s.selectedGraduationTrack)
  const setSelectedGraduationTrack = useGalaxyStore((s) => s.setSelectedGraduationTrack)

  // AI Roadmap State
  const [goals, setGoals] = useState('')
  const [career, setCareer] = useState('')
  const [aiError, setAiError] = useState<string | null>(null)
  const { mutate: generateRoadmap, data: aiResult, isPending: isAiLoading, error: aiMutationError } = useAiRoadmap()

  // Elective pool selection state
  const [selectedElectiveCodes, setSelectedElectiveCodes] = useState<Set<string>>(new Set())
  const [expandedPools, setExpandedPools] = useState<Set<string>>(new Set(['co-so-nganh', 'chuyen-nganh']))

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
      const mandatoryIds = new Set<number>(
        aiResult.recommendedCourses.filter((c: RoadmapRecommendation) => c.isMandatory).map((c: RoadmapRecommendation) => c.courseId)
      )
      const electiveIds = new Set<number>(
        aiResult.recommendedCourses.filter((c: RoadmapRecommendation) => !c.isMandatory).map((c: RoadmapRecommendation) => c.courseId)
      )
      const electiveData = aiResult.recommendedCourses
        .filter((c: RoadmapRecommendation) => !c.isMandatory)
        .map((c: RoadmapRecommendation) => ({
          id: c.courseId,
          code: c.courseCode,
          name: c.courseName,
          semester: c.semester,
          description: c.description,
        }))
      setAiRecommendedNodes(mandatoryIds)
      setAiElectiveNodes(electiveIds)
      setAiElectiveNodeData(electiveData)
      // Initialize elective selection from backend suggestions
      if (aiResult.electivePools) {
        const preSelected = new Set<string>()
        aiResult.electivePools.forEach(pool => {
          pool.candidates.forEach(c => {
            if (c.isSelected) preSelected.add(c.courseCode)
          })
        })
        setSelectedElectiveCodes(preSelected)
        setExpandedPools(new Set(['co-so-nganh', 'chuyen-nganh']))
      }
      // Auto-select recommended graduation track
      const recommended = aiResult.graduationTracks?.find((t: GraduationTrack) => t.recommended)
      if (recommended) {
        setSelectedGraduationTrack(recommended.type)
      }
    }
  }, [aiResult, setAiRecommendedNodes, setAiElectiveNodes, setAiElectiveNodeData, setSelectedGraduationTrack])

  // Toggle elective course selection
  const toggleElective = (courseCode: string) => {
    setSelectedElectiveCodes(prev => {
      const next = new Set(prev)
      if (next.has(courseCode)) {
        next.delete(courseCode)
      } else {
        next.add(courseCode)
      }
      return next
    })
  }

  // Sync elective selection to graph store
  useEffect(() => {
    if (!aiResult?.electivePools) return
    // Find all course IDs matching the selected codes
    const allCandidates = aiResult.electivePools.flatMap(p => p.candidates)
    const selectedCourses = allCandidates
      .filter(c => selectedElectiveCodes.has(c.courseCode))
    const selectedNodeIds = new Set(selectedCourses.map(c => c.courseId))
    // Get semester from recommendedCourses (has semester info) instead of ElectiveCandidate
    const recByCode = new Map(aiResult.recommendedCourses.map((r: RoadmapRecommendation) => [r.courseCode, r]))
    const electiveNodeData = selectedCourses
      .map(c => {
        const rec = recByCode.get(c.courseCode)
        return {
          id: c.courseId,
          code: c.courseCode,
          name: c.courseName,
          semester: c.semester || rec?.semester || 4,
          description: c.description || '',
        }
      })
    setAiElectiveNodes(selectedNodeIds)
    setAiElectiveNodeData(electiveNodeData)
  }, [selectedElectiveCodes, aiResult, setAiElectiveNodes, setAiElectiveNodeData])

  // Collapse/expand a pool
  const togglePool = (poolId: string) => {
    setExpandedPools(prev => {
      const next = new Set(prev)
      if (next.has(poolId)) next.delete(poolId)
      else next.add(poolId)
      return next
    })
  }

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
            <label className="text-[10px] font-bold text-clay-text-muted uppercase">Định hướng nghề nghiệp</label>
            <div className="grid grid-cols-2 gap-1.5">
              {[
                { id: 'backend', label: 'Backend Developer', icon: '🖥️', goal: 'API, database, server-side' },
                { id: 'frontend', label: 'Frontend Developer', icon: '🎨', goal: 'UI, UX, responsive design' },
                { id: 'fullstack', label: 'Fullstack Developer', icon: '⚡', goal: 'React, Spring Boot, REST' },
                { id: 'mobile', label: 'Mobile Developer', icon: '📱', goal: 'Android, Kotlin, mobile UI' },
                { id: 'ai', label: 'AI Engineer', icon: '🤖', goal: 'Machine Learning, Data Science' },
                { id: 'game', label: 'Game Developer', icon: '🎮', goal: 'Unity, game design, graphics' },
                { id: 'devops', label: 'DevOps Engineer', icon: '🐳', goal: 'Docker, CI/CD, cloud' },
                { id: 'security', label: 'Security Engineer', icon: '🔒', goal: 'Network security, pentest' },
                { id: 'data', label: 'Data Engineer', icon: '📊', goal: 'Big data, ETL, data pipeline' },
              ].map(p => {
                const selected = career === p.label
                return (
                  <button
                    key={p.id}
                    onClick={() => {
                      setCareer(p.label)
                      setGoals(p.goal)
                      setAiError(null)
                      generateRoadmap({ learningGoals: p.goal, careerPath: p.label })
                    }}
                    disabled={isAiLoading}
                    className={`flex items-center gap-1.5 px-2 py-2 rounded-lg border text-left transition-all ${
                      selected
                        ? 'border-clay-primary bg-clay-primary/10'
                        : 'border-clay-border/60 bg-white hover:border-clay-primary/40 hover:bg-clay-surface/50'
                    } disabled:opacity-50`}
                  >
                    <span className="text-[14px]">{p.icon}</span>
                    <span className={`text-[9px] font-semibold leading-tight ${
                      selected ? 'text-clay-primary' : 'text-clay-text'
                    }`}>
                      {p.label}
                    </span>
                  </button>
                )
              })}
            </div>
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

          {/* Elective Pool Selector */}
          {aiResult?.electivePools && aiResult.electivePools.length > 0 && (
            <div className="mt-5 pt-5 border-t border-clay-border/50">
              <div className="flex items-center gap-2 mb-3">
                <span className="text-[10px] font-bold uppercase tracking-widest text-clay-text">
                  Tự chọn môn học
                </span>
                <span className="text-[7px] text-clay-text-muted bg-clay-surface px-1.5 py-0.5 rounded-full">
                  {selectedElectiveCodes.size} môn
                </span>
              </div>
              <div className="space-y-2">
                {aiResult.electivePools.map((pool: ElectivePool) => {
                  const isExpanded = expandedPools.has(pool.poolId)
                  const poolTC = pool.candidates
                    .filter(c => selectedElectiveCodes.has(c.courseCode))
                    .reduce((sum, c) => sum + c.credits, 0)
                  const overBudget = poolTC > pool.targetTC + 2
                  return (
                    <div key={pool.poolId} className="border border-clay-border/50 rounded-lg overflow-hidden">
                      {/* Pool Header */}
                      <button
                        onClick={() => togglePool(pool.poolId)}
                        className="w-full flex items-center justify-between px-2 py-2 hover:bg-clay-surface/50 transition-colors text-left"
                      >
                        <div className="flex items-center gap-1.5 min-w-0">
                          {isExpanded ? <ChevronDown className="w-3 h-3 text-clay-text-muted shrink-0" /> : <ChevronRight className="w-3 h-3 text-clay-text-muted shrink-0" />}
                          <span className="text-[9px] font-semibold text-clay-text truncate">{pool.poolName}</span>
                        </div>
                        <div className="flex items-center gap-1 shrink-0">
                          <span className={`text-[8px] font-bold ${overBudget ? 'text-red-500' : 'text-emerald-600'}`}>
                            {poolTC}
                          </span>
                          <span className="text-[7px] text-clay-text-muted">/{pool.targetTC}TC</span>
                        </div>
                      </button>
                      {/* Pool Candidates */}
                      {isExpanded && (
                        <div className="max-h-[200px] overflow-y-auto custom-scrollbar divide-y divide-clay-border/30">
                          {pool.candidates
                            .sort((a, b) => (b.isSelected ? 1 : 0) - (a.isSelected ? 1 : 0) || b.score - a.score)
                            .map((candidate: ElectiveCandidate) => {
                            const isSel = selectedElectiveCodes.has(candidate.courseCode)
                            return (
                              <button
                                key={candidate.courseCode}
                                onClick={() => toggleElective(candidate.courseCode)}
                                className={`w-full flex items-center gap-2 px-2 py-1.5 transition-colors text-left ${
                                  isSel ? 'bg-violet-50/50' : 'hover:bg-clay-surface/30'
                                }`}
                              >
                                {isSel ? (
                                  <CheckCircle2 className="w-3 h-3 text-violet-600 shrink-0" />
                                ) : (
                                  <Circle className="w-3 h-3 text-clay-text-muted/40 shrink-0" />
                                )}
                                <span className="text-[8px] font-medium text-clay-text-muted w-[42px] shrink-0">{candidate.courseCode}</span>
                                <span className="text-[9px] text-clay-text min-w-0 flex-1 truncate">{candidate.courseName}</span>
                                <span className="text-[7px] text-clay-text-muted/60 shrink-0 w-[22px] text-right">{candidate.credits}TC</span>
                                {candidate.score > 0 && (
                                  <span className="text-[7px] text-clay-text-muted/40 shrink-0 ml-0.5">{candidate.score}</span>
                                )}
                              </button>
                            )
                          })}
                        </div>
                      )}
                    </div>
                  )
                })}
              </div>
            </div>
          )}

          <button
            onClick={() => {
              setAiError(null)
              generateRoadmap({ learningGoals: goals || 'Lập trình', careerPath: career || 'Backend Developer' })
            }}
            disabled={isAiLoading}
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

            <div className="space-y-1.5">
              <span className="text-[10px] font-bold text-clay-text-muted uppercase mb-2 block">Môn học đề xuất:</span>
              <div className="divide-y divide-clay-border/50 max-h-[280px] overflow-y-auto custom-scrollbar -mx-1 px-1">
                {aiResult.recommendedCourses.filter(rc => !rc.isMandatory).map((rc: RoadmapRecommendation) => (
                  <div
                    key={rc.courseId}
                    className="flex items-center gap-2 py-1.5"
                  >
                    <span className="text-[10px] font-semibold text-clay-primary shrink-0 w-[48px]">{rc.courseCode}</span>
                    <span className="text-[10px] text-clay-text leading-tight flex-1 truncate">{rc.courseName}</span>
                    <span className="text-[7px] text-clay-text-muted/60 shrink-0 w-[22px] text-right">{rc.credits}TC</span>
                    <span className={`text-[8px] font-bold uppercase px-1.5 py-0.5 rounded shrink-0 ${
                      rc.isMandatory
                        ? 'bg-slate-100 text-slate-500'
                        : 'bg-violet-100 text-violet-600'
                    }`}>
                      {rc.isMandatory ? 'BẮT BUỘC' : 'TỰ CHỌN'}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {/* Semester credit breakdown */}
            {aiResult.recommendedCourses && aiResult.recommendedCourses.length > 0 && (() => {
              const semesterData: Record<number, { total: number; mandatory: number; elective: number }> = {}
              aiResult.recommendedCourses.forEach((rc: RoadmapRecommendation) => {
                const s = rc.semester || 1
                if (!semesterData[s]) semesterData[s] = { total: 0, mandatory: 0, elective: 0 }
                semesterData[s].total += rc.credits
                if (rc.isMandatory) semesterData[s].mandatory += rc.credits
                else semesterData[s].elective += rc.credits
              })
              const sortedSemesters = Object.entries(semesterData).sort(([a], [b]) => Number(a) - Number(b))
              return (
                <div className="mt-4 mb-3">
                  <div className="flex items-center gap-2 mb-2">
                    <span className="text-[9px] font-bold uppercase tracking-widest text-clay-text-muted">
                      Phân bổ tín chỉ
                    </span>
                  </div>
                  <div className="flex gap-2 overflow-x-auto pb-1 custom-scrollbar">
                    {sortedSemesters.map(([sem, data]) => {
                      const isBalanced = data.total >= 18 && data.total <= 22
                      return (
                        <div key={sem} className={`shrink-0 w-[52px] p-1.5 rounded-lg border text-center ${
                          isBalanced
                            ? 'border-emerald-200 bg-emerald-50/50'
                            : data.total > 22
                            ? 'border-amber-200 bg-amber-50/50'
                            : 'border-slate-200 bg-slate-50/50'
                        }`}>
                          <div className="text-[8px] font-bold text-clay-text-muted">HK{sem}</div>
                          <div className={`text-[12px] font-bold ${
                            isBalanced ? 'text-emerald-600' : 'text-clay-text'
                          }`}>
                            {data.total}
                          </div>
                          <div className="text-[6px] text-clay-text-muted leading-tight">
                            {data.mandatory}BB+{data.elective}TC
                          </div>
                          <div className="flex gap-0.5 mt-1 justify-center">
                            {Array.from({ length: Math.min(Math.ceil(data.total / 4), 6) }).map((_, i) => (
                              <div key={i} className={`w-1 h-2 rounded-sm ${
                                isBalanced ? 'bg-emerald-300' : 'bg-slate-300'
                              }`} />
                            ))}
                          </div>
                        </div>
                      )
                    })}
                  </div>
                </div>
              )
            })()}

            {/* Graduation Track Selector */}
            {aiResult.graduationTracks && aiResult.graduationTracks.length > 0 && (
              <div className="mt-5 pt-5 border-t border-clay-border/50">
                <div className="flex items-center gap-2 mb-3">
                  <GraduationCap className="w-4 h-4 text-emerald-600" />
                  <span className="text-[10px] font-bold uppercase tracking-widest text-clay-text">
                    Hướng tốt nghiệp
                  </span>
                </div>
                <div className="space-y-2">
                  {aiResult.graduationTracks.map((track: GraduationTrack) => {
                    const isSelected = selectedGraduationTrack === track.type
                    const TrackIcon = track.type === 'THESIS' ? FileText
                      : track.type === 'COMPANY' ? Building2
                      : GraduationCap
                    return (
                      <button
                        key={track.type}
                        onClick={() => setSelectedGraduationTrack(
                          isSelected ? null : track.type
                        )}
                        className={`w-full text-left p-2.5 rounded-lg border transition-all duration-200 ${
                          isSelected
                            ? 'border-emerald-500 bg-emerald-50 shadow-sm'
                            : track.recommended && !selectedGraduationTrack
                            ? 'border-emerald-300 bg-emerald-50/50'
                            : 'border-clay-border bg-white hover:border-clay-primary/40'
                        }`}
                      >
                        <div className="flex items-start gap-2">
                          <div className={`p-1.5 rounded-full shrink-0 ${
                            isSelected ? 'bg-emerald-100 text-emerald-600'
                            : track.recommended && !selectedGraduationTrack ? 'bg-emerald-50 text-emerald-500'
                            : 'bg-clay-surface text-clay-text-muted'
                          }`}>
                            <TrackIcon className="w-3 h-3" />
                          </div>
                          <div className="min-w-0 flex-1">
                            <div className="flex items-center gap-1.5">
                              <span className={`text-[10px] font-bold leading-tight ${
                                isSelected ? 'text-emerald-800' : 'text-clay-text'
                              }`}>
                                {track.name}
                              </span>
                              <span className="text-[8px] font-bold text-white bg-emerald-500 px-1.5 py-0.5 rounded">
                                {track.credits}TC
                              </span>
                              {track.recommended && !selectedGraduationTrack && (
                                <span className="text-[8px] font-bold text-emerald-600 bg-emerald-100 px-1.5 py-0.5 rounded">
                                  ĐỀ XUẤT
                                </span>
                              )}
                              {isSelected && (
                                <span className="text-[8px] font-bold text-emerald-600">
                                  ✓ ĐÃ CHỌN
                                </span>
                              )}
                            </div>
                            <p className="text-[9px] text-clay-text-muted mt-0.5 leading-tight line-clamp-2">
                              {track.description}
                            </p>
                            <p className="text-[9px] text-clay-text-muted/70 mt-0.5">
                              Yêu cầu: {track.requirements}
                            </p>
                            {isSelected && track.recommendation && (
                              <p className="text-[9px] text-emerald-700 mt-1 leading-tight">
                                {track.recommendation}
                              </p>
                            )}
                          </div>
                        </div>
                      </button>
                    )
                  })}
                </div>
              </div>
            )}
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
