import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// README: React uygulamasi 3200 portunda calismali.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3200,
    strictPort: true, // port doluysa baska porta kaymasin, hata versin
  },
})
