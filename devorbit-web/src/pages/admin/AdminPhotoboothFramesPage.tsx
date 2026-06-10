import { useState, useEffect, useRef, useCallback, useMemo } from "react";
import { createPortal } from "react-dom";
import { useRequireAuth } from "../../lib/hooks";
import { frameService } from "../../lib/frames/frameService";
import { reloadFrames, normalizeStoredFrameSlots } from "../../lib/frames/frameDefinitions";
import type { StoredFrame, StoredSlot } from "../../types/frames";

const LOGICAL_MAX = 2000;

// ─── Canvas helpers ───

function emptySlots(count: number): StoredSlot[] {
  const h = Math.round(LOGICAL_MAX / count);
  return Array.from({ length: count }, (_, i) => ({
    id: `slot${i + 1}`,
    x: 0,
    y: i * h,
    width: LOGICAL_MAX,
    height: h,
    borderRadius: 0,
  }));
}

export function AdminPhotoboothFramesPage() {
  useRequireAuth();

  const [frames, setFrames] = useState<StoredFrame[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingSlots, setEditingSlots] = useState<StoredFrame | null>(null);
  const [showUpload, setShowUpload] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const list = await frameService.list();
      const normalized = await Promise.all(list.map(normalizeStoredFrameSlots));
      setFrames(normalized);
    } catch (err: any) {
      setError(err?.message || "Không thể tải danh sách khung");
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleDelete = async (id: string) => {
    if (!confirm("Xóa khung này?")) return;
    setError(null);
    try {
      const ok = await frameService.delete(id);
      if (!ok) { setError("Xóa khung thất bại"); return; }
      reloadFrames();
      load();
    } catch (e: any) {
      setError(e?.message || "Xóa khung thất bại");
    }
  };

  const handleRename = async (frame: StoredFrame, newDisplayName: string) => {
    setError(null);
    try {
      const ok = await frameService.upsert({ ...frame, displayName: newDisplayName });
      if (!ok) { setError("Đổi tên khung thất bại"); return; }
      reloadFrames();
      setFrames((prev) => prev.map((f) => (f.id === frame.id ? { ...f, displayName: newDisplayName } : f)));
    } catch (e: any) {
      setError(e?.message || "Đổi tên khung thất bại");
    }
  };

  const handleSaveSlots = async (updated: StoredFrame) => {
    setError(null);
    try {
      const ok = await frameService.upsert(updated);
      if (!ok) { setError("Lưu slot thất bại"); setEditingSlots(null); return; }
      const saved = await frameService.get(updated.id);
      if (saved) {
        const normalized = await normalizeStoredFrameSlots(saved);
        setFrames(prev => prev.map(f => f.id === normalized.id ? normalized : f));
      }
    } catch (e: any) {
      setError(e?.message || "Lưu slot thất bại");
    }
    setEditingSlots(null);
  };

  const handleUpload = async (frame: StoredFrame, file: File | null) => {
    setError(null);
    try {
      let imageUrl = frame.overlayImage;
      if (file) {
        const url = await frameService.uploadImage(frame.id, file);
        if (!url) { setError("Tải ảnh lên thất bại"); setShowUpload(false); return; }
        imageUrl = url;
      }
      frame.overlayImage = imageUrl;
      const ok = await frameService.upsert(frame);
      if (!ok) { setError("Lưu khung thất bại"); setShowUpload(false); return; }
    } catch (e: any) {
      setError(e?.message || "Lưu khung thất bại");
    }
    setShowUpload(false);
    load();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-[96px]">
        <div className="inline-flex animate-spin">
          <svg className="h-8 w-8 text-emerald-400" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
        </div>
      </div>
    );
  }

  return (
    <div className="w-full max-w-[1280px] mx-auto px-[32px] py-[64px]">
      <div className="mb-[32px] flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="display-sm text-clay-text mb-1">Khung Photobooth</h1>
          <p className="body-sm text-ink-secondary">
            Cấu hình ảnh nền và vị trí slot ảnh
          </p>
        </div>
        <button onClick={() => setShowUpload(true)} className="btn-primary self-start">
          + Tải khung mới
        </button>
      </div>

      {error && (
        <div className="mb-6 flex items-center gap-3 rounded-xl border border-red-500/30 bg-red-500/10 px-5 py-4 body-sm text-red-400">
          <span>{error}</span>
          <button onClick={() => setError(null)} className="ml-auto text-red-400 hover:text-red-300">&times;</button>
        </div>
      )}

      {frames.length === 0 ? (
        <div className="glass-card p-12 text-center">
          <p className="body-md text-ink-secondary">Chưa có khung nào. Tải khung đầu tiên!</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {frames.map((frame) => (
            <FrameCard
              key={frame.id}
              frame={frame}
              onEditSlots={() => setEditingSlots(frame)}
              onDelete={() => handleDelete(frame.id)}
              onRename={handleRename}
            />
          ))}
        </div>
      )}

      {editingSlots && (
        <SlotEditor
          frame={editingSlots}
          onSave={handleSaveSlots}
          onClose={() => setEditingSlots(null)}
        />
      )}

      {showUpload && (
        <UploadDialog
          onUpload={handleUpload}
          onClose={() => setShowUpload(false)}
        />
      )}
    </div>
  );
}

function FrameCard({
  frame,
  onEditSlots,
  onDelete,
  onRename,
}: {
  frame: StoredFrame;
  onEditSlots: () => void;
  onDelete: () => void;
  onRename: (f: StoredFrame, name: string) => void;
}) {
  const [imgErr, setImgErr] = useState(false);
  const [editing, setEditing] = useState(false);
  const [editName, setEditName] = useState(frame.displayName);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (editing && inputRef.current) {
      inputRef.current.focus();
      inputRef.current.select();
    }
  }, [editing]);

  const handleSave = () => {
    const trimmed = editName.trim();
    if (trimmed && trimmed !== frame.displayName) {
      onRename(frame, trimmed);
    }
    setEditing(false);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") handleSave();
    if (e.key === "Escape") { setEditName(frame.displayName); setEditing(false); }
  };

  return (
    <div className="glass-card overflow-hidden border border-glass-border">
      <div className="bg-cosmic-base flex items-center justify-center overflow-hidden h-[300px]">
        {frame.overlayImage && !imgErr ? (
          <img
            src={frame.overlayImage}
            alt={frame.displayName}
            className="w-full h-full object-contain"
            onError={() => setImgErr(true)}
          />
        ) : (
          <span className="text-xs font-mono text-ink-muted">
            {frame.photoCount} photos
          </span>
        )}
      </div>
      <div className="p-4">
        {editing ? (
          <input
            ref={inputRef}
            value={editName}
            onChange={(e) => setEditName(e.target.value)}
            onBlur={handleSave}
            onKeyDown={handleKeyDown}
            className="w-full rounded border border-glass-border bg-glass-surface px-2 py-1 text-sm text-ink mb-2"
          />
        ) : (
          <h3
            className="body-sm-medium text-ink truncate cursor-pointer hover:text-emerald-400 transition-colors"
            onClick={() => { setEditing(true); setEditName(frame.displayName); }}
            title="Click để sửa tên"
          >
            {frame.displayName}
          </h3>
        )}
        <p className="body-xs text-ink-secondary mt-1">
          {frame.photoCount} photos &middot; {frame.slots.length} slots
        </p>
        <div className="mt-3 flex gap-2">
          <button onClick={onEditSlots} className="btn-primary text-xs px-4 py-2">
              Sửa Slot
            </button>
            <button onClick={onDelete} className="btn-ghost text-xs px-4 py-2 text-red-400 hover:text-red-300">
              Xóa
          </button>
        </div>
      </div>
    </div>
  );
}

const COLORS = [
  "rgba(16, 185, 129, 0.3)",
  "rgba(59, 130, 246, 0.3)",
  "rgba(239, 68, 68, 0.3)",
  "rgba(234, 179, 8, 0.3)",
  "rgba(168, 85, 247, 0.3)",
  "rgba(236, 72, 153, 0.3)",
];

const CANVAS_DISPLAY_SIZE = 600;

function roundCanvasRect(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  w: number,
  h: number,
  r: number,
) {
  const rad = Math.min(r, Math.min(w, h) / 2);
  ctx.beginPath();
  ctx.moveTo(x + rad, y);
  ctx.lineTo(x + w - rad, y);
  ctx.arcTo(x + w, y, x + w, y + rad, rad);
  ctx.lineTo(x + w, y + h - rad);
  ctx.arcTo(x + w, y + h, x + w - rad, y + h, rad);
  ctx.lineTo(x + rad, y + h);
  ctx.arcTo(x, y + h, x, y + h - rad, rad);
  ctx.lineTo(x, y + rad);
  ctx.arcTo(x, y, x + rad, y, rad);
  ctx.closePath();
}

function getLogicalSize(imgW: number, imgH: number) {
  const maxDim = Math.max(imgW, imgH);
  if (maxDim === 0) return { w: LOGICAL_MAX, h: LOGICAL_MAX };
  const scale = LOGICAL_MAX / maxDim;
  return { w: Math.round(imgW * scale), h: Math.round(imgH * scale) };
}

function scaleToCanvas(clientX: number, clientY: number, canvas: HTMLCanvasElement, logicalW: number, logicalH: number) {
  const rect = canvas.getBoundingClientRect();
  return {
    x: (clientX - rect.left) * (logicalW / rect.width),
    y: (clientY - rect.top) * (logicalH / rect.height),
  };
}

function SlotEditor({
  frame,
  onSave,
  onClose,
}: {
  frame: StoredFrame;
  onSave: (f: StoredFrame) => void;
  onClose: () => void;
}) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const imgRef = useRef<HTMLImageElement | null>(null);
  const [slots, setSlots] = useState<StoredSlot[]>(frame.slots);
  const [selectedIdx, setSelectedIdx] = useState<number>(-1);
  const [corner1, setCorner1] = useState<{ x: number; y: number } | null>(null);
  const mousePosRef = useRef<{ x: number; y: number } | null>(null);
  const [imgSize, setImgSize] = useState<{ w: number; h: number } | null>(null);

  const logicalSize = useMemo(() => {
    if (!imgSize) return { w: LOGICAL_MAX, h: LOGICAL_MAX };
    return getLogicalSize(imgSize.w, imgSize.h);
  }, [imgSize]);

  const displaySize = useMemo(() => {
    if (!imgSize) return { w: LOGICAL_MAX, h: LOGICAL_MAX };
    const maxDim = Math.max(imgSize.w, imgSize.h);
    const scale = CANVAS_DISPLAY_SIZE / maxDim;
    return {
      w: Math.round(imgSize.w * scale),
      h: Math.round(imgSize.h * scale),
    };
  }, [imgSize]);

  const redraw = useCallback(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    ctx.clearRect(0, 0, canvas.width, canvas.height);
    const { w: lw, h: lh } = logicalSize;
    const scaleX = canvas.width / lw;
    const scaleY = canvas.height / lh;
    ctx.save();
    ctx.scale(scaleX, scaleY);

    ctx.fillStyle = "#1a1a2e";
    ctx.fillRect(0, 0, lw, lh);

    if (imgRef.current) {
      ctx.drawImage(imgRef.current, 0, 0, lw, lh);
    }

    slots.forEach((slot, i) => {
      ctx.fillStyle = COLORS[i % COLORS.length];
      roundCanvasRect(ctx, slot.x, slot.y, slot.width, slot.height, slot.borderRadius);
      ctx.fill();
      ctx.strokeStyle = i === selectedIdx ? "#fbbf24" : "#ffffff";
      ctx.lineWidth = i === selectedIdx ? 3 : 2;
      ctx.stroke();
      ctx.fillStyle = "#ffffff";
      ctx.font = "bold 13px monospace";
      ctx.textAlign = "center";
      ctx.textBaseline = "middle";
      ctx.fillText(`#${i + 1}`, slot.x + slot.width / 2, slot.y + slot.height / 2);
    });

    const mp = mousePosRef.current;
    if (corner1 && mp) {
      const sx = Math.min(corner1.x, mp.x);
      const sy = Math.min(corner1.y, mp.y);
      const sw = Math.abs(mp.x - corner1.x);
      const sh = Math.abs(mp.y - corner1.y);
      const idx = slots.length;
      ctx.fillStyle = COLORS[idx % COLORS.length];
      roundCanvasRect(ctx, sx, sy, sw, sh, 0);
      ctx.fill();
      ctx.strokeStyle = "#ffffff";
      ctx.lineWidth = 2;
      ctx.setLineDash([6, 4]);
      ctx.stroke();
      ctx.setLineDash([]);
      ctx.fillStyle = "#fbbf24";
      ctx.beginPath();
      ctx.arc(corner1.x, corner1.y, 12, 0, Math.PI * 2);
      ctx.fill();
    }

    ctx.restore();
  }, [slots, selectedIdx, corner1, logicalSize]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    if (!frame.overlayImage) { imgRef.current = null; setImgSize(null); return; }
    const img = new Image();
    img.crossOrigin = "anonymous";
    img.onload = () => {
      imgRef.current = img;
      setImgSize({ w: img.naturalWidth, h: img.naturalHeight });
    };
    img.onerror = () => { imgRef.current = null; setImgSize(null); };
    img.src = frame.overlayImage;
  }, [frame.overlayImage]);

  useEffect(() => { redraw(); }, [redraw, imgSize]);

  const handleCanvasClick = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const { w: lw, h: lh } = logicalSize;
    const { x, y } = scaleToCanvas(e.clientX, e.clientY, canvas, lw, lh);
    if (!corner1) { setCorner1({ x, y }); return; }
    const sx = Math.round(Math.min(corner1.x, x));
    const sy = Math.round(Math.min(corner1.y, y));
    const sw = Math.round(Math.abs(x - corner1.x));
    const sh = Math.round(Math.abs(y - corner1.y));
    if (sw < 10 || sh < 10) { setCorner1(null); return; }
    if (selectedIdx >= 0) {
      setSlots((prev) => prev.map((s, i) => (i === selectedIdx ? { ...s, x: sx, y: sy, width: sw, height: sh } : s)));
    } else {
      setSlots((prev) => [...prev, { id: `slot${prev.length + 1}`, x: sx, y: sy, width: sw, height: sh, borderRadius: 0 }]);
    }
    setCorner1(null);
  }, [corner1, selectedIdx, logicalSize]);

  const handleCanvasMouseMove = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    if (!corner1) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const { w: lw, h: lh } = logicalSize;
    const { x, y } = scaleToCanvas(e.clientX, e.clientY, canvas, lw, lh);
    mousePosRef.current = { x, y };
    redraw();
  }, [corner1, redraw, logicalSize]);

  const handleCanvasMouseLeave = useCallback(() => {
    mousePosRef.current = null;
    if (corner1) redraw();
  }, [corner1, redraw]);

  const updateSlot = (index: number, field: keyof StoredSlot, value: number) => {
    setSlots((prev) => prev.map((s, i) => (i === index ? { ...s, [field]: value } : s)));
  };

  const removeSlot = (index: number) => {
    if (slots.length <= 1) return;
    setSlots((prev) => prev.filter((_, i) => i !== index));
    setSelectedIdx(-1);
  };

  const autoDetectSlots = useCallback(async () => {
    if (!imgRef.current) return;
    const img = imgRef.current;
    const { w: lw, h: lh } = getLogicalSize(img.naturalWidth, img.naturalHeight);

    const offscreen = document.createElement("canvas");
    offscreen.width = img.naturalWidth || LOGICAL_MAX;
    offscreen.height = img.naturalHeight || LOGICAL_MAX;
    const ctx = offscreen.getContext("2d");
    if (!ctx) return;

    ctx.drawImage(img, 0, 0, offscreen.width, offscreen.height);
    const imageData = ctx.getImageData(0, 0, offscreen.width, offscreen.height);
    const data = imageData.data;
    const w = offscreen.width;
    const h = offscreen.height;

    const visited = new Uint8Array(w * h);
    const regions: { minX: number; maxX: number; minY: number; maxY: number }[] = [];
    const threshold = 128;

    function floodFill(startX: number, startY: number) {
      const stack: [number, number][] = [[startX, startY]];
      let minX = startX, maxX = startX, minY = startY, maxY = startY;
      visited[startY * w + startX] = 1;

      while (stack.length > 0) {
        const [cx, cy] = stack.pop()!;
        if (cx < minX) minX = cx;
        if (cx > maxX) maxX = cx;
        if (cy < minY) minY = cy;
        if (cy > maxY) maxY = cy;

        for (const [nx, ny] of [[cx - 1, cy], [cx + 1, cy], [cx, cy - 1], [cx, cy + 1]]) {
          if (nx < 0 || nx >= w || ny < 0 || ny >= h) continue;
          if (visited[ny * w + nx]) continue;
          const alpha = data[(ny * w + nx) * 4 + 3];
          if (alpha < threshold) {
            visited[ny * w + nx] = 1;
            stack.push([nx, ny]);
          }
        }
      }

      const area = (maxX - minX) * (maxY - minY);
      if (area > 5000) {
        regions.push({ minX, maxX, minY, maxY });
      }
    }

    for (let y = 0; y < h; y++) {
      for (let x = 0; x < w; x++) {
        if (visited[y * w + x]) continue;
        const alpha = data[(y * w + x) * 4 + 3];
        if (alpha < threshold) {
          floodFill(x, y);
        }
      }
    }

    if (regions.length === 0) return;

    const scaleX = lw / w;
    const scaleY = lh / h;

    const detected: StoredSlot[] = regions
      .filter((r) => (r.maxX - r.minX) > 20 && (r.maxY - r.minY) > 20)
      .map((r, i) => ({
        id: `slot${i + 1}`,
        x: Math.round(r.minX * scaleX),
        y: Math.round(r.minY * scaleY),
        width: Math.round((r.maxX - r.minX) * scaleX),
        height: Math.round((r.maxY - r.minY) * scaleY),
        borderRadius: 0,
      }));

    if (detected.length > 0) {
      setSlots(detected);
      setSelectedIdx(-1);
      setCorner1(null);
    }
  }, []);

  return createPortal(
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
      <div className="bg-clay-bg border border-glass-border rounded-2xl w-full max-w-5xl max-h-[90vh] overflow-y-auto p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="heading-5 text-ink">Sửa Slot: {frame.displayName}</h2>
          <button onClick={onClose} className="text-ink-muted hover:text-ink text-xl">&times;</button>
        </div>
        <p className="body-sm text-ink-secondary mb-4">
          Nhấn <strong>Auto Detect</strong> để tự động tìm vùng trong suốt &mdash; hoặc nhấn hai điểm trên ảnh để đặt slot thủ công
        </p>
        <div className="flex flex-col lg:flex-row gap-6">
          <div className="flex-1 flex flex-col items-center">
            <canvas
              ref={canvasRef}
              width={displaySize.w}
              height={displaySize.h}
              className="rounded-lg border border-glass-border cursor-crosshair"
              style={{ maxWidth: '100%' }}
              onClick={handleCanvasClick}
              onMouseMove={handleCanvasMouseMove}
              onMouseLeave={handleCanvasMouseLeave}
            />
            {corner1 && (
              <p className="body-xs text-emerald-400 mt-2 text-center">
                Đã chọn góc 1 &mdash; nhấn lại để chọn góc đối diện
              </p>
            )}
          </div>
          <div className="w-full lg:w-80 space-y-3">
            <div className="flex items-center justify-between">
              <p className="body-sm-medium text-ink">Slots ({slots.length})</p>
              <button onClick={autoDetectSlots} className="text-xs text-emerald-400 hover:text-emerald-300" title="Tự động phát hiện vùng trong suốt">
Tự động
              </button>
            </div>
            {slots.map((slot, i) => (
              <div
                key={slot.id}
                className={`glass-card p-3 space-y-2 cursor-pointer border-2 ${selectedIdx === i ? "border-amber-400" : "border-transparent"}`}
                onClick={() => { setSelectedIdx(i); setCorner1(null); }}
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-ink">Slot #{i + 1}</span>
                  <button onClick={(e) => { e.stopPropagation(); removeSlot(i); }} className="text-xs text-red-400 hover:text-red-300">Xóa</button>
                </div>
                <div className="grid grid-cols-2 gap-2">
                  {(["x", "y", "width", "height"] as const).map((field) => (
                    <label key={field} className="flex flex-col">
                      <span className="text-[10px] uppercase tracking-wider text-ink-muted mb-0.5">{field}</span>
                      <input type="number" value={slot[field]} onChange={(e) => updateSlot(i, field, Number(e.target.value))}
                        className="w-full rounded border border-glass-border bg-glass-surface px-2 py-1 text-xs text-ink" min={0} max={LOGICAL_MAX} />
                    </label>
                  ))}
                </div>
                <label className="flex flex-col">
                  <span className="text-[10px] uppercase tracking-wider text-ink-muted mb-0.5">Bo góc</span>
                  <input type="number" value={slot.borderRadius} onChange={(e) => updateSlot(i, "borderRadius", Number(e.target.value))}
                    className="w-full rounded border border-glass-border bg-glass-surface px-2 py-1 text-xs text-ink" min={0} max={200} />
                </label>
              </div>
            ))}
          </div>
        </div>
        <div className="mt-6 flex gap-3 justify-end">
          <button onClick={onClose} className="btn-ghost px-6 py-3">Hủy</button>
          <button onClick={() => onSave({ ...frame, slots, photoCount: slots.length as 1 | 2 | 3 | 4 | 6 })} className="btn-primary px-6 py-3">Lưu Slot</button>
        </div>
      </div>
    </div>,
    document.body
  );
}

function UploadDialog({
  onUpload,
  onClose,
}: {
  onUpload: (frame: StoredFrame, file: File | null) => void;
  onClose: () => void;
}) {
  const [name, setName] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [photoCount, setPhotoCount] = useState<number>(4);
  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const handleFile = (f: File | null) => {
    setFile(f);
    if (f) {
      const reader = new FileReader();
      reader.onload = (e) => setPreview(e.target?.result as string);
      reader.readAsDataURL(f);
    } else {
      setPreview(null);
    }
  };

  const handleSubmit = async () => {
    if (!name.trim()) return;
    setErr(null);
    setUploading(true);
    try {
      const id = name.trim().toLowerCase().replace(/\s+/g, "-");
      const frame: StoredFrame = {
        id,
        name: name.trim(),
        displayName: displayName.trim() || name.trim(),
        photoCount: photoCount as 1 | 2 | 3 | 4 | 6,
        description: `Khung ${photoCount} ảnh với ảnh nền trang trí`,
        slots: emptySlots(photoCount),
        overlayImage: "",
        filter: "normal",
        backgroundColor: "#ffffff",
      };
      await onUpload(frame, file);
    } catch (e: any) {
      setErr(e?.message || "Tải lên thất bại");
    } finally {
      setUploading(false);
    }
  };

  return createPortal(
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
      <div className="bg-clay-bg border border-glass-border rounded-2xl w-full max-w-lg p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="heading-5 text-ink">Tải khung mới</h2>
          <button onClick={onClose} className="text-ink-muted hover:text-ink text-xl">&times;</button>
        </div>

        {err && (
          <div className="mb-4 flex items-center gap-3 rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 body-sm text-red-400">
            <span>{err}</span>
            <button onClick={() => setErr(null)} className="ml-auto text-red-400 hover:text-red-300">&times;</button>
          </div>
        )}

        <div className="space-y-4">
          <label className="flex flex-col">
            <span className="body-sm-medium text-ink mb-1">Mã khung / Tên *</span>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="vd: khung-cua-toi"
              className="w-full rounded-lg border border-glass-border bg-glass-surface px-4 py-3 text-ink"
              disabled={uploading}
            />
          </label>

          <label className="flex flex-col">
            <span className="body-sm-medium text-ink mb-1">Tên hiển thị</span>
            <input
              type="text"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              placeholder="vd: Khung Của Tôi"
              className="w-full rounded-lg border border-glass-border bg-glass-surface px-4 py-3 text-ink"
              disabled={uploading}
            />
          </label>

          <label className="flex flex-col">
            <span className="body-sm-medium text-ink mb-1">Số lượng ảnh</span>
            <select
              value={photoCount}
              onChange={(e) => setPhotoCount(Number(e.target.value))}
              className="w-full rounded-lg border border-glass-border bg-glass-surface px-4 py-3 text-ink"
              disabled={uploading}
            >
              {[1, 2, 3, 4, 6].map((n) => (
                <option key={n} value={n}>
                  {n} photos
                </option>
              ))}
            </select>
          </label>

          <label className="flex flex-col">
            <span className="body-sm-medium text-ink mb-1">Ảnh nền khung</span>
            <input
              type="file"
              accept="image/png,image/jpeg,image/webp"
              onChange={(e) => handleFile(e.target.files?.[0] ?? null)}
              className="w-full text-ink file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-emerald-500/10 file:text-emerald-400 file:text-sm file:font-bold hover:file:bg-emerald-500/20"
              disabled={uploading}
            />
          </label>

          {preview && (
            <div className="rounded-lg overflow-hidden border border-glass-border">
              <img src={preview} alt="Preview" className="w-full max-h-48 object-contain bg-cosmic-base" />
            </div>
          )}
        </div>

        <div className="mt-6 flex gap-3 justify-end">
          <button onClick={onClose} className="btn-ghost px-6 py-3" disabled={uploading}>
            Hủy
          </button>
          <button
            onClick={handleSubmit}
            disabled={!name.trim() || uploading}
            className="btn-primary px-6 py-3 disabled:opacity-50 flex items-center gap-2"
          >
            {uploading && (
              <svg className="h-4 w-4 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
              </svg>
            )}
            {uploading ? "Đang tải lên..." : "Tải khung lên"}
          </button>
        </div>
      </div>
    </div>,
    document.body
  );
}
