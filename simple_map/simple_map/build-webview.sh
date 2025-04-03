#!/bin/bash

echo "📦 Building Vite project..."
npm run build

HTML_FILE="dist/index.html"

if [ -f "$HTML_FILE" ]; then
  echo "🔧 Patching index.html for Android WebView..."

  # Удаляем type="module" и crossorigin
  sed -i 's/type="module"//g' "$HTML_FILE"
  sed -i 's/crossorigin//g' "$HTML_FILE"

  echo "✅ index.html patched successfully."
else
  echo "❌ Error: dist/index.html not found!"
fi
