import { useState } from 'react'
import { RepoScanTab } from './RepoScanTab'
import { RepoCandidatesTab } from './RepoCandidatesTab'
import { RepoApprovedTab } from './RepoApprovedTab'
import { RepoStatsTab } from './RepoStatsTab'

const TABS = [
  { key: 'scan', label: 'Quét' },
  { key: 'candidates', label: 'Ứng viên' },
  { key: 'approved', label: 'Đã duyệt' },
  { key: 'stats', label: 'Thống kê' },
] as const

export function RepoPipelineTabs() {
  const [activeTab, setActiveTab] = useState<string>('scan')

  return (
    <div>
      <div className="flex gap-1 mb-6">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`px-4 py-2 text-sm rounded-lg transition-colors ${
              activeTab === tab.key
                ? 'bg-orbit-accent/15 text-orbit-accent'
                : 'text-ink-secondary hover:text-ink-primary'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'scan' && <RepoScanTab />}
      {activeTab === 'candidates' && <RepoCandidatesTab />}
      {activeTab === 'approved' && <RepoApprovedTab />}
      {activeTab === 'stats' && <RepoStatsTab />}
    </div>
  )
}
