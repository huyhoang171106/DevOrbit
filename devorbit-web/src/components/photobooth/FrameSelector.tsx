import { useState } from "react";
import {
  FrameDefinition,
  FRAME_DEFINITIONS,
} from "../../lib/frames/frameDefinitions";

interface FrameSelectorProps {
  selectedFrame: FrameDefinition | null;
  onFrameSelect: (frame: FrameDefinition) => void;
}

export function FrameSelector({
  selectedFrame,
  onFrameSelect,
}: FrameSelectorProps) {
  const [imgErrors, setImgErrors] = useState<Set<string>>(new Set());

  return (
    <div className="glass-card p-8">
      <h3 className="heading-5 text-ink mb-2">Chọn khung</h3>
      <p className="body-sm text-ink-secondary mb-6">
        Chọn kiểu cho dải ảnh của bạn
      </p>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {FRAME_DEFINITIONS.map((frame) => (
          <button
            key={frame.id}
            onClick={() => onFrameSelect(frame)}
            className={`orbit-card p-4 text-left transition-all border-2 ${
              selectedFrame?.id === frame.id
                ? "border-emerald-400 bg-emerald-400/10"
                : "border-zinc-700/50 hover:border-emerald-400/50"
            }`}>
            <div className="w-full h-[200px] rounded-lg mb-3 overflow-hidden flex items-center justify-center bg-cosmic-base">
              {frame.overlayImage && !imgErrors.has(frame.id) ? (
                <img
                  src={frame.overlayImage}
                  alt={frame.displayName}
                  className="w-full h-full object-contain"
                  onError={() =>
                    setImgErrors((prev) => new Set(prev).add(frame.id))
                  }
                />
              ) : (
                <span className="text-xs font-mono text-ink-muted">
                  {frame.photoCount}x
                  {frame.photoCount === 4 ? "4" : ""}
                </span>
              )}
            </div>

            <div className="flex items-start justify-between gap-2">
              <div className="min-w-0 flex-1">
                <p className="body-sm-medium text-ink truncate flex items-center gap-1">
                  {frame.isSpecial && <span>🎓</span>}
                  {frame.displayName}
                </p>
                <p className="body-sm text-ink-secondary mt-1 line-clamp-2">
                  {frame.description}
                </p>
              </div>
              <div className="flex-shrink-0">
                {selectedFrame?.id === frame.id && (
                  <svg
                    className="h-5 w-5 text-clay-primary"
                    fill="currentColor"
                    viewBox="0 0 20 20">
                    <path
                      fillRule="evenodd"
                      d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                      clipRule="evenodd"
                    />
                  </svg>
                )}
              </div>
            </div>

            <div className="mt-3 flex items-center gap-2">
              <span className="badge-tag text-xs">
                {frame.photoCount} ảnh
              </span>
              <span className="badge-tag text-xs capitalize">
                {frame.filter}
              </span>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
