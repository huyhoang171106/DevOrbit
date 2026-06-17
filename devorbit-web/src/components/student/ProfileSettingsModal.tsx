import { useState } from 'react'
import { m as motion, AnimatePresence } from 'framer-motion'
import { X, User, Lock, CheckCircle, Warning } from '@phosphor-icons/react'
import { apiStudentPost } from '../../lib/api'
import type { StudentProfileResponse } from '../../types/api'

interface ProfileSettingsModalProps {
  open: boolean
  onClose: () => void
  profile: StudentProfileResponse
  onProfileUpdate: (updated: StudentProfileResponse) => void
}

type Tab = 'name' | 'password'

export function ProfileSettingsModal({ open, onClose, profile, onProfileUpdate }: ProfileSettingsModalProps) {
  const [activeTab, setActiveTab] = useState<Tab>('name')
  const [fullName, setFullName] = useState(profile.fullName)
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState('')
  const [error, setError] = useState('')

  function resetState() {
    setFullName(profile.fullName)
    setCurrentPassword('')
    setNewPassword('')
    setConfirmPassword('')
    setSuccess('')
    setError('')
  }

  function handleClose() {
    resetState()
    onClose()
  }

  async function handleUpdateName() {
    if (!fullName.trim() || fullName.trim() === profile.fullName) return
    setLoading(true)
    setError('')
    try {
      const updated = await apiStudentPost<StudentProfileResponse>('/api/student/me/name', { fullName: fullName.trim() })
      onProfileUpdate(updated)
      setSuccess('Đã cập nhật tên hiển thị')
      setTimeout(() => setSuccess(''), 2000)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Có lỗi xảy ra')
    } finally {
      setLoading(false)
    }
  }

  async function handleChangePassword() {
    if (!currentPassword || !newPassword) return
    if (newPassword !== confirmPassword) {
      setError('Mật khẩu xác nhận không khớp')
      return
    }
    if (newPassword.length < 6) {
      setError('Mật khẩu mới phải từ 6 ký tự')
      return
    }
    setLoading(true)
    setError('')
    try {
      await apiStudentPost('/api/student/me/password', { currentPassword, newPassword })
      setSuccess('Đổi mật khẩu thành công')
      setCurrentPassword('')
      setNewPassword('')
      setConfirmPassword('')
      setTimeout(() => setSuccess(''), 2000)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Có lỗi xảy ra')
    } finally {
      setLoading(false)
    }
  }

  return (
    <AnimatePresence>
      {open && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.95 }}
            transition={{ duration: 0.2 }}
            className="bg-orbit-surface border border-orbit-border rounded-2xl w-full max-w-md shadow-2xl overflow-hidden"
          >
            {/* Header */}
            <div className="flex items-center justify-between px-6 py-4 border-b border-orbit-border">
              <h3 className="font-heading text-base font-bold text-orbit-text">Cài đặt tài khoản</h3>
              <button
                onClick={handleClose}
                className="p-1.5 rounded-lg text-orbit-text-muted hover:text-orbit-text hover:bg-orbit-elevated transition-colors"
              >
                <X className="h-4 w-4" weight="bold" />
              </button>
            </div>

            {/* Tabs */}
            <div className="flex border-b border-orbit-border">
              <button
                onClick={() => { setActiveTab('name'); setError(''); setSuccess('') }}
                className={`flex-1 flex items-center justify-center gap-2 px-4 py-3 text-[13px] font-semibold transition-colors ${
                  activeTab === 'name'
                    ? 'text-orbit-accent border-b-2 border-orbit-accent'
                    : 'text-orbit-text-muted hover:text-orbit-text'
                }`}
              >
                <User className="h-4 w-4" weight={activeTab === 'name' ? 'fill' : 'regular'} />
                Đổi tên
              </button>
              <button
                onClick={() => { setActiveTab('password'); setError(''); setSuccess('') }}
                className={`flex-1 flex items-center justify-center gap-2 px-4 py-3 text-[13px] font-semibold transition-colors ${
                  activeTab === 'password'
                    ? 'text-orbit-accent border-b-2 border-orbit-accent'
                    : 'text-orbit-text-muted hover:text-orbit-text'
                }`}
              >
                <Lock className="h-4 w-4" weight={activeTab === 'password' ? 'fill' : 'regular'} />
                Đổi mật khẩu
              </button>
            </div>

            {/* Content */}
            <div className="px-6 py-5">
              {activeTab === 'name' ? (
                <div className="space-y-4">
                  <div>
                    <label className="block text-[12px] font-semibold text-orbit-text-secondary mb-1.5">Họ tên hiện tại</label>
                    <p className="text-[14px] text-orbit-text font-medium">{profile.fullName}</p>
                  </div>
                  <div>
                    <label className="block text-[12px] font-semibold text-orbit-text-secondary mb-1.5">Họ tên mới</label>
                    <input
                      type="text"
                      value={fullName}
                      onChange={(e) => setFullName(e.target.value)}
                      className="w-full px-4 py-2.5 rounded-xl bg-orbit-elevated border border-orbit-border text-[14px] text-orbit-text placeholder-orbit-text-muted focus:outline-none focus:border-orbit-accent/50 transition-colors"
                      placeholder="Nhập họ tên mới"
                    />
                  </div>
                </div>
              ) : (
                <div className="space-y-4">
                  <div>
                    <label className="block text-[12px] font-semibold text-orbit-text-secondary mb-1.5">Mật khẩu hiện tại</label>
                    <input
                      type="password"
                      value={currentPassword}
                      onChange={(e) => setCurrentPassword(e.target.value)}
                      className="w-full px-4 py-2.5 rounded-xl bg-orbit-elevated border border-orbit-border text-[14px] text-orbit-text placeholder-orbit-text-muted focus:outline-none focus:border-orbit-accent/50 transition-colors"
                      placeholder="Nhập mật khẩu hiện tại"
                    />
                  </div>
                  <div>
                    <label className="block text-[12px] font-semibold text-orbit-text-secondary mb-1.5">Mật khẩu mới</label>
                    <input
                      type="password"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      className="w-full px-4 py-2.5 rounded-xl bg-orbit-elevated border border-orbit-border text-[14px] text-orbit-text placeholder-orbit-text-muted focus:outline-none focus:border-orbit-accent/50 transition-colors"
                      placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)"
                    />
                  </div>
                  <div>
                    <label className="block text-[12px] font-semibold text-orbit-text-secondary mb-1.5">Xác nhận mật khẩu mới</label>
                    <input
                      type="password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      className="w-full px-4 py-2.5 rounded-xl bg-orbit-elevated border border-orbit-border text-[14px] text-orbit-text placeholder-orbit-text-muted focus:outline-none focus:border-orbit-accent/50 transition-colors"
                      placeholder="Nhập lại mật khẩu mới"
                    />
                  </div>
                </div>
              )}

              {/* Messages */}
              {error && (
                <div className="mt-4 flex items-center gap-2 text-[13px] text-red-400 font-medium">
                  <Warning className="h-4 w-4 shrink-0" weight="fill" />
                  {error}
                </div>
              )}
              {success && (
                <div className="mt-4 flex items-center gap-2 text-[13px] text-emerald-400 font-medium">
                  <CheckCircle className="h-4 w-4 shrink-0" weight="fill" />
                  {success}
                </div>
              )}
            </div>

            {/* Footer */}
            <div className="flex justify-end gap-3 px-6 py-4 border-t border-orbit-border">
              <button
                onClick={handleClose}
                className="px-4 py-2 rounded-xl text-[13px] font-semibold text-orbit-text-muted hover:text-orbit-text border border-orbit-border hover:border-orbit-accent/30 transition-colors"
                disabled={loading}
              >
                Đóng
              </button>
              <button
                onClick={activeTab === 'name' ? handleUpdateName : handleChangePassword}
                disabled={loading || (activeTab === 'name' && (!fullName.trim() || fullName.trim() === profile.fullName)) || (activeTab === 'password' && (!currentPassword || !newPassword || !confirmPassword))}
                className="px-5 py-2 rounded-xl text-[13px] font-bold text-white bg-orbit-accent hover:bg-orbit-accent/90 transition-colors disabled:opacity-40 disabled:cursor-not-allowed flex items-center gap-2"
              >
                {loading ? (
                  <>
                    <svg className="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                    </svg>
                    Đang lưu...
                  </>
                ) : (
                  'Lưu'
                )}
              </button>
            </div>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  )
}
