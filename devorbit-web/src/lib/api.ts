import { getStudentToken, clearStudentToken } from './auth'

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? ''

export function buildApiUrl(baseUrl: string, path: string): string {
  if (!baseUrl) return path

  const normalizedBase = baseUrl.replace(/\/+$/, '')
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  const firstPathSegment = normalizedPath.split('/')[1]

  if (firstPathSegment && normalizedBase.endsWith(`/${firstPathSegment}`)) {
    return `${normalizedBase}${normalizedPath.slice(firstPathSegment.length + 1)}`
  }

  return `${normalizedBase}${normalizedPath}`
}

type RequestOptions = {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'
  token?: string
  body?: unknown
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {}
  // Allow browser cache for GET requests; react-query manages invalidation
  if (!options.method || options.method === 'GET') {
    headers['Cache-Control'] = 'max-age=0, must-revalidate'
  }
  if (options.body) headers['Content-Type'] = 'application/json'
  if (options.token) headers['Authorization'] = `Bearer ${options.token}`

  const response = await fetch(buildApiUrl(apiBaseUrl, path), {
    method: options.method ?? 'GET',
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined,
  })

  if (!response.ok) {
    const body = await response.text().catch(() => '')
    if (response.status === 403 && path.startsWith('/api/student/')) {
      clearStudentToken()
      window.location.href = '/student/login'
    }
    let message = body || `Yêu cầu thất bại (${response.status})`
    try {
      const parsed = JSON.parse(body)
      if (parsed.error) message = parsed.error
      if (parsed.detail) message = parsed.detail
    } catch {}
    throw new Error(message)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return normalizeResponse(await response.json()) as T
}

function normalizeResponse(value: unknown): unknown {
  if (Array.isArray(value)) return value.map(normalizeResponse)
  if (!value || typeof value !== 'object') return value
  const record = { ...(value as Record<string, unknown>) }
  if (Array.isArray(record.techStacks)) {
    record.techStacks = record.techStacks.flatMap((stack) => {
      const normalized = typeof stack === 'string' ? stack : String((stack as { name?: string }).name ?? '')
      return normalized ? [normalized] : []
    })
  }
  if (Array.isArray(record.repos)) record.repos = record.repos.map(normalizeResponse)
  return record
}

// --- Public API ---
export const apiGet = <T>(path: string) => request<T>(path)
export const apiPost = <T>(path: string, body: unknown) => request<T>(path, { method: 'POST', body })
export const apiPut = <T>(path: string, body: unknown) => request<T>(path, { method: 'PUT', body })
export const apiDelete = (path: string) => request<void>(path, { method: 'DELETE' })

export const apiUpload = <T>(path: string, formData: FormData): Promise<T> => {
  return fetch(buildApiUrl(apiBaseUrl, path), {
    method: 'POST',
    body: formData,
  }).then(async (res) => {
    if (!res.ok) throw new Error(`Tải lên thất bại (${res.status})`)
    return (await res.json()) as T
  })
}

// --- Student API (authenticated) ---
export const apiStudentGet = <T>(path: string) => {
  const token = getStudentToken()
  if (!token) throw new Error('Vui lòng đăng nhập')
  return request<T>(path, { token })
}

export const apiStudentPost = <T>(path: string, body: unknown) => {
  const token = getStudentToken()
  if (!token) throw new Error('Vui lòng đăng nhập')
  return request<T>(path, { method: 'POST', token, body })
}

export const apiStudentDelete = (path: string) => {
  const token = getStudentToken()
  if (!token) throw new Error('Vui lòng đăng nhập')
  return request<void>(path, { method: 'DELETE', token })
}

export const apiStudentPatch = <T>(path: string, body: unknown) => {
  const token = getStudentToken()
  if (!token) throw new Error('Vui lòng đăng nhập')
  return request<T>(path, { method: 'PATCH', token, body })
}

// --- Admin API (authenticated) ---
export const apiAdminGet = <T>(path: string, token: string) => request<T>(path, { token })
export const apiAdminPost = <T>(path: string, token: string, body: unknown) =>
  request<T>(path, { method: 'POST', token, body })
export const apiAdminPut = <T>(path: string, token: string, body: unknown) =>
  request<T>(path, { method: 'PUT', token, body })
export const apiAdminDelete = <T = void>(path: string, token: string) =>
  request<T>(path, { method: 'DELETE', token })
