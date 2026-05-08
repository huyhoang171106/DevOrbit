import { Routes, Route } from 'react-router-dom'
import { HomePage } from './pages/student/HomePage'
import { CourseListPage } from './pages/student/CourseListPage'
import { CourseDetailPage } from './pages/student/CourseDetailPage'
import { KnowledgeGraphPage } from './pages/student/KnowledgeGraphPage'
import { RepoDetailPage } from './pages/student/RepoDetailPage'
import { LoginPage } from './pages/admin/LoginPage'
import { AdminDashboardPage } from './pages/admin/AdminDashboardPage'
import { AdminCoursesPage } from './pages/admin/AdminCoursesPage'
import { AdminScanPage } from './pages/admin/AdminScanPage'
import { AdminCandidatesPage } from './pages/admin/AdminCandidatesPage'
import { AdminReposPage } from './pages/admin/AdminReposPage'
import { AdminCourseResourcesPage } from './pages/admin/AdminCourseResourcesPage'
import { AdminRoadmapsPage } from './pages/admin/AdminRoadmapsPage'
import { AdminRelationshipsPage } from './pages/admin/AdminRelationshipsPage'
import { AdminNotesPage } from './pages/admin/AdminNotesPage'

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/courses" element={<CourseListPage />} />
      <Route path="/courses/:courseId" element={<CourseDetailPage />} />
      <Route path="/knowledge-graph" element={<KnowledgeGraphPage />} />
      <Route path="/repos/:repoId" element={<RepoDetailPage />} />
      <Route path="/admin/login" element={<LoginPage />} />
      <Route path="/admin" element={<AdminDashboardPage />} />
      <Route path="/admin/courses" element={<AdminCoursesPage />} />
      <Route path="/admin/courses/:courseId/resources" element={<AdminCourseResourcesPage />} />
      <Route path="/admin/scan" element={<AdminScanPage />} />
      <Route path="/admin/candidates" element={<AdminCandidatesPage />} />
      <Route path="/admin/repos" element={<AdminReposPage />} />
      <Route path="/admin/roadmaps" element={<AdminRoadmapsPage />} />
      <Route path="/admin/relationships" element={<AdminRelationshipsPage />} />
      <Route path="/admin/notes" element={<AdminNotesPage />} />
    </Routes>
  )
}
