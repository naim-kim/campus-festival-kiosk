#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-$HOME/demo-deploy-app}"
JAR_NAME="${JAR_NAME:-demo-deploy-backend-0.0.1.jar}"
PORT="${PORT:-8080}"

mkdir -p "$APP_DIR"
cd "$APP_DIR"

if [[ ! -f "./$JAR_NAME" ]]; then
  echo "Missing ./$JAR_NAME in $APP_DIR"
  echo "Copy it here first (scp)."
  exit 1
fi

if [[ -f ./app.pid ]]; then
  if kill -0 "$(cat ./app.pid)" 2>/dev/null; then
    echo "Already running (pid $(cat ./app.pid)). Stop first or remove app.pid."
    exit 1
  fi
fi

nohup env PORT="$PORT" java -jar "./$JAR_NAME" > app.log 2>&1 & echo $! > app.pid
echo "Started. Port=$PORT  pid=$(cat ./app.pid)"
echo "Logs: $APP_DIR/app.log"

