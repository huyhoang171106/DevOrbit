/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        heading: ['"Baloo 2"', 'system-ui', 'sans-serif'],
        body: ['Inter', 'system-ui', 'sans-serif'],
        mono: ['"Geist Mono"', 'monospace'],
      },
      colors: {
        orbit: {
          bg: '#09090b',
          surface: '#18181b',
          elevated: '#27272a',
          border: 'rgba(39, 39, 42, 0.8)',
          accent: '#34d399',
          'accent-subtle': 'rgba(52, 211, 153, 0.1)',
          text: '#fafafa',
          'text-secondary': '#a1a1aa',
          'text-muted': '#52525b',
        },
        ink: {
          DEFAULT: '#fafafa',
          secondary: '#a1a1aa',
          muted: '#52525b',
        },
      },
      borderRadius: {
        '2xl': '1rem',
        '3xl': '1.5rem',
        '4xl': '2rem',
        '5xl': '2.5rem',
      },
      boxShadow: {
        'diffusion': '0 20px 40px -15px rgba(0, 0, 0, 0.5)',
        'diffusion-lg': '0 30px 60px -20px rgba(0, 0, 0, 0.6)',
        'glow': '0 0 20px rgba(52, 211, 153, 0.15)',
        'glow-lg': '0 0 40px rgba(52, 211, 153, 0.1)',
        'inner-glow': 'inset 0 1px 0 rgba(255, 255, 255, 0.05)',
      },
      keyframes: {
        'shimmer': {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
        'float': {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-8px)' },
        },
        'pulse-soft': {
          '0%, 100%': { opacity: 0.4 },
          '50%': { opacity: 0.8 },
        },
        'breathing': {
          '0%, 100%': { transform: 'scale(1)', opacity: 0.5 },
          '50%': { transform: 'scale(1.05)', opacity: 0.8 },
        },
      },
      animation: {
        'shimmer': 'shimmer 3s ease-in-out infinite',
        'float': 'float 4s ease-in-out infinite',
        'pulse-soft': 'pulse-soft 3s ease-in-out infinite',
        'breathing': 'breathing 3s ease-in-out infinite',
      },
    },
  },
  plugins: [],
}
