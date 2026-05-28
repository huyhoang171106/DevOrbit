import type { RepoSummary } from '../types/api'

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
  bestFor: string
  mainReason: string
  quickSummary: string
  strengths: string[]
  weaknesses: string[]
  nextActions: string[]
  suitableUse: string[]
  applicability: string[]
  checksBeforeUsing: string[]
  evidence: string[]
  confidence: ConfidenceLabel
  confidenceLabel: string
  typeReason: string
  sections: EvaluationSection[]
  signals: RepoSignals
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
  files?: string[] | Array<{ path?: string | null; name?: string | null }> | null
  fileList?: string[] | Array<{ path?: string | null; name?: string | null }> | null
  paths?: string[] | null
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

export function evaluateRepository(repo: RepoSummary): RepoEvaluationResult {
  const signals = extractRepoSignals(repo)
  const { repoType, reason } = classifyRepoType(signals)
  const usefulnessRating = rateUsefulness(repoType, signals)
  const confidence = getConfidence(signals, repoType)
  const bestFor = buildBestFor(repoType, usefulnessRating, signals)
  const mainReason = buildMainReason(repoType, usefulnessRating, signals)
  const quickSummary = buildQuickSummary(repoType, usefulnessRating, signals, confidence)
  const strengths = buildStrengths(repoType, signals, usefulnessRating)
  const weaknesses = buildWeaknesses(repoType, signals, usefulnessRating)
  const nextActions = buildNextActions(repoType, signals)
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
    bestFor,
    mainReason,
    quickSummary,
    strengths,
    weaknesses,
    nextActions,
    suitableUse,
    applicability,
    checksBeforeUsing,
    evidence,
    confidence,
    confidenceLabel: confidence === 'high' ? 'Cao' : confidence === 'medium' ? 'Trung bình' : 'Thấp',
    typeReason: reason,
    sections: [
      { title: 'Repo nói về gì?', items: buildAboutRepo(repoType, signals) },
      { title: 'Có áp dụng được không?', items: applicability },
      { title: 'Cần kiểm tra trước khi tin/dùng', items: checksBeforeUsing },
      { title: 'Tín hiệu đã dùng để đánh giá', items: evidence.slice(0, 6) },
      { title: 'Vì sao xếp loại này?', items: [reason, mainReason] },
    ],
    signals,
  }
}

export function extractRepoSignals(repo: RepoSummary): RepoSignals {
  const metadata = repo as RepoSummary & OptionalRepoMetadata
  const name = cleanText(repo.displayName) || `Repo #${repo.id}`
  const description = cleanText(repo.description)
  const topics = normalizeList(metadata.topics ?? metadata.tags)
  const primaryLanguage = cleanText(repo.primaryLanguage)
  const techStacks = Array.from(new Set((repo.techStacks ?? []).map(cleanText).filter(Boolean) as string[]))
  const readmeText = cleanText(
    metadata.readmeExcerpt ?? metadata.readmeContent ?? metadata.readmeMarkdown ?? metadata.readmeText ?? metadata.readme,
  )
  const filePaths = normalizeFilePaths(metadata.files ?? metadata.fileList ?? metadata.paths)
  const haystack = normalizeSearchText([name, description, primaryLanguage, ...techStacks, ...topics, readmeText, ...filePaths])
  const hasFileList = filePaths.length > 0
  const hasReadme = Boolean(readmeText) || hasPath(filePaths, /(^|\/)readme(\.md|\.txt)?$/i)
  const hasPackageFile = hasPath(filePaths, /(^|\/)(package\.json|pnpm-lock\.yaml|yarn\.lock|bun\.lock|requirements\.txt|pyproject\.toml|pom\.xml|build\.gradle|settings\.gradle|pubspec\.yaml|composer\.json|go\.mod)$/i)
  const hasBuildFile = hasPackageFile || hasPath(filePaths, /(^|\/)(makefile|cmakelists\.txt|mvnw|gradlew|dockerfile)$/i)
  const hasEnvExample = hasPath(filePaths, /(^|\/)\.env\.(example|sample|template)$/i)
  const hasDockerConfig = hasPath(filePaths, /(^|\/)(docker-compose\.ya?ml|dockerfile)$/i)
  const hasSourceCode = Boolean(primaryLanguage || techStacks.length > 0 || hasPath(filePaths, /(^|\/)(src|app|lib|components|controllers|services|models)(\/|$)/i))
  const hasTests = hasPath(filePaths, /(^|\/)(__tests__|tests?|spec|input|output|sample)(\/|$)/i) || contains(haystack, /\b(test|tests|unit test|input|output|sample)\b/)
  const hasAssignments = contains(haystack, /\b(lab|labs|assignment|assignments|exercise|exercises|practice|bai tap|bài tập|thuc hanh|thực hành|dsa|algorithm|oop)\b/)
  const hasSolutions = contains(haystack, /\b(solution|solutions|answer|answers|loi giai|lời giải)\b/) || hasPath(filePaths, /(^|\/)(solution|solutions|answer|answers)(\/|$)/i)
  const hasSlides = contains(haystack, /\b(slide|slides|ppt|pptx|lecture|lectures)\b/) || hasPath(filePaths, /\.(pptx?|pdf)$/i)
  const hasNotes = contains(haystack, /\b(note|notes|document|docs|theory|ly thuyet|lý thuyết|summary|cheatsheet|giao trinh|giáo trình)\b/)
  const hasDocs = hasNotes || hasPath(filePaths, /(^|\/)(docs?|documents?)(\/|$)/i)
  const hasExam = contains(haystack, /\b(exam|exams|midterm|final|quiz|past exam|de thi|đề thi|on tap|ôn tập)\b/)
  const hasAnswerOrSolution = hasSolutions || contains(haystack, /\b(answer key|dap an|đáp án)\b/)
  const hasLicense = hasPath(filePaths, /(^|\/)licen[cs]e(\.md|\.txt)?$/i)
  const organizedFolders = hasFileList && countTopLevelFolders(filePaths) >= 3
  const stars = typeof repo.stars === 'number' ? repo.stars : null
  const forks = typeof metadata.forks === 'number' ? metadata.forks : null
  const updatedAt = cleanText(metadata.updatedAt ?? metadata.lastPushedAt)
  const evidence = buildEvidence({
    name,
    description,
    topics,
    primaryLanguage,
    techStacks,
    stars,
    forks,
    updatedAt,
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

function classifyRepoType(signals: RepoSignals): { repoType: RepoType; reason: string } {
  const text = normalizeSearchText([
    signals.name,
    signals.description,
    signals.primaryLanguage,
    ...signals.techStacks,
    ...signals.topics,
    ...signals.filePaths,
  ])
  const scores: Record<RepoType, number> = {
    programming_exercise: score([
      signals.hasAssignments,
      signals.hasSourceCode && contains(text, /\b(code|programming|algorithm|dsa|oop|java|python|c\+\+|cpp|c#)\b/),
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

  if (strongTypes.length >= 2 && strongTypes[0][1] - strongTypes[1][1] <= 1) {
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

function rateUsefulness(repoType: RepoType, signals: RepoSignals): UsefulnessRating {
  if (repoType === 'unknown') {
    return signals.evidence.length <= 2 ? 'insufficient_data' : 'quick_reference'
  }
  if (repoType === 'mixed_resource') {
    if (signals.organizedFolders && (signals.hasReadme || signals.hasDocs)) return 'recommended'
    return 'selective'
  }
  if (repoType === 'programming_exercise') {
    if (signals.hasSourceCode && signals.hasAssignments && signals.hasTests && (signals.hasSolutions || signals.organizedFolders)) return 'highly_recommended'
    if (signals.hasSourceCode && (signals.hasTests || signals.hasSolutions || (signals.hasAssignments && signals.organizedFolders))) return 'recommended'
    if (signals.hasSourceCode && (signals.description || signals.topics.length > 0)) return 'selective'
    return 'quick_reference'
  }
  if (repoType === 'project_practice') {
    if (signals.hasReadme && signals.hasSourceCode && signals.hasPackageFile && (signals.hasEnvExample || signals.hasDockerConfig || signals.hasBuildFile)) return 'highly_recommended'
    if (signals.hasSourceCode && (signals.hasPackageFile || signals.techStacks.length >= 2) && (signals.description || signals.hasReadme)) return 'recommended'
    if (signals.hasSourceCode) return 'selective'
    return 'low_priority'
  }
  if (repoType === 'study_material') {
    if ((signals.hasSlides || signals.hasNotes) && (signals.organizedFolders || signals.hasDocs || signals.hasReadme)) return 'highly_recommended'
    if (signals.hasSlides || signals.hasNotes || signals.hasDocs) return 'recommended'
    return 'quick_reference'
  }
  if (repoType === 'exam_review') {
    if (signals.hasExam && signals.hasAnswerOrSolution && (signals.organizedFolders || signals.hasFileList || signals.description)) return 'highly_recommended'
    if (signals.hasExam) return signals.hasAnswerOrSolution ? 'recommended' : 'selective'
    return 'quick_reference'
  }
  return 'selective'
}

function getConfidence(signals: RepoSignals, repoType: RepoType): ConfidenceLabel {
  const signalCount = [
    signals.description,
    signals.primaryLanguage,
    signals.topics.length > 0,
    signals.hasReadme,
    signals.hasFileList,
    signals.techStacks.length > 0,
    signals.stars !== null,
    signals.forks !== null,
    signals.updatedAt,
  ].filter(Boolean).length
  if (repoType === 'unknown' || signalCount <= 3) return 'low'
  if (signalCount >= 6 || signals.hasFileList) return 'high'
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
  if (rating === 'insufficient_data') return 'Thiếu description, topics, README và danh sách file/folder nên chưa đủ cơ sở kết luận sâu.'
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
  if (repoType === 'programming_exercise') {
    return `${signals.name} phù hợp để tham khảo bài giải lập trình hơn là học lý thuyết từ đầu. Nên ưu tiên xem đề bài, source code, test case hoặc input/output trước khi dùng làm mẫu.${caution}`
  }
  if (repoType === 'project_practice') {
    return `${signals.name} phù hợp để xem cách tổ chức một project thực hành. Giá trị chính nằm ở cấu trúc source code, stack và file cấu hình; cần kiểm tra README/setup trước khi clone.${caution}`
  }
  if (repoType === 'study_material') {
    return `${signals.name} phù hợp để ôn hoặc hệ thống kiến thức môn học. Nên xem nội dung có chia theo chương/buổi/chủ đề không trước khi dùng làm tài liệu chính.${caution}`
  }
  if (repoType === 'exam_review') {
    return `${signals.name} phù hợp để luyện đề và kiểm tra kiến thức trước kỳ thi. Cần xác nhận đề có đáp án, lời giải, năm/kỳ hoặc phạm vi rõ không.${caution}`
  }
  if (repoType === 'mixed_resource') {
    return `${signals.name} có vẻ là repo tổng hợp nhiều loại tài nguyên. Nên mở đúng phần mình cần, rồi kiểm tra từng folder thay vì đọc tuần tự từ đầu.${caution}`
  }
  return `Repo này chỉ nên tham khảo nhanh cho đến khi bạn xác nhận được mục tiêu, cấu trúc và nội dung chính trên GitHub.${caution}`
}

function buildStrengths(repoType: RepoType, signals: RepoSignals, rating: UsefulnessRating): string[] {
  const items: string[] = []
  if (signals.description) items.push(`Description cho biết trọng tâm: ${truncate(signals.description, 110)}.`)
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
  if (!signals.hasFileList) items.push('DevOrbit chưa có danh sách file/folder, nên chưa kiểm chứng được cấu trúc repo.')
  if (!signals.description) items.push('Thiếu description nên mục tiêu repo chưa rõ từ màn hình này.')
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
    return ['Xác định năm/kỳ/midterm/final của đề.', 'Kiểm tra có đáp án hoặc lời giải không.', 'Làm thử đề trước khi xem lời giải để tự đánh giá.']
  }
  return ['Mở GitHub để kiểm tra README và cấu trúc repo.', 'Xác định repo chứa code, tài liệu, hay đề ôn tập.', 'Chỉ dùng làm tham khảo nhanh nếu vẫn thiếu dữ liệu.']
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
  if (repoType === 'exam_review') return ['Phù hợp luyện tốc độ và dạng đề.', 'Độ tin cậy cao hơn nếu có năm/kỳ và đáp án.', 'Không nên học tủ nếu thiếu nguồn hoặc phạm vi.']
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
    !signals.hasReadme ? 'README có tồn tại trên GitHub không' : null,
    !signals.hasFileList ? 'danh sách file/folder thực tế' : null,
    signals.updatedAt ? null : 'repo còn được cập nhật gần đây không',
    signals.hasLicense ? null : 'license nếu muốn dùng lại code',
  ].filter(Boolean) as string[]
  return compact([...base, ...dynamic], 4)
}

function buildAboutRepo(repoType: RepoType, signals: RepoSignals): string[] {
  return compact([
    signals.description ? `Mô tả hiện có: ${truncate(signals.description, 130)}.` : 'Chưa có description rõ trong dữ liệu DevOrbit.',
    `Loại repo suy ra: ${repoTypeLabels[repoType]}.`,
    signals.topics.length > 0 ? `Topics/tags: ${signals.topics.slice(0, 6).join(', ')}.` : null,
    signals.primaryLanguage ? `Ngôn ngữ chính: ${signals.primaryLanguage}.` : null,
  ], 4)
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
    signals.description ? 'Có description từ dữ liệu repo.' : null,
    signals.topics.length > 0 ? `Có topics/tags: ${signals.topics.slice(0, 5).join(', ')}.` : null,
    signals.primaryLanguage ? `Primary language: ${signals.primaryLanguage}.` : null,
    signals.techStacks.length > 0 ? `Tech stack: ${signals.techStacks.slice(0, 5).join(', ')}.` : null,
    signals.stars !== null ? `Stars: ${signals.stars}.` : null,
    signals.forks !== null ? `Forks: ${signals.forks}.` : null,
    signals.updatedAt ? `Last updated: ${signals.updatedAt}.` : null,
    signals.hasReadme ? 'Có README hoặc README excerpt.' : null,
    signals.hasFileList ? `Có ${signals.filePaths.length} file/folder path để phân tích.` : null,
    signals.hasPackageFile ? 'Có package/build/dependency file.' : null,
    signals.hasTests ? 'Có tín hiệu test/input/output/sample.' : null,
    signals.hasExam ? 'Có tín hiệu exam/midterm/final/quiz.' : null,
    signals.hasSlides || signals.hasNotes ? 'Có tín hiệu slide/note/tài liệu học.' : null,
  ], 12)
}

function normalizeFilePaths(value: OptionalRepoMetadata['files'] | OptionalRepoMetadata['paths']): string[] {
  if (!value) return []
  const rawValues = Array.isArray(value) ? value : []
  return Array.from(new Set(rawValues.map((item) => {
    if (typeof item === 'string') return cleanText(item)
    return cleanText(item.path ?? item.name)
  }).filter(Boolean) as string[]))
}

function normalizeList(value: string[] | string | null | undefined): string[] {
  if (!value) return []
  const rawValues = Array.isArray(value) ? value : value.split(/[,;|]/)
  return Array.from(new Set(rawValues.map(cleanText).filter(Boolean) as string[]))
}

function normalizeSearchText(values: Array<string | null | undefined>): string {
  return values.filter(Boolean).join(' ').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
}

function cleanText(value: unknown): string | null {
  if (typeof value !== 'string') return null
  const normalized = value.replace(/\s+/g, ' ').trim()
  return normalized ? normalized : null
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

function countTopLevelFolders(paths: string[]): number {
  return new Set(paths.map((path) => path.split(/[\\/]/)[0]).filter((part) => part && !part.includes('.'))).size
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
