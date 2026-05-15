import { lazy, Suspense } from 'react'
import { Routes, Route } from 'react-router-dom'
import { HomePage } from './pages/student/HomePage'
import { CourseListPage } from './pages/student/CourseListPage'
import { CourseDetailPage } from './pages/student/CourseDetailPage'
import { RepoDetailPage } from './pages/student/RepoDetailPage'
import { StudentLoginPage } from './pages/student/StudentLoginPage'
import { StudentBookmarksPage } from './pages/student/StudentBookmarksPage'
import { PhotoboothPage } from './pages/student/PhotoboothPage'
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
import { AdminPhotoboothFramesPage } from './pages/admin/AdminPhotoboothFramesPage'

const GalaxyPage = lazy(() => import('./pages/student/knowledge-graph/GalaxyPage'))

function GalaxyFallback() {
  return (
    <div className="h-[80vh] flex items-center justify-center bg-orbit-bg">
      <div className="flex flex-col items-center gap-6">
        <div className="relative h-10 w-10">
          <div className="absolute inset-0 rounded-full border-2 border-orbit-accent/10" />
          <div className="absolute inset-0 rounded-full border-t-2 border-orbit-accent animate-spin shadow-[0_0_15px_rgba(52,211,153,0.2)]" />
        </div>
        <p className="text-[11px] font-black text-orbit-accent tracking-[0.25em] uppercase">Đang tải</p>
      </div>
    </div>
  )
}

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/courses" element={<CourseListPage />} />
      <Route path="/courses/:courseId" element={<CourseDetailPage />} />
      <Route path="/knowledge-graph" element={
        <Suspense fallback={<GalaxyFallback />}>
          <GalaxyPage />
        </Suspense>
      } />
      <Route path="/repos/:repoId" element={<RepoDetailPage />} />
      <Route path="/student/login" element={<StudentLoginPage />} />
      <Route path="/student/bookmarks" element={<StudentBookmarksPage />} />
      <Route path="/student/photobooth" element={<PhotoboothPage />} />
      <Route path="/admin/login" element={<LoginPage />} />
      <Route path="/admin" element={<AdminDashboardPage />} />
      <Route path="/admin/courses" element={<AdminCoursesPage />} />
      <Route
        path="/admin/courses/:courseId/resources"
        element={<AdminCourseResourcesPage />}
      />
      <Route path="/admin/scan" element={<AdminScanPage />} />
      <Route path="/admin/candidates" element={<AdminCandidatesPage />} />
      <Route path="/admin/repos" element={<AdminReposPage />} />
      <Route path="/admin/roadmaps" element={<AdminRoadmapsPage />} />
      <Route path="/admin/relationships" element={<AdminRelationshipsPage />} />
      <Route path="/admin/notes" element={<AdminNotesPage />} />
      <Route path="/admin/photobooth-frames" element={<AdminPhotoboothFramesPage />} />
    </Routes>
  );
}
