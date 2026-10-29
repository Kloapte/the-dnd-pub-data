# Phase 5 Rule Conversion - Batch 07

## Verified checkpoint before batch

RULE-CONV-06 passed validation at 118 of 231 Rules conversion-accounted, 113 pending, 114 canonical Rule outputs, 434 Phase 5 canonical outputs, and 696 permanent checks. All 231 Rules remain semantically reviewed.

## Selected Rules and conversion

The approved plan assigns these six maritime Rules to RULE-CONV-07: 6 identities, 21 source-entry roots, and 128 nested-clause paths. The full source records, including tables, nested sections, and references, were inspected.

| Canonical ID | Source path | Roots / nested paths | Coverage | Conversion |
|---|---|---:|---|---|
| `rule:aag:crashing` | `$.variantrule[33]` | 4 / 12 | UNSUPPORTED | Reference-only. Crash attack resolution, collision damage, continued movement, gravity-plane effects, and falling are linked vehicle/spatial procedures deferred under R-GAP-31. The cited PHB Falling text has no matching canonical Rule output. |
| `rule:aag:crew` | `$.variantrule[37]` | 2 / 0 | UNSUPPORTED | Reference-only. Crew roles, passenger counts, ship-air degradation, and paid helm service depend on vessel and campaign state deferred under R-GAP-31. |
| `rule:gos:officers-and-crew` | `$.variantrule[164]` | 3 / 27 | UNSUPPORTED | Reference-only. Role staffing, crew quality, loyalty, and shore leave depend on R-GAP-31. Mutiny checks and outcomes also intersect skipped R-GAP-44. |
| `rule:aag:ship-repairs` | `$.variantrule[191]` | 2 / 0 | UNSUPPORTED | Reference-only. Repair targets, berth state, hit points, cost, and time depend on Vehicle and Equipment state deferred under R-GAP-31. The Mending spell has no canonical Spell target. |
| `rule:gos:ship-stat-blocks` | `$.variantrule[192]` | 5 / 60 | UNSUPPORTED | Reference-only. Ship space, capacities, abilities, defenses, components, damage thresholds, and actions require the deferred Vehicle model. Sample ship and siege equipment targets are not available as canonical records. |
| `rule:aag:ship-to-ship-combat` | `$.variantrule[193]` | 5 / 29 | UNSUPPORTED | Reference-only. Starting ranges, ship movement/orientation, targeting, boarding, and separation depend on deferred maritime encounter state. The canonical output preserves resolved references to Side Initiative and the Ready Action. |

No Rule mechanics were added. The approved `VEHICLE_PROCEDURE` structure does not express the required vessel properties, repair amounts and costs, crash damage branches, crew state, or ship combat sequence; emitting a bare procedure label would not preserve source meaning. Existing canonical references were reused for the explicit Initiative Variants and Ready Action citations. Canonical identity and references do not count as represented mechanics, so all six remain UNSUPPORTED.

## Source accounting, gaps, and references

All 21 roots and 128 nested paths have source-backed dispositions. Counts are 0 INTERPRETED, 0 DUPLICATE, and 21 OMITTED. Three introductory roots are `NON_MECHANICAL`, sixteen procedural roots are `OUT_OF_APPROVED_SCOPE`, and two roots are `REFERENCE_UNRESOLVED` (Mending; sample vehicle/equipment targets). Nested paths are explicitly listed within each accounting portion.

Deferred R-GAP-31 is encountered in all six records. Skipped R-GAP-44 applies to morale-triggered mutiny checks and outcomes in Officers and Crew. Neither family was implemented. The canonical Ready Action and Side Initiative references resolve. Other source-linked targets without canonical outputs include Mending, PHB Falling, the Commoner creature, equipment/tool targets, sample vehicles, and siege equipment. No substitute edition targets were used.

The omission register now includes the source path, coverage reason, and full original source entry for every omitted root. No duplicate register changes were needed.

## Validation and checkpoint

One permanent source-backed batch assertion was added. It checks all six identities, provenance, exact roots and nested-path totals, reference resolution and values, deferred/skipped gap accounting, reference-only schema shapes, and final manifest totals. The six new canonical files are also independently validated.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 118 | 124 |
| Rule conversions pending | 113 | 107 |
| Canonical Rule outputs | 114 | 120 |
| Phase 5 canonical outputs | 434 | 440 |
| Permanent checks | 696 | 703 |

Coverage is now 62 FULL, 29 PARTIAL, and 33 UNSUPPORTED. Semantic review remains 231/231; conversion accounting and mechanic coverage are separate measures.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 440 Phase 5 records validated, and 703 permanent checks passed. `git diff --check` passed (Git may report configured LF-to-CRLF notices).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/aag-crashing.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/aag-crew.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/aag-ship-repairs.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/aag-ship-to-ship-combat.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/gos-officers-and-crew.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/gos-ship-stat-blocks.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_07.md`
