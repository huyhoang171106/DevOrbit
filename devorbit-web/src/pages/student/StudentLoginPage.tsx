import { useState, useEffect, useRef, type FormEvent, type KeyboardEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiPost } from '../../lib/api'
import { saveStudentToken } from '../../lib/auth'
import type { StudentAuthResponse, OtpVerificationRequest } from '../../types/api'

const EyeIcon = ({ slash }: { slash: boolean }) => (
  <svg className="h-[20px] w-[20px]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
    {slash ? (
      <>
        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94" />
        <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19" />
        <line x1="1" y1="1" x2="23" y2="23" />
      </>
    ) : (
      <>
        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
        <circle cx="12" cy="12" r="3" />
      </>
    )}
  </svg>
)

function PasswordInput({ id, label, value, onChange, placeholder }: {
  id: string; label: string; value: string; onChange: (v: string) => void; placeholder: string
}) {
  const [show, setShow] = useState(false)
  return (
    <div>
      <label htmlFor={id} className="label block mb-[6px]">{label}</label>
      <div className="relative">
        <input
          id={id}
          className="input-field pr-[40px]"
          type={show ? 'text' : 'password'}
          placeholder={placeholder}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          required
          minLength={6}
        />
        <button
          type="button"
          className="absolute right-[10px] top-1/2 -translate-y-1/2 text-zinc-400 hover:text-zinc-300 cursor-pointer"
          onClick={() => setShow((p) => !p)}
          tabIndex={-1}
        >
          <EyeIcon slash={show} />
        </button>
      </div>
    </div>
  )
}

// ─── OTP Digit Input ────────────────────────────

function DigitInput({ id, value, onChange, onKeyDown, onPaste, autoFocus }: {
  id?: string; value: string; onChange: (v: string) => void; onKeyDown: (e: KeyboardEvent<HTMLInputElement>) => void
  onPaste?: (e: React.ClipboardEvent) => void; autoFocus?: boolean
}) {
  const ref = useRef<HTMLInputElement>(null)
  const filled = value !== ''

  useEffect(() => {
    if (autoFocus && ref.current) ref.current.focus()
  }, [autoFocus])

  return (
    <input
      ref={ref}
      id={id}
      inputMode="numeric"
      autoComplete="one-time-code"
      className={`w-[44px] h-[52px] text-center text-[20px] font-bold rounded-xl border transition-all duration-200 outline-none bg-white/5
        ${filled
          ? 'border-emerald-500/50 bg-emerald-500/5 text-emerald-400 shadow-[0_0_16px_rgba(52,211,153,0.08)]'
          : 'border-white/10 text-white placeholder:text-white/30'
        }
        focus:border-emerald-500/50 focus:ring-4 focus:ring-emerald-500/5`}
      maxLength={1}
      value={value}
      onChange={(e) => { const v = e.target.value.replace(/\D/g, '').slice(0, 1); onChange(v) }}
      onKeyDown={onKeyDown}
      onPaste={onPaste}
      required
    />
  )
}

export function StudentLoginPage() {
  const navigate = useNavigate()
  const [mode, setMode] = useState<'login' | 'register' | 'otp' | 'forgot'>('login')
  const [forgotStep, setForgotStep] = useState<1 | 2>(1)
  const [studentCode, setStudentCode] = useState('')
  const [fullName, setFullName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [otpDigits, setOtpDigits] = useState<string[]>(() => Array(6).fill(''))
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [countdown, setCountdown] = useState(0)
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null)

  const otpCode = otpDigits.join('')

  function startCountdown() {
    setCountdown(60)
    if (timerRef.current) clearInterval(timerRef.current)
    timerRef.current = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          if (timerRef.current) clearInterval(timerRef.current)
          return 0
        }
        return prev - 1
      })
    }, 1000)
  }

  useEffect(() => {
    return () => { if (timerRef.current) clearInterval(timerRef.current) }
  }, [])

  async function handleResendOtp() {
    setError('')
    setLoading(true)
    try {
      if (mode === 'forgot' && forgotStep === 2) {
        await apiPost('/api/student/forgot-password', { studentCode })
      } else {
        await apiPost('/api/student/resend-otp', { email })
      }
      startCountdown()
    } catch {
      setError('Gửi lại mã OTP thất bại.')
    } finally {
      setLoading(false)
    }
  }

  function switchMode(newMode: 'login' | 'register' | 'forgot') {
    setError('')
    if (newMode !== mode) {
      setStudentCode('')
      setPassword('')
      setConfirmPassword('')
      setOtpDigits(Array(6).fill(''))
      setEmail('')
      setFullName('')
      setForgotStep(1)
      setCountdown(0)
      if (timerRef.current) clearInterval(timerRef.current)
    }
    setMode(newMode)
  }

  // ─── OTP digit handlers ───────────────────────

  function handleOtpPaste(e: React.ClipboardEvent) {
    e.preventDefault()
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6)
    if (!pasted) return
    const digits = pasted.split('')
    setOtpDigits((prev) => {
      const next = [...prev]
      for (let i = 0; i < 6; i++) {
        next[i] = digits[i] || ''
      }
      return next
    })
    // Focus last filled or last input
    const focusIndex = Math.min(digits.length, 5)
    const target = document.getElementById(`otp-${focusIndex}`)
    target?.focus()
  }

  function handleOtpDigitChange(index: number, value: string) {
    if (value && index < 5) {
      const next = document.getElementById(`otp-${index + 1}`)
      next?.focus()
    }
    setOtpDigits((prev) => {
      const next = [...prev]
      next[index] = value
      return next
    })
  }

  function handleOtpKeyDown(index: number, e: KeyboardEvent<HTMLInputElement>) {
    if (e.key === 'Backspace' && !otpDigits[index] && index > 0) {
      const prev = document.getElementById(`otp-${index - 1}`)
      prev?.focus()
    }
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setLoading(true)

    if (mode === 'register') {
      if (password !== confirmPassword) {
        setError('Mật khẩu nhập lại không khớp.')
        setLoading(false)
        return
      }
      try {
        const res = await apiPost<{ id: number; studentCode: string; fullName: string; email: string }>(
          '/api/student/register',
          { studentCode, fullName, email, password }
        )
        setEmail(res.email)
        setMode('otp')
        startCountdown()
      } catch {
        setError('Đăng ký thất bại. Kiểm tra lại thông tin.')
      } finally {
        setLoading(false)
      }
      return
    }

    if (mode === 'otp') {
      try {
        const body: OtpVerificationRequest = { email, otpCode }
        const res = await apiPost<StudentAuthResponse>('/api/student/verify-otp', body)
        saveStudentToken(res.token)
        navigate('/')
      } catch (e) {
        console.error('OTP verify error:', e)
        setError(e instanceof Error ? e.message : 'Mã OTP không đúng hoặc đã hết hạn.')
      } finally {
        setLoading(false)
      }
      return
    }

    if (mode === 'forgot') {
      if (forgotStep === 1) {
        try {
          await apiPost<{ message: string }>('/api/student/forgot-password', { studentCode })
          setEmail('')
          setForgotStep(2)
          startCountdown()
        } catch (e) {
          const msg = e instanceof Error ? e.message : 'Gửi yêu cầu thất bại.'
          setError(msg)
        } finally {
          setLoading(false)
        }
      } else {
        if (password !== confirmPassword) {
          setError('Mật khẩu nhập lại không khớp.')
          setLoading(false)
          return
        }
        try {
          const res = await apiPost<StudentAuthResponse>('/api/student/reset-password', {
            studentCode, otpCode, newPassword: password,
          })
          saveStudentToken(res.token)
          navigate('/')
        } catch (e) {
          const msg = e instanceof Error ? e.message : 'Đặt lại mật khẩu thất bại.'
          setError(msg)
        } finally {
          setLoading(false)
        }
      }
      return
    }

    try {
      const res = await apiPost<StudentAuthResponse>('/api/student/login', { studentCode, password })
      saveStudentToken(res.token)
      navigate('/')
    } catch (e) {
      const msg = e instanceof Error ? e.message : 'Đăng nhập thất bại.'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-[65vh] items-center justify-center py-[64px]">
      <div className="glass-card w-full max-w-md p-[32px] mx-4">
        <div className="mb-[24px] text-center">
          <div className="mx-auto mb-[12px] inline-flex rounded-[8px] bg-emerald-500/10 p-[10px] border border-emerald-500/20">
            {mode === 'otp' ? (
              <svg className="h-[22px] w-[22px] text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <rect x="2" y="4" width="20" height="16" rx="2" />
                <path d="M6 8h.01M10 8h.01M14 8h.01" />
                <path d="M6 12h.01M10 12h.01M14 12h.01" />
                <path d="M6 16h.01M10 16h.01M14 16h.01" />
              </svg>
            ) : mode === 'forgot' ? (
              <svg className="h-[22px] w-[22px] text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
              </svg>
            ) : (
              <svg className="h-[22px] w-[22px] text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
            )}
          </div>
          <h1 className="heading-2 text-ink">
            {mode === 'login' ? 'Đăng nhập' : mode === 'register' ? 'Tạo tài khoản' : mode === 'otp' ? 'Xác thực email' : 'Quên mật khẩu'}
          </h1>
          {(mode === 'otp' || (mode === 'forgot' && forgotStep === 2)) && (
            <p className="body-sm text-ink/60 mt-[4px]">
              Mã OTP đã được gửi đến <strong>{mode === 'forgot' ? 'email của tài khoản' : email}</strong>
            </p>
          )}
        </div>

        {error && (
          <div className="mb-[20px] flex items-center gap-[8px] rounded-md bg-red-500/10 border border-red-500/20 px-4 py-3 body-sm text-red-600 dark:text-red-400">
            <svg className="h-[16px] w-[16px] flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <path d="M12 8v4M12 16h.01" />
            </svg>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-[14px]">
          {/* ─── LOGIN MODE ─────────────────────────── */}
          {mode === 'login' && (
            <>
              <div>
                <label htmlFor="login-code-input" className="label block mb-[6px]">Tên đăng nhập</label>
                <input id="login-code-input" className="input-field" placeholder="Nhập tên đăng nhập"
                  value={studentCode} onChange={(e) => setStudentCode(e.target.value)} required />
              </div>
              <PasswordInput id="login-password-input" label="Mật khẩu" placeholder="Tối thiểu 6 ký tự"
                value={password} onChange={setPassword} />
              <div className="flex justify-end body-sm">
                <button type="button" className="text-emerald-400 hover:text-emerald-400/80 font-medium transition-colors cursor-pointer"
                  onClick={() => switchMode('forgot')}>
                  Quên mật khẩu?
                </button>
              </div>
              <button id="auth-submit-btn" className="btn-primary w-full" type="submit" disabled={loading}>
                {loading ? <span className="flex items-center justify-center gap-[8px]"><svg className="h-[16px] w-[16px] animate-spin" viewBox="0 0 24 24" fill="none"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" /><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" /></svg>Vui lòng đợi...</span> : 'Đăng nhập'}
              </button>
            </>
          )}

          {/* ─── REGISTER MODE ──────────────────────── */}
          {mode === 'register' && (
            <>
              <div>
                <label htmlFor="reg-code-input" className="label block mb-[6px]">Tên đăng nhập</label>
                <input id="reg-code-input" className="input-field" placeholder="Nhập tên đăng nhập"
                  value={studentCode} onChange={(e) => setStudentCode(e.target.value)} required />
              </div>
              <div>
                <label htmlFor="reg-name-input" className="label block mb-[6px]">Họ và tên</label>
                <input id="reg-name-input" className="input-field" placeholder="Ví dụ: Nguyễn Văn A"
                  value={fullName} onChange={(e) => setFullName(e.target.value)} required />
              </div>
              <div>
                <label htmlFor="reg-email-input" className="label block mb-[6px]">Email</label>
                <input id="reg-email-input" className="input-field" type="email" placeholder="Ví dụ: 24520554@gm.uit.edu.vn"
                  value={email} onChange={(e) => setEmail(e.target.value)} required />
              </div>
              <PasswordInput id="reg-password-input" label="Mật khẩu" placeholder="Tối thiểu 6 ký tự"
                value={password} onChange={setPassword} />
              <PasswordInput id="reg-confirm-password-input" label="Nhập lại mật khẩu" placeholder="Xác nhận mật khẩu"
                value={confirmPassword} onChange={setConfirmPassword} />
              <button id="auth-submit-btn" className="btn-primary w-full" type="submit" disabled={loading}>
                {loading ? <span className="flex items-center justify-center gap-[8px]"><svg className="h-[16px] w-[16px] animate-spin" viewBox="0 0 24 24" fill="none"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" /><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" /></svg>Vui lòng đợi...</span> : 'Đăng ký'}
              </button>
            </>
          )}

          {/* ─── OTP MODE ───────────────────────────── */}
          {mode === 'otp' && (
            <>
              <div>
                <label className="label block mb-[10px] text-center">Mã xác thực OTP</label>
                <div className="flex justify-center gap-[8px]" onPaste={handleOtpPaste}>
                  {otpDigits.map((d, i) => (
                    <DigitInput
                      key={i}
                      id={`otp-${i}`}
                      value={d}
                      onChange={(v) => handleOtpDigitChange(i, v)}
                      onKeyDown={(e) => handleOtpKeyDown(i, e)}
                      autoFocus={i === 0}
                    />
                  ))}
                </div>
              </div>
              {countdown > 0 ? (
                <p className="body-sm text-ink/60 text-center">
                  Mã OTP còn hiệu lực trong <strong className="text-emerald-400">{countdown}</strong> giây
                </p>
              ) : (
                <div className="flex justify-center">
                  <button type="button" className="text-emerald-400 hover:text-emerald-400/80 font-medium transition-colors cursor-pointer body-sm"
                    onClick={handleResendOtp} disabled={loading}>
                    Gửi lại mã OTP
                  </button>
                </div>
              )}
              <button id="auth-submit-btn" className="btn-primary w-full" type="submit" disabled={loading || otpCode.length < 6}>
                {loading ? <span className="flex items-center justify-center gap-[8px]"><svg className="h-[16px] w-[16px] animate-spin" viewBox="0 0 24 24" fill="none"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" /><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" /></svg>Vui lòng đợi...</span> : 'Xác thực'}
              </button>
            </>
          )}

          {/* ─── FORGOT MODE ────────────────────────── */}
          {mode === 'forgot' && (
            <>
              <div>
                <label htmlFor="forgot-code-input" className="label block mb-[6px]">Tên đăng nhập</label>
                <input id="forgot-code-input" className="input-field" placeholder="Nhập tên đăng nhập"
                  value={studentCode} onChange={(e) => setStudentCode(e.target.value)} required />
              </div>

              {forgotStep === 1 ? (
                <button id="auth-submit-btn" className="btn-primary w-full" type="submit" disabled={loading}>
                  {loading ? <span className="flex items-center justify-center gap-[8px]"><svg className="h-[16px] w-[16px] animate-spin" viewBox="0 0 24 24" fill="none"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" /><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" /></svg>Vui lòng đợi...</span> : 'Gửi mã OTP'}
                </button>
              ) : (
                <>
                  <div>
                    <label className="label block mb-[10px] text-center">Mã OTP đặt lại mật khẩu</label>
                    <div className="flex justify-center gap-[8px]" onPaste={handleOtpPaste}>
                      {otpDigits.map((d, i) => (
                        <DigitInput
                          key={i}
                          id={`otp-${i}`}
                          value={d}
                          onChange={(v) => handleOtpDigitChange(i, v)}
                          onKeyDown={(e) => handleOtpKeyDown(i, e)}
                          autoFocus={i === 0}
                        />
                      ))}
                    </div>
                  </div>
                  {countdown > 0 ? (
                    <p className="body-sm text-ink/60 text-center">
                      Mã OTP còn hiệu lực trong <strong className="text-emerald-400">{countdown}</strong> giây
                    </p>
                  ) : (
                    <div className="flex justify-center">
                      <button type="button" className="text-emerald-400 hover:text-emerald-400/80 font-medium transition-colors cursor-pointer body-sm"
                        onClick={handleResendOtp} disabled={loading}>
                        Gửi lại mã OTP
                      </button>
                    </div>
                  )}
                  <PasswordInput id="reset-password-input" label="Mật khẩu mới" placeholder="Tối thiểu 6 ký tự"
                    value={password} onChange={setPassword} />
                  <PasswordInput id="reset-confirm-password-input" label="Nhập lại mật khẩu" placeholder="Xác nhận mật khẩu"
                    value={confirmPassword} onChange={setConfirmPassword} />
                  <button id="auth-submit-btn" className="btn-primary w-full" type="submit" disabled={loading || otpCode.length < 6}>
                    {loading ? <span className="flex items-center justify-center gap-[8px]"><svg className="h-[16px] w-[16px] animate-spin" viewBox="0 0 24 24" fill="none"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" /><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" /></svg>Vui lòng đợi...</span> : 'Đặt lại mật khẩu'}
                  </button>
                </>
              )}
            </>
          )}
        </form>

        {/* ─── FOOTER LINKS ─────────────────────────── */}
        {mode === 'otp' && (
          <div className="mt-[20px] pt-[20px] border-t border-glass-border flex items-center justify-center body-sm">
            <button type="button" className="text-emerald-400 hover:text-emerald-400/80 font-medium transition-colors cursor-pointer"
              onClick={() => switchMode('login')}>
              Quay lại đăng nhập
            </button>
          </div>
        )}

        {mode === 'forgot' && (
          <div className="mt-[20px] pt-[20px] border-t border-glass-border flex items-center justify-center body-sm">
            <button type="button" className="text-emerald-400 hover:text-emerald-400/80 font-medium transition-colors cursor-pointer"
              onClick={() => switchMode('login')}>
              Quay lại đăng nhập
            </button>
          </div>
        )}

        {mode !== 'otp' && mode !== 'forgot' && (
          <div className="mt-[20px] pt-[20px] border-t border-glass-border flex items-center justify-center body-sm text-zinc-400">
            <span>{mode === 'login' ? 'Chưa có tài khoản? ' : 'Đã có tài khoản? '}</span>
            <button type="button" className="text-emerald-400 hover:text-emerald-400/80 font-medium transition-colors cursor-pointer ml-1"
              onClick={() => switchMode(mode === 'login' ? 'register' : 'login')}>
              {mode === 'login' ? 'Đăng ký ngay' : 'Đăng nhập ngay'}
            </button>
          </div>
        )}
      </div>
    </div>
  )
}
