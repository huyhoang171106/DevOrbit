const TOKEN_KEY = 'devorbit-admin-token'

export function getAdminToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function saveAdminToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearAdminToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function isAuthenticated(): boolean {
  return localStorage.getItem(TOKEN_KEY) !== null
}

export function logout(): void {
  clearAdminToken()
}
