---
description: Generate or run tests for a Spring Boot class or run the full suite
argument-hint: [ClassName | "run" | empty for changed-files mode]
allowed-tools: Bash(./mvnw:*), Bash(git diff:*), Bash(git status:*), Read, Grep, Glob, Edit, Write
---

You are the test author/runner for this Spring Boot 3.5 (Java 17) project.

## Project test conventions

- Test framework: JUnit 5 + `spring-boot-starter-test` (already on the classpath via `pom.xml`).
- Tests live under `src/test/java/com/example/invoicing/<domain>/...` mirroring the main package.
- Prefer **slice tests** over `@SpringBootTest` when possible:
  - Service layer: plain JUnit + Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`).
  - Controller layer: `@WebMvcTest(XxxController.class)` + `MockMvc` + `@MockBean` for the service.
  - Repository layer: `@DataJpaTest` (uses an embedded DB unless we wire Testcontainers later).
- Money: assert with `BigDecimal` and `compareTo`, never `equals` (scale traps).
- Naming: `methodUnderTest_givenCondition_expectsOutcome`.
- One behavior per test; use `@DisplayName` for readability when the name gets long.

## Argument handling

User input: **$ARGUMENTS**

Decide the mode based on the argument:

1. **Empty** -> "changed files" mode. Look at:
   !`git status --short`
   !`git diff --name-only HEAD`
   For each changed `*.java` file under `src/main/java`, generate or update its sibling test. For changed frontend files, list what would need a test (we don't have a JS test runner wired yet — flag this).

2. **`run`** -> just execute the suite and report:
   !`./mvnw -q test`
   Then summarize failures with file:line and the likely root cause. Do not modify code.

3. **A class name** (e.g. `CustomerService`, `InvoiceController`) -> generate a focused test class for it.
   - Find the source file with Glob (`**/<ClassName>.java`).
   - Read it, plus its collaborators (repository, DTOs, related entities).
   - Choose the right slice (`@WebMvcTest` for `*Controller`, Mockito for `*Service`, `@DataJpaTest` for `*Repository`).
   - Cover: happy path, validation failure (controllers), `ResourceNotFoundException` on missing id, edge cases for `BigDecimal` math (invoicing-specific).
   - Write the test file under `src/test/java/...` mirroring the package.
   - After writing, run only that test class: `./mvnw -q -Dtest=<ClassName>Test test` and report the result.

## Output

- For generation modes: list each test file you created/updated, the methods you added, and any production-code gaps you noticed (do **not** modify production code without asking).
- For run modes: a short pass/fail summary, then per-failure diagnosis.
