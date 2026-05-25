---
description: Review uncommitted changes against project conventions (Spring Boot + React)
argument-hint: [optional focus, e.g. "security" or a path]
allowed-tools: Bash(git status:*), Bash(git diff:*), Bash(git log:*), Read, Grep, Glob
---

You are reviewing uncommitted work in this Spring Boot + React + Postgres invoicing project.

## Context

- Backend: Spring Boot 3.5, Java 17, JPA + Flyway + Postgres, Lombok, jakarta validation.
- Package layout: `com.example.invoicing.<domain>` with a `dto/` subpackage holding `XxxRequest` / `XxxResponse` records.
- Standard slice: `Entity` -> `Repository` (JpaRepository) -> `@Service @Transactional` -> `@RestController @RequestMapping("/api/v1/<plural>")`.
- 404s use `com.example.invoicing.common.exception.ResourceNotFoundException`.
- Frontend: React 19 + TS + Vite. API helpers live in `web/src/api/*.ts` and use `apiRequest<T>` from `./client`. Types live in `web/src/types/api.ts`.

## Current state

Branch & status:
!`git status --short --branch`

Recent commits for tone/style:
!`git log -5 --oneline`

Staged changes:
!`git diff --staged`

Unstaged changes:
!`git diff`

## Your task

Review the diff above. Optional focus from the user: **$ARGUMENTS** (if empty, do a general review).

For each finding, output:

1. **File:line** and a short title.
2. **Severity**: blocker / major / minor / nit.
3. **Why it matters** (one or two sentences).
4. **Suggested fix** as a concrete code snippet when useful.

Pay particular attention to:

- **Conventions**: does the new code match the customer/product slice pattern? Records for DTOs, `@RequiredArgsConstructor`, `@Transactional(readOnly = true)` on read methods, `URI.create("/api/v1/...")` on `201 Created`.
- **Validation & errors**: `@Valid` on request bodies, jakarta validation annotations on DTOs, `ResourceNotFoundException` for missing entities (not raw `RuntimeException`).
- **Persistence**: N+1 risks, missing `@Transactional`, entities leaking out of the service layer, missing Flyway migration for schema changes (`src/main/resources/db/migration`).
- **Money & invoicing correctness**: `BigDecimal` for prices/amounts (never `double`/`float`), correct rounding, currency handling, totals recomputed server-side.
- **API shape**: `/api/v1/...` prefix, plural resource paths, correct HTTP verbs and status codes, no entity-as-response leaks.
- **Frontend**: `apiRequest<T>` usage, typed payloads from `web/src/types/api.ts`, no hardcoded URLs, error/loading states via `AsyncState`.
- **Security & input**: SQL injection (use JPA / parameter binding), unsanitized strings reaching the DB, missing length/format constraints.
- **Tests**: are there tests for the new behavior? If not, list what's missing.

End with a **one-paragraph verdict**: ship / ship after minor fixes / needs changes, and the top 3 things to address first.
