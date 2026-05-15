import { useState } from "react";
import { PhotoCompositor } from "../../lib/photoCompositor";
import { FrameDefinition } from "../../lib/frames/frameDefinitions";

interface DownloadSectionProps {
  canvas: HTMLCanvasElement | null;
  frame: FrameDefinition | null;
}

export function DownloadSection({ canvas, frame }: DownloadSectionProps) {
  const [isExporting, setIsExporting] = useState(false);
  const [exportSuccess, setExportSuccess] = useState(false);

  const handleDownload = async () => {
    if (!canvas || !frame) return;

    setIsExporting(true);
    try {
      const timestamp = new Date().toISOString().slice(0, 10);
      const fileName = `photobooth-${frame.id}-${timestamp}.png`;
      await PhotoCompositor.exportImage(canvas, fileName);
      setExportSuccess(true);
      setTimeout(() => setExportSuccess(false), 3000);
    } catch (error) {
      console.error("Export failed:", error);
      alert("Xuất ảnh thất bại. Vui lòng thử lại.");
    } finally {
      setIsExporting(false);
    }
  };

  return (
    <div className="glass-card p-8">
      <h3 className="heading-5 text-ink mb-2">Tải Photobooth của bạn</h3>
      <p className="body-sm text-ink-secondary mb-6">
        Lưu kỷ niệm dưới dạng ảnh PNG chất lượng cao
      </p>

      <div className="space-y-3">
        <button
          onClick={handleDownload}
          disabled={!canvas || isExporting}
          className="w-full btn-primary disabled:opacity-50 disabled:cursor-not-allowed py-3 flex items-center justify-center gap-2">
          {isExporting ? (
            <>
              <svg
                className="h-5 w-5 animate-spin"
                fill="none"
                viewBox="0 0 24 24">
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                />
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                />
              </svg>
              Đang chuẩn bị...
            </>
          ) : (
            <>
              <svg
                className="h-5 w-5"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"
                />
              </svg>
              Tải xuống PNG ({canvas?.width ?? 2000}&times;{canvas?.height ?? 2000}px)
            </>
          )}
        </button>

        {exportSuccess && (
          <div className="p-3 bg-emerald-400/10 border border-emerald-400/30 rounded-lg flex items-center gap-2">
            <svg
              className="h-5 w-5 text-emerald-400 flex-shrink-0"
              fill="currentColor"
              viewBox="0 0 20 20">
              <path
                fillRule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                clipRule="evenodd"
              />
            </svg>
            <p className="body-sm text-clay-primary">
              Đã tải xuống! Kiểm tra thư mục tải về
            </p>
          </div>
        )}
      </div>

      <div className="mt-6 p-4 bg-glass-surface rounded-lg">
        <div className="body-sm text-ink-secondary">
          <span className="font-semibold text-ink block mb-2">📸 Mẹo:</span>
          <ul className="space-y-1 ml-4 list-disc">
            <li>Sử dụng ảnh chân dung để có kết quả tốt nhất</li>
            <li>Đảm bảo ánh sáng tốt trong ảnh</li>
            <li>Chia sẻ tác phẩm lên mạng xã hội!</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
