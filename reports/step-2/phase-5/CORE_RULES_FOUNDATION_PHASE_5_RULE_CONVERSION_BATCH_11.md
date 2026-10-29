# Phase 5 Rule Conversion - Batch 11

## Verified checkpoint before batch

RULE-CONV-10 passed validation with 128 Rules conversion-accounted, 103 pending, 124 canonical Rule outputs, 444 Phase 5 outputs, and 710 permanent checks. Semantic review remains complete for all 231 Rules.

## Selected Rule and conversion

The approved plan selects `rule:gos:ocean-environs` (`$.variantrule[163]`): 14 source-entry roots and 349 nested-clause paths. The complete source record, including environmental procedures and all tables, was inspected.

| Canonical ID | Coverage | Represented mechanic |
|---|---|---|
| `rule:gos:ocean-environs` | PARTIAL | Dense kelp forest is difficult terrain. The canonical component expresses the extra foot of movement per foot traveled using the approved `MOVEMENT_SPATIAL/DIFFICULT_TERRAIN` pattern. |

The adjacent kelp obscuration rule is not encoded: the approved obscuration model has no distance threshold, and omitting the 10-foot bound would broaden the rule. Current travel, reefs, depth, mist, hazards, storms, sandbars, sapping snow, shipwrecks, and whirlpools include triggers or state that the frozen contract cannot express independently.

## Accounting, gaps, and references

All 14 roots and 349 nested paths are accounted for. The root dispositions contain 1 INTERPRETED portion, 0 DUPLICATE portions, and 16 OMITTED portions: 3 `NON_MECHANICAL`, 7 `SCHEMA_UNSUPPORTED`, and 6 `OUT_OF_APPROVED_SCOPE`. Two roots contain split dispositions so the represented kelp terrain cost remains distinct from its omitted description/obscuration. Every nested path is named in its parent disposition.

Deferred R-GAP-31 is recorded; no gap implementation was added. The source references external Creature, Spell, Item, Hazard, and DMG environmental content for which this Rule batch has no matching canonical targets. The canonical Rule does not invent or substitute references. No duplicate register changes were required.

## Validation and checkpoint

One permanent source-backed assertion verifies the Rule identity and provenance, exact source path and counts, partial coverage, the difficult-terrain mechanic, the unsupported obscuration threshold, gap linkage, and complete nested-path accounting.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 128 | 129 |
| Rule conversions pending | 103 | 102 |
| Canonical Rule outputs | 124 | 125 |
| Phase 5 canonical outputs | 444 | 445 |
| Permanent checks | 710 | 712 |

Coverage is 62 FULL, 30 PARTIAL, and 38 UNSUPPORTED. All 231 Rules remain semantically reviewed.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 445 Phase 5 records validated, and 712 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/gos-ocean-environs.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_11.md`
