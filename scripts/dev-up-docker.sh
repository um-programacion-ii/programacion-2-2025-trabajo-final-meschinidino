#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

if [[ ! -f "${ROOT_DIR}/.env" ]]; then
  echo "Missing .env in repo root. Copy .env.example to .env first." >&2
  exit 1
fi

docker compose --project-directory "${ROOT_DIR}" up --build
