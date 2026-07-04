#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/demo-common.sh"

echo "=== CENARIO ADMIN ==="

echo "[A1] Login admin"
authenticate_admin

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

echo "[A5] Criar produto 2 (ADMIN)"
PRODUCT_RESPONSE_2=$(curl -s -X POST "${BASE_URL}/products" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d "{\"name\":\"Suporte de Cabos ${UNIQ}\",\"sku\":\"SUP-CAB-${UNIQ}\",\"description\":\"Organizador de cabos\",\"defaultWeightGrams\":80,\"defaultPrintMinutes\":110}")
print_response "$PRODUCT_RESPONSE_2"
PRODUCT_ID_2=$(echo "$PRODUCT_RESPONSE_2" | jq -r '.id')

echo "[A6] Criar produto 3 (ADMIN)"
PRODUCT_RESPONSE_3=$(curl -s -X POST "${BASE_URL}/products" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d "{\"name\":\"Caixa Organizadora ${UNIQ}\",\"sku\":\"CX-ORG-${UNIQ}\",\"description\":\"Caixa para escritorio\",\"defaultWeightGrams\":150,\"defaultPrintMinutes\":300}")
print_response "$PRODUCT_RESPONSE_3"
PRODUCT_ID_3=$(echo "$PRODUCT_RESPONSE_3" | jq -r '.id')

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

echo "[A11] Admin deleta categorias criadas para limpeza"
ADMIN_DELETE_STATUS_1=$(curl -s -o /tmp/admin_demo_delete_resp_1.txt -w "%{http_code}" -X DELETE "${BASE_URL}/categories/${CATEGORY_ID_1}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}")
echo "Status categoria 1: ${ADMIN_DELETE_STATUS_1}"
ADMIN_DELETE_STATUS_2=$(curl -s -o /tmp/admin_demo_delete_resp_2.txt -w "%{http_code}" -X DELETE "${BASE_URL}/categories/${CATEGORY_ID_2}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}")
echo "Status categoria 2: ${ADMIN_DELETE_STATUS_2}"

if [[ "$ADMIN_DELETE_STATUS_1" != "204" || "$ADMIN_DELETE_STATUS_2" != "204" ]]; then
  echo "Falha ao limpar categorias criadas pelo admin."
  exit 1
fi

echo ""
echo "#####################################"
echo "Demo admin concluida com sucesso."
echo "#####################################"
