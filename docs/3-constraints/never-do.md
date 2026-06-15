# Never Do

🚫 These are absolute prohibitions. No exceptions.

## Security & secrets
- 🚫 Commit secrets, credentials, API keys, or tokens to the repository
- 🚫 Log passwords, raw JWTs, or sensitive user data in plain text
- 🚫 Skip server-side validation on public APIs

## File boundaries
- 🚫 Edit generated directories such as `personal_crm_web/node_modules/`, `personal_crm_web/dist/`, `personal_crm_backend/target/`
- 🚫 Modify CI, deployment, or production configuration without explicit approval
- 🚫 Modify files outside the active spec scope once feature work starts

## Database & API design contracts
- 🚫 Change API request or response structures without updating `docs/2-designs/api_contract.md`
- 🚫 Change database schema, entity constraints, or table meanings without updating `docs/2-designs/db_schema.md`
- 🚫 Introduce Agent write paths that bypass existing business Service checks

## Tests
- 🚫 Delete or weaken a failing test to make the build pass
- 🚫 Replace assertions with logs

## Naming, Exceptions & Hardcoding
- 🚫 Use meaningless identifiers like `a`, `b`, `c`, or vague pinyin abbreviations in naming
- 🚫 Catch exceptions silently (empty catch blocks) or use naked catches
- 🚫 Run database CRUD operations directly on unvalidated inputs
- 🚫 Log sensitive user data (passwords, phone numbers, etc.)
- 🚫 Hardcode fixed parameters, status codes, URLs, ports, or timeouts in business code

## From dev-standards repo
Source: `https://raw.githubusercontent.com/weiqiang612/dev-standards/main`

### Universal
- 🚫 NEVER bypass linting, static analysis, or compiler warnings with blanket ignores without a tracked reason
- 🚫 NEVER force-push directly to protected branches
- 🚫 NEVER commit binary assets larger than 5MB directly into the repository
- 🚫 NEVER leave commented-out production code blocks in standard source files
- 🚫 NEVER bypass local validation hooks with `git commit --no-verify`
- 🚫 NEVER hardcode environment-specific configuration values

### Java / Spring
- 🚫 NEVER use `var`; keep explicit Java types
- 🚫 NEVER use field injection; always prefer constructor injection
- 🚫 NEVER apply `@Transactional` to Controller, DAO, or Repository layers
- 🚫 NEVER perform slow external IO inside active database transactions
- 🚫 NEVER use pinyin, mixed-language, or Chinese identifiers in Java code
- 🚫 NEVER set default values inside POJO classes
- 🚫 NEVER prefix POJO boolean fields with `is`

### Vue / Pinia
- 🚫 NEVER use the Vue Options API; use Composition API with `<script setup>`
- 🚫 NEVER use Vuex; use Pinia for global state
- 🚫 NEVER write Pinia Option Stores; use Setup Stores only
- 🚫 NEVER destructure Pinia state directly in components; use `storeToRefs`
- 🚫 NEVER mutate `props` directly
- 🚫 NEVER place business logic or API integration directly in Vue templates
- 🚫 NEVER use a single-word component filename
