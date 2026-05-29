import type { RepoSummary } from '../types/api'
import { formatVietnameseRelativeDate } from './repoEvaluation'

export type RepoAiAnalysisTone = 'accent' | 'indigo' | 'amber' | 'rose'

export type RepoAiAnalysisSection = {
  key: 'overview' | 'technology' | 'fit' | 'readmeInsights' | 'reviewFirst' | 'strategy' | 'nextSteps' | 'warnings'
  title: string
  content: string
  tone: RepoAiAnalysisTone
  items?: string[]
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
  deadline?: string | null
}

type RepoSignals = {
  name: string
  description: string | null
  language: string | null
  techStacks: string[]
  topics: string[]
  readmeExcerpt: string | null
  courseLabel: string | null
  stars: number | null
  forks: number | null
  updatedAt: string | null
  deadline: string | null
}

type RepoAssessment = {
  category: string
  completeness: 'ít' | 'trung bình' | 'đầy đủ'
  learningFit: 'thấp' | 'vừa' | 'cao'
  evidenceCount: number
  missingSignals: string[]
  warnings: string[]
}

const languagePreparation: Array<{
  match: RegExp
  label: string
  category: string
  prepare: string
  reading: string
  config: string
}> = [
  {
    match: /typescript|javascript|react|vue|angular|vite|next/,
    label: 'web/frontend',
    category: 'frontend/web UI',
    prepare: 'component, routing, state/data flow, gọi API và cách quản lý form/error state',
    reading: 'Bắt đầu từ router hoặc component màn hình chính, sau đó lần theo API client và state management.',
    config: 'package.json, vite.config hoặc file cấu hình framework',
  },
  {
    match: /java|spring|maven|gradle/,
    label: 'Java/backend',
    category: 'backend hoặc full-stack',
    prepare: 'controller, service, repository, DTO/entity và cấu hình database',
    reading: 'Bắt đầu từ controller/API route, rồi lần xuống service, repository và model dữ liệu.',
    config: 'pom.xml, build.gradle, application.yml hoặc cấu hình database',
  },
  {
    match: /python|pandas|jupyter|notebook|machine learning|data/,
    label: 'Python/data',
    category: 'xử lý dữ liệu hoặc AI/ML',
    prepare: 'script/notebook chính, dữ liệu đầu vào, pipeline xử lý và dependency',
    reading: 'Xác định notebook/script chính trước, sau đó đọc phần load data, transform và đánh giá kết quả.',
    config: 'requirements.txt, pyproject.toml, notebook hoặc thư mục data mẫu',
  },
  {
    match: /kotlin|android|compose|flutter|dart|mobile/,
    label: 'mobile',
    category: 'ứng dụng mobile',
    prepare: 'navigation, màn hình chính, ViewModel/state và lớp gọi API/local storage',
    reading: 'Bắt đầu từ navigation graph hoặc màn hình entry, rồi đọc state holder và repository gọi dữ liệu.',
    config: 'build.gradle, pubspec.yaml hoặc manifest/config của app',
  },
  {
    match: /c\+\+|(^|\s)c(\s|$)|rust|system|embedded|operating/,
    label: 'system/programming fundamentals',
    category: 'lập trình hệ thống',
    prepare: 'entrypoint, cấu trúc module, thuật toán chính, memory/input-output và cách build',
    reading: 'Tìm main/entrypoint, đọc luồng input-output, sau đó kiểm tra thuật toán và cấu trúc dữ liệu.',
    config: 'Makefile, CMakeLists.txt hoặc hướng dẫn build trong README',
  },
]

export function buildRepoAiAnalysisSections(repo: RepoSummary): RepoAiAnalysisSection[] {
  const signals = getRepoSignals(repo)
  const assessment = assessRepository(signals)
  const techProfile = getTechnologyProfile(signals)
  const sections: RepoAiAnalysisSection[] = [
    {
      key: 'overview',
      title: 'Tổng quan',
      tone: 'accent',
      content: buildOverview(signals, assessment),
      items: [
        signals.description
          ? `Mô tả repo cho biết trọng tâm là: ${signals.description}`
          : 'Repo chưa có description trong dữ liệu DevOrbit, nên phần tổng quan chỉ dựa trên tên và metadata còn lại.',
        signals.topics.length > 0
          ? `Topics/tags gợi ý phạm vi: ${signals.topics.join(', ')}.`
          : 'Chưa có topics/tags để nhận diện rõ domain hoặc mục tiêu repo.',
        `Mức độ đầy đủ thông tin: ${assessment.completeness} (${assessment.evidenceCount} nhóm tín hiệu dùng được).`,
      ],
    },
    {
      key: 'technology',
      title: 'Công nghệ chính',
      tone: 'indigo',
      content: buildTechnologyContent(signals, techProfile),
      items: [
        signals.techStacks.length > 0
          ? `Tech stack đang có: ${signals.techStacks.join(', ')}.`
          : 'Chưa có tech stack chi tiết ngoài primary language.',
        techProfile
          ? `Người học nên chuẩn bị: ${techProfile.prepare}.`
          : 'Người học nên xác định entrypoint, dependency và cách build trước khi đọc sâu.',
        techProfile
          ? `File nên kiểm tra sớm: ${techProfile.config}.`
          : 'File nên kiểm tra sớm: README, thư mục src/docs và file cấu hình dependency nếu có.',
      ],
    },
    {
      key: 'fit',
      title: 'Mức độ phù hợp',
      tone: assessment.learningFit === 'cao' ? 'accent' : assessment.learningFit === 'vừa' ? 'amber' : 'rose',
      content: buildFitContent(signals, assessment),
      items: [
        `Mức độ phù hợp học tập: ${assessment.learningFit}.`,
        signals.courseLabel
          ? `Ngữ cảnh môn học: ${signals.courseLabel}.`
          : 'Chưa có courseCode/courseName nên cần tự đối chiếu repo với môn học UIT đang làm.',
        buildDeadlineHint(signals, assessment),
      ],
    },
    {
      key: 'readmeInsights',
      title: 'README insights',
      tone: signals.readmeExcerpt ? 'accent' : 'amber',
      content: buildReadmeInsightsContent(signals),
      items: getReadmeInsightItems(signals),
    },
    {
      key: 'reviewFirst',
      title: 'Điểm nên xem trước',
      tone: 'amber',
      content: buildReviewFirstContent(signals, assessment),
      items: getReviewFirstItems(signals, techProfile),
    },
    {
      key: 'strategy',
      title: 'Chiến lược học tập',
      tone: 'indigo',
      content: buildStrategyContent(assessment, techProfile),
      items: getLearningStrategyItems(signals, assessment, techProfile),
    },
    {
      key: 'nextSteps',
      title: 'Bước tiếp theo',
      tone: 'accent',
      content: buildNextStepsContent(signals, assessment),
      items: getNextStepItems(signals, assessment, techProfile),
    },
  ]

  if (assessment.warnings.length > 0) {
    sections.push({
      key: 'warnings',
      title: 'Cảnh báo dữ liệu',
      tone: 'rose',
      content: assessment.completeness === 'ít'
        ? 'Chưa đủ dữ liệu để phân tích sâu. Repo này chỉ nên dùng để định hướng ban đầu cho đến khi bạn kiểm tra README/source trực tiếp.'
        : 'Một số tín hiệu còn thiếu, nên kết luận dưới đây vẫn cần được kiểm chứng trên GitHub trước khi dùng cho bài tập hoặc deadline.',
      items: assessment.warnings,
    })
  }

  return sections
}

function getRepoSignals(repo: RepoSummary): RepoSignals {
  const metadata = repo as RepoSummary & OptionalRepoMetadata
  return {
    name: cleanText(repo.displayName) || `Repo #${repo.id}`,
    description: cleanText(repo.description),
    language: cleanText(repo.primaryLanguage),
    techStacks: Array.from(new Set((repo.techStacks ?? []).map(cleanText).filter(Boolean) as string[])),
    topics: normalizeList(metadata.topics ?? metadata.tags),
    readmeExcerpt: cleanText(metadata.readmeExcerpt ?? metadata.readmeContent ?? metadata.readmeMarkdown ?? metadata.readmeText ?? metadata.readme),
    courseLabel: formatCourseLabel(repo),
    stars: typeof repo.stars === 'number' ? repo.stars : null,
    forks: typeof metadata.forks === 'number' ? metadata.forks : null,
    updatedAt: formatVietnameseRelativeDate(metadata.lastPushedAt ?? metadata.updatedAt),
    deadline: cleanText(metadata.deadline),
  }
}

function cleanText(value: string | null | undefined): string | null {
  const normalized = value?.replace(/\s+/g, ' ').trim()
  return normalized ? normalized : null
}

function normalizeList(value: string[] | string | null | undefined): string[] {
  if (!value) return []
  const rawValues = Array.isArray(value) ? value : value.split(/[,;|]/)
  return Array.from(new Set(rawValues.map(cleanText).filter(Boolean) as string[]))
}

function formatCourseLabel(repo: RepoSummary): string | null {
  const code = cleanText(repo.courseCode)
  const name = cleanText(repo.courseName)
  if (code && name) return `môn ${code} - ${name}`
  if (code) return `môn ${code}`
  if (name) return `môn ${name}`
  return null
}

function assessRepository(signals: RepoSignals): RepoAssessment {
  const evidence = [
    signals.description,
    signals.language,
    signals.techStacks.length > 0 ? signals.techStacks.join(',') : null,
    signals.topics.length > 0 ? signals.topics.join(',') : null,
    signals.readmeExcerpt,
    signals.courseLabel,
    signals.stars !== null ? String(signals.stars) : null,
    signals.forks !== null ? String(signals.forks) : null,
    signals.updatedAt,
  ].filter(Boolean)

  const missingSignals: string[] = []
  const warnings: string[] = []
  if (!signals.description) {
    missingSignals.push('description')
    warnings.push('Thiếu mô tả nên chưa xác định chắc repo đang giải quyết bài toán gì.')
  }
  if (!signals.language) {
    missingSignals.push('primaryLanguage')
    warnings.push('Chưa rõ công nghệ chính vì thiếu primaryLanguage.')
  }
  if (signals.techStacks.length === 0) {
    missingSignals.push('techStacks')
    warnings.push('Thiếu techStacks nên cần mở source/config để xác nhận framework và dependency.')
  }
  if (signals.topics.length === 0) {
    missingSignals.push('topics')
    warnings.push('Thiếu topics/tags nên khó nhận diện domain hoặc mục tiêu repo từ metadata.')
  }
  if (!signals.readmeExcerpt) {
    missingSignals.push('README')
    warnings.push('Thiếu README excerpt; hãy đọc README trên GitHub trước khi dùng repo cho deadline.')
  }
  if (!signals.courseLabel) {
    missingSignals.push('course')
    warnings.push('Thiếu courseCode/courseName nên chưa đánh giá được mức độ khớp với môn học UIT cụ thể.')
  }
  if (signals.forks === null) warnings.push('Chưa có forks để tham khảo mức độ được tái sử dụng.')
  if (!signals.updatedAt) warnings.push('Chưa có updatedAt/lastPushedAt nên chưa biết repo còn được duy trì gần đây hay không.')

  const completeness: RepoAssessment['completeness'] =
    evidence.length >= 7 ? 'đầy đủ' : evidence.length >= 4 ? 'trung bình' : 'ít'

  const learningScore = [
    Boolean(signals.description),
    Boolean(signals.language),
    signals.techStacks.length > 0 || signals.topics.length > 0,
    Boolean(signals.readmeExcerpt),
    Boolean(signals.courseLabel),
  ].filter(Boolean).length

  const hasDeepEvidence = Boolean(signals.readmeExcerpt) || signals.topics.length > 0
  const learningFit: RepoAssessment['learningFit'] =
    learningScore >= 4 && hasDeepEvidence ? 'cao' : learningScore >= 2 ? 'vừa' : 'thấp'

  return {
    category: inferRepoCategory(signals),
    completeness,
    learningFit,
    evidenceCount: evidence.length,
    missingSignals,
    warnings,
  }
}

function inferRepoCategory(signals: RepoSignals): string {
  const haystack = [
    signals.name,
    signals.description,
    signals.language,
    signals.readmeExcerpt,
    ...signals.techStacks,
    ...signals.topics,
  ].filter(Boolean).join(' ').toLowerCase()

  return getTechnologyProfileFromText(haystack)?.category ?? 'phần mềm học thuật'
}

function getTechnologyProfile(signals: RepoSignals) {
  const haystack = [signals.language, ...signals.techStacks, ...signals.topics].filter(Boolean).join(' ').toLowerCase()
  return getTechnologyProfileFromText(haystack)
}

function getTechnologyProfileFromText(haystack: string) {
  return languagePreparation.find((profile) => profile.match.test(haystack))
}

function buildOverview(signals: RepoSignals, assessment: RepoAssessment): string {
  if (signals.description) {
    const domainHint = signals.topics.length > 0
      ? ` Topics/tags (${signals.topics.slice(0, 3).join(', ')}) củng cố hướng đọc này.`
      : ''
    return `${signals.name} được mô tả là "${signals.description}", nên analysis xem repo này như một ví dụ về ${assessment.category}.${domainHint} Với sinh viên KTPM UIT, nên đọc repo để hiểu bài toán và cách triển khai, không xem đây là lời giải hoàn chỉnh để sao chép.`
  }

  if (signals.readmeExcerpt) {
    return `${signals.name} chưa có description, nhưng README excerpt có dữ liệu nên có thể dùng README làm nguồn xác định mục tiêu trước. Analysis vẫn cần thận trọng vì thiếu mô tả ngắn từ GitHub.`
  }

  return `${signals.name} hiện thiếu description và README excerpt trong dữ liệu DevOrbit. Chưa đủ dữ liệu để phân tích sâu; chỉ nên xem metadata còn lại như tín hiệu ban đầu rồi mở GitHub để kiểm tra README, source và cấu trúc thư mục.`
}

function buildTechnologyContent(signals: RepoSignals, techProfile: ReturnType<typeof getTechnologyProfile>): string {
  if (signals.language) {
    const stackText = signals.techStacks.length > 0
      ? ` Kết hợp với tech stack ${signals.techStacks.join(', ')}, repo có dấu hiệu thuộc nhóm ${techProfile?.label ?? 'công nghệ cần kiểm tra thêm'}.`
      : ' Chưa có tech stack phụ trợ, nên chưa kết luận chắc framework.'
    return `GitHub ghi nhận ngôn ngữ chính là ${signals.language}.${stackText}`
  }

  if (signals.techStacks.length > 0) {
    return `Repo chưa có primaryLanguage, nhưng tech stack ${signals.techStacks.join(', ')} vẫn cho tín hiệu ban đầu về cách đọc source. Cần mở file cấu hình để xác nhận ngôn ngữ và cách chạy.`
  }

  return 'Chưa có primaryLanguage hoặc techStacks, nên không nên suy đoán stack. Hãy xác nhận bằng README, thư mục src và file cấu hình trước.'
}

function buildFitContent(signals: RepoSignals, assessment: RepoAssessment): string {
  if (assessment.learningFit === 'cao') {
    return `Repo có đủ tín hiệu để dùng làm tài liệu học tập: có ngữ cảnh ${signals.courseLabel ?? 'môn học'}, mô tả/stack rõ và có thêm dữ liệu hỗ trợ. Phù hợp để đọc trước khi làm bài tập hoặc đồ án, miễn là bạn kiểm chứng README và license trên GitHub.`
  }

  if (assessment.learningFit === 'vừa') {
    return `Repo có thể dùng để tham khảo ${assessment.category}, nhưng còn thiếu ${assessment.missingSignals.slice(0, 3).join(', ')}. Với deadline gấp, chỉ nên dùng sau khi xác nhận cách chạy và phạm vi bài toán.`
  }

  return 'Mức phù hợp học tập hiện thấp vì metadata quá ít. Repo này nên được xem như nguồn tham khảo ý tưởng, chưa nên dùng trực tiếp cho bài tập/deadline cho đến khi kiểm tra README và source.'
}

function buildDeadlineHint(signals: RepoSignals, assessment: RepoAssessment): string {
  if (signals.deadline) return `Deadline liên quan: ${signals.deadline}; ưu tiên kiểm tra khả năng chạy được trước khi đọc tối ưu/refactor.`
  if (assessment.learningFit === 'cao') return 'Có thể đưa vào danh sách tham khảo cho deadline nếu README có setup rõ và source chạy được.'
  if (!signals.readmeExcerpt) return 'Không nên chọn làm nguồn chính cho deadline khi chưa đọc README/source trực tiếp.'
  return 'Có thể đọc để lấy ý tưởng, nhưng cần kiểm tra setup và phạm vi trước khi dùng cho deadline.'
}

function buildReviewFirstContent(signals: RepoSignals, assessment: RepoAssessment): string {
  if (signals.readmeExcerpt) {
    return 'README excerpt đang có dữ liệu, nên hãy dùng README để xác nhận mục tiêu, setup và cách chạy trước khi đi vào source.'
  }

  if (assessment.completeness === 'ít') {
    return 'Dữ liệu repo còn ít, nên bước đầu tiên không phải đọc code ngay mà là xác minh repo có README, src và config đủ rõ hay không.'
  }

  return 'Trước khi đọc sâu, hãy kiểm tra README, source chính và file cấu hình để biến metadata thành kế hoạch đọc cụ thể.'
}

function buildReadmeInsightsContent(signals: RepoSignals): string {
  if (!signals.readmeExcerpt) {
    return 'Chưa có README để phân tích sâu. DevOrbit chưa thể nhận diện mục tiêu, lệnh setup/run hoặc cấu trúc đọc từ README, nên bạn cần kiểm tra source chính, description và topics trước.'
  }

  const goal = summarizeReadmeGoal(signals)
  const commands = detectSetupCommands(signals.readmeExcerpt)
  const keywords = detectReadmeKeywords(signals)
  const details: string[] = []

  if (goal) details.push(`README gợi ý mục tiêu: ${goal}`)
  if (commands.length > 0) details.push(`Có tín hiệu setup/run như ${commands.slice(0, 3).join(', ')}.`)
  if (keywords.length > 0) details.push(`Keyword nổi bật: ${keywords.slice(0, 5).join(', ')}.`)

  return details.length > 0
    ? details.join(' ')
    : 'README có dữ liệu nhưng chưa đủ rõ để rút ra mục tiêu, setup hoặc cấu trúc cụ thể. Hãy đọc README đầy đủ trên GitHub trước khi dùng repo cho deadline.'
}

function getReadmeInsightItems(signals: RepoSignals): string[] {
  if (!signals.readmeExcerpt) {
    return [
      'Chưa có README để phân tích sâu.',
      signals.description
        ? `Dùng description làm tín hiệu tạm thời: ${signals.description}`
        : 'Nếu description cũng thiếu, hãy mở GitHub và kiểm tra thư mục src hoặc file cấu hình chính.',
      signals.topics.length > 0
        ? `Có thể đối chiếu thêm topics/tags: ${signals.topics.join(', ')}.`
        : 'Chưa có topics/tags, nên chưa nhận diện chắc domain từ metadata.',
    ]
  }

  const commands = detectSetupCommands(signals.readmeExcerpt)
  const keywords = detectReadmeKeywords(signals)
  const readFirst = detectReadmeReadingTargets(signals.readmeExcerpt)
  const items = [
    `Mục tiêu đọc được từ README: ${summarizeReadmeGoal(signals) ?? 'README có nội dung nhưng mục tiêu chưa được viết rõ trong excerpt.'}`,
  ]

  items.push(commands.length > 0
    ? `Lệnh setup/run phát hiện: ${commands.join(', ')}.`
    : 'Chưa phát hiện lệnh setup/run rõ như npm install, docker compose, mvn, gradle, dotnet, pip hoặc python.')
  items.push(keywords.length > 0
    ? `Keyword quan trọng: ${keywords.join(', ')}.`
    : 'Chưa phát hiện keyword kỹ thuật nổi bật ngoài metadata hiện có.')
  items.push(readFirst.length > 0
    ? `README nhắc tới phần nên đọc/kiểm tra: ${readFirst.join(', ')}.`
    : 'README excerpt chưa nhắc rõ cấu trúc như src, docs, tests hoặc file cấu hình.')

  return items
}

function summarizeReadmeGoal(signals: RepoSignals): string | null {
  if (!signals.readmeExcerpt) return null

  const withoutCodeFence = signals.readmeExcerpt.replace(/```[\s\S]*?```/g, ' ')
  const heading = withoutCodeFence.match(/(?:^|\s)#{1,3}\s+([^#.!?]{4,120})/)
  if (heading?.[1]) return truncate(cleanText(heading[1]) ?? heading[1], 140)

  const sentences = withoutCodeFence
    .split(/(?<=[.!?])\s+/)
    .map(cleanText)
    .filter(Boolean) as string[]
  const usefulSentence = sentences.find((sentence) => {
    const lower = sentence.toLowerCase()
    return sentence.length >= 24 && !/(npm|yarn|pnpm|docker|mvn|gradle|pip install|clone|cd\s)/.test(lower)
  })

  return usefulSentence ? truncate(usefulSentence, 180) : null
}

function detectSetupCommands(readme: string): string[] {
  const commandPatterns = [
    /npm\s+install/g,
    /npm\s+run\s+[a-z0-9:_-]+/gi,
    /yarn\s+(?:install|dev|start|build|test)/gi,
    /pnpm\s+(?:install|dev|start|build|test)/gi,
    /bun\s+(?:install|dev|start|run|test)/gi,
    /docker\s+compose\s+up(?:\s+--build)?/gi,
    /docker-compose\s+up(?:\s+--build)?/gi,
    /docker\s+build\s+[^.;,)]+/gi,
    /dotnet\s+(?:restore|build|run|test)/gi,
    /mvnw?(?:\.cmd)?\s+[a-z0-9:_-]+/gi,
    /gradlew?(?:\.bat)?\s+[a-z0-9:_-]+/gi,
    /pip\s+install\s+[^.;,)]+/gi,
    /python(?:3)?\s+[a-z0-9_./-]+\.py/gi,
  ]

  return uniqueMatches(readme, commandPatterns).slice(0, 6)
}

function detectReadmeKeywords(signals: RepoSignals): string[] {
  const haystack = [signals.readmeExcerpt, signals.description, signals.language, ...signals.techStacks, ...signals.topics]
    .filter(Boolean)
    .join(' ')
    .toLowerCase()
  const candidates = [
    'react',
    'vite',
    'typescript',
    'spring boot',
    'postgresql',
    'mysql',
    'docker',
    'api',
    'rest',
    'jwt',
    'authentication',
    'machine learning',
    'jupyter',
    'android',
    'kotlin',
    'compose',
    'flutter',
    'clean architecture',
    'microservice',
  ]

  return candidates.filter((keyword) => haystack.includes(keyword)).slice(0, 8)
}

function detectReadmeReadingTargets(readme: string): string[] {
  const targets: Array<[RegExp, string]> = [
    [/\bsrc\b/i, 'thư mục src'],
    [/\bdocs?\b/i, 'docs'],
    [/\btests?\b|\b__tests__\b/i, 'test/example'],
    [/\bpackage\.json\b/i, 'package.json'],
    [/\bpom\.xml\b/i, 'pom.xml'],
    [/\bbuild\.gradle\b|\bgradle\b/i, 'Gradle config'],
    [/\bDockerfile\b|\bdocker-compose\b|\bdocker compose\b/i, 'Docker config'],
    [/\bcontrollers?\b/i, 'controller/API layer'],
    [/\bservices?\b/i, 'service layer'],
    [/\bcomponents?\b/i, 'components'],
    [/\bnotebooks?\b|\b\.ipynb\b/i, 'notebook'],
  ]

  return targets
    .filter(([pattern]) => pattern.test(readme))
    .map(([, label]) => label)
    .slice(0, 6)
}

function uniqueMatches(value: string, patterns: RegExp[]): string[] {
  const seen = new Set<string>()
  const matches: string[] = []
  for (const pattern of patterns) {
    for (const match of value.matchAll(pattern)) {
      const normalized = cleanText(match[0])
      if (!normalized) continue
      const key = normalized.toLowerCase()
      if (seen.has(key)) continue
      seen.add(key)
      matches.push(normalized)
    }
  }
  return matches
}

function getReviewFirstItems(signals: RepoSignals, techProfile: ReturnType<typeof getTechnologyProfile>): string[] {
  const items = [
    signals.readmeExcerpt
      ? `README: ${truncate(signals.readmeExcerpt, 180)}`
      : 'README trên GitHub: mục tiêu repo, cách setup, cách chạy và ví dụ sử dụng.',
    'Thư mục src hoặc module chính để xác định repo là demo nhỏ, bài tập hay project nhiều lớp.',
  ]

  if (techProfile) items.push(techProfile.reading)
  if (signals.language) items.push(`Entrypoint hoặc file build liên quan đến ${signals.language}.`)
  if (signals.techStacks.length > 0) items.push(`Dependency/framework của ${signals.techStacks.slice(0, 3).join(', ')}.`)
  if (signals.updatedAt) items.push(`Lịch sử cập nhật gần nhất: ${signals.updatedAt}.`)
  else items.push('Commit/release gần đây để biết repo còn đáng dùng hay chỉ nên tham khảo.')

  return items
}

function buildStrategyContent(assessment: RepoAssessment, techProfile: ReturnType<typeof getTechnologyProfile>): string {
  const base = techProfile
    ? `Với repo ${techProfile.label}, cách đọc hiệu quả là ${techProfile.reading.toLowerCase()}`
    : 'Cách đọc hiệu quả là đi từ README, entrypoint, dependency rồi mới đến từng module.'

  if (assessment.learningFit === 'thấp') {
    return `${base} Vì dữ liệu còn ít, sinh viên KTPM UIT nên ghi rõ giả định và không dùng repo như nguồn chính cho deadline.`
  }

  return `${base} Sau đó đối chiếu từng phần code với kiến thức môn học để rút ra pattern có thể áp dụng, thay vì sao chép nguyên project.`
}

function getLearningStrategyItems(
  signals: RepoSignals,
  assessment: RepoAssessment,
  techProfile: ReturnType<typeof getTechnologyProfile>,
): string[] {
  const items = [
    signals.description
      ? 'Viết lại mục tiêu repo bằng lời của bạn dựa trên description/README trước khi đọc code.'
      : 'Tự xác định mục tiêu repo từ README/source vì description đang thiếu.',
    'Vẽ nhanh luồng chính: input, xử lý, output và module chịu trách nhiệm.',
  ]

  if (signals.courseLabel) items.push(`Đối chiếu từng phần code với kiến thức trong ${signals.courseLabel}.`)
  else items.push('Tự gắn repo với chủ đề học phần phù hợp trước khi đọc sâu.')

  if (techProfile) items.push(`Chuẩn bị kiến thức nền: ${techProfile.prepare}.`)
  if (assessment.completeness === 'ít') items.push('Ghi lại phần dữ liệu còn thiếu để tránh suy luận quá mức khi dùng repo làm tài liệu học.')

  items.push('Chạy thử hoặc đọc test/example trước khi sửa code.')
  return items
}

function buildNextStepsContent(signals: RepoSignals, assessment: RepoAssessment): string {
  if (assessment.completeness === 'ít') {
    return 'Bước tiếp theo là bổ sung bằng chứng từ GitHub trước: README, source chính và file config. Khi chưa có các dữ liệu này, repo chỉ nên dùng để khảo sát nhanh.'
  }

  return `Sau khi hiểu metadata của ${signals.name}, hãy kiểm chứng trực tiếp trên GitHub/local để quyết định repo có dùng được cho bài tập, đồ án hoặc deadline hay không.`
}

function getNextStepItems(
  signals: RepoSignals,
  assessment: RepoAssessment,
  techProfile: ReturnType<typeof getTechnologyProfile>,
): string[] {
  const items = [
    'Mở repo trên GitHub và đọc README trước.',
    techProfile ? `Kiểm tra ${techProfile.config}.` : 'Kiểm tra file dependency/config để biết cách build/run.',
    'Tìm thư mục src, docs và test/example nếu có.',
  ]

  if (assessment.learningFit === 'cao') {
    items.push('Clone/fork một nhánh nhỏ để chạy thử và ghi lại pattern học được cho môn học.')
  } else {
    items.push('Nếu README hoặc setup thiếu, chỉ dùng repo để tham khảo ý tưởng và tìm thêm repo đầy đủ hơn.')
  }

  if (signals.stars !== null) items.push(`Stars hiện có: ${signals.stars}; chỉ xem như tín hiệu phụ, không thay thế việc đọc code.`)
  if (signals.forks !== null) items.push(`Forks hiện có: ${signals.forks}; có thể tham khảo mức độ được tái sử dụng.`)

  return items
}

function truncate(value: string, maxLength: number): string {
  if (value.length <= maxLength) return value
  return `${value.slice(0, maxLength - 1).trim()}…`
}
