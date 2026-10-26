# Phase 5 Rule Conversion — Batch 21

## Verified checkpoint before batch

RULE-CONV-20 passed validation with 177 conversion-accounted Rules, 54 pending conversions, 173 canonical Rule outputs, 493 Phase 5 canonical outputs, and 769 permanent checks. All 231 Rules remain semantically reviewed.

The approved plan selects `rule:dmg:new-ability-scores-honor-and-sanity` at `$.variantrule[159]`: 7 source-entry roots and 46 nested clause paths. The complete source object was inspected.

## Conversion result

| Canonical ID | Coverage | Representation |
|---|---|---|
| `rule:dmg:new-ability-scores-honor-and-sanity` | UNSUPPORTED | Reference-only, with the source's OPTIONAL rule type preserved. The Rule adds Honor and Sanity as abilities and specifies their score generation, creature fallback, checks, saves, and lifecycle. The approved Rule model only accepts the six standard abilities; optional score identity and lifecycle are deferred under R-GAP-26. The Sanity save consequences also depend on skipped R-GAP-24 madness outcomes. |

No supported mechanics or references can represent an independent portion faithfully. No existing mechanics pattern was reused.

## Source accounting and gaps

All 7 roots and 46 nested paths are accounted for: 0 INTERPRETED, 0 DUPLICATE, and 7 OMITTED roots, with nested paths attached to their parent dispositions. The complete source entry evidence was added to the omission register.

Deferred R-GAP-26 and skipped R-GAP-24 were encountered. No canonical references were emitted and no unresolved canonical references were introduced.

## Validation and checkpoint

A permanent source-backed assertion verifies identity, source metadata and OPTIONAL type, source path, root and nested-path totals, unsupported reference-only form, gap IDs, and omission coverage for score creation, creature fallback, checks/saves, and Sanity's madness interaction.

| Metric | Before | After |
|---|---:|---:|
| Rule identities | 231 | 231 |
| Semantically reviewed | 231 | 231 |
| Conversion-accounted | 177 | 178 |
| Pending conversion | 54 | 53 |
| Canonical Rule outputs | 173 | 174 |
| Phase 5 canonical outputs | 493 | 494 |
| Permanent checks | 769 | 771 |

Coverage is 68 FULL, 54 PARTIAL, and 56 UNSUPPORTED. `npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 494 Phase 5 records validated, and 771 permanent checks passed. `git diff --check` passed.

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-new-ability-scores-honor-and-sanity.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_21.md`
