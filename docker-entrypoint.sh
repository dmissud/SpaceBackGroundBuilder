#!/bin/sh
set -e

BASE_HREF="${BASE_HREF:-/sbgb/}"
API_URL="${API_URL:-/sbgb/api}"

JS_DIR="/usr/share/nginx/html/sbgb"

echo "Starting SBGB frontend: BASE_HREF=${BASE_HREF} API_URL=${API_URL}"

# Replace API URL placeholder in compiled JS
find "${JS_DIR}" -name "*.js" -exec sed -i "s|__API_URL__|${API_URL}|g" {} +

# Replace base href in index.html
sed -i "s|<base href=\"/sbgb/\">|<base href=\"${BASE_HREF}\">|g" "${JS_DIR}/index.html"

exec nginx -g "daemon off;"
