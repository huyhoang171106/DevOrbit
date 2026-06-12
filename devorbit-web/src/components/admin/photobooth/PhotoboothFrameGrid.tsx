import { useState, useEffect, useRef } from 'react'
import type { StoredFrame } from '../../../types/frames'

interface FrameCardProps {
  frame: StoredFrame
  onEditSlots: () => void
  onDelete: () => void
  onRename: (f: StoredFrame, name: string) => void
}

export function FrameCard({ frame, onEditSlots, onDelete, onRename }: FrameCardProps) {
  const [imgErr, setImgErr] = useState(false)
  const [editing, setEditing] = useState(false)
  const [editName, setEditName] = useState(frame.displayName)
  const inputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    if (editing && inputRef.current) {
      inputRef.current.focus()
      inputRef.current.select()
    }
  }, [editing])

  const handleSave = () => {
    const trimmed = editName.trim()
    if (trimmed && trimmed !== frame.displayName) {
      onRename(frame, trimmed)
    }
    setEditing(false)
  }

  return (
    <div className="glass-card overflow-hidden border border-orbit-border">
      <div className="bg-black/40 flex items-center justify-center overflow-hidden h-[300px]">
        {frame.overlayImage && !imgErr ? (
          <img
            src={frame.overlayImage}
            alt={frame.displayName}
            className="w-full h-full object-contain"
            onError={() => setImgErr(true)}
          />
        ) : (
          <span className="text-xs font-mono text-ink-muted">
{frame.photoCount} ảnh
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
            onKeyDown={(e) => {
              if (e.key === 'Enter') handleSave()
              if (e.key === 'Escape') { setEditName(frame.displayName); setEditing(false) }
            }}
            className="w-full rounded border border-orbit-border bg-orbit-surface px-2 py-1 text-sm text-ink-primary mb-2"
          />
        ) : (
          <h3
            className="text-sm font-medium text-ink-primary truncate cursor-pointer hover:text-orbit-accent transition-colors"
            onClick={() => { setEditing(true); setEditName(frame.displayName) }}
            title="Nhấn để đổi tên"
          >
            {frame.displayName}
          </h3>
        )}
        <p className="text-xs text-ink-secondary mt-1">
          {frame.photoCount} ảnh &middot; {frame.slots.length} ô
        </p>
        <div className="mt-3 flex gap-2">
          <button onClick={onEditSlots} className="btn-primary text-xs px-4 py-2">Sửa ô</button>
          <button onClick={onDelete} className="btn-ghost text-xs px-4 py-2 text-red-400">Xoá</button>
        </div>
      </div>
    </div>
  )
}

interface PhotoboothFrameGridProps {
  frames: StoredFrame[]
  onEditSlots: (frame: StoredFrame) => void
  onDelete: (id: string) => void
  onRename: (frame: StoredFrame, name: string) => void
}

export function PhotoboothFrameGrid({ frames, onEditSlots, onDelete, onRename }: PhotoboothFrameGridProps) {
  if (frames.length === 0) {
    return (
      <div className="glass-card p-12 text-center">
        <p className="text-ink-secondary">Chưa có frame nào. Tải lên frame đầu tiên!</p>
      </div>
    )
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {frames.map((frame) => (
        <FrameCard
          key={frame.id}
          frame={frame}
          onEditSlots={() => onEditSlots(frame)}
          onDelete={() => onDelete(frame.id)}
          onRename={onRename}
        />
      ))}
    </div>
  )
}
