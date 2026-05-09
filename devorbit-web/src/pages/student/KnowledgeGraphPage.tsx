import { useRef } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import ForceGraph2D from 'react-force-graph-2d'
import { useKnowledgeGraph } from '../../hooks/useKnowledgeGraph'

export function KnowledgeGraphPage() {
  const fgRef = useRef<any>(null)
  const navigate = useNavigate()
  const { data, isLoading, error } = useKnowledgeGraph()

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
            <p className="text-[12px] font-black text-emerald-500 tracking-[0.4em] uppercase mb-2">Architecting</p>
            <p className="heading-3 text-clay-text animate-pulse tracking-widest font-bold">NEURAL NETWORK</p>
          </div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh', color: '#94a3b8' }}>
        <h2 className="text-clay-text">Failed to load knowledge graph</h2>
        <p className="text-ink-secondary">{error instanceof Error ? error.message : 'An unexpected error occurred'}</p>
        <button onClick={() => window.location.reload()} style={{ marginTop: '16px', padding: '8px 24px', cursor: 'pointer' }}>
          Retry
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
            <span className="text-[10px] font-black uppercase tracking-[0.2em] text-emerald-500">Live Knowledge Matrix</span>
          </div>

          <h1 className="display-sm text-white mb-4 leading-tight">Academic Ecosystem</h1>
          <p className="body-sm text-ink-secondary mb-8 leading-relaxed">
            A dynamic, interactive visualization of the UIT course network. Analyze dependencies and synchronize your learning path across the academic landscape.
          </p>

          <div className="grid grid-cols-2 gap-4 mb-8">
            <div className="space-y-3">
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-cyan-500 shadow-[0_0_8px_rgba(34,211,238,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Root Level</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Secondary</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-indigo-500 shadow-[0_0_8px_rgba(99,102,241,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Intermediate</span>
              </div>
            </div>
            <div className="space-y-3">
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-violet-500 shadow-[0_0_8px_rgba(139,92,246,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Advanced</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-amber-500 shadow-[0_0_8px_rgba(245,158,11,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Specialized</span>
              </div>
              <div className="flex items-center gap-3">
                <div className="h-1.5 w-4 rounded-full bg-rose-500 shadow-[0_0_8px_rgba(244,63,94,0.8)]" />
                <span className="text-[9px] font-black uppercase tracking-widest text-ink-muted">Deep Nodes</span>
              </div>
            </div>
          </div>

          <div className="space-y-4 pt-6 border-t border-white/5">
            <div className="flex items-center gap-4 group/legend">
              <div className="h-1 w-10 bg-emerald-500/30 rounded-full group-hover/legend:bg-emerald-500 transition-colors" />
              <div className="flex flex-col">
                <span className="text-[10px] font-black uppercase tracking-widest text-emerald-500/80">Prerequisite</span>
                <span className="text-[8px] text-ink-muted font-medium tracking-wide">Mandatory dependency path</span>
              </div>
            </div>
            <div className="flex items-center gap-4 group/legend">
              <div className="h-0.5 w-10 border-t border-dashed border-white/30" />
              <div className="flex flex-col">
                <span className="text-[10px] font-black uppercase tracking-widest text-ink-muted">Complementary</span>
                <span className="text-[8px] text-ink-muted font-medium tracking-wide">Recommended sync node</span>
              </div>
            </div>
          </div>

          <div className="mt-10 flex gap-4 pointer-events-auto">
            <Link to="/courses" className="btn-primary text-[11px] px-6 py-2.5 uppercase tracking-[0.2em]">
              Catalog View
            </Link>
          </div>
        </div>
      </div>

      <div className="absolute bottom-8 right-8 z-20 flex flex-col items-end gap-3 pointer-events-none animate-in fade-in slide-in-from-right-8 duration-1000">
        <div className="glass-card px-5 py-3 text-[10px] font-black uppercase tracking-[0.2em] text-ink-muted border-glass-border backdrop-blur-md">
          Scroll: Zoom • Drag: Move • Click: View Node
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

          // Use the pre-calculated topological level (computed server-side)
          const level = node.level || 0;

          let nodeColor = '#10b981'; // Default Emerald
          let glowColor = 'rgba(16, 185, 129, 0.4)';
          let glowColorMuted = 'rgba(16, 185, 129, 0.1)';

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

          // Draw outer glow
          const gradient = ctx.createRadialGradient(node.x, node.y, 0, node.x, node.y, node.val + 2);
          gradient.addColorStop(0, glowColor);
          gradient.addColorStop(0.5, glowColorMuted);
          gradient.addColorStop(1, 'rgba(16, 185, 129, 0)');

          ctx.beginPath();
          ctx.arc(node.x, node.y, node.val + 2, 0, 2 * Math.PI, false);
          ctx.fillStyle = gradient;
          ctx.fill();

          // Draw main node
          ctx.beginPath();
          ctx.arc(node.x, node.y, node.val / 2, 0, 2 * Math.PI, false);
          ctx.fillStyle = nodeColor;
          ctx.fill();

          // Draw border
          ctx.strokeStyle = nodeColor;
          ctx.lineWidth = 2 / globalScale;
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
        linkColor={(link: any) => link.type === 'PREREQUISITE' ? 'rgba(16, 185, 129, 0.6)' : 'rgba(99, 102, 241, 0.5)'}
        linkDirectionalArrowLength={4}
        linkDirectionalArrowRelPos={1}
        linkDirectionalParticles={4}
        linkDirectionalParticleSpeed={0.015}
        linkDirectionalParticleWidth={2.5}
        linkDirectionalParticleColor={(link: any) => link.type === 'PREREQUISITE' ? '#10b981' : '#6366f1'}
        linkCurvature={0.1}
        linkLineDash={(link: any) => link.type === 'COMPLEMENTARY' ? [6, 4] : []}
        onNodeClick={(node: any) => navigate(`/courses/${node.id}`)}
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
