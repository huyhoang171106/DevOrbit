export type ThemeColor = 'emerald' | 'indigo' | 'violet' | 'amber' | 'rose' | 'cyan';

export const getCourseColor = (code: string): ThemeColor => {
  const prefix = code.substring(0, 2).toUpperCase();
  switch (prefix) {
    case 'IT': return 'emerald';
    case 'CS': return 'indigo';
    case 'SE': return 'violet';
    case 'IS': return 'amber';
    case 'NT': return 'rose';
    case 'MA': return 'cyan';
    default: return 'emerald';
  }
};

// Map of colors to their specific Tailwind class strings to prevent purging
export const colorMap = {
  emerald: {
    text: 'text-emerald-500',
    textLight: 'text-emerald-400',
    bg: 'bg-emerald-500',
    bgLight: 'bg-emerald-500/5',
    bgHover: 'group-hover:bg-emerald-500/10',
    border: 'border-emerald-500/20',
    borderHover: 'group-hover:border-emerald-500/40',
    shadow: 'shadow-emerald-500/10',
    ring: 'focus:ring-emerald-500/5',
  },
  indigo: {
    text: 'text-indigo-500',
    textLight: 'text-indigo-400',
    bg: 'bg-indigo-500',
    bgLight: 'bg-indigo-500/5',
    bgHover: 'group-hover:bg-indigo-500/10',
    border: 'border-indigo-500/20',
    borderHover: 'group-hover:border-indigo-500/40',
    shadow: 'shadow-indigo-500/10',
    ring: 'focus:ring-indigo-500/5',
  },
  violet: {
    text: 'text-violet-500',
    textLight: 'text-violet-400',
    bg: 'bg-violet-500',
    bgLight: 'bg-violet-500/5',
    bgHover: 'group-hover:bg-violet-500/10',
    border: 'border-violet-500/20',
    borderHover: 'group-hover:border-violet-500/40',
    shadow: 'shadow-violet-500/10',
    ring: 'focus:ring-violet-500/5',
  },
  amber: {
    text: 'text-amber-500',
    textLight: 'text-amber-400',
    bg: 'bg-amber-500',
    bgLight: 'bg-amber-500/5',
    bgHover: 'group-hover:bg-amber-500/10',
    border: 'border-amber-500/20',
    borderHover: 'group-hover:border-amber-500/40',
    shadow: 'shadow-amber-500/10',
    ring: 'focus:ring-amber-500/5',
  },
  rose: {
    text: 'text-rose-500',
    textLight: 'text-rose-400',
    bg: 'bg-rose-500',
    bgLight: 'bg-rose-500/5',
    bgHover: 'group-hover:bg-rose-500/10',
    border: 'border-rose-500/20',
    borderHover: 'group-hover:border-rose-500/40',
    shadow: 'shadow-rose-500/10',
    ring: 'focus:ring-rose-500/5',
  },
  cyan: {
    text: 'text-cyan-500',
    textLight: 'text-cyan-400',
    bg: 'bg-cyan-500',
    bgLight: 'bg-cyan-500/5',
    bgHover: 'group-hover:bg-cyan-500/10',
    border: 'border-cyan-500/20',
    borderHover: 'group-hover:border-cyan-500/40',
    shadow: 'shadow-cyan-500/10',
    ring: 'focus:ring-cyan-500/5',
  },
};
