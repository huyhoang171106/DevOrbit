import { api } from "./baseApi";
export const addTagTypes = [
  "admin-roadmap-controller",
  "admin-repo-controller",
  "admin-course-controller",
  "admin-course-resource-controller",
  "student-auth-controller",
  "student-social-controller",
  "student-bookmark-controller",
  "Photobooth",
  "subject-qa-controller",
  "public-ai-controller",
  "admin-repo-candidate-controller",
  "admin-knowledge-controller",
  "admin-github-controller",
  "admin-course-relationship-controller",
  "admin-auth-controller",
  "public-tech-stack-controller",
  "student-community-controller",
  "public-repo-controller",
  "public-social-controller",
  "public-discovery-controller",
  "public-course-controller",
  "public-course-relationship-controller",
  "admin-note-controller",
] as const;
const injectedRtkApi = api
  .enhanceEndpoints({
    addTagTypes,
  })
  .injectEndpoints({
    endpoints: (build) => ({
      update: build.mutation<UpdateApiResponse, UpdateApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps/${queryArg.id}`,
          method: "PUT",
          body: queryArg.roadmapRequest,
        }),
        invalidatesTags: ["admin-roadmap-controller"],
      }),
      deleteApiAdminRoadmapsById: build.mutation<
        DeleteApiAdminRoadmapsByIdApiResponse,
        DeleteApiAdminRoadmapsByIdApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps/${queryArg.id}`,
          method: "DELETE",
        }),
        invalidatesTags: ["admin-roadmap-controller"],
      }),
      updatePhase: build.mutation<UpdatePhaseApiResponse, UpdatePhaseApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps/phases/${queryArg.phaseId}`,
          method: "PUT",
          body: queryArg.phaseRequest,
        }),
        invalidatesTags: ["admin-roadmap-controller"],
      }),
      deletePhase: build.mutation<DeletePhaseApiResponse, DeletePhaseApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps/phases/${queryArg.phaseId}`,
          method: "DELETE",
        }),
        invalidatesTags: ["admin-roadmap-controller"],
      }),
      updateItem: build.mutation<UpdateItemApiResponse, UpdateItemApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps/items/${queryArg.itemId}`,
          method: "PUT",
          body: queryArg.itemRequest,
        }),
        invalidatesTags: ["admin-roadmap-controller"],
      }),
      deleteItem: build.mutation<DeleteItemApiResponse, DeleteItemApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps/items/${queryArg.itemId}`,
          method: "DELETE",
        }),
        invalidatesTags: ["admin-roadmap-controller"],
      }),
      updateRepo: build.mutation<UpdateRepoApiResponse, UpdateRepoApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/repos/${queryArg.repoId}`,
          method: "PUT",
          body: queryArg.approvedRepoUpdateRequest,
        }),
        invalidatesTags: ["admin-repo-controller"],
      }),
      deleteRepo: build.mutation<DeleteRepoApiResponse, DeleteRepoApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/repos/${queryArg.repoId}`,
          method: "DELETE",
        }),
        invalidatesTags: ["admin-repo-controller"],
      }),
      update1: build.mutation<Update1ApiResponse, Update1ApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.id}`,
          method: "PUT",
          body: queryArg.adminCourseUpsertRequest,
        }),
        invalidatesTags: ["admin-course-controller"],
      }),
      delete1: build.mutation<Delete1ApiResponse, Delete1ApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.id}`,
          method: "DELETE",
        }),
        invalidatesTags: ["admin-course-controller"],
      }),
      updateYoutubePlaylist: build.mutation<
        UpdateYoutubePlaylistApiResponse,
        UpdateYoutubePlaylistApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/youtube-playlists/${queryArg.id}`,
          method: "PUT",
          body: queryArg.youtubePlaylistRequest,
        }),
        invalidatesTags: ["admin-course-resource-controller"],
      }),
      deleteYoutubePlaylist: build.mutation<
        DeleteYoutubePlaylistApiResponse,
        DeleteYoutubePlaylistApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/youtube-playlists/${queryArg.id}`,
          method: "DELETE",
        }),
        invalidatesTags: ["admin-course-resource-controller"],
      }),
      updateTutorial: build.mutation<
        UpdateTutorialApiResponse,
        UpdateTutorialApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/tutorials/${queryArg.id}`,
          method: "PUT",
          body: queryArg.tutorialRequest,
        }),
        invalidatesTags: ["admin-course-resource-controller"],
      }),
      deleteTutorial: build.mutation<
        DeleteTutorialApiResponse,
        DeleteTutorialApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/tutorials/${queryArg.id}`,
          method: "DELETE",
        }),
        invalidatesTags: ["admin-course-resource-controller"],
      }),
      updateArticle: build.mutation<
        UpdateArticleApiResponse,
        UpdateArticleApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/articles/${queryArg.id}`,
          method: "PUT",
          body: queryArg.articleRequest,
        }),
        invalidatesTags: ["admin-course-resource-controller"],
      }),
      deleteArticle: build.mutation<
        DeleteArticleApiResponse,
        DeleteArticleApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/articles/${queryArg.id}`,
          method: "DELETE",
        }),
        invalidatesTags: ["admin-course-resource-controller"],
      }),
      verifyOtp: build.mutation<VerifyOtpApiResponse, VerifyOtpApiArg>({
        query: (queryArg) => ({
          url: `/api/student/verify-otp`,
          method: "POST",
          body: queryArg.otpVerificationRequest,
        }),
        invalidatesTags: ["student-auth-controller"],
      }),
      resetPassword: build.mutation<
        ResetPasswordApiResponse,
        ResetPasswordApiArg
      >({
        query: (queryArg) => ({
          url: `/api/student/reset-password`,
          method: "POST",
          body: queryArg.resetPasswordRequest,
        }),
        invalidatesTags: ["student-auth-controller"],
      }),
      resendOtp: build.mutation<ResendOtpApiResponse, ResendOtpApiArg>({
        query: (queryArg) => ({
          url: `/api/student/resend-otp`,
          method: "POST",
          body: queryArg.body,
        }),
        invalidatesTags: ["student-auth-controller"],
      }),
      voteRepo: build.mutation<VoteRepoApiResponse, VoteRepoApiArg>({
        query: (queryArg) => ({
          url: `/api/student/repos/${queryArg.repoId}/vote`,
          method: "POST",
          body: queryArg.repoVoteRequest,
        }),
        invalidatesTags: ["student-social-controller"],
      }),
      upsertRepoReview: build.mutation<
        UpsertRepoReviewApiResponse,
        UpsertRepoReviewApiArg
      >({
        query: (queryArg) => ({
          url: `/api/student/repos/${queryArg.repoId}/review`,
          method: "POST",
          body: queryArg.reviewRequest,
        }),
        invalidatesTags: ["student-social-controller"],
      }),
      deleteRepoReview: build.mutation<
        DeleteRepoReviewApiResponse,
        DeleteRepoReviewApiArg
      >({
        query: (queryArg) => ({
          url: `/api/student/repos/${queryArg.repoId}/review`,
          method: "DELETE",
        }),
        invalidatesTags: ["student-social-controller"],
      }),
      register: build.mutation<RegisterApiResponse, RegisterApiArg>({
        query: (queryArg) => ({
          url: `/api/student/register`,
          method: "POST",
          body: queryArg.studentRegisterRequest,
        }),
        invalidatesTags: ["student-auth-controller"],
      }),
      logout: build.mutation<LogoutApiResponse, LogoutApiArg>({
        query: (queryArg) => ({
          url: `/api/student/logout`,
          method: "POST",
          headers: {
            Authorization: queryArg.authorization,
          },
        }),
        invalidatesTags: ["student-auth-controller"],
      }),
      login: build.mutation<LoginApiResponse, LoginApiArg>({
        query: (queryArg) => ({
          url: `/api/student/login`,
          method: "POST",
          body: queryArg.studentLoginRequest,
        }),
        invalidatesTags: ["student-auth-controller"],
      }),
      forgotPassword: build.mutation<
        ForgotPasswordApiResponse,
        ForgotPasswordApiArg
      >({
        query: (queryArg) => ({
          url: `/api/student/forgot-password`,
          method: "POST",
          body: queryArg.forgotPasswordRequest,
        }),
        invalidatesTags: ["student-auth-controller"],
      }),
      upsertCourseReview: build.mutation<
        UpsertCourseReviewApiResponse,
        UpsertCourseReviewApiArg
      >({
        query: (queryArg) => ({
          url: `/api/student/courses/${queryArg.courseId}/review`,
          method: "POST",
          body: queryArg.reviewRequest,
        }),
        invalidatesTags: ["student-social-controller"],
      }),
      deleteCourseReview: build.mutation<
        DeleteCourseReviewApiResponse,
        DeleteCourseReviewApiArg
      >({
        query: (queryArg) => ({
          url: `/api/student/courses/${queryArg.courseId}/review`,
          method: "DELETE",
        }),
        invalidatesTags: ["student-social-controller"],
      }),
      getBookmarks: build.query<GetBookmarksApiResponse, GetBookmarksApiArg>({
        query: () => ({ url: `/api/student/bookmarks` }),
        providesTags: ["student-bookmark-controller"],
      }),
      addBookmark: build.mutation<AddBookmarkApiResponse, AddBookmarkApiArg>({
        query: (queryArg) => ({
          url: `/api/student/bookmarks`,
          method: "POST",
          body: queryArg.studentBookmarkRequest,
        }),
        invalidatesTags: ["student-bookmark-controller"],
      }),
      listFrames: build.query<ListFramesApiResponse, ListFramesApiArg>({
        query: () => ({ url: `/api/photobooth/frames` }),
        providesTags: ["Photobooth"],
      }),
      upsertFrame: build.mutation<UpsertFrameApiResponse, UpsertFrameApiArg>({
        query: (queryArg) => ({
          url: `/api/photobooth/frames`,
          method: "POST",
          body: queryArg.photoboothFrameDto,
        }),
        invalidatesTags: ["Photobooth"],
      }),
      uploadOverlay: build.mutation<
        UploadOverlayApiResponse,
        UploadOverlayApiArg
      >({
        query: (queryArg) => ({
          url: `/api/photobooth/frames/${queryArg.frameId}/overlay`,
          method: "POST",
          body: queryArg.body,
        }),
        invalidatesTags: ["Photobooth"],
      }),
      query: build.mutation<QueryApiResponse, QueryApiArg>({
        query: (queryArg) => ({
          url: `/api/ai/subject-qa/query`,
          method: "POST",
          body: queryArg.subjectQaRequest,
        }),
        invalidatesTags: ["subject-qa-controller"],
      }),
      queryKnowledgeGraph: build.mutation<
        QueryKnowledgeGraphApiResponse,
        QueryKnowledgeGraphApiArg
      >({
        query: (queryArg) => ({
          url: `/api/ai/knowledge-graph/query`,
          method: "POST",
          body: queryArg.aiQueryRequest,
        }),
        invalidatesTags: ["public-ai-controller"],
      }),
      generateRoadmap: build.mutation<
        GenerateRoadmapApiResponse,
        GenerateRoadmapApiArg
      >({
        query: (queryArg) => ({
          url: `/api/ai/generate-roadmap`,
          method: "POST",
          body: queryArg.roadmapGenerationRequest,
        }),
        invalidatesTags: ["public-ai-controller"],
      }),
      chat: build.mutation<ChatApiResponse, ChatApiArg>({
        query: (queryArg) => ({
          url: `/api/ai/chat`,
          method: "POST",
          body: queryArg.chatRequest,
        }),
        invalidatesTags: ["public-ai-controller"],
      }),
      list: build.query<ListApiResponse, ListApiArg>({
        query: () => ({ url: `/api/admin/roadmaps` }),
        providesTags: ["admin-roadmap-controller"],
      }),
      create: build.mutation<CreateApiResponse, CreateApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps`,
          method: "POST",
          body: queryArg.roadmapRequest,
        }),
        invalidatesTags: ["admin-roadmap-controller"],
      }),
      getPhases: build.query<GetPhasesApiResponse, GetPhasesApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps/${queryArg.roadmapId}/phases`,
        }),
        providesTags: ["admin-roadmap-controller"],
      }),
      createPhase: build.mutation<CreatePhaseApiResponse, CreatePhaseApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps/${queryArg.roadmapId}/phases`,
          method: "POST",
          body: queryArg.phaseRequest,
        }),
        invalidatesTags: ["admin-roadmap-controller"],
      }),
      getItems: build.query<GetItemsApiResponse, GetItemsApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps/phases/${queryArg.phaseId}/items`,
        }),
        providesTags: ["admin-roadmap-controller"],
      }),
      createItem: build.mutation<CreateItemApiResponse, CreateItemApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/roadmaps/phases/${queryArg.phaseId}/items`,
          method: "POST",
          body: queryArg.itemRequest,
        }),
        invalidatesTags: ["admin-roadmap-controller"],
      }),
      reject: build.mutation<RejectApiResponse, RejectApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/repo-candidates/${queryArg.candidateId}/reject`,
          method: "POST",
        }),
        invalidatesTags: ["admin-repo-candidate-controller"],
      }),
      approve: build.mutation<ApproveApiResponse, ApproveApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/repo-candidates/${queryArg.candidateId}/approve`,
          method: "POST",
          body: queryArg.candidateReviewRequest,
        }),
        invalidatesTags: ["admin-repo-candidate-controller"],
      }),
      recrawl: build.mutation<RecrawlApiResponse, RecrawlApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/knowledge/sources/${queryArg.sourceId}/recrawl`,
          method: "POST",
        }),
        invalidatesTags: ["admin-knowledge-controller"],
      }),
      embedSource: build.mutation<EmbedSourceApiResponse, EmbedSourceApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/knowledge/sources/${queryArg.sourceId}/embed`,
          method: "POST",
          params: {
            force: queryArg.force,
          },
        }),
        invalidatesTags: ["admin-knowledge-controller"],
      }),
      search: build.mutation<SearchApiResponse, SearchApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/knowledge/search`,
          method: "POST",
          body: queryArg.searchRequest,
        }),
        invalidatesTags: ["admin-knowledge-controller"],
      }),
      ragPreview: build.mutation<RagPreviewApiResponse, RagPreviewApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/knowledge/rag-preview`,
          method: "POST",
          body: queryArg.ragPreviewRequest,
        }),
        invalidatesTags: ["admin-knowledge-controller"],
      }),
      ingestFolder: build.mutation<IngestFolderApiResponse, IngestFolderApiArg>(
        {
          query: () => ({
            url: `/api/admin/knowledge/ingest-folder`,
            method: "POST",
          }),
          invalidatesTags: ["admin-knowledge-controller"],
        },
      ),
      ingestFile: build.mutation<IngestFileApiResponse, IngestFileApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/knowledge/ingest-file`,
          method: "POST",
          body: queryArg.ingestFileRequest,
        }),
        invalidatesTags: ["admin-knowledge-controller"],
      }),
      importUrl: build.mutation<ImportUrlApiResponse, ImportUrlApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/knowledge/import-url`,
          method: "POST",
          body: queryArg.webImportRequest,
        }),
        invalidatesTags: ["admin-knowledge-controller"],
      }),
      crawlUrl: build.mutation<CrawlUrlApiResponse, CrawlUrlApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/knowledge/crawl-url`,
          method: "POST",
          body: queryArg.crawlRequest,
        }),
        invalidatesTags: ["admin-knowledge-controller"],
      }),
      embedCourse: build.mutation<EmbedCourseApiResponse, EmbedCourseApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/knowledge/courses/${queryArg.courseCode}/embed`,
          method: "POST",
          params: {
            force: queryArg.force,
          },
        }),
        invalidatesTags: ["admin-knowledge-controller"],
      }),
      scan: build.mutation<ScanApiResponse, ScanApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/github/scan`,
          method: "POST",
          body: queryArg.githubScanRequest,
        }),
        invalidatesTags: ["admin-github-controller"],
      }),
      scanAll: build.mutation<ScanAllApiResponse, ScanAllApiArg>({
        query: () => ({ url: `/api/admin/github/scan-all`, method: "POST" }),
        invalidatesTags: ["admin-github-controller"],
      }),
      list1: build.query<List1ApiResponse, List1ApiArg>({
        query: () => ({ url: `/api/admin/courses` }),
        providesTags: ["admin-course-controller"],
      }),
      create1: build.mutation<Create1ApiResponse, Create1ApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/courses`,
          method: "POST",
          body: queryArg.adminCourseUpsertRequest,
        }),
        invalidatesTags: ["admin-course-controller"],
      }),
      getYoutubePlaylists: build.query<
        GetYoutubePlaylistsApiResponse,
        GetYoutubePlaylistsApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/youtube-playlists`,
        }),
        providesTags: ["admin-course-resource-controller"],
      }),
      createYoutubePlaylist: build.mutation<
        CreateYoutubePlaylistApiResponse,
        CreateYoutubePlaylistApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/youtube-playlists`,
          method: "POST",
          body: queryArg.youtubePlaylistRequest,
        }),
        invalidatesTags: ["admin-course-resource-controller"],
      }),
      getTutorials: build.query<GetTutorialsApiResponse, GetTutorialsApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/tutorials`,
        }),
        providesTags: ["admin-course-resource-controller"],
      }),
      createTutorial: build.mutation<
        CreateTutorialApiResponse,
        CreateTutorialApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/tutorials`,
          method: "POST",
          body: queryArg.tutorialRequest,
        }),
        invalidatesTags: ["admin-course-resource-controller"],
      }),
      getArticles: build.query<GetArticlesApiResponse, GetArticlesApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/articles`,
        }),
        providesTags: ["admin-course-resource-controller"],
      }),
      createArticle: build.mutation<
        CreateArticleApiResponse,
        CreateArticleApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/courses/${queryArg.courseId}/resources/articles`,
          method: "POST",
          body: queryArg.articleRequest,
        }),
        invalidatesTags: ["admin-course-resource-controller"],
      }),
      getAll: build.query<GetAllApiResponse, GetAllApiArg>({
        query: () => ({ url: `/api/admin/courses/relationships` }),
        providesTags: ["admin-course-relationship-controller"],
      }),
      create2: build.mutation<Create2ApiResponse, Create2ApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/courses/relationships`,
          method: "POST",
          body: queryArg.courseRelationshipRequest,
        }),
        invalidatesTags: ["admin-course-relationship-controller"],
      }),
      logout1: build.mutation<Logout1ApiResponse, Logout1ApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/auth/logout`,
          method: "POST",
          headers: {
            Authorization: queryArg.authorization,
          },
        }),
        invalidatesTags: ["admin-auth-controller"],
      }),
      login1: build.mutation<Login1ApiResponse, Login1ApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/auth/login`,
          method: "POST",
          body: queryArg.loginRequest,
        }),
        invalidatesTags: ["admin-auth-controller"],
      }),
      getAllTechStacks: build.query<
        GetAllTechStacksApiResponse,
        GetAllTechStacksApiArg
      >({
        query: () => ({ url: `/api/tech-stacks` }),
        providesTags: ["public-tech-stack-controller"],
      }),
      me: build.query<MeApiResponse, MeApiArg>({
        query: () => ({ url: `/api/student/me` }),
        providesTags: ["student-auth-controller"],
      }),
      getChannels: build.query<GetChannelsApiResponse, GetChannelsApiArg>({
        query: () => ({ url: `/api/student/community` }),
        providesTags: ["student-community-controller"],
      }),
      getMessages: build.query<GetMessagesApiResponse, GetMessagesApiArg>({
        query: (queryArg) => ({
          url: `/api/student/community/channels/${queryArg.channelId}/messages`,
          params: {
            page: queryArg.page,
            size: queryArg.size,
          },
        }),
        providesTags: ["student-community-controller"],
      }),
      getRepoById: build.query<GetRepoByIdApiResponse, GetRepoByIdApiArg>({
        query: (queryArg) => ({ url: `/api/repos/${queryArg.repoId}` }),
        providesTags: ["public-repo-controller"],
      }),
      getRepoSocialInfo: build.query<
        GetRepoSocialInfoApiResponse,
        GetRepoSocialInfoApiArg
      >({
        query: (queryArg) => ({
          url: `/api/repos/${queryArg.repoId}/social-info`,
        }),
        providesTags: ["public-social-controller"],
      }),
      getFrame: build.query<GetFrameApiResponse, GetFrameApiArg>({
        query: (queryArg) => ({
          url: `/api/photobooth/frames/${queryArg.frameId}`,
        }),
        providesTags: ["Photobooth"],
      }),
      deleteFrame: build.mutation<DeleteFrameApiResponse, DeleteFrameApiArg>({
        query: (queryArg) => ({
          url: `/api/photobooth/frames/${queryArg.frameId}`,
          method: "DELETE",
        }),
        invalidatesTags: ["Photobooth"],
      }),
      getTopStacks: build.query<GetTopStacksApiResponse, GetTopStacksApiArg>({
        query: () => ({ url: `/api/discovery/top-stacks` }),
        providesTags: ["public-discovery-controller"],
      }),
      getAllRepos: build.query<GetAllReposApiResponse, GetAllReposApiArg>({
        query: () => ({ url: `/api/discovery/repos` }),
        providesTags: ["public-discovery-controller"],
      }),
      getRecentRepos: build.query<
        GetRecentReposApiResponse,
        GetRecentReposApiArg
      >({
        query: () => ({ url: `/api/discovery/recent-repos` }),
        providesTags: ["public-discovery-controller"],
      }),
      getCourses: build.query<GetCoursesApiResponse, GetCoursesApiArg>({
        query: (queryArg) => ({
          url: `/api/courses`,
          params: {
            q: queryArg.q,
            subjectType: queryArg.subjectType,
            semester: queryArg.semester,
            managementUnit: queryArg.managementUnit,
          },
        }),
        providesTags: ["public-course-controller"],
      }),
      getCourseDetail: build.query<
        GetCourseDetailApiResponse,
        GetCourseDetailApiArg
      >({
        query: (queryArg) => ({ url: `/api/courses/${queryArg.id}` }),
        providesTags: ["public-course-controller"],
      }),
      getVideos: build.query<GetVideosApiResponse, GetVideosApiArg>({
        query: (queryArg) => ({ url: `/api/courses/${queryArg.id}/videos` }),
        providesTags: ["public-course-controller"],
      }),
      getTutorials1: build.query<GetTutorials1ApiResponse, GetTutorials1ApiArg>(
        {
          query: (queryArg) => ({
            url: `/api/courses/${queryArg.id}/tutorials`,
          }),
          providesTags: ["public-course-controller"],
        },
      ),
      getArticles1: build.query<GetArticles1ApiResponse, GetArticles1ApiArg>({
        query: (queryArg) => ({ url: `/api/courses/${queryArg.id}/articles` }),
        providesTags: ["public-course-controller"],
      }),
      getCourseReviews: build.query<
        GetCourseReviewsApiResponse,
        GetCourseReviewsApiArg
      >({
        query: (queryArg) => ({
          url: `/api/courses/${queryArg.courseId}/reviews`,
        }),
        providesTags: ["public-social-controller"],
      }),
      getReposByCourse: build.query<
        GetReposByCourseApiResponse,
        GetReposByCourseApiArg
      >({
        query: (queryArg) => ({
          url: `/api/courses/${queryArg.courseId}/repos`,
          params: {
            techStack: queryArg.techStack,
          },
        }),
        providesTags: ["public-repo-controller"],
      }),
      getAll1: build.query<GetAll1ApiResponse, GetAll1ApiArg>({
        query: () => ({ url: `/api/courses/relationships` }),
        providesTags: ["public-course-relationship-controller"],
      }),
      getByCourse: build.query<GetByCourseApiResponse, GetByCourseApiArg>({
        query: (queryArg) => ({
          url: `/api/courses/relationships/course/${queryArg.courseId}`,
        }),
        providesTags: ["public-course-relationship-controller"],
      }),
      getGraph: build.query<GetGraphApiResponse, GetGraphApiArg>({
        query: () => ({ url: `/api/courses/graph` }),
        providesTags: ["public-course-controller"],
      }),
      getRepoSummary: build.query<
        GetRepoSummaryApiResponse,
        GetRepoSummaryApiArg
      >({
        query: (queryArg) => ({
          url: `/api/ai/repo/${queryArg.repoId}/summary`,
        }),
        providesTags: ["public-ai-controller"],
      }),
      getTutorAdvice: build.query<
        GetTutorAdviceApiResponse,
        GetTutorAdviceApiArg
      >({
        query: (queryArg) => ({
          url: `/api/ai/repo/${queryArg.repoId}/advice`,
        }),
        providesTags: ["public-ai-controller"],
      }),
      getChatHistory: build.query<
        GetChatHistoryApiResponse,
        GetChatHistoryApiArg
      >({
        query: (queryArg) => ({
          url: `/api/ai/chat/${queryArg.sessionId}/history`,
        }),
        providesTags: ["public-ai-controller"],
      }),
      getAllApprovedRepos: build.query<
        GetAllApprovedReposApiResponse,
        GetAllApprovedReposApiArg
      >({
        query: () => ({ url: `/api/admin/repos` }),
        providesTags: ["admin-repo-controller"],
      }),
      getPendingCandidates: build.query<
        GetPendingCandidatesApiResponse,
        GetPendingCandidatesApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/repo-candidates`,
          params: {
            reviewer: queryArg.reviewer,
          },
        }),
        providesTags: ["admin-repo-candidate-controller"],
      }),
      stats: build.query<StatsApiResponse, StatsApiArg>({
        query: () => ({ url: `/api/admin/repo-candidates/stats` }),
        providesTags: ["admin-repo-candidate-controller"],
      }),
      list2: build.query<List2ApiResponse, List2ApiArg>({
        query: () => ({ url: `/api/admin/notes` }),
        providesTags: ["admin-note-controller"],
      }),
      listSources: build.query<ListSourcesApiResponse, ListSourcesApiArg>({
        query: () => ({ url: `/api/admin/knowledge/sources` }),
        providesTags: ["admin-knowledge-controller"],
      }),
      getCourseDetails: build.query<
        GetCourseDetailsApiResponse,
        GetCourseDetailsApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/knowledge/courses/${queryArg.courseCode}`,
        }),
        providesTags: ["admin-knowledge-controller"],
      }),
      getCourseChunks: build.query<
        GetCourseChunksApiResponse,
        GetCourseChunksApiArg
      >({
        query: (queryArg) => ({
          url: `/api/admin/knowledge/courses/${queryArg.courseCode}/chunks`,
        }),
        providesTags: ["admin-knowledge-controller"],
      }),
      getScanLogs: build.query<GetScanLogsApiResponse, GetScanLogsApiArg>({
        query: () => ({ url: `/api/admin/github/scan-logs` }),
        providesTags: ["admin-github-controller"],
      }),
      getByCourse1: build.query<GetByCourse1ApiResponse, GetByCourse1ApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/courses/relationships/course/${queryArg.courseId}`,
        }),
        providesTags: ["admin-course-relationship-controller"],
      }),
      removeBookmark: build.mutation<
        RemoveBookmarkApiResponse,
        RemoveBookmarkApiArg
      >({
        query: (queryArg) => ({
          url: `/api/student/bookmarks/${queryArg.id}`,
          method: "DELETE",
        }),
        invalidatesTags: ["student-bookmark-controller"],
      }),
      delete2: build.mutation<Delete2ApiResponse, Delete2ApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/notes/${queryArg.id}`,
          method: "DELETE",
        }),
        invalidatesTags: ["admin-note-controller"],
      }),
      delete3: build.mutation<Delete3ApiResponse, Delete3ApiArg>({
        query: (queryArg) => ({
          url: `/api/admin/courses/relationships/${queryArg.id}`,
          method: "DELETE",
        }),
        invalidatesTags: ["admin-course-relationship-controller"],
      }),
    }),
    overrideExisting: false,
  });
export { injectedRtkApi as generatedApi };
export type UpdateApiResponse = /** status 200 OK */ RoadmapResponse;
export type UpdateApiArg = {
  id: number;
  roadmapRequest: RoadmapRequest;
};
export type DeleteApiAdminRoadmapsByIdApiResponse = unknown;
export type DeleteApiAdminRoadmapsByIdApiArg = {
  id: number;
};
export type UpdatePhaseApiResponse = /** status 200 OK */ PhaseResponse;
export type UpdatePhaseApiArg = {
  phaseId: number;
  phaseRequest: PhaseRequest;
};
export type DeletePhaseApiResponse = unknown;
export type DeletePhaseApiArg = {
  phaseId: number;
};
export type UpdateItemApiResponse = /** status 200 OK */ ItemResponse;
export type UpdateItemApiArg = {
  itemId: number;
  itemRequest: ItemRequest;
};
export type DeleteItemApiResponse = unknown;
export type DeleteItemApiArg = {
  itemId: number;
};
export type UpdateRepoApiResponse = /** status 200 OK */ RepoSummaryResponse;
export type UpdateRepoApiArg = {
  repoId: number;
  approvedRepoUpdateRequest: ApprovedRepoUpdateRequest;
};
export type DeleteRepoApiResponse = unknown;
export type DeleteRepoApiArg = {
  repoId: number;
};
export type Update1ApiResponse = /** status 200 OK */ CourseDetailResponse;
export type Update1ApiArg = {
  id: number;
  adminCourseUpsertRequest: AdminCourseUpsertRequest;
};
export type Delete1ApiResponse = unknown;
export type Delete1ApiArg = {
  id: number;
};
export type UpdateYoutubePlaylistApiResponse =
  /** status 200 OK */ YoutubePlaylistResponse;
export type UpdateYoutubePlaylistApiArg = {
  courseId: number;
  id: number;
  youtubePlaylistRequest: YoutubePlaylistRequest;
};
export type DeleteYoutubePlaylistApiResponse = unknown;
export type DeleteYoutubePlaylistApiArg = {
  courseId: number;
  id: number;
};
export type UpdateTutorialApiResponse = /** status 200 OK */ TutorialResponse;
export type UpdateTutorialApiArg = {
  courseId: number;
  id: number;
  tutorialRequest: TutorialRequest;
};
export type DeleteTutorialApiResponse = unknown;
export type DeleteTutorialApiArg = {
  courseId: number;
  id: number;
};
export type UpdateArticleApiResponse = /** status 200 OK */ ArticleResponse;
export type UpdateArticleApiArg = {
  courseId: number;
  id: number;
  articleRequest: ArticleRequest;
};
export type DeleteArticleApiResponse = unknown;
export type DeleteArticleApiArg = {
  courseId: number;
  id: number;
};
export type VerifyOtpApiResponse = /** status 200 OK */ StudentAuthResponse;
export type VerifyOtpApiArg = {
  otpVerificationRequest: OtpVerificationRequest;
};
export type ResetPasswordApiResponse = /** status 200 OK */ StudentAuthResponse;
export type ResetPasswordApiArg = {
  resetPasswordRequest: ResetPasswordRequest;
};
export type ResendOtpApiResponse = /** status 200 OK */ {
  [key: string]: string;
};
export type ResendOtpApiArg = {
  body: {
    [key: string]: string;
  };
};
export type VoteRepoApiResponse = /** status 200 OK */ RepoVoteResponse;
export type VoteRepoApiArg = {
  repoId: number;
  repoVoteRequest: RepoVoteRequest;
};
export type UpsertRepoReviewApiResponse = /** status 200 OK */ ReviewResponse;
export type UpsertRepoReviewApiArg = {
  repoId: number;
  reviewRequest: ReviewRequest;
};
export type DeleteRepoReviewApiResponse = unknown;
export type DeleteRepoReviewApiArg = {
  repoId: number;
};
export type RegisterApiResponse = /** status 200 OK */ StudentProfileResponse;
export type RegisterApiArg = {
  studentRegisterRequest: StudentRegisterRequest;
};
export type LogoutApiResponse = /** status 200 OK */ {
  [key: string]: string;
};
export type LogoutApiArg = {
  authorization: string;
};
export type LoginApiResponse = /** status 200 OK */ StudentAuthResponse;
export type LoginApiArg = {
  studentLoginRequest: StudentLoginRequest;
};
export type ForgotPasswordApiResponse = /** status 200 OK */ {
  [key: string]: string;
};
export type ForgotPasswordApiArg = {
  forgotPasswordRequest: ForgotPasswordRequest;
};
export type UpsertCourseReviewApiResponse = /** status 200 OK */ ReviewResponse;
export type UpsertCourseReviewApiArg = {
  courseId: number;
  reviewRequest: ReviewRequest;
};
export type DeleteCourseReviewApiResponse = unknown;
export type DeleteCourseReviewApiArg = {
  courseId: number;
};
export type GetBookmarksApiResponse =
  /** status 200 OK */ StudentBookmarkResponse[];
export type GetBookmarksApiArg = void;
export type AddBookmarkApiResponse =
  /** status 200 OK */ StudentBookmarkResponse;
export type AddBookmarkApiArg = {
  studentBookmarkRequest: StudentBookmarkRequest;
};
export type ListFramesApiResponse = /** status 200 OK */ PhotoboothFrameDto[];
export type ListFramesApiArg = void;
export type UpsertFrameApiResponse = /** status 200 OK */ PhotoboothFrameDto;
export type UpsertFrameApiArg = {
  photoboothFrameDto: PhotoboothFrameDto;
};
export type UploadOverlayApiResponse = /** status 200 OK */ object;
export type UploadOverlayApiArg = {
  frameId: string;
  body: {
    file: Blob;
  };
};
export type QueryApiResponse = /** status 200 OK */ SubjectQaResponse;
export type QueryApiArg = {
  subjectQaRequest: SubjectQaRequest;
};
export type QueryKnowledgeGraphApiResponse =
  /** status 200 OK */ AiQueryResponse;
export type QueryKnowledgeGraphApiArg = {
  aiQueryRequest: AiQueryRequest;
};
export type GenerateRoadmapApiResponse =
  /** status 200 OK */ RoadmapRecommendationResponse;
export type GenerateRoadmapApiArg = {
  roadmapGenerationRequest: RoadmapGenerationRequest;
};
export type ChatApiResponse = /** status 200 OK */ ChatResponse;
export type ChatApiArg = {
  chatRequest: ChatRequest;
};
export type ListApiResponse = /** status 200 OK */ RoadmapResponse[];
export type ListApiArg = void;
export type CreateApiResponse = /** status 200 OK */ RoadmapResponse;
export type CreateApiArg = {
  roadmapRequest: RoadmapRequest;
};
export type GetPhasesApiResponse = /** status 200 OK */ PhaseResponse[];
export type GetPhasesApiArg = {
  roadmapId: number;
};
export type CreatePhaseApiResponse = /** status 200 OK */ PhaseResponse;
export type CreatePhaseApiArg = {
  roadmapId: number;
  phaseRequest: PhaseRequest;
};
export type GetItemsApiResponse = /** status 200 OK */ ItemResponse[];
export type GetItemsApiArg = {
  phaseId: number;
};
export type CreateItemApiResponse = /** status 200 OK */ ItemResponse;
export type CreateItemApiArg = {
  phaseId: number;
  itemRequest: ItemRequest;
};
export type RejectApiResponse = /** status 200 OK */ RepoCandidateResponse;
export type RejectApiArg = {
  candidateId: number;
};
export type ApproveApiResponse = /** status 200 OK */ RepoCandidateResponse;
export type ApproveApiArg = {
  candidateId: number;
  candidateReviewRequest: CandidateReviewRequest;
};
export type RecrawlApiResponse = /** status 200 OK */ KnowledgeSource;
export type RecrawlApiArg = {
  sourceId: string;
};
export type EmbedSourceApiResponse = /** status 200 OK */ EmbedResponse;
export type EmbedSourceApiArg = {
  sourceId: string;
  force?: boolean;
};
export type SearchApiResponse = /** status 200 OK */ SearchResponse;
export type SearchApiArg = {
  searchRequest: SearchRequest;
};
export type RagPreviewApiResponse = /** status 200 OK */ RagPreviewResponse;
export type RagPreviewApiArg = {
  ragPreviewRequest: RagPreviewRequest;
};
export type IngestFolderApiResponse =
  /** status 200 OK */ FolderIngestionSummary;
export type IngestFolderApiArg = void;
export type IngestFileApiResponse = /** status 200 OK */ IngestionReport;
export type IngestFileApiArg = {
  ingestFileRequest: IngestFileRequest;
};
export type ImportUrlApiResponse = /** status 200 OK */ KnowledgeSource;
export type ImportUrlApiArg = {
  webImportRequest: WebImportRequest;
};
export type CrawlUrlApiResponse = /** status 200 OK */ KnowledgeSource[];
export type CrawlUrlApiArg = {
  crawlRequest: CrawlRequest;
};
export type EmbedCourseApiResponse = /** status 200 OK */ EmbedResponse;
export type EmbedCourseApiArg = {
  courseCode: string;
  force?: boolean;
};
export type ScanApiResponse = /** status 200 OK */ RepoCandidateResponse[];
export type ScanApiArg = {
  githubScanRequest: GithubScanRequest;
};
export type ScanAllApiResponse = /** status 200 OK */ {
  [key: string]: string;
};
export type ScanAllApiArg = void;
export type List1ApiResponse = /** status 200 OK */ CourseSummaryResponse[];
export type List1ApiArg = void;
export type Create1ApiResponse = /** status 200 OK */ CourseDetailResponse;
export type Create1ApiArg = {
  adminCourseUpsertRequest: AdminCourseUpsertRequest;
};
export type GetYoutubePlaylistsApiResponse =
  /** status 200 OK */ YoutubePlaylistResponse[];
export type GetYoutubePlaylistsApiArg = {
  courseId: number;
};
export type CreateYoutubePlaylistApiResponse =
  /** status 200 OK */ YoutubePlaylistResponse;
export type CreateYoutubePlaylistApiArg = {
  courseId: number;
  youtubePlaylistRequest: YoutubePlaylistRequest;
};
export type GetTutorialsApiResponse = /** status 200 OK */ TutorialResponse[];
export type GetTutorialsApiArg = {
  courseId: number;
};
export type CreateTutorialApiResponse = /** status 200 OK */ TutorialResponse;
export type CreateTutorialApiArg = {
  courseId: number;
  tutorialRequest: TutorialRequest;
};
export type GetArticlesApiResponse = /** status 200 OK */ ArticleResponse[];
export type GetArticlesApiArg = {
  courseId: number;
};
export type CreateArticleApiResponse = /** status 200 OK */ ArticleResponse;
export type CreateArticleApiArg = {
  courseId: number;
  articleRequest: ArticleRequest;
};
export type GetAllApiResponse =
  /** status 200 OK */ CourseRelationshipResponse[];
export type GetAllApiArg = void;
export type Create2ApiResponse =
  /** status 200 OK */ CourseRelationshipResponse;
export type Create2ApiArg = {
  courseRelationshipRequest: CourseRelationshipRequest;
};
export type Logout1ApiResponse = /** status 200 OK */ {
  [key: string]: string;
};
export type Logout1ApiArg = {
  authorization: string;
};
export type Login1ApiResponse = /** status 200 OK */ LoginResponse;
export type Login1ApiArg = {
  loginRequest: LoginRequest;
};
export type GetAllTechStacksApiResponse =
  /** status 200 OK */ TechStackResponse[];
export type GetAllTechStacksApiArg = void;
export type MeApiResponse = /** status 200 OK */ StudentProfileResponse;
export type MeApiArg = void;
export type GetChannelsApiResponse = /** status 200 OK */ ChatChannelResponse[];
export type GetChannelsApiArg = void;
export type GetMessagesApiResponse =
  /** status 200 OK */ PageChatMessageResponse;
export type GetMessagesApiArg = {
  channelId: number;
  page?: number;
  size?: number;
};
export type GetRepoByIdApiResponse = /** status 200 OK */ RepoSummaryResponse;
export type GetRepoByIdApiArg = {
  repoId: number;
};
export type GetRepoSocialInfoApiResponse =
  /** status 200 OK */ RepoSocialInfoResponse;
export type GetRepoSocialInfoApiArg = {
  repoId: number;
};
export type GetFrameApiResponse = /** status 200 OK */ PhotoboothFrameDto;
export type GetFrameApiArg = {
  frameId: string;
};
export type DeleteFrameApiResponse = unknown;
export type DeleteFrameApiArg = {
  frameId: string;
};
export type GetTopStacksApiResponse = /** status 200 OK */ string[];
export type GetTopStacksApiArg = void;
export type GetAllReposApiResponse = /** status 200 OK */ RepoSummaryResponse[];
export type GetAllReposApiArg = void;
export type GetRecentReposApiResponse =
  /** status 200 OK */ RepoSummaryResponse[];
export type GetRecentReposApiArg = void;
export type GetCoursesApiResponse =
  /** status 200 OK */ CourseSummaryResponse[];
export type GetCoursesApiArg = {
  q?: string;
  subjectType?: string;
  semester?: number;
  managementUnit?: string;
};
export type GetCourseDetailApiResponse =
  /** status 200 OK */ CourseDetailResponse;
export type GetCourseDetailApiArg = {
  id: number;
};
export type GetVideosApiResponse = /** status 200 OK */ object[];
export type GetVideosApiArg = {
  id: number;
};
export type GetTutorials1ApiResponse = /** status 200 OK */ object[];
export type GetTutorials1ApiArg = {
  id: number;
};
export type GetArticles1ApiResponse = /** status 200 OK */ object[];
export type GetArticles1ApiArg = {
  id: number;
};
export type GetCourseReviewsApiResponse =
  /** status 200 OK */ ReviewSummaryResponse;
export type GetCourseReviewsApiArg = {
  courseId: number;
};
export type GetReposByCourseApiResponse =
  /** status 200 OK */ RepoSummaryResponse[];
export type GetReposByCourseApiArg = {
  courseId: number;
  techStack?: string;
};
export type GetAll1ApiResponse =
  /** status 200 OK */ CourseRelationshipResponse[];
export type GetAll1ApiArg = void;
export type GetByCourseApiResponse =
  /** status 200 OK */ CourseRelationshipResponse[];
export type GetByCourseApiArg = {
  courseId: number;
};
export type GetGraphApiResponse = /** status 200 OK */ KnowledgeGraphResponse;
export type GetGraphApiArg = void;
export type GetRepoSummaryApiResponse = /** status 200 OK */ AiResponse;
export type GetRepoSummaryApiArg = {
  repoId: number;
};
export type GetTutorAdviceApiResponse = /** status 200 OK */ AiResponse;
export type GetTutorAdviceApiArg = {
  repoId: number;
};
export type GetChatHistoryApiResponse = /** status 200 OK */ ChatResponse[];
export type GetChatHistoryApiArg = {
  sessionId: string;
};
export type GetAllApprovedReposApiResponse =
  /** status 200 OK */ RepoSummaryResponse[];
export type GetAllApprovedReposApiArg = void;
export type GetPendingCandidatesApiResponse =
  /** status 200 OK */ RepoCandidateResponse[];
export type GetPendingCandidatesApiArg = {
  reviewer?: string;
};
export type StatsApiResponse = /** status 200 OK */ ReviewerStatsResponse[];
export type StatsApiArg = void;
export type List2ApiResponse = /** status 200 OK */ NoteResponse[];
export type List2ApiArg = void;
export type ListSourcesApiResponse =
  /** status 200 OK */ KnowledgeSourceResponse[];
export type ListSourcesApiArg = void;
export type GetCourseDetailsApiResponse =
  /** status 200 OK */ SyllabusDetailsResponse;
export type GetCourseDetailsApiArg = {
  courseCode: string;
};
export type GetCourseChunksApiResponse = /** status 200 OK */ KnowledgeChunk[];
export type GetCourseChunksApiArg = {
  courseCode: string;
};
export type GetScanLogsApiResponse = /** status 200 OK */ string[];
export type GetScanLogsApiArg = void;
export type GetByCourse1ApiResponse =
  /** status 200 OK */ CourseRelationshipResponse[];
export type GetByCourse1ApiArg = {
  courseId: number;
};
export type RemoveBookmarkApiResponse = unknown;
export type RemoveBookmarkApiArg = {
  id: number;
};
export type Delete2ApiResponse = unknown;
export type Delete2ApiArg = {
  id: number;
};
export type Delete3ApiResponse = unknown;
export type Delete3ApiArg = {
  id: number;
};
export type RoadmapResponse = {
  id?: number;
  studentId?: number;
  studentCode?: string;
  studentName?: string;
  title?: string;
  description?: string;
  markdownContent?: string;
  isPublic?: boolean;
  createdAt?: string;
  updatedAt?: string;
};
export type RoadmapRequest = {
  studentId: number;
  title?: string;
  description?: string;
  markdownContent?: string;
  isPublic?: boolean;
};
export type PhaseResponse = {
  id?: number;
  roadmapId?: number;
  title?: string;
  description?: string;
  sortOrder?: number;
  createdAt?: string;
};
export type PhaseRequest = {
  title?: string;
  description?: string;
  sortOrder?: number;
};
export type ItemResponse = {
  id?: number;
  phaseId?: number;
  targetType?: "COURSE" | "REPO";
  targetId?: number;
  title?: string;
  note?: string;
  sortOrder?: number;
  createdAt?: string;
};
export type ItemRequest = {
  targetType: "COURSE" | "REPO";
  targetId: number;
  title?: string;
  note?: string;
  sortOrder?: number;
};
export type TechStackResponse = {
  name?: string;
};
export type RepoSummaryResponse = {
  id?: number;
  displayName?: string;
  description?: string;
  githubUrl?: string;
  primaryLanguage?: string;
  stars?: number;
  techStacks?: TechStackResponse[];
  courseId?: number;
  courseCode?: string;
  courseName?: string;
  readmeExcerpt?: string;
  fileTree?: string;
  hasReadme?: boolean;
  lastPushedAt?: string;
};
export type ApprovedRepoUpdateRequest = {
  displayName?: string;
  description?: string;
  githubUrl?: string;
  primaryLanguage?: string;
  stars?: number;
  techStacks?: string[];
  active?: boolean;
  courseId?: number;
};
export type JsonNode = object;
export type CourseDetailResponse = {
  id?: number;
  code?: string;
  name?: string;
  nameEn?: string;
  description?: string;
  theoryHours?: number;
  practiceHours?: number;
  credits?: number;
  subjectType?: string;
  isOpen?: boolean;
  managementUnit?: string;
  codeOld?: string;
  equivalentMH?: string;
  prerequisiteMH?: string;
  previousMH?: string;
  learningObjectives?: string;
  gradingCriteria?: string;
  topics?: JsonNode;
  repos?: RepoSummaryResponse[];
};
export type AdminCourseUpsertRequest = {
  code?: string;
  name?: string;
  nameEn?: string;
  credits: number;
  lectureHours?: number;
  practiceHours?: number;
  subjectType?: string;
  isOpen?: boolean;
  managementUnit?: string;
  codeOld?: string;
  equivalentMH?: string;
  prerequisiteMH?: string;
  previousMH?: string;
  description?: string;
  learningObjectives?: string;
  gradingCriteria?: string;
  topics?: JsonNode;
};
export type YoutubePlaylistResponse = {
  id?: number;
  courseId?: number;
  title?: string;
  url?: string;
  description?: string;
  channelName?: string;
  createdAt?: string;
};
export type YoutubePlaylistRequest = {
  title?: string;
  url?: string;
  description?: string;
  channelName?: string;
};
export type TutorialResponse = {
  id?: number;
  courseId?: number;
  title?: string;
  url?: string;
  type?: string;
  description?: string;
  createdAt?: string;
};
export type TutorialRequest = {
  title?: string;
  url?: string;
  type?: string;
  description?: string;
};
export type ArticleResponse = {
  id?: number;
  courseId?: number;
  title?: string;
  url?: string;
  author?: string;
  description?: string;
  createdAt?: string;
};
export type ArticleRequest = {
  title?: string;
  url?: string;
  author?: string;
  description?: string;
};
export type StudentAuthResponse = {
  token?: string;
  id?: number;
  studentCode?: string;
  fullName?: string;
  email?: string;
};
export type OtpVerificationRequest = {
  email?: string;
  otpCode?: string;
};
export type ResetPasswordRequest = {
  email?: string;
  studentCode?: string;
  otpCode?: string;
  newPassword?: string;
};
export type RepoVoteResponse = {
  repoId?: number;
  studentId?: number;
  voteValue?: number;
  voteScore?: number;
};
export type RepoVoteRequest = {
  voteValue: number;
};
export type ReviewResponse = {
  id?: number;
  targetId?: number;
  studentId?: number;
  studentName?: string;
  rating?: number;
  comment?: string;
  createdAt?: string;
  updatedAt?: string;
};
export type ReviewRequest = {
  rating: number;
  comment?: string;
};
export type StudentProfileResponse = {
  id?: number;
  studentCode?: string;
  fullName?: string;
  email?: string;
};
export type StudentRegisterRequest = {
  studentCode?: string;
  fullName?: string;
  email?: string;
  password?: string;
};
export type StudentLoginRequest = {
  studentCode?: string;
  password?: string;
};
export type ForgotPasswordRequest = {
  studentCode?: string;
};
export type StudentBookmarkResponse = {
  id?: number;
  targetType?: string;
  targetId?: number;
  title?: string;
  subtitle?: string;
  url?: string;
  createdAt?: string;
};
export type StudentBookmarkRequest = {
  targetType?: string;
  targetId: number;
  title?: string;
  subtitle?: string;
  url?: string;
};
export type PhotoboothFrameDto = {
  frameId?: string;
  name?: string;
  displayName?: string;
  photoCount?: number;
  description?: string;
  overlayImageUrl?: string;
  filter?: string;
  backgroundColor?: string;
  slots?: string;
};
export type SubjectQaResponse = {
  answer?: string;
  sessionId?: string;
  relevantNodeIds?: number[];
  sources?: string[];
  type?: string;
  roadmap?: RoadmapRecommendationResponse;
};
export type SubjectQaRequest = {
  message?: string;
  sessionId?: string;
};
export type AiQueryResponse = {
  answer?: string;
  relevantNodeIds?: number[];
  type?: string;
};
export type AiQueryRequest = {
  query?: string;
};
export type CourseRecommendation = {
  courseId?: number;
  courseCode?: string;
  courseName?: string;
  reasoning?: string;
  description?: string;
  isMandatory?: boolean;
  semester?: number;
  credits?: number;
};
export type GraduationTrack = {
  type?: string;
  name?: string;
  description?: string;
  credits?: number;
  requirements?: string;
  recommendation?: string;
  recommended?: boolean;
  courseCodes?: string[];
};
export type ElectiveCandidate = {
  courseId?: number;
  courseCode?: string;
  courseName?: string;
  credits?: number;
  score?: number;
  isSelected?: boolean;
  description?: string;
  reasoning?: string;
  semester?: number;
};
export type ElectivePoolCandidates = {
  poolId?: string;
  poolName?: string;
  targetTC?: number;
  currentTC?: number;
  candidates?: ElectiveCandidate[];
};
export type RoadmapRecommendationResponse = {
  summary?: string;
  recommendedCourses?: CourseRecommendation[];
  graduationTracks?: GraduationTrack[];
  electivePools?: ElectivePoolCandidates[];
};
export type RoadmapGenerationRequest = {
  learningGoals?: string;
  careerPath?: string;
};
export type ChatResponse = {
  sessionId?: string;
  message?: string;
  sources?: string[];
  createdAt?: string;
};
export type ChatRequest = {
  sessionId?: string;
  message?: string;
};
export type RepoCandidateResponse = {
  id?: number;
  githubOwner?: string;
  githubName?: string;
  githubUrl?: string;
  status?: string;
  description?: string;
  primaryLanguage?: string;
  topics?: string;
  stars?: number;
  forks?: number;
  lastPushedAt?: string;
  readmeExcerpt?: string;
  hasReadme?: boolean;
  fileTree?: string;
  assignedReviewer?: string;
  courseId?: number;
  courseCode?: string;
  courseName?: string;
  reviewNote?: string;
};
export type CandidateReviewRequest = {
  description?: string;
  techStacks?: string[];
  reviewNote?: string;
};
export type KnowledgeSource = {
  id?: string;
  sourceType?: string;
  title?: string;
  fileName?: string;
  filePath?: string;
  url?: string;
  contentHash?: string;
  trustLevel?: string;
  status?: string;
  rawText?: string;
  errorMessage?: string;
  createdAt?: string;
  updatedAt?: string;
};
export type EmbedResponse = {
  status?: string;
  chunksEmbedded?: number;
  totalChunks?: number;
  message?: string;
};
export type SearchResult = {
  chunkId?: string;
  sourceId?: string;
  courseCode?: string;
  sectionTitle?: string;
  pageFrom?: number;
  pageTo?: number;
  score?: number;
  text?: string;
};
export type SearchResponse = {
  query?: string;
  courseCode?: string;
  results?: SearchResult[];
};
export type SearchRequest = {
  courseCode?: string;
  query?: string;
  topK?: number;
};
export type RagPreviewResponse = {
  courseCode?: string;
  query?: string;
  topK?: number;
  retrievedChunks?: SearchResult[];
  constructedPrompt?: string;
};
export type RagPreviewRequest = {
  courseCode?: string;
  query?: string;
  topK?: number;
};
export type FolderIngestionSummary = {
  totalFiles?: number;
  completed?: number;
  skipped?: number;
  failed?: number;
  courseCodes?: string[];
};
export type IngestionReport = {
  sourceId?: string;
  courseCode?: string;
  status?: string;
  warnings?: string[];
  errorMessage?: string;
};
export type IngestFileRequest = {
  filePath?: string;
};
export type WebImportRequest = {
  url?: string;
  courseCode?: string;
  trustLevel?: string;
  embedAfterImport?: boolean;
};
export type CrawlRequest = {
  url?: string;
  courseCode?: string;
  trustLevel?: string;
  maxPages?: number;
  embedAfterImport?: boolean;
};
export type GithubScanRequest = {
  courseId: number;
  query: string;
};
export type CourseSummaryResponse = {
  id?: number;
  code?: string;
  name?: string;
  description?: string;
  repoCount?: number;
  semester?: number;
  credits?: number;
  loaiMonHoc?: string;
  managementUnit?: string;
};
export type CourseRelationshipResponse = {
  id?: number;
  courseId?: number;
  courseCode?: string;
  courseName?: string;
  courseNameEn?: string;
  relatedCourseId?: number;
  relatedCourseCode?: string;
  relatedCourseName?: string;
  relatedCourseNameEn?: string;
  relationType?: "PREREQUISITE" | "COMPLEMENTARY" | "COREQUISITE";
  createdAt?: string;
};
export type CourseRelationshipRequest = {
  courseId: number;
  relatedCourseId: number;
  relationType: "PREREQUISITE" | "COMPLEMENTARY" | "COREQUISITE";
};
export type LoginResponse = {
  token?: string;
};
export type LoginRequest = {
  username?: string;
  password?: string;
};
export type ChatChannelResponse = {
  id?: number;
  channelId?: string;
  name?: string;
  type?: "GENERAL" | "COURSE" | "TECH_STACK";
  referenceId?: string;
};
export type ChatMessageResponse = {
  id?: number;
  channelId?: number;
  studentId?: number;
  senderName?: string;
  content?: string;
  createdAt?: string;
};
export type SortObject = {
  empty?: boolean;
  sorted?: boolean;
  unsorted?: boolean;
};
export type PageableObject = {
  offset?: number;
  pageNumber?: number;
  pageSize?: number;
  paged?: boolean;
  sort?: SortObject;
  unpaged?: boolean;
};
export type PageChatMessageResponse = {
  totalPages?: number;
  totalElements?: number;
  size?: number;
  content?: ChatMessageResponse[];
  number?: number;
  first?: boolean;
  last?: boolean;
  pageable?: PageableObject;
  numberOfElements?: number;
  sort?: SortObject;
  empty?: boolean;
};
export type RepoSocialInfoResponse = {
  repoId?: number;
  voteScore?: number;
  averageRating?: number;
  reviews?: ReviewResponse[];
};
export type ReviewSummaryResponse = {
  targetId?: number;
  averageRating?: number;
  reviews?: ReviewResponse[];
};
export type GraphNode = {
  id?: number;
  name?: string;
  code?: string;
  description?: string;
  val?: number;
  level?: number;
  impactScore?: number;
  semester?: number;
  electiveGroup?: string;
};
export type GraphLink = {
  source?: number;
  target?: number;
  type?: "PREREQUISITE" | "COMPLEMENTARY" | "COREQUISITE";
};
export type KnowledgeGraphResponse = {
  nodes?: GraphNode[];
  links?: GraphLink[];
};
export type AiResponse = {
  content?: string;
  type?: string;
};
export type ReviewerStatsResponse = {
  reviewer?: string;
  remaining?: number;
  completed?: number;
};
export type NoteCodeSnippetResponse = {
  id?: number;
  noteId?: number;
  language?: string;
  code?: string;
  caption?: string;
  sortOrder?: number;
};
export type NoteResponse = {
  id?: number;
  studentId?: number;
  studentCode?: string;
  studentName?: string;
  title?: string;
  contentMarkdown?: string;
  targetType?: "COURSE" | "REPO" | "NONE";
  targetId?: number;
  createdAt?: string;
  updatedAt?: string;
  snippets?: NoteCodeSnippetResponse[];
};
export type KnowledgeSourceResponse = {
  id?: string;
  sourceType?: string;
  fileName?: string;
  status?: string;
  contentHash?: string;
  errorMessage?: string;
  updatedAt?: string;
};
export type SyllabusDto = {
  id?: string;
  sourceId?: string;
  courseCode?: string;
  courseNameVi?: string;
  courseNameEn?: string;
  credits?: number;
  theoryHours?: number;
  practiceHours?: number;
  selfStudyHours?: number;
  prerequisite?: string;
  previousCourse?: string;
  department?: string;
  description?: string;
};
export type ObjectiveDto = {
  id?: string;
  courseCode?: string;
  description?: string;
  outcomeRefs?: JsonNode;
};
export type OutcomeDto = {
  id?: string;
  courseCode?: string;
  outcomeCode?: string;
  description?: string;
};
export type SessionDto = {
  id?: string;
  courseCode?: string;
  sourceId?: string;
  sessionNo?: string;
  sessionType?: string;
  topic?: string;
  activities?: string;
  assessmentComponent?: string;
};
export type AssessmentDto = {
  id?: string;
  courseCode?: string;
  componentCode?: string;
  description?: string;
  weightPercent?: number;
};
export type SyllabusDetailsResponse = {
  syllabus?: SyllabusDto;
  objectives?: ObjectiveDto[];
  outcomes?: OutcomeDto[];
  sessions?: SessionDto[];
  assessments?: AssessmentDto[];
  references?: string[];
  tools?: string[];
};
export type KnowledgeChunk = {
  id?: string;
  source?: KnowledgeSource;
  courseCode?: string;
  chunkIndex?: number;
  sectionTitle?: string;
  chunkText?: string;
  metadataJson?: JsonNode;
  pageFrom?: number;
  pageTo?: number;
  embedding?: number[];
  createdAt?: string;
};
export const {
  useUpdateMutation,
  useDeleteApiAdminRoadmapsByIdMutation,
  useUpdatePhaseMutation,
  useDeletePhaseMutation,
  useUpdateItemMutation,
  useDeleteItemMutation,
  useUpdateRepoMutation,
  useDeleteRepoMutation,
  useUpdate1Mutation,
  useDelete1Mutation,
  useUpdateYoutubePlaylistMutation,
  useDeleteYoutubePlaylistMutation,
  useUpdateTutorialMutation,
  useDeleteTutorialMutation,
  useUpdateArticleMutation,
  useDeleteArticleMutation,
  useVerifyOtpMutation,
  useResetPasswordMutation,
  useResendOtpMutation,
  useVoteRepoMutation,
  useUpsertRepoReviewMutation,
  useDeleteRepoReviewMutation,
  useRegisterMutation,
  useLogoutMutation,
  useLoginMutation,
  useForgotPasswordMutation,
  useUpsertCourseReviewMutation,
  useDeleteCourseReviewMutation,
  useGetBookmarksQuery,
  useAddBookmarkMutation,
  useListFramesQuery,
  useUpsertFrameMutation,
  useUploadOverlayMutation,
  useQueryMutation,
  useQueryKnowledgeGraphMutation,
  useGenerateRoadmapMutation,
  useChatMutation,
  useListQuery,
  useCreateMutation,
  useGetPhasesQuery,
  useCreatePhaseMutation,
  useGetItemsQuery,
  useCreateItemMutation,
  useRejectMutation,
  useApproveMutation,
  useRecrawlMutation,
  useEmbedSourceMutation,
  useSearchMutation,
  useRagPreviewMutation,
  useIngestFolderMutation,
  useIngestFileMutation,
  useImportUrlMutation,
  useCrawlUrlMutation,
  useEmbedCourseMutation,
  useScanMutation,
  useScanAllMutation,
  useList1Query,
  useCreate1Mutation,
  useGetYoutubePlaylistsQuery,
  useCreateYoutubePlaylistMutation,
  useGetTutorialsQuery,
  useCreateTutorialMutation,
  useGetArticlesQuery,
  useCreateArticleMutation,
  useGetAllQuery,
  useCreate2Mutation,
  useLogout1Mutation,
  useLogin1Mutation,
  useGetAllTechStacksQuery,
  useMeQuery,
  useGetChannelsQuery,
  useGetMessagesQuery,
  useGetRepoByIdQuery,
  useGetRepoSocialInfoQuery,
  useGetFrameQuery,
  useDeleteFrameMutation,
  useGetTopStacksQuery,
  useGetAllReposQuery,
  useGetRecentReposQuery,
  useGetCoursesQuery,
  useGetCourseDetailQuery,
  useGetVideosQuery,
  useGetTutorials1Query,
  useGetArticles1Query,
  useGetCourseReviewsQuery,
  useGetReposByCourseQuery,
  useGetAll1Query,
  useGetByCourseQuery,
  useGetGraphQuery,
  useGetRepoSummaryQuery,
  useGetTutorAdviceQuery,
  useGetChatHistoryQuery,
  useGetAllApprovedReposQuery,
  useGetPendingCandidatesQuery,
  useStatsQuery,
  useList2Query,
  useListSourcesQuery,
  useGetCourseDetailsQuery,
  useGetCourseChunksQuery,
  useGetScanLogsQuery,
  useGetByCourse1Query,
  useRemoveBookmarkMutation,
  useDelete2Mutation,
  useDelete3Mutation,
} = injectedRtkApi;
