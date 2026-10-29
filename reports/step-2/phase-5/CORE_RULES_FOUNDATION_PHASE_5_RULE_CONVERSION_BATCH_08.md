# Phase 5 Rule Conversion - Batch 08

## Verified checkpoint before batch

RULE-CONV-07 passed validation with 124 Rules conversion-accounted, 107 pending, 120 canonical Rule outputs, 440 Phase 5 outputs, and 703 permanent checks. Semantic review is complete for all 231 Rules.

## Selected Rules and conversion

The plan assigns two GoS maritime Rules to this batch: 12 source-entry roots and 83 nested paths. Both full source records were inspected.

| Canonical ID | Source path | Roots / nested paths | Coverage | Conversion |
|---|---|---:|---|---|
| `rule:gos:ships-in-combat` | `$.variantrule[194]` | 5 / 41 | UNSUPPORTED | Reference-only. Initiative and crew modifiers, officer combat actions, crew positioning/casualties, and ship crashing require vessel, crew, and encounter state deferred under R-GAP-31. |
| `rule:gos:superior-ship-upgrades` | `$.variantrule[214]` | 7 / 42 | UNSUPPORTED | Reference-only. Upgrade cost, work duration, port requirements, component restrictions, and upgrade effects depend on deferred ship state. Vehicle-upgrade targets are unavailable as canonical records. |

The Rule contract has a generic vehicle procedure type, but it cannot express these actions' speed and attack modifiers, crash branches, component state, or upgrade costs and restrictions. Emitting only a procedure label would misstate the mechanics. No new mechanics or references were invented.

## Accounting and gap decisions

All 12 roots and 83 nested paths are accounted for: 0 INTERPRETED, 0 DUPLICATE, and 12 OMITTED (2 `NON_MECHANICAL`, 5 `OUT_OF_APPROVED_SCOPE`, 5 `REFERENCE_UNRESOLVED`). The named ship upgrades have no canonical vehicle-upgrade targets. R-GAP-31 is deferred and was not implemented. No duplicate register changes were needed.

## Validation and checkpoint

One permanent source-backed assertion was added for identity, provenance, root and nested-path counts, reference-only shape, gap linkage, and complete omissions. Both new canonical records also pass schema validation.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 124 | 126 |
| Rule conversions pending | 107 | 105 |
| Canonical Rule outputs | 120 | 122 |
| Phase 5 canonical outputs | 440 | 442 |
| Permanent checks | 703 | 706 |

Coverage is 62 FULL, 29 PARTIAL, and 35 UNSUPPORTED. This does not change the 231/231 semantic-review status.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 442 Phase 5 records validated, and 706 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/gos-ships-in-combat.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/gos-superior-ship-upgrades.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_08.md`
