# Sistema de Chat LOADING

API REST para cadastro de usuários, requisições de chat, troca de mensagens criptografadas e histórico de conversas com autenticação JWT.

## Stack

- Java 21+
- Spring Boot 3.5.6
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL (prod) / H2 (dev)
- Swagger/OpenAPI (springdoc)

## Funcionalidades implementadas

- Cadastro de usuários com username e senha criptografada (BCrypt).
- Login com emissão de JWT.
- Envio de solicitação de chat para outro usuário.
- Aceite/recusa de solicitações pendentes.
- Envio e listagem de mensagens apenas em chats ativos.
- Encerramento de chat por qualquer participante.
- Histórico de chats do usuário autenticado.
- Persistência de chats e mensagens no banco.
- Criptografia de mensagens em repouso com AES (`chat.crypto.secret`).
- Validação de entrada e tratamento padronizado de erros HTTP.
- Documentação automática via Swagger.

## Como executar

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Swagger UI:

- `http://localhost:8080/swagger-ui/index.html`

## Principais endpoints

### Público

- `POST /users` — cadastro de usuário
- `POST /auth/login` — autenticação e token JWT

### Protegidos (Bearer Token)

- `POST /chats` — cria solicitação de chat
- `POST /chats/respond` — aceita/recusa chat pendente
- `POST /chats/close/{chatId}` — encerra chat ativo
- `GET /chats/user/{userId}` — histórico de chats por usuário
- `GET /chats/my-chats` — histórico do usuário autenticado
- `POST /messages` — envia mensagem em chat ativo
- `GET /messages/chat/{chatId}` — lista mensagens por chat
- `GET /messages/{chatId}/page` — lista mensagens paginadas

## Testes

```bash
mvn test
```

Inclui testes unitários para transições de status do chat.
