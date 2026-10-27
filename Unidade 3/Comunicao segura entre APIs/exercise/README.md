# API REST CRUD com autenticação HMAC (Spring Boot 3 / Java 17)

## Executar
```bash
mvn spring-boot:run                              # H2 em memória
# ou com PostgreSQL:
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
mvn test                                         # testes dos cenários de autenticação
./scripts/demo.sh                                # gera a saída para os prints (curl + openssl)
```

## Endpoints (todos exigem HMAC)
| Método | Rota | Descrição |
|---|---|---|
| POST | /api/produtos | cria (201) |
| GET | /api/produtos | lista |
| GET | /api/produtos/{id} | busca (404 se não existir) |
| PUT | /api/produtos/{id} | atualiza |
| DELETE | /api/produtos/{id} | remove (204) |

## Protocolo de autenticação
Cabeçalhos obrigatórios: `X-Consumer-Id`, `X-Timestamp` (epoch em segundos), `X-Signature`.

```
MAC = HMAC-SHA256(secret_do_consumidor,
        METHOD \n PATH[?QUERY] \n TIMESTAMP \n CONSUMER_ID \n SHA256_HEX(BODY))   # em hexadecimal
```
Consumidores (em `application.yml`, chaves via variável de ambiente): `consumidor-a`, `consumidor-b`.

## Respostas / justificativas
**1 – Sucesso dos dois consumidores:** cada consumidor assina com a sua própria chave; o servidor
busca a chave pelo `X-Consumer-Id`, recalcula o MAC e compara. Iguais → a operação é executada.

**2 – Mensagem inconsistente com o MAC:** o corpo entra no MAC por meio do seu hash SHA-256. Se o corpo
(ou método, caminho, timestamp, consumidor) for alterado, o MAC recalculado difere do recebido → **401**.
A comparação é feita em tempo constante (`MessageDigest.isEqual`).

**3 – Replay attack:** duas defesas combinadas:
1. *Timestamp assinado + janela de 60 s*: o timestamp faz parte do MAC (não pode ser trocado sem invalidá-lo).
   Mensagem idêntica enviada minutos depois tem timestamp fora da janela → **401**.
2. *Cache de MACs já usados* (`ReplayCache`) durante a janela: bloqueia reenvio imediato, dentro dos 60 s.
O cache só recebe o MAC depois que ele é validado, para que um atacante não consiga poluí-lo.
Obs.: exige relógios razoavelmente sincronizados (NTP). Com várias instâncias, o cache deve ser compartilhado (Redis com TTL).

## Pontos de segurança
- Segredos fora do código (`CONSUMER_A_SECRET`, `CONSUMER_B_SECRET`); chaves longas e aleatórias em produção.
- Mensagens de erro genéricas ao cliente; o motivo detalhado vai só para o log.
- DTOs (nada de expor a entidade), Bean Validation, limite de tamanho do corpo, console H2 desabilitado.
- HMAC garante autenticidade e integridade, **não** confidencialidade: use HTTPS em produção.

## Prints a anexar
Rode `./scripts/demo.sh` e capture a saída dos 3 cenários (esperado: 201 e 200 / 401 / 401).
