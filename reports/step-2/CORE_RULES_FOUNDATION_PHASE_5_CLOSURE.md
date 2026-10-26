# Step 2 Phase 5 Core Rules Foundation Closure

## Closure decision

**Phase 5 is factually complete and formally approved.** Factual completion was checked against the immutable source inventories, canonical outputs, manifests, omission/duplicate registers, approved contracts, and validator. Formal approval to close Phase 5 was explicitly provided in the user's remediation-and-closure instruction on 2026-10-25. This approval does not declare all mechanics complete or remove documented limitations.

## Reconciled inventory and outputs

| Measure | Final value |
|---|---:|
| In-scope source identities | 588 |
| Excluded font-only LanguageScript records | 6 |
| Phase 3 representative identities and outputs | 34 |
| Phase 5 expansion identities | 554 |
| Phase 5 canonical outputs | 551 |
| Total canonical outputs across Phase 3 and Phase 5 | 585 |
| Documented unsupported identities without canonical shells | 3 |
| Rule identities / reviewed / conversions pending / outputs | 231 / 231 / 0 / 231 |

The three accepted unsupported identities without canonical outputs are `action:dmg:healing-surge`, `action:xge:waking-someone`, and `disease:tftyp:super-tetanus`. Their complete source entries, source-qualified identities, reasons, and omission dispositions are recorded in the accounting manifest and model omission registers. They remain intentionally non-referenceable canonical identities; no placeholder records or mechanics were added.

Overall in-scope mechanical classifications are 268 FULL, 244 PARTIAL, and 76 UNSUPPORTED. Phase 5's own 554 expansion records account for 243 FULL, 235 PARTIAL, and 76 UNSUPPORTED; three of those unsupported records have no canonical output under the accepted exception above. These classifications do not equate catalog coverage with mechanical completeness.

## Remediation evidence

The Phase 5 manifest contained 165 `sourceFieldIssues` records but declared 162. Each record was checked against its source file, source path, canonical source-qualified identity, source field, and value. All 165 identities and fields resolve. This review found that the Planar Effects `reprintedAs` `sourceValue` omitted the source's one-element array wrapper; the manifest value was corrected to match the immutable source. The manifest now declares 165 issues.

The permanent validator now asserts:

- `sourceFieldIssueCount` equals the `sourceFieldIssues` array length and the current expected total of 165.
- Every issue's canonical ID matches the source record at its locator.
- Every named source field exists and its recorded value exactly matches the pinned source object.

The checks preserve all 165 issues: six Action out-of-scope structured fields, eleven unresolved Rule reprint targets, and 148 Language issues across external dependencies, unresolved fields, exclusions, schema limitation, and ambiguity.

## Source accounting and boundaries

The Phase 3 and Phase 5 manifests account for 980 source-entry roots: 74 representative roots and 906 expansion roots. Their disposition-part totals are 482 INTERPRETED, 39 DUPLICATE, and 768 OMITTED; dispositions are not a one-to-one count of roots because mixed roots may have multiple classifications. Nested paths and complete original entries are verified through the established manifest/register validation conventions.

All previously approved FULL/PARTIAL/UNSUPPORTED classifications, gap-family decisions, exclusions, unresolved references, and external dependencies remain in force. LanguageScript font-only data remains excluded. No schema, immutable source catalog, or deferred/skipped mechanic was changed for this remediation.

## Validation checkpoint

Final verification after remediation:

- `npm run validate:core-rules` passed: 9 Draft 2020-12 schemas compiled, 89 examples validated, 34 Phase 3 canonical records validated, 551 Phase 5 canonical records validated, and 838 permanent checks passed.
- `git diff --check` passed (exit 0). Git reported line-ending normalization notices only.

## Changed files for remediation and closure

- `ROADMAP.md` — corrected factual Phase 5 status and records formal approval separately from factual completion.
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json` — corrected the Planar Effects source value shape and issue total.
- `tools/validate-core-rules.cjs` — added permanent source-field count and pinned-source fidelity checks.
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_COMPLETION_AUDIT.md` — recorded remediation and superseded the prior blocker recommendation.
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_DATASET_EXPANSION.md` — marked intermediate checkpoints historical and linked the final closure result.
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_CLOSURE.md` — this closure record.

Phase 6 implementation is outside this closure report and is governed by its separate integration plan and approval checkpoint.
