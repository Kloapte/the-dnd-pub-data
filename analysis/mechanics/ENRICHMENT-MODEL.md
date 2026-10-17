# Proposed Mechanics Enrichment Model

## Design boundary

Use small structures tied to actual domains, with a common evidence locator and separate
confidence/publication assessments. Extend existing definitions only where directly authored
mechanics are missing. Keep syntax annotations in a separate immutable internal index. No
universal effect AST, formula evaluator, entity instance, campaign state or rule executor is
justified by the corpus.

This is a design proposal, not new Java/API code. Type names below are suggestions; each future
public top-level Java type would have its own file and immutable collection handling.

## Common internal evidence

`MechanicsEvidenceLocation` should identify:

- Existing owner catalog and catalog-owned stable ID. Do not recompute legacy Monster IDs or
  manufacture IDs for rows/headings/components.
- Source file/root collection and exact JSON locator within the fingerprinted snapshot.
- Exact primary source/page and available copy/version lineage; preserve authored source codes.
- File hash and optional source-node/token hash. A locator index is valid within its snapshot,
  not a claim of cross-release identity stability.
- For strings, UTF-16 start/end offsets; for tables, source table/row/column indexes; for entries,
  parent section/action kind/index and higher-level context where explicitly present.
- Authored choice/group context, feature level or scaling parent if established by a typed owner
  structure. Omit unknown context rather than deriving it from prose.

`MechanicsAssessment` should keep three independent values:

1. Classification A/B/C/D/E from the overview.
2. Parse confidence and semantic confidence, each with the reliability vocabulary; optional
   review decision and explicit uncertainty reason.
3. Publication assessment: `UNREVIEWED`, `APPROVED_SUBSET`, `WITHHELD` or `OUT_OF_SCOPE`, with a
   decision reference and rights/attribution basis when known.

Diagnostics, source locators, hashes, confidence and review records are internal and do not
belong in a public mechanical DTO. An existing source field does not need a new evidence object
per scalar if the containing definition already retains an unambiguous provenance path.

## Domain-specific proposals

| Structure | Purpose | Explicit exclusions |
|---|---|---|
| `ClassProgressionCellDto` with source `columnIndex` | Preserve positions of existing projected number/bonus/dice/speed cells | No parsing withheld string values, no new table text |
| `ItemFocusRequirement` sealed Boolean/ordered-label variants | Preserve source `focus` completely in ItemDefinition | No Class target IDs, eligibility or inferred source |
| `FeatGrantBundle` with typed proficiency/defense/sense/progression members | Move 14 existing field families from import-only retention to domain normalization | No selection, application or choice flattening |
| `FeatProficiencyRequirement` with category keys | Preserve armor/weapon/weaponGroup prerequisite distinctions | No inferred interpretation of current joined strings |
| `ItemBonusValue` with authored token and optional parsed integer | Lossless normalization of observed signed-integer strings | No stacking, character modifier or conditional effect |
| `SpellFormulaAnnotation` | Preserve formula tag, exact argument sequence, entry/higher-level/table context | No default healing/damage subject, save branch or target count |
| `MonsterActionAnnotation` | Preserve attack/DC/recharge/save/response labels inside a specific action category/node | No inferred Multiattack graph, action execution or branch pairing |
| `ObjectAttackAnnotation` | Retain the explicit attackEntries/hitEntries container association on seven roots | No automatic rider/condition/damage effect |
| `TreasureQuantityAnnotation` | Optional lossless expression representation where an existing typed currency field supplies role | No rolling, awarding, filter expansion or generated Items |

Existing narrow value objects can be reused only when source shape and semantics match. For
example, a Vehicle's component movement descriptor is `mode + entries`, so a numeric Monster
movement value is not a compatible substitute. Likewise Feat choices and proficiency requirements
cannot be merged solely because their property names resemble Race/Background fields.

## Annotations preserve syntax

A source annotation has a domain-specific kind and payload, not a generic untyped map. Candidate
payload variants are a formula token sequence, a compact attack code, a save/branch label, a
recharge marker with optional authored tokens, and an explicit reference token. Preserve original
expressions and every argument. Unknown grammar/code variants remain unsupported internal
annotations or fail a narrowly declared parser contract; they must not be silently discarded.

A later, optional `ReviewedEffectAssociation` can point to one or more annotation IDs and record
only explicitly established facts: subject/target, trigger, branch, value, scaling or choice
context. Unknown values remain absent. An empty target is not `self`; no trigger is not `on use`;
a damage expression is not a guaranteed damage application. Association does not execute rules.
Do not introduce such associations as the first annotation batch.

## Concrete source-grounded examples

### Existing Class cell position

PHB Barbarian, `classes/class-barbarian.json`,
`$.class[0].classTableGroups[0].rows[0]` has a withheld string at column zero and a typed BONUS
value 2 at column one. A proposed projection can expose:

```json
{"columnIndex":1,"kind":"BONUS","number":2}
```

The full table shape must preserve the missing column's identity/withheld status through a
reviewed sparse or placeholder contract. This snippet does not grant permission to publish the
string at column zero. Decide whether to add a positioned representation or version the existing
cell contract; do not retain a misleading legacy position silently.

### Item focus representation

`+1 All-Purpose Tool | TCE`, `items.json`, `$.item[0].focus` authors an array containing
`Artificer`. Normalize it as an ordered source-label restriction, with a separate Boolean variant
for source Boolean fields. No authoritative Class source or target ID is present in that array.
Its parsing/representation confidence is exact; eligibility evaluation is outside scope.

### Feat mechanics

`Aberrant Anatomy | RHW`, `feats.json`, `$.feat[0]` authors Perception proficiency, Perception
expertise and blindsight 15 in separate structured fields. Normalize the three values separately
without inferring stacking, character ownership or a Skill target source. Review publication of
each field/source before exposing an added Feat DTO member.

### Spell expression with unknown effect association

PHB Fireball's `damage 8d6` tag can become an internal formula annotation whose value is the
authored `8d6` expression. Its target, save outcome and repeat count remain unspecified in the
annotation. `scaledamage 8d6|3-9|1d6` is a separate annotation with all three arguments and a
higher-level-entry locator. Do not compute damage by spell slot or infer a 20-foot radius from
the formula. Existing root fire/save/range classifications stay in SpellDefinition.

### Monster save label

`Fiendish Icon | ABH`, `monsters/bestiary-abh.json`,
`$.monster[0].action[2].entries[0]` explicitly marks `actSave dex` and a later failure label.
An internal annotation can retain `dex`, source offsets and the owning action. Do not assume the
failure label's whole following paragraph is one effect or that a nearby damage tag is its only
outcome. The proposed model permits separate label annotations without an effect association.

### Treasure expression

`Challenge 0-4 | DMG`, `loot.json`, `$.individual[0].table[0].coins.cp` authors `5d6`.
The coin field establishes denomination and quantity-expression role; it does not award currency,
calculate a roll or authorize publishing the complete row. Preserve the expression internally
and its exact row location. Current public range metadata remains independent.

## Existing models to extend or leave intact

- Extend Class public table projection for source positions; existing internal rows already
  preserve order. Keep feature graph identities/resolution unchanged.
- Extend ItemDefinition for focus and optionally lossless bonus values. Keep import provenance,
  base-item links, ItemVariantMatchData and catalog identity stable.
- Extend FeatDefinition for missing typed bundles/requirements; retain FeatVersionExpander and
  all authored alternatives. A separate public projection decides approved subsets.
- Keep SpellDefinition's existing casting/range/components/duration/classifications/scaling.
  Add an internal annotation index instead of replacing those fields with inferred effects.
- Keep Monster source resolution/IDs and core statistics intact. A dedicated public projection
  requires its own publication/compatibility decision, separate from annotation extraction.
- Keep Facility/Reward/Trap/Hazard/Vehicle/Upgrade/Rule/Treasure definitions as data. Do not add
  activation, ownership, reset, damage, movement or generation engines.
- Keep the shared resolver snapshot, target vocabulary and public reference projection intact.
  An annotation may point to an existing occurrence/role but cannot resolve new targets in Step 7.

## Public projection rule

Only a domain-specific value with adequate semantic evidence **and** an applicable publication
decision is a candidate for a public DTO. Raw annotations, expressions lifted from prose, source
snippets, confidence labels, diagnostics and full source content have no proposed generic API.
Use `NON_EMPTY`, immutable records and meaningful zero/false preservation when a future approved
batch adds a public field. No endpoint changes are proposed for this analysis.
