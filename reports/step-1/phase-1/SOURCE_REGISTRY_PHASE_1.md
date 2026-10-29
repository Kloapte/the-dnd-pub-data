# Step 1 — Source Registry: Phase 1 Discovery

**Status:** Phase 1 analysis complete; canonical modeling and conversion are not authorized until review and approval.

**Scope:** Source identity and provenance metadata only. `adventures.json` was inspected only for source-identification metadata. Publication contents, book contents, adventure storylines, and rules text were not interpreted.

## 1. Source inventory and structural findings

### Inspected data

The read-only census covered all 174 JSON files in the pinned source corpus to validate source-code use and identity references. Focused structural analysis covered:

| File | Records | Structural fields observed |
| --- | ---: | --- |
| `sources/registry.json` | 181 | `code` (string, 181/181), `aliases` (array, 181/181) |
| `books.json` | 66 | `name`, `id`, `source`, `group`, `published`, `author`, `contents` (66/66); `coverCredit` (3), `revised` (2), `alias` (4), `parentSource` (4) |
| `adventures.json` | 101 | `name`, `id`, `source`, `group`, `published`, `contents`, `storyline`, `level` (101/101); `author` (100), `parentSource` (50), `publishedOrder` (26), `alias` (2) |

Observed types for identity-relevant properties are uniform: registry `code` is a string and registry `aliases` is an array; book/adventure `name`, `id`, `source`, `group`, `published`, `author`, `coverCredit`, `revised`, and `parentSource` values are strings where present; book/adventure `alias` is an array; adventure `publishedOrder` is numeric. Presence counts above distinguish fields observed on every record from optional fields. These are source-shape facts, not canonical requiredness decisions. The values of `contents` and `storyline`, and the values inside `level`, were excluded from analysis; their field presence is listed only as a structural observation. All 66 book publication dates and 101 adventure publication dates use `YYYY-MM-DD`. Adventure `publishedOrder` values occur in 26/101 records; 75 omit the field. All 101 adventure records have the same observed `level` key shape (`start`, `end`), without interpretation of the values. `publishedOrder` and the `level` field are metadata outside the proposed minimal source-identity model unless later analysis establishes an identity use.

The book `group` distribution is: `core` 6, `screen` 6, `setting` 15, `setting-alt` 8, `organized-play` 1, `supplement` 11, `supplement-alt` 12, `other` 2, `recipe` 4, `homecraft` 1. Adventure groups are `supplement` 59 and `supplement-alt` 42. These source labels should be preserved as supplied and not treated as authoritative game or publication semantics without later review.

### Identity keys, aliases, and duplicates

The registry has 181 case-insensitively unique source codes, with no duplicate codes, case-only collisions, or code/alias conflicts. Every registry entry has an `aliases` array; 175 arrays are empty and six contain one alias. Those six aliases are `PS-A`→`PSA`, `PS-D`→`PSD`, `PS-I`→`PSI`, `PS-K`→`PSK`, `PS-X`→`PSX`, and `PS-Z`→`PSZ`. The aliases account for all six book records where `id` differs from `source` (the Plane Shift records). The alias field therefore has demonstrated identity value and should remain separate from display names.

Book and adventure `id` and `source` values are unique within their respective collections. No repeated display names were found within either collection. Eight records have different `id` and `source` values:

| Kind | Publication `id` | Source code | Observation |
| --- | --- | --- | --- |
| Book | `PS-Z`, `PS-I`, `PS-K`, `PS-A`, `PS-X`, `PS-D` | `PSZ`, `PSI`, `PSK`, `PSA`, `PSX`, `PSD` | Registry aliases explicitly connect these identifiers. |
| Adventure | `MOT-NSS` | `MOT` | Publication identity differs from the containing/source identity. |
| Adventure | `FRAiF-TLLoL` | `FRAiF` | Publication identity differs from the containing/source identity. |

Do not collapse `id`, `source`, alias, and display name into one identifier. Four book records and two adventure records have metadata `alias` arrays. Their values are human-readable titles, not registry code aliases. The title “Planescape: Adventures in the Multiverse” occurs as an alias on two books and one adventure, so it is not a unique identity key. Registry aliases and metadata aliases require distinct treatment.

### Metadata presence and code-only entries

All 66 book `source` values and all 101 adventure `source` values match registry codes. Across the metadata collections, 165 of 181 registry codes (91.2%) have a book or adventure identity record matched by its source or publication identifier. Six additional metadata records have an `id` differing from their source code, but still connect through the source-code field; do not count their publication IDs as new registry codes.

Sixteen registry codes have neither a matching book/adventure `source` nor a matching publication `id`. They are present in other source-domain references and must remain valid source identities without fabricated publication metadata:

| Code | Source-field references in other datasets |
| --- | ---: |
| `DrDe` | 2 |
| `EEPC` | 55 |
| `EET` | 1 |
| `ESK` | 6 |
| `HAT-LMI` | 5 |
| `HFDoMM` | 57 |
| `MCV2DC` | 12 |
| `MCV3MC` | 5 |
| `MFF` | 18 |
| `MisMV1` | 6 |
| `RoTOS` | 4 |
| `SADS` | 4 |
| `TftYP` | 194 |
| `ToD` | 63 |
| `UATheMysticClass` | 66 |
| `VD` | 2 |

These are code-only relative to the supplied book/adventure identity metadata. This finding does not establish that their publication metadata is missing from the original source, nor does it authorize filling the gap from outside material.

### Corpus source references

Across the other 171 source files, the census found 18,354 string-valued properties named `source`. These resolve to 141 distinct registry codes; the remaining registry entries are still represented in the registry and/or metadata and may have no such property occurrence. Every `book` and `adventure` reference tag counted by the scanner resolved against the inspected source identity keys: 696 book-tag occurrences and 72 adventure-tag occurrences, with zero unmatched targets. Per the approved roadmap, 5eTools page navigation is navigation metadata, not a resolved entity reference.

One `source` value does not match the registry: `WGE` at `monsters/bestiary-erlw.json#/monster/3/altArt/0/source`. It occurs in nested alternate-art metadata, rather than as the owning entity's primary source identity. Preserve and report this source value as unresolved pending the relevant monster/alternate-art analysis; do not silently add it as a registry identity based on this single field.

## 2. Semantic and identity analysis

For this domain, the meaningful semantics are publication/source identity, aliases, source grouping, and parent/source relationships. The registry's `code` is the strongest supplied source identity key. Book/adventure `source` identifies source context; their `id` identifies the publication or adventure record; display `name` and `alias` provide labels. These are related facts with different roles.

The source data supports explicit defaults and editions only where those values are actually present in the source identity metadata. A missing field must remain missing. In particular, a source code with no book or adventure record does not support an inferred title, date, edition, publisher, or parent. The data alone does not establish equivalence between reprints or between same-named entries from different sources. Edition, default-source, and reprint decisions need to be reviewed against the source entry and relevant source conventions during affected domain analysis, with uncertainty recorded instead of inferred.

No game mechanics are in the Source Registry domain. Semantic enrichment is feasible for identity and traceability fields, but only with explicit identity roles and preserved raw values. Mechanical effects, story content, and publication prose are outside this analysis.

## 3. Dependency analysis

### Parent relationships

There are 54 metadata records carrying `parentSource`. The parent token matches a registry code in 36/54 records (66.7%). The other 18 occurrences use four values absent from the registry: `SAiS` (3 records), `PAitM` (3), `AitFR` (5), and `NRH` (7). The supplied data records these tokens on child metadata but does not provide a corresponding parent metadata record. It does not establish whether each token is a registry omission, a parent publication identifier, or source-specific shorthand. Retain the supplied token and mark the canonical target unresolved pending review.

When matching a parent token against the 167 book/adventure records, 13 parent-bearing records have one matching metadata target and 39 have no matching metadata target; two records have multiple match paths when source and publication identifiers are checked independently. Those two cases resolve to the same source context, not evidence of distinct parents. A parent reference should first be resolved by its documented identity role and then by key, rather than by treating every matching string as a separate target.

The parent value `ESK` is an important split case: it is a registry code and parent for four adventure records, while it is among the sixteen codes without a book/adventure identity record. Other parent values such as `TftYP`, `GGR`, `AI`, `ERLW`, `RMR`, `EGW`, `MOT`, `VRGR`, `SCC`, `DrDe`, and `FRAiF` match registry codes. The full edge list and individual affected records are retained in the machine-readable evidence file.

The metadata expresses `parentSource` as a one-level token and does not encode parent relationships as record-ID chains, so a canonical graph-cycle check is not possible at this stage. Because some parent values are unresolved as registry identities, repeat dependency and cycle checks after their meaning is approved.

### Dependencies on later roadmap domains

Every later normalized entity needs a stable source identity and a source location. The registry is therefore an upstream identity reference for all domains in the roadmap. The observed direct source references span actions, backgrounds, classes, conditions and diseases, feats, equipment, languages, monsters, optional features, races, senses, skills, spells, hazards, recipes, rewards, and other source files. Sixteen code-only entries already have source references in these domains, including substantial usage in spells, monsters, recipes, classes, and equipment. They cannot be omitted just because publication metadata is absent.

Book and adventure metadata can provide source labels and parent context, but it is not a prerequisite for mechanically normalizing every entity. Stable references to later domains should be retained even when a mechanics dependency is deferred, consistent with the approved roadmap. Publication contents are not required or authorized for this step.

## 4. Domain model and submodel alternatives

The following are analysis proposals, not approved canonical structures.

| Alternative | Advantages | Costs and risks | Assessment |
| --- | --- | --- | --- |
| One broad `Source` object with many optional publication and adventure fields | One lookup surface; simple initial registry ingestion. | Mixes source-code identity, publication record identity, aliases, parentage, and category-specific metadata. Many optional fields and ambiguous relationships; code-only entries risk being forced into publication-shaped records. | Not recommended. |
| One abstract source with specialized book/adventure submodels | Shared identity plus differentiated publication metadata. | Adds inheritance and DTO/schema complexity; registry codes are not always one-to-one with records; parent and source identifiers do not always identify a publication record. | Possible if the approved application needs subtype polymorphism; not needed by evidence alone. |
| A source-code registry composed with separate book/adventure identity records and explicit references | Keeps registry codes stable; represents multiple publication records per source context; preserves code-only sources and distinct `id`/`source`/alias roles. | Requires references to be resolved by declared key and may retain unresolved parent tokens. | Recommended: the simplest faithful structure supported by current data. |
| Fully independent registry, book, and adventure models with no shared identity relationship | Preserves each file's shape and avoids modeling assumptions. | Duplicates references and prevents reliable unified source lookup; loses explicit join semantics already supported by `source`. | Too disconnected for the required downstream provenance and source-reference use. |

Recommended modeling direction: keep a stable source-code identity record (starting from `sources/registry.json`) separate from optional book and adventure metadata records. Let metadata records reference a source code while retaining their own publication/adventure identifier, display name, supplied labels, publication date, author where present, and source-specific optional attributes. Keep code aliases distinct from human-title aliases. Represent parent context as an explicit reference only when the parent identity is established; preserve the raw supplied token and an unresolved-reference record otherwise. Do not place implementation or review states in canonical identity objects. Track complete, deferred, unsupported, and ambiguous findings in the discovery/provenance or incomplete-reference tracking layer.

Do not force adventure metadata into the source registry record. Do not create publication records for the sixteen code-only entries. Whether parent identity deserves a distinct model from a source-code reference is an unresolved contract question for Phase 2.

## 5. Coverage, outliers, and feasibility

| Measure | Result |
| --- | ---: |
| Source JSON files inspected for source references | 174 |
| Registry codes / unique case-insensitive codes | 181 / 181 |
| Book metadata records; unique IDs and sources | 66; no duplicates observed |
| Adventure metadata records; unique IDs and sources | 101; no duplicates observed |
| Registry codes represented by book/adventure `source` or `id` | 165 / 181 (91.2%) |
| Registry codes without either metadata match | 16 / 181 (8.8%) |
| Registry alias strings; duplicate or conflicting aliases | 6; none |
| Book/adventure parentSource occurrences matching registry codes | 36 / 54 (66.7%) |
| ParentSource occurrences using unregistered values | 18 / 54 (33.3%) |
| Book/adventure identity tags resolved / unmatched | 768 / 0 |
| Other-corpus `source` field occurrences resolving to a registry code | 18,353 / 18,354 |
| Other-corpus unregistered nested source value | 1 (`WGE`, alternate art) |

The registry identity inventory has complete structural coverage of its supplied records, and all book/adventure source links resolve. Identity modeling can preserve all supplied fields and source relationships without interpreting story content. The main coverage limitations are code-only source metadata, unresolved parent tokens, and the isolated alternate-art source. The 18,354 count is for properties named exactly `source` in the 171 non-focus files; it excludes the focused metadata files and does not count `parentSource` as a primary-source occurrence.

Common structures: registry entries are uniform (181/181); book/adventure records share the required identity fields listed above (66/66 and 101/101). Specialized handling: 8/167 metadata records have different `id`/`source`; 54/167 have a parent token; 6/181 registry entries have aliases; 16/181 registry codes have no book/adventure identity record. Categories overlap and should not be summed. No source records require dropping information to preserve their identity, but 18 parent occurrences and one nested source value cannot yet be resolved to a canonical registry target.

## 6. Recommended approach and unresolved decisions

1. In Phase 2, design the source-code registry as the stable lookup foundation and keep book/adventure metadata as separate related identity records. Keep source codes, publication IDs, aliases, and display names distinct.
2. Preserve all 181 registry codes, including all sixteen code-only identities, without fabricating metadata. Retain all supplied source refs and provenance locations.
3. Define how metadata aliases differ from registry-code aliases and how a metadata record may share a parent source context with other records.
4. Review source defaults, edition distinctions, alternate spellings, reprints, and publication identity against actual source evidence before resolving canonical identity. Never use name similarity alone.
5. Preserve `SAiS`, `PAitM`, `AitFR`, and `NRH` as unresolved parent tokens until their identity role is established. Recheck the two source/id double-match cases with a key-aware join.
6. Keep the `WGE` alternate-art reference explicitly unresolved for the relevant monster-domain analysis; do not add a registry entry from it alone.
7. Record missing publication details for code-only entries as unavailable in the supplied metadata. Treat publication content and catalogs as unsupported when needed to interpret an in-scope rule, following the roadmap's interpretation-coverage rules.
8. Decide in Phase 2 whether `parentSource` is a source-context reference, a publication-to-publication relation, or a relation that needs both roles. Current data supports preserving the raw field, but not one universal interpretation.

The model proposal, unresolved identity decisions, and source coverage are ready for review. No schema, normalized dataset, source JSON, or Java code was created or modified. Phase 2 requires separate approval.

## Evidence and reproducibility

- [Machine-readable census evidence](../../../tools/evidence/step-1/source-registry-evidence.json) contains inventory, hashes, field frequencies, identity joins, source usage, parent edges, and reference counts.
- [Read-only census script](../../../tools/scripts/step-1/analyze-source-registry.cjs) produced the evidence. It reads the pinned JSON corpus and writes only the evidence artifact under `tools/evidence/step-1`.
- [Approved Step 0 dependency analysis](../../step-0/STEP_0_DEPENDENCY_ANALYSIS.md) and [dependency tables](../../step-0/dependency-tables.md) provide the already-approved roadmap dependency context; the analysis was not repeated.

The source corpus SHA-256 recorded for this run is `df60cc4ad8599701aff178bed2826fb80d2d2cb1e862d67938d1873ec793b8c1`. It matches the Step 0 census. Per-file hashes for the three focused inputs are recorded in the evidence artifact.
