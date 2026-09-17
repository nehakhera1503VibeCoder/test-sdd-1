# Library Lending Service

A small Spring Boot REST API (book catalog + member lending) built to
demonstrate a **spec-driven, multi-persona, requirement-traceable**
delivery process — usable from Claude Code, Devin, GitHub Copilot, or any
other coding agent. Read **[`AGENTS.md`](AGENTS.md) first** — it's the
entry point for both this app and the process behind it. (Claude Code
users: `CLAUDE.md` imports it automatically and adds Claude-specific
enforcement on top — see `.claude/agents/` and `.claude/commands/`.)

## The process, in one paragraph

Four personas — Product Owner, Tech Lead, Developer, QA/Tester — each own
one kind of artifact and are each a real Claude Code subagent
(`.claude/agents/`). A new request is a one-line prompt to
`/new-requirement` or a plain-language ask; everything else — current
scope, architecture, who does what, what's already built — is read
automatically from `docs/`, `traceability/`, and `tracker/` before any
work starts. See `docs/03-spec-driven-development-playbook.md` for the
full process and `docs/04-new-requirement-intake.md` for exactly what to
type.

| Read this to answer... | ...this question |
|---|---|
| `docs/00-roles-and-responsibilities.md` | Who does what, and which subagent enforces it |
| `docs/01-po-requirements.md` | What are we building, and why |
| `docs/02-techlead-design.md` | How is it architected |
| `docs/03-spec-driven-development-playbook.md` | How does the process itself work |
| `docs/04-new-requirement-intake.md` | What do I type to start new work |
| `traceability/TRACEABILITY_MATRIX.md` | What code/tests cover requirement X, right now |
| `tracker/PROJECT_TRACKER.md` | What has actually happened, in order, with proof |

## Running it

```bash
mvn test                     # 22 tests, all passing as of PO-01
mvn spring-boot:run          # starts on :8080
```

## Sample requests

```bash
# Add a book
curl -X POST localhost:8080/api/v1/books -H 'Content-Type: application/json' \
  -d '{"isbn":"978-0-13-468599-1","title":"Effective Java","author":"Joshua Bloch","totalCopies":2}'

# Register a member
curl -X POST localhost:8080/api/v1/members -H 'Content-Type: application/json' \
  -d '{"name":"Ada Lovelace","email":"ada@example.com"}'

# Borrow (use the ids returned above)
curl -X POST localhost:8080/api/v1/loans -H 'Content-Type: application/json' \
  -d '{"bookId":"<bookId>","memberId":"<memberId>"}'

# Return
curl -X POST localhost:8080/api/v1/loans/<loanId>/return

# A member's active loans / library-wide overdue loans
curl localhost:8080/api/v1/members/<memberId>/loans
curl localhost:8080/api/v1/loans/overdue
```

## Adding a new feature

Run `/new-requirement <what you need, and why>` — see
`docs/04-new-requirement-intake.md`. To pick up in-flight work with no
further detail, run `/resume-project`.
