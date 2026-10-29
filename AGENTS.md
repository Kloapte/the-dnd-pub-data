# The DnD Pub — Compendium Backend — Agent Instructions

## 1. Authority and scope

- `ROADMAP.md` defines the domain sequence, six phases, deliverables, and approval gates.
- `BOUNDARIES.md` defines what belongs in canonical data, schema and mechanical completeness, omissions, identities, references, provenance, and licensing.
- This file defines Codex execution behavior, project architecture, artifact placement, Java conventions, and verification.
- Read the relevant sections of all three before acting. If they conflict, stop and request a decision. Do not silently choose one.
- Perform only the explicitly authorized step and phase. Never proceed to the next phase or domain without approval.
- Previously approved Step 1 artifacts remain approved; do not retroactively redesign them without authorization.

## 2. Architecture

- The application is a **read-only canonical compendium service**, not a D&D rules engine.
- Codex interprets source data offline and authors canonical JSON. Runtime Java never interprets original source entries, executes D&D mechanics, or reconstructs missing rules.
- Backend responsibilities are loading canonical datasets, deserializing typed records, retrieving/listing/filtering records, resolving supported references, returning API responses, and validating data integrity where appropriate.
- Keep original source datasets immutable and separate from canonical data. Canonical JSON conforms to approved JSON Schema Draft 2020-12 contracts.
- Prefer cohesive domain models. Reuse small semantic structures when actual repeated patterns justify reuse; do not create a universal mechanic object, framework, or speculative hierarchy.
- References should connect records rather than recursively embedding complete related models. Preserve the distinction between unresolved references and invented targets as defined in `BOUNDARIES.md`.
- Avoid unnecessary layers, factories, registries, generic repositories, adapters, and DTO transformations. Add an abstraction only for a demonstrable responsibility.

## 3. Repository locations

Use the following paths consistently. Do not create alternative locations for the same artifact type.

| Artifact | Location | Rule |
| --- | --- | --- |
| Immutable original source | `src/main/resources/data/` | Never modify source files |
| Canonical JSON Schema | `src/main/resources/schemas/<model-type>/schema.json` | Model-specific contract; approved revisions only |
| Schema support definitions | `src/main/resources/schemas/<model-type>/` | Keep near the owning schema; share only when justified |
| Representative canonical examples | `datasets/<current-step>/` | Use during Phase 3; keep distinguishable from production datasets |
| Approved runtime canonical datasets | `src/main/resources/normalized/` | Domain-specific subdirectories and consistent filenames |
| Source/domain discovery | `discover/` | Inventories, structural and semantic research |
| Validation and execution reports | `reports/` | Coverage, exceptions, checks, phase outcomes |
| Durable documentation | `doc/` | Markdown only; maintain `doc/INDEX.md` |
| Omitted source portions | `doc/<model>/omitted.md` | Exact source locator and reason; do not copy full entries |
| Duplicate source portions | `doc/<model>/duplicate.md` | Exact source locator and represented equivalent |
| Java implementation | `src/main/java/` | Follow existing root package and domain-oriented packages |
| Java tests | `src/test/java/` | Mirror the tested production packages |

- Do not place schemas or JSON examples under `doc/` or create a second root-level `/schemas` directory.
- Do not place phase progress or temporary research under `doc/`.
- Keep file and directory naming consistent with existing approved artifacts. If the existing repository conflicts with these locations, report the discrepancy and request approval for a focused migration; do not silently move approved files.
- Do not duplicate canonical datasets under multiple runtime paths.

## 4. Java package and class responsibilities

Organize code by domain first, using the repository's established base package. Within a domain, separate responsibilities only when necessary:

- **API/controller:** HTTP endpoints, request validation, response status, OpenAPI annotations; no source interpretation or mechanics execution.
- **Service:** Read-only application operations, filtering, lookup, and reference coordination; no unnecessary business-rule engine.
- **Model/DTO:** Strongly typed records matching canonical contracts and public response shapes. Keep separate API DTOs only when the public contract genuinely differs.
- **Repository/loader:** Access to approved canonical datasets, loading and indexing as needed. Do not introduce a database or generic persistence framework without a use case and approval.
- **Validation:** Structural and referential integrity checks, not semantic interpretation.

Prefer an existing cohesive class/package over introducing new layers. Do not enforce arbitrary class-count limits where a larger cohesive package is clearer. Keep cross-domain shared code small and purposeful.

## 5. Java and JSON conventions

- Use Java 21+, Spring Boot 4, the repository's configured Jackson version, and JUnit 5. Follow the actual build and formatting setup.
- Use four-space indentation, descriptive names, braces for control flow, one statement per line, and readable line lengths (approximately 120 characters when practical).
- Keep one public top-level type per Java file. Do not nest DTO types inside DTOs.
- Prefer immutable records for data carriers and defensive copies for mutable inputs.
- Use explicit types. Avoid `Object`, `JsonNode`, untyped maps, and custom deserializers in application-facing models unless a documented contract requires them.
- Use polymorphism only for genuinely bounded domain variants; avoid source-format-driven inheritance.
- For public API DTOs, use `@JsonInclude(JsonInclude.Include.NON_EMPTY)` where appropriate, but preserve meaningful `false` and `0` values and the approved contract.
- Avoid serializing empty nested objects. Annotate controller endpoints with appropriate OpenAPI metadata.
- Use concise comments for non-obvious decisions; do not narrate self-explanatory code.
- Produce valid, pretty-printed JSON. Treat JSON Schema Draft 2020-12 as the structural authority.
- Do not compress code or introduce unnecessary cleverness to save tokens.
- Place model-specific enums under `/dto/<model-type>/enums`.
- Organize model-type packages into responsibility-based subpackages when this improves cohesion and maintainability. Avoid unnecessary package fragmentation.
- Prefer simple, readable, and maintainable Java code that is easy to follow.

## 6. Offline interpretation and allowed automation

- **Codex itself** must inspect source evidence and interpret mechanics using the full approved domain inventory, schemas, and researched conventions. Interpret every meaningful part of each selected record.
- **Do not implement or use an automatic semantic source-to-canonical converter in any language** (including Java, JavaScript, Python, shell, regex pipelines, or rule-based extraction programs). A finite pattern matcher is not a substitute for Codex reasoning.
- Reproducibility means that source evidence, schema version, interpretation decisions, and conventions are recorded well enough to review and repeat the result consistently. It does not require executable interpretation software.
- Scripts and tools may inventory source files, extract exact source records for inspection, format JSON, validate schema/identities/references, calculate coverage, and perform other nonsemantic checks. They must not decide mechanical meaning or generate interpreted mechanics.
- First recognize structured mechanics already present in source fields; do not reinterpret or discard them unnecessarily. Then interpret semantic mechanics embedded in entries according to `BOUNDARIES.md`.
- Before declaring an omission, inspect the complete applicable schema and existing patterns. A missing scripted pattern, an unexamined example, or inconvenience is not a valid omission reason.
- Keep source provenance and interpretation accounting outside canonical domain records; do not insert workflow fields such as `needsReview` or `implementationStatus`.
- Keep canonical parent entities referenceable even when some or all prose-derived mechanics are not interpretable, as required by `BOUNDARIES.md`.

## 7. Working within the six phases

Follow the exact phase definitions and deliverables in `ROADMAP.md`; do not maintain a second, competing phase specification here.

- **Phase 1:** Inspect the complete in-scope domain catalog, including original structured mechanics, semantic patterns, outliers, and dependencies. Research only; do not modify canonical datasets or schemas.
- **Phase 2:** Design the intended complete domain contracts from the inventory; place schemas in the prescribed location. Obtain approval before conversion.
- **Phase 3:** Author small representative canonical batches under `datasets/<current-step>/`; assess mechanical relevance and report unsupported or unnecessary interpretations. Schema changes require review and approval.
- **Phase 4:** Verify and refine interpretation conventions, schemas, examples, and validation. Conversion remains Codex-authored, never automatic. Schema changes require review and approval.
- **Phase 5:** Interpret the remaining catalog in manageable batches, with coverage and omission accounting. Treat schema redesign as exceptional and request approval before changes.
- **Phase 6:** Integrate approved datasets into read-only Java services, test APIs and references, and update durable documentation. Do not add D&D mechanics execution.

For Step 6 (Spells Reference Resolution & Validation), apply the reference-focused scope in `ROADMAP.md`, not a second spell-mechanics discovery exercise.

## 8. Verification and quality gates

- Validate canonical JSON against its approved schema; check uniqueness, stable identities, source coverage, source locations, unresolved references, and cross-record integrity.
- Verify semantic interpretations against original evidence. Schema validation alone cannot prove mechanical correctness.
- Track interpreted, duplicated, omitted, ambiguous, and deferred portions as specified in `BOUNDARIES.md`; do not silently discard information.
- Keep original entry text out of canonical JSON and omission/duplicate registers; use precise locators to the immutable source.
- Test API retrieval, filters, unknown IDs, serialization/deserialization, and reference resolution where applicable. Do **not** add gameplay execution tests.
- Use focused, self-contained tests and pinned fixtures where appropriate. Normal Java tests must not depend on temporary discovery reports.
- Do not weaken schemas or tests just to make generated data pass.
- Run relevant focused checks and the applicable full test suite after changes. Distinguish existing failures from regressions. Check formatting and `git diff --check`.

## 9. Codex execution and reporting

- Inspect only the files needed for the authorized milestone; use targeted searches and avoid unrelated refactors.
- Preserve approved schemas, canonical data, and source files unless a change has been explicitly authorized.
- Keep changes small, reviewable, and consistent with established conventions.
- Store durable design/implementation documentation under `doc/`, research under `discover/`, and verification outcomes under `reports/`.
- Maintain `doc/INDEX.md` when durable documents are added or changed.
- In the final response summarize: changes made, paths affected, checks performed and results, coverage/omissions where relevant, and unresolved decisions.
- Stop at the approved milestone. Do not plan or execute the next phase without explicit user approval.
