# Scenara — Backend

**API REST para simulação financeira, gerenciamento de cenários e análise de resultados.**

O backend da Scenara é responsável por centralizar as regras de negócio, gerenciar os dados dos usuários, projetos e cenários, executar projeções financeiras e disponibilizar os resultados por meio de uma API REST.

A aplicação é desenvolvida em Java com Spring Boot e utiliza uma arquitetura de monólito modular, inspirada nos princípios da Clean Architecture.

## Tecnologias

| Tecnologia | Utilização |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot | Framework backend |
| Spring Web | Construção da API REST |
| Spring Security | Segurança e autorização |
| Spring Data MongoDB | Persistência de dados |
| Spring Data Redis | Cache |
| Spring Validation | Validação de entradas |
| JWT | Autenticação baseada em tokens |
| Springdoc OpenAPI | Documentação da API |
| JUnit | Testes automatizados |
| Mockito | Simulação de dependências |
| Docker | Conteinerização |

## Arquitetura

A Scenara utiliza uma arquitetura de **Monólito Modular**, organizada por domínios de negócio.

Essa abordagem permite manter os módulos separados por responsabilidade, preservando a simplicidade operacional de uma aplicação única.

Os módulos devem possuir responsabilidades bem definidas e evitar dependências desnecessárias entre si.

### Estrutura de diretórios

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/com/scenara/
│   │   │   │
│   │   │   ├── ScenaraApplication.java
│   │   │   │
│   │   │   ├── auth/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── dto/
│   │   │   │   └── security/
│   │   │   │
│   │   │   ├── users/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   │
│   │   │   ├── projects/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   │
│   │   │   ├── scenarios/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   │
│   │   │   ├── simulations/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── engine/
│   │   │   │   ├── calculator/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   │
│   │   │   ├── integrations/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── provider/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   └── dto/
│   │   │   │
│   │   │   └── shared/
│   │   │       ├── config/
│   │   │       ├── exception/
│   │   │       ├── validation/
│   │   │       └── security/
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-dev.yml
│   │
│   └── test/
│       └── java/com/scenara/
│
├── .env.example
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

*As pastas representam a organização proposta e devem ser criadas conforme os módulos forem implementados.*

## Módulos principais

### Auth

Responsável pela autenticação e segurança da aplicação.

- Cadastro de usuários.
- Login.
- Geração e validação de JWT.
- Controle de acesso.
- Proteção de rotas.

### Users

Responsável pelo gerenciamento dos usuários.

- Dados cadastrais.
- Consulta de perfil.
- Atualização de informações.
- Relacionamento entre usuários e projetos.

### Projects

Responsável pelo gerenciamento dos projetos.

- Criação de projetos.
- Consulta de projetos.
- Atualização.
- Exclusão.
- Controle de propriedade e acesso.

### Scenarios

Responsável pelas configurações financeiras de cada cenário.

- Criação de cenários.
- Configuração de premissas.
- Atualização de variáveis.
- Consulta de cenários.
- Gerenciamento de cenários vinculados aos projetos.

### Simulations

Responsável pelo processamento das projeções financeiras.

- Execução do motor matemático.
- Cálculo de receitas.
- Cálculo de custos.
- Projeção de resultados operacionais.
- Cálculo do fluxo acumulado.
- Identificação do payback simples.
- Armazenamento dos resultados.
- Histórico de simulações.

### Integrations

Módulo destinado às integrações externas, especialmente Open Finance.

Evolução planejada:

- Comunicação com provedores financeiros.
- Gerenciamento de conexões bancárias.
- Controle de consentimentos.
- Importação de contas e transações.
- Sincronização de movimentações.
- Normalização dos dados financeiros.

## Motor de simulação financeira

O motor de simulação é independente da camada de persistência e das integrações externas.

Seu objetivo é receber premissas financeiras e produzir projeções determinísticas.

### Variáveis

| Variável | Descrição |
|---|---|
| InitialInvestment | Investimento inicial |
| InitialRevenue | Receita mensal inicial |
| InitialCost | Custo operacional mensal |
| RevenueGrowthRate | Crescimento mensal da receita |
| CostGrowthRate | Crescimento mensal dos custos |
| ProjectionMonths | Período da projeção |

### Fórmulas

**Receita projetada**

```text
Revenue[n] = Revenue[n-1] * (1 + RevenueGrowthRate)
```

**Custos projetados**

```text
Cost[n] = Cost[n-1] * (1 + CostGrowthRate)
```

**Resultado operacional**

```text
OperatingResult = Revenue - Cost
```

**Fluxo acumulado**

```text
AccumulatedCashFlow[n] =
    AccumulatedCashFlow[n-1] + OperatingResult[n]
```

O fluxo acumulado inicial considera o investimento como saída de caixa.

### Indicadores gerados

- Receita total projetada.
- Custos totais projetados.
- Resultado operacional acumulado.
- Saldo acumulado final.
- Margem operacional.
- Payback simples.

Os valores monetários devem utilizar `BigDecimal`, com escala e política de arredondamento definidas explicitamente.

## Banco de dados

### MongoDB

Banco principal responsável pela persistência dos dados.

Coleções previstas:

| Coleção | Responsabilidade |
|---|---|
| `users` | Usuários |
| `projects` | Projetos |
| `scenarios` | Cenários |
| `simulations` | Execuções e resultados |
| `integrations` | Conexões financeiras |
| `financial_accounts` | Contas importadas |
| `financial_transactions` | Movimentações importadas |
| `synchronization_logs` | Histórico de sincronizações |

### Redis

Utilizado para:

- Cache de consultas frequentes.
- Armazenamento temporário de informações.
- Controle de sincronizações.
- Otimização de operações repetitivas.

O Redis não substitui o MongoDB como fonte persistente dos dados.

## Segurança e integridade

A aplicação deve garantir:

- Autenticação por JWT.
- Autorização baseada no usuário autenticado.
- Isolamento dos projetos por proprietário.
- Validação de entradas.
- Proteção de endpoints.
- Tratamento centralizado de exceções.
- Armazenamento seguro de credenciais.
- Proteção de tokens de integrações.
- Controle de consentimentos financeiros.

Cada simulação deve armazenar um snapshot das premissas utilizadas, garantindo que execuções anteriores não sejam alteradas quando um cenário for editado.

## API REST

A API segue os princípios REST e utiliza JSON para comunicação.

Os endpoints devem ser organizados por domínio, com versionamento quando necessário.

Exemplo de organização futura:

```text
/api/auth
/api/users
/api/projects
/api/projects/{projectId}/scenarios
/api/scenarios/{scenarioId}/simulations
/api/projects/{projectId}/simulations
/api/integrations
```

A documentação dos endpoints será disponibilizada por OpenAPI/Swagger.

## Configuração e execução

### Pré-requisitos

- Java 21.
- Maven.
- MongoDB.
- Redis.
- Docker (opcional).

### 1. Acessar o backend

```bash
cd backend
```

### 2. Configurar variáveis de ambiente

Crie um arquivo `.env` ou configure as variáveis no ambiente de execução:

```env
MONGODB_URI=mongodb://localhost:27017/scenara

REDIS_HOST=localhost
REDIS_PORT=6379

JWT_SECRET=replace-with-a-secure-secret
```

Não compartilhe credenciais reais ou arquivos `.env` no repositório.

### 3. Executar a aplicação

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

### 4. Executar testes

```bash
./mvnw test
```

### 5. Gerar build

```bash
./mvnw clean package
```

## Boas práticas de desenvolvimento

- Manter os módulos separados por domínio.
- Evitar dependências desnecessárias entre módulos.
- Utilizar DTOs para entrada e saída da API.
- Centralizar o tratamento de exceções.
- Manter as regras financeiras no motor de simulação.
- Utilizar `BigDecimal` para valores monetários.
- Escrever testes unitários para os cálculos.
- Garantir isolamento de dados por usuário.
- Não misturar lógica de integração externa com regras financeiras.
- Preservar o histórico das simulações.
- Não utilizar Redis como fonte definitiva de dados.

## Roadmap

| Etapa | Objetivo |
|---|---|
| 01 | Configuração do Spring Boot |
| 02 | Estrutura modular e MongoDB |
| 03 | Autenticação e segurança |
| 04 | Gerenciamento de projetos |
| 05 | Gerenciamento de cenários |
| 06 | Motor de simulação |
| 07 | Histórico de execuções |
| 08 | Cache com Redis |
| 09 | Integração Open Finance |
| 10 | Inteligência financeira |

---

**Scenara — Explore possibilidades. Decida com clareza.**
