---
name: dnd-json-mapper
description: Analyze, import, normalize, and expose D&D JSON entity types through strongly typed Java DTEs, DTOs, services, and REST endpoints. Includes schema discovery, copy/version resolution, mechanical analysis, catalog design, and full-corpus validation.
---

# D&D JSON Import and Normalization

## Purpose

Provide a repeatable, reliable workflow for introducing D&D entity types into The DnD Pub.

Supported examples include:

- Monsters
- Spells
- Feats
- Actions
- Languages
- Skills
- Senses
- Conditions
- Damage types
- Items
- Optional features
- Other structured D&D rules entities

The objective is not merely to deserialize JSON.

The objective is to understand the source data, design an appropriate application model, preserve useful mechanics and references, and expose clean, maintainable REST APIs.

Always follow `AGENTS.md`.

## Core Principles

1. Analyze before implementing.
2. Use the complete available source corpus, not a few convenient examples.
3. Preserve source semantics without inventing missing information.
4. Keep DTEs, normalized DTOs, public API DTOs, and internal provenance conceptually separate.
5. Prefer strongly typed Java records and sealed interfaces.
6. Use separate source files for top-level DTO types.
7. Use `@JsonInclude(JsonInclude.Include.NON_EMPTY)` for public API DTO records.
8. Avoid exposing raw source JSON structures directly through public APIs.
9. Prefer reusable definitions and references over duplicated entity data.
10. Build incrementally, validate each phase, and report uncertainty.

Do not implement all phases blindly when a design decision requires user approval.

---

# Phase 1 — Source Discovery and Schema Analysis

Before writing production code, inspect the complete dataset for the requested entity type.

Determine:

- Relevant JSON files and source directories.
- Number of files and records.
- Root properties and their frequencies.
- Nested property paths.
- Property types and polymorphic structures.
- Optional, nullable, and required fields.
- Explicit-null occurrences.
- Rare structures and edge cases.
- Cross-file references.
- References to other entity types.
- Copy inheritance and template instructions.
- Version definitions and generated variants.
- Differences between source books or rules editions.
- Source-specific presentation metadata.
- Fields containing rules mechanics.
- Fields containing narrative or potentially copyrighted descriptions.

Reuse existing schema-discovery tooling where possible.

Do not assume that every entity type uses the monster copy/version model.

Generate analysis artifacts under:

`analysis/<entity-type>/`

Include:

- Source inventory.
- Field inventory.
- Property-path coverage.
- Representative examples.
- Unusual or unsupported structures.
- Cross-entity reference inventory.

Do not implement production DTEs or DTOs during this phase unless explicitly requested.

---

# Phase 2 — DTE Design and Source Resolution

Design strongly typed Java DTEs representing the source schema.

Requirements:

- Use Jackson-compatible Java records where appropriate.
- Model polymorphic source structures explicitly.
- Use sealed interfaces when they improve type safety.
- Avoid `Object`, `Map<String, Object>`, and generic `JsonNode` in public DTE components.
- Keep source-specific names and semantics where necessary.
- Preserve explicit-null behavior.
- Preserve rich text and nested entries without flattening or losing information.
- Handle source-specific fields that are absent from other entities.

Investigate whether the entity type requires:

- Copy resolution.
- Template application.
- Version expansion.
- Reference resolution.
- Other source-specific preprocessing.

Reuse existing resolver infrastructure when compatible.

Do not force unrelated entity types through monster-specific resolvers.

Use deterministic identity and resolution rules.

Add focused tests and full-corpus deserialization tests.

---

# Phase 3 — DTO and Domain Model Analysis

This is a mandatory design checkpoint.

Do not immediately implement DTOs.

Analyze the discovered source data and propose the appropriate normalized application model.

Identify:

### Identity and classification

- Stable definition identity.
- Name and aliases.
- Source book.
- Rules edition.
- Source references.
- Reprint relationships.

### Mechanical information

- Actions and action economy.
- Attacks and damage.
- Saving throws and checks.
- Conditions and effects.
- Resource usage and recharge.
- Range, targeting, and area.
- Duration and concentration.
- Prerequisites.
- Scaling and progression.
- Granted features.
- Equipment modifiers.

Only include categories supported by the actual entity data.

### Reusable references

Identify opportunities to reference existing definitions:

- Spells
- Actions
- Feats
- Skills
- Languages
- Senses
- Conditions
- Damage types
- Items
- Creature types
- Other rules definitions

Distinguish:

1. Reusable definition.
2. Reference to a definition.
3. Entity-specific configuration or override.

Do not automatically duplicate referenced definitions.

### Presentation and source metadata

Identify fields that belong only to the source dataset, such as:

- Internal asset paths.
- Media availability flags.
- Foundry-specific fields.
- Search-index tags.
- Source rendering instructions.
- Import lineage.
- Diagnostic metadata.

Determine which fields belong in:

- Normalized internal DTOs.
- Public API DTOs.
- Internal catalog/search indexes.
- Import provenance.
- Explicitly excluded source metadata.

### Copyright and publication

Identify content that may require publication rights.

Do not assume converting descriptions into mechanical fields automatically makes them publishable.

Prefer appropriately licensed rules content and independently authored descriptions for public APIs.

Keep publication decisions separate from import correctness.

### Design deliverable

Create:

`analysis/<entity-type>/DTO-DESIGN.md`

Document:

- Proposed DTO types.
- Record components.
- Polymorphic hierarchies.
- Reference relationships.
- Fields intentionally excluded from public JSON.
- Mechanical information that can be represented directly.
- Mechanics requiring later interpretation.
- Ambiguous or unsupported structures.
- Expected REST representations.

Present the design for review before implementation unless the user explicitly authorizes continuing.

---

# Phase 4 — DTO and Mapper Implementation

After design approval, implement the normalized DTO model and mapper.

Requirements:

- Use readable Java records.
- One public top-level type per file.
- Organize DTOs into logical packages.
- Use descriptive type names.
- Use defensive collection copies.
- Preserve deterministic output.
- Keep internal provenance separate.
- Avoid duplicating source data unnecessarily.
- Avoid guessing missing mechanics.
- Preserve unresolved references explicitly.
- Use typed values for known mechanical concepts.
- Retain source-specific fallback structures internally where necessary to prevent data loss.

Use the resolved source representation as mapper input when resolution is required.

Do not accidentally lose fields that were not represented by an earlier DTE.

Verify that all discovered source properties have a documented destination or explicit exclusion.

Add representative mapping tests and full-corpus tests.

Create:

`analysis/<entity-type>/DTO-IMPLEMENTATION.md`

---

# Phase 5 — Public API Projection and Serialization

Design a clean public representation separately from internal source normalization where necessary.

Implement:

- Detail DTO.
- Summary DTO.
- Stable identifiers.
- References to reusable definitions.
- Appropriate source/licensing metadata.

Use:

`@JsonInclude(JsonInclude.Include.NON_EMPTY)`

on public API DTO records.

Serialization requirements:

- Omit null properties.
- Omit empty strings.
- Omit empty lists, sets, and maps.
- Preserve meaningful zero and false values.
- Avoid meaningless empty nested objects.
- Preserve required identity and grouping information.
- Do not expose raw import instructions or provenance.
- Do not expose internal media paths or source-specific metadata without approval.
- Do not expose raw copyrighted prose by default.

Do not flatten meaningful structures merely to reduce response size.

If a reference cannot yet be resolved, preserve its identity and record the unresolved status rather than inventing a target.

Test the actual serialized JSON.

---

# Phase 6 — Catalog and Service Layer

Implement a reusable service layer for retrieving normalized definitions.

For static reference datasets, prefer an immutable in-memory catalog unless persistence is justified.

Responsibilities:

### Catalog

- Load and normalize data once at startup.
- Build indexes by stable ID.
- Build name/source lookup indexes.
- Retain internal metadata required for filtering.
- Validate duplicate identities.
- Fail clearly on unexpected import errors.
- Support concurrent reads.
- Work from a packaged Spring Boot JAR.

### Service

- Retrieve definitions by stable ID.
- Retrieve definitions by exact name and source.
- Return paginated summaries.
- Support optional name and source filtering.
- Apply deterministic sorting.
- Handle missing definitions consistently.

Do not repeatedly deserialize or normalize the complete dataset for each request.

Do not introduce JPA or database persistence for static catalogs without a concrete requirement.

---

# Phase 7 — Simple REST Controllers

Implement a minimal read-only REST API for the entity type.

Use existing project routing and naming conventions.

Recommended endpoints:

GET /api/<entities>

Returns paginated summary DTOs.

GET /api/<entities>/{id}

Returns a complete public detail DTO.

GET /api/<entities>/by-name/{name}?source=<source>

Returns a complete definition by exact name and source.

Requirements:

- Thin controllers.
- Constructor injection.
- Service-owned query logic.
- Pagination defaults and maximum page size.
- Deterministic sorting.
- Optional case-insensitive name filtering.
- Optional case-insensitive source filtering.
- Consistent 400 and 404 responses.
- No framework-specific pagination serialization leaks.
- No raw source JSON exposure.
- No internal provenance in responses.

For small fixed reference datasets, pagination may be unnecessary. Document that decision.

Do not introduce write endpoints unless requested.

Add controller and JSON serialization tests.

---

# Phase 8 — Full Validation and Documentation

Run the appropriate focused tests and the full applicable test suite.

Verify:

1. All expected source records are discovered.
2. All supported copies and versions are resolved.
3. All expected definitions normalize successfully.
4. IDs are unique and stable.
5. Mapping is deterministic.
6. No source properties are silently discarded.
7. Polymorphic content is preserved correctly.
8. References are valid or explicitly unresolved.
9. Public JSON omits null and empty properties.
10. Public JSON does not contain unwanted source metadata.
11. Summary pagination and filtering work.
12. Detail retrieval works by ID and name/source.
13. Resource loading works from a packaged application.
14. Existing unrelated entity pipelines still pass their tests.

Document:

- Source counts.
- Generated variant counts.
- DTO structure.
- Mapper coverage.
- Known limitations.
- Unresolved references.
- Unsupported mechanics.
- API endpoints.
- Validation results.

## Cross-Entity Domain Discoveries

For every completed entity type, create or update:

`analysis/domain/DOMAIN-DISCOVERIES.md`

Record:

- Reusable domain concepts discovered.
- Potential references to other entity definitions.
- Candidate shared mechanical types.
- Differences between source-specific and domain-level representations.
- Unresolved questions that may affect later entity types.
- Opportunities to reuse existing DTOs, value objects, or services.

Do not implement speculative shared abstractions.

Use these discoveries when analyzing subsequent entity types so that the application gradually develops a consistent D&D rules model rather than independent, disconnected DTO hierarchies.

Report pre-existing failures separately.

---

# Execution Rules

When invoked for a new entity type:

1. Read `AGENTS.md`.
2. Inspect existing implementations and shared infrastructure.
3. Determine the current implementation phase.
4. Execute only the requested or approved phases.
5. Do not regenerate already completed work unnecessarily.
6. Produce analysis artifacts before significant implementation.
7. Stop at the DTO design checkpoint unless continuation was explicitly authorized.
8. Keep changes scoped to the requested entity type.
9. Do not refactor unrelated entity pipelines.
10. Never compress Java code into dense one-line implementations.

## Shared Source Registry Integration

All new entity pipelines must integrate with the existing shared Source Registry.

### Source identity

- Use the existing `SourceIdentity` type for normalized source identities.
- Use `SourceReference` for source occurrences with optional page numbers.
- Do not introduce entity-specific source identity or reference types when the shared types already express the required semantics.
- Preserve original source codes and source-aware entity IDs.
- Do not merge definitions from different sources.

### Source validation

During source analysis, collect every source code used by the new entity corpus, including primary, secondary, reprint, and other reference sources.

Compare all discovered codes with the existing Source Registry.

- Report previously unknown source codes.
- Add confirmed source identities to `data/sources/registry.json` using the existing registry conventions.
- Do not invent publication metadata, titles, editions, or source classifications.
- Do not silently generate new registry identities during catalog initialization.
- Preserve ambiguous references and document unresolved relationships.
- Ensure catalog startup validates every applicable source reference.

### Publication metadata

- The existing Book Catalog is authoritative for known publication metadata.
- Source identities must remain valid even without a corresponding publication.
- Resolve publication relationships through the Source Registry.
- Do not duplicate publication records inside entity catalogs.
- Do not embed complete publication DTOs in entity API responses by default.

### Public API

- Preserve existing source field representations when possible.
- Use stable source codes or lightweight references.
- Preserve page numbers and the distinct semantics of secondary sources.
- Apply `@JsonInclude(JsonInclude.Include.NON_EMPTY)` to public DTOs.

### Documentation and tests

For every new entity type:

- Record distinct source codes and any new identities.
- Document source-reference semantics.
- Test all primary and secondary source references against the registry.
- Test preservation of source-aware IDs.
- Verify existing catalogs still initialize after registry changes.
- Update `analysis/domain/DOMAIN-DISCOVERIES.md` with new cross-entity findings.

## Example invocation

`$dnd-json-mapper Analyze the 5eTools feats dataset, design the DTE and DTO models, identify reusable mechanics and references, and stop after the DTO design checkpoint.`