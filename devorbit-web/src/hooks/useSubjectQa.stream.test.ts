// @vitest-environment jsdom

import { describe, expect, test, vi } from 'vitest'
import type { WebSearchResult, SubjectQaResponse } from './useSubjectQa'

// We must import after mocks are set up
let streamSubjectQa: typeof import('./useSubjectQa').streamSubjectQa

// Mock fetch before importing the module
const mockFetch = vi.fn()
vi.stubGlobal('fetch', mockFetch)

async function loadModule() {
    if (!streamSubjectQa) {
        const mod = await import('./useSubjectQa')
        streamSubjectQa = mod.streamSubjectQa
    }
}

function createMockReadableStream(chunks: string[]): ReadableStream<Uint8Array> {
    const encoder = new TextEncoder()
    let index = 0

    return new ReadableStream({
        pull(controller) {
            if (index < chunks.length) {
                controller.enqueue(encoder.encode(chunks[index]))
                index++
            } else {
                controller.close()
            }
        },
    })
}

function eventBlock(eventName: string, data: unknown): string {
    return `event:${eventName}\ndata:${JSON.stringify(data)}\n\n`
}

describe('streamSubjectQa SSE parser', () => {
    test('dispatches status, search_result, delta, and complete events in order', async () => {
        await loadModule()

        const searchResult: WebSearchResult = {
            url: 'https://example.com/se104',
            title: 'SE104 material',
            description: 'Study guide',
            position: 1,
            highlights: ['Important'],
            publishedDate: '2026-01-01',
            author: 'Author',
            sourceProvider: 'exa',
        }

        const mockResponse: SubjectQaResponse = {
            answer: 'Final answer.',
            sessionId: 'sess-1',
            relevantNodeIds: [1],
            sources: ['https://example.com'],
            type: 'SEARCH',
            searchResults: [searchResult],
        }

        mockFetch.mockResolvedValue({
            ok: true,
            body: createMockReadableStream([
                eventBlock('status', { type: 'status', stage: 'session', message: 'Đang mở phiên chat' }),
                eventBlock('status', { type: 'status', stage: 'analyze', message: 'Tìm thấy mã môn: SE104' }),
                eventBlock('search_result', { type: 'search_result', searchResult }),
                eventBlock('delta', { type: 'delta', content: 'Xin ' }),
                eventBlock('delta', { type: 'delta', content: 'chào!' }),
                eventBlock('complete', { type: 'complete', stage: 'done', message: 'Hoàn tất', response: mockResponse }),
            ]),
            headers: new Headers(),
            status: 200,
        })

        const statusEvents: { stage: string; message: string }[] = []
        const searchResults: WebSearchResult[] = []
        let accumulatedDelta = ''
        let completeResponse: SubjectQaResponse | null = null
        let errorMessage: string | null = null

        await streamSubjectQa(
            { message: 'test', sessionId: undefined },
            {
                onStatus: (evt) => statusEvents.push({ stage: evt.stage, message: evt.message }),
                onSearchResult: (result) => searchResults.push(result),
                onDelta: (content) => { accumulatedDelta += content },
                onComplete: (resp) => { completeResponse = resp },
                onError: (msg) => { errorMessage = msg },
            },
        )

        expect(statusEvents).toHaveLength(2)
        expect(statusEvents[0].stage).toBe('session')
        expect(statusEvents[0].message).toBe('Đang mở phiên chat')
        expect(statusEvents[1].stage).toBe('analyze')

        expect(searchResults).toHaveLength(1)
        expect(searchResults[0].url).toBe('https://example.com/se104')

        expect(accumulatedDelta).toBe('Xin chào!')

        expect(completeResponse).not.toBeNull()
        expect(completeResponse!.answer).toBe('Final answer.')
        expect(completeResponse!.sessionId).toBe('sess-1')

        expect(errorMessage).toBeNull()
    })

    test('handles non-OK response and throws error', async () => {
        await loadModule()

        mockFetch.mockResolvedValue({
            ok: false,
            status: 400,
            text: async () => 'Bad request',
            headers: new Headers(),
        })

        await expect(
            streamSubjectQa(
                { message: 'test', sessionId: undefined },
                {},
            ),
        ).rejects.toThrow('Bad request')
    })

    test('throws streaming not supported error when body is null', async () => {
        await loadModule()

        mockFetch.mockResolvedValue({
            ok: true,
            body: null,
            headers: new Headers(),
            status: 200,
        })

        await expect(
            streamSubjectQa(
                { message: 'test' },
                {},
            ),
        ).rejects.toThrow('Streaming is not supported in this browser')
    })

    test('handles fetch error', async () => {
        await loadModule()

        mockFetch.mockRejectedValue(new Error('Network error'))

        await expect(
            streamSubjectQa(
                { message: 'test' },
                {},
            ),
        ).rejects.toThrow('Network error')
    })

    test('splits SSE events across chunks correctly', async () => {
        await loadModule()

        mockFetch.mockResolvedValue({
            ok: true,
            body: createMockReadableStream([
                'event:delta\nda',
                'ta:{"type":"delta","content":"Hello"}\n\n',
            ]),
            headers: new Headers(),
            status: 200,
        })

        const deltas: string[] = []

        await streamSubjectQa(
            { message: 'test' },
            {
                onDelta: (content) => deltas.push(content),
            },
        )

        expect(deltas).toEqual(['Hello'])
    })

    test('calls onError when error event is received', async () => {
        await loadModule()

        mockFetch.mockResolvedValue({
            ok: true,
            body: createMockReadableStream([
                eventBlock('error', { type: 'error', stage: 'error', message: 'Something went wrong' }),
            ]),
            headers: new Headers(),
            status: 200,
        })

        let errorMsg: string | null = null

        await streamSubjectQa(
            { message: 'test' },
            {
                onError: (msg) => { errorMsg = msg },
            },
        )

        expect(errorMsg).toBe('Something went wrong')
    })
})
