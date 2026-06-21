import { Star, Eye, ThumbsUp, ThumbsDown, Books, Code } from '@phosphor-icons/react'
import type { DevOrbitRepoInfo } from '../../hooks/useSubjectQa'

interface DevOrbitRepoCardsProps {
    repos: DevOrbitRepoInfo[]
}

function getUsefulnessColor(rating: string | null): string {
    switch (rating) {
        case 'excellent': return 'text-emerald-400'
        case 'good': return 'text-blue-400'
        case 'average': return 'text-yellow-400'
        case 'limited': return 'text-orange-400'
        case 'unusable': return 'text-red-400'
        default: return 'text-zinc-400'
    }
}

function getUsefulnessLabel(rating: string | null): string {
    switch (rating) {
        case 'excellent': return 'Xuất sắc'
        case 'good': return 'Tốt'
        case 'average': return 'Trung bình'
        case 'limited': return 'Hạn chế'
        case 'unusable': return 'Không sử dụng'
        default: return ''
    }
}

function RatingStars({ rating }: { rating: number }) {
    const full = Math.floor(rating)
    const hasHalf = rating - full >= 0.3
    return (
        <span className="inline-flex items-center gap-0.5">
            {Array.from({ length: 5 }, (_, i) => (
                <Star
                    key={i}
                    className={`h-3.5 w-3.5 ${
                        i < full ? 'text-amber-400 fill-amber-400' :
                        i === full && hasHalf ? 'text-amber-400 fill-amber-400/50' :
                        'text-zinc-600'
                    }`}
                    weight="fill"
                />
            ))}
            <span className="text-[12px] text-zinc-400 ml-1">{rating.toFixed(1)}</span>
        </span>
    )
}

function RepoCard({ repo }: { repo: DevOrbitRepoInfo }) {
    return (
        <a
            href={repo.githubUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="block rounded-xl border border-zinc-800/60 bg-zinc-900/40 p-3.5 hover:border-orbit-accent/40 hover:bg-zinc-900/60 transition-all group"
        >
            <div className="flex items-start justify-between gap-2">
                <div className="min-w-0 flex-1">
                    <h4 className="text-[13px] font-semibold text-zinc-100 truncate group-hover:text-orbit-accent transition-colors">
                        {repo.name}
                    </h4>
                    {repo.courseCode && (
                        <span className="inline-flex items-center gap-1 mt-0.5 text-[11px] text-zinc-500">
                            <Books className="h-3 w-3" weight="fill" />
                            {repo.courseCode}{repo.courseName ? ` - ${repo.courseName}` : ''}
                        </span>
                    )}
                </div>
                {repo.usefulnessRating && (
                    <span className={`shrink-0 text-[11px] font-medium px-2 py-0.5 rounded-full border border-current/20 ${getUsefulnessColor(repo.usefulnessRating)}`}>
                        {getUsefulnessLabel(repo.usefulnessRating)}
                    </span>
                )}
            </div>

            {repo.description && (
                <p className="text-[12px] text-zinc-400 mt-1.5 line-clamp-2 leading-relaxed">
                    {repo.description}
                </p>
            )}

            <div className="flex flex-wrap items-center gap-3 mt-2.5 text-[12px] text-zinc-500">
                {repo.stars != null && repo.stars > 0 && (
                    <span className="inline-flex items-center gap-1">
                        <Star className="h-3.5 w-3.5 text-amber-400" weight="fill" />
                        {repo.stars}
                    </span>
                )}
                {repo.voteScore !== 0 && (
                    <span className={`inline-flex items-center gap-1 ${repo.voteScore > 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                        {repo.voteScore > 0 ? <ThumbsUp className="h-3.5 w-3.5" weight="fill" /> : <ThumbsDown className="h-3.5 w-3.5" weight="fill" />}
                        {repo.voteScore > 0 ? '+' : ''}{repo.voteScore}
                    </span>
                )}
                {repo.averageRating > 0 && (
                    <span className="inline-flex items-center gap-1">
                        <RatingStars rating={repo.averageRating} />
                        <span className="text-zinc-600">({repo.reviewCount})</span>
                    </span>
                )}
                {repo.viewCount != null && repo.viewCount > 0 && (
                    <span className="inline-flex items-center gap-1">
                        <Eye className="h-3.5 w-3.5" />
                        {repo.viewCount}
                    </span>
                )}
            </div>

            <div className="flex flex-wrap gap-1.5 mt-2">
                {repo.primaryLanguage && (
                    <span className="inline-flex items-center gap-1 text-[11px] px-1.5 py-0.5 rounded-md bg-zinc-800/60 text-zinc-300">
                        <Code className="h-3 w-3" weight="fill" />
                        {repo.primaryLanguage}
                    </span>
                )}
                {repo.techStacks.map((tech) => (
                    <span key={tech} className="text-[11px] px-1.5 py-0.5 rounded-md bg-orbit-accent/10 text-orbit-accent/80 border border-orbit-accent/10">
                        {tech}
                    </span>
                ))}
            </div>
        </a>
    )
}

export function DevOrbitRepoCards({ repos }: DevOrbitRepoCardsProps) {
    if (!repos || repos.length === 0) return null

    return (
        <div className="mt-3 space-y-2">
            <p className="text-[12px] font-medium text-zinc-400 uppercase tracking-wide">
                Repository từ DevOrbit
            </p>
            <div className="grid gap-2">
                {repos.map((repo) => (
                    <RepoCard key={repo.id} repo={repo} />
                ))}
            </div>
        </div>
    )
}
