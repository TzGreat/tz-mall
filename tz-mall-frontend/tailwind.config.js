/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'gold-primary': '#D4AF37',
        'gold-light': '#E8D5B7',
        'dark': '#1a1a1a',
        'light-gray': '#f5f5f5',
      },
      fontFamily: {
        'display': ['Playfair Display', 'serif'],
        'body': ['Lato', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
