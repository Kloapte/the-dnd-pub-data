# Step 2 Phase 5 — Rule Conversion Batch 01

## A. Baseline and final checkpoint

RULE-CONV-01 converted the 12 identities assigned to the Glossary and Definitions batch. The verified starting checkpoint matched the approved plan.

| Metric | Before | After |
|---|---:|---:|
| Rule source identities | 231 | 231 |
| Semantically reviewed identities | 231 | 231 |
| Semantic review pending | 0 | 0 |
| Conversion-accounted identities | 67 | 79 |
| Conversion pending | 164 | 152 |
| Canonical Rule outputs, including five Phase 3/4 representatives | 63 | 75 |
| Phase 5 canonical outputs across all domains | 383 | 395 |
| Permanent validation checks | 638 | 652 |

The conversion accounting is now 57 FULL, 17 PARTIAL, and 5 UNSUPPORTED across the 79 accounted Rule identities. The Phase 5 Rule batch alone has 53 FULL, 16 PARTIAL, and 5 UNSUPPORTED records across 74 examined records, excluding the five representatives. Its 70 canonical outputs include the 12 new outputs.

The approved plan's structural source-entry totals reconcile to 15 roots and 30 nested clause paths. The existing Rule accounting convention records nested `entries`/`items` nodes in `nestedSourcePaths` (26 such nodes here); the four scalar list-format fields (`type` and `style` for two lists) are separately classified in the entry portions. All 30 plan paths are therefore accounted without changing the established manifest path convention.

## B. Per-record conversion results

All 12 outputs are reference-only: they have canonical identity, source provenance, and no `mechanics` property. Seven include exact source-qualified navigation references; none invents an executable relationship model. FULL indicates that no source mechanic is omitted; descriptive definitions may still be recorded externally as `NON_MECHANICAL`.

| Canonical ID | Source path | Planned approach | Actual approach / coverage | Canonical references | Roots / nested paths | Accounting portions (I/D/O) |
|---|---|---|---|---|---:|---:|
| `rule:xphb:action` | `$.variantrule[2]` | Mixed | Reference-only / PARTIAL | 11 resolved XPHB Actions; XPHB Dodge unresolved | 2 / 14 | 12 / 0 / 4 |
| `rule:xphb:adventure` | `$.variantrule[6]` | Reference-only | Reference-only / FULL | `rule:xphb:encounter` | 1 / 0 | 1 / 0 / 1 |
| `rule:xphb:alignment` | `$.variantrule[8]` | Reference-only | Reference-only / FULL | — | 1 / 0 | 0 / 0 / 1 |
| `rule:xphb:campaign` | `$.variantrule[22]` | Reference-only | Reference-only / FULL | `rule:xphb:adventure` | 1 / 0 | 1 / 0 / 1 |
| `rule:xphb:character-sheet` | `$.variantrule[26]` | Reference-only | Reference-only / FULL | — | 1 / 0 | 0 / 0 / 1 |
| `rule:xphb:creature` | `$.variantrule[35]` | Reference-only | Reference-only / FULL | `rule:xphb:player-character` | 1 / 0 | 1 / 0 / 1 |
| `rule:xphb:creature-type` | `$.variantrule[36]` | Reference-only | Reference-only / PARTIAL | `rule:xphb:player-character` | 3 / 16 | 1 / 0 / 19 |
| `rule:xphb:encounter` | `$.variantrule[97]` | Reference-only | Reference-only / FULL | — | 1 / 0 | 0 / 0 / 1 |
| `rule:xphb:hazard` | `$.variantrule[119]` | Reference-only | Reference-only / UNSUPPORTED | No canonical Hazard target exists | 1 / 0 | 0 / 0 / 1 |
| `rule:xphb:monster` | `$.variantrule[154]` | Reference-only | Reference-only / FULL | `rule:xphb:creature` | 1 / 0 | 1 / 0 / 1 |
| `rule:xphb:player-character` | `$.variantrule[169]` | Reference-only | Reference-only / FULL | — | 1 / 0 | 0 / 0 / 1 |
| `rule:xphb:target` | `$.variantrule[218]` | Reference-only | Reference-only / PARTIAL | `rule:xphb:attack-roll`, `rule:xphb:saving-throw` | 1 / 0 | 1 / 0 / 1 |
| **Batch total** |  |  | **8 FULL / 3 PARTIAL / 1 UNSUPPORTED** | **18 exact references** | **15 / 30** | **18 / 0 / 33** |

### Interpretation and omissions

- **Action:** The source says a creature can take one Action on its turn, allows feature-provided Actions, and lists 12 XPHB Actions. Eleven links resolve to XPHB canonical Action identities. The missing XPHB Dodge output is recorded as `REFERENCE_UNRESOLVED`; PHB Dodge was not substituted. The turn action economy and feature-granted Actions are `SCHEMA_UNSUPPORTED` for this glossary contract.
- **Creature Type:** The exact Player Character link is retained. Creature tags, the 14 listed tag values, and type-dependent applicability are deferred to the Creature domain. The source list's presentation fields are classified `NON_MECHANICAL`; type taxonomy portions are `EXTERNAL_DEPENDENCY`.
- **Target:** Attack Roll and Saving Throw Rule references are retained. The broader creature/object target-selection semantics and Spell/phenomenon applicability remain unrepresented under the approved Rule contract.
- **Hazard:** The source links to an external trap/hazard page without an exact canonical Hazard identity. That link is recorded `REFERENCE_UNRESOLVED`; no placeholder reference or Hazard mechanics were created.
- The remaining entries define or describe glossary concepts. Their descriptive portions are accounted as `NON_MECHANICAL`; they do not warrant new Rule mechanics or a generic definition structure.

## C. Contract and reference review

No schema changes were needed. The existing Rule v1 contract supports reference-only records with absent `mechanics`, required source identity/provenance, `ruleType`, and typed references. Empty mechanics were not used.

Every reference in the new canonical outputs resolves to an existing canonical output. The 11 Action references resolve to XPHB Actions. The XPHB Dodge source item does not: only a PHB Dodge record exists, so it remains omitted rather than crossing editions. All internal Rule links among the 12 glossary records resolve; the other Rule targets resolve to existing Rule outputs. The external Hazard page has no canonical target.

No new Rule records were converted through these references. No deferred gap family or other domain was expanded.

## D. Source accounting

The Phase 5 Rule accounting now covers 74 non-representative Rule identities and 167 source-entry roots; five Phase 3/4 representatives remain separate. Rule source portions are classified as 108 INTERPRETED, 4 DUPLICATE, and 101 OMITTED. This batch contributes 18 INTERPRETED, 0 DUPLICATE, and 33 OMITTED portions. The complete original source entry is preserved in `doc/rule/omitted.md` wherever an omission exists; no duplicate entry was found. All 12 inventory records now carry a canonical output and FULL/PARTIAL/UNSUPPORTED status.

The global Phase 5 manifest now accounts for 402 source records across domains, 443 roots, and 312 INTERPRETED, 28 DUPLICATE, and 291 OMITTED portions. It contains 395 canonical outputs and coverage totals of 232 FULL, 162 PARTIAL, and 8 UNSUPPORTED.

The legacy field `ruleCatalogSummary.pendingSemanticReviewCount` remains 164 exactly as instructed. It conflicts with the separately verified semantic-discovery inventory (231 reviewed, zero pending) and now also differs from the current conversion-pending count of 152. This field-name/value correction remains separately pending human authorization; it was not silently altered.

## E. Validation

`npm run validate:core-rules` passes:

- 9 Draft 2020-12 schemas compiled.
- 89 schema examples validated.
- 34 Phase 3/4 canonical records validated.
- 395 Phase 5 canonical records validated.
- 652 permanent checks passed (14 added for RULE-CONV-01 identity, provenance, reference, source-path, and coverage assertions).
- 49 Rule example components and 27 Action records checked.

The new assertions verify the exact 12-ID batch, no additional converted Rule identity, all outputs remain mechanics-free and schema-valid, exact edition/source metadata, all 30 semantic inventory nested paths accounted, canonical references resolve, Action Dodge remains unresolved rather than misreferenced, and coverage is 8/3/1. Structural and deterministic checks do not by themselves establish semantic correctness.

## F. Remaining work

- **152 Rule identities remain pending conversion accounting.** Semantic discovery is complete for all 231; these are not semantic-review tasks.
- RULE-CONV-01 leaves Action turn-economy behavior, Creature Type taxonomy/applicability, and Target selection partly unsupported. XPHB Dodge and the external Hazard target remain unresolved references.
- The legacy `pendingSemanticReviewCount` field discrepancy is separately pending correction.
- No later conversion batch has begun. Phase 5 remains open and unapproved.
