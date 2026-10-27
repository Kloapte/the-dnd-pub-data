# Step 2 Core Rules Rule Model Complexity Assessment

**Scope:** Read-only architectural assessment of `schemas/core-rules/v1/rule.schema.json`, its shared definitions, Rule DTOs, and all 231 canonical Rule records in `src/main/resources/normalized/core-rules/rules.json`. No schema, dataset, Java, test, roadmap, or governance changes were made. This report is an architectural review artifact, not approval to implement a redesign or proceed to Step 3.

## Executive assessment

The Rule model captures real and varied mechanics. Distinct concepts such as D20 tests, movement, damage, recovery, rests, and downtime should remain typed and distinguishable. The main complexity concern is how finely the current design decomposes those concepts: it has many single-use mechanics and nested DTOs, plus a hand-maintained discriminator deserializer, while the API does not yet expose mechanic-aware filtering or calculations.

The catalog supports a **selective simplification** review. Preserve semantic distinctions and typed references, but review whether one-off variants can be represented as explicit fields or small enums/value records within a cohesive family. Keep distinct record shapes where they carry different rules or constraints. Avoid a general expression tree or generic rules engine.

## Current complexity metrics

| Area | Count / observation |
|---|---:|
| Canonical Rule records | 231 |
| Rules with non-empty `mechanics` | 98 (42.4%) |
| Rules with no `mechanics` | 133 (57.6%); these may still carry references or composition |
| Top-level mechanic objects | 145 across the 98 mechanics-bearing Rules |
| Distinct top-level mechanic `kind` families used | 14 |
| Distinct `kind`/`subtype` pairs used | 51 |
| Rule DTO Java files | 156 total: 9 root DTO files, 5 root enum files, 130 mechanics-package files, 10 mechanics enum files, 2 deserializers |
| Rule mechanic implementations | 34 concrete DTO types implementing `RuleMechanic` |
| Interfaces | 11 total (including `RuleMechanic`, `RuleCompositionMember`, and nested mechanics family interfaces) |
| Enums | 15 Rule-package enums (5 root, 10 mechanics) |
| Custom deserializers | 2 (`RuleMechanicDeserializer`, `RuleTurnTimingDeserializer`) |
| Rule schema `$defs` | 62 |
| Rule schema union/composition keywords | 13 `oneOf`, 2 `anyOf`, 9 `allOf` occurrences |
| Root `mechanics` union | 28 alternatives |

The 156 Java declarations comprise 128 records, 15 enums, 11 interfaces, and 2 deserializer classes. The DTO file total includes the two deserializers; it does not imply 156 independent domain concepts. Mechanics-package files include the 34 top-level mechanic variants, helper value records, interfaces, and enums. The schema's 62 definitions similarly include shared nested structures, not 62 top-level mechanic types.

The lexical `oneOf`/`anyOf`/`allOf` counts describe schema composition points, not the number of branches: nested unions can have many alternatives. A substantial proportion of the Rule schema is devoted to nested mechanics, predicates, outcomes, qualifications, formulas, and discriminated variants.

## Mechanics concepts and structural variation

### Genuinely distinct concepts

The 14 used top-level families represent recognizably different rule subjects: D20 tests, ability scores, proficiency, positioning/flanking, skill procedures, movement and spatial rules, combat procedures, damage and health, magic, downtime, social procedures, rests, initiative, and carrying capacity. These are useful typed categories for clients and avoid treating all mechanics as opaque prose.

Several families are internally cohesive and have multiple meaningful subtypes. D20 tests share the D20-test context while differentiating checks, attacks, saves, death saves, DC definitions, and modifiers. Movement holds distinct procedures such as jumping, falling, swimming, flying, cover, and area geometry. Damage/health separates damage, resistance, vulnerability, hit points, healing, and death. These differences affect rule meaning and should not be flattened merely to reduce class count.

Nested structures also encode real reusable semantics: applicability, saving throws, damage rolls, restoration outcomes, condition application, timing, qualifying spaces, and references to other canonical records. They preserve explicit mechanics and are preferable to raw prose or arbitrary JSON.

### Fine-grained variants and potential structural overhead

The current system often assigns a dedicated record/DTO to a narrow structure even when it occurs once. Examples include `RuleJumpLandingCheck`, `RuleJumpObstacleCheck`, `RuleLandingCondition`, `RuleJumpReachFormula`, and `RuleJumpDistanceFormula`; `RuleFlyingFallRule`, `RuleFlightFalling`, `RuleHoverPrevention`, and `RuleFlySpeedCapability`; and separate `RuleInclusiveChallengeRatingBand`, `RuleExclusiveChallengeRatingBand`, and `RuleProficiencyChallengeRatingBand` forms. These may be justified by distinct constraints, but each is a candidate for reviewing whether a common typed value shape plus a small discriminator or enum expresses the same semantics more economically.

The qualifying-space family is another concentration of small types: the common `RuleQualifyingSpace` wrapper plus forms for creature presence, furniture size, narrow openings, slopes, liquid depth, surface material, and DM-adjudicated similarity. Some are genuinely distinct predicates; others could be explicit alternatives in a smaller typed predicate model. Their distinctness should be evaluated from behavior and validation constraints, not naming similarity alone.

Other candidate clusters include the failed-save outcome types (`RuleFailedSaveApplyCondition`, `RuleFailedSaveOutcomeApplyCondition`, `RuleFailedSaveForcedMovement`, and `RuleFailedSaveChooseOne`), recovery/restoration forms, and separate damage base/value/modifier records. The design has some parallel wrappers or types whose meaning is primarily to label an alternative. The evidence justifies targeted review, not automatic merging.

The hierarchy uses both `kind` and `subtype`, and subtype identity is sometimes repeated in Java constructor validation and a central `switch`. The top-level `RuleMechanicDeserializer` is effectively a manual registry, with one special field-based choice for area geometry (`shapes` selects one DTO). Adding or changing a subtype therefore requires coordinated schema, DTO, enum/deserializer, and tests updates. `RuleTurnTimingDeserializer` repeats this pattern for a nested union. This is real structural and maintenance overhead imposed by the chosen polymorphic JSON contract.

`RuleCompositionMember` similarly models component membership and nested AND/OR composition as a recursive polymorphic union. This has a clear logical meaning, but it is another interface/type-dispatch surface and should be checked against actual catalog use before assuming recursive groups are needed broadly.

## Actual catalog usage

### Family frequency

Counts below are **Rules containing at least one mechanic of the family**, out of 231. A Rule may contain several mechanics and a family may therefore have multiple mechanics in one Rule.

| Family | Rules | Share of catalog |
|---|---:|---:|
| Movement / spatial | 29 | 12.6% |
| D20 test | 19 | 8.2% |
| Downtime procedure | 16 | 6.9% |
| Damage / health state | 11 | 4.8% |
| Proficiency | 7 | 3.0% |
| Social procedure | 6 | 2.6% |
| Rest procedure | 5 | 2.2% |
| Initiative procedure | 2 | 0.9% |
| Spell / magic | 2 | 0.9% |
| Carrying capacity | 2 | 0.9% |
| Positional modifier | 1 | 0.4% |
| Combat procedure | 1 | 0.4% |
| Skill procedure | 1 | 0.4% |
| Ability score rule | 1 | 0.4% |

The 14 family counts total more than the 98 mechanics-bearing Rules because some Rules contain mechanics from multiple families. The 145 top-level mechanic objects distribute as follows: movement/spatial 39, D20 tests 25, damage/health 17, downtime 16, rests 11, social 8, proficiency 8, initiative 3, and 2 each for combat, magic, and carrying capacity, plus one each for positional, skill, and ability-score rules.

### Subtype frequency

There are 51 used `kind`/`subtype` pairs. At the high end, downtime activity appears 16 times across 16 Rules; area-of-effect geometry appears 13 times across 8 Rules; saving throws appear 10 times across 5 Rules; long rest 7 times across 4 Rules; damage 7 times across 3 Rules; social attitude 6 times across 4 Rules. Attack rolls, test modifiers, and ability checks each appear 4 times. All remaining subtypes appear no more than 4 times, with most appearing once or twice.

In particular, 32 of the 51 used top-level subtype pairs have exactly one mechanic instance. Rare does not mean redundant: some encode unique rules that need a dedicated representation. It does mean that the runtime and maintenance value of a bespoke DTO should be weighed against the low frequency and distinct validation needs.

### Nested structures

Observed nested `kind` tags include saving throws (11 instances), fixed values (8), D20 tests (7), failed-save outcomes (7), damage rolls (6), restoration of all (5), dice (5), D20 roll modifiers (5), attack rolls (5), effect-defined outcomes (3), and condition application (3). Many other nested tags occur once or twice. This pattern suggests reusing common small semantic values where the same operation recurs, while keeping rare mechanics explicit when their unique fields materially affect interpretation.

The published aggregate does not demonstrate broad use of every schema alternative. Some options exist as single-record or single-occurrence structures. This is evidence for a reviewable smaller core, but not by itself proof that those options can be removed: source-edition variants and unique rules still need faithful representation.

## Schema/DTO overhead and practical usefulness

### Essential complexity

- Typed, stable identity, name, source metadata, and references connect Rules to the rest of the catalog and support API display and navigation.
- Multiple mechanics can be associated with a Rule, which preserves component boundaries and supports interpretation of compound rules.
- Separate mechanical families and meaningful subtype constraints prevent invalid combinations and preserve useful distinctions for filtering and future calculations.
- Nested records model actual concepts such as timing, applicability, target/condition references, restoration, damage, and geometric qualifications.
- `RuleComposition` can describe AND/OR relationships between mechanic components without embedding executable expressions.

### Structural overhead

- 130 Java files in the mechanics package support only 145 top-level mechanic objects in this dataset; many files are nested values or one-off variants rather than independent frequently used concepts.
- 34 direct `RuleMechanic` implementations are selected through a manual deserializer. This duplicates the discriminator map already constrained by the schema.
- The 28-way root `mechanics` union is broad, and nested unions further multiply the DTO and schema branches. The contract's strict `additionalProperties: false` and per-variant requirements provide value, but impose synchronized change costs.
- A number of helper DTOs create several layers from one mechanic into narrow wrapper/value types. The clearest candidates for scrutiny are jumping formula/check details, flight/fall substructures, challenge-rating bands, qualifying-space predicates, failed-save outcomes, and recovery operations.
- Discriminator choices are represented in several ways: `RuleMechanicKind`, subtype strings, literal schema constraints, Java constructor checks, and dispatch switches. This can drift and makes contract changes expensive.
- `RuleAreaGeometryShapeSet` and `RuleAreaGeometryDimensions` split one conceptual area-of-effect mechanic into separate DTOs selected by property presence, adding shape-sensitive deserialization logic.

### Demonstrated and potential use

The current Rule service/controller demonstrates retrieval of all Rules, exact ID lookup, and exact name lookup. It loads and deserializes typed mechanics. No current Rule endpoint filters by family/subtype or executes calculations. Therefore:

- **Demonstrated:** Rule display/retrieval, identity and source citation, name/ID lookup, and typed mechanics in returned JSON.
- **Plausible near-term client value:** frontend filtering by mechanic family/subtype, cross-linking through typed references, grouped display of components, and showing source edition/type.
- **Potential future value:** deterministic evaluation of explicit damage, movement, rest/recovery, proficiency, or D20 mechanics. Such calculations require implemented behavior and tests; a typed DTO does not itself provide execution semantics.
- **Not demonstrated:** consumer reliance on each specialized Java subtype, recursive composition, or the detailed nested formula alternatives. These may be valuable but are not currently evidenced by API operations.

## Reducing complexity while preserving types

The same semantic information can likely be represented with fewer reusable typed concepts, provided a simplification preserves the observed distinctions and strict validation. Suitable directions for review:

1. Keep the 14 meaningful top-level families, but merge only mechanically equivalent leaf variants into a shared family DTO with explicit typed fields, enums, or a small value record. For example, compare jump distance/reach formulas and landing/obstacle checks for a shared typed procedure structure; compare the several CR-band types for one typed range value with explicit inclusive/exclusive bounds; compare qualifying-space subtypes as a closed set of explicitly typed predicates.
2. Replace repeated labeled wrappers with composition where the wrapper adds no independent identity, lifecycle, or validation. Keep a distinct record when it prevents invalid field combinations or represents a distinct operation.
3. Reduce discriminator duplication. A schema-generated or Jackson-supported discriminator mapping could replace manual switch maintenance if compatible with project Jackson/build conventions; alternatively, retain explicit dispatch but establish a single authoritative mapping and contract tests. This assessment does not prescribe implementation.
4. Review whether all 28 root mechanic alternatives belong in the Rule's general-purpose `mechanics` array or whether some logically related details can be nested under a family mechanic. Only do so if component identity, API filtering, and source interpretation remain clear.

These options do not require generic maps, source prose, expression trees, or a generic rules engine.

## Alternatives and estimated migration impact

| Alternative | Description | Canonical JSON impact | Java DTO impact | Tests and API impact |
|---|---|---|---|---|
| **Retain as-is** | Keep all types and unions; improve only documentation and continue adding corresponding dispatch/schema/test entries. | None. Existing 231 records remain stable. | None immediately; ongoing subtype additions continue to touch several files. | No breaking API change. Existing deserialization contract remains. Ongoing maintenance cost remains high. |
| **Selectively simplify (recommended)** | Preserve the top-level semantic families and high-value common structures; consolidate demonstrably equivalent one-off wrappers/variants. Keep precise typed fields and constraints. | Moderate, targeted changes for affected mechanics only. Likely require explicit schema versioning or an approved migration policy; existing JSON may change discriminator/shape where types are consolidated. | Moderate; removes some one-use records and narrows dispatch logic, while retaining domain-specific types. | Focused fixture and full-catalog deserialization updates; API JSON shape changes for affected variants, so compatibility/versioning review is required. Service routes can remain unchanged. |
| **Substantially redesign** | Reorganize all mechanics around a smaller set of broad family DTOs and nested submodels; reconsider discriminator arrangement, component IDs, and composition. | High. Most or all 145 mechanic objects could need a transformation and revalidation, even if meanings stay stable. | High; broad rewrite of DTO hierarchy, discriminator logic, and validation boundaries. | High regression and compatibility risk; contract and API migration would need a dedicated milestone and consumer review. No current usage evidence requires this breadth. |

Migration estimates are qualitative and limited to inspected artifacts. The selective option should begin with a proposal identifying exact fields/types, affected record counts, unchanged semantics, and API versioning effects. No estimate assumes approval to modify the canonical contract.

## Recommendation

Choose **selective simplification for architectural review**, with no immediate schema or data change. The observed catalog has 145 top-level mechanic objects, 51 top-level kind/subtype pairs, and a long tail: most subtypes occur once or twice, while usage clusters around movement/spatial, D20 tests, damage/health, downtime, and rest. This supports retaining explicit typed concepts for the common families and examining narrow one-off DTOs for consolidation.

Do not make low frequency the sole deletion criterion. Preserve each source-backed distinction when it changes mechanical behavior, applicability, allowed fields, or referential meaning. In particular, keep the model domain-specific and typed, and avoid replacing the current detail with prose or an unbounded expression system.

The strongest first review candidates are: (a) the jump formula/check cluster; (b) qualifying-space predicate variants; (c) challenge-rating band representations; (d) flight/fall/speed substructures; (e) failed-save and recovery outcome wrappers; and (f) the duplicated discriminator dispatch between schema and custom deserializers. Confirm each candidate against the actual records and API consumer needs before proposing contract changes.

## Limitations

This assessment counted catalog usage and inspected schema/DTO/API structure. It did not benchmark consumer behavior, inspect external clients, evaluate mechanical correctness record by record, or run tests. Current service methods demonstrate only whole-record retrieval and lookup; future filtering and deterministic execution value remain prospective. The dataset is a canonical aggregate snapshot; per-file dataset storage contains 226 Rule files, so the 231-record aggregate is the authoritative count used here.
