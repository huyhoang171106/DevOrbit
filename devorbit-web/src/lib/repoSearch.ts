import type { CourseSummary, RepoSummary } from '../types/api'

export type { CourseSummary, RepoSummary }

function normalizeText(text: string): string {
  return text
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'd')
    .toLowerCase()
    .trim()
}

export function tokenizeSearchQuery(query: string): string[] {
  return normalizeText(query).split(/\s+/).filter(Boolean)
}

function escapeRegex(s: string): string {
  return s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

const expansionMap: Record<string, string[]> = {
  java: ['java', 'spring', 'spring boot', 'swing', 'javafx', 'maven', 'gradle', 'jdk'],
  'lap trinh': ['lập trình', 'programming', 'code', 'lab', 'exercise', 'bài tập', 'thực hành', 'c++', 'java', 'python', 'cấu trúc dữ liệu', 'giải thuật'],
  'lập trình': ['lập trình', 'programming', 'code', 'lab', 'exercise', 'bài tập', 'thực hành', 'c++', 'java', 'python', 'cấu trúc dữ liệu', 'giải thuật'],
  'do an': ['đồ án', 'project', 'capstone', 'thesis', 'khóa luận', 'fullstack', 'demo', 'deployment'],
  'đồ án': ['đồ án', 'project', 'capstone', 'thesis', 'khóa luận', 'fullstack', 'demo', 'deployment'],
  web: ['web', 'frontend', 'backend', 'html', 'css', 'javascript', 'typescript', 'react', 'vue', 'angular', 'node', 'express', 'rest api', 'fullstack'],
  mobile: ['mobile', 'android', 'ios', 'flutter', 'react native', 'kotlin', 'swift', 'app'],
  'di động': ['mobile', 'android', 'ios', 'flutter', 'react native', 'kotlin', 'swift', 'app'],
  'co so du lieu': ['cơ sở dữ liệu', 'database', 'sql', 'mysql', 'postgresql', 'mongodb', 'erd', 'query'],
  'cơ sở dữ liệu': ['cơ sở dữ liệu', 'database', 'sql', 'mysql', 'postgresql', 'mongodb', 'erd', 'query'],
  csdl: ['cơ sở dữ liệu', 'database', 'sql', 'mysql', 'postgresql', 'mongodb', 'erd', 'query'],
  database: ['cơ sở dữ liệu', 'database', 'sql', 'mysql', 'postgresql', 'mongodb', 'erd', 'query'],
  'thiet ke': ['thiết kế', 'design', 'srs', 'uml', 'use case', 'erd', 'ooad', 'figma', 'prototype'],
  'thiết kế': ['thiết kế', 'design', 'srs', 'uml', 'use case', 'erd', 'ooad', 'figma', 'prototype'],
  'kiem thu': ['kiểm thử', 'testing', 'unit test', 'test case', 'selenium', 'junit', 'mockito'],
  'kiểm thử': ['kiểm thử', 'testing', 'unit test', 'test case', 'selenium', 'junit', 'mockito'],
  testing: ['kiểm thử', 'testing', 'unit test', 'test case', 'selenium', 'junit', 'mockito'],
  hci: ['hci', 'ui', 'ux', 'figma', 'prototype', 'wireframe', 'usability', 'giao diện người dùng'],
  'giao diện': ['hci', 'ui', 'ux', 'figma', 'prototype', 'wireframe', 'usability', 'giao diện người dùng'],
  ai: ['ai', 'machine learning', 'ml', 'data', 'python', 'notebook', 'deep learning', 'nlp'],
  data: ['data', 'machine learning', 'ml', 'python', 'notebook', 'dataset', 'deep learning'],
  'xac suat': ['xác suất', 'thống kê', 'probability', 'statistics', 'regression', 'data analysis'],
  'xác suất': ['xác suất', 'thống kê', 'probability', 'statistics', 'regression', 'data analysis'],
  'thong ke': ['xác suất', 'thống kê', 'probability', 'statistics', 'regression', 'data analysis'],
  'thống kê': ['xác suất', 'thống kê', 'probability', 'statistics', 'regression', 'data analysis'],
  'mang': ['mạng', 'network', 'socket', 'tcp', 'udp', 'http', 'routing', 'mạng máy tính'],
  'mạng': ['mạng', 'network', 'socket', 'tcp', 'udp', 'http', 'routing', 'mạng máy tính'],
  network: ['mạng', 'network', 'socket', 'tcp', 'udp', 'http', 'routing', 'mạng máy tính'],
  'bao mat': ['bảo mật', 'security', 'cybersecurity', 'encryption', 'authentication', 'authorization'],
  'bảo mật': ['bảo mật', 'security', 'cybersecurity', 'encryption', 'authentication', 'authorization'],
  security: ['bảo mật', 'security', 'cybersecurity', 'encryption', 'authentication', 'authorization'],
  'he dieu hanh': ['hệ điều hành', 'operating system', 'os', 'process', 'thread', 'memory', 'scheduling'],
  'hệ điều hành': ['hệ điều hành', 'operating system', 'os', 'process', 'thread', 'memory', 'scheduling'],
  os: ['hệ điều hành', 'operating system', 'os', 'process', 'thread', 'memory', 'scheduling'],
  'kien truc': ['kiến trúc', 'architecture', 'design pattern', 'mvc', 'clean architecture', 'microservices'],
  'kiến trúc': ['kiến trúc', 'architecture', 'design pattern', 'mvc', 'clean architecture', 'microservices'],
  'tieng anh': ['tiếng anh', 'english', 'toeic', 'ielts', 'vocabulary', 'grammar', 'anh văn'],
  'tiếng anh': ['tiếng anh', 'english', 'toeic', 'ielts', 'vocabulary', 'grammar', 'anh văn'],
  english: ['tiếng anh', 'english', 'toeic', 'ielts', 'vocabulary', 'grammar', 'anh văn'],
  'ky nang': ['kỹ năng', 'soft skills', 'presentation', 'report', 'cv', 'teamwork', 'rubric', 'communication', 'ss004'],
  'kỹ năng': ['kỹ năng', 'soft skills', 'presentation', 'report', 'cv', 'teamwork', 'rubric', 'communication', 'ss004'],
  ss004: ['kỹ năng', 'soft skills', 'presentation', 'report', 'cv', 'teamwork', 'rubric', 'communication', 'ss004'],
  'on tap': ['ôn tập', 'đề thi', 'exam', 'final', 'midterm', 'quiz', 'review', 'đáp án', 'lời giải', 'answer key'],
  'ôn tập': ['ôn tập', 'đề thi', 'exam', 'final', 'midterm', 'quiz', 'review', 'đáp án', 'lời giải', 'answer key'],
  'de thi': ['ôn tập', 'đề thi', 'exam', 'final', 'midterm', 'quiz', 'review', 'đáp án', 'lời giải', 'answer key'],
  'đề thi': ['ôn tập', 'đề thi', 'exam', 'final', 'midterm', 'quiz', 'review', 'đáp án', 'lời giải', 'answer key'],
  'tai lieu': ['tài liệu', 'slide', 'document', 'lecture', 'note', 'textbook', 'giáo trình', 'chapter', 'syllabus'],
  'tài liệu': ['tài liệu', 'slide', 'document', 'lecture', 'note', 'textbook', 'giáo trình', 'chapter', 'syllabus'],
  slide: ['tài liệu', 'slide', 'document', 'lecture', 'note', 'textbook', 'giáo trình', 'chapter', 'syllabus'],
  python: ['python', 'django', 'flask', 'notebook', 'jupyter', 'pandas', 'data', 'machine learning'],
  react: ['react', 'reactjs', 'frontend', 'javascript', 'typescript', 'vite', 'nextjs', 'ui'],
  spring: ['spring boot', 'spring', 'java', 'backend', 'rest api', 'jpa', 'hibernate', 'maven'],
  sql: ['sql', 'database', 'mysql', 'postgresql', 'query'],
  frontend: ['frontend', 'web', 'react', 'vue', 'angular', 'html', 'css', 'javascript', 'typescript', 'ui'],
  backend: ['backend', 'spring boot', 'node', 'express', 'api', 'rest', 'database', 'server'],
  oop: ['oop', 'object oriented', 'java', 'c++', 'class', 'inheritance', 'hướng đối tượng'],
  'doi tuong': ['đối tượng', 'object', 'oop', 'object oriented', 'hướng đối tượng', 'java'],
  'đối tượng': ['đối tượng', 'object', 'oop', 'object oriented', 'hướng đối tượng', 'java'],
  'huong doi tuong': ['hướng đối tượng', 'oop', 'object oriented', 'java'],
  'hướng đối tượng': ['hướng đối tượng', 'oop', 'object oriented', 'java'],
  'co so lap trinh': ['cơ sở lập trình', 'programming fundamentals', 'nhập môn lập trình', 'c', 'c++'],
  'cơ sở lập trình': ['cơ sở lập trình', 'programming fundamentals', 'nhập môn lập trình', 'c', 'c++'],
  'nhap mon': ['nhập môn', 'introduction', 'fundamentals', 'cơ bản'],
  'nhập môn': ['nhập môn', 'introduction', 'fundamentals', 'cơ bản'],
  'phan tich': ['phân tích', 'analysis', 'design', 'srs', 'uml', 'requirement', 'ooad'],
  'phân tích': ['phân tích', 'analysis', 'design', 'srs', 'uml', 'requirement', 'ooad'],
  uml: ['uml', 'class diagram', 'sequence diagram', 'use case', 'design', 'ooad'],
  android: ['android', 'mobile', 'kotlin', 'app', 'java'],
  flutter: ['flutter', 'mobile', 'dart', 'app', 'ios', 'android'],
  docker: ['docker', 'container', 'devops', 'deployment'],
  c: ['c', 'c++'],
  'c++': ['c++', 'cpp', 'cplusplus'],
  'c#': ['c#', 'csharp'],
  '.net': ['.net', 'dotnet', 'asp.net'],
  javascript: ['javascript', 'js', 'nodejs', 'react', 'vue', 'angular', 'typescript'],
  typescript: ['typescript', 'ts', 'react', 'angular'],
  kotlin: ['kotlin', 'android', 'kotlin multiplatform'],
  swift: ['swift', 'ios', 'macos'],
  go: ['go', 'golang'],
  rust: ['rust', 'rustlang'],
}

const SPECIFIC_TERMS = new Set([
  'c++', 'c#', 'csharp', 'java', 'python', 'react', 'spring', 'spring boot',
  'vue', 'angular', 'nodejs', 'flutter', 'dart', 'kotlin', 'swift', 'go', 'rust',
  'typescript', 'javascript', 'html', 'css', 'sql', 'mysql', 'postgresql',
  'mongodb', 'redis', 'docker', 'kubernetes', 'aws', 'firebase',
  'figma', 'uml', 'srs', 'erd', 'ooad', 'junit', 'mockito', 'selenium',
  'oop', 'dsa', 'git', 'linux', 'hci', 'rest', 'api',
])

function isSpecificQuery(query: string): boolean {
  const norm = normalizeText(query)
  if (SPECIFIC_TERMS.has(norm)) return true
  if (/^[a-z]{2,4}\d{3}$/.test(norm)) return true
  return false
}

type QueryIntent = 'broad' | 'specific' | 'phrase'

function getQueryIntent(query: string): QueryIntent {
  if (isSpecificQuery(query)) return 'specific'
  const tokens = tokenizeSearchQuery(query)
  if (tokens.length >= 2) return 'phrase'
  return 'broad'
}

function tokensInOrder(text: string, tokens: string[]): boolean {
  let idx = 0
  for (const t of tokens) {
    const found = text.indexOf(t, idx)
    if (found === -1) return false
    idx = found + t.length
  }
  return true
}

function expandKeywords(keyword: string, intent: QueryIntent): string[] {
  const norm = normalizeText(keyword)
  const seen = new Set<string>()
  const result: string[] = []
  const tokens = tokenizeSearchQuery(norm)

  const add = (w: string) => {
    const nw = normalizeText(w)
    if (nw && !seen.has(nw) && nw !== norm) {
      seen.add(nw)
      result.push(nw)
    }
  }

  seen.add(norm)
  result.push(norm)

  if (intent === 'phrase') {
    if (expansionMap[norm]) {
      for (const exp of expansionMap[norm]) add(exp)
    }
    return result
  }

  for (const token of tokens) {
    if (expansionMap[token]) {
      for (const exp of expansionMap[token]) add(exp)
    }
  }
  for (const [key, expansions] of Object.entries(expansionMap)) {
    if (/\s/.test(key) && hasPhrase(norm, key)) {
      for (const exp of expansions) add(exp)
    }
  }
  if (expansionMap[norm]) {
    for (const exp of expansionMap[norm]) add(exp)
  }
  return result
}

function fieldMatchScore(text: string, keyword: string): number {
  const lower = normalizeText(text)
  const kw = normalizeText(keyword)
  if (/^[a-z0-9]+$/i.test(kw)) {
    return new RegExp(`\\b${escapeRegex(kw)}\\b`).test(lower) ? 1 : 0
  }
  return containsText(lower, kw) ? 1 : 0
}

function containsText(value: string, keyword: string): boolean {
  return value.indexOf(keyword) !== -1
}

function hasPhrase(value: string, phrase: string): boolean {
  return new RegExp(escapeRegex(phrase)).test(value)
}

function matchScoreAll(text: string, keywords: string[]): number {
  let score = 0
  for (const kw of keywords) {
    if (fieldMatchScore(text, kw)) {
      const len = kw.replace(/[^a-z0-9]/gi, '').length
      score += len >= 6 ? 3 : len >= 3 ? 2 : 1
    }
  }
  return score
}

function exactFieldMatch(text: string, keyword: string): boolean {
  return normalizeText(text) === normalizeText(keyword)
}

const courseAliases: Record<string, string[]> = {
  dsa: ['cấu trúc dữ liệu', 'giải thuật', 'data structures'],
  ctdl: ['cấu trúc dữ liệu', 'giải thuật', 'data structures'],
  'ctdl&gt': ['cấu trúc dữ liệu', 'giải thuật'],
  oop: ['lập trình hướng đối tượng', 'hướng đối tượng', 'object oriented'],
  lthdt: ['lập trình hướng đối tượng', 'hướng đối tượng'],
  sql: ['cơ sở dữ liệu', 'database'],
  csdl: ['cơ sở dữ liệu', 'database'],
  database: ['cơ sở dữ liệu', 'database'],
  os: ['hệ điều hành', 'operating system'],
  hdh: ['hệ điều hành'],
  web: ['công nghệ web', 'web'],
  frontend: ['công nghệ web', 'frontend'],
  backend: ['công nghệ web', 'backend'],
  mobile: ['thiết bị di động', 'di động', 'mobile'],
  android: ['thiết bị di động', 'android', 'di động'],
  ios: ['thiết bị di động', 'ios', 'di động'],
  dotnet: ['công nghệ .net', '.net'],
  'c#': ['công nghệ .net', '.net', 'c#'],
  csharp: ['công nghệ .net', '.net', 'c#'],
  game: ['phát triển game', 'game'],
  'mạng': ['mạng máy tính', 'network'],
  network: ['mạng máy tính', 'network'],
  'máy tính': ['mạng máy tính', 'máy tính'],
  hci: ['giao tiếp người máy', 'hci'],
  'xác suất': ['xác suất thống kê', 'xác suất'],
  statistics: ['xác suất thống kê', 'statistics'],
  'thống kê': ['xác suất thống kê', 'thống kê'],
  se: ['công nghệ phần mềm', 'software engineering'],
  'yêu cầu': ['phân tích yêu cầu', 'yêu cầu'],
  requirement: ['phân tích yêu cầu', 'requirement'],
  architecture: ['kiến trúc phần mềm', 'architecture'],
  'kiến trúc': ['kiến trúc phần mềm', 'kiến trúc'],
  'rời rạc': ['cấu trúc rời rạc', 'rời rạc'],
  discrete: ['cấu trúc rời rạc', 'discrete'],
  cloud: ['điện toán đám mây', 'cloud'],
  'đám mây': ['điện toán đám mây', 'cloud'],
  service: ['điện toán đám mây', 'service'],
  elearning: ['e-learning', 'elearning'],
  english: ['anh văn', 'english'],
  'anh văn': ['anh văn', 'english'],
  'kỹ năng': ['kỹ năng nghề nghiệp', 'kỹ năng'],
  ss004: ['kỹ năng nghề nghiệp', 'ss004'],
  'đồ án': ['đồ án', 'project'],
  'do an': ['đồ án', 'project'],
  project: ['đồ án', 'project'],
  'khóa luận': ['khóa luận tốt nghiệp', 'khóa luận', 'thesis'],
  thesis: ['khóa luận tốt nghiệp', 'thesis'],
  'đặc tả': ['đặc tả hình thức', 'formal'],
  formal: ['đặc tả hình thức', 'formal'],
  'nhập môn': ['nhập môn', 'introduction'],
  'cấu trúc': ['cấu trúc dữ liệu', 'cấu trúc rời rạc'],
  'dữ liệu': ['cơ sở dữ liệu', 'cấu trúc dữ liệu'],
}

export type SearchCourseResult = CourseSummary & { _score: number; _matched: boolean }

export type SearchRepoResult = RepoSummary & { _score: number; _matched: boolean }

export function hasExactCourseMatch(courses: CourseSummary[], query: string): boolean {
  const normQuery = normalizeText(query)
  if (!normQuery) return false

  return courses.some((course) =>
    normalizeText(course.name) === normQuery || normalizeText(course.code) === normQuery,
  )
}

export function searchCourses(
  courses: CourseSummary[],
  query: string,
  repos?: RepoSummary[],
): SearchCourseResult[] {
  if (!query.trim()) return courses.map(c => ({ ...c, _score: 0, _matched: false }))

  const normQuery = normalizeText(query)
  const exactMatches = courses.filter((course) =>
    normalizeText(course.name) === normQuery || normalizeText(course.code) === normQuery,
  )
  if (exactMatches.length > 0) {
    return exactMatches.map((course) => ({ ...course, _score: 1000, _matched: true }))
  }

  const intent = getQueryIntent(query)
  const keywords = expandKeywords(query, intent)
  const threshold = intent === 'phrase' ? 80 : intent === 'specific' ? 30 : 15
  const results: SearchCourseResult[] = []
  const queryTokens = tokenizeSearchQuery(normQuery)

  const aliasTargets = new Set<string>()
  for (const [alias, targets] of Object.entries(courseAliases)) {
    const normalizedAlias = normalizeText(alias)
    if (queryTokens.includes(normalizedAlias) || containsText(normQuery, normalizedAlias)) {
      for (const t of targets) aliasTargets.add(normalizeText(t))
    }
  }

  const reposByCourse = new Map<number, RepoSummary[]>()
  if (repos) {
    for (const r of repos) {
      if (r.courseId == null) continue
      const list = reposByCourse.get(r.courseId) ?? []
      list.push(r)
      reposByCourse.set(r.courseId, list)
    }
  }

  for (const course of courses) {
    let score = 0
    const code = normalizeText(course.code)
    const name = normalizeText(course.name)
    const description = normalizeText(course.description || '')
    const searchableCourseText = [code, name, description].filter(Boolean).join(' ')

    if (intent === 'phrase' && queryTokens.length >= 2) {
      if (containsText(name, normQuery)) score += 120
      else if (containsText(code, normQuery)) score += 120
      if (tokensInOrder(name, queryTokens)) score += 100
      if (tokensInOrder(description, queryTokens)) score += 60
      if (queryTokens.every((token) => containsText(searchableCourseText, token))) score += 80
    }

    for (const kw of keywords) {
      if (exactFieldMatch(code, kw) || exactFieldMatch(name, kw)) score += 100
      else if (containsText(code, kw) || containsText(name, kw)) score += 40
    }

    score += matchScoreAll(course.code, keywords) * 3
    score += matchScoreAll(course.name, keywords) * 3
    if (description) score += matchScoreAll(description, keywords)

    if (aliasTargets.size > 0) {
      for (const target of aliasTargets) {
        if (containsText(name, target)) score += 70
      }
    }

    const courseRepos = reposByCourse.get(course.id)
    if (courseRepos && courseRepos.length > 0 && score < 80) {
      let repoScore = 0
      const repoKeywords = keywords
      for (const r of courseRepos) {
        const rn = normalizeText(r.displayName || '')
        const rl = normalizeText(r.primaryLanguage || '')
        const rs = (r.techStacks ?? []).map(normalizeText)
        if (repos) {
          for (const kw of repoKeywords) {
            if (fieldMatchScore(rn, kw)) repoScore += 3
            else if (fieldMatchScore(rl, kw)) repoScore += 3
            else if (rs.some(s => containsText(s, kw) || containsText(kw, s))) repoScore += 2
            else if (r.readmeExcerpt && fieldMatchScore(r.readmeExcerpt, kw)) repoScore += 1
            else if (r.fileTree && containsText(normalizeText(r.fileTree), kw)) repoScore += 1
          }
        }
        if (repoScore >= 3) break
      }
      if (repoScore >= 3) score += 50
    }

    if (score >= threshold) {
      results.push({ ...course, _score: score, _matched: true })
    }
  }

  return results.sort((a, b) => b._score - a._score)
}

export function searchRepos(repos: RepoSummary[], query: string): SearchRepoResult[] {
  if (!query.trim()) return repos.map(r => ({ ...r, _score: 0, _matched: false }))

  const intent = getQueryIntent(query)
  const keywords = expandKeywords(query, intent)
  const threshold = intent === 'phrase' ? 80 : intent === 'specific' ? 60 : 40
  const results: SearchRepoResult[] = []
  const normQuery = normalizeText(query)
  const queryTokens = tokenizeSearchQuery(normQuery)

  for (const repo of repos) {
    let score = 0
    const name = normalizeText(repo.displayName || '')
    const desc = normalizeText(repo.description || '')
    const lang = normalizeText(repo.primaryLanguage || '')
    const courseName = normalizeText(repo.courseName || '')
    const courseCode = normalizeText(repo.courseCode || '')
    const stacks = (repo.techStacks || []).map(normalizeText)
    const searchableRepoText = [name, desc, lang, courseName, courseCode, ...stacks].filter(Boolean).join(' ')

    if (intent === 'phrase' && queryTokens.length >= 2) {
      if (containsText(name, normQuery)) score += 120
      if (tokensInOrder(name, queryTokens)) score += 100
      if (containsText(desc, normQuery)) score += 80
      if (tokensInOrder(desc, queryTokens)) score += 60
      if (containsText(courseName, normQuery)) score += 60
      if (containsText(courseCode, normQuery)) score += 60
      if (queryTokens.every((token) => containsText(searchableRepoText, token))) score += 80
    }

    for (const kw of keywords) {
      if (exactFieldMatch(name, kw)) score += 100
      else if (exactFieldMatch(courseCode, kw)) score += 80
      else if (exactFieldMatch(courseName, kw)) score += 60
      else if (containsText(name, kw)) score += 50
      else if (containsText(lang, kw)) score += 40
    }

    for (const kw of keywords) {
      for (const stack of stacks) {
        if (exactFieldMatch(stack, kw)) score += 85
        else if (containsText(stack.toLowerCase(), kw)) score += 40
      }
    }

    for (const kw of keywords) {
      if (fieldMatchScore(desc, kw)) score += 65
    }

    if (repo.readmeExcerpt) {
      for (const kw of keywords) {
        if (fieldMatchScore(repo.readmeExcerpt, kw)) score += 30
      }
    }

    if (repo.fileTree) {
      for (const kw of keywords) {
        if (fieldMatchScore(repo.fileTree, kw)) score += 20
      }
    }

    if (score >= threshold) {
      results.push({ ...repo, _score: Math.round(score), _matched: true })
    }
  }

  return results.sort((a, b) => b._score - a._score)
}
