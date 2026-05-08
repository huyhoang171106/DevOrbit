import { useQuery } from '@tanstack/react-query'
import { apiGet } from '../lib/api'
import type { GraphResponse } from '../types/api'

export function useKnowledgeGraph() {
    return useQuery<GraphResponse>({
        queryKey: ['knowledge-graph'],
        queryFn: () => apiGet<GraphResponse>('/api/courses/graph'),
        staleTime: 5 * 60 * 1000,  // 5 min — graph data rarely changes
        gcTime: 30 * 60 * 1000,    // 30 min garbage collection
    })
}
