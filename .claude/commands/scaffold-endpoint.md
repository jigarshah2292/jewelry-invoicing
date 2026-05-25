---
description: Scaffold a full CRUD vertical slice (entity -> controller -> React API client) for a new resource
argument-hint: <ResourceName> [field:type field:type ...]
allowed-tools: Read, Grep, Glob, Write, Edit, Bash(ls:*)
---

You are scaffolding a new REST resource end-to-end in this Spring Boot + React invoicing project.

## Inputs

- **Resource name** (PascalCase singular): `$1`
- **Optional fields**: `$2 $3 $4 $5 $6 $7 $8 $9` — each in `name:type` form.
  - Allowed types: `String`, `Integer`, `Long`, `BigDecimal`, `LocalDate`, `Instant`, `Boolean`.
  - If no fields are given, generate a sensible minimum: `name:String` (and a `notes:String` for non-trivial domains).
- Full args echoed: `$ARGUMENTS`

Derive these from the resource name:
- `EntityName` = `$1` (e.g. `Order`)
- `entityName` = camelCase (e.g. `order`)
- `entities` = lowercase plural for URL + JPA table + frontend file (e.g. `orders`)
- `package` = `com.example.invoicing.<entities>`

If `$1` is missing, stop and ask the user for the resource name.

## Reference: existing slice to mirror exactly

Read these first so the new code matches conventions one-for-one:

- @src/main/java/com/example/invoicing/customer/Customer.java
- @src/main/java/com/example/invoicing/customer/CustomerRepository.java
- @src/main/java/com/example/invoicing/customer/CustomerService.java
- @src/main/java/com/example/invoicing/customer/CustomerController.java
- @src/main/java/com/example/invoicing/customer/dto/CustomerRequest.java
- @src/main/java/com/example/invoicing/customer/dto/CustomerResponse.java
- @src/main/java/com/example/invoicing/common/exception/ResourceNotFoundException.java
- @web/src/api/client.ts
- @web/src/api/customers.ts
- @web/src/types/api.ts

## Files to create

### Backend (`src/main/java/com/example/invoicing/<entities>/`)

1. `<EntityName>.java` — JPA entity with `@Entity`, `@Table(name = "<entities>")`, Lombok (`@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`), `@Id @GeneratedValue(strategy = IDENTITY)` Long id, plus the requested fields with appropriate column annotations. Include `createdAt`/`updatedAt` `Instant` fields with `@CreationTimestamp` / `@UpdateTimestamp` if the customer entity uses them; otherwise omit.
2. `<EntityName>Repository.java` — `extends JpaRepository<<EntityName>, Long>`.
3. `dto/<EntityName>Request.java` — Java record with jakarta validation annotations (`@NotBlank` on Strings, `@NotNull` + `@PositiveOrZero` on `BigDecimal` money fields, etc.).
4. `dto/<EntityName>Response.java` — Java record with a `static <EntityName>Response from(<EntityName> entity)` factory mirroring `CustomerResponse.from`.
5. `<EntityName>Service.java` — `@Service @RequiredArgsConstructor @Transactional`, with `findAll`, `findById`, `create`, `update`, `delete`, and a private `getOrThrow(Long id)` that throws `ResourceNotFoundException("<EntityName>", id)`. Read methods get `@Transactional(readOnly = true)`.
6. `<EntityName>Controller.java` — `@RestController @RequestMapping("/api/v1/<entities>") @RequiredArgsConstructor` with the same five endpoints as `CustomerController`, including `URI.create("/api/v1/<entities>/" + created.id())` on POST and `@ResponseStatus(NO_CONTENT)` on DELETE.

### Database (`src/main/resources/db/migration/`)

7. A new Flyway migration `V<next>__create_<entities>.sql`.
   - Inspect existing migrations with Glob `src/main/resources/db/migration/V*.sql` and pick the next `V<n>` number.
   - Use Postgres types: `BIGSERIAL PRIMARY KEY` for id, `VARCHAR(255)` for short strings, `TEXT` for free-form, `NUMERIC(19,2)` for money, `TIMESTAMP WITH TIME ZONE` for `Instant`, `DATE` for `LocalDate`. NOT NULL where the request DTO requires it.

### Frontend (`web/src/`)

8. Append `<EntityName>` and `<EntityName>Request` interfaces to `web/src/types/api.ts` (matching the response/request DTOs; numbers for `Long`, string for `BigDecimal`/`LocalDate`/`Instant`).
9. Create `web/src/api/<entities>.ts` modeled exactly on `web/src/api/customers.ts` (same `list`/`get`/`create`/`update`/`remove` shape, `apiRequest<T>('/<entities>', ...)`).

## Process

1. Read all reference files above.
2. Validate inputs: confirm resource name, parse fields, list any unknown types and stop if you find one.
3. Show the user a short plan: list of files you will create with one-line descriptions and the chosen Flyway version number. Wait for confirmation before writing **only if the change feels risky** (e.g. resource name collision with an existing package). Otherwise proceed.
4. Create files in the order listed (entity -> repository -> dtos -> service -> controller -> migration -> frontend types -> frontend api).
5. After writing, summarize:
   - Each created file with a one-line description.
   - The new endpoints: `GET/POST /api/v1/<entities>`, `GET/PUT/DELETE /api/v1/<entities>/{id}`.
   - Suggested next steps: `/test <EntityName>Service`, wire the new API into a page, add it to navigation if appropriate.

## Hard rules

- Money fields must be `BigDecimal` in Java and `string` in TypeScript — never `double`/`number`.
- Never edit unrelated files.
- Do not invent new conventions; if the customer slice doesn't do something, you don't either.
- Do not run Maven or git commands as part of scaffolding.
