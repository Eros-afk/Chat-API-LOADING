# Sistema de Chat LOADING

## Matriz de versões

| Componente | Versão |
|---|---|
| Java | 21 (LTS) |
| Spring Boot | 3.5.6 |
| Maven | 3.9+ |

## Dependências e perfis

- O `pom.xml` mantém `spring-boot-starter-test` como base única para testes.
- Dependências de teste específicas (`*-test` por stack) foram removidas para reduzir redundância.
- O `spring-boot-starter` foi removido por já existir cobertura via starters específicos (web, data, security, validation).
- `h2` fica no perfil Maven `local`, evitando habilitação em todos os ambientes.

### Como ativar o perfil local

```bash
./mvnw spring-boot:run -Plocal
```
