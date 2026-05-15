import { useState, useEffect, useCallback } from "react";
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
import { motion, AnimatePresence } from "framer-motion";
import {
  ImageSquare,
  Palette,
  Rocket,
  LockSimple,
} from "@phosphor-icons/react";

const SESSION_UNLOCK_KEY = "photobooth_unlocked";

export function PhotoboothPage() {
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
      <div className="min-h-screen bg-orbit-bg pt-24 pb-12 px-4 flex items-center justify-center">
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
      <div className="min-h-screen bg-orbit-bg pt-24 pb-12 px-4">
        <div className="mx-auto max-w-3xl">
          <div className="text-center mb-12">
            <motion.h1
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, ease: "easeOut" }}
              className="hero-display text-orbit-text mb-4"
            >
              <ImageSquare size={48} className="inline-block text-emerald-400 mb-2" weight="duotone" />
              <br />Photobooth
            </motion.h1>
            <p className="subtitle text-zinc-400">
              Create memorable moments with Korean-style photo strips
            </p>
          </div>
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.15, ease: "easeOut" }}
            className="space-y-6"
          >
            <DocumentUnlock isUnlocked={isUnlocked} onUnlock={handleUnlock} />
            <div className="orbit-card p-8 text-center">
              <p className="body-md text-zinc-400">
                Upload a document to unlock exclusive photobooth frames and start creating photo strips with custom frames, including our special{" "}
                <span className="text-emerald-400 font-semibold">UIT 20th Anniversary</span>{" "}
                frame!
              </p>
            </div>
          </motion.div>
        </div>
      </div>
    );
  }

  return (
    <AnimatePresence mode="wait">
      <motion.div
        key="photobooth"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.4, ease: "easeOut" }}
        className="min-h-screen bg-orbit-bg pt-24 pb-12 px-4"
      >
        <div className="mx-auto max-w-6xl">
          <div className="text-center mb-12">
            <motion.h1
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, ease: "easeOut" }}
              className="hero-display text-orbit-text mb-2"
            >
              <ImageSquare size={48} className="inline-block text-emerald-400 mb-2" weight="duotone" />
              <br />Photobooth
            </motion.h1>
            <p className="subtitle text-zinc-400 mb-4">
              Create memorable moments with Korean-style photo strips
            </p>
            <button
              onClick={() => {
                sessionStorage.removeItem(SESSION_UNLOCK_KEY);
                setIsUnlocked(false);
              }}
              className="inline-flex items-center gap-2 text-sm text-zinc-500 hover:text-emerald-400 transition-colors"
            >
              <LockSimple size={14} /> Lock feature
            </button>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-6">
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: 0.1, ease: "easeOut" }}
              >
                <FrameSelector
                  selectedFrame={selectedFrame}
                  onFrameSelect={handleFrameSelect}
                />
              </motion.div>

              {selectedFrame && (
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.4, delay: 0.2, ease: "easeOut" }}
                >
                  <PhotoUploadSection
                    frame={selectedFrame}
                    slotPhotos={slotPhotos}
                    slotOffsets={slotOffsets}
                    onSlotPhoto={handleSlotPhoto}
                    onSlotOffset={handleSlotOffset}
                  />
                </motion.div>
              )}
            </div>

            <div className="lg:col-span-1 space-y-6">
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: 0.15, ease: "easeOut" }}
              >
                <PreviewCanvas canvas={previewCanvas} isLoading={isCompositing} frame={selectedFrame} />
              </motion.div>
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: 0.25, ease: "easeOut" }}
              >
                <DownloadSection canvas={previewCanvas} frame={selectedFrame} />
              </motion.div>
            </div>
          </div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, delay: 0.3, ease: "easeOut" }}
            className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-4"
          >
            <div className="orbit-card p-5 text-center">
              <div className="flex justify-center mb-3">
                <div className="w-10 h-10 rounded-full bg-emerald-400/10 flex items-center justify-center">
                  <ImageSquare size={20} className="text-emerald-400" weight="duotone" />
                </div>
              </div>
              <p className="text-sm font-semibold text-orbit-text mb-1">Responsive</p>
              <p className="text-xs text-zinc-500">Works on all devices</p>
            </div>
            <div className="orbit-card p-5 text-center">
              <div className="flex justify-center mb-3">
                <div className="w-10 h-10 rounded-full bg-emerald-400/10 flex items-center justify-center">
                  <Palette size={20} className="text-emerald-400" weight="duotone" />
                </div>
              </div>
              <p className="text-sm font-semibold text-orbit-text mb-1">Frames</p>
              <p className="text-xs text-zinc-500">{FRAME_DEFINITIONS.length} styles available</p>
            </div>
            <div className="orbit-card p-5 text-center">
              <div className="flex justify-center mb-3">
                <div className="w-10 h-10 rounded-full bg-emerald-400/10 flex items-center justify-center">
                  <Rocket size={20} className="text-emerald-400" weight="duotone" />
                </div>
              </div>
              <p className="text-sm font-semibold text-orbit-text mb-1">Fast</p>
              <p className="text-xs text-zinc-500">Real-time compositing</p>
            </div>
          </motion.div>
        </div>
      </motion.div>
    </AnimatePresence>
  );
}
