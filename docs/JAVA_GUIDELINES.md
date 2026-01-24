# Java Code Guidelines - Checkmate Chess

## Java Version
- **Java 25** with latest features enabled
- Gradle 9.2+ with Groovy DSL

## Core Principles

### 1. Immutability by Default
- Use `final` for ALL method parameters
- Use `final` for ALL class declarations
- Use `final` for ALL fields where possible
- Use `final` for ALL local variables where beneficial

### 2. Lombok Usage
**Required Annotations:**
- `@RequiredArgsConstructor` - For dependency injection
- `@Getter/@Setter` - For entity fields
- `@NoArgsConstructor` - For JPA entities
- Never write manual getters/setters/constructors

**Example:**
```java
@Service
@RequiredArgsConstructor
public final class GameService {
  private final GameRepository gameRepository;
  // No constructor needed!
}
```

### 3. Java 25 Features
- **var** - Use for local variable type inference
- **final** - Use everywhere for immutability
- **Records** - Use for DTOs
- **Pattern matching** - Use when available
- **Text blocks** - Use for multi-line strings

### 4. No Warnings Policy
- Code MUST compile without warnings
- No unused imports
- No unused variables
- No raw types
- No unchecked operations

### 5. Clean Code Standards

#### Method Parameters
```java
// ✅ CORRECT
public void processMove(final UUID gameId, final String notation) {
    final var game = findGame(gameId);
    // ...
}

// ❌ WRONG
public void processMove(UUID gameId, String notation) {
    Game game = findGame(gameId);
    // ...
}
```

#### Class Declarations
```java
// ✅ CORRECT
@Service
@RequiredArgsConstructor
public final class UserService {
    private final UserRepository userRepository;
}

// ❌ WRONG
@Service
public class UserService {
    private UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

#### Local Variables
```java
// ✅ CORRECT
public User createUser(final String email) {
    final var username = extractUsername(email);
    final var user = new User(email, username);
    return userRepository.save(user);
}

// ❌ WRONG
public User createUser(String email) {
    String username = extractUsername(email);
    User user = new User(email, username);
    return userRepository.save(user);
}
```

### 6. Entity Best Practices
```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor  // Required for JPA
public final class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // No getters/setters needed!
    // Lombok generates them
}
```

### 7. Service Layer Pattern
```java
@Service
@RequiredArgsConstructor  // Generates constructor with final fields
public final class GameService {
    private final GameRepository gameRepository;  // Injected automatically
    private final MoveService moveService;         // Injected automatically
    
    @Transactional
    public Game createGame(final UUID whitePlayerId, final UUID blackPlayerId) {
        final var white = findPlayer(whitePlayerId);
        final var black = findPlayer(blackPlayerId);
        final var game = new Game(white, black, "STANDARD");
        return gameRepository.save(game);
    }
}
```

### 8. Controller Pattern
```java
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor  // Dependency injection
public final class GameController {
    private final GameService gameService;
    
    @PostMapping
    public ResponseEntity<GameResponse> createGame(
            @RequestBody final CreateGameRequest request) {
        final var game = gameService.createGame(request.whiteId(), request.blackId());
        final var response = GameResponse.from(game);
        return ResponseEntity.ok(response);
    }
}
```

### 9. Exception Handling
```java
@RestControllerAdvice
public final class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            final ResourceNotFoundException ex) {
        final var response = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Resource not found",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
```

### 10. Configuration Classes
```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) 
            throws Exception {
        // ...configuration
        return http.build();
    }
}
```

## Code Smells to Avoid

### ❌ DON'T DO THIS:
```java
// No manual constructors with Lombok
public GameService(GameRepository repo) { this.repo = repo; }

// No explicit type when var works
String username = user.getUsername();

// No mutable parameters
public void process(UUID id, String name) { }

// No final on classes (prevents mocking/proxying)
public final class Service { }

// No manual getters/setters
public String getName() { return name; }
public void setName(String name) { this.name = name; }
```

### ✅ DO THIS:
```java
// Lombok constructor
@RequiredArgsConstructor

// Use var
final var username = user.getUsername();

// Final parameters
public void process(final UUID id, final String name) { }

// No final on classes (allows Spring proxying)
public class Service { }

// Lombok getters/setters
@Getter @Setter
```

## Testing Standards
- Use `@MockitoExtension` for unit tests
- Use `@SpringBootTest` for integration tests
- Follow AAA pattern (Arrange-Act-Assert)
- Test names: `shouldDoSomethingWhenCondition()`

## Commit Standards
- Use conventional commits
- Reference issue numbers
- Clear, concise messages
- Group related changes

## Tools & IDE Setup
- Enable "final" suggestions in IDE
- Enable Lombok annotation processing
- Enable Java 25 language level
- Enable all compiler warnings
- Auto-format on save

---

**Remember**: Less code = Less bugs. Use Lombok, use final, use var. Make code immutable by default.

