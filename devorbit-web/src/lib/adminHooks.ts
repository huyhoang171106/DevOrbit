import { useState, useEffect, useCallback, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { getAdminToken } from './adminAuth'

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
) {
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const token = getAdminToken()

  const fetchRef = useRef(fetchFn)
  fetchRef.current = fetchFn

  const refetch = useCallback(async () => {
    if (!token) return
    setLoading(true)
    setError(null)
    try {
      const result = await fetchRef.current(token)
      setData(result)
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'An error occurred')
    } finally {
      setLoading(false)
    }
  }, [token, ...deps])

  useEffect(() => { refetch() }, [refetch])

  return { data, loading, error, refetch }
}
