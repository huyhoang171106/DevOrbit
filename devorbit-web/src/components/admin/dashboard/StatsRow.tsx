import { AdminStatsCard } from '../shared/AdminStatsCard'
import { Users, BookOpen, GitBranch, ClockClockwise } from '@phosphor-icons/react'
import type { AdminStats } from '../../../types/admin'

interface StatsRowProps {
  stats: AdminStats
}

export function StatsRow({ stats }: StatsRowProps) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <AdminStatsCard
        label="Sinh viên"
        value={stats.totalStudents}
        icon={<Users size={24} />}
      />
      <AdminStatsCard
        label="Môn học"
        value={stats.totalCourses}
        icon={<BookOpen size={24} />}
      />
      <AdminStatsCard
        label="Repos"
        value={stats.totalRepos}
        icon={<GitBranch size={24} />}
      />
      <AdminStatsCard
        label="Chờ duyệt"
        value={stats.pendingCandidates}
        icon={<ClockClockwise size={24} />}
      />
    </div>
  )
}
