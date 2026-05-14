import type { StoredFrame } from "../../types/frames";
import { frameService } from "./frameService";

export type FilterType = "sepia" | "vintage" | "cool" | "normal";

export interface FrameSlot {
  id: string;
  x: number;
  y: number;
  width: number;
  height: number;
  borderRadius: number;
  border?: { color: string; width: number };
  shadow?: string;
}

export interface FrameOverlay {
  type: "rect" | "text" | "circle" | "line";
  x: number;
  y: number;
  width?: number;
  height?: number;
  content?: string;
  color?: string;
  fontSize?: number;
  fontWeight?: number;
  textAlign?: "center" | "left" | "right";
  radius?: number;
  strokeWidth?: number;
  fillColor?: string;
}

export interface FrameDefinition {
  id: string;
  name: string;
  displayName: string;
  photoCount: 1 | 2 | 3 | 4 | 6;
  description: string;
  slots: FrameSlot[];
  overlays: FrameOverlay[];
  overlayImage?: string;
  filter: FilterType;
  backgroundColor: string;
  accentColor?: string;
  isSpecial?: boolean;
}

export let FRAME_DEFINITIONS: FrameDefinition[] = [];

let loadPromise: Promise<void> | null = null;

export async function reloadFrames(): Promise<void> {
  try {
    const stored = await frameService.list();
    FRAME_DEFINITIONS = stored.map(toFrameDefinition);
  } catch (err) {
    console.error("Failed to load frames:", err);
    FRAME_DEFINITIONS = [];
  }
}

function toFrameDefinition(stored: StoredFrame): FrameDefinition {
  return {
    id: stored.id,
    name: stored.name,
    displayName: stored.displayName,
    photoCount: stored.photoCount as 1 | 2 | 3 | 4 | 6,
    description: stored.description || "",
    slots: stored.slots.map((s: any) => ({
      id: s.id,
      x: s.x,
      y: s.y,
      width: s.width,
      height: s.height,
      borderRadius: s.borderRadius || 0,
    })),
    overlays: [],
    overlayImage: stored.overlayImage || undefined,
    filter: (stored.filter as FilterType) || "normal",
    backgroundColor: stored.backgroundColor || "#ffffff",
  };
}

export function ensureFramesLoaded(): Promise<void> {
  if (!loadPromise) {
    loadPromise = reloadFrames();
  }
  return loadPromise;
}

export function getFrameById(id: string): FrameDefinition | undefined {
  return FRAME_DEFINITIONS.find((frame) => frame.id === id);
}
