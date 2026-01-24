# Chess Frontend - React Application

Frontend for the Checkmate chess web application.

## Technology Stack

- **Runtime & Package Manager**: Bun 1.0+ (JavaScript runtime and package manager)
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite 7 (dev server with HMR + production bundler)
- **Chess Logic**: chess.js
- **Board UI**: react-chessboard
- **State Management**: React Context API
- **Styling**: CSS Modules
- **Unit/Integration Testing**: Vitest + React Testing Library
- **E2E Testing**: Playwright
- **Code Quality**: ESLint + Prettier

**Note**: Bun is used as the runtime and package manager (replacing npm/yarn), while Vite provides the dev server with Hot Module Replacement and production bundling. This combination offers the best of both: Bun's speed for package management and Vite's excellent DX for React development.

## Prerequisites

- **Bun**: v1.0+ (https://bun.sh)
- **Playwright Browsers**: Chromium, Firefox, WebKit (installed automatically)

## Getting Started

### 1. Install Dependencies

```bash
bun install
```

### 2. Run Development Server

```bash
bun run dev
```

The application will start on `http://localhost:5173`

### 3. Build for Production

```bash
bun run build
```

Build output will be in the `dist/` directory.

### 4. Preview Production Build

```bash
bun run preview
```

## Project Structure

```
frontend/
├── e2e/                       # Playwright E2E tests
│   └── example.spec.ts
├── public/                    # Static assets
├── src/
│   ├── __tests__/             # Unit and integration tests
│   ├── assets/                # Images, fonts, etc.
│   ├── components/            # React components (coming soon)
│   │   ├── game/              # Game-related components
│   │   ├── layout/            # Layout components
│   │   └── auth/              # Authentication components
│   ├── context/               # React contexts (coming soon)
│   ├── hooks/                 # Custom hooks (coming soon)
│   ├── pages/                 # Page components (coming soon)
│   ├── api/                   # API client utilities (coming soon)
│   ├── test/                  # Test setup and utilities
│   │   └── setup.ts
│   ├── App.tsx                # Root component
│   ├── main.tsx               # Entry point
│   └── index.css              # Global styles
├── .eslintrc.json             # ESLint configuration
├── .prettierrc.json           # Prettier configuration
├── playwright.config.ts       # Playwright configuration
├── vitest.config.ts           # Vitest configuration
├── vite.config.ts             # Vite configuration
├── tsconfig.json              # TypeScript configuration
├── package.json               # Dependencies and scripts
└── README.md                  # This file
```

## Available Scripts

### Development
- `bun run dev` - Start development server with hot reload
- `bun run build` - Build for production
- `bun run preview` - Preview production build locally

### Testing
- `bun test` - Run unit/integration tests in watch mode
- `bun run test:ui` - Run tests with Vitest UI
- `bun run test:coverage` - Run tests with coverage report
- `bun run test:e2e` - Run Playwright E2E tests
- `bun run test:e2e:ui` - Run E2E tests in UI mode
- `bun run test:e2e:debug` - Debug E2E tests
- `bun run test:e2e:report` - View E2E test report

### Code Quality
- `bun run lint` - Run ESLint
- `bun run lint:fix` - Fix ESLint errors automatically
- `bun run format` - Format code with Prettier
- `bun run format:check` - Check code formatting

## Testing

### Unit and Integration Tests (Vitest)

Tests are located in `src/__tests__/` and use Vitest with React Testing Library.

```bash
# Run tests in watch mode
bun test

# Run tests once with coverage
bun run test:coverage
```

Coverage thresholds are set to 80% minimum. Reports are generated in `coverage/` directory.

### End-to-End Tests (Playwright)

E2E tests are located in `e2e/` and test the complete user flows.

```bash
# Run all E2E tests
bun run test:e2e

# Run tests in UI mode (interactive)
bun run test:e2e:ui

# Run tests in debug mode
bun run test:e2e:debug

# View test report
bun run test:e2e:report
```

Playwright tests run against:
- Chromium (Desktop)
- Firefox (Desktop)
- WebKit (Desktop Safari)
- Mobile Chrome (Pixel 5)
- Mobile Safari (iPhone 12)

## Code Quality Standards

### ESLint Rules
- Max function complexity: 10
- Max lines per function: 20
- Max nesting depth: 3
- No unused variables
- No duplicate imports

### Prettier Configuration
- Single quotes
- 2 spaces indentation
- 100 character line width
- Semicolons required
- Trailing commas (ES5)

### TypeScript
- Strict mode enabled
- No implicit any
- Strict null checks

## Development Guidelines

### Component Structure
- Use functional components with hooks
- Keep components small and focused (<20 lines per function)
- Follow single responsibility principle
- Use TypeScript for type safety

### State Management
- Use React Context for global state
- Use local state for component-specific data
- Avoid prop drilling (use context instead)

### Styling
- Use CSS Modules for component styles
- Follow BEM naming convention
- Ensure WCAG 2.1 AA accessibility standards

### Testing Best Practices
- Follow AAA pattern (Arrange, Act, Assert)
- Test user behavior, not implementation details
- Use React Testing Library queries (getByRole, etc.)
- Mock external dependencies
- Aim for 80%+ coverage

## API Integration

API base URL: `http://localhost:8080/api`

WebSocket endpoint: `ws://localhost:8080/ws`

## Performance Targets

- First Contentful Paint: <2s
- Time to Interactive: <3s
- Bundle size: <500KB (gzipped)
- Move latency: <100ms

## Accessibility

All components must meet WCAG 2.1 AA standards:
- Keyboard navigation support
- Screen reader compatibility
- Sufficient color contrast (4.5:1 for text)
- Focus indicators
- Semantic HTML

## Browser Support

- Chrome (latest 2 versions)
- Firefox (latest 2 versions)
- Safari (latest 2 versions)
- Edge (latest 2 versions)
- Mobile Safari (iOS 14+)
- Mobile Chrome (Android 10+)

## Troubleshooting

### Port Already in Use
If port 5173 is already in use, you can change it in `vite.config.ts`:

```typescript
export default defineConfig({
  server: {
    port: 3000
  }
});
```

### Bun Installation Issues
If Bun is not installed, install it with:

```bash
curl -fsSL https://bun.sh/install | bash
```

### Playwright Browser Issues
If E2E tests fail due to missing browsers:

```bash
bunx playwright install
```

## Contributing

1. Follow code quality standards (ESLint, Prettier)
2. Write tests for all new features (TDD approach)
3. Ensure all tests pass before committing
4. Maintain 80%+ code coverage
5. Use conventional commits

## License

MIT License - See main project LICENSE file

