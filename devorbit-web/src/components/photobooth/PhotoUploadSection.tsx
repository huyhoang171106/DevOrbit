import { useRef, useState, useEffect, useCallback } from "react";
import { FrameDefinition } from "../../lib/frames/frameDefinitions";
import { SlotFilter, FILTER_PRESETS, FILTER_LIST, DEFAULT_FILTER } from "../../lib/photoFilters";

const SLOT_COLORS = [
  "#10b981", "#3b82f6", "#ef4444", "#eab308",
  "#a855f7", "#ec4899",
];

interface PhotoUploadSectionProps {
  frame: FrameDefinition;
  slotPhotos: (File | null)[];
  slotOffsets: { dx: number; dy: number; zoom?: number; filter?: SlotFilter }[];
  onSlotPhoto: (slotIndex: number, file: File | null) => void;
  onSlotOffset: (slotIndex: number, offset: { dx: number; dy: number; zoom?: number; filter?: SlotFilter }) => void;
}

export function PhotoUploadSection({
  frame,
  slotPhotos,
  slotOffsets,
  onSlotPhoto,
  onSlotOffset,
}: PhotoUploadSectionProps) {
  const [cameraSlot, setCameraSlot] = useState<number | null>(null);
  const [adjustSlot, setAdjustSlot] = useState<number | null>(null);

  return (
    <div className="glass-card p-8">
      <h3 className="heading-5 text-ink mb-2">Upload Photos</h3>
      <p className="body-sm text-ink-secondary mb-6">
        {slotPhotos.filter(Boolean).length}/{frame.photoCount} photos ready
      </p>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {frame.slots.map((slot, i) => (
          <SlotCard
            key={slot.id}
            index={i}
            file={slotPhotos[i] ?? null}
            offset={slotOffsets[i] ?? { dx: 0, dy: 0, filter: DEFAULT_FILTER }}
            color={SLOT_COLORS[i % SLOT_COLORS.length]}
            slotAspect={slot.width / slot.height}
            onFile={(f) => onSlotPhoto(i, f)}
            onOpenCamera={() => setCameraSlot(i)}
            onAdjust={() => setAdjustSlot(i)}
            onFilterChange={(f) => {
              const cur = slotOffsets[i] ?? { dx: 0, dy: 0 };
              onSlotOffset(i, { ...cur, filter: f });
            }}
          />
        ))}
      </div>

      {cameraSlot !== null && (
        <CameraCapture
          onCapture={(file) => {
            onSlotPhoto(cameraSlot, file);
            setCameraSlot(null);
          }}
          onClose={() => setCameraSlot(null)}
        />
      )}

      {adjustSlot !== null && slotPhotos[adjustSlot] && (
        <ImageAdjuster
          file={slotPhotos[adjustSlot]!}
          offset={slotOffsets[adjustSlot] ?? { dx: 0, dy: 0, filter: DEFAULT_FILTER }}
          slotIndex={adjustSlot}
          slotAspect={frame.slots[adjustSlot].width / frame.slots[adjustSlot].height}
          onSave={(offset) => onSlotOffset(adjustSlot, offset)}
          onClose={() => setAdjustSlot(null)}
        />
      )}
    </div>
  );
}

function SlotCard({
  index,
  file,
  offset,
  color,
  slotAspect,
  onFile,
  onOpenCamera,
  onAdjust,
  onFilterChange,
}: {
  index: number;
  file: File | null;
  offset: { dx: number; dy: number; zoom?: number; filter?: SlotFilter };
  color: string;
  slotAspect: number;
  onFile: (f: File | null) => void;
  onOpenCamera: () => void;
  onAdjust: () => void;
  onFilterChange: (f: SlotFilter) => void;
}) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const imgRef = useRef<HTMLImageElement | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (!file) { imgRef.current = null; return; }
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = new Image();
      img.onload = () => {
        imgRef.current = img;
        drawThumb(img, slotAspect, offset, color);
      };
      img.src = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }, [file, slotAspect, offset, color]);

  function drawThumb(
    img: HTMLImageElement,
    aspect: number,
    off: { dx: number; dy: number; zoom?: number; filter?: SlotFilter },
    borderColor: string,
  ) {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;
    const cw = canvas.width;
    const ch = canvas.height;

    ctx.fillStyle = "#1a1a2e";
    ctx.fillRect(0, 0, cw, ch);

    // Base cover-fit crop matching slot aspect (same as compositor)
    const imgAspect = img.width / img.height;
    let baseW = img.width, baseH = img.height;
    if (imgAspect > aspect) {
      baseW = img.height * aspect;
    } else {
      baseH = img.width / aspect;
    }

    const zoom = off.zoom ?? 1;
    const viewW = baseW / zoom;
    const viewH = baseH / zoom;

    const maxDx = (img.width - viewW) / 2;
    const maxDy = (img.height - viewH) / 2;
    const srcX = (img.width - viewW) / 2 + Math.max(-maxDx, Math.min(maxDx, off.dx));
    const srcY = (img.height - viewH) / 2 + Math.max(-maxDy, Math.min(maxDy, off.dy));

    // Fit into canvas preserving crop aspect ratio
    const fitAspect = viewW / viewH;
    let drawW: number, drawH: number, dx: number, dy: number;
    if (fitAspect > 1) {
      drawW = cw;
      drawH = cw / fitAspect;
      dx = 0;
      dy = (ch - drawH) / 2;
    } else {
      drawH = ch;
      drawW = ch * fitAspect;
      dx = (cw - drawW) / 2;
      dy = 0;
    }

    ctx.save();
    ctx.beginPath();
    ctx.rect(dx, dy, drawW, drawH);
    ctx.clip();

    const flt = off.filter && off.filter !== "normal" ? FILTER_PRESETS[off.filter]?.css : undefined;
    if (flt) ctx.filter = flt;
    ctx.drawImage(img, srcX, srcY, viewW, viewH, dx, dy, drawW, drawH);
    ctx.filter = "none";
    ctx.restore();

    // Border
    ctx.strokeStyle = borderColor;
    ctx.lineWidth = 2;
    ctx.strokeRect(dx, dy, drawW, drawH);

    // Label
    ctx.fillStyle = "#ffffff";
    ctx.font = "bold 11px monospace";
    ctx.textAlign = "center";
    ctx.fillText(`#${index + 1}${off.filter && off.filter !== "normal" ? ` · ${FILTER_PRESETS[off.filter].emoji}` : ""}`, cw / 2, 14);
  }

  return (
    <div className="glass-card p-3 border border-glass-border">
      <div className="flex items-center justify-between mb-2">
        <span className="text-xs font-bold text-ink">Slot #{index + 1}</span>
        {file && (
          <button onClick={(e) => { e.stopPropagation(); onFile(null); }} className="text-xs text-red-400 hover:text-red-300">Clear</button>
        )}
      </div>

      <div
        className="rounded-lg mb-2 overflow-hidden border-2 relative cursor-pointer group"
        style={{ borderColor: color }}
        onClick={file ? onAdjust : undefined}
      >
        {file ? (
          <>
            <canvas ref={canvasRef} width={400} height={400} className="w-full" />
            <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity bg-black/40">
              <span className="text-xs text-white font-bold px-3 py-1.5 rounded-lg bg-white/20 backdrop-blur">Click to adjust</span>
            </div>
          </>
        ) : (
          <div className="flex items-center justify-center" style={{ minHeight: "140px" }}>
            <span className="text-[10px] font-mono text-ink-muted">No photo</span>
          </div>
        )}
      </div>

      <div className="flex gap-2">
        <button
          onClick={(e) => { e.stopPropagation(); inputRef.current?.click(); }}
          className="flex-1 text-xs px-3 py-2 rounded-lg border border-glass-border bg-glass-surface hover:bg-glass-surface-hover text-ink transition-colors"
        >
          📁 Upload
        </button>
        <button
          onClick={(e) => { e.stopPropagation(); onOpenCamera(); }}
          className="flex-1 text-xs px-3 py-2 rounded-lg border border-glass-border bg-glass-surface hover:bg-glass-surface-hover text-ink transition-colors"
        >
          📷 Capture
        </button>
      </div>

      {file && (
        <div className="mt-2 flex flex-wrap gap-1">
          {FILTER_LIST.map(([key, preset]) => (
            <button
              key={key}
              onClick={(e) => { e.stopPropagation(); onFilterChange(key); }}
              className={`text-[10px] px-2 py-1 rounded-md border transition-colors ${
                (offset.filter ?? DEFAULT_FILTER) === key
                  ? "border-emerald-500 bg-emerald-500/15 text-emerald-300"
                  : "border-glass-border bg-glass-surface hover:bg-glass-surface-hover text-ink-muted"
              }`}
            >
              {preset.emoji} {preset.label}
            </button>
          ))}
        </div>
      )}

      <input
        ref={inputRef}
        type="file"
        accept="image/*"
        className="hidden"
        onChange={(e) => {
          const f = e.target.files?.[0] ?? null;
          if (f) onFile(f);
        }}
      />
    </div>
  );
}

function ImageAdjuster({
  file,
  offset,
  slotIndex,
  slotAspect,
  onSave,
  onClose,
}: {
  file: File;
  offset: { dx: number; dy: number; zoom?: number; filter?: SlotFilter };
  slotIndex: number;
  slotAspect: number;
  onSave: (offset: { dx: number; dy: number; zoom?: number; filter?: SlotFilter }) => void;
  onClose: () => void;
}) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const imgRef = useRef<HTMLImageElement | null>(null);
  const [dx, setDx] = useState(offset.dx);
  const [dy, setDy] = useState(offset.dy);
  const [zoom, setZoom] = useState(offset.zoom ?? 1);
  const dragRef = useRef<{ startX: number; startY: number; origDx: number; origDy: number } | null>(null);

  const redraw = useCallback(() => {
    const canvas = canvasRef.current;
    const img = imgRef.current;
    if (!canvas || !img) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const cw = canvas.width;
    const ch = canvas.height;
    ctx.clearRect(0, 0, cw, ch);

    // Base cover-fit crop matching compositor
    const imgAspect = img.width / img.height;
    let baseW = img.width, baseH = img.height;
    if (imgAspect > slotAspect) {
      baseW = img.height * slotAspect;
    } else {
      baseH = img.width / slotAspect;
    }

    const viewW = baseW / zoom;
    const viewH = baseH / zoom;

    const maxDx = (img.width - viewW) / 2;
    const maxDy = (img.height - viewH) / 2;
    const srcX = (img.width - viewW) / 2 + Math.max(-maxDx, Math.min(maxDx, dx));
    const srcY = (img.height - viewH) / 2 + Math.max(-maxDy, Math.min(maxDy, dy));

    // Auto-scale: crop area fills ~70% of canvas
    const targetSize = Math.min(cw, ch) * 0.7;
    const displayScale = targetSize / Math.max(viewW, viewH);

    const drawW = img.width * displayScale;
    const drawH = img.height * displayScale;

    // Position image so crop center is at canvas center
    const cropCenterX = srcX + viewW / 2;
    const cropCenterY = srcY + viewH / 2;
    const imgX = cw / 2 - cropCenterX * displayScale;
    const imgY = ch / 2 - cropCenterY * displayScale;

    // Draw full image dimmed
    ctx.globalAlpha = 0.15;
    ctx.drawImage(img, imgX, imgY, drawW, drawH);
    ctx.globalAlpha = 1;

    // Crop rect in canvas coords
    const dispX = cw / 2 - (viewW * displayScale) / 2;
    const dispY = ch / 2 - (viewH * displayScale) / 2;
    const dispW = viewW * displayScale;
    const dispH = viewH * displayScale;

    // Dim everything outside crop
    ctx.save();
    ctx.beginPath();
    ctx.rect(0, 0, cw, ch);
    ctx.rect(dispX, dispY, dispW, dispH);
    ctx.clip("evenodd");
    ctx.fillStyle = "rgba(0,0,0,0.5)";
    ctx.fillRect(0, 0, cw, ch);
    ctx.restore();

    // Draw crop area full brightness
    ctx.save();
    ctx.beginPath();
    ctx.rect(dispX, dispY, dispW, dispH);
    ctx.clip();
    ctx.drawImage(img, imgX, imgY, drawW, drawH);
    ctx.restore();

    // Crop border
    ctx.strokeStyle = "#10b981";
    ctx.lineWidth = 3;
    ctx.setLineDash([8, 4]);
    ctx.strokeRect(dispX, dispY, dispW, dispH);
    ctx.setLineDash([]);

    // Labels
    ctx.fillStyle = "#ffffff";
    ctx.font = "bold 13px monospace";
    ctx.textAlign = "center";
    ctx.fillText(`Drag to reposition`, cw / 2, ch - 10);
  }, [dx, dy, zoom, slotAspect]);

  useEffect(() => {
    const img = new Image();
    img.onload = () => {
      imgRef.current = img;
      redraw();
    };
    const reader = new FileReader();
    reader.onload = (e) => { img.src = e.target?.result as string; };
    reader.readAsDataURL(file);
  }, [file]);

  useEffect(() => { redraw(); }, [redraw]);

  const handleMouseDown = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    dragRef.current = { startX: e.clientX, startY: e.clientY, origDx: dx, origDy: dy };
  }, [dx, dy]);

  const handleMouseMove = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    const drag = dragRef.current;
    if (!drag) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const img = imgRef.current;
    if (!img) return;

    const cw = canvas.width, ch = canvas.height;
    const imgAspect = img.width / img.height;
    let baseW = img.width, baseH = img.height;
    if (imgAspect > slotAspect) {
      baseW = img.height * slotAspect;
    } else {
      baseH = img.width / slotAspect;
    }
    const viewW = baseW / zoom;
    const viewH = baseH / zoom;
    const targetSize = Math.min(cw, ch) * 0.7;
    const displayScale = targetSize / Math.max(viewW, viewH);

    const maxDx = (img.width - viewW) / 2;
    const maxDy = (img.height - viewH) / 2;
    const deltaX = (e.clientX - drag.startX) / displayScale;
    const deltaY = (e.clientY - drag.startY) / displayScale;
    setDx(Math.max(-maxDx, Math.min(maxDx, drag.origDx + deltaX)));
    setDy(Math.max(-maxDy, Math.min(maxDy, drag.origDy + deltaY)));
  }, [zoom, slotAspect]);

  const handleMouseUp = useCallback(() => {
    dragRef.current = null;
  }, []);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
      <div className="bg-clay-bg border border-glass-border rounded-2xl w-full max-w-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="heading-5 text-ink">Adjust Photo - Slot #{slotIndex + 1}</h3>
          <button onClick={onClose} className="text-ink-muted hover:text-ink text-xl">&times;</button>
        </div>

        <canvas
          ref={canvasRef}
          width={800}
          height={600}
          className="w-full rounded-lg border border-glass-border cursor-grab active:cursor-grabbing"
          onMouseDown={handleMouseDown}
          onMouseMove={handleMouseMove}
          onMouseUp={handleMouseUp}
          onMouseLeave={handleMouseUp}
        />

        <div className="mt-4 space-y-3">
          <div className="flex items-center gap-3">
            <span className="text-xs text-ink-muted w-8">Zoom:</span>
            <button onClick={() => setZoom(z => Math.max(0.5, +(z - 0.1).toFixed(1)))} className="w-8 h-8 rounded-lg border border-glass-border bg-glass-surface hover:bg-glass-surface-hover text-ink text-sm font-bold flex items-center justify-center">−</button>
            <span className="text-xs text-ink-mono w-12 text-center font-mono">{zoom.toFixed(1)}×</span>
            <button onClick={() => setZoom(z => Math.min(3, +(z + 0.1).toFixed(1)))} className="w-8 h-8 rounded-lg border border-glass-border bg-glass-surface hover:bg-glass-surface-hover text-ink text-sm font-bold flex items-center justify-center">+</button>
            <button onClick={() => { setZoom(1); setDx(0); setDy(0); }} className="text-xs text-ink-muted hover:text-ink ml-2">Reset</button>
          </div>
          <div className="space-y-2">
            <label className="flex items-center gap-2">
              <span className="text-xs text-ink-muted w-8">X:</span>
              <input
                type="range"
                min={-400}
                max={400}
                value={dx}
                onChange={(e) => setDx(Number(e.target.value))}
                className="flex-1"
              />
              <span className="text-xs text-ink-mono w-10">{Math.round(dx)}</span>
            </label>
            <label className="flex items-center gap-2">
              <span className="text-xs text-ink-muted w-8">Y:</span>
              <input
                type="range"
                min={-400}
                max={400}
                value={dy}
                onChange={(e) => setDy(Number(e.target.value))}
                className="flex-1"
              />
              <span className="text-xs text-ink-mono w-10">{Math.round(dy)}</span>
            </label>
          </div>
        </div>

        <div className="mt-6 flex gap-3 justify-end">
          <button onClick={onClose} className="btn-ghost px-6 py-3">Cancel</button>
          <button onClick={() => onSave({ dx, dy, zoom, filter: offset.filter ?? DEFAULT_FILTER })} className="btn-primary px-6 py-3">Save Position</button>
        </div>
      </div>
    </div>
  );
}

function CameraCapture({
  onCapture,
  onClose,
}: {
  onCapture: (file: File) => void;
  onClose: () => void;
}) {
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [stream, setStream] = useState<MediaStream | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [mirrored, setMirrored] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const s = await navigator.mediaDevices.getUserMedia({
          video: { facingMode: "user", width: 1280, height: 720 },
          audio: false,
        });
        setStream(s);
        if (videoRef.current) videoRef.current.srcObject = s;
      } catch (err: any) {
        setError(err?.message || "Camera access denied");
      }
    })();
    return () => {
      stream?.getTracks().forEach((t) => t.stop());
    };
  }, []);

  const handleCapture = useCallback(() => {
    const video = videoRef.current;
    const canvas = canvasRef.current;
    if (!video || !canvas) return;
    const w = video.videoWidth || 640;
    const h = video.videoHeight || 480;
    canvas.width = w;
    canvas.height = h;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;
    if (mirrored) {
      ctx.save();
      ctx.translate(w, 0);
      ctx.scale(-1, 1);
    }
    ctx.drawImage(video, 0, 0, w, h);
    if (mirrored) ctx.restore();
    canvas.toBlob((blob) => {
      if (blob) {
        onCapture(new File([blob], `capture-${Date.now()}.png`, { type: "image/png" }));
      }
    }, "image/png");
  }, [onCapture, mirrored]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
      <div className="bg-clay-bg border border-glass-border rounded-2xl w-full max-w-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="heading-5 text-ink">Capture Photo</h3>
          <button onClick={onClose} className="text-ink-muted hover:text-ink text-xl">&times;</button>
        </div>

        {error ? (
          <div className="p-4 rounded-lg bg-red-500/10 border border-red-500/30 text-red-400 body-sm">
            {error}
          </div>
        ) : (
          <>
            <div className="relative">
              <video
                ref={videoRef}
                autoPlay
                playsInline
                className="w-full rounded-lg bg-black"
                style={{ transform: mirrored ? "scaleX(-1)" : undefined }}
              />
            </div>
            <canvas ref={canvasRef} className="hidden" />

            <label className="flex items-center gap-2 mt-3 cursor-pointer">
              <input
                type="checkbox"
                checked={mirrored}
                onChange={(e) => setMirrored(e.target.checked)}
                className="rounded border-glass-border"
              />
              <span className="text-xs text-ink-secondary">Mirror image (flip horizontally)</span>
            </label>

            <button
              onClick={handleCapture}
              className="w-full mt-4 btn-primary py-3 flex items-center justify-center gap-2"
            >
              <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              Capture
            </button>
          </>
        )}
      </div>
    </div>
  );
}
