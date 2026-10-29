# Phase 5 Rule Conversion — Batch 28

## Scope and checkpoint

`RULE-CONV-28` contains six XGE downtime Rules, 22 source-entry roots, and 275 nested clause paths. The pre-batch validator passed at 224 conversion-accounted Rules, 7 pending, 220 canonical Rule outputs, 540 Phase 5 outputs, and 823 permanent checks.

| Measure | Before | After |
|---|---:|---:|
| Rule identities / semantically reviewed | 231 / 231 | 231 / 231 |
| Conversion-accounted / pending | 224 / 7 | 230 / 1 |
| Canonical Rule outputs | 220 | 226 |
| Phase 5 canonical outputs | 540 | 546 |
| Permanent checks | 823 | 830 |
| Coverage (FULL / PARTIAL / UNSUPPORTED) | 68 / 77 / 79 | 68 / 81 / 81 |

## Conversions

- `rule:xge:downtime-activity-religious-service` — **UNSUPPORTED**. Reference-only: the temple-access condition, alternative skill checks, favor grants/storage/use, and complication procedure are not represented by the approved Rule schema.
- `rule:xge:downtime-activity-research` — **PARTIAL**. Represents one week of research, an Intelligence check, and information-gained outcome. Resource access/cost, variable modifiers, conditional advantage, outcome bands, and complication procedures are omitted.
- `rule:xge:downtime-activity-scribing-a-spell-scroll` — **PARTIAL**. Represents scroll creation as a downtime craft activity with item-created outcome. Spell-level time/cost, spell requirements, material components, cantrip behavior, and complications are omitted.
- `rule:xge:downtime-activity-selling-a-magic-item` — **PARTIAL**. Represents a one-week sell activity and Charisma (Persuasion) check. Costs, item-count restriction, retries, rarity prices, offer bands, and complications are omitted.
- `rule:xge:downtime-activity-training` — **UNSUPPORTED**. Reference-only: its language/tool proficiency outcomes, Intelligence-modified time, recurring cost, and complications cross into other domains and exceed the supported procedure fields.
- `rule:xge:downtime-activity-work` — **PARTIAL**. Represents one workweek of work. Check alternatives, wage/lifestyle bands, and complications are omitted.

The conversions reused the approved `DOWNTIME_PROCEDURE/ACTIVITY` and `abilityCheck` structures. R-PAT-18 (downtime procedures), R-PAT-07 (repeated checks/outcome bands), R-PAT-13 (social roles/attitudes), and R-PAT-09 (ability checks) were reviewed. No schema or mechanics pattern was added. All six Rules are within skipped R-GAP-13; the batch represents only independent activity, duration, check, and outcome portions.

## Source accounting and dependencies

All 22 roots and 275 nested paths are covered. Root-level dispositions total 6 `INTERPRETED`, 19 `OMITTED`, and 0 `DUPLICATE`. Some roots have both interpreted and omitted portions; the manifest links each nested path to the parent disposition and the permanent checks verify that coverage.

Omissions distinguish schema limits from external Spell, Item/Equipment, Skill, and Character dependencies. The source references Spell Scroll costs and spell/item prerequisites, but those targets are not encoded as invented canonical Rule references. The represented Persuasion check reference resolves. No canonical references or `reprintedAs` fields remain unresolved in this batch.

Permanent assertions verify the six identities, provenance, source paths, coverage, R-GAP-13, accounting completeness, supported checks/durations/outcomes, reference-only validity, and absence of flattened spell-level costs, advantage conditions, or complication mechanics.

Validation passed:

- `npm run validate:core-rules` — 9 schemas, 89 examples, 34 Phase 3 records, 546 Phase 5 records, 830 permanent checks.
- `git diff --check` — passed.

Created six canonical Rule records and this report. Updated `doc/rule/omitted.md`, `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`, and `tools/scripts/step-2/validate-core-rules.cjs`.
