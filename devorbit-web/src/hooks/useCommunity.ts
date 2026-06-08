import { useQuery } from '@tanstack/react-query'
import { apiStudentGet, apiGet } from '../lib/api'
import type {
  ChatChannelResponse,
  PaginatedMessagesResponse,
  RepoSocialInfoResponse,
  ReviewSummaryResponse,
} from '../types/api'

const CHANNELS_CACHE_KEY = 'devorbit-channels-cache'

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

export function useChannels() {
  return useQuery<ChatChannelResponse[]>({
    queryKey: ['community', 'channels'],
    queryFn: async () => {
      const data = await apiStudentGet<ChatChannelResponse[]>('/api/student/community')
      setCachedChannels(data)
      return data
    },
    placeholderData: getCachedChannels(),
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
    enabled: channelId !== null,
    staleTime: 30 * 1000,
    gcTime: 5 * 60 * 1000,
  })
}

export function useRepoSocialInfo(repoId: number) {
  return useQuery<RepoSocialInfoResponse>({
    queryKey: ['repos', repoId, 'social-info'],
    queryFn: () => apiGet<RepoSocialInfoResponse>(`/api/repos/${repoId}/social-info`),
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
