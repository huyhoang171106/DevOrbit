import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Layout } from './components/Layout'
import { CourseListPage } from './pages/student/CourseListPage'
import { CourseDetailPage } from './pages/student/CourseDetailPage'
import { LoginPage } from './pages/admin/LoginPage'
import { AdminDashboardPage } from './pages/admin/AdminDashboardPage'
import { AdminCoursesPage } from './pages/admin/AdminCoursesPage'
import { AdminScanPage } from './pages/admin/AdminScanPage'
import { AdminCandidatesPage } from './pages/admin/AdminCandidatesPage'
import { AdminReposPage } from './pages/admin/AdminReposPage'

export function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Navigate to="/courses" replace />} />
          <Route path="/courses" element={<CourseListPage />} />
          <Route path="/courses/:courseId" element={<CourseDetailPage />} />
          <Route path="/admin/login" element={<LoginPage />} />
          <Route path="/admin" element={<AdminDashboardPage />} />
          <Route path="/admin/courses" element={<AdminCoursesPage />} />
          <Route path="/admin/scan" element={<AdminScanPage />} />
          <Route path="/admin/candidates" element={<AdminCandidatesPage />} />
          <Route path="/admin/repos" element={<AdminReposPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  )
}
