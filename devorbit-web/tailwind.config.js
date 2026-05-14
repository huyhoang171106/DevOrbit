/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        heading: ['"Be Vietnam Pro"', 'Inter', 'sans-serif'],
        body: ['Inter', 'sans-serif'],
        mono: ['"IBM Plex Mono"', 'monospace'],
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
      },
    },
  },
  plugins: [],
}
