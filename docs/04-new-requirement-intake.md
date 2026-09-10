# New Requirement Intake

**Goal:** a new request should be a one- or two-sentence prompt. Everything
else it needs — the roles, the process, the current state of the project —
already lives in this repo and gets loaded automatically. This document is
what makes that true, and what to type.

## What to actually type

Use the slash command:

```
/new-requirement Let a member place a hold on a fully-borrowed book, and notify them when it's returned.
```

or, without the command, just say it in-session — the same
`docs/03-spec-driven-development-playbook.md` §6 protocol is what
`CLAUDE.md` tells any session to follow for a new-scope request either way.
`/new-requirement` exists so you don't have to remember that; it exists at
`.claude/commands/new-requirement.md`.

That one line is the entire input. You do not need to (and shouldn't) also
explain: what this project does, how the layers are structured, what the
persona roles are, or what tracker ID comes next. All of that is already on
disk and gets read before any work starts.

## What happens automatically, in order

1. **Context load.** The session reads `CLAUDE.md`, which points at (and
   requires reading, in order) `docs/00-roles-and-responsibilities.md`,
   `docs/01-po-requirements.md`, `docs/02-techlead-design.md`, this file,
   `traceability/TRACEABILITY_MATRIX.md`, and `tracker/PROJECT_TRACKER.md`.
   Nothing about your one-line ask needs to restate any of that.
2. **Product Owner pass** (`@agent-product-owner` or the command's first
   phase): drafts a `REQ-XXX` row and a PRD delta, resolves any open
   question it can, flags any it can't.
3. **Tech Lead pass**: drafts a `DES-XXX` row and a design delta — what's
   reused, what's new, any real decision named explicitly.
4. **Developer pass**: implements it story by story, `DEV-XXX` rows, unit
   tests per story, actually running `mvn compile`/`mvn test`.
5. **QA/Tester pass**: independent full-stack verification, `TEST-XXX` row,
   with its own hand-derived expected values.
6. **Sign-off**: Product Owner reviews the actual delivered behavior
   against the PRD's acceptance criteria and logs `PO-XXX`.
7. **Traceability updated throughout** — the matrix row for the new `REQ-`
   gets its Design/Implementation/Tests columns filled in as steps 3-5
   happen, not retrofitted afterward.

## Resuming instead of starting new work

If instead you want to pick up in-flight work with no further detail, say
so (or use `/resume-project`, `.claude/commands/resume-project.md`) — that
follows `docs/03-spec-driven-development-playbook.md` §5 instead: read the
same files, then resume at whatever the tracker's last rows and "Open
Items" section say is next, in the same persona, logging the same way.

## Why a one-liner is enough

Every piece of context a human would otherwise have to restate is durable,
on-disk state, not conversation history:

- **"What does this system do"** → `docs/01-po-requirements.md`.
- **"How is it built"** → `docs/02-techlead-design.md`.
- **"Who does what"** → `docs/00-roles-and-responsibilities.md`, enforced
  by the `.claude/agents/*.md` subagent definitions.
- **"What's already done, and what's next"** → `tracker/PROJECT_TRACKER.md`.
- **"What covers requirement X, and is it fully covered"** →
  `traceability/TRACEABILITY_MATRIX.md`.

A fresh Claude Code session — or a different person entirely — reading
those five things has the same picture as anyone who was in the room for
every prior decision.
