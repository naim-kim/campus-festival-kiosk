#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-$HOME/demo-deploy-app}"
cd "$APP_DIR"

if [[ ! -f ./app.pid ]]; then
  echo "No app.pid found in $APP_DIR"
  exit 0
fi

PID="$(cat ./app.pid)"
if kill -0 "$PID" 2>/dev/null; then
  kill "$PID"
  echo "Stopped pid=$PID"
else
  echo "Not running (pid=$PID)"
fi

rm -f ./app.pid

