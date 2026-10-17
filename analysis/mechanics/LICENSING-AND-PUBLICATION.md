# Licensing and Publication Assessment

## Evidence boundary

This is an engineering publication assessment, not a determination of the copyright status of
individual rules, facts or transformations. No new source license or public field is approved by
this analysis. The existing repository `LICENSE` is MIT for the application; this file supplies no
evidence that every imported third-party text, table, name, image or derived structure shares that
license. Current imports/public APIs are described as observed behavior, not retrospectively
certified redistribution rights.

Primary external sources checked on 2026-10-17:

- Wizards' [SRD page](https://www.dndbeyond.com/srd) supplies SRD 5.1 and 5.2.1 and identifies
  their Creative Commons licensing. These are specific published documents, not a license for
  every book represented by the dataset.
- The [Creator FAQ](https://www.dndbeyond.com/creator-faq) distinguishes the Basic Rules from
  the SRD and directs publishing to the SRD. Dataset Basic Rules flags therefore cannot establish
  SRD publication scope.
- The [CC BY 4.0 legal code](https://creativecommons.org/licenses/by/4.0/legalcode.en) governs
  its licensed material and attribution/change indications, subject to its terms. A publication
  assessment must identify the actual licensed material and applicable obligations.

These sources support a licensing path for verified SRD content. They do not establish that an
arbitrary 5eTools field is verbatim SRD material, that a derived effect is approved, or that the
product's intended distribution satisfies every applicable requirement. No general legal
conclusion about numerical mechanics or transformed prose is made here.

## Five distinct dispositions

| Disposition | What the proposed enrichment may contain | Gate |
|---|---|---|
| Retained internally | Existing typed source content and exact unsupported syntax | Existing import/data-access boundary; do not broaden access through a public DTO |
| Normalized internally | Lossless structured mechanics and syntax annotations | Provenance, semantic confidence, complete shape coverage and no execution |
| Eligible for public consideration | Minimal structured fields with a documented source/field publication decision | Verified rights basis, attribution requirements, product approval and tested projection |
| Requires review | Derived effects, authored result collections, expressive qualifiers, non-SRD material or uncertain provenance | Source/field-specific licensing or legal review; default to withholding new fields |
| Excluded | Media paths/art, raw markup, unreviewed full prose/tables, unsupported inferred mechanics, Homecraft | No public projection in Step 7 |

Internal retention is not declared exempt from rights obligations. It simply records the current
system boundary; deployment/access rights remain the product owner's responsibility. The proposed
annotation layer must not widen distribution of retained source content.

## Per-enrichment publication decisions

| Category | Internal retention/normalization | Public candidate | Review / exclusion |
|---|---|---|---|
| Existing typed statistics | Preserve source variants, conditions and provenance | Existing approved projections can remain; new fields need a decision | Do not treat every number as universally free to redistribute |
| Class table positions | Keep column indexes and existing values | Positional indexes/withheld markers around already public cells | Do not publish removed strings or whole progression tables merely to fix shape |
| Item focus arrays | Keep exact Boolean/label-array variants | Narrow restriction metadata if approved | Class labels are not inferred Class IDs or calculated eligibility |
| Feat grant/defense/proficiency normalization | Keep authored alternatives and category keys | Selected structured grants after field/source review | Source-specific categories, choices and collection structure are not blanket approvals |
| Formula annotations | Keep exact tag category/arguments/locator and token hash | No generic raw annotation API proposed | Formulas extracted from prose still need rights review and verified effect semantics |
| Damage/healing/save associations | Reviewed association can reference evidence without copying a paragraph | Only narrow verified mechanical metadata with approval | No generated description intended to reproduce source wording; no automatic effect publication |
| Item charge/recharge expressions | Preserve original expression internally | Reviewed typed formula/usage metadata | Current markup strings are an exposure to assess; stripping tags does not establish rights |
| Monster action/spellcasting content | Retain source for fidelity | Reviewed numeric/statistical subset through an explicit contract | Full rich entries/headers/footer/source properties currently public need a rights and compatibility decision |
| Prerequisite qualifiers | Preserve authored text plus structured alternatives internally | Structured keys and a withheld-detail indicator where needed | Optional Feature text-bearing prerequisites require field review; do not omit meaningful restriction silently |
| Vehicle component/upgrade data | Retain stats, mode labels and entry content | Previously approved structured stats, e.g. top-level AC/HP consistency | Do not derive or publish descriptive movement/upgrade instructions |
| Treasure/Encounter/Name tables | Preserve exact rows, order, ranges and expressions | Existing metadata/range/count projection; selected quantities only after review | Table selection/arrangement and full result lists remain withheld; no assumption that numeric tables are unrestricted |
| Explicit catalog references | Preserve existing source/token/role context | Current lightweight selected resolver projection | Link/mention is not grant or effect; public authoredToken fields themselves are an existing reviewed contract, not a license for surrounding text |
| Lore, examples and instructions | Keep existing internal source retention | Existing minimal metadata only | No mechanic extraction to circumvent content restrictions; no generated paraphrases offered as a rights workaround |
| Media | Preserve only within current import policy | No new media field | Artwork, tokens, paths, credits and external URLs need separate rights/safety decisions |
| Recipes / Homecraft | Recipes retain their existing internal culinary content; Homecraft excluded | Recipes metadata only | No revisit of either completed product decision |

## Provenance is necessary but insufficient

The source has `srd`, `srd52`, `basicRules` and `basicRules2024` flags, with Boolean or string
variants in some catalogs. Examples of field presence on authored roots:

| Catalog | `srd` | `srd52` | Limit |
|---|---:|---:|---|
| Monsters | 323 | 336 | Does not certify generated-version text or every field |
| Spells | 319 | 339 | Does not bind an extracted clause to a licensed SRD passage |
| Items | 493 | 456 | Copy inheritance, aliases and charge text need field-level provenance |
| Base Items | 91 | 94 | Flags are source indicators, not a replacement for attribution |
| Feats | 1 | 17 | Unflagged mechanics are not automatically approved or automatically unlawful |
| Class Features | 254 | 283 | Source/parent/copy context matters |

All per-catalog flag values/counts are in `corpus-evidence.json`. A string-valued flag can encode
an alternate source label, not a legal license record. `SourceRegistry` validates source identity;
it is not an authorization service. Book publication metadata supplies attribution context but
does not certify field-level rights. Copying a licensed parent's numeric field does not establish
that all child-specific text shares the same terms.

No exact SRD-to-source-record/field comparison was performed here. Consequently **zero new
fields receive a blanket public approval** from this analysis. Candidate public value in the
backlog is conditional on a documented decision, independent of extraction confidence.

## Proposed publication assessment record

For each future public enrichment, record an explicit decision with:

- Owner catalog/definition and source-field locator or a tightly scoped field policy.
- Exact snapshot/file fingerprint; copy/version parent where relevant.
- Rights basis: specific licensed document/version/section, separate permission, or reviewed
  metadata policy; use `UNREVIEWED` when unknown.
- Attribution/notice and change-indication requirements where applicable.
- Approved value subset, exclusions and any source/edition limitations.
- Reviewer/decision reference, date and rationale, kept internal.

An internal extraction-confidence enum cannot act as the publication gate. The public mapper
should eventually require both verified semantics and an applicable publication decision. A
source sync or new tag variant must not automatically inherit approval for broader content.
This proposal requires no persistence entity or database migration.

## Existing exposure to resolve before broader publication

The confirmed cases in [NORMALIZATION-GAPS.md](NORMALIZATION-GAPS.md) are Monster rich content,
Item charge markup on 220 raw roots, and 14 Optional Feature prerequisite strings on seven roots.
They require a documented decision about present rights and API compatibility. This analysis
does not suppress fields, rewrite tests, or infer that all such fields are forbidden. Review
scope and current authorizations first; any contract change is a separate explicit batch.
