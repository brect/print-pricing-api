#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080/api}"
ADMIN_EMAIL="${ADMIN_EMAIL:-admin@authserver.com}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-admin}"
USER_PASSWORD="${USER_PASSWORD:-Senha@123}"

if ! command -v jq >/dev/null 2>&1; then
  echo "Erro: jq nao encontrado. Instale com: brew install jq"
  exit 1
fi

login() {
  local email="$1"
  local password="$2"
  curl -s -X POST "${BASE_URL}/users/login" \
    -H 'Content-Type: application/json' \
    -d "{\"email\":\"${email}\",\"password\":\"${password}\"}"
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

echo "=== CENARIO ADMIN ==="

echo "[A1] Login admin"
ADMIN_LOGIN=$(login "$ADMIN_EMAIL" "$ADMIN_PASSWORD")
print_response "$ADMIN_LOGIN"
ADMIN_TOKEN=$(extract_token "$ADMIN_LOGIN")
if [[ -z "$ADMIN_TOKEN" || "$ADMIN_TOKEN" == "null" ]]; then
  echo "Falha no login admin:"
  echo "$ADMIN_LOGIN"
  exit 1
fi

UNIQ="$(date +%s)"

echo "[A2] Criar categoria 1 (ADMIN)"
CATEGORY_RESPONSE_1=$(curl -s -X POST "${BASE_URL}/categories" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d "{\"name\":\"Decoracao-${UNIQ}\",\"description\":\"Produtos decorativos\"}")
print_response "$CATEGORY_RESPONSE_1"
CATEGORY_ID_1=$(echo "$CATEGORY_RESPONSE_1" | jq -r '.id')
if [[ -z "$CATEGORY_ID_1" || "$CATEGORY_ID_1" == "null" ]]; then
  echo "Falha ao criar categoria 1:"
  echo "$CATEGORY_RESPONSE_1"
  exit 1
fi

echo "[A3] Criar categoria 2 (ADMIN)"
CATEGORY_RESPONSE_2=$(curl -s -X POST "${BASE_URL}/categories" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d "{\"name\":\"Utilitarios-${UNIQ}\",\"description\":\"Produtos utilitarios\"}")
print_response "$CATEGORY_RESPONSE_2"
CATEGORY_ID_2=$(echo "$CATEGORY_RESPONSE_2" | jq -r '.id')
if [[ -z "$CATEGORY_ID_2" || "$CATEGORY_ID_2" == "null" ]]; then
  echo "Falha ao criar categoria 2:"
  echo "$CATEGORY_RESPONSE_2"
  exit 1
fi

echo "[A4] Criar produto 1 (ADMIN)"
PRODUCT_RESPONSE_1=$(curl -s -X POST "${BASE_URL}/products" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d "{\"name\":\"Vaso Geometrico ${UNIQ}\",\"sku\":\"VASO-GEO-${UNIQ}\",\"description\":\"Vaso decorativo\",\"defaultWeightGrams\":120,\"defaultPrintMinutes\":240}")
print_response "$PRODUCT_RESPONSE_1"
PRODUCT_ID_1=$(echo "$PRODUCT_RESPONSE_1" | jq -r '.id')
if [[ -z "$PRODUCT_ID_1" || "$PRODUCT_ID_1" == "null" ]]; then
  echo "Falha ao criar produto 1:"
  echo "$PRODUCT_RESPONSE_1"
  exit 1
fi

echo "[A5] Criar produto 2 (ADMIN)"
PRODUCT_RESPONSE_2=$(curl -s -X POST "${BASE_URL}/products" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d "{\"name\":\"Suporte de Cabos ${UNIQ}\",\"sku\":\"SUP-CAB-${UNIQ}\",\"description\":\"Organizador de cabos\",\"defaultWeightGrams\":80,\"defaultPrintMinutes\":110}")
print_response "$PRODUCT_RESPONSE_2"
PRODUCT_ID_2=$(echo "$PRODUCT_RESPONSE_2" | jq -r '.id')
if [[ -z "$PRODUCT_ID_2" || "$PRODUCT_ID_2" == "null" ]]; then
  echo "Falha ao criar produto 2:"
  echo "$PRODUCT_RESPONSE_2"
  exit 1
fi

echo "[A6] Criar produto 3 (ADMIN)"
PRODUCT_RESPONSE_3=$(curl -s -X POST "${BASE_URL}/products" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d "{\"name\":\"Caixa Organizadora ${UNIQ}\",\"sku\":\"CX-ORG-${UNIQ}\",\"description\":\"Caixa para escritorio\",\"defaultWeightGrams\":150,\"defaultPrintMinutes\":300}")
print_response "$PRODUCT_RESPONSE_3"
PRODUCT_ID_3=$(echo "$PRODUCT_RESPONSE_3" | jq -r '.id')
if [[ -z "$PRODUCT_ID_3" || "$PRODUCT_ID_3" == "null" ]]; then
  echo "Falha ao criar produto 3:"
  echo "$PRODUCT_RESPONSE_3"
  exit 1
fi

echo "[A7] Associar produtos-categorias (ADMIN)"
A7_1=$(curl -s -X PUT "${BASE_URL}/products/${PRODUCT_ID_1}/categories/${CATEGORY_ID_1}" -H "Authorization: Bearer ${ADMIN_TOKEN}")
A7_2=$(curl -s -X PUT "${BASE_URL}/products/${PRODUCT_ID_2}/categories/${CATEGORY_ID_2}" -H "Authorization: Bearer ${ADMIN_TOKEN}")
A7_3=$(curl -s -X PUT "${BASE_URL}/products/${PRODUCT_ID_3}/categories/${CATEGORY_ID_1}" -H "Authorization: Bearer ${ADMIN_TOKEN}")
print_response "$A7_1"
print_response "$A7_2"
print_response "$A7_3"

echo "[A8] Listagem sem filtro (ADMIN)"
A8_RESPONSE=$(curl -s -X GET "${BASE_URL}/products" -H "Authorization: Bearer ${ADMIN_TOKEN}")
print_response "$A8_RESPONSE"

echo "[A9] Listagem somente ordenada por nome DESC (ADMIN)"
A9_RESPONSE=$(curl -s -X GET "${BASE_URL}/products?sortBy=name&direction=DESC" -H "Authorization: Bearer ${ADMIN_TOKEN}")
print_response "$A9_RESPONSE"

echo "[A10] Listagem somente por categoria (ADMIN)"
A10_RESPONSE=$(curl -s -X GET "${BASE_URL}/products?categoryId=${CATEGORY_ID_1}" -H "Authorization: Bearer ${ADMIN_TOKEN}")
print_response "$A10_RESPONSE"

echo "=== CENARIO USER ==="

USER_EMAIL="user${UNIQ}@example.com"

echo "[U1] Criar usuario (publico)"
USER_CREATE=$(curl -s -X POST "${BASE_URL}/users" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"${USER_EMAIL}\",\"password\":\"${USER_PASSWORD}\",\"name\":\"Usuario Demo\"}")
print_response "$USER_CREATE"

if [[ "$(echo "$USER_CREATE" | jq -r '.id // empty')" == "" ]]; then
  echo "Falha ao criar usuario:"
  echo "$USER_CREATE"
  exit 1
fi

echo "[U2] Login usuario"
USER_LOGIN=$(login "$USER_EMAIL" "$USER_PASSWORD")
print_response "$USER_LOGIN"
USER_TOKEN=$(extract_token "$USER_LOGIN")
if [[ -z "$USER_TOKEN" || "$USER_TOKEN" == "null" ]]; then
  echo "Falha no login user:"
  echo "$USER_LOGIN"
  exit 1
fi

echo "[U3] Usuario tenta deletar categoria (deve falhar com 403)"
USER_DELETE_STATUS=$(curl -s -o /tmp/user_delete_resp.txt -w "%{http_code}" -X DELETE "${BASE_URL}/categories/${CATEGORY_ID_1}" \
  -H "Authorization: Bearer ${USER_TOKEN}")

echo "Status recebido: ${USER_DELETE_STATUS}"
print_response "$(cat /tmp/user_delete_resp.txt)"
if [[ "$USER_DELETE_STATUS" != "403" ]]; then
  echo "Esperava 403 para USER em rota destrutiva de categoria. Resposta:"
  cat /tmp/user_delete_resp.txt
  exit 1
fi

echo "[U4] Admin deleta categoria 1 (deve sucesso 204)"
ADMIN_DELETE_STATUS_1=$(curl -s -o /tmp/admin_delete_resp_1.txt -w "%{http_code}" -X DELETE "${BASE_URL}/categories/${CATEGORY_ID_1}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}")

echo "Status recebido: ${ADMIN_DELETE_STATUS_1}"
if [[ -s /tmp/admin_delete_resp_1.txt ]]; then
  print_response "$(cat /tmp/admin_delete_resp_1.txt)"
else
  echo "{}"
fi
if [[ "$ADMIN_DELETE_STATUS_1" != "204" ]]; then
  echo "Falha ao deletar categoria 1 com ADMIN. Resposta:"
  cat /tmp/admin_delete_resp_1.txt
  exit 1
fi

echo "[U5] Admin deleta categoria 2 (deve sucesso 204)"
ADMIN_DELETE_STATUS_2=$(curl -s -o /tmp/admin_delete_resp_2.txt -w "%{http_code}" -X DELETE "${BASE_URL}/categories/${CATEGORY_ID_2}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}")

echo "Status recebido: ${ADMIN_DELETE_STATUS_2}"
if [[ -s /tmp/admin_delete_resp_2.txt ]]; then
  print_response "$(cat /tmp/admin_delete_resp_2.txt)"
else
  echo "{}"
fi
if [[ "$ADMIN_DELETE_STATUS_2" != "204" ]]; then
  echo "Falha ao deletar categoria 2 com ADMIN. Resposta:"
  cat /tmp/admin_delete_resp_2.txt
  exit 1
fi

echo ""
echo "#####################################"
echo "Roteiro concluido com sucesso."
echo "#####################################"
echo ""
echo "Resumo:"
echo "- CATEGORY_1_ID=${CATEGORY_ID_1}"
echo "- CATEGORY_2_ID=${CATEGORY_ID_2}"
echo "- PRODUCT_1_ID=${PRODUCT_ID_1}"
echo "- PRODUCT_2_ID=${PRODUCT_ID_2}"
echo "- PRODUCT_3_ID=${PRODUCT_ID_3}"
echo "- USER_EMAIL=${USER_EMAIL}"
