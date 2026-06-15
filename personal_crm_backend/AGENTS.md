# AGENTS.md — personal_crm_backend

> Supplements the root `AGENTS.md`. All root rules apply unless explicitly overridden.

## Module context
- **Purpose**: Spring Boot backend for authentication, contacts, todos, dashboard, file upload, and Contact Agent orchestration
- **Port**: 8080
- **Entry point**: `com.weiqiang.personal_crm_backend.PersonalCrmBackendApplication`

## Commands
- **Run**: `mvn spring-boot:run -pl personal_crm_backend -Dspring-boot.run.profiles=dev`
- **Test**: `mvn -f personal_crm_backend/pom.xml test`

## Module-specific constraints
<!-- Rules that differ from or extend root constraints for this module only -->
- 🚫 Controller MUST NOT import or call Repository/Mapper directly.
- 🚫 Expose exception stack traces in API responses.
- 🚫 Return raw HashMap or Hashtable as response body — use typed VO classes.

## Documentation
- Requirements: root `docs/1-requirements/`
- Designs: root `docs/2-designs/`
- Constraints: root `docs/3-constraints/`
- Tasks: root `docs/4-tasks/`
