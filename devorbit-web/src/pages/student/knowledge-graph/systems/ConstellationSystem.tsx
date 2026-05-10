import { Text } from '@react-three/drei'
import { Planet } from '../entities/Planet'
import { OrbitalGroup } from './OrbitalGroup'
import { PlanetTrail } from '../effects/PlanetTrail'
import type { DomainGalaxy } from '../layout/types'

type Props = {
  galaxies: DomainGalaxy[]
  onPlanetClick: (id: number) => void
}

export function ConstellationSystem({ galaxies, onPlanetClick }: Props) {
  return (
    <group>
      {galaxies.map((galaxy) => (
        <group key={galaxy.id}>
          {/* galaxy label billboard */}
          <Text
            position={[galaxy.center[0], galaxy.center[1] + 20, galaxy.center[2]]}
            fontSize={2.5}
            color={galaxy.color}
            anchorX="center"
            anchorY="middle"
            outlineWidth={0.05}
            outlineColor="#000"
          >
            {galaxy.name}
          </Text>

          {/* planets */}
          {galaxy.nodes.map((node) => (
            <OrbitalGroup key={node.id} node={node} galaxyCenter={galaxy.center}>
              <Planet node={node} color={galaxy.color} onClick={onPlanetClick} />
              <PlanetTrail nodeId={node.id} color={galaxy.color} />
            </OrbitalGroup>
          ))}
        </group>
      ))}
    </group>
  )
}
