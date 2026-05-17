import { GraphNode } from '../../types/api'

export type CareerOrientation = {
  id: string
  name: string
  description: string
  icon: string
  color: string
  /** Course codes recommended for this orientation */
  recommendedCodes: string[]
}

/** Common elective courses across all orientations (Nhóm chung — 13 courses, up to 39 credits) */
const COMMON_ELECTIVE_CODES = [
  'SE330', // Ngôn ngữ lập trình Java (4tc)
  'SE332', // Chuyên đề CSDL nâng cao (2tc)
  'SE334', // Các phương pháp lập trình (3tc)
  'SE347', // Công nghệ Web và ứng dụng (4tc)
  'SE350', // Chuyên đề E-learning (2tc)
  'SE343', // Công nghệ Portal (3tc)
  'SE355', // Máy học và các công cụ (3tc)
  'SE310', // Công nghệ .NET (4tc)
  'SE346', // Lập trình trên thiết bị di động (4tc)
  'SE404', // Chuyên đề E-Government (2tc)
  'SE331', // Chuyên đề E-Commerce (2tc)
  'SE313', // Một số thuật toán thông minh (2tc)
  'SE352', // Phát triển ứng dụng VR (3tc)
]

export const ORIENTATION_OPTIONS: CareerOrientation[] = [
  {
    id: 'software-dev',
    name: 'Phát triển phần mềm',
    description: 'Kiến trúc phần mềm, phân tích yêu cầu, J2EE, UI/UX, an toàn phần mềm, học sâu',
    icon: '🖥️',
    color: '#10b981',
    recommendedCodes: [
      ...COMMON_ELECTIVE_CODES,
      'SE109', // Phát triển, vận hành, bảo trì phần mềm (3tc)
      'SE356', // Kiến trúc phần mềm (4tc)
      'SE357', // Kỹ thuật phân tích yêu cầu (3tc)
      'SE325', // Chuyên đề J2EE (4tc)
      'SE101', // Phương pháp mô hình hóa (3tc)
      'SE106', // Đặc tả hình thức (4tc)
      'SE214', // Công nghệ phần mềm chuyên sâu (4tc)
      'SE362', // An toàn phần mềm và hệ thống (4tc)
      'SE363', // Phát triển ứng dụng trên nền tảng dữ liệu lớn (4tc)
      'SE364', // Thiết kế giao diện và trải nghiệm người dùng (4tc)
      'SE365', // Học sâu ứng dụng trong phát triển phần mềm (4tc)
    ],
  },
  {
    id: 'game-vr',
    name: 'Môi trường ảo và Game',
    description: 'Thiết kế game, lập trình game 3D, đồ họa, VR, game online',
    icon: '🎮',
    color: '#8b5cf6',
    recommendedCodes: [
      ...COMMON_ELECTIVE_CODES,
      'SE221', // Lập trình game nâng cao (4tc)
      'SE220', // Thiết kế game (4tc)
      'SE320', // Lập trình đồ họa 3 chiều với Direct 3D (4tc)
      'SE327', // Phát triển và vận hành game (4tc)
      'SE328', // Lập trình TTNT trong game (4tc)
      'SE344', // Lập trình game trên các thiết bị di động (4tc)
      'SE314', // Công nghệ game 3D (3tc)
      'SE315', // Công nghệ Game Online (4tc)
      'SE316', // Phát triển Game đa nền tảng (3tc)
      'SE317', // Công nghệ tiên tiến trong phát triển game (3tc)
    ],
  },
]

/** Backward-compat alias */
export const CAREER_ORIENTATIONS = ORIENTATION_OPTIONS

/** Per-course credit map from elective curriculum (used when API data unavailable) */
export const ELECTIVE_CREDITS: Record<string, number> = {
  // Cơ sở ngành (section 3.4.1) — tối thiểu 12TC
  'SE359': 3, 'SE102': 3, 'SE115': 3, 'SE116': 4,
  'SE114': 3, 'SE360': 4, 'SE361': 4, 'SE301': 3,
  'SE215': 4, 'SE113': 3, 'SE358': 4, 'SE117': 4,
  // Nhóm chung
  'SE330': 4, 'SE332': 2, 'SE334': 3, 'SE347': 4,
  'SE350': 2, 'SE343': 3, 'SE355': 3, 'SE310': 4,
  'SE346': 4, 'SE404': 2, 'SE331': 2, 'SE313': 2, 'SE352': 3,
  // Phát triển phần mềm
  'SE109': 3, 'SE356': 4, 'SE357': 3, 'SE325': 4,
  'SE101': 3, 'SE106': 4, 'SE214': 4, 'SE362': 4,
  'SE363': 4, 'SE364': 4, 'SE365': 4,
  // Môi trường ảo và Game
  'SE221': 4, 'SE220': 4, 'SE320': 4, 'SE327': 4,
  'SE328': 4, 'SE344': 4, 'SE314': 3, 'SE315': 4,
  'SE316': 3, 'SE317': 3,
}

/** Course names for nicer display in elective picker */
export const COURSE_NAMES: Record<string, string> = {
  'SE101': 'Phương pháp mô hình hóa',
  'SE102': 'Nhập môn phát triển game',
  'SE106': 'Đặc tả hình thức',
  'SE109': 'Phát triển, vận hành, bảo trì PM',
  'SE113': 'Kiểm chứng phần mềm',
  'SE114': 'Nhập môn ứng dụng di động',
  'SE115': 'Phát triển game với Unity',
  'SE116': 'Phát triển KNL trình game',
  'SE117': 'Kỹ thuật lập trình',
  'SE214': 'Công nghệ phần mềm chuyên sâu',
  'SE215': 'Giao tiếp người máy',
  'SE220': 'Thiết kế Game',
  'SE221': 'Lập trình game nâng cao',
  'SE301': 'PT PM mã nguồn mở',
  'SE310': 'Công nghệ .NET',
  'SE313': 'Một số thuật toán thông minh',
  'SE314': 'Công nghệ game 3D',
  'SE315': 'Công nghệ Game Online',
  'SE316': 'Phát triển Game đa nền tảng',
  'SE317': 'Công nghệ tiên tiến trong PT game',
  'SE320': 'Lập trình đồ họa 3D với Direct3D',
  'SE325': 'Chuyên đề J2EE',
  'SE327': 'Phát triển và vận hành game',
  'SE328': 'Lập trình TTNT trong Game',
  'SE330': 'Ngôn ngữ lập trình Java',
  'SE331': 'Chuyên đề E-commerce',
  'SE332': 'Chuyên đề CSDL nâng cao',
  'SE334': 'Các phương pháp lập trình',
  'SE343': 'Công nghệ Portal',
  'SE344': 'Lập trình game trên thiết bị di động',
  'SE346': 'Lập trình trên thiết bị di động',
  'SE347': 'Công nghệ Web và ứng dụng',
  'SE350': 'Chuyên đề E-learning',
  'SE352': 'Phát triển ứng dụng VR',
  'SE355': 'Máy học và các công cụ',
  'SE356': 'Kiến trúc Phần mềm',
  'SE357': 'Kỹ thuật phân tích yêu cầu',
  'SE358': 'Quản lý dự án PT PM',
  'SE359': 'DevOps trong PT PM',
  'SE360': 'Điện toán đám mây & PT UD hướng DV',
  'SE361': 'PT PM theo kiến trúc Microservices',
  'SE362': 'An toàn PM và hệ thống',
  'SE363': 'PT ứng dụng trên nền tảng DL lớn',
  'SE364': 'Thiết kế giao diện và trải nghiệm',
  'SE365': 'Học sâu ứng dụng trong PT PM',
  'SE400': 'Seminar CNPM hiện đại',
  'SE403': 'Nguyên lý thiết kế thế giới ảo',
  'SE404': 'Chuyên đề E-Government',
  'SE406': 'Mẫu thiết kế HĐT',
  'SE407': 'Pervasive and Mobile Computing',
  'SE408': 'PT game với Blockchain',
  'SE409': 'Phát triển dự án Game',
}

/** Cơ sở ngành elective courses (section 3.4.1) */
export const CO_SO_NGANH_COURSES = ['SE359','SE102','SE115','SE116','SE114','SE360','SE361','SE301','SE215','SE113','SE358','SE117']

/** Tối thiểu tín chỉ cơ sở ngành */
export const CO_SO_NGANH_MIN_TC = 12

/** Tối thiểu tín chỉ chuyên ngành */
export const CHUYEN_NGANH_MIN_TC = 16

// ─── Elective picker sub-groups (thematic grouping) ───

export type ElectiveSubGroup = {
  title: string
  codes: string[]
}

export type ElectiveGroupDef = {
  title: string
  minTC: number
  subgroups: ElectiveSubGroup[]
}

export const CO_SO_NGANH_GROUPS: ElectiveSubGroup[] = [
  {
    title: 'Game & Đồ họa',
    codes: ['SE102', 'SE115', 'SE116'],
  },
  {
    title: 'Cloud & DevOps',
    codes: ['SE359', 'SE360', 'SE361'],
  },
  {
    title: 'Mobile & Nhúng',
    codes: ['SE114', 'SE117'],
  },
  {
    title: 'Chất lượng & Quản lý',
    codes: ['SE113', 'SE358', 'SE301'],
  },
  {
    title: 'HCI & Kỹ thuật',
    codes: ['SE215'],
  },
]

export const NHOM_CHUNG_GROUPS: ElectiveSubGroup[] = [
  {
    title: 'Ngôn ngữ & Nền tảng',
    codes: ['SE330', 'SE310', 'SE334', 'SE313'],
  },
  {
    title: 'Web & Thương mại điện tử',
    codes: ['SE347', 'SE343', 'SE331', 'SE350', 'SE404'],
  },
  {
    title: 'Dữ liệu & Di động',
    codes: ['SE332', 'SE346'],
  },
  {
    title: 'Thực tế ảo',
    codes: ['SE352'],
  },
]

export const PT_PM_GROUPS: ElectiveSubGroup[] = [
  {
    title: 'Phân tích & Thiết kế',
    codes: ['SE357', 'SE101', 'SE106'],
  },
  {
    title: 'Kiến trúc & Nền tảng',
    codes: ['SE356', 'SE325', 'SE109'],
  },
  {
    title: 'Quy trình & Chuyên sâu',
    codes: ['SE214', 'SE362', 'SE363', 'SE364', 'SE365'],
  },
]

export const GAME_GROUPS: ElectiveSubGroup[] = [
  {
    title: 'Thiết kế & Lập trình Game',
    codes: ['SE220', 'SE221'],
  },
  {
    title: 'Đồ họa & Vận hành',
    codes: ['SE320', 'SE327', 'SE344'],
  },
  {
    title: 'AI & Công nghệ tiên tiến',
    codes: ['SE328', 'SE314', 'SE315', 'SE316', 'SE317'],
  },
]

// Map từ hướng lớn → sub-groups (để GalaxyPage loop)
export const ELECTIVE_SECTION_GROUPS: {
  title: string
  minTC: number
  subgroups: ElectiveSubGroup[]
  allCodes: string[]
}[] = [
  {
    title: 'Cơ sở ngành',
    minTC: 12,
    subgroups: CO_SO_NGANH_GROUPS,
    allCodes: CO_SO_NGANH_COURSES,
  },
  {
    title: 'Nhóm chung',
    minTC: 0,
    subgroups: NHOM_CHUNG_GROUPS,
    allCodes: [
      'SE330','SE332','SE334','SE347','SE350','SE343',
      'SE355','SE310','SE346','SE404','SE331','SE313','SE352',
    ],
  },
  {
    title: 'Phát triển phần mềm',
    minTC: 16,
    subgroups: PT_PM_GROUPS,
    allCodes: [
      'SE109','SE356','SE357','SE325','SE101','SE106',
      'SE214','SE362','SE363','SE364','SE365',
    ],
  },
  {
    title: 'Môi trường ảo và Game',
    minTC: 16,
    subgroups: GAME_GROUPS,
    allCodes: [
      'SE221','SE220','SE320','SE327','SE328','SE344',
      'SE314','SE315','SE316','SE317',
    ],
  },
]

/** Get recommended node IDs based on a career orientation. Matches course codes against loaded graph nodes. */
export function getRecommendedNodeIds(
  orientation: CareerOrientation | null,
  nodes: GraphNode[],
): Set<number> {
  if (!orientation) return new Set()
  const codeSet = new Set(orientation.recommendedCodes.map(c => c.trim().toUpperCase()))
  return new Set(
    nodes.filter(n => codeSet.has(n.code.toUpperCase())).map(n => n.id),
  )
}

export const SEMESTER_LABELS: Record<number, string> = {
  1: 'Học kỳ 1',
  2: 'Học kỳ 2',
  3: 'Học kỳ 3',
  4: 'Học kỳ 4',
  5: 'Học kỳ 5',
  6: 'Học kỳ 6',
  7: 'Học kỳ 7',
  8: 'Học kỳ 8',
}

export const SEMESTER_COLORS: Record<number, string> = {
  1: '#22d3ee',
  2: '#34d399',
  3: '#14b8a6',
  4: '#6366f1',
  5: '#8b5cf6',
  6: '#a78bfa',
  7: '#f59e0b',
  8: '#f43f5e',
}

// ─── Graduation (HK8) ───

export type GraduationPath = {
  id: string
  name: string
  description: string
  courses: { code: string; credits: number }[]
  totalCredits: number
}

export const GRADUATION_PATHS: GraduationPath[] = [
  {
    id: 'thesis',
    name: 'Khóa luận tốt nghiệp',
    description: 'Nghiên cứu chuyên sâu, tối đa 02 SV/đề tài',
    courses: [{ code: 'SE505', credits: 10 }],
    totalCredits: 10,
  },
  {
    id: 'enterprise',
    name: 'Đồ án tại doanh nghiệp',
    description: 'Giải quyết bài toán thực tế, 01 SV/đề tài, GPA ≥ 70',
    courses: [{ code: 'SE506', credits: 10 }],
    totalCredits: 10,
  },
  {
    id: 'combined',
    name: 'Đồ án + Chuyên đề TN',
    description: 'Đồ án TN (6TC) + 1 môn chuyên đề (4TC), GPA ≥ 65',
    courses: [{ code: 'SE507', credits: 6 }],
    totalCredits: 10,
  },
]

export const SPECIALTY_COURSES: { code: string; name: string; credits: number }[] = [
  { code: 'SE400', name: 'Seminar CNPM hiện đại', credits: 4 },
  { code: 'SE406', name: 'Mẫu thiết kế hướng đối tượng', credits: 4 },
  { code: 'SE403', name: 'Nguyên lý thiết kế thế giới ảo', credits: 4 },
  { code: 'SE407', name: 'Pervasive and Mobile Computing', credits: 4 },
  { code: 'SE408', name: 'Phát triển game với Blockchain', credits: 4 },
  { code: 'SE409', name: 'Phát triển dự án Game', credits: 4 },
]
