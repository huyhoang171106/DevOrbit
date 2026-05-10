import { useRef, useEffect } from 'react'
import { useThree } from '@react-three/fiber'
import { CameraControls } from '@react-three/drei'
import * as THREE from 'three'

export function GalaxyCamera() {
  const controlsRef = useRef<CameraControls>(null)
  const { camera } = useThree()

  useEffect(() => {
    camera.position.set(0, 0, 50)
    camera.lookAt(0, 0, 0)
  }, [camera])

  return (
    <CameraControls
      ref={controlsRef}
      minDistance={5}
      maxDistance={120}
      dollySpeed={0.5}
      polarAngle={THREE.MathUtils.degToRad(85)}
    />
  )
}
