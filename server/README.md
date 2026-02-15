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
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |

### Users (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | Get current user |

### Notes (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notes` | Get all notes |
| GET | `/api/notes/{id}` | Get note by ID |
| POST | `/api/notes` | Create note |
| PUT | `/api/notes/{id}` | Update note |
| DELETE | `/api/notes/{id}` | Delete note |

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | 8080 | Server port |
| `DATABASE_URL` | jdbc:postgresql://localhost:5432/kmpshowcase | DB connection |
| `DATABASE_USER` | postgres | DB username |
| `DATABASE_PASSWORD` | postgres | DB password |
| `JWT_SECRET` | (dev default) | JWT signing secret |
| `JWT_ISSUER` | kmp-showcase | JWT issuer |
| `JWT_AUDIENCE` | kmp-showcase-users | JWT audience |

## Example Requests

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","name":"Test User"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### Create Note (with token)
```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"My Note","content":"Note content"}'
```
