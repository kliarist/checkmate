# Chess Backend - Spring Boot Application

Backend API for the Checkmate chess web application.

## Technology Stack

- **Framework**: Spring Boot 4.0.1
- **Java Version**: JDK 25 (with Lombok, var, final)
- **Build Tool**: Gradle 9.2+ (Groovy DSL)
- **Database**: PostgreSQL 14+
- **Database Migrations**: Liquibase (SQL format, timestamp naming)
- **Authentication**: JWT (JSON Web Tokens)
- **Real-time**: WebSocket with STOMP
- **Code Quality**: Lombok (boilerplate reduction)
- **Testing**: JUnit 5, Spring Boot Test
- **Code Style**: Checkstyle (Google Java Style Guide)
- **Coverage**: JaCoCo

## Prerequisites

- JDK 25 or higher
- Gradle 9.2+ (or use included Gradle wrapper)
- PostgreSQL 14+
- (Optional) Redis 6+ for caching

## Getting Started

### 1. Database Setup

Create the development and test databases:

```bash
# Development database
createdb checkmate_dev

# Test database
createdb checkmate_test
```

### 2. Configuration

Update `src/main/resources/application.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/checkmate_dev
    username: your_username
    password: your_password
```

### 3. Build the Project

```bash
# Clean and compile
./gradlew clean build

# Run tests
./gradlew test

# Build without tests
./gradlew build -x test
```

### 4. Run the Application

```bash
# Using Gradle
./gradlew bootRun

# Or run the JAR directly
java -jar build/libs/chess-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/checkmate/chess/
│   │   │   ├── config/          # Spring configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Custom exceptions
│   │   │   ├── model/           # JPA entities
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── security/        # Security configuration
│   │   │   ├── service/         # Business logic
│   │   │   ├── websocket/       # WebSocket handlers
│   │   │   └── ChessApplication.java
│   │   └── resources/
│   │       ├── application.yml  # Main configuration
│   │       └── db/migration/    # Database migrations
│   └── test/
│       ├── java/com/checkmate/chess/
│       │   ├── controller/      # Controller tests
│       │   ├── integration/     # Integration tests
│       │   ├── repository/      # Repository tests
│       │   ├── service/         # Service tests
│       │   └── ChessApplicationTests.java
│       └── resources/
│           └── application-test.yml
├── pom.xml
└── README.md
```

## API Endpoints

### Health Check
- `GET /actuator/health` - Application health status

### Authentication (Coming Soon)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

### Games (Coming Soon)
- `POST /api/games/guest` - Create guest game
- `GET /api/games/{id}` - Get game details
- `POST /api/games/{id}/moves` - Make a move

### WebSocket (Coming Soon)
- `/topic/game/{gameId}/moves` - Real-time move updates

## Testing

### Run All Tests
```bash
./gradlew test
```

### Run with Coverage Report
```bash
./gradlew test jacocoTestReport
```

Coverage report will be available at `build/reports/jacoco/test/html/index.html`

### Coverage Requirements
- **Overall**: Minimum 80% line coverage
- **Critical Logic**: 100% coverage (chess rules, authentication)

## Code Quality

### Checkstyle
The project uses Google Java Style Guide:

```bash
# Run Checkstyle
./gradlew checkstyleMain checkstyleTest
```

### Code Quality Standards
- Functions must be <20 lines
- Follow SOLID principles
- No code duplication (DRY)
- Comprehensive error handling
- Meaningful variable names

## Database Migrations

Database schema changes are managed using Liquibase with YAML format:

```
src/main/resources/db/changelog/
├── db.changelog-master.yaml          # Master changelog
└── changes/
    ├── v1.0.0-initial-schema.yaml   # Initial schema
    ├── v1.1.0-add-ratings.yaml      # Future migrations
    └── ...
```

### Running Migrations

Liquibase runs automatically on application startup. To run manually:

```bash
# Run all pending migrations
./gradlew update

# Rollback last changeset
./gradlew rollback

# Generate SQL for review (without applying)
./gradlew updateSQL
```

### Creating New Migrations

Create a new YAML file in `src/main/resources/db/changelog/changes/`:

```yaml
databaseChangeLog:
  - changeSet:
      id: 2
      author: your-name
      comment: Description of changes
      changes:
        - createTable:
            tableName: example
            columns:
              - column:
                  name: id
                  type: uuid
```

Then include it in `db.changelog-master.yaml`.

## Development Tips

### Hot Reload
Spring Boot DevTools enables automatic restart:
```bash
./gradlew bootRun
```

### Debug Mode
```bash
./gradlew bootRun --debug-jvm
```

### Profiles
- `default` - Development profile
- `test` - Testing profile
- `prod` - Production profile (to be configured)

## Docker Image with Paketo Buildpacks

Build an optimized Docker image using Cloud Native Buildpacks:

```bash
./gradlew bootBuildImage
```

This creates `checkmate-backend:latest` using Paketo buildpacks with:
- Automatic JVM memory tuning
- Layered JAR structure for faster rebuilds
- Security-hardened base image
- Production-ready defaults

Run the image:

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/checkmate_dev \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  checkmate-backend:latest
```

## Dependencies

Key dependencies:
- `spring-boot-starter-web` - REST API
- `spring-boot-starter-websocket` - WebSocket support
- `spring-boot-starter-security` - Security & authentication
- `spring-boot-starter-data-jpa` - Database access
- `postgresql` - PostgreSQL driver
- `jjwt` - JWT token handling
- `lombok` - Reduce boilerplate code

## Troubleshooting

### Database Connection Issues
1. Ensure PostgreSQL is running: `pg_isready`
2. Verify database exists: `psql -l | grep checkmate`
3. Check credentials in `application.yml`

### Port Already in Use
Change the port in `application.yml`:
```yaml
server:
  port: 8081
```

### Tests Failing
Ensure test database exists:
```bash
createdb checkmate_test
```

## Contributing

1. Follow Google Java Style Guide
2. Write tests for all new features (TDD approach)
3. Ensure all tests pass before committing
4. Maintain 80%+ code coverage
5. Use conventional commits

## License

MIT License - See main project LICENSE file

