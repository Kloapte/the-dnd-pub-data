# Phase 5 Rule Conversion - Batch 12

## Verified checkpoint before batch

RULE-CONV-11 passed validation with 129 Rules conversion-accounted, 102 pending, 125 canonical Rule outputs, 445 Phase 5 outputs, and 712 permanent checks. Semantic review is complete for all 231 Rules.

## Selected Rule and conversion

The plan selects `rule:gos:random-ships` (`$.variantrule[178]`): 6 source-entry roots and 353 nested paths. The complete source object and its ship-type, name, crew, purpose, attitude, race, and disposition tables were inspected.

| Canonical ID | Coverage | Represented mechanic |
|---|---|---|
| `rule:gos:random-ships` | PARTIAL | Ghost ships resist acid, fire, lightning, and thunder damage. These unconditional damage types use the approved resistance pattern. |

The source's resistance to nonmagical bludgeoning, piercing, and slashing damage cannot be represented without losing its nonmagical-attack qualifier. Immunities, passage through creatures/objects, and force damage on ending inside an object also exceed the approved component patterns. The remaining random ship generation and encounter procedures depend on deferred Vehicle, Creature, Equipment, and encounter state.

## Accounting, gaps, and references

All 6 roots and 353 nested paths are accounted for. There are 1 INTERPRETED, 0 DUPLICATE, and 8 OMITTED dispositions across 6 roots. Three name-generating/introduction portions are `NON_MECHANICAL`; three ship-generation branches are `OUT_OF_APPROVED_SCOPE`; two unsupported conditional-damage and table branches are `SCHEMA_UNSUPPORTED`.

Deferred R-GAP-31 and R-GAP-28 were encountered. The source also describes a mutiny disposition, which remains within the existing skipped morale/outcome boundary R-GAP-44. None of these gaps was implemented. Named GoS Vehicle and Creature targets, disease, and equipment references lack matching canonical targets; no substitute edition targets were used. No duplicate register changes were needed.

## Validation and checkpoint

One permanent source-backed assertion verifies the source identity, provenance, exact root/nested counts, supported damage resistance, unrepresented ghost-ship clauses, gap linkage, and complete nested-path accounting.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 129 | 130 |
| Rule conversions pending | 102 | 101 |
| Canonical Rule outputs | 125 | 126 |
| Phase 5 canonical outputs | 445 | 446 |
| Permanent checks | 712 | 714 |

Coverage is 62 FULL, 31 PARTIAL, and 37 UNSUPPORTED. Semantic review remains 231/231.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 446 Phase 5 records validated, and 714 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/gos-random-ships.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_12.md`
