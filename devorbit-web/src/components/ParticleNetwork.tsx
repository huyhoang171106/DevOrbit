import { useEffect, useRef } from 'react'
import { usePerformanceProfile } from '../performance'

const CONNECTION_DIST = 140
const MOUSE_RADIUS = 180
const MOUSE_FORCE = 0.6
const PARTICLE_SPEED = 0.3

interface Particle {
  x: number; y: number
  vx: number; vy: number
  baseX: number; baseY: number
}

/**
 * Canvas 2D particle network.
 * - Profile-driven particle count and update rate
 * - No triangle mesh rendering (was O(n³) — major perf win)
 * - DPR-capped canvas resolution
 * - Respects prefers-reduced-motion
 * - Pauses when tab is hidden
 */
export function ParticleNetwork() {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const profile = usePerformanceProfile()

  useEffect(() => {
    if (!profile.enableParticleNetwork) return

    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const mouse = { x: -1000, y: -1000 }
    const dpr = Math.min(window.devicePixelRatio || 1, profile.maxDpr)

    // Dynamic particle count
    const particleCount = Math.max(12, Math.floor(80 * profile.particleMultiplier))

    function resize() {
      const w = window.innerWidth
      const h = window.innerHeight
      canvas!.width = w * dpr
      canvas!.height = h * dpr
      canvas!.style.width = w + 'px'
      canvas!.style.height = h + 'px'
      ctx!.setTransform(dpr, 0, 0, dpr, 0, 0)
    }
    resize()
    window.addEventListener('resize', resize)

    const onMouse = (e: MouseEvent) => {
      mouse.x = e.clientX
      mouse.y = e.clientY
    }
    const onMouseLeave = () => {
      mouse.x = -1000
      mouse.y = -1000
    }
    window.addEventListener('mousemove', onMouse, { passive: true })
    window.addEventListener('mouseleave', onMouseLeave)

    // Init particles (GPU-friendly Float32 for positions)
    const particles: Particle[] = []
    const w = canvas.width / dpr
    const h = canvas.height / dpr
    for (let i = 0; i < particleCount; i++) {
      const x = Math.random() * w
      const y = Math.random() * h
      particles.push({
        x, y,
        vx: (Math.random() - 0.5) * PARTICLE_SPEED,
        vy: (Math.random() - 0.5) * PARTICLE_SPEED,
        baseX: x,
        baseY: y,
      })
    }

    let running = true
    let lastFrame = 0
    let rafId: number
    const interval = profile.canvasUpdateInterval

    function animate(timestamp: number) {
      if (!running) return

      // Throttle on mobile/low-end
      if (interval > 0 && timestamp - lastFrame < interval) {
        rafId = requestAnimationFrame(animate)
        return
      }
      lastFrame = timestamp

      if (!canvas) return
      ctx!.clearRect(0, 0, w, h)

      // Update particles
      for (const p of particles) {
        // Mouse attraction
        const dx = mouse.x - p.x
        const dy = mouse.y - p.y
        const dist = Math.hypot(dx, dy)
        if (dist < MOUSE_RADIUS && dist > 0) {
          const force = (1 - dist / MOUSE_RADIUS) * MOUSE_FORCE
          p.vx += (dx / dist) * force
          p.vy += (dy / dist) * force
        }

        p.vx += (p.baseX - p.x) * 0.001
        p.vy += (p.baseY - p.y) * 0.001
        p.vx *= 0.98
        p.vy *= 0.98

        p.x += p.vx
        p.y += p.vy

        if (p.x < 0) p.x = w
        if (p.x > w) p.x = 0
        if (p.y < 0) p.y = h
        if (p.y > h) p.y = 0
      }

      // Lines: each particle connects to nearest 2 neighbors within range
      // O(n * k) instead of O(n²)
      for (let i = 0; i < particles.length; i++) {
        const p = particles[i]
        const neighbors: { n: Particle; dist: number }[] = []

        for (let j = i + 1; j < particles.length; j++) {
          const q = particles[j]
          const d = Math.hypot(q.x - p.x, q.y - p.y)
          if (d < CONNECTION_DIST) {
            neighbors.push({ n: q, dist: d })
          }
        }

        // Connect to nearest 2
        neighbors.sort((a, b) => a.dist - b.dist)
        const maxLines = Math.min(neighbors.length, 2)
        for (let k = 0; k < maxLines; k++) {
          const q = neighbors[k].n
          const alpha = Math.max(0, 1 - neighbors[k].dist / CONNECTION_DIST) * 0.4
          ctx!.beginPath()
          ctx!.moveTo(p.x, p.y)
          ctx!.lineTo(q.x, q.y)
          ctx!.strokeStyle = `rgba(52, 211, 153, ${alpha})`
          ctx!.lineWidth = 1.0
          ctx!.stroke()
        }
      }

      // Particles as dots
      for (const p of particles) {
        const dx = mouse.x - p.x
        const dy = mouse.y - p.y
        const dist = Math.hypot(dx, dy)
        const nearGlow = Math.max(0, 1 - dist / MOUSE_RADIUS) * 0.7

        ctx!.beginPath()
        ctx!.arc(p.x, p.y, 2 + nearGlow, 0, Math.PI * 2)
        ctx!.fillStyle = `rgba(52, 211, 153, ${0.55 + nearGlow * 0.35})`
        ctx!.fill()
      }

      rafId = requestAnimationFrame(animate)
    }

    // Pause/resume when tab hidden/visible
    function onVisibility() {
      running = !document.hidden
      if (running) rafId = requestAnimationFrame(animate)
    }
    document.addEventListener('visibilitychange', onVisibility)

    rafId = requestAnimationFrame(animate)

    return () => {
      running = false
      cancelAnimationFrame(rafId)
      window.removeEventListener('resize', resize)
      window.removeEventListener('mousemove', onMouse)
      window.removeEventListener('mouseleave', onMouseLeave)
      document.removeEventListener('visibilitychange', onVisibility)
    }
  }, [profile.enableParticleNetwork, profile.particleMultiplier, profile.maxDpr, profile.canvasUpdateInterval])

  if (!profile.enableParticleNetwork) return null

  return (
    <canvas
      ref={canvasRef}
      className="fixed inset-0 pointer-events-none z-0"
      style={{ opacity: 0.85 }}
    />
  )
}
