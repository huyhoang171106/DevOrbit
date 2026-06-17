import { useMutation } from '@tanstack/react-query'
import { apiStudentPost } from '../lib/api'

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
 * Generates a personalized learning roadmap by calling the authenticated backend API.
 */
export function useAiRoadmap() {
    return useMutation<RoadmapResponse, Error, RoadmapRequest>({
        mutationFn: async ({ learningGoals, careerPath }) => {
            return apiStudentPost<RoadmapResponse>('/api/ai/generate-roadmap', { learningGoals, careerPath })
        }
    })
}
