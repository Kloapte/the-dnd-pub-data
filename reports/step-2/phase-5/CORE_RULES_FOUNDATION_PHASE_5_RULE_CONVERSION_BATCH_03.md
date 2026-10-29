# Step 2 Phase 5 — Rule Conversion Batch 03 (RULE-CONV-03)

## 1. Verified baseline and final checkpoint

The required pre-change `npm run validate:core-rules` passed. The RULE-CONV-02 checkpoint matched: 231 Rule identities, all 231 semantically reviewed, 86 conversion-accounted, 145 pending, 82 canonical Rule outputs, 402 Phase 5 outputs, and 660 permanent checks. RULE-CONV-01 and RULE-CONV-02 were preserved.

| Metric | Before | After |
|---|---:|---:|
| Rule identities | 231 | 231 |
| Semantically reviewed | 231 | 231 |
| Semantic review pending | 0 | 0 |
| Conversion-accounted | 86 | 104 |
| Pending conversion | 145 | 127 |
| Canonical Rule outputs | 82 | 100 |
| Phase 5 canonical outputs | 402 | 420 |
| Permanent checks | 660 | 679 |

The legacy `pendingSemanticReviewCount` value remains unchanged at 164 as instructed. It is not evidence of pending semantic review.

The 18 Rule coverage results are 3 FULL, 10 PARTIAL, and 5 UNSUPPORTED. Global Rule coverage is now 60 FULL, 28 PARTIAL, and 16 UNSUPPORTED across 104 accounted Rules.

## 2. Exact selected identities and results

Batch membership came from `batchId = RULE-CONV-03` in the approved conversion plan JSON. Every source path and identity was checked against the immutable `variantrules.json` catalog before conversion.

| Canonical ID | Source path | Roots / nested paths | Coverage | Conversion |
|---|---|---:|---|---|
| `rule:xge:adamantine-weapons` | `$.variantrule[4]` | 3 / 0 | PARTIAL | Links the object-hit critical-hit outcome to Critical Hit. Equipment identities, object-hit conditional resolution, and price are not represented. |
| `rule:xphb:bonus-action` | `$.variantrule[18]` | 1 / 0 | PARTIAL | Links its same-turn relationship to Action. The once-per-turn limit and explicit-grant requirement are omitted. |
| `rule:xphb:breaking-objects` | `$.variantrule[19]` | 6 / 58 | PARTIAL | Links the no-ability-score rule to Ability Check and Saving Throw owners. Object durability and tables are deferred under R-GAP-39. |
| `rule:xphb:damage` | `$.variantrule[45]` | 1 / 0 | FULL | Links damage to its Hit Points target state. |
| `rule:xphb:damage-roll` | `$.variantrule[46]` | 1 / 0 | PARTIAL | Links its result to Damage. A generic die roll and modifier procedure cannot be encoded without inventing a base formula or damage type. |
| `rule:xphb:damage-threshold` | `$.variantrule[47]` | 1 / 0 | UNSUPPORTED | The schema has a numeric threshold field but no comparison or whole-damage-instance procedure. |
| `rule:xphb:damage-types` | `$.variantrule[48]` | 2 / 32 | PARTIAL | Links the use of damage-type labels to Resistance and Vulnerability. The type taxonomy is not represented by Rule v1. |
| `rule:xphb:grappling` | `$.variantrule[117]` | 2 / 10 | PARTIAL | Links initiation, Grappled, escape, skills, and ending conditions to canonical owners. The one-grapple-per-hand/body-part restriction is omitted. |
| `rule:xphb:hit-points` | `$.variantrule[127]` | 1 / 0 | FULL | Structures current, maximum, and zero Hit Point states and links Damage and Healing. |
| `rule:xphb:immunity` | `$.variantrule[133]` | 1 / 0 | UNSUPPORTED | Rule v1 has no Immunity component for damage types or conditions. |
| `rule:xphb:improvised-weapons` | `$.variantrule[134]` | 2 / 13 | UNSUPPORTED | Weapon identity and thrown properties depend on Equipment; negative proficiency and DM-selected damage type are unsupported. No Item output was invented. |
| `rule:xphb:knocking-out-a-creature` | `$.variantrule[142]` | 2 / 0 | PARTIAL | Links Hit Points, Unconscious, Short Rest, Medicine, and DC concepts. The conditional transition and first-aid sequence are omitted. |
| `rule:xphb:occupied-space` | `$.variantrule[162]` | 1 / 0 | PARTIAL | Links creature occupancy to the Creature Rule. The filled-by-objects alternative has no canonical Object target. |
| `rule:xphb:reach` | `$.variantrule[179]` | 1 / 0 | FULL | Represents the default five-foot melee reach using the approved Attack Roll reach field. |
| `rule:xphb:reaction` | `$.variantrule[180]` | 1 / 0 | PARTIAL | Links Opportunity Attack and same-turn Action relationships. Trigger scheduling and the once-until-next-turn limit are omitted. |
| `rule:xge:simultaneous-effects` | `$.variantrule[198]` | 1 / 0 | UNSUPPORTED | Rule v1 cannot express controller choice over simultaneous-effect order. Source reprint provenance is preserved. |
| `rule:xphb:simultaneous-effects` | `$.variantrule[199]` | 1 / 0 | UNSUPPORTED | Rule v1 cannot express turn-owner choice over simultaneous-effect order. |
| `rule:xphb:size` | `$.variantrule[200]` | 1 / 0 | PARTIAL | Links creature Size to Occupied Space and object Size to Hit Points. Size categories are not represented. |

Three reference-only outputs preserve brief concepts or supported relationships: Damage is FULL; Damage Roll and Damage Types are PARTIAL. Overall, 2 Rules contain structured mechanics (Hit Points and Reach); 16 are reference-only. Unsupported or incomplete mechanics were not replaced with placeholders.

## 3. Patterns and ownership boundaries

The batch reused the approved reference, turn-order, object interaction, creature-state, and damage/attack patterns from R-PAT-04, R-PAT-06, R-PAT-08, and R-PAT-16. The spatial pattern R-PAT-03 informed the Occupied Space and Size ownership boundaries. The only structured components added are existing Rule v1 Hit Points and Attack Roll structures; no schema pattern was introduced.

Canonical Action, Condition, Skill, and Rule records remain the owners of their mechanics. No related domain records were modified. References emitted in these 18 Rules resolve against canonical outputs. No source references were guessed or substituted across editions.

Unresolved dependencies retained in the omission register:

- Adamantine armor, weapons, and ammunition, and Improvised Weapon/Club identities need Equipment outputs.
- Breaking Objects references an Armor Class Rule that has no canonical output in the current dataset; Object durability tables and procedures are deferred.
- Occupied Space's object-filled alternative has no canonical Object target.
- Generic damage types have no Rule v1 taxonomy structure; their examples are descriptive guidance.

These are not broken canonical references: unresolved targets are accounted as omissions rather than emitted as references.

## 4. Source accounting and gap decisions

The selected records account for all 29 source-entry roots and all 113 nested clause paths in the semantic inventory. Nested paths are named in their parent-root dispositions; they are not treated as additional source roots.

| RULE-CONV-03 disposition | Count |
|---|---:|
| INTERPRETED | 15 |
| DUPLICATE | 0 |
| OMITTED | 25 |

The 40 disposition records include multiple classifications for mixed roots. The batch introduced 15 INTERPRETED and 25 OMITTED dispositions. Global Phase 5 totals are 503 roots and 334 INTERPRETED, 28 DUPLICATE, and 341 OMITTED dispositions.

R-GAP-39 (object durability and break procedures) is deferred and was encountered in Breaking Objects. The approved skipped R-GAP-42 Massive Damage procedure was not revived. The Damage Threshold Rule was separately classified as schema-unsupported because its comparison procedure is not representable. No other skipped gap family was implemented.

No duplicate source content was identified. The Rule omission register was extended with exact original source entries for omitted portions, causes, and paths. The source-accounting manifest records each source root, nested path set, disposition, coverage, and canonical output.

## 5. Permanent validation assertions

One dedicated RULE-CONV-03 semantic/accounting check was added. Together with the 18 generic canonical-record checks, permanent checks rose by 19 (660 to 679). The dedicated check verifies exact identities and source paths, coverage classifications, provenance, all root and nested-path accounting, canonical reference resolution, Hit Points and Reach mechanics, grapple ownership references, omitted unsupported mechanics, XGE reprint provenance, and manifest totals.

## 6. Validation and remaining work

`npm run validate:core-rules` passes:

- 9 Draft 2020-12 schemas compiled.
- 89 schema examples validated.
- 34 Phase 3 canonical records validated.
- 420 Phase 5 canonical records validated.
- 679 permanent checks passed.
- 49 Rule example components and 27 Action records checked.

`git diff --check` passes. Git printed line-ending conversion warnings for pre-existing modified workspace files; it reported no whitespace errors.

127 Rules remain pending conversion accounting. Semantic review remains complete at 231 reviewed and 0 pending. Phase 5 remains in progress. No RULE-CONV-04 work, schema change, runtime packaging, or Phase 6 integration was started.

## 7. Files changed for RULE-CONV-03

- `datasets/phase-5/step-2/core-rules/v1/rule/xge-adamantine-weapons.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xge-simultaneous-effects.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-bonus-action.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-breaking-objects.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-damage.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-damage-roll.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-damage-threshold.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-damage-types.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-grappling.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-hit-points.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-immunity.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-improvised-weapons.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-knocking-out-a-creature.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-occupied-space.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-reach.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-reaction.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-simultaneous-effects.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-size.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/scripts/step-2/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_03.md`

No source catalog, schema, Action, Creature, Equipment, or unrelated Rule conversion was changed.
