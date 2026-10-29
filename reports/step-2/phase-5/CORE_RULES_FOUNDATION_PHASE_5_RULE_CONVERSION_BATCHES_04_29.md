# Phase 5 Rule Conversion — Batches 04–29 Consolidated Report

## Final checkpoint

Each batch from RULE-CONV-04 through RULE-CONV-29 was selected from the approved plan, accounted, given a batch report and permanent source-backed assertions, and validated before proceeding. The starting checkpoint is the validated end of RULE-CONV-03; the ending checkpoint is the validated end of RULE-CONV-29.

| Measure | Before RULE-CONV-04 | After RULE-CONV-29 |
|---|---:|---:|
| Rule identities | 231 | 231 |
| Semantically reviewed | 231 | 231 |
| Semantic review pending | 0 | 0 |
| Conversion-accounted | 104 | 231 |
| Pending conversion | 127 | 0 |
| Canonical Rule outputs | 100 | 227 |
| Phase 5 canonical outputs | 420 | 547 |
| Permanent checks | 679 | 832 |
| Rule coverage (FULL / PARTIAL / UNSUPPORTED) | 60 / 28 / 16 | 68 / 82 / 81 |

This is the RULE-CONV-29 checkpoint report. Its legacy `pendingSemanticReviewCount` statement is superseded by the catalog reconciliation: semantic review is complete for all 231 Rules, conversion accounting is complete, and the misleading legacy field has been removed. The four missing canonical outputs were recovered during reconciliation, bringing the current canonical Rule output count to 231.

## Batch reconciliation

The table reports plan-selected identities and source workloads from RULE-CONV-04–29. Batch reports contain per-Rule IDs, classifications, source paths, and interpretations.

| Batch | Rules | FULL | PARTIAL | UNSUPPORTED | Roots | Nested paths |
|---|---:|---:|---:|---:|---:|---:|
| RULE-CONV-04 | 4 | 2 | 1 | 1 | 7 | 0 |
| RULE-CONV-05 | 6 | 0 | 0 | 6 | 14 | 323 |
| RULE-CONV-06 | 4 | 0 | 0 | 4 | 9 | 197 |
| RULE-CONV-07 | 6 | 0 | 0 | 6 | 21 | 128 |
| RULE-CONV-08 | 2 | 0 | 0 | 2 | 12 | 83 |
| RULE-CONV-09 | 1 | 0 | 0 | 1 | 2 | 162 |
| RULE-CONV-10 | 1 | 0 | 0 | 1 | 4 | 417 |
| RULE-CONV-11 | 1 | 0 | 1 | 0 | 14 | 349 |
| RULE-CONV-12 | 1 | 0 | 1 | 0 | 6 | 353 |
| RULE-CONV-13 | 1 | 0 | 0 | 1 | 4 | 307 |
| RULE-CONV-14 | 2 | 0 | 0 | 2 | 13 | 152 |
| RULE-CONV-15 | 5 | 2 | 2 | 1 | 5 | 0 |
| RULE-CONV-16 | 12 | 1 | 7 | 4 | 34 | 254 |
| RULE-CONV-17 | 6 | 1 | 3 | 2 | 7 | 61 |
| RULE-CONV-18 | 11 | 0 | 5 | 6 | 42 | 188 |
| RULE-CONV-19 | 8 | 2 | 6 | 0 | 10 | 10 |
| RULE-CONV-20 | 2 | 0 | 0 | 2 | 10 | 143 |
| RULE-CONV-21 | 1 | 0 | 0 | 1 | 7 | 46 |
| RULE-CONV-22 | 5 | 0 | 3 | 2 | 25 | 173 |
| RULE-CONV-23 | 10 | 0 | 1 | 9 | 31 | 141 |
| RULE-CONV-24 | 13 | 0 | 7 | 6 | 26 | 155 |
| RULE-CONV-25 | 10 | 0 | 6 | 4 | 38 | 150 |
| RULE-CONV-26 | 2 | 0 | 2 | 0 | 6 | 0 |
| RULE-CONV-27 | 6 | 0 | 4 | 2 | 27 | 443 |
| RULE-CONV-28 | 6 | 0 | 4 | 2 | 22 | 275 |
| RULE-CONV-29 | 1 | 0 | 1 | 0 | 7 | 99 |
| **Total** | **127** | **8** | **54** | **65** | **403** | **4,609** |

Source accounting for those 127 identities contains 78 `INTERPRETED`, 0 `DUPLICATE`, and 396 `OMITTED` disposition records. Disposition-record totals exceed source roots because some source roots have separate represented and omitted portions. All 4,609 nested paths are covered by parent-entry dispositions and independently checked; raw classification counts are not treated as nested-path counts.

## Remaining gaps and references

All gap decisions remain as previously approved. No new gap family was implemented. Among families encountered in these batches:

- **Record-specific skipped decisions:** R-GAP-13, R-GAP-16, R-GAP-21, and R-GAP-35 are `SKIPPED_OMIT_FOR_NOW` for the assigned Rules in the plan. Earlier batch summaries incorrectly grouped these four with families described as deferred in the family-level prioritization summary; the record-specific decisions govern these Rule conversions. Their skipped mechanics remain omitted.
- **Deferred to later domains for the assigned Rules:** R-GAP-25, R-GAP-26, R-GAP-28, R-GAP-29, R-GAP-31, R-GAP-33, R-GAP-34, R-GAP-37, and R-GAP-46.
- **Omitted for now:** R-GAP-19, R-GAP-22, R-GAP-24, R-GAP-27, R-GAP-43, and R-GAP-44.
- **Implemented under its existing decision:** R-GAP-36.

The reconciliation inventory currently contains 11 unresolved Rule source-field references: the XDMG `reprintedAs` targets Alien Technology, Milestones, Renown, Loyalty, Explosives, Mixing Potions, Training to Gain Levels, Planar Effects, Diagonal Movement, and Firearms, plus the XPHB Falling hazard target. These remain `REFERENCE_UNRESOLVED`; no target IDs were invented. Explicit canonical references emitted by these batches resolve against their pinned source catalogs. Other external dependencies (such as source tables, Creature/Item/Spell domains, or book-section references) are documented in the affected batch reports and omission evidence rather than represented as invented canonical targets.

Coverage is not equivalent to accounting: every Rule is accounted and referenceable, while 8 are FULL, 54 PARTIAL, and 65 UNSUPPORTED across these 26 batches. A reference-only record with unsupported mechanics remains UNSUPPORTED; reference-bearing overview records are PARTIAL only where their represented relationship is meaningful.

## Validation and boundaries

The final `npm run validate:core-rules` passed: 9 Draft 2020-12 schemas compiled, 89 examples validated, 34 Phase 3 canonical records validated, 547 Phase 5 canonical records validated, and 832 permanent checks passed. The final `git diff --check` passed; Git reported only existing LF-to-CRLF working-tree notices for the edited text files.

No approved schema, immutable source file, or gap decision was changed. No normalized runtime packaging or Phase 6 work was started. Phase 5 is not declared complete: this report records only the authorized Rule Conversion batches, and subsequent milestone work still requires its separate approval.
