import { describe, expect, test } from 'vitest'
import { evaluateRepository, extractRepoSignals, formatVietnameseRelativeDate } from './repoEvaluation'
import type { RepoSummary } from '../types/api'

type RepoFixture = RepoSummary & {
  topics?: string[] | string | null
  forks?: number | null
  updatedAt?: string | null
  readmeExcerpt?: string | null
  files?: string[]
  fileTree?: string | null
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
  test('distinguishes a DSA repo with graph tree sorting topics from loose code', () => {
    const rich = evaluateRepository(repo({
      displayName: 'ctdl-gt-labs',
      description: 'CTDL GT lab solutions',
      primaryLanguage: 'C++',
      courseCode: 'IT003',
      courseName: 'Cau truc du lieu va giai thuat',
      fileTree: [
        'README.md',
        'linked-list/main.cpp',
        'stack-queue/stack.cpp',
        'tree/binary_tree.cpp',
        'graph/dijkstra.cpp',
        'sorting/quick_sort.cpp',
        'tests/graph_test.cpp',
      ].join('\n'),
    }))
    const loose = evaluateRepository(repo({
      displayName: 'ctdl-code',
      description: 'CTDL snippets',
      primaryLanguage: 'C++',
      courseCode: 'IT003',
      courseName: 'Cau truc du lieu va giai thuat',
      fileTree: 'main.cpp\nhelper.cpp',
    }))

    expect(rich.courseGroup).toBe('foundation_algorithms')
    expect(rich.coreTopics).toEqual(expect.arrayContaining(['Danh sách liên kết', 'Cây', 'Đồ thị', 'Sắp xếp']))
    expect(rich.usefulnessScore).toBeGreaterThan(loose.usefulnessScore)
    expect(loose.readyToUseLevel).not.toBe('very_ready')
  })

  test('keeps DSA repo with course and language but missing file tree above low priority', () => {
    const result = evaluateRepository(repo({
      displayName: 'dsa-practice',
      description: 'C++ DSA practice for stack and queue',
      primaryLanguage: 'C++',
      courseCode: 'IT003',
      courseName: 'Cau truc du lieu va giai thuat',
      readmeExcerpt: 'Practice stack queue tree graph exercises.',
    }))

    expect(result.courseGroup).toBe('foundation_algorithms')
    expect(result.usefulnessRating).not.toBe('low_priority')
    expect(result.recommendation).toContain('Tham khảo')
  })

  test('classifies IT003 C++ repo without file tree as programming exercise, not project setup', () => {
    const result = evaluateRepository(repo({
      displayName: 'IT003 - Data Structures and Algorithms',
      description: 'Data Structures and Algorithms practice',
      primaryLanguage: 'C++',
      techStacks: [],
      courseCode: 'IT003',
      courseName: 'Data Structures and Algorithms',
      fileTree: null,
      readmeExcerpt: null,
    }))
    const checks = result.checksBeforeUsing.join(' ')

    expect(result.courseGroup).toBe('foundation_algorithms')
    expect(result.repoType).toBe('programming_exercise')
    expect(result.repoIdentity).not.toBe('Project thực hành')
    expect(result.weapons).toContain('C++')
    expect(checks).toContain('source code')
    expect(checks).toContain('test case')
    expect(checks).not.toContain('env')
    expect(checks).not.toContain('database/API')
  })

  test('does not treat SS004 Python metadata as the main weapon without code context', () => {
    const result = evaluateRepository(repo({
      displayName: 'ss004-career-skills',
      description: 'Ky nang nghe nghiep assignments and presentation',
      primaryLanguage: 'Python',
      techStacks: [],
      courseCode: 'SS004',
      courseName: 'Ky nang nghe nghiep',
      fileTree: 'README.md\nrubric.pdf\npresentation.pptx\nreport.docx',
    }))

    expect(result.courseGroup).toBe('general_skills')
    expect(result.weapons).not.toContain('Python')
    expect(result.coreTopics).toEqual(expect.arrayContaining(['Thuyết trình', 'Báo cáo', 'Tiêu chí chấm']))
    expect(result.groupHighlights.join(' ')).toContain('rubric')
  })

  test('rates project with env package and setup readme more ready than project without setup', () => {
    const ready = evaluateRepository(repo({
      displayName: 'course-management-api',
      description: 'Course management REST API with auth and database',
      primaryLanguage: 'TypeScript',
      techStacks: ['React', 'Spring Boot', 'PostgreSQL'],
      courseCode: 'SE104',
      courseName: 'Do an phan mem',
      readmeExcerpt: 'Setup: npm install, configure .env.example, run database migration, docker compose up.',
      fileTree: 'README.md\npackage.json\n.env.example\ndocker-compose.yml\nsrc/server.ts\nmigrations/init.sql',
    }))
    const rough = evaluateRepository(repo({
      displayName: 'course-management-demo',
      description: 'Course app demo',
      primaryLanguage: 'TypeScript',
      techStacks: ['React'],
      courseCode: 'SE104',
      courseName: 'Do an phan mem',
      fileTree: 'src/App.tsx\nsrc/main.tsx',
    }))

    expect(ready.courseGroup).toBe('software_project')
    expect(ready.readyToUseStars).toBeGreaterThan(rough.readyToUseStars)
    expect(ready.coreTopics).toEqual(expect.arrayContaining(['Xác thực', 'REST API', 'Cơ sở dữ liệu', 'Triển khai']))
  })

  test('recognizes design process repos with SRS UML ERD and Figma', () => {
    const result = evaluateRepository(repo({
      displayName: 'ooad-srs-uml-figma',
      description: 'OOAD software design documents with SRS UML ERD and Figma prototype',
      primaryLanguage: '',
      courseCode: 'SE101',
      courseName: 'Nhap mon cong nghe phan mem',
      fileTree: 'README.md\ndocs/SRS.pdf\ndiagrams/use-case.drawio\ndiagrams/erd.drawio\ndiagrams/sequence.puml\nprototype/figma-link.md',
    }))

    expect(result.courseGroup).toBe('design_process')
    expect(result.coreTopics).toEqual(expect.arrayContaining(['SRS', 'Use Case', 'UML', 'ERD', 'Prototype']))
    expect(result.techTools).toEqual(expect.arrayContaining(['Figma', 'Draw.io', 'PlantUML']))
  })

  test('recognizes final exam repo with answer solution as exam review', () => {
    const result = evaluateRepository(repo({
      displayName: 'csdl-final-2024-answer',
      description: 'Final exam 2024 with answer solution',
      primaryLanguage: '',
      courseCode: 'IT004',
      courseName: 'Co so du lieu',
      fileTree: 'final/2024.pdf\nanswer/final-2024-solution.md',
    }))

    expect(result.repoType).toBe('exam_review')
    expect(result.repoIdentity).toBe('Tài liệu ôn thi')
    expect(result.coreTopics).toEqual(expect.arrayContaining(['Đề thi cuối kỳ', 'Đáp án']))
  })

  test('unknown sparse repo falls back without fake X-ray insights', () => {
    const result = evaluateRepository(repo({
      displayName: 'abc',
      description: '',
      primaryLanguage: '',
      stars: null,
      techStacks: [],
      courseId: null,
      courseCode: null,
      courseName: null,
      fileTree: null,
      readmeExcerpt: null,
    }))

    expect(result.repoIdentity).toBe('Chưa đủ dữ liệu để xác định')
    expect(result.readyToUseLevel).toBe('insufficient_data')
    expect(result.quickBullets.join(' ')).toContain('Chưa đủ dữ liệu')
  })
})

describe('formatVietnameseRelativeDate', () => {
  const now = new Date('2026-05-29T12:00:00Z')

  test('formats recent dates without raw ISO text', () => {
    expect(formatVietnameseRelativeDate('2026-05-29T01:00:00Z', now)).toBe('Hôm nay')
    expect(formatVietnameseRelativeDate('2026-05-28T01:00:00Z', now)).toBe('Hôm qua')
    expect(formatVietnameseRelativeDate('2026-05-27T01:00:00Z', now)).toBe('2 ngày trước')
  })

  test('formats month and year distances in Vietnamese', () => {
    expect(formatVietnameseRelativeDate('2026-04-20T10:00:00Z', now)).toBe('1 tháng trước')
    expect(formatVietnameseRelativeDate('2026-03-20T10:00:00Z', now)).toBe('2 tháng trước')
    expect(formatVietnameseRelativeDate('2024-05-20T10:00:00Z', now)).toBe('2 năm trước')
  })

  test('returns null for missing or invalid dates', () => {
    expect(formatVietnameseRelativeDate(null, now)).toBeNull()
    expect(formatVietnameseRelativeDate('not-a-date', now)).toBeNull()
  })
})
