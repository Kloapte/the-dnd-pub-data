# Step 2 Phase 5 — Core Rules Foundation Completion Audit

## Remediation and closure update

The audit blockers below have been resolved under the authorized remediation. All 165 Phase 5 source-field issue entries were checked against their pinned source object, identity, field, and value. The Planar Effects `reprintedAs` value was corrected to preserve the source's one-element array shape, and `sourceFieldIssueCount` is now 165. A permanent validator assertion verifies the count and rechecks every issue against the immutable source catalog. Validation now passes with 838 permanent checks.

Phase 5 was formally marked complete and approved in `ROADMAP.md` under the user's explicit authorization. Factual output/accounting completion and formal milestone approval remain stated separately. See [the Phase 5 closure report](CORE_RULES_FOUNDATION_PHASE_5_CLOSURE.md). The original audit findings below are retained as evidence of the pre-remediation checkpoint.

## Audit scope and method

This read-only audit checked the immutable source catalogs, the Phase 3 and Phase 5 source-accounting manifests, all canonical outputs under `datasets/phase-3/step-2/core-rules/v1/` and `datasets/phase-5/step-2/core-rules/v1/`, the eight domain schemas plus shared schema, the permanent validator, `BOUNDARIES.md`, `ROADMAP.md`, `doc/CORE_RULES.md`, and the current domain and batch reports. No existing project artifact was changed. This report is the only file created by the audit.

Source counts below were recalculated from the pinned JSON catalogs. Output and disposition counts were checked against manifests and file trees, then the permanent validation command was run. Semantic correctness is assessed from the source-backed domain and batch reports; automated validation is not treated as proof of semantic correctness.

## Domain-by-domain completion matrix

| Domain | Source inventory: total / in scope / excluded | Semantic review | Canonical output coverage | Mechanical coverage (FULL / PARTIAL / UNSUPPORTED) | Accounting and reference assessment |
|---|---:|---|---|---:|---|
| Action | 48 / 48 / 0 | All 48 reviewed across the representative baseline and expansion. | 46 outputs; 2 identities have explicit unsupported dispositions and full entry accounting, but no shells. | 31 / 15 / 2 | 97 entry roots across Phase 3 and 5 are accounted. Typed references validate against pinned source identities. Healing Surge depends on character Hit Die state; Waking Someone depends on natural-sleep and encounter state. |
| Rule | 231 / 231 / 0 | All 231 reviewed; manifest explicitly reports 231 complete, 0 pending. | 231 outputs, including the four recovered by reconciliation. | 68 / 90 / 73 | 648 entry roots are accounted. All canonical references validate against pinned source identities. Eleven unresolved reprint/source-field targets remain outside canonical references. |
| Skill | 36 / 36 / 0 | All 36 reviewed. | 36 outputs. | 36 / 0 / 0 | 38 entry roots accounted. Skill-to-ability and other represented relationships are source-backed; no unresolved canonical references reported. |
| Condition | 30 / 30 / 0 | All 30 reviewed, including both editions. | 30 outputs. | 29 / 1 / 0 | 50 entry roots accounted. PHB Petrified remains partial for Disease suspension lifecycle behavior; the separate immunity representation is not conflated with suspension. |
| Disease | 29 / 29 / 0 | All 29 reviewed. | 28 outputs; Super-Tetanus has a documented unsupported disposition, with its source entry accounted. | 25 / 3 / 1 | 83 entry roots accounted. Spell, Rule, Condition, and Creature references are source-qualified. Cackle Fever ambiguity and Disease lifecycle/schema limits remain documented. |
| Status | 5 / 5 / 0 | All five source records reviewed in the representative baseline. | 5 outputs. | 2 / 3 / 0 | 12 entry roots accounted. PHB/XPHB Concentration and PHB Surprised retain documented external dependencies or assignment-procedure limits. |
| Sense | 8 / 8 / 0 | All eight reviewed; the Phase 4 refinement was incorporated into the Darkvision/Tremorsense/Truesight representations. | 8 outputs. | 5 / 3 / 0 | 10 entry roots accounted. Remaining limits include Total Cover applicability, precision or pinpointing variants, and some Truesight interactions. |
| Language | 201 / 201 / 0 | All 201 reviewed, including the four representative identities and later approved refinements. | 201 outputs. | 72 / 129 / 0 | 42 entry roots accounted. Typed creature references resolve where exact source identity is established. Origins, dialects, and some speaker links remain external or unresolved source-field issues. |
| Language Script | 6 / 0 / 6 | Excluded from semantic conversion by the approved Step 2 scope. | 0 outputs; no Language Script schema. | N/A | Font-only LanguageScript records are explicitly excluded in the Step 2 roadmap and canonical-data policy. Script names used by Languages remain on Language records. |
| **In-scope total** | **588 / 588 / 0** | **All 588 reviewed by domain reports and accounting.** | **585 outputs; 3 documented unsupported alternatives.** | **268 / 244 / 76** | **980 entry roots across the Phase 3 representative set and Phase 5 expansion.** |

The 594 source records consist of 588 in-scope identities plus six excluded LanguageScript records. The 588 in-scope identities split into 34 Phase 3 representative records and 554 Phase 5 expansion records. Phase 5 produced 551 canonical outputs; combined with 34 Phase 3 outputs, the current total is 585. The three missing outputs are the two unsupported Action records and Super-Tetanus; all three have explicit source-qualified unsupported dispositions, reasons, and complete entry-level omission accounting. They are not referenceable canonical entities. This follows the documented no-placeholder policy, but remains a deliberate coverage limitation for human review.

## Reconciled accounting and validation

Independent checks confirm the requested checkpoint values:

| Measure | Repository evidence | Result |
|---|---|---:|
| Rule identities | `variantrules.json` and `ruleCatalogSummary` | 231 |
| Rule semantic reviews complete | `ruleCatalogSummary` and inventory review statuses | 231 |
| Rule semantic reviews pending | `ruleCatalogSummary` | 0 |
| Rule conversions pending | `ruleCatalogSummary` | 0 |
| Rule canonical outputs | combined Phase 3/5 output inventory and Rule summary | 231 |
| Phase 5 canonical outputs | Phase 5 manifest and actual Phase 5 output files | 551 |
| Permanent checks | validator output | 837 |

The Phase 5 manifest has 906 source-entry roots; the Phase 3 manifest has 74, for 980 total roots. Across those manifests, disposition records are 482 INTERPRETED, 39 DUPLICATE, and 768 OMITTED. These are disposition parts, not a partition of roots: mixed roots may have multiple dispositions. The accounting files enumerate source-entry and nested content; the validator recomputes the supported `entries`/`items` child paths from immutable source. The Rule inventory separately reports 6,318 nested clause paths under its broader path-count convention, so that metric is not added to the 2,081 entry/item child paths in the conversion accounting.

### Validator blind spot: inconsistent Phase 5 source-field issue count

At the audit checkpoint, the Phase 5 manifest declared `sourceFieldIssueCount: 162` while containing 165 entries. Verification found all entries source-backed except the Planar Effects `reprintedAs` value, whose array wrapper was missing. The manifest value and count are corrected in the remediation update above, and permanent validation now covers both source fidelity and count consistency.

## Reference integrity and remaining limitations

The validator resolves typed canonical references against the appropriate pinned source catalogs and checks supported Rule/Action/Disease/Condition component links. The final validation passed all 551 Phase 5 and 34 Phase 3 canonical records, without duplicate canonical IDs. Source-target existence is distinct from canonical-output availability: later-domain Spell and Creature targets may resolve in their pinned source catalog without having a canonical output in this Phase 5 dataset.

Outstanding source-field findings are documented rather than encoded as guessed references:

- **11 Rule reprint targets:** Alien Technology, Diagonal Movement, Explosives, Firearms, Loyalty, Milestones, Mixing Potions, Renown, Training to Gain Levels, Planar Effects, and Falling.
- **43 Language unresolved fields:** 35 origin values without an exact approved target identity, six explicit Creature speaker links that do not resolve uniquely, and two dialect lists whose named dialect records do not exist in the pinned Language catalog.
- **76 Language external dependencies:** non-Creature speaker populations/groups without an approved target domain identity.
- **Other Language fields:** 26 out-of-scope presentation flags, one schema limitation, and two ambiguous selector expressions.
- **Action/Disease canonical identities without outputs:** Healing Surge, Waking Someone, and Super-Tetanus remain explicitly unsupported, not silently omitted.

Mechanical limitations in PARTIAL and UNSUPPORTED records are generally documented against the existing contracts and approved gap decisions. The most significant examples include multi-step or stateful combat/action procedures; deferred character, equipment, spell, creature, vehicle, and encounter dependencies; Disease progression and suspension lifecycles; incomplete Language origins/dialects/speaker population targets; and the 73 unsupported Rule records. The gap registers and conversion reports preserve implemented, deferred, and skipped decisions. R-GAP-13, R-GAP-16, R-GAP-21, and R-GAP-35 remain record-specifically skipped where assigned; their mechanics were not revived by the reconciliation.

## Contract and scope stability

There are nine Core Rules schema files under `src/main/resources/schemas`: eight entity contracts (Action, Condition, Disease, Language, Rule, Sense, Skill, Status) and the shared contract. No Language Script schema is expected. The Phase 4/5 reports identify bounded refinements to Action, Condition, Disease, Language, Rule, Status, and Sense; no ninth entity model, generic effects engine, or source-prose fallback was introduced. The current validator compiles all nine schemas. Immutable source catalogs were read but not modified by this audit. No Phase 6 integration was started.

### Governance status conflict

The artifacts do not agree on the milestone's current status. `ROADMAP.md` §Phase 5 still says only 99 of 554 non-representative records have been examined, says expansion in the remaining domains has not resumed, and says Phase 5 is not approved. Its Step 2 section also retains pre-expansion Phase 3/Phase 4 status language. Those claims conflict with the Phase 5 manifest, RULE-CONV-01 through RULE-CONV-29 reports, the reconciliation report, and the current passing validator checkpoint. The Phase 5 Dataset Expansion report also contains historical “remaining 226 Rules” status text alongside later Rule conversion addenda. This audit does not update roadmap status; human review and a separately authorized status reconciliation are required before a close decision can be reflected authoritatively.

## Findings at original audit checkpoint (superseded)

| Severity | Finding and evidence | Classification |
|---|---|---|
| **BLOCKER** | `ROADMAP.md` says Phase 5 remains unapproved and in progress at 99/554 examined, contradicting the actual 551-output validated checkpoint and completed batch reports. Milestone authority/status is not reconciled. | Governance/status evidence must be reviewed and reconciled before closing. |
| **BLOCKER** | Phase 5 `sourceFieldIssueCount` is 162 while its issue array has 165 records; permanent validation passes without checking this equality. | Source-accounting aggregate is inconsistent and the validator misses it. |
| **NON-BLOCKING LIMITATION** | Three in-scope source identities lack canonical records: Healing Surge, Waking Someone, and Super-Tetanus. Their complete source entries have explicit unsupported dispositions and reasons; no placeholder mechanics are fabricated. | Documented unsupported alternative, subject to human acceptance. |
| **NON-BLOCKING LIMITATION** | 244 PARTIAL and 76 UNSUPPORTED in-scope records retain documented mechanics gaps, schema limitations, or approved gap omissions. | Coverage limitation; catalog accounting and approved scope remain distinct. |
| **NON-BLOCKING LIMITATION** | 11 Rule reprint references, 43 Language unresolved fields, 76 Language external dependencies, and other listed field issues remain unresolved outside canonical references. | Documented provenance/reference limitations; target identities were not invented. |
| **NON-BLOCKING LIMITATION** | Six LanguageScript font records have no canonical output. | Explicitly excluded source category, not missing in-scope coverage. |
| **FUTURE WORK** | Spell/Creature canonical target availability, character/equipment/vehicle/encounter state, Disease lifecycle execution, and other later-domain dependencies. | Requires those later domains or separately approved work. |

## Validation results

- At the original audit checkpoint, `npm run validate:core-rules` passed with 837 permanent checks. After remediation, the final run passed with 838 permanent checks; see the remediation update and closure report.
- `git diff --check` — passed (exit 0). Git printed line-ending normalization notices for existing working-tree files; no whitespace error was reported.

## Final recommendation

**READY TO CLOSE WITH DOCUMENTED LIMITATIONS.** This was the pre-remediation recommendation. Both blockers have since been resolved and Phase 5 is formally closed and approved as recorded above. The documented unsupported identities and deferred/external dependencies remain unchanged.
