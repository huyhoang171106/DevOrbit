/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
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
          base: 'var(--color-cosmic-base)',
          surface: 'var(--color-cosmic-surface)',
          elevated: 'var(--color-cosmic-elevated)',
          border: 'var(--color-cosmic-border)',
          'border-hover': 'var(--color-cosmic-border-hover)',
        },
        emerald: {
          50: '#ecfdf5',
          200: '#a7f3d0',
          400: '#34d399',
          500: '#10b981',
          600: '#059669',
        },
        indigo: {
          400: '#818cf8',
          500: '#6366f1',
          600: '#4f46e5',
        },
        violet: {
          400: '#a78bfa',
          500: '#8b5cf6',
          600: '#7c3aed',
        },
        amber: {
          400: '#fbbf24',
          500: '#f59e0b',
          600: '#d97706',
        },
        rose: {
          400: '#fb7185',
          500: '#f43f5e',
          600: '#e11d48',
        },
        cyan: {
          400: '#22d3ee',
          500: '#06b6d4',
          600: '#0891b2',
        },
        ink: {
          DEFAULT: 'var(--color-ink)',
          secondary: 'var(--color-ink-secondary)',
          muted: 'var(--color-ink-muted)',
        },
        glass: {
          border: 'var(--color-glass-border)',
          'border-hover': 'var(--color-glass-border-hover)',
          surface: 'var(--color-glass-surface)',
          'surface-hover': 'var(--color-glass-surface-hover)',
          'surface-raised': 'var(--color-glass-surface-raised)',
        },
      },
    },
  },
  plugins: [],
}
