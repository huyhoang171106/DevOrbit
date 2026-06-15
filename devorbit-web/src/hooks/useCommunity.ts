import { useQuery, useQueryClient } from '@tanstack/react-query'
import { apiStudentGet, apiGet } from '../lib/api'
import type {
  ChatChannelResponse,
  ChatMessageResponse,
  PaginatedMessagesResponse,
  RepoSocialInfoResponse,
  ReviewSummaryResponse,
  StudentProfileResponse,
} from '../types/api'

const CHANNELS_CACHE_KEY = 'devorbit-channels-cache'
const MESSAGES_CACHE_KEY = 'devorbit-messages-cache'
const MESSAGES_CACHE_TTL = 10 * 60 * 1000 // 10 minutes

function getCachedChannels(): ChatChannelResponse[] | undefined {
  try {
    const raw = localStorage.getItem(CHANNELS_CACHE_KEY)
    if (!raw) return undefined
    const parsed = JSON.parse(raw)
    if (Date.now() - parsed.timestamp > 10 * 60 * 1000) {
      localStorage.removeItem(CHANNELS_CACHE_KEY)
      return undefined
    }
    return parsed.data
  } catch {
    return undefined
  }
}

function setCachedChannels(data: ChatChannelResponse[]) {
  try {
    localStorage.setItem(CHANNELS_CACHE_KEY, JSON.stringify({ data, timestamp: Date.now() }))
  } catch {}
}

// ─── Messages cache ───────────────────────────────────────────────────────────

type CachedMessages = {
  messages: ChatMessageResponse[]
  totalPages: number
  fetchedAt: number
}

function getMessagesCacheKey(channelId: number): string {
  return `${MESSAGES_CACHE_KEY}-${channelId}`
}

export function getCachedMessages(channelId: number): CachedMessages | null {
  try {
    const raw = localStorage.getItem(getMessagesCacheKey(channelId))
    if (!raw) return null
    const parsed: CachedMessages = JSON.parse(raw)
    if (Date.now() - parsed.fetchedAt > MESSAGES_CACHE_TTL) {
      localStorage.removeItem(getMessagesCacheKey(channelId))
      return null
    }
    return parsed
  } catch {
    return null
  }
}

export function setCachedMessages(channelId: number, messages: ChatMessageResponse[], totalPages: number) {
  try {
    localStorage.setItem(
      getMessagesCacheKey(channelId),
      JSON.stringify({ messages, totalPages, fetchedAt: Date.now() }),
    )
  } catch {}
}

// ─── Parallel fetch all pages ──────────────────────────────────────────────────

export async function fetchAllMessages(channelId: number, totalPages: number, size: number = 50): Promise<ChatMessageResponse[]> {
  const pageRequests = Array.from({ length: totalPages }, (_, i) =>
    apiStudentGet<PaginatedMessagesResponse>(
      `/api/student/community/channels/${channelId}/messages?page=${i}&size=${size}`,
    ),
  )
  const results = await Promise.all(pageRequests)
  // Server returns pages newest-first, so reverse each and flatten
  return results
    .map((r) => [...r.content].reverse())
    .flat()
}

// ─── Hooks ────────────────────────────────────────────────────────────────────

const FALLBACK_CHANNELS: ChatChannelResponse[] = [
  { id: -1, channelId: 'general', name: 'General', type: 'GENERAL', referenceId: null },
  { id: -2, channelId: 'study', name: 'Học tập', type: 'GENERAL', referenceId: null },
  { id: -3, channelId: 'relax', name: 'Thư giãn', type: 'GENERAL', referenceId: null },
]

export function useChannels() {
  return useQuery<ChatChannelResponse[]>({
    queryKey: ['community', 'channels'],
    queryFn: async () => {
      const data = await apiStudentGet<ChatChannelResponse[]>('/api/student/community')
      setCachedChannels(data)
      return data
    },
    placeholderData: getCachedChannels() ?? FALLBACK_CHANNELS,
    staleTime: 5 * 60 * 1000,
    gcTime: 30 * 60 * 1000,
  })
}

export function useChannelMessages(channelId: number | null, page: number, size: number = 50) {
  return useQuery<PaginatedMessagesResponse>({
    queryKey: ['community', 'messages', channelId, page],
    queryFn: () =>
      apiStudentGet<PaginatedMessagesResponse>(
        `/api/student/community/channels/${channelId}/messages?page=${page}&size=${size}`,
      ),
    enabled: channelId !== null && channelId > 0,
    staleTime: 0,
    gcTime: 5 * 60 * 1000,
  })
}

export function useInvalidateChannelMessages() {
  const queryClient = useQueryClient()
  return (channelId: number) => {
    queryClient.invalidateQueries({ queryKey: ['community', 'messages', channelId] })
  }
}

export function useCurrentStudent() {
  return useQuery<StudentProfileResponse>({
    queryKey: ['student', 'me'],
    queryFn: () => apiStudentGet<StudentProfileResponse>('/api/student/me'),
    staleTime: 5 * 60 * 1000,
    gcTime: 30 * 60 * 1000,
  })
}

export function useRepoSocialInfo(repoId: number | undefined) {
  return useQuery<RepoSocialInfoResponse>({
    queryKey: ['repos', repoId, 'social-info'],
    queryFn: () => apiGet<RepoSocialInfoResponse>(`/api/repos/${repoId}/social-info`),
    enabled: repoId !== undefined && !Number.isNaN(repoId),
    staleTime: 30 * 1000,
    gcTime: 5 * 60 * 1000,
  })
}

export function useCourseReviews(courseId: number) {
  return useQuery<ReviewSummaryResponse>({
    queryKey: ['courses', courseId, 'reviews'],
    queryFn: () => apiGet<ReviewSummaryResponse>(`/api/courses/${courseId}/reviews`),
    staleTime: 30 * 1000,
    gcTime: 5 * 60 * 1000,
  })
}