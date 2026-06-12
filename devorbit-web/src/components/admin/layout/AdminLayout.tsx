import { Suspense } from 'react'
import { Outlet } from 'react-router-dom'
import { AdminSidebar } from './AdminSidebar'
import { AdminTopbar } from './AdminTopbar'
import { ParticleNetwork } from '../../ParticleNetwork'
import { useRequireAdminAuth } from '../../../lib/adminHooks'
import { getSidebarCollapsed } from '../../../lib/adminAuth'
import { useState, useEffect } from 'react'

export function AdminLayout() {
  useRequireAdminAuth()
  const [collapsed, setCollapsed] = useState(getSidebarCollapsed)

  useEffect(() => {
    const check = () => setCollapsed(getSidebarCollapsed())
    window.addEventListener('storage', check)
    const interval = setInterval(check, 500)
    return () => { window.removeEventListener('storage', check); clearInterval(interval) }
  }, [])

  return (
    <div className="min-h-screen bg-orbit-bg relative">
      <div className="fixed inset-0 z-0 pointer-events-none">
        <ParticleNetwork />
      </div>
      <AdminSidebar />
      <div className={`relative z-10 transition-all duration-200 ${collapsed ? 'ml-[64px]' : 'ml-[240px]'}`}>
        <AdminTopbar />
        <main>
          <Suspense fallback={
            <div className="flex items-center justify-center py-20">
              <div className="w-8 h-8 border-2 border-orbit-border border-t-orbit-accent rounded-full animate-spin" />
            </div>
          }>
            <Outlet />
          </Suspense>
        </main>
      </div>
    </div>
  )
}
