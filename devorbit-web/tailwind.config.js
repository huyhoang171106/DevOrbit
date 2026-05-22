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
        'glow-pulse': {
          '0%, 100%': { boxShadow: '0 0 20px rgba(52, 211, 153, 0.1), 0 0 40px rgba(52, 211, 153, 0.05)' },
          '50%': { boxShadow: '0 0 30px rgba(52, 211, 153, 0.2), 0 0 60px rgba(52, 211, 153, 0.1)' },
        },
        'aurora': {
          '0%': { transform: 'translateX(-10%) translateY(-10%) scale(1)' },
          '25%': { transform: 'translateX(5%) translateY(-5%) scale(1.05)' },
          '50%': { transform: 'translateX(10%) translateY(5%) scale(0.95)' },
          '75%': { transform: 'translateX(0%) translateY(10%) scale(1.02)' },
          '100%': { transform: 'translateX(-10%) translateY(-10%) scale(1)' },
        },
        'lens-blur': {
          '0%': { backdropFilter: 'blur(0px)', WebkitBackdropFilter: 'blur(0px)' },
          '100%': { backdropFilter: 'blur(12px)', WebkitBackdropFilter: 'blur(12px)' },
        },
      },
      animation: {
        'shimmer': 'shimmer 3s ease-in-out infinite',
        'float': 'float 4s ease-in-out infinite',
        'pulse-soft': 'pulse-soft 3s ease-in-out infinite',
        'breathing': 'breathing 3s ease-in-out infinite',
        'glow-pulse': 'glow-pulse 4s ease-in-out infinite',
        'aurora': 'aurora 20s ease-in-out infinite',
        'lens-blur': 'lens-blur 0.8s ease-out forwards',
      },
      transitionTimingFunction: {
        'cinematic': 'cubic-bezier(0.25, 0.1, 0.25, 1)',
        'spring': 'cubic-bezier(0.34, 1.56, 0.64, 1)',
      },
      backgroundImage: {
        'orbit-gradient': 'linear-gradient(135deg, rgba(52, 211, 153, 0.15), rgba(129, 140, 248, 0.15))',
        'orbit-glow': 'radial-gradient(circle at 50% 50%, rgba(52, 211, 153, 0.1), transparent 70%)',
      },
    },
  },
  plugins: [],
}
