# Project Tracker — Library Lending Service

Updated after every unit of work: requirement, design, dev, test, review,
or sign-off. Each row is one unit of work, in the writing persona's voice.
Status values: `Drafted`, `In Review`, `Approved`, `In Progress`,
`Complete`, `Blocked`. See `docs/03-spec-driven-development-playbook.md`
§4 for the ID convention and evidence convention this table follows.

| ID | Date | Persona | Work Item | Artifact | Status | Notes |
|----|------|---------|-----------|----------|--------|-------|
| REQ-001 | 2026-09-10 | Product Owner | Draft MVP functional requirements: book catalog (add/list/search), member registration, borrow/return, active-loans-by-member, overdue-loans (FR-1..FR-7) | `docs/01-po-requirements.md` §4 | Approved | Scope explicitly excludes renewals, fines, persistence, and auth for this MVP (§2) — candidates for a future `REQ-` via the propose-new-work protocol. |
| REQ-002 | 2026-09-10 | Product Owner | Draft non-functional requirements (NFR-1..NFR-4) and resolve 3 open questions: no loan renewal in MVP; overdue status computed, never stored; no persistence layer beyond in-memory | `docs/01-po-requirements.md` §5, §7 | Approved | Binding decisions carried into design. |
| DES-001 | 2026-09-10 | Tech Lead | Package layout (domain/repository/service/config/exception/api), the computed-vs-stored-overdue design decision + shared `Clock` bean, full API surface table, 4-story Dev breakdown, testing strategy (unit + full-stack per FR) | `docs/02-techlead-design.md` | Approved | Also created `traceability/TRACEABILITY_MATRIX.md` with one row per FR/NFR ahead of Dev starting, per playbook §6 step 3. |
| DEV-01 | 2026-09-10 | Dev | Domain model (`Book`, `Member`, `Loan`, `LoanStatus`) and in-memory repositories (`BookRepository`/`MemberRepository`/`LoanRepository` + `InMemory*` impls) | `src/main/java/com/example/library/{domain,repository}/**` | Complete | Compiles clean as part of this session's full `mvn compile` (`BUILD SUCCESS`, zero errors) — see DEV-04 row for the full-suite run these files are exercised by. |
| DEV-02 | 2026-09-10 | Dev | `BookService`/`BookServiceImpl` (FR-1, FR-2) and `MemberService`/`MemberServiceImpl` (FR-3), each with a unit test class against the in-memory repository (no Spring context) | `src/main/java/com/example/library/service/**`, `src/test/java/com/example/library/service/{BookServiceImplTest,MemberServiceImplTest}.java` | Complete | `mvn test` → `BookServiceImplTest` 5/5 pass, `MemberServiceImplTest` 3/3 pass (duplicate-ISBN, duplicate-email, and not-found paths all exercised). |
| DEV-03 | 2026-09-10 | Dev | `LoanService`/`LoanServiceImpl`: borrow (due-date calc from `LoanPolicyProperties`, no-copies / duplicate-active-loan rejection), return (double-return rejection), list-active-by-member, list-overdue via `Loan.statusAsOf` — plus a settable-`Clock` unit test class asserting exact before/on/after-due-date behavior against hand-computed dates | `src/main/java/com/example/library/service/impl/LoanServiceImpl.java`, `src/test/java/com/example/library/service/LoanServiceImplTest.java` | Complete | `mvn test` → `LoanServiceImplTest` 11/11 pass, incl. `listOverdueLoans_flipsOverdueOnlyStrictlyAfterDueDate` (asserts ACTIVE exactly on the due date `2026-01-15`, OVERDUE the day after — both hand-computed from a 2026-01-01 borrow date + the 14-day configured policy) and `listOverdueLoans_excludesReturnedLoansEvenIfReturnedLate`. |
| DEV-04 | 2026-09-10 | Dev | REST layer: `BookController`, `MemberController`, `LoanController`, request/response DTOs, `ApiExceptionHandler` (404/409/400 mapping), `application.yml` (`library.loan-policy.loan-period-days`) | `src/main/java/com/example/library/api/**`, `src/main/java/com/example/library/config/LoanPolicyProperties.java`, `src/main/resources/application.yml` | Complete | **Full suite, this run**: `mvn test` → `Tests run: 22, Failures: 0, Errors: 0, Skipped: 0` — `BUILD SUCCESS` (breakdown: `LibraryApiIntegrationTest` 3, `MemberServiceImplTest` 3, `BookServiceImplTest` 5, `LoanServiceImplTest` 11). **Also**: `mvn clean package` → `BUILD SUCCESS`, produced `target/library-lending-service-0.1.0-SNAPSHOT.jar`. **Also verified live**: ran the packaged jar standalone (`java -jar ...jar`, no Maven), curled the entire lifecycle — `POST /api/v1/books` (201, `availableCopies:1`) → `POST /api/v1/members` (201) → `POST /api/v1/loans` (201, `status:ACTIVE`, `dueDate:2026-09-24` = borrow date `2026-09-10` + the configured 14-day policy) → book's `availableCopies` confirmed `0` → a second borrow attempt on the now-empty book correctly returned `409` → `GET /api/v1/loans/overdue` correctly empty → `POST /api/v1/loans/{id}/return` (200, `status:RETURNED`) → book's `availableCopies` confirmed restored to `1`. |
| TEST-01 | 2026-09-10 | QA/Tester | Independent full-stack verification pass: `LibraryApiIntegrationTest` (`@SpringBootTest` + `MockMvc`) walks the entire lending lifecycle through the real REST endpoints in client call order (add book → search → register member → borrow → conflict-on-no-copies → active-loans → overdue-empty → return → conflict-on-double-return), plus dedicated validation (`400`) and not-found (`404`) edge-case tests | `src/test/java/com/example/library/api/LibraryApiIntegrationTest.java` | Complete | Re-ran the full suite independently of the Dev pass: `mvn test` → `22/22 pass`, `BUILD SUCCESS`, same run confirms no regression from DEV-01..04. Cross-checked the traceability matrix (`traceability/TRACEABILITY_MATRIX.md`) row by row against actual test method names in this run — no FR/NFR left without both a unit and a full-stack test. |
| TECHLEAD-01 | 2026-09-10 | Tech Lead | Build review against `docs/02-techlead-design.md` (DES-001): confirms package layout, the computed-overdue decision (`Loan.statusAsOf`, no stored flag), the shared `Clock` bean, and the full API surface all match the design as drafted. No deviations found. | `docs/02-techlead-design.md` §8 | Approved | Reviewed class-by-class against the actual `src/main/java` tree, not from memory. Confirms `NFR-1` (repository interfaces, in-memory impls only) and `NFR-3` (loan period from `application.yml`, no hardcoded constant) both hold in the shipped code. |
| PO-01 | 2026-09-10 | Product Owner | Final MVP sign-off against `docs/01-po-requirements.md` §6 acceptance criteria | `docs/01-po-requirements.md` §6 | Approved | All 8 acceptance criteria met — see "PO Sign-off" section below. **MVP accepted.** |
| TECHLEAD-02 | 2026-09-17 | Tech Lead | Cross-tool portability: added root `AGENTS.md` as the canonical, tool-neutral entry point (read natively by Devin and GitHub Copilot's coding agent, per each tool's current docs/changelog); slimmed `CLAUDE.md` to `@AGENTS.md` (Claude Code's native import syntax) plus a short Claude-only section on `.claude/agents/*.md` and the two slash commands; added `.github/copilot-instructions.md` as a thin pointer to `AGENTS.md` for Copilot Chat sessions that don't auto-load it; updated `README.md`, `docs/00-roles-and-responsibilities.md`, `docs/03-spec-driven-development-playbook.md`, `docs/04-new-requirement-intake.md`, and the `LibraryApplication` javadoc to reference `AGENTS.md` as canonical rather than assuming Claude Code | `AGENTS.md`, `CLAUDE.md`, `.github/copilot-instructions.md` | Complete | No code changed — verified no regression: `mvn test` → `Tests run: 22, Failures: 0, Errors: 0, Skipped: 0`, `BUILD SUCCESS`. **Explicit limitation, not silently glossed over**: `.claude/agents/*.md`'s hard tool-access restriction per persona (e.g. Product Owner has no `Bash`) is a Claude Code–only enforcement mechanism — Devin and Copilot have no equivalent for a checked-in file, so on those tools the persona split in `docs/00-roles-and-responsibilities.md` is a followed convention, not an enforced boundary. |

---

## PO Sign-off (PO-01)

Reviewed against `docs/01-po-requirements.md` §6:

- [x] A book can be added and found via both an unfiltered list and a
      matching search. Verified: `LibraryApiIntegrationTest`, live curl run
      (DEV-04).
- [x] A member can be registered. Verified: same.
- [x] Borrowing an available book drops its available-copy count by one and
      computes the correct due date from the configured loan period.
      Verified live: `dueDate 2026-09-24` = borrow date + 14 days (DEV-04);
      hand-computed unit assertion in `LoanServiceImplTest` (DEV-03).
- [x] Borrowing is rejected with no copies remaining, and when the member
      already holds an active loan for that book. Verified:
      `LoanServiceImplTest`, live curl (409 on second borrow) (DEV-03/04).
- [x] Returning a book restores its available-copy count; a second return
      of the same loan is rejected. Verified: `LoanServiceImplTest`, live
      curl (DEV-03/04).
- [x] A member's active-loans list excludes returned loans. Verified:
      `LoanServiceImplTest#listActiveLoansForMember_excludesReturnedLoans`.
- [x] The overdue list includes only unreturned, past-due loans and
      excludes loans returned late. Verified:
      `LoanServiceImplTest#listOverdueLoans_flipsOverdueOnlyStrictlyAfterDueDate`,
      `#listOverdueLoans_excludesReturnedLoansEvenIfReturnedLate`.
- [x] `mvn test` passes with real, run verification evidence recorded for
      every row above. 22/22, `BUILD SUCCESS` (TEST-01).

**MVP is accepted. Ready to demo or extend via `/new-requirement`.**

## Open Items / Blockers

None. All 7 FRs and 4 NFRs are fully covered per
`traceability/TRACEABILITY_MATRIX.md`.

## Phase Overview

- [x] Phase 0: Requirements (PO) — `REQ-001`, `REQ-002`
- [x] Phase 1: Architecture & Design (Tech Lead) — `DES-001`
- [x] Phase 2: Domain + Services (Dev) — `DEV-01`..`DEV-03`
- [x] Phase 3: REST API (Dev) — `DEV-04`
- [x] Phase 4: Independent Testing (QA) — `TEST-01`
- [x] Phase 5: Build Review (Tech Lead) — `TECHLEAD-01`
- [x] Phase 6: PO Sign-off — `PO-01`, MVP accepted
