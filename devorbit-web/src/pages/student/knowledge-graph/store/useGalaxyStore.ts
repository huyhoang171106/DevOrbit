import { create } from 'zustand'

type ElectiveNodeData = {
  id: number
  code: string
  name: string
  semester: number
  description?: string
}

type GalaxyState = {
  focusNodeId: number | null
  hoveredNodeId: number | null
  failedNodes: Set<number>
  blockedNodes: Set<number>
  isSimulationMode: boolean
  isTimeTravelMode: boolean
  currentSemester: number
  weekMarkers: Array<{ week: number; count: number }>
  aiRecommendedNodes: Set<number>
  aiElectiveNodes: Set<number>
  aiElectiveNodeData: ElectiveNodeData[]
  selectedGraduationTrack: string | null
}

type GalaxyActions = {
  setFocusNode: (id: number | null) => void
  setHoveredNode: (id: number | null) => void
  toggleFailedNode: (id: number) => void
  setBlockedNodes: (ids: Set<number>) => void
  resetFailedNodes: () => void
  toggleSimulation: () => void
  toggleTimeTravel: () => void
  setCurrentSemester: (semester: number) => void
  setWeekMarkers: (markers: Array<{ week: number; count: number }>) => void
  setAiRecommendedNodes: (ids: Set<number>) => void
  setAiElectiveNodes: (ids: Set<number>) => void
  setAiElectiveNodeData: (data: ElectiveNodeData[]) => void
  setSelectedGraduationTrack: (type: string | null) => void
}

export const useGalaxyStore = create<GalaxyState & GalaxyActions>((set) => ({
  focusNodeId: null,
  hoveredNodeId: null,
  failedNodes: new Set(),
  blockedNodes: new Set(),
  isSimulationMode: false,
  isTimeTravelMode: false,
  currentSemester: 0,
  weekMarkers: [],
  aiRecommendedNodes: new Set(),
  aiElectiveNodes: new Set(),
  aiElectiveNodeData: [],
  selectedGraduationTrack: null,

  setFocusNode: (id) => set({ focusNodeId: id }),
  setHoveredNode: (id) => set({ hoveredNodeId: id }),
  toggleFailedNode: (id) => set((s) => {
    const next = new Set(s.failedNodes)
    if (next.has(id)) next.delete(id)
    else next.add(id)
    return { failedNodes: next }
  }),
  setBlockedNodes: (ids) => set({ blockedNodes: ids }),
  resetFailedNodes: () => set({ failedNodes: new Set(), blockedNodes: new Set() }),
  toggleSimulation: () => set((s) => ({
    isSimulationMode: !s.isSimulationMode,
    failedNodes: s.isSimulationMode ? new Set() : s.failedNodes,
    blockedNodes: s.isSimulationMode ? new Set() : s.blockedNodes,
  })),
  toggleTimeTravel: () => set((s) => ({ isTimeTravelMode: !s.isTimeTravelMode })),
  setCurrentSemester: (semester) => set({ currentSemester: semester }),
  setWeekMarkers: (markers) => set({ weekMarkers: markers }),
  setAiRecommendedNodes: (ids) => set({ aiRecommendedNodes: ids }),
  setAiElectiveNodes: (ids) => set({ aiElectiveNodes: ids }),
  setAiElectiveNodeData: (data) => set({ aiElectiveNodeData: data }),
  setSelectedGraduationTrack: (type) => set({ selectedGraduationTrack: type }),
}))
