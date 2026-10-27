#!/usr/bin/env bash
# Demonstração dos 3 cenários. Requer: curl, openssl. API rodando em $BASE.
# Uso: ./scripts/demo.sh        (aguarda ~70s no cenário 3)
BASE=${BASE:-http://localhost:8080}
SECRET_A=${CONSUMER_A_SECRET:-segredo-do-consumidor-A-troque-em-producao-0001}
SECRET_B=${CONSUMER_B_SECRET:-segredo-do-consumidor-B-troque-em-producao-0002}
SLEEP=${SLEEP:-70}   # deve ser maior que security.hmac.tolerance-seconds

sha256() { printf '%s' "$1" | openssl dgst -sha256 -hex | awk '{print $NF}'; }
hmac()   { printf '%s' "$2" | openssl dgst -sha256 -hmac "$1" -hex | awk '{print $NF}'; }
# sign SECRET CONSUMER METHOD PATH TIMESTAMP BODY
sign() {
  local h; h=$(sha256 "$6")
  hmac "$1" "$(printf '%s\n%s\n%s\n%s\n%s' "$3" "$4" "$5" "$2" "$h")"
}
# send CONSUMER TIMESTAMP SIGNATURE METHOD PATH BODY
send() {
  local args=(-sS -X "$4" "$BASE$5" -H "X-Consumer-Id: $1" -H "X-Timestamp: $2" -H "X-Signature: $3"
              -w "\n-> HTTP %{http_code}\n")
  [ -n "$6" ] && args+=(-H "Content-Type: application/json" -d "$6")
  curl "${args[@]}"
}

echo "=================== CENÁRIO 1: sucesso com os dois consumidores ==================="
BODY1='{"nome":"Notebook","descricao":"14 polegadas","preco":4500.00,"quantidade":10}'
TS1=$(date +%s)
SIG1=$(sign "$SECRET_A" consumidor-a POST /api/produtos "$TS1" "$BODY1")
echo "[consumidor-a] POST /api/produtos"
echo "  X-Timestamp=$TS1  X-Signature=$SIG1"
send consumidor-a "$TS1" "$SIG1" POST /api/produtos "$BODY1"

TS2=$(date +%s)
SIG2=$(sign "$SECRET_B" consumidor-b GET /api/produtos "$TS2" "")
echo; echo "[consumidor-b] GET /api/produtos"
echo "  X-Timestamp=$TS2  X-Signature=$SIG2"
send consumidor-b "$TS2" "$SIG2" GET /api/produtos ""

echo; echo "=================== CENÁRIO 2: mensagem inconsistente com o MAC ==================="
BODY_ASSINADO='{"nome":"Mouse","descricao":"sem fio","preco":150.00,"quantidade":5}'
BODY_ENVIADO='{"nome":"Mouse","descricao":"sem fio","preco":0.01,"quantidade":5}'
TS3=$(date +%s)
SIG3=$(sign "$SECRET_A" consumidor-a POST /api/produtos "$TS3" "$BODY_ASSINADO")
echo "[consumidor-a] MAC calculado sobre preco=150.00, mas enviado preco=0.01"
send consumidor-a "$TS3" "$SIG3" POST /api/produtos "$BODY_ENVIADO"

echo; echo "=================== CENÁRIO 3: replay da chamada do cenário 1 ==================="
echo "Aguardando ${SLEEP}s (janela de validade do timestamp = 60s)..."
sleep "$SLEEP"
echo "[consumidor-a] reenviando EXATAMENTE a chamada do cenário 1 (mesmo timestamp e MAC)"
send consumidor-a "$TS1" "$SIG1" POST /api/produtos "$BODY1"
