import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { apiPost } from '../../lib/api'
import { saveStudentToken } from '../../lib/auth'
import type { StudentAuthResponse } from '../../types/api'

export function StudentLoginPage() {
  const navigate = useNavigate()
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [studentCode, setStudentCode] = useState('')
  const [fullName, setFullName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const path = mode === 'login' ? '/api/student/login' : '/api/student/register'
      const body = mode === 'login' ? { studentCode, password } : { studentCode, fullName, email, password }
      const res = await apiPost<StudentAuthResponse>(path, body)
      saveStudentToken(res.token)
      navigate('/student/bookmarks')
    } catch {
      setError(mode === 'login' ? 'Login failed. Check your credentials.' : 'Registration failed. Check your information.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-[65vh] items-center justify-center py-[64px]">
      <div className="card-base w-full max-w-md p-[32px] mx-4">
        <div className="mb-[32px] text-center">
          <div className="mx-auto mb-[16px] inline-flex rounded-[8px] bg-brand-green/10 p-[12px] border border-brand-green/20">
            <svg className="h-[24px] w-[24px] text-brand-green" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </div>
          <p className="body-sm text-brand-green mb-[8px] font-medium tracking-wide uppercase">Phase 2</p>
          <h1 className="heading-3 text-ink mb-[8px]">
            {mode === 'login' ? 'Student Login' : 'Create Account'}
          </h1>
          <p className="body-sm text-steel">
            Save courses and repositories to continue learning later.
          </p>
        </div>

        {error && (
          <div className="mb-[24px] flex items-center gap-[8px] rounded-md bg-red-50 border border-red-100 px-4 py-3 body-sm text-red-600">
            <svg className="h-[16px] w-[16px] flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <path d="M12 8v4M12 16h.01" />
            </svg>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-[16px]">
          <div>
            <label className="label block mb-[6px]">Student code</label>
            <input
              className="input-field"
              value={studentCode}
              onChange={(e) => setStudentCode(e.target.value)}
              required
            />
          </div>

          {mode === 'register' && (
            <>
              <div>
                <label className="label block mb-[6px]">Full name</label>
                <input
                  className="input-field"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  required
                />
              </div>
              <div>
                <label className="label block mb-[6px]">Email</label>
                <input
                  className="input-field"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
            </>
          )}

          <div>
            <label className="label block mb-[6px]">Password</label>
            <input
              className="input-field"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={6}
            />
          </div>

          <button className="btn-primary w-full mt-2" type="submit" disabled={loading}>
            {loading ? (
              <span className="flex items-center justify-center gap-[8px]">
                <svg className="h-[16px] w-[16px] animate-spin" viewBox="0 0 24 24" fill="none">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                </svg>
                Please wait...
              </span>
            ) : mode === 'login' ? (
              'Log In'
            ) : (
              'Register'
            )}
          </button>
        </form>

        <div className="mt-[24px] pt-[24px] border-t border-hairline-soft flex items-center justify-between body-sm">
          <button
            className="text-brand-green hover:text-brand-green/80 font-medium transition-colors cursor-pointer"
            type="button"
            onClick={() => setMode(mode === 'login' ? 'register' : 'login')}
          >
            {mode === 'login' ? 'Create an account' : 'Use existing account'}
          </button>
          <Link to="/courses" className="text-steel hover:text-ink transition-colors">
            Continue browsing
          </Link>
        </div>
      </div>
    </div>
  )
}
