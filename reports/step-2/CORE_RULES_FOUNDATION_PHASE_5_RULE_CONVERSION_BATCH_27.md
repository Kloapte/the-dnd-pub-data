# Phase 5 Rule Conversion — Batch 27

## Scope and checkpoint

The plan assigns six XGE downtime Rules to `RULE-CONV-27`: 27 source-entry roots and 443 nested clause paths. The validated pre-batch checkpoint was 218 conversion-accounted Rules, 13 pending, 214 canonical Rule outputs, 534 Phase 5 outputs, and 816 permanent checks.

| Measure | Before | After |
|---|---:|---:|
| Rule identities / semantically reviewed | 231 / 231 | 231 / 231 |
| Conversion-accounted / pending | 218 / 13 | 224 / 7 |
| Canonical Rule outputs | 214 | 220 |
| Phase 5 canonical outputs | 534 | 540 |
| Permanent checks | 816 | 823 |
| Coverage (FULL / PARTIAL / UNSUPPORTED) | 68 / 73 / 77 | 68 / 77 / 79 |

## Conversions

- `rule:xge:downtime-activity-buying-a-magic-item` — **PARTIAL**. Represents a one-workweek `buy` activity and its Charisma (Persuasion) seller-quality check. Costs, bonuses, tables, price, and complications are omitted.
- `rule:xge:downtime-activity-carousing` — **PARTIAL**. Represents one workweek of `carouse` and its Charisma (Persuasion) contact check. Resource/class restrictions, check-total contact outcomes, contact state/use limits, and complication tables are omitted.
- `rule:xge:downtime-activity-crafting-an-item` — **PARTIAL**. Represents `craft` with an item-created outcome. Mundane production rates, costs, collaboration, prerequisites, magic-item requirements/tables, potion creation, and complications are omitted.
- `rule:xge:downtime-activity-crime` — **UNSUPPORTED**. Reference-only. The procedure depends on three checks, selectable DCs and alternatives, success-count outcomes, loot, jail/fines, and complications; representing only the checks would misstate the procedure.
- `rule:xge:downtime-activity-gambling` — **PARTIAL**. Represents the one-week `gamble` activity. Stake, three random-DC checks, substitutions, profit/loss/debt outcomes, and complications are omitted.
- `rule:xge:downtime-activity-pit-fighting` — **UNSUPPORTED**. Reference-only. Multiple random-DC checks, a Hit Die-derived bonus, attack-roll substitution, success-count payouts, and complications cannot be represented faithfully. The source's reference to standard combat for deadly matches was not converted into a partial pit-fighting mechanic.

The batch reused the approved `DOWNTIME_PROCEDURE/ACTIVITY` and `abilityCheck` shapes. R-PAT-18 (downtime procedures) and R-PAT-07 (repeated checks and outcome bands) were reviewed; the latter's table resolution is unsupported by Rule v1. No new schema or pattern was introduced. All six Rules are in skipped R-GAP-13, and no general downtime engine or deferred subsystem was implemented.

## Source accounting and unresolved dependencies

All 27 roots and 443 nested paths are accounted for. Root-level dispositions total 3 `INTERPRETED`, 26 `OMITTED`, and 0 `DUPLICATE`; split dispositions on mixed roots mean disposition-record totals do not equal source-root counts. The manifest and permanent checks link every nested path to its parent disposition.

Omissions distinguish descriptive framing (`NON_MECHANICAL`), contract limitations (`SCHEMA_UNSUPPORTED`), and external domain/table dependencies (`EXTERNAL_DEPENDENCY`). Buying magic items references the DMG Magic Item Tables A–I. Crafting references item/tool definitions, Spell Scroll and Potion of Healing procedures, Creature challenges, and adventures. These do not have canonical Rule targets in this conversion, so no IDs were invented. The represented Persuasion references resolve. There are no unresolved canonical references or unresolved `reprintedAs` fields in this batch.

Permanent assertions verify the six identities and provenance, coverage, source roots/nested paths, supported activity/check details, reference resolution, reference-only crime and pit-fighting records, omission boundaries, uniqueness, and manifest reconciliation.

Validation passed:

- `npm run validate:core-rules` — 9 schemas, 89 examples, 34 Phase 3 records, 540 Phase 5 records, 823 permanent checks.
- `git diff --check` — passed.

Created six canonical Rule records and this report. Updated `doc/rule/omitted.md`, `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`, and `tools/validate-core-rules.cjs`.
