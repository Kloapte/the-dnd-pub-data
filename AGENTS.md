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
- Keep JSON schemas and its examples under `/schemas`, categorized by its model name

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

## Architecture and canonical-data boundaries

- The backend consumes normalized, strongly typed compendium data; source interpretation happens offline, never in runtime services.
- Java implements deterministic application behavior, validation, domain logic, and approved mechanics execution.
- Original source datasets are immutable and remain separate from normalized application data.
- **Read and follow [`BOUNDARIES.md`](BOUNDARIES.md) before any domain analysis, schema design, conversion, validation, or integration.** It is the authoritative policy for semantic interpretation, allowed canonical content, identity, references, omissions, duplication, licensing considerations, and provenance.
- Prefer explicit domain-specific mechanics models, with small shared identity/source/reference components where justified. Do not introduce a universal mechanics model or source-specific runtime adapters by default.
- JSON Schema Draft 2020-12 is authoritative for normalized structural contracts. Define and approve schemas before conversion; version them and validate generated data. Structural validation does not establish mechanical correctness.
- Do not change an approved schema or normalized interpretation without review and approval. Do not modify original source data.
- Treat the Source Registry as an approved, metadata-only domain. New semantic-normalization policies do not retroactively change approved Step 1 artifacts without separate authorization.

## Normalization workflow

Each roadmap domain follows six phases. Every phase is a separate milestone requiring approval before proceeding to the next.

Semantic interpretation into domain-specific canonical mechanics is the intended strategy for mechanics-bearing domains. Metadata-only domains, such as the approved Source Registry, are exempt from mechanic extraction. If semantic interpretation is unreliable or inappropriate for a domain, document the evidence and seek approval for an alternative. Follow BOUNDARIES.md.

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
- Model supported mechanics explicitly and domain-specifically; never retain source `entries` or rich-text as a default fallback.
- Keep provenance and omitted/duplicate source content outside canonical JSON as specified in `BOUNDARIES.md`.
- Define how unsupported or ambiguous mechanics are accounted for outside canonical JSON.
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
- Preserve typed source references and external traceability.
- Account for every original entry, documenting omitted and duplicate entries under `/doc/<model>/` as specified in `BOUNDARIES.md`.
- Do not silently modify approved schemas.
- Produce a conversion report showing coverage and identified issues.

### Phase 4 — Validation and feedback

- Validate generated JSON against the approved JSON Schemas.
- Verify source-entry accounting, mechanically correct interpretation, and documentation of omitted/duplicate content.
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
- Track processed, successful, omitted, duplicate, unresolved, and unsupported entries; no silent loss.
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
- Verify every source entry has been accounted for under the canonical mechanics and external omission/duplication policy.
- Track normalization coverage, unsupported constructs, and unresolved references.
- Use representative fixtures for known structural variations and edge cases.
- For semantic mechanics, test meaningful gameplay behavior rather than only JSON structure.
- Prefer focused, maintainable tests over exhaustive duplication of source data.
- The normal Java test suite must not depend on temporary discovery or generated analysis artifacts.
- Tests should use self-contained fixtures or actual pinned source data where appropriate.
- Do not treat successful schema validation as proof of semantic correctness.
- Do not weaken validation or tests merely to make generated data pass.

## Codex execution workflow

- Read this `AGENTS.md`, `BOUNDARIES.md`, and the relevant `ROADMAP.md` section before starting work.
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
- Do not silently lose meaningful source mechanics; document source content excluded from canonical JSON according to `BOUNDARIES.md`.
- Keep generated data understandable and reviewable by humans.
- Treat Codex-generated interpretations as proposals until validated and approved.
- Preserve the distinction between structurally valid data and mechanically correct data.
- Prefer incremental, verifiable progress over large unreviewed transformations.