import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    host: '0.0.0.0',
    proxy: {
      '/api/admin': {
        target: 'http://cs.huilintai.com:8081',
        changeOrigin: true
      },
      '/api': {
        target: 'http://cs.huilintai.com:8080',
        changeOrigin: true
      }
    }
  }
})