import { useState, useRef, useEffect, useCallback, useMemo } from 'react'
import { createPortal } from 'react-dom'
import type { StoredFrame, StoredSlot } from '../../../types/frames'

const LOGICAL_MAX = 2000
const COLORS = [
  'rgba(16, 185, 129, 0.3)', 'rgba(59, 130, 246, 0.3)', 'rgba(239, 68, 68, 0.3)',
  'rgba(234, 179, 8, 0.3)', 'rgba(168, 85, 247, 0.3)', 'rgba(236, 72, 153, 0.3)',
]
const CANVAS_DISPLAY_SIZE = 600

function roundCanvasRect(ctx: CanvasRenderingContext2D, x: number, y: number, w: number, h: number, r: number) {
  const rad = Math.min(r, Math.min(w, h) / 2)
  ctx.beginPath()
  ctx.moveTo(x + rad, y)
  ctx.lineTo(x + w - rad, y)
  ctx.arcTo(x + w, y, x + w, y + rad, rad)
  ctx.lineTo(x + w, y + h - rad)
  ctx.arcTo(x + w, y + h, x + w - rad, y + h, rad)
  ctx.lineTo(x + rad, y + h)
  ctx.arcTo(x, y + h, x, y + h - rad, rad)
  ctx.lineTo(x, y + rad)
  ctx.arcTo(x, y, x + rad, y, rad)
  ctx.closePath()
}

function getLogicalSize(imgW: number, imgH: number) {
  const maxDim = Math.max(imgW, imgH)
  if (maxDim === 0) return { w: LOGICAL_MAX, h: LOGICAL_MAX }
  const scale = LOGICAL_MAX / maxDim
  return { w: Math.round(imgW * scale), h: Math.round(imgH * scale) }
}

function scaleToCanvas(clientX: number, clientY: number, canvas: HTMLCanvasElement, logicalW: number, logicalH: number) {
  const rect = canvas.getBoundingClientRect()
  return {
    x: (clientX - rect.left) * (logicalW / rect.width),
    y: (clientY - rect.top) * (logicalH / rect.height),
  }
}

interface FrameSlotEditorProps {
  frame: StoredFrame
  onSave: (f: StoredFrame) => void
  onClose: () => void
}

export function FrameSlotEditor({ frame, onSave, onClose }: FrameSlotEditorProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const imgRef = useRef<HTMLImageElement | null>(null)
  const [slots, setSlots] = useState<StoredSlot[]>(frame.slots)
  const [selectedIdx, setSelectedIdx] = useState<number>(-1)
  const [corner1, setCorner1] = useState<{ x: number; y: number } | null>(null)
  const mousePosRef = useRef<{ x: number; y: number } | null>(null)
  const [imgSize, setImgSize] = useState<{ w: number; h: number } | null>(null)

  const logicalSize = useMemo(() => {
    if (!imgSize) return { w: LOGICAL_MAX, h: LOGICAL_MAX }
    return getLogicalSize(imgSize.w, imgSize.h)
  }, [imgSize])

  const displaySize = useMemo(() => {
    if (!imgSize) return { w: CANVAS_DISPLAY_SIZE, h: CANVAS_DISPLAY_SIZE }
    const maxDim = Math.max(imgSize.w, imgSize.h)
    const scale = CANVAS_DISPLAY_SIZE / maxDim
    return { w: Math.round(imgSize.w * scale), h: Math.round(imgSize.h * scale) }
  }, [imgSize])

  const redraw = useCallback(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext('2d')
    if (!ctx) return
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    const { w: lw, h: lh } = logicalSize
    const scaleX = canvas.width / lw
    const scaleY = canvas.height / lh
    ctx.save()
    ctx.scale(scaleX, scaleY)
    ctx.fillStyle = '#1a1a2e'
    ctx.fillRect(0, 0, lw, lh)
    if (imgRef.current) ctx.drawImage(imgRef.current, 0, 0, lw, lh)
    slots.forEach((slot, i) => {
      ctx.fillStyle = COLORS[i % COLORS.length]
      roundCanvasRect(ctx, slot.x, slot.y, slot.width, slot.height, slot.borderRadius)
      ctx.fill()
      ctx.strokeStyle = i === selectedIdx ? '#fbbf24' : '#ffffff'
      ctx.lineWidth = i === selectedIdx ? 3 : 2
      ctx.stroke()
      ctx.fillStyle = '#ffffff'
      ctx.font = 'bold 13px monospace'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      ctx.fillText(`#${i + 1}`, slot.x + slot.width / 2, slot.y + slot.height / 2)
    })
    const mp = mousePosRef.current
    if (corner1 && mp) {
      const sx = Math.min(corner1.x, mp.x)
      const sy = Math.min(corner1.y, mp.y)
      const sw = Math.abs(mp.x - corner1.x)
      const sh = Math.abs(mp.y - corner1.y)
      const idx = slots.length
      ctx.fillStyle = COLORS[idx % COLORS.length]
      roundCanvasRect(ctx, sx, sy, sw, sh, 0)
      ctx.fill()
      ctx.strokeStyle = '#ffffff'
      ctx.lineWidth = 2
      ctx.setLineDash([6, 4])
      ctx.stroke()
      ctx.setLineDash([])
      ctx.fillStyle = '#fbbf24'
      ctx.beginPath()
      ctx.arc(corner1.x, corner1.y, 12, 0, Math.PI * 2)
      ctx.fill()
    }
    ctx.restore()
  }, [slots, selectedIdx, corner1, logicalSize])

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    if (!frame.overlayImage) { imgRef.current = null; setImgSize(null); return }
    const img = new Image()
    img.crossOrigin = 'anonymous'
    img.onload = () => { imgRef.current = img; setImgSize({ w: img.naturalWidth, h: img.naturalHeight }) }
    img.onerror = () => { imgRef.current = null; setImgSize(null) }
    img.src = frame.overlayImage
  }, [frame.overlayImage])

  useEffect(() => { redraw() }, [redraw, imgSize])

  const handleCanvasClick = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current
    if (!canvas) return
    const { w: lw, h: lh } = logicalSize
    const { x, y } = scaleToCanvas(e.clientX, e.clientY, canvas, lw, lh)
    if (!corner1) { setCorner1({ x, y }); return }
    const sx = Math.round(Math.min(corner1.x, x))
    const sy = Math.round(Math.min(corner1.y, y))
    const sw = Math.round(Math.abs(x - corner1.x))
    const sh = Math.round(Math.abs(y - corner1.y))
    if (sw < 10 || sh < 10) { setCorner1(null); return }
    if (selectedIdx >= 0) {
      setSlots((prev) => prev.map((s, i) => (i === selectedIdx ? { ...s, x: sx, y: sy, width: sw, height: sh } : s)))
    } else {
      setSlots((prev) => [...prev, { id: `slot${prev.length + 1}`, x: sx, y: sy, width: sw, height: sh, borderRadius: 0 }])
    }
    setCorner1(null)
  }, [corner1, selectedIdx, logicalSize])

  const handleCanvasMouseMove = useCallback((e: React.MouseEvent<HTMLCanvasElement>) => {
    if (!corner1) return
    const canvas = canvasRef.current
    if (!canvas) return
    const { w: lw, h: lh } = logicalSize
    const { x, y } = scaleToCanvas(e.clientX, e.clientY, canvas, lw, lh)
    mousePosRef.current = { x, y }
    redraw()
  }, [corner1, redraw, logicalSize])

  const handleCanvasMouseLeave = useCallback(() => {
    mousePosRef.current = null
    if (corner1) redraw()
  }, [corner1, redraw])

  const updateSlot = (index: number, field: keyof StoredSlot, value: number) => {
    setSlots((prev) => prev.map((s, i) => (i === index ? { ...s, [field]: value } : s)))
  }

  const removeSlot = (index: number) => {
    if (slots.length <= 1) return
    setSlots((prev) => prev.filter((_, i) => i !== index))
    setSelectedIdx(-1)
  }

  const autoDetectSlots = useCallback(async () => {
    if (!imgRef.current) return
    const img = imgRef.current
    const { w: lw, h: lh } = getLogicalSize(img.naturalWidth, img.naturalHeight)
    const offscreen = document.createElement('canvas')
    offscreen.width = img.naturalWidth || LOGICAL_MAX
    offscreen.height = img.naturalHeight || LOGICAL_MAX
    const ctx = offscreen.getContext('2d')
    if (!ctx) return
    ctx.drawImage(img, 0, 0, offscreen.width, offscreen.height)
    const imageData = ctx.getImageData(0, 0, offscreen.width, offscreen.height)
    const data = imageData.data
    const w = offscreen.width
    const h = offscreen.height
    const visited = new Uint8Array(w * h)
    const regions: { minX: number; maxX: number; minY: number; maxY: number }[] = []
    const threshold = 128

    function floodFill(startX: number, startY: number) {
      const stack: [number, number][] = [[startX, startY]]
      let minX = startX, maxX = startX, minY = startY, maxY = startY
      visited[startY * w + startX] = 1
      while (stack.length > 0) {
        const [cx, cy] = stack.pop()!
        if (cx < minX) minX = cx; if (cx > maxX) maxX = cx
        if (cy < minY) minY = cy; if (cy > maxY) maxY = cy
        for (const [nx, ny] of [[cx - 1, cy], [cx + 1, cy], [cx, cy - 1], [cx, cy + 1]] as const) {
          if (nx < 0 || nx >= w || ny < 0 || ny >= h) continue
          if (visited[ny * w + nx]) continue
          const alpha = data[(ny * w + nx) * 4 + 3]
          if (alpha < threshold) { visited[ny * w + nx] = 1; stack.push([nx, ny]) }
        }
      }
      const area = (maxX - minX) * (maxY - minY)
      if (area > 5000) regions.push({ minX, maxX, minY, maxY })
    }

    for (let y = 0; y < h; y++)
      for (let x = 0; x < w; x++)
        if (!visited[y * w + x]) {
          const alpha = data[(y * w + x) * 4 + 3]
          if (alpha < threshold) floodFill(x, y)
        }

    if (regions.length === 0) return
    const scaleX = lw / w
    const scaleY = lh / h
    const detected: StoredSlot[] = regions
      .filter((r) => (r.maxX - r.minX) > 20 && (r.maxY - r.minY) > 20)
      .map((r, i) => ({
        id: `slot${i + 1}`,
        x: Math.round(r.minX * scaleX),
        y: Math.round(r.minY * scaleY),
        width: Math.round((r.maxX - r.minX) * scaleX),
        height: Math.round((r.maxY - r.minY) * scaleY),
        borderRadius: 0,
      }))
    if (detected.length > 0) { setSlots(detected); setSelectedIdx(-1); setCorner1(null) }
  }, [])

  return createPortal(
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
       <div className="glass-card w-full max-w-5xl max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between mb-4">
          <h2 className="heading-5 text-ink-primary">Edit Slots: {frame.displayName}</h2>
          <button onClick={onClose} className="text-ink-secondary hover:text-ink-primary text-xl">&times;</button>
        </div>
        <p className="text-sm text-ink-secondary mb-4">
          Click <strong>Auto Detect</strong> to find transparent regions, or click two points on the image to create a slot manually.
        </p>
        <div className="flex flex-col lg:flex-row gap-6">
          <div className="flex-1 flex flex-col items-center" style={{ touchAction: 'pan-y' }}>
            <canvas
              ref={canvasRef}
              width={displaySize.w}
              height={displaySize.h}
              className="rounded-lg border border-orbit-border cursor-crosshair"
              style={{ maxWidth: '100%', touchAction: 'pan-y' }}
              onClick={handleCanvasClick}
              onMouseMove={handleCanvasMouseMove}
              onMouseLeave={handleCanvasMouseLeave}
            />
            {corner1 && <p className="text-xs text-amber-400 mt-2">First corner selected — click again for opposite corner</p>}
          </div>
          <div className="w-full lg:w-80 space-y-3">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium text-ink-primary">Slots ({slots.length})</p>
              <button onClick={autoDetectSlots} className="text-xs text-orbit-accent hover:text-orbit-accent/80">Auto Detect</button>
            </div>
            {slots.map((slot, i) => (
              <div
                key={slot.id}
                className={`glass-card p-3 space-y-2 cursor-pointer border-2 ${selectedIdx === i ? 'border-amber-400' : 'border-transparent'}`}
                onClick={() => { setSelectedIdx(i); setCorner1(null) }}
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-ink-primary">Slot #{i + 1}</span>
                  <button onClick={(e) => { e.stopPropagation(); removeSlot(i) }} className="text-xs text-red-400">Delete</button>
                </div>
                <div className="grid grid-cols-2 gap-2">
                  {(['x', 'y', 'width', 'height'] as const).map((field) => (
                    <label key={field} className="flex flex-col">
                      <span className="text-[10px] uppercase tracking-wider text-ink-muted mb-0.5">{field}</span>
                      <input type="number" value={slot[field]} onChange={(e) => updateSlot(i, field, Number(e.target.value))}
                        className="w-full rounded border border-orbit-border bg-orbit-surface px-2 py-1 text-xs text-ink-primary" min={0} max={LOGICAL_MAX} />
                    </label>
                  ))}
                </div>
                <label className="flex flex-col">
                  <span className="text-[10px] uppercase tracking-wider text-ink-muted mb-0.5">Border Radius</span>
                  <input type="number" value={slot.borderRadius} onChange={(e) => updateSlot(i, 'borderRadius', Number(e.target.value))}
                    className="w-full rounded border border-orbit-border bg-orbit-surface px-2 py-1 text-xs text-ink-primary" min={0} max={200} />
                </label>
              </div>
            ))}
          </div>
        </div>
        <div className="mt-6 flex gap-3 justify-end">
          <button onClick={onClose} className="btn-ghost px-6 py-3">Cancel</button>
          <button onClick={() => onSave({ ...frame, slots, photoCount: slots.length as 1 | 2 | 3 | 4 | 6 })} className="btn-primary px-6 py-3">Save Slots</button>
        </div>
      </div>
    </div>,
    document.body,
  )
}
