# Checkmate - Chess Web Application

A modern, real-time multiplayer chess web application with user accounts, matchmaking, and game analysis.

## Features

- ♟️ **Guest Play**: Jump right in and play without registration
- 👤 **User Accounts**: Create an account to track your games and ratings
- 🏆 **Ranked Games**: Compete in rated matches with ELO rating system
- 🤖 **Play vs Computer**: Practice against AI with adjustable difficulty
- 👥 **Private Games**: Create private game links to play with friends
- 📊 **Game Analysis**: Review past games move-by-move with position evaluation
- ⏱️ **Time Controls**: Bullet, Blitz, Rapid, and Classical time controls
- 🎨 **Themes**: Light and dark mode support

## Tech Stack

### Frontend
- **Runtime & Package Manager**: Bun 1.0+ (fast JavaScript runtime)
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite 7 (dev server with HMR, production bundler)
- **Chess Logic**: chess.js
- **Board UI**: react-chessboard
- **State Management**: React Context API
- **Styling**: CSS Modules
- **Testing**: Vitest (unit/integration), Playwright (E2E)

### Backend
- **Framework**: Spring Boot 4.0.1
- **Language**: Java 21
- **Build Tool**: Gradle 8.11+ (Groovy DSL)
- **Database Migrations**: Liquibase
- **Real-time**: WebSocket (STOMP)
- **Database**: PostgreSQL
- **Authentication**: JWT with Spring Security
- **Caching**: Redis (for active games)

## Project Structure

```
checkmate/
├── frontend/          # React TypeScript application
│   ├── src/
│   │   ├── components/   # Reusable UI components
│   │   ├── pages/        # Page components
│   │   ├── hooks/        # Custom React hooks
│   │   ├── api/          # API client utilities
│   │   └── context/      # React context providers
│   └── tests/
├── backend/           # Spring Boot application
│   ├── src/
│   │   ├── main/java/com/checkmate/chess/
│   │   │   ├── model/       # JPA entities
│   │   │   ├── repository/  # Data access layer
│   │   │   ├── service/     # Business logic
│   │   │   ├── controller/  # REST controllers
│   │   │   ├── websocket/   # WebSocket handlers
│   │   │   └── config/      # Spring configuration
│   │   └── test/
│   └── resources/
└── specs/             # Feature specifications
    └── 001-chess-web-app/
        ├── spec.md           # Feature specification
        ├── plan.md           # Technical plan
        ├── tasks.md          # Implementation tasks
        └── checklists/       # Quality checklists
```

## Getting Started

### Prerequisites

- **Bun**: v1.0+ (for frontend - https://bun.sh)
- **Java**: JDK 21+ (for backend)
- **Gradle**: v8.11+ (for backend build)
- **PostgreSQL**: v14+ (for database)
- **Redis**: v6+ (optional, for caching)

### Local Development Setup

#### 1. Clone the repository

```bash
git clone https://github.com/yourusername/checkmate.git
cd checkmate
```

#### 2. Setup Database

```bash
# Create PostgreSQL database
createdb checkmate_dev

# Update backend/src/main/resources/application.yml with your database credentials
```

#### 3. Start Backend

```bash
cd backend
./gradlew build
./gradlew bootRun
```

Backend will start on `http://localhost:8080`

#### 4. Start Frontend

```bash
cd frontend
bun install
bun run dev
```

Frontend will start on `http://localhost:5173`

### Running Tests

#### Frontend Tests
```bash
cd frontend
bun test                  # Run unit/integration tests
bun run test:coverage     # Run with coverage report
bun run test:e2e          # Run E2E tests with Playwright
bun run test:e2e:ui       # Run E2E tests in UI mode
```

#### Backend Tests
```bash
cd backend
./gradlew test            # Run all tests
./gradlew jacocoTestReport # Run with coverage report
```

## Development Guidelines

### Code Quality Standards

This project follows strict code quality principles:

- **SOLID Principles**: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
- **DRY**: Don't Repeat Yourself - avoid code duplication
- **KISS**: Keep It Simple, Stupid - favor simplicity
- **YAGNI**: You Aren't Gonna Need It - implement only what's needed

### Testing Standards

- **Test Pyramid**: 70% unit tests, 20% integration tests, 10% e2e tests
- **Coverage Target**: Minimum 80% overall, 100% for critical business logic
- **TDD Approach**: Write tests first, then implement
- **Test Independence**: Each test must run independently

### Commit Convention

We use [Conventional Commits](https://www.conventionalcommits.org/):

```
feat(scope): add new feature
fix(scope): fix a bug
docs(scope): documentation changes
test(scope): add or update tests
refactor(scope): code refactoring
style(scope): formatting changes
chore(scope): maintenance tasks
```

### Accessibility

All UI components must meet WCAG 2.1 AA standards:
- Keyboard navigation support
- Screen reader compatibility
- Sufficient color contrast
- Focus indicators

## Performance Targets

- **Move Latency**: <100ms for real-time updates
- **Page Load**: <2s initial load
- **Concurrent Games**: Support 1000+ simultaneous games
- **Database Queries**: <50ms for critical operations

## Documentation

- [Feature Specification](./specs/001-chess-web-app/spec.md)
- [Technical Plan](./specs/001-chess-web-app/plan.md)
- [Implementation Tasks](./specs/001-chess-web-app/tasks.md)
- [API Documentation](./docs/api.md) _(coming soon)_
- [Deployment Guide](./docs/deployment.md) _(coming soon)_

## License

MIT License - See LICENSE file for details

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes following our code quality standards
4. Write tests for your changes
5. Ensure all tests pass and coverage meets targets
6. Commit using conventional commits
7. Push to your branch
8. Open a Pull Request

## Support

For issues and questions, please open a GitHub issue.

---

**Status**: 🚧 In Development (MVP Phase)

