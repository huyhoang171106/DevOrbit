import { useMutation } from '@tanstack/react-query'
import { generateText } from 'ai'
import { aiModel } from '../lib/ai'
import { apiGet } from '../lib/api'
import type { GraphResponse } from '../types/api'

export interface RoadmapRecommendation {
    courseId: number
    courseCode: string
    courseName: string
    reasoning: string
    description: string
}

export interface RoadmapResponse {
    summary: string
    recommendedCourses: RoadmapRecommendation[]
}

export interface RoadmapRequest {
    learningGoals: string
    careerPath: string
}

export function useAiRoadmap() {
    return useMutation<RoadmapResponse, Error, RoadmapRequest>({
        mutationFn: async ({ learningGoals, careerPath }) => {
            // 1. Fetch academic graph for context
            const graph = await apiGet<GraphResponse>('/api/courses/graph')

            // Format course list concisely to reduce prompt tokens
            const courseLines = graph.nodes.map((n: any) => 
                `${n.id}:${n.code}:${n.name}`
            ).join('\n')

            // Build prerequisite pairs
            const prereqPairs: string[] = []
            for (const link of graph.links) {
                if (link.type !== 'PREREQUISITE') continue
                const sourceNode = graph.nodes.find((n: any) => n.id === link.source)
                const targetNode = graph.nodes.find((n: any) => n.id === link.target)
                if (sourceNode && targetNode) {
                    prereqPairs.push(`${sourceNode.code} -> ${targetNode.code}`)
                }
            }

            // 2. Generate personalized roadmap using the AI SDK
            // big-pickle = deepseek-v4-flash (reasoning model)
            // Reasoning consumes ~2000-4000 tokens before any output
            const { text } = await generateText({
                model: aiModel,
                maxOutputTokens: 16384,
                system: `Bạn là chuyên gia tư vấn học thuật UIT.

DANH SÁCH MÔN (id:code:tên):
${courseLines}

TIÊN QUYẾT (prerequisite -> course):
${prereqPairs.join('\n') || '(không có)'}

MÔN BẮT BUỘC KTPM: IT001,IT012,MA003,MA006,ENG01,SE005,IT002,IT003,MA004,MA005,ENG02,IT008,IT004,IT005,IT007,ENG03,SE104,SS004,SS007,SS008,SE100,SS009,SS010,SE503,SS003,SS006,SE502,SE505,SE506,SE507

QUY TẮC:
1. Chỉ chọn môn từ danh sách trên. Không bịa.
2. Môn bắt buộc sinh viên PHẢI học.
3. Đề xuất thêm môn tự chọn phù hợp.
4. Trả về JSON nguyên chất, không markdown.

{"summary":"...","recommendedCourses":[{"courseId":number,"courseCode":"...","courseName":"...","reasoning":"...","description":"..."}]}`,
                prompt: `Mục tiêu học: ${learningGoals}
Định hướng: ${careerPath}`,
            })

            // 3. Parse result
            try {
                // Try to find JSON block in case AI wraps it in markdown
                const jsonMatch = text.match(/\{[\s\S]*\}/)
                const jsonStr = jsonMatch ? jsonMatch[0] : text
                return JSON.parse(jsonStr)
            } catch (e) {
                console.error("AI response parse error. Raw text:", text)
                throw new Error("AI trả về định dạng không hợp lệ. Phản hồi thô: " + text.substring(0, 200))
            }
        }
    })
}
