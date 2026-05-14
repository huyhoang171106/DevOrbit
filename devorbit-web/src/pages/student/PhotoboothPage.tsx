import { useState, useEffect } from "react";
import { useRequireAuth } from "../../lib/hooks";
import { DocumentUnlock } from "../../components/photobooth/DocumentUnlock";
import { FrameSelector } from "../../components/photobooth/FrameSelector";
import { PhotoUploadSection } from "../../components/photobooth/PhotoUploadSection";
import { PreviewCanvas } from "../../components/photobooth/PreviewCanvas";
import { DownloadSection } from "../../components/photobooth/DownloadSection";
import { PhotoCompositor } from "../../lib/photoCompositor";
import {
  FrameDefinition,
  FRAME_DEFINITIONS,
} from "../../lib/frames/frameDefinitions";

const SESSION_UNLOCK_KEY = "photobooth_unlocked";

export function PhotoboothPage() {
  useRequireAuth();

  const [isUnlocked, setIsUnlocked] = useState(false);
  const [selectedFrame, setSelectedFrame] = useState<FrameDefinition | null>(
    null,
  );
  const [selectedPhotos, setSelectedPhotos] = useState<File[]>([]);
  const [previewCanvas, setPreviewCanvas] = useState<HTMLCanvasElement | null>(
    null,
  );
  const [isCompositing, setIsCompositing] = useState(false);

  // Check for unlock on mount
  useEffect(() => {
    const wasUnlocked = sessionStorage.getItem(SESSION_UNLOCK_KEY) === "true";
    setIsUnlocked(wasUnlocked);

    // Default to UIT Anniversary frame if available
    if (!selectedFrame) {
      const uitFrame = FRAME_DEFINITIONS.find(
        (f) => f.id === "uit-anniversary",
      );
      if (uitFrame) {
        setSelectedFrame(uitFrame);
      }
    }
  }, []);

  // Generate preview when photos or frame changes
  useEffect(() => {
    if (
      !selectedFrame ||
      selectedPhotos.length === 0 ||
      selectedPhotos.length !== selectedFrame.photoCount
    ) {
      setPreviewCanvas(null);
      return;
    }

    const generatePreview = async () => {
      setIsCompositing(true);
      try {
        const images = await PhotoCompositor.loadImages(selectedPhotos);
        const canvas = await PhotoCompositor.compositePhotos(
          images,
          selectedFrame,
        );
        setPreviewCanvas(canvas);
      } catch (error) {
        console.error("Failed to generate preview:", error);
      } finally {
        setIsCompositing(false);
      }
    };

    generatePreview();
  }, [selectedFrame, selectedPhotos]);

  const handleUnlock = () => {
    sessionStorage.setItem(SESSION_UNLOCK_KEY, "true");
    setIsUnlocked(true);
  };

  const handleFrameSelect = (frame: FrameDefinition) => {
    setSelectedFrame(frame);
    // Reset photos when frame changes if count doesn't match
    if (selectedPhotos.length !== frame.photoCount) {
      setSelectedPhotos([]);
      setPreviewCanvas(null);
    }
  };

  const handlePhotosSelected = (files: File[]) => {
    setSelectedPhotos(files);
  };

  if (!isUnlocked) {
    return (
      <div className="min-h-screen bg-clay-surface pt-24 pb-12 px-4">
        <div className="mx-auto max-w-3xl">
          <div className="text-center mb-12">
            <h1 className="hero-display text-clay-text mb-4">🎞️ Photobooth</h1>
            <p className="subtitle text-ink-secondary">
              Create memorable moments with Korean-style photo strips
            </p>
          </div>

          <div className="space-y-6">
            <DocumentUnlock isUnlocked={isUnlocked} onUnlock={handleUnlock} />

            <div className="glass-card p-8 text-center border border-glass-border/50">
              <p className="body-md text-ink-secondary">
                Upload a document to unlock exclusive photobooth frames,
                including our special{" "}
                <span className="text-clay-primary">UIT 20th Anniversary</span>{" "}
                frame!
              </p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-clay-surface pt-24 pb-12 px-4">
      <div className="mx-auto max-w-6xl">
        {/* Header */}
        <div className="text-center mb-12">
          <h1 className="hero-display text-clay-text mb-2">🎞️ Photobooth</h1>
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

        {/* Main layout: 2-column on desktop, 1-column on mobile */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left column: Controls */}
          <div className="lg:col-span-2 space-y-6">
            {/* Frame Selector */}
            <FrameSelector
              selectedFrame={selectedFrame}
              onFrameSelect={handleFrameSelect}
            />

            {/* Photo Upload */}
            {selectedFrame && (
              <PhotoUploadSection
                maxPhotos={selectedFrame.photoCount}
                onPhotosSelected={handlePhotosSelected}
                selectedCount={selectedPhotos.length}
              />
            )}
          </div>

          {/* Right column: Preview & Download */}
          <div className="lg:col-span-1 space-y-6">
            {/* Preview Canvas */}
            <PreviewCanvas canvas={previewCanvas} isLoading={isCompositing} />

            {/* Download Section */}
            <DownloadSection canvas={previewCanvas} frame={selectedFrame} />
          </div>
        </div>

        {/* Info Section */}
        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="glass-card p-4 text-center">
            <p className="heading-5 text-clay-primary mb-2">📱 Responsive</p>
            <p className="body-sm text-ink-secondary">Works on all devices</p>
          </div>
          <div className="glass-card p-4 text-center">
            <p className="heading-5 text-clay-primary mb-2">🎨 Frames</p>
            <p className="body-sm text-ink-secondary">
              {FRAME_DEFINITIONS.length} styles available
            </p>
          </div>
          <div className="glass-card p-4 text-center">
            <p className="heading-5 text-clay-primary mb-2">🚀 Fast</p>
            <p className="body-sm text-ink-secondary">Real-time compositing</p>
          </div>
        </div>
      </div>
    </div>
  );
}
