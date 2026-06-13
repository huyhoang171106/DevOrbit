import { m as motion } from 'framer-motion'
import { Sparkle } from '@phosphor-icons/react'

export function AiTutorPage() {
  return (
    <div className="w-full min-h-screen flex items-center justify-center">
      <div className="max-w-md mx-auto px-6 text-center">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <div className="h-16 w-16 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center mx-auto mb-6">
            <Sparkle className="h-8 w-8 text-orbit-accent" weight="duotone" />
          </div>
          <h1 className="text-2xl font-bold text-orbit-text mb-3">AI Tutor</h1>
          <p className="text-orbit-muted">Trợ lý học tập thông minh</p>
        </motion.div>
      </div>
    </div>
  )
}
