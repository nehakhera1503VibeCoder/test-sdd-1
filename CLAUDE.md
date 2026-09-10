# CLAUDE.md

This file is the entry point for any Claude Code session working in this
repository. Read it first, then follow §1 before doing anything else.

## What this project is

A small Spring Boot REST service (library book catalog + member lending)
that exists to demonstrate a **spec-driven, multi-persona (Product Owner /
Tech Lead / Developer / QA), requirement-traceable delivery process** —
both goals are real: it's a working service, and it's a template for
running that process on any new project. See
`docs/03-spec-driven-development-playbook.md` for the process itself and
`tracker/PROJECT_TRACKER.md` for this project's complete, dated history
from requirement to sign-off.

## §1. Before doing anything: read these, in order

1. `docs/00-roles-and-responsibilities.md` — the four personas, what each
   owns, and the subagent each one maps to.
2. `docs/01-po-requirements.md` — the PRD. Binding scope and acceptance
   criteria.
3. `docs/02-techlead-design.md` — the architecture, package layout, and
   the design decisions named explicitly (e.g. why "overdue" is computed,
   not stored).
4. `docs/03-spec-driven-development-playbook.md` — the process: ID
   conventions, the resume protocol, the propose-new-work protocol,
   copy-paste templates. **Read this before creating any new tracker or
   matrix row.**
5. `docs/04-new-requirement-intake.md` — exactly what to type for a new
   request, and what happens automatically as a result.
6. `traceability/TRACEABILITY_MATRIX.md` — per requirement, what design
   section, what code, and what tests cover it right now.
7. `tracker/PROJECT_TRACKER.md` — the actual history. Read end to end,
   especially the last rows, "Open Items / Blockers", and the "Phase
   Overview" checklist.

If asked to "continue the project" with no further detail, those seven
reads are the entire briefing needed — see `.claude/commands/resume-project.md`.
If asked to build something new, see `.claude/commands/new-requirement.md`.

## Hard rules

- **Stay in persona.** Use the matching subagent (`.claude/agents/*.md`)
  for each phase — Product Owner, Tech Lead, Developer, QA/Tester — rather
  than blending them into one pass. See `docs/00-roles-and-responsibilities.md`.
- **Keep the tracker updated.** Every unit of work gets its own row, with
  real verification evidence (a command actually run, its actual output),
  not a description of intent. `docs/03-spec-driven-development-playbook.md`
  §4 and §7.
- **Keep the traceability matrix current.** A new `REQ-` row without a
  matching `traceability/TRACEABILITY_MATRIX.md` row is a process defect —
  flag it in the next Tech Lead review, don't let it slide.
- **Actually run things.** `mvn compile` / `mvn test` / `mvn spring-boot:run`
  (or the packaged jar) — verify claims by executing them. A tracker row
  that says "implemented" without a command and its output is not
  following the convention.
- **New scope → new `REQ-`.** Don't improvise new behavior mid-implementation;
  follow the propose-new-work protocol (playbook §6) — even for a small
  ask, the artifacts still get written in each persona's voice and logged.

## Quick reference

```bash
mvn compile                  # verify it builds
mvn test                     # run the full suite (22 tests as of PO-01)
mvn spring-boot:run          # run it locally — REST API at /api/v1/{books,members,loans}
mvn clean package            # produce the runnable fat jar
java -jar target/library-lending-service-0.1.0-SNAPSHOT.jar   # run the packaged jar standalone
```

See `README.md` for sample `curl` requests covering the full lending
lifecycle.
