'use client';

import { Float, MeshDistortMaterial, MeshWobbleMaterial, Sphere } from "@react-three/drei";
import { useFrame } from "@react-three/fiber";
import { useRef } from "react";
import * as THREE from "three";

export function UITCore(props: any) {
  const groupRef = useRef<THREE.Group>(null);
  const ring1Ref = useRef<THREE.Mesh>(null);
  const ring2Ref = useRef<THREE.Mesh>(null);
  const ring3Ref = useRef<THREE.Mesh>(null);

  useFrame((state) => {
    const time = state.clock.getElapsedTime();
    if (groupRef.current) {
      groupRef.current.rotation.y = time * 0.1;
    }
    if (ring1Ref.current) {
      ring1Ref.current.rotation.z = time * 0.3;
      ring1Ref.current.rotation.x = time * 0.15;
    }
    if (ring2Ref.current) {
      ring2Ref.current.rotation.z = -time * 0.2;
      ring2Ref.current.rotation.y = time * 0.1;
    }
    if (ring3Ref.current) {
      ring3Ref.current.rotation.x = -time * 0.4;
      ring3Ref.current.rotation.z = time * 0.05;
    }
  });

  const pulseIntensity = useRef(1);
  useFrame((state) => {
    pulseIntensity.current = 50 + Math.sin(state.clock.getElapsedTime() * 2) * 30;
  });

  return (
    <group {...props} ref={groupRef}>
      {/* Central Core Sphere - Representing Knowledge/Data */}
      <Float speed={1.5} rotationIntensity={0.5} floatIntensity={1}>
        <Sphere args={[0.8, 64, 64]}>
          <MeshDistortMaterial
            color="#0077b6"
            speed={2}
            distort={0.3}
            radius={1}
            emissive="#00b4d8"
            emissiveIntensity={2}
          />
        </Sphere>
      </Float>

      {/* Orbital Rings - Representing Connectivity and Progress */}
      <mesh ref={ring1Ref}>
        <torusGeometry args={[1.2, 0.01, 16, 100]} />
        <meshStandardMaterial color="#48cae4" emissive="#48cae4" emissiveIntensity={10} />
      </mesh>

      <mesh ref={ring2Ref} rotation={[Math.PI / 2, 0, 0]}>
        <torusGeometry args={[1.5, 0.008, 16, 100]} />
        <meshStandardMaterial color="#0096c7" emissive="#0096c7" emissiveIntensity={10} />
      </mesh>

      <mesh ref={ring3Ref} rotation={[0, Math.PI / 4, 0]}>
        <torusGeometry args={[1.8, 0.005, 16, 100]} />
        <meshStandardMaterial color="#90e0ef" emissive="#90e0ef" emissiveIntensity={10} />
      </mesh>

      {/* Floating Data Nodes */}
      {[...Array(12)].map((_, i) => (
        <group key={i} rotation={[0, (i * Math.PI) / 6, 0]}>
          <Float speed={2} rotationIntensity={1} floatIntensity={2}>
            <mesh position={[2, Math.sin(i) * 0.5, 0]}>
              <boxGeometry args={[0.05, 0.05, 0.05]} />
              <MeshWobbleMaterial color="#caf0f8" speed={1} factor={0.5} emissive="#caf0f8" emissiveIntensity={5} />
            </mesh>
          </Float>
        </group>
      ))}

      {/* Core Glow Pulse */}
      <pointLight intensity={pulseIntensity.current} distance={8} color="#00d4ff" />
      <pointLight intensity={20} distance={15} color="#0077b6" position={[0, -2, 0]}/>
    </group>
  );
}
