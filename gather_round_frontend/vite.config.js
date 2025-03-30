import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteSingleFile } from 'vite-plugin-singlefile'

export default defineConfig({
  base: './',
  plugins: [vue(), viteSingleFile()],
  build: {
    assetsInlineLimit: 100000000,
    cssCodeSplit: false,
    outDir: 'dist'
  }
})
