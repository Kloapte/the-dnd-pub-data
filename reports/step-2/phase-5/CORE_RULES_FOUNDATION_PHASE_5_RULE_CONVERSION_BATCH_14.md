# Phase 5 Rule Conversion - Batch 14

## Verified checkpoint before batch

RULE-CONV-13 passed validation with 131 Rules conversion-accounted, 100 pending, 127 canonical Rule outputs, 447 Phase 5 outputs, and 716 permanent checks. Semantic review remains complete for all 231 Rules.

## Selected Rules and conversion

The approved plan selects the two edition-specific Sidekicks Rules below. Both complete source records, including nested progression entries and tables, were inspected.

| Canonical ID | Source path | Coverage | Conversion |
|---|---|---|---|
| `rule:esk:sidekicks` | `$.variantrule[196]` | UNSUPPORTED | Reference-only. Archetype stat blocks, level progression, and feature tables depend on creature/class content and progression mechanics deferred under R-GAP-34. The cited creature records do not have canonical outputs. |
| `rule:tce:sidekicks` | `$.variantrule[197]` | UNSUPPORTED | Reference-only. Eligibility, sidekick classes, level advancement, proficiency progression, and features form the deferred sidekick progression subsystem under R-GAP-34. |

No independently supported mechanics can be separated from the progression rules without losing their prerequisites and meaning. No creature or class records were created or substituted across editions.

## Source accounting and gap decisions

The plan assigns 13 source-entry roots and 152 nested-clause paths. All are covered by 13 root dispositions; nested clauses are covered explicitly by those root-level omissions, rather than being counted as independent disposition rows. The dispositions are 0 INTERPRETED, 0 DUPLICATE, and 13 OMITTED: 8 `NON_MECHANICAL`, 4 `OUT_OF_APPROVED_SCOPE`, and 1 `REFERENCE_UNRESOLVED` for the ESK stat-block references. No duplicate register changes were needed.

Deferred R-GAP-34 was encountered. No deferred mechanic was implemented. The missing canonical creature references remain unresolved dependencies; the source contains no target identity ambiguity.

## Validation and checkpoint

The permanent assertion verifies both identities, exact source paths and path counts, provenance, reference-only shapes, unsupported classification, R-GAP-34 disposition, unresolved dependency accounting, and manifest reconciliation.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 131 | 133 |
| Rule conversions pending | 100 | 98 |
| Canonical Rule outputs | 127 | 129 |
| Phase 5 canonical outputs | 447 | 449 |
| Permanent checks | 716 | 719 |

Coverage is 62 FULL, 31 PARTIAL, and 40 UNSUPPORTED. All 231 Rules remain semantically reviewed.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 449 Phase 5 records validated, and 719 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/esk-sidekicks.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/tce-sidekicks.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_14.md`
