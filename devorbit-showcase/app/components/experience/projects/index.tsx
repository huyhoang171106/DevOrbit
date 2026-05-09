import { Text, useScroll } from "@react-three/drei";
import { useFrame, useThree } from "@react-three/fiber";
import gsap from "gsap";
import { useEffect, useMemo } from "react";
import { isMobile } from "react-device-detect";
import * as THREE from "three";
import { usePortalStore } from "@stores";
import { DEVORBIT_FEATURES } from "@constants";
import { Wanderer } from "../../models/Wanderer";
import FeatureStrip from "./FeatureStrip";
import { TouchPanControls } from "./TouchPanControls";

const Projects = () => {
  const { camera } = useThree();
  const isActive = usePortalStore((state) => state.activePortalId === "projects");
  const data = useScroll();

  useEffect(() => {
    data.el.style.overflow = isActive ? 'hidden' : 'auto';
    if (isActive) {
      if (isMobile) {
        gsap.to(camera.position, { z: 11.5, y: -39, x: 1, duration: 1 });
      } else {
        gsap.to(camera.position, { y: -39, x: 2, duration: 1 });
      }
    }
  }, [isActive]);

  useFrame((state, delta) => {
    if (isActive) {
      if (!isMobile) {
        camera.rotation.y = THREE.MathUtils.lerp(camera.rotation.y, -(state.pointer.x * Math.PI) / 4, 0.03);
        camera.position.z = THREE.MathUtils.damp(camera.position.z, 11.5 - state.pointer.y, 7, delta);
      }
    }
  });



  return (
    <group>
      {/* Subtle dark backdrop */}
      <mesh position={[0, 0, -2]} scale={[12, 8, 1]}>
        <planeGeometry args={[1, 1]} />
        <meshBasicMaterial color="#050510" transparent opacity={0.7} />
      </mesh>

      {/* Wanderer — positioned elegantly on the right */}
      <Wanderer
        rotation={new THREE.Euler(0, -Math.PI / 6, 0)}
        scale={new THREE.Vector3(1.1, 1.1, 1.1)}
        position={new THREE.Vector3(5.2, -1.8, -2)}
      />

      {/* Section subtitle */}
      <Text
        position={[-3.5, 2.6, 2]}
        fontSize={0.1}
        font="./Vercetti-Regular.woff"
        color="#10b981"
        anchorX="left"
        anchorY="bottom"
      >
        CÔNG CỤ HỖ TRỢ HỌC TẬP THÔNG MINH
      </Text>

      {/* Left column */}
      {DEVORBIT_FEATURES.slice(0, 4).map((f, i) => (
        <FeatureStrip
          key={f.title}
          title={f.title}
          subtitle={f.subtitle}
          description={f.description}
          icon={f.icon}
          index={i}
          position={[-3.5, 1.8 - i * 0.75, 2]}
          accentColor={f.color}
        />
      ))}
      {DEVORBIT_FEATURES.slice(4, 8).map((f, i) => (
        <FeatureStrip
          key={f.title}
          title={f.title}
          subtitle={f.subtitle}
          description={f.description}
          icon={f.icon}
          index={i + 4}
          position={[0, 1.8 - i * 0.75, 2]}
          accentColor={f.color}
        />
      ))}

      {isActive && isMobile && <TouchPanControls />}
    </group>
  );
};

export default Projects;
