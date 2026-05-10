import { Suspense } from 'react'
import { Canvas } from '@react-three/fiber'
import { Starfield } from './effects/Starfield'
import { GalaxyCamera } from './camera/GalaxyCamera'

type Props = {
  children: React.ReactNode
}

export function GalaxyCanvas({ children }: Props) {
  return (
    <Canvas
      camera={{ position: [0, 0, 50], fov: 60, far: 300 }}
      gl={{ antialias: true, alpha: false }}
      style={{ background: '#05070a' }}
      dpr={[1, 2]}
    >
      <Suspense fallback={null}>
        <ambientLight intensity={0.3} />
        <pointLight position={[0, 30, 0]} intensity={0.5} />
        <pointLight position={[0, -30, 0]} intensity={0.3} />

        <GalaxyCamera />
        <Starfield />

        {children}
      </Suspense>
    </Canvas>
  )
}
