import { ADMIN_TOKEN_KEY, getAdminToken, clearAdminToken } from './auth'

export { getAdminToken, clearAdminToken }

const SIDEBAR_KEY = 'devorbit-admin-sidebar-collapsed'

export function setAdminToken(token: string): void {
  localStorage.setItem(ADMIN_TOKEN_KEY, token)
}

export function isAdminAuthenticated(): boolean {
  return !!getAdminToken()
}

export function getSidebarCollapsed(): boolean {
  return localStorage.getItem(SIDEBAR_KEY) === 'true'
}

export function setSidebarCollapsed(collapsed: boolean): void {
  localStorage.setItem(SIDEBAR_KEY, String(collapsed))
}
