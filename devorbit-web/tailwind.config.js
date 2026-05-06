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
