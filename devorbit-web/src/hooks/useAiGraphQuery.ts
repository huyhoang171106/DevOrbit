import { useMutation } from '@tanstack/react-query'

export interface AiQueryResponse {
    answer: string
    relevantNodeIds: number[]
    type: string
}

/**
 * Queries the knowledge graph using natural language.
 * Backend parses the question with pattern matching against graph data.
 */
export function useAiGraphQuery() {
    return useMutation<AiQueryResponse, Error, string>({
        mutationFn: async (query: string) => {
            const response = await fetch('/api/ai/knowledge-graph/query', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ query })
            })

            if (!response.ok) {
                const errorText = await response.text().catch(() => 'Unknown error')
                throw new Error(`Lỗi từ máy chủ: ${response.status}. ${errorText}`)
            }

            return response.json()
        }
    })
}
