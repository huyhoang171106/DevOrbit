import { apiAdminGet, apiAdminPut, apiAdminPost, apiAdminDelete } from './api'
import type {
  AdminStats, AdminStudent, CourseReviewAdmin, RepoReviewAdmin,
  CommunityMessageAdmin, ChatSessionAdmin, ChatMessageAdmin, TechStackAdmin,
  CourseUpsertRequest, ApprovedRepoUpdateRequest, CandidateReviewRequest,
  AdminNotification,
} from '../types/admin'
import type {
  CourseSummary, CourseDetail, RepoSummary, RepoCandidate, ReviewerStats,
  YoutubePlaylistResponse, YoutubePlaylistRequest,
  ArticleResponse, ArticleRequest,
  TutorialResponse, TutorialRequest,
  CourseRelationshipResponse, CourseRelationshipRequest,
  ChatChannelResponse,
} from '../types/api'

export const adminApi = {
  // --- Stats ---
  getStats: (token: string, sortBy?: string) =>
    apiAdminGet<AdminStats>(`/api/admin/stats${sortBy ? `?sortBy=${sortBy}` : ''}`, token),

  // --- Students ---
  getStudents: (token: string, search?: string) =>
    apiAdminGet<AdminStudent[]>(`/api/admin/students${search ? `?search=${encodeURIComponent(search)}` : ''}`, token),
  toggleStudentActive: (token: string, id: number) =>
    apiAdminPut<AdminStudent>(`/api/admin/students/${id}/toggle-active`, token, {}),

  // --- Course Reviews ---
  getCourseReviews: (token: string) =>
    apiAdminGet<CourseReviewAdmin[]>('/api/admin/reviews/courses', token),
  deleteCourseReview: (token: string, id: number) =>
    apiAdminDelete(`/api/admin/reviews/courses/${id}`, token),

  // --- Repo Reviews ---
  getRepoReviews: (token: string) =>
    apiAdminGet<RepoReviewAdmin[]>('/api/admin/reviews/repos', token),
  deleteRepoReview: (token: string, id: number) =>
    apiAdminDelete(`/api/admin/reviews/repos/${id}`, token),

  // --- Community ---
  getChannels: (token: string) =>
    apiAdminGet<ChatChannelResponse[]>('/api/admin/community/channels', token),
  getCommunityMessages: (token: string) =>
    apiAdminGet<CommunityMessageAdmin[]>('/api/admin/community/messages', token),
  deleteCommunityMessage: (token: string, id: number) =>
    apiAdminDelete(`/api/admin/community/messages/${id}`, token),

  // --- Chat ---
  getChatSessions: (token: string) =>
    apiAdminGet<ChatSessionAdmin[]>('/api/admin/chat/sessions', token),
  getChatMessages: (token: string, sessionId: string) =>
    apiAdminGet<ChatMessageAdmin[]>(`/api/admin/chat/sessions/${sessionId}/messages`, token),

  // --- Tech Stack ---
  getTechStacks: (token: string) =>
    apiAdminGet<TechStackAdmin[]>('/api/admin/techstack', token),
  createTechStack: (token: string, data: { name: string; category: string }) =>
    apiAdminPost<TechStackAdmin>('/api/admin/techstack', token, data),
  deleteTechStack: (token: string, id: number) =>
    apiAdminDelete<{ channelDeactivated: boolean }>(`/api/admin/techstack/${id}`, token),

  // --- Courses ---
  getCourses: (token: string) =>
    apiAdminGet<CourseSummary[]>('/api/admin/courses', token),
  createCourse: (token: string, data: CourseUpsertRequest) =>
    apiAdminPost<CourseDetail>('/api/admin/courses', token, data),
  updateCourse: (token: string, id: number, data: CourseUpsertRequest) =>
    apiAdminPut<CourseDetail>(`/api/admin/courses/${id}`, token, data),
  deleteCourse: (token: string, id: number) =>
    apiAdminDelete<{ channelDeactivated: boolean }>(`/api/admin/courses/${id}`, token),

  // --- Course Resources: YouTube ---
  getYoutubePlaylists: (token: string, courseId: number) =>
    apiAdminGet<YoutubePlaylistResponse[]>(`/api/admin/courses/${courseId}/resources/youtube-playlists`, token),
  createYoutubePlaylist: (token: string, courseId: number, data: YoutubePlaylistRequest) =>
    apiAdminPost<YoutubePlaylistResponse>(`/api/admin/courses/${courseId}/resources/youtube-playlists`, token, data),
  updateYoutubePlaylist: (token: string, courseId: number, id: number, data: YoutubePlaylistRequest) =>
    apiAdminPut<YoutubePlaylistResponse>(`/api/admin/courses/${courseId}/resources/youtube-playlists/${id}`, token, data),
  deleteYoutubePlaylist: (token: string, courseId: number, id: number) =>
    apiAdminDelete(`/api/admin/courses/${courseId}/resources/youtube-playlists/${id}`, token),

  // --- Course Resources: Articles ---
  getArticles: (token: string, courseId: number) =>
    apiAdminGet<ArticleResponse[]>(`/api/admin/courses/${courseId}/resources/articles`, token),
  createArticle: (token: string, courseId: number, data: ArticleRequest) =>
    apiAdminPost<ArticleResponse>(`/api/admin/courses/${courseId}/resources/articles`, token, data),
  updateArticle: (token: string, courseId: number, id: number, data: ArticleRequest) =>
    apiAdminPut<ArticleResponse>(`/api/admin/courses/${courseId}/resources/articles/${id}`, token, data),
  deleteArticle: (token: string, courseId: number, id: number) =>
    apiAdminDelete(`/api/admin/courses/${courseId}/resources/articles/${id}`, token),

  // --- Course Resources: Tutorials ---
  getTutorials: (token: string, courseId: number) =>
    apiAdminGet<TutorialResponse[]>(`/api/admin/courses/${courseId}/resources/tutorials`, token),
  createTutorial: (token: string, courseId: number, data: TutorialRequest) =>
    apiAdminPost<TutorialResponse>(`/api/admin/courses/${courseId}/resources/tutorials`, token, data),
  updateTutorial: (token: string, courseId: number, id: number, data: TutorialRequest) =>
    apiAdminPut<TutorialResponse>(`/api/admin/courses/${courseId}/resources/tutorials/${id}`, token, data),
  deleteTutorial: (token: string, courseId: number, id: number) =>
    apiAdminDelete(`/api/admin/courses/${courseId}/resources/tutorials/${id}`, token),

  // --- Repos: Scan ---
  scan: (token: string, courseId: number, query: string) =>
    apiAdminPost<RepoCandidate[]>('/api/admin/github/scan', token, { courseId, query }),
  scanAll: (token: string) =>
    apiAdminPost<{ message: string }>('/api/admin/github/scan-all', token, {}),
  getScanLogs: (token: string) =>
    apiAdminGet<string[]>('/api/admin/github/scan-logs', token),
  clearScanLogs: (token: string) =>
    apiAdminDelete('/api/admin/github/scan-logs', token),

  // --- Repos: Candidates ---
  getCandidates: (token: string, reviewer?: string) =>
    apiAdminGet<RepoCandidate[]>(`/api/admin/repo-candidates${reviewer && reviewer !== 'all' ? `?reviewer=${reviewer}` : ''}`, token),
  getReviewerStats: (token: string) =>
    apiAdminGet<ReviewerStats[]>('/api/admin/repo-candidates/stats', token),
  approveCandidate: (token: string, id: number, data: CandidateReviewRequest) =>
    apiAdminPost<RepoCandidate>(`/api/admin/repo-candidates/${id}/approve`, token, data),
  rejectCandidate: (token: string, id: number) =>
    apiAdminPost<RepoCandidate>(`/api/admin/repo-candidates/${id}/reject`, token, {}),

  // --- Repos: Approved ---
  getApprovedRepos: (token: string) =>
    apiAdminGet<RepoSummary[]>('/api/admin/repos', token),
  updateApprovedRepo: (token: string, id: number, data: ApprovedRepoUpdateRequest) =>
    apiAdminPut<RepoSummary>(`/api/admin/repos/${id}`, token, data),
  deleteApprovedRepo: (token: string, id: number) =>
    apiAdminDelete(`/api/admin/repos/${id}`, token),

  // --- Relationships ---
  getRelationships: (token: string) =>
    apiAdminGet<CourseRelationshipResponse[]>('/api/admin/courses/relationships', token),
  createRelationship: (token: string, data: CourseRelationshipRequest) =>
    apiAdminPost<CourseRelationshipResponse>('/api/admin/courses/relationships', token, data),
  deleteRelationship: (token: string, id: number) =>
    apiAdminDelete(`/api/admin/courses/relationships/${id}`, token),

  // --- Notifications ---
  getNotifications: (token: string) =>
    apiAdminGet<AdminNotification[]>('/api/admin/notifications', token),
  getUnreadNotificationCount: (token: string) =>
    apiAdminGet<{ count: number }>('/api/admin/notifications/unread-count', token),
  markNotificationRead: (token: string, id: number) =>
    apiAdminPut(`/api/admin/notifications/${id}/read`, token, {}),
  markAllNotificationsRead: (token: string) =>
    apiAdminPut('/api/admin/notifications/read-all', token, {}),
}
