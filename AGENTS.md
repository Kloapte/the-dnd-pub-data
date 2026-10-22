# The DnD Pub — Compendium Backend

## Project guidance

- Documentation must always be placed under `/doc`.
- Documentation under `/doc` must consist exclusively of Markdown files.
- Maintain `/doc/INDEX.md` with hyperlinks to other documentation and a brief description of each document.
- Documentation under `/doc` must describe the system, its architecture, contracts, and implemented functionality. Do not place temporary research, analysis, or execution reports there.
- Place temporary discovery, research, and analysis artifacts under `/discover`.
- Place execution reports, validation summaries, and coverage reports under `/reports`.
- JSON Schema files, normalized datasets, and other machine-readable contracts are project artifacts, not Markdown documentation. Keep them in dedicated directories outside `/doc`.
- Document implemented features when applicable, including:
  - Use cases
  - Process flows
  - Technical flows
  - Data mappings
  - Test coverage
- Keep documentation synchronized with implemented functionality and approved contracts.

## Technology and code conventions

- Use Java 21+, Spring Boot 4, Jackson, and JUnit 5. Follow the repository's actual build and formatting configuration.
- Prefer readable, maintainable Java:
  - Four-space indentation.
  - Descriptive names.
  - Braces for control flow.
  - One statement per line.
  - Split dense expressions and long declarations where useful.
  - Aim for approximately 120 characters per line when practical.
- Keep one public top-level class, record, interface, or enum per Java file.
- Do not nest DTO types inside other DTOs.
- Prefer Java records for immutable transfer and domain data.
- Use sealed interfaces only when bounded domain polymorphism is genuinely necessary.
- Use defensive copies for mutable collections.
- Model known structures using explicit types.
- Avoid `Object`, `JsonNode`, and generic maps in application-facing models.
- Avoid custom Jackson deserializers unless a documented contract genuinely requires them.
- Prefer extending an existing cohesive class over introducing another service, factory, registry, or wrapper unless the new abstraction has a demonstrably separate responsibility.
- Keep classes tightly organized into packages. Aim for a maximum of five classes per package unless a larger cohesive package is more practical.
- Use meaningful comments for non-obvious constraints. Do not restate what the code already expresses.
- Keep changes focused and reviewable. Never compress code to save tokens.
- Write valid, pretty-printed JSON using the JSON syntax standardized by ISO/IEC 21778:2017.
- Annotate new public API DTO records with `@JsonInclude(JsonInclude.Include.NON_EMPTY)` unless the documented contract requires otherwise.
- Omit absent or empty optional values without suppressing meaningful `false` or `0` values.
- Avoid serializing empty nested objects.
- Annotate controller methods with appropriate Swagger/OpenAPI annotations.

## Architecture principles

- The backend consumes normalized, strongly typed compendium data.
- Original source data and normalized application data must remain separate.
- Source-specific interpretation and normalization occur offline, not during application runtime.
- Codex may interpret source data and generate normalized JSON artifacts.
- Java is responsible for deterministic application behavior, validation, domain logic, and mechanics execution.
- Do not introduce source-specific parsing or interpretation logic into runtime services when the information can be normalized beforehand.
- Avoid unnecessary abstraction layers, generic interpretation frameworks, dynamic registries, and source-specific adapters.
- Prefer stable, explicit data contracts over flexible but weakly typed structures.
- Keep normalization concerns separate from runtime mechanics execution.
- Preserve the original source data for auditing, comparison, and future normalization updates.
- Do not modify original source files as part of normalization.

## JSON Schema contracts

- Use JSON Schema Draft 2020-12 for normalized JSON contracts.
- JSON Schema is the authoritative structural contract for normalized datasets.
- Define and review a schema before generating its normalized dataset.
- Keep schemas versioned and under source control.
- Prefer explicit property names, predictable types, and consistent representations.
- Avoid unnecessary polymorphism, dynamic property names, and source-specific JSON structures.
- Use `$ref` and `$defs` to reuse shared schema definitions when appropriate.
- Prefer cohesive schemas over excessive fragmentation into many small files.
- Define enumerations explicitly when their allowed values are known.
- Use consistent conventions for identifiers, references, optional properties, arrays, and enumerations.
- Distinguish required fields from optional fields.
- Do not use `null`, empty strings, or empty collections interchangeably to represent missing information.
- Do not introduce additional properties into normalized JSON unless permitted by the schema.
- Validate generated JSON against its corresponding schema.
- Schema validation alone is insufficient for cross-document references and game-rule correctness. Perform additional integrity checks where applicable.
- Treat schema changes as contract changes. Do not silently modify an approved schema to accommodate unexpected source data.
- Evaluate compatibility and migration implications before changing approved contracts.

## Normalization principles

### General

- Codex performs source interpretation and normalization offline.
- Normalized JSON is a versioned, reviewable project artifact.
- Follow the currently authorized normalization stage and roadmap milestone.
- Do not combine structural and semantic normalization unless explicitly authorized.
- Preserve source meaning and traceability throughout normalization.
- Never invent mechanical values, source relationships, resolved references, or provenance.
- Preserve ambiguity and unsupported constructs explicitly.
- Abstain and report when source evidence is insufficient.
- Do not silently omit source information because it is difficult to represent.
- Prefer reusable, domain-oriented structures over source-specific representations.
- Do not create Java interpretation frameworks to compensate for incomplete normalized data.
- Prefer improving the normalized contract or reporting unsupported structures.

### Stage A — Structural normalization

The objective is to convert source JSON into clean, consistent, strongly typed JSON structures without changing the meaning of the source.

- Normalize inconsistent field names and representations.
- Replace unnecessary nesting and source-specific polymorphism with predictable structures.
- Replace dynamic property names with explicit fields or collections where practical.
- Preserve meaningful descriptions, mechanics text, references, and metadata.
- Do not infer executable mechanics from descriptive text unless explicitly authorized.
- Do not discard information solely to simplify a schema.
- Represent genuinely unsupported source structures explicitly and report them.
- Avoid introducing speculative domain properties that are not supported by the source.
- Design canonical structures that can support later semantic enrichment without unnecessary redesign.

### Stage B — Semantic normalization

The objective is to interpret descriptive game rules and convert them into structured, executable mechanics.

- Perform semantic normalization only when explicitly authorized.
- Interpret mechanics using source evidence and established game rules.
- Prefer reusable mechanic structures over spell-specific, monster-specific, or feature-specific implementations.
- Separate mechanical effects, activation requirements, conditions, targeting, duration, and other relevant concepts where appropriate.
- Do not invent missing values or assume unspecified behavior.
- Preserve conditional behavior and relevant rule restrictions.
- Preserve ambiguous or unsupported mechanics explicitly rather than guessing.
- Keep human adjudication requirements identifiable.
- Report interpretations that cannot be represented by the current mechanics contract.
- Do not modify the mechanics execution engine merely to accommodate a single source entry without explicit approval.
- Keep generated mechanics compatible with deterministic backend execution.

## Model identity and references

- Use predictable model identifiers following the convention `<type>:<source>:<name>`.
- Define and consistently apply normalization rules for identifier components.
- Preserve stable identifiers across normalization runs whenever the underlying entity identity has not changed.
- Preserve existing SRD and SRD52 flags.
- Never invent, infer, or reassign SRD or SRD52 status.
- Reference other normalized entities by their predictable identifiers.
- Do not duplicate complete external models inside another DTO or JSON entry.
- Preserve unresolved references explicitly and report them.
- Do not invent references based solely on similar names.
- Source entries may be split into multiple normalized entities when there is a clear domain reason.
- Maintain traceability between original source entries and all normalized entities derived from them.
- Do not merge distinct source entities unless their equivalence is established.

## Provenance and versioning

- Every normalized entity must be traceable to its original source entry.
- Track provenance using a manifest or equivalent structured metadata.
- Include applicable information such as:
  - Original source identifier and location.
  - Source version or revision.
  - Source content hash.
  - Normalized entity identifier.
  - JSON Schema version.
  - Normalization or review status.
- Never fabricate unavailable provenance metadata.
- Keep provenance metadata separate from application DTOs unless the application contract explicitly requires it.
- Preserve approved normalized data when processing updated source versions.
- Use deterministic source comparisons to identify added, modified, and removed entries.
- Reinterpret changed entries and any affected dependencies rather than regenerating the entire dataset unnecessarily.
- Do not silently overwrite previously approved normalized interpretations.
- Report changed interpretations, unresolved differences, and compatibility concerns.
- Treat source removal separately from deletion of normalized entities. Report the impact before removing previously approved data.

### Incomplete normalization and reference tracking

- Keep canonical normalized JSON free of implementation, normalization, and review status metadata.
- Do not introduce fields such as `implementationStatus`, `normalizationStatus`, `reviewStatus`, `isComplete`, `needsReview`, or similar workflow indicators into canonical domain models.
- Preserve valid source-derived references even when their targets have not yet been normalized or integrated.
- Document incomplete interpretations, unresolved or missing references, deferred dependencies, unsupported mechanics, and missing source content under `/doc`.
- Maintain a discoverable overview of incomplete artifacts, including their canonical identifiers, affected properties or mechanics, reasons for incompleteness, dependencies, and required follow-up work.
- Distinguish deferred, unsupported, and ambiguous cases in documentation rather than encoding those workflow states in normalized structures.
- Update the documentation when an incomplete artifact is resolved, including removing obsolete limitations.
- Use validation reports under `/reports` for generated findings and verification results. Keep persistent, actionable limitations documented under `/doc`.
- Do not invent placeholder mechanics or reference targets merely to make normalized artifacts appear complete.
- Provenance and schema versioning may be maintained in separate manifests, but implementation and review progress must remain outside canonical domain JSON.

## Normalization workflow

Each roadmap domain follows six phases. Every phase is a separate milestone requiring approval before proceeding to the next.

Semantic enrichment is the intended normalization strategy from Step 1 onward. If source analysis indicates that semantic normalization is unreliable, unnecessarily complex, or counterproductive for a particular domain, Codex must explain the findings and propose an alternative for approval.

### Phase 1 — Source and domain analysis

Inspect all relevant source files and determine how the source information should be represented in the canonical DnD Pub model.

Do not modify production code, approved schemas, or normalized datasets during this phase.

#### 1.1 Structural analysis

- Inventory relevant source files and entity counts.
- Identify every observed property and its data types.
- Determine required, optional, conditional, and mutually exclusive properties.
- Identify nested structures, dynamic properties, polymorphism, and inconsistent representations.
- Identify structural variations and their frequency.
- Identify source-specific formatting, tags, and encoded references.
- Identify duplicate, inherited, copied, or overridden source entries.
- Identify structures that may require preprocessing or reference resolution.
- Report unusual and unsupported source structures with representative examples.

#### 1.2 Semantic analysis

- Determine the game meaning and intended behavior of each significant source structure.
- Identify explicit and implied mechanics.
- Identify activation requirements, conditions, prerequisites, restrictions, effects, durations, and exceptions where applicable.
- Determine which mechanics are shared with other domains.
- Distinguish source facts from interpretations and assumptions.
- Identify mechanics that require external rules or other model types.
- Identify ambiguous, contradictory, or incomplete source information.
- Assess whether semantic enrichment is feasible without excessive complexity or unreliable assumptions.

#### 1.3 Dependency analysis

- Identify references to other source entries, model types, and rules.
- Inspect embedded references, including 5eTools `{@...}` tags.
- Distinguish explicit references from inferred relationships.
- Identify unresolved, ambiguous, and circular dependencies.
- Determine which dependencies require prior normalization and which can remain as stable references.
- Identify shared concepts that should be normalized independently.
- Assess the impact of dependencies on the proposed roadmap order.

#### 1.4 Domain model and submodel analysis

Evaluate whether each source model should become one canonical model or several related models.

- Identify common properties shared across entries.
- Identify meaningful groups of entities with different structures or behaviors.
- Determine whether specialized submodels would simplify the canonical contracts.
- Compare inheritance, composition, and independent model types.
- Consider abstract parent models where shared domain identity or behavior justifies them.
- Identify reusable value objects, effect models, and referenced entities.
- Investigate entries that belong to multiple semantic categories.
- Avoid artificial inheritance hierarchies based solely on source formatting.
- Avoid excessively broad models containing many unrelated optional properties.
- Avoid excessive fragmentation into models without meaningful independent responsibilities.
- Evaluate the impact of proposed submodels on JSON Schema, Java DTOs, references, and future mechanics execution.
- Recommend the simplest maintainable model structure that faithfully represents the domain.

For each significant modeling alternative, document its advantages, disadvantages, and expected impact.

#### 1.5 Coverage and outlier analysis

Quantify the findings wherever possible.

Report:

- Total source entries inspected.
- Number and percentage of entries matching common structural patterns.
- Number and percentage of entries requiring specialized handling.
- Observed property and structure frequencies.
- Identified semantic categories and their occurrence counts.
- Potential submodels and the number of entries associated with each.
- Unsupported or ambiguous structures and their occurrence counts.
- Representative examples for common cases, edge cases, and outliers.
- Estimated coverage achievable with the proposed canonical model.

Do not invent statistics or extrapolate exact counts from incomplete inspection. Clearly distinguish complete inventories from sampled findings.

#### 1.6 Analysis deliverables

Produce a discovery report under `/discover` containing:

1. Source inventory and structural findings.
2. Semantic and mechanics analysis.
3. Dependency analysis.
4. Proposed domain model and submodel alternatives.
5. Coverage statistics and outliers.
6. Normalization feasibility assessment.
7. Recommended approach and unresolved decisions.

Do not proceed to schema creation without approval.

### Phase 2 — Canonical model and JSON Schema

Using the approved analysis:

- Design the canonical domain model.
- Define shared components, specialized submodels, and references.
- Create JSON Schema Draft 2020-12 contracts.
- Use explicit and predictable structures.
- Define required fields, optional fields, enumerations, and constraints.
- Preserve source meaning and necessary provenance.
- Define how unsupported or ambiguous mechanics are represented.
- Provide representative JSON examples.
- Document source-to-canonical mappings.
- Validate the schemas and examples.
- Report any deviations from the approved domain model.

Do not begin source conversion before the contracts are approved.

### Phase 3 — Representative conversion

- Select a small but representative set of source entries.
- Cover common structures, identified submodels, edge cases, and outliers.
- Convert the selected entries into canonical JSON.
- Perform semantic enrichment according to the approved contracts.
- Preserve source references and traceability.
- Record ambiguous interpretations and unsupported mechanics.
- Do not silently modify approved schemas.
- Produce a conversion report showing coverage and identified issues.

### Phase 4 — Validation and feedback

- Validate generated JSON against the approved JSON Schemas.
- Verify source coverage and preservation of meaningful information.
- Verify identifiers, references, and provenance.
- Review semantic interpretations against original source evidence.
- Evaluate whether the chosen domain model remains appropriate.
- Identify unnecessary complexity, duplication, or missing abstractions.
- Evaluate whether submodel boundaries remain valid.
- Identify recurring exceptions that suggest a contract problem.
- Report schema changes or modeling refinements that would improve maintainability.
- Repeat representative conversion and validation after approved changes.
- Do not proceed to full dataset expansion until the representative results are approved.

### Phase 5 — Dataset expansion

- Normalize the remaining source entries using the approved contracts.
- Process data in manageable, reviewable batches.
- Apply established interpretation conventions consistently.
- Validate each batch against the approved schemas.
- Track processed, successful, unresolved, and unsupported entries.
- Detect missing entities, duplicate identifiers, and broken references.
- Preserve provenance and previously approved interpretations.
- Report unexpected source variations before introducing significant contract changes.
- Produce a final coverage and exception report.
- Do not claim complete normalization while entries remain unaccounted for.

### Phase 6 — Backend integration

- Integrate approved normalized datasets into the backend.
- Create or update strongly typed Java DTOs matching the canonical contracts.
- Prefer straightforward Jackson serialization and deserialization.
- Keep source-specific interpretation outside the application runtime.
- Implement deterministic mechanics execution where supported by the approved models.
- Add tests for deserialization, validation, references, and relevant domain behavior.
- Verify compatibility with existing application functionality.
- Remove obsolete extraction code only when explicitly authorized.
- Update applicable system documentation under `/doc`.
- Report integration results, test coverage, and remaining limitations.

Stop after the authorized milestone. Do not automatically proceed to the next roadmap phase or domain.

## Validation and testing

- Use automated JSON Schema validation for normalized datasets.
- Verify cross-document references separately from schema validation.
- Verify uniqueness and stability of normalized identifiers.
- Verify preservation of meaningful source information.
- Track normalization coverage, unsupported constructs, and unresolved references.
- Use representative fixtures for known structural variations and edge cases.
- For semantic mechanics, test meaningful gameplay behavior rather than only JSON structure.
- Prefer focused, maintainable tests over exhaustive duplication of source data.
- The normal Java test suite must not depend on temporary discovery or generated analysis artifacts.
- Tests should use self-contained fixtures or actual pinned source data where appropriate.
- Do not treat successful schema validation as proof of semantic correctness.
- Do not weaken validation or tests merely to make generated data pass.

## Codex execution workflow

- Read this `AGENTS.md` and the relevant `ROADMAP.md` section before starting work.
- Inspect only the code, schemas, source data, and documentation necessary for the authorized task.
- Prefer targeted searches and focused inspection.
- Consult historical discovery or analysis artifacts only when answering a concrete question.
- Respect the current roadmap milestone and normalization stage.
- Do not begin a subsequent phase, domain, or milestone without explicit authorization.
- Treat inspection, contract design, conversion, validation, and integration as separate approval checkpoints when specified by the roadmap.
- Implement the authorized scope directly once sufficient inspection and required approvals are complete.
- Reuse existing cohesive abstractions.
- Avoid speculative frameworks, unrelated refactors, and unsolicited follow-up work.
- Do not modify source datasets.
- Do not silently change approved JSON Schemas or normalized datasets.
- Run focused tests during implementation.
- Run the full applicable test suite once after implementation, repeating it only if later changes invalidate the result.
- Distinguish pre-existing failures from regressions.
- Check formatting and `git diff --check`.
- Ensure the final diff contains no unrelated changes.
- Keep completion reports concise:
  - Changes made.
  - Observable results and coverage.
  - Verification performed.
  - Remaining limitations and unresolved issues.
- Write detailed reports only when required by the roadmap or explicitly requested.
- Stop after the requested milestone and its verification.
- Do not automatically begin the next roadmap step.

## Quality principles

- Favor correctness, maintainability, and traceability over maximum normalization coverage.
- Favor explicit contracts over implicit conventions.
- Favor simple domain models over generalized interpretation frameworks.
- Avoid overengineering for hypothetical future source formats.
- Do not hide uncertainty behind default values or guessed interpretations.
- Do not sacrifice meaningful source information for simpler DTOs.
- Keep generated data understandable and reviewable by humans.
- Treat Codex-generated interpretations as proposals until validated and approved.
- Preserve the distinction between structurally valid data and mechanically correct data.
- Prefer incremental, verifiable progress over large unreviewed transformations.