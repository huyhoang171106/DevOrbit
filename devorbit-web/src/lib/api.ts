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

    // Parse backend JSON error body, then fall back to status-code-specific message
    const knownErrors: Record<string, string> = {
      'Invalid username or password': 'Sai tên đăng nhập hoặc mật khẩu',
      'Account is deactivated': 'Tài khoản đã bị vô hiệu hoá',
      'Too many login attempts. Try again later.': 'Quá nhiều lần đăng nhập thất bại, vui lòng thử lại sau',
      'Refresh token has already been used': 'Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại',
      'Internal server error': 'Máy chủ gặp lỗi, vui lòng thử lại sau',
    }
    const statusMessages: Record<number, string> = {
      400: 'Dữ liệu gửi lên không hợp lệ',
      401: 'Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại',
      403: 'Bạn không có quyền thực hiện thao tác này',
      404: 'Không tìm thấy dữ liệu yêu cầu',
      409: 'Dữ liệu đã bị thay đổi, vui lòng tải lại và thử lại',
      422: 'Dữ liệu gửi lên không hợp lệ',
      429: 'Quá nhiều yêu cầu, vui lòng thử lại sau',
      500: 'Máy chủ gặp lỗi, vui lòng thử lại sau',
      502: 'Máy chủ tạm thời không hoạt động, vui lòng thử lại sau',
      503: 'Dịch vụ tạm thời ngừng hoạt động, vui lòng thử lại sau',
      504: 'Máy chủ quá thời gian phản hồi, vui lòng thử lại sau',
    }

    let message: string
    try {
      const parsed = JSON.parse(body)
      const raw = parsed.error || parsed.detail || body
      message = knownErrors[raw] || raw
    } catch {
      message = body
        ? (body.length < 200 ? body : 'Máy chủ trả về lỗi, vui lòng thử lại sau')
        : (statusMessages[response.status] || `Yêu cầu thất bại (mã lỗi ${response.status})`)
    }
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
    if (!res.ok) {
      const body = await res.text().catch(() => '')
      let msg: string
      try {
        const parsed = JSON.parse(body)
        msg = parsed.error || parsed.detail || `Tải lên thất bại (mã lỗi ${res.status})`
      } catch {
        msg = body && body.length < 200 ? body : `Tải lên thất bại (mã lỗi ${res.status})`
      }
      throw new Error(msg)
    }
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
