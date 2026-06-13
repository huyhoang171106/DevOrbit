export interface AdminStats {
  totalStudents: number
  totalCourses: number
  totalRepos: number
  pendingCandidates: number
  recentStudents: StudentSummary[]
  recentCourseReviews: ReviewSummary[]
  recentSubmissions: SubmissionSummary[]
}

export interface StudentSummary {
  id: number
  fullName: string
  studentCode: string
}

export interface ReviewSummary {
  id: number
  studentName: string
  courseName: string
  rating: number
  comment: string
  createdAt: string
}

export interface SubmissionSummary {
  id: number
  githubUrl: string
  courseName: string
  status: string
}

export interface AdminStudent {
  id: number
  studentCode: string
  fullName: string
  email: string
  active: boolean
  emailVerified: boolean
}

export interface CourseReviewAdmin {
  id: number
  studentName: string
  courseName: string
  rating: number
  comment: string
  createdAt: string
}

export interface RepoReviewAdmin {
  id: number
  studentName: string
  repoName: string
  rating: number
  comment: string
  createdAt: string
}

export interface CommunityMessageAdmin {
  id: number
  channelName: string
  studentName: string
  content: string
  createdAt: string
}

export interface ChatSessionAdmin {
  id: string
  studentName: string
  title: string
  messageCount: number
  createdAt: string
}

export interface ChatMessageAdmin {
  id: number
  sender: string
  content: string
  createdAt: string
}

export interface TechStackAdmin {
  id: number
  name: string
}

export interface CourseUpsertRequest {
  code: string
  name: string
  nameEn?: string
  credits: number
  lectureHours?: number | null
  practiceHours?: number | null
  subjectType: string
  isOpen?: boolean
  managementUnit?: string
  codeOld?: string
  equivalentMH?: string
  prerequisiteMH?: string
  previousMH?: string
  description?: string
  learningObjectives?: string
  gradingCriteria?: string
  topics?: unknown
}

export interface ApprovedRepoUpdateRequest {
  displayName?: string
  description?: string
  githubUrl?: string
  primaryLanguage?: string
  stars?: number | null
  techStacks?: string[]
  active?: boolean
  courseId?: number | null
}

export interface CandidateReviewRequest {
  description?: string
  techStacks?: string[]
  reviewNote?: string
}
