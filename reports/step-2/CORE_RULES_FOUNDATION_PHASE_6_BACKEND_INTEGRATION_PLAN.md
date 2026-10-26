# Step 2 Phase 6 — Core Rules Backend Integration Plan

## Status

**Planning checkpoint only; implementation has not started.** Step 2 Phase 5 is complete and approved. The repository defines the general Phase 6 objective and activities, and contains the separate Step 1 Source Registry integration as an architectural precedent. It does not define enough Step 2-specific integration decisions to safely implement a Core Rules runtime surface without selecting architecture and API behavior that the contracts do not approve. This report records a concrete proposed sequence and the decisions required before implementation.

## Approved scope to carry forward

Integrate only the existing approved Step 2 canonical dataset and schemas for Action, Rule, Skill, Condition, Disease, Status, Sense, and Language. LanguageScript remains excluded. Preserve source-qualified identity and edition distinctions, omit the three explicitly unsupported Action/Disease outputs, retain the accepted FULL/PARTIAL/UNSUPPORTED classifications, and keep external target references source-aware without inventing canonical outputs.

Phase 6's repository-level requirements are: strongly typed Java DTOs, straightforward Jackson mapping, integration into existing backend services, deterministic behavior only where the approved contracts support it, reference validation, deserialization/contract/domain tests, compatibility checks, and system documentation. Phase 6 does not authorize a generic mechanics engine or runtime interpretation of source prose.

## Proposed implementation checkpoints

### 0. Approve integration decisions before code

Resolve the items below and amend/approve this plan. Acceptance: the runtime dataset location, DTO boundary, reference policy, public/internal service surface, validation timing, and mechanics-execution subset are explicit. No code or resource packaging is part of this checkpoint.

### 1. Package canonical runtime data

After the data location is approved, package the 551 Phase 5 canonical outputs and 34 Phase 3 representative outputs without altering their contents. Keep the eight domains separate and preserve relative identity/provenance fields. Acceptance: deterministic packaging matches the reviewed dataset byte-for-byte after canonical JSON formatting; immutable source datasets remain untouched; the three accepted unsupported alternatives are absent rather than represented by placeholders.

### 2. Add schema-aligned typed DTOs and deserialization tests

Model each approved domain with explicit Java records/components; avoid `Object`, generic maps, `JsonNode`, and source-prose fallback. Keep Rule and Action mechanics domain-specific and model their approved tagged unions explicitly. Acceptance: every packaged canonical record deserializes and serializes with equivalent canonical fields, unknown values fail clearly, absent optionals remain absent, and representative union variants from every domain are covered.

### 3. Add deterministic loading and identity/reference validation

Use exact `<type>:<source>:<slug>` identity keys and preserve dataset order only if consumers require it. Validate duplicate IDs, source identity metadata, local references, cross-domain references, and source-known versus canonical-output-available targets according to the approved reference policy. Acceptance: startup/load validation is deterministic; known canonical targets resolve; source-known but unnormalized targets are distinguished from nonexistent source identities; malformed data and duplicate IDs fail with actionable diagnostics.

### 4. Integrate with an approved application surface

Use an existing cohesive service if one owns the chosen responsibility; otherwise add only the smallest approved catalog/service abstraction. Add controllers only if a public API is approved. Acceptance: all required Core Rules data is reachable through the approved application use cases, and existing Source Registry behavior/security remains unchanged.

### 5. Implement only an approved deterministic behavior subset

Select explicit canonical operations and source-backed cases for runtime execution. Do not infer that every mechanics component requires an executor. Acceptance: each supported operation has behavior-level tests for valid, boundary, and invalid inputs; deferred/skipped gap mechanics and unsupported records have no execution behavior.

### 6. Documentation and integration verification

Update `/doc` only after behavior and service/API contracts are approved. Run the appropriate Maven test/package flow, Phase 5 canonical validator, compatibility checks, and `git diff --check`. Acceptance: tests pass from a clean build, the integration report lists covered and unimplemented behaviors, and no Phase 6 completion status is recorded without separate review.

## Decisions required before checkpoint 1

1. **Runtime resource location and packaging source:** Should canonical files be copied into `src/main/resources/normalized/core-rules/v1/`, loaded from the existing `datasets/phase-3` and `datasets/phase-5` trees during build, or packaged by another approved mechanism? The project currently uses `src/main/resources/normalized/sources/sources.json` for the Source Registry, but that precedent alone does not establish the Step 2 packaging contract.
2. **Application surface:** Which Core Rules use cases must backend consumers support? The roadmap says to update services/APIs “where required,” but no Step 2 query or public API contract is specified. Do not infer endpoints from `/api/sources`.
3. **DTO and union policy:** Define whether Phase 6 should mirror all schema variants immediately or stage domain DTOs by consumer requirement. Rule v1 and Action v1 contain substantial typed unions; a blanket DTO strategy is not described.
4. **Reference policy:** Canonical references can be source-valid even where no canonical target output exists in this dataset or a later domain. Specify whether startup validation accepts pinned-source identity, requires a canonical target, or returns an explicit resolution state. Do not change canonical schema to encode runtime status.
5. **Structural validation runtime:** The repository currently validates Draft 2020-12 schemas through the Node `validate:core-rules` workflow. No Java JSON Schema dependency or runtime-versus-build validation responsibility is approved. Do not add a new runtime dependency without this decision.
6. **Mechanics execution scope:** “Implement deterministic mechanics execution where supported” does not identify which Action, Rule, Condition, Disease, Status, Sense, Skill, or Language behaviors must execute. Approve a bounded list of use cases and behavior tests before adding execution logic.
7. **Test baseline:** The Step 1 Phase 6 report records that the full checked-in Java test source set did not compile cleanly in its isolated build because `DndPubApplicationTests` imports missing Monster classes. Reconfirm and assign ownership of that pre-existing issue before using a full-suite result as Phase 6 acceptance.

## Current checkpoint and stopping point

Phase 5 remediation and closure passed the canonical validator: 9 schemas, 89 examples, 34 Phase 3 records, 551 Phase 5 records, and 838 permanent checks. `git diff --check` passed. Phase 6 implementation tests were not run because no Step 2 integration code was written. Implementation stops here until the decisions above are resolved and this plan is approved. The separate Step 1 Source Registry Phase 6 status is unaffected.
