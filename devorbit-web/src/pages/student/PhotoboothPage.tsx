import { useState, useEffect, useCallback } from "react";
import { useRequireAuth } from "../../lib/hooks";
import { DocumentUnlock } from "../../components/photobooth/DocumentUnlock";
import { FrameSelector } from "../../components/photobooth/FrameSelector";
import { PhotoUploadSection } from "../../components/photobooth/PhotoUploadSection";
import { PreviewCanvas } from "../../components/photobooth/PreviewCanvas";
import { DownloadSection } from "../../components/photobooth/DownloadSection";
import { PhotoCompositor } from "../../lib/photoCompositor";
import { SlotFilter, DEFAULT_FILTER } from "../../lib/photoFilters";
import {
  FrameDefinition,
  FRAME_DEFINITIONS,
  ensureFramesLoaded,
} from "../../lib/frames/frameDefinitions";

const SESSION_UNLOCK_KEY = "photobooth_unlocked";

export function PhotoboothPage() {
  useRequireAuth();

  const [isUnlocked, setIsUnlocked] = useState(false);
  const [framesReady, setFramesReady] = useState(false);
  const [selectedFrame, setSelectedFrame] = useState<FrameDefinition | null>(null);
  const [slotPhotos, setSlotPhotos] = useState<(File | null)[]>([]);
  const [slotOffsets, setSlotOffsets] = useState<{ dx: number; dy: number; zoom?: number; filter?: SlotFilter }[]>([]);
  const [previewCanvas, setPreviewCanvas] = useState<HTMLCanvasElement | null>(null);
  const [isCompositing, setIsCompositing] = useState(false);

  useEffect(() => {
    (async () => {
      await ensureFramesLoaded();
      setFramesReady(true);
    })();
    const wasUnlocked = sessionStorage.getItem(SESSION_UNLOCK_KEY) === "true";
    setIsUnlocked(wasUnlocked);
  }, []);

  useEffect(() => {
    if (framesReady && !selectedFrame && FRAME_DEFINITIONS.length > 0) {
      const f = FRAME_DEFINITIONS[0];
      setSelectedFrame(f);
      setSlotPhotos(new Array(f.photoCount).fill(null));
      setSlotOffsets(new Array(f.photoCount).fill(null).map(() => ({ dx: 0, dy: 0, zoom: 1, filter: DEFAULT_FILTER })));
    }
  }, [framesReady, selectedFrame]);

  const generatePreview = useCallback(async () => {
    if (!selectedFrame) { setPreviewCanvas(null); return; }
    const files = slotPhotos.filter(Boolean) as File[];
    if (files.length === 0) { setPreviewCanvas(null); return; }

    setIsCompositing(true);
    try {
      const images = await PhotoCompositor.loadImages(files);

      const orderedImages = selectedFrame.slots.map((_, i) => {
        const f = slotPhotos[i];
        if (!f) return null;
        const idx = files.indexOf(f);
        return images[idx] ?? null;
      }).filter(Boolean) as HTMLImageElement[];

      const canvas = await PhotoCompositor.compositePhotos(
        orderedImages, selectedFrame, slotOffsets,
        slotOffsets.map(o => o.filter ?? DEFAULT_FILTER),
      );
      setPreviewCanvas(canvas);
    } catch (err) {
      console.error("Compositing failed:", err);
    } finally {
      setIsCompositing(false);
    }
  }, [selectedFrame, slotPhotos, slotOffsets]);

  useEffect(() => {
    generatePreview();
  }, [generatePreview]);

  const handleUnlock = () => {
    sessionStorage.setItem(SESSION_UNLOCK_KEY, "true");
    setIsUnlocked(true);
  };

  const handleFrameSelect = (frame: FrameDefinition) => {
    setSelectedFrame(frame);
    setSlotPhotos(new Array(frame.photoCount).fill(null));
    setSlotOffsets(new Array(frame.photoCount).fill(null).map(() => ({ dx: 0, dy: 0, zoom: 1, filter: DEFAULT_FILTER })));
    setPreviewCanvas(null);
  };

  const handleSlotPhoto = (slotIndex: number, file: File | null) => {
    setSlotPhotos((prev) => {
      const next = [...prev];
      next[slotIndex] = file;
      return next;
    });
    setSlotOffsets((prev) => {
      const next = [...prev];
      next[slotIndex] = { dx: 0, dy: 0, zoom: 1, filter: DEFAULT_FILTER };
      return next;
    });
  };

  const handleSlotOffset = (slotIndex: number, offset: { dx: number; dy: number; zoom?: number; filter?: SlotFilter }) => {
    setSlotOffsets((prev) => {
      const next = [...prev];
      next[slotIndex] = offset;
      return next;
    });
  };

  if (!framesReady) {
    return (
      <div className="min-h-screen bg-cosmic-base pt-24 pb-12 px-4 flex items-center justify-center">
        <div className="inline-flex animate-spin">
          <svg className="h-8 w-8 text-emerald-400" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
        </div>
      </div>
    );
  }

  if (!isUnlocked) {
    return (
      <div className="min-h-screen bg-cosmic-base pt-24 pb-12 px-4">
        <div className="mx-auto max-w-3xl">
          <div className="text-center mb-12">
            <h1 className="hero-display text-ink mb-4">🎞️ Photobooth</h1>
            <p className="subtitle text-ink-secondary">
              Create memorable moments with Korean-style photo strips
            </p>
          </div>
          <div className="space-y-6">
            <DocumentUnlock isUnlocked={isUnlocked} onUnlock={handleUnlock} />
            <div className="glass-card p-8 text-center border border-glass-border/50">
              <p className="body-md text-ink-secondary">
                Upload a document to unlock the photobooth feature
                and start creating photo strips with custom frames!
              </p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-cosmic-base pt-24 pb-12 px-4">
      <div className="mx-auto max-w-6xl">
        <div className="text-center mb-12">
          <h1 className="hero-display text-ink mb-2">🎞️ Photobooth</h1>
          <p className="subtitle text-ink-secondary">
            Create memorable moments with Korean-style photo strips
          </p>
          <button
            onClick={() => {
              sessionStorage.removeItem(SESSION_UNLOCK_KEY);
              setIsUnlocked(false);
            }}
            className="mt-4 text-sm text-ink-secondary hover:text-ink transition-colors">
            ↻ Lock feature
          </button>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <FrameSelector
              selectedFrame={selectedFrame}
              onFrameSelect={handleFrameSelect}
            />

            {selectedFrame && (
              <PhotoUploadSection
                frame={selectedFrame}
                slotPhotos={slotPhotos}
                slotOffsets={slotOffsets}
                onSlotPhoto={handleSlotPhoto}
                onSlotOffset={handleSlotOffset}
              />
            )}
          </div>

          <div className="lg:col-span-1 space-y-6">
            <PreviewCanvas canvas={previewCanvas} isLoading={isCompositing} frame={selectedFrame} />
            <DownloadSection canvas={previewCanvas} frame={selectedFrame} />
          </div>
        </div>

        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="glass-card p-4 text-center">
            <p className="heading-5 text-emerald-400 mb-2">📱 Responsive</p>
            <p className="body-sm text-ink-secondary">Works on all devices</p>
          </div>
          <div className="glass-card p-4 text-center">
            <p className="heading-5 text-emerald-400 mb-2">🎨 Frames</p>
            <p className="body-sm text-ink-secondary">{FRAME_DEFINITIONS.length} styles available</p>
          </div>
          <div className="glass-card p-4 text-center">
            <p className="heading-5 text-emerald-400 mb-2">🚀 Fast</p>
            <p className="body-sm text-ink-secondary">Real-time compositing</p>
          </div>
        </div>
      </div>
    </div>
  );
}
