import { useMutation } from '@tanstack/react-query'

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
            const response = await fetch('/api/ai/subject-qa/query', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })

            if (!response.ok) {
                const rawBody = await response.text().catch(() => '')
                let message: string
                try {
                    const parsed = JSON.parse(rawBody)
                    message = parsed.error || parsed.message || rawBody
                } catch {
                    message = rawBody || `Request failed: ${response.status}`
                }
                throw new Error(message)
            }

            return response.json()
        }
    })
}
