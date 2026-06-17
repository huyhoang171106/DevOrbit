import { Edges, Text } from "@react-three/drei";
import gsap from "gsap";
import { useEffect, useRef, useState } from "react";
import * as THREE from "three";

interface FeatureStripProps {
  title: string;
  subtitle: string;
  description?: string;
  icon?: string;
  index: number;
  position: [number, number, number];
  accentColor: string;
}

const FeatureStrip = ({ title, subtitle, description, icon, index, position, accentColor }: FeatureStripProps) => {
  const stripRef = useRef<THREE.Group>(null);
  const barRef = useRef<THREE.Mesh>(null);
  const bgRef = useRef<THREE.Mesh>(null);
  const descRef = useRef<THREE.Group>(null);
  const [hovered, setHovered] = useState(false);

  useEffect(() => {
    if (!stripRef.current) return;

    gsap.to(stripRef.current.position, {
      x: hovered ? position[0] + 0.1 : position[0],
      duration: 0.3,
      ease: "power2.out"
    });

    if (barRef.current) {
      gsap.to(barRef.current.scale, {
        y: hovered ? 1.5 : 1,
        duration: 0.3,
      });
    }

    if (bgRef.current) {
      gsap.to(bgRef.current.scale, {
        x: hovered ? 1.05 : 1,
        duration: 0.3,
      });
    }

    if (descRef.current) {
      gsap.to(descRef.current.position, {
        y: hovered ? -0.25 : -0.15,
        opacity: hovered ? 1 : 0,
        duration: 0.3,
      });
    }
  }, [hovered]);

  // Entrance
  useEffect(() => {
    if (stripRef.current) {
      gsap.fromTo(stripRef.current.position,
        { y: position[1] - 2, opacity: 0 },
        {
          y: position[1],
          opacity: 1,
          duration: 0.6,
          delay: 0.05 * index + 0.2,
          ease: "power3.out",
        }
      );
    }
  }, []);

  return (
    <group
      ref={stripRef}
      position={position}
      onPointerOver={() => { document.body.style.cursor = 'pointer'; setHovered(true); }}
      onPointerOut={() => { document.body.style.cursor = 'auto'; setHovered(false); }}
    >
      {/* Glass Background */}
      <mesh ref={bgRef} position={[-1, 0, -0.05]} scale={[1, 1, 1]}>
        <planeGeometry args={[3.5, 0.6]} />
        <meshBasicMaterial 
          color={hovered ? accentColor : "#1e293b"} 
          transparent 
          opacity={hovered ? 0.15 : 0.05} 
        />
      </mesh>

      {/* Accent bar */}
      <mesh ref={barRef} position={[-2.7, 0, 0.02]} scale={[1, 1, 1]}>
        <planeGeometry args={[0.04, 0.4]} />
        <meshBasicMaterial color={accentColor} />
      </mesh>

      {/* Icon */}
      {icon && (
        <Text
          position={[-2.45, 0.08, 0.02]}
          fontSize={0.2}
          anchorX="center"
          anchorY="middle"
        >
          {icon}
        </Text>
      )}

      {/* Title */}
      <Text
        position={[-2.2, 0.12, 0.02]}
        fontSize={0.16}
        font="./soria-font.ttf"
        color="white"
        anchorX="left"
        anchorY="middle"
        maxWidth={2.5}
      >
        {title}
      </Text>

      {/* Subtitle */}
      <Text
        position={[-2.2, -0.08, 0.02]}
        fontSize={0.07}
        font="./Vercetti-Regular.woff"
        color={hovered ? "white" : "#94a3b8"}
        anchorX="left"
        anchorY="middle"
        maxWidth={3}
      >
        {subtitle}
      </Text>

      {/* Description (visible on hover) */}
      <group ref={descRef} position={[0, -0.15, 0.02]} visible={hovered}>
        <Text
          position={[-2.2, 0, 0]}
          fontSize={0.06}
          font="./Vercetti-Regular.woff"
          color="#cbd5e1"
          anchorX="left"
          anchorY="top"
          maxWidth={3.2}
          lineHeight={1.4}
        >
          {description}
        </Text>
      </group>
    </group>
  );
};

export default FeatureStrip;
