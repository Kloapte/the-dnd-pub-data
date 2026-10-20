# The DnD Pub Data Backend — Development Roadmap

> **Status:** Authoritative working roadmap, 21 October 2026. Step 7.2 and Steps 7.3.1–7.3.3 are implemented. Step 7.4.3 remains PARTIAL after batch 7.4.3C. Step 7.4.4 is IN PROGRESS with 7.4.4A through 7.4.4S complete: 124 of 969 Spell owners are processed with 261 typed mechanics; all six benchmark fixtures await human review, so authoritative benchmark acceptance remains outstanding.
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
bringing the catalog to 87 processed owners and 156 typed mechanics, including 91 condition claims
and the exclusive condition-choice claim added in the corrective addendum.
Application, removal and immunity have distinct operations; this batch extracts application and
immunity only. Claims remain partial, pending review and NEEDS_REVIEW for licensing. Exact source
fields and available same-sentence qualifier text are retained; target context, typed save/trigger
dependencies, recurring saves, concentration and complex endings remain unsupported. See the
[7.4.3A implementation report](../analysis/mechanics/step-7/7.4.3A-spell-conditions-status-effects.md).
This 7.4.3A batch left condition targets, typed save dependencies, recurring saves, concentration and
complex endings unsupported or partial. Batch 7.4.3B addresses a bounded set of those gaps below.

Batch 7.4.3B adds typed saving-throw condition dependencies, target scopes, declared duration and
concentration relationships, and selected condition-ending relationships for five existing PHB
owners. Four new saving-throw requirement claims bring the catalog to 160 mechanics; no owners were
added. Complex conditional endings remain partial or abstained. All claims remain pending human
review and NEEDS_REVIEW for licensing. See the
[7.4.3B implementation report](../analysis/mechanics/step-7/7.4.3B-condition-dependencies-endings.md).
Step 7.4.3 remained PARTIAL after B; its subsequent C batch is recorded below.

Batch 7.4.3C adds reusable typed statistic modifiers, damage resistance/immunity operations,
damage type selections, source-backed applicability, modifier save-outcome dependencies, and
bounded target-count context. Eight additional PHB owners contribute eleven claims, bringing the
catalog to 95 processed owners and 171 typed mechanics. Shield remains on its existing AC/prevention
mechanics. Mage Armor's conditional endings remain partial; generic numeric damage reduction,
damage immunity extraction and the wider modifier/resistance corpus remain unsupported or
unattempted. Absorb Elements is absent from the pinned PHB source and was skipped. All new claims
remain pending mechanics review and NEEDS_REVIEW for licensing. See the
[7.4.3C implementation report](../analysis/mechanics/step-7/7.4.3C-spell-modifiers-resistances-protection.md).
Step 7.4.3 remains PARTIAL because planned modifier and protective effect coverage is bounded.

#### 7.4.4. Corpus-wide Spell capability and interpretation — IN PROGRESS

##### 7.4.4A. Corpus-wide Spell extraction capability inventory — COMPLETE

The offline, deterministic inventory assesses all 969 source-aware Spell identities against the
existing Shield and PHB extraction patterns. The refreshed inventory records 103 currently processed
owners, 272 potential typed claims, and 57 convertible but unprocessed owners. Classifications are
0 SUPPORTED, 160 PARTIALLY_SUPPORTED, 428 UNSUPPORTED based on source-authored structured mechanic
classifications without a matching pattern, and 381 NOT_ASSESSED where evidence is insufficient.
The original 7.4.4A result (95 processed, 171 claims, 493 unsupported) is preserved in the baseline
files. The inventory distinguishes capability from processing, preserves PHB/XPHB identities, and
isolates failures per owner. Its current reports are
[JSON](../analysis/mechanics/step-7/7.4.4A-spell-capability-inventory.json) and
[Markdown](../analysis/mechanics/step-7/7.4.4A-spell-capability-inventory.md). This read-only assessment
does not activate corpus-wide production extraction, alter REST behavior, or change review or
publication decisions.

##### 7.4.4B. Generalize saving-throw and damage extraction — COMPLETE

Investigation of the inventory grouped repeated PHB gaps into explicit save clauses, damage clauses
split across adjacent sentences, source-tagged multi-component damage, and simple typed amounts.
Reusable patterns now cover `must make` / `must succeed on` saves with local subject context, explicit
failed-save damage and successful-save half/no outcomes, multiple typed damage components, dice and
dice-plus-integer amounts, and existing unambiguous single-type slot scaling. Ambiguous component
scaling, repeated saves, conditional triggers and unrelated riders remain unrepresented.

The offline inventory finds 65 newly convertible Spell owners and 101 additional potential claims
versus the preserved baseline. Saving-throw and damage structured-gap reason counts each fell by 72;
39 save-classified and 44 damage-classified owners from those groups now yield claims. Eight
representative owners were added to bounded production matching: Acid Splash, Arms of Hadar,
Call Lightning, Calm Emotions, Disintegrate, Dissonant Whispers, Ice Storm and Thunderwave. This
increases processed owners by eight and production claims by 19 (95 to 103 owners; 171 to 190 claims).
No regressions were found among previously processed owners. Classifications now stand at 0
SUPPORTED, 160 PARTIALLY_SUPPORTED, 428 UNSUPPORTED and 381 NOT_ASSESSED. All eight new owners remain
partial; no completeness is inferred from claim count.

The inventory cannot currently assign SUPPORTED: canonical assembly coverage always leaves `/entries`
unassessed and marks emitted claims PARTIAL. This coverage limitation is documented but not redesigned.
The current 7.4.4A reports are regenerated; their original baseline is preserved separately. The
[before/after comparison](../analysis/mechanics/step-7/7.4.4B-saving-throw-damage-comparison.md)
includes claim and classification transitions. Review and licensing defaults remain unchanged, and
no REST contracts, benchmark fixtures, pinned JSON or publication decisions changed.

At the completion of 7.4.4B, condition generalization, full-corpus production activation, Step 7.5,
and frontend work remained out of scope for that batch. Condition work is recorded in the separately
authorized 7.4.4C section below.

##### 7.4.4C. Generalize Spell condition extraction — COMPLETE

PHB condition handling now reuses the existing typed saving-throw and condition bindings for any
single-ability save whose source explicitly connects failure to an inflicted condition. One save claim
is reused across linked conditions; generic target scopes are retained where stated; ambiguous
multi-ability relationships abstain. Explicit condition removal is generalized for condition-ending
clauses, one-condition choice lists, and named effects that impose conditions. Negations, protection,
prerequisites, and other unclear references remain unsupported rather than becoming unconditional
`APPLY` claims.

The bounded production additions are Greater Restoration, Lesser Restoration, and Power Word Heal.
The three owners add ten condition-removal claims; 26 existing condition owners gain typed save
requirements. Production coverage rises from 103 owners / 190 claims to 106 owners / 228 claims.
The offline inventory assesses all 969 identities and records 310 potential claims, with 57
convertible but unprocessed owners. Classifications are 0 SUPPORTED, 163 PARTIALLY_SUPPORTED, 440
UNSUPPORTED, and 366 NOT_ASSESSED. No previously processed owner lost candidate claims, and there
were no per-Spell extraction failures. The existing structured condition gap remains 131 owners;
newly assessed tag-only ambiguity has explicit diagnostics. Claims remain partial, pending human
review, and `NEEDS_REVIEW` for licensing. The private inspection contract remains compatible.

The follow-up correction groups Lesser Restoration's four typed `REMOVE` conditions as one-of-four
alternatives and Greater Restoration's charmed/petrified removals as one-of-two. The former retains
an explicit unmodeled disease option; Greater Restoration's other remedy types remain unmodeled.
Power Word Heal's four removals remain independent. This adds two choice-group claims while keeping
all existing removal claim IDs and private REST compatibility. The C inventory now reports 310
potential claims and 228 production claims; classifications remain 0 SUPPORTED, 163
PARTIALLY_SUPPORTED, 440 UNSUPPORTED, and 366 NOT_ASSESSED. The correction adds two claims, no newly
convertible owners, and no classification changes. Focused tests pass (62), and the full
Maven suite passes (790); repeated inventory outputs are byte-identical and `git diff --check` passes.

The refreshed current inventory remains at the [7.4.4A report paths](../analysis/mechanics/step-7/7.4.4A-spell-capability-inventory.md);
the original A baseline and B comparison are preserved. A B inventory snapshot and the [C condition
comparison](../analysis/mechanics/step-7/7.4.4C-condition-extraction-comparison.md) record before/after
results. The [7.4.4C implementation report](../analysis/mechanics/step-7/7.4.4C-condition-extraction-generalization.md)
documents source patterns and limitations. Canonical coverage still leaves `/entries` unassessed, so
no candidate reaches SUPPORTED; coverage tracking was not redesigned. Step 7.4.3 remains PARTIAL and
parent Step 7.4.4 remains IN PROGRESS. Verification: focused extractor/inventory/private REST tests,
790 passing Maven tests, deterministic repeated report output, and `git diff --check`.

##### 7.4.4D. Spell mechanic-family discovery — COMPLETE (analysis only)

The read-only discovery report verifies the current 969-owner inventory baseline (163 PARTIALLY_SUPPORTED,
440 UNSUPPORTED, 366 NOT_ASSESSED, 0 SUPPORTED; 106 production owners, 228 typed claims, and 310
potential typed claims). It scans pinned source fields and authored `entries`/`entriesHigherLevel` text,
counts each source-aware owner once per family, and records overlapping family counts, classifications,
representative source pointers, candidate model fit, false-positive limits, and priorities. Structured
condition-immunity evidence is the clearest bounded follow-up candidate; no extraction or coverage change
is authorized by this analysis.

See the [7.4.4D discovery report](../analysis/mechanics/step-7/7.4.4D-mechanic-family-discovery.md)
and its [deterministic machine summary](../analysis/mechanics/step-7/7.4.4D-mechanic-family-discovery.json).
Step 7.4.3 remains PARTIAL, and parent Step 7.4.4 remains IN PROGRESS. Verification: inventory baseline
reconciled; repeated machine-summary output is byte-identical; no source, fixture, inventory, or historical
report was changed; `git diff --check` passes.

##### 7.4.4E. Structured Spell condition immunity extraction — COMPLETE

The bounded production rule assesses eight verified AU/TCE/XPHB owners with structured `conditionImmune`
fields. Five owners yield seven `GRANT_IMMUNITY` claims with source-bound subject, duration and evidence;
Tasha's Otherworldly Guise, XPHB Freedom of Movement, and XPHB Heroes' Feast are processed with explicit
ambiguity diagnostics and no immunity claims. Existing PHB condition documents remain unchanged. Iron
Body's separate removal of already-present conditions remains explicitly diagnosed rather than being
conflated with immunity.
Production processing rises from 106 to 114 owners and 228 to 235 claims. Inventory classifications move
from 163 to 168 PARTIALLY_SUPPORTED and from 440 to 435 UNSUPPORTED; NOT_ASSESSED remains 366 and
SUPPORTED remains 0 because the existing coverage-accounting limitation is unchanged. Review and licensing
defaults remain pending/`NEEDS_REVIEW`, and no mechanics are publication-authorized.

See the [7.4.4E implementation report](../analysis/mechanics/step-7/7.4.4E-structured-condition-immunity.md)
for all 12 candidate decisions, evidence pointers, claims, abstentions, and verification. The preview
inventory/comparison was written under `target/`; historical reports and pinned sources were preserved.
Step 7.4.3 remains PARTIAL and parent Step 7.4.4 remains IN PROGRESS.

##### 7.4.4F. Spell attack-roll modeling and feasibility — COMPLETE (analysis only)

The analysis verifies the post-E inventory baseline: 969 Spell identities, 168 PARTIALLY_SUPPORTED,
435 UNSUPPORTED, 366 NOT_ASSESSED, 0 SUPPORTED, 114 processed owners, and 235 production claims.
The 7.4.4D discovery's 75 attack-roll cue owners remain a search signal, not an eligibility list.
Its machine summary preserves only five representative owners, so the full post-E owner-by-owner
classification rejoin is not asserted. A direct pinned-corpus scan confirms 65 non-empty structured
`spellAttack` owners and documents why the field alone cannot prove an actual spell attack.

The analysis finds that existing damage, hit-trigger, target, statistic modifier, timing, evidence,
revision, review and licensing types cover useful parts of the family but do not represent the attack
roll itself or general hit/miss/critical dependencies. It proposes a bounded `AttackRollMechanic`
and revision-bound outcome binding, with a one-owner Inflict Wounds pilot recommendation for a
future separately authorized batch. No model or extraction behavior is changed by F.

See the [7.4.4F report](../analysis/mechanics/step-7/7.4.4F-attack-roll-modeling.md) and
[deterministic machine summary](../analysis/mechanics/step-7/7.4.4F-attack-roll-modeling.json).
Step 7.4.3 remains PARTIAL; parent Step 7.4.4 remains IN PROGRESS. Verification: baseline checked
against the E preview; source-field owner count reconciled with D evidence-mode counts; deterministic
JSON SHA-256 repeated identically; no source, fixture, production, test or historical report changes;
`git diff --check` passes.

##### 7.4.4G. Attack-roll canonical model and Inflict Wounds pilot — COMPLETE

This bounded pilot adds a typed `AttackRollMechanic` and revision-bound `AppliesOnAttackRollOutcome`
relationship for exactly `spell:phb:inflict-wounds`. Its new attack-roll and hit-damage claims retain
pending review and licensing states. Inflict Wounds moves from unattempted with no claims to partial
coverage with two claims; its inventory classification remains PARTIALLY_SUPPORTED. Corpus totals
are 969 identities, 168 PARTIALLY_SUPPORTED, 435 UNSUPPORTED, 366 NOT_ASSESSED, 0 SUPPORTED, 115
processed owners, and 237 production typed claims. No other attack owner is activated. Higher-level
damage scaling and the wider candidate family remain deferred.

The [7.4.4G implementation report](../analysis/mechanics/step-7/7.4.4G-attack-roll-canonical-model-and-inflict-wounds-pilot.md)
records exact IDs, revision hashes, evidence pointers/spans, baseline comparisons and verification.
The pre/post owner-level comparison changes only Inflict Wounds in canonical mechanics and private
inspection; default REST output is unchanged for all 969 owners. Inventory and comparison previews
were generated under `target/`; pinned source data, benchmark fixtures and historical reports remain
unchanged. Focused tests and the 800-test Maven suite pass. Step 7.4.3 remains PARTIAL and parent
Step 7.4.4 remains IN PROGRESS.

##### 7.4.4H. Simple Spell attack generalization — COMPLETE

The bounded PHB attack rule now covers the Inflict Wounds pilot and the individually reviewed
`spell:phb:ray-of-frost` owner. Ray of Frost contributes an attack-roll claim, HIT-bound cold damage,
and a HIT-bound movement-speed reduction ending at the caster's next turn start. Cantrip scaling is
an evidence-backed unsupported diagnostic. Fire Bolt, Chill Touch, and Shocking Grasp remain
unactivated because their target/effect/attack-modifier semantics cannot be represented without
omitting material mechanics.

The [7.4.4H report](../analysis/mechanics/step-7/7.4.4H-simple-spell-attack-generalization.md)
records owner eligibility, claim revisions, evidence spans, before/after hashes, and verification.
Inventory totals are 969 identities, 116 processed owners, and 240 typed claims; classifications
remain 168 PARTIALLY_SUPPORTED, 435 UNSUPPORTED, 366 NOT_ASSESSED, and 0 SUPPORTED. Inflict Wounds
and all unrelated owners remain byte-identical in canonical output; default REST is unchanged for
all 969 owners. Focused extractor, inventory, inspection, benchmark, and Shield tests pass; the
802-test Maven suite passes. Step 7.4.3 remains PARTIAL and parent Step 7.4.4 remains IN PROGRESS.

##### 7.4.4I. Cantrip damage scaling — COMPLETE

Added the typed `CharacterLevelDamageScaling` and `CharacterLevelDamageTier` extension to
`DamageMechanic`, keeping character-level progression distinct from spell-slot scaling. The
already-eligible `spell:phb:ray-of-frost` damage claim now records the source-backed 1d8/2d8/3d8/4d8
progression at levels 1/5/11/17. Its existing damage mechanic ID is unchanged and advances from
revision 1 to revision 2, superseding the captured revision 1 hash; its attack and movement-speed
claims are unchanged. The now-fully-represented cantrip-scaling diagnostic is removed. Acid Splash
remains unchanged because its one-or-two-creature targeting choice is not fully represented.

The [7.4.4I report](../analysis/mechanics/step-7/7.4.4I-cantrip-damage-scaling.md) records the
preimplementation model decision, source pointers, bounded owner review, claim revisions, inventory
counts, and comparisons. Inventory remains at 969 identities, 116 processed owners, 240 typed claims,
168 PARTIALLY_SUPPORTED, 435 UNSUPPORTED, 366 NOT_ASSESSED, and 0 SUPPORTED. Focused extraction,
inventory/benchmark previews, and the 814-test Maven suite pass. Character-level scaling remains a
pending private interpretation; review/licensing are not approved and publication authorization
remains zero. Step 7.4.3 remains PARTIAL and parent Step 7.4.4 remains IN PROGRESS.

##### 7.4.4J. Creature/object target eligibility — COMPLETE

The typed `SpellTargetSubject` supports exactly one selected target eligible from mutually exclusive
`CREATURE` or `OBJECT` categories. The PHB-only Fire Bolt pilot adds attack-roll and HIT-bound damage
claims with its explicit character-level damage progression. Its separately qualified object-ignition
rule remains an evidence-backed unsupported diagnostic because the model cannot yet represent the
flammability and not-worn-or-carried requirements. No other attack owner or source edition was activated.

The [7.4.4J report](../analysis/mechanics/step-7/7.4.4J-creature-object-target-eligibility.md)
records source review, exact claim revisions, target validation, ignition abstention, previews and
compatibility hashes. Inventory remains at 969 identities, 117 processed owners, and 242 typed claims;
classifications remain 168 PARTIALLY_SUPPORTED, 435 UNSUPPORTED, 366 NOT_ASSESSED, and 0 SUPPORTED.
Canonical documents and private inspection change only for Fire Bolt; default REST is byte-identical
for all 969 owners. The 818-test Maven suite passes, with one existing skipped test. Review/licensing
remain pending and publication authorization remains zero. Step 7.4.3 remains PARTIAL and parent
Step 7.4.4 remains IN PROGRESS.

##### 7.4.4K. Conditional on-hit effects — COMPLETE

The shared PHB one-roll spell attack rule now extracts source-backed hit point recovery prevention for
Chill Touch and reaction prevention for Shocking Grasp, each bound one-way to the attack claim's HIT
outcome and source-specified turn boundary. The two effects use dedicated typed payloads. Chill Touch's
undead-target attack disadvantage and Shocking Grasp's pre-hit metal-armor advantage remain explicit,
source-evidenced unsupported diagnostics because target predicates and advantage semantics are not in
the current model. No other owner or source edition was activated.

The [7.4.4K report](../analysis/mechanics/step-7/7.4.4K-conditional-on-hit-effects.md) records both
candidate reviews, claim IDs and content hashes, evidence spans, abstentions, inventory metrics and
compatibility comparisons. Inventory remains at 969 identities, 168 PARTIALLY_SUPPORTED, 435
UNSUPPORTED, 366 NOT_ASSESSED, and 0 SUPPORTED; processed owners increase to 119 and production claims
to 248. Canonical documents and private inspection change only for Chill Touch and Shocking Grasp;
default REST remains byte-identical for all owners. The 827-test Maven suite passes with one skipped
test. Review/licensing remain pending and publication authorization remains zero. Step 7.4.3 remains
PARTIAL and parent Step 7.4.4 remains IN PROGRESS.

##### 7.4.4L. Conditional attack-roll advantage and disadvantage — COMPLETE

Added the typed `AttackRollModifierMechanic` with explicit advantage/disadvantage and selectors for
an existing revision-bound attack roll or future rolls identified by actor and target roles.
`AppliesWhen`, `AppliesOnAttackRollOutcome`, and `AppliesForDuration` separately represent the
qualifier, activation event, and duration. Shocking Grasp's modifier applies to its own attack roll
before the outcome when its target wears metal armor. Chill Touch's modifier applies after a HIT to
future attacks by the affected undead creature against the original caster, until the end of that
caster's next turn. The two K diagnostics are resolved; no other owner or edition was activated.

The [7.4.4L report](../analysis/mechanics/step-7/7.4.4L-conditional-attack-roll-modifiers.md)
records exact claims, revision hashes, source evidence spans, model validation, owner-level comparisons,
and deterministic previews. Inventory remains at 969 identities, 119 processed owners, and 250
production claims; classifications remain 168 PARTIALLY_SUPPORTED, 435 UNSUPPORTED, 366 NOT_ASSESSED,
and 0 SUPPORTED. Only Chill Touch and Shocking Grasp changed in canonical documents and private
inspection; default REST is byte-identical for all 969 owners. The forked-compiler Maven suite passes
831 tests with one skipped test. Review and licensing remain pending; publication authorization remains
zero. Step 7.4.3 remains PARTIAL and parent Step 7.4.4 remains IN PROGRESS.

##### 7.4.4M. Multi-instance Spell mechanics feasibility and design — COMPLETE

Completed a source-based feasibility review of PHB Scorching Ray, Eldritch Blast and Magic Missile.
The report distinguishes independent attack instances, automatic-hit damage instances, one damage
event across multiple targets, and one attack affecting multiple targets. It proposes typed count
progression, per-instance target allocation and corresponding-attack HIT binding for attack spells,
plus a separate automatic-hit damage-series payload for Magic Missile. No owners, claims, production
types, fixtures or canonical outputs changed. The existing Magic Missile benchmark remains pending
human review with its three deferred expectations intact.

The [7.4.4M report](../analysis/mechanics/step-7/7.4.4M-multi-instance-spell-feasibility.md)
records exact authored evidence pointers and spans, structured-field limits, model gaps, proposed
canonical JSON, migration risks and a staged implementation sequence. The inventory remains at 969
identities, 119 processed owners, 250 production claims and 326 potential claims. Focused inventory,
inspection and benchmark tests pass (84 tests, 0 failures, 0 errors, 1 skipped); `git diff --check`
passes. Review/licensing remain pending and publication authorization remains zero. Step 7.4.3 remains
PARTIAL and parent Step 7.4.4 remains IN PROGRESS.

##### 7.4.4N. Eldritch Blast multi-attack pilot — COMPLETE

Activated only PHB Eldritch Blast. Added typed character-level instance-count progression and
per-instance target allocation to `AttackRollMechanic`, plus a one-way corresponding-instance HIT
binding from its 1d10 force damage claim to the exact attack revision. Each beam independently targets
one creature within range; targets may be repeated or differ. Levels 5, 11 and 17 increase total beam
counts to 2, 3 and 4. No spell-slot count scaling, damage progression, visibility, object targeting,
or runtime combat behavior was added.

The [7.4.4N report](../analysis/mechanics/step-7/7.4.4N-eldritch-blast-multi-attack-pilot.md)
records exact source pointers and spans, claim revisions and hashes, validation rules, inventory
deltas, deterministic previews and compatibility comparisons. Inventory is now 969 identities,
120 processed owners, 252 production claims and 327 potential claims. Classification totals are
unchanged. All unrelated canonical and private inspection documents are byte-identical; default Spell
detail responses are byte-identical for all 969 owners. Focused tests pass (154 tests, 0 failures,
0 errors, 1 skipped); the full Maven suite passes (840 tests, 0 failures, 0 errors, 1 skipped). Review
and licensing remain pending and publication authorization remains zero. Step 7.4.3 remains PARTIAL
and parent Step 7.4.4 remains IN PROGRESS.

##### 7.4.4O. Scorching Ray slot-scaled multi-attack — COMPLETE

Activated only PHB Scorching Ray. Added `SpellSlotInstanceCount` for total ray counts, with three
base rays at spell level 2 and one additional ray per higher slot level. Each ray has an independent
ranged spell attack, one target assignment within range, and a corresponding-instance HIT binding to
one 2d6 fire damage event. The source allows one or several targets, so repeated and distinct target
assignments are supported. It does not specify eligible target categories; the allocation preserves
that state as `UNSPECIFIED_IN_SOURCE`, and an evidence-backed diagnostic keeps the owner partial. No
creature-only or object eligibility is inferred.

The [7.4.4O report](../analysis/mechanics/step-7/7.4.4O-scorching-ray-slot-scaled-multi-attack.md)
records the pinned source evidence, claim revisions and hashes, target-category decision, inventory
deltas, deterministic previews and compatibility comparisons. Inventory is now 969 identities, 121
processed owners, 254 production claims and 328 potential claims. Classification totals remain 168
PARTIALLY_SUPPORTED, 435 UNSUPPORTED, 366 NOT_ASSESSED and 0 SUPPORTED. Only Scorching Ray's
canonical and private-inspection output changed; default Spell detail responses are byte-identical for
all 969 owners. The full Maven suite passes 848 tests with one skipped test. Review remains pending,
licensing remains `NEEDS_REVIEW`, and publication authorization remains zero. Step 7.4.3 remains
PARTIAL and parent Step 7.4.4 remains IN PROGRESS.

##### 7.4.4P. Magic Missile automatic-hit damage instances — COMPLETE

Activated only PHB Magic Missile. Added a typed damage-instance-series claim with three initial
`1d4 + 1` force darts, one creature assignment per dart, caster visibility and range constraints,
automatic-hit resolution, simultaneous impact, and one extra dart per higher slot level. Damage roll
grouping remains `UNSPECIFIED`; no attack-roll claim or combat execution semantics were added. The
owner remains PARTIAL, and the existing Magic Missile benchmark fixture remains byte-identical and
pending human review.

The [7.4.4P report](../analysis/mechanics/step-7/7.4.4P-magic-missile-automatic-hit-instances.md)
records pinned evidence, claim identity and hash, benchmark deferrals, deterministic inventory
previews, and byte-level compatibility. Inventory is now 969 identities, 122 processed owners, 255
production claims and 328 potential claims. Classification totals remain 168 PARTIALLY_SUPPORTED,
435 UNSUPPORTED, 366 NOT_ASSESSED and 0 SUPPORTED. Canonical and private inspection outputs changed
only for Magic Missile; default Spell responses remain byte-identical for all 969 owners. The full
Maven suite passes 851 tests with one skipped test. Review remains pending, licensing remains
`NEEDS_REVIEW`, publication authorization remains zero, Step 7.4.3 remains PARTIAL, and parent Step
7.4.4 remains IN PROGRESS.

##### 7.4.4Q. Multi-instance consolidation and coverage discovery — COMPLETE (ANALYSIS ONLY)

Verified the post-P baseline at 969 Spell owners, 122 processed owners, 255 production claims, and
328 potential typed claims; classifications remain 168 PARTIALLY_SUPPORTED, 435 UNSUPPORTED,
366 NOT_ASSESSED, and 0 SUPPORTED. Reviewed the N–P model implementations, evidence, dependency
validation, pilot semantics, Magic Missile benchmark boundary, and all 168 partial inventory rows.
Scanned authored entries and structured fields across all 18 pinned Spell resources, then
source-reviewed representative candidates across PHB, XGE, TCE, AI, and XPHB. The report records
screening counts, false positives, candidate classifications, model gaps, and a bounded R–X
implementation sequence. No extraction, claims, source files, fixtures, or classifications changed.
Repeated target-only inventory generation was deterministic. Review and licensing remain independent;
publication authorization remains zero. Step 7.4.3 remains PARTIAL and parent Step 7.4.4 remains IN
PROGRESS. See the [7.4.4Q discovery report](../analysis/mechanics/step-7/7.4.4Q-multi-instance-consolidation-discovery.md).

##### 7.4.4R. Steel Wind Strike selected-target-count independent attacks — COMPLETE

Added a source-bounded XGE pattern for `spell:xge:steel-wind-strike` using the pinned 5eTools
v2.37.0 resource. The typed `SelectedTargetCount` represents up to five distinct visible creatures,
one independent melee spell attack per selected creature, with no fixed or minimum attack count.
One revision-1 damage claim binds 6d10 force damage to the HIT of its corresponding attack instance.
The separate post-attack teleport choice remains an evidence-backed unsupported diagnostic because
the current model cannot express its target-relative destination and hit-or-miss eligibility.
Coverage remains PARTIAL; both claims remain PENDING_REVIEW / NEEDS_REVIEW for licensing, and
publication authorization remains zero. Inventory is 123 processed owners / 257 claims / 330
potential claims, with PARTIALLY_SUPPORTED 169, UNSUPPORTED 434, NOT_ASSESSED 366 and SUPPORTED 0.
All 968 other canonical/private inspection documents and all 969 default Spell REST responses are
byte-identical to the pre-R baseline. See the [7.4.4R report](../analysis/mechanics/step-7/7.4.4R-steel-wind-strike-selected-target-attacks.md).

##### 7.4.4S. Melf's Acid Arrow HIT/MISS branches and delayed damage — COMPLETE

Added four revision-1 claims for `spell:phb:melf-s-acid-arrow`: one ranged spell attack, immediate
`4d4` acid damage on HIT, `2d4` acid damage at the end of the target's next turn on HIT, and half of
the initial damage on MISS. Typed spell-slot damage scaling adds `1d4` to both HIT amounts per slot
level above 2; the MISS fraction references the scaled initial damage claim. MISS evidence explicitly
records no damage at the end of the target's next turn. The target remains a single target with its
creature/object category unspecified by source. Overall coverage stays PARTIAL. Review remains
PENDING_REVIEW, licensing NEEDS_REVIEW, and publication authorization zero.

The verified inventory is 124 processed owners / 261 claims / 334 potential claims, with
PARTIALLY_SUPPORTED 170, UNSUPPORTED 433, NOT_ASSESSED 366 and SUPPORTED 0. Only Melf's Acid Arrow
changes canonical and private inspection; all 969 default Spell REST responses remain byte-identical.
The pinned PHB hash, benchmark fixtures, existing 257 claims and historical reports remain unchanged.
The full Surefire suite passes 862 tests with one existing skip; the normal Maven `test` lifecycle
encountered the Windows javac archive-close access denial, so tests were compiled against the Maven
test classpath before running the complete Surefire suite. See the
[7.4.4S implementation report](../analysis/mechanics/step-7/7.4.4S-melfs-acid-arrow-hit-miss-delayed-damage.md).

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

**Step 7.4.3 remains PARTIAL after batch 7.4.3C.** The six benchmark fixtures still require human review
before authoritative benchmark acceptance; there is no approved gold set or independently validated
extractor accuracy. Candidate diagnostics do not constitute mechanics verification or publication
permission. Step 7.4.4 remains IN PROGRESS through completed implementation batch 7.4.4S. Stop at
7.4.4S; no later multi-instance implementation, benchmark adjudication, Step 7.5, database, or
frontend work is authorized by this batch.
