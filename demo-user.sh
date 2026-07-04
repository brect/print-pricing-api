#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/demo-common.sh"

echo "=== CENARIO USER ==="

echo "[U0] Login admin para preparar recurso protegido"
authenticate_admin

UNIQ="$(date +%s)"
USER_EMAIL="user${UNIQ}@example.com"
USER_PHONE="${USER_PHONE:-+5511999${UNIQ: -6}}"
USER_UUID="${USER_UUID:-user-device-${UNIQ}}"
USER_REPLACEMENT_UUID="${USER_REPLACEMENT_UUID:-user-device-replaced-${UNIQ}}"
NO_CODE_PHONE="${NO_CODE_PHONE:-+5511888${UNIQ: -6}}"
NO_CODE_UUID="${NO_CODE_UUID:-no-code-device-${UNIQ}}"
echo " "
echo "[U1] Admin cria categoria para teste de autorizacao"
CATEGORY_RESPONSE=$(curl -s -X POST "${BASE_URL}/categories" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d "{\"name\":\"Auth-Test-${UNIQ}\",\"description\":\"Categoria para validar autorizacao USER\"}")
print_response "$CATEGORY_RESPONSE"
CATEGORY_ID=$(echo "$CATEGORY_RESPONSE" | jq -r '.id')
echo " "
echo "[U2] Confirmar sem codigo enviado (deve retornar 404)"
NO_CODE_CONFIRM_STATUS=$(curl -s -o /tmp/no_code_confirm_resp.txt -w "%{http_code}" -X POST "${BASE_URL}/users/confirm" \
  -H 'Content-Type: application/json' \
  -d "{\"phone\":\"${NO_CODE_PHONE}\",\"uuid\":\"${NO_CODE_UUID}\",\"code\":\"000000\"}")
echo "Dados enviados: {\"phone\":\"${NO_CODE_PHONE}\",\"uuid\":\"${NO_CODE_UUID}\"}"
echo "Status recebido: ${NO_CODE_CONFIRM_STATUS}"
print_response "$(cat /tmp/no_code_confirm_resp.txt)"
if [[ "$NO_CODE_CONFIRM_STATUS" != "404" ]]; then
  echo "Esperava 404 para confirmacao sem codigo enviado. Resposta:"
  cat /tmp/no_code_confirm_resp.txt
  exit 1
fi
echo " "
echo "[U3] Login usuario novo por telefone (deve solicitar confirmacao com 202)"
USER_LOGIN_STATUS=$(curl -s -o /tmp/user_login_resp.txt -w "%{http_code}" -X POST "${BASE_URL}/users/login" \
  -H 'Content-Type: application/json' \
  -d "{\"phone\":\"${USER_PHONE}\",\"uuid\":\"${USER_UUID}\"}")
echo "Dados enviados: {\"phone\":\"${USER_PHONE}\",\"uuid\":\"${USER_UUID}\"}"
echo "Status recebido: ${USER_LOGIN_STATUS}"
print_response "$(cat /tmp/user_login_resp.txt)"
if [[ "$USER_LOGIN_STATUS" != "202" ]]; then
  echo "Esperava 202 para usuario sem telefone/uuid confirmado. Resposta:"
  cat /tmp/user_login_resp.txt
  exit 1
fi
echo " "
echo "[U4] Confirmar usuario novo com codigo invalido (deve retornar 400)"
INVALID_CONFIRM_STATUS=$(curl -s -o /tmp/invalid_confirm_resp.txt -w "%{http_code}" -X POST "${BASE_URL}/users/confirm" \
  -H 'Content-Type: application/json' \
  -d "{\"phone\":\"${USER_PHONE}\",\"uuid\":\"${USER_UUID}\",\"code\":\"000000\"}")
echo "Status recebido: ${INVALID_CONFIRM_STATUS}"
print_response "$(cat /tmp/invalid_confirm_resp.txt)"
if [[ "$INVALID_CONFIRM_STATUS" != "400" ]]; then
  echo "Esperava 400 para codigo de confirmacao invalido. Resposta:"
  cat /tmp/invalid_confirm_resp.txt
  exit 1
fi
echo " "
echo "[U5] Confirmar usuario novo com codigo SMS correto"
if [[ -z "${USER_CONFIRMATION_CODE:-}" ]]; then
  echo "Copie do log da aplicacao o codigo da linha: SMS de confirmacao para telefone ${USER_PHONE}"
  read -r -p "Digite o codigo SMS do usuario: " USER_CONFIRMATION_CODE
fi
USER_CONFIRM_STATUS=$(curl -s -o /tmp/user_confirm_resp.txt -w "%{http_code}" -X POST "${BASE_URL}/users/confirm" \
  -H 'Content-Type: application/json' \
  -d "{\"phone\":\"${USER_PHONE}\",\"uuid\":\"${USER_UUID}\",\"code\":\"${USER_CONFIRMATION_CODE}\"}")
echo "Status recebido: ${USER_CONFIRM_STATUS}"
print_response "$(cat /tmp/user_confirm_resp.txt)"
if [[ "$USER_CONFIRM_STATUS" != "200" ]]; then
  echo "Falha ao confirmar usuario. Resposta:"
  cat /tmp/user_confirm_resp.txt
  exit 1
fi
echo " "
echo "[U6] Login usuario ativo com mesmo telefone e uuid (deve retornar token e usuario)"
USER_LOGIN=$(login "$USER_PHONE" "$USER_UUID")
print_response "$USER_LOGIN"
USER_TOKEN=$(extract_token "$USER_LOGIN")
USER_ID=$(echo "$USER_LOGIN" | jq -r '.user.id')
echo "Token recebido: ${USER_TOKEN}"
if [[ -z "$USER_TOKEN" || "$USER_TOKEN" == "null" ]]; then
  echo "Falha no login user confirmado:"
  echo "$USER_LOGIN"
  exit 1
fi
echo " "
echo "[U7] Login mesmo telefone com outro uuid (deve solicitar confirmacao com 202)"
USER_REPLACEMENT_LOGIN_STATUS=$(curl -s -o /tmp/user_replacement_login_resp.txt -w "%{http_code}" -X POST "${BASE_URL}/users/login" \
  -H 'Content-Type: application/json' \
  -d "{\"phone\":\"${USER_PHONE}\",\"uuid\":\"${USER_REPLACEMENT_UUID}\"}")
echo "Dados enviados: {\"phone\":\"${USER_PHONE}\",\"uuid\":\"${USER_REPLACEMENT_UUID}\"}"
echo "Status recebido: ${USER_REPLACEMENT_LOGIN_STATUS}"
print_response "$(cat /tmp/user_replacement_login_resp.txt)"
if [[ "$USER_REPLACEMENT_LOGIN_STATUS" != "202" ]]; then
  echo "Esperava 202 para telefone existente com outro uuid. Resposta:"
  cat /tmp/user_replacement_login_resp.txt
  exit 1
fi
echo " "
echo "[U8] Confirmar novo uuid do telefone existente (deve substituir uuid do usuario)"
if [[ -z "${USER_REPLACEMENT_CONFIRMATION_CODE:-}" ]]; then
  echo "Copie do log da aplicacao o codigo da linha: SMS de confirmacao para telefone ${USER_PHONE} e uuid ${USER_REPLACEMENT_UUID}"
  read -r -p "Digite o codigo SMS para substituir o uuid: " USER_REPLACEMENT_CONFIRMATION_CODE
fi
USER_REPLACEMENT_CONFIRM_STATUS=$(curl -s -o /tmp/user_replacement_confirm_resp.txt -w "%{http_code}" -X POST "${BASE_URL}/users/confirm" \
  -H 'Content-Type: application/json' \
  -d "{\"phone\":\"${USER_PHONE}\",\"uuid\":\"${USER_REPLACEMENT_UUID}\",\"code\":\"${USER_REPLACEMENT_CONFIRMATION_CODE}\"}")
echo "Status recebido: ${USER_REPLACEMENT_CONFIRM_STATUS}"
print_response "$(cat /tmp/user_replacement_confirm_resp.txt)"
if [[ "$USER_REPLACEMENT_CONFIRM_STATUS" != "200" ]]; then
  echo "Falha ao confirmar substituicao de uuid. Resposta:"
  cat /tmp/user_replacement_confirm_resp.txt
  exit 1
fi
USER_REPLACEMENT_ID=$(cat /tmp/user_replacement_confirm_resp.txt | jq -r '.id')
if [[ "$USER_REPLACEMENT_ID" != "$USER_ID" ]]; then
  echo "Esperava substituir o uuid do usuario existente USER_ID=${USER_ID}, mas recebeu id ${USER_REPLACEMENT_ID}"
  exit 1
fi
echo " "
echo "[U9] Login usuario ativo com telefone e novo uuid (deve retornar token e usuario)"
USER_REPLACEMENT_LOGIN=$(login "$USER_PHONE" "$USER_REPLACEMENT_UUID")
print_response "$USER_REPLACEMENT_LOGIN"
USER_TOKEN=$(extract_token "$USER_REPLACEMENT_LOGIN")
if [[ -z "$USER_TOKEN" || "$USER_TOKEN" == "null" ]]; then
  echo "Falha no login user com novo uuid:"
  echo "$USER_REPLACEMENT_LOGIN"
  exit 1
fi
echo " "
echo "[U10] Usuario atualiza seus dados com o proprio token"
USER_UPDATE_RESPONSE=$(curl -s -X PUT "${BASE_URL}/users/${USER_ID}" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer ${USER_TOKEN}" \
  -d "{\"email\":\"${USER_EMAIL}\",\"name\":\"Usuario Demo\",\"description\":\"Usuario confirmado por telefone\"}")
print_response "$USER_UPDATE_RESPONSE"
echo " "
echo "[U11] Usuario tenta deletar categoria com token USER (deve falhar com 403)"
USER_DELETE_STATUS=$(curl -s -o /tmp/user_delete_resp.txt -w "%{http_code}" -X DELETE "${BASE_URL}/categories/${CATEGORY_ID}" \
  -H "Authorization: Bearer ${USER_TOKEN}")
echo "Status recebido: ${USER_DELETE_STATUS}"
print_response "$(cat /tmp/user_delete_resp.txt)"
if [[ "$USER_DELETE_STATUS" != "403" ]]; then
  echo "Esperava 403 para USER em rota destrutiva de categoria. Resposta:"
  cat /tmp/user_delete_resp.txt
  exit 1
fi
echo " "
echo "[U12] Admin deleta categoria criada para limpeza"
ADMIN_DELETE_STATUS=$(curl -s -o /tmp/user_demo_admin_delete_resp.txt -w "%{http_code}" -X DELETE "${BASE_URL}/categories/${CATEGORY_ID}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}")
echo "Status recebido: ${ADMIN_DELETE_STATUS}"
if [[ "$ADMIN_DELETE_STATUS" != "204" ]]; then
  echo "Falha ao deletar categoria com ADMIN. Resposta:"
  cat /tmp/user_demo_admin_delete_resp.txt
  exit 1
fi

echo ""
echo "#####################################"
echo "Demo user concluida com sucesso."
echo "#####################################"
echo ""
echo "Resumo:"
echo "- USER_ID=${USER_ID}"
echo "- USER_EMAIL=${USER_EMAIL}"
echo "- USER_PHONE=${USER_PHONE}"
echo "- USER_UUID=${USER_UUID}"
echo "- USER_REPLACEMENT_UUID=${USER_REPLACEMENT_UUID}"
