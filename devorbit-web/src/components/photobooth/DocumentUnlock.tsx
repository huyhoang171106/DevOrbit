import { useRef } from "react";

interface DocumentUnlockProps {
  isUnlocked: boolean;
  onUnlock: () => void;
}

export function DocumentUnlock({ isUnlocked, onUnlock }: DocumentUnlockProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      onUnlock();
      e.target.value = "";
    }
  };

  if (isUnlocked) {
    return (
      <div className="orbit-card p-6 text-center border border-emerald-400/30 bg-emerald-400/5">
        <div className="flex items-center justify-center gap-2 text-emerald-400 text-sm font-semibold mb-2">
          <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
            <path
              fillRule="evenodd"
              d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
              clipRule="evenodd"
            />
          </svg>
          Đã mở khóa Photobooth!
        </div>
        <p className="text-xs text-zinc-400">
          Bạn đã sẵn sàng tạo kỷ niệm
        </p>
      </div>
    );
  }

  return (
    <div className="glass-card p-8">
      <h3 className="text-sm font-semibold text-orbit-text tracking-wide mb-2">Mở khóa kỷ niệm</h3>
      <p className="text-xs text-zinc-400 mb-6">
        Tải tài liệu lên để mở khóa tính năng photobooth và bắt đầu tạo
      </p>

      <button
        onClick={() => fileInputRef.current?.click()}
        className="w-full border-2 border-dashed border-zinc-700 rounded-lg p-8 hover:border-emerald-400/50 transition-colors cursor-pointer group">
        <input
          ref={fileInputRef}
          type="file"
          accept=".pdf,.doc,.docx,.jpg,.jpeg,.png,.zip"
          onChange={handleFileSelect}
          className="hidden"
        />
        <div className="flex flex-col items-center gap-3">
          <svg
            className="h-8 w-8 text-zinc-500 group-hover:text-emerald-400 transition-colors"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 4v16m8-8H4"
            />
          </svg>
          <div className="text-center">
            <p className="text-xs font-semibold text-zinc-300">
              Nhấn để tải tài liệu lên
            </p>
            <p className="text-xs text-zinc-500">hoặc kéo thả</p>
          </div>
        </div>
      </button>

      <p className="text-xs text-zinc-600 mt-4 text-center">
        PDF, DOC, ảnh hoặc ZIP
      </p>
    </div>
  );
}
