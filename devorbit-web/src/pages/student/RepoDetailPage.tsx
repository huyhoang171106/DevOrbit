import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { apiGet } from '../../lib/api'
import type { RepoSummary, AiResponse } from '../../types/api'

export function RepoDetailPage() {
  const { repoId } = useParams<{ repoId: string }>()
  const [repo, setRepo] = useState<RepoSummary | null>(null)
  const [summary, setSummary] = useState<AiResponse | null>(null)
  const [advice, setAdvice] = useState<AiResponse | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!repoId) return
    setLoading(true)
    
    Promise.all([
      apiGet<RepoSummary>(`/api/repos/${repoId}`),
      apiGet<AiResponse>(`/api/ai/repo/${repoId}/summary`),
      apiGet<AiResponse>(`/api/ai/repo/${repoId}/advice`)
    ])
      .then(([repoData, summaryData, adviceData]) => {
        setRepo(repoData)
        setSummary(summaryData)
        setAdvice(adviceData)
      })
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [repoId])

  if (loading) {
    return (
      <div className="relative min-h-[80vh] flex items-center justify-center overflow-hidden">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 h-[500px] w-[500px] bg-emerald-500/[0.05] blur-[150px] rounded-full animate-pulse" />
        <div className="relative flex flex-col items-center gap-6">
          <div className="relative h-16 w-16">
            <div className="absolute inset-0 rounded-full border-4 border-emerald-500/10" />
            <div className="absolute inset-0 rounded-full border-t-4 border-emerald-500 animate-spin shadow-[0_0_15px_rgba(16,185,129,0.5)]" />
          </div>
          <div className="flex flex-col items-center">
            <p className="text-[12px] font-black text-emerald-500 tracking-[0.4em] uppercase mb-2">Analyzing Node</p>
            <p className="heading-4 text-ink animate-pulse tracking-wide font-bold">RESOURCE SYNCHRONIZATION</p>
          </div>
        </div>
      </div>
    )
  }

  if (!repo) {
    return (
      <div className="max-w-[800px] mx-auto px-6 py-24 text-center">
        <div className="h-20 w-20 rounded-full bg-glass-surface flex items-center justify-center mx-auto mb-8 border border-glass-border">
          <svg className="h-10 w-10 text-ink-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008h-.008v-.008z" />
          </svg>
        </div>
        <h1 className="display-sm mb-4">Resource Not Located</h1>
        <p className="body-md text-ink-muted mb-10 max-w-sm mx-auto">
          The academic resource node you are looking for does not exist in the current knowledge matrix.
        </p>
        <Link to="/courses" className="btn-primary px-8 py-3 uppercase tracking-[0.2em] text-[12px]">
          Return to Catalog
        </Link>
      </div>
    )
  }

  return (
    <div className="relative w-full overflow-hidden min-h-screen pb-24">
      {/* Background elements */}
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute top-[-10%] right-[-10%] w-[50%] h-[700px] bg-emerald-500/[0.03] blur-[150px] rounded-full" />
        <div className="absolute bottom-[10%] left-[-10%] w-[40%] h-[600px] bg-indigo-500/[0.03] blur-[120px] rounded-full" />
      </div>

      <div className="relative z-10 max-w-[1000px] mx-auto px-6 pt-12 md:pt-20">
        <Link
          to={repo.courseId ? `/courses/${repo.courseId}` : "/courses"}
          className="mb-10 inline-flex items-center gap-3 text-[11px] font-black uppercase tracking-[0.2em] text-ink-secondary hover:text-emerald-500 transition-all group"
        >
          <div className="h-8 w-8 rounded-full border border-glass-border flex items-center justify-center group-hover:border-emerald-500/30 group-hover:bg-emerald-500/5 transition-all">
            <svg className="h-3.5 w-3.5 transition-transform group-hover:-translate-x-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
              <path d="M19 12H5M12 19l-7-7 7-7" />
            </svg>
          </div>
          Return to Node
        </Link>

        <div className="grid lg:grid-cols-12 gap-10">
          <div className="lg:col-span-12">
            <div className="glass-card-glow p-8 md:p-12 border-emerald-500/20">
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-8 mb-10">
                <div className="space-y-4">
                  <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/20">
                    <div className="h-1.5 w-1.5 rounded-full bg-emerald-500 shadow-[0_0_5px_rgba(16,185,129,0.5)]" />
                    <span className="text-[10px] font-black uppercase tracking-widest text-emerald-500">Repository Node</span>
                  </div>
                  <h1 className="hero-display text-4xl md:text-6xl leading-tight tracking-tight">{repo.displayName}</h1>
                </div>

                {repo.stars !== null && repo.stars > 0 && (
                  <div className="flex flex-col items-end">
                    <div className="flex items-center gap-2.5 bg-amber-500/5 px-5 py-3 rounded-2xl border border-amber-500/20 text-amber-500 shadow-sm">
                      <svg className="h-5 w-5 fill-current" viewBox="0 0 24 24">
                        <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
                      </svg>
                      <span className="text-xl font-black tracking-tighter">{repo.stars}</span>
                    </div>
                    <span className="text-[10px] font-black uppercase tracking-[0.2em] text-amber-500/60 mt-2">Node Appreciation</span>
                  </div>
                )}
              </div>

              <div className="max-w-3xl mb-12">
                <p className="body-md text-xl text-ink leading-relaxed opacity-90">
                  {repo.description || "Comprehensive implementation of course concepts and standardized academic modules."}
                </p>
              </div>

              <div className="flex flex-wrap items-center gap-3 mb-12">
                <span className="inline-flex items-center gap-2 rounded-xl bg-glass-surface px-4 py-2 border border-glass-border">
                  <span className="h-2 w-2 rounded-full bg-emerald-500 shadow-[0_0_5px_rgba(16,185,129,0.5)]" />
                  <span className="text-xs font-black uppercase tracking-wider text-ink-secondary">{repo.primaryLanguage}</span>
                </span>
                {repo.techStacks.map((stack) => (
                  <span key={stack} className="px-4 py-2 rounded-xl bg-emerald-500/5 border border-emerald-500/10 text-[11px] font-black uppercase tracking-widest text-emerald-500/70">
                    {stack}
                  </span>
                ))}
              </div>

              <a
                href={repo.githubUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="btn-primary inline-flex items-center gap-3 px-8 py-4 uppercase tracking-[0.2em] text-[13px]"
              >
                Access Source Code
                <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6M15 3h6v6M10 14L21 3" />
                </svg>
              </a>
            </div>
          </div>

          <div className="lg:col-span-12 grid md:grid-cols-2 gap-8">
            {summary && (
              <div className="glass-card-glow p-10 border-emerald-500/10 hover:border-emerald-500/30 transition-all duration-500 group">
                <div className="h-12 w-12 rounded-2xl bg-emerald-500/5 flex items-center justify-center text-emerald-500 border border-emerald-500/10 mb-8 group-hover:bg-emerald-500/10 transition-all">
                  <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364-6.364l-.707.707M6.343 17.657l-.707.707m12.728 0l-.707-.707M6.343 6.343l-.707-.707M12 8a4 4 0 100 8 4 4 0 000-8z" />
                  </svg>
                </div>
                <h3 className="heading-3 text-white mb-6 flex items-center gap-4">
                  Node Analysis
                </h3>
                <div className="prose prose-invert prose-sm">
                  <p className="body-md text-ink-secondary leading-[1.8] text-lg">
                    {summary.content}
                  </p>
                </div>
              </div>
            )}

            {advice && (
              <div className="glass-card-glow p-10 border-indigo-500/10 hover:border-indigo-500/30 transition-all duration-500 group">
                <div className="h-12 w-12 rounded-2xl bg-indigo-500/5 flex items-center justify-center text-indigo-400 border border-indigo-500/10 mb-8 group-hover:bg-indigo-500/10 transition-all">
                  <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12 14l9-5-9-5-9 5 9 5z" />
                    <path d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z" />
                  </svg>
                </div>
                <h3 className="heading-3 text-white mb-6 flex items-center gap-4">
                  Academic Strategy
                </h3>
                <div className="prose prose-invert prose-sm">
                  <p className="body-md text-ink-secondary leading-[1.8] text-lg italic">
                    {advice.content}
                  </p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

