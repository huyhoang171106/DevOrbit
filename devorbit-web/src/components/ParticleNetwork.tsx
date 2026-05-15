import { useEffect, useRef } from 'react'

const PARTICLE_COUNT = 80
const CONNECTION_DIST = 140
const MOUSE_RADIUS = 180
const MOUSE_FORCE = 0.6
const PARTICLE_SPEED = 0.3

interface Particle {
  x: number; y: number
  vx: number; vy: number
  baseX: number; baseY: number
}

export function ParticleNetwork() {
  const canvasRef = useRef<HTMLCanvasElement>(null)

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const mouse = { x: -1000, y: -1000 }

    function resize() {
      canvas!.width = window.innerWidth
      canvas!.height = window.innerHeight
    }
    resize()
    window.addEventListener('resize', resize)

    const onMouse = (e: MouseEvent) => {
      mouse.x = e.clientX
      mouse.y = e.clientY
    }
    window.addEventListener('mousemove', onMouse, { passive: true })
    window.addEventListener('mouseleave', () => { mouse.x = -1000; mouse.y = -1000 })

    // Init particles
    const particles: Particle[] = []
    for (let i = 0; i < PARTICLE_COUNT; i++) {
      const x = Math.random() * canvas.width
      const y = Math.random() * canvas.height
      particles.push({
        x, y,
        vx: (Math.random() - 0.5) * PARTICLE_SPEED,
        vy: (Math.random() - 0.5) * PARTICLE_SPEED,
        baseX: x,
        baseY: y,
      })
    }

    let animId = 0
    let running = true

    function animate() {
      if (!canvas || !running) { animId = requestAnimationFrame(animate); return }
      ctx!.clearRect(0, 0, canvas.width, canvas.height)

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

        // Return to base (gentle pull)
        p.vx += (p.baseX - p.x) * 0.001
        p.vy += (p.baseY - p.y) * 0.001

        // Damping
        p.vx *= 0.98
        p.vy *= 0.98

        p.x += p.vx
        p.y += p.vy

        // Wrap
        if (p.x < 0) p.x = canvas.width
        if (p.x > canvas.width) p.x = 0
        if (p.y < 0) p.y = canvas.height
        if (p.y > canvas.height) p.y = 0
      }

      // Build connections and triangles (greedy: sort by dist, connect closest)
      const lines: [Particle, Particle][] = []
      const candidates: Particle[] = [...particles]
      for (const p of particles) {
        const neighbors = candidates
          .filter(n => n !== p)
          .map(n => ({ n, dist: Math.hypot(n.x - p.x, n.y - p.y) }))
          .filter(({ dist }) => dist < CONNECTION_DIST)
          .sort((a, b) => a.dist - b.dist)
          .slice(0, 3)

        for (const { n } of neighbors) {
          // Avoid duplicate lines
          if (!lines.some(l => (l[0] === p && l[1] === n) || (l[0] === n && l[1] === p))) {
            lines.push([p, n])
          }
        }
      }

      // Draw triangles (mesh fill)
      const triangles: [Particle, Particle, Particle][] = []
      for (let i = 0; i < particles.length; i++) {
        for (let j = i + 1; j < particles.length; j++) {
          for (let k = j + 1; k < particles.length; k++) {
            const a = particles[i], b = particles[j], c = particles[k]
            // Check if all 3 sides are connected
            const ab = lines.some(l => (l[0] === a && l[1] === b) || (l[0] === b && l[1] === a))
            const bc = lines.some(l => (l[0] === b && l[1] === c) || (l[0] === c && l[1] === b))
            const ca = lines.some(l => (l[0] === c && l[1] === a) || (l[0] === a && l[1] === c))
            if (ab && bc && ca) {
              triangles.push([a, b, c])
            }
          }
        }
      }

      // Draw triangle fills (glow)
      for (const tri of triangles) {
        const avgDist =
          (Math.hypot(tri[0].x - mouse.x, tri[0].y - mouse.y) +
           Math.hypot(tri[1].x - mouse.x, tri[1].y - mouse.y) +
           Math.hypot(tri[2].x - mouse.x, tri[2].y - mouse.y)) / 3

        const nearFactor = Math.max(0, 1 - avgDist / 500)
        const alpha = Math.min(0.04 + nearFactor * 0.08, 0.12)

        ctx!.beginPath()
        ctx!.moveTo(tri[0].x, tri[0].y)
        ctx!.lineTo(tri[1].x, tri[1].y)
        ctx!.lineTo(tri[2].x, tri[2].y)
        ctx!.closePath()
        ctx!.fillStyle = `rgba(52, 211, 153, ${alpha})`
        ctx!.fill()
      }

      // Draw lines
      for (const [a, b] of lines) {
        const dist = Math.hypot(a.x - b.x, a.y - b.y)
        const alpha = Math.max(0, 1 - dist / CONNECTION_DIST) * 0.3

        ctx!.beginPath()
        ctx!.moveTo(a.x, a.y)
        ctx!.lineTo(b.x, b.y)
        ctx!.strokeStyle = `rgba(52, 211, 153, ${alpha})`
        ctx!.lineWidth = 0.8
        ctx!.stroke()
      }

      // Draw particles
      for (const p of particles) {
        const dist = Math.hypot(p.x - mouse.x, p.y - mouse.y)
        const nearGlow = Math.max(0, 1 - dist / MOUSE_RADIUS) * 0.7

        ctx!.beginPath()
        ctx!.arc(p.x, p.y, 2 + nearGlow * 1.5, 0, Math.PI * 2)
        ctx!.fillStyle = `rgba(52, 211, 153, ${0.5 + nearGlow * 0.5})`
        ctx!.fill()
      }

      animId = requestAnimationFrame(animate)
    }

    // Pause when tab hidden
    function onVisibility() { running = !document.hidden }
    document.addEventListener('visibilitychange', onVisibility)

    animate()

    return () => {
      cancelAnimationFrame(animId)
      window.removeEventListener('resize', resize)
      window.removeEventListener('mousemove', onMouse)
      document.removeEventListener('visibilitychange', onVisibility)
    }
  }, [])

  return (
    <canvas
      ref={canvasRef}
      className="fixed inset-0 pointer-events-none z-0"
      style={{ opacity: 0.6 }}
    />
  )
}
