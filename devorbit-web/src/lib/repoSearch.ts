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

function expandKeywords(keyword: string): string[] {
  const norm = normalizeText(keyword)
  const seen = new Set<string>()
  const result: string[] = []
  const tokens = norm.split(/\s+/).filter(Boolean)

  const add = (w: string) => {
    const nw = w.toLowerCase().trim()
    if (nw && !seen.has(nw) && nw !== norm) {
      seen.add(nw)
      result.push(nw)
    }
  }

  seen.add(norm)
  result.push(norm)

  for (const token of tokens) {
    if (expansionMap[token]) {
      for (const exp of expansionMap[token]) add(exp)
    }
  }

  if (expansionMap[norm]) {
    for (const exp of expansionMap[norm]) add(exp)
  }

  return result
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

function fieldMatchScore(text: string, keyword: string): number {
  const lower = text.toLowerCase()
  const kw = keyword.toLowerCase()

  if (/^[a-z0-9]+$/i.test(kw)) {
    return new RegExp(`\\b${escapeRegex(kw)}\\b`).test(lower) ? 1 : 0
  }
  return lower.includes(kw) ? 1 : 0
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
  return text.toLowerCase() === keyword.toLowerCase()
}

export type SearchCourseResult = CourseSummary & { _score: number; _matched: boolean }

export type SearchRepoResult = RepoSummary & { _score: number; _matched: boolean }

export function searchCourses(courses: CourseSummary[], query: string): SearchCourseResult[] {
  if (!query.trim()) return courses.map(c => ({ ...c, _score: 0, _matched: false }))

  const keywords = expandKeywords(query)
  const specific = isSpecificQuery(query)
  const threshold = specific ? 30 : 15
  const results: SearchCourseResult[] = []

  for (const course of courses) {
    let score = 0
    const code = course.code.toLowerCase()
    const name = course.name.toLowerCase()

    for (const kw of keywords) {
      if (exactFieldMatch(code, kw) || exactFieldMatch(name, kw)) score += 100
      else if (code.includes(kw) || name.includes(kw)) score += 40
    }

    score += matchScoreAll(course.code, keywords) * 3
    score += matchScoreAll(course.name, keywords) * 3
    if (course.description) score += matchScoreAll(course.description, keywords)

    if (score >= threshold) {
      results.push({ ...course, _score: score, _matched: true })
    }
  }

  return results.sort((a, b) => b._score - a._score)
}

export function searchRepos(repos: RepoSummary[], query: string): SearchRepoResult[] {
  if (!query.trim()) return repos.map(r => ({ ...r, _score: 0, _matched: false }))

  const keywords = expandKeywords(query)
  const specific = isSpecificQuery(query)
  const threshold = specific ? 60 : 35
  const results: SearchRepoResult[] = []

  for (const repo of repos) {
    let score = 0
    const name = (repo.displayName || '').toLowerCase()
    const desc = (repo.description || '').toLowerCase()
    const lang = (repo.primaryLanguage || '').toLowerCase()
    const courseCode = (repo.courseCode || '').toLowerCase()
    const courseName = (repo.courseName || '').toLowerCase()

    for (const kw of keywords) {
      if (exactFieldMatch(name, kw)) score += 100
      else if (exactFieldMatch(lang, kw)) score += 90
      else if (name.includes(kw)) score += 50
      else if (lang.includes(kw)) score += 40
    }

    for (const kw of keywords) {
      for (const stack of repo.techStacks || []) {
        if (exactFieldMatch(stack, kw)) score += 85
        else if (stack.toLowerCase().includes(kw)) score += 40
      }
    }

    for (const kw of keywords) {
      if (fieldMatchScore(desc, kw)) score += 50
    }

    for (const kw of keywords) {
      if (fieldMatchScore(courseCode, kw) || fieldMatchScore(courseName, kw)) score += 40
    }

    if (score < threshold && repo.readmeExcerpt) {
      score += matchScoreAll(repo.readmeExcerpt, keywords) * 15
    }
    if (score < threshold && repo.fileTree) {
      score += matchScoreAll(repo.fileTree, keywords) * 10
    }

    if (score >= threshold) {
      results.push({ ...repo, _score: Math.round(score), _matched: true })
    }
  }

  return results.sort((a, b) => b._score - a._score).slice(0, 20)
}
