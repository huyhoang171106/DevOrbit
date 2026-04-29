import { createBrowserRouter, Navigate } from 'react-router-dom'
import { CourseListPage } from './pages/student/CourseListPage'
import { CourseDetailPage } from './pages/student/CourseDetailPage'
import { LoginPage } from './pages/admin/LoginPage'
import { AdminDashboardPage } from './pages/admin/AdminDashboardPage'
import { AdminCoursesPage } from './pages/admin/AdminCoursesPage'
import { AdminScanPage } from './pages/admin/AdminScanPage'
import { AdminCandidatesPage } from './pages/admin/AdminCandidatesPage'
import { AdminReposPage } from './pages/admin/AdminReposPage'

export const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/courses" replace /> },
  { path: '/courses', element: <CourseListPage /> },
  { path: '/courses/:courseId', element: <CourseDetailPage /> },
  { path: '/admin/login', element: <LoginPage /> },
  { path: '/admin', element: <AdminDashboardPage /> },
  { path: '/admin/courses', element: <AdminCoursesPage /> },
  { path: '/admin/scan', element: <AdminScanPage /> },
  { path: '/admin/candidates', element: <AdminCandidatesPage /> },
  { path: '/admin/repos', element: <AdminReposPage /> },
])
