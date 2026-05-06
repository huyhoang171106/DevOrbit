import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { apiAdminGet, apiAdminPost, apiAdminPut, apiAdminDelete } from '../../lib/api'
import { getAdminToken } from '../../lib/auth'
import { useRequireAuth, useApiFetch } from '../../lib/hooks'
import { YoutubePlaylistDialog } from '../../components/admin/YoutubePlaylistDialog'
import { ArticleDialog } from '../../components/admin/ArticleDialog'
import { TutorialDialog } from '../../components/admin/TutorialDialog'
import type {
  YoutubePlaylistResponse, YoutubePlaylistRequest,
  ArticleResponse, ArticleRequest,
  TutorialResponse, TutorialRequest,
} from '../../types/api'

type Tab = 'youtube' | 'articles' | 'tutorials'

export function AdminCourseResourcesPage() {
  useRequireAuth()
  const { courseId } = useParams<{ courseId: string }>()
  const token = getAdminToken()!
  const cid = Number(courseId)

  const [tab, setTab] = useState<Tab>('youtube')

  // YouTube state
  const [ytDialog, setYtDialog] = useState(false)
  const [ytEdit, setYtEdit] = useState<YoutubePlaylistRequest | undefined>(undefined)
  const [ytEditId, setYtEditId] = useState<number | null>(null)
  const { data: ytList, loading: ytLoading, refetch: ytRefetch } = useApiFetch(
    () => apiAdminGet<YoutubePlaylistResponse[]>(`/api/admin/courses/${cid}/resources/youtube-playlists`, token),
    [cid, token],
  )

  // Article state
  const [artDialog, setArtDialog] = useState(false)
  const [artEdit, setArtEdit] = useState<ArticleRequest | undefined>(undefined)
  const [artEditId, setArtEditId] = useState<number | null>(null)
  const { data: artList, loading: artLoading, refetch: artRefetch } = useApiFetch(
    () => apiAdminGet<ArticleResponse[]>(`/api/admin/courses/${cid}/resources/articles`, token),
    [cid, token],
  )

  // Tutorial state
  const [tutDialog, setTutDialog] = useState(false)
  const [tutEdit, setTutEdit] = useState<TutorialRequest | undefined>(undefined)
  const [tutEditId, setTutEditId] = useState<number | null>(null)
  const { data: tutList, loading: tutLoading, refetch: tutRefetch } = useApiFetch(
    () => apiAdminGet<TutorialResponse[]>(`/api/admin/courses/${cid}/resources/tutorials`, token),
    [cid, token],
  )

  const tabs: { key: Tab; label: string }[] = [
    { key: 'youtube', label: 'YouTube Playlists' },
    { key: 'articles', label: 'Articles' },
    { key: 'tutorials', label: 'Tutorials' },
  ]

  async function handleYtSubmit(data: YoutubePlaylistRequest) {
    try {
      if (ytEditId) {
        await apiAdminPut(`/api/admin/courses/${cid}/resources/youtube-playlists/${ytEditId}`, token, data)
      } else {
        await apiAdminPost(`/api/admin/courses/${cid}/resources/youtube-playlists`, token, data)
      }
      setYtDialog(false); setYtEdit(undefined); setYtEditId(null); ytRefetch()
    } catch (e) { console.error(e) }
  }

  async function handleYtDelete(id: number) {
    if (!confirm('Delete this playlist?')) return
    try { await apiAdminDelete(`/api/admin/courses/${cid}/resources/youtube-playlists/${id}`, token); ytRefetch() }
    catch (e) { console.error(e) }
  }

  async function handleArtSubmit(data: ArticleRequest) {
    try {
      if (artEditId) {
        await apiAdminPut(`/api/admin/courses/${cid}/resources/articles/${artEditId}`, token, data)
      } else {
        await apiAdminPost(`/api/admin/courses/${cid}/resources/articles`, token, data)
      }
      setArtDialog(false); setArtEdit(undefined); setArtEditId(null); artRefetch()
    } catch (e) { console.error(e) }
  }

  async function handleArtDelete(id: number) {
    if (!confirm('Delete this article?')) return
    try { await apiAdminDelete(`/api/admin/courses/${cid}/resources/articles/${id}`, token); artRefetch() }
    catch (e) { console.error(e) }
  }

  async function handleTutSubmit(data: TutorialRequest) {
    try {
      if (tutEditId) {
        await apiAdminPut(`/api/admin/courses/${cid}/resources/tutorials/${tutEditId}`, token, data)
      } else {
        await apiAdminPost(`/api/admin/courses/${cid}/resources/tutorials`, token, data)
      }
      setTutDialog(false); setTutEdit(undefined); setTutEditId(null); tutRefetch()
    } catch (e) { console.error(e) }
  }

  async function handleTutDelete(id: number) {
    if (!confirm('Delete this tutorial?')) return
    try { await apiAdminDelete(`/api/admin/courses/${cid}/resources/tutorials/${id}`, token); tutRefetch() }
    catch (e) { console.error(e) }
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px]">
        <Link to="/admin/courses" className="text-xs text-emerald-400/70 hover:text-emerald-400 transition-colors font-medium">&larr; Back to Courses</Link>
        <h1 className="display-sm text-ink mt-3 mb-1">Course #{cid}</h1>
        <p className="body-sm text-ink-secondary">Course Resources</p>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 mb-[24px] border-b border-glass-border">
        {tabs.map((t) => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={`py-2 text-sm font-medium transition-colors border-b-2 -mb-[1px] ${
              tab === t.key
                ? 'text-emerald-400 border-emerald-400'
                : 'text-ink-secondary border-transparent hover:text-ink'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {/* YouTube Playlists Tab */}
      {tab === 'youtube' && (
        <>
          <div className="mb-[16px] flex justify-end">
            <button onClick={() => { setYtEdit(undefined); setYtEditId(null); setYtDialog(true) }} className="btn-primary text-sm px-4 py-2">
              <svg className="mr-2 h-4 w-4 inline" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 5v14M5 12h14" /></svg>
              Add Playlist
            </button>
          </div>
          {ytLoading ? <Spinner /> : (
            <div className="glass-card overflow-hidden border border-glass-border p-0">
              <table className="min-w-full text-sm">
                <thead><tr className="border-b border-glass-border bg-glass-surface-raised"><th className="table-header text-left font-medium text-ink-secondary py-3 px-4">Title</th><th className="table-header text-left font-medium text-ink-secondary py-3 px-4">Channel</th><th className="table-header text-right font-medium text-ink-secondary py-3 px-4">Actions</th></tr></thead>
                <tbody className="divide-y divide-glass-border bg-cosmic-surface">
                  {(ytList ?? []).length === 0 && <tr><td colSpan={3} className="px-4 py-10 text-center body-sm text-ink-secondary">No playlists.</td></tr>}
                  {(ytList ?? []).map((item) => (
                    <tr key={item.id} className="transition-colors hover:bg-glass-surface-raised">
                      <td className="table-cell py-3 px-4">
                        <div className="text-ink font-medium body-sm">{item.title}</div>
                        <a href={item.url} target="_blank" rel="noopener noreferrer" className="text-xs text-ink-secondary hover:text-emerald-400 truncate block max-w-[300px] mt-0.5">{item.url}</a>
                      </td>
                      <td className="table-cell text-ink-secondary body-sm py-3 px-4">{item.channelName || <span className="text-ink-secondary/50">—</span>}</td>
                      <td className="table-cell text-right py-3 px-4">
                        <div className="flex justify-end gap-2">
                          <button onClick={() => { setYtEdit({ title: item.title, url: item.url, description: item.description ?? '', channelName: item.channelName ?? '' }); setYtEditId(item.id); setYtDialog(true) }} className="btn-secondary !py-1 !px-3 !bg-glass-surface !text-xs !text-ink-secondary hover:!text-ink border border-glass-border">Edit</button>
                          <button onClick={() => handleYtDelete(item.id)} className="btn-secondary !py-1 !px-3 !bg-glass-surface !text-xs !text-red-400 hover:!bg-red-500/10 border border-red-500/30">Delete</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          <YoutubePlaylistDialog open={ytDialog} onClose={() => { setYtDialog(false); setYtEdit(undefined); setYtEditId(null) }} onSubmit={handleYtSubmit} initial={ytEdit} />
        </>
      )}

      {/* Articles Tab */}
      {tab === 'articles' && (
        <>
          <div className="mb-[16px] flex justify-end">
            <button onClick={() => { setArtEdit(undefined); setArtEditId(null); setArtDialog(true) }} className="btn-primary text-sm px-4 py-2">
              <svg className="mr-2 h-4 w-4 inline" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 5v14M5 12h14" /></svg>
              Add Article
            </button>
          </div>
          {artLoading ? <Spinner /> : (
            <div className="glass-card overflow-hidden border border-glass-border p-0">
              <table className="min-w-full text-sm">
                <thead><tr className="border-b border-glass-border bg-glass-surface-raised"><th className="table-header text-left font-medium text-ink-secondary py-3 px-4">Title</th><th className="table-header text-left font-medium text-ink-secondary py-3 px-4">Author</th><th className="table-header text-right font-medium text-ink-secondary py-3 px-4">Actions</th></tr></thead>
                <tbody className="divide-y divide-glass-border bg-cosmic-surface">
                  {(artList ?? []).length === 0 && <tr><td colSpan={3} className="px-4 py-10 text-center body-sm text-ink-secondary">No articles.</td></tr>}
                  {(artList ?? []).map((item) => (
                    <tr key={item.id} className="transition-colors hover:bg-glass-surface-raised">
                      <td className="table-cell py-3 px-4">
                        <div className="text-ink font-medium body-sm">{item.title}</div>
                        <a href={item.url} target="_blank" rel="noopener noreferrer" className="text-xs text-ink-secondary hover:text-emerald-400 truncate block max-w-[300px] mt-0.5">{item.url}</a>
                      </td>
                      <td className="table-cell text-ink-secondary body-sm py-3 px-4">{item.author || <span className="text-ink-secondary/50">—</span>}</td>
                      <td className="table-cell text-right py-3 px-4">
                        <div className="flex justify-end gap-2">
                          <button onClick={() => { setArtEdit({ title: item.title, url: item.url, author: item.author ?? '', description: item.description ?? '' }); setArtEditId(item.id); setArtDialog(true) }} className="btn-secondary !py-1 !px-3 !bg-glass-surface !text-xs !text-ink-secondary hover:!text-ink border border-glass-border">Edit</button>
                          <button onClick={() => handleArtDelete(item.id)} className="btn-secondary !py-1 !px-3 !bg-glass-surface !text-xs !text-red-400 hover:!bg-red-500/10 border border-red-500/30">Delete</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          <ArticleDialog open={artDialog} onClose={() => { setArtDialog(false); setArtEdit(undefined); setArtEditId(null) }} onSubmit={handleArtSubmit} initial={artEdit} />
        </>
      )}

      {/* Tutorials Tab */}
      {tab === 'tutorials' && (
        <>
          <div className="mb-[16px] flex justify-end">
            <button onClick={() => { setTutEdit(undefined); setTutEditId(null); setTutDialog(true) }} className="btn-primary text-sm px-4 py-2">
              <svg className="mr-2 h-4 w-4 inline" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 5v14M5 12h14" /></svg>
              Add Tutorial
            </button>
          </div>
          {tutLoading ? <Spinner /> : (
            <div className="glass-card overflow-hidden border border-glass-border p-0">
              <table className="min-w-full text-sm">
                <thead><tr className="border-b border-glass-border bg-glass-surface-raised"><th className="table-header text-left font-medium text-ink-secondary py-3 px-4">Title</th><th className="table-header text-left font-medium text-ink-secondary py-3 px-4">Type</th><th className="table-header text-right font-medium text-ink-secondary py-3 px-4">Actions</th></tr></thead>
                <tbody className="divide-y divide-glass-border bg-cosmic-surface">
                  {(tutList ?? []).length === 0 && <tr><td colSpan={3} className="px-4 py-10 text-center body-sm text-ink-secondary">No tutorials.</td></tr>}
                  {(tutList ?? []).map((item) => (
                    <tr key={item.id} className="transition-colors hover:bg-glass-surface-raised">
                      <td className="table-cell py-3 px-4">
                        <div className="text-ink font-medium body-sm">{item.title}</div>
                        <a href={item.url} target="_blank" rel="noopener noreferrer" className="text-xs text-ink-secondary hover:text-emerald-400 truncate block max-w-[300px] mt-0.5">{item.url}</a>
                      </td>
                      <td className="table-cell py-3 px-4">
                        {item.type ? <span className="badge-tag bg-glass-surface text-ink-secondary border-glass-border">{item.type}</span> : <span className="text-ink-secondary/50">—</span>}
                      </td>
                      <td className="table-cell text-right py-3 px-4">
                        <div className="flex justify-end gap-2">
                          <button onClick={() => { setTutEdit({ title: item.title, url: item.url, type: item.type ?? '', description: item.description ?? '' }); setTutEditId(item.id); setTutDialog(true) }} className="btn-secondary !py-1 !px-3 !bg-glass-surface !text-xs !text-ink-secondary hover:!text-ink border border-glass-border">Edit</button>
                          <button onClick={() => handleTutDelete(item.id)} className="btn-secondary !py-1 !px-3 !bg-glass-surface !text-xs !text-red-400 hover:!bg-red-500/10 border border-red-500/30">Delete</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          <TutorialDialog open={tutDialog} onClose={() => { setTutDialog(false); setTutEdit(undefined); setTutEditId(null) }} onSubmit={handleTutSubmit} initial={tutEdit} />
        </>
      )}
    </div>
  )
}

function Spinner() {
  return (
    <div className="flex items-center justify-center py-[96px]">
      <div className="flex items-center gap-3 body-sm text-ink-secondary">
        <svg className="h-5 w-5 animate-spin text-emerald-400" viewBox="0 0 24 24" fill="none">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
        </svg>
        Loading...
      </div>
    </div>
  )
}
