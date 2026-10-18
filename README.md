# the-dnd-pub-data

The DnD Pub's Java data backend for the pinned 5eTools v2.37.0 corpus.

## Start here

- **[Backend roadmap](docs/THE_DND_PUB_BACKEND_ROADMAP.md)** — authoritative progress and future scope.
- [Current documentation](docs/README.md) — architecture, mechanics model, API and development workflow.
- [Agent instructions](AGENTS.md) — repository conventions and task boundaries.
- [Analysis and historical evidence](analysis/README.md) — indexed reports and reproducibility assets.

The pipeline is source JSON → typed DTEs → immutable normalized definitions → immutable catalogs →
dedicated REST projections. Java 21 and Spring Boot 4 are configured in [pom.xml](pom.xml).

## Build

Use the Maven wrapper (`./mvnw package` or `.\mvnw.cmd package`) or an installed Maven executable.
Run the resulting Spring Boot JAR from `target/`. See [development workflow](docs/development/WORKFLOW.md)
for verification commands and artifact precautions.

## Current inspection API

Spell detail and by-name responses support `include=interpretedMechanics`. The current envelope
reports interpretation **NOT_ATTEMPTED**, not an absence of mechanics. Ordinary responses are unchanged.
See the [Spell inspection contract](docs/api/SPELL-INSPECTION.md).

The inspection API assumes the owner's private network. External publication and backend authorization
remain separate future work. The [application license](LICENSE) must not be treated as blanket
publication permission for the bundled source dataset or derived evidence.
