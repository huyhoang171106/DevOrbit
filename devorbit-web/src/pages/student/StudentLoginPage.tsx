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
    <div className="flex min-h-[70vh] items-center justify-center">
      <div className="glass-card w-full max-w-md p-8">
        <div className="mb-6 text-center">
          <p className="mb-2 text-xs font-semibold uppercase tracking-[0.3em] text-cyan-300">Phase 2</p>
          <h1 className="font-heading text-2xl font-bold text-slate-100">
            {mode === 'login' ? 'Student Login' : 'Create Student Account'}
          </h1>
          <p className="mt-2 text-sm text-slate-400">Save courses and repositories to continue learning later.</p>
        </div>
        {error && <div className="mb-4 rounded-xl bg-red-500/10 px-4 py-2.5 text-sm text-red-400">{error}</div>}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label">Student code</label>
            <input className="input-field" value={studentCode} onChange={(e) => setStudentCode(e.target.value)} required />
          </div>
          {mode === 'register' && (
            <>
              <div>
                <label className="label">Full name</label>
                <input className="input-field" value={fullName} onChange={(e) => setFullName(e.target.value)} required />
              </div>
              <div>
                <label className="label">Email</label>
                <input className="input-field" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
              </div>
            </>
          )}
          <div>
            <label className="label">Password</label>
            <input className="input-field" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required minLength={6} />
          </div>
          <button className="btn-primary w-full" type="submit" disabled={loading}>
            {loading ? 'Please wait...' : mode === 'login' ? 'Log In' : 'Register'}
          </button>
        </form>
        <div className="mt-5 flex items-center justify-between text-sm">
          <button className="text-cyan-300 hover:text-cyan-200" type="button" onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
            {mode === 'login' ? 'Create an account' : 'Use existing account'}
          </button>
          <Link to="/courses" className="text-slate-500 hover:text-slate-300">Continue browsing</Link>
        </div>
      </div>
    </div>
  )
}
