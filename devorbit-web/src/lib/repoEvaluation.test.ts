import { describe, expect, test } from 'vitest'
import { evaluateRepository, extractRepoSignals } from './repoEvaluation'
import type { RepoSummary } from '../types/api'

type RepoFixture = RepoSummary & {
  topics?: string[] | string | null
  forks?: number | null
  updatedAt?: string | null
  readmeExcerpt?: string | null
  files?: string[]
}

function repo(overrides: Partial<RepoFixture> = {}): RepoFixture {
  return {
    id: 1,
    displayName: 'sample-repo',
    description: 'Sample repository',
    githubUrl: 'https://github.com/example/sample-repo',
    primaryLanguage: 'TypeScript',
    stars: 3,
    techStacks: [],
    courseId: 10,
    courseCode: 'SE100',
    courseName: 'Sample course',
    ...overrides,
  }
}

describe('evaluateRepository', () => {
  test('rates a programming exercise with source and tests as highly recommended', () => {
    const result = evaluateRepository(repo({
      displayName: 'oop-labs-solutions',
      description: 'Java OOP lab assignments with input output samples',
      primaryLanguage: 'Java',
      topics: ['oop', 'lab', 'assignment'],
      files: ['README.md', 'lab01/src/Main.java', 'lab01/tests/MainTest.java', 'lab01/input/sample.txt', 'lab01/output/sample.txt', 'solutions/lab01.md'],
    }))

    expect(result.repoType).toBe('programming_exercise')
    expect(result.usefulnessRating).toBe('highly_recommended')
    expect(result.bestFor).toContain('Luyện bài')
    expect(result.quickSummary).toContain('bài giải lập trình')
    expect(result.checksBeforeUsing.join(' ')).toContain('test case')
  })

  test('keeps a loose code-only programming repo selective instead of overrating it', () => {
    const result = evaluateRepository(repo({
      displayName: 'cpp-snippets',
      description: 'Small C++ programming practice snippets',
      primaryLanguage: 'C++',
      topics: ['programming'],
      files: ['main.cpp', 'sort.cpp'],
    }))

    expect(result.repoType).toBe('programming_exercise')
    expect(result.usefulnessRating).toBe('selective')
    expect(result.weaknesses.join(' ')).toContain('test')
  })

  test('rates a project with readme, package file, and env example as highly recommended', () => {
    const result = evaluateRepository(repo({
      displayName: 'student-portal-api',
      description: 'Fullstack student portal with REST API and database',
      primaryLanguage: 'TypeScript',
      techStacks: ['React', 'Express', 'PostgreSQL'],
      topics: ['fullstack', 'api', 'database'],
      readmeExcerpt: 'Setup with npm install, npm run dev and configure .env.example.',
      files: ['README.md', 'package.json', '.env.example', 'src/server.ts', 'docs/api.md'],
    }))

    expect(result.repoType).toBe('project_practice')
    expect(result.usefulnessRating).toBe('highly_recommended')
    expect(result.bestFor).toContain('Clone thử')
    expect(result.checksBeforeUsing.join(' ')).toContain('file env/config')
  })

  test('keeps a project without setup guide as selective', () => {
    const result = evaluateRepository(repo({
      displayName: 'web-app-demo',
      description: 'React frontend app demo',
      primaryLanguage: 'JavaScript',
      techStacks: ['React'],
      topics: ['frontend', 'app'],
      files: ['src/App.jsx', 'src/components/Home.jsx'],
    }))

    expect(result.repoType).toBe('project_practice')
    expect(result.usefulnessRating).toBe('selective')
    expect(result.weaknesses.join(' ')).toContain('README')
  })

  test('classifies slide and note repository as study material', () => {
    const result = evaluateRepository(repo({
      displayName: 'database-course-notes',
      description: 'Lecture slides and notes for database theory',
      primaryLanguage: '',
      topics: ['lecture', 'slides', 'notes'],
      files: ['README.md', 'slides/chapter-01.pdf', 'notes/normalization.md', 'docs/syllabus.md'],
    }))

    expect(result.repoType).toBe('study_material')
    expect(result.usefulnessRating).toBe('highly_recommended')
    expect(result.quickSummary).toContain('hệ thống kiến thức')
  })

  test('classifies exams with answers as highly recommended exam review', () => {
    const result = evaluateRepository(repo({
      displayName: 'se-midterm-final-review',
      description: 'Past midterm and final exam with answer key',
      primaryLanguage: '',
      topics: ['exam', 'midterm', 'final', 'answer'],
      files: ['README.md', 'midterm/2024.pdf', 'final/2024.pdf', 'answers/midterm-2024.md'],
    }))

    expect(result.repoType).toBe('exam_review')
    expect(result.usefulnessRating).toBe('highly_recommended')
    expect(result.bestFor).toContain('Luyện đề')
  })

  test('detects Vietnamese accented exam and answer wording after normalization', () => {
    const result = evaluateRepository(repo({
      displayName: 'Đề thi cuối kỳ CSDL',
      description: 'Tổng hợp đề thi, đáp án và lời giải ôn tập cuối kỳ',
      primaryLanguage: '',
      topics: ['ôn tập', 'đáp án'],
    }))

    expect(result.repoType).toBe('exam_review')
    expect(result.usefulnessRating).toBe('highly_recommended')
    expect(result.quickSummary).toContain('luyện đề')
  })

  test('detects source and config signals from file paths without language metadata', () => {
    const result = evaluateRepository(repo({
      displayName: 'bai-tap-tuan-01',
      description: 'Bài tập thực hành nhập môn lập trình',
      primaryLanguage: '',
      techStacks: [],
      files: ['README.md', 'lab01\\Main.java', 'lab01\\MainTest.java', 'lab01\\input\\sample.txt', 'pom.xml'],
    }))

    expect(result.repoType).toBe('programming_exercise')
    expect(result.signals.hasSourceCode).toBe(true)
    expect(result.signals.hasTests).toBe(true)
    expect(result.signals.hasPackageFile).toBe(true)
    expect(result.signals.filePaths).toContain('lab01/Main.java')
  })

  test('does not treat any standalone exam pdf as study material slides', () => {
    const signals = extractRepoSignals(repo({
      displayName: 'final-exam-2024',
      description: 'Final exam review',
      primaryLanguage: '',
      topics: ['final', 'exam'],
      files: ['final/2024.pdf'],
    }))

    expect(signals.hasExam).toBe(true)
    expect(signals.hasSlides).toBe(false)
  })

  test('returns insufficient data when metadata is too sparse', () => {
    const result = evaluateRepository(repo({
      displayName: 'abc',
      description: '',
      primaryLanguage: '',
      stars: null,
      techStacks: [],
      courseId: null,
      courseCode: null,
      courseName: null,
    }))

    expect(result.repoType).toBe('unknown')
    expect(result.usefulnessRating).toBe('insufficient_data')
    expect(result.confidence).toBe('low')
    expect(result.quickSummary).toContain('Chưa đủ dữ liệu')
  })
})
