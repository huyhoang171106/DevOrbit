import { useState } from 'react'
import { AdminPageLayout } from '../../components/admin/shared/AdminPageLayout'
import { AdminSpinner } from '../../components/admin/shared/AdminSpinner'
import { AdminErrorBanner } from '../../components/admin/shared/AdminErrorBanner'
import { useAdminFetch } from '../../lib/adminHooks'
import { adminApi } from '../../lib/adminApi'
import { getAdminToken } from '../../lib/auth'
import type { TechStackAdmin } from '../../types/admin'

export function TechStackPage() {
  const token = getAdminToken()
  const [name, setName] = useState('')
  const [creating, setCreating] = useState(false)

  const { data: techStacks, loading, error, refetch } = useAdminFetch(
    (t) => adminApi.getTechStacks(t),
    [],
  )

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!token || !name.trim()) return
    setCreating(true)
    try {
      await adminApi.createTechStack(token, { name: name.trim(), category: '' })
      setName('')
      refetch()
    } catch (e) {
      console.error(e)
    } finally {
      setCreating(false)
    }
  }

  const handleDelete = async (id: number) => {
    if (!token || !confirm('Xoá nhãn công nghệ này?')) return
    try {
      await adminApi.deleteTechStack(token, id)
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  return (
    <AdminPageLayout title="Tech Stack" description="Quản lý nhãn công nghệ">
      <form onSubmit={handleCreate} className="flex gap-3 mb-6">
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="input-field max-w-sm"
          placeholder="Tên công nghệ mới..."
          required
        />
        <button type="submit" className="btn-primary text-sm" disabled={creating}>
          {creating ? 'Đang thêm...' : 'Thêm'}
        </button>
      </form>

      {loading && <AdminSpinner text="Đang tải..." />}
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}

      {techStacks && (
        <div className="glass-card overflow-hidden border border-orbit-border">
          <table className="w-full">
            <thead>
              <tr className="border-b border-orbit-border bg-orbit-surface/50">
                <th className="px-4 py-3 text-center text-xs font-medium text-ink-secondary uppercase">Tên</th>
                <th className="px-4 py-3 text-center text-xs font-medium text-ink-secondary uppercase">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-clay-border">
              {techStacks.length === 0 && (
                <tr>
                  <td colSpan={2} className="px-4 py-10 text-center body-sm text-ink-secondary">Chưa có nhãn công nghệ nào</td>
                </tr>
              )}
              {techStacks.map((ts: TechStackAdmin) => (
                <tr key={ts.id} className="transition-colors hover:bg-orbit-surface/30">
                  <td className="px-4 py-3 text-sm text-center font-medium text-ink-primary">{ts.name}</td>
                  <td className="px-4 py-3 text-sm text-center">
                    <div className="flex items-center justify-center">
                      <button onClick={() => handleDelete(ts.id)} className="btn-ghost text-xs text-red-400">Xoá</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </AdminPageLayout>
  )
}
