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
  photoCount: 1 | 2 | 3 | 4;
  description: string;
  slots: FrameSlot[];
  overlays: FrameOverlay[];
  filter: FilterType;
  backgroundColor: string;
  accentColor?: string;
  isSpecial?: boolean;
}

const CANVAS_SIZE = 2000;
const SLOT_SIZE = 450;
const BORDER_COLOR = "rgba(255, 255, 255, 0.3)";

const createClassicFrame = (): FrameDefinition => ({
  id: "classic",
  name: "Classic Film",
  displayName: "35mm Classic",
  photoCount: 4,
  description: "Timeless film strip style with clean aesthetic",
  filter: "sepia",
  backgroundColor: "#1a1a1a",
  slots: [
    {
      id: "slot1",
      x: 250,
      y: 100,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 8,
      border: { color: BORDER_COLOR, width: 2 },
    },
    {
      id: "slot2",
      x: 250,
      y: 200 + SLOT_SIZE,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 8,
      border: { color: BORDER_COLOR, width: 2 },
    },
    {
      id: "slot3",
      x: 250,
      y: 300 + SLOT_SIZE * 2,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 8,
      border: { color: BORDER_COLOR, width: 2 },
    },
    {
      id: "slot4",
      x: 250,
      y: 400 + SLOT_SIZE * 3,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 8,
      border: { color: BORDER_COLOR, width: 2 },
    },
  ],
  overlays: [
    {
      type: "text",
      x: 1000,
      y: 1900,
      width: 800,
      height: 80,
      content: "35MM",
      color: "#888",
      fontSize: 32,
      fontWeight: 700,
      textAlign: "center",
    },
    {
      type: "line",
      x: 200,
      y: 150,
      width: 1600,
      height: 0,
      color: "#444",
      strokeWidth: 1,
    },
  ],
});

const createRetroFrame = (): FrameDefinition => ({
  id: "retro",
  name: "Retro Vibes",
  displayName: "Polaroid Retro",
  photoCount: 4,
  description: "Vintage polaroid with warm tones",
  filter: "vintage",
  backgroundColor: "#f4e4c1",
  slots: [
    {
      id: "slot1",
      x: 275,
      y: 150,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 12,
      border: { color: "#f4e4c1", width: 0 },
    },
    {
      id: "slot2",
      x: 275,
      y: 250 + SLOT_SIZE,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 12,
      border: { color: "#f4e4c1", width: 0 },
    },
    {
      id: "slot3",
      x: 275,
      y: 350 + SLOT_SIZE * 2,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 12,
      border: { color: "#f4e4c1", width: 0 },
    },
    {
      id: "slot4",
      x: 275,
      y: 450 + SLOT_SIZE * 3,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 12,
      border: { color: "#f4e4c1", width: 0 },
    },
  ],
  overlays: [
    {
      type: "rect",
      x: 0,
      y: 0,
      width: CANVAS_SIZE,
      height: CANVAS_SIZE,
      fillColor: "#f4e4c1",
      strokeWidth: 40,
    },
    {
      type: "text",
      x: 275,
      y: 1850,
      width: SLOT_SIZE,
      height: 60,
      content: "captured",
      color: "#999",
      fontSize: 24,
      fontWeight: 500,
      textAlign: "center",
    },
  ],
});

const createAestheticFrame = (): FrameDefinition => ({
  id: "aesthetic",
  name: "Aesthetic Y2K",
  displayName: "Y2K Aesthetic",
  photoCount: 4,
  description: "Modern aesthetic with gradient accents",
  filter: "cool",
  backgroundColor: "#e8d5f2",
  accentColor: "#b19cd9",
  slots: [
    {
      id: "slot1",
      x: 225,
      y: 100,
      width: 500,
      height: 500,
      borderRadius: 20,
      border: { color: "#b19cd9", width: 3 },
    },
    {
      id: "slot2",
      x: 1275,
      y: 100,
      width: 500,
      height: 500,
      borderRadius: 20,
      border: { color: "#b19cd9", width: 3 },
    },
    {
      id: "slot3",
      x: 225,
      y: 750,
      width: 500,
      height: 500,
      borderRadius: 20,
      border: { color: "#b19cd9", width: 3 },
    },
    {
      id: "slot4",
      x: 1275,
      y: 750,
      width: 500,
      height: 500,
      borderRadius: 20,
      border: { color: "#b19cd9", width: 3 },
    },
  ],
  overlays: [
    {
      type: "circle",
      x: 100,
      y: 100,
      width: 100,
      height: 100,
      fillColor: "#b19cd9",
      radius: 50,
    },
    {
      type: "circle",
      x: 1800,
      y: 1800,
      width: 100,
      height: 100,
      fillColor: "#b19cd9",
      radius: 50,
    },
    {
      type: "text",
      x: 600,
      y: 1750,
      width: 800,
      height: 100,
      content: "moment captured",
      color: "#b19cd9",
      fontSize: 28,
      fontWeight: 600,
      textAlign: "center",
    },
  ],
});

const createFilmFrame = (): FrameDefinition => ({
  id: "film",
  name: "Film Strip",
  displayName: "Motion Film",
  photoCount: 4,
  description: "Actual film strip with sprocket holes",
  filter: "normal",
  backgroundColor: "#0a0a0a",
  slots: [
    {
      id: "slot1",
      x: 300,
      y: 120,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 4,
      border: { color: "#333", width: 1 },
    },
    {
      id: "slot2",
      x: 300,
      y: 220 + SLOT_SIZE,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 4,
      border: { color: "#333", width: 1 },
    },
    {
      id: "slot3",
      x: 300,
      y: 320 + SLOT_SIZE * 2,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 4,
      border: { color: "#333", width: 1 },
    },
    {
      id: "slot4",
      x: 300,
      y: 420 + SLOT_SIZE * 3,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 4,
      border: { color: "#333", width: 1 },
    },
  ],
  overlays: [
    // Left sprocket holes
    ...Array.from({ length: 12 }).map((_, i) => ({
      type: "rect" as const,
      x: 80,
      y: 100 + i * 155,
      width: 40,
      height: 40,
      fillColor: "#222",
    })),
    // Right sprocket holes
    ...Array.from({ length: 12 }).map((_, i) => ({
      type: "rect" as const,
      x: 1880,
      y: 100 + i * 155,
      width: 40,
      height: 40,
      fillColor: "#222",
    })),
  ],
});

const createUITAnniversaryFrame = (): FrameDefinition => ({
  id: "uit-anniversary",
  name: "UIT 20 Years",
  displayName: "🎓 UIT 20th Anniversary",
  photoCount: 4,
  description: "Commemorating UIT's 20 years of excellence (2004-2024)",
  filter: "normal",
  backgroundColor: "#0d1225",
  accentColor: "#10b981",
  isSpecial: true,
  slots: [
    {
      id: "slot1",
      x: 250,
      y: 100,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 12,
      border: { color: "#10b981", width: 3 },
    },
    {
      id: "slot2",
      x: 250,
      y: 200 + SLOT_SIZE,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 12,
      border: { color: "#10b981", width: 3 },
    },
    {
      id: "slot3",
      x: 250,
      y: 300 + SLOT_SIZE * 2,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 12,
      border: { color: "#10b981", width: 3 },
    },
    {
      id: "slot4",
      x: 250,
      y: 400 + SLOT_SIZE * 3,
      width: SLOT_SIZE,
      height: SLOT_SIZE,
      borderRadius: 12,
      border: { color: "#10b981", width: 3 },
    },
  ],
  overlays: [
    // Top border gradient bar
    {
      type: "rect",
      x: 0,
      y: 0,
      width: CANVAS_SIZE,
      height: 60,
      fillColor: "#10b981",
    },

    // Main anniversary text
    {
      type: "text",
      x: 850,
      y: 30,
      width: 300,
      height: 40,
      content: "UIT",
      color: "#ffffff",
      fontSize: 36,
      fontWeight: 700,
      textAlign: "center",
    },

    // 20th anniversary text
    {
      type: "text",
      x: 850,
      y: 2100,
      width: 300,
      height: 50,
      content: "20 YEARS",
      color: "#10b981",
      fontSize: 40,
      fontWeight: 700,
      textAlign: "center",
    },

    // Year range
    {
      type: "text",
      x: 800,
      y: 2160,
      width: 400,
      height: 40,
      content: "2004 - 2024",
      color: "#94a3b8",
      fontSize: 28,
      fontWeight: 600,
      textAlign: "center",
    },

    // Bottom commemorative bar
    {
      type: "rect",
      x: 0,
      y: 1940,
      width: CANVAS_SIZE,
      height: 80,
      fillColor: "#10b981",
    },
    {
      type: "text",
      x: 100,
      y: 1945,
      width: 1800,
      height: 70,
      content: "UNIVERSITY OF INFORMATION TECHNOLOGY",
      color: "#0d1225",
      fontSize: 32,
      fontWeight: 700,
      textAlign: "center",
    },

    // Corner decorative circles
    {
      type: "circle",
      x: 50,
      y: 50,
      width: 30,
      height: 30,
      fillColor: "#fbbf24",
      radius: 15,
    },
    {
      type: "circle",
      x: 1920,
      y: 50,
      width: 30,
      height: 30,
      fillColor: "#fbbf24",
      radius: 15,
    },
    {
      type: "circle",
      x: 50,
      y: 1920,
      width: 30,
      height: 30,
      fillColor: "#fbbf24",
      radius: 15,
    },
    {
      type: "circle",
      x: 1920,
      y: 1920,
      width: 30,
      height: 30,
      fillColor: "#fbbf24",
      radius: 15,
    },

    // Side decorative lines
    {
      type: "line",
      x: 100,
      y: 100,
      width: 0,
      height: 1800,
      color: "#10b981",
      strokeWidth: 2,
    },
    {
      type: "line",
      x: 1900,
      y: 100,
      width: 0,
      height: 1800,
      color: "#10b981",
      strokeWidth: 2,
    },
  ],
});

export const FRAME_DEFINITIONS: FrameDefinition[] = [
  createClassicFrame(),
  createRetroFrame(),
  createAestheticFrame(),
  createFilmFrame(),
  createUITAnniversaryFrame(),
];

export function getFrameById(id: string): FrameDefinition | undefined {
  return FRAME_DEFINITIONS.find((frame) => frame.id === id);
}
