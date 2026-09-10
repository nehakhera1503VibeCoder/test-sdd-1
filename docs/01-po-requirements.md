# Product Requirements — Library Lending Service (MVP)

**Author:** Product Owner
**Status:** Approved
**Date:** 2026-09-10
**Traces to:** `REQ-001`, `REQ-002` in `tracker/PROJECT_TRACKER.md`

## 1. Summary

A small REST service for a library to catalog books, register members, and
track lending — who borrowed what, when it's due, and what's overdue. It
exists to demonstrate a spec-driven, multi-persona delivery process (see
`docs/03-spec-driven-development-playbook.md`) on a domain simple enough
that the *process* stays the focus, not the domain's own complexity.

## 2. Scope (MVP)

In scope:
- A book catalog (add, list, search).
- Member registration.
- Borrowing and returning a book copy.
- Listing a member's active loans.
- Listing overdue loans library-wide.

Out of scope for this MVP (candidates for a future `REQ-` delta, per the
playbook's propose-new-work protocol):
- Reservations/holds queue for a fully-borrowed book.
- Fines/fees for overdue returns.
- Persistence beyond in-memory (a database-backed repository).
- Authentication/authorization.
- Renewing a loan in place (a member must return, then re-borrow).

## 3. Glossary

- **Book**: a catalog entry with one or more physical copies.
- **Member**: a registered person who may borrow books.
- **Loan**: one member borrowing one copy of one book, from a borrow date
  to either a due date (unreturned) or an actual return date.
- **Overdue**: an unreturned loan whose due date has passed.

## 4. Functional Requirements

| ID | Requirement |
|----|-------------|
| FR-1 | A librarian can add a book to the catalog with an ISBN, title, author, and a total copy count (≥ 1). Adding a book with an ISBN that already exists in the catalog is rejected. |
| FR-2 | Anyone can list the full catalog, or search it by a case-insensitive substring match against title or author. |
| FR-3 | A librarian can register a member with a name and an email address. Registering a duplicate email is rejected. |
| FR-4 | A member can borrow one available copy of a book. This is rejected if the book has no available copies, or if the member already has an active (unreturned) loan for that same book. A successful borrow records a due date computed from a configurable loan period (see NFR-3) and reduces the book's available copies by one. |
| FR-5 | A member can return a borrowed copy. This is rejected if the loan was already returned. A successful return restores the book's available copies by one. |
| FR-6 | Anyone can list a given member's currently active (unreturned) loans. |
| FR-7 | Anyone can list all loans, library-wide, that are unreturned and past their due date ("overdue"). A loan that has since been returned — even late — is never listed as overdue. |

## 5. Non-Functional Requirements

| ID | Requirement |
|----|-------------|
| NFR-1 | In-memory storage only for this MVP — no external database. The repository layer is an interface so a persistent implementation is a later, additive story, not a rewrite (see `docs/02-techlead-design.md` §2). |
| NFR-2 | REST API only, JSON request/response bodies. No UI in this MVP. |
| NFR-3 | The loan period (days between borrow and due date) is a configuration value (`application.yml`), not a hardcoded constant, so it can change without a code change. |
| NFR-4 | Every functional requirement has both a unit-level test (service layer) and coverage in the full-stack API test — see `traceability/TRACEABILITY_MATRIX.md`. |

## 6. Acceptance Criteria (Definition of Done for MVP)

- [ ] A book can be added and then found via both an unfiltered list and a
      matching search (FR-1, FR-2).
- [ ] A member can be registered (FR-3).
- [ ] A member can borrow an available book; the book's available-copy
      count drops by one and the loan's due date matches the configured
      loan period (FR-4).
- [ ] Borrowing is correctly rejected when no copies remain, and when the
      same member already holds an active loan for that book (FR-4).
- [ ] A member can return a borrowed book; the book's available-copy count
      is restored, and a second return of the same loan is rejected (FR-5).
- [ ] A member's active loans list correctly excludes returned loans (FR-6).
- [ ] The overdue list correctly includes only unreturned, past-due loans,
      and excludes loans returned late (FR-7).
- [ ] `mvn test` passes, and the project tracker records real, run
      verification evidence for every row up to sign-off.
- [ ] Product Owner reviews the actual delivered behavior against this
      document and signs off (see `PO-01` in the tracker once recorded).

## 7. Open Questions — Resolved

1. **Should a loan support renewal in place?** No — out of scope for MVP
   (§2). A member returns, then re-borrows if still needed. Revisit if
   requested as a new `REQ-`.
2. **Should overdue status be stored on the Loan, or computed?** Computed,
   as of a given date, from `dueDate`/`returnedDate` — see
   `docs/02-techlead-design.md` §4. A stored flag can silently go stale;
   a computed one cannot.
3. **Persistence beyond in-memory?** Explicitly out of scope for MVP
   (NFR-1) — the repository interface exists specifically so this can be
   added later without touching the service layer.
