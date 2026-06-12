import { apiAdminGet, apiAdminPut, apiAdminPost, apiAdminDelete } from './api'
import type {
  AdminStats, AdminStudent, CourseReviewAdmin, RepoReviewAdmin,
  CommunityMessageAdmin, ChatSessionAdmin, ChatMessageAdmin, TechStackAdmin,
  CourseUpsertRequest, ApprovedRepoUpdateRequest, CandidateReviewRequest,
} from '../types/admin'
import type {
  CourseSummary, CourseDetail, RepoSummary, RepoCandidate, ReviewerStats,
  YoutubePlaylistResponse, YoutubePlaylistRequest,
  ArticleResponse, ArticleRequest,
  TutorialResponse, TutorialRequest,
  RoadmapResponse, RoadmapRequest,
  PhaseResponse, PhaseRequest,
  ItemResponse, ItemRequest,
  CourseRelationshipResponse, CourseRelationshipRequest,
  NoteResponse, ChatChannelResponse,
} from '../types/api'

export const adminApi = {
  // --- Stats ---
  getStats: (token: string) =>
    apiAdminGet<AdminStats>('/api/admin/stats', token),

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
    apiAdminDelete(`/api/admin/techstack/${id}`, token),

  // --- Courses ---
  getCourses: (token: string) =>
    apiAdminGet<CourseSummary[]>('/api/admin/courses', token),
  createCourse: (token: string, data: CourseUpsertRequest) =>
    apiAdminPost<CourseDetail>('/api/admin/courses', token, data),
  updateCourse: (token: string, id: number, data: CourseUpsertRequest) =>
    apiAdminPut<CourseDetail>(`/api/admin/courses/${id}`, token, data),
  deleteCourse: (token: string, id: number) =>
    apiAdminDelete(`/api/admin/courses/${id}`, token),

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

  // --- Roadmaps ---
  getRoadmaps: (token: string) =>
    apiAdminGet<RoadmapResponse[]>('/api/admin/roadmaps', token),
  createRoadmap: (token: string, data: RoadmapRequest) =>
    apiAdminPost<RoadmapResponse>('/api/admin/roadmaps', token, data),
  updateRoadmap: (token: string, id: number, data: RoadmapRequest) =>
    apiAdminPut<RoadmapResponse>(`/api/admin/roadmaps/${id}`, token, data),
  deleteRoadmap: (token: string, id: number) =>
    apiAdminDelete(`/api/admin/roadmaps/${id}`, token),

  // --- Roadmap Phases ---
  getPhases: (token: string, roadmapId: number) =>
    apiAdminGet<PhaseResponse[]>(`/api/admin/roadmaps/${roadmapId}/phases`, token),
  createPhase: (token: string, roadmapId: number, data: PhaseRequest) =>
    apiAdminPost<PhaseResponse>(`/api/admin/roadmaps/${roadmapId}/phases`, token, data),
  updatePhase: (token: string, phaseId: number, data: PhaseRequest) =>
    apiAdminPut<PhaseResponse>(`/api/admin/roadmaps/phases/${phaseId}`, token, data),
  deletePhase: (token: string, phaseId: number) =>
    apiAdminDelete(`/api/admin/roadmaps/phases/${phaseId}`, token),

  // --- Roadmap Items ---
  getItems: (token: string, phaseId: number) =>
    apiAdminGet<ItemResponse[]>(`/api/admin/roadmaps/phases/${phaseId}/items`, token),
  createItem: (token: string, phaseId: number, data: ItemRequest) =>
    apiAdminPost<ItemResponse>(`/api/admin/roadmaps/phases/${phaseId}/items`, token, data),
  updateItem: (token: string, itemId: number, data: ItemRequest) =>
    apiAdminPut<ItemResponse>(`/api/admin/roadmaps/items/${itemId}`, token, data),
  deleteItem: (token: string, itemId: number) =>
    apiAdminDelete(`/api/admin/roadmaps/items/${itemId}`, token),

  // --- Relationships ---
  getRelationships: (token: string) =>
    apiAdminGet<CourseRelationshipResponse[]>('/api/admin/courses/relationships', token),
  createRelationship: (token: string, data: CourseRelationshipRequest) =>
    apiAdminPost<CourseRelationshipResponse>('/api/admin/courses/relationships', token, data),
  deleteRelationship: (token: string, id: number) =>
    apiAdminDelete(`/api/admin/courses/relationships/${id}`, token),

  // --- Notes ---
  getNotes: (token: string) =>
    apiAdminGet<NoteResponse[]>('/api/admin/notes', token),
  deleteNote: (token: string, id: number) =>
    apiAdminDelete(`/api/admin/notes/${id}`, token),
}
