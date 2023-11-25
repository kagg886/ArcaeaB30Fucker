import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue({
      script: {
        defineModel: true
      }
    })],
    base: './',
    build: {
        outDir: "../app/src/main/assets/",
    },
    server: {
        host: '0.0.0.0'
    }
})
