import { useQuery } from '@tanstack/react-query'
import { apiGet } from '../lib/api'
import type { CourseSummary } from '../types/api'

export function useCourseList() {
  return useQuery<CourseSummary[]>({
    queryKey: ['courses', 'all'],
    queryFn: () => apiGet<CourseSummary[]>('/api/courses'),
    staleTime: 5 * 60 * 1000,
    gcTime: 30 * 60 * 1000,
  })
}
