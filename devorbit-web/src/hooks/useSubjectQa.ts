import { useMutation } from '@tanstack/react-query'
import { apiPost } from '../lib/api'

export interface SubjectQaRequest {
    message: string
    sessionId?: string
}

export interface SubjectQaResponse {
    answer: string
    sessionId: string
    relevantNodeIds: number[]
    sources: string[]
    type: string
}

/**
 * Hook to send user queries to the AI Course Assistant.
 */
export function useSubjectQa() {
    return useMutation<SubjectQaResponse, Error, SubjectQaRequest>({
        mutationFn: async (payload: SubjectQaRequest) => {
            return apiPost<SubjectQaResponse>('/api/ai/subject-qa/query', payload)
        }
    })
}
