# Chess Backend - Spring Boot Application

Backend API for the Checkmate chess web application.

## Technology Stack

- **Framework**: Spring Boot 3.4.2
- **Java Version**: JDK 21
- **Build Tool**: Maven 3.9+
- **Database**: PostgreSQL 14+
- **Authentication**: JWT (JSON Web Tokens)
- **Real-time**: WebSocket with STOMP
- **Testing**: JUnit 5, Spring Boot Test
- **Code Quality**: Checkstyle (Google Java Style Guide)
- **Coverage**: JaCoCo

## Prerequisites

- JDK 21 or higher
- Maven 3.9+
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
mvn clean compile

# Run tests
mvn test

# Package (creates JAR file)
mvn package
```

### 4. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or run the JAR directly
java -jar target/chess-0.0.1-SNAPSHOT.jar
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
mvn test
```

### Run with Coverage Report
```bash
mvn test jacoco:report
```

Coverage report will be available at `target/site/jacoco/index.html`

### Coverage Requirements
- **Overall**: Minimum 80% line coverage
- **Critical Logic**: 100% coverage (chess rules, authentication)

## Code Quality

### Checkstyle
The project uses Google Java Style Guide:

```bash
# Run Checkstyle
mvn checkstyle:check
```

### Code Quality Standards
- Functions must be <20 lines
- Follow SOLID principles
- No code duplication (DRY)
- Comprehensive error handling
- Meaningful variable names

## Database Migrations

Database schema changes are managed using Flyway:

```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_ratings.sql
└── ...
```

## Development Tips

### Hot Reload
Spring Boot DevTools enables automatic restart:
```bash
mvn spring-boot:run
```

### Debug Mode
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Profiles
- `default` - Development profile
- `test` - Testing profile
- `prod` - Production profile (to be configured)

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

