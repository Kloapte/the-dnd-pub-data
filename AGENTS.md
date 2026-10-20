# The DnD Pub — Data Backend

## Project guidance

- `docs/THE_DND_PUB_BACKEND_ROADMAP.md` is authoritative for progress, scope, and separately authorized milestones. Do not hardcode current milestone status here.
- `docs/README.md` and `analysis/README.md` index current documentation and historical analysis. Consult only the entries relevant to the task.
- Before relocating evidence, reports, or scripts, consult `docs/development/DOCUMENTATION-CONSOLIDATION.md`.
- Use `.agents/skills/dnd-json-mapper/SKILL.md` (`$dnd-json-mapper`) when introducing or materially changing source JSON mappings.
- Historical reports describe their original scope and do not authorize new work.

## Technology and code conventions

- Java 21+, Spring Boot 4, Jackson, and JUnit 5; follow the repository's actual build and formatting configuration.
- Prefer readable, maintainable Java: four-space indentation, descriptive names, braces for control flow, and one statement per line. Split dense expressions and long declarations where useful; aim for approximately 120 characters per line when practical.
- Keep one public top-level class, record, interface, or enum per Java file. Do not nest DTO types inside other DTOs.
- Prefer Java records for immutable transfer and domain data, sealed interfaces for bounded polymorphism, and defensive copies for mutable collections.
- Model known shapes with explicit types; avoid `Object`, `JsonNode`, and generic maps in application-facing models. Use custom Jackson deserializers only when needed.
- Use meaningful comments for non-obvious constraints; do not restate the code. Keep changes focused and the final diff reviewable. Never compress code to save tokens.
- Prefer extending an existing cohesive class over introducing another service, factory, registry or wrapper unless the new abstraction has a demonstrably separate responsibility.
- The normal test suite must not depend on generated analysis artifacts. Tests should use self-contained fixtures or actual pinned source data where appropriate.

## Source ingestion and normalization

- Treat third-party D&D JSON as input, not the permanent application domain model. Keep source DTEs, normalized definitions, and public DTOs separate.
- Name source-mapping records with the `Dte` suffix; use `@JsonProperty` where source field names differ.
- Preserve source distinctions and data, including relevant absent, null, and empty states. Never silently discard unknown fields or skip supported records.
- Do not modify pinned source JSON. For large files, use targeted searches and scripts rather than loading entire files into model context.
- For new or materially changed import models, inspect the full relevant source schema, including polymorphism, copy inheritance, versions, references, and exceptional shapes. Check for existing types before adding new ones.
- Preserve source-aware stable identities and distinguish definitions, references, and entity-specific configurations.
- Validate affected mappings against the full applicable corpus; report unsupported structures, unresolved references, and skipped or failed entries.

## Mechanics, evidence, and publication

- Never invent mechanical values, source relationships, resolved references, or provenance. Preserve ambiguity and unsupported constructs explicitly; abstain when source evidence is insufficient.
- Keep authored observations separate from typed interpretations, review decisions, licensing assessments, and publication authorization.
- Preserve exact source ownership, revisions/fingerprints, evidence pointers and spans, and stable mechanic identities when producing interpretations.
- Do not treat a successful extractor or benchmark candidate match as human verification. Never automatically approve fixtures, mark mechanics verified, grant a license status, or authorize publication.
- Do not expose raw third-party source text, internal provenance, or media paths in externally publishable APIs without an explicit publication decision. Preserve the separately defined private inspection contract; do not mistake private inspection for publication authorization.

## REST and serialization

- Keep controllers thin; separate loading, normalization, indexing, querying, and HTTP handling. Prefer immutable startup catalogs for static reference data rather than unnecessary persistence.
- Use stable, source-aware IDs, deterministic ordering, consistent validation and errors, and pagination for potentially large collections.
- Keep public API projections distinct from internal import and inspection models. Use summary DTOs for collections and detail DTOs for individual resources where appropriate.
- Annotate new public API DTO records with `@JsonInclude(JsonInclude.Include.NON_EMPTY)` unless a documented contract requires otherwise. Omit absent/empty optional values without suppressing meaningful `false` or `0` values; avoid empty nested objects.
- Do not change global Jackson settings for a local DTO issue. Test serialization and preserve established REST contracts unless the task explicitly authorizes changes.

## Codex execution workflow

- Read these instructions and the relevant roadmap section, then inspect only the code and documentation needed for the task. Prefer targeted searches; consult historical analysis only to answer a concrete question.
- Implement the authorized scope directly after enough targeted inspection to understand it. Reuse existing abstractions and avoid speculative frameworks, unrelated refactors, and unsolicited follow-up work.
- Run focused tests while developing. Run the full applicable test suite once after implementation, repeating it only if later changes invalidate the result.
- Run full-corpus, source-hash, provenance, packaged-JAR, or publication-boundary checks when the change affects those guarantees or the task requires them. Never skip necessary verification solely to reduce tokens.
- Distinguish pre-existing failures from regressions. Check formatting and `git diff --check`; ensure the final diff contains no unrelated changes.
- Keep completion reports concise: changes made, observable results and coverage, verification performed, and remaining limitations. Write detailed reports when the roadmap or task explicitly requires them.
- Stop after the requested milestone and its verification. Do not start the next roadmap step without authorization.
