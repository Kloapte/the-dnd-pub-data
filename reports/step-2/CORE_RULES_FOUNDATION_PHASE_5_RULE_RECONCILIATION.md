# Step 2 Phase 5 Rule Catalog Reconciliation

## Scope and verified starting point

This reconciliation followed RULE-CONV-29. The validated pre-reconciliation checkpoint had 231 Rule identities; 231 semantically reviewed; 231 conversion-accounted; 0 pending conversions; 227 canonical Rule outputs; 547 Phase 5 canonical outputs; 832 permanent checks; and Rule coverage of 68 FULL, 82 PARTIAL, and 81 UNSUPPORTED. The immutable Rule source catalog contains 231 records. The stale manifest field `pendingSemanticReviewCount` was not used as evidence of incomplete semantic review.

The source-accounting inventory retains all 231 Rules and accounts for 648 entry roots and 6,318 nested paths. The Phase 5 Rule conversion batch summary separately covers 226 records and 630 entry roots; the remaining five canonical Rule records belong to the earlier representative-conversion checkpoint. Disposition totals across all Phase 5 domains are 422 INTERPRETED, 28 DUPLICATE, and 734 OMITTED; parent-level dispositions can cover multiple nested source paths and split dispositions can exceed root counts.

## Four canonical outputs recovered

Each record uses the approved Rule v1 schema, preserves exact source identity and provenance, and adds no fabricated mechanics.

| Canonical ID | Source path | Coverage | Canonical representation |
|---|---|---|---|
| `rule:xphb:armor-class` | `$.variantrule[12]` | UNSUPPORTED | Reference-only identity. AC calculations and tables remain outside the supported Rule mechanics contract. |
| `rule:dmg:automatic-success` | `$.variantrule[17]` | UNSUPPORTED | Reference-only identity. Automatic-success resolution has no faithful existing Rule v1 component. |
| `rule:xphb:save` | `$.variantrule[186]` | FULL | Alias reference to `rule:xphb:saving-throw`; retains the existing duplicate/alias coverage decision. |
| `rule:xphb:weapon-attack` | `$.variantrule[230]` | FULL | Alias reference to `rule:xphb:attack-roll`; retains the existing duplicate/alias coverage decision. |

Permanent validation asserts the four identities, source provenance, alias targets, reference-only validity, and absence of placeholder mechanics. Canonical output availability is now 231/231, with no duplicate canonical IDs.

## Semantic and conversion accounting

The manifest now records semantic-review status independently from conversion status:

| Measure | Before | After |
|---|---:|---:|
| Rule identities | 231 | 231 |
| Semantic review complete | 231 | 231 |
| Semantic review pending | misleading legacy value 164 | 0 |
| Conversion-accounted | 231 | 231 |
| Conversion pending | 0 | 0 |
| Canonical Rule outputs | 227 | 231 |
| Phase 5 canonical outputs | 547 | 551 |
| Permanent checks | 832 | 837 |

The misleading `pendingSemanticReviewCount`, `reviewedAndAccountedCount`, and ambiguous `pendingCount` manifest summaries were removed. Explicit `semanticReviewCompleteCount`, `semanticReviewPendingCount`, `conversionAccountedCount`, and `conversionPendingCount` fields now carry the respective totals. All Rule inventory records carry `semanticReviewStatus: "REVIEWED"`. Semantic review completeness does not imply mechanical coverage completeness.

## Coverage audit and changed representations

| Classification | Before | After | Change |
|---|---:|---:|---:|
| FULL | 68 | 68 | 0 |
| PARTIAL | 82 | 90 | +8 |
| UNSUPPORTED | 81 | 73 | -8 |

The eight reclassified reference-only Rules now preserve source-backed relationships that the approved schema already supports through typed references. No new mechanic arrays or executable procedures were added:

- `rule:xphb:curses`: references Remove Curse and Greater Restoration.
- `rule:xphb:possession`: references Protection from Evil and Good, and Dispel Evil and Good.
- `rule:mm:familiars`: references Find Familiar and named example Creatures.
- `rule:vgm:familiars`: references Find Familiar and named example Creatures.
- `rule:mm:player-characters-as-lycanthropes`: references source-specific Creature forms and Remove Curse.
- `rule:mm:player-characters-as-vampires`: references Vampire Spawn, Vampire, and Wish.
- `rule:dmg:more-difficult-identification`: references Identify.
- `rule:dmg:inspiration`: references Advantage as the benefit Inspiration grants when spent; the Inspiration resource lifecycle remains outside the approved scope under deferred R-GAP-25.

The new relationships are useful cross-domain pointers, not claims that spell or creature effects are fully normalized in Rule records. Their targets resolve against the pinned Spell or Creature source catalogs. Canonical Spell/Creature outputs are external-domain dependencies where not yet available.

Three existing Rules received additional references to already recovered canonical Rules: `rule:xphb:breaking-objects` now references Armor Class; `rule:xphb:occupied-space` references Object; and `rule:xphb:unoccupied-space` references Object. The last two remain PARTIAL because Rule v1 cannot encode the source's Boolean composition of alternative/combined space conditions. The accounting now describes that as `SCHEMA_UNSUPPORTED`, not an unresolved reference. Their classifications did not change.

## Gap-label reconciliation

The approved conversion plan's record-level `gapFamilyDispositions` marks assigned uses of R-GAP-13, R-GAP-16, R-GAP-21, and R-GAP-35 as `SKIPPED_OMIT_FOR_NOW`. Several earlier consolidated batch summaries grouped those families under a broad family-level “deferred” heading copied from the prioritization summary. That wording was misleading for these records. The consolidated batch report now distinguishes the record-specific skipped decisions from families deferred for the assigned Rules. Omitted mechanics remain omitted; no skipped family was revived or implemented.

The reconciliation preserved the existing approved decisions for implemented, deferred, and skipped families. It did not modify the plan, prioritization artifact, schema, or source catalog. The omission register and validator assertions retain the explicit skipped status for the relevant source accounting. Other genuinely deferred families, including R-GAP-37, remain deferred.

## Remaining references and contract limits

There are 11 unresolved Rule `reprintedAs` source-field references. They remain source-field accounting issues and have no invented canonical target IDs:

- XDMG: Alien Technology, Diagonal Movement, Explosives, Firearms, Loyalty, Milestones, Mixing Potions, Renown, and Training to Gain Levels.
- XDMG hazard: Planar Effects.
- XPHB hazard: Falling.

No newly added canonical reference is unresolved against its pinned source catalog. Some referenced Spell/Creature targets lack canonical records in those external domains; the Rule records retain source-backed typed references where supported.

Remaining limitations are represented in the existing omission accounting: unsupported AC and automatic-success mechanics; the Boolean space-composition contract gap; omitted content belonging to approved skipped/deferred gap families; and external-domain mechanics that cannot be copied into Rule records. No schema change or new gap implementation is proposed here.

## Validation

Final validation passed:

- `npm run validate:core-rules`: 9 Draft 2020-12 schemas compiled; 89 examples validated; 34 Phase 3 records validated; 551 Phase 5 records validated; 837 permanent checks passed.
- `git diff --check`: passed.

## Reconciliation files

Created four canonical Rule records:

- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-armor-class.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-automatic-success.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-save.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-weapon-attack.json`

Updated canonical Rules: `dmg-inspiration`, `dmg-more-difficult-identification`, `mm-familiars`, `mm-player-characters-as-lycanthropes`, `mm-player-characters-as-vampires`, `vgm-familiars`, `xphb-breaking-objects`, `xphb-curses`, `xphb-occupied-space`, `xphb-possession`, and `xphb-unoccupied-space`.

Updated `doc/rule/omitted.md`, `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`, `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCHES_04_29.md`, and `tools/validate-core-rules.cjs`. This report records the reconciliation. Immutable source data and approved schemas were not changed.
