---
description: "Implementation tasks for Chess Web Application"
---

# Tasks: Chess Web Application

**Input**: Design documents from `/specs/001-chess-web-app/`
**Prerequisites**: spec.md (complete), plan.md (template - will be filled during execution)

**Tests**: Tests are included per NFR-006 to NFR-010 (80% coverage minimum, Test Pyramid: 70% unit, 20% integration, 10% e2e)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

**Web app structure** (React frontend + Spring Boot backend):
- **Frontend**: `frontend/src/` 
- **Backend**: `backend/src/main/java/com/checkmate/chess/`
- **Frontend tests**: `frontend/src/__tests__/`
- **Backend tests**: `backend/src/test/java/com/checkmate/chess/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Create project structure with frontend/ and backend/ directories
- [X] T002 Initialize React project with TypeScript in frontend/ (npm create vite@latest frontend -- --template react-ts, then migrate to Bun)
- [X] T003 Initialize Spring Boot 4 project in backend/ (Spring Boot 4.0.1, Gradle 8.11+ with Groovy DSL, JDK 21: Web, WebSocket, Security, Data JPA, PostgreSQL, Liquibase)
- [X] T004 [P] Configure ESLint and Prettier for frontend code quality
- [X] T005 [P] Configure Checkstyle for backend code quality (Google Java Style Guide)
- [X] T006 [P] Setup Vitest for unit/integration testing and Playwright for E2E testing with coverage reporting
- [X] T007 [P] Setup JUnit 5 for backend testing with JaCoCo coverage reporting (via Gradle)
- [X] T008 [P] Configure Git pre-commit hooks (Husky) for linting and conventional commits
- [X] T009 [P] Create .gitignore for frontend (node_modules, dist) and backend (target, .class)
- [X] T010 [P] Setup CI/CD pipeline configuration file (GitHub Actions with Bun, Gradle, Playwright)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Database Setup
- [X] T011 Setup PostgreSQL database locally and configure connection in backend/src/main/resources/application.yml
- [X] T012 Create database migration tool configuration (Liquibase)
- [X] T013 Create initial database schema migration: users table (id, email, password_hash, username, created_at, elo_rating, games_played, wins, losses, draws)
- [X] T014 Create games table migration (id, white_player_id, black_player_id, game_type, time_control, current_fen, pgn, status, result, end_reason, created_at, ended_at)
- [X] T015 Create moves table migration (id, game_id, move_number, player_color, algebraic_notation, fen_after_move, time_remaining, timestamp)
- [X] T016 Create game_invitations table migration (id, creator_id, invitation_code, time_control, game_type, created_at, expires_at, status)
- [X] T017 Add database indexes on frequently queried columns (users.email, games.white_player_id, games.black_player_id, moves.game_id)

### Backend Core Infrastructure
- [X] T018 [P] Configure Spring Security for JWT authentication in backend/src/main/java/com/checkmate/chess/config/SecurityConfig.java
- [X] T019 [P] Implement JWT utility class in backend/src/main/java/com/checkmate/chess/security/JwtUtils.java (token generation, validation)
- [X] T020 [P] Configure Spring WebSocket with STOMP in backend/src/main/java/com/checkmate/chess/config/WebSocketConfig.java
- [X] T021 [P] Create global exception handler in backend/src/main/java/com/checkmate/chess/exception/GlobalExceptionHandler.java
- [X] T022 [P] Create base response DTOs (SuccessResponse, ErrorResponse) in backend/src/main/java/com/checkmate/chess/dto/
- [X] T023 [P] Configure CORS for frontend-backend communication in SecurityConfig
- [X] T024 [P] Setup logging configuration (Logback) with appropriate log levels

### Frontend Core Infrastructure
- [X] T025 [P] Install and configure chess.js library in frontend (npm install chess.js @types/chess.js)
- [X] T026 [P] Install and configure react-chessboard in frontend (npm install react-chessboard)
- [X] T027 [P] Create React Context for authentication state in frontend/src/context/AuthContext.tsx
- [X] T028 [P] Create React Context for WebSocket connection in frontend/src/context/WebSocketContext.tsx
- [X] T029 [P] Create API client utility in frontend/src/api/client.ts (axios with JWT interceptors)
- [X] T030 [P] Create WebSocket client utility in frontend/src/api/websocket.ts (STOMP client)
- [X] T031 [P] Create routing configuration in frontend/src/App.tsx (React Router)
- [X] T032 [P] Create base layout components (Header, Footer, Navigation) in frontend/src/components/layout/

### Performance & Monitoring
- [X] T033 [P] Configure performance monitoring (Spring Actuator endpoints) in backend
- [X] T034 [P] Setup frontend performance budgets in package.json and build config
- [X] T035 [P] Configure database connection pooling (HikariCP) with appropriate settings

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Guest Quick Play (Priority: P1) 🎯 MVP

**Goal**: Visitors can immediately play chess against computer without registration

**Independent Test**: Open app → Click "Play as Guest" → Make moves on board → Complete game

### Backend Tests for US1 (TDD - Write First)

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [X] T036 [P] [US1] Unit tests for Game entity validation in backend/src/test/java/com/checkmate/chess/model/GameTest.java
- [X] T037 [P] [US1] Unit tests for chess move validation wrapper in backend/src/test/java/com/checkmate/chess/service/ChessRulesServiceTest.java (100% coverage required)
- [X] T038 [P] [US1] Unit tests for guest user creation in backend/src/test/java/com/checkmate/chess/service/GuestServiceTest.java
- [X] T039 [P] [US1] Integration test for create guest game endpoint in backend/src/test/java/com/checkmate/chess/controller/GameControllerTest.java
- [X] T040 [P] [US1] Integration test for WebSocket move handling in backend/src/test/java/com/checkmate/chess/websocket/GameWebSocketHandlerTest.java
- [X] T041 [P] [US1] E2E test for complete guest game flow using Playwright

### Frontend Tests for US1 (TDD - Write First)

- [X] T042 [P] [US1] Unit tests for ChessBoard component in frontend/src/__tests__/components/ChessBoard.test.tsx
- [X] T043 [P] [US1] Unit tests for chess move validation using chess.js in frontend/src/__tests__/utils/chessValidation.test.ts
- [X] T044 [P] [US1] Integration test for guest game creation flow in frontend/src/__tests__/integration/guestGame.test.tsx

### Backend Implementation for US1

- [X] T045 [P] [US1] Create User entity in backend/src/main/java/com/checkmate/chess/model/User.java (verify Single Responsibility)
- [X] T046 [P] [US1] Create Game entity in backend/src/main/java/com/checkmate/chess/model/Game.java (verify Single Responsibility)
- [X] T047 [P] [US1] Create Move entity in backend/src/main/java/com/checkmate/chess/model/Move.java
- [X] T048 [P] [US1] Create UserRepository interface in backend/src/main/java/com/checkmate/chess/repository/UserRepository.java
- [X] T049 [P] [US1] Create GameRepository interface in backend/src/main/java/com/checkmate/chess/repository/GameRepository.java
- [X] T050 [P] [US1] Create MoveRepository interface in backend/src/main/java/com/checkmate/chess/repository/MoveRepository.java
- [X] T051 [US1] Implement GuestService in backend/src/main/java/com/checkmate/chess/service/GuestService.java (create temporary guest users)
- [X] T052 [US1] Implement ChessRulesService in backend/src/main/java/com/checkmate/chess/service/ChessRulesService.java (wrap chess.js equivalent - use chess4j or similar Java library, functions <20 lines)
- [X] T053 [US1] Implement GameService in backend/src/main/java/com/checkmate/chess/service/GameService.java (create game, validate moves, detect game end, verify SOLID principles)
- [X] T054 [US1] Implement MoveService in backend/src/main/java/com/checkmate/chess/service/MoveService.java (persist moves, validate legality)
- [X] T055 [US1] Create GameController REST endpoints in backend/src/main/java/com/checkmate/chess/controller/GameController.java (POST /api/games/guest, GET /api/games/{id})
- [X] T056 [US1] Create GameWebSocketHandler in backend/src/main/java/com/checkmate/chess/websocket/GameWebSocketHandler.java (handle /topic/game/{gameId}/moves)
- [X] T057 [US1] Implement move synchronization logic in GameWebSocketHandler (broadcast moves to both players)
- [X] T058 [US1] Implement checkmate/stalemate detection in GameService using chess rules library
- [X] T059 [US1] Implement resignation logic in GameService (update game status, notify via WebSocket)
- [X] T060 [US1] Add comprehensive error handling with user-friendly messages in all services
- [X] T061 [US1] Add logging for game creation, moves, and game end events

### Frontend Implementation for US1

- [X] T062 [P] [US1] Create GuestLandingPage component in frontend/src/pages/GuestLandingPage.tsx (with "Play as Guest" button)
- [X] T063 [P] [US1] Create ChessBoard component in frontend/src/components/game/ChessBoard.tsx (using react-chessboard, handles piece selection and legal moves highlighting)
- [X] T064 [P] [US1] Create MoveList component in frontend/src/components/game/MoveList.tsx (displays move history in algebraic notation)
- [X] T065 [P] [US1] Create GameEndModal component in frontend/src/components/game/GameEndModal.tsx (shows winner/draw with game summary)
- [X] T066 [US1] Create chess game state hook in frontend/src/hooks/useChessGame.ts (manages local game state with chess.js validation)
- [X] T067 [US1] Create WebSocket game connection hook in frontend/src/hooks/useGameWebSocket.ts (connects to /topic/game/{gameId}/moves)
- [X] T068 [US1] Implement move handling logic in useChessGame (validate locally, send via WebSocket, update on opponent move)
- [X] T069 [US1] Implement checkmate/stalemate detection on frontend using chess.js
- [X] T070 [US1] Implement resignation button and logic in ChessBoard component
- [X] T071 [US1] Create GamePage component in frontend/src/pages/GamePage.tsx (integrates ChessBoard, MoveList, resignation)
- [X] T072 [US1] Implement guest game creation API call in frontend/src/api/gameApi.ts
- [X] T073 [US1] Add smooth piece movement animations (CSS transitions, 60fps target)
- [X] T074 [US1] Add sound effects for moves, captures, check (using HTML5 Audio API)
- [X] T075 [US1] Implement board flip functionality (button to flip orientation)
- [X] T076 [US1] Add loading states for game creation and move processing
- [X] T077 [US1] Add error messages for connection failures and invalid moves (clear, actionable text)

### Accessibility for US1

- [X] T078 [US1] Add keyboard navigation for chess board (arrow keys to select squares, Enter to move)
- [X] T079 [US1] Add ARIA labels to all interactive elements (pieces, squares, buttons)
- [X] T080 [US1] Add screen reader announcements for moves and game state changes
- [X] T081 [US1] Verify color contrast meets WCAG 2.1 AA standards (board colors, text)
- [X] T082 [US1] Add focus indicators for keyboard navigation

### Performance Testing for US1

- [X] T083 [US1] Performance test WebSocket move latency (verify <100ms p95)
- [X] T084 [US1] Performance test frontend page load (verify <2s First Contentful Paint)
- [X] T085 [US1] Load test backend with 50 concurrent games (simulate 100 players making moves)

### Constitution Verification for US1

- [X] T086 [US1] Code review checklist: SOLID principles, DRY (no duplicate chess logic), functions <20 lines
- [X] T087 [US1] Verify test coverage ≥80% overall, 100% for chess validation logic
- [X] T088 [US1] Verify Test Pyramid ratio (70% unit, 20% integration, 10% e2e)
- [X] T089 [US1] Run ESLint/Checkstyle and fix all warnings (zero warnings required)
- [X] T090 [US1] Accessibility audit with Lighthouse (score ≥90 required)

**Checkpoint**: At this point, guests can play complete chess games. This is a deployable MVP.

---

## Phase 4: User Story 2 - User Account Management (Priority: P2)

**Goal**: Users can register, log in, and view their game history and statistics

**Independent Test**: Register account → Log out → Log in → View profile with stats

### Backend Tests for US2 (TDD - Write First)

- [x] T091 [P] [US2] Unit tests for User registration validation in backend/src/test/java/com/checkmate/chess/service/UserServiceTest.java
- [x] T092 [P] [US2] Unit tests for password hashing (BCrypt) in UserService
- [ ] T093 [P] [US2] Unit tests for JWT token generation and validation
- [ ] T094 [P] [US2] Integration test for registration endpoint (POST /api/auth/register)
- [ ] T095 [P] [US2] Integration test for login endpoint (POST /api/auth/login)
- [ ] T096 [P] [US2] Integration test for profile endpoint (GET /api/users/me)
- [ ] T097 [P] [US2] Integration test for game history endpoint (GET /api/users/me/games)

### Frontend Tests for US2 (TDD - Write First)

- [ ] T098 [P] [US2] Unit tests for RegistrationForm component in frontend/src/__tests__/components/RegistrationForm.test.tsx
- [ ] T099 [P] [US2] Unit tests for LoginForm component in frontend/src/__tests__/components/LoginForm.test.tsx
- [ ] T100 [P] [US2] Integration test for registration flow in frontend/src/__tests__/integration/auth.test.tsx
- [ ] T101 [P] [US2] Integration test for login flow with JWT storage

### Backend Implementation for US2

- [x] T102 [P] [US2] Implement UserService in backend/src/main/java/com/checkmate/chess/service/UserService.java (registration, login, profile retrieval)
- [x] T103 [P] [US2] Implement password hashing with BCrypt (10 rounds minimum per NFR-031)
- [x] T104 [P] [US2] Implement AuthController in backend/src/main/java/com/checkmate/chess/controller/AuthController.java (POST /api/auth/register, POST /api/auth/login)
- [x] T105 [P] [US2] Implement UserController in backend/src/main/java/com/checkmate/chess/controller/UserController.java (GET /api/users/me, GET /api/users/me/games)
- [x] T106 [US2] Add email validation (format check, duplicate check)
- [x] T107 [US2] Add password validation (min 8 chars, at least one number per FR-002)
- [x] T108 [US2] Implement game history retrieval with pagination (page size 20)
- [x] T109 [US2] Calculate and return user statistics (total games, wins, losses, draws, win rate)
- [ ] T110 [US2] Add JWT token expiration (24 hours per NFR-032)
- [ ] T111 [US2] Implement JWT refresh logic (optional for MVP)
- [ ] T112 [US2] Add rate limiting on auth endpoints (100 requests/minute per IP per NFR-036)
- [ ] T113 [US2] Add SQL injection prevention (use parameterized queries, JPA handles this)
- [ ] T114 [US2] Add XSS prevention (sanitize user inputs, escape outputs)

### Frontend Implementation for US2

- [ ] T115 [P] [US2] Create RegistrationPage component in frontend/src/pages/RegistrationPage.tsx
- [ ] T116 [P] [US2] Create LoginPage component in frontend/src/pages/LoginPage.tsx
- [ ] T117 [P] [US2] Create ProfilePage component in frontend/src/pages/ProfilePage.tsx (displays stats and game history)
- [ ] T118 [P] [US2] Create RegistrationForm component in frontend/src/components/auth/RegistrationForm.tsx (email, password, confirm password)
- [ ] T119 [P] [US2] Create LoginForm component in frontend/src/components/auth/LoginForm.tsx (email, password)
- [ ] T120 [P] [US2] Create UserStatsCard component in frontend/src/components/profile/UserStatsCard.tsx (displays wins/losses/draws)
- [ ] T121 [P] [US2] Create GameHistoryList component in frontend/src/components/profile/GameHistoryList.tsx (paginated list)
- [ ] T122 [US2] Implement authentication API calls in frontend/src/api/authApi.ts (register, login, logout)
- [ ] T123 [US2] Implement JWT storage in localStorage with expiration handling
- [ ] T124 [US2] Update AuthContext to persist auth state across page refreshes
- [ ] T125 [US2] Add protected route wrapper for authenticated pages
- [ ] T126 [US2] Implement form validation on frontend (email format, password requirements)
- [ ] T127 [US2] Add client-side validation error messages (clear, actionable)
- [ ] T128 [US2] Implement logout functionality (clear JWT, reset auth state)
- [ ] T129 [US2] Add "Remember me" checkbox for login (optional)
- [ ] T130 [US2] Update navigation to show user email when logged in
- [ ] T131 [US2] Add loading states for registration and login forms

### Integration with US1

- [ ] T132 [US2] Update GameService to associate games with registered users (not guest IDs)
- [ ] T133 [US2] Persist game history for registered users automatically after game end
- [ ] T134 [US2] Update GamePage to show user information if logged in

### Constitution Verification for US2

- [ ] T135 [US2] Code review: Verify password hashing, JWT security, input validation
- [ ] T136 [US2] Verify test coverage ≥80% for auth and user services
- [ ] T137 [US2] Security audit: Check for SQL injection, XSS, CSRF vulnerabilities
- [ ] T138 [US2] Run static analysis and fix all warnings

**Checkpoint**: At this point, users can create accounts and track their game history. US1 and US2 work independently.

---

## Phase 5: User Story 3 - Real-time Ranked Games with Ratings (Priority: P3)

**Goal**: Registered users can play rated games with time controls that update ELO ratings

**Independent Test**: Log in → Select "Ranked Game" → Match with opponent → Play with clock → Rating updates

### Backend Tests for US3 (TDD - Write First)

- [ ] T139 [P] [US3] Unit tests for ELO rating calculation in backend/src/test/java/com/checkmate/chess/service/RatingServiceTest.java (100% coverage required)
- [ ] T140 [P] [US3] Unit tests for matchmaking algorithm in backend/src/test/java/com/checkmate/chess/service/MatchmakingServiceTest.java
- [ ] T141 [P] [US3] Unit tests for chess clock logic in backend/src/test/java/com/checkmate/chess/service/ChessClockServiceTest.java
- [ ] T142 [P] [US3] Integration test for matchmaking queue (POST /api/matchmaking/queue)
- [ ] T143 [P] [US3] Integration test for chess clock WebSocket events
- [ ] T144 [P] [US3] Integration test for rating updates after game completion

### Frontend Tests for US3 (TDD - Write First)

- [ ] T145 [P] [US3] Unit tests for ChessClock component in frontend/src/__tests__/components/ChessClock.test.tsx
- [ ] T146 [P] [US3] Unit tests for TimeControlSelector component
- [ ] T147 [P] [US3] Integration test for matchmaking flow

### Backend Implementation for US3

- [ ] T148 Create Rating entity in backend/src/main/java/com/checkmate/chess/model/Rating.java (tracks rating history)
- [ ] T149 Create MatchmakingQueue entity in backend/src/main/java/com/checkmate/chess/model/MatchmakingQueue.java
- [ ] T150 Create GameClock entity in backend/src/main/java/com/checkmate/chess/model/GameClock.java
- [ ] T151 [P] [US3] Create RatingRepository in backend/src/main/java/com/checkmate/chess/repository/RatingRepository.java
- [ ] T152 [P] [US3] Create MatchmakingQueueRepository
- [ ] T153 [P] [US3] Implement RatingService in backend/src/main/java/com/checkmate/chess/service/RatingService.java (ELO calculation algorithm, verify functions <20 lines)
- [ ] T154 [US3] Implement MatchmakingService in backend/src/main/java/com/checkmate/chess/service/MatchmakingService.java (queue management, pairing within ±200 ELO)
- [ ] T155 [US3] Implement ChessClockService in backend/src/main/java/com/checkmate/chess/service/ChessClockService.java (track time, handle increment/delay)
- [ ] T156 [US3] Create MatchmakingController (POST /api/matchmaking/queue, DELETE /api/matchmaking/queue/{userId})
- [ ] T157 [US3] Implement matchmaking pairing scheduler (runs every 2 seconds, pairs waiting users)
- [ ] T158 [US3] Update GameWebSocketHandler to send clock updates (every second via WebSocket)
- [ ] T159 [US3] Implement timeout detection in ChessClockService (game ends when time reaches zero)
- [ ] T160 [US3] Update GameService to update ratings after ranked game completion
- [ ] T161 [US3] Add time control validation (bullet: <3min, blitz: 3-10min, rapid: 10-30min, classical: 30min+)
- [ ] T162 [US3] Implement queue cancellation logic in MatchmakingService
- [ ] T163 [US3] Add matchmaking timeout (remove from queue after 5 minutes)

### Frontend Implementation for US3

- [ ] T164 [P] [US3] Create RankedGamePage component in frontend/src/pages/RankedGamePage.tsx
- [ ] T165 [P] [US3] Create TimeControlSelector component in frontend/src/components/matchmaking/TimeControlSelector.tsx (buttons for bullet, blitz, rapid, classical)
- [ ] T166 [P] [US3] Create MatchmakingModal component in frontend/src/components/matchmaking/MatchmakingModal.tsx (searching animation, cancel button)
- [ ] T167 [P] [US3] Create ChessClock component in frontend/src/components/game/ChessClock.tsx (displays time remaining, increments)
- [ ] T168 [US3] Create matchmaking API calls in frontend/src/api/matchmakingApi.ts (join queue, cancel queue)
- [ ] T169 [US3] Create WebSocket subscription for matchmaking events in useGameWebSocket hook
- [ ] T170 [US3] Implement clock display logic (format mm:ss, show increment)
- [ ] T171 [US3] Implement clock update handling via WebSocket (sync from server)
- [ ] T172 [US3] Add visual time pressure indicator (clock turns red when <10 seconds)
- [ ] T173 [US3] Add timeout notification (modal showing "Time's up!")
- [ ] T174 [US3] Display rating change after game completion (+15, -12, etc.)
- [ ] T175 [US3] Update ProfilePage to show current ELO rating prominently
- [ ] T176 [US3] Add matchmaking timeout notification (after 5 minutes)

### Integration with US1 and US2

- [ ] T177 [US3] Update Game entity to include time_control and game_type (casual/ranked)
- [ ] T178 [US3] Ensure rated games only available to logged-in users (authentication check)

### Constitution Verification for US3

- [ ] T179 [US3] Code review: Verify ELO algorithm correctness, clock accuracy
- [ ] T180 [US3] Verify 100% test coverage for ELO calculation (critical business logic per NFR-007)
- [ ] T181 [US3] Performance test: Verify clock updates <100ms latency
- [ ] T182 [US3] Load test: 100 concurrent ranked games with clocks
- [ ] T183 [US3] Run static analysis and fix warnings

**Checkpoint**: Users can play competitive ranked games with ratings. All three core stories (P1, P2, P3) are complete.

---

## Phase 6: User Story 4 - Play Against Computer (Priority: P4)

**Goal**: Users can practice against AI opponent with adjustable difficulty

**Independent Test**: Select "Play vs Computer" → Choose difficulty → Play game → Computer responds within 1 second

### Backend Tests for US4 (TDD - Write First)

- [ ] T184 [P] [US4] Unit tests for Stockfish integration in backend/src/test/java/com/checkmate/chess/service/StockfishServiceTest.java
- [ ] T185 [P] [US4] Unit tests for difficulty level configuration
- [ ] T186 [P] [US4] Integration test for computer game creation endpoint

### Backend Implementation for US4

- [ ] T187 [P] [US4] Research Stockfish integration options (Java UCI library or REST API)
- [ ] T188 [US4] Implement StockfishService in backend/src/main/java/com/checkmate/chess/service/StockfishService.java (send position, get move, configure skill level)
- [ ] T189 [US4] Add computer opponent endpoints to GameController (POST /api/games/computer)
- [ ] T190 [US4] Implement difficulty level mapping (beginner: skill 1-5, intermediate: 10-15, advanced: 18-20)
- [ ] T191 [US4] Implement computer move generation on player move (async, respond via WebSocket)
- [ ] T192 [US4] Add 1-second timeout for computer moves (per NFR-017)
- [ ] T193 [US4] Handle Stockfish errors gracefully (restart engine, notify user)

### Frontend Implementation for US4

- [ ] T194 [P] [US4] Create ComputerGamePage component in frontend/src/pages/ComputerGamePage.tsx
- [ ] T195 [P] [US4] Create DifficultySelector component in frontend/src/components/computer/DifficultySelector.tsx (beginner, intermediate, advanced)
- [ ] T196 [US4] Create computer game API calls in frontend/src/api/gameApi.ts
- [ ] T197 [US4] Update useChessGame hook to handle computer responses via WebSocket
- [ ] T198 [US4] Add "New Game" button on game end for quick restart against computer
- [ ] T199 [US4] Add visual indicator when computer is "thinking"

### Constitution Verification for US4

- [ ] T200 [US4] Verify computer response time <1 second (99% of cases per SC-011)
- [ ] T201 [US4] Verify test coverage ≥80% for Stockfish integration
- [ ] T202 [US4] Code review and static analysis

**Checkpoint**: Users can practice against computer. US1-US4 all independently functional.

---

## Phase 7: User Story 5 - Private Games with Friends (Priority: P5)

**Goal**: Users can create private game links to play with specific friends

**Independent Test**: Create private game → Copy link → Friend opens link → Game starts

### Backend Tests for US5 (TDD - Write First)

- [ ] T203 [P] [US5] Unit tests for invitation code generation in backend/src/test/java/com/checkmate/chess/service/InvitationServiceTest.java
- [ ] T204 [P] [US5] Integration test for create invitation endpoint
- [ ] T205 [P] [US5] Integration test for join via invitation code

### Backend Implementation for US5

- [ ] T206 Create GameInvitation entity (already in schema from T016)
- [ ] T207 [P] [US5] Create GameInvitationRepository
- [ ] T208 [US5] Implement InvitationService (create unique codes, validate, expire after 10 minutes)
- [ ] T209 [US5] Add invitation endpoints to GameController (POST /api/games/invite, POST /api/games/join/{code})
- [ ] T210 [US5] Implement invitation expiration scheduler (runs every minute, expires old invitations)
- [ ] T211 [US5] Validate invitation: check expiry, check not already joined, check capacity

### Frontend Implementation for US5

- [ ] T212 [P] [US5] Create PrivateGamePage component in frontend/src/pages/PrivateGamePage.tsx
- [ ] T213 [P] [US5] Create InvitationModal component in frontend/src/components/invite/InvitationModal.tsx (shows link, copy button)
- [ ] T214 [US5] Implement invitation creation API call
- [ ] T215 [US5] Implement "Copy Link" functionality with clipboard API
- [ ] T216 [US5] Implement join via invitation code (extract from URL params)
- [ ] T217 [US5] Add "Waiting for opponent" state while friend hasn't joined
- [ ] T218 [US5] Add invitation expired notification

### Constitution Verification for US5

- [ ] T219 [US5] Verify test coverage ≥80%
- [ ] T220 [US5] Code review and static analysis

**Checkpoint**: Users can play private games with friends. US1-US5 all functional.

---

## Phase 8: User Story 6 - Game Analysis and History (Priority: P6)

**Goal**: Users can review completed games move-by-move and export to PGN

**Independent Test**: View game history → Select past game → Replay moves → Export PGN

### Backend Tests for US6 (TDD - Write First)

- [ ] T221 [P] [US6] Unit tests for PGN generation in backend/src/test/java/com/checkmate/chess/service/PgnServiceTest.java
- [ ] T222 [P] [US6] Integration test for game replay endpoint
- [ ] T223 [P] [US6] Integration test for PGN export endpoint

### Backend Implementation for US6

- [ ] T224 [US6] Implement PgnService (generate PGN format with headers and moves)
- [ ] T225 [US6] Add game analysis endpoints (GET /api/games/{id}/moves, GET /api/games/{id}/pgn)
- [ ] T226 [US6] Implement position evaluation (basic Stockfish evaluation at depth 15)
- [ ] T227 [US6] Store evaluation scores with moves during analysis

### Frontend Implementation for US6

- [ ] T228 [P] [US6] Create GameReplayPage component in frontend/src/pages/GameReplayPage.tsx
- [ ] T229 [P] [US6] Create ReplayControls component in frontend/src/components/replay/ReplayControls.tsx (prev, next, jump to move)
- [ ] T230 [P] [US6] Create EvaluationBar component in frontend/src/components/replay/EvaluationBar.tsx (shows position advantage)
- [ ] T231 [US6] Implement game replay API calls
- [ ] T232 [US6] Implement move navigation (forward, backward, jump to specific move)
- [ ] T233 [US6] Implement PGN download functionality
- [ ] T234 [US6] Display evaluation scores next to moves in move list
- [ ] T235 [US6] Add "Analyze" button on game history list

### Constitution Verification for US6

- [ ] T236 [US6] Verify test coverage ≥80%
- [ ] T237 [US6] Code review and static analysis

**Checkpoint**: Users can analyze completed games. US1-US6 all functional.

---

## Phase 9: User Story 7 - Draw Offers and Game Actions (Priority: P7)

**Goal**: Players can offer/accept/decline draws and claim draws by rules

**Independent Test**: During game → Offer draw → Opponent accepts/declines → Draw claimed by repetition

### Backend Tests for US7 (TDD - Write First)

- [ ] T238 [P] [US7] Unit tests for threefold repetition detection in ChessRulesService
- [ ] T239 [P] [US7] Unit tests for fifty-move rule detection
- [ ] T240 [P] [US7] Integration test for draw offer workflow

### Backend Implementation for US7

- [ ] T241 [US7] Implement draw offer logic in GameService (one pending offer per player)
- [ ] T242 [US7] Implement threefold repetition detection in ChessRulesService
- [ ] T243 [US7] Implement fifty-move rule detection in ChessRulesService
- [ ] T244 [US7] Add draw action endpoints (POST /api/games/{id}/draw/offer, POST /api/games/{id}/draw/accept, POST /api/games/{id}/draw/decline, POST /api/games/{id}/draw/claim)
- [ ] T245 [US7] Broadcast draw offers and responses via WebSocket

### Frontend Implementation for US7

- [ ] T246 [P] [US7] Create DrawOfferModal component in frontend/src/components/game/DrawOfferModal.tsx
- [ ] T247 [US7] Add "Offer Draw" button to GamePage
- [ ] T248 [US7] Implement draw offer notification (modal with accept/decline buttons)
- [ ] T249 [US7] Implement draw claim buttons (when repetition/fifty-move rule detected)
- [ ] T250 [US7] Handle draw offer responses via WebSocket

### Constitution Verification for US7

- [ ] T251 [US7] Verify 100% test coverage for repetition and fifty-move detection
- [ ] T252 [US7] Code review and static analysis

**Checkpoint**: All chess rules complete. US1-US7 fully implemented.

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

### UI/UX Enhancements

- [ ] T253 [P] Implement light/dark theme toggle in frontend/src/context/ThemeContext.tsx
- [ ] T254 [P] Add theme switcher button to header
- [ ] T255 [P] Create consistent color palette for both themes
- [ ] T256 [P] Add premove capability (queue next move before opponent moves)
- [ ] T257 [P] Improve loading indicators across all pages (skeleton screens)
- [ ] T258 [P] Add toast notifications for errors and success messages (react-hot-toast)
- [ ] T259 [P] Optimize piece movement animations for mobile touch
- [ ] T260 [P] Add haptic feedback for mobile moves (if supported)

### Performance Optimization

- [ ] T261 Implement Redis caching for active game states (optional, add if performance demands)
- [ ] T262 Add database query optimization (EXPLAIN ANALYZE on slow queries)
- [ ] T263 Implement CDN for static assets (frontend build artifacts)
- [ ] T264 Add lazy loading for game history (infinite scroll)
- [ ] T265 Optimize bundle size (code splitting, tree shaking)
- [ ] T266 Add service worker for offline board view (optional PWA feature)

### Security Hardening

- [ ] T267 Implement CSRF token validation for state-changing operations
- [ ] T268 Add rate limiting on WebSocket connections
- [ ] T269 Implement secure WebSocket (WSS) configuration for production
- [ ] T270 Add security headers (HSTS, CSP, X-Frame-Options)
- [ ] T271 Conduct security audit (OWASP Top 10 checklist)
- [ ] T272 Add input sanitization on all user inputs

### Documentation

- [ ] T273 Create API documentation with Swagger/OpenAPI spec
- [ ] T274 Create WebSocket event documentation
- [ ] T275 Write deployment guide in docs/deployment.md
- [ ] T276 Write local development setup guide in README.md
- [ ] T277 Document database schema with ER diagram
- [ ] T278 Create quickstart guide for contributors

### Final Validation

- [ ] T279 Run full test suite and verify 80%+ coverage
- [ ] T280 Run E2E tests for all 7 user stories
- [ ] T281 Performance testing: 1000 concurrent users (per NFR-025)
- [ ] T282 Accessibility audit: Verify WCAG 2.1 AA compliance (Lighthouse ≥90)
- [ ] T283 Cross-browser testing (Chrome, Firefox, Safari, Edge)
- [ ] T284 Mobile responsiveness testing (iOS Safari, Android Chrome)
- [ ] T285 Load testing: Database queries <50ms (per NFR-015)
- [ ] T286 WebSocket latency testing: <100ms move sync (per NFR-012)
- [ ] T287 Security testing: Penetration test, vulnerability scan
- [ ] T288 Code quality audit: Run all static analysis tools, fix all warnings
- [ ] T289 Constitution compliance final check: All 5 principles verified
- [ ] T290 Create production deployment checklist

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1 (Setup)
    ↓
Phase 2 (Foundational) ← BLOCKS all user stories
    ↓
    ├─→ Phase 3 (US1 - Guest Play) 🎯 MVP ← Deploy here for early feedback
    ├─→ Phase 4 (US2 - Accounts) ← Required for US3
    ├─→ Phase 5 (US3 - Ranked Games) ← Requires US2
    ├─→ Phase 6 (US4 - Computer) ← Independent
    ├─→ Phase 7 (US5 - Private Games) ← Independent
    ├─→ Phase 8 (US6 - Analysis) ← Independent
    └─→ Phase 9 (US7 - Draw Actions) ← Independent
    ↓
Phase 10 (Polish) ← After all desired features
```

### Critical Path for MVP (Fastest Route to Value)

1. **Phase 1**: Setup (1-2 days)
2. **Phase 2**: Foundational (3-5 days)
3. **Phase 3**: US1 - Guest Quick Play (5-7 days) ← **Deploy MVP here**

**Total MVP Timeline**: 9-14 days (2 weeks)

### Recommended Sequence for Full Feature Set

**Sprint 1** (2 weeks): MVP
- Phase 1: Setup
- Phase 2: Foundational
- Phase 3: US1 - Guest Quick Play
- **Deploy to staging, gather feedback**

**Sprint 2** (2 weeks): User Accounts & Ratings
- Phase 4: US2 - User Account Management
- Phase 5: US3 - Ranked Games with Ratings
- **Deploy to production**

**Sprint 3** (2 weeks): Additional Features
- Phase 6: US4 - Play Against Computer
- Phase 7: US5 - Private Games with Friends
- **Deploy to production**

**Sprint 4** (1 week): Analysis & Polish
- Phase 8: US6 - Game Analysis and History
- Phase 9: US7 - Draw Offers and Game Actions
- Phase 10: Polish & Cross-Cutting Concerns (partial)
- **Deploy to production**

**Sprint 5** (1 week): Optimization & Hardening
- Phase 10: Polish & Cross-Cutting Concerns (complete)
- Performance optimization
- Security hardening
- Final testing and validation
- **Production ready**

### Parallelization Opportunities

If multiple developers available:

**After Phase 2 completes:**
- Dev 1: US1 (Guest Play) - Priority 1
- Dev 2: US2 (Accounts) - Priority 2, can work in parallel on different files
- Dev 3: Infrastructure (CI/CD, monitoring) - Parallel to US work

**After US2 completes:**
- Dev 1: US3 (Ranked Games) - Depends on US2
- Dev 2: US4 (Computer) - Independent
- Dev 3: US5 (Private Games) - Independent

### Testing Strategy Per Phase

**TDD Approach** (per constitution):
1. Write tests FIRST (they should FAIL)
2. Implement minimal code to pass tests
3. Refactor while keeping tests green
4. Verify coverage targets before moving on

**Test Execution Order:**
1. Unit tests (70% of suite, <10ms each)
2. Integration tests (20% of suite)
3. E2E tests (10% of suite, run last)

**Coverage Gates** (per NFR-006, NFR-007):
- Overall: ≥80% line coverage
- Chess logic: 100% coverage (critical business logic)
- Authentication: ≥90% coverage (security critical)
- All other services: ≥80% coverage

---

## Task Count Summary

- **Phase 1 (Setup)**: 10 tasks
- **Phase 2 (Foundational)**: 25 tasks
- **Phase 3 (US1 - MVP)**: 55 tasks
- **Phase 4 (US2)**: 47 tasks
- **Phase 5 (US3)**: 45 tasks
- **Phase 6 (US4)**: 18 tasks
- **Phase 7 (US5)**: 15 tasks
- **Phase 8 (US6)**: 17 tasks
- **Phase 9 (US7)**: 15 tasks
- **Phase 10 (Polish)**: 38 tasks

**Total**: 290 tasks

**MVP (Phases 1-3)**: 90 tasks ← Focus here first
**Full Feature Set**: 290 tasks

---

## Success Criteria Verification

After all phases complete, verify these success criteria from spec.md:

- [ ] **SC-001**: Users complete registration and first game within 3 minutes ✓
- [ ] **SC-002**: System handles 1000 concurrent games with <100ms latency ✓
- [ ] **SC-003**: 95% of moves synchronized within 100ms ✓
- [ ] **SC-004**: 90% of users complete at least one full game on first visit ✓
- [ ] **SC-005**: Page load <2 seconds for 95% of users globally ✓
- [ ] **SC-006**: Zero false positives/negatives in move validation ✓
- [ ] **SC-007**: All checkmate/stalemate/timeout detected with 100% accuracy ✓
- [ ] **SC-008**: 80% retention after 3+ games within 7 days ✓
- [ ] **SC-009**: ELO ratings converge after 20 games ✓
- [ ] **SC-010**: 99.5% uptime during peak hours ✓
- [ ] **SC-011**: Computer responds within 1 second (99% of cases) ✓
- [ ] **SC-012**: Lighthouse accessibility score ≥90 ✓
- [ ] **SC-013**: Mobile users complete games with <5% error rate ✓
- [ ] **SC-014**: Database queries <50ms (95th percentile) ✓
- [ ] **SC-015**: Zero games lost to server crashes/data corruption ✓

---

## Notes

- All tasks follow conventional commits format: `type(scope): description`
- Constitution principles (SOLID, DRY, KISS, YAGNI) must be verified at each checkpoint
- Tests written FIRST (TDD) per constitution
- Performance budgets enforced at each phase
- Security considerations integrated throughout, not bolted on
- Accessibility (WCAG 2.1 AA) built-in from start, not retrofitted
- Each user story is independently deployable MVP increment
- Tasks include exact file paths for clarity
- [P] indicates tasks that can run in parallel
- [US#] indicates which user story the task belongs to

**Remember**: The MVP (Phase 3) delivers a complete, playable chess game. Deploy early, gather feedback, iterate based on real user behavior.

