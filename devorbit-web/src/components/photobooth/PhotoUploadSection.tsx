import { useRef, useState } from "react";

interface PhotoUploadSectionProps {
  maxPhotos: number;
  onPhotosSelected: (files: File[]) => void;
  selectedCount: number;
}

export function PhotoUploadSection({
  maxPhotos,
  onPhotosSelected,
  selectedCount,
}: PhotoUploadSectionProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [dragActive, setDragActive] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleFiles = (files: FileList | null) => {
    setError(null);
    if (!files || files.length === 0) return;

    const imageFiles = Array.from(files).filter((file) =>
      file.type.startsWith("image/"),
    );

    if (imageFiles.length === 0) {
      setError("Please select image files");
      return;
    }

    if (imageFiles.length > maxPhotos) {
      setError(`You can only upload up to ${maxPhotos} photos for this frame`);
      return;
    }

    onPhotosSelected(imageFiles);
  };

  const handleDrag = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    handleFiles(e.dataTransfer.files);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    handleFiles(e.target.files);
  };

  return (
    <div className="glass-card p-8">
      <h3 className="heading-5 text-ink mb-2">Upload Photos</h3>
      <p className="body-sm text-ink-secondary mb-6">
        Select {maxPhotos} photo{maxPhotos !== 1 ? "s" : ""} ({selectedCount}/
        {maxPhotos} selected)
      </p>

      <div
        onDragEnter={handleDrag}
        onDragLeave={handleDrag}
        onDragOver={handleDrag}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current?.click()}
        className={`border-2 border-dashed rounded-lg p-12 cursor-pointer transition-all ${
          dragActive
            ? "border-clay-primary bg-clay-primary/10"
            : "border-glass-border hover:border-clay-primary/50 hover:bg-glass-surface-hover"
        }`}>
        <input
          ref={fileInputRef}
          type="file"
          multiple
          accept="image/*"
          onChange={handleInputChange}
          className="hidden"
        />

        <div className="flex flex-col items-center gap-3">
          <svg
            className="h-10 w-10 text-clay-primary"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
            />
          </svg>
          <div className="text-center">
            <p className="body-sm-medium text-ink">
              Drag photos here or click to select
            </p>
            <p className="body-sm text-ink-secondary mt-1">
              JPG, PNG, GIF supported • Max {maxPhotos} files
            </p>
          </div>
        </div>
      </div>

      {error && (
        <div className="mt-4 p-3 bg-red-500/10 border border-red-500/30 rounded-lg">
          <p className="body-sm text-red-400">{error}</p>
        </div>
      )}

      {selectedCount > 0 && (
        <div className="mt-4 p-3 bg-clay-primary/10 border border-clay-primary/30 rounded-lg">
          <p className="body-sm text-clay-primary">
            ✓ {selectedCount} photo{selectedCount !== 1 ? "s" : ""} ready to
            composite
          </p>
        </div>
      )}
    </div>
  );
}
