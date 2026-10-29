# Step 2 Phase 5 — Rule Conversion Batch 02 (RULE-CONV-02)

## 1. Verified baseline and final checkpoint

The pre-conversion validator and accounting matched the requested baseline. RULE-CONV-01 remained intact.

| Metric | Before | After |
|---|---:|---:|
| Rule catalog identities | 231 | 231 |
| Semantically reviewed | 231 | 231 |
| Semantic review pending | 0 | 0 |
| Conversion-accounted | 79 | 86 |
| Conversion pending | 152 | 145 |
| Canonical Rule outputs | 75 | 82 |
| Phase 5 canonical outputs | 395 | 402 |
| Permanent checks | 652 | 660 |

The legacy `pendingSemanticReviewCount` remains 164 as instructed. It is not used as a semantic-review metric.

The Rule catalog conversion coverage is now 57 FULL, 18 PARTIAL, and 11 UNSUPPORTED across 86 accounted identities.

## 2. Per-Rule results

All seven canonical records are reference-only at the Rule level: none has a `mechanics` array. Action Options links its six procedures to resolved Action records, but Rule v1 cannot represent whether the options are adopted as a group or individually, so its coverage is PARTIAL. The other six retain identity and provenance while their mechanics remain unsupported in the approved Rule contract or excluded by an approved gap decision.

| Canonical ID | Coverage | Roots / nested paths | Conversion |
|---|---|---:|---|
| `rule:dmg:action-options` | PARTIAL | 7 / 22 | References all six optional procedures to their existing canonical Action records. The Action domain owns the procedures, so Rule mechanics are not duplicated. The group-or-individual adoption policy is omitted because Rule v1 has no selection structure. |
| `rule:dmg:cleaving-through-creatures` | UNSUPPORTED | 2 / 0 | Reference-only. The excess-damage and repeated-target procedure is skipped under R-GAP-40. |
| `rule:dmg:facing` | UNSUPPORTED | 6 / 0 | Reference-only. All mechanics depend on the skipped facing and orientation model in R-GAP-14. |
| `rule:dmg:firearms` | UNSUPPORTED | 3 / 15 | Reference-only. Firearm proficiency, ammunition, burst fire, and reload combine Equipment data and Action execution that this Rule contract cannot represent independently. |
| `rule:dmg:injuries` | UNSUPPORTED | 6 / 28 | Reference-only. Injury triggers, selection, persistent effects, and recovery are skipped under R-GAP-23. |
| `rule:dmg:massive-damage` | UNSUPPORTED | 3 / 16 | Reference-only. The saving throw is inseparable from the skipped trigger and System Shock outcomes in R-GAP-42. |
| `rule:mm:player-characters-as-lycanthropes` | UNSUPPORTED | 4 / 29 | Reference-only. Transformation, creature traits, character changes, curse behavior, and condition outcomes span external domains and state. |

The six Action references for Action Options resolve to `action:dmg:climb-onto-a-bigger-creature`, `action:dmg:disarm`, `action:dmg:mark`, `action:dmg:overrun`, `action:dmg:shove-aside`, and `action:dmg:tumble`. Permanent assertions inspect representative operation details on each target to confirm the Rule references the existing mechanic owners.

## 3. Coverage and source accounting

The seven records account for 31 source-entry roots and all 110 nested clause paths recorded in the semantic inventory. The established source-accounting model records 32 disposition entries: 7 INTERPRETED, 0 DUPLICATE, and 25 OMITTED. One source root has both an interpreted Action-reference portion and an omitted option-selection portion. Each nested clause path is named in its root classification and verified against the immutable source. Full original source entries for omissions are retained in `doc/rule/omitted.md`.

After this batch, the Phase 5 Rule batch accounts for 81 Rule identities (including 5 Phase 3/4 representatives excluded from conversion batches), 198 source-entry roots, and 77 Phase 5 Rule outputs. Its coverage is 53 FULL, 17 PARTIAL, and 11 UNSUPPORTED.

Global Phase 5 source accounting now records 474 roots and 319 INTERPRETED, 28 DUPLICATE, and 316 OMITTED disposition entries across 409 examined source records. Coverage totals are 232 fully represented, 163 partially represented, and 14 unsupported source records. The canonical output count is 402; source-record and output totals differ where existing source records do not produce separate outputs.

## 4. Gap decisions and dependencies

- **R-GAP-40 — Cleaving Through Creatures:** repeated target selection and carried damage are omitted as `OUT_OF_APPROVED_SCOPE`.
- **R-GAP-14 — Facing:** orientation state, arcs, and spatial targeting rules are omitted as `OUT_OF_APPROVED_SCOPE`.
- **R-GAP-23 — Injuries:** injury selection, consequences, persistence, and recovery are omitted as `OUT_OF_APPROVED_SCOPE`.
- **R-GAP-42 — Massive Damage:** trigger, save resolution, and System Shock outcomes are omitted together as `OUT_OF_APPROVED_SCOPE`; the saving throw is not presented on its own.
- R-GAP-44 (Morale) is not part of this batch.

For Firearms, `Firearms|XDMG` has no target Rule identity in the immutable catalog; it is recorded as `REFERENCE_UNRESOLVED` in the source-field register. The source's PHB training reference and firearm equipment/statistics remain external dependencies. No equipment identity or mechanic was invented.

The lycanthrope record names Werebear, Wereboar, Wererat, Weretiger, Werewolf, Wereraven (VRGR), Werebat (WDMM), and Loup Garou (VRGR). No matching canonical Creature outputs are available in this checkpoint, so no unresolved Creature references were emitted. Their source-backed identities and mechanics remain accounted as external Creature/Character dependencies. The full source evidence remains in the omission register.

## 5. Registers and permanent checks

The Rule omission register now includes the complete source entries for unsupported procedures and dependencies. No duplicate source content was identified. The Phase 5 accounting manifest records the seven outputs, coverage, source paths, omission causes, conversion totals, and the unresolved Firearms reprint.

Permanent validation now checks the seven exact identities and source paths, provenance, schema-valid reference-only forms, Action reference resolution and owned mechanics, absence of unsupported mechanics/references, all root and nested-path dispositions, omission-register evidence, duplicate IDs, the exact Batch 2 boundary, and reconciled manifest totals. Permanent checks rose from 652 to 660: seven per-record canonical checks and one Batch 2 semantic/accounting check.

## 6. Validation

`npm run validate:core-rules` passes:

- 9 Draft 2020-12 schemas compiled.
- 89 schema examples validated.
- 34 Phase 3 canonical records validated.
- 402 Phase 5 canonical records validated.
- 660 permanent checks passed.
- 49 Rule components and 27 Action records checked.

`git diff --check` passes. It emitted Git line-ending warnings for pre-existing modified workspace files, but no whitespace errors.

## 7. Remaining conversion work

145 Rule identities remain pending conversion accounting. Semantic review remains complete at 231 reviewed and 0 pending. RULE-CONV-02 is complete; no later conversion batch or Phase 6 integration was started. The legacy manifest field discrepancy remains unchanged.

## 8. Files changed for RULE-CONV-02

- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-action-options.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-cleaving-through-creatures.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-facing.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-firearms.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-injuries.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-massive-damage.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/mm-player-characters-as-lycanthropes.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_02.md`
- `tools/scripts/step-2/validate-core-rules.cjs`

No schema, source catalog, Action, Creature, or Equipment record was changed.
