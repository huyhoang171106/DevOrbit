import type { RepoSummary } from '../types/api'

export type RepoAiAnalysisTone = 'accent' | 'indigo' | 'amber' | 'rose'

export type RepoAiAnalysisSection = {
  key: 'overview' | 'technology' | 'fit' | 'reviewFirst' | 'strategy' | 'nextSteps' | 'warnings'
  title: string
  content: string
  tone: RepoAiAnalysisTone
  items?: string[]
}

type RepoSignals = {
  name: string
  description: string | null
  language: string | null
  techStacks: string[]
  courseLabel: string | null
  stars: number | null
}

export function buildRepoAiAnalysisSections(repo: RepoSummary): RepoAiAnalysisSection[] {
  const signals = getRepoSignals(repo)
  const category = inferRepoCategory(signals)
  const warnings = getDataWarnings(signals)

  const sections: RepoAiAnalysisSection[] = [
    {
      key: 'overview',
      title: 'Tổng quan',
      tone: 'accent',
      content: signals.description
        ? `Dựa trên mô tả hiện có, ${signals.name} có dấu hiệu là repo ${category}. Repo này nên được đọc như một tài liệu tham khảo có ngữ cảnh, không phải lời giải hoàn chỉnh để sao chép.`
        : `Chưa đủ dữ liệu để phân tích sâu nội dung của ${signals.name}. Có thể bắt đầu từ tên repo và metadata kỹ thuật, nhưng cần mở GitHub để kiểm tra README và cấu trúc source trước khi đánh giá.`,
      items: [
        signals.description ? `Mô tả gốc: ${signals.description}` : 'Thiếu mô tả repository trong dữ liệu public.',
        signals.courseLabel ? `Đang gắn với ${signals.courseLabel}.` : 'Chưa thấy môn học liên kết trong dữ liệu repo.',
      ],
    },
    {
      key: 'technology',
      title: 'Công nghệ chính',
      tone: 'indigo',
      content: signals.language
        ? `Ngôn ngữ chính được GitHub ghi nhận là ${signals.language}. Đây là tín hiệu tốt để chọn cách đọc source và môi trường chạy thử.`
        : 'Chưa có ngôn ngữ chính, nên chưa thể suy luận chắc chắn stack hoặc cách build.',
      items: [
        signals.techStacks.length > 0
          ? `Tech stack liên quan: ${signals.techStacks.join(', ')}.`
          : 'Chưa có tech stack chi tiết ngoài metadata cơ bản.',
        getTechnologyReadingHint(signals),
      ],
    },
    {
      key: 'fit',
      title: 'Mức độ phù hợp',
      tone: 'accent',
      content: getFitContent(signals, category),
      items: [
        getDeadlineHint(signals),
        signals.stars !== null
          ? `Stars hiện tại: ${signals.stars}. Dùng như tín hiệu tham khảo, không thay thế việc đọc code.`
          : 'Chưa có dữ liệu stars.',
      ],
    },
    {
      key: 'reviewFirst',
      title: 'Điểm nên xem trước',
      tone: 'amber',
      content: 'Trước khi đọc sâu, hãy xác nhận repo có đủ hướng dẫn chạy và cấu trúc source rõ ràng. Public data hiện chưa có file tree hoặc README excerpt.',
      items: getReviewFirstItems(signals),
    },
    {
      key: 'strategy',
      title: 'Chiến lược học tập',
      tone: 'indigo',
      content: 'Cách tiếp cận phù hợp là đọc từ tổng quan đến luồng chạy chính, rồi mới đi vào từng module. Với sinh viên KTPM UIT, nên liên hệ code với kiến thức môn học trước khi fork để sửa.',
      items: getLearningStrategyItems(signals),
    },
    {
      key: 'nextSteps',
      title: 'Bước tiếp theo',
      tone: 'accent',
      content: 'Sau khi hiểu metadata, hãy chuyển sang thao tác thực tế trên GitHub/local để kiểm chứng repo có dùng được cho bài tập, đồ án hoặc deadline hay không.',
      items: [
        'Mở repo trên GitHub và đọc README nếu có.',
        'Clone hoặc fork repo nếu README có hướng dẫn chạy rõ ràng.',
        'Ghi lại các module, pattern, hoặc dependency có thể áp dụng cho môn học.',
        'Nếu repo thiếu hướng dẫn, chỉ dùng để tham khảo ý tưởng và tìm repo khác đầy đủ hơn.',
      ],
    },
  ]

  if (warnings.length > 0) {
    sections.push({
      key: 'warnings',
      title: 'Cảnh báo dữ liệu',
      tone: 'rose',
      content: 'Một số dữ liệu cần thiết để phân tích sâu chưa có trong response hiện tại, nên kết luận dưới đây chỉ ở mức định hướng.',
      items: warnings,
    })
  }

  return sections
}

function getRepoSignals(repo: RepoSummary): RepoSignals {
  return {
    name: cleanText(repo.displayName) || `Repo #${repo.id}`,
    description: cleanText(repo.description),
    language: cleanText(repo.primaryLanguage),
    techStacks: Array.from(new Set((repo.techStacks ?? []).map(cleanText).filter(Boolean) as string[])),
    courseLabel: formatCourseLabel(repo),
    stars: typeof repo.stars === 'number' ? repo.stars : null,
  }
}

function cleanText(value: string | null | undefined): string | null {
  const normalized = value?.trim()
  return normalized ? normalized : null
}

function formatCourseLabel(repo: RepoSummary): string | null {
  const code = cleanText(repo.courseCode)
  const name = cleanText(repo.courseName)
  if (code && name) return `môn ${code} - ${name}`
  if (code) return `môn ${code}`
  if (name) return `môn ${name}`
  return null
}

function inferRepoCategory(signals: RepoSignals): string {
  const haystack = [signals.name, signals.description, signals.language, ...signals.techStacks]
    .filter(Boolean)
    .join(' ')
    .toLowerCase()

  if (/(android|kotlin|compose|mobile|flutter|dart|swift)/.test(haystack)) return 'ứng dụng mobile'
  if (/(react|vue|angular|frontend|ui|tailwind|vite|typescript|javascript)/.test(haystack)) return 'frontend/web UI'
  if (/(spring|java|api|backend|server|node|express|\.net|postgres|mysql)/.test(haystack)) return 'backend hoặc full-stack'
  if (/(machine learning|data|pandas|jupyter|python|notebook|analytics)/.test(haystack)) return 'xử lý dữ liệu hoặc AI/ML'
  if (/(c\+\+| c |rust|system|embedded|operating)/.test(` ${haystack} `)) return 'lập trình hệ thống'
  return 'phần mềm học thuật'
}

function getTechnologyReadingHint(signals: RepoSignals): string {
  const language = signals.language?.toLowerCase() ?? ''
  const stackText = signals.techStacks.join(' ').toLowerCase()
  const combined = `${language} ${stackText}`

  if (/react|vue|angular|typescript|javascript/.test(combined)) {
    return 'Khi đọc repo web, ưu tiên xác định component chính, routing, state/data flow và API client.'
  }
  if (/java|spring/.test(combined)) {
    return 'Khi đọc repo Java/Spring, ưu tiên controller/service/repository, model dữ liệu và cấu hình chạy.'
  }
  if (/python|pandas|jupyter/.test(combined)) {
    return 'Khi đọc repo Python/data, ưu tiên notebook/script chính, input data, pipeline xử lý và dependency.'
  }
  if (/kotlin|android|compose|flutter|dart/.test(combined)) {
    return 'Khi đọc repo mobile, ưu tiên màn hình chính, navigation, ViewModel/state và lớp gọi API.'
  }
  return 'Ưu tiên xác định entrypoint, dependency và cách build trước khi đọc chi tiết từng file.'
}

function getFitContent(signals: RepoSignals, category: string): string {
  if (!signals.courseLabel) {
    return `Repo có thể hữu ích để tham khảo ${category}, nhưng chưa đủ dữ liệu để kết luận mức độ phù hợp với một môn học cụ thể.`
  }

  if (!signals.description && !signals.language && signals.techStacks.length === 0) {
    return `Repo đang gắn với ${signals.courseLabel}, nhưng metadata quá ít nên chưa nên dùng làm nguồn chính cho deadline.`
  }

  return `Repo phù hợp để tham khảo khi học ${signals.courseLabel}, đặc biệt nếu bài tập hoặc đồ án cần ví dụ về ${category}. Với deadline gấp, chỉ nên dùng sau khi kiểm tra README và khả năng chạy được project.`
}

function getDeadlineHint(signals: RepoSignals): string {
  if (!signals.description) return 'Không nên chọn repo này làm nguồn chính cho deadline nếu chưa kiểm tra README/source trên GitHub.'
  if (!signals.language && signals.techStacks.length === 0) return 'Cần xác nhận stack trước khi dùng cho deadline để tránh mất thời gian setup.'
  return 'Có thể dùng làm tài liệu tham khảo cho deadline nếu README, license và cấu trúc source rõ ràng.'
}

function getReviewFirstItems(signals: RepoSignals): string[] {
  const items = [
    'README hoặc phần hướng dẫn chạy, vì public data hiện chưa cho biết README có tồn tại hay không.',
    'Cấu trúc thư mục để biết repo là bài tập nhỏ, demo, hay project nhiều module.',
  ]

  if (signals.language) items.push(`File entrypoint hoặc cấu hình build của ${signals.language}.`)
  if (signals.techStacks.length > 0) items.push(`Dependency/framework liên quan đến ${signals.techStacks.slice(0, 3).join(', ')}.`)
  items.push('Issues/commits gần đây nếu cần đánh giá repo còn duy trì hay không.')

  return items
}

function getLearningStrategyItems(signals: RepoSignals): string[] {
  const items = [
    'Đọc mô tả và README để viết lại mục tiêu repo bằng lời của bạn.',
    'Vẽ nhanh luồng chính: input, xử lý, output, module nào chịu trách nhiệm.',
  ]

  if (signals.courseLabel) {
    items.push(`Đối chiếu từng phần code với kiến thức trong ${signals.courseLabel}.`)
  } else {
    items.push('Tự gắn repo với chủ đề học phần phù hợp trước khi đọc sâu.')
  }

  items.push('Chạy thử hoặc đọc test/example trước khi sửa code.')
  items.push('Fork một nhánh nhỏ để thử thay đổi một chức năng, tránh sửa trực tiếp vào bản tham khảo.')

  return items
}

function getDataWarnings(signals: RepoSignals): string[] {
  const warnings: string[] = []

  if (!signals.description) warnings.push('Thiếu description nên chưa xác định chắc repo đang giải quyết bài toán gì.')
  if (!signals.language) warnings.push('Thiếu primaryLanguage nên chưa thể suy luận chắc cách build/chạy.')
  if (signals.techStacks.length === 0) warnings.push('Thiếu techStacks nên phân tích công nghệ chỉ dựa trên metadata tối thiểu.')
  if (!signals.courseLabel) warnings.push('Thiếu courseCode/courseName nên chưa đánh giá được mức độ khớp với môn học UIT cụ thể.')
  warnings.push('Public repo detail hiện chưa có README excerpt, topics, forks hoặc last pushed date.')

  return warnings
}
