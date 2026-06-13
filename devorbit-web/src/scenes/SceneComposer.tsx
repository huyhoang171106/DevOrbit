
import { lazy, Suspense } from 'react'
import { Canvas } from '@react-three/fiber'
import { usePerformanceProfile } from '../performance/usePerformanceProfile'

const FloatingParticles = lazy(() =>
  import('./FloatingParticles').then((m) => ({ default: m.FloatingParticles }))
)
const KnowledgeGalaxy = lazy(() =>
  import('./KnowledgeGalaxy').then((m) => ({ default: m.KnowledgeGalaxy }))
)
const CameraController = lazy(() =>
  import('./CameraController').then((m) => ({ default: m.CameraController }))
)
const OrbitalNodes = lazy(() =>
  import('./OrbitalNodes').then((m) => ({ default: m.OrbitalNodes }))
)

interface SceneComposerProps {
  preset?: 'particles' | 'galaxy' | 'orbital' | 'minimal' | 'full'
  cameraOrbit?: boolean
  className?: string
  opacity?: number
}

const presets = {
  minimal: { particles: false, galaxy: false, orbital: false },
  particles: { particles: true, galaxy: false, orbital: false },
  galaxy: { particles: false, galaxy: true, orbital: false },
  orbital: { particles: false, galaxy: false, orbital: true },
  full: { particles: true, galaxy: true, orbital: true },
}

/**
 * Profile-aware 3D scene composer.
 * Lazy-loads all 3D assets. Adaptive DPR, antialias, and scene count.
 * Reduced-motion → Canvas skipped entirely.
 */
export function SceneComposer({
  preset = 'particles',
  cameraOrbit = true,
  className = '',
  opacity = 0.8,
}: SceneComposerProps) {
  const prof = usePerformanceProfile()

  if (!prof.enable3D) return null

  const config = presets[preset]
  const dpr = [1, prof.maxDpr] as [number, number]
  const hasAntialias = prof.tier !== 'low'

  // Scale scene density based on tier
  const particleCount = prof.tier === 'high' ? 1200 : prof.tier === 'medium' ? 600 : 250
  const starCount = prof.tier === 'high' ? 400 : prof.tier === 'medium' ? 200 : 80
  const orbitalCount = prof.tier === 'high' ? 20 : prof.tier === 'medium' ? 12 : 6

  return (
    <div
      className={`fixed inset-0 pointer-events-none z-0 ${className}`}
      style={{ opacity }}
    >
      <Canvas
        camera={{ position: [0, 0, 40], fov: 60, far: 200 }}
        gl={{
          antialias: hasAntialias,
          alpha: true,
          powerPreference: 'high-performance',
        }}
        dpr={dpr}
        style={{ background: 'transparent' }}
      >
        <Suspense fallback={null}>
          <ambientLight intensity={0.4} />

          {cameraOrbit && <CameraController speed={0.05} radius={35} />}

          {config.particles && (
            <FloatingParticles count={particleCount} radius={25} speed={0.1} />
          )}
          {config.galaxy && (
            <KnowledgeGalaxy starCount={starCount} radius={35} rotationSpeed={0.015} />
          )}
          {config.orbital && (
            <OrbitalNodes count={orbitalCount} maxRadius={30} minRadius={5} />
          )}
        </Suspense>
      </Canvas>
    </div>
  )
}
