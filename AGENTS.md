# AGENTS.md

## Project
- **Name**: Personal CRM Intelligent Contact Management Platform
- **Stack**: Spring Boot 3.5 + Java 17 + MyBatis-Plus + MySQL 8 + JWT + Vue 3 + Vite + TypeScript + Pinia + Vue Router + Element Plus + ECharts
- **Rule**: This file is an index only. All details live in `docs/`.

## Session start (Mandatory Checklist)
This project uses the Antigravity Session Start Protocol. The agent MUST run the following diagnostics automatically at the beginning of every session:
1. Verify if the dev server is running on port 8080:
   - Windows: `powershell -Command "Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue"`
   - UNIX/macOS: `lsof -t -i:8080`
2. Get Git branch & status: `git branch --show-current` and `git status -s`
3. Get recent commits: `git log -n 3 --oneline`
4. Read `docs/4-tasks/CURRENT_PLAN.md` to identify the active feature.
5. If the dev server is NOT running, run `.\init.ps1` (Windows PowerShell) or `bash init.sh` (UNIX) to start the environment.

## Commands
- **Build**: `mvn -f personal_crm_backend/pom.xml package -DskipTests && npm --prefix personal_crm_web run build`
- **Test**: `mvn -f personal_crm_backend/pom.xml test`
- **Lint**: `# none configured`

## Boundaries (read before acting)
| Before you...                         | Read this first                         |
|---------------------------------------|-----------------------------------------|
| Write or modify any code              | `docs/3-constraints/never-do.md`        |
| Take any action you are unsure about  | `docs/3-constraints/ask-first.md`       |
| Start a session                       | `docs/3-constraints/always-do.md`       |
| Understand business context           | `docs/1-requirements/project_overview.md` |
| Touch any database or API             | `docs/2-designs/`                       |
| Work on a specific task               | `docs/4-tasks/features/<TASK-NNN>/spec.md` |
| Start a new session                   | `docs/4-tasks/CURRENT_PLAN.md`          |
| Make an architectural decision        | `docs/3-constraints/adr/`               |

## Workflow
1. Verify that the dev server is running on port 8080 by executing the Session Start diagnostics. If not running, run `.\init.ps1` (Windows) or `bash init.sh` (Unix).
2. Read `docs/4-tasks/CURRENT_PLAN.md` — orient to current stage and active feature.
3. Read `docs/1-requirements/` and `docs/2-designs/` to align with business and design spec.
4. Read `docs/4-tasks/features/<active-task>/spec.md` — do not start without a confirmed spec.
5. Check `docs/3-constraints/never-do.md` before every non-trivial change.
6. Run `mvn -f personal_crm_backend/pom.xml test` early and often. Fix failures before continuing.
7. Commit working increments — do not accumulate large uncommitted diffs.
8. Update task progress in `docs/4-tasks/features/<active-task>/tasks.md` as you go.
9. Update `docs/4-tasks/CURRENT_PLAN.md` when the feature is complete.

## Submodules
| Module       | AGENTS.md                    | Port       |
|--------------|------------------------------|------------|
| personal_crm_backend | `personal_crm_backend/AGENTS.md` | 8080       |
| personal_crm_web | `personal_crm_web/AGENTS.md` | 5173       |
