# School Manager API

API REST para gerenciamento de alunos e professores de uma instituicao de ensino.

## Tecnologias

- Java 21
- Spring Boot 3.5.9
- Spring Data JPA
- H2 Database (em memoria)
- Lombok
- Swagger/OpenAPI
- JUnit 5 + Mockito

## Como Executar

### Pre-requisitos

- Java 21+
- Maven 3.8+

### Rodar a aplicacao

```bash
# Clonar o repositorio
git clone https://github.com/gabrielfonsecasantos/RID224620-MiniProjetoJava.git
cd school-manager

# Executar
mvn spring-boot:run
```

A aplicacao estara disponivel em `http://localhost:8080`

### Rodar os testes

```bash
mvn test
```

## Endpoints

### Students

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| GET | `/api/students` | Lista todos os alunos |
| GET | `/api/students/{id}` | Busca aluno por ID |
| POST | `/api/students` | Cria novo aluno |
| PUT | `/api/students/{id}` | Atualiza aluno |
| DELETE | `/api/students/{id}` | Remove aluno |

### Teachers

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| GET | `/api/teachers` | Lista todos os professores |
| GET | `/api/teachers/{id}` | Busca professor por ID |
| POST | `/api/teachers` | Cria novo professor |
| PUT | `/api/teachers/{id}` | Atualiza professor |
| DELETE | `/api/teachers/{id}` | Remove professor |

## Exemplos de Requisicao

### Criar Student

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Joao Silva",
    "cpf": "12345678901",
    "email": "joao@email.com",
    "phoneNumber": "11999999999",
    "registration": "2024001",
    "registrationDate": "2024-01-15",
    "cep": "01310100",
    "number": "100",
    "complement": "Apto 42"
  }'
```

### Criar Teacher

```bash
curl -X POST http://localhost:8080/api/teachers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Santos",
    "cpf": "98765432100",
    "email": "maria@email.com",
    "phoneNumber": "11888888888",
    "subject": "Mathematics",
    "hiringDate": "2023-03-01",
    "cep": "04538133",
    "number": "200"
  }'
```

## Integracao ViaCEP

A API integra com o servico ViaCEP para validar e complementar dados de endereco automaticamente.

Ao enviar apenas o CEP, os campos `street`, `neighborhood`, `city` e `uf` sao preenchidos automaticamente.

## Documentacao

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/api-docs
- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:schooldb`
  - User: `sa`
  - Password: (vazio)

## Estrutura do Projeto

```
src/main/java/com/java/dnc/school_manager/
├── config/          # Configuracoes (RestTemplate)
├── controller/      # Endpoints REST
├── dto/             # Data Transfer Objects
├── exception/       # Excecoes personalizadas
├── model/           # Entidades JPA
├── repository/      # Repositorios Spring Data
└── service/         # Logica de negocio
```

## Tratamento de Erros

| Codigo | Descricao |
|--------|-----------|
| 200 | Sucesso |
| 201 | Criado com sucesso |
| 204 | Deletado com sucesso |
| 400 | Dados invalidos (CPF duplicado, CEP invalido) |
| 404 | Recurso nao encontrado |
| 500 | Erro interno do servidor |
