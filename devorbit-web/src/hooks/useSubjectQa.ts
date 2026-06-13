import { useMutation } from '@tanstack/react-query'
import { apiPost, apiBaseUrl, buildApiUrl } from '../lib/api'
import { getStudentToken } from '../lib/auth'
import type { RoadmapResponse } from './useAiRoadmap'

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
    roadmap?: RoadmapResponse
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
    const url = buildApiUrl(apiBaseUrl, '/api/ai/subject-qa/stream')
    const response = await fetch(url, {
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
        const currentType = eventType || 'message'
        if (dataLines.length === 0) return
        const rawData = dataLines.join('\n')

        try {
            const parsed = JSON.parse(rawData) as SubjectQaStreamEvent

            const finalType = (parsed.type || currentType) as SubjectQaStreamEvent['type']

            switch (finalType) {
                case 'status':
                    handlers.onStatus?.(parsed as any)
                    break
                case 'search_result':
                    handlers.onSearchResult?.((parsed as any).searchResult)
                    break
                case 'delta':
                    handlers.onDelta?.((parsed as any).content)
                    break
                case 'complete':
                    handlers.onComplete?.((parsed as any).response)
                    break
                case 'error':
                    handlers.onError?.((parsed as any).message)
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
                if (buffer.trim()) {
                    const block = buffer.replace(/\r\n/g, '\n')
                    const lines = block.split('\n')
                    for (const line of lines) {
                        if (line.startsWith('event:')) {
                            if (eventType) flushEvent()
                            eventType = line.slice(6).trim()
                        } else if (line.startsWith('data:')) {
                            dataLines.push(line.slice(5).trim())
                        }
                    }
                    flushEvent()
                }
                flushEvent()
                break
            }

            buffer += decoder.decode(value, { stream: true })
            const normalizedBuffer = buffer.replace(/\r\n/g, '\n')
            const blocks = normalizedBuffer.split('\n\n')

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
