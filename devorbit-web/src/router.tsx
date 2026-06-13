import { lazy, Suspense } from 'react'
import { Routes, Route } from 'react-router-dom'

const HomePage = lazy(() => import('./pages/student/HomePage').then(m => ({ default: m.HomePage })))
const CourseListPage = lazy(() => import('./pages/student/CourseListPage').then(m => ({ default: m.CourseListPage })))
const CourseDetailPage = lazy(() => import('./pages/student/CourseDetailPage').then(m => ({ default: m.CourseDetailPage })))
const RepoDetailPage = lazy(() => import('./pages/student/RepoDetailPage').then(m => ({ default: m.RepoDetailPage })))
const StudentLoginPage = lazy(() => import('./pages/student/StudentLoginPage').then(m => ({ default: m.StudentLoginPage })))
const StudentBookmarksPage = lazy(() => import('./pages/student/StudentBookmarksPage').then(m => ({ default: m.StudentBookmarksPage })))
const StudentProfilePage = lazy(() => import('./pages/student/StudentProfilePage').then(m => ({ default: m.StudentProfilePage })))
const PhotoboothPage = lazy(() => import('./pages/student/PhotoboothPage').then(m => ({ default: m.PhotoboothPage })))
const GpaCalculatorPage = lazy(() => import('./pages/student/GpaCalculatorPage').then(m => ({ default: m.GpaCalculatorPage })))
const GalaxyPage = lazy(() => import('./pages/student/knowledge-graph/GalaxyPage'))
const CommunityPage = lazy(() => import('./pages/student/CommunityPage').then(m => ({ default: m.CommunityPage })))
const AiTutorPage = lazy(() => import('./pages/student/AiTutorPage').then(m => ({ default: m.AiTutorPage })))

// Admin
const LoginPage = lazy(() => import('./pages/admin/LoginPage').then(m => ({ default: m.LoginPage })))
const AdminLayout = lazy(() => import('./components/admin/layout/AdminLayout').then(m => ({ default: m.AdminLayout })))
const DashboardPage = lazy(() => import('./pages/admin/DashboardPage').then(m => ({ default: m.DashboardPage })))
const CoursesPage = lazy(() => import('./pages/admin/CoursesPage').then(m => ({ default: m.CoursesPage })))
const ReposPage = lazy(() => import('./pages/admin/ReposPage').then(m => ({ default: m.ReposPage })))
const StudentsPage = lazy(() => import('./pages/admin/StudentsPage').then(m => ({ default: m.StudentsPage })))
const ReviewsPage = lazy(() => import('./pages/admin/ReviewsPage').then(m => ({ default: m.ReviewsPage })))
const CommunityAdminPage = lazy(() => import('./pages/admin/CommunityPage').then(m => ({ default: m.CommunityPage })))
const ChatMonitorPage = lazy(() => import('./pages/admin/ChatMonitorPage').then(m => ({ default: m.ChatMonitorPage })))
const RelationshipsPage = lazy(() => import('./pages/admin/RelationshipsPage').then(m => ({ default: m.RelationshipsPage })))
const PhotoboothAdminPage = lazy(() => import('./pages/admin/PhotoboothPage').then(m => ({ default: m.PhotoboothPage })))
const TechStackPage = lazy(() => import('./pages/admin/TechStackPage').then(m => ({ default: m.TechStackPage })))

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

function AdminSuspense({ children }: { children: React.ReactNode }) {
  return <Suspense fallback={<PageFallback />}>{children}</Suspense>
}

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<AdminSuspense><HomePage /></AdminSuspense>} />
      <Route path="/courses" element={<AdminSuspense><CourseListPage /></AdminSuspense>} />
      <Route path="/courses/:courseId" element={<AdminSuspense><CourseDetailPage /></AdminSuspense>} />
      <Route path="/knowledge-graph" element={<AdminSuspense><GalaxyPage /></AdminSuspense>} />
      <Route path="/repos/:repoId" element={<AdminSuspense><RepoDetailPage /></AdminSuspense>} />
      <Route path="/student/login" element={<AdminSuspense><StudentLoginPage /></AdminSuspense>} />
      <Route path="/student/bookmarks" element={<AdminSuspense><StudentBookmarksPage /></AdminSuspense>} />
      <Route path="/student/profile" element={<AdminSuspense><StudentProfilePage /></AdminSuspense>} />
      <Route path="/photobooth" element={<AdminSuspense><PhotoboothPage /></AdminSuspense>} />
      <Route path="/gpa-calculator" element={<AdminSuspense><GpaCalculatorPage /></AdminSuspense>} />
      <Route path="/community" element={<AdminSuspense><CommunityPage /></AdminSuspense>} />
      <Route path="/ai-tutor" element={<AdminSuspense><AiTutorPage /></AdminSuspense>} />
      <Route path="/admin/login" element={<AdminSuspense><LoginPage /></AdminSuspense>} />
      <Route path="/admin" element={<AdminSuspense><AdminLayout /></AdminSuspense>}>
        <Route index element={<DashboardPage />} />
        <Route path="courses" element={<CoursesPage />} />
        <Route path="repos" element={<ReposPage />} />
        <Route path="students" element={<StudentsPage />} />
        <Route path="reviews" element={<ReviewsPage />} />
        <Route path="community" element={<CommunityAdminPage />} />
        <Route path="chat" element={<ChatMonitorPage />} />
        <Route path="relationships" element={<RelationshipsPage />} />
        <Route path="photobooth" element={<PhotoboothAdminPage />} />
        <Route path="techstack" element={<TechStackPage />} />
      </Route>
    </Routes>
  );
}
