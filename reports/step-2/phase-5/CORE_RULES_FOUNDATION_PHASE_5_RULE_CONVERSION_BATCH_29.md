# Phase 5 Rule Conversion — Batch 29

## Scope and checkpoint

The authoritative plan assigns `rule:xge:downtime-revisited` from `$.variantrule[94]` to `RULE-CONV-29`, with 7 source-entry roots and 99 nested clause paths. The validated pre-batch checkpoint was 230 conversion-accounted Rules, 1 pending, 226 canonical Rule outputs, 546 Phase 5 outputs, and 830 permanent checks.

| Measure | Before | After |
|---|---:|---:|
| Rule identities / semantically reviewed | 231 / 231 | 231 / 231 |
| Conversion-accounted / pending | 230 / 1 | 231 / 0 |
| Canonical Rule outputs | 226 | 227 |
| Phase 5 canonical outputs | 546 | 547 |
| Permanent checks | 830 | 832 |
| Coverage (FULL / PARTIAL / UNSUPPORTED) | 68 / 81 / 81 | 68 / 82 / 81 |

## Conversion

`rule:xge:downtime-revisited` is **PARTIAL** and has no mechanics array. Its 13 explicit example activity links are preserved as canonical Rule references, all of which resolve to XGE Rule outputs. The generic presentation is not a reusable activity mechanic: rival creation and agendas, cross-activity durations and costs, daily-hour and scheduling rules, inefficiency penalties, and complication resolution exceed the approved Rule schema and remain skipped under R-GAP-13. PHB/DMG book-section references are not exact canonical Rule identities and remain documented as external dependencies.

## Source accounting and validation

The 7 roots and 99 nested paths are accounted for. Root-level classification records contain 1 `INTERPRETED`, 7 `OMITTED`, and 0 `DUPLICATE` dispositions; the activity-list root has both an interpreted reference list and an omitted availability restriction. Nested paths are traceable through the parent disposition. No canonical reference is unresolved and no source `reprintedAs` field is unresolved.

Permanent assertions verify identity, provenance, exact source path, partial reference-only validity, all 13 target IDs and their resolution, all source paths, omission of the unsupported generic framework, unique IDs, and final manifest totals.

Validation passed:

- `npm run validate:core-rules` — 9 schemas, 89 examples, 34 Phase 3 records, 547 Phase 5 records, 832 permanent checks.
- `git diff --check` — passed.

Created the canonical Rule record and this report. Updated `doc/rule/omitted.md`, `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`, and `tools/scripts/step-2/validate-core-rules.cjs`.
