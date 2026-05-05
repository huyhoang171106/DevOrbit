/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        heading: ['Sora', 'sans-serif'],
        body: ['DM Sans', 'sans-serif'],
        mono: ['JetBrains Mono', 'monospace'],
      },
      colors: {
        cosmic: {
          base: '#070b15',
          surface: '#0d1225',
          elevated: '#141b33',
          border: '#1e2848',
          'border-hover': '#2a3a66',
        },
        emerald: {
          50: '#ecfdf5',
          200: '#a7f3d0',
          400: '#34d399',
          500: '#10b981',
          600: '#059669',
        },
        ink: {
          DEFAULT: '#e2e8f0',
          secondary: '#94a3b8',
          muted: '#64748b',
        },
        glass: {
          border: 'rgba(255,255,255,0.08)',
          'border-hover': 'rgba(16,185,129,0.3)',
          surface: 'rgba(255,255,255,0.04)',
          'surface-hover': 'rgba(255,255,255,0.07)',
          'surface-raised': 'rgba(255,255,255,0.03)',
        },
      },
    },
  },
  plugins: [],
}
