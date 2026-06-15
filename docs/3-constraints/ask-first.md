# Ask First

⚠️ Confirm before taking any of these actions.

## Dependencies
- ⚠️ Add a new dependency or plugin
- ⚠️ Upgrade a major dependency version

## Architecture & structure
- ⚠️ Introduce a new architectural layer or abstraction
- ⚠️ Rename a public API, type, database table, or module
- ⚠️ Move modules across directories/packages
- ⚠️ Add a new service or submodule

## Database & API contracts
- ⚠️ Add or modify API endpoints in a breaking way
- ⚠️ Create, modify, or delete migration files
- ⚠️ Change entity field types, names, indexes, or constraints
- ⚠️ Add or remove indexes

## CI / deployment
- ⚠️ Modify `.github/workflows/`, `Dockerfile`, or `docker-compose.yml`
- ⚠️ Change production or staging environment configuration

## From dev-standards repo
Source: `https://raw.githubusercontent.com/weiqiang612/dev-standards/main`

### Universal
- ⚠️ ASK FIRST before adding new external packages, Maven dependencies, or Node libraries
- ⚠️ ASK FIRST before introducing database schema modifications or raw SQL migrations
- ⚠️ ASK FIRST before changing core system directories or base architectural naming rules
- ⚠️ ASK FIRST before introducing a new architectural pattern, utility framework, or cross-cutting library
- ⚠️ ASK FIRST before upgrading the language compiler or major framework version

### Java / Spring
- ⚠️ ASK FIRST before introducing new thread-pool setups or changing core execution boundaries
- ⚠️ ASK FIRST before establishing new package segments or altering the global backend layering structure
- ⚠️ ASK FIRST before introducing new global filters, interceptors, or complex AOP aspects
- ⚠️ ASK FIRST before creating custom Spring Boot starter modules or advanced configuration beans

### Vue / Pinia
- ⚠️ ASK FIRST before adding new global Vue directives, plugins, or third-party UI component packages
- ⚠️ ASK FIRST before writing custom composables that bypass Pinia state managers
- ⚠️ ASK FIRST before establishing complex Pinia plugins, persistence drivers, or store-to-store circular dependencies
- ⚠️ ASK FIRST before creating highly global stores spanning multiple major domain areas
