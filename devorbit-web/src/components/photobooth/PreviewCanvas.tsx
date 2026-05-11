import { useEffect, useRef, useState } from "react";
import { FrameDefinition } from "../../lib/frames/frameDefinitions";

interface PreviewCanvasProps {
  canvas: HTMLCanvasElement | null;
  isLoading: boolean;
  frame: FrameDefinition | null;
}

export function PreviewCanvas({ canvas, isLoading, frame }: PreviewCanvasProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const displayCanvasRef = useRef<HTMLCanvasElement>(null);
  const [frameImg, setFrameImg] = useState<HTMLImageElement | null>(null);

  useEffect(() => {
    if (canvas && displayCanvasRef.current) {
      const displayCtx = displayCanvasRef.current.getContext("2d");
      if (displayCtx) {
        displayCtx.drawImage(canvas, 0, 0);
      }
    }
  }, [canvas]);

  useEffect(() => {
    if (!frame?.overlayImage) { setFrameImg(null); return; }
    const img = new Image();
    img.crossOrigin = "anonymous";
    img.onload = () => setFrameImg(img);
    img.onerror = () => setFrameImg(null);
    img.src = frame.overlayImage;
  }, [frame?.overlayImage]);

  return (
    <div className="glass-card p-8">
      <h3 className="heading-5 text-ink mb-2">Preview</h3>
      <p className="body-sm text-ink-secondary mb-6">
        Your composite will appear here
      </p>

      <div
        ref={containerRef}
        className="bg-cosmic-base rounded-lg overflow-hidden flex items-center justify-center min-h-[500px] sm:min-h-[800px]">
        {isLoading ? (
          <div className="flex flex-col items-center gap-3">
            <div className="inline-flex animate-spin">
              <svg className="h-8 w-8 text-emerald-400" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
              </svg>
            </div>
            <p className="body-sm text-ink-secondary">Creating your photobooth...</p>
          </div>
        ) : canvas ? (
          <canvas
            ref={displayCanvasRef}
            width={canvas.width}
            height={canvas.height}
            className="max-w-full h-auto shadow-xl rounded-lg"
            style={{ maxHeight: "800px" }}
          />
        ) : frameImg && frame ? (
          <div className="relative w-full h-full flex items-center justify-center p-4">
            <img
              src={frame.overlayImage!}
              alt="Frame"
              className="max-w-full max-h-[700px] object-contain opacity-40 rounded-lg"
            />
            <div className="absolute inset-0 flex items-center justify-center">
              <p className="body-sm text-ink-secondary">Add photos to see preview</p>
            </div>
          </div>
        ) : (
          <div className="text-center">
            <svg className="h-16 w-16 text-ink-muted mx-auto mb-3 opacity-40" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <p className="body-sm text-ink-secondary">
              Select frame and upload photos to preview
            </p>
          </div>
        )}
      </div>

      {canvas && (
        <div className="mt-4 flex items-center gap-2 text-xs text-ink-secondary">
          <svg className="h-4 w-4" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd"
              d="M18 5v8a2 2 0 01-2 2h-5l-5 4v-4H4a2 2 0 01-2-2V5a2 2 0 012-2h12a2 2 0 012 2zM7 8H5v2h2V8zm2 0h2v2H9V8zm6 0h-2v2h2V8z"
              clipRule="evenodd" />
          </svg>
          <span>Resolution: {canvas.width}×{canvas.height}px • Ready to download</span>
        </div>
      )}
    </div>
  );
}
