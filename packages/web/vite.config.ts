import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tsconfigPaths from 'vite-tsconfig-paths'

export default defineConfig({
  plugins: [react(), tsconfigPaths()],
  build: {
    assetsInlineLimit: 0,
  },
  server: {
    port: 3000,
    open: true,
  },
  resolve: {
    alias: {
      '@api': '/src/api',
      '@app': '/src/app',
      '@store': '/src/store',
      '@assets': '/src/assets',
      '@features': '/src/features',
      '@hooks': '/src/hooks',
      '@pages': '/src/pages',
      '@shared': '/src/shared',
      '@types': '/src/types',
      '@utils': '/src/utils',
    },
  },
})
