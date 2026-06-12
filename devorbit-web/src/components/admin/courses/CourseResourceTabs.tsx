import { useState } from 'react'
import { useAdminFetch } from '../../../lib/adminHooks'
import { adminApi } from '../../../lib/adminApi'
import { AdminTable } from '../shared/AdminTable'
import { AdminSpinner } from '../shared/AdminSpinner'
import { AdminErrorBanner } from '../shared/AdminErrorBanner'
import { ResourceDialog } from './ResourceDialog'
import { getAdminToken } from '../../../lib/auth'
import type { YoutubePlaylistResponse, ArticleResponse, TutorialResponse, YoutubePlaylistRequest, ArticleRequest, TutorialRequest } from '../../../types/api'

type ResourceType = 'youtube' | 'article' | 'tutorial'

const TABS: { key: ResourceType; label: string }[] = [
  { key: 'youtube', label: 'YouTube' },
  { key: 'article', label: 'Bài viết' },
  { key: 'tutorial', label: 'Hướng dẫn' },
]

interface CourseResourceTabsProps {
  courseId: number
}

export function CourseResourceTabs({ courseId }: CourseResourceTabsProps) {
  const [activeTab, setActiveTab] = useState<ResourceType>('youtube')
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingData, setEditingData] = useState<YoutubePlaylistRequest | ArticleRequest | TutorialRequest | null>(null)
  const [editingId, setEditingId] = useState<number | null>(null)

  const token = getAdminToken()

  const fetchMap = {
    youtube: (t: string) => adminApi.getYoutubePlaylists(t, courseId) as Promise<unknown>,
    article: (t: string) => adminApi.getArticles(t, courseId) as Promise<unknown>,
    tutorial: (t: string) => adminApi.getTutorials(t, courseId) as Promise<unknown>,
  }

  const { data, loading, error, refetch } = useAdminFetch(
    fetchMap[activeTab],
    [courseId, activeTab],
  )

  const openCreate = () => {
    setEditingId(null)
    setEditingData(null)
    setDialogOpen(true)
  }

  const openEdit = (item: YoutubePlaylistResponse | ArticleResponse | TutorialResponse) => {
    setEditingId(item.id)
    setEditingData({
      title: item.title,
      url: item.url,
      description: item.description ?? '',
      ...(activeTab === 'youtube' ? { channelName: (item as YoutubePlaylistResponse).channelName ?? '' } : {}),
      ...(activeTab === 'article' ? { author: (item as ArticleResponse).author ?? '' } : {}),
      ...(activeTab === 'tutorial' ? { type: (item as TutorialResponse).type ?? '' } : {}),
    })
    setDialogOpen(true)
  }

  const handleSubmit = async (data: YoutubePlaylistRequest | ArticleRequest | TutorialRequest) => {
    try {
      if (editingId) {
        const updateMap = {
          youtube: () => adminApi.updateYoutubePlaylist(token!, courseId, editingId, data as YoutubePlaylistRequest),
          article: () => adminApi.updateArticle(token!, courseId, editingId, data as ArticleRequest),
          tutorial: () => adminApi.updateTutorial(token!, courseId, editingId, data as TutorialRequest),
        }
        await updateMap[activeTab]()
      } else {
        const createMap = {
          youtube: () => adminApi.createYoutubePlaylist(token!, courseId, data as YoutubePlaylistRequest),
          article: () => adminApi.createArticle(token!, courseId, data as ArticleRequest),
          tutorial: () => adminApi.createTutorial(token!, courseId, data as TutorialRequest),
        }
        await createMap[activeTab]()
      }
      setDialogOpen(false)
      setEditingData(null)
      setEditingId(null)
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  const handleDelete = async (item: YoutubePlaylistResponse | ArticleResponse | TutorialResponse) => {
    if (!confirm('Xoá tài nguyên này?')) return
    try {
      const deleteMap = {
        youtube: () => adminApi.deleteYoutubePlaylist(token!, courseId, item.id),
        article: () => adminApi.deleteArticle(token!, courseId, item.id),
        tutorial: () => adminApi.deleteTutorial(token!, courseId, item.id),
      }
      await deleteMap[activeTab]()
      refetch()
    } catch (e) {
      console.error(e)
    }
  }

  const columns = {
    youtube: [
      { key: 'title', header: 'Tiêu đề', render: (r: YoutubePlaylistResponse) => <span>{r.title}</span> },
      { key: 'channelName', header: 'Kênh', render: (r: YoutubePlaylistResponse) => <span className="text-ink-secondary">{r.channelName ?? '-'}</span> },
      { key: 'createdAt', header: 'Ngày thêm', render: (r: YoutubePlaylistResponse) => <span className="text-ink-secondary">{new Date(r.createdAt).toLocaleDateString()}</span> },
      {
        key: 'actions', header: '', className: 'text-right',
        render: (r: YoutubePlaylistResponse) => (
          <div className="flex justify-end gap-2">
            <button onClick={() => openEdit(r)} className="btn-ghost text-xs">Sửa</button>
            <button onClick={() => handleDelete(r)} className="btn-ghost text-xs text-red-400">Xoá</button>
          </div>
        ),
      },
    ],
    article: [
      { key: 'title', header: 'Tiêu đề', render: (r: ArticleResponse) => <span>{r.title}</span> },
      { key: 'author', header: 'Tác giả', render: (r: ArticleResponse) => <span className="text-ink-secondary">{r.author ?? '-'}</span> },
      { key: 'createdAt', header: 'Ngày thêm', render: (r: ArticleResponse) => <span className="text-ink-secondary">{new Date(r.createdAt).toLocaleDateString()}</span> },
      {
        key: 'actions', header: '', className: 'text-right',
        render: (r: ArticleResponse) => (
          <div className="flex justify-end gap-2">
            <button onClick={() => openEdit(r)} className="btn-ghost text-xs">Sửa</button>
            <button onClick={() => handleDelete(r)} className="btn-ghost text-xs text-red-400">Xoá</button>
          </div>
        ),
      },
    ],
    tutorial: [
      { key: 'title', header: 'Tiêu đề', render: (r: TutorialResponse) => <span>{r.title}</span> },
      { key: 'type', header: 'Nền tảng', render: (r: TutorialResponse) => <span className="text-ink-secondary">{r.type ?? '-'}</span> },
      { key: 'createdAt', header: 'Ngày thêm', render: (r: TutorialResponse) => <span className="text-ink-secondary">{new Date(r.createdAt).toLocaleDateString()}</span> },
      {
        key: 'actions', header: '', className: 'text-right',
        render: (r: TutorialResponse) => (
          <div className="flex justify-end gap-2">
            <button onClick={() => openEdit(r)} className="btn-ghost text-xs">Sửa</button>
            <button onClick={() => handleDelete(r)} className="btn-ghost text-xs text-red-400">Xoá</button>
          </div>
        ),
      },
    ],
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <div className="flex gap-1">
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
        <button onClick={openCreate} className="btn-primary text-xs px-4 py-2">
          + Thêm {TABS.find((t) => t.key === activeTab)?.label}
        </button>
      </div>

      {loading && <AdminSpinner text="Đang tải tài nguyên..." />}
      {error && <AdminErrorBanner message={error} onRetry={refetch} />}

      {!!data && activeTab === 'youtube' && (
        <AdminTable
          columns={columns.youtube}
          data={data as unknown as YoutubePlaylistResponse[]}
          keyExtractor={(r) => r.id}
          emptyMessage="Chưa có YouTube playlist"
        />
      )}
      {!!data && activeTab === 'article' && (
        <AdminTable
          columns={columns.article}
          data={data as unknown as ArticleResponse[]}
          keyExtractor={(r) => r.id}
          emptyMessage="Chưa có bài viết"
        />
      )}
      {!!data && activeTab === 'tutorial' && (
        <AdminTable
          columns={columns.tutorial}
          data={data as unknown as TutorialResponse[]}
          keyExtractor={(r) => r.id}
          emptyMessage="Chưa có hướng dẫn"
        />
      )}

      <ResourceDialog
        open={dialogOpen}
        onClose={() => { setDialogOpen(false); setEditingData(null); setEditingId(null) }}
        onSubmit={handleSubmit}
        resourceType={activeTab}
        initial={editingData}
      />
    </div>
  )
}
