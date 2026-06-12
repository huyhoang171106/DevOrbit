export function AdminSpinner({ text = 'Đang tải...' }: { text?: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 gap-4">
      <div className="relative h-10 w-10">
        <div className="absolute inset-0 rounded-full border-2 border-orbit-accent/10" />
        <div className="absolute inset-0 rounded-full border-t-2 border-orbit-accent animate-spin shadow-[0_0_15px_rgba(52,211,153,0.2)]" />
      </div>
      <p className="text-xs font-black text-orbit-accent tracking-[0.25em] uppercase">{text}</p>
    </div>
  )
}
