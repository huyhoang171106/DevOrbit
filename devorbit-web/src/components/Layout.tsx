import { useEffect, useRef } from 'react'
import { Link } from 'react-router-dom'

function StarField() {
  const canvasRef = useRef<HTMLCanvasElement>(null)

  useEffect(() => {
    const canvas = canvasRef.current!
    if (!canvas) return
    const ctx = canvas.getContext('2d')!

    canvas.width = window.innerWidth
    canvas.height = window.innerHeight

    const stars: { x: number; y: number; r: number; a: number; s: number }[] = []
    for (let i = 0; i < 200; i++) {
      stars.push({
        x: Math.random() * canvas.width,
        y: Math.random() * canvas.height,
        r: Math.random() * 1.5 + 0.3,
        a: Math.random() * 0.8 + 0.2,
        s: Math.random() * 0.02 + 0.005,
      })
    }

    let frame: number
    function animate() {
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      for (const star of stars) {
        star.a = Math.sin(Date.now() * star.s) * 0.4 + 0.6
        ctx.beginPath()
        ctx.arc(star.x, star.y, star.r, 0, Math.PI * 2)
        ctx.fillStyle = `rgba(255, 255, 255, ${star.a})`
        ctx.fill()
      }
      frame = requestAnimationFrame(animate)
    }
    animate()

    const handleResize = () => {
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
    }
    window.addEventListener('resize', handleResize)
    return () => {
      cancelAnimationFrame(frame)
      window.removeEventListener('resize', handleResize)
    }
  }, [])

  return <canvas ref={canvasRef} className="fixed inset-0 z-0 pointer-events-none" />
}

export function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="relative min-h-screen">
      <StarField />
      <nav className="relative z-10 border-b border-white/5 bg-[#070b15]/80 backdrop-blur-xl">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
          <Link to="/" className="flex items-center gap-2 text-lg font-bold font-heading text-amber-400">
            <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="5" />
              <circle cx="12" cy="12" r="8" strokeDasharray="3 3" opacity="0.4" />
              <circle cx="12" cy="12" r="11" strokeDasharray="2 4" opacity="0.2" />
            </svg>
            DevOrbit
          </Link>
          <div className="flex items-center gap-4">
            <Link to="/courses" className="text-sm text-slate-400 transition-colors hover:text-slate-200">
              Courses
            </Link>
            <Link to="/admin/login" className="text-sm text-slate-400 transition-colors hover:text-slate-200">
              Admin
            </Link>
          </div>
        </div>
      </nav>
      <main className="relative z-10 mx-auto max-w-6xl px-4 py-8">
        {children}
      </main>
      <footer className="relative z-10 border-t border-white/5 py-6 text-center text-xs text-slate-600">
        DevOrbit &mdash; UIT Knowledge Repository Explorer
      </footer>
    </div>
  )
}
