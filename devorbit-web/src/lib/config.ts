/**
 * DevOrbit Web — Typed Configuration Loader
 *
 * Reads environment variables at build time (Vite) and provides
 * a validated, typed config object.  Throws on startup if required
 * vars are missing or malformed.
 */

export interface WebConfig {
  /** API base path (e.g. /api or http://localhost:8080) */
  apiBaseUrl: string
  /** Dev server port */
  port: number
  /** API proxy target for Vite dev server */
  proxyTarget: string
  /** Allowed dev-server hosts (comma-separated) */
  allowedHosts: string[]
}

export interface AppConfig {
  web: WebConfig
  /** Whether the app runs in development mode */
  isDev: boolean
  /** Environment name */
  env: string
}

function requireEnv(key: string, fallback?: string): string {
  const val = import.meta.env[key] ?? fallback
  if (val === undefined || val === '') {
    if (fallback === undefined) {
      throw new Error(
        `Missing required env var: ${key}. Set it in .env or pass via shell.`
      )
    }
    return fallback
  }
  return String(val)
}

function parseNumber(val: string, key: string): number {
  const n = Number(val)
  if (Number.isNaN(n)) {
    throw new Error(`Invalid number for ${key}: "${val}"`)
  }
  return n
}

// ── Validation schema builder ────────────────────

type ValidationRule<T> = { key: string; label: string; validate: (v: T) => string | null }

function check<T>(key: string, label: string, validate: (v: T) => string | null): ValidationRule<T> {
  return { key, label, validate }
}

function runChecks<T>(vals: Record<string, T>, rules: ValidationRule<T>[]): void {
  const errors: string[] = []
  for (const rule of rules) {
    const err = rule.validate(vals[rule.key] as T)
    if (err) errors.push(`  ${rule.key} (${rule.label}): ${err}`)
  }
  if (errors.length > 0) {
    throw new Error(`Configuration validation failed:\n${errors.join('\n')}`)
  }
}

// ── Load & validate ──────────────────────────────

let cached: AppConfig | null = null

export function loadConfig(): AppConfig {
  if (cached) return cached

  const rawApiBaseUrl = requireEnv('VITE_API_BASE_URL', '/api')
  const rawPort = requireEnv('WEB_PORT', '5173')
  const rawProxyTarget = requireEnv('VITE_PROXY_TARGET', 'http://localhost:8080')
  const rawAllowedHosts = requireEnv('VITE_ALLOWED_HOSTS', 'localhost')
  const rawEnv = requireEnv('MODE', 'development')

  const config: AppConfig = {
    web: {
      apiBaseUrl: rawApiBaseUrl,
      port: parseNumber(rawPort, 'WEB_PORT'),
      proxyTarget: rawProxyTarget,
      allowedHosts: rawAllowedHosts.split(',').map(s => s.trim()).filter(Boolean),
    },
    isDev: rawEnv === 'development',
    env: rawEnv,
  }

  // ── Validate ──
  runChecks(
    {
      apiBaseUrl: config.web.apiBaseUrl,
      port: config.web.port,
      proxyTarget: config.web.proxyTarget,
      allowedHostsCount: config.web.allowedHosts.length,
    },
    [
      check('apiBaseUrl', 'API base URL', v =>
        typeof v === 'string' && v.length > 0 ? null : 'must not be empty',
      ),
      check('port', 'Dev server port', v =>
        typeof v === 'number' && v > 0 && v < 65536 ? null : `invalid port: ${v}`,
      ),
      check('proxyTarget', 'API proxy target', v =>
        typeof v === 'string' && (v.startsWith('http://') || v.startsWith('https://'))
          ? null
          : 'must start with http:// or https://',
      ),
      check('allowedHostsCount', 'Allowed hosts', v =>
        typeof v === 'number' && v > 0 ? null : 'at least one host required',
      ),
    ],
  )

  cached = config
  return config
}

/** Singleton config — safe to import anywhere after first call. */
export const config: AppConfig = /* @__PURE__ */ loadConfig()
