import { type ComponentPropsWithoutRef } from 'react'

const AVATAR_COLORS = [
  'from-emerald-500 to-emerald-600',
  'from-sky-500 to-sky-600',
  'from-violet-500 to-violet-600',
  'from-rose-500 to-rose-600',
  'from-amber-500 to-amber-600',
  'from-cyan-500 to-cyan-600',
  'from-pink-500 to-pink-600',
  'from-indigo-500 to-indigo-600',
  'from-teal-500 to-teal-600',
  'from-fuchsia-500 to-fuchsia-600',
  'from-orange-500 to-orange-600',
  'from-lime-500 to-lime-600',
]

const RING_COLORS = [
  'from-emerald-400/30 via-emerald-400/10 to-emerald-400/5',
  'from-sky-400/30 via-sky-400/10 to-sky-400/5',
  'from-violet-400/30 via-violet-400/10 to-violet-400/5',
  'from-rose-400/30 via-rose-400/10 to-rose-400/5',
  'from-amber-400/30 via-amber-400/10 to-amber-400/5',
  'from-cyan-400/30 via-cyan-400/10 to-cyan-400/5',
  'from-pink-400/30 via-pink-400/10 to-pink-400/5',
  'from-indigo-400/30 via-indigo-400/10 to-indigo-400/5',
  'from-teal-400/30 via-teal-400/10 to-teal-400/5',
  'from-fuchsia-400/30 via-fuchsia-400/10 to-fuchsia-400/5',
  'from-orange-400/30 via-orange-400/10 to-orange-400/5',
  'from-lime-400/30 via-lime-400/10 to-lime-400/5',
]

function hashIndex(name: string, max: number): number {
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  return Math.abs(hash) % max
}

function getInitials(name: string): string {
  return name
    .split(' ')
    .map((w) => w.charAt(0))
    .join('')
    .toUpperCase()
    .slice(0, 2)
}

function getFontSize(size: number): number {
  if (size >= 96) return size * 0.38
  if (size >= 64) return size * 0.4
  if (size >= 48) return size * 0.42
  return size * 0.44
}

export interface AvatarProps extends ComponentPropsWithoutRef<'div'> {
  name: string
  size?: number
  /** Optional image URL overrides the initials avatar */
  src?: string | null
  /** Alt text for image avatars */
  alt?: string
}

/**
 * Deterministic random avatar — generates a gradient circle with initials
 * based on the provided name. No external API dependency.
 *
 * Pass `src` to use a custom image instead (e.g. DiceBear, uploaded photo).
 */
export function Avatar({
  name,
  size = 40,
  src,
  alt,
  className = '',
  ...rest
}: AvatarProps) {
  const idx = hashIndex(name, AVATAR_COLORS.length)
  const gradient = AVATAR_COLORS[idx]
  const ringGradient = RING_COLORS[idx]
  const initials = getInitials(name)
  const fontSize = getFontSize(size)
  const ringWidth = Math.max(1.5, size * 0.035)

  if (src) {
    return (
      <div
        className={`relative flex-shrink-0 overflow-hidden rounded-full ${className}`}
        style={{ width: size, height: size }}
        {...rest}
      >
        {/* Ring */}
        <div
          className="absolute inset-0 rounded-full bg-gradient-to-br opacity-40"
          style={{ padding: ringWidth }}
        >
          <div className="h-full w-full rounded-full bg-orbit-bg" />
        </div>
        <img
          src={src}
          alt={alt ?? name}
          className="h-full w-full rounded-full object-cover"
          style={{ padding: ringWidth * 1.5 }}
          loading="lazy"
        />
      </div>
    )
  }

  return (
    <div
      className={`relative flex-shrink-0 ${className}`}
      style={{ width: size, height: size }}
      title={name}
      {...rest}
    >
      {/* Decorative ring glow */}
      <div
        className="absolute inset-0 rounded-full bg-gradient-to-br opacity-40 blur-[2px]"
        style={{ backgroundImage: `linear-gradient(135deg, var(--tw-gradient-from), var(--tw-gradient-to))` }}
      />
      {/* Main circle */}
      <div
        className={`h-full w-full rounded-full bg-gradient-to-br ${gradient} flex items-center justify-center font-bold text-white select-none relative z-10`}
      >
        <span style={{ fontSize }}>{initials}</span>
      </div>
      {/* Orbital glow ring */}
      <div
        className={`absolute -inset-[2px] rounded-full bg-gradient-to-br ${ringGradient} -z-10`}
      />
    </div>
  )
}
