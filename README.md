# API de Gestão de Agendamento e Controle de Vagas de Coworking (Desk Booking)

API REST desenvolvida em **Java 21** e **Spring Boot** com banco de dados **PostgreSQL**, documentada via **Swagger (OpenAPI)** e pronta para execução via **Docker**.

---

## Como Executar a Aplicação

Você pode rodar a aplicação de duas formas: **via Docker (recomendado)** ou **localmente via Maven**.

### Opção 1: Executando tudo com Docker Compose (API + PostgreSQL)

1. Certifique-se de estar na raiz do projeto (onde está o arquivo `docker-compose.yml`).
2. Suba os containers:
   ```bash
   docker compose up --build -d
   ```
3. Para acompanhar os logs da aplicação:
   ```bash
   docker compose logs -f api
   ```
4. Para encerrar os containers:
   ```bash
   docker compose down
   ```

### Opção 2: Executando Localmente com Maven

1. Suba apenas o banco de dados PostgreSQL via Docker (ou utilize uma instância local na porta `5432` com o banco `coworking_db`):
   ```bash
   docker compose up -d postgres
   ```
2. Execute a aplicação com o Maven Wrapper:
   - **Linux / macOS**:
     ```bash
     ./mvnw spring-boot:run
     ```
   - **Windows (PowerShell / CMD)**:
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```

---

## Como Acessar a Aplicação

É possível acessar a API e sua documentação interativa através do navegador pelo seguinte link:

https://coworking-6oog.onrender.com/

Entretanto ainda é necessário acessar o caminho correto para a documentação Swagger, que é:

https://coworking-6oog.onrender.com/swagger-ui.html
Ou
http://localhost:8080/v3/api-docs`

Caso queira rodar localmente siga as instruções abaixo para acessar a aplicação na porta `8080`.
Assim que a aplicação estiver em execução na porta `8080`, utilize os seguintes endereços no navegador ou cliente HTTP (Postman, Insomnia, cURL):

- **URL Base da API**: `http://localhost:8080`
- **Documentação Interativa (Swagger UI)**: `http://localhost:8080/swagger-ui.html`
- **Especificação OpenAPI (JSON)**: `http://localhost:8080/v3/api-docs`

> Através do **Swagger UI** (`http://localhost:8080/swagger-ui.html`), você pode testar visualmente todos os endpoints (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`) diretamente pelo navegador clicando em **"Try it out"**.

---

## Rotas Disponíveis e Exemplos de Acesso

### 1. Usuários (`/api/usuarios`) — CRUD Completo
- `POST /api/usuarios` — Cadastrar usuário
- `GET /api/usuarios` — Listar todos os usuários
- `GET /api/usuarios/{id}` — Buscar usuário por ID
- `PUT /api/usuarios/{id}` — Atualizar todos os dados do usuário
- `PATCH /api/usuarios/{id}` — Atualizar parcialmente o usuário (ex.: alterar inadimplência, limite semanal ou e-mail)
- `DELETE /api/usuarios/{id}` — Remover usuário

**Exemplo de criação de usuário (`POST http://localhost:8080/api/usuarios`):**
```json
{
  "nome": "Ana Carolina Silva",
  "email": "ana.silva@email.com",
  "documento": "12345678909",
  "senha": "senhaSegura123",
  "inadimplente": false,
  "ativo": true,
  "limiteHorasSemanais": 40
}
```

**Exemplo de atualização parcial (`PATCH http://localhost:8080/api/usuarios/1`):**
```json
{
  "inadimplente": false,
  "limiteHorasSemanais": 30
}
```

### 2. Estações de Trabalho (`/api/estacoes`)
- `POST /api/estacoes` — Cadastrar estação
- `GET /api/estacoes` — Listar estações
- `GET /api/estacoes/{id}` — Buscar estação por ID
- `PUT /api/estacoes/{id}` — Atualizar estação
- `PATCH /api/estacoes/{id}/disponibilidade?ativa=true` — Ativar/inativar estação
- `DELETE /api/estacoes/{id}` — Remover estação

**Exemplo de criação de estação (`POST http://localhost:8080/api/estacoes`):**
```json
{
  "codigo": "DESK-A01",
  "descricao": "Mesa individual próxima à janela",
  "capacidadeMaxima": 1,
  "valorPorHora": 25.00,
  "ativa": true
}
```

### 3. Salas de Reunião (`/api/salas-reuniao`)
- `POST /api/salas-reuniao` — Cadastrar sala de reunião
- `GET /api/salas-reuniao` — Listar salas de reunião
- `GET /api/salas-reuniao/{id}` — Buscar sala por ID
- `DELETE /api/salas-reuniao/{id}` — Remover sala

### 4. Reservas / Desk Booking (`/api/reservas`)
- `POST /api/reservas` — Criar nova reserva
- `GET /api/reservas` — Listar todas as reservas
- `GET /api/reservas/{id}` — Buscar reserva por ID
- `GET /api/reservas/usuario/{usuarioId}` — Listar reservas de um usuário
- `PATCH /api/reservas/{id}/cancelar` — Cancelar reserva (exige no mínimo 2h de antecedência)
- `DELETE /api/reservas/{id}` — Excluir reserva

**Exemplo de criação de reserva (`POST http://localhost:8080/api/reservas`):**
```json
{
  "usuarioId": 1,
  "estacaoId": 1,
  "inicio": "2026-11-20T09:00:00",
  "fim": "2026-11-20T13:00:00"
}
```

---

## Como Executar os Testes Unitários

Para rodar todos os testes unitários do projeto:

- **Linux / macOS**:
  ```bash
  ./mvnw test
  ```
- **Windows (PowerShell / CMD)**:
  ```powershell
  .\mvnw.cmd test
  ```

---

## Resumo das Regras de Negócio (DDD)
- **Sem sobreposição de horários**: uma estação não aceita duas reservas confirmadas no mesmo intervalo de tempo.
- **Duração do agendamento**: mínimo de 1 hora e máximo de 8 horas por reserva.
- **Antecedência para cancelamento**: reservas só podem ser canceladas com pelo menos **2 horas** de antecedência.
- **Bloqueio automático**: usuários inativos, inadimplentes ou que excedam seu limite semanal de horas são impedidos de criar novas reservas.
