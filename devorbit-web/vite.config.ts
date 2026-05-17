import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import viteCompression from 'vite-plugin-compression'

// Lazy-loaded routes mapped to their chunk labels for preload hints
const routeChunks = {
  HomePage: 'home',
  CourseListPage: 'courses',
  CourseDetailPage: 'course-detail',
  GalaxyPage: 'galaxy',
  PhotoboothPage: 'photobooth',
  AdminDashboardPage: 'admin',
}

export default defineConfig({
  plugins: [
    react(),
    viteCompression({
      algorithm: 'brotliCompress',
      ext: '.br',
      threshold: 1024,
    }),
    viteCompression({
      algorithm: 'gzip',
      ext: '.gz',
      threshold: 1024,
    }),
  ],
  envPrefix: ['VITE_', 'NEXT_PUBLIC_'],
  server: {
    port: 5173,
    allowedHosts: ['provide-significance-consumer-guys.trycloudflare.com'],
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
    cssMinify: 'lightningcss',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks(id) {
          // Framework core
          if (id.includes('node_modules/react-dom') || id.includes('node_modules/react/')) {
            return 'vendor-react'
          }
          // Animation libraries
          if (id.includes('node_modules/framer-motion') || id.includes('node_modules/gsap') || id.includes('node_modules/@gsap')) {
            return 'vendor-anim'
          }
          // 3D / Three.js
          if (id.includes('node_modules/three') || id.includes('node_modules/@react-three')) {
            return 'vendor-3d'
          }
          // UI component libs
          if (id.includes('node_modules/@dnd-kit') || id.includes('node_modules/@phosphor-icons') || id.includes('node_modules/lucide-react')) {
            return 'vendor-ui'
          }
          // AI / data
          if (id.includes('node_modules/@ai-sdk') || id.includes('node_modules/ai') || id.includes('node_modules/@supabase')) {
            return 'vendor-data'
          }
          // Routing / state
          if (id.includes('node_modules/react-router-dom') || id.includes('node_modules/zustand') || id.includes('node_modules/@tanstack')) {
            return 'vendor-core'
          }
        },
      },
    },
  },
  optimizeDeps: {
    include: ['react', 'react-dom', 'react-router-dom', 'framer-motion', '@phosphor-icons/react'],
  },
})