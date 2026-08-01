# KMP Showcase - Backend Server

Ktor server with PostgreSQL database for the KMP Showcase project.

## Tech Stack

- **Ktor 3** - Kotlin async web framework
- **Exposed** - Kotlin SQL framework
- **PostgreSQL** - Database
- **JWT** - Authentication
- **BCrypt** - Password hashing
- **Docker** - Containerization

## Quick Start

### Local Development

1. Start PostgreSQL:
```bash
docker compose up db -d
```

2. Run server:
```bash
./gradlew :server:run
```

Server runs at `http://localhost:8080`

### Docker (Full Stack)

```bash
docker compose up --build
```

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/v1/auth/register` | Register new user |
| POST | `/v1/auth/login` | Login user |

### Users (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/v1/users/me` | Get current user |

### Notes (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/v1/notes` | Get all notes |
| GET | `/v1/notes/{id}` | Get note by ID |
| POST | `/v1/notes` | Create note |
| PUT | `/v1/notes/{id}` | Update note |
| DELETE | `/v1/notes/{id}` | Delete note |

## Configuration

Config lives in [`src/main/resources/application.yaml`](src/main/resources/application.yaml) (Ktor `EngineMain` style). Every value supports environment-variable override via `"$ENV_VAR:default"` substitution:

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | 8080 | Server port |
| `USE_H2` | true | H2 in-memory DB (development) vs PostgreSQL |
| `DATABASE_URL` | jdbc:postgresql://localhost:5432/kmpshowcase | DB connection |
| `DATABASE_USER` | postgres | DB username |
| `DATABASE_PASSWORD` | postgres | DB password |
| `JWT_SECRET` | (dev default) | JWT signing secret |
| `JWT_ISSUER` | kmp-showcase | JWT issuer |
| `JWT_AUDIENCE` | kmp-showcase-users | JWT audience |

## Example Requests

### Register
```bash
curl -X POST http://localhost:8080/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","name":"Test User"}'
```

### Login
```bash
curl -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### Create Note (with token)
```bash
curl -X POST http://localhost:8080/v1/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"My Note","content":"Note content"}'
```
