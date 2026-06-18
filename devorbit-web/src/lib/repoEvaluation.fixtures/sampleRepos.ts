import type { RepoSummary } from '../../types/api'

export type RepoEvaluationFixture = RepoSummary & {
  topics?: string[] | string | null
  forks?: number | null
  updatedAt?: string | null
  files?: string[]
}

export const lab04It007: RepoEvaluationFixture = {
  id: 7001,
  displayName: 'Lab04-IT007',
  description: "Our team's works for a lab in Operating System",
  githubUrl: 'https://github.com/uyenbhku/Lab04-IT007',
  primaryLanguage: 'C++',
  stars: 0,
  techStacks: [],
  courseId: 7,
  courseCode: 'IT007',
  courseName: 'Operating System',
  readmeExcerpt: "Our team's works for a lab in Operating System.",
  topics: ['operating-system', 'lab', 'scheduling'],
  files: [
    'README.md',
    'SJF.cpp',
    'SRTF.cpp',
    'RR.cpp',
  ],
}

export const it008Notepad: RepoEvaluationFixture = {
  id: 7002,
  displayName: 'UIT.IT008.Notepad',
  description: 'Đồ án môn học lập trình trực quan',
  githubUrl: 'https://github.com/hotrungnhan/UIT.IT008.Notepad',
  primaryLanguage: 'C#',
  stars: 0,
  techStacks: ['WinForms', '.NET Framework 4.7.2'],
  courseId: 8,
  courseCode: 'IT008',
  courseName: 'Lập trình trực quan',
  readmeExcerpt: 'Đồ án môn học lập trình trực quan.',
  topics: ['winforms', 'notepad', 'project'],
  files: [
    'README.md',
    'UIT.IT008.Notepad.sln',
    'Nodepad+=.csproj',
    'Form1.cs',
    'Form1.Designer.cs',
    'Program.cs',
    'Properties/AssemblyInfo.cs',
    'Properties/Resources.resx',
    'Properties/Settings.settings',
  ],
}

export const lab5It008: RepoEvaluationFixture = {
  id: 7003,
  displayName: '24521736_Lab5_IT008',
  description: 'Bài tập thực hành Lab 5 Lập trình trực quan',
  githubUrl: 'https://github.com/DonThuanUIT-24521736/24521736_Lab5_IT008',
  primaryLanguage: 'C#',
  stars: 0,
  techStacks: ['WinForms', '.NET Framework'],
  courseId: 8,
  courseCode: 'IT008',
  courseName: 'Lập trình trực quan',
  readmeExcerpt: '# 24521736_Lab5_IT008',
  topics: ['lab', 'winforms', 'visual-programming'],
  files: [
    'README.md',
    'Bai04/Bai04.sln',
    'Bai04/Bai04.csproj',
    'Bai04/Form1.cs',
    'Bai04/.vs/Bai04/v17/.suo',
    'Bai04/bin/Debug/Bai04.exe',
    'Bai04/obj/Debug/Bai04.csproj.FileListAbsolute.txt',
    'Bai06/Bai06.sln',
    'Bai06/Bai06.csproj',
    'Bai06/Form1.cs',
    'Bai09/Bai09.sln',
    'Bai09/Bai09.csproj',
    'Bai09/Form1.cs',
  ],
}
