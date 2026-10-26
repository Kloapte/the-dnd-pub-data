# Step 2 Phase 6 — Core Rules Backend Integration

## Status

**Implementation remains in progress.** Eight domain-specific repository, service, and REST controller paths now share internal loading, indexing, provenance, identity, and reference-resolution infrastructure. Schema-shaped Java DTOs and verified Jackson round-trips are complete for Skill, Language, Sense, and Status. Action, Rule, Condition, and Disease mechanics unions remain untyped Java data and are returned as their full approved canonical JSON records; Phase 6 is not complete.

## Runtime datasets

The validated Phase 3 and Phase 5 datasets are consolidated into unversioned classpath resources under `../../src/main/resources/normalized/core-rules`:

| Resource | Records |
|---|---:|
| `actions.json` | 46 |
| `conditions.json` | 30 |
| `diseases.json` | 28 |
| `languages.json` | 201 |
| `rules.json` | 231 |
| `senses.json` | 8 |
| `skills.json` | 36 |
| `statuses.json` | 5 |
| **Total** | **585** |

`source-identities.json` contains 6,277 pinned identities across the 11 referenced source types. `npm run aggregate:core-rules` deterministically builds the grouped arrays from the individual Phase 3/5 conversion artifacts and rejects incomplete identity/provenance, wrong domains, and duplicate IDs. Source prose and immutable source records are not changed.

## Backend implementation

- The shared `CoreRulesRepository` loads every domain resource at Spring startup, validates the expected total, required identity and provenance, source/name/domain-derived ID, identity uniqueness, and references against pinned source identities.
- `CoreRulesCatalog` provides shared source-aware reference resolution and canonical lookup.
- Separate domain-specific repositories, services, and controllers are registered for Action, Rule, Condition, Disease, Language, Skill, Sense, and Status. Domain routes retain the existing `/api/core-rules/{domain}` and source-qualified identity paths. Reference resolution remains at `/api/core-rules/references/resolve`.
- Skill DTOs represent ability and check references. Language DTOs model speaker selector variants, language links, and concealed-message structure. Sense DTOs model Sight, Nonvisual Perception, and Condition Interaction variants. Status DTOs model all five approved mechanic variants and their maintenance-save DC structure.
- Action, Rule, Condition, and Disease currently use the shared canonical identity/provenance/reference index and return the complete validated canonical JSON unchanged. Their Java mechanics DTO unions are not implemented yet.
- No generic mechanics executor or source-prose interpretation was added.

## Tests and validation

- `npm run aggregate:core-rules`: passed; 585 records aggregated into eight sorted domain resources.
- `npm run validate:core-rules`: passed; 9 schemas, 89 examples, 34 Phase 3 records, 551 Phase 5 records, **840 permanent checks**. Permanent checks compare aggregated resources to validated inputs and verify the pinned source identity index.
- Full clean Maven test suite: passed; 21 tests total (12 Core Rules integration tests, 3 startup-loader tests, and all 6 existing Source Registry tests), 0 failures, 0 errors.
- `git diff --check`: passed. Git emitted line-ending conversion notices for existing modified text files, without whitespace errors.
- Integration tests verify all eight domain endpoints and first-record lookups, record counts and ID uniqueness, shared identity/provenance Jackson round-trips for all 585 records, complete per-record Jackson round-trips for 36 Skills, 201 Languages, 8 Senses, and 5 Statuses, and all three reference-resolution states.
- Negative startup tests reject duplicate canonical identities, identity/source metadata mismatch, and references absent from pinned source identities.
- Existing Source Registry integration tests remain part of the full suite; no Source Registry behavior was changed.

## Remaining Phase 6 gaps

1. Implement and test schema-shaped Java models and tagged unions for Action (46 records), Rule (231), Condition (30), and Disease (28). Add full Jackson round-trip equality tests for every record in those domains and retain their current complete REST payloads.
2. Add domain-specific model tests for invalid enum/discriminator values and service/repository not-found behavior for each typed domain.
3. Run the full Maven suite after the remaining implementation and repeat canonical data validation and `git diff --check`.

## Governance consistency review

No governance contract was changed. Findings and proposed corrections for human review:

| File | Finding | Proposed correction |
|---|---|---|
| `ROADMAP.md` | Its top-level Step 2 summary still says Phase 1 awaits review, while the Step 2 domain section records Phases 1–5 complete and approved. | Reconcile the top-level summary with the approved Phase 5 closure and preserve phase-specific approval gates. |
| `ROADMAP.md` | Phase 6 status still says implementation has not started or awaits plan decisions. | After review, change the factual status to implementation in progress. Do not mark complete until all domain DTO, round-trip, REST, and compatibility acceptance criteria pass and closure is reviewed. |
| Phase 6 plan report | Its planning-only status and unresolved resource/API/reference decisions are superseded by the user's explicit grouped-resource, REST, and reference-policy directions. | Revise or supersede after review; retain the remaining Action, Rule, Condition, and Disease DTO work. |
| `AGENTS.md` | Its typed-model and repository conventions align with the approved work; no new conflict identified. | No change proposed for this batch. |
| `BOUNDARIES.md` | Its frozen-schema, source-immutability, no-prose-fallback, and no-generic-engine boundaries remain satisfied. | No change proposed. |
| Source resource layout | Pinned source catalogs remain under the existing `src/main/resources/data` organization; this work did not relocate or modify them. | Any broader source layout migration should be planned separately from immutable source data and reviewed independently. |

These findings do not authorize governance edits. No Step 3 work has started.

## Continued integration checkpoint (2026-10-25)

The current domain API architecture remains in progress. Separate repositories, services, controllers, grouped runtime resources, and shared identity/reference infrastructure cover all eight domains. Full schema-shaped Java records and per-record round-trip equality are currently implemented only for Skill, Language, Sense, and Status. Action, Rule, Condition, and Disease still serve their complete canonical JSON payloads through shared controller infrastructure and do not yet have the requested typed mechanics unions. They are not counted as complete domain integrations.

Verification in this checkpoint:

- `npm run validate:core-rules`: passed (9 schemas, 89 examples, 34 Phase 3 records, 551 Phase 5 records, 840 permanent checks).
- `git diff --check`: passed; Git emitted only line-ending conversion notices on existing modified files.
- `mvn clean test`: could not start because Maven's default local repository resolves to unwritable `C:\.m2\repository` in this environment.
- Retrying with an explicit temporary Maven settings file and workspace-local cache reached dependency resolution, then failed because network access to Maven Central is denied and the Spring Boot 4.0.5 parent POM is not cached there. This is an environment dependency blocker, not a compilation or test result. No Maven tests are claimed for this checkpoint.

No canonical schemas, immutable source files, or domain records were changed in this checkpoint. Phase 6 remains open; the four untyped domain mechanics models, their typed controller/service/repository signatures, and complete regression suite remain required before final review. Governance discrepancies above remain for human review; no governance files were changed here.
