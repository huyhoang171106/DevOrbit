'use client'

import { useEffect, useMemo } from 'react'
import { useThree } from '@react-three/fiber'
import * as THREE from 'three'

/**
 * Simulated bloom effect — set tone mapping once (not per frame).
 * Using ACESFilmicToneMapping for warmer cinematic highlights.
 */
export function AmbientBloom() {
  const { gl } = useThree()

  useEffect(() => {
    gl.toneMapping = THREE.ACESFilmicToneMapping
    gl.toneMappingExposure = 0.9
    ;(gl as unknown as Record<string, number>).toneMappingWhitePoint = 1.0
  }, [gl])

  return null
}

/**
 * Soft glow sprite behind bright objects.
 * Texture is cached per-color via useMemo.
 */
export function GlowSprite({
  color = '#34d399',
  intensity = 0.5,
  scale = 2,
}: {
  color?: string
  intensity?: number
  scale?: number
}) {
  const texture = useMemo(() => {
    const canvas = document.createElement('canvas')
    canvas.width = 64
    canvas.height = 64
    const ctx = canvas.getContext('2d')
    if (ctx) {
      const gradient = ctx.createRadialGradient(32, 32, 0, 32, 32, 32)
      gradient.addColorStop(0, 'rgba(255,255,255,1)')
      gradient.addColorStop(0.2, `rgba(255,255,255,${intensity})`)
      gradient.addColorStop(1, 'rgba(255,255,255,0)')
      ctx.fillStyle = gradient
      ctx.fillRect(0, 0, 64, 64)
    }
    const tex = new THREE.CanvasTexture(canvas)
    tex.needsUpdate = true
    return tex
  }, [intensity])

  return (
    <sprite scale={[scale, scale, 1]}>
      <spriteMaterial
        map={texture}
        color={color}
        transparent
        opacity={intensity * 0.5}
        blending={THREE.AdditiveBlending}
        depthWrite={false}
      />
    </sprite>
  )
}
