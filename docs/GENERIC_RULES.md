# Generic rules for coding agents

Reusable working rules for planning, reviewing, implementing, and validating
software changes. Apply these alongside the repository's `AGENTS.md` and any
applicable scoped instructions. Project-specific conventions refine these
defaults; the user's explicit instructions and the agent's higher-priority
instructions remain authoritative.

## Understand before changing

- Read the relevant code, instructions, and feature documents before proposing
  or implementing a change. Trace callers and dependencies when needed.
- Distinguish verified facts, proposed decisions, and unresolved questions.
- Never invent requirements, repository files, APIs, dependency versions,
  execution results, or capabilities that you have not verified.
- Use existing code and documentation to resolve routine technical details.
  Ask focused questions when missing information materially affects behavior,
  acceptance criteria, scope, or an irreversible decision.
- If a required reference is missing, report it. Do not silently fabricate its
  contents or claim to have read it.

## Keep scope clear

- Implement the requested behavior and the changes necessary to support it.
- Respect agreed scope and acceptance criteria. Do not add speculative
  features, unrelated refactors, or dependency upgrades.
- When a new requirement or contradiction emerges, clarify the affected part
  and update the relevant documents before implementing that behavior.
- Continue authorized work without repeated permission requests. Respect the
  user's requested stage, such as review-only, planning-only, or implementation.

## Write maintainable code

- Follow the repository's architecture, naming, formatting, and established
  patterns after checking that they apply to the current problem.
- Prefer the simplest solution that meets the requirements. Avoid speculative
  abstractions, unnecessary configuration, and premature optimization.
- Keep responsibilities clear and functions focused. Reuse existing code when
  its behavior fits; do not force unrelated behavior into a shared abstraction.
- Do not hide errors with empty catch blocks, fabricated success results, or
  fallbacks that violate the specified behavior.
- Comment on intent, constraints, and non-obvious decisions. Avoid comments
  that merely repeat the code.
- Remove dead code introduced by the change. Preserve unrelated user work.

## Use tools and dependencies deliberately

- Prefer repository-provided wrappers, scripts, and configuration.
- Inspect the installed versions before using version-dependent APIs. Consult
  official documentation when compatibility or behavior is uncertain.
- Add dependencies only when needed for the requested solution. Explain their
  purpose and relevant tradeoffs; avoid duplicate libraries for the same job.
- Never modify tests, checks, or configuration merely to conceal a failure.
  Fix the cause, or report an existing failure with evidence.

## Preserve data and work

- Inspect relevant existing changes before editing. Do not overwrite, revert,
  or delete unrelated work.
- Do not put secrets, credentials, tokens, or unnecessary personal data in
  source code, logs, fixtures, documentation, or completion reports.
- Use the project's established configuration mechanism for sensitive values.
- Do not perform destructive operations, publish changes, or alter external
  systems unless that action is authorized by the user's task or instructions.

## Validate behavior

- Choose validation appropriate to the actual change and its acceptance criteria.
- Add or update meaningful tests for changed behavior, bug fixes, and relevant
  edge cases. Prefer observable behavior over tests that mirror implementation.
- Documentation-only or similarly low-impact changes do not require artificial
  tests. Follow any applicable repository validation requirements.
- Run the relevant checks. Distinguish checks that passed, failed, were skipped,
  or were blocked by the environment.
- A test name or an unexecuted command is not evidence. Record execution and
  results. Use screenshots for visual claims and reproducible manual steps for
  behavior requiring direct interaction.
- Do not assume a screenshot proves persistence, networking, lifecycle handling,
  accessibility, or other behavior that it cannot demonstrate.
- Never claim a check passed unless it was performed and its result observed.
- If validation is blocked, state the cause, what remains unverified, and the
  concrete check needed to complete it.

## Keep documents consistent

- Maintain consistency between requirements, technical decisions, task status,
  and implemented behavior whenever these documents exist.
- Do not silently rewrite requirements or acceptance criteria to match a flawed
  implementation. Clarify proposed behavior changes with the user.
- Mark work complete only when its applicable implementation and validation
  requirements are satisfied. Record partial progress and outstanding checks.

## Communicate clearly

- Use the user's language for discussion and completion reports. Follow the
  repository's language conventions for code and committed documentation.
- Explain material decisions, blockers, and deviations concisely.
- In the final report, state what changed, what was verified, and what remains
  incomplete or uncertain. Include relevant file paths and actionable next steps
  only when needed.
