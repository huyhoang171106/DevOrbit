import { lazy, Suspense } from 'react'
import { Routes, Route } from 'react-router-dom'

const HomePage = lazy(() => import('./pages/student/HomePage').then(m => ({ default: m.HomePage })))
const CourseListPage = lazy(() => import('./pages/student/CourseListPage').then(m => ({ default: m.CourseListPage })))
const CourseDetailPage = lazy(() => import('./pages/student/CourseDetailPage').then(m => ({ default: m.CourseDetailPage })))
const RepoDetailPage = lazy(() => import('./pages/student/RepoDetailPage').then(m => ({ default: m.RepoDetailPage })))
const StudentLoginPage = lazy(() => import('./pages/student/StudentLoginPage').then(m => ({ default: m.StudentLoginPage })))
const StudentBookmarksPage = lazy(() => import('./pages/student/StudentBookmarksPage').then(m => ({ default: m.StudentBookmarksPage })))
const PhotoboothPage = lazy(() => import('./pages/student/PhotoboothPage').then(m => ({ default: m.PhotoboothPage })))
const LoginPage = lazy(() => import('./pages/admin/LoginPage').then(m => ({ default: m.LoginPage })))
const AdminDashboardPage = lazy(() => import('./pages/admin/AdminDashboardPage').then(m => ({ default: m.AdminDashboardPage })))
const AdminCoursesPage = lazy(() => import('./pages/admin/AdminCoursesPage').then(m => ({ default: m.AdminCoursesPage })))
const AdminScanPage = lazy(() => import('./pages/admin/AdminScanPage').then(m => ({ default: m.AdminScanPage })))
const AdminCandidatesPage = lazy(() => import('./pages/admin/AdminCandidatesPage').then(m => ({ default: m.AdminCandidatesPage })))
const AdminReposPage = lazy(() => import('./pages/admin/AdminReposPage').then(m => ({ default: m.AdminReposPage })))
const AdminCourseResourcesPage = lazy(() => import('./pages/admin/AdminCourseResourcesPage').then(m => ({ default: m.AdminCourseResourcesPage })))
const AdminRoadmapsPage = lazy(() => import('./pages/admin/AdminRoadmapsPage').then(m => ({ default: m.AdminRoadmapsPage })))
const AdminRelationshipsPage = lazy(() => import('./pages/admin/AdminRelationshipsPage').then(m => ({ default: m.AdminRelationshipsPage })))
const AdminNotesPage = lazy(() => import('./pages/admin/AdminNotesPage').then(m => ({ default: m.AdminNotesPage })))
const AdminPhotoboothFramesPage = lazy(() => import('./pages/admin/AdminPhotoboothFramesPage').then(m => ({ default: m.AdminPhotoboothFramesPage })))
const GalaxyPage = lazy(() => import('./pages/student/knowledge-graph/GalaxyPage'))

function PageFallback() {
  return (
    <div className="h-[80vh] flex items-center justify-center">
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
      <Route path="/" element={<Suspense fallback={<PageFallback />}><HomePage /></Suspense>} />
      <Route path="/courses" element={<Suspense fallback={<PageFallback />}><CourseListPage /></Suspense>} />
      <Route path="/courses/:courseId" element={<Suspense fallback={<PageFallback />}><CourseDetailPage /></Suspense>} />
      <Route path="/knowledge-graph" element={<Suspense fallback={<PageFallback />}><GalaxyPage /></Suspense>} />
      <Route path="/repos/:repoId" element={<Suspense fallback={<PageFallback />}><RepoDetailPage /></Suspense>} />
      <Route path="/student/login" element={<Suspense fallback={<PageFallback />}><StudentLoginPage /></Suspense>} />
      <Route path="/student/bookmarks" element={<Suspense fallback={<PageFallback />}><StudentBookmarksPage /></Suspense>} />
      <Route path="/student/photobooth" element={<Suspense fallback={<PageFallback />}><PhotoboothPage /></Suspense>} />
      <Route path="/admin/login" element={<Suspense fallback={<PageFallback />}><LoginPage /></Suspense>} />
      <Route path="/admin" element={<Suspense fallback={<PageFallback />}><AdminDashboardPage /></Suspense>} />
      <Route path="/admin/courses" element={<Suspense fallback={<PageFallback />}><AdminCoursesPage /></Suspense>} />
      <Route path="/admin/courses/:courseId/resources" element={<Suspense fallback={<PageFallback />}><AdminCourseResourcesPage /></Suspense>} />
      <Route path="/admin/scan" element={<Suspense fallback={<PageFallback />}><AdminScanPage /></Suspense>} />
      <Route path="/admin/candidates" element={<Suspense fallback={<PageFallback />}><AdminCandidatesPage /></Suspense>} />
      <Route path="/admin/repos" element={<Suspense fallback={<PageFallback />}><AdminReposPage /></Suspense>} />
      <Route path="/admin/roadmaps" element={<Suspense fallback={<PageFallback />}><AdminRoadmapsPage /></Suspense>} />
      <Route path="/admin/relationships" element={<Suspense fallback={<PageFallback />}><AdminRelationshipsPage /></Suspense>} />
      <Route path="/admin/notes" element={<Suspense fallback={<PageFallback />}><AdminNotesPage /></Suspense>} />
      <Route path="/admin/photobooth-frames" element={<Suspense fallback={<PageFallback />}><AdminPhotoboothFramesPage /></Suspense>} />
    </Routes>
  );
}
