#!/usr/bin/env bash

BASE_URL="${BASE_URL:-http://localhost:8080/api}"
ADMIN_PHONE="${ADMIN_PHONE:-+5511964100140}"
ADMIN_UUID="${ADMIN_UUID:-admin-device}"

if ! command -v jq >/dev/null 2>&1; then
  echo "Erro: jq nao encontrado. Instale com: brew install jq"
  exit 1
fi

login() {
  local phone="$1"
  local uuid="$2"
  curl -s -X POST "${BASE_URL}/users/login" \
    -H 'Content-Type: application/json' \
    -d "{\"phone\":\"${phone}\",\"uuid\":\"${uuid}\"}"
}

extract_token() {
  echo "$1" | jq -r '.token'
}

print_response() {
  local payload="${1:-}"
  if [[ -z "$payload" ]]; then
    echo "{}"
    return
  fi
  if echo "$payload" | jq . >/dev/null 2>&1; then
    echo "$payload" | jq .
  else
    echo "$payload"
  fi
}

authenticate_admin() {
  ADMIN_LOGIN=$(login "$ADMIN_PHONE" "$ADMIN_UUID")
  print_response "$ADMIN_LOGIN"

  ADMIN_TOKEN=$(extract_token "$ADMIN_LOGIN")
  if [[ -z "$ADMIN_TOKEN" || "$ADMIN_TOKEN" == "null" ]]; then
    echo "Falha no login admin:"
    echo "$ADMIN_LOGIN"
    exit 1
  fi
}
