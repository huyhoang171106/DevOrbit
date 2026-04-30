export type CourseSummary = {
  id: number
  code: string
  name: string
}

export type CourseDetail = {
  id: number
  code: string
  name: string
  description: string | null
  credits: number
  theoryHours: number | null
  practiceHours: number | null
  subjectType: string | null
  repos: RepoSummary[]
}

export type RepoSummary = {
  id: number
  displayName: string
  description: string
  githubUrl: string
  primaryLanguage: string
  stars: number
  techStacks: string[]
}

export type RepoCandidate = {
  id: number
  githubOwner: string
  githubName: string
  githubUrl: string
  status: string
}

export type LoginRequest = {
  username: string
  password: string
}

export type LoginResponse = {
  token: string
}
