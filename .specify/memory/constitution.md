<!-- SYNC IMPACT REPORT (temporary - remove before commit)
Version change: 0.0.0 -> 1.0.0
Modified principles: none (first ratification; all placeholders replaced)
Added sections: Core Principles (5), Technical Constraints, Development Workflow
Removed sections: none
Deferred TODOs: RATIFICATION_DATE marked TODO (no prior adoption date found in repo)
-->

# Task Management Practice IA Constitution

## Core Principles

### I. Hexagonal Architecture (NON-NEGOTIABLE)

The backend MUST follow hexagonal architecture with three layers: `application`
(dao, service), `domain`, and `infrastructure`. Business logic lives in
`application/service`; HTTP controllers live in `infrastructure`; business rules
live in `domain`. No layer may bypass another (e.g., a controller must not call
a dao directly).

Rationale: keeps the single Spring Boot backend (`backend/springboot`) testable
and decoupled from transport concerns; every business API change is made there.

### II. Test-First (NON-NEGOTIABLE)

Tests are written before or alongside implementation and MUST pass before code
is considered done. Backend tests use JUnit via Maven; frontend tests use Karma
+ Jasmine against ChromeHeadless. Tests live next to their code
(`src/test` backend, `*.spec.ts` frontend). The full suite runs via
`./scripts/test.sh`.

Rationale: this project exists to demonstrate AI-driven development practices
(test generation, bug fixing); a green suite is the proof of correctness.

### III. Transactional Integrity

Business operations that mutate more than one aggregate MUST be wrapped in a
transaction so partial writes cannot leave the data inconsistent. Assignment
limits per user MUST be enforced atomically with the assignment itself.

Rationale: task assignment has a per-user limit; a non-atomic write could
over-assign a user and corrupt the kanban state.

### IV. Schema by Migration Only

Every database schema change MUST go through a Liquibase changelog. Manual
edits to PostgreSQL are forbidden. The reference schema lives in
`docs/Architecture/db-design.md`. Local reset goes through
`./scripts/setup-db.sh` (PostgreSQL via Docker).

Rationale: PostgreSQL is the only state outside the repository; migrations keep
it reproducible for every contributor.

### V. Simplicity & YAGNI

Start simple. Do not add abstractions, dependencies, or features beyond the
current need. The stack is fixed: Spring Boot (Java 21), Angular standalone,
Tailwind CSS, RxJS, PostgreSQL. New third-party services require explicit
justification.

Rationale: the project is an exercise/demonstration base; complexity dilutes
the AIDD demonstration value.

## Technical Constraints

- REST API only, all routes prefixed `/api/v1`. Path parameters for identifiers.
  Contract specs live in `docs/Architecture/url-api-structure.md` and
  `docs/Architecture/db-design.md`.
- Frontend calls the backend exclusively through the dev proxy
  (`proxy.conf.json`); no direct DB access from the frontend.
- UI styling uses Tailwind CSS 4; no heavy component CSS framework. Reusable
  UI components live in `frontend/src/app/ui`.
- CI (`.github/workflows/build.yml`) builds and tests both backends on every
  push/PR to `main`. CI verifies; it does not deploy.
- No authentication is in place; do not introduce security claims that assume
  otherwise.

## Development Workflow

- Branches: `<type/ticket-short-description>` with types `feature`, `fix`,
  `chore`. Main branch is `main`.
- Commits: conventional commits, imperative mood, concise description
  (`type(scope): description`).
- Before commit: `cd frontend && ng build` (fast guard).
- Before push: `./scripts/test.sh` (heavy guard, backend + frontend).
- The AI MUST NOT commit or push without an explicit user request.
- On failure, run one agent per failing assertion (build / tests / rules).

## Governance

This constitution supersedes all other development practices in this repository.
Where a practice conflicts with a principle here, the constitution wins.

- **Amendments**: propose changes via PR to this file; document rationale,
  affected principles, and any migration needed for existing code.
- **Versioning**: semantic versioning. MAJOR = principle removed or redefined
  incompatibly; MINOR = new principle or section added or materially expanded;
  PATCH = wording, typos, clarifications.
- **Compliance review**: every PR/review MUST verify the coding assertions in
  `aidd_docs/memory/coding-assertions.md` pass and that no principle is
  violated. Complexity beyond the stated need MUST be justified in the PR.
- Runtime development guidance lives in `aidd_docs/memory/` (project memory).

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): first ratification, original adoption date unknown | **Last Amended**: 2026-11-20
