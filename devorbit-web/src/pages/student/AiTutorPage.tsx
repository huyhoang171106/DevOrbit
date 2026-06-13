import { Sparkle, ArrowsClockwise } from '@phosphor-icons/react'
import { ChatPanel } from '../../components/student/ChatPanel'
import { useChat } from '../../components/student/ChatContext'

export function AiTutorPage() {
  const { clearHistory } = useChat()

  return (
    <div className="w-full h-full flex flex-col overflow-hidden">
      <div className="flex-1 flex justify-center px-6 md:px-10">
        <div className="w-full max-w-[1000px] flex flex-col gap-4 py-4 md:py-6 min-h-0">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="h-12 w-12 rounded-2xl bg-orbit-accent/15 border border-orbit-accent/20 flex items-center justify-center">
                <Sparkle className="h-6 w-6 text-orbit-accent" weight="fill" />
              </div>
              <div>
                <h1 className="text-[20px] font-bold text-zinc-100">DevOrbit AI</h1>
                <p className="text-[13px] text-zinc-400">Trợ lý học tập thông minh</p>
              </div>
            </div>
            <button
              onClick={clearHistory}
              title="Đoạn chat mới"
              aria-label="Đoạn chat mới"
              className="p-2 rounded-lg text-zinc-400 hover:text-orbit-accent hover:bg-zinc-800/40 transition-colors"
            >
              <ArrowsClockwise className="h-5 w-5" aria-hidden="true" />
            </button>
          </div>
          <div className="h-[720px]">
            <ChatPanel fullPage />
          </div>
        </div>
      </div>
    </div>
  )
}
