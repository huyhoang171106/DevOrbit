import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { isAuthenticated } from './auth'

/**
 * Redirects unauthenticated users to the admin login page.
 * Call at the top of any admin page component.
 */
export function useRequireAuth(): void {
  const navigate = useNavigate()
  useEffect(() => {
    if (!isAuthenticated()) navigate('/admin/login')
  }, [navigate])
}

/**
 * Fetches data from the API and manages loading/error state.
 * Calls `fetchFn` on mount and whenever dependencies change.
 */
export function useApiFetch<T>(
  fetchFn: () => Promise<T>,
  deps: React.DependencyList = [],
): { data: T | null; loading: boolean; refetch: () => void } {
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState(true)

  const fetch = useCallback(async () => {
    setLoading(true)
    try {
      const result = await fetchFn()
      setData(result)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps)

  useEffect(() => { fetch() }, [fetch])

  return { data, loading, refetch: fetch }
}
