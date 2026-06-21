import { useState, useEffect, useCallback, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { getAdminToken } from './adminAuth'

const cache = new Map<string, { data: unknown; expiry: number }>()
const CACHE_TTL = 300_000 // 5 min

export function useRequireAdminAuth() {
  const navigate = useNavigate()
  const token = getAdminToken()

  useEffect(() => {
    if (!token) navigate('/admin/login', { replace: true })
  }, [token, navigate])

  return token
}

export function useAdminFetch<T>(
  fetchFn: (token: string) => Promise<T>,
  deps: unknown[],
  cacheKey?: string,
) {
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const token = getAdminToken()

  const fetchRef = useRef(fetchFn)
  fetchRef.current = fetchFn

  const refetch = useCallback(async () => {
    if (!token) return

    // Check cache
    if (cacheKey) {
      const cached = cache.get(cacheKey)
      if (cached && Date.now() < cached.expiry) {
        setData(cached.data as T)
        setLoading(false)
        return
      }
    }

    setLoading(true)
    setError(null)
    try {
      const result = await fetchRef.current(token)
      if (cacheKey) {
        cache.set(cacheKey, { data: result, expiry: Date.now() + CACHE_TTL })
      }
      setData(result)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'An error occurred')
    } finally {
      setLoading(false)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps, react-hooks/use-memo
  }, [token, ...deps])

  useEffect(() => { refetch() }, [refetch])

  return { data, loading, error, refetch }
}
