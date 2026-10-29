# Phase 5 Rule Conversion — Batch 23

## Verified checkpoint before batch

RULE-CONV-22 passed validation with 183 conversion-accounted Rules, 48 pending conversions, 179 canonical Rule outputs, 499 Phase 5 canonical outputs, and 777 permanent checks. All 231 Rules remain semantically reviewed.

The plan selects these 10 Rules and exact source paths: `rule:dmg:explosives` (`$.variantrule[104]`), `rule:dmg:mixing-potions` (`$.variantrule[153]`), `rule:dmg:more-difficult-identification` (`$.variantrule[156]`), `rule:dmg:scroll-mishaps` (`$.variantrule[188]`), `rule:dmg:spell-points` (`$.variantrule[208]`), `rule:dmg:wands-that-don-t-recharge` (`$.variantrule[228]`), `rule:mm:familiars` (`$.variantrule[107]`), `rule:mm:player-characters-as-vampires` (`$.variantrule[171]`), `rule:vgm:familiars` (`$.variantrule[108]`), and `rule:vrgr:haunted-traps` (`$.variantrule[118]`). The batch contains 31 roots and 141 nested paths. Complete source objects were inspected.

## Conversion results

| Canonical ID | Coverage | Representation |
|---|---|---|
| `rule:dmg:explosives` | PARTIAL | Structured area, save, damage, and obscuration components for bombs, powder containers, dynamite, fragmentation grenades, and smoke grenades. Action use/ranges, half damage on successful saves, scaling/fuse timing, illumination, smoke duration, and wind dispersal are omitted. |
| `rule:dmg:mixing-potions` | UNSUPPORTED | Reference-only; the potion-combination and heterogeneous d100 outcomes are skipped under R-GAP-43. |
| `rule:dmg:more-difficult-identification` | UNSUPPORTED | Reference-only; the schema has no magic-item identification procedure, and the Identify Spell target is unresolved. |
| `rule:dmg:scroll-mishaps` | UNSUPPORTED | Reference-only; the failed-casting save only triggers a heterogeneous spell-mishap table, so the save is not represented separately. |
| `rule:dmg:spell-points` | UNSUPPORTED | Reference-only; the approved schema lacks point pools, slot-conversion costs, class-level progression, slot caps, and recovery lifecycle. |
| `rule:dmg:wands-that-don-t-recharge` | UNSUPPORTED | Reference-only; charge maximum and permanent depletion belong to item state and are not modeled by the Rule contract. |
| `rule:mm:familiars` | UNSUPPORTED | Reference-only; NPC familiar assignment depends on Find Familiar and Creature-domain candidates. |
| `rule:mm:player-characters-as-vampires` | UNSUPPORTED | Reference-only; the procedure combines character score changes, Creature trait transfer, Charm DC formula, alignment/control state, and Creature stat blocks. VRGR citation is preserved. |
| `rule:vgm:familiars` | UNSUPPORTED | Reference-only; spell and Creature-domain dependencies are not normalized by this Rule contract. |
| `rule:vrgr:haunted-traps` | UNSUPPORTED | Reference-only; the hazard detection window and disarm procedure are deferred under R-GAP-29. |

One Rule is mechanics-bearing; nine are reference-only. No Rule references were emitted for unavailable Spell, Creature, Equipment, or Hazard targets. No new mechanics patterns were added.

## Source accounting and dependencies

All 31 roots and 141 nested paths are accounted for. Root disposition totals are 4 INTERPRETED, 32 OMITTED, and 0 DUPLICATE; the total exceeds root count because some source roots contain separate represented and omitted portions. Nested paths are explicitly attached to their covering dispositions. Complete omitted source entries were added to the omission register.

Skipped R-GAP-43 (potion mixing) and deferred R-GAP-29 (haunted-trap detection/disarming) were encountered. No gap was implemented.

Unresolved references recorded: the Identify Spell and Haunted Traps' named spell, class-feature, and thieves' tools references have no usable canonical targets in the current approved outputs. Find Familiar, Creature candidates/stat blocks, and Equipment identities are documented as external dependencies. Two explicit XDMG reprint targets (`Explosives|XDMG`, `Mixing Potions|XDMG`) are absent from the pinned Rule catalog and are preserved as `REFERENCE_UNRESOLVED` source-field issues; they are omitted from canonical metadata rather than guessed.

## Validation and checkpoint

A permanent source-backed assertion verifies all ten identities, exact source provenance, root/nested totals, coverage and gap IDs, unresolved XDMG reprints, the exact Explosion mechanics/components and composition, reference-only fallbacks, and that unsupported Spell Points, vampire, familiar, potion, scroll, wand, identification, and haunted-trap procedures were not encoded.

| Metric | Before | After |
|---|---:|---:|
| Rule identities | 231 | 231 |
| Semantically reviewed | 231 | 231 |
| Conversion-accounted | 183 | 193 |
| Pending conversion | 48 | 38 |
| Canonical Rule outputs | 179 | 189 |
| Phase 5 canonical outputs | 499 | 509 |
| Permanent checks | 777 | 788 |

Coverage is 68 FULL, 58 PARTIAL, and 67 UNSUPPORTED. `npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 509 Phase 5 records validated, and 788 permanent checks passed. `git diff --check` passed.

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-explosives.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-mixing-potions.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-more-difficult-identification.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-scroll-mishaps.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-spell-points.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-wands-that-don-t-recharge.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/mm-familiars.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/mm-player-characters-as-vampires.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/vgm-familiars.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/vrgr-haunted-traps.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_23.md`
