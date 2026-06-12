import { useMutation } from '@tanstack/react-query'
import { apiPost } from '../lib/api'
import { getStudentToken } from '../lib/auth'

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
    searchResults?: WebSearchResult[]
}

export interface WebSearchResult {
    url: string
    title?: string
    description?: string
    position?: number
    highlights?: string[]
    publishedDate?: string
    author?: string
    sourceProvider?: string
}

// ─── Streaming types ───

export type SubjectQaStreamStage =
    | 'session'
    | 'analyze'
    | 'devorbit_context'
    | 'rag'
    | 'web_search'
    | 'web_read'
    | 'answer'
    | 'done'
    | 'error'

export type SubjectQaStreamEvent =
    | { type: 'status'; stage: SubjectQaStreamStage; message: string }
    | { type: 'search_result'; searchResult: WebSearchResult }
    | { type: 'delta'; content: string }
    | { type: 'complete'; response: SubjectQaResponse }
    | { type: 'error'; stage: 'error'; message: string }

export interface SubjectQaStreamHandlers {
    onStatus?: (event: Extract<SubjectQaStreamEvent, { type: 'status' }>) => void
    onSearchResult?: (result: WebSearchResult) => void
    onDelta?: (content: string) => void
    onComplete?: (response: SubjectQaResponse) => void
    onError?: (message: string) => void
}

/**
 * Consume an SSE stream from the /api/ai/subject-qa/stream endpoint.
 * Parses SSE events manually using fetch + ReadableStream.
 */
export async function streamSubjectQa(
    payload: SubjectQaRequest,
    handlers: SubjectQaStreamHandlers,
    signal?: AbortSignal,
): Promise<void> {
    const studentToken = getStudentToken()
    const response = await fetch('/api/ai/subject-qa/stream', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Accept: 'text/event-stream',
            ...(studentToken ? { Authorization: `Bearer ${studentToken}` } : {}),
        },
        body: JSON.stringify(payload),
        signal,
    })

    if (!response.ok) {
        const body = await response.text().catch(() => '')
        throw new Error(body || `Streaming request failed: ${response.status}`)
    }

    if (!response.body) {
        throw new Error('Streaming is not supported in this browser')
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    let eventType = ''
    let dataLines: string[] = []

    function flushEvent() {
        if (!eventType || dataLines.length === 0) return
        const rawData = dataLines.join('\n')

        try {
            const parsed = JSON.parse(rawData) as SubjectQaStreamEvent

            switch (parsed.type) {
                case 'status':
                    handlers.onStatus?.(parsed)
                    break
                case 'search_result':
                    handlers.onSearchResult?.(parsed.searchResult)
                    break
                case 'delta':
                    handlers.onDelta?.(parsed.content)
                    break
                case 'complete':
                    handlers.onComplete?.(parsed.response)
                    break
                case 'error':
                    handlers.onError?.(parsed.message)
                    break
            }
        } catch {
            // Ignore malformed JSON in stream
        }

        eventType = ''
        dataLines = []
    }

    try {
        while (true) {
            const { done, value } = await reader.read()
            if (done) {
                flushEvent()
                break
            }

            buffer += decoder.decode(value, { stream: true })
            const blocks = buffer.split('\n\n')

            // Keep the last potentially incomplete block in the buffer
            buffer = blocks.pop() ?? ''

            for (const block of blocks) {
                const lines = block.split('\n')
                for (const line of lines) {
                    if (line.startsWith('event:')) {
                        // Flush previous event if starting a new one
                        if (eventType) flushEvent()
                        eventType = line.slice(6).trim()
                    } else if (line.startsWith('data:')) {
                        dataLines.push(line.slice(5).trim())
                    }
                }
                // Flush at end of each blank-line-delimited block
                flushEvent()
            }
        }
    } finally {
        reader.releaseLock()
    }
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
