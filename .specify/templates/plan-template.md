# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

[Extract from feature spec: primary requirement + technical approach from research]

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: [e.g., Python 3.11, Swift 5.9, Rust 1.75 or NEEDS CLARIFICATION]  
**Primary Dependencies**: [e.g., FastAPI, UIKit, LLVM or NEEDS CLARIFICATION]  
**Storage**: [if applicable, e.g., PostgreSQL, CoreData, files or N/A]  
**Testing**: [e.g., pytest, XCTest, cargo test or NEEDS CLARIFICATION]  
**Target Platform**: [e.g., Linux server, iOS 15+, WASM or NEEDS CLARIFICATION]
**Project Type**: [single/web/mobile - determines source structure]  
**Performance Goals**: [domain-specific, e.g., 1000 req/s, 10k lines/sec, 60 fps or NEEDS CLARIFICATION]  
**Constraints**: [domain-specific, e.g., <200ms p95, <100MB memory, offline-capable or NEEDS CLARIFICATION]  
**Scale/Scope**: [domain-specific, e.g., 10k users, 1M LOC, 50 screens or NEEDS CLARIFICATION]

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

This feature MUST comply with all five core principles from the constitution:

### I. Code Quality Principles ✓
- [ ] Design follows SOLID principles (Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion)
- [ ] No code duplication (DRY) - common functionality abstracted into reusable components
- [ ] Solution is simple and avoids over-engineering (KISS)
- [ ] Only implements specified requirements, no speculative features (YAGNI)
- [ ] Clear separation of concerns (business logic, presentation, data access)
- [ ] Uses composition over inheritance where appropriate
- [ ] Follows Law of Demeter (minimal coupling between modules)

### II. Clean Code Practices ✓
- [ ] All names (variables, functions, classes) are descriptive and intention-revealing
- [ ] Functions are small (<20 lines) and focused on single tasks
- [ ] Nesting levels limited to 2-3 maximum
- [ ] Code is self-documenting; comments explain "why" not "what"
- [ ] Error handling is explicit and comprehensive
- [ ] Code improvements made wherever touched (Boy Scout Rule)

### III. Testing Standards ✓
- [ ] Test strategy defined following Test Pyramid (70% unit, 20% integration, 10% e2e)
- [ ] Critical business logic has near 100% test coverage
- [ ] All tests are independent and can run in any order
- [ ] Tests use AAA pattern (Arrange-Act-Assert)
- [ ] Test names are descriptive and explain expected behavior
- [ ] Tests focus on public APIs, not private implementation details
- [ ] No redundant tests - each test adds unique value

### IV. User Experience Consistency ✓
- [ ] UI follows established design system (if applicable)
- [ ] WCAG 2.1 AA accessibility standards met
- [ ] Loading states and progress indicators for all async operations
- [ ] Error messages are clear, actionable, and user-friendly
- [ ] Responsive design for all supported devices
- [ ] Keyboard navigation support for all interactive elements

### V. Performance Requirements ✓
- [ ] Performance budgets defined (page load, API response, bundle size, memory)
- [ ] Lazy loading strategy for resources
- [ ] Caching strategy defined at appropriate levels
- [ ] Database queries optimized with proper indexing
- [ ] Assets optimized (images compressed, CSS/JS minified)
- [ ] Performance monitoring and profiling plan in place
- [ ] System designed to handle 1000 concurrent users

**Violations**: If any principle cannot be met, document in "Complexity Tracking" section below with justification.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
# [REMOVE IF UNUSED] Option 1: Single project (DEFAULT)
src/
├── models/
├── services/
├── cli/
└── lib/

tests/
├── contract/
├── integration/
└── unit/

# [REMOVE IF UNUSED] Option 2: Web application (when "frontend" + "backend" detected)
backend/
├── src/
│   ├── models/
│   ├── services/
│   └── api/
└── tests/

frontend/
├── src/
│   ├── components/
│   ├── pages/
│   └── services/
└── tests/

# [REMOVE IF UNUSED] Option 3: Mobile + API (when "iOS/Android" detected)
api/
└── [same as backend above]

ios/ or android/
└── [platform-specific structure: feature modules, UI flows, platform tests]
```

**Structure Decision**: [Document the selected structure and reference the real
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
