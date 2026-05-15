#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

# ─── Load .env ──────────────────────────────────────────
if [ ! -f ".env" ]; then
    echo "[ERR]  File .env not found!" >&2
    exit 1
fi
set -a; source .env; set +a

log()  { echo "[$(date '+%H:%M:%S')]  $*"; }
die()  { log "FATAL: $*"; exit 1; }

# ─── Validate ───────────────────────────────────────────
[ -z "$GITHUB_TOKEN" ] && die "GITHUB_TOKEN is empty — set it in .env"

# ─── 1. API ─────────────────────────────────────────────
log "Building API…"
cd devorbit-api
chmod +x mvnw
./mvnw package -q -DskipTests -Dmaven.test.skip=true || die "API build failed"

log "Starting API on :${SERVER_PORT:-8080}…"
JAR=$(ls target/*.jar 2>/dev/null | head -1)
[ -z "$JAR" ] && die "No JAR found in target/"
nohup java -jar "$JAR" > ../api.log 2>&1 &
API_PID=$!
echo "$API_PID" > ../api.pid
log "API started (PID $API_PID)  →  api.log"

cd "$ROOT_DIR"

# ─── 2. Web ─────────────────────────────────────────────
log "Building Web…"
cd devorbit-web
npm install --silent
npm run build --silent || die "Web build failed"

log "Starting Web on :${WEB_PORT:-3000}…"
nohup npx serve dist -l "${WEB_PORT:-3000}" --no-clipboard > ../web.log 2>&1 &
WEB_PID=$!
echo "$WEB_PID" > ../web.pid
log "Web started (PID $WEB_PID)  →  web.log"

cd "$ROOT_DIR"

# ─── Done ───────────────────────────────────────────────
log "──────────────────────────────────────────────"
log "  API  →  http://localhost:${SERVER_PORT:-8080}"
log "  Web  →  http://localhost:${WEB_PORT:-3000}"
log "  Logs →  tail -f api.log | web.log"
log "  Stop →  kill \$(cat api.pid) \$(cat web.pid)"
log "──────────────────────────────────────────────"
