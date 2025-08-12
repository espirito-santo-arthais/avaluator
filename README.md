# Evaluator API — Jogo da Velha

API simples em **Spring Boot (Java 21)** para avaliar o estado de um tabuleiro de Jogo da Velha (tic-tac-toe).

## Arquitetura
- **Domain**: `Board`, `CellMark`, `GameStatus`, `GameResult`, `BoardEvaluator`, `InvalidBoardException`.
- **Application**: `EvaluateBoardUseCase` + `EvaluateBoardUseCaseImpl`, `ApplicationConfig`.
- **Infra/Web**: `EvaluatorApiController`, `WebDtoMapper`, `ApiExceptionHandler`.
- **Infra/Web/Filter**: `CorrelationIdFilter` (gera/propaga `X-Request-Id` → MDC `reqId`).

## Executar o projeto
Pré-requisitos: **Java 21** e **Maven**.

```bash
# rodar
mvn spring-boot:run

# ou empacotar e executar
mvn clean package
java -jar target/*-SNAPSHOT.jar
```

Servidor padrão em `http://localhost:8080`.

## Endpoint
`POST /api/evaluator/evaluate`

### Request (JSON)
```json
{
  "board": {
    "r1": { "c1": "X", "c2": "O", "c3": "EMPTY" },
    "r2": { "c1": "EMPTY", "c2": "X", "c3": "O" },
    "r3": { "c1": "EMPTY", "c2": "EMPTY", "c3": "X" }
  }
}
```

- Valores válidos de célula: `"X" | "O" | "EMPTY"`.
- Células **nulas/ausentes** são normalizadas para `EMPTY`.
- `board`, `r1`, `r2`, `r3` **são obrigatórios**.

### Response (JSON)
```json
{
  "status": "X_WINS",   // X_WINS | O_WINS | DRAW | ONGOING
  "winner": "X"         // X | O | EMPTY (sempre presente)
}
```

## Regras de avaliação
- Diferença de jogadas: `diff = #X - #O` deve ser **0 ou 1**.
- **X vence** ⇒ `diff == 1`.
- **O vence** ⇒ `diff == 0`.
- **Dupla vitória** (X e O ao mesmo tempo) é inválida.
- Sem vencedor: Tabuleiro **cheio** ⇒ `DRAW`. Ainda há `EMPTY` ⇒ `ONGOING`.

## Exemplos de uso
### X vence — Linha 1
```bash
curl --location 'http://localhost:8080/api/evaluator/evaluate'   --header 'accept: application/json'   --header 'Content-Type: application/json'   --data '{
  "board":{
    "r1":{"c1":"X","c2":"X","c3":"X"},
    "r2":{"c1":"O","c2":"EMPTY","c3":"EMPTY"},
    "r3":{"c1":"EMPTY","c2":"O","c3":"EMPTY"}
  }
}'
```

### Erro — board nulo (422)
```bash
curl --location 'http://localhost:8080/api/evaluator/evaluate'   --header 'accept: application/json'   --header 'Content-Type: application/json'   --data '{ "board": null }'
```

## Erros e status HTTP
- `422 Unprocessable Entity` — violações de negócio/contrato do tabuleiro
  - `O tabuleiro não pode ser nulo`
  - `A linha 1/2/3 do tabuleiro não pode ser nulo`
  - `contagem inválida: X-O deve ser 0 ou 1`
  - `X e O não podem vencer ao mesmo tempo`
  - `estado inconsistente para vitória de X|O`
- `400 Bad Request` — JSON malformado / enum inválido
  - `requisição inválida: ...`
- `500 Internal Server Error` — falha inesperada
  - `{"message":"internal_error"}`

## Logs & Correlation ID
- **Filtro** `CorrelationIdFilter`:
  - Lê `X-Request-Id` (UUID). Se ausente/inválido, **gera** um novo.
  - Escreve no **MDC** como `reqId` e propaga no header da resposta.
- Logs (português): controller/use case/serviço registram início/fim, tempo e resultado; avisos para estados inválidos no domínio.

> Dica de layout Logback:
```
%d %-5level [%X{reqId}] %logger{36} - %msg%n
```

## Testes
- **Unitários**
  - `BoardEvaluatorTest`: erros de diff, dupla vitória, paridade; 16 vitórias (parametrizado); `DRAW`; `ONGOING`.
  - `BoardTest`: `count(X/O/EMPTY)`, `get(i,j)` válidos e limites inválidos.
  - `EvaluateBoardUseCaseImplTest`: chama `evaluator.evaluate` uma vez e propaga exceções.
  - `WebDtoMapperTest`: valida nulos (422), normalização de células e `toDto`.
  - `ApiExceptionHandlerTest`: `422` para `InvalidBoardException`, `500` genérico.
  - `CorrelationIdFilterTest`: gera/propaga `reqId` e limpa MDC.
- **E2E**
  - `@SpringBootTest(webEnvironment=RANDOM_PORT)` + `TestRestTemplate`.
  - Casos: X vence (200), board nulo (422).

Executar:
```bash
mvn test
# e2e específico
mvn -Dtest=EvaluatorE2ETest test
```

## Cobertura
Com **JaCoCo** no `pom.xml`:
```bash
mvn clean verify
# relatório
target/site/jacoco/index.html
```

## Estrutura do projeto
```
src/
├─ main
│  ├─ java
│  │  └─ br/com/petros/avaluator
│  │     │  EvaluatorApplication.java
│  │     │
│  │     ├─ application
│  │     │  │  ApplicationConfig.java
│  │     │  │  EvaluateBoardUseCase.java
│  │     │  │
│  │     │  └─ impl
│  │     │        EvaluateBoardUseCaseImpl.java
│  │     │
│  │     ├─ domain
│  │     │  ├─ exception
│  │     │  │     InvalidBoardException.java
│  │     │  │
│  │     │  ├─ model
│  │     │  │     Board.java
│  │     │  │     CellMark.java
│  │     │  │     GameResult.java
│  │     │  │     GameStatus.java
│  │     │  │
│  │     │  └─ service
│  │     │        BoardEvaluator.java
│  │     │
│  │     └─ infrastructure/web
│  │        ├─ controller
│  │        │     EvaluatorApiController.java
│  │        │
│  │        ├─ error
│  │        │     ApiExceptionHandler.java
│  │        │
│  │        ├─ filter
│  │        │     CorrelationIdFilter.java
│  │        │
│  │        └─ mapper
│  │              WebDtoMapper.java
│  │
│  └─ resources
│     │  application.yml
│     │  logback-spring.xml
│     │
│     ├─ openapi
│     │     evaluator.yaml
│     │
│     ├─ static
│     └─ templates
└─ test
   └─ java
      └─ br/com/petros/avaluator
         │  EvaluatorApplicationTests.java
         │
         ├─ application/impl
         │     EvaluateBoardUseCaseImplTest.java
         │
         ├─ domain
         │  ├─ model
         │  │     BoardTest.java
         │  │
         │  └─ service
         │        BoardEvaluatorTest.java
         │
         ├─ e2e
         │     EvaluatorE2ETest.java
         │
         └─ infrastructure/web
            ├─ controller
            │     EvaluatorApiControllerTest.java
            │
            ├─ error
            │     ApiExceptionHandlerTest.java
            │
            ├─ filter
            │     CorrelationIdFilterTest.java
            │
            └─ mapper
                  WebDtoMapperTest.java
```
