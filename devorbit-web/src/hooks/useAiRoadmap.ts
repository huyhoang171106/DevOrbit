import { useMutation } from '@tanstack/react-query'

export interface RoadmapRecommendation {
    courseId: number
    courseCode: string
    courseName: string
    reasoning: string
    description: string
    isMandatory: boolean
    semester: number
    credits: number
}

export interface GraduationTrack {
    type: string
    name: string
    description: string
    credits: number
    requirements: string
    recommendation: string
    recommended: boolean
    courseCodes: string[]
}

export interface RoadmapResponse {
    summary: string
    recommendedCourses: RoadmapRecommendation[]
    graduationTracks: GraduationTrack[]
    electivePools: ElectivePool[]
}

export interface ElectivePool {
    poolId: string
    poolName: string
    targetTC: number
    currentTC: number
    candidates: ElectiveCandidate[]
}

export interface ElectiveCandidate {
    courseId: number
    courseCode: string
    courseName: string
    credits: number
    score: number
    isSelected: boolean
    description: string
    reasoning: string
    semester?: number
}

export interface RoadmapRequest {
    learningGoals: string
    careerPath: string
}

/**
 * Generates a personalized learning roadmap by calling the backend API.
 * The backend uses a ScenarioEngine that leverages knowledge graph data
 * and curriculum rules — no LLM dependency, deterministic results.
 */
export function useAiRoadmap() {
    return useMutation<RoadmapResponse, Error, RoadmapRequest>({
        mutationFn: async ({ learningGoals, careerPath }) => {
            const response = await fetch('/api/ai/generate-roadmap', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ learningGoals, careerPath })
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
