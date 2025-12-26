#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PID_FILE="${ROOT_DIR}/.dev-pids"

if [[ ! -f "${PID_FILE}" ]]; then
  echo "No running PIDs found at ${PID_FILE}." >&2
  exit 0
fi

source "${PID_FILE}"

if [[ -n "${BACKEND_PID:-}" ]]; then
  kill "${BACKEND_PID}" 2>/dev/null || true
fi

if [[ -n "${PROXY_PID:-}" ]]; then
  kill "${PROXY_PID}" 2>/dev/null || true
fi

rm -f "${PID_FILE}"
echo "Stopped backend and proxy."
