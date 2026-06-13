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
  return normalizeText(query).split(/[^a-z0-9+#.]+/).filter(Boolean)
}

function tokenizeSearchValue(value: string): string[] {
  return tokenizeSearchQuery(value)
}

function includesTokenPrefix(words: string[], token: string): boolean {
  return words.some((word) => word.startsWith(token))
}

function includesTokenSequence(words: string[], tokens: string[]): boolean {
  if (tokens.length === 0 || tokens.length > words.length) return false

  return words.some((_, start) =>
    tokens.every((token, index) => words[start + index] === token),
  )
}

function scoreExactTokenMatches(words: string[], tokens: string[]): number {
  return tokens.filter((token) => words.includes(token)).length
}

function scorePrefixTokenMatches(words: string[], tokens: string[]): number {
  return tokens.filter((token) => includesTokenPrefix(words, token)).length
}

export type SearchCourseResult = CourseSummary & { _score: number; _matched: boolean }

export type SearchRepoResult = RepoSummary & { _score: number; _matched: boolean }

export function hasExactCourseMatch(courses: CourseSummary[], query: string): boolean {
  const normalizedQuery = normalizeText(query)
  if (!normalizedQuery) return false

  return courses.some((course) =>
    normalizeText(course.name) === normalizedQuery || normalizeText(course.code) === normalizedQuery,
  )
}

export function searchCourses(
  courses: CourseSummary[],
  query: string,
  _repos?: RepoSummary[],
): SearchCourseResult[] {
  const normalizedQuery = normalizeText(query)
  if (!normalizedQuery) {
    return courses.map((course) => ({ ...course, _score: 0, _matched: false }))
  }

  const tokens = tokenizeSearchQuery(normalizedQuery)
  const searchableCourses = courses.map((course) => {
    const name = normalizeText(course.name)
    const code = normalizeText(course.code)
    const words = tokenizeSearchValue(`${course.code} ${course.name}`)
    const exactMatch = name === normalizedQuery || code === normalizedQuery
    const phraseMatch = includesTokenSequence(tokenizeSearchValue(course.name), tokens)
      || includesTokenSequence(tokenizeSearchValue(course.code), tokens)

    return { course, name, code, words, exactMatch, phraseMatch }
  })
  const hasPhraseMatch = searchableCourses.some(({ phraseMatch }) => phraseMatch)
  const hasExactTokenMatch = searchableCourses.some(({ words }) =>
    scoreExactTokenMatches(words, tokens) > 0,
  )

  return searchableCourses
    .flatMap(({ course, name, code, words, exactMatch, phraseMatch }): SearchCourseResult[] => {
      const exactTokenCount = scoreExactTokenMatches(words, tokens)
      const prefixTokenCount = scorePrefixTokenMatches(words, tokens)
      const matchedTokenCount = hasExactTokenMatch ? exactTokenCount : prefixTokenCount
      if (hasPhraseMatch ? !phraseMatch : matchedTokenCount === 0) return []

      let score = matchedTokenCount * 100
      if (exactMatch) score += 1000
      if (phraseMatch) score += 400
      if (name.includes(normalizedQuery)) score += 200
      if (code.includes(normalizedQuery)) score += 180
      if (name.startsWith(normalizedQuery)) score += 80

      return [{ ...course, _score: score, _matched: true }]
    })
    .sort((a, b) => b._score - a._score)
}

export function searchRepos(repos: RepoSummary[], query: string): SearchRepoResult[] {
  const normalizedQuery = normalizeText(query)
  if (!normalizedQuery) {
    return repos.map((repo) => ({ ...repo, _score: 0, _matched: false }))
  }

  const tokens = tokenizeSearchQuery(normalizedQuery)
  const searchableRepos = repos.map((repo) => {
      const name = normalizeText(repo.displayName || '')
      const courseName = normalizeText(repo.courseName || '')
      const courseCode = normalizeText(repo.courseCode || '')
      const language = normalizeText(repo.primaryLanguage || '')
      const techStacks = (repo.techStacks || []).map(normalizeText)
      const directValues = [name, courseName, courseCode, language, ...techStacks]
      const words = tokenizeSearchValue(directValues.join(' '))
      const phraseMatch = directValues.some((value) =>
        includesTokenSequence(tokenizeSearchValue(value), tokens),
      )

      return { repo, name, courseName, courseCode, language, techStacks, words, phraseMatch }
    })
  const hasPhraseMatch = searchableRepos.some(({ phraseMatch }) => phraseMatch)
  const hasExactTokenMatch = searchableRepos.some(({ words }) =>
    scoreExactTokenMatches(words, tokens) > 0,
  )

  return searchableRepos
    .flatMap(({
      repo,
      name,
      courseName,
      courseCode,
      language,
      techStacks,
      words,
      phraseMatch,
    }): SearchRepoResult[] => {
      const exactTokenCount = scoreExactTokenMatches(words, tokens)
      const prefixTokenCount = scorePrefixTokenMatches(words, tokens)
      const matchedTokenCount = hasExactTokenMatch ? exactTokenCount : prefixTokenCount
      if (hasPhraseMatch ? !phraseMatch : matchedTokenCount === 0) return []

      let score = matchedTokenCount * 100
      if (phraseMatch) score += 400
      if (name.includes(normalizedQuery)) score += 200
      if (courseName.includes(normalizedQuery)) score += 160
      if (courseCode.includes(normalizedQuery)) score += 150
      if (language === normalizedQuery || techStacks.includes(normalizedQuery)) score += 140

      return [{ ...repo, _score: score, _matched: true }]
    })
    .sort((a, b) => b._score - a._score)
}
