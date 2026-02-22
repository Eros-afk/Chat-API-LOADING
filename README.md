# Sistema de Chat LOADING

## Matriz de versões

| Componente | Versão |
|---|---|
| Java | 21 (LTS) |
| Spring Boot | 4.0.2 |
| Maven | 3.9+ |

## Dependências e perfis

- O `pom.xml` mantém `spring-boot-starter-test` como base única para testes.
- Dependências de teste específicas (`*-test` por stack) foram removidas para reduzir redundância.
- O `spring-boot-starter` foi removido por já existir cobertura via starters específicos (web, data, security, validation).
- `spring-boot-h2console` e `h2` ficam no perfil Maven `local`, evitando habilitação em todos os ambientes.

### Como ativar o perfil local

```bash
./mvnw spring-boot:run -Plocal
```
