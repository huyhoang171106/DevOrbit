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

const LOGICAL_MAX = 2000;
const imgSizeCache = new Map<string, { w: number; h: number }>();

function loadImageSize(url: string): Promise<{ w: number; h: number }> {
  const cached = imgSizeCache.get(url);
  if (cached) return Promise.resolve(cached);
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.crossOrigin = "anonymous";
    img.onload = () => {
      const size = { w: img.naturalWidth, h: img.naturalHeight };
      imgSizeCache.set(url, size);
      resolve(size);
    };
    img.onerror = () => reject(new Error("Failed to load image"));
    img.src = url;
  });
}

function getLogicalSize(imgW: number, imgH: number) {
  const maxDim = Math.max(imgW, imgH);
  if (maxDim === 0) return { w: LOGICAL_MAX, h: LOGICAL_MAX };
  const scale = LOGICAL_MAX / maxDim;
  return { w: Math.round(imgW * scale), h: Math.round(imgH * scale) };
}

export async function reloadFrames(): Promise<void> {
  try {
    const stored = await frameService.list();
    FRAME_DEFINITIONS = await Promise.all(stored.map(toFrameDefinition));
  } catch (err) {
    console.error("Failed to load frames:", err);
    FRAME_DEFINITIONS = [];
    throw err;
  }
}

export async function forceRefreshFrames(): Promise<void> {
  loadPromise = null;
  return ensureFramesLoaded();
}

export async function normalizeStoredFrameSlots(stored: StoredFrame): Promise<StoredFrame> {
  let logicalW = LOGICAL_MAX, logicalH = LOGICAL_MAX;
  let imageLoaded = false;
  if (stored.overlayImage) {
    try {
      const size = await loadImageSize(stored.overlayImage);
      const ls = getLogicalSize(size.w, size.h);
      logicalW = ls.w;
      logicalH = ls.h;
      imageLoaded = true;
    } catch {}
  }

  // Fast path: image loaded → compare against real proportional logical space
  if (imageLoaded) {
    const isOldFormat = stored.slots.some(
      s => s.x > logicalW || s.y > logicalH || s.width > logicalW || s.height > logicalH
    );
    if (!isOldFormat) return stored;
  }

  // Fallback heuristic (no image or failed load): detect old square format by checking
  // if any slot has width > 1500 or height > 500 (typical old format in 2000 space)
  const isOldHeuristic = stored.slots.some(s => s.width > 1500 || s.height > 500);
  if (!imageLoaded && !isOldHeuristic) return stored;

  const sx = logicalW / LOGICAL_MAX;
  const sy = logicalH / LOGICAL_MAX;
  return {
    ...stored,
    slots: stored.slots.map((s) => ({
      ...s,
      x: Math.round(s.x * sx),
      y: Math.round(s.y * sy),
      width: Math.max(1, Math.round(s.width * sx)),
      height: Math.max(1, Math.round(s.height * sy)),
    })),
  };
}

async function toFrameDefinition(stored: StoredFrame): Promise<FrameDefinition> {
  // Determine proportional logical space from overlay image
  let logicalW = LOGICAL_MAX, logicalH = LOGICAL_MAX;
  if (stored.overlayImage) {
    try {
      const size = await loadImageSize(stored.overlayImage);
      const ls = getLogicalSize(size.w, size.h);
      logicalW = ls.w;
      logicalH = ls.h;
    } catch {}
  }

  // Detect old square format: any slot coord > proportional logical max
  const isOldFormat = stored.slots.some(
    s => s.x > logicalW || s.y > logicalH || s.width > logicalW || s.height > logicalH
  );

  let slots = stored.slots;
  if (isOldFormat) {
    const oldLogical = LOGICAL_MAX;
    const sx = logicalW / oldLogical;
    const sy = logicalH / oldLogical;
    slots = stored.slots.map((s) => ({
      ...s,
      x: Math.round(s.x * sx),
      y: Math.round(s.y * sy),
      width: Math.max(1, Math.round(s.width * sx)),
      height: Math.max(1, Math.round(s.height * sy)),
    }));
  }

  return {
    id: stored.id,
    name: stored.name,
    displayName: stored.displayName,
    photoCount: stored.photoCount as 1 | 2 | 3 | 4 | 6,
    description: stored.description || "",
    slots: slots.map((s: any) => ({
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

export async function ensureFramesLoaded(): Promise<void> {
  if (!loadPromise) {
    loadPromise = reloadFrames().catch(() => {
      loadPromise = null;
    });
  }
  return loadPromise;
}

export function getFrameById(id: string): FrameDefinition | undefined {
  return FRAME_DEFINITIONS.find((frame) => frame.id === id);
}
