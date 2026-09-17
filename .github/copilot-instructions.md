# Copilot instructions

This repo's actual agent instructions live in `AGENTS.md` at the repo
root — read it in full before doing anything else. This file exists only
because Copilot Chat sessions in an IDE don't always pick up `AGENTS.md`
by default; Copilot's coding agent already reads `AGENTS.md` natively.

In short: `AGENTS.md` §1 names a fixed reading order
(`docs/00-roles-and-responsibilities.md` through
`tracker/PROJECT_TRACKER.md`) to follow before any change, a persona
discipline (Product Owner / Tech Lead / Developer / QA — see
`docs/00-roles-and-responsibilities.md`) to stay inside, and a hard rule
to keep `tracker/PROJECT_TRACKER.md` and
`traceability/TRACEABILITY_MATRIX.md` updated with real, run command
evidence for every unit of work. Do not duplicate or fork those rules
here — if they need to change, change `AGENTS.md`.
