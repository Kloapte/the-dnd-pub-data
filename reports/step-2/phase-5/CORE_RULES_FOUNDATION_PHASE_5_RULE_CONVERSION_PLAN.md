# Step 2 Phase 5 - Rule Conversion Plan

**Planning only. This report does not authorize conversion.**

## Verified baseline

231 Rule identities; 231 semantically reviewed; 0 semantic review pending. Conversion accounting: 67 complete, 164 pending. Existing canonical Rule outputs: 63. Pending workload: 478 source-entry roots and 4,862 nested-clause paths. Phase 5 has 383 canonical outputs and 638 permanent checks.

## Planning approach

The prior monolithic attempt left no retained checkpoint; the user reports transient changes were removed. The plan starts from the verified Batch 4 state and does not infer any conversion from that attempt. Complexity uses roots, nested-path workload, pattern diversity, gaps, dependencies, and observation count. Nested paths estimate review/accounting effort and are not treated as mechanics. The planning score is 0.8 per root + 0.08 per nested path (capped at 250) + 2.5 per distinct pattern + 2 per gap family + 2 per dependency + 1.5 per semantic observation. LOW is below 14 unless root/pattern thresholds apply; MEDIUM begins at 14 or 4 roots/4 patterns; HIGH begins at 35, 7 roots, or 80 nested paths; VERY_HIGH begins at 10 roots, 250 nested paths, 6 roots plus 100 nested paths, 7 patterns, or an explicit subsystem override for ESK Sidekicks. Approaches are provisional: A existing patterns; B mixed; C reference-only after source verification; D targeted source-level investigation.

## Batch sequence

| Batch | Group | Records | Roots | Nested paths | Complexity L/M/H/VH | Prior batches required |
|---|---|---:|---:|---:|---|---|
| RULE-CONV-01 | Glossary and definitions | 12 | 15 | 30 | 11/1/0/0 | - |
| RULE-CONV-02 | Combat, damage, and space | 7 | 31 | 110 | 2/4/1/0 | - |
| RULE-CONV-03 | Combat, damage, and space 2 | 18 | 29 | 113 | 16/2/0/0 | - |
| RULE-CONV-04 | Combat, damage, and space 3 | 4 | 7 | 0 | 3/1/0/0 | - |
| RULE-CONV-05 | Acquisitions Incorporated | 6 | 14 | 323 | 0/6/0/0 | - |
| RULE-CONV-06 | Acquisitions Incorporated 2 | 4 | 9 | 197 | 0/4/0/0 | - |
| RULE-CONV-07 | Maritime operations | 6 | 21 | 128 | 2/4/0/0 | - |
| RULE-CONV-08 | Maritime operations 2 | 2 | 12 | 83 | 0/1/1/0 | - |
| RULE-CONV-09 | Maritime scenarios | 1 | 2 | 162 | 0/0/1/0 | - |
| RULE-CONV-10 | Maritime scenarios 2 | 1 | 4 | 417 | 0/0/0/1 | - |
| RULE-CONV-11 | Maritime scenarios 3 | 1 | 14 | 349 | 0/0/0/1 | - |
| RULE-CONV-12 | Maritime scenarios 4 | 1 | 6 | 353 | 0/0/0/1 | - |
| RULE-CONV-13 | Maritime scenarios 5 | 1 | 4 | 307 | 0/0/0/1 | - |
| RULE-CONV-14 | Sidekick progression | 2 | 13 | 152 | 0/1/0/1 | - |
| RULE-CONV-15 | Environment and senses | 5 | 5 | 0 | 5/0/0/0 | - |
| RULE-CONV-16 | Equipment, objects, and other rules | 12 | 34 | 254 | 8/3/1/0 | - |
| RULE-CONV-17 | Equipment, objects, and other rules 2 | 6 | 7 | 61 | 5/1/0/0 | - |
| RULE-CONV-18 | Character options and progression | 11 | 42 | 188 | 4/7/0/0 | - |
| RULE-CONV-19 | Social procedures | 8 | 10 | 10 | 7/1/0/0 | RULE-CONV-01 |
| RULE-CONV-20 | Social, horror, and state procedures | 2 | 10 | 143 | 0/1/1/0 | - |
| RULE-CONV-21 | Character options and progression 2 | 1 | 7 | 46 | 0/0/1/0 | RULE-CONV-20 |
| RULE-CONV-22 | Social, horror, and state procedures 2 | 5 | 25 | 173 | 1/1/3/0 | - |
| RULE-CONV-23 | Magic and spell procedures | 10 | 31 | 141 | 6/3/1/0 | - |
| RULE-CONV-24 | Magic and spell procedures 2 | 13 | 26 | 155 | 11/1/1/0 | - |
| RULE-CONV-25 | PHB/DMG downtime | 10 | 38 | 150 | 4/5/1/0 | - |
| RULE-CONV-26 | PHB/DMG downtime 2 | 2 | 6 | 0 | 2/0/0/0 | - |
| RULE-CONV-27 | XGE downtime | 6 | 27 | 443 | 0/3/3/0 | - |
| RULE-CONV-28 | XGE downtime 2 | 6 | 22 | 275 | 0/6/0/0 | - |
| RULE-CONV-29 | XGE downtime 3 | 1 | 7 | 99 | 0/0/1/0 | - |

## Batch implementation details

| Batch | Dominant existing patterns | Approach counts (A/B/C/D) | Main risk or blocker |
|---|---|---|---|
| RULE-CONV-01 | R-PAT-11, R-PAT-04 | 0/1/11/0 | Confirm source edition, exact references, and source-entry coverage. |
| RULE-CONV-02 | R-PAT-16, R-PAT-03, R-PAT-06, R-PAT-08, R-PAT-07 | 0/7/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-03 | R-PAT-16, R-PAT-06, R-PAT-04, R-PAT-03, R-PAT-08 | 11/7/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-04 | R-PAT-16, R-PAT-03, R-PAT-06, R-PAT-08 | 3/1/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-05 | R-PAT-07, R-PAT-13, R-PAT-18, R-PAT-09, R-PAT-21 | 0/6/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-06 | R-PAT-07, R-PAT-13, R-PAT-18, R-PAT-09, R-PAT-08 | 0/4/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-07 | R-PAT-16, R-PAT-03, R-PAT-08, R-PAT-06, R-PAT-13 | 0/6/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-08 | R-PAT-03, R-PAT-16, R-PAT-04, R-PAT-07, R-PAT-08 | 0/2/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-09 | R-PAT-07, R-PAT-13, R-PAT-19, R-PAT-21 | 0/0/0/1 | Dense or multi-procedure source needs full clause-by-clause interpretation. |
| RULE-CONV-10 | R-PAT-07, R-PAT-08, R-PAT-13, R-PAT-19, R-PAT-21 | 0/0/0/1 | Dense or multi-procedure source needs full clause-by-clause interpretation. |
| RULE-CONV-11 | R-PAT-03, R-PAT-05, R-PAT-07, R-PAT-08, R-PAT-14 | 0/0/0/1 | Dense or multi-procedure source needs full clause-by-clause interpretation. |
| RULE-CONV-12 | R-PAT-06, R-PAT-08, R-PAT-13, R-PAT-19, R-PAT-21 | 0/0/0/1 | Dense or multi-procedure source needs full clause-by-clause interpretation. |
| RULE-CONV-13 | R-PAT-03, R-PAT-07, R-PAT-08, R-PAT-09, R-PAT-13 | 0/0/0/1 | Dense or multi-procedure source needs full clause-by-clause interpretation. |
| RULE-CONV-14 | R-PAT-09, R-PAT-12, R-PAT-17 | 0/1/0/1 | Dense or multi-procedure source needs full clause-by-clause interpretation. |
| RULE-CONV-15 | R-PAT-14, R-PAT-08, R-PAT-02 | 3/2/0/0 | Confirm source edition, exact references, and source-entry coverage. |
| RULE-CONV-16 | R-PAT-08, R-PAT-01, R-PAT-15, R-PAT-07, R-PAT-06 | 1/10/1/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-17 | R-PAT-15, R-PAT-06, R-PAT-08, R-PAT-01, R-PAT-11 | 4/2/0/0 | Confirm source edition, exact references, and source-entry coverage. |
| RULE-CONV-18 | R-PAT-12, R-PAT-09, R-PAT-15, R-PAT-08, R-PAT-13 | 0/11/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-19 | R-PAT-13, R-PAT-02 | 4/4/0/0 | Confirm source edition, exact references, and source-entry coverage. |
| RULE-CONV-20 | R-PAT-08, R-PAT-02, R-PAT-13, R-PAT-01, R-PAT-07 | 0/2/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-21 | R-PAT-01, R-PAT-09, R-PAT-12 | 0/1/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-22 | R-PAT-08, R-PAT-02, R-PAT-12, R-PAT-19, R-PAT-01 | 0/4/0/1 | Dense or multi-procedure source needs full clause-by-clause interpretation. |
| RULE-CONV-23 | R-PAT-17, R-PAT-08, R-PAT-16, R-PAT-07, R-PAT-06 | 0/10/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-24 | R-PAT-17, R-PAT-08, R-PAT-01, R-PAT-02, R-PAT-12 | 2/11/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-25 | R-PAT-18, R-PAT-13, R-PAT-07, R-PAT-12 | 0/10/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-26 | R-PAT-18, R-PAT-07 | 0/2/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-27 | R-PAT-18, R-PAT-07 | 0/6/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-28 | R-PAT-18, R-PAT-07, R-PAT-09, R-PAT-13 | 0/6/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |
| RULE-CONV-29 | R-PAT-07, R-PAT-13, R-PAT-18 | 0/1/0/0 | Preserve deferred/skipped gaps; represent independently supported portions only. |

## Per-record conversion plan

Each Rule appears once. The immutable source file is `src/main/resources/data/variantrules.json` (`$.variantrule`). The companion JSON contains each identity, exact source path, entry-root paths, nested-path count, source metadata, discovery findings, dependencies, approach, complexity drivers, and batch assignment.

| Rule ID | Source path | Source | Roots | Nested paths | Fit | Patterns | Gaps | Approach | Complexity | Batch |
|---|---|---|---:|---:|---|---|---|---|---|---|
| rule:xphb:action | $.variantrule[2] | XPHB | 2 | 14 | PARTIAL | R-PAT-04, R-PAT-11 |  | B | MEDIUM | RULE-CONV-01 |
| rule:dmg:action-options | $.variantrule[3] | DMG | 7 | 22 | PARTIAL | R-PAT-03, R-PAT-06, R-PAT-07, R-PAT-16 |  | B | HIGH | RULE-CONV-02 |
| rule:xge:adamantine-weapons | $.variantrule[4] | XGE | 3 | 0 | PARTIAL | R-PAT-06, R-PAT-16 |  | B | LOW | RULE-CONV-03 |
| rule:xphb:adventure | $.variantrule[6] | XPHB | 1 | 0 | SUPPORTED | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:dmg:alien-technology | $.variantrule[7] | DMG | 4 | 14 | PARTIAL | R-PAT-07 |  | B | MEDIUM | RULE-CONV-16 |
| rule:xphb:alignment | $.variantrule[8] | XPHB | 1 | 0 | SUPPORTED | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:xphb:ally | $.variantrule[9] | XPHB | 1 | 0 | SUPPORTED | R-PAT-13 |  | A | LOW | RULE-CONV-19 |
| rule:dmg:alternatives-to-epic-boons | $.variantrule[10] | DMG | 2 | 7 | PARTIAL | R-PAT-12 |  | B | LOW | RULE-CONV-18 |
| rule:xphb:armor-training | $.variantrule[13] | XPHB | 1 | 0 | PARTIAL | R-PAT-09, R-PAT-17 |  | B | LOW | RULE-CONV-24 |
| rule:xphb:attitude | $.variantrule[15] | XPHB | 1 | 0 | SUPPORTED | R-PAT-13 |  | B | MEDIUM | RULE-CONV-19 |
| rule:xphb:attunement | $.variantrule[16] | XPHB | 1 | 0 | PARTIAL | R-PAT-06 |  | B | LOW | RULE-CONV-16 |
| rule:xphb:bonus-action | $.variantrule[18] | XPHB | 1 | 0 | SUPPORTED | R-PAT-04 |  | A | LOW | RULE-CONV-03 |
| rule:xphb:breaking-objects | $.variantrule[19] | XPHB | 6 | 58 | PARTIAL | R-PAT-06, R-PAT-16 | R-GAP-39 | B | MEDIUM | RULE-CONV-03 |
| rule:xphb:bright-light | $.variantrule[20] | XPHB | 1 | 0 | SUPPORTED | R-PAT-14 |  | A | LOW | RULE-CONV-15 |
| rule:xphb:campaign | $.variantrule[22] | XPHB | 1 | 0 | SUPPORTED | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:xphb:cantrip | $.variantrule[23] | XPHB | 1 | 0 | PARTIAL | R-PAT-17 |  | B | LOW | RULE-CONV-24 |
| rule:xphb:challenge-rating | $.variantrule[25] | XPHB | 1 | 0 | PARTIAL | R-PAT-08 |  | B | LOW | RULE-CONV-16 |
| rule:xphb:character-sheet | $.variantrule[26] | XPHB | 1 | 0 | SUPPORTED | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:dmg:cleaving-through-creatures | $.variantrule[27] | DMG | 2 | 0 | PARTIAL | R-PAT-16 | R-GAP-40 | B | LOW | RULE-CONV-02 |
| rule:xphb:condition | $.variantrule[30] | XPHB | 3 | 17 | PARTIAL | R-PAT-08 |  | B | LOW | RULE-CONV-16 |
| rule:aag:crashing | $.variantrule[33] | AAG | 4 | 12 | PARTIAL | R-PAT-03, R-PAT-16 | R-GAP-31 | B | MEDIUM | RULE-CONV-07 |
| rule:xphb:creature | $.variantrule[35] | XPHB | 1 | 0 | SUPPORTED | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:xphb:creature-type | $.variantrule[36] | XPHB | 3 | 16 | SUPPORTED | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:aag:crew | $.variantrule[37] | AAG | 2 | 0 | PARTIAL | R-PAT-08 | R-GAP-31 | B | LOW | RULE-CONV-07 |
| rule:xphb:curses | $.variantrule[40] | XPHB | 1 | 0 | PARTIAL | R-PAT-17 |  | B | LOW | RULE-CONV-24 |
| rule:phb:customizing-ability-scores | $.variantrule[41] | PHB | 5 | 13 | PARTIAL | R-PAT-12 |  | B | MEDIUM | RULE-CONV-18 |
| rule:tce:customizing-your-origin | $.variantrule[42] | TCE | 5 | 60 | PARTIAL | R-PAT-12, R-PAT-09 |  | B | MEDIUM | RULE-CONV-18 |
| rule:xphb:damage | $.variantrule[45] | XPHB | 1 | 0 | SUPPORTED | R-PAT-16 |  | A | LOW | RULE-CONV-03 |
| rule:xphb:damage-roll | $.variantrule[46] | XPHB | 1 | 0 | SUPPORTED | R-PAT-16 |  | A | LOW | RULE-CONV-03 |
| rule:xphb:damage-threshold | $.variantrule[47] | XPHB | 1 | 0 | SUPPORTED | R-PAT-16 |  | A | LOW | RULE-CONV-03 |
| rule:xphb:damage-types | $.variantrule[48] | XPHB | 2 | 32 | SUPPORTED | R-PAT-16 |  | A | LOW | RULE-CONV-03 |
| rule:xphb:darkness | $.variantrule[49] | XPHB | 1 | 0 | SUPPORTED | R-PAT-14 |  | A | LOW | RULE-CONV-15 |
| rule:xphb:dead | $.variantrule[50] | XPHB | 2 | 0 | PARTIAL | R-PAT-08, R-PAT-15 |  | B | LOW | RULE-CONV-16 |
| rule:xphb:dim-light | $.variantrule[55] | XPHB | 1 | 0 | SUPPORTED | R-PAT-14 |  | A | LOW | RULE-CONV-15 |
| rule:dmg:downtime-activity-building-a-stronghold | $.variantrule[57] | DMG | 4 | 35 | PARTIAL | R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-25 |
| rule:xge:downtime-activity-buying-a-magic-item | $.variantrule[58] | XGE | 6 | 85 | PARTIAL | R-PAT-18 | R-GAP-13 | B | HIGH | RULE-CONV-27 |
| rule:dmg:downtime-activity-carousing | $.variantrule[59] | DMG | 3 | 20 | PARTIAL | R-PAT-18 | R-GAP-13 | B | LOW | RULE-CONV-25 |
| rule:xge:downtime-activity-carousing | $.variantrule[60] | XGE | 4 | 105 | PARTIAL | R-PAT-18, R-PAT-07 | R-GAP-13 | B | HIGH | RULE-CONV-27 |
| rule:phb:downtime-activity-crafting | $.variantrule[61] | PHB | 4 | 0 | PARTIAL | R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-25 |
| rule:xge:downtime-activity-crafting-an-item | $.variantrule[63] | XGE | 5 | 115 | PARTIAL | R-PAT-18 | R-GAP-13 | B | HIGH | RULE-CONV-27 |
| rule:xge:downtime-activity-crime | $.variantrule[64] | XGE | 4 | 52 | PARTIAL | R-PAT-18, R-PAT-07 | R-GAP-13 | B | MEDIUM | RULE-CONV-27 |
| rule:dmg:downtime-activity-gaining-renown | $.variantrule[65] | DMG | 1 | 0 | PARTIAL | R-PAT-18 | R-GAP-13 | B | LOW | RULE-CONV-25 |
| rule:xge:downtime-activity-gambling | $.variantrule[66] | XGE | 4 | 43 | PARTIAL | R-PAT-18, R-PAT-07 | R-GAP-13 | B | MEDIUM | RULE-CONV-27 |
| rule:dmg:downtime-activity-performing-sacred-rites | $.variantrule[67] | DMG | 3 | 0 | PARTIAL | R-PAT-18 | R-GAP-13 | B | LOW | RULE-CONV-25 |
| rule:xge:downtime-activity-pit-fighting | $.variantrule[68] | XGE | 4 | 43 | PARTIAL | R-PAT-18, R-PAT-07 | R-GAP-13 | B | MEDIUM | RULE-CONV-27 |
| rule:phb:downtime-activity-practicing-a-profession | $.variantrule[69] | PHB | 3 | 0 | PARTIAL | R-PAT-18, R-PAT-13 | R-GAP-13 | B | LOW | RULE-CONV-25 |
| rule:xge:downtime-activity-religious-service | $.variantrule[72] | XGE | 4 | 42 | PARTIAL | R-PAT-18, R-PAT-07, R-PAT-13 | R-GAP-13 | B | MEDIUM | RULE-CONV-28 |
| rule:xge:downtime-activity-research | $.variantrule[73] | XGE | 4 | 45 | PARTIAL | R-PAT-18, R-PAT-07 | R-GAP-13 | B | MEDIUM | RULE-CONV-28 |
| rule:phb:downtime-activity-researching | $.variantrule[74] | PHB | 3 | 0 | PARTIAL | R-PAT-18, R-PAT-07 | R-GAP-13 | B | LOW | RULE-CONV-26 |
| rule:dmg:downtime-activity-running-a-business | $.variantrule[75] | DMG | 4 | 20 | PARTIAL | R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-25 |
| rule:xge:downtime-activity-scribing-a-spell-scroll | $.variantrule[76] | XGE | 3 | 64 | PARTIAL | R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-28 |
| rule:xge:downtime-activity-selling-a-magic-item | $.variantrule[77] | XGE | 4 | 57 | PARTIAL | R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-28 |
| rule:dmg:downtime-activity-selling-magic-items | $.variantrule[78] | DMG | 8 | 43 | PARTIAL | R-PAT-18 | R-GAP-13 | B | HIGH | RULE-CONV-25 |
| rule:dmg:downtime-activity-sowing-rumors | $.variantrule[79] | DMG | 5 | 12 | PARTIAL | R-PAT-18, R-PAT-07, R-PAT-13 | R-GAP-13 | B | MEDIUM | RULE-CONV-25 |
| rule:phb:downtime-activity-training | $.variantrule[80] | PHB | 3 | 0 | PARTIAL | R-PAT-18 | R-GAP-13 | B | LOW | RULE-CONV-26 |
| rule:xge:downtime-activity-training | $.variantrule[81] | XGE | 3 | 25 | PARTIAL | R-PAT-18, R-PAT-09 | R-GAP-13 | B | MEDIUM | RULE-CONV-28 |
| rule:dmg:downtime-activity-training-to-gain-levels | $.variantrule[82] | DMG | 3 | 20 | PARTIAL | R-PAT-18, R-PAT-12 | R-GAP-13 | B | MEDIUM | RULE-CONV-25 |
| rule:xge:downtime-activity-work | $.variantrule[83] | XGE | 4 | 42 | PARTIAL | R-PAT-18, R-PAT-07, R-PAT-09 | R-GAP-13 | B | MEDIUM | RULE-CONV-28 |
| rule:ai:downtime-and-franchise-activity-explore-territory | $.variantrule[84] | AI | 2 | 69 | PARTIAL | R-PAT-07, R-PAT-13, R-PAT-18, R-PAT-21 | R-GAP-13 | B | MEDIUM | RULE-CONV-05 |
| rule:ai:downtime-and-franchise-activity-franchise-restructuring | $.variantrule[85] | AI | 2 | 44 | PARTIAL | R-PAT-07, R-PAT-09, R-PAT-13, R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-05 |
| rule:ai:downtime-and-franchise-activity-headquarters-modification | $.variantrule[86] | AI | 3 | 44 | PARTIAL | R-PAT-07, R-PAT-09, R-PAT-13, R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-05 |
| rule:ai:downtime-and-franchise-activity-marketeering | $.variantrule[87] | AI | 2 | 54 | PARTIAL | R-PAT-07, R-PAT-09, R-PAT-13, R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-05 |
| rule:ai:downtime-and-franchise-activity-philanthropic-enterprise | $.variantrule[88] | AI | 3 | 48 | PARTIAL | R-PAT-07, R-PAT-13, R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-05 |
| rule:ai:downtime-and-franchise-activity-running-a-franchise | $.variantrule[89] | AI | 2 | 64 | PARTIAL | R-PAT-07, R-PAT-08, R-PAT-13, R-PAT-18, R-PAT-21 | R-GAP-13 | B | MEDIUM | RULE-CONV-05 |
| rule:ai:downtime-and-franchise-activity-schmoozing | $.variantrule[90] | AI | 2 | 46 | PARTIAL | R-PAT-07, R-PAT-13, R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-06 |
| rule:ai:downtime-and-franchise-activity-scrutineering | $.variantrule[91] | AI | 2 | 44 | PARTIAL | R-PAT-07, R-PAT-13, R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-06 |
| rule:ai:downtime-and-franchise-activity-shady-business-practice | $.variantrule[92] | AI | 2 | 53 | PARTIAL | R-PAT-07, R-PAT-09, R-PAT-13, R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-06 |
| rule:ai:downtime-and-franchise-activity-team-building | $.variantrule[93] | AI | 3 | 54 | PARTIAL | R-PAT-07, R-PAT-08, R-PAT-13, R-PAT-17, R-PAT-18 | R-GAP-13 | B | MEDIUM | RULE-CONV-06 |
| rule:xge:downtime-revisited | $.variantrule[94] | XGE | 7 | 99 | PARTIAL | R-PAT-07, R-PAT-13, R-PAT-18 | R-GAP-13 | B | HIGH | RULE-CONV-29 |
| rule:psi:emrakul-s-madness | $.variantrule[96] | PSI | 7 | 15 | PARTIAL | R-PAT-08, R-PAT-12, R-PAT-19 | R-GAP-26, R-GAP-27 | B | HIGH | RULE-CONV-22 |
| rule:xphb:encounter | $.variantrule[97] | XPHB | 1 | 0 | SUPPORTED | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:gos:encounters-at-sea | $.variantrule[98] | GoS | 2 | 162 | PARTIAL | R-PAT-07, R-PAT-13, R-PAT-19, R-PAT-21 | R-GAP-31, R-GAP-28 | D | HIGH | RULE-CONV-09 |
| rule:xphb:enemy | $.variantrule[100] | XPHB | 1 | 0 | SUPPORTED | R-PAT-13 |  | A | LOW | RULE-CONV-19 |
| rule:phb:equipment-sizes | $.variantrule[101] | PHB | 3 | 0 | PARTIAL | R-PAT-06 |  | B | LOW | RULE-CONV-16 |
| rule:xphb:experience-points | $.variantrule[102] | XPHB | 1 | 0 | PARTIAL | R-PAT-12 |  | B | LOW | RULE-CONV-18 |
| rule:dmg:explosives | $.variantrule[104] | DMG | 5 | 17 | PARTIAL | R-PAT-16, R-PAT-17 |  | B | MEDIUM | RULE-CONV-23 |
| rule:dmg:facing | $.variantrule[105] | DMG | 6 | 0 | PARTIAL | R-PAT-03 | R-GAP-14 | B | MEDIUM | RULE-CONV-02 |
| rule:mm:familiars | $.variantrule[107] | MM | 2 | 0 | PARTIAL | R-PAT-17 |  | B | LOW | RULE-CONV-23 |
| rule:vgm:familiars | $.variantrule[108] | VGM | 2 | 0 | PARTIAL | R-PAT-17 |  | B | LOW | RULE-CONV-23 |
| rule:dmg:fear-and-horror | $.variantrule[109] | DMG | 3 | 7 | PARTIAL | R-PAT-01, R-PAT-08 |  | B | LOW | RULE-CONV-16 |
| rule:vrgr:fear-and-stress | $.variantrule[110] | VRGR | 4 | 65 | PARTIAL | R-PAT-01, R-PAT-02, R-PAT-08, R-PAT-15 | R-GAP-16 | B | MEDIUM | RULE-CONV-16 |
| rule:phb:feats | $.variantrule[111] | PHB | 4 | 0 | PARTIAL | R-PAT-12 |  | B | MEDIUM | RULE-CONV-18 |
| rule:dmg:firearms | $.variantrule[112] | DMG | 3 | 15 | PARTIAL | R-PAT-06, R-PAT-16 |  | B | MEDIUM | RULE-CONV-02 |
| rule:xphb:friendly-attitude | $.variantrule[116] | XPHB | 1 | 0 | PARTIAL | R-PAT-13, R-PAT-02 |  | B | LOW | RULE-CONV-19 |
| rule:xphb:grappling | $.variantrule[117] | XPHB | 2 | 10 | PARTIAL | R-PAT-03, R-PAT-08 |  | B | LOW | RULE-CONV-03 |
| rule:vrgr:haunted-traps | $.variantrule[118] | VRGR | 4 | 17 | PARTIAL | R-PAT-01, R-PAT-04, R-PAT-08, R-PAT-14, R-PAT-17, R-PAT-21 | R-GAP-29 | B | MEDIUM | RULE-CONV-23 |
| rule:xphb:hazard | $.variantrule[119] | XPHB | 1 | 0 | PARTIAL | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:xphb:healing | $.variantrule[121] | XPHB | 1 | 0 | SUPPORTED | R-PAT-15 |  | A | LOW | RULE-CONV-16 |
| rule:xphb:heavily-obscured | $.variantrule[122] | XPHB | 1 | 0 | PARTIAL | R-PAT-14, R-PAT-08 |  | B | LOW | RULE-CONV-15 |
| rule:dmg:hero-points | $.variantrule[123] | DMG | 4 | 0 | PARTIAL | R-PAT-01, R-PAT-08 | R-GAP-46 | B | MEDIUM | RULE-CONV-16 |
| rule:xphb:heroic-inspiration | $.variantrule[124] | XPHB | 2 | 0 | PARTIAL | R-PAT-02, R-PAT-08 | R-GAP-25 | B | LOW | RULE-CONV-22 |
| rule:xphb:hit-point-dice | $.variantrule[126] | XPHB | 1 | 0 | SUPPORTED | R-PAT-15 |  | A | LOW | RULE-CONV-17 |
| rule:xphb:hit-points | $.variantrule[127] | XPHB | 1 | 0 | SUPPORTED | R-PAT-16 |  | A | LOW | RULE-CONV-03 |
| rule:xphb:hostile-attitude | $.variantrule[129] | XPHB | 1 | 0 | PARTIAL | R-PAT-13, R-PAT-02 |  | B | LOW | RULE-CONV-19 |
| rule:scag:human-languages | $.variantrule[131] | SCAG | 3 | 59 | PARTIAL | R-PAT-11 |  | C | LOW | RULE-CONV-16 |
| rule:xphb:illusions | $.variantrule[132] | XPHB | 2 | 0 | PARTIAL | R-PAT-17 |  | B | LOW | RULE-CONV-24 |
| rule:xphb:immunity | $.variantrule[133] | XPHB | 1 | 0 | SUPPORTED | R-PAT-16, R-PAT-08 |  | A | LOW | RULE-CONV-03 |
| rule:xphb:improvised-weapons | $.variantrule[134] | XPHB | 2 | 13 | PARTIAL | R-PAT-06, R-PAT-16 |  | B | MEDIUM | RULE-CONV-03 |
| rule:xphb:indifferent-attitude | $.variantrule[135] | XPHB | 1 | 0 | SUPPORTED | R-PAT-13 |  | A | LOW | RULE-CONV-19 |
| rule:dmg:injuries | $.variantrule[138] | DMG | 6 | 28 | PARTIAL | R-PAT-08, R-PAT-16 | R-GAP-23 | B | MEDIUM | RULE-CONV-02 |
| rule:dmg:inspiration | $.variantrule[139] | DMG | 6 | 44 | PARTIAL | R-PAT-02, R-PAT-08, R-PAT-13 | R-GAP-25 | B | MEDIUM | RULE-CONV-20 |
| rule:phb:inspiration | $.variantrule[140] | PHB | 3 | 8 | PARTIAL | R-PAT-02, R-PAT-08 | R-GAP-25 | B | MEDIUM | RULE-CONV-22 |
| rule:xphb:knocking-out-a-creature | $.variantrule[142] | XPHB | 2 | 0 | PARTIAL | R-PAT-16, R-PAT-08 |  | B | LOW | RULE-CONV-03 |
| rule:dmg:level-advancement-without-xp | $.variantrule[143] | DMG | 4 | 6 | PARTIAL | R-PAT-12 |  | B | MEDIUM | RULE-CONV-18 |
| rule:xphb:lightly-obscured | $.variantrule[144] | XPHB | 1 | 0 | PARTIAL | R-PAT-14, R-PAT-02 |  | B | LOW | RULE-CONV-15 |
| rule:dmg:loyalty | $.variantrule[148] | DMG | 3 | 10 | PARTIAL | R-PAT-13 |  | B | LOW | RULE-CONV-19 |
| rule:dmg:madness | $.variantrule[149] | DMG | 4 | 99 | PARTIAL | R-PAT-01, R-PAT-07, R-PAT-08, R-PAT-17, R-PAT-19 | R-GAP-24 | B | HIGH | RULE-CONV-20 |
| rule:xphb:magical-effect | $.variantrule[150] | XPHB | 1 | 0 | SUPPORTED | R-PAT-17 |  | A | LOW | RULE-CONV-24 |
| rule:dmg:massive-damage | $.variantrule[151] | DMG | 3 | 16 | PARTIAL | R-PAT-16, R-PAT-01 | R-GAP-42 | B | LOW | RULE-CONV-02 |
| rule:dmg:milestones | $.variantrule[152] | DMG | 4 | 8 | PARTIAL | R-PAT-12, R-PAT-15 |  | B | MEDIUM | RULE-CONV-18 |
| rule:dmg:mixing-potions | $.variantrule[153] | DMG | 3 | 22 | PARTIAL | R-PAT-16, R-PAT-17 | R-GAP-43 | B | MEDIUM | RULE-CONV-23 |
| rule:xphb:monster | $.variantrule[154] | XPHB | 1 | 0 | SUPPORTED | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:dmg:morale | $.variantrule[155] | DMG | 8 | 8 | PARTIAL | R-PAT-01, R-PAT-13 | R-GAP-44 | B | HIGH | RULE-CONV-22 |
| rule:dmg:more-difficult-identification | $.variantrule[156] | DMG | 1 | 0 | PARTIAL | R-PAT-17 |  | B | LOW | RULE-CONV-23 |
| rule:gos:mysterious-islands | $.variantrule[158] | GoS | 4 | 417 | PARTIAL | R-PAT-07, R-PAT-08, R-PAT-13, R-PAT-19, R-PAT-21 | R-GAP-28 | D | VERY_HIGH | RULE-CONV-10 |
| rule:dmg:new-ability-scores-honor-and-sanity | $.variantrule[159] | DMG | 7 | 46 | UNSUPPORTED | R-PAT-01, R-PAT-09, R-PAT-12 | R-GAP-26 | B | HIGH | RULE-CONV-21 |
| rule:xphb:nonplayer-character | $.variantrule[160] | XPHB | 1 | 0 | SUPPORTED | R-PAT-13 |  | A | LOW | RULE-CONV-19 |
| rule:xphb:object | $.variantrule[161] | XPHB | 1 | 0 | SUPPORTED | R-PAT-06 |  | A | LOW | RULE-CONV-17 |
| rule:xphb:occupied-space | $.variantrule[162] | XPHB | 1 | 0 | SUPPORTED | R-PAT-03, R-PAT-06 |  | A | LOW | RULE-CONV-03 |
| rule:gos:ocean-environs | $.variantrule[163] | GoS | 14 | 349 | PARTIAL | R-PAT-03, R-PAT-05, R-PAT-07, R-PAT-08, R-PAT-14, R-PAT-16, R-PAT-17, R-PAT-20, R-PAT-21 | R-GAP-31 | D | VERY_HIGH | RULE-CONV-11 |
| rule:gos:officers-and-crew | $.variantrule[164] | GoS | 3 | 27 | PARTIAL | R-PAT-07, R-PAT-08, R-PAT-09, R-PAT-13, R-PAT-20 | R-GAP-31 | B | MEDIUM | RULE-CONV-07 |
| rule:tce:optional-class-features | $.variantrule[165] | TCE | 3 | 0 | PARTIAL | R-PAT-12 |  | B | LOW | RULE-CONV-18 |
| rule:xphb:per-day | $.variantrule[167] | XPHB | 1 | 0 | PARTIAL | R-PAT-08, R-PAT-15 |  | B | LOW | RULE-CONV-17 |
| rule:xphb:player-character | $.variantrule[169] | XPHB | 1 | 0 | SUPPORTED | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:mm:player-characters-as-lycanthropes | $.variantrule[170] | MM | 4 | 29 | PARTIAL | R-PAT-08, R-PAT-16 |  | B | MEDIUM | RULE-CONV-02 |
| rule:mm:player-characters-as-vampires | $.variantrule[171] | MM | 2 | 4 | PARTIAL | R-PAT-08, R-PAT-17 |  | B | LOW | RULE-CONV-23 |
| rule:dmg:plot-points | $.variantrule[173] | DMG | 5 | 18 | PARTIAL | R-PAT-08, R-PAT-12 |  | B | MEDIUM | RULE-CONV-18 |
| rule:xphb:possession | $.variantrule[174] | XPHB | 1 | 0 | PARTIAL | R-PAT-17, R-PAT-08 |  | B | LOW | RULE-CONV-24 |
| rule:cos:prophet-s-gambit | $.variantrule[177] | CoS | 5 | 92 | UNSUPPORTED | R-PAT-07, R-PAT-08 | R-GAP-22 | B | HIGH | RULE-CONV-16 |
| rule:gos:random-ships | $.variantrule[178] | GoS | 6 | 353 | PARTIAL | R-PAT-06, R-PAT-08, R-PAT-13, R-PAT-19, R-PAT-21 | R-GAP-31, R-GAP-28 | D | VERY_HIGH | RULE-CONV-12 |
| rule:xphb:reach | $.variantrule[179] | XPHB | 1 | 0 | SUPPORTED | R-PAT-03 |  | A | LOW | RULE-CONV-03 |
| rule:xphb:reaction | $.variantrule[180] | XPHB | 1 | 0 | PARTIAL | R-PAT-04 |  | B | LOW | RULE-CONV-03 |
| rule:dmg:renown | $.variantrule[181] | DMG | 6 | 76 | PARTIAL | R-PAT-13, R-PAT-12, R-PAT-18 |  | B | MEDIUM | RULE-CONV-18 |
| rule:xphb:ritual | $.variantrule[184] | XPHB | 1 | 0 | PARTIAL | R-PAT-17 |  | B | LOW | RULE-CONV-24 |
| rule:xphb:round-down | $.variantrule[185] | XPHB | 1 | 0 | SUPPORTED | R-PAT-01 |  | A | LOW | RULE-CONV-17 |
| rule:dmg:scroll-mishaps | $.variantrule[188] | DMG | 2 | 18 | PARTIAL | R-PAT-17, R-PAT-07 |  | B | LOW | RULE-CONV-23 |
| rule:xphb:shape-shifting | $.variantrule[189] | XPHB | 1 | 0 | PARTIAL | R-PAT-08, R-PAT-17 |  | B | LOW | RULE-CONV-24 |
| rule:xge:shared-campaign-variant-rules | $.variantrule[190] | XGE | 5 | 142 | UNSUPPORTED | R-PAT-06, R-PAT-08, R-PAT-12, R-PAT-19 | R-GAP-33 | D | HIGH | RULE-CONV-22 |
| rule:aag:ship-repairs | $.variantrule[191] | AAG | 2 | 0 | PARTIAL | R-PAT-16, R-PAT-17 | R-GAP-31 | B | LOW | RULE-CONV-07 |
| rule:gos:ship-stat-blocks | $.variantrule[192] | GoS | 5 | 60 | PARTIAL | R-PAT-03, R-PAT-06, R-PAT-08, R-PAT-16 | R-GAP-31 | B | MEDIUM | RULE-CONV-07 |
| rule:aag:ship-to-ship-combat | $.variantrule[193] | AAG | 5 | 29 | PARTIAL | R-PAT-03, R-PAT-04, R-PAT-06, R-PAT-13, R-PAT-16 | R-GAP-31 | B | MEDIUM | RULE-CONV-07 |
| rule:gos:ships-in-combat | $.variantrule[194] | GoS | 5 | 41 | PARTIAL | R-PAT-03, R-PAT-04, R-PAT-07, R-PAT-08, R-PAT-16, R-PAT-20 | R-GAP-31 | B | MEDIUM | RULE-CONV-08 |
| rule:esk:sidekicks | $.variantrule[196] | ESK | 8 | 120 | PARTIAL | R-PAT-09, R-PAT-12, R-PAT-17 | R-GAP-34 | D | VERY_HIGH | RULE-CONV-14 |
| rule:tce:sidekicks | $.variantrule[197] | TCE | 5 | 32 | PARTIAL | R-PAT-09, R-PAT-12, R-PAT-17 | R-GAP-34 | B | MEDIUM | RULE-CONV-14 |
| rule:xge:simultaneous-effects | $.variantrule[198] | XGE | 1 | 0 | SUPPORTED | R-PAT-04 |  | A | LOW | RULE-CONV-03 |
| rule:xphb:simultaneous-effects | $.variantrule[199] | XPHB | 1 | 0 | SUPPORTED | R-PAT-04 |  | A | LOW | RULE-CONV-03 |
| rule:xphb:size | $.variantrule[200] | XPHB | 1 | 0 | PARTIAL | R-PAT-08, R-PAT-03 |  | B | LOW | RULE-CONV-03 |
| rule:xphb:spell | $.variantrule[206] | XPHB | 1 | 0 | SUPPORTED | R-PAT-17 |  | A | LOW | RULE-CONV-24 |
| rule:dmg:spell-points | $.variantrule[208] | DMG | 9 | 63 | PARTIAL | R-PAT-17, R-PAT-08 |  | B | HIGH | RULE-CONV-23 |
| rule:xge:spellcasting | $.variantrule[209] | XGE | 5 | 58 | PARTIAL | R-PAT-01, R-PAT-02, R-PAT-05, R-PAT-17 | R-GAP-35, R-GAP-36 | B | MEDIUM | RULE-CONV-24 |
| rule:xphb:spellcasting-focus | $.variantrule[210] | XPHB | 1 | 0 | PARTIAL | R-PAT-17, R-PAT-06 |  | B | LOW | RULE-CONV-24 |
| rule:xphb:stable | $.variantrule[212] | XPHB | 1 | 0 | SUPPORTED | R-PAT-08, R-PAT-16 |  | A | LOW | RULE-CONV-04 |
| rule:xphb:stat-block | $.variantrule[213] | XPHB | 2 | 61 | PARTIAL | R-PAT-08, R-PAT-11 |  | B | MEDIUM | RULE-CONV-17 |
| rule:gos:superior-ship-upgrades | $.variantrule[214] | GoS | 7 | 42 | PARTIAL | R-PAT-03, R-PAT-06, R-PAT-16, R-PAT-17 | R-GAP-31 | B | HIGH | RULE-CONV-08 |
| rule:vrgr:survivors | $.variantrule[215] | VRGR | 7 | 97 | PARTIAL | R-PAT-01, R-PAT-02, R-PAT-08, R-PAT-12, R-PAT-17 | R-GAP-37 | B | HIGH | RULE-CONV-24 |
| rule:xphb:target | $.variantrule[218] | XPHB | 1 | 0 | SUPPORTED | R-PAT-11 |  | C | LOW | RULE-CONV-01 |
| rule:xphb:telepathy | $.variantrule[219] | XPHB | 3 | 0 | PARTIAL | R-PAT-17 |  | B | LOW | RULE-CONV-24 |
| rule:xphb:teleportation | $.variantrule[220] | XPHB | 4 | 0 | PARTIAL | R-PAT-03, R-PAT-06 | R-GAP-21 | B | MEDIUM | RULE-CONV-04 |
| rule:xphb:temporary-hit-points | $.variantrule[221] | XPHB | 1 | 0 | SUPPORTED | R-PAT-16 |  | A | LOW | RULE-CONV-04 |
| rule:gos:travel-at-sea | $.variantrule[223] | GoS | 4 | 307 | PARTIAL | R-PAT-03, R-PAT-07, R-PAT-08, R-PAT-09, R-PAT-13, R-PAT-14, R-PAT-16, R-PAT-20, R-PAT-21 | R-GAP-31, R-GAP-28 | D | VERY_HIGH | RULE-CONV-13 |
| rule:xge:tying-knots | $.variantrule[224] | XGE | 3 | 0 | PARTIAL | R-PAT-01, R-PAT-09 | R-GAP-19 | B | LOW | RULE-CONV-18 |
| rule:xphb:unoccupied-space | $.variantrule[226] | XPHB | 1 | 0 | SUPPORTED | R-PAT-03, R-PAT-06 |  | A | LOW | RULE-CONV-04 |
| rule:dmg:wands-that-don-t-recharge | $.variantrule[228] | DMG | 1 | 0 | PARTIAL | R-PAT-06, R-PAT-17 |  | B | LOW | RULE-CONV-23 |
| rule:xphb:weapon | $.variantrule[229] | XPHB | 1 | 0 | SUPPORTED | R-PAT-06 |  | A | LOW | RULE-CONV-17 |

## Highest complexity

- rule:gos:ocean-environs - 14 roots, 349 nested paths; RULE-CONV-11.
- rule:gos:travel-at-sea - 4 roots, 307 nested paths; RULE-CONV-13.
- rule:gos:random-ships - 6 roots, 353 nested paths; RULE-CONV-12.
- rule:gos:mysterious-islands - 4 roots, 417 nested paths; RULE-CONV-10.
- rule:esk:sidekicks - 8 roots, 120 nested paths; RULE-CONV-14.

The main concentrated workloads are the GoS maritime scenario and subsystem records, ESK Sidekicks, XGE Downtime Revisited and Spellcasting, DMG Madness and Honor/Sanity, VRGR Survivors, and CoS Prophets Gambit. These remain source-level interpretation tasks; counts alone do not determine their mechanics.

## High-complexity records

- `rule:cos:prophet-s-gambit` - 5 roots, 92 nested paths; RULE-CONV-16.
- `rule:dmg:action-options` - 7 roots, 22 nested paths; RULE-CONV-02.
- `rule:dmg:downtime-activity-selling-magic-items` - 8 roots, 43 nested paths; RULE-CONV-25.
- `rule:dmg:madness` - 4 roots, 99 nested paths; RULE-CONV-20.
- `rule:dmg:morale` - 8 roots, 8 nested paths; RULE-CONV-22.
- `rule:dmg:new-ability-scores-honor-and-sanity` - 7 roots, 46 nested paths; RULE-CONV-21.
- `rule:dmg:spell-points` - 9 roots, 63 nested paths; RULE-CONV-23.
- `rule:gos:encounters-at-sea` - 2 roots, 162 nested paths; RULE-CONV-09.
- `rule:gos:superior-ship-upgrades` - 7 roots, 42 nested paths; RULE-CONV-08.
- `rule:psi:emrakul-s-madness` - 7 roots, 15 nested paths; RULE-CONV-22.
- `rule:vrgr:survivors` - 7 roots, 97 nested paths; RULE-CONV-24.
- `rule:xge:downtime-activity-buying-a-magic-item` - 6 roots, 85 nested paths; RULE-CONV-27.
- `rule:xge:downtime-activity-carousing` - 4 roots, 105 nested paths; RULE-CONV-27.
- `rule:xge:downtime-activity-crafting-an-item` - 5 roots, 115 nested paths; RULE-CONV-27.
- `rule:xge:downtime-revisited` - 7 roots, 99 nested paths; RULE-CONV-29.
- `rule:xge:shared-campaign-variant-rules` - 5 roots, 142 nested paths; RULE-CONV-22.

## Dependencies and risk controls

Pending Rule-to-Rule dependencies are ordered target before consumer using the completed semantic inventory. Strongly connected references are placed in one batch. The resulting batch dependency graph is acyclic. Dependencies on already accounted and external domain records remain per-record validation obligations. Per-record gap dispositions distinguish gap families already implemented in Batches 1-4, deferred families, and skipped/omitted families. Deferred and skipped gap families stay out of scope; independently supported portions may still be converted.

For every batch: verify ID and source path; inspect the complete source object; use approved schemas only; preserve exact references and editions; account roots and nested clauses as INTERPRETED, DUPLICATE, or OMITTED; update registers; classify coverage from actual represented mechanics; add source-backed assertions; reconcile manifest totals; run npm run validate:core-rules and git diff --check. Stop at a validated checkpoint for human review.

Before Batch 1, check whether the current validator supports incomplete Rule catalogs at interim checkpoints. If not, propose a narrow checkpoint-aware validation adjustment rather than weakening completeness checks.

## Manifest field discrepancy

ruleCatalogSummary.pendingSemanticReviewCount is 164, but the semantic inventory says 231 reviewed and 0 pending. The value matches pending conversion accounting. In a separately authorized manifest correction, rename it to pendingConversionAccountingCount and report semanticPendingReviewCount as 0. This planning task leaves the manifest unchanged.

## Recommended next step

After explicit human approval, implement only RULE-CONV-01 - Glossary and definitions (12 records; 15 roots; 30 nested paths).



