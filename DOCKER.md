# Docker Setup for Local Development

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+

## Quick Start

Start all services:

```bash
docker-compose up -d
```

Stop all services:

```bash
docker-compose down
```

Stop and remove volumes:

```bash
docker-compose down -v
```

## Services

### PostgreSQL
- Port: 5432
- Database: checkmate_dev
- Username: postgres
- Password: postgres

### Redis
- Port: 6379

### Backend (Spring Boot)
- Port: 8080
- API: http://localhost:8080/api
- WebSocket: ws://localhost:8080/ws
- Health: http://localhost:8080/actuator/health

### Frontend (React + Vite)
- Port: 5173
- URL: http://localhost:5173

## Development Workflow

### Backend Development

Rebuild backend after code changes:

```bash
docker-compose up -d --build backend
```

View backend logs:

```bash
docker-compose logs -f backend
```

### Frontend Development

Rebuild frontend after code changes:

```bash
docker-compose up -d --build frontend
```

View frontend logs:

```bash
docker-compose logs -f frontend
```

### Database Access

Connect to PostgreSQL:

```bash
docker exec -it checkmate-postgres psql -U postgres -d checkmate_dev
```

### Redis Access

Connect to Redis CLI:

```bash
docker exec -it checkmate-redis redis-cli
```

## Troubleshooting

### Port Already in Use

If ports are already in use, modify `docker-compose.yml`:

```yaml
ports:
  - "5433:5432"  # PostgreSQL
  - "6380:6379"  # Redis
  - "8081:8080"  # Backend
  - "5174:5173"  # Frontend
```

### Database Connection Issues

Reset database:

```bash
docker-compose down -v
docker-compose up -d postgres
```

### View All Logs

```bash
docker-compose logs -f
```

### Restart Specific Service

```bash
docker-compose restart backend
```

## Production Build

Build production images:

```bash
docker-compose -f docker-compose.prod.yml build
```

## Clean Up

Remove all containers and volumes:

```bash
docker-compose down -v
docker system prune -a
```

