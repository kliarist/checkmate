# Docker Setup for Local Development

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- Gradle 9.2+ (for building backend image)
- Bun 1.0+ (for building frontend image)

## Building Images

### Backend Image (Paketo Buildpacks)

The backend uses Gradle's `bootBuildImage` task with Paketo buildpacks instead of a Dockerfile:

```bash
cd backend
./gradlew bootBuildImage
```

This creates an optimized OCI image using Cloud Native Buildpacks with:
- Paketo Java buildpack
- Layered JARs for optimal caching
- Memory calculator for JVM tuning
- Security updates from Paketo

### Frontend Image

```bash
cd frontend
docker build -t checkmate-frontend:latest .
```

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

Rebuild backend after code changes using Paketo buildpacks:

```bash
cd backend
./gradlew bootBuildImage
docker-compose up -d backend
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
# Backend (using Paketo buildpacks)
cd backend
./gradlew bootBuildImage

# Frontend
cd ../frontend
docker build -t checkmate-frontend:latest .
```

## Benefits of Paketo Buildpacks

The backend uses Paketo buildpacks which provide:

- **Automatic JVM Configuration**: Memory calculator optimizes heap/metaspace
- **Layered Images**: Efficient caching and faster rebuilds
- **Security**: Regularly updated base images and dependencies
- **Best Practices**: Production-ready configurations out of the box
- **No Dockerfile Needed**: Declarative configuration in build.gradle

## Clean Up

Remove all containers and volumes:

```bash
docker-compose down -v
docker system prune -a
```

