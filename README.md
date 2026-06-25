# API Back-End — Rede de Lanchonetes "Raízes do Nordeste"

API REST para a gestão de uma rede de lanchonetes multicanal (App, Totem, Balcão, Pickup e Web), desenvolvida como Atividade Prática do **Projeto Multidisciplinar — Trilha Back-End** (UNINTER).

A solução cobre autenticação com perfis de acesso, gestão de unidades, produtos e estoque por unidade, criação e acompanhamento de pedidos com registro do canal de origem, e o processamento de pagamentos por meio de um **gateway externo simulado (mock)**.

---

## Sumário

- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Requisitos](#requisitos)
- [Configuração do banco de dados](#configuração-do-banco-de-dados)
- [Banco de dados, schema e seed](#banco-de-dados-schema-e-seed)
- [Como instalar e executar](#como-instalar-e-executar)
- [Documentação da API (Swagger/OpenAPI)](#documentação-da-api-swaggeropenapi)
- [Perfis de acesso (roles)](#perfis-de-acesso-roles)
- [Resumo dos endpoints](#resumo-dos-endpoints)
- [Como rodar os testes](#como-rodar-os-testes)
- [Estrutura de pastas](#estrutura-de-pastas)
- [Segurança e LGPD](#segurança-e-lgpd)
- [Limitações conhecidas](#limitações-conhecidas)
- [Evidências e links](#evidências-e-links)

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Build | Maven (wrapper incluído) |
| Banco de dados | MySQL 8 |
| Persistência | Spring Data JPA / Hibernate |
| Segurança | Spring Security + OAuth2 Resource Server, JWT (HS256), BCrypt |
| Mapeamento DTO | MapStruct 1.5.5 |
| Produtividade | Lombok |
| Documentação | SpringDoc OpenAPI / Swagger UI 3.0.2 |

---

## Arquitetura

Organização em camadas (estilo MVC), com separação clara de responsabilidades:

- **API (`Controller`)** — rotas/endpoints, autenticação e autorização (`@PreAuthorize`), contratos de request/response (DTOs) e tratamento de erros (`@ExceptionHandler`).
- **Application (`Service`)** — casos de uso e regras de negócio (criar pedido, processar pagamento, repor estoque, atualizar status).
- **Domain (`Model`)** — entidades JPA e enumerações do domínio (`Pedido`, `Produto`, `Estoque`, `Pagamento`, `Usuário`, `Unidade` e seus estados).
- **Infrastructure (`Repository`, `Mapper`, `Security`, `Config`)** — persistência via Spring Data JPA, conversão entidade↔DTO com MapStruct, segurança e integração com o gateway mock.

---

## Requisitos

- **JDK 21** instalado (`java -version` deve reportar 21).
- **MySQL 8** em execução, acessível em `localhost:3306`.
- **Maven** — não é obrigatório instalar; o projeto traz o *wrapper* (`./mvnw`).
- Um cliente de API para os testes: **Postman** ou **Insomnia**.

---

## Configuração do banco de dados

Para reduzir o atrito de execução, as credenciais ficam **diretamente no arquivo de configuração** `src/main/resources/application.yaml` — **não é necessário criar nenhuma variável de ambiente**.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/users?createDatabaseIfNotExist=true
    username: root      # ajuste para o usuário do seu MySQL
    password: root      # ajuste para a senha do seu MySQL
    driver-class-name: com.mysql.cj.jdbc.Driver

jwt:
  secret: c2VjcmV0S2V5UGFyYU9Qcm9qZXRvQmFzZVNwcmluZ1NlY3VyaXR5SmF2YQ==
  expiration-ms: 86400000
```

> **Único ajuste necessário:** se o seu MySQL usar usuário/senha diferentes de `root`/`root`, altere as duas linhas `username` e `password` no `application.yaml`. Não há mais nada a configurar.

O token JWT expira em **24 horas** (`jwt.expiration-ms = 86400000`) e o segredo já está fixo no arquivo.

---

## Banco de dados, schema e seed

- **Banco:** a aplicação conecta em `jdbc:mysql://localhost:3306/users` e usa `createDatabaseIfNotExist=true`, ou seja, **cria o banco `users` automaticamente** caso não exista. Basta o MySQL estar rodando e as credenciais do `application.yaml` (seção anterior) estarem corretas.
- **Schema / migrations:** o schema (tabelas e relacionamentos) é **gerado automaticamente pelo Hibernate** na inicialização (`spring.jpa.hibernate.ddl-auto: update`). O projeto **não** utiliza ferramentas de migration (Flyway/Liquibase) — não há scripts SQL a executar manualmente.
- **Seed:** **não há carga inicial de dados.** O primeiro usuário deve ser criado pelo endpoint `POST /auth/register`. A coleção Postman já automatiza esse cadastro (ver [Como rodar os testes](#como-rodar-os-testes)).

---

## Como instalar e executar

Clone o repositório e entre na pasta:

```bash
git clone https://github.com/guilhermepodgorodecki/projeto-back-end-uninter.git
cd projetoBase
```

Garanta que o MySQL está rodando e que as credenciais no `application.yaml` correspondem ao seu MySQL (seção [Configuração do banco de dados](#configuração-do-banco-de-dados)). Em seguida, suba a API:

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```powershell
mvnw.cmd spring-boot:run
```

> O wrapper baixa as dependências na primeira execução. Se preferir um Maven já instalado, use `mvn spring-boot:run`.

A API sobe em **`http://localhost:8080`**.

Para gerar o artefato executável (opcional):
```bash
./mvnw clean package
java -jar target/projetoBase-0.0.1-SNAPSHOT.jar
```

---

## Documentação da API (Swagger/OpenAPI)

Com a aplicação no ar, a documentação interativa é gerada automaticamente pelo SpringDoc:

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **Especificação OpenAPI (JSON):** http://localhost:8080/v3/api-docs

As rotas `/auth/**`, `/swagger-ui/**` e `/v3/api-docs/**` são públicas; as demais exigem token JWT.

---

## Perfis de acesso (roles)

| Perfil | Acessos principais |
|---|---|
| `ADMIN` | Gestão completa (unidades, produtos, estoque, pedidos, usuários). |
| `GERENTE` | Gestão de unidades, produtos, estoque, pedidos e status. |
| `ATENDENTE` | Consultas, criação de pedidos, atualização de status e pagamento. |
| `CLIENTE` | Criação de pedidos e solicitação de pagamento. |

A autenticação usa **Bearer Token**: envie o cabeçalho `Authorization: Bearer <token>` nas requisições protegidas. O token é obtido em `POST /auth/login`.

---

## Resumo dos endpoints

| Recurso | Método e rota | Perfis | Descrição |
|---|---|---|---|
| Auth | `POST /auth/register` | público | Cadastrar usuário (com `role`). |
| Auth | `POST /auth/login` | público | Autenticar e retornar o token JWT. |
| Users | `GET /users` | ADMIN, GERENTE | Listar usuários. |
| Unidades | `POST /unidade/cadastra` | ADMIN, GERENTE | Cadastrar unidade. |
| Unidades | `GET /unidade/busca` | ADMIN, GERENTE | Listar unidades. |
| Produtos | `GET /produto/listar` | ADMIN, GERENTE, ATENDENTE | Listar produtos. |
| Produtos | `GET /produto/{id}` | ADMIN, GERENTE, ATENDENTE | Detalhar produto. |
| Produtos | `POST /produto/cadastra` | ADMIN, GERENTE | Cadastrar produto. |
| Estoque | `GET /estoque/listar` | ADMIN, GERENTE, ATENDENTE | Consultar estoque (filtros `produtoId`, `unidadeId`). |
| Estoque | `GET /estoque/{id}` | ADMIN, GERENTE, ATENDENTE | Detalhar registro de estoque. |
| Estoque | `PATCH /estoque/{produtoId}/repor` | ADMIN, GERENTE | Repor saldo (quantidade > 0). |
| Estoque | `POST /estoque/cadastra` | ADMIN, GERENTE | Cadastrar saldo inicial. |
| Pedidos | `POST /pedidos/criar` | autenticado | Criar pedido (valida estoque, calcula total, registra canal). |
| Pedidos | `GET /pedidos/listar` | ADMIN, GERENTE, ATENDENTE | Listar/filtrar por canal (`?canalPedido=`). |
| Pedidos | `GET /pedidos/{id}` | ADMIN, GERENTE, ATENDENTE | Detalhar pedido. |
| Pedidos | `PATCH /pedidos/{id}/status` | ADMIN, GERENTE, ATENDENTE | Atualizar status. |
| Pagamentos | `POST /pagamentos` | CLIENTE, ATENDENTE | Processar pagamento (mock). |
| Pagamentos | `GET /pagamentos/{pedidoId}` | autenticado | Consultar pagamento do pedido. |

**Campos de domínio (enums):**
- `canalPedido`: `APP`, `TOTEM`, `BALCAO`, `PICKUP`, `WEB`
- `status` (pedido): `RECEBIDO`, `EM_PREPARO`, `PAGO`, `SAIU_PARA_ENTREGA`, `ENTREGUE`, `CANCELADO`
- `metodo` (pagamento): `DEBITO`, `CREDITO`, `DINHEIRO`, `PIX`

**Padrão de erro** (todas as falhas):
```json
{
  "status": 400,
  "mensagem": "canalPedido: canalPedido é obrigatório",
  "timestamp": "2026-06-23T12:00:00"
}
```

---

## Como rodar os testes

A validação é feita pela coleção **Postman/Insomnia** versionada no repositório:
`postman/Raizes_do_Nordeste.postman_collection.json`.

São **15 cenários (T01–T15)**: 9 positivos e 6 negativos, cobrindo autenticação/autorização (401/403), validação (400), regra de negócio (404 / pagamento não elegível), pagamento mock (aprovação e recusa) e multicanalidade (filtro por canal).

**Passo a passo:**

1. Suba a API e o MySQL (seções anteriores).
2. No Postman, **Import** → selecione o arquivo `.json` da coleção.
3. Confirme a variável de coleção `base_url` (padrão `http://localhost:8080`).
4. Execute as pastas **na ordem (1 → 7)** ou use o **Collection Runner**. Os scripts de teste **capturam o token e os IDs automaticamente** (`token`, `token_cliente`, `unidadeId`, `produtoId`, `pedidoId`) — não é preciso copiar nada manualmente.
5. As pastas 1–6 cobrem o fluxo positivo; a pasta **7. Erros** cobre os cenários negativos e deve ser executada **após** o fluxo principal.

> **Ordem do fluxo crítico:** o pagamento exige o pedido em `RECEBIDO`, por isso a atualização de status (T07) ocorre **após** o pagamento (T06).

**Testes automatizados (contexto):**
```bash
./mvnw test
```

---

## Estrutura de pastas

```
src/main/java/br/com/guilherme/projetoBase
├── Config/          # Configuração de segurança e beans
├── Controller/      # Endpoints REST (camada de API)
├── DTO/             # Objetos de request/response
├── Mapper/          # Conversão entidade <-> DTO (MapStruct)
├── Model/           # Entidades JPA e enums (domínio)
├── Repository/      # Repositórios Spring Data JPA
├── Security/        # JWT, filtros e autenticação
└── Service/         # Regras de negócio (casos de uso)
src/main/resources
└── application.yaml # Configuração da aplicação
```

---

## Segurança e LGPD

- **Senhas** armazenadas com hash **BCrypt** (nunca em texto puro).
- **Autenticação** por **JWT** (HS256), com API **stateless**.
- **Autorização** por perfil aplicada nos endpoints (`@PreAuthorize` + `@EnableMethodSecurity`).
- **Minimização de dados:** as respostas usam DTOs que **não expõem** o hash de senha.
- **Dados pessoais coletados:** nome, e-mail e senha (hash), com finalidade de autenticação e vínculo do pedido ao cliente.

---

## Limitações conhecidas

Declaradas de forma transparente:

- **Auditoria de ações sensíveis não implementada** (apenas `show-sql` em desenvolvimento). Evolução sugerida: tabela de auditoria alimentada nos serviços de pedido/pagamento.
- **Fidelização e promoções** ficaram como proposta (não implementadas).
- Na criação de pedido, falhas de **produto/unidade inexistente** e **estoque insuficiente** retornam atualmente **HTTP 500**. Melhoria recomendada: mapeá-las para **404** e **409**, respectivamente.

---

## Evidências e links

- **Repositório:** https://github.com/guilhermepodgorodecki/projeto-back-end-uninter.git
- **Swagger (local):** `http://localhost:8080/swagger-ui/index.html`
- **Coleção de testes:** `postman/Raizes_do_Nordeste.postman_collection.json`
- **DER / diagramas:** pasta `docs/` (DER, classes, casos de uso e sequência)

---

*Projeto acadêmico — UNINTER, Projeto Multidisciplinar (Trilha Back-End), 2026.*
