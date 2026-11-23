# Por que a API usa `projectId` e `userId`?

Ao trabalhar com cenários, a API recebe dois IDs com funções diferentes:

- `projectId`: identifica **em qual projeto** o cenário será criado, listado ou alterado.
- `userId`: identifica **qual usuário está autenticado** e tentando executar a operação.

## De onde vem cada ID?

### `projectId`: vem da rota

O cliente informa o projeto na URL:

```http
POST /projects/project_001/scenarios
```

No controller, o Spring extrai `project_001` da parte `{projectId}` da rota:

```java
@PathVariable String projectId
```

Portanto:

```java
projectId = "project_001";
```

O `projectId` não vai no corpo da requisição porque a própria URL já informa a qual projeto o cenário pertence.

### `userId`: vem do token JWT

Após o login, o frontend envia o token em todas as requisições protegidas:

```http
Authorization: Bearer <token>
```

O `JwtAuthenticationFilter` lê o token e coloca o ID do usuário autenticado no contexto de segurança. Então o controller o recebe assim:

```java
@AuthenticationPrincipal String userId
```

O frontend não informa esse valor no body nem na URL. A API confia no ID extraído do token, não em um ID que o cliente poderia manipular.

## Por que usar os dois?

Somente ter o `projectId` não protege os dados. Um usuário poderia tentar acessar um projeto de outra pessoa caso soubesse seu ID:

```http
GET /projects/project_de_outro_usuario/scenarios
```

Por isso, antes de acessar cenários, o backend verifica se os dois IDs correspondem:

```java
projectRepository.existsByIdAndUserId(projectId, userId)
```

Essa consulta significa:

> Existe um projeto com este `projectId` que pertence a este `userId`?

Se a resposta for `false`, a API retorna `404 Not Found` e não mostra nenhum dado do projeto.

## Fluxo completo

```text
URL: /projects/project_001/scenarios
                 │
                 └── projectId = project_001

JWT enviado no header Authorization
                 │
                 └── userId = usuário autenticado

ProjectAccessService.requireOwner(projectId, userId)
                 │
                 ├── projeto pertence ao usuário → continua
                 └── não pertence / não existe → responde 404
```

Depois da validação, o `ScenarioService` pode criar, listar, buscar, atualizar ou excluir cenários daquele projeto com segurança.
