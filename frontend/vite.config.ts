import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  build: {
    target: 'esnext',
    minify: 'esbuild',
    cssMinify: true,
    reportCompressedSize: true,
    chunkSizeWarningLimit: 500,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom'],
          chess: ['chess.js', 'react-chessboard'],
          websocket: ['@stomp/stompjs', 'sockjs-client'],
        },
      },
    },
  },
  server: {
    port: 5173,
  },
});
