# Step 2 Phase 6 — Core Rules Backend Integration

## Status and closure decision

**Integration implemented; Step 2 closure remains pending.** Typed domain integrations are present for Actions, Conditions, Diseases, Languages, Rules, Senses, Skills, and Statuses. The separate Source Registry remains the Step 1 deliverable. Step 3 has not started.

The closure request's acceptance criterion for automated canonical round-trip and API semantic-equivalence coverage across all eight domains is not met. This report records that gap rather than treating implementation presence or ordinary endpoint tests as equivalent coverage. No implementation or test scope was added solely to close the roadmap item.

## Runtime architecture

Each domain has its own typed DTO package, repository, service/facade, and read-only REST controller. The services expose collection, ID, and exact-name lookup as applicable. Canonical domain arrays are packaged under `src/main/resources/normalized/core-rules/` and loaded as typed records through Jackson during repository construction. Resource parsing or mapping exceptions are rethrown as `IllegalStateException`; repository bean construction therefore fails and prevents Spring application startup. The repositories also reject duplicate IDs.

The runtime resource set contains 585 records:

| Domain | Records | Resource |
| --- | ---: | --- |
| Actions | 46 | `actions.json` |
| Conditions | 30 | `conditions.json` |
| Diseases | 28 | `diseases.json` |
| Languages | 201 | `languages.json` |
| Rules | 231 | `rules.json` |
| Senses | 8 | `senses.json` |
| Skills | 36 | `skills.json` |
| Statuses | 5 | `statuses.json` |

The API routes are `/api/actions`, `/api/conditions`, `/api/diseases`, `/api/languages`, `/api/rules`, `/api/senses`, `/api/skills`, and `/api/statuses`, each with collection, ID, and name lookup endpoints where the model has a name.

Canonical JSON and the approved JSON Schemas remain authoritative. DTOs preserve the modeled canonical values and structures. As explicitly accepted for this closure, Java DTOs are not a second enforcement layer for every JSON Schema required-field and constraint rule. In particular, the documented Rule DTO validation gap is an accepted architectural boundary, not unfinished Phase 6 work. No database or mechanics execution engine is required. Previously approved omissions, unsupported mechanics, and limitations remain in force.

## Coverage audit

### Canonical DTO round-trip coverage

Automated tests deserialize and serialize every canonical record with semantic JSON equivalence for:

- Rules: 231 records (`RuleMechanicDtoTests`).
- Senses: 8 records (`SenseIntegrationTests`).
- Skills: 36 records (`SkillIntegrationTests`).
- Statuses: 5 records (`StatusIntegrationTests`).

Equivalent all-record DTO round-trip tests are absent for Actions (46), Conditions (30), Diseases (28), and Languages (201). Their repository count and endpoint tests do not establish round-trip equivalence.

### API semantic-equivalence coverage

`RuleIntegrationTests` compares all, ID, and name endpoint JSON with the corresponding canonical JSON. No equivalent canonical semantic-equivalence assertion covers the API responses for Actions, Conditions, Diseases, Languages, Senses, Skills, or Statuses. Their endpoint tests verify endpoint behavior and selected record counts/lookups, but do not establish semantic equality for all returned records.

Consequently, the requested automated coverage across all eight domains is incomplete. This is the substantive acceptance criterion preventing Phase 6 and Step 2 closure at this checkpoint.

## Final verification

Commands were run from the repository root:

- `mvn -q test` — passed (exit code 0).
- `npm run validate:core-rules` — passed (exit code 0): 9 schemas compiled, 89 examples, 34 Phase 3 records, 551 Phase 5 records, 840 permanent checks, 49 Rule components, and 27 Action records checked.
- `git diff --check` — passed (exit code 0). Git reported a line-ending conversion notice for the pre-existing modified `doc/INDEX.md`; no whitespace errors were reported.

Startup behavior is confirmed by repository construction: all eight Core Rules repositories synchronously deserialize their packaged `normalized/core-rules/<domain>.json` resource into the domain record type, and parsing/mapping failures are propagated as startup-failing `IllegalStateException`s. The Maven suite passed with the packaged canonical resources.

## Closure decision

Step 2 Phase 6 integration is implemented, and the requested final Maven and canonical-data validations pass. **Closure is pending**, because all-record canonical DTO round-trip coverage is missing for Actions, Conditions, Diseases, and Languages, and full API semantic-equivalence coverage is missing for seven of the eight domains. The user requested that substantive unmet criteria leave closure pending rather than trigger test or implementation expansion in this closure task.

The Source Registry remains completed under Step 1 and is not included in Step 2's record counts or completion state. No schemas, canonical JSON, DTOs, repositories, services, controllers, or tests were changed for this closure checkpoint. No Step 3 work is authorized or started.
