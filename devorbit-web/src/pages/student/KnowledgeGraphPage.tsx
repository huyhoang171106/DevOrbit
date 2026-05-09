import { useRef, useState, useMemo } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import ForceGraph2D from 'react-force-graph-2d'
import { useKnowledgeGraph } from '../../hooks/useKnowledgeGraph'
import { Activity, Play, RotateCcw, AlertTriangle } from 'lucide-react'

export function KnowledgeGraphPage() {
  const fgRef = useRef<any>(null)
  const navigate = useNavigate()
  const { data, isLoading, error } = useKnowledgeGraph()

  // --- Simulation State ---
  const [isSimulationMode, setIsSimulationMode] = useState(false)
  const [failedNodes, setFailedNodes] = useState<Set<number>>(new Set())

  // --- Recursive Blocking Logic ---
  const blockedNodes = useMemo(() => {
    if (!data || failedNodes.size === 0) return new Set<number>()

    const blocked = new Set<number>(failedNodes)
    const queue = Array.from(failedNodes)

    while (queue.length > 0) {
      const currentId = queue.shift()!
      // Find all courses that have 'currentId' as a prerequisite
      const downstream = data.links
        .filter(link => link.source === currentId && link.type === 'PREREQUISITE')
        .map(link => link.target)

      for (const targetId of downstream) {
        if (!blocked.has(targetId)) {
          blocked.add(targetId)
          queue.push(targetId)
        }
      }
    }
    return blocked
  }, [data, failedNodes])

  // --- Semester Delay Estimation ---
  const delayEstimate = useMemo(() => {
    if (!data || blockedNodes.size === 0) return 0
    
    // Find the max level difference among blocked nodes
    const blockedNodeList = data.nodes.filter(n => blockedNodes.has(n.id))
    const maxLevel = Math.max(...blockedNodeList.map(n => n.level))
    const minLevel = Math.min(...blockedNodeList.map(n => n.level))
    
    // Roughly estimate: delay is the number of semester levels affected
    return Math.max(1, Math.floor((maxLevel - minLevel) / 1.5) + 1)
  }, [data, blockedNodes])

  if (isLoading) {
    return (
      <div className="relative h-[80vh] flex items-center justify-center overflow-hidden">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 h-[600px] w-[600px] bg-emerald-500/[0.05] blur-[150px] rounded-full animate-pulse" />
        <div className="relative flex flex-col items-center gap-6">
          <div className="relative h-20 w-20">
            <div className="absolute inset-0 rounded-full border-4 border-emerald-500/10" />
            <div className="absolute inset-0 rounded-full border-t-4 border-emerald-500 animate-spin shadow-[0_0_20px_rgba(16,185,129,0.4)]" />
          </div>
          <div className="flex flex-col items-center">
            <p className="text-[12px] font-black text-emerald-500 tracking-[0.4em] uppercase mb-2">Đang thiết lập</p>
            <p className="heading-3 text-clay-text animate-pulse tracking-widest font-bold">MẠNG LƯỚI KIẾN THỨC</p>
          </div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh', color: '#94a3b8' }}>
        <h2 className="text-clay-text">Không thể tải sơ đồ kiến thức</h2>
        <p className="text-ink-secondary">{error instanceof Error ? error.message : 'Đã xảy ra lỗi không xác định'}</p>
        <button onClick={() => window.location.reload()} style={{ marginTop: '16px', padding: '8px 24px', cursor: 'pointer' }}>
          Thử lại
        </button>
      </div>
    )
  }

  const graphData = data ?? { nodes: [], links: [] }

  return (
    <div className="relative w-full h-[calc(100vh-80px)] bg-[#05070a] overflow-hidden">
      {/* Immersive background texture */}
      <div className="absolute inset-0 opacity-[0.03] pointer-events-none"
        style={{ backgroundImage: 'radial-gradient(#10b981 1px, transparent 1px)', backgroundSize: '40px 40px' }} />

      {/* Overlay UI */}
      <div className="absolute top-8 left-8 z-20 pointer-events-none max-w-md animate-in fade-in slide-in-from-left-8 duration-1000">
        <div className="glass-card-glow p-8 border-emerald-500/20 backdrop-blur-2xl">
          <div className="inline-flex items-center gap-3 px-3 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/20 mb-6">
            <div className="h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
            <span className="text-[10px] font-black uppercase tracking-[0.2em] text-emerald-500">Ma trận kiến thức trực tuyến</span>
          </div>

          <h1 className="display-sm text-white mb-4 leading-tight">Hệ sinh thái học thuật</h1>
          <p className="body-sm text-ink-secondary mb-8 leading-relaxed">
            Một hình ảnh trực quan, sống động về mạng lưới các môn học tại UIT. Phân tích các mối liên hệ và đồng bộ hóa lộ trình học tập của bạn.
          </p>

          <div className="grid grid-cols-2 gap-4 mb-8">
            <div className="space-y-3">
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-cyan-500 shadow-[0_0_8px_rgba(34,211,238,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Cơ sở</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Cơ sở ngành</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-indigo-500 shadow-[0_0_8px_rgba(99,102,241,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Trung cấp</span>
              </div>
            </div>
            <div className="space-y-3">
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-violet-500 shadow-[0_0_8px_rgba(139,92,246,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Nâng cao</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-amber-500 shadow-[0_0_8px_rgba(245,158,11,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Chuyên ngành</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-rose-500 shadow-[0_0_8px_rgba(244,63,94,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Chuyên sâu</span>
              </div>
            </div>
          </div>

          <div className="space-y-4 pt-6 border-t border-white/5">
            <div className="flex items-center gap-4 group/legend">
              <div className="h-1 w-10 bg-emerald-500/30 rounded-full group-hover/legend:bg-emerald-500 transition-colors" />
              <div className="flex flex-col">
                <span className="text-[10px] font-black uppercase tracking-widest text-emerald-500/80">Môn tiên quyết</span>
                <span className="text-[8px] text-ink-muted font-medium tracking-wide">Mối liên hệ bắt buộc</span>
              </div>
            </div>
            <div className="flex items-center gap-4 group/legend">
              <div className="h-0.5 w-10 border-t border-dashed border-white/30" />
              <div className="flex flex-col">
                <span className="text-[10px] font-black uppercase tracking-widest text-ink-muted">Bổ sung</span>
                <span className="text-[8px] text-ink-muted font-medium tracking-wide">Được khuyến nghị</span>
              </div>
            </div>
          </div>

          <div className="mt-10 flex flex-col gap-4 pointer-events-auto">
            <button 
              onClick={() => {
                setIsSimulationMode(!isSimulationMode)
                if (isSimulationMode) setFailedNodes(new Set())
              }}
              className={`flex items-center justify-center gap-3 w-full px-6 py-3 rounded-xl font-black uppercase tracking-[0.2em] text-[11px] transition-all duration-500 border ${
                isSimulationMode 
                  ? 'bg-rose-500 border-rose-400 text-white shadow-[0_0_30px_rgba(244,63,94,0.4)]' 
                  : 'bg-emerald-500 border-emerald-400 text-white shadow-[0_0_30px_rgba(16,185,129,0.4)]'
              }`}
            >
              {isSimulationMode ? <Activity className="w-4 h-4" /> : <Play className="w-4 h-4" />}
              {isSimulationMode ? 'Thoát giả lập' : 'Bật chế độ giả lập'}
            </button>
            
            {isSimulationMode && (
              <div className="space-y-4 animate-in fade-in slide-in-from-top-4 duration-500">
                <div className="p-5 rounded-2xl bg-white/[0.03] border border-white/5 backdrop-blur-sm">
                  <div className="flex items-center justify-between mb-4">
                    <span className="text-[10px] font-black uppercase tracking-widest text-ink-muted">Chỉ số giả lập</span>
                    <button 
                      onClick={() => setFailedNodes(new Set())}
                      className="text-ink-muted hover:text-white transition-colors"
                    >
                      <RotateCcw className="w-3 h-3" />
                    </button>
                  </div>
                  
                  <div className="grid grid-cols-2 gap-4">
                    <div className="flex flex-col gap-1">
                      <span className="text-[20px] font-bold text-white">{blockedNodes.size}</span>
                      <span className="text-[8px] font-black uppercase tracking-widest text-ink-muted">Môn học bị ảnh hưởng</span>
                    </div>
                    <div className="flex flex-col gap-1">
                      <div className="flex items-center gap-2">
                        <span className="text-[20px] font-bold text-rose-500">+{delayEstimate}</span>
                        <span className="text-[10px] text-rose-500 font-bold uppercase">Kỳ</span>
                      </div>
                      <span className="text-[8px] font-black uppercase tracking-widest text-ink-muted">Độ trễ tốt nghiệp</span>
                    </div>
                  </div>
                </div>

                {failedNodes.size > 0 && (
                  <div className="flex items-start gap-3 p-4 rounded-xl bg-rose-500/10 border border-rose-500/20">
                    <AlertTriangle className="w-4 h-4 text-rose-500 shrink-0 mt-0.5" />
                    <p className="text-[10px] text-rose-200 leading-relaxed font-medium">
                      Rủi ro cao. Mô hình thất bại hiện tại làm gián đoạn các môn tiên quyết quan trọng cho việc tốt nghiệp.
                    </p>
                  </div>
                )}

                <p className="text-[9px] text-ink-muted italic text-center px-4">
                  Nhấn vào bất kỳ môn học nào để giả định việc không hoàn thành và xem mức độ ảnh hưởng lan truyền.
                </p>
              </div>
            )}

            {!isSimulationMode && (
              <Link to="/courses" className="btn-secondary text-[11px] px-6 py-2.5 uppercase tracking-[0.2em] text-center">
                Xem danh mục
              </Link>
            )}
          </div>
        </div>
      </div>

      <div className="absolute bottom-8 right-8 z-20 flex flex-col items-end gap-3 pointer-events-none animate-in fade-in slide-in-from-right-8 duration-1000">
        <div className="glass-card px-5 py-3 text-[10px] font-black uppercase tracking-[0.2em] text-ink-muted border-glass-border backdrop-blur-md">
          Cuộn: Phóng to • Kéo: Di chuyển • Nhấn: Xem môn học
        </div>
      </div>

      <ForceGraph2D
        ref={fgRef}
        graphData={graphData}
        backgroundColor="transparent"
        nodeLabel={(node: any) => `
          <div style="background: rgba(13, 18, 37, 0.95); border: 1px solid rgba(16, 185, 129, 0.3); border-radius: 12px; padding: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.5); backdrop-filter: blur(10px);">
            <div style="color: #10b981; font-weight: 900; font-size: 10px; margin-bottom: 4px; letter-spacing: 0.1em; text-transform: uppercase;">${node.code}</div>
            <div style="color: #fff; font-weight: 700; font-size: 13px;">${node.name}</div>
          </div>
        `}
        nodeColor={() => '#10b981'}
        nodeRelSize={1}
        nodeCanvasObject={(node: any, ctx, globalScale) => {
          const label = node.code;
          const fontSize = 12 / globalScale;
          ctx.font = `${fontSize}px JetBrains Mono, monospace`;

          // Safety check for finite values to prevent Canvas errors
          if (!Number.isFinite(node.x) || !Number.isFinite(node.y) || !Number.isFinite(node.val) || node.val <= 0) return;

          const isFailed = failedNodes.has(node.id)
          const isBlocked = blockedNodes.has(node.id) && !isFailed
          
          // --- Risk Heatmap (Impact Score) ---
          const impact = node.impactScore || 0;
          const impactFactor = impact / 10; // 0.0 to 1.0
          
          // Use the pre-calculated topological level (computed server-side)
          const level = node.level || 0;

          let nodeColor = '#10b981'; // Default Emerald
          let glowColor = 'rgba(16, 185, 129, 0.4)';
          let glowColorMuted = 'rgba(16, 185, 129, 0.1)';

          if (isFailed) {
            const pulse = (Math.sin(Date.now() / 200) + 1) / 2; // 0 to 1 pulse
            nodeColor = pulse > 0.5 ? '#f43f5e' : '#e11d48'; // Pulsing Rose
            glowColor = `rgba(244, 63, 94, ${0.4 + pulse * 0.4})`;
            glowColorMuted = `rgba(244, 63, 94, ${0.1 + pulse * 0.1})`;
          } else if (isBlocked) {
            const pulse = (Math.sin(Date.now() / 400) + 1) / 2; // Slower pulse
            nodeColor = '#fb7185';
            glowColor = `rgba(251, 113, 133, ${0.2 + pulse * 0.2})`;
            glowColorMuted = `rgba(251, 113, 133, 0.05)`;
          } else if (isSimulationMode && impact > 7) {
            // Heatmap highlight for critical nodes in simulation mode
            nodeColor = '#f59e0b'; // Amber (Critical Warning)
            glowColor = 'rgba(245, 158, 11, 0.6)';
            glowColorMuted = 'rgba(245, 158, 11, 0.2)';
          } else {
            // Level-based color mapping (Topological Depth)
            switch (level) {
            case 0: // Root nodes (no prerequisites)
              nodeColor = '#22d3ee'; // Cyan
              glowColor = 'rgba(34, 211, 238, 0.4)';
              glowColorMuted = 'rgba(34, 211, 238, 0.1)';
              break;
            case 1: // Depend on level 0
              nodeColor = '#10b981'; // Emerald
              glowColor = 'rgba(16, 185, 129, 0.4)';
              glowColorMuted = 'rgba(16, 185, 129, 0.1)';
              break;
            case 2: // Depend on level 1
              nodeColor = '#6366f1'; // Indigo
              glowColor = 'rgba(99, 102, 241, 0.4)';
              glowColorMuted = 'rgba(99, 102, 241, 0.1)';
              break;
            case 3: // Depend on level 2
              nodeColor = '#8b5cf6'; // Violet
              glowColor = 'rgba(139, 92, 246, 0.4)';
              glowColorMuted = 'rgba(139, 92, 246, 0.1)';
              break;
            case 4: // Depend on level 3
              nodeColor = '#f59e0b'; // Amber
              glowColor = 'rgba(245, 158, 11, 0.4)';
              glowColorMuted = 'rgba(245, 158, 11, 0.1)';
              break;
            default: // Deeply nested specializations
              nodeColor = '#f43f5e'; // Rose
              glowColor = 'rgba(244, 63, 94, 0.4)';
              glowColorMuted = 'rgba(244, 63, 94, 0.1)';
          }
        }

          // Draw outer glow (Scale based on impact in heatmap mode)
          const glowSize = (node.val + 2) + (isSimulationMode ? impactFactor * 6 : 0);
          const gradient = ctx.createRadialGradient(node.x, node.y, 0, node.x, node.y, glowSize);
          gradient.addColorStop(0, glowColor);
          gradient.addColorStop(0.5, glowColorMuted);
          gradient.addColorStop(1, 'rgba(16, 185, 129, 0)');

          ctx.beginPath();
          ctx.arc(node.x, node.y, glowSize, 0, 2 * Math.PI, false);
          ctx.fillStyle = gradient;
          ctx.fill();

          // Draw main node (Larger for higher impact)
          const radius = (node.val / 2) + (isSimulationMode ? impactFactor * 2 : 0);
          ctx.beginPath();
          ctx.arc(node.x, node.y, radius, 0, 2 * Math.PI, false);
          ctx.fillStyle = nodeColor;
          ctx.fill();

          // Draw border
          ctx.strokeStyle = (isSimulationMode && impact > 8) ? '#fff' : nodeColor;
          ctx.lineWidth = (isSimulationMode && impact > 8) ? 3 / globalScale : 2 / globalScale;
          ctx.stroke();

          // Draw label
          if (globalScale > 2) {
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillStyle = 'rgba(255, 255, 255, 0.9)';
            ctx.fillText(label, node.x, node.y + (node.val / 2) + fontSize + 4);
          }
        }}
        linkWidth={1.8}
        linkColor={(link: any) => {
          if (blockedNodes.has(link.source.id) || blockedNodes.has(link.target.id)) {
            return 'rgba(244, 63, 94, 0.6)' // Alert Red
          }
          return link.type === 'PREREQUISITE' ? 'rgba(16, 185, 129, 0.6)' : 'rgba(99, 102, 241, 0.5)'
        }}
        linkDirectionalArrowLength={4}
        linkDirectionalArrowRelPos={1}
        linkDirectionalParticles={4}
        linkDirectionalParticleSpeed={(link: any) => {
          if (blockedNodes.has(link.source.id) || blockedNodes.has(link.target.id)) {
            return 0.04 // High speed "alert" particles
          }
          return 0.015
        }}
        linkDirectionalParticleWidth={2.5}
        linkDirectionalParticleColor={(link: any) => {
          if (blockedNodes.has(link.source.id) || blockedNodes.has(link.target.id)) {
            return '#f43f5e' // Rose-500 Alert
          }
          return link.type === 'PREREQUISITE' ? '#10b981' : '#6366f1'
        }}
        linkCurvature={0.1}
        linkLineDash={(link: any) => link.type === 'COMPLEMENTARY' ? [6, 4] : []}
        onNodeClick={(node: any) => {
          if (isSimulationMode) {
            setFailedNodes(prev => {
              const next = new Set(prev)
              if (next.has(node.id)) next.delete(node.id)
              else next.add(node.id)
              return next
            })
          } else {
            navigate(`/courses/${node.id}`)
          }
        }}
        cooldownTicks={50}
        d3AlphaDecay={0.02}
        d3VelocityDecay={0.4}
        onEngineStop={() => {
          if (graphData.nodes.length > 0 && fgRef.current) {
            fgRef.current.zoomToFit(400, 100)
          }
        }}
      />
    </div>
  )
}
