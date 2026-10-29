# Phase 5 Rule Conversion — Batch 25

## Scope and checkpoint

The authoritative conversion plan assigns these 10 identities to `RULE-CONV-25`, with 38 source-entry roots and 150 nested clause paths. The pre-batch validator passed with 522 Phase 5 canonical records and 802 permanent checks.

| Measure | Before | After |
|---|---:|---:|
| Rule identities | 231 | 231 |
| Semantically reviewed | 231 | 231 |
| Conversion-accounted | 206 | 216 |
| Pending conversion | 25 | 15 |
| Canonical Rule outputs | 202 | 212 |
| Phase 5 canonical outputs | 522 | 532 |
| Permanent checks | 802 | 813 |
| Rule coverage (FULL / PARTIAL / UNSUPPORTED) | 68 / 65 / 73 | 68 / 71 / 77 |

The legacy `pendingSemanticReviewCount` remains unchanged. Semantic review is complete; the remaining 15 are pending conversion, not semantic review.

## Rule conversions

| Canonical ID | Coverage | Representation |
|---|---|---|
| `rule:dmg:downtime-activity-building-a-stronghold` | UNSUPPORTED | Reference-only identity. Land acquisition, correlated construction costs/durations, and absence delay depend on a construction and economy procedure absent from the schema. |
| `rule:dmg:downtime-activity-carousing` | UNSUPPORTED | Reference-only identity. The wealthy-lifestyle expense and percentile-plus-level outcome table cannot be represented faithfully by the approved downtime shape. The source-backed XGE reprint is retained in provenance metadata. |
| `rule:dmg:downtime-activity-gaining-renown` | PARTIAL | References the canonical Renown Rule. The score-dependent duration and increment procedure are omitted. |
| `rule:dmg:downtime-activity-performing-sacred-rites` | PARTIAL | References the Inspiration Rule as the reward. Temple/role restrictions and the minimum duration and recurring random reward cadence are omitted. |
| `rule:dmg:downtime-activity-running-a-business` | UNSUPPORTED | Reference-only identity. The business outcome table, maintenance/profit accounting, and persistent debt penalty require an unsupported economy procedure. |
| `rule:dmg:downtime-activity-selling-magic-items` | PARTIAL | Models the downtime sell activity and its Intelligence (Investigation) DC 20 buyer-search check. Rarity/location restrictions, search outcomes, assistance, price tables, the Persuasion check, and buyer consequences remain omitted. |
| `rule:dmg:downtime-activity-sowing-rumors` | PARTIAL | Models a Social Influence procedure with a Charisma DC 15 check. The settlement duration table, continuity and cost rules, optional skill choice, attitude shift, retry consequence, and community exceptions remain omitted. |
| `rule:dmg:downtime-activity-training-to-gain-levels` | UNSUPPORTED | Reference-only identity. XP/level triggers, level-indexed costs and durations, and delayed Character progression are not expressible here. Its XDMG reprint target is absent from the pinned Rule catalog and is recorded as unresolved provenance. |
| `rule:phb:downtime-activity-crafting` | PARTIAL | Models a one-day crafting activity with an item-created outcome. Tool/facility/material prerequisites, production rate, cost, group crafting, and lifestyle benefits remain omitted. |
| `rule:phb:downtime-activity-practicing-a-profession` | PARTIAL | Models work supporting a modest lifestyle. The cost waiver and organization/Performance-based comfortable or wealthy outcomes are not represented by the schema. |

The batch reused the approved `DOWNTIME_PROCEDURE/ACTIVITY`, `SOCIAL_PROCEDURE`, and nested `abilityCheck` structures. Patterns R-PAT-07 (repeated checks and outcome bands), R-PAT-12 (character generation and advancement), and R-PAT-13 (social roles and attitudes) were reviewed but not forced into these records because the source outcomes require table, state, or domain behavior not represented faithfully. No schema or mechanics pattern was added.

## Source accounting and limitations

All 38 roots and 150 nested paths are traceable through the source-accounting manifest and omission register. The batch produced 7 `INTERPRETED`, 37 `OMITTED`, and 0 `DUPLICATE` root-level classification records; some roots have multiple classifications, so disposition-record totals do not equal root count. Every nested source path is linked to its parent entry disposition and checked by the validator.

All seven Rules identify skipped R-GAP-13. The limited supported checks, activity outcome, and cross-Rule relationships above are represented independently; the deferred downtime subsystems and their omitted mechanics were not implemented. Other omissions distinguish nonmechanical setup, schema limits, and source ambiguity (the discretionary buyer identity/complication in item selling).

The source-backed `rule:xge:downtime-activity-carousing` reprint identity is preserved. The `Training to Gain Levels|XDMG` reprint target is not in the pinned source catalog; its `reprintedAs` field is recorded in `sourceFieldIssues` with `REFERENCE_UNRESOLVED`, without inventing a target. Canonical Skill references used by the represented check resolve. No other canonical reference is unresolved.

## Permanent validation and files

Permanent checks now verify exact identities and provenance, coverage, source paths, root/nested accounting completeness, reference-only validity, the supported checks and downtime mechanics, the deliberate absence of unsupported table mechanics, unresolved reprint handling, uniqueness, and manifest totals.

Validation passed:

- `npm run validate:core-rules` — 9 schemas, 89 examples, 34 Phase 3 records, 532 Phase 5 records, 813 permanent checks.
- `git diff --check` — passed.

Batch files created: 10 canonical Rule records listed above and this report. Supporting files updated: `doc/rule/omitted.md`, `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`, and `tools/scripts/step-2/validate-core-rules.cjs`.
