/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // Primary brand color (투자앱 메인 컬러)
        primary: {
          DEFAULT: '#6A5ACD',
          50: '#F5F3FF',
          100: '#EDE9FE',
          200: '#DDD6FE',
          300: '#C4B5FD',
          400: '#A59CE1',
          500: '#6A5ACD',
          600: '#5B4BBE',
          700: '#4C3CAF',
          800: '#3F367B',
          900: '#2E2A5E',
        },
        // 성공/상승 색상
        success: {
          DEFAULT: '#3E82E4',
          50: '#EFF6FF',
          100: '#DBEAFE',
          500: '#3E82E4',
          600: '#2563EB',
          700: '#1D4ED8',
        },
        // 실패/하락 색상
        danger: {
          DEFAULT: '#FF5C5C',
          50: '#FEF2F2',
          100: '#FEE2E2',
          500: '#FF5C5C',
          600: '#EF4444',
          700: '#DC2626',
        },
        // Light mode colors
        light: {
          bg: {
            primary: '#FAFAFA',
            secondary: '#FFFFFF',
          },
          text: {
            primary: '#17171C',
            secondary: '#6E6E73',
          },
          border: '#EAEAEA',
        },
        // Dark mode colors
        dark: {
          bg: {
            primary: '#17171C',
            secondary: '#2C2C34',
          },
          text: {
            primary: '#FAFAFA',
            secondary: '#A1A1A6',
          },
          border: '#3A3A3F',
        },
      },
      fontFamily: {
        sans: ['Pretendard', '-apple-system', 'BlinkMacSystemFont', 'system-ui', 'Roboto', 'sans-serif'],
      },
      fontSize: {
        'overline': ['12px', { lineHeight: '1.4' }],
        'caption': ['14px', { lineHeight: '1.5' }],
        'body': ['16px', { lineHeight: '1.6' }],
        'subtitle': ['16px', { lineHeight: '1.5', fontWeight: '500' }],
        'title': ['20px', { lineHeight: '1.4', fontWeight: '600' }],
        'headline': ['24px', { lineHeight: '1.3', fontWeight: '700' }],
      },
      animation: {
        'fade-in': 'fadeIn 0.2s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
        'slide-down': 'slideDown 0.3s ease-out',
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        slideDown: {
          '0%': { transform: 'translateY(-10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
    },
  },
  plugins: [],
}
