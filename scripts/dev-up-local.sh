#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PID_FILE="${ROOT_DIR}/.dev-pids"

if [[ ! -f "${ROOT_DIR}/.env" ]]; then
  echo "Missing .env in repo root. Copy .env.example to .env first." >&2
  exit 1
fi

set -a
source "${ROOT_DIR}/.env"
set +a

cleanup() {
  if [[ -n "${BACKEND_PID:-}" ]]; then
    kill "${BACKEND_PID}" 2>/dev/null || true
  fi
  if [[ -n "${PROXY_PID:-}" ]]; then
    kill "${PROXY_PID}" 2>/dev/null || true
  fi
  rm -f "${PID_FILE}"
}

trap cleanup EXIT INT TERM

echo "Starting backend..."
("${ROOT_DIR}/backend/gradlew" -p "${ROOT_DIR}/backend" bootRun) &
BACKEND_PID=$!

echo "Starting proxy..."
("${ROOT_DIR}/proxy/gradlew" -p "${ROOT_DIR}/proxy" bootRun) &
PROXY_PID=$!

cat > "${PID_FILE}" <<EOF
BACKEND_PID=${BACKEND_PID}
PROXY_PID=${PROXY_PID}
EOF

wait
