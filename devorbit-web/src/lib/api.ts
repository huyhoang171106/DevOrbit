export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? ''

type RequestOptions = {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  token?: string
  body?: unknown
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {}
  if (options.body) headers['Content-Type'] = 'application/json'
  if (options.token) headers['Authorization'] = `Bearer ${options.token}`

  const response = await fetch(`${apiBaseUrl}${path}`, {
    method: options.method ?? 'GET',
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined,
    cache: 'no-cache',
  })

  if (!response.ok) {
    const body = await response.text().catch(() => '')
    throw new Error(body || `Request failed: ${response.status}`)
  }

  // DELETE returns 204 No Content — no body to parse
  if (options.method === 'DELETE' || response.status === 204) {
    return undefined as T
  }

  return normalizeResponse(await response.json()) as T
}

function normalizeResponse(value: unknown): unknown {
  if (Array.isArray(value)) return value.map(normalizeResponse)
  if (!value || typeof value !== 'object') return value
  const record = { ...(value as Record<string, unknown>) }
  if (Array.isArray(record.techStacks)) {
    record.techStacks = record.techStacks.map((stack) =>
      typeof stack === 'string' ? stack : String((stack as { name?: string }).name ?? ''),
    ).filter(Boolean)
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
  return fetch(`${apiBaseUrl}${path}`, {
    method: 'POST',
    body: formData,
  }).then(async (res) => {
    if (!res.ok) throw new Error(`Upload failed: ${res.status}`)
    return (await res.json()) as T
  })
}

// --- Admin API (authenticated) ---
export const apiAdminGet = <T>(path: string, token: string) => request<T>(path, { token })
export const apiAdminPost = <T>(path: string, token: string, body: unknown) =>
  request<T>(path, { method: 'POST', token, body })
export const apiAdminPut = <T>(path: string, token: string, body: unknown) =>
  request<T>(path, { method: 'PUT', token, body })
export const apiAdminDelete = (path: string, token: string) =>
  request<void>(path, { method: 'DELETE', token })
