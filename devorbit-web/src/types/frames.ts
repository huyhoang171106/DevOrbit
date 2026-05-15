export interface StoredSlot {
  id: string
  x: number
  y: number
  width: number
  height: number
  borderRadius: number
}

export interface StoredFrame {
  id: string
  name: string
  displayName: string
  photoCount: number
  description: string
  slots: StoredSlot[]
  overlayImage: string
  filter: string
  backgroundColor: string
}
