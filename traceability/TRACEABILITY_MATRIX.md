# Requirement Traceability Matrix

Maintained by: Tech Lead (created), all personas (kept current).
Update this file in the same unit of work that adds/changes the
`REQ-`/`DES-`/`DEV-`/`TEST-` row it references — never as a later cleanup
pass. See `docs/03-spec-driven-development-playbook.md` §4, §6.

**How to read a row:** given a requirement, this tells you exactly which
design section, which source files, and which test methods implement and
verify it, right now — so "what would changing FR-4 affect?" is a lookup,
not a codebase-wide search.

| Requirement | Description | Design | Implementation | Tests | Status |
|---|---|---|---|---|---|
| `FR-1` (REQ-001) | Add a book to the catalog; reject duplicate ISBN | `docs/02-techlead-design.md` §5 (`POST /api/v1/books`) | `service/BookService.java`, `service/impl/BookServiceImpl.java` (`addBook`), `api/BookController.java` (`addBook`), `api/dto/BookRequestDto.java`/`BookResponseDto.java` (DEV-01, DEV-04) | `BookServiceImplTest#addBook_createsBookWithAllCopiesAvailable`, `#addBook_rejectsDuplicateIsbn`; `LibraryApiIntegrationTest#fullLendingLifecycle_endToEnd`, `#addBook_blankTitle_returns400` (TEST-01) | Done |
| `FR-2` (REQ-001) | List/search the catalog | `docs/02-techlead-design.md` §5 (`GET /api/v1/books`) | `BookServiceImpl#listBooks`, `BookRepository#search`/`InMemoryBookRepository`, `BookController#listBooks` (DEV-01, DEV-04) | `BookServiceImplTest#listBooks_withNoQuery_returnsFullCatalog`, `#listBooks_withQuery_matchesTitleOrAuthorCaseInsensitively`; `LibraryApiIntegrationTest#fullLendingLifecycle_endToEnd` (search step) (TEST-01) | Done |
| `FR-3` (REQ-001) | Register a member; reject duplicate email | `docs/02-techlead-design.md` §5 (`POST /api/v1/members`) | `service/MemberService.java`, `service/impl/MemberServiceImpl.java`, `api/MemberController.java`, `api/dto/MemberRequestDto.java`/`MemberResponseDto.java` (DEV-02, DEV-04) | `MemberServiceImplTest` (all 3 methods); `LibraryApiIntegrationTest#fullLendingLifecycle_endToEnd` (TEST-01) | Done |
| `FR-4` (REQ-001) | Borrow a book: due-date calc, reject no-copies / duplicate active loan | `docs/02-techlead-design.md` §4 (computed status), §5 (`POST /api/v1/loans`) | `service/LoanService.java`, `service/impl/LoanServiceImpl.java` (`borrowBook`), `domain/Book.java` (`borrowOneCopy`), `api/LoanController.java` (`borrowBook`) (DEV-01, DEV-03, DEV-04) | `LoanServiceImplTest#borrowBook_computesDueDateFromConfiguredLoanPeriod_andDecrementsAvailableCopies`, `#borrowBook_noAvailableCopies_throwsConflict`, `#borrowBook_sameMemberSameBookAlreadyActive_throwsConflictEvenWithCopiesAvailable`, `#borrowBook_unknownBookOrMember_throwsNotFound`; `LibraryApiIntegrationTest#fullLendingLifecycle_endToEnd` (TEST-01) | Done |
| `FR-5` (REQ-001) | Return a book; reject double-return | `docs/02-techlead-design.md` §5 (`POST /api/v1/loans/{id}/return`) | `LoanServiceImpl#returnBook`, `domain/Book.java` (`returnOneCopy`), `domain/Loan.java` (`markReturned`), `api/LoanController.java` (`returnBook`) (DEV-01, DEV-03, DEV-04) | `LoanServiceImplTest#returnBook_marksReturnedAndRestoresAvailableCopy`, `#returnBook_alreadyReturned_throwsConflict`, `#returnBook_unknownLoan_throwsNotFound`; `LibraryApiIntegrationTest#fullLendingLifecycle_endToEnd` (TEST-01) | Done |
| `FR-6` (REQ-001) | List a member's active loans | `docs/02-techlead-design.md` §5 (`GET /api/v1/members/{id}/loans`) | `LoanServiceImpl#listActiveLoansForMember`, `LoanRepository#findActiveByMemberId`, `api/LoanController.java` (`listActiveLoansForMember`) (DEV-01, DEV-03, DEV-04) | `LoanServiceImplTest#listActiveLoansForMember_excludesReturnedLoans`, `#listActiveLoansForMember_unknownMember_throwsNotFound`; `LibraryApiIntegrationTest#fullLendingLifecycle_endToEnd` (TEST-01) | Done |
| `FR-7` (REQ-001) | List overdue loans library-wide | `docs/02-techlead-design.md` §4, §5 (`GET /api/v1/loans/overdue`) | `LoanServiceImpl#listOverdueLoans`, `domain/Loan.java` (`statusAsOf`), `api/LoanController.java` (`listOverdueLoans`) (DEV-01, DEV-03, DEV-04) | `LoanServiceImplTest#listOverdueLoans_flipsOverdueOnlyStrictlyAfterDueDate`, `#listOverdueLoans_excludesReturnedLoansEvenIfReturnedLate`; `LibraryApiIntegrationTest#fullLendingLifecycle_endToEnd` (overdue step) (TEST-01) | Done |
| `NFR-1` (REQ-002) | In-memory storage only, behind a repository interface | `docs/02-techlead-design.md` §2 | `repository/*Repository.java` (interfaces), `repository/InMemory*Repository.java` (impls) (DEV-01) | Implicitly exercised by every service test above (all run against the in-memory impls) | Done |
| `NFR-2` (REQ-002) | REST/JSON only, no UI | `docs/02-techlead-design.md` §1, §5 | `api/**` (all controllers return JSON DTOs; no template engine dependency) (DEV-04) | `LibraryApiIntegrationTest` (all methods hit real HTTP + assert JSON) (TEST-01) | Done |
| `NFR-3` (REQ-002) | Configurable loan period, not a hardcoded constant | `docs/02-techlead-design.md` §5 | `config/LoanPolicyProperties.java`, `src/main/resources/application.yml` (`library.loan-policy.loan-period-days`) (DEV-04) | `LoanServiceImplTest` constructs `LoanPolicyProperties` explicitly and asserts the resulting due date against it (hand-computed) | Done |
| `NFR-4` (REQ-002) | Every FR has a unit test + full-stack test | `docs/02-techlead-design.md` §7 | N/A (a testing-strategy requirement, not code) | This table's Tests column, cross-checked row by row (TEST-01) | Done |

## Coverage summary (as of `TEST-01` / `PO-01`)

- 7/7 functional requirements: unit-tested and full-stack-tested.
- 4/4 non-functional requirements: satisfied and verified.
- 0 requirements with a design or test gap.

Add a new row here in the same unit of work that adds the `REQ-` tracker
row for it (`docs/03-spec-driven-development-playbook.md` §4) — a `REQ-`
row with no corresponding matrix row is itself a process defect worth
flagging in the next Tech Lead review.
