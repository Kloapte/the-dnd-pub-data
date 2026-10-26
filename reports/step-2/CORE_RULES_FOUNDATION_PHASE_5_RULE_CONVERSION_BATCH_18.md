# Phase 5 Rule Conversion - Batch 18

## Verified checkpoint before batch

RULE-CONV-17 passed validation with 156 Rules conversion-accounted, 75 pending, 152 canonical Rule outputs, 472 Phase 5 outputs, and 745 permanent checks. Semantic review remains complete for all 231 Rules.

## Selected Rules and conversion

The approved plan selects 11 Rules, totaling 42 source-entry roots and 188 nested-clause paths. Every complete source object was inspected. The approved Rule contract has no character advancement, feat, point-buy, lineage, optional class feature, campaign narrative resource, or reputation progression mechanic. Consequently these canonical records are reference-only; no unsupported mechanics were approximated.

| Canonical ID | Coverage | Conversion |
|---|---|---|
| `rule:dmg:alternatives-to-epic-boons` | PARTIAL | References the ability-score and feat reward concepts. Level-20 eligibility, repeatability, and reward grant procedure are unsupported. |
| `rule:dmg:level-advancement-without-xp` | PARTIAL | References the XPHB XP-based system that this variant replaces. Session/story advancement mechanics are unsupported. |
| `rule:dmg:milestones` | PARTIAL | References XP, encounter, Short Rest, and Hit Dice concepts. Milestone selection, XP equivalence, and recovery award procedures are unsupported. |
| `rule:dmg:plot-points` | UNSUPPORTED | Reference-only. Plot-point economy, narrative authority, scene complications, and DM rotation are unsupported. |
| `rule:dmg:renown` | PARTIAL | References the source's attitude relationships. Organization-scoped tracking, awards, ranks, perks, loss, and piety are unsupported. |
| `rule:phb:customizing-ability-scores` | UNSUPPORTED | Reference-only. The 27-point budget and score cost table cannot be represented by the approved modifier mapping. |
| `rule:phb:feats` | UNSUPPORTED | Reference-only. Feat grants, class-level replacement, uniqueness, and prerequisite activation require character progression modeling. |
| `rule:tce:customizing-your-origin` | PARTIAL | References all 16 source-listed PHB language options. Ability, language, proficiency, species, size, speed, sense, feat, and personality selection require character-origin composition. |
| `rule:tce:optional-class-features` | UNSUPPORTED | Reference-only. Optional feature selection, eligibility, replacement, and prerequisite effects require Class/Character progression. |
| `rule:xge:tying-knots` | UNSUPPORTED | References the PHB skill-ability variant, but the knot check/DC procedure is wholly excluded by skipped R-GAP-19. |
| `rule:xphb:experience-points` | UNSUPPORTED | Reference-only. XP accumulation and character-level thresholds have no approved mechanic. The XDMG award guidance is an external book dependency without a specific Rule target. |

The only reused structures were approved typed references and canonical provenance. No mechanics pattern was forced onto character-generation or campaign-progression content.

## Accounting, gaps, and references

All 42 roots and 188 nested paths are accounted for: 9 INTERPRETED, 42 OMITTED, 0 DUPLICATE. Entries with an interpreted reference and an omitted procedure carry both dispositions. Nested paths are enumerated in their parent dispositions; the omission register preserves the source entries.

Skipped R-GAP-19 was encountered and left unimplemented. No other approved gap family was implemented.

The three Renown attitude references (`rule:xphb:attitude`, `rule:xphb:friendly-attitude`, and `rule:xphb:indifferent-attitude`) resolve to source catalog identities but do not yet have canonical outputs; they are assigned to RULE-CONV-19. All other emitted references resolve to canonical outputs. The DMG source reprints for Level Advancement and Renown point to absent XDMG Rule identities and are recorded as `REFERENCE_UNRESOLVED`; their identities were not invented. The Experience Points Rule cites general XDMG book guidance, recorded as `EXTERNAL_DEPENDENCY` without a fabricated Rule reference.

## Validation and checkpoint

One permanent source-backed assertion verifies all 11 identities, provenance, exact source paths and root/nested totals, coverage, typed references, the skipped R-GAP-19 decision, unresolved reprint targets, and reference-only shapes.

| Metric | Before | After |
|---|---:|---:|
| Rule identities conversion-accounted | 156 | 167 |
| Rule conversions pending | 75 | 64 |
| Canonical Rule outputs | 152 | 163 |
| Phase 5 canonical outputs | 472 | 483 |
| Permanent checks | 745 | 757 |

Coverage is 66 FULL, 48 PARTIAL, and 53 UNSUPPORTED. All 231 Rules remain semantically reviewed.

`npm run validate:core-rules` passed: 9 schemas compiled, 89 examples validated, 34 Phase 3 records validated, 483 Phase 5 records validated, and 757 permanent checks passed. `git diff --check` passed (configured LF-to-CRLF notices may be printed).

## Files changed for this batch

- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-alternatives-to-epic-boons.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-level-advancement-without-xp.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-milestones.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-plot-points.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/dmg-renown.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/phb-customizing-ability-scores.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/phb-feats.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/tce-customizing-your-origin.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/tce-optional-class-features.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xge-tying-knots.json`
- `datasets/phase-5/step-2/core-rules/v1/rule/xphb-experience-points.json`
- `doc/rule/omitted.md`
- `reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`
- `tools/validate-core-rules.cjs`
- `reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_CONVERSION_BATCH_18.md`
