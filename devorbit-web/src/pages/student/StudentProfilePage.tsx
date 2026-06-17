import { useEffect, useState, useRef } from 'react'
import { Link } from 'react-router-dom'
import { m as motion } from 'framer-motion'
import { BookOpen, GithubLogo, BookmarkSimple, Trash, ArrowSquareOut, Camera, CheckCircle, Gear, User, Lock, Warning } from '@phosphor-icons/react'
import { apiStudentGet, apiStudentDelete, apiStudentPatch } from '../../lib/api'
import { isStudentAuthenticated } from '../../lib/auth'
import { Avatar } from '../../components/shared/Avatar'
import type { StudentProfileResponse, StudentBookmark } from '../../types/api'

export function StudentProfilePage() {
  const [profile, setProfile] = useState<StudentProfileResponse | null>(null)
  const [bookmarks, setBookmarks] = useState<StudentBookmark[]>([])
  const [loading, setLoading] = useState(true)
  const [uploading, setUploading] = useState(false)
  const [uploadSuccess, setUploadSuccess] = useState(false)
  const [previewUrl, setPreviewUrl] = useState<string | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const [fullName, setFullName] = useState('')
  const [savingName, setSavingName] = useState(false)
  const [nameSuccess, setNameSuccess] = useState(false)
  const [nameError, setNameError] = useState('')

  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [savingPassword, setSavingPassword] = useState(false)
  const [passwordSuccess, setPasswordSuccess] = useState(false)
  const [passwordError, setPasswordError] = useState('')

  useEffect(() => {
    if (!isStudentAuthenticated()) {
      setLoading(false)
      return
    }
    Promise.all([
      apiStudentGet<StudentProfileResponse>('/api/student/me'),
      apiStudentGet<StudentBookmark[]>('/api/student/bookmarks'),
    ])
      .then(([profileData, bookmarksData]) => {
        setProfile(profileData)
        setFullName(profileData.fullName)
        setBookmarks(bookmarksData)
      })
      .finally(() => setLoading(false))
  }, [])

  async function handleRemove(bookmarkId: number) {
    try {
      await apiStudentDelete(`/api/student/bookmarks/${bookmarkId}`)
      setBookmarks((prev) => prev.filter((b) => b.id !== bookmarkId))
    } catch {
      // silently fail
    }
  }

  function handleAvatarClick() {
    fileInputRef.current?.click()
  }

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0]
    if (!file) return

    // Validate file type
    if (!file.type.startsWith('image/')) return

    // Create preview
    const reader = new FileReader()
    reader.onload = () => {
      setPreviewUrl(reader.result as string)
    }
    reader.readAsDataURL(file)
  }

  async function confirmAvatarUpload() {
    const file = fileInputRef.current?.files?.[0]
    if (!file || !previewUrl) return

    setUploading(true)
    try {
      // Upload file to Supabase Storage via the new endpoint
      const formData = new FormData()
      formData.append('file', file)
      
      const updated = await apiStudentUpload<StudentProfileResponse>('/api/student/me/avatar/upload', formData)
      setProfile(updated)
      setPreviewUrl(null)
      setUploadSuccess(true)
      setTimeout(() => setUploadSuccess(false), 2000)
    } catch (err) {
      console.error('Failed to update avatar', err)
    } finally {
      setUploading(false)
      if (fileInputRef.current) fileInputRef.current.value = ''
    }
  }

  function cancelAvatarUpload() {
    setPreviewUrl(null)
    if (fileInputRef.current) fileInputRef.current.value = ''
  }

  async function handleUpdateFullName() {
    if (!fullName.trim() || fullName.trim().length < 2) {
      setNameError('Tên phải có ít nhất 2 ký tự')
      return
    }
    setSavingName(true)
    setNameError('')
    try {
      const updated = await apiStudentPatch<StudentProfileResponse>('/api/student/me/fullname', { fullName: fullName.trim() })
      setProfile(updated)
      setNameSuccess(true)
      setTimeout(() => setNameSuccess(false), 2000)
    } catch (err: unknown) {
      setNameError(err instanceof Error ? err.message : 'Có lỗi xảy ra')
    } finally {
      setSavingName(false)
    }
  }

  async function handleChangePassword() {
    setPasswordError('')
    if (!currentPassword) {
      setPasswordError('Vui lòng nhập mật khẩu hiện tại')
      return
    }
    if (!newPassword || newPassword.length < 6) {
      setPasswordError('Mật khẩu mới phải có ít nhất 6 ký tự')
      return
    }
    if (newPassword !== confirmPassword) {
      setPasswordError('Mật khẩu xác nhận không khớp')
      return
    }
    setSavingPassword(true)
    try {
      await apiStudentPatch('/api/student/me/password', { currentPassword, newPassword })
      setPasswordSuccess(true)
      setCurrentPassword('')
      setNewPassword('')
      setConfirmPassword('')
      setTimeout(() => setPasswordSuccess(false), 2000)
    } catch (err: unknown) {
      setPasswordError(err instanceof Error ? err.message : 'Có lỗi xảy ra')
    } finally {
      setSavingPassword(false)
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="flex items-center gap-3 text-[14px] font-medium text-orbit-text-secondary">
          <svg className="h-5 w-5 animate-spin text-orbit-accent" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          Đang tải...
        </div>
      </div>
    )
  }

  if (!profile) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <Link to="/student/login" className="btn-primary">Đăng nhập</Link>
      </div>
    )
  }

  return (
    <div className="w-full">
      <div className="mx-auto max-w-[1200px] px-6 md:px-10 py-12 md:py-20">

        {/* Profile Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="flex flex-col md:flex-row items-center md:items-start gap-6 md:gap-10 pb-10 border-b border-orbit-border mb-10"
        >
          {/* Avatar with upload overlay */}
          <div className="relative w-[128px] h-[128px] group cursor-pointer" onClick={handleAvatarClick}>
            <div className="absolute inset-0 rounded-full overflow-hidden">
              <Avatar
                name={profile.fullName}
                size={128}
                src={profile.avatar}
                className="ring-4 ring-orbit-accent/20 transition-all duration-300 group-hover:scale-105 group-hover:brightness-50"
              />
            </div>
            {/* Hover overlay */}
            <div className="absolute inset-0 rounded-full bg-black/60 flex flex-col items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-300 gap-2">
              <Camera className="h-8 w-8 text-white scale-75 group-hover:scale-100 transition-transform duration-300" weight="fill" />
              <span className="text-white text-[11px] font-bold tracking-wide">Đổi ảnh</span>
            </div>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              className="hidden"
              onChange={handleFileChange}
            />
          </div>

          <div className="text-center md:text-left">
            <h1 className="font-heading text-3xl md:text-4xl font-black text-orbit-text tracking-tight">
              {profile.fullName}
            </h1>
            <p className="text-[15px] text-orbit-accent font-mono font-medium mt-1">
              @{profile.studentCode}
            </p>
            <p className="text-[14px] text-orbit-text-secondary mt-2 max-w-lg">
              {profile.email}
            </p>
            <div className="flex flex-wrap items-center justify-center md:justify-start gap-3 mt-5">
              <div className="flex items-center gap-2 text-[13px] text-orbit-text-secondary">
                <BookmarkSimple className="h-4 w-4" weight="fill" />
                <span className="font-medium text-orbit-text">{bookmarks.length}</span>
                <span>bookmarks</span>
              </div>
            </div>
            {uploadSuccess && (
              <div className="mt-3 text-[12px] text-emerald-400 font-medium flex items-center gap-1.5">
                <CheckCircle className="h-4 w-4" weight="fill" />
                Đã cập nhật ảnh đại diện
              </div>
            )}
          </div>
        </motion.div>

        {/* Avatar preview modal */}
        {previewUrl && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-orbit-surface border border-orbit-border rounded-2xl p-6 max-w-sm w-full shadow-2xl"
            >
              <h3 className="font-heading text-sm font-bold text-orbit-text mb-4 text-center">
                Xác nhận ảnh đại diện
              </h3>
              <div className="flex justify-center mb-6">
                <img
                  src={previewUrl}
                  alt="Preview"
                  className="w-32 h-32 rounded-full object-cover ring-4 ring-orbit-accent/20"
                />
              </div>
              <div className="flex gap-3 justify-end">
                <button
                  onClick={cancelAvatarUpload}
                  className="px-4 py-2 rounded-xl text-[12px] font-bold text-orbit-text-muted hover:text-orbit-text border border-orbit-border hover:border-orbit-accent/30 transition-colors"
                  disabled={uploading}
                >
                  Hủy
                </button>
                <button
                  onClick={confirmAvatarUpload}
                  disabled={uploading}
                  className="px-4 py-2 rounded-xl text-[12px] font-bold text-white bg-orbit-accent hover:bg-orbit-accent/90 transition-colors disabled:opacity-50 flex items-center gap-2"
                >
                  {uploading ? (
                    <>
                      <svg className="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                      </svg>
                      Đang tải...
                    </>
                  ) : (
                    <>
                      <CheckCircle className="h-4 w-4" weight="fill" />
                      Lưu
                    </>
                  )}
                </button>
              </div>
            </motion.div>
          </div>
        )}

        {/* Settings Section */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="mt-10"
        >
          <div className="flex items-center gap-2.5 mb-6">
            <Gear className="h-5 w-5 text-orbit-accent" weight="fill" />
            <h2 className="font-heading text-xl font-bold text-orbit-text tracking-tight">
              Cài đặt
            </h2>
          </div>

          <div className="grid gap-6 md:grid-cols-2">
            {/* Change Display Name Card */}
            <div className="rounded-2xl border border-orbit-border bg-orbit-surface/50 p-6">
              <div className="flex items-center gap-2.5 mb-4">
                <User className="h-4 w-4 text-orbit-accent" weight="fill" />
                <h3 className="text-[15px] font-bold text-orbit-text">Đổi tên hiển thị</h3>
              </div>
              <div className="space-y-3">
                <input
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  className="w-full px-4 py-2.5 rounded-xl bg-orbit-bg border border-orbit-border text-[14px] text-orbit-text placeholder:text-orbit-text-muted focus:outline-none focus:border-orbit-accent/50 transition-colors"
                  placeholder="Nhập tên mới"
                />
                {nameError && (
                  <div className="flex items-center gap-1.5 text-[12px] text-red-400">
                    <Warning className="h-3.5 w-3.5" weight="fill" />
                    {nameError}
                  </div>
                )}
                {nameSuccess && (
                  <div className="flex items-center gap-1.5 text-[12px] text-emerald-400">
                    <CheckCircle className="h-3.5 w-3.5" weight="fill" />
                    Đã cập nhật tên
                  </div>
                )}
                <button
                  onClick={handleUpdateFullName}
                  disabled={savingName || fullName.trim() === profile.fullName}
                  className="px-4 py-2 rounded-xl text-[12px] font-bold text-white bg-orbit-accent hover:bg-orbit-accent/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
                >
                  {savingName ? 'Đang lưu...' : 'Lưu tên'}
                </button>
              </div>
            </div>

            {/* Change Password Card */}
            <div className="rounded-2xl border border-orbit-border bg-orbit-surface/50 p-6">
              <div className="flex items-center gap-2.5 mb-4">
                <Lock className="h-4 w-4 text-orbit-accent" weight="fill" />
                <h3 className="text-[15px] font-bold text-orbit-text">Đổi mật khẩu</h3>
              </div>
              <div className="space-y-3">
                <input
                  type="password"
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  className="w-full px-4 py-2.5 rounded-xl bg-orbit-bg border border-orbit-border text-[14px] text-orbit-text placeholder:text-orbit-text-muted focus:outline-none focus:border-orbit-accent/50 transition-colors"
                  placeholder="Mật khẩu hiện tại"
                />
                <input
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  className="w-full px-4 py-2.5 rounded-xl bg-orbit-bg border border-orbit-border text-[14px] text-orbit-text placeholder:text-orbit-text-muted focus:outline-none focus:border-orbit-accent/50 transition-colors"
                  placeholder="Mật khẩu mới (ít nhất 6 ký tự)"
                />
                <input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="w-full px-4 py-2.5 rounded-xl bg-orbit-bg border border-orbit-border text-[14px] text-orbit-text placeholder:text-orbit-text-muted focus:outline-none focus:border-orbit-accent/50 transition-colors"
                  placeholder="Xác nhận mật khẩu mới"
                />
                {passwordError && (
                  <div className="flex items-center gap-1.5 text-[12px] text-red-400">
                    <Warning className="h-3.5 w-3.5" weight="fill" />
                    {passwordError}
                  </div>
                )}
                {passwordSuccess && (
                  <div className="flex items-center gap-1.5 text-[12px] text-emerald-400">
                    <CheckCircle className="h-3.5 w-3.5" weight="fill" />
                    Đã đổi mật khẩu thành công
                  </div>
                )}
                <button
                  onClick={handleChangePassword}
                  disabled={savingPassword}
                  className="px-4 py-2 rounded-xl text-[12px] font-bold text-white bg-orbit-accent hover:bg-orbit-accent/90 transition-colors disabled:opacity-50 cursor-pointer"
                >
                  {savingPassword ? 'Đang lưu...' : 'Đổi mật khẩu'}
                </button>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Bookmarks Section */}
        <div>
          <div className="flex items-center justify-between mb-6">
            <h2 className="font-heading text-xl font-bold text-orbit-text tracking-tight">
              Bookmarks
            </h2>
            <span className="text-[13px] text-orbit-text-muted font-mono">
              {bookmarks.length} {bookmarks.length === 1 ? 'mục' : 'mục'}
            </span>
          </div>

          {bookmarks.length === 0 ? (
            <div className="rounded-2xl border border-dashed border-orbit-border p-16 text-center">
              <BookmarkSimple className="h-12 w-12 text-orbit-text-muted mx-auto mb-4" weight="light" />
              <p className="text-[15px] font-medium text-orbit-text-secondary">
                Chưa có bookmark nào
              </p>
              <p className="text-[13px] text-orbit-text-muted mt-1">
                Lưu các khóa học và repository để theo dõi sau.
              </p>
              <Link to="/courses" className="btn-primary text-[12px] px-6 py-3 mt-6 inline-flex">
                Duyệt khóa học
              </Link>
            </div>
          ) : (
            <div className="grid gap-4">
              {bookmarks.map((bookmark, i) => (
                <motion.div
                  key={bookmark.id}
                  initial={{ opacity: 0, y: 12 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: i * 0.04 }}
                  className="group relative rounded-2xl border border-orbit-border bg-orbit-surface/50 p-5 hover:border-orbit-accent/30 hover:bg-orbit-surface transition-[border-color,background-color] duration-200"
                >
                  <div className="flex items-start justify-between gap-4">
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2.5 mb-2">
                        {bookmark.targetType === 'REPO' ? (
                          <GithubLogo className="h-4 w-4 text-orbit-text-muted flex-shrink-0" weight="fill" />
                        ) : (
                          <BookOpen className="h-4 w-4 text-orbit-text-muted flex-shrink-0" weight="fill" />
                        )}
                        <a
                          href={bookmark.url}
                          target={bookmark.targetType === 'REPO' ? '_blank' : undefined}
                          rel={bookmark.targetType === 'REPO' ? 'noreferrer' : undefined}
                          className="text-[15px] font-bold text-orbit-accent hover:text-orbit-accent/80 hover:underline transition-colors truncate"
                        >
                          {bookmark.title}
                        </a>
                        <ArrowSquareOut className="h-3 w-3 text-orbit-text-muted flex-shrink-0" weight="bold" />
                      </div>
                      {bookmark.subtitle && (
                        <p className="text-[13px] text-orbit-text-secondary leading-relaxed line-clamp-2 mb-3">
                          {bookmark.subtitle}
                        </p>
                      )}
                      <div className="flex items-center gap-3">
                        <span className={`inline-flex items-center gap-1.5 text-[11px] font-semibold uppercase tracking-wider px-2.5 py-1 rounded-full ${
                          bookmark.targetType === 'REPO'
                            ? 'bg-violet-500/10 text-violet-400 border border-violet-500/20'
                            : 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                        }`}>
                          <span className={`h-2 w-2 rounded-full ${
                            bookmark.targetType === 'REPO' ? 'bg-violet-400' : 'bg-emerald-400'
                          }`} />
                          {bookmark.targetType}
                        </span>
                      </div>
                    </div>
                    <button
                      type="button"
                      onClick={() => handleRemove(bookmark.id)}
                      className="flex-shrink-0 p-2 rounded-lg text-orbit-text-muted hover:text-red-400 hover:bg-red-500/10 opacity-0 group-hover:opacity-100 transition-all duration-200 cursor-pointer"
                      title="Xóa bookmark"
                    >
                      <Trash className="h-4 w-4" weight="regular" />
                    </button>
                  </div>
                </motion.div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
