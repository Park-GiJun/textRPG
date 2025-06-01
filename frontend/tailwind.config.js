/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{html,js,svelte,ts}'],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eff6ff',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8'
        },
        rpg: {
          gold: '#ffd700',
          bronze: '#cd7f32',
          dark: '#1a1a1a',
          darker: '#0f0f0f'
        }
      },
      fontFamily: {
        'fantasy': ['Cinzel', 'serif'],
        'game': ['Orbitron', 'monospace']
      }
    },
  },
  plugins: [],
}
