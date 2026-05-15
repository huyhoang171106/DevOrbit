export type CourseSummary = {
  id: number
  code: string
  name: string
  description?: string | null
  repoCount: number
  semester?: number | null
  credits?: number
  loaiMonHoc?: string | null
}

export type CourseDetail = {
  id: number
  code: string
  name: string
  nameEn: string | null
  description: string | null
  credits: number
  theoryHours: number | null
  practiceHours: number | null
  subjectType: string | null
  isOpen: boolean
  managementUnit: string | null
  codeOld: string | null
  equivalentMH: string | null
  prerequisiteMH: string | null
  previousMH: string | null
  repos: RepoSummary[]
}

export type RepoSummary = {
  id: number
  displayName: string
  description: string
  githubUrl: string
  primaryLanguage: string
  stars: number | null
  techStacks: string[]
  courseId: number | null
  courseCode: string | null
  courseName: string | null
}

export type RepoCandidate = {
  id: number
  githubOwner: string
  githubName: string
  githubUrl: string
  status: string
  description: string | null
  primaryLanguage: string | null
  topics: string | null
  stars: number
  forks: number
  lastPushedAt: string | null
  readmeExcerpt: string | null
  assignedReviewer: string | null
  courseId: number | null
  courseCode: string | null
  courseName: string | null
  reviewNote: string | null
}

export type ReviewerStats = {
  reviewer: string
  remaining: number
  completed: number
}

export type LoginRequest = {
  username: string
  password: string
}

export type LoginResponse = {
  token: string
}

export type StudentAuthResponse = {
  token: string
  id: number
  studentCode: string
  fullName: string
  email: string
}


export type BookmarkTargetType = 'COURSE' | 'REPO'

export type StudentBookmark = {
  id: number
  targetType: BookmarkTargetType
  targetId: number
  title: string
  subtitle: string | null
  url: string
  createdAt: string
}

// --- Course Resources ---

export type YoutubePlaylistRequest = {
  title: string
  url: string
  description?: string
  channelName?: string
}

export type YoutubePlaylistResponse = {
  id: number
  courseId: number
  title: string
  url: string
  description: string | null
  channelName: string | null
  createdAt: string
}

export type ArticleRequest = {
  title: string
  url: string
  author?: string
  description?: string
}

export type ArticleResponse = {
  id: number
  courseId: number
  title: string
  url: string
  author: string | null
  description: string | null
  createdAt: string
}

export type TutorialRequest = {
  title: string
  url: string
  type?: string
  description?: string
}

export type TutorialResponse = {
  id: number
  courseId: number
  title: string
  url: string
  type: string | null
  description: string | null
  createdAt: string
}

// --- Roadmaps ---

export type RoadmapRequest = {
  studentId: number
  title: string
  description?: string
  markdownContent?: string
  isPublic?: boolean
}

export type RoadmapResponse = {
  id: number
  studentId: number
  studentCode: string
  studentName: string
  title: string
  description: string | null
  markdownContent: string | null
  isPublic: boolean
  createdAt: string
  updatedAt: string
}

export type PhaseRequest = {
  title: string
  description?: string
  sortOrder?: number
}

export type PhaseResponse = {
  id: number
  roadmapId: number
  title: string
  description: string | null
  sortOrder: number
  createdAt: string
}

export type RoadmapItemTargetType = 'COURSE' | 'REPO'

export type ItemRequest = {
  targetType: RoadmapItemTargetType
  targetId: number
  title?: string
  note?: string
  sortOrder?: number
}

export type ItemResponse = {
  id: number
  phaseId: number
  targetType: RoadmapItemTargetType
  targetId: number
  title: string | null
  note: string | null
  sortOrder: number
  createdAt: string
}

// --- Knowledge Graph ---

export type GraphNode = {
  id: number
  name: string
  code: string
  description?: string | null
  val: number
  level: number
  impactScore: number
  semester?: number | null
  electiveGroup?: string | null
}

export type GraphLink = {
  source: number
  target: number
  type: 'PREREQUISITE' | 'COMPLEMENTARY' | 'COREQUISITE'
}

export type GraphResponse = {
  nodes: GraphNode[]
  links: GraphLink[]
}

export type ElectiveGroupInfo = {
  code: string
  name: string
  description: string
  minCredits: number
  parentGroupCode: string | null
  courseCount: number
}

export type CourseRelationType = 'PREREQUISITE' | 'COMPLEMENTARY' | 'COREQUISITE'

export type CourseRelationshipRequest = {
  courseId: number
  relatedCourseId: number
  relationType: CourseRelationType
}

export type CourseRelationshipResponse = {
  id: number
  courseId: number
  courseCode: string
  courseName: string
  courseNameEn: string | null
  relatedCourseId: number
  relatedCourseCode: string
  relatedCourseName: string
  relatedCourseNameEn: string | null
  relationType: CourseRelationType
  createdAt: string
}

// --- Notes ---

export type NoteTargetType = 'COURSE' | 'REPO' | 'NONE'

export type NoteCodeSnippetResponse = {
  id: number
  noteId: number
  language: string
  code: string
  caption: string
  sortOrder: number
}

export type NoteResponse = {
  id: number
  studentId: number
  studentCode: string
  studentName: string
  title: string
  contentMarkdown: string
  targetType: NoteTargetType
  targetId: number | null
  createdAt: string
  updatedAt: string
  snippets: NoteCodeSnippetResponse[]
}

// --- AI ---

export type AiResponse = {
  content: string
  type: 'SUMMARY' | 'TUTOR_ADVICE'
}
