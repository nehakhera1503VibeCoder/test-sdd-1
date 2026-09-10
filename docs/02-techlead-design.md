# Technical Design — Library Lending Service

**Author:** Tech Lead
**Status:** Approved
**Date:** 2026-09-10
**Traces to:** `DES-001` in `tracker/PROJECT_TRACKER.md`; implements
`docs/01-po-requirements.md`

## 1. Stack

Spring Boot 3.3.4, Java 21, Maven. `spring-boot-starter-web` +
`spring-boot-starter-validation` only — no persistence starter, per NFR-1.
Tests: `spring-boot-starter-test` (JUnit 5, AssertJ, MockMvc).

## 2. Package layout

```
com.example.library
├── LibraryApplication.java     Spring Boot entry point + the shared Clock bean
├── domain/                     Plain Java model: Book, Member, Loan, LoanStatus
├── repository/                 Interface + in-memory implementation, one pair per entity
├── service/                    Interface + impl, one pair per entity (Book/Member/Loan)
├── config/                     LoanPolicyProperties — binds library.loan-policy.* from application.yml
├── exception/                  NotFoundException, ConflictException
└── api/                        REST controllers, request/response DTOs, ApiExceptionHandler
```

Each layer depends only downward (`api` → `service` → `repository` →
`domain`), and every repository/service is an interface with exactly one
implementation today — chosen specifically so a second implementation
(e.g. a JPA-backed `BookRepository`) is a new class behind the existing
interface, not a service-layer rewrite, when persistence is eventually
requested (NFR-1, `docs/01-po-requirements.md` §2).

## 3. Domain model

- `Book`: id, isbn, title, author, totalCopies, availableCopies (mutable —
  `borrowOneCopy()`/`returnOneCopy()` are the only ways to change it, and
  both guard against going out of range).
- `Member`: id, name, email. Immutable.
- `Loan`: id, bookId, memberId, borrowedDate, dueDate, returnedDate
  (nullable — null means still out). Immutable except for
  `markReturned(date)`, which rejects a second call.
- `LoanStatus`: `ACTIVE` / `RETURNED` / `OVERDUE`.

## 4. Design decision: overdue is computed, never stored

`Loan` does not have an `overdue` field. `Loan.statusAsOf(LocalDate asOf)`
derives the status from `returnedDate`/`dueDate` at the moment it's asked,
given an explicit date rather than calling `LocalDate.now()` internally.

This is named explicitly because it's the one place a naive implementation
would go wrong: a stored `overdue` boolean set at borrow time (`false`)
would never flip true on its own — it would need a scheduled job to sweep
and update it, adding a moving part and a staleness window for no benefit,
since "is this loan overdue" is a pure function of two dates the system
already has.

A single `Clock` bean (`LibraryApplication.clock()`) is threaded through
`LoanServiceImpl` instead of static `LocalDate.now()` calls, so tests can
substitute a controllable clock and assert exact before/on/after-due-date
behavior deterministically (see `LoanServiceImplTest`'s `SettableClock`).

## 5. API surface

| Method & Path | FR | Success | Failure |
|---|---|---|---|
| `POST /api/v1/books` | FR-1 | 201 + book | 400 (validation), 409 (duplicate ISBN) |
| `GET /api/v1/books[?query=]` | FR-2 | 200 + list | — |
| `GET /api/v1/books/{id}` | (supporting) | 200 + book | 404 |
| `POST /api/v1/members` | FR-3 | 201 + member | 400 (validation), 409 (duplicate email) |
| `GET /api/v1/members` | (supporting) | 200 + list | — |
| `POST /api/v1/loans` | FR-4 | 201 + loan | 400, 404 (unknown book/member), 409 (no copies / duplicate active loan) |
| `POST /api/v1/loans/{id}/return` | FR-5 | 200 + loan | 404 (unknown loan), 409 (already returned) |
| `GET /api/v1/members/{id}/loans` | FR-6 | 200 + list | 404 (unknown member) |
| `GET /api/v1/loans/overdue` | FR-7 | 200 + list | — |

`ApiExceptionHandler` maps `NotFoundException` → 404, `ConflictException` →
409, and bean-validation/`IllegalArgumentException` → 400, all as a
`{"error": "..."}` JSON body — one exception-mapping surface for every
controller rather than per-controller try/catch.

## 6. Story breakdown (Dev)

| Story | Covers |
|---|---|
| `DEV-01` | Domain model + in-memory repositories (`domain/**`, `repository/**`) |
| `DEV-02` | Book + Member services and their unit tests (FR-1, FR-2, FR-3) |
| `DEV-03` | Loan service — borrow/return/list-active/list-overdue — and its unit tests, incl. the settable-clock overdue tests (FR-4, FR-5, FR-6, FR-7) |
| `DEV-04` | REST layer: controllers, DTOs, `ApiExceptionHandler`, `application.yml` loan-policy config |

## 7. Testing strategy

- **Unit (service layer, Dev-owned):** one test class per service, against
  the in-memory repository directly (no Spring context) — fast, and
  exercises every success/failure branch named in §5.
- **Full-stack (QA-owned, separate persona pass):** `@SpringBootTest` +
  `MockMvc` walking the entire lending lifecycle through the real REST
  endpoints in the sequence a real client would use them, plus the
  validation/404 edge cases. See `traceability/TRACEABILITY_MATRIX.md` for
  the exact file/method per requirement.
- Both layers are required per FR (NFR-4) — the unit test proves the
  business rule; the full-stack test proves the wiring (DTO mapping,
  status codes, exception handling) actually exposes that rule correctly
  over HTTP.

## 8. Build review

Recorded at sign-off time as a `TECHLEAD-` row in
`tracker/PROJECT_TRACKER.md` — confirms the shipped code matches this
document, and names any deviation explicitly rather than leaving it
implicit.
