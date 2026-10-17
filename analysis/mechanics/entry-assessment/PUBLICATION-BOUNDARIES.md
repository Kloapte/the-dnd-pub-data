# Publication and compatibility boundaries

This is an engineering assessment. It approves no new public field and makes no determination that converting a particular source clause into data establishes redistribution rights. Extraction correctness and publication permission remain separate decisions.

## Current exposure

| Content | Observed current handling | Assessment disposition |
|---|---|---|
| Monster statistics and rich content | MonsterDto is both normalized model and public detail; Feature.entries and spellcasting headers/footer/source extensions are exposed | Existing rights/compatibility review required before expansion; no automatic suppression in this task |
| Item charges | ItemChargeDto forwards count/recharge text, including raw tags on 220 authored roots identified previously | Review exact fields and consumers before a separately approved projection migration |
| Optional Feature prerequisites | Seven raw roots/14 entry or entrySummary strings flow through existing DTOs | Review concise qualifiers independently from main description text |
| Class tables | Batch 1 adds indexes only for safe retained values | No new permission for withheld strings or feature-entry tables |
| Item focus/signed bonuses and Feat mechanics/categories | Batches 2–4 preserve additional internal structure; public contracts unchanged | Future public exposure requires separate field/source and compatibility decisions |
| Other rule-entry content | Generally internal definition/import retention with metadata/safe-stat public projections | Retain established domain-specific boundaries; do not equate internal retention with a legal exemption |
| Table collections/results | Encounter/Name/Treasure and generic rule tables preserve full internal structures; selected metadata/ranges/counts public | Review numerical arrangement and authored result collections separately; no blanket table-data clearance |
| Recipes/Homecraft | Recipes metadata only; Homecraft excluded | No change to either product decision |

Java evidence is catalog-specific in [CROSS-CATALOG-MATRIX.md](CROSS-CATALOG-MATRIX.md); the prior explicit exposure findings are [G5–G7](../NORMALIZATION-GAPS.md).

## Rules for proposed observations and models

| Proposed content | Internal retention | Internal normalization | Public consideration | Review/exclusion |
|---|---|---|---|---|
| Direct mechanical fields | Preserve source and lineage | Lossless narrow values, as existing contracts permit | Only specifically reviewed existing/new subsets | Numeric form alone supplies no blanket authorization |
| Exact syntax tags/formulas | Retain original source internally | Evidence spans, argument/token data, unknown grammar | No generic annotation endpoint proposed | Raw markup, expressive arguments and full source strings withheld from new APIs |
| Damage/healing/save relationships | Retain evidence | Only verified limited associations | Requires adequate semantics plus source/field approval | Parsing prose into fields does not bypass publication review |
| Choices/requirements | Retain alternatives/qualifiers | Preserve roles, grouping and missing-detail context | A reviewed structured subset with honest completeness | Do not publish a misleading selection universe or drop restrictions silently |
| Source evidence hashes/locators/diagnostics | Retain privately | Internal indexes | No new API fields | Hashes are integrity evidence, not authorization or user-facing mechanics |
| Tables/nested results | Preserve order/ranges/quantities | No generation/evaluation | Existing reviewed metadata or separately approved subset | Authored lists/arrangements and result prose require review |
| Media and generated descriptions | Existing import boundary only | No new transformation proposed | None authorized | Media paths/art and generated descriptions are not a rights workaround |

## Source and licensing evidence

The application's [MIT LICENSE](../../../LICENSE) does not establish a license for all imported third-party material. Source Registry/Book metadata identifies provenance; it does not authorize publishing every source field. Source flags such as srd/srd52/basicRules identify source assertions and require verification against the actual licensed material, including copy/version-specific changes.

Primary sources rechecked for this assessment:

- Wizards' [SRD page](https://www.dndbeyond.com/srd) supplies specific SRD releases and their publication/licensing context. This supports identifying an applicable licensed document; it does not license every dataset record.
- Wizards' [Creator FAQ](https://www.dndbeyond.com/creator-faq) distinguishes publication routes and SRD/Basic Rules context. Dataset free-access flags cannot replace that scope assessment.
- The [CC BY 4.0 legal code](https://creativecommons.org/licenses/by/4.0/legalcode.en) sets terms for the licensed material, including applicable attribution/change indications. This assessment does not establish that a given non-SRD record is covered.

These are external licensing references, not an upstream markup specification. The pinned dataset and field provenance remain the authority for what was imported. No field-by-field SRD equivalence check was performed, so no new field receives public approval here.

## Compatibility gate

Any future public addition/removal/type change needs an explicit field-level decision, consumer review, and serialization/endpoint tests. Internal annotation work should use a separate internal index or explicitly private projection; serializing it through MonsterDto would widen an existing public surface. Unknown semantics must not become public defaults.

Potential completeness indicators for public choices, typed Item charge projections, and Monster public/internal separation are independent future decisions. This analysis does not introduce them or reinterpret current output.
