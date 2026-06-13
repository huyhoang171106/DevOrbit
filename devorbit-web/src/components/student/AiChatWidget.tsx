import { useState, useEffect, useRef } from 'react'
import { useLocation } from 'react-router-dom'
import { m as motion, AnimatePresence } from 'framer-motion'
import { X, ChatTeardropText } from '@phosphor-icons/react'
import { useChat } from './ChatContext'
import { ChatPanel } from './ChatPanel'

// Re-export types and sub-components for backward compatibility
export type { AiChatMessage, AiChatStatusEvent } from './ChatContext'

export { ChatMessage } from './ChatMessage'

// ─── AiChatWidget (floating) ───

export function AiChatWidget() {
  const location = useLocation()
  const { setIsOpen } = useChat()
  const [localOpen, setLocalOpen] = useState(false)

  const isAiTutorPage = location.pathname === '/ai-tutor'

  // When leaving /ai-tutor, auto-open the widget
  const prevPathRef = useRef(location.pathname)
  useEffect(() => {
    if (prevPathRef.current === '/ai-tutor' && location.pathname !== '/ai-tutor') {
      setLocalOpen(true)
      setIsOpen(true)
    }
    prevPathRef.current = location.pathname
  }, [location, setIsOpen])

  // Hide widget on /ai-tutor
  if (isAiTutorPage) return null

  return (
    <div className="fixed bottom-6 right-6 z-50 flex flex-col items-end">
      <AnimatePresence>
        {localOpen && (
          <motion.div
            initial={{ opacity: 0, scale: 0.95, y: 15 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: 15 }}
            transition={{ type: 'spring', stiffness: 350, damping: 30 }}
            data-lenis-prevent
            className="w-[90vw] sm:w-[420px] h-[600px] max-h-[80vh] mb-4 flex flex-col rounded-[2rem] border border-zinc-800/50 bg-zinc-950/80 backdrop-blur-xl shadow-[0_20px_50px_rgba(0,0,0,0.5)] overflow-hidden"
            role="dialog"
            aria-label="DevOrbit AI Chat"
            aria-modal="true"
          >
            <ChatPanel onClose={() => setLocalOpen(false)} />
          </motion.div>
        )}
      </AnimatePresence>

      {/* ─── FAB Toggle ─── */}
      <motion.button
        onClick={() => setLocalOpen(!localOpen)}
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        aria-label={localOpen ? 'Đóng chat' : 'Mở chat AI'}
        className={`h-14 w-14 rounded-full shadow-2xl flex items-center justify-center border transition-all duration-300 ${
          localOpen
            ? 'bg-zinc-900 border-zinc-800 text-zinc-100 hover:bg-zinc-800'
            : 'bg-orbit-accent border-orbit-accent/20 text-zinc-950 hover:shadow-[0_0_20px_rgba(52,211,153,0.4)]'
        }`}
      >
        {localOpen ? (
          <X className="h-6 w-6" weight="bold" aria-hidden="true" />
        ) : (
          <ChatTeardropText className="h-6 w-6" weight="fill" aria-hidden="true" />
        )}
      </motion.button>
    </div>
  )
}
