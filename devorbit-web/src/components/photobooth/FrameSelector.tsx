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
  return (
    <div className="glass-card p-8">
      <h3 className="heading-5 text-ink mb-2">Choose a Frame</h3>
      <p className="body-sm text-ink-secondary mb-6">
        Select a style for your photo strip
      </p>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {FRAME_DEFINITIONS.map((frame) => (
          <button
            key={frame.id}
            onClick={() => onFrameSelect(frame)}
            className={`glass-card p-4 text-left transition-all border-2 ${
              selectedFrame?.id === frame.id
                ? "border-clay-primary bg-clay-primary/10"
                : "border-glass-border hover:border-clay-primary/50"
            }`}>
            {/* Frame preview simulation */}
            <div
              className="w-full aspect-video rounded-lg mb-3 flex items-center justify-center text-xs font-mono"
              style={{ backgroundColor: frame.backgroundColor }}>
              <span style={{ color: frame.accentColor || "#10b981" }}>
                {frame.photoCount}x{frame.photoCount === 4 ? "4" : ""}
              </span>
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
                {frame.photoCount} photos
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
