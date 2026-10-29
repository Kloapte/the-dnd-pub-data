# Phase 5 Rule Conversion — Batch 26

## Scope and checkpoint

The plan assigns two PHB downtime Rules to `RULE-CONV-26`, totaling 6 source-entry roots and no nested clause paths. The validated pre-batch checkpoint was 216 Rules conversion-accounted, 15 pending, 212 canonical Rule outputs, 532 Phase 5 outputs, and 813 permanent checks.

| Measure | Before | After |
|---|---:|---:|
| Rule identities / semantically reviewed | 231 / 231 | 231 / 231 |
| Conversion-accounted / pending | 216 / 15 | 218 / 13 |
| Canonical Rule outputs | 212 | 214 |
| Phase 5 canonical outputs | 532 | 534 |
| Permanent checks | 813 | 816 |
| Coverage (FULL / PARTIAL / UNSUPPORTED) | 68 / 71 / 77 | 68 / 73 / 77 |

## Conversions

- `rule:phb:downtime-activity-researching` — **PARTIAL**. Reuses the approved downtime `research` procedure and `information-gained` outcome. The source leaves information availability, duration, restrictions, and optional checks to the DM; its daily expense is not supported by the schema. The optional checks are examples with no fixed DC, so none were added.
- `rule:phb:downtime-activity-training` — **PARTIAL**. Reuses the approved downtime `train` procedure and represents the stated 250-day duration. The instructor/check requirements are DM-determined; cost and the alternative Language or tool-proficiency outcomes cannot be represented faithfully in this Rule schema. No generic resource outcome was invented.

Both records are affected by skipped R-GAP-13. The independently supported activity and fixed-duration details were represented without adding a general downtime engine. The reviewed patterns were R-PAT-18 (downtime procedures) and R-PAT-07 (repeated checks/outcome bands); the latter was not applied because checks are optional and DM-determined in the source.

## Source accounting and validation

All 6 roots are accounted for: 2 `INTERPRETED`, 6 `OMITTED`, 0 `DUPLICATE` dispositions, with split dispositions on roots that include both represented and unsupported details. There are no nested paths. Omissions identify DM-discretionary content as `SOURCE_AMBIGUOUS`, nonmechanical examples as `NON_MECHANICAL`, and unsupported costs or outcomes as `SCHEMA_UNSUPPORTED`. No canonical reference is unresolved; source skill dependencies for the optional research checks resolve.

Permanent assertions cover both identities, provenance, source paths, R-GAP-13, source accounting, the research outcome, the 250-day training duration, omitted conditional checks and costs, and the absence of a fabricated generic proficiency reward. No contract or source data changed.

Validation passed:

- `npm run validate:core-rules` — 9 schemas, 89 examples, 34 Phase 3 records, 534 Phase 5 records, 816 permanent checks.
- `git diff --check` — passed.

Created two canonical Rule records and this report. Updated `doc/rule/omitted.md`, `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`, and `tools/scripts/step-2/validate-core-rules.cjs`.
