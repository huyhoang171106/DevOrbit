import type { RepoSummary } from '../types/api'
import { cleanText, normalizeStringList } from './repoText'

export type RepoType =
  | 'programming_exercise'
  | 'project_practice'
  | 'study_material'
  | 'exam_review'
  | 'mixed_resource'
  | 'unknown'

export type UsefulnessRating =
  | 'highly_recommended'
  | 'recommended'
  | 'selective'
  | 'quick_reference'
  | 'low_priority'
  | 'insufficient_data'

export type ConfidenceLabel = 'high' | 'medium' | 'low'
export type ReadyToUseLevel = 'very_ready' | 'ready' | 'needs_check' | 'quick_reference' | 'insufficient_data'
export type CourseGroup = 'foundation_algorithms' | 'software_project' | 'design_process' | 'general_skills' | 'unknown'
export type CriterionApplicability = 'applicable' | 'not_applicable' | 'unknown'
export type CriterionStatus = 'strong' | 'ok' | 'weak' | 'missing' | 'not_applicable'
export type RecommendationTag =
  | 'ready_to_use'
  | 'needs_check'
  | 'reference_only'
  | 'good_study_material'
  | 'good_project_sample'
  | 'insufficient_data'

export type RepoEvaluationEvidence = {
  type:
    | 'readme'
    | 'file'
    | 'directory'
    | 'manifest'
    | 'test'
    | 'demo'
    | 'metadata'
    | 'warning_signal'
  path?: string
  message: string
}

export type RepoEvaluationWarning = {
  code: string
  severity: 'info' | 'warning' | 'critical'
  message: string
  paths?: string[]
}

export type RepoEvaluationCriterion = {
  key: string
  label: string
  score: number
  maxScore: number
  status: CriterionStatus
  applicability: CriterionApplicability
  confidence: number
  evidence: RepoEvaluationEvidence[]
  warnings: RepoEvaluationWarning[]
}

export type EvaluationSection = {
  title: string
  items: string[]
}

export type RepoSignals = {
  name: string
  description: string | null
  topics: string[]
  primaryLanguage: string | null
  techStacks: string[]
  stars: number | null
  forks: number | null
  updatedAt: string | null
  readmeText: string | null
  courseCode: string | null
  courseName: string | null
  hasReadme: boolean
  hasFileList: boolean
  filePaths: string[]
  hasSourceCode: boolean
  hasTests: boolean
  hasDocs: boolean
  hasAssignments: boolean
  hasSolutions: boolean
  hasSlides: boolean
  hasNotes: boolean
  hasExam: boolean
  hasAnswerOrSolution: boolean
  hasPackageFile: boolean
  hasBuildFile: boolean
  hasEnvExample: boolean
  hasDockerConfig: boolean
  hasLicense: boolean
  organizedFolders: boolean
  evidence: string[]
  missingSignals: string[]
}

export type RepoEvaluationResult = {
  repoType: RepoType
  repoTypeLabel: string
  usefulnessRating: UsefulnessRating
  usefulnessLabel: string
  usefulnessScore: number
  bestFor: string
  mainReason: string
  quickSummary: string
  quickBullets: string[]
  repoIdentity: string
  weapons: string
  techTools: string[]
  coreTopics: string[]
  readyToUseLevel: ReadyToUseLevel
  readyToUseLabel: string
  readyToUseStars: number
  readyToUseNote: string
  courseGroup: CourseGroup
  courseGroupLabel: string
  groupHighlights: string[]
  recommendation: string
  strengths: string[]
  weaknesses: string[]
  nextActions: string[]
  learningStrategy: string[]
  cautionNotes: string[]
  suitableUse: string[]
  applicability: string[]
  checksBeforeUsing: string[]
  evidence: string[]
  confidence: ConfidenceLabel
  confidenceLabel: string
  typeReason: string
  sections: EvaluationSection[]
  signals: RepoSignals
  classificationConfidence: number
  evaluationConfidence: number
  evidenceCoverage: number
  learningUsefulnessScore: number
  technicalReadinessScore: number | null
  runReadinessScore: number | null
  recommendationTag: RecommendationTag
  criteria: RepoEvaluationCriterion[]
  warnings: RepoEvaluationWarning[]
}

type OptionalRepoMetadata = {
  topics?: string[] | string | null
  tags?: string[] | string | null
  forks?: number | null
  updatedAt?: string | null
  lastPushedAt?: string | null
  readmeExcerpt?: string | null
  readmeContent?: string | null
  readmeMarkdown?: string | null
  readmeText?: string | null
  readme?: string | null
  hasReadme?: boolean | null
  files?: string[] | Array<{ path?: string | null; name?: string | null }> | null
  fileList?: string[] | Array<{ path?: string | null; name?: string | null }> | null
  paths?: string[] | null
  fileTree?: string[] | string | null
}

const repoTypeLabels: Record<RepoType, string> = {
  programming_exercise: 'Bài tập lập trình',
  project_practice: 'Project thực hành',
  study_material: 'Tài liệu học',
  exam_review: 'Đề thi / ôn tập',
  mixed_resource: 'Tài nguyên tổng hợp',
  unknown: 'Chưa rõ loại repo',
}

const usefulnessLabels: Record<UsefulnessRating, string> = {
  highly_recommended: 'Rất nên xem',
  recommended: 'Nên xem',
  selective: 'Xem có chọn lọc',
  quick_reference: 'Chỉ nên tham khảo nhanh',
  low_priority: 'Không ưu tiên',
  insufficient_data: 'Chưa đủ dữ liệu để kết luận',
}

const sourceFilePattern = /\.(c|cc|cpp|cs|dart|go|h|hpp|ipynb|java|js|jsx|kt|m|php|py|rb|rs|sql|swift|ts|tsx)$/i
const readyToUseLabels: Record<ReadyToUseLevel, string> = {
  very_ready: 'Rất sẵn sàng',
  ready: 'Khá sẵn sàng',
  needs_check: 'Cần kiểm tra thêm',
  quick_reference: 'Chỉ tham khảo nhanh',
  insufficient_data: 'Chưa đủ dữ liệu',
}

const courseGroupLabels: Record<CourseGroup, string> = {
  foundation_algorithms: 'Nền tảng lập trình & thuật toán',
  software_project: 'Phát triển phần mềm / Project',
  design_process: 'Phân tích thiết kế & quy trình phần mềm',
  general_skills: 'Đại cương, ngoại ngữ & kỹ năng',
  unknown: 'Chưa rõ nhóm môn',
}

const topicViMap: Record<string, string> = {
  // Học tập / đề thi
  'Answer key': 'Đáp án',
  'Answer': 'Đáp án',
  'Answers': 'Đáp án',
  'Solution': 'Lời giải',
  'Solutions': 'Lời giải',
  'Exam': 'Đề thi',
  'Final exam': 'Đề thi cuối kỳ',
  'Final': 'Cuối kỳ',
  'Midterm': 'Giữa kỳ',
  'Quiz': 'Quiz',
  'Assignment': 'Bài tập',
  'Assignments': 'Bài tập',
  'Homework': 'Bài tập về nhà',
  'Practice': 'Thực hành',
  'Lab': 'Lab',
  'Labs': 'Lab',
  // Tài liệu / kỹ năng
  'Report': 'Báo cáo',
  'Reports': 'Báo cáo',
  'Presentation': 'Thuyết trình',
  'Presentations': 'Thuyết trình',
  'Slide': 'Slide',
  'Slides': 'Slide',
  'Rubric': 'Tiêu chí chấm',
  'Guideline': 'Hướng dẫn',
  'Guidelines': 'Hướng dẫn',
  'Teamwork': 'Làm việc nhóm',
  'CV': 'CV',
  'Reflection': 'Nhật ký phản hồi',
  // Kỹ thuật / project
  'Database': 'Cơ sở dữ liệu',
  'DB': 'Cơ sở dữ liệu',
  'Authentication': 'Xác thực',
  'Authorization': 'Phân quyền',
  'Deployment': 'Triển khai',
  'Routing': 'Điều hướng',
  'Auth': 'Xác thực',
  'CRUD': 'CRUD',
  'Realtime': 'Thời gian thực',
  // DSA / thuật toán
  'Sorting': 'Sắp xếp',
  'Search': 'Tìm kiếm',
  'Searching': 'Tìm kiếm',
  'Linked List': 'Danh sách liên kết',
  'Stack': 'Ngăn xếp',
  'Queue': 'Hàng đợi',
  'Tree': 'Cây',
  'Binary Tree': 'Cây nhị phân',
  'Binary Search Tree': 'Cây nhị phân tìm kiếm',
  'Graph': 'Đồ thị',
  'Dynamic Programming': 'Quy hoạch động',
  'Hash Table': 'Bảng băm',
  'Recursion': 'Đệ quy',
}

function translateTopics(items: string[]): string[] {
  return items.map((t) => topicViMap[t] ?? t)
}

const projectConfigPattern = /(^|\/)(package(-lock)?\.json|pnpm-lock\.yaml|yarn\.lock|bun\.lock|requirements\.txt|pyproject\.toml|poetry\.lock|pom\.xml|build\.gradle|settings\.gradle|gradle\.properties|pubspec\.yaml|composer\.json|go\.mod|cargo\.toml|[^/]+\.(csproj|sln))$/i
const buildFilePattern = /(^|\/)(makefile|cmakelists\.txt|mvnw|gradlew|dockerfile)$/i
const sourceFolderPattern = /(^|\/)(src|source|app|lib|components|controllers|services|models)(\/|$)/i
const testPathPattern = /(^|\/)(__tests__|tests?|spec|input|output|sample)(\/|$)|(\.|-)(test|spec)\.[a-z0-9]+$/i

export function evaluateRepository(repo: RepoSummary): RepoEvaluationResult {
  const signals = extractRepoSignals(repo)
  const { repoType, reason } = classifyRepoType(signals)
  const courseGroup = detectCourseGroup(signals)
  const rubric = buildEvidenceRubric(repoType, courseGroup, signals)
  const runStars = mapRunReadinessToStars(rubric.runReadinessScore)
  const ready = buildReadyToUseFromRubric(repoType, signals, rubric.runReadinessScore, runStars)
  const usefulnessScore = rubric.learningUsefulnessScore
  const usefulnessRating = ratingFromScore(usefulnessScore, repoType)
  const confidence = getConfidenceFromScore(rubric.evaluationConfidence, repoType)
  const repoIdentity = buildRepoIdentity(repoType, courseGroup, signals)
  const techTools = detectTechTools(repoType, courseGroup, signals)
  const coreTopics = translateTopics(detectCoreTopics(courseGroup, repoType, signals))
  const groupHighlights = buildGroupHighlights(courseGroup, repoType, signals, coreTopics)
  const recommendation = buildRecommendationFromTag(rubric.recommendationTag, repoType, signals)
  const quickBullets = buildQuickBullets(repoIdentity, techTools, coreTopics, recommendation, signals)
  const bestFor = buildBestFor(repoType, usefulnessRating, signals)
  const mainReason = buildMainReason(repoType, usefulnessRating, signals)
  const quickSummary = buildQuickSummary(repoType, usefulnessRating, signals, confidence)
  const strengths = deriveStrengthsFromRubric(rubric.criteria, signals)
  const weaknesses = deriveWeaknessesFromRubric(rubric.criteria, rubric.warnings, repoType, signals, usefulnessRating)
  const nextActions = deriveActionsFromRubric(rubric.criteria, rubric.warnings, repoType, signals)
  const learningStrategy = buildLearningStrategy(repoType, courseGroup, signals)
  const cautionNotes = buildCautionNotes(repoType, courseGroup, signals)
  const suitableUse = buildSuitableUse(repoType, signals)
  const applicability = buildApplicability(repoType, usefulnessRating, signals)
  const checksBeforeUsing = buildChecksBeforeUsing(repoType, signals)
  const evidence = signals.evidence.length > 0
    ? signals.evidence
    : ['DevOrbit hiện chỉ có rất ít metadata cho repo này.']

  return {
    repoType,
    repoTypeLabel: repoTypeLabels[repoType],
    usefulnessRating,
    usefulnessLabel: usefulnessLabels[usefulnessRating],
    usefulnessScore,
    bestFor,
    mainReason,
    quickSummary,
    quickBullets,
    repoIdentity,
    weapons: techTools.length > 0 ? techTools.join(', ') : 'Chưa rõ',
    techTools,
    coreTopics,
    readyToUseLevel: ready.level,
    readyToUseLabel: ready.label,
    readyToUseStars: ready.stars,
    readyToUseNote: ready.note,
    courseGroup,
    courseGroupLabel: courseGroupLabels[courseGroup],
    groupHighlights,
    recommendation,
    strengths,
    weaknesses,
    nextActions,
    learningStrategy,
    cautionNotes,
    suitableUse,
    applicability,
    checksBeforeUsing,
    evidence,
    confidence,
    confidenceLabel: confidence === 'high' ? 'Cao' : confidence === 'medium' ? 'Trung bình' : 'Thấp',
    typeReason: reason,
    sections: [
      { title: 'Cần kiểm tra', items: checksBeforeUsing },
      { title: 'Thông tin tham khảo', items: evidence.slice(0, 6) },
    ],
    signals,
    classificationConfidence: rubric.classificationConfidence,
    evaluationConfidence: rubric.evaluationConfidence,
    evidenceCoverage: rubric.evidenceCoverage,
    learningUsefulnessScore: rubric.learningUsefulnessScore,
    technicalReadinessScore: rubric.technicalReadinessScore,
    runReadinessScore: rubric.runReadinessScore,
    recommendationTag: rubric.recommendationTag,
    criteria: rubric.criteria,
    warnings: rubric.warnings,
  }
}

export function extractRepoSignals(repo: RepoSummary): RepoSignals {
  const metadata = repo as RepoSummary & OptionalRepoMetadata
  const name = cleanText(repo.displayName) || `Repo #${repo.id}`
  const description = cleanText(repo.description)
  const topics = normalizeStringList(metadata.topics ?? metadata.tags)
  const primaryLanguage = cleanText(repo.primaryLanguage)
  const techStacks = normalizeStringList(repo.techStacks)
  const readmeText = cleanText(
    metadata.readmeExcerpt ?? metadata.readmeContent ?? metadata.readmeMarkdown ?? metadata.readmeText ?? metadata.readme,
  )
  const filePaths = normalizeFilePaths(metadata.files ?? metadata.fileList ?? metadata.paths ?? metadata.fileTree)
  const haystack = normalizeSearchText([name, description, primaryLanguage, ...techStacks, ...topics, readmeText, ...filePaths])
  const hasFileList = filePaths.length > 0
  const hasReadme = Boolean(readmeText) || metadata.hasReadme === true || hasPath(filePaths, /(^|\/)readme(\.md|\.txt)?$/i)
  const hasPackageFile = hasPath(filePaths, projectConfigPattern)
  const hasBuildFile = hasPackageFile || hasPath(filePaths, buildFilePattern)
  const hasEnvExample = hasPath(filePaths, /(^|\/)\.env\.(example|sample|template)$/i)
  const hasDockerConfig = hasPath(filePaths, /(^|\/)(docker-compose\.ya?ml|dockerfile)$/i)
  const hasSourceCode = Boolean(primaryLanguage || techStacks.length > 0 || hasPath(filePaths, sourceFolderPattern) || hasPath(filePaths, sourceFilePattern))
  const hasTests = hasPath(filePaths, testPathPattern) || contains(haystack, /\b(test|tests|unit test|input|output|sample)\b/)
  const hasAssignments = contains(haystack, /\b(lab|labs|assignment|assignments|exercise|exercises|homework|practice|practical|bai tap|bài tập|thuc hanh|thực hành|dsa|algorithm|oop)\b/)
  const hasSolutions = contains(haystack, /\b(solution|solutions|answer|answers|loi giai|lời giải)\b/) || hasPath(filePaths, /(^|\/)(solution|solutions|answer|answers)(\/|$)/i)
  const hasSlides = contains(haystack, /\b(slide|slides|ppt|pptx|lecture|lectures)\b/) || hasPath(filePaths, /(^|\/)(slides?|lectures?)(\/|$)|\.(pptx?)$/i)
  const hasNotes = contains(haystack, /\b(note|notes|document|docs|theory|ly thuyet|lý thuyết|summary|cheatsheet|giao trinh|giáo trình)\b/)
  const hasDocs = hasNotes || hasPath(filePaths, /(^|\/)(docs?|documents?|notes?)(\/|$)/i)
  const hasExam = contains(haystack, /\b(exam|exams|midterm|final|quiz|past exam|de thi|đề thi|on tap|ôn tập)\b/)
  const hasAnswerOrSolution = hasSolutions || contains(haystack, /\b(answer key|dap an|đáp án)\b/)
  const hasLicense = hasPath(filePaths, /(^|\/)licen[cs]e(\.md|\.txt)?$/i)
  const organizedFolders = hasFileList && countTopLevelFolders(filePaths) >= 3
  const stars = typeof repo.stars === 'number' ? repo.stars : null
  const forks = typeof metadata.forks === 'number' ? metadata.forks : null
  const updatedAt = formatVietnameseRelativeDate(metadata.lastPushedAt ?? metadata.updatedAt)
  const courseCode = cleanText(repo.courseCode)
  const courseName = cleanText(repo.courseName)
  const evidence = buildEvidence({
    name,
    description,
    topics,
    primaryLanguage,
    techStacks,
    stars,
    forks,
    updatedAt,
    readmeText,
    courseCode,
    courseName,
    hasReadme,
    hasFileList,
    filePaths,
    hasSourceCode,
    hasTests,
    hasDocs,
    hasAssignments,
    hasSolutions,
    hasSlides,
    hasNotes,
    hasExam,
    hasAnswerOrSolution,
    hasPackageFile,
    hasBuildFile,
    hasEnvExample,
    hasDockerConfig,
    hasLicense,
    organizedFolders,
    evidence: [],
    missingSignals: [],
  })
  const missingSignals = [
    !description ? 'description' : null,
    topics.length === 0 ? 'topics/tags' : null,
    !primaryLanguage ? 'primaryLanguage' : null,
    !hasReadme ? 'README' : null,
    !hasFileList ? 'file/folder list' : null,
    forks === null ? 'forks' : null,
    !updatedAt ? 'last updated' : null,
  ].filter(Boolean) as string[]

  return {
    name,
    description,
    topics,
    primaryLanguage,
    techStacks,
    stars,
    forks,
    updatedAt,
    readmeText,
    courseCode,
    courseName,
    hasReadme,
    hasFileList,
    filePaths,
    hasSourceCode,
    hasTests,
    hasDocs,
    hasAssignments,
    hasSolutions,
    hasSlides,
    hasNotes,
    hasExam,
    hasAnswerOrSolution,
    hasPackageFile,
    hasBuildFile,
    hasEnvExample,
    hasDockerConfig,
    hasLicense,
    organizedFolders,
    evidence,
    missingSignals,
  }
}

export function formatVietnameseRelativeDate(value: string | null | undefined, now = new Date()): string | null {
  if (!value) return null
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return null

  const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime()
  const startOfDate = new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime()
  const dayDiff = Math.floor((startOfToday - startOfDate) / 86_400_000)
  if (dayDiff <= 0) return 'Hôm nay'
  if (dayDiff === 1) return 'Hôm qua'
  if (dayDiff < 7) return `${dayDiff} ngày trước`

  const monthDiff = (now.getFullYear() - date.getFullYear()) * 12 + now.getMonth() - date.getMonth()
  if (monthDiff < 1) {
    const weeks = Math.max(1, Math.floor(dayDiff / 7))
    return `${weeks} tuần trước`
  }
  if (monthDiff < 12) return `${monthDiff} tháng trước`

  const yearDiff = Math.max(1, Math.floor(monthDiff / 12))
  return `${yearDiff} năm trước`
}

function detectCourseGroup(signals: RepoSignals): CourseGroup {
  const text = normalizeSearchText([
    signals.courseCode,
    signals.courseName,
    signals.name,
    signals.description,
    ...signals.topics,
  ])
  if (contains(text, /\b(ss004|ky nang|soft skill|english|anh van|xac suat|thong ke|phuong phap|seminar|chuyen de|cv|presentation)\b/)) return 'general_skills'
  if (contains(text, /\b(ooad|hci|srs|sad|uml|use case|erd|figma|prototype|software engineering|cong nghe phan mem|kien truc phan mem|thiet ke|dac ta)\b/)) return 'design_process'
  if (contains(text, /\b(web|mobile|android|flutter|react|spring|\.net|dotnet|game|cloud|do an|khoa luan|fullstack|backend|frontend|deployment)\b/)) return 'software_project'
  if (contains(text, /\b(dsa|ctdl|giai thuat|algorithms?|data structures?|lap trinh|co so du lieu|database|csdl|he dieu hanh|operating system|cau truc roi rac|to chuc may tinh)\b/)) return 'foundation_algorithms'
  return 'unknown'
}

function buildRepoIdentity(repoType: RepoType, courseGroup: CourseGroup, signals: RepoSignals): string {
  const text = normalizeSearchText([signals.name, signals.description, signals.readmeText, ...signals.filePaths])
  if (repoType === 'unknown') return 'Chưa đủ dữ liệu để xác định'
  if (repoType === 'exam_review') return 'Tài liệu ôn thi'
  if (contains(text, /\b(khoa luan|thesis|graduation)\b/)) return 'Khóa luận tốt nghiệp'
  if (contains(text, /\b(final project|cuoi ky|do an cuoi ky)\b/)) return 'Đồ án cuối kỳ'
  if (contains(text, /\b(report|bao cao|rubric|assignment|bai nop)\b/) && (courseGroup === 'general_skills' || courseGroup === 'design_process')) return 'Repo bài nộp/báo cáo'
  if (repoType === 'programming_exercise') return contains(text, /\b(lab|labs)\b/) ? 'Bài tập Lab' : 'Bài tập lập trình'
  if (repoType === 'project_practice') return contains(text, /\b(do an|project)\b/) ? 'Đồ án môn' : 'Project thực hành'
  if (repoType === 'study_material') return courseGroup === 'general_skills' ? 'Kho tài liệu môn học' : 'Tài liệu học'
  if (repoType === 'mixed_resource') return 'Kho tài nguyên môn học'
  return 'Repo tham khảo'
}

function detectTechTools(repoType: RepoType, courseGroup: CourseGroup, signals: RepoSignals): string[] {
  const text = normalizeSearchText([signals.readmeText, signals.description, ...signals.filePaths, ...signals.techStacks])
  const tools = collectMatches(text, [
    ['React', /\breact|vite|nextjs|next\.js\b/],
    ['Spring Boot', /\bspring boot|spring\b/],
    ['.NET', /\b\.net|dotnet|aspnet|asp\.net\b/],
    ['Flutter', /\bflutter|pubspec\.yaml\b/],
    ['Android', /\bandroid|kotlin|gradle\b/],
    ['MySQL', /\bmysql\b/],
    ['PostgreSQL', /\bpostgres|postgresql\b/],
    ['MongoDB', /\bmongodb|mongo\b/],
    ['Docker', /\bdocker|dockerfile|docker-compose\b/],
    ['Figma', /\bfigma\b/],
    ['Draw.io', /\bdraw\.io|drawio\b/],
    ['PlantUML', /\bplantuml|\.puml\b/],
    ['StarUML', /\bstaruml\b/],
  ])
  for (const stack of signals.techStacks) tools.push(stack)
  if (shouldTreatLanguageAsTool(repoType, courseGroup, signals)) {
    const language = cleanText(signals.primaryLanguage)
    if (language) tools.unshift(language)
  }
  return unique(tools).slice(0, 6)
}

function shouldTreatLanguageAsTool(repoType: RepoType, courseGroup: CourseGroup, signals: RepoSignals): boolean {
  if (!signals.primaryLanguage) return false
  if ((courseGroup === 'general_skills' || courseGroup === 'design_process') && !signals.filePaths.some((path) => sourceFilePattern.test(path))) return false
  if (repoType === 'project_practice' || repoType === 'programming_exercise') return true
  if (courseGroup === 'foundation_algorithms' && signals.hasSourceCode) return true
  return signals.filePaths.some((path) => sourceFilePattern.test(path))
}

function detectCoreTopics(courseGroup: CourseGroup, repoType: RepoType, signals: RepoSignals): string[] {
  const text = normalizeSearchText([
    signals.name,
    signals.description,
    signals.readmeText,
    signals.courseCode,
    signals.courseName,
    ...signals.topics,
    ...signals.filePaths,
  ])
  const common = collectMatches(text, [
    ['Linked List', /\blinked[-_ ]?list|danh sach lien ket\b/],
    ['Stack', /\bstack|ngan xep\b/],
    ['Queue', /\bqueue|hang doi\b/],
    ['Tree', /\btree|binary tree|bst|cay\b/],
    ['Graph', /\bgraph|dijkstra|dfs|bfs|do thi\b/],
    ['Sorting', /\bsort|sorting|quick sort|merge sort|sap xep\b/],
    ['Auth', /\bauth|login|jwt|oauth\b/],
    ['CRUD', /\bcrud|create read update delete\b/],
    ['REST API', /\brest|api|controller|endpoint\b/],
    ['Realtime', /\brealtime|socket|websocket\b/],
    ['Database', /\bdatabase|sql|mysql|postgres|mongodb|csdl\b/],
    ['Deployment', /\bdeploy|docker|ci\/cd|cicd|cloud\b/],
    ['SRS', /\bsrs|software requirement|dac ta yeu cau\b/],
    ['Use Case', /\buse[-_ ]?case|usecase\b/],
    ['UML', /\buml|class diagram|sequence diagram|activity diagram\b/],
    ['ERD', /\berd|entity relationship\b/],
    ['Prototype', /\bprototype|wireframe|figma\b/],
    ['Report', /\breport|bao cao|tieu luan\b/],
    ['Presentation', /\bpresentation|slide|pptx|thuyet trinh\b/],
    ['Rubric', /\brubric|grading|criteria\b/],
    ['CV', /\bcv|resume\b/],
    ['Final exam', /\bfinal|cuoi ky\b/],
    ['Answer key', /\banswer|solution|dap an|loi giai\b/],
  ])
  if (common.length > 0) return unique(common).slice(0, 7)
  if (repoType === 'study_material' && courseGroup !== 'unknown') return [courseGroupLabels[courseGroup]]
  return signals.topics.slice(0, 5)
}

export function buildReadyToUse(repoType: RepoType, courseGroup: CourseGroup, signals: RepoSignals): { level: ReadyToUseLevel; label: string; stars: number; note: string } {
  if (repoType === 'unknown' && signals.evidence.length <= 2) {
    return { level: 'insufficient_data', label: readyToUseLabels.insufficient_data, stars: 1, note: 'Thiếu README, cây thư mục và mô tả nên cần mở GitHub để xem trước.' }
  }
  let score: number
  if (repoType === 'programming_exercise') {
    score = scoreSignals([[signals.hasReadme, 1], [signals.hasAssignments, 1], [signals.hasSourceCode, 1], [signals.hasTests, 1], [signals.hasSolutions || signals.organizedFolders, 1], [courseGroup === 'foundation_algorithms' && signals.hasFileList, 1]])
  } else if (repoType === 'project_practice') {
    score = scoreSignals([[signals.hasReadme, 1], [signals.hasPackageFile, 1], [signals.hasEnvExample || signals.hasDockerConfig, 1], [signals.hasBuildFile, 1], [signals.hasDocs || contains(normalizeSearchText([signals.readmeText]), /\b(run|setup|install|database|migration|npm|mvn|gradle|docker)\b/), 1], [signals.hasSourceCode, 1]])
  } else if (repoType === 'study_material') {
    score = scoreSignals([[signals.hasReadme, 1], [signals.hasSlides, 1], [signals.hasNotes || signals.hasDocs, 1], [signals.organizedFolders, 1], [signals.hasFileList, 1]])
  } else if (repoType === 'exam_review') {
    score = scoreSignals([[signals.hasExam, 1], [signals.hasAnswerOrSolution, 1], [signals.hasFileList, 1], [contains(normalizeSearchText([signals.name, signals.description, signals.readmeText, ...signals.filePaths]), /\b(20\d{2}|midterm|final|cuoi ky|giua ky)\b/), 1], [signals.hasReadme, 1]])
  } else if (courseGroup === 'general_skills' || courseGroup === 'design_process') {
    const text = normalizeSearchText([signals.name, signals.description, signals.readmeText, ...signals.filePaths])
    score = scoreSignals([[signals.hasReadme, 1], [contains(text, /\brubric|guideline|assignment\b/), 1], [contains(text, /\breport|bao cao|docx|pdf\b/), 1], [contains(text, /\bslide|presentation|pptx\b/), 1], [signals.organizedFolders || signals.hasFileList, 1]])
  } else {
    score = scoreSignals([[signals.hasReadme, 1], [signals.hasFileList, 1], [signals.description !== null, 1], [signals.topics.length > 0, 1], [signals.primaryLanguage !== null, 1]])
  }
  const stars = Math.max(1, Math.min(5, score))
  const level: ReadyToUseLevel = stars >= 5 ? 'very_ready' : stars >= 4 ? 'ready' : stars >= 3 ? 'needs_check' : stars >= 2 ? 'quick_reference' : 'insufficient_data'
  return { level, label: readyToUseLabels[level], stars, note: buildReadyNote(repoType, level, signals) }
}

function buildReadyNote(repoType: RepoType, level: ReadyToUseLevel, signals: RepoSignals): string {
  if (repoType === 'programming_exercise') {
    if (level === 'very_ready' || level === 'ready') return signals.hasTests ? 'Có README/source theo bài và test/input-output để đối chiếu.' : 'Có README/source rõ, vẫn nên kiểm tra đề bài và test case.'
    if (level === 'needs_check') return 'Có source code nhưng chưa xác nhận đủ đề bài và test case.'
    return 'Mới thấy code rời hoặc metadata ít, cần mở repo để biết bài nào.'
  }
  if (repoType === 'project_practice') {
    if (level === 'very_ready' || level === 'ready') return 'Có README/setup và file dependency/config để clone thử nhanh.'
    if (level === 'needs_check') return 'Có source/stack nhưng setup, env hoặc database script chưa rõ.'
    return 'Chưa rõ cách chạy local, chỉ nên xem cấu trúc trước.'
  }
  if (repoType === 'study_material') {
    if (level === 'very_ready' || level === 'ready') return 'Có slide/note/docs và cấu trúc đủ để đọc theo chủ đề.'
    if (level === 'needs_check') return 'Có tài liệu nhưng cần xem thứ tự chương/buổi và phạm vi.'
    return 'Tài liệu còn rời rạc hoặc thiếu cấu trúc.'
  }
  if (repoType === 'exam_review') {
    if (level === 'very_ready' || level === 'ready') return 'Có đề, đáp án/lời giải và dấu hiệu năm/kỳ để ôn tập.'
    if (level === 'needs_check') return 'Có đề ôn tập nhưng cần xác nhận đáp án và năm/kỳ.'
    return 'File đề thi còn rời rạc, chưa rõ kỳ/năm.'
  }
  if (level === 'very_ready' || level === 'ready') return 'Đủ tín hiệu để bắt đầu xem nhanh theo đúng phần cần dùng.'
  if (!signals.hasFileList) return 'Thiếu cây thư mục nên chưa nhìn được cấu trúc thật của repo.'
  return 'Có thể tham khảo, nhưng cần lọc đúng folder/nội dung.'
}

export function buildUsefulnessScore(repoType: RepoType, courseGroup: CourseGroup, readyStars: number, signals: RepoSignals): number {
  const hasCourseContext = courseGroup !== 'unknown'
  if (repoType === 'unknown' && !signals.description && !signals.hasReadme && !signals.hasFileList && !hasCourseContext) return 10
  let value = readyStars * 13
  value += scoreSignals([
    [signals.description !== null, 10],
    [signals.hasFileList, 12],
    [signals.hasReadme, 12],
    [hasCourseContext, 10],
    [signals.stars !== null && signals.stars > 0, 4],
  ])
  if (repoType === 'programming_exercise') value += scoreSignals([[signals.hasSourceCode, 8], [signals.hasTests, 10], [signals.hasSolutions, 8], [signals.hasAssignments, 10], [courseGroup === 'foundation_algorithms', 8]])
  if (repoType === 'project_practice') value += scoreSignals([[signals.hasSourceCode, 8], [signals.hasPackageFile, 10], [signals.hasEnvExample || signals.hasDockerConfig, 10], [signals.techStacks.length >= 2, 8]])
  if (repoType === 'study_material') value += scoreSignals([[signals.hasSlides, 10], [signals.hasNotes || signals.hasDocs, 10], [signals.organizedFolders, 6]])
  if (repoType === 'exam_review') value += scoreSignals([[signals.hasExam, 12], [signals.hasAnswerOrSolution, 12]])
  if (repoType === 'exam_review' && signals.hasExam && signals.hasAnswerOrSolution) value += 8
  if ((courseGroup === 'general_skills' || courseGroup === 'design_process') && repoType !== 'project_practice') value += scoreSignals([[signals.hasDocs, 8], [signals.hasSlides, 8]])
  if (repoType === 'programming_exercise') value -= scoreSignals([[!signals.hasReadme, 8], [!signals.hasTests, 10]])
  if (repoType === 'project_practice') value -= scoreSignals([[!signals.hasReadme, 8], [!signals.hasPackageFile, 8], [!signals.hasEnvExample && !signals.hasDockerConfig, 6]])
  return clampScore(value)
}

function ratingFromScore(value: number, repoType: RepoType): UsefulnessRating {
  if (repoType === 'unknown' && value < 30) return 'insufficient_data'
  if (repoType === 'exam_review' && value >= 56) return 'highly_recommended'
  if (value >= 68) return 'highly_recommended'
  if (value >= 64) return 'recommended'
  if (value >= 34) return 'selective'
  if (value >= 20) return 'quick_reference'
  return 'low_priority'
}

function buildConfidenceScore(signals: RepoSignals, repoType: RepoType): number {
  const value = scoreSignals([
    [signals.description !== null, 14],
    [signals.primaryLanguage !== null, 8],
    [signals.techStacks.length > 0, 8],
    [signals.topics.length > 0, 10],
    [signals.hasReadme, 14],
    [signals.hasFileList, 24],
    [signals.forks !== null, 5],
    [signals.updatedAt !== null, 5],
    [repoType !== 'unknown', 12],
  ])
  return clampScore(value)
}

function buildGroupHighlights(courseGroup: CourseGroup, repoType: RepoType, signals: RepoSignals, coreTopics: string[]): string[] {
  if (courseGroup === 'foundation_algorithms') return compact([
    coreTopics.length > 0 ? `Trọng tâm: ${coreTopics.slice(0, 5).join(', ')}.` : null,
    signals.hasTests ? 'Có test/input-output để đối chiếu.' : 'Điểm cần kiểm tra: đề bài và test/input-output.',
  ], 2)
  if (courseGroup === 'software_project') return compact([
    detectTechTools(repoType, courseGroup, signals).length > 0 ? `Stack/tools: ${detectTechTools(repoType, courseGroup, signals).slice(0, 5).join(', ')}.` : null,
    signals.hasEnvExample || signals.hasDockerConfig ? 'Có tín hiệu env/Docker cho setup.' : 'Cần kiểm tra env, DB script và lệnh run.',
  ], 2)
  if (courseGroup === 'design_process') return compact([
    coreTopics.length > 0 ? `Artifact chính: ${coreTopics.slice(0, 5).join(', ')}.` : null,
    signals.hasSourceCode ? 'Có thể có demo/prototype kèm tài liệu.' : 'Có vẻ nghiêng về tài liệu/diagram hơn source code.',
  ], 2)
  if (courseGroup === 'general_skills') return compact([
    coreTopics.length > 0 ? `Nội dung chính: ${coreTopics.slice(0, 5).join(', ')}.` : null,
    'Đánh giá theo rubric/guideline/report/slide, không đưa primary language thành điểm mạnh nếu không có code context.',
  ], 2)
  return ['Chưa rõ nhóm môn, cần mở GitHub để xác nhận ngữ cảnh.']
}

export function buildRecommendation(repoType: RepoType, rating: UsefulnessRating, readyLevel: ReadyToUseLevel, signals: RepoSignals): string {
  if (rating === 'insufficient_data') return 'Chưa nên clone; mở GitHub kiểm tra README và cây thư mục trước.'
  if (rating === 'highly_recommended' || rating === 'recommended') {
    if (repoType === 'project_practice') return readyLevel === 'very_ready' || readyLevel === 'ready' ? 'Clone thử để học setup và cách tổ chức project.' : 'Xem source và cấu trúc project trước khi clone chạy.'
    if (repoType === 'programming_exercise') return 'Tham khảo code bài lab và đối chiếu cách giải sau khi tự làm.'
    if (repoType === 'exam_review') return 'Ôn đề thi; cần kiểm tra đáp án và năm/kỳ.'
    return 'Dùng như nguồn học phụ, ưu tiên đúng phần liên quan.'
  }
  if (!signals.hasFileList) return 'Chỉ nên tham khảo nhanh vì DevOrbit chưa đọc được cây thư mục của repo này.'
  return 'Xem có chọn lọc; cần tự xác minh README/phạm vi.'
}

function buildQuickBullets(repoIdentity: string, techTools: string[], coreTopics: string[], recommendation: string, signals: RepoSignals): string[] {
  return compact([
    `Repo này là ${repoIdentity.toLowerCase()}.`,
    techTools.length > 0 ? `Công nghệ / công cụ: ${techTools.slice(0, 5).join(', ')}.` : null,
    coreTopics.length > 0 ? `Chủ đề chính: ${coreTopics.slice(0, 6).join(', ')}.` : 'Chưa đủ dữ liệu để tách chủ đề chính.',
    recommendation,
  ], 3, [signals.hasFileList ? 'Có cây thư mục để nhìn nhanh cấu trúc repo.' : 'Chưa đủ dữ liệu để phân tích repo.'])
}

function classifyRepoType(signals: RepoSignals): { repoType: RepoType; reason: string } {
  const text = normalizeSearchText([
    signals.courseCode,
    signals.courseName,
    signals.name,
    signals.description,
    signals.readmeText,
    signals.primaryLanguage,
    ...signals.techStacks,
    ...signals.topics,
    ...signals.filePaths,
  ])
  const hasFoundationAlgorithmContext = isFoundationAlgorithmContext(text)
  const scores: Record<RepoType, number> = {
    programming_exercise: score([
      signals.hasAssignments,
      signals.hasSourceCode && contains(text, /\b(code|programming|algorithms?|dsa|data structures?|ctdl|giai thuat|oop|java|python|c\+\+|cpp|c#)\b/),
      signals.hasSourceCode && hasFoundationAlgorithmContext,
      signals.hasTests,
      signals.hasSolutions,
    ]),
    project_practice: score([
      contains(text, /\b(project|web|frontend|backend|fullstack|api|database|mobile|app|demo|spring|react|android|server)\b/),
      signals.hasPackageFile || signals.hasBuildFile,
      signals.hasDockerConfig || signals.hasEnvExample,
      signals.hasSourceCode && signals.techStacks.length > 0,
    ]),
    study_material: score([
      signals.hasSlides,
      signals.hasNotes,
      signals.hasDocs,
      contains(text, /\b(lecture|slides|note|document|theory|material|course|chuong|chapter)\b/),
    ]),
    exam_review: score([
      signals.hasExam,
      signals.hasAnswerOrSolution,
      contains(text, /\b(midterm|final|quiz|exam|de thi|đề thi|answer|solution)\b/),
    ]),
    mixed_resource: 0,
    unknown: 0,
  }
  const ranked = (Object.entries(scores) as Array<[RepoType, number]>)
    .filter(([type]) => type !== 'mixed_resource' && type !== 'unknown')
    .sort((a, b) => b[1] - a[1])
  const strongTypes = ranked.filter(([, value]) => value >= 2)

  if (
    signals.hasAssignments
    && signals.hasSourceCode
    && !contains(text, /\b(do an|project|fullstack|backend|frontend|api|server)\b/)
  ) {
    return {
      repoType: 'programming_exercise',
      reason: 'Repo has lab/assignment wording and source evidence.',
    }
  }

  if (strongTypes.length >= 2 && strongTypes[0][1] <= 3 && strongTypes[0][1] - strongTypes[1][1] <= 1) {
    return {
      repoType: 'mixed_resource',
      reason: `Repo có tín hiệu chồng lấn giữa ${repoTypeLabels[strongTypes[0][0]].toLowerCase()} và ${repoTypeLabels[strongTypes[1][0]].toLowerCase()}.`,
    }
  }
  if (ranked[0][1] >= 2) {
    return {
      repoType: ranked[0][0],
      reason: `Tín hiệu mạnh nhất đến từ ${repoTypeLabels[ranked[0][0]].toLowerCase()}: ${typeEvidence(ranked[0][0], signals)}.`,
    }
  }
  if (hasFoundationAlgorithmContext && signals.hasSourceCode) {
    return {
      repoType: 'programming_exercise',
      reason: 'Ngữ cảnh môn nền tảng/thuật toán có ngôn ngữ/source signal, nên ưu tiên xem như bài tập/lab thay vì project app.',
    }
  }
  if (signals.hasSourceCode && (signals.description || signals.techStacks.length > 0)) {
    return {
      repoType: 'project_practice',
      reason: 'Có ngôn ngữ/stack và mô tả kỹ thuật, nhưng chưa đủ dữ liệu để biết đây là project hoàn chỉnh hay code mẫu.',
    }
  }
  return {
    repoType: 'unknown',
    reason: 'Metadata hiện có chưa đủ để xác định rõ repo là bài tập, project, tài liệu hay đề ôn tập.',
  }
}

function isFoundationAlgorithmContext(text: string): boolean {
  return contains(text, /\b(it003|dsa|ctdl|data structures?|cau truc du lieu|giai thuat|algorithms?|lap trinh|co so lap trinh|programming fundamentals|stack|queue|tree|graph|sorting)\b/)
}

export function getConfidence(signals: RepoSignals, repoType: RepoType): ConfidenceLabel {
  const confidenceScore = buildConfidenceScore(signals, repoType)
  if (repoType === 'unknown' || confidenceScore < 42) return 'low'
  if (confidenceScore >= 70) return 'high'
  return 'medium'
}

function buildBestFor(repoType: RepoType, rating: UsefulnessRating, signals: RepoSignals): string {
  if (rating === 'insufficient_data') return 'Mở repo trực tiếp để kiểm tra metadata và cấu trúc trước'
  if (repoType === 'programming_exercise') return signals.hasTests ? 'Luyện bài, đối chiếu cách giải và kiểm tra input/output' : 'Tham khảo cách triển khai bài thực hành hoặc cú pháp'
  if (repoType === 'project_practice') return signals.hasReadme && signals.hasPackageFile ? 'Clone thử, xem cấu trúc project và cách setup' : 'Xem cấu trúc source code và cách triển khai chức năng'
  if (repoType === 'study_material') return signals.hasSlides ? 'Ôn lý thuyết theo slide/note và hệ thống lại kiến thức' : 'Đọc nhanh tài liệu môn học theo chủ đề'
  if (repoType === 'exam_review') return signals.hasAnswerOrSolution ? 'Luyện đề và đối chiếu đáp án/lời giải' : 'Làm quen dạng đề trước khi kiểm tra'
  if (repoType === 'mixed_resource') return 'Lọc từng phần: code, tài liệu, đề ôn tập hoặc ví dụ theo nhu cầu'
  return 'Tham khảo nhanh sau khi kiểm tra trực tiếp trên GitHub'
}

function buildMainReason(repoType: RepoType, rating: UsefulnessRating, signals: RepoSignals): string {
  if (rating === 'insufficient_data') return 'Thiếu description, topics, README và danh sách file, thư mục nên chưa đủ cơ sở kết luận sâu.'
  if (repoType === 'programming_exercise') {
    if (signals.hasTests) return 'Có tín hiệu bài/lab/source code kèm test hoặc input/output nên hữu ích cho luyện thực hành.'
    if (signals.hasAssignments) return 'Có dấu hiệu là repo bài tập/lab phục vụ thực hành, nhưng cần kiểm tra đề bài và test case.'
    return 'Có code hoặc ngôn ngữ lập trình rõ, nhưng chưa thấy đủ tín hiệu bài tập/test.'
  }
  if (repoType === 'project_practice') {
    if (signals.hasReadme && signals.hasPackageFile) return 'Có source/stack và file setup hoặc dependency, phù hợp để kiểm tra cách chạy project.'
    return 'Có source code hoặc stack project, nhưng tín hiệu setup/local config chưa đủ rõ.'
  }
  if (repoType === 'study_material') return signals.organizedFolders ? 'Có tài liệu học và cấu trúc thư mục tương đối rõ để đọc theo chủ đề.' : 'Có tín hiệu slide/note/tài liệu, nhưng cần kiểm tra thứ tự nội dung.'
  if (repoType === 'exam_review') return signals.hasAnswerOrSolution ? 'Có tín hiệu đề thi/quiz kèm đáp án hoặc lời giải.' : 'Có tín hiệu đề ôn tập, nhưng chưa chắc có đáp án/lời giải.'
  if (repoType === 'mixed_resource') return 'Repo có nhiều nhóm tài nguyên, nên giá trị phụ thuộc vào phần bạn cần mở.'
  return 'Metadata chưa cho thấy mục đích học tập rõ ràng.'
}

function buildQuickSummary(repoType: RepoType, rating: UsefulnessRating, signals: RepoSignals, confidence: ConfidenceLabel): string {
  if (rating === 'insufficient_data') {
    return 'Chưa đủ dữ liệu để kết luận sâu về repo này. Trước khi dùng, nên mở trực tiếp repo để kiểm tra README, cấu trúc thư mục, source code chính và file hướng dẫn nếu có.'
  }
  const caution = confidence === 'low'
    ? ' Đánh giá này dựa trên dữ liệu giới hạn, nên kiểm tra trực tiếp repo trước khi dùng.'
    : ''
  const coursePrefix = signals.courseCode
    ? `Repo môn ${signals.courseCode}${signals.courseName ? ` - ${signals.courseName}` : ''}: `
    : ''

  if (repoType === 'programming_exercise') {
    return `${coursePrefix}${signals.name} phù hợp để tham khảo bài giải lập trình hơn là học lý thuyết từ đầu. Nên ưu tiên xem đề bài, source code, test case hoặc input/output trước khi dùng làm mẫu.${caution}`
  }
  if (repoType === 'project_practice') {
    return `${coursePrefix}${signals.name} phù hợp để xem cách tổ chức một project thực hành. Giá trị chính nằm ở cấu trúc source code, stack và file cấu hình; cần kiểm tra README/setup trước khi clone.${caution}`
  }
  if (repoType === 'study_material') {
    return `${coursePrefix}${signals.name} phù hợp để ôn hoặc hệ thống kiến thức môn học. Nên xem nội dung có chia theo chương/buổi/chủ đề không trước khi dùng làm tài liệu chính.${caution}`
  }
  if (repoType === 'exam_review') {
    return `${coursePrefix}${signals.name} phù hợp để luyện đề và kiểm tra kiến thức trước kỳ thi. Cần xác nhận đề có đáp án, lời giải, năm/kỳ hoặc phạm vi rõ không.${caution}`
  }
  if (repoType === 'mixed_resource') {
    return `${coursePrefix}${signals.name} có vẻ là repo tổng hợp nhiều loại tài nguyên. Nên mở đúng phần mình cần, rồi kiểm tra từng folder thay vì đọc tuần tự từ đầu.${caution}`
  }
  return `Repo này chỉ nên tham khảo nhanh cho đến khi bạn xác nhận được mục tiêu, cấu trúc và nội dung chính trên GitHub.${caution}`
}

export function buildStrengths(repoType: RepoType, signals: RepoSignals, rating: UsefulnessRating): string[] {
  const items: string[] = []
  if (signals.description) items.push(`Mô tả cho biết trọng tâm: ${truncate(signals.description, 110)}.`)
  if (signals.primaryLanguage || signals.techStacks.length > 0) items.push(`Có tín hiệu kỹ thuật: ${[signals.primaryLanguage, ...signals.techStacks].filter(Boolean).slice(0, 4).join(', ')}.`)
  if (repoType === 'programming_exercise' && signals.hasTests) items.push('Có tín hiệu test/input/output, hữu ích khi đối chiếu lời giải.')
  if (repoType === 'project_practice' && signals.hasPackageFile) items.push('Có file dependency/build, dễ kiểm tra cách chạy hơn repo chỉ có code rời.')
  if (repoType === 'study_material' && (signals.hasSlides || signals.hasNotes)) items.push('Có tín hiệu slide/note/tài liệu học đúng mục đích ôn tập.')
  if (repoType === 'exam_review' && signals.hasAnswerOrSolution) items.push('Có tín hiệu đáp án hoặc lời giải để tự kiểm tra.')
  if (signals.organizedFolders) items.push('Danh sách file/folder cho thấy repo có cấu trúc tương đối rõ.')
  if (rating === 'highly_recommended') items.push('Nhiều tín hiệu khớp với mục đích sử dụng, đáng mở trước các repo ít metadata hơn.')
  return compact(items, 4, ['Có một vài metadata cơ bản để định hướng trước khi mở GitHub.'])
}

function buildWeaknesses(repoType: RepoType, signals: RepoSignals, rating: UsefulnessRating): string[] {
  const items: string[] = []
  if (!signals.hasFileList) items.push('DevOrbit chưa có danh sách file, thư mục, nên chưa kiểm chứng được cấu trúc repo.')
  if (!signals.description) items.push('Thiếu mô tả nên mục tiêu repo chưa rõ từ màn hình này.')
  if (!signals.hasReadme && repoType === 'project_practice') items.push('Chưa thấy README, đây là điểm yếu lớn nếu cần setup project.')
  else if (!signals.hasReadme && repoType !== 'unknown') items.push('Chưa thấy README; vẫn có thể hữu ích nhưng cần mở repo để hiểu phạm vi.')
  if (repoType === 'programming_exercise' && !signals.hasTests) items.push('Chưa thấy test/input/output, nên khó biết lời giải có đúng đủ không.')
  if (repoType === 'exam_review' && !signals.hasAnswerOrSolution) items.push('Chưa thấy đáp án/lời giải, nên chỉ nên dùng để luyện đề trước.')
  if (repoType === 'study_material' && !signals.organizedFolders && !signals.hasFileList) items.push('Chưa rõ tài liệu có chia theo chương/buổi hay không.')
  if (rating === 'insufficient_data') items.push('Thiếu nhiều tín hiệu quan trọng nên không nên kết luận repo đáng dùng ngay.')
  return compact(items, 4, ['Chưa thấy điểm yếu rõ ngoài việc vẫn cần kiểm tra trực tiếp trên GitHub.'])
}

function buildNextActions(repoType: RepoType, signals: RepoSignals): string[] {
  if (repoType === 'programming_exercise') {
    return compact([
      'Mở đề bài hoặc folder lab/assignment trước.',
      signals.hasTests ? 'Chạy hoặc đọc test/input/output để hiểu yêu cầu.' : 'Tìm test case, input/output hoặc sample nếu có.',
      'Đọc source theo từng bài, không copy nguyên lời giải khi chưa hiểu.',
    ], 3)
  }
  if (repoType === 'project_practice') {
    return compact([
      signals.hasReadme ? 'Đọc README để lấy lệnh setup/run.' : 'Kiểm tra README hoặc hướng dẫn chạy local.',
      signals.hasEnvExample ? 'Mở .env.example để biết biến môi trường cần cấu hình.' : 'Tìm file env/config/database trước khi clone chạy.',
      signals.hasPackageFile ? 'Kiểm tra file dependency/build để biết project có chạy được không.' : 'Tìm package/build file hoặc entrypoint chính.',
    ], 3)
  }
  if (repoType === 'study_material') {
    return ['Xem mục lục hoặc folder theo chương/buổi.', 'Ưu tiên slide/note có thứ tự học rõ.', 'Đối chiếu nội dung với đề cương môn trước khi dùng làm tài liệu chính.']
  }
  if (repoType === 'exam_review') {
    return ['Xác định năm/kỳ/giữa kỳ/cuối kỳ của đề.', 'Kiểm tra có đáp án hoặc lời giải không.', 'Làm thử đề trước khi xem lời giải để tự đánh giá.']
  }
  return ['Mở GitHub để kiểm tra README và cấu trúc repo.', 'Xác định repo chứa code, tài liệu, hay đề ôn tập.', 'Chỉ dùng làm tham khảo nhanh nếu vẫn thiếu dữ liệu.']
}

function buildLearningStrategy(repoType: RepoType, courseGroup: CourseGroup, signals: RepoSignals): string[] {
  const text = normalizeSearchText([signals.name, signals.description, signals.readmeText, ...signals.filePaths, ...signals.topics])
  const isSecurityTool = contains(text, /\b(security|penetration|exploit|hack|crack|keygen|scrape|crawl|botnet|malware|ransomware|keylogger|reverse.?shell|backdoor|trojan|virus|payload|exploit|pentest)\b/)

  if (isSecurityTool) {
    return [
      'Đọc README và source trước khi chạy bất kỳ lệnh nào.',
      'Không chạy bằng quyền cao nếu chưa hiểu rõ tác động.',
      'Ưu tiên chạy trong môi trường test/sandbox.',
      'Dùng để nghiên cứu kỹ thuật, không dùng sai mục đích.',
      'Kiểm tra kỹ các phần liên quan network, subprocess, file access.',
    ]
  }

  if (repoType === 'programming_exercise') {
    const items = [
      'Tự làm bài trước rồi mới đối chiếu lời giải để hiểu cách triển khai.',
      'Đọc theo thứ tự lab/chapter nếu repo có chia thư mục rõ ràng.',
      'Tập trung vào ý tưởng và cấu trúc code, không copy nguyên.',
    ]
    if (signals.hasTests) items.push('Chạy thử input/output để kiểm tra lời giải có khớp yêu cầu không.')
    if (signals.organizedFolders) items.push('Ghi chú pattern hay gặp: cách chia file, xử lý dữ liệu, hàm chính.')
    return items
  }

  if (repoType === 'project_practice') {
    const items = [
      'Bắt đầu từ README và cấu trúc thư mục để hiểu cách tổ chức project.',
      'Xem flow chính: frontend → backend → database.',
      'Học cách tổ chức module, không chỉ copy giao diện hay route.',
    ]
    if (signals.hasReadme && (signals.hasPackageFile || signals.hasDockerConfig)) items.push('Clone và chạy local để hiểu luồng chức năng thực tế.')
    items.push('Ghi chú phần có thể áp dụng: authentication, CRUD, API design, database.')
    return items
  }

  if (repoType === 'study_material') {
    return [
      'Đọc theo chương/buổi, không đọc lướt file rời rạc.',
      'Tóm tắt lại ý chính sau mỗi phần để ghi nhớ.',
      'Đối chiếu với đề cương môn học hiện tại.',
      'Kết hợp với bài tập hoặc đề thi nếu repo có kèm.',
      'Dùng repo để ôn nhanh, không xem là nguồn duy nhất.',
    ]
  }

  if (repoType === 'exam_review') {
    const items = [
      'Làm đề trước khi xem đáp án để tự đánh giá năng lực.',
      'Ghi lại dạng câu hỏi thường lặp lại qua các năm.',
    ]
    if (signals.hasAnswerOrSolution) items.push('Sau khi xem đáp án, tự giải lại lần nữa để nhớ sâu hơn.')
    items.push('Kiểm tra đáp án với tài liệu chính thức nếu có thể.')
    return items
  }

  if (courseGroup === 'general_skills' || courseGroup === 'design_process') {
    const hasReportOrSlide = signals.hasSlides || contains(normalizeSearchText([...signals.filePaths]), /\b(report|docx|pdf)\b/)
    const items = [
      'Xem rubric, guideline hoặc yêu cầu bài nộp trước khi tham khảo.',
      'Dùng repo để học cách trình bày và bố cục, không copy nội dung.',
      'So sánh với yêu cầu giảng viên hiện tại — rubric có thể thay đổi.',
    ]
    if (hasReportOrSlide) items.push('Tự viết lại nội dung theo trường hợp của mình, không copy y nguyên.')
    return items
  }

  if (repoType === 'mixed_resource') {
    return [
      'Xác định phần repo phù hợp với nhu cầu trước khi đọc.',
      'Lọc theo folder/file thay vì đọc tuần tự từ đầu.',
      'Dùng repo như kho tham khảo, không phải giáo trình chính.',
    ]
  }

  return [
    'Xem lướt trước: README → cây thư mục → file chính để hiểu repo chứa gì.',
    'Xác định repo thuộc loại code, tài liệu, đề thi hay bài nộp.',
    'Không clone/chạy ngay nếu mục đích repo chưa rõ ràng.',
    'Chỉ dùng để tham khảo nhanh cho đến khi có đủ dữ liệu.',
  ]
}

function buildCautionNotes(repoType: RepoType, courseGroup: CourseGroup, signals: RepoSignals): string[] {
  const text = normalizeSearchText([signals.name, signals.description, signals.readmeText, ...signals.filePaths, ...signals.topics])
  const isSecurityTool = contains(text, /\b(security|penetration|exploit|hack|crack|keygen|scrape|crawl|botnet|malware|ransomware|keylogger|reverse.?shell|backdoor|trojan|virus|payload|exploit|pentest)\b/)

  if (isSecurityTool) {
    return [
      'Không chạy với quyền admin/root nếu chưa hiểu rõ source code.',
      'Cẩn thận với lệnh hệ thống, network request, subprocess, os.system.',
      'Không chạy trên máy chính nếu repo không rõ nguồn gốc.',
      'Chỉ dùng trong môi trường hợp pháp và an toàn.',
    ]
  }

  if (repoType === 'programming_exercise') {
    const items = [
      'Không copy nguyên code nếu chưa hiểu cách hoạt động.',
      'Code có thể không khớp đề bài hiện tại của bạn.',
    ]
    if (!signals.hasTests) items.push('Thiếu test case hoặc input/output, lời giải có thể chưa đáng tin cậy.')
    items.push('Cần tự chạy thử trước khi dùng làm lời giải mẫu.')
    return items
  }

  if (repoType === 'project_practice') {
    const items: string[] = []
    if (!signals.hasEnvExample && !signals.hasDockerConfig) items.push('Không chạy project nếu thiếu .env, database hoặc config.')
    items.push('Cẩn thận dependency cũ hoặc package lỗi thời.')
    items.push('Không dùng trực tiếp credentials hay token nếu repo lỡ public.')
    if (!signals.hasReadme) items.push('Project có thể không chạy được nếu thiếu hướng dẫn setup rõ ràng.')
    return items
  }

  if (repoType === 'study_material') {
    return [
      'Tài liệu có thể cũ hoặc không khớp với đề cương hiện tại.',
      'Note cá nhân có thể thiếu hoặc sai, cần đối chiếu với nguồn chính thức.',
      'Không nên học duy nhất từ repo này nếu thiếu giáo trình hoặc slide chính thống.',
    ]
  }

  if (repoType === 'exam_review') {
    return [
      'Đáp án có thể sai hoặc thiếu giải thích chi tiết.',
      'Format thi có thể đã thay đổi so với đề trong repo.',
      'Không học thuộc đáp án nếu chưa hiểu bản chất vấn đề.',
      'Nên làm thử trước khi xem lời giải để tránh ỷ lại.',
    ]
  }

  if (courseGroup === 'general_skills' || courseGroup === 'design_process') {
    return [
      'Không copy report/slide y nguyên — rubric có thể thay đổi theo giảng viên.',
      'Bài mẫu có thể chỉ phù hợp với một học kỳ hoặc nhóm cụ thể.',
      'Cần đối chiếu yêu cầu bài nộp hiện tại trước khi tham khảo bố cục.',
    ]
  }

  if (repoType === 'unknown') {
    return [
      'Không nên clone hoặc chạy ngay nếu chưa rõ repo làm gì.',
      'Không dùng làm nguồn chính cho bài tập hay deadline.',
      'Cần kiểm tra README, cây thư mục và file chính trước khi quyết định.',
    ]
  }

  return [
    'Cần kiểm tra README và cấu trúc trước khi sử dụng.',
    'Không nên dựa hoàn toàn vào repo khi chưa xác minh nội dung.',
  ]
}

function buildSuitableUse(repoType: RepoType, signals: RepoSignals): string[] {
  if (repoType === 'programming_exercise') return ['Tham khảo cách chia bài và cách giải.', 'Luyện cú pháp hoặc thuật toán qua code mẫu.', 'Đối chiếu test/input/output nếu repo có cung cấp.']
  if (repoType === 'project_practice') return ['Học cách tổ chức module và luồng xử lý.', 'Xem setup/dependency/config của project.', 'Clone thử nếu hướng dẫn chạy đủ rõ.']
  if (repoType === 'study_material') return ['Ôn lại lý thuyết theo slide/note.', 'Tìm nhanh khái niệm hoặc ví dụ môn học.', 'Tổng hợp lại kiến thức trước khi làm bài.']
  if (repoType === 'exam_review') return ['Luyện đề trước kỳ thi.', 'Kiểm tra dạng câu hỏi thường gặp.', signals.hasAnswerOrSolution ? 'Đối chiếu đáp án/lời giải sau khi tự làm.' : 'Tự làm đề và tìm nguồn khác để đối chiếu đáp án.']
  if (repoType === 'mixed_resource') return ['Chọn đúng folder theo nhu cầu.', 'Dùng như kho tài nguyên tham khảo.', 'Không nên đọc tuần tự nếu repo gom nhiều loại nội dung.']
  return ['Khảo sát nhanh metadata.', 'Mở repo trực tiếp để xác minh mục tiêu.', 'Tìm nguồn khác nếu repo vẫn thiếu thông tin.']
}

function buildApplicability(repoType: RepoType, rating: UsefulnessRating, signals: RepoSignals): string[] {
  if (rating === 'insufficient_data') return ['Chưa nên áp dụng trực tiếp vào bài tập hoặc project.', 'Chỉ dùng để khảo sát tên repo và metadata cơ bản.', 'Cần mở GitHub để xác minh trước.']
  if (repoType === 'project_practice') return [signals.hasReadme ? 'Có thể clone thử nếu README hướng dẫn rõ.' : 'Chưa nên clone làm mẫu nếu thiếu hướng dẫn setup.', 'Có thể học cách chia module/source nếu code rõ.', 'Không nên dùng cho deadline khi chưa chạy local được.']
  if (repoType === 'programming_exercise') return ['Có thể dùng để so sánh hướng giải sau khi tự làm.', 'Áp dụng tốt nhất khi có đề bài và test case.', 'Không nên xem như lời giải chuẩn nếu thiếu input/output.']
  if (repoType === 'study_material') return ['Dùng tốt cho ôn tập nếu nội dung có thứ tự rõ.', 'Nên đối chiếu với đề cương hoặc slide chính thức.', 'Không thay thế tài liệu môn nếu file rời rạc.']
  if (repoType === 'exam_review') return ['Phù hợp luyện tốc độ và dạng đề.', 'Mức an tâm cao hơn nếu có năm/kỳ và đáp án.', 'Không nên học tủ nếu thiếu nguồn hoặc phạm vi.']
  return ['Có thể dùng làm nguồn phụ.', 'Cần tự lọc phần đúng nhu cầu.', 'Không nên dựa hoàn toàn vào repo khi tín hiệu còn lẫn lộn.']
}

function buildChecksBeforeUsing(repoType: RepoType, signals: RepoSignals): string[] {
  const base = repoType === 'project_practice'
    ? ['README/setup', 'file env/config', 'database/API docs hoặc lệnh chạy local']
    : repoType === 'programming_exercise'
      ? ['đề bài', 'source code từng bài', 'test case hoặc input/output']
      : repoType === 'study_material'
        ? ['mục lục', 'thứ tự chương/buổi', 'nguồn hoặc phạm vi tài liệu']
        : repoType === 'exam_review'
          ? ['năm/kỳ', 'midterm/final/quiz', 'đáp án hoặc lời giải']
          : ['README', 'cấu trúc thư mục', 'source/tài liệu chính']
  const dynamic = [
    !signals.hasFileList ? 'danh sách file/folder thực tế' : null,
    signals.updatedAt ? null : 'repo còn được cập nhật gần đây không',
    signals.hasLicense ? null : 'license nếu muốn dùng lại code',
  ].filter(Boolean) as string[]
  return compact([...base, ...dynamic], 4)
}

function typeEvidence(repoType: RepoType, signals: RepoSignals): string {
  if (repoType === 'programming_exercise') return compact([signals.hasAssignments ? 'có lab/assignment/exercise' : null, signals.hasTests ? 'có test/input/output' : null, signals.hasSourceCode ? 'có ngôn ngữ/source signal' : null], 3).join(', ')
  if (repoType === 'project_practice') return compact([signals.techStacks.length > 0 ? `stack ${signals.techStacks.slice(0, 3).join(', ')}` : null, signals.hasPackageFile ? 'có package/build file' : null, signals.hasEnvExample ? 'có env example' : null], 3).join(', ')
  if (repoType === 'study_material') return compact([signals.hasSlides ? 'có slide/lecture' : null, signals.hasNotes ? 'có note/docs/theory' : null], 3).join(', ')
  if (repoType === 'exam_review') return compact([signals.hasExam ? 'có exam/midterm/final/quiz' : null, signals.hasAnswerOrSolution ? 'có answer/solution' : null], 3).join(', ')
  return 'nhiều nhóm tín hiệu khác nhau'
}

function buildEvidence(signals: RepoSignals): string[] {
  return compact([
    signals.description ? 'Có mô tả từ dữ liệu repo.' : null,
    signals.topics.length > 0 ? `Có chủ đề/tags: ${signals.topics.slice(0, 5).join(', ')}.` : null,
    signals.primaryLanguage ? `Ngôn ngữ chính: ${signals.primaryLanguage}.` : null,
    signals.techStacks.length > 0 ? `Công nghệ: ${signals.techStacks.slice(0, 5).join(', ')}.` : null,
    signals.stars !== null ? `Sao: ${signals.stars}.` : null,
    signals.forks !== null ? `Fork: ${signals.forks}.` : null,
    signals.updatedAt ? `Cập nhật lần cuối: ${signals.updatedAt}.` : null,
    signals.hasReadme ? 'Có README hoặc nội dung từ README.' : null,
    signals.hasFileList ? `Có ${signals.filePaths.length} file, thư mục để phân tích.` : null,
    signals.hasPackageFile ? 'Có file cấu hình/dependency/build.' : null,
    signals.hasTests ? 'Có tín hiệu test/input/output mẫu.' : null,
    signals.hasExam ? 'Có tín hiệu đề thi/giữa kỳ/cuối kỳ.' : null,
    signals.hasSlides || signals.hasNotes ? 'Có tín hiệu slide/tài liệu học.' : null,
  ], 12)
}

type EvidenceRubric = {
  criteria: RepoEvaluationCriterion[]
  warnings: RepoEvaluationWarning[]
  classificationConfidence: number
  evaluationConfidence: number
  evidenceCoverage: number
  learningUsefulnessScore: number
  technicalReadinessScore: number | null
  runReadinessScore: number | null
  recommendationTag: RecommendationTag
}

function buildEvidenceRubric(repoType: RepoType, courseGroup: CourseGroup, signals: RepoSignals): EvidenceRubric {
  const warnings = detectRepositoryWarnings(repoType, signals)
  const criteria = buildCriteriaForRepoType(repoType, courseGroup, signals, warnings)
  const classificationConfidence = buildConfidenceScore(signals, repoType)
  const evidenceCoverage = calculateEvidenceCoverage(criteria)
  const learningUsefulnessScore = normalizedScore(criteria)
  const technicalReadinessScore = calculateTechnicalReadinessScore(repoType, criteria, signals)
  const runReadinessScore = calculateRunReadinessScore(repoType, criteria, signals, warnings)
  const evaluationConfidence = calculateEvaluationConfidence(classificationConfidence, evidenceCoverage, criteria, warnings)
  const recommendationTag = chooseRecommendationTag({
    repoType,
    signals,
    warnings,
    criteria,
    classificationConfidence,
    evaluationConfidence,
    evidenceCoverage,
    learningUsefulnessScore,
    technicalReadinessScore,
    runReadinessScore,
  })

  return {
    criteria,
    warnings,
    classificationConfidence,
    evaluationConfidence,
    evidenceCoverage,
    learningUsefulnessScore,
    technicalReadinessScore,
    runReadinessScore,
    recommendationTag,
  }
}

function buildCriteriaForRepoType(
  repoType: RepoType,
  courseGroup: CourseGroup,
  signals: RepoSignals,
  warnings: RepoEvaluationWarning[],
): RepoEvaluationCriterion[] {
  if (repoType === 'programming_exercise') return buildProgrammingExerciseCriteria(signals, warnings)
  if (repoType === 'project_practice') return buildProjectPracticeCriteria(signals, warnings)
  if (repoType === 'study_material') return buildStudyMaterialCriteria(signals, warnings)
  if (repoType === 'exam_review') return buildExamReviewCriteria(signals, warnings)
  if (repoType === 'mixed_resource') return buildMixedResourceCriteria(signals, warnings)
  return buildUnknownCriteria(courseGroup, signals, warnings)
}

function buildProgrammingExerciseCriteria(signals: RepoSignals, warnings: RepoEvaluationWarning[]): RepoEvaluationCriterion[] {
  const assignmentEvidence = evidenceList([
    signals.hasAssignments ? evidence('metadata', 'Tên, mô tả, README hoặc đường dẫn nhắc đến lab/bài tập.') : null,
    hasUsefulReadme(signals) ? evidence('readme', 'README có đủ nội dung mô tả ngữ cảnh bài tập.') : null,
    findFirstPath(signals.filePaths, /(assignment|exercise|lab|de|problem|requirement|readme)\b/i, 'file', 'File mô tả bài tập hoặc lab.'),
  ])
  const validationEvidence = evidenceList([
    ...findPaths(signals.filePaths, testPathPattern, 'test', 'Test, sample input/output hoặc spec path.').slice(0, 4),
    contains(normalizeSearchText([signals.readmeText]), /\b(sample|input|output|expected|test case|unit test)\b/)
      ? evidence('readme', 'README nhắc đến sample, expected output hoặc test cases.')
      : null,
  ])
  const explanationEvidence = evidenceList([
    contains(normalizeSearchText([signals.readmeText]), /\b(algorithm|approach|explain|complexity|thuat toan|giai thich|y tuong)\b/)
      ? evidence('readme', 'README có giải thích cách tiếp cận hoặc thuật toán.')
      : null,
    findFirstPath(signals.filePaths, /(explain|algorithm|note|docs?|report)\b/i, 'file', 'File giải thích, ghi chú hoặc báo cáo.'),
  ])

  return [
    criterion({
      key: 'course_topic_identification',
      label: 'Nhận diện môn học/lab/chủ đề',
      maxScore: 15,
      score: scorePart(15, [
        [Boolean(signals.courseCode || signals.courseName), 6],
        [signals.hasAssignments, 5],
        [signals.description !== null || signals.topics.length > 0, 4],
      ]),
      evidence: evidenceList([
        signals.courseCode ? evidence('metadata', `Mã môn: ${signals.courseCode}.`) : null,
        signals.courseName ? evidence('metadata', `Môn: ${signals.courseName}.`) : null,
        signals.hasAssignments ? evidence('metadata', 'Phát hiện từ khóa bài tập/lab.') : null,
      ]),
      warnings,
    }),
    criterion({
      key: 'assignment_description',
      label: 'Mô tả bài tập/vấn đề',
      maxScore: 20,
      score: scorePart(20, [
        [hasUsefulReadme(signals), 9],
        [signals.description !== null && signals.description.length >= 30, 5],
        [assignmentEvidence.length > 0, 6],
      ]),
      evidence: assignmentEvidence,
      warnings: warnings.filter((warning) => warning.code === 'minimal_readme'),
    }),
    criterion({
      key: 'relevant_implementation',
      label: 'Source code / triển khai',
      maxScore: 25,
      score: scorePart(25, [
        [signals.hasSourceCode, 12],
        [sourcePaths(signals).length >= 2, 7],
        [signals.primaryLanguage !== null, 3],
        [signals.hasFileList, 3],
      ]),
      evidence: evidenceList([
        signals.primaryLanguage ? evidence('metadata', `Ngôn ngữ chính: ${signals.primaryLanguage}.`) : null,
        ...sourcePaths(signals).slice(0, 4).map((path) => evidence('file', 'Phát hiện file source code.', path)),
      ]),
      warnings: warnings.filter((warning) => warning.severity === 'critical'),
    }),
    criterion({
      key: 'validation_evidence',
      label: 'Test / sample I/O / kết quả kỳ vọng',
      maxScore: 20,
      score: scorePart(20, [
        [signals.hasTests, 12],
        [validationEvidence.length >= 2, 5],
        [contains(normalizeSearchText([signals.readmeText]), /\b(expected|result|output)\b/), 3],
      ]),
      evidence: validationEvidence,
      warnings: warnings.filter((warning) => warning.code === 'missing_validation_evidence'),
    }),
    criterion({
      key: 'approach_explanation',
      label: 'Giải thích cách tiếp cận/thuật toán',
      maxScore: 10,
      score: scorePart(10, [
        [explanationEvidence.length > 0, 7],
        [signals.hasDocs, 3],
      ]),
      evidence: explanationEvidence,
      warnings: [],
    }),
    criterion({
      key: 'file_organization_hygiene',
      label: 'Tổ chức file và vệ sinh repo',
      maxScore: 10,
      score: Math.max(0, scorePart(10, [
        [signals.hasFileList, 3],
        [signals.organizedFolders || sourcePaths(signals).length <= 6, 3],
        [warnings.every((warning) => warning.code !== 'committed_build_artifacts'), 4],
      ])),
      evidence: evidenceList([
        signals.hasFileList ? evidence('metadata', `${signals.filePaths.length} đường dẫn có sẵn để xem cấu trúc.`) : null,
        signals.organizedFolders ? evidence('directory', 'Repo có nhiều thư mục cấp cao nhất.') : null,
      ]),
      warnings: warnings.filter((warning) => warning.code === 'committed_build_artifacts'),
    }),
    notApplicableCriterion('production_runtime_config', 'Cấu hình runtime/Docker', 0, 'Không yêu cầu cho bài tập lập trình nhẹ.'),
  ]
}

function buildProjectPracticeCriteria(signals: RepoSignals, warnings: RepoEvaluationWarning[]): RepoEvaluationCriterion[] {
  const setupEvidence = evidenceList([
    ...manifestPaths(signals).slice(0, 4).map((path) => evidence('manifest', 'Phát hiện file manifest build hoặc project.', path)),
    contains(normalizeSearchText([signals.readmeText]), /\b(setup|install|run|usage|build|open|visual studio|npm|mvn|gradle|dotnet|docker)\b/)
      ? evidence('readme', 'README có đề cập bước setup, run, build hoặc usage.')
      : null,
  ])
  const demoEvidence = evidenceList([
    ...findPaths(signals.filePaths, /(demo|screenshot|screenshots|image|images|preview|manual|verification|docs?)\b|\.(png|jpe?g|gif|webp)$/i, 'demo', 'Bằng chứng demo, screenshot hoặc xác minh thủ công.').slice(0, 4),
    contains(normalizeSearchText([signals.readmeText]), /\b(demo|screenshot|manual|feature|usage|preview)\b/)
      ? evidence('readme', 'README nhắc đến demo, features, screenshot, usage hoặc preview.')
      : null,
  ])

  return [
    criterion({
      key: 'project_goal_scope',
      label: 'Mục tiêu và phạm vi project',
      maxScore: 10,
      score: scorePart(10, [
        [Boolean(signals.description && signals.description.length >= 30), 4],
        [hasUsefulReadme(signals), 4],
        [signals.topics.length > 0 || signals.courseCode !== null, 2],
      ]),
      evidence: evidenceList([
        signals.description ? evidence('metadata', `Mô tả: ${truncate(signals.description, 120)}.`) : null,
        hasUsefulReadme(signals) ? evidence('readme', 'README có ngữ cảnh project có ý nghĩa.') : null,
      ]),
      warnings: warnings.filter((warning) => warning.code === 'minimal_readme'),
    }),
    criterion({
      key: 'expected_open_build_readiness',
      label: 'Mức độ sẵn sàng mở/build',
      maxScore: 20,
      score: scorePart(20, [
        [manifestPaths(signals).length > 0, 8],
        [setupEvidence.some((item) => item.type === 'readme'), 6],
        [signals.hasBuildFile, 3],
        [signals.hasFileList, 3],
      ]),
      evidence: setupEvidence,
      warnings: warnings.filter((warning) => warning.code === 'missing_setup_guidance'),
    }),
    criterion({
      key: 'source_structure',
      label: 'Cấu trúc source code',
      maxScore: 15,
      score: scorePart(15, [
        [signals.hasSourceCode, 6],
        [sourcePaths(signals).length >= 4, 4],
        [signals.organizedFolders, 3],
        [signals.techStacks.length > 0 || signals.primaryLanguage !== null, 2],
      ]),
      evidence: evidenceList([
        signals.primaryLanguage ? evidence('metadata', `Ngôn ngữ chính: ${signals.primaryLanguage}.`) : null,
        ...sourcePaths(signals).slice(0, 5).map((path) => evidence('file', 'Phát hiện file source.', path)),
      ]),
      warnings: warnings.filter((warning) => warning.severity === 'critical'),
    }),
    criterion({
      key: 'observable_feature_completeness',
      label: 'Mức độ hoàn thiện tính năng',
      maxScore: 15,
      score: scorePart(15, [
        [contains(normalizeSearchText([signals.description, signals.readmeText]), /\b(feature|screen|crud|login|auth|editor|notepad|app|module|function)\b/), 6],
        [sourcePaths(signals).length >= 6, 4],
        [signals.hasDocs || demoEvidence.length > 0, 3],
        [signals.hasFileList, 2],
      ]),
      evidence: evidenceList([
        contains(normalizeSearchText([signals.description, signals.readmeText]), /\b(feature|screen|crud|login|auth|editor|notepad|app|module|function)\b/)
          ? evidence('metadata', 'Phát hiện từ khóa tính năng trong mô tả hoặc README.')
          : null,
        ...demoEvidence.slice(0, 2),
      ]),
      warnings: [],
    }),
    criterion({
      key: 'demo_manual_verification',
      label: 'Demo/ảnh chụp/xác minh thủ công',
      maxScore: 15,
      score: scorePart(15, [
        [demoEvidence.length > 0, 8],
        [demoEvidence.length >= 2, 4],
        [contains(normalizeSearchText([signals.readmeText]), /\b(run|usage|manual|verify|test)\b/), 3],
      ]),
      evidence: demoEvidence,
      warnings: warnings.filter((warning) => warning.code === 'missing_demo_evidence'),
    }),
    criterion({
      key: 'test_validation_evidence',
      label: 'Bằng chứng kiểm thử/validation',
      maxScore: 10,
      score: scorePart(10, [
        [signals.hasTests, 7],
        [contains(normalizeSearchText([signals.readmeText]), /\b(test|validation|verify|manual test)\b/), 3],
      ]),
      evidence: evidenceList([
        ...findPaths(signals.filePaths, testPathPattern, 'test', 'Test/spec/sample path.').slice(0, 4),
        contains(normalizeSearchText([signals.readmeText]), /\b(test|validation|verify|manual test)\b/)
          ? evidence('readme', 'README nhắc đến validation hoặc testing.')
          : null,
      ]),
      warnings: warnings.filter((warning) => warning.code === 'missing_validation_evidence'),
    }),
    criterion({
      key: 'config_secret_hygiene',
      label: 'Cấu hình và bảo mật secrets',
      maxScore: 10,
      score: Math.max(0, scorePart(10, [
        [!hasSecretLikePaths(signals), 6],
        [!needsExternalConfig(signals) || signals.hasEnvExample || signals.hasDockerConfig, 4],
      ])),
      evidence: evidenceList([
        signals.hasEnvExample ? evidence('file', 'Phát hiện file env example.') : null,
        signals.hasDockerConfig ? evidence('manifest', 'Phát hiện cấu hình Docker.') : null,
        !needsExternalConfig(signals) ? evidence('metadata', 'Không phát hiện yêu cầu cấu hình ngoài rõ ràng.') : null,
      ]),
      warnings: warnings.filter((warning) => warning.code === 'possible_secret_file' || warning.code === 'missing_config_template'),
    }),
    criterion({
      key: 'repository_hygiene',
      label: 'Vệ sinh repo',
      maxScore: 5,
      score: warnings.some((warning) => warning.code === 'committed_build_artifacts') ? 1 : 5,
      evidence: evidenceList([
        signals.hasFileList ? evidence('metadata', `${signals.filePaths.length} đường dẫn có sẵn để đánh giá vệ sinh.`) : null,
      ]),
      warnings: warnings.filter((warning) => warning.code === 'committed_build_artifacts'),
    }),
  ]
}

function buildStudyMaterialCriteria(signals: RepoSignals, warnings: RepoEvaluationWarning[]): RepoEvaluationCriterion[] {
  return [
    criterion({
      key: 'course_relevance',
      label: 'Mức độ liên quan đến môn học',
      maxScore: 20,
      score: scorePart(20, [
        [Boolean(signals.courseCode || signals.courseName), 8],
        [signals.description !== null || signals.topics.length > 0, 6],
        [signals.hasSlides || signals.hasNotes || signals.hasDocs, 6],
      ]),
      evidence: evidenceList([
        signals.courseCode ? evidence('metadata', `Mã môn: ${signals.courseCode}.`) : null,
        signals.hasSlides ? evidence('file', 'Phát hiện slide hoặc bài giảng.') : null,
        signals.hasNotes || signals.hasDocs ? evidence('file', 'Phát hiện ghi chú hoặc tài liệu.') : null,
      ]),
      warnings,
    }),
    criterion({
      key: 'learning_content',
      label: 'Mức độ bao phủ nội dung',
      maxScore: 30,
      score: scorePart(30, [
        [signals.hasSlides, 10],
        [signals.hasNotes || signals.hasDocs, 10],
        [signals.hasFileList, 5],
        [signals.organizedFolders, 5],
      ]),
      evidence: evidenceList([
        ...findPaths(signals.filePaths, /(slides?|lectures?|notes?|docs?|documents?)\b|\.(pdf|pptx?|docx?|md)$/i, 'file', 'Đường dẫn tài liệu học tập.').slice(0, 6),
      ]),
      warnings: warnings.filter((warning) => warning.code === 'minimal_readme'),
    }),
    criterion({
      key: 'navigation_structure',
      label: 'Điều hướng và cấu trúc',
      maxScore: 20,
      score: scorePart(20, [
        [signals.hasReadme, 6],
        [hasUsefulReadme(signals), 5],
        [signals.organizedFolders, 5],
        [contains(normalizeSearchText([...signals.filePaths]), /\b(chapter|week|lecture|chuong|tuan|buoi)\b/), 4],
      ]),
      evidence: evidenceList([
        hasUsefulReadme(signals) ? evidence('readme', 'README cung cấp ngữ cảnh điều hướng hữu ích.') : null,
        signals.organizedFolders ? evidence('directory', 'Cấu trúc thư mục giúp duyệt tài liệu dễ dàng.') : null,
      ]),
      warnings: [],
    }),
    criterion({
      key: 'reference_context',
      label: 'Ngữ cảnh tham khảo',
      maxScore: 15,
      score: scorePart(15, [
        [signals.description !== null, 5],
        [signals.topics.length > 0, 4],
        [signals.updatedAt !== null, 3],
        [signals.hasReadme, 3],
      ]),
      evidence: evidenceList([
        signals.description ? evidence('metadata', 'Có mô tả.') : null,
        signals.updatedAt ? evidence('metadata', `Hoạt động gần nhất: ${signals.updatedAt}.`) : null,
      ]),
      warnings: [],
    }),
    criterion({
      key: 'material_hygiene',
      label: 'Vệ sinh repo',
      maxScore: 15,
      score: warnings.some((warning) => warning.code === 'committed_build_artifacts') ? 8 : 15,
      evidence: evidenceList([
        signals.hasFileList ? evidence('metadata', 'Cây thư mục có sẵn để đánh giá vệ sinh repo.') : null,
      ]),
      warnings: warnings.filter((warning) => warning.code === 'committed_build_artifacts'),
    }),
    notApplicableCriterion('build_test_docker', 'Build/kiểm thử/Docker', 0, 'Không yêu cầu cho repo tài liệu học tập.'),
  ]
}

function buildExamReviewCriteria(signals: RepoSignals, warnings: RepoEvaluationWarning[]): RepoEvaluationCriterion[] {
  return [
    criterion({
      key: 'exam_identification',
      label: 'Nhận diện đề thi/quiz',
      maxScore: 25,
      score: scorePart(25, [
        [signals.hasExam, 10],
        [contains(normalizeSearchText([signals.name, signals.description, signals.readmeText, ...signals.filePaths]), /\b(20\d{2}|midterm|final|quiz|giua ky|cuoi ky)\b/), 8],
        [signals.courseCode !== null || signals.courseName !== null, 4],
        [signals.hasFileList, 3],
      ]),
      evidence: evidenceList([
        signals.hasExam ? evidence('metadata', 'Phát hiện từ khóa đề thi, giữa kỳ, cuối kỳ hoặc quiz.') : null,
        ...findPaths(signals.filePaths, /(exam|midterm|final|quiz|de-thi|de_thi|20\d{2})/i, 'file', 'Đường dẫn liên quan đề thi.').slice(0, 4),
      ]),
      warnings,
    }),
    criterion({
      key: 'answer_solution_evidence',
      label: 'Bằng chứng đáp án/lời giải',
      maxScore: 25,
      score: scorePart(25, [
        [signals.hasAnswerOrSolution, 15],
        [findPaths(signals.filePaths, /(answer|answers|solution|solutions|dap-an|loi-giai)/i, 'file', 'Đường dẫn đáp án hoặc lời giải.').length > 0, 6],
        [contains(normalizeSearchText([signals.readmeText]), /\b(answer|solution|dap an|loi giai)\b/), 4],
      ]),
      evidence: evidenceList([
        ...findPaths(signals.filePaths, /(answer|answers|solution|solutions|dap-an|loi-giai)/i, 'file', 'Đường dẫn đáp án hoặc lời giải.').slice(0, 4),
        signals.hasAnswerOrSolution ? evidence('metadata', 'Phát hiện từ khóa đáp án hoặc lời giải.') : null,
      ]),
      warnings: [],
    }),
    criterion({
      key: 'organization',
      label: 'Tổ chức bộ đề thi',
      maxScore: 20,
      score: scorePart(20, [
        [signals.hasFileList, 5],
        [signals.organizedFolders, 5],
        [contains(normalizeSearchText([...signals.filePaths]), /\b(20\d{2}|midterm|final|quiz|giua|cuoi)\b/), 5],
        [signals.hasReadme, 5],
      ]),
      evidence: evidenceList([
        signals.organizedFolders ? evidence('directory', 'Tài liệu thi được phân nhóm vào các thư mục.') : null,
        signals.hasReadme ? evidence('readme', 'Phát hiện README hoặc đường dẫn README.') : null,
      ]),
      warnings: warnings.filter((warning) => warning.code === 'minimal_readme'),
    }),
    criterion({
      key: 'study_context',
      label: 'Ngữ cảnh học tập',
      maxScore: 20,
      score: scorePart(20, [
        [signals.description !== null, 5],
        [hasUsefulReadme(signals), 7],
        [signals.topics.length > 0, 4],
        [signals.updatedAt !== null, 4],
      ]),
      evidence: evidenceList([
        signals.description ? evidence('metadata', 'Có mô tả.') : null,
        hasUsefulReadme(signals) ? evidence('readme', 'README có ngữ cảnh hữu ích.') : null,
      ]),
      warnings: [],
    }),
    criterion({
      key: 'material_hygiene',
      label: 'Vệ sinh repo',
      maxScore: 10,
      score: warnings.some((warning) => warning.code === 'committed_build_artifacts') ? 5 : 10,
      evidence: evidenceList([
        signals.hasFileList ? evidence('metadata', 'Cây thư mục có sẵn để đánh giá vệ sinh.') : null,
      ]),
      warnings: warnings.filter((warning) => warning.code === 'committed_build_artifacts'),
    }),
    notApplicableCriterion('project_build_readiness', 'Sẵn sàng build project', 0, 'Không yêu cầu cho repo ôn thi.'),
  ]
}

function buildMixedResourceCriteria(signals: RepoSignals, warnings: RepoEvaluationWarning[]): RepoEvaluationCriterion[] {
  return [
    criterion({
      key: 'content_identification',
      label: 'Nhận diện nội dung',
      maxScore: 25,
      score: scorePart(25, [
        [signals.description !== null, 7],
        [signals.hasReadme, 6],
        [signals.hasFileList, 6],
        [signals.topics.length > 0, 6],
      ]),
      evidence: evidenceList([
        signals.description ? evidence('metadata', 'Có mô tả.') : null,
        signals.hasReadme ? evidence('readme', 'Phát hiện README hoặc đường dẫn README.') : null,
      ]),
      warnings,
    }),
    criterion({
      key: 'resource_coverage',
      label: 'Mức độ bao phủ tài nguyên',
      maxScore: 35,
      score: scorePart(35, [
        [signals.hasSourceCode, 8],
        [signals.hasDocs || signals.hasNotes, 8],
        [signals.hasSlides, 6],
        [signals.hasExam, 6],
        [signals.organizedFolders, 7],
      ]),
      evidence: evidenceList([
        signals.hasSourceCode ? evidence('file', 'Phát hiện tín hiệu source code.') : null,
        signals.hasDocs || signals.hasNotes ? evidence('file', 'Phát hiện tín hiệu tài liệu hoặc ghi chú.') : null,
        signals.hasExam ? evidence('file', 'Phát hiện tín hiệu đề thi.') : null,
      ]),
      warnings: warnings.filter((warning) => warning.code === 'minimal_readme'),
    }),
    criterion({
      key: 'safe_navigation',
      label: 'Điều hướng an toàn',
      maxScore: 20,
      score: scorePart(20, [
        [hasUsefulReadme(signals), 8],
        [signals.hasFileList, 5],
        [signals.organizedFolders, 5],
        [warnings.every((warning) => warning.severity !== 'critical'), 2],
      ]),
      evidence: evidenceList([
        hasUsefulReadme(signals) ? evidence('readme', 'README cung cấp ngữ cảnh hữu ích.') : null,
        signals.organizedFolders ? evidence('directory', 'Cấu trúc thư mục giúp duyệt tài nguyên.') : null,
      ]),
      warnings: warnings.filter((warning) => warning.severity === 'critical'),
    }),
    criterion({
      key: 'repository_hygiene',
      label: 'Vệ sinh repo',
      maxScore: 20,
      score: warnings.some((warning) => warning.code === 'committed_build_artifacts') ? 10 : 20,
      evidence: evidenceList([
        signals.hasFileList ? evidence('metadata', 'Cây thư mục có sẵn để đánh giá vệ sinh.') : null,
      ]),
      warnings: warnings.filter((warning) => warning.code === 'committed_build_artifacts'),
    }),
  ]
}

function buildUnknownCriteria(courseGroup: CourseGroup, signals: RepoSignals, warnings: RepoEvaluationWarning[]): RepoEvaluationCriterion[] {
  return [
    criterion({
      key: 'minimum_metadata',
      label: 'Thông tin tối thiểu',
      maxScore: 40,
      score: scorePart(40, [
        [signals.description !== null, 10],
        [signals.hasReadme, 10],
        [signals.hasFileList, 10],
        [signals.primaryLanguage !== null || signals.techStacks.length > 0 || signals.topics.length > 0, 10],
      ]),
      evidence: evidenceList([
        signals.description ? evidence('metadata', 'Có mô tả.') : null,
        signals.hasReadme ? evidence('readme', 'Phát hiện README hoặc đường dẫn README.') : null,
        signals.hasFileList ? evidence('metadata', 'Cây thư mục có sẵn.') : null,
      ]),
      warnings,
    }),
    criterion({
      key: 'content_signal',
      label: 'Tín hiệu nội dung chính',
      maxScore: 40,
      score: scorePart(40, [
        [signals.hasSourceCode, 10],
        [signals.hasDocs || signals.hasNotes || signals.hasSlides, 10],
        [signals.hasExam, 10],
        [courseGroup !== 'unknown', 10],
      ]),
      evidence: evidenceList([
        signals.hasSourceCode ? evidence('file', 'Phát hiện tín hiệu source code.') : null,
        signals.hasDocs || signals.hasNotes || signals.hasSlides ? evidence('file', 'Phát hiện tín hiệu tài liệu học tập.') : null,
        signals.hasExam ? evidence('file', 'Phát hiện tín hiệu đề thi.') : null,
      ]),
      warnings: [],
    }),
    criterion({
      key: 'safe_first_step',
      label: 'Bước đầu an toàn',
      maxScore: 20,
      score: scorePart(20, [
        [hasUsefulReadme(signals), 8],
        [signals.hasFileList, 6],
        [warnings.every((warning) => warning.severity !== 'critical'), 6],
      ]),
      evidence: evidenceList([
        hasUsefulReadme(signals) ? evidence('readme', 'README có đủ nội dung để đọc lần đầu.') : null,
        signals.hasFileList ? evidence('metadata', 'Có thể xem cây thư mục trước khi clone.') : null,
      ]),
      warnings: warnings.filter((warning) => warning.severity === 'critical'),
    }),
  ]
}

function criterion(input: {
  key: string
  label: string
  score: number
  maxScore: number
  evidence: RepoEvaluationEvidence[]
  warnings: RepoEvaluationWarning[]
  applicability?: CriterionApplicability
  confidence?: number
}): RepoEvaluationCriterion {
  const applicability = input.applicability ?? 'applicable'
  const maxScore = Math.max(0, input.maxScore)
  const scoreValue = applicability === 'applicable' ? Math.max(0, Math.min(maxScore, Math.round(input.score))) : 0
  return {
    key: input.key,
    label: input.label,
    score: scoreValue,
    maxScore,
    status: statusFor(scoreValue, maxScore, applicability),
    applicability,
    confidence: input.confidence ?? confidenceForCriterion(input.evidence, input.warnings, applicability),
    evidence: input.evidence,
    warnings: input.warnings,
  }
}

function notApplicableCriterion(key: string, label: string, maxScore: number, message: string): RepoEvaluationCriterion {
  return criterion({
    key,
    label,
    maxScore,
    score: 0,
    applicability: 'not_applicable',
    evidence: [evidence('metadata', message)],
    warnings: [],
    confidence: 100,
  })
}

function statusFor(scoreValue: number, maxScore: number, applicability: CriterionApplicability): CriterionStatus {
  if (applicability === 'not_applicable') return 'not_applicable'
  if (applicability === 'unknown') return 'missing'
  if (maxScore <= 0) return 'not_applicable'
  const ratio = scoreValue / maxScore
  if (ratio >= 0.8) return 'strong'
  if (ratio >= 0.55) return 'ok'
  if (ratio > 0) return 'weak'
  return 'missing'
}

function confidenceForCriterion(evidenceItems: RepoEvaluationEvidence[], warnings: RepoEvaluationWarning[], applicability: CriterionApplicability): number {
  if (applicability === 'not_applicable') return 100
  if (applicability === 'unknown') return 20
  const base = Math.min(80, evidenceItems.length * 20)
  const penalty = warnings.some((warning) => warning.severity === 'critical') ? 25 : warnings.length * 8
  return clampScore(base + 20 - penalty)
}

function normalizedScore(criteria: RepoEvaluationCriterion[]): number {
  const applicable = criteria.filter((item) => item.applicability === 'applicable')
  const maxScore = applicable.reduce((total, item) => total + item.maxScore, 0)
  if (maxScore <= 0) return 0
  return clampScore((applicable.reduce((total, item) => total + item.score, 0) / maxScore) * 100)
}

function normalizedScoreForKeys(criteria: RepoEvaluationCriterion[], keys: string[]): number | null {
  const selected = criteria.filter((item) => keys.includes(item.key) && item.applicability === 'applicable')
  if (selected.length === 0) return null
  const maxScore = selected.reduce((total, item) => total + item.maxScore, 0)
  if (maxScore <= 0) return null
  return clampScore((selected.reduce((total, item) => total + item.score, 0) / maxScore) * 100)
}

function calculateTechnicalReadinessScore(repoType: RepoType, criteria: RepoEvaluationCriterion[], signals: RepoSignals): number | null {
  if (!signals.hasSourceCode && !signals.hasPackageFile && !signals.hasBuildFile) return null
  if (repoType === 'study_material' || repoType === 'exam_review') return null
  if (repoType === 'programming_exercise') {
    return normalizedScoreForKeys(criteria, ['relevant_implementation', 'validation_evidence', 'approach_explanation', 'file_organization_hygiene'])
  }
  if (repoType === 'project_practice') {
    return normalizedScoreForKeys(criteria, ['expected_open_build_readiness', 'source_structure', 'observable_feature_completeness', 'test_validation_evidence', 'config_secret_hygiene', 'repository_hygiene'])
  }
  return normalizedScoreForKeys(criteria, ['content_signal', 'safe_first_step', 'repository_hygiene'])
}

function calculateRunReadinessScore(
  repoType: RepoType,
  criteria: RepoEvaluationCriterion[],
  signals: RepoSignals,
  warnings: RepoEvaluationWarning[],
): number | null {
  if (repoType === 'study_material' || repoType === 'exam_review') return null
  if (!signals.hasSourceCode && !signals.hasPackageFile && !signals.hasBuildFile) return null
  const base = repoType === 'programming_exercise'
    ? normalizedScoreForKeys(criteria, ['relevant_implementation', 'validation_evidence'])
    : normalizedScoreForKeys(criteria, ['expected_open_build_readiness', 'demo_manual_verification', 'test_validation_evidence', 'config_secret_hygiene'])
  if (base === null) return null
  const penalty = warnings.some((warning) => warning.severity === 'critical') ? 25 : warnings.filter((warning) => warning.severity === 'warning').length * 5
  return clampScore(base - penalty)
}

function calculateEvidenceCoverage(criteria: RepoEvaluationCriterion[]): number {
  const applicable = criteria.filter((item) => item.applicability === 'applicable')
  if (applicable.length === 0) return 0
  const covered = applicable.filter((item) => item.evidence.length > 0).length
  return clampScore((covered / applicable.length) * 100)
}

function calculateEvaluationConfidence(
  classificationConfidence: number,
  evidenceCoverage: number,
  criteria: RepoEvaluationCriterion[],
  warnings: RepoEvaluationWarning[],
): number {
  const averageCriterionConfidence = criteria.length === 0
    ? 0
    : criteria.reduce((total, item) => total + item.confidence, 0) / criteria.length
  const penalty = warnings.some((warning) => warning.severity === 'critical') ? 12 : warnings.length * 2
  return clampScore((classificationConfidence * 0.35) + (evidenceCoverage * 0.35) + (averageCriterionConfidence * 0.3) - penalty)
}

function chooseRecommendationTag(input: {
  repoType: RepoType
  signals: RepoSignals
  warnings: RepoEvaluationWarning[]
  criteria: RepoEvaluationCriterion[]
  classificationConfidence: number
  evaluationConfidence: number
  evidenceCoverage: number
  learningUsefulnessScore: number
  technicalReadinessScore: number | null
  runReadinessScore: number | null
}): RecommendationTag {
  const hasCriticalWarning = input.warnings.some((warning) => warning.severity === 'critical')
  const hasSeriousWarning = input.warnings.some((warning) => warning.severity === 'critical' || warning.code === 'committed_build_artifacts')
  const hasMainContent = input.signals.hasSourceCode || input.signals.hasDocs || input.signals.hasNotes || input.signals.hasSlides || input.signals.hasExam
  const hasSetupEvidence = input.signals.hasPackageFile || input.signals.hasBuildFile || hasUsefulReadme(input.signals)
  const hasValidationOrDemo = input.signals.hasTests || hasDemoEvidence(input.signals)
  const sparse = input.repoType === 'unknown'
    || input.classificationConfidence < 42
    || input.evidenceCoverage < 35
    || (!input.signals.hasFileList && !input.signals.hasReadme && !input.signals.description)

  if (sparse) return 'insufficient_data'

  if (
    hasMainContent
    && hasSetupEvidence
    && hasValidationOrDemo
    && input.evaluationConfidence >= 70
    && !hasCriticalWarning
    && input.runReadinessScore !== null
    && input.runReadinessScore >= 80
  ) {
    return 'ready_to_use'
  }

  if (
    input.repoType === 'project_practice'
    && input.signals.hasSourceCode
    && input.signals.hasPackageFile
    && (hasUsefulReadme(input.signals) || hasDemoEvidence(input.signals))
    && input.technicalReadinessScore !== null
    && input.technicalReadinessScore >= 72
    && !hasSeriousWarning
  ) {
    return 'good_project_sample'
  }

  if (
    (input.repoType === 'study_material' || input.repoType === 'exam_review')
    && input.learningUsefulnessScore >= 68
    && input.evaluationConfidence >= 55
    && !hasCriticalWarning
  ) {
    return 'good_study_material'
  }

  if (
    input.learningUsefulnessScore >= 45
    && (!hasValidationOrDemo || input.runReadinessScore === null || input.runReadinessScore < 45 || hasSeriousWarning)
  ) {
    return hasSeriousWarning && input.learningUsefulnessScore < 65 ? 'reference_only' : 'needs_check'
  }

  if (input.learningUsefulnessScore >= 30 || hasMainContent) return 'reference_only'
  return 'insufficient_data'
}

function buildReadyToUseFromRubric(repoType: RepoType, signals: RepoSignals, runReadinessScore: number | null, stars: number): { level: ReadyToUseLevel; label: string; stars: number; note: string } {
  if (runReadinessScore === null) {
    if (repoType === 'study_material' || repoType === 'exam_review') {
      return {
        level: signals.hasReadme || signals.hasFileList ? 'needs_check' : 'insufficient_data',
        label: signals.hasReadme || signals.hasFileList ? readyToUseLabels.needs_check : readyToUseLabels.insufficient_data,
        stars,
        note: 'Repo này phù hợp đánh giá như tài liệu học; mức độ sẵn sàng chạy không áp dụng.',
      }
    }
    return {
      level: 'insufficient_data',
      label: readyToUseLabels.insufficient_data,
      stars,
      note: 'Chưa đủ bằng chứng kỹ thuật để đánh giá mức sẵn sàng chạy.',
    }
  }
  const level: ReadyToUseLevel = runReadinessScore >= 85 ? 'very_ready' : runReadinessScore >= 68 ? 'ready' : runReadinessScore >= 40 ? 'needs_check' : runReadinessScore >= 20 ? 'quick_reference' : 'insufficient_data'
  return {
    level,
    label: readyToUseLabels[level],
    stars,
    note: level === 'very_ready' || level === 'ready'
      ? 'Metadata cho thấy repo có thể chạy được, nhưng chưa xác minh build/test.'
      : 'Metadata chưa đủ để xem repo này sẵn sàng clone và chạy.',
  }
}

function mapRunReadinessToStars(value: number | null): number {
  if (value === null) return 1
  if (value >= 85) return 5
  if (value >= 68) return 4
  if (value >= 40) return 3
  if (value >= 20) return 2
  return 1
}

function buildRecommendationFromTag(tag: RecommendationTag, repoType: RepoType, signals: RepoSignals): string {
  if (tag === 'ready_to_use') return 'Metadata cho thấy repo có thể dùng được, nhưng vẫn cần kiểm tra build/test local trước khi tin tưởng.'
  if (tag === 'good_project_sample') return 'Phù hợp để học cấu trúc project sau khi kiểm tra setup, demo và cảnh báo.'
  if (tag === 'good_study_material') return 'Hữu ích làm tài liệu học; đối chiếu với đề cương môn trước khi dùng chính thức.'
  if (tag === 'reference_only') return 'Tham khảo có chọn lọc; thiếu bằng chứng nên không copy hay chạy mù.'
  if (tag === 'needs_check') return 'Tham khảo sau khi kiểm tra; mở GitHub xem README, file, validation và cảnh báo trước.'
  if (repoType === 'programming_exercise' && signals.hasSourceCode) return 'Chỉ xem source sau khi tìm được đề bài và sample case gốc.'
  return 'Chưa đủ bằng chứng để đề xuất dùng trực tiếp.'
}

function getConfidenceFromScore(value: number, repoType: RepoType): ConfidenceLabel {
  if (repoType === 'unknown' || value < 42) return 'low'
  if (value >= 70) return 'high'
  return 'medium'
}

function deriveStrengthsFromRubric(criteria: RepoEvaluationCriterion[], signals: RepoSignals): string[] {
  const strong = criteria
    .filter((item) => item.applicability === 'applicable' && (item.status === 'strong' || item.status === 'ok'))
    .map((item) => `${item.label}: ${item.score}/${item.maxScore}.`)
  return compact([
    ...strong,
    signals.description ? `Mô tả cho biết trọng tâm: ${truncate(signals.description, 110)}.` : null,
  ], 4, ['Có một số metadata cơ bản để đánh giá sơ bộ.'])
}

function deriveWeaknessesFromRubric(
  criteria: RepoEvaluationCriterion[],
  warnings: RepoEvaluationWarning[],
  repoType: RepoType,
  signals: RepoSignals,
  rating: UsefulnessRating,
): string[] {
  const weak = criteria
    .filter((item) => item.applicability === 'applicable' && (item.status === 'weak' || item.status === 'missing'))
    .map((item) => `${item.label}: evidence is weak or missing.`)
  const warningMessages = warnings
    .filter((warning) => warning.severity !== 'info')
    .map((warning) => warning.message)
  return compact([
    !signals.hasReadme && repoType === 'project_practice' ? 'Missing README/setup guidance is a major weakness for a runnable project.' : null,
    ...warningMessages,
    ...weak,
    ...buildWeaknesses(repoType, signals, rating),
  ], 4, ['Chưa thấy điểm yếu lớn từ metadata hiện tại, nhưng vẫn cần kiểm tra trực tiếp trên GitHub.'])
}

function deriveActionsFromRubric(
  criteria: RepoEvaluationCriterion[],
  warnings: RepoEvaluationWarning[],
  repoType: RepoType,
  signals: RepoSignals,
): string[] {
  const missing = criteria.filter((item) => item.applicability === 'applicable' && (item.status === 'missing' || item.status === 'weak'))
  const actions = compact([
    warnings.some((warning) => warning.severity === 'critical') ? 'Kiểm tra file secret/config trước khi clone hoặc chạy.' : null,
    warnings.some((warning) => warning.code === 'committed_build_artifacts') ? 'Kiểm tra file sinh tự động, tập trung vào source, bỏ qua bin/obj/.vs.' : null,
    missing.find((item) => item.key.includes('validation') || item.key.includes('demo')) ? 'Tìm sample input/output, test, screenshot hoặc xác minh thủ công trước khi tin kết quả.' : null,
    missing.find((item) => item.key.includes('setup') || item.key.includes('build')) ? 'Đọc README và file manifest để suy luận bước setup; đừng cho rằng build chạy được.' : null,
    missing.find((item) => item.key.includes('description') || item.key.includes('goal')) ? 'Xác định rõ mục tiêu bài tập/project trước khi dùng repo này làm tham khảo.' : null,
  ], 4)
  return compact([...actions, ...buildNextActions(repoType, signals)], 4)
}

function detectRepositoryWarnings(repoType: RepoType, signals: RepoSignals): RepoEvaluationWarning[] {
  const warnings: RepoEvaluationWarning[] = []
  const artifactPaths = signals.filePaths.filter((path) => /(^|\/)(\.vs|bin\/debug|bin\/release|obj\/debug|obj\/release|target|dist|build|node_modules)(\/|$)/i.test(path))
  if (artifactPaths.length > 0) {
    warnings.push({
      code: 'committed_build_artifacts',
      severity: 'warning',
      message: 'Repo có vẻ chứa file IDE/build được sinh tự động.',
      paths: artifactPaths.slice(0, 8),
    })
  }
  if (signals.hasReadme && !hasUsefulReadme(signals)) {
    warnings.push({
      code: 'minimal_readme',
      severity: 'info',
      message: 'README có tồn tại nhưng quá ngắn hoặc chưa đủ chi tiết.',
    })
  }
  if ((repoType === 'programming_exercise' || repoType === 'project_practice') && !signals.hasTests) {
    warnings.push({
      code: 'missing_validation_evidence',
      severity: 'warning',
      message: 'Không thấy test, sample input/output hoặc bằng chứng xác thực từ metadata.',
    })
  }
  if (repoType === 'project_practice' && !hasSetupGuidance(signals)) {
    warnings.push({
      code: 'missing_setup_guidance',
      severity: 'warning',
      message: 'File manifest project có thể có, nhưng không thấy hướng dẫn setup/run.',
    })
  }
  if (repoType === 'project_practice' && !hasDemoEvidence(signals)) {
    warnings.push({
      code: 'missing_demo_evidence',
      severity: 'info',
      message: 'Không thấy screenshot, demo hoặc bằng chứng xác minh thủ công.',
    })
  }
  if (needsExternalConfig(signals) && !signals.hasEnvExample && !signals.hasDockerConfig) {
    warnings.push({
      code: 'missing_config_template',
      severity: 'warning',
      message: 'Có vẻ cần cấu hình ngoài, nhưng không thấy .env example hoặc Docker config.',
    })
  }
  const secretPaths = signals.filePaths.filter((path) => /(^|\/)(\.env|id_rsa|.*secret.*|.*credential.*|.*token.*|.*key.*)(\..*)?$/i.test(path) && !/\.env\.(example|sample|template)$/i.test(path))
  if (secretPaths.length > 0) {
    warnings.push({
      code: 'possible_secret_file',
      severity: 'critical',
      message: 'Phát hiện đường dẫn có thể chứa secret hoặc credential trong cây thư mục.',
      paths: secretPaths.slice(0, 6),
    })
  }
  return warnings
}

function evidence(type: RepoEvaluationEvidence['type'], message: string, path?: string): RepoEvaluationEvidence {
  return path ? { type, path, message } : { type, message }
}

function evidenceList(items: Array<RepoEvaluationEvidence | null | undefined>): RepoEvaluationEvidence[] {
  const seen = new Set<string>()
  const result: RepoEvaluationEvidence[] = []
  for (const item of items) {
    if (!item) continue
    const key = `${item.type}:${item.path ?? ''}:${item.message}`
    if (seen.has(key)) continue
    seen.add(key)
    result.push(item)
  }
  return result
}

function findPaths(paths: string[], pattern: RegExp, type: RepoEvaluationEvidence['type'], message: string): RepoEvaluationEvidence[] {
  return paths
    .filter((path) => pattern.test(path))
    .map((path) => evidence(type, message, path))
}

function findFirstPath(paths: string[], pattern: RegExp, type: RepoEvaluationEvidence['type'], message: string): RepoEvaluationEvidence | null {
  const path = paths.find((item) => pattern.test(item))
  return path ? evidence(type, message, path) : null
}

function scorePart(maxScore: number, values: Array<[boolean, number]>): number {
  return Math.min(maxScore, scoreSignals(values))
}

function sourcePaths(signals: RepoSignals): string[] {
  return signals.filePaths.filter((path) => sourceFilePattern.test(path))
}

function manifestPaths(signals: RepoSignals): string[] {
  return signals.filePaths.filter((path) => projectConfigPattern.test(path) || buildFilePattern.test(path))
}

function hasUsefulReadme(signals: RepoSignals): boolean {
  const readme = signals.readmeText?.trim() ?? ''
  if (readme.length < 50) return false
  const text = normalizeSearchText([readme])
  return contains(text, /\b(setup|install|run|usage|feature|assignment|lab|exercise|algorithm|overview|description|guide|how to|requirement|demo|test|chapter|lecture|note)\b/)
    || readme.split(/\s+/).length >= 30
}

function hasSetupGuidance(signals: RepoSignals): boolean {
  return contains(normalizeSearchText([signals.readmeText]), /\b(setup|install|run|usage|build|open|visual studio|npm|mvn|gradle|dotnet|docker|compile)\b/)
}

function hasDemoEvidence(signals: RepoSignals): boolean {
  return hasPath(signals.filePaths, /(demo|screenshot|screenshots|image|images|preview|manual|verification|docs?)\b|\.(png|jpe?g|gif|webp)$/i)
    || contains(normalizeSearchText([signals.readmeText]), /\b(demo|screenshot|manual|feature|usage|preview|verification)\b/)
}

function needsExternalConfig(signals: RepoSignals): boolean {
  return contains(normalizeSearchText([signals.name, signals.description, signals.readmeText, ...signals.filePaths, ...signals.techStacks]), /\b(api|database|db|server|backend|postgres|mysql|mongodb|firebase|supabase|auth|jwt|oauth|docker|deploy|cloud)\b/)
}

function hasSecretLikePaths(signals: RepoSignals): boolean {
  return signals.filePaths.some((path) => /(^|\/)(\.env|id_rsa|.*secret.*|.*credential.*|.*token.*|.*key.*)(\..*)?$/i.test(path) && !/\.env\.(example|sample|template)$/i.test(path))
}

function normalizeFilePaths(value: OptionalRepoMetadata['files'] | OptionalRepoMetadata['paths'] | OptionalRepoMetadata['fileTree']): string[] {
  if (!value) return []
  const rawValues = Array.isArray(value) ? value : value.split(/\r?\n/)
  const paths: string[] = []
  const seen = new Set<string>()
  for (const item of rawValues) {
    const rawPath = typeof item === 'string' ? item : item.path ?? item.name
    const normalized = cleanText(rawPath)?.replace(/\\/g, '/')
    if (!normalized || seen.has(normalized)) continue
    seen.add(normalized)
    paths.push(normalized)
  }
  return paths
}

function normalizeSearchText(values: Array<string | null | undefined>): string {
  return values
    .filter(Boolean)
    .join(' ')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'd')
    .toLowerCase()
}

function hasPath(paths: string[], pattern: RegExp): boolean {
  return paths.some((path) => pattern.test(path))
}

function contains(value: string, pattern: RegExp): boolean {
  return pattern.test(value)
}

function score(values: boolean[]): number {
  return values.filter(Boolean).length
}

function scoreSignals(values: Array<[boolean, number]>): number {
  return values.reduce((total, [enabled, weight]) => total + (enabled ? weight : 0), 0)
}

function clampScore(value: number): number {
  return Math.max(0, Math.min(100, Math.round(value)))
}

function collectMatches(text: string, matchers: Array<[string, RegExp]>): string[] {
  const matches: string[] = []
  for (const [label, pattern] of matchers) {
    if (pattern.test(text)) matches.push(label)
  }
  return matches
}

function unique(values: string[]): string[] {
  return uniqueClean(values)
}

function countTopLevelFolders(paths: string[]): number {
  const folders = new Set<string>()
  for (const path of paths) {
    const part = path.split(/[\\/]/)[0]
    if (part && !/\./.test(part)) folders.add(part)
  }
  return folders.size
}

function uniqueClean(values: unknown[]): string[] {
  const seen = new Set<string>()
  const result: string[] = []
  for (const value of values) {
    const normalized = cleanText(value)
    if (!normalized || seen.has(normalized)) continue
    seen.add(normalized)
    result.push(normalized)
  }
  return result
}

function compact(items: Array<string | null | undefined>, limit: number, fallback: string[] = []): string[] {
  const seen = new Set<string>()
  const result: string[] = []
  for (const item of items) {
    const normalized = item?.trim()
    if (!normalized) continue
    const key = normalized.toLowerCase()
    if (seen.has(key)) continue
    seen.add(key)
    result.push(normalized)
    if (result.length >= limit) break
  }
  return result.length > 0 ? result : fallback
}

function truncate(value: string, maxLength: number): string {
  if (value.length <= maxLength) return value
  return `${value.slice(0, maxLength - 1).trim()}…`
}
