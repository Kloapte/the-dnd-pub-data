# Phase 5 Rule Conversion — Batch 20

## Verified checkpoint before batch

The pre-change `npm run validate:core-rules` passed with 491 Phase 5 canonical records and 766 permanent checks. The Rule catalog has 231 identities and all 231 are semantically reviewed; 175 are conversion-accounted and 56 remain pending. There were 171 canonical Rule outputs. The legacy `pendingSemanticReviewCount` remains unchanged and was not used as a review-status measure.

The approved plan selects `rule:dmg:inspiration` at `$.variantrule[139]` and `rule:dmg:madness` at `$.variantrule[149]`, totaling 10 source-entry roots and 143 nested clause paths. Complete source objects were inspected.

## Conversion results

| Canonical ID | Coverage | Representation |
|---|---|---|
| `rule:dmg:inspiration` | UNSUPPORTED | Reference-only. Its advantage benefit and one-token limit depend on a held Inspiration resource, award/transfer/spend timing, and the DM-held variant. These are within deferred R-GAP-25. The campaign option to disable Inspiration also has no representation in the approved Rule schema. |
| `rule:dmg:madness` | UNSUPPORTED | Reference-only. The source bundles madness-inducing triggers and Wisdom/Charisma or optional Sanity saves with severity tables, heterogeneous effects, durations, and cures. A standalone save would misstate the procedure; R-GAP-24 is skipped. The introductory horror-theme framing is nonmechanical. |

No mechanics or canonical references were emitted. This avoids encoding partial mechanics whose meaning relies on the deferred or skipped state/resource procedures. No reusable mechanics pattern was needed.

## Source accounting and gaps

All 10 roots and 143 nested paths are accounted for. The source accounting records 10 OMITTED root dispositions, 0 INTERPRETED, and 0 DUPLICATE; nested paths are explicitly attached to their parent source-entry dispositions. The omission register contains the complete original source entries.

R-GAP-25 (Inspiration award, possession, and spend variants) was encountered and remains deferred. R-GAP-24 (table-selected madness states and severity-dependent recovery) was encountered and remains skipped. Madness cites spells and conditions in its omitted outcome and recovery procedure; no unresolved canonical references were introduced because those mechanics were not represented.

## Permanent validation and checkpoint

A permanent source-backed assertion verifies both canonical identities, exact source provenance and metadata, root and nested-path totals, reference-only schema-valid form, unsupported coverage, matching gap IDs, omission reasons, and the absence of unsupported partial mechanics. Existing aggregate assertions were reconciled to the actual manifest totals.

| Metric | Before | After |
|---|---:|---:|
| Rule identities | 231 | 231 |
| Semantically reviewed | 231 | 231 |
| Conversion-accounted | 175 | 177 |
| Pending conversion | 56 | 54 |
| Canonical Rule outputs | 171 | 173 |
| Phase 5 canonical outputs | 491 | 493 |
| Permanent checks | 766 | 769 |

Coverage after this batch is 68 FULL, 54 PARTIAL, and 55 UNSUPPORTED. `npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 493 Phase 5 records validated, and 769 permanent checks passed. `git diff --check` passed.

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-inspiration.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-madness.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_20.md`
