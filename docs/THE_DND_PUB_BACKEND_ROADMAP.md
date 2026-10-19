# The DnD Pub Data Backend — Development Roadmap

> **Status:** Authoritative working roadmap, 19 October 2026. Step 7.2 and Steps 7.3.1–7.3.3 are implemented. Step 7.4.3 is PARTIAL after batch 7.4.3A: 87 of 969 Spell owners are processed with 155 typed mechanics. All six benchmark fixtures await human review, so authoritative benchmark acceptance remains outstanding.
>
> **Scope:** Java / Spring Boot data backend and its supporting evidence, tests, and APIs. Angular UI, Keycloak integration, persistence, and public internet deployment are future work unless explicitly authorized.

## 0. Mission and guiding principles

Build a reliable, inspectable, source-aware D&D 5e data API that converts pinned 5eTools JSON into strongly typed normalized definitions and **usable, structured gameplay mechanics** for The DnD Pub character manager and tabletop play.

**Current priority: extraction accuracy and reviewability, not licensing clearance.** Every proposed interpretation should be inspectable with its evidence, and uncertainty must be visible. Preserve legal/publication metadata without allowing that metadata to suppress mechanics in the owner's private-network development environment. Do not equate private-network use with a grant to redistribute protected material.

**Development environment:** the owner runs the API on a private network and is not currently exposing it on the public internet. An endpoint named `public` is a Java/API contract name, **not a statement that the deployment is publicly reachable**. The eventual internet-facing release must enforce authorization and publication policies at the backend; frontend filtering alone is not sufficient. Keycloak is planned, not currently implemented.

### Non-negotiable engineering rules

1. Preserve original source identities and edition distinctions (e.g. PHB versus XPHB). Never silently merge differently sourced entries.
2. Keep pinned source bytes, original provenance, and stable IDs intact unless a separately approved source-upgrade step changes them.
3. Prefer strongly typed immutable Java records, enums, explicit schema models, defensive collection copies, and one public top-level type per file.
4. No `Object`, `JsonNode`, or `Map<String,Object>` as substitutes for known, stable source shapes; isolate any unavoidable unknown extension points.
5. Preserve authored choices, prerequisites, alternatives, scaling, targets, timing, and context. Do not convert options into unconditional grants.
6. Distinguish **observed source facts**, **proposed interpretations**, **human-reviewed interpretations**, and **publication rights decisions**.
7. Every interpreted mechanic should be traceable to the exact source entry, source version/fingerprint, and evidence location; expose a safe inspection representation.
8. Abstain rather than guess when an interpretation is unsupported or ambiguous. Partial extraction is a valid outcome.
9. Prefer descriptive mechanics over an executable rules engine. Do not infer game effects solely from proximity of text or tags.
10. Preserve existing REST compatibility unless an API change is explicitly authorized and tested.
11. Require meaningful tests, golden fixtures, full-corpus regression checks, and explicit completion reports for implementation batches.
12. Do not start another numbered step or batch without user approval.

## 1. Core catalogs — COMPLETE

Import the pinned source collection; establish Jackson DTEs, normalized definitions, immutable catalogs and read-only REST access. Keep original source-aware IDs and full-corpus parsing. Historical implementation details are recorded in the corresponding implementation documentation.

## 2. Source normalization — COMPLETE

Normalize source provenance and edition identity consistently across catalogs. Maintain a source registry and stable source fingerprints.

## 3. Mechanics investigation — COMPLETE (FOUNDATIONAL)

Investigate authored mechanics shapes and information loss without inventing effects. Preserve reports as historical evidence, not competing development plans.

## 4. Cross-catalog reference infrastructure — COMPLETE

Provide typed, source-aware reference resolution and distinguish resolved, ambiguous, unresolved and deferred outcomes. Last reported regression baseline: **6,246 outcomes** (2,517 resolved; 2,054 ambiguous; 181 unresolved; 1,494 deferred). Do not infer a match from a name alone.

## 5. Public cross-catalog REST projections — COMPLETE

Expose stable normalized reference projections. Maintain existing public API compatibility while the new mechanics inspection API evolves.

## 6. Remaining source catalogs — COMPLETE FOR APPROVED SCOPE

Implement remaining planned catalogs, including Recipes (reported 316); Homecraft remains excluded. Do not treat an excluded catalog as an accidental omission.

## 7. Mechanics enrichment and validation — ACTIVE

**Primary objective:** produce usable typed mechanics with source evidence, quality status, and inspection endpoints. Publication decisions remain independent. Step 7's completed investigations are foundational inputs, not a requirement to keep investigating indefinitely.

### 7.1. Foundational enrichment and publication groundwork — COMPLETE FOR CURRENT SCOPE

Historical work includes:

- Class/Subclass positioned table cells and Class choice completeness/status analysis.
- Item focus shape, signed bonuses, recharge declarations, charge-related syntax.
- Feat structured mechanics and prerequisites.
- Shared syntax/evidence indexing with source pointers and exact spans.
- Spell formula observations and scaling investigation.
- Legendary group and Monster syntax observations.
- Object attack/hit evidence relationships.
- Spell Public Mechanics v1 P1A status projection; P1B publication crosswalk; P1C candidate manifest/approval design; P1D internal publication gate.

**P1D reported state:** 969 Spell identities; 930 internal formula observations; 250 licensed-match candidates pending review on 195 Spell owners; 458 excluded from that publication gate; **zero authorized formulas**; 547 `NONE_OBSERVED` and 422 `WITHHELD` in the existing public projection; **561 passing Maven tests**; all 175 pinned source hashes unchanged. These are a reported baseline, not permanent hardcoded acceptance values after intentional data changes.

P1D's `SpellPublicationCatalog` is a **publication authorization mechanism**, not a mechanics interpretation or correctness review system. Preserve it, but do not require its approval to inspect proposed mechanics in the owner's private-network environment. Do not silently modify or bypass P1D's authorization logic in existing release-oriented projections.

### 7.2. Mechanics model and review API foundation — COMPLETE

#### 7.2.1. Review the actual repository and finalize the API contract — COMPLETE (DESIGN ONLY)

The [7.2.1 mechanics model and API contract](../analysis/mechanics/step-7/7.2.1-mechanics-model-and-api-contract.md)
records the source/model audit, typed family design, independent review/rights metadata, evidence and
revision semantics, proposed JSON contracts and benchmark strategy. It recommends an opt-in
`interpretedMechanics` envelope on existing Spell details, preserving the existing `mechanics`
classifications and ordinary responses. P1D remains unchanged and does not filter private inspection.
No production code, API response, source file or test was changed. The proposed subdivisions below
require separate implementation authorization; no interpretation or benchmark suite was implemented.

- Inventory existing normalized definitions, evidence indexes, Spell API DTOs, P1D infrastructure and documentation.
- Define a shared typed **mechanics-first** observation model: stable observation identity, mechanic kind, typed payload, applicability/context, source identity, extraction method, evidence, interpretation status and publication metadata.
- Distinguish explicitly authored/structured facts from parsed syntax and inferred interpretations.
- Allow multiple evidence references and incomplete/unsupported interpretations.
- Define stable ordering and backward-compatible response shape. Do not implement a universal rules engine.
- Decide whether mechanics are embedded in Spell details, offered through a companion endpoint, or both. Prefer an additive contract that supports easy inspection without breaking existing clients.

#### 7.2.2. Canonical mechanics values, rights and review metadata — COMPLETE

The [7.2.2 implementation report](../analysis/mechanics/step-7/7.2.2-canonical-mechanics-model-implementation.md)
records the passive canonical model, seven typed payload families, evidence and revision integrity,
independent human review and rights metadata, and strict polymorphic serialization. Verification:
625 passing Maven tests (64 new), unchanged 175 source hashes, catalog identities, public contracts,
930 Spell observations and 6,246 resolver outcomes; packaged startup passed. No interpretation,
REST inspection, production review decisions or publication authorizations were added.

- Implement the passive immutable envelope, narrow sealed payloads, source/evidence references,
  stable claim/revision identities, completeness/diagnostic model and independent review/rights states
  specified by 7.2.1. Preserve the existing normalized definitions and syntax indexes.
- Validate type/serialization invariants, deterministic review summaries, stale-revision handling and
  partial/unknown values. No source-to-effect interpreter, REST change or benchmark implementation.

Use independent properties, not one mixed approval enum:

**Rights classification (provisional vocabulary):**

- `SRD`: coverage established for a specific SRD version and license; include provenance, scope, and attribution reference.
- `LICENSED`: an applicable non-SRD license or permission has been established; include evidence and scope.
- `OPEN`: independently reusable status established under a documented defensible rule; not inferred simply because content is numeric.
- `NEEDS_REVIEW`: insufficient rights determination.

**Mechanics review:**

- `PENDING_REVIEW`: a proposed interpretation not yet independently verified.
- `NEEDS_MECHANICS_REVIEW`: identified uncertainty, unsupported relationship, or failed validation requiring attention.
- `VERIFIED`: independently reviewed against source/expected behavior.
- `REJECTED`: reviewed and found incorrect.

Also track extraction/interpretation state separately (`OBSERVED`, `INTERPRETED`, `UNINTERPRETED` or a carefully designed equivalent). Avoid a fake precision percentage: use evidence-based diagnostics and benchmark results. No automatic `VERIFIED` or rights clearance from a model's confidence claim.

**Publication boundary:** The development inspection response can show all mechanically interpreted observations on the owner's trusted private network, regardless of rights-review state. Rights flags are informational, **not access control**. Before any internet-facing deployment, implement backend enforcement, authorization and a deliberate policy for unreviewed material. Full source prose should not be indiscriminately included in internet-facing responses.

#### 7.2.3. Additive inspection API and evidence contract — COMPLETE

The [7.2.3 implementation report](../analysis/mechanics/step-7/7.2.3-spell-inspection-api-implementation.md)
records the opt-in detail/by-name envelope, dedicated inspection projection and startup-validated
immutable owner index. At 7.2 completion, all 969 Spells reported NOT_ATTEMPTED / NOT_ASSESSED, with 930 formula
references across 422 owners and no invented effects. Verification: 653 passing tests (28 new),
packaged HTTP smoke checks and exact ordinary-response comparisons, unchanged 175 source hashes,
6,246 resolver outcomes and zero P1D authorizations. Step 7.2 exit criteria pass.

- Expose typed mechanics, statuses, diagnostic reasons and evidence locators; support browsing by Spell ID and distinct PHB/XPHB variants.
- Use the proposed `include=interpretedMechanics` detail/by-name contract. Keep default responses,
  summaries, legacy constructors and P1A formula status behavior compatible. Before an interpreter
  exists, return NOT_ATTEMPTED / NOT_ASSESSED rather than fabricated effects or false completeness.
- Keep source fingerprints and internal evidence pointers available to development reviewers; separate safe metadata from full source text if needed.
- Reuse existing syntax evidence and indexes instead of reparsing or duplicating them.
- Add tests for absence of invented effects, null/empty semantics, ordering, provenance, immutability and existing REST compatibility.
- Preserve private-network deployment assumptions and document the required production hardening; do not pretend that Angular hides server responses.

**Exit criterion for 7.2:** the API can return an empty/partial, evidence-backed mechanics representation for an existing Spell without falsely claiming interpretation or approval; existing catalog/API regressions remain green.

#### 7.2.4. Documentation consolidation — COMPLETE (MAINTENANCE ONLY)

The [maintenance report](development/DOCUMENTATION-CONSOLIDATION.md) records the inventory,
documentation hierarchy, indexed historical archive, preserved artifact dependencies and hygiene
review. Runtime resources, source data, approval decisions and mechanics behavior are unchanged.
The [documentation index](README.md) and [analysis index](../analysis/README.md) identify current
guidance. This maintenance note does not start or change the scope of Step 7.3.

### 7.3. Spell mechanics interpretation pilot — IN PROGRESS

#### 7.3.1. Benchmark fixtures and validation harness — COMPLETE (HUMAN REVIEW PENDING)

The [implementation report](../analysis/mechanics/step-7/7.3.1-spell-benchmark-implementation.md)
records six versioned fixtures: Shield PHB/XPHB, Fireball PHB, Healing Word PHB, Magic Missile PHB
and Prestidigitation PHB. All are PENDING_HUMAN_REVIEW, with 13 proposed typed claims, five formula
controls and 16 deferred expectations. The isolated comparison harness separates semantic, evidence,
abstention and completeness results, with exact source/revision joins and independently configured
human review authority. Pending fixtures cannot produce approved benchmark acceptance.

Verification: 702 passing Maven tests (49 new), packaged startup/HTTP checks, unchanged 175 source
hashes, 969 Spell identities, 930 observations, public contracts and 6,246 resolver outcomes; P1D
still authorizes zero observations. No interpreter, production interpretations or approved gold set
were created. Review/adjudicate the two Shield fixtures before claiming authoritative pilot accuracy.

#### 7.3.2. Shield end-to-end interpretation — COMPLETE

Use the exact pinned PHB and XPHB entries as separate sources. Attempt typed representation of the reaction trigger, AC bonus, duration, and Magic Missile interaction. Distinguish explicitly encoded metadata from prose interpretation. Do not assume a single evidence path or identical wording across editions. Return partially interpreted results when necessary.

Implemented in the [7.3.2 report](../analysis/mechanics/step-7/7.3.2-shield-extraction-implementation.md):
five claims per edition, with exact source evidence and deterministic startup extraction. Two owners
are PROCESSED / PARTIAL; the other 967 remain NOT_ATTEMPTED / NOT_ASSESSED. All ten claims have
recorded PENDING_REVIEW and licensing NEEDS_REVIEW. Effective review is four pending and six
needing mechanics review because of unresolved mentions and dependencies; none is verified.
Candidate diagnostics match 5/5 proposed claims and 17/17 evidence associations per edition;
these are not approved gold results.

Verification: 725 passing Maven tests (23 new), final packaged startup/HTTP checks, byte-identical
ordinary response samples, unchanged 175 source hashes, 969 Spell identities, 930 observations and
6,246 resolver outcomes. P1D still authorizes zero observations. No other Spell family, combat engine,
human fixture approval or publication decision was implemented.

#### 7.3.3. Benchmark validation and Spell extraction expansion — IMPLEMENTED

Measure precision, recall, evidence alignment, false positive rate and abstention quality against reviewed fixtures. Define a reproducible rule for `NEEDS_MECHANICS_REVIEW` based on known unsupported shapes, failed assertions, ambiguous relationships or missing evidence; a global benchmark score alone cannot certify an individual mechanic.

The authorized scope also implements bounded PHB Fireball and Healing Word patterns; see the
[7.3.3 implementation](../analysis/mechanics/step-7/7.3.3-spell-extraction-expansion.md). Startup now
processes four owners: five claims per Shield edition, two Fireball claims and one Healing Word
claim. The other 965 remain unattempted. Fireball links failed-save fire damage to a Dexterity-save
requirement with unknown DC; successful-save half damage, targeting and scaling remain unsupported.
Healing Word preserves the dice-plus-symbolic sum, with targeting/exclusions and scaling unsupported.
All new claims are partial, recorded PENDING_REVIEW, effective NEEDS_MECHANICS_REVIEW, and rights
NEEDS_REVIEW. Shield output is unchanged.

Verification: 743 passing Maven tests (18 new), packaged HTTP/ordinary-response and complete Shield
inspection comparisons pass. All 175 source hashes, 969 identities, 930 observations and 6,246
resolver outcomes remain unchanged. P1D still authorizes zero observations. Four-fixture candidate
diagnostics match 13 proposed claims and 39 evidence associations, honoring five expected abstentions
with no qualifier, negative-rule or completeness errors. These are not independently validated
accuracy results; no fixture or mechanic was human-approved. The reviewed-fixture acceptance
portion of the 7.3 exit criterion remains outstanding.

**Exit criterion for 7.3:** Shield and the approved pilot fixtures have inspectable typed outputs, explicit gaps, independent expectations and passing targeted regression tests.

### 7.4. Expand Spell interpretation — IN PROGRESS

Iteratively add supported families: damage, healing, modifiers, saving throws, attack rolls, targeting/area, duration/concentration, conditions, resource use, triggers, scaling and choice-dependent effects. Preserve distinctions among authored structured fields, formula syntax, conditional rules and natural-language effects. Measure full-corpus coverage and errors; avoid claiming all 969 Spells are fully interpreted when some are partial.

**Exit criterion:** documented per-family support and per-Spell interpretation completeness, stable evidence links and meaningful benchmark coverage.

#### 7.4.1. Generalize existing Spell mechanics extraction — COMPLETE

The startup registry now composes the edition-specific Shield adapter with bounded PHB saving-throw,
failed-save damage and symbolic healing patterns. In addition to the original four owners, it
processes Burning Hands, Circle of Death, Cone of Cold, Lightning Bolt, Shatter, Cure Wounds, Mass
Cure Wounds, Mass Healing Word and Prayer of Healing. The resulting 13 owners have 27 partial claims;
the other 956 remain NOT_ATTEMPTED. See the
[7.4.1 implementation report](../analysis/mechanics/step-7/7.4.1-generalized-spell-extraction.md).
All new claims remain pending mechanics review and NEEDS_REVIEW for licensing. Existing pending
benchmark fixtures are unchanged and remain candidate-only evidence.

#### 7.4.2. Bounded Spell pattern expansion and adjudication — COMPLETE

Choose the next narrow extraction scope from explicit unsupported or unassessed findings and
reviewer feedback. Keep unsupported outcomes visible; do not introduce broad scaling, condition,
resistance or arbitrary prose interpretation without evidence and model support.

The completed bounded expansion adds reusable damage and save-outcome interpretation for ten
additional PHB Spell owners. The catalog now processes 23 of 969 Spell owners and contains 64 typed
mechanics, including 17 slot-scaling claims. All new mechanics remain pending review and NEEDS_REVIEW
for licensing. Other effect riders, ambiguous or multiple damage clauses, and unrecognized scaling
wording remain unsupported or unattempted. See the
[7.4.2 implementation report](../analysis/mechanics/step-7/7.4.2-spell-damage-healing-scaling.md).
Do not begin 7.4.3B or 7.4.4 as part of this batch.

#### 7.4.3. Spell modifiers, conditions and status effects — PARTIAL

Batch 7.4.3A adds the typed `ConditionChangeMechanic` and a bounded PHB mapping for explicit
`conditionInflict` and `conditionImmune` source classifications. It processes 64 additional owners,
bringing the catalog to 87 processed owners and 155 typed mechanics, including 91 condition claims.
Application, removal and immunity have distinct operations; this batch extracts application and
immunity only. Claims remain partial, pending review and NEEDS_REVIEW for licensing. Exact source
fields and available same-sentence qualifier text are retained; target context, typed save/trigger
dependencies, recurring saves, concentration and complex endings remain unsupported. See the
[7.4.3A implementation report](../analysis/mechanics/step-7/7.4.3A-spell-conditions-status-effects.md).
Step 7.4.3 remains PARTIAL; do not begin 7.4.3B or 7.4.4 without authorization.

### 7.5. Monster mechanics interpretation — PLANNED

Reuse the common mechanics model for actions, attacks, saves, traits, legendary actions and spellcasting. Preserve action economy, authored alternatives, recharge and nested effect context. Reuse Batch 7/8 evidence and identify missing effective text-origin mappings rather than manufacturing them.

### 7.6. Item, Feat, Class and feature interpretation — PLANNED

Reuse Item/Feat/Class enrichment, recharge and prerequisites evidence. Preserve choice semantics, attunement, limited uses, recharge, conditional effects and class-level progression. Distinguish source facts from inferred grants. Extend the benchmark suite by mechanic family.

### 7.7. Cross-catalog mechanics and quality consolidation — PLANNED

Resolve mechanic references to typed catalog identities where supported; preserve ambiguity/deferred outcomes. Build repeatable corpus-level validation, coverage reports, compatibility checks and reviewer workflows. Treat human overrides as future persistence concerns, not source-file edits.

### 7.8. External publication readiness — DEFERRED

When an internet-facing API is actually planned: apply P1D and subsequent approved publication policies to release projections, deliver required attribution, verify rights status and source provenance, implement backend role-based filtering/authentication (Keycloak if selected), and audit what a client can actually retrieve. No frontend-only protection. This is distinct from extraction completeness.

## 8. Remaining cross-catalog integration — PLANNED

Integrate any catalog and mechanics links not covered by 7.7. Reconcile scope before implementation to avoid duplicate work. Keep original unresolved and ambiguous identities visible.

## 9. Comprehensive automated validation — PLANNED

Strengthen full-corpus parsing, deterministic fixtures, source fingerprints, schema compatibility, cross-catalog integrity, review/benchmark regression, packaged-JAR smoke tests and CI checks. Existing tests continue throughout earlier steps; this is the final systematic hardening phase.

## 10. Persistent database storage and review workflow — PLANNED

Introduce persistence only after the mechanics contract stabilizes. Store normalized imports, source revisions, reviewer decisions and corrections with distinct provenance. Prefer immutable imported source snapshots plus separately versioned human overrides; reimport must not silently erase reviewed corrections. Define migration and rollback behavior. Do not use a database as a substitute for accurate extraction.

## 11. Source revision, update and synchronization — PLANNED

Design controlled upgrades from pinned 5eTools v2.37.0 to later snapshots. Compare source fingerprints, changed entry identities, interpretation validity, review decisions and rights evidence. Stale evidence/approvals must not silently carry forward. Maintain reproducible import and rollback.

## Future adjacent work (not part of current backend implementation authorization)

- Angular character manager consumption and review UX.
- Keycloak identity, roles and access-control enforcement.
- Public internet deployment and rights/attribution release policy.
- Runtime game execution or a complete rules engine, if ever desired.

## Repository documentation and analysis organization

- **Authority:** this file keeps its existing name and is the sole roadmap. [AGENTS.md](../AGENTS.md)
  is the agent entry point; [docs/README.md](README.md) indexes current architecture and contracts.
- **Active references:** [analysis/active/mechanics](../analysis/active/mechanics/README.md) points to
  the approved mechanics design, actual implementations and the current inspection API.
- **Historical archive:** [analysis/archive](../analysis/archive/README.md) indexes completed reports.
  Their old plans and test counts are historical, not competing instructions. Hash-pinned reports
  retain their original paths instead of being moved indiscriminately.
- **Evidence:** [analysis/artifacts](../analysis/artifacts/README.md) identifies manifests, schemas,
  gold sets, source fingerprints and scripts. Runtime/test consumers keep their original paths.
- **Maintenance:** the [consolidation report](development/DOCUMENTATION-CONSOLIDATION.md) records
  every inventoried artifact, dependencies, exact-original snapshots, validation and deferred hygiene
  decisions. No empty benchmark/extraction directories or duplicate roadmap were created.

For future maintenance, inventory dependencies before moving artifacts, preserve original evidence,
use Git-aware relocations where safe, validate links and hashes, and report hygiene issues without
printing credentials or rewriting history. Do not treat analysis assets as disposable generated text.

## Codex operating protocol

For every numbered batch:

1. Read `AGENTS.md`, this roadmap, the relevant architecture documents, and `.agents/skills/dnd-json-mapper/SKILL.md` (invoke `$dnd-json-mapper` where available).
2. Confirm the exact step number, goal, in-scope/out-of-scope items and current baseline.
3. Inspect existing implementation before proposing new types, endpoints or indexes.
4. Implement only the authorized scope. Preserve historical evidence and existing behavior unless explicitly approved.
5. Test at the appropriate level: focused unit tests, full Maven suite for production changes, source fingerprints, resolver baselines, deterministic exporters and packaged startup where relevant.
6. Write one concise implementation report for the batch: changed files, contract, design choices, metrics, tests, limitations, follow-up proposals.
7. Update the roadmap **status only** when work is verified. Do not invent new top-level phases or ad hoc batch names; use `7.2`, `7.2.1`, etc.
8. Stop and await approval before proceeding.

## Current decision log

| Decision | State |
|---|---|
| Step 7 remains the active phase | Agreed |
| P1A–P1D remain historical identifiers within 7.1 | Agreed |
| Mechanics-first interpretation with evidence | Agreed |
| Rights classification and mechanics review are independent | Agreed |
| Inspect all proposed mechanics on owner's private network | Agreed, subject to network isolation |
| Public internet deployment / role-based filtering | Deferred |
| Database persistence and reviewer overrides | Deferred |
| Repository documentation consolidation | Complete as maintenance milestone 7.2.4; see linked inventory/report |
| Evidence and historical report relocations | Retained where fingerprint or tooling dependencies exist; no artifact deletions |

## Immediate next action

**Step 7.4.3 is PARTIAL after batch 7.4.3A.** The six benchmark fixtures still require human review before
authoritative benchmark acceptance; there is no approved gold set or independently validated
extractor accuracy. Candidate diagnostics do not constitute mechanics verification or publication
permission. Do not begin 7.4.3B or 7.4.4 as part of this batch or modify P1D,
Keycloak, database or frontend work from this mechanics batch.
