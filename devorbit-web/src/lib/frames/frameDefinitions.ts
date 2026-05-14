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

const FALLBACK_FRAMES: FrameDefinition[] = [
  {
    id: "classic-4",
    name: "Classic",
    displayName: "Classic 4-Hình",
    photoCount: 4,
    description: "Phong cách photobooth cổ điển 4 tấm",
    slots: [
      { id: "s1", x: 120, y: 120, width: 1760, height: 380, borderRadius: 24 },
      { id: "s2", x: 120, y: 540, width: 1760, height: 380, borderRadius: 24 },
      { id: "s3", x: 120, y: 960, width: 1760, height: 380, borderRadius: 24 },
      { id: "s4", x: 120, y: 1380, width: 1760, height: 380, borderRadius: 24 },
    ],
    overlays: [
      { type: "text", x: 0, y: 1820, width: 2000, height: 60, content: "DevOrbit Photobooth", color: "#a1a1aa", fontSize: 28, fontWeight: 600, textAlign: "center" },
      { type: "line", x: 100, y: 1900, width: 1800, height: 0, color: "rgba(255,255,255,0.1)", strokeWidth: 1 },
    ],
    overlayImage: "/frames/frame01-01.png",
    filter: "normal",
    backgroundColor: "#1a1a2e",
    accentColor: "#34d399",
    isSpecial: false,
  },
  {
    id: "duo-2",
    name: "Duo",
    displayName: "Song Hỷ",
    photoCount: 2,
    description: "Hai tấm ảnh song song",
    slots: [
      { id: "s1", x: 100, y: 200, width: 850, height: 1100, borderRadius: 32 },
      { id: "s2", x: 1050, y: 200, width: 850, height: 1100, borderRadius: 32 },
    ],
    overlays: [
      { type: "text", x: 0, y: 1400, width: 2000, height: 60, content: "✦ Duo ✦", color: "#34d399", fontSize: 36, fontWeight: 700, textAlign: "center" },
      { type: "line", x: 100, y: 1500, width: 1800, height: 0, color: "rgba(52,211,153,0.2)", strokeWidth: 2 },
    ],
    overlayImage: "/frames/frame02-01.png",
    filter: "normal",
    backgroundColor: "#0f0f1a",
    accentColor: "#34d399",
    isSpecial: false,
  },
  {
    id: "single-1",
    name: "Single",
    displayName: "Cá Nhân",
    photoCount: 1,
    description: "Một tấm ảnh lớn",
    slots: [
      { id: "s1", x: 150, y: 150, width: 1700, height: 1200, borderRadius: 40 },
    ],
    overlays: [
      { type: "text", x: 0, y: 1450, width: 2000, height: 50, content: "DevOrbit", color: "#52525b", fontSize: 24, fontWeight: 500, textAlign: "center" },
    ],
    overlayImage: "/frames/frame03-01.png",
    filter: "normal",
    backgroundColor: "#09090b",
    accentColor: "#34d399",
    isSpecial: false,
  },
];

export async function reloadFrames(): Promise<void> {
  try {
    const stored = await frameService.list();
    if (stored.length > 0) {
      FRAME_DEFINITIONS = stored.map(toFrameDefinition);
      return;
    }
    console.warn("No frames from Supabase, using fallback frames");
  } catch (err) {
    console.error("Failed to load frames from Supabase:", err);
  }
  FRAME_DEFINITIONS = FALLBACK_FRAMES.map((f) => ({ ...f }));
}

function toFrameDefinition(stored: StoredFrame): FrameDefinition {
  return {
    id: stored.id,
    name: stored.name,
    displayName: stored.displayName,
    photoCount: stored.photoCount as 1 | 2 | 3 | 4 | 6,
    description: stored.description || "",
    slots: stored.slots.map((s) => ({
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
