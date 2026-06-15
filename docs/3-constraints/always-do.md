# Always Do

✅ These behaviors are mandatory in every session.

## Session start
- ✅ Let the `.codex` SessionStart hook inject git state and current-plan context
- ✅ If the backend dev server is not running, run the platform init script before coding
- ✅ Read `docs/4-tasks/CURRENT_PLAN.md` and the active feature `spec.md`
- ✅ Read `docs/2-designs/` before changing API or DB behavior
- ✅ Check `docs/3-constraints/never-do.md` before non-trivial changes

## Before committing
- ✅ Run `npm --prefix personal_crm_web run lint`
- ✅ Run `mvn -f personal_crm_backend/pom.xml test`
- ✅ Confirm no secrets are staged
- ✅ Update `docs/4-tasks/features/<active-task>/tasks.md`
- ✅ Update `docs/4-tasks/CURRENT_PLAN.md`
- ✅ Keep `docs/2-designs/` in sync with implementation when contracts change

## General
- ✅ Use explicit types where the stack expects them
- ✅ Define constants for repeated values
- ✅ Write tests before calling the task done
- ✅ Re-run tests after each logical change
- ✅ Sync long-lived Harness documents (`docs/1-requirements/`, `docs/2-designs/`, `docs/3-constraints/`, `AGENTS.md`) whenever you change what they describe

## From dev-standards repo
Source: `https://raw.githubusercontent.com/weiqiang612/dev-standards/main`

### Universal
- ✅ ALWAYS inspect staged `git diff` for credentials, debug comments, and temporary hacks
- ✅ ALWAYS keep methods, components, and classes single-responsibility
- ✅ ALWAYS clean up temporary debug outputs before check-in

### Java / Spring
- ✅ ALWAYS use constructor-based injection, preferably with `@RequiredArgsConstructor`
- ✅ ALWAYS implement global exception handling using unified `@RestControllerAdvice`
- ✅ ALWAYS validate controller input with `@Validated` and strict DTO annotations
- ✅ ALWAYS keep transaction scopes narrow
- ✅ ALWAYS use explicit Java types, `Objects.equals`, and constants for repeated literals

### Vue / Pinia
- ✅ ALWAYS use `<script setup lang="ts">` in Vue components
- ✅ ALWAYS declare reactive state using `ref()` under the current team standard
- ✅ ALWAYS keep Vue components small and split them once complexity grows
- ✅ ALWAYS clean up event listeners, intervals, and observers in `onUnmounted()`
- ✅ ALWAYS scope component styles and prefer CSS variables for styling tokens
- ✅ ALWAYS implement Pinia stores as Setup Stores
- ✅ ALWAYS reset sensitive store state such as auth/session data on logout
