# jtech-tasklist

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Gradle](https://img.shields.io/badge/Gradle-8.7-green)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Ready-000000?logo=docker&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-336791?logo=postgresql&logoColor=white)
![H2 Database](https://img.shields.io/badge/Test%20DB-H2%20Database-lightgrey)

---

## What is

jtech-tasklist é uma API backend construída com Spring Boot para gerenciamento de tarefas.  
Ela fornece endpoints REST completos para criar, listar, buscar, atualizar e remover tarefas, utilizando PostgreSQL localmente e H2 para testes.  
O projeto segue arquitetura em camadas, usa DTOs, MapStruct, testes unitários, testes de integração e boas práticas de organização.

---

## Services

### TaskService
- Implementa regras de criação, atualização e remoção de tarefas.
- Atualiza timestamps automaticamente.
- Converte objetos via MapStruct.
- Aplica regras de domínio.
- Interage com os repositórios de persistência.

### Mapper (MapStruct)
- Mapeia Domain ↔ Entity ↔ DTO automaticamente.
- Evita boilerplate e garante consistência.

---

## Helper

- Bean Validation para garantir campos obrigatórios.
- Exception Handler global com respostas padronizadas.
- `Clock` injetado para controle de horários nos testes.
- Perfis de execução:
    - **local:** PostgreSQL
    - **test:** H2
- Dockerfile e docker-compose prontos para uso.

---

## How to use

### Criar tarefa

**POST /tasks**
```json
{
  "titulo": "Nova tarefa",
  "descricao": "Detalhes da tarefa"
}
```

### Listar tarefas

**GET /tasks**

### Buscar por ID

**GET /tasks/{id}**

### Atualizar tarefa

**PUT /tasks/{id}**
```json
{
  "titulo": "Atualizada",
  "descricao": "Descrição",
  "status": "CONCLUIDA"
}
```

### Deletar tarefa

**DELETE /tasks/{id}**

---

## Sample

Exemplo de resposta do POST:

```json
{
  "id": "b8b4c1b0-52f9-4c8e-912a-8909c7ecf50a",
  "titulo": "Nova tarefa",
  "descricao": "Detalhes da tarefa",
  "status": "PENDING",
  "statusDescricao": "Tarefa ainda não iniciada",
  "dataCriacao": "2025-12-11T14:00:00",
  "dataAtualizacao": "2025-12-11T14:00:00"
}
```

---

## How to run

### Requisitos
- Java 21
- Gradle 8+
- Docker
- Docker Compose

### Executar localmente
```bash
./gradlew bootRun
```

### Executar testes
```bash
./gradlew test
```

### Executar com Docker
```bash
docker build -t jtech-tasklist .
docker run -p 8080:8080 jtech-tasklist
```

### Executar via Docker Compose
```bash
docker-compose up --build
```

---

## Points to improve

- Paginação no endpoint de listagem.
- Autenticação e autorização (JWT).
- Testes de integração com Testcontainers no lugar de H2.
- Cache para otimizar performance.
- Atualizações em tempo real com WebSockets.

---
