/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        heading: ['"Be Vietnam Pro"', 'Inter', 'sans-serif'],
        body: ['Inter', 'sans-serif'],
        mono: ['Geist Mono', 'monospace'],
      },
      colors: {
        ink: {
          DEFAULT: 'var(--color-ink)',
          secondary: 'var(--color-ink-secondary)',
          muted: 'var(--color-ink-muted)',
        },
        clay: {
          bg: 'var(--color-clay-bg)',
          surface: 'var(--color-clay-surface)',
          primary: 'var(--color-clay-primary)',
          secondary: 'var(--color-clay-secondary)',
          accent: 'var(--color-clay-accent)',
          text: 'var(--color-clay-text)',
          'text-muted': 'var(--color-clay-text-muted)',
          border: 'var(--color-clay-border)',
        },
        glass: {
          surface: 'var(--color-glass-surface)',
          'surface-raised': 'var(--color-glass-surface-raised)',
          'surface-hover': 'var(--color-glass-surface-hover)',
          border: 'var(--color-glass-border)',
        },
        cosmic: {
          surface: 'var(--color-cosmic-surface)',
          elevated: 'var(--color-cosmic-elevated)',
        },
      },
    },
  },
  plugins: [],
}
