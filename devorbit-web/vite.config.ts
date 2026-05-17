import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  envPrefix: ["VITE_", "NEXT_PUBLIC_"],
  server: {
    port: 5173,
    allowedHosts: ["provide-significance-consumer-guys.trycloudflare.com"],
    watch: { usePolling: false },
    fs: { cached: true },
    proxy: {
      '/api': {
        target: 'http://104.214.179.110:8080',
        changeOrigin: true,
      }
    }
  },
  build: {
    target: 'esnext',
    modulePreload: { polyfill: false },
  },
  optimizeDeps: {
    include: ['react', 'react-dom', 'react-router-dom', 'framer-motion', '@phosphor-icons/react'],
  },
})
