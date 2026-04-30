const TOKEN_KEY = 'devorbit-admin-token'
const STUDENT_TOKEN_KEY = 'devorbit-student-token'

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

export function getStudentToken(): string | null {
  return localStorage.getItem(STUDENT_TOKEN_KEY)
}

export function saveStudentToken(token: string): void {
  localStorage.setItem(STUDENT_TOKEN_KEY, token)
}

export function clearStudentToken(): void {
  localStorage.removeItem(STUDENT_TOKEN_KEY)
}

export function isStudentAuthenticated(): boolean {
  return localStorage.getItem(STUDENT_TOKEN_KEY) !== null
}
