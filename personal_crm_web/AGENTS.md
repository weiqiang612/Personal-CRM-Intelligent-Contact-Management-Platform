# AGENTS.md — personal_crm_web

> Supplements the root `AGENTS.md`. All root rules apply unless explicitly overridden.

## Module context
- **Purpose**: Vue 3 frontend for login, contacts, blacklist, todos, dashboard, and Contact Agent UI
- **Port**: 5173
- **Entry point**: `src/main.ts`

## Commands
- **Run**: `npm --prefix personal_crm_web run dev`
- **Test**: `npm --prefix personal_crm_web run build`

## Module-specific constraints
<!-- Rules that differ from or extend root constraints for this module only -->
- 🚫 Use Options API in new components — always use Composition API + `<script setup>`.
- 🚫 Mutate props directly — emit an event and let the parent update state.
- 🚫 Standardize on ref() only, never use reactive().

## Documentation
- Requirements: root `docs/1-requirements/`
- Designs: root `docs/2-designs/`
- Constraints: root `docs/3-constraints/`
- Tasks: root `docs/4-tasks/`
