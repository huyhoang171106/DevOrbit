import { useState, useEffect, useCallback } from 'react'
import { adminApi } from '../lib/adminApi'
import { getAdminToken } from '../lib/auth'
import type { AdminNotification } from '../types/admin'

export function useNotifications(enabled = false, pollInterval = 15000) {
  const [notifications, setNotifications] = useState<AdminNotification[]>([])
  const [unreadCount, setUnreadCount] = useState(0)
  const [loading, setLoading] = useState(true)

  const token = getAdminToken()

  const fetchAll = useCallback(async () => {
    if (!token) return
    try {
      const [notes, countRes] = await Promise.all([
        adminApi.getNotifications(token),
        adminApi.getUnreadNotificationCount(token),
      ])
      setNotifications(notes)
      setUnreadCount(countRes.count)
    } catch {
      // silently fail
    } finally {
      setLoading(false)
    }
  }, [token])

  const fetchCountOnly = useCallback(async () => {
    if (!token) return
    try {
      const countRes = await adminApi.getUnreadNotificationCount(token)
      setUnreadCount(countRes.count)
    } catch { /* ignore */ }
  }, [token])

  // Fetch count every 60s even when closed (for badge)
  useEffect(() => {
    fetchCountOnly()
    const interval = setInterval(fetchCountOnly, 60000)
    return () => clearInterval(interval)
  }, [fetchCountOnly])

  // Full fetch + poll only when dropdown is open
  useEffect(() => {
    if (!enabled) {
      setLoading(false)
      return
    }
    fetchAll()
    const interval = setInterval(fetchAll, pollInterval)
    return () => clearInterval(interval)
  }, [fetchAll, pollInterval, enabled])

  const markAsRead = useCallback(async (id: number) => {
    if (!token) return
    try {
      await adminApi.markNotificationRead(token, id)
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
      )
      setUnreadCount((prev) => Math.max(0, prev - 1))
    } catch {
      // silently fail
    }
  }, [token])

  const markAllAsRead = useCallback(async () => {
    if (!token) return
    setNotifications((prev) =>
      prev.map((n) => ({ ...n, isRead: true }))
    )
    setUnreadCount(0)
    try {
      await adminApi.markAllNotificationsRead(token)
    } catch {
      // silently fail
    }
  }, [token])

  return { notifications, unreadCount, loading, refetch: fetchAll, markAsRead, markAllAsRead }
}
