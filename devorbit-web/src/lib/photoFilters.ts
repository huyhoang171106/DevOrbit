export type SlotFilter = "normal" | "sepia" | "vintage" | "cool" | "warm" | "vivid" | "soft" | "mono";

export interface FilterPreset {
  label: string;
  css: string;
  emoji: string;
}

export const FILTER_PRESETS: Record<SlotFilter, FilterPreset> = {
  normal: { label: "Normal", css: "none", emoji: "📷" },
  sepia: { label: "Sepia", css: "sepia(0.7) saturate(0.8)", emoji: "🟫" },
  vintage: { label: "Vintage", css: "sepia(0.3) saturate(0.5) brightness(0.9) contrast(0.85)", emoji: "📀" },
  cool: { label: "Cool", css: "hue-rotate(200deg) saturate(0.8) brightness(1.05)", emoji: "❄️" },
  warm: { label: "Warm", css: "sepia(0.15) saturate(1.3) brightness(1.05)", emoji: "☀️" },
  vivid: { label: "Vivid", css: "contrast(1.2) saturate(1.5) brightness(1.05)", emoji: "🌈" },
  soft: { label: "Soft", css: "brightness(1.1) contrast(0.85) saturate(0.7) blur(0.5px)", emoji: "✨" },
  mono: { label: "Mono", css: "grayscale(1) contrast(1.1)", emoji: "⚫" },
};

export const DEFAULT_FILTER: SlotFilter = "normal";

export const FILTER_LIST = Object.entries(FILTER_PRESETS) as [SlotFilter, FilterPreset][];
