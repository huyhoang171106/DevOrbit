
import { useMemo } from 'react'

export type DeviceTier = 'low' | 'medium' | 'high'

export interface PerformanceProfile {
  /** Device performance tier */
  tier: DeviceTier
  /** True if touch-primary device (phone/tablet) */
  isMobile: boolean
  /** Respects prefers-reduced-motion */
  prefersReducedMotion: boolean
  /** 0–1 multiplier for particle counts */
  particleMultiplier: number
  /** Max devicePixelRatio to use in canvases/3D */
  maxDpr: number
  /** Enable backdrop-filter blur */
  enableBackdropBlur: boolean
  /** Enable parallax effects */
  enableParallax: boolean
  /** Enable 3D scenes */
  enable3D: boolean
  /** Enable canvas 2D particle network */
  enableParticleNetwork: boolean
  /** Suggested lenis lerp (higher = less smooth = less GPU) */
  lenisLerp: number
  /** Cap rAF updates for canvas 2D to this interval (ms), 0 = uncapped on desktop */
  canvasUpdateInterval: number
}

/**
 * Device-aware performance profile.
 * Evaluated once on mount — does not react to viewport changes.
 * All animated components should read this to scale their visual complexity.
 */
export function usePerformanceProfile(): PerformanceProfile {
  return useMemo(() => {
    const mqReduced = window.matchMedia('(prefers-reduced-motion: reduce)')
    const prefersReducedMotion = mqReduced.matches

    const width = window.innerWidth
    const isMobile = width < 768
    const isTablet = width >= 768 && width < 1024

    const cpuCores = navigator.hardwareConcurrency ?? 4
    const deviceMemory =
      ((navigator as any).deviceMemory as number | undefined) ?? 4

    // Determine tier
    let tier: DeviceTier
    if (isMobile || cpuCores <= 4 || deviceMemory <= 2 || prefersReducedMotion) {
      tier = 'low'
    } else if (isTablet || cpuCores <= 6 || deviceMemory <= 4) {
      tier = 'medium'
    } else {
      tier = 'high'
    }

    // Reduce everything when reduced motion is preferred
    if (prefersReducedMotion) {
      return {
        tier,
        isMobile,
        prefersReducedMotion: true,
        particleMultiplier: 0,
        maxDpr: 1,
        enableBackdropBlur: false,
        enableParallax: false,
        enable3D: false,
        enableParticleNetwork: false,
        lenisLerp: 0.15,
        canvasUpdateInterval: 0,
      }
    }

    // Profile values by tier
    const profiles: Record<DeviceTier, Omit<PerformanceProfile, 'tier' | 'isMobile' | 'prefersReducedMotion'>> = {
      low: {
        particleMultiplier: 0.2,
        maxDpr: 1,
        enableBackdropBlur: false,
        enableParallax: false,
        enable3D: false,
        enableParticleNetwork: true,
        lenisLerp: 0.15,
        canvasUpdateInterval: 32, // ~30fps
      },
      medium: {
        particleMultiplier: 0.5,
        maxDpr: 1.5,
        enableBackdropBlur: true,
        enableParallax: false,
        enable3D: true,
        enableParticleNetwork: true,
        lenisLerp: 0.1,
        canvasUpdateInterval: 16, // ~60fps
      },
      high: {
        particleMultiplier: 1,
        maxDpr: 2,
        enableBackdropBlur: true,
        enableParallax: true,
        enable3D: true,
        enableParticleNetwork: true,
        lenisLerp: 0.08,
        canvasUpdateInterval: 0, // uncapped
      },
    }

    return {
      tier,
      isMobile,
      prefersReducedMotion,
      ...profiles[tier],
    }
  }, [])
}
