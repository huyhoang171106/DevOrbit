import { createContext, useContext, useRef, useCallback, useMemo, type ReactNode } from 'react'
import * as THREE from 'three'

type PlanetPositionContextType = {
  registerPlanet: (id: number) => (pos: THREE.Vector3) => void
  getPosition: (id: number) => THREE.Vector3 | undefined
}

const Ctx = createContext<PlanetPositionContextType | null>(null)

export function PlanetPositionProvider({ children }: { children: ReactNode }) {
  const positionsRef = useRef<Map<number, THREE.Vector3> | null>(null)
  if (positionsRef.current === null) {
    positionsRef.current = new Map<number, THREE.Vector3>()
  }

  const registerPlanet = useCallback((id: number) => {
    const pos = new THREE.Vector3()
    positionsRef.current!.set(id, pos)
    return (newPos: THREE.Vector3) => {
      pos.copy(newPos)
    }
  }, [])

  const getPosition = useCallback((id: number) => {
    return positionsRef.current!.get(id)
  }, [])

  const value = useMemo(
    () => ({ registerPlanet, getPosition }),
    [registerPlanet, getPosition],
  )

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>
}

export function usePlanetPositions() {
  return useContext(Ctx)
}
