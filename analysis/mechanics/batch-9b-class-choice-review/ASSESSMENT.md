# Batch 9B — Class Choice Metadata and Source Resolution Review

## Decision

Keep the current public choice representation. Both `PARTIAL` and both `WITHHELD` statuses are
accurate. There is no internal preservation defect, and the two XGE-owned references select the
correct Optional Feature definitions within the pinned corpus. A production Batch 9C is not
currently justified as a correctness fix.

The 11 omitted labels have corresponding level prerequisites already available through existing
Optional Feature detail endpoints. The single required-option marker is not public. Additional
labels, required markers or withheld-alternative counts would require a specific publication and
API decision; their technical extractability supplies no such permission. The existing
`choiceProjectionStatus` is sufficient to tell clients that a projection is incomplete.

There is a broader source-default provenance question: 27 of the 60 unqualified Optional Feature
reference occurrences have same-name candidates in more than one primary source. This does not
establish wrong targets. It does justify a bounded review of source defaults and provenance before
changing how those tokens are represented or resolved.

## Scope, evidence and reproducibility

This is analysis only against pinned v2.37.0. Read-only inputs include the
[Batch 9 assessment](../batch-9-class-completeness/ASSESSMENT.md), its
[complete occurrence evidence](../batch-9-class-completeness/source-occurrences.json), the
[Batch 9A report](../batch-9a-class-choice-completeness/IMPLEMENTATION.md), the
[publication boundary](../entry-assessment/PUBLICATION-BOUNDARIES.md), the implementation order,
current Java types/mappers/resolvers, Source Registry, and actual JSON records.

[`audit-source.mjs`](audit-source.mjs) parses the four selected choice groups, the XGE owner,
Optional Features, Feats and source registry. It records metadata and locations without copying
entry prose. [`source-evidence.json`](source-evidence.json) contains all labels, target prerequisite
structures, source-qualified match sets, both reprint records, all 60 defaulted references and
the same-name source alternatives. The reference match sets reproduce the current exact
name/primary-source lookup from source records; they do not run or alter the shared resolver.

Run from repository root:

```powershell
node analysis/mechanics/batch-9b-class-choice-review/audit-source.mjs
git diff --check
```

The retained `scope-baseline.json` fingerprints all `src` files and the original audit/evidence
trees before this analysis. It is not overwritten by the command. A separate comparison checks
all 175 JSON resource hashes against the saved pre-Batch 9A manifest.

## Complete omitted metadata inventory

All omitted labels originate in the `name` property of `type=refOptionalfeature` nodes inside
the source `type=options` list. They are not names synthesized by the backend. A label is distinct
from the `optionalfeature` UID token and from the referenced definition's canonical name.

In the following tables, files are relative to `src/main/resources/data/classes/`. No omitted
`required` marker exists for Rune Carver. The only marker is the Elemental Attunement record
listed separately below.

### Rune Carver

Owner: Fighter / Rune Knight, TCE, level 3; `class-fighter.json#/subclassFeature/55`.
The option list at `/entries/5` has authored `count=2` and six reference alternatives.

| Exact source field | Authored label | UID token | Existing target level prerequisite |
|---|---|---|---|
| `/subclassFeature/55/entries/5/entries/4/name` | `Hill Rune (7th Level or Higher)` | `Hill Rune\|TCE` | 7; Fighter / Rune Knight |
| `/subclassFeature/55/entries/5/entries/5/name` | `Storm Rune (7th Level or Higher)` | `Storm Rune\|TCE` | 7; Fighter / Rune Knight |

The other four references have no source label override. Both target records carry structured
`prerequisite[0].level`, including the Rune Knight source qualifier TCE. Their exact locations
are `optionalfeatures.json#/optionalfeature/105` and `/optionalfeature/187` respectively.

### Elemental Disciplines

Owner: Monk / Four Elements, PHB, level 3; `class-monk.json#/subclassFeature/8`.
The option list at `/entries/1` has authored `count=2` and 17 reference alternatives.

| Exact source field | Authored label | UID token | Existing target level prerequisite |
|---|---|---|---:|
| `/subclassFeature/8/entries/1/entries/0/name` | `Breath of Winter (17th Level Required)` | `Breath of Winter` | 17 |
| `/subclassFeature/8/entries/1/entries/1/name` | `Clench of the North Wind (6th Level Required)` | `Clench of the North Wind` | 6 |
| `/subclassFeature/8/entries/1/entries/3/name` | `Eternal Mountain Defense (17th Level Required)` | `Eternal Mountain Defense` | 17 |
| `/subclassFeature/8/entries/1/entries/7/name` | `Flames of the Phoenix (11th Level Required)` | `Flames of the Phoenix` | 11 |
| `/subclassFeature/8/entries/1/entries/8/name` | `Gong of the Summit (6th Level Required)` | `Gong of the Summit` | 6 |
| `/subclassFeature/8/entries/1/entries/9/name` | `Mist Stance (11th Level Required)` | `Mist Stance` | 11 |
| `/subclassFeature/8/entries/1/entries/10/name` | `Ride the Wind (11th Level Required)` | `Ride the Wind` | 11 |
| `/subclassFeature/8/entries/1/entries/11/name` | `River of Hungry Flame (17th Level Required)` | `River of Hungry Flame` | 17 |
| `/subclassFeature/8/entries/1/entries/16/name` | `Wave of Rolling Earth (17th Level Required)` | `Wave of Rolling Earth` | 17 |

All nine referenced PHB Optional Feature records have structured level prerequisites identifying
Monk / Four Elements. Exact target pointers and prerequisite values are in `source-evidence.json`.
The other eight references have no source label override.

| Exact source field | Reference token | Authored value | Normalized destination |
|---|---|---|---|
| `class-monk.json#/subclassFeature/8/entries/1/entries/2/data/isRequiredOption` | `Elemental Attunement` | Boolean `true` | `ClassExternalFeatureNode.required` |

This is one required-option marker, not 17 markers and not a source-authored `false` on the
remaining alternatives. Elemental Attunement's target is
`optional-feature:phb:elemental-attunement` (`optionalfeatures.json#/optionalfeature/61`), and it
has no separate structured level prerequisite. The marker supplies selection-related metadata
on this specific reference. It does not establish a universal algorithm for including mandatory
alternatives in `count`, nor does it mean the player has already selected or acquired the option.

### Source → typed import → normalized model → public projection

| Stage | Actual implementation | Preservation/projection |
|---|---|---|
| Typed import | [ClassEntryDeserializer](../../../src/main/java/com/thedndpub/data/dte/classes/ClassEntryDeserializer.java), [ClassReferenceEntryDte](../../../src/main/java/com/thedndpub/data/dte/classes/ClassReferenceEntryDte.java), [ClassEntryMetadataDte](../../../src/main/java/com/thedndpub/data/dte/classes/ClassEntryMetadataDte.java) | Strict reference fields retain exact UID, nullable `name` and nullable `data.isRequiredOption`; choices retain count/style/ordered entries |
| Normalization | [ClassFeatureContentNormalizer.reference](../../../src/main/java/com/thedndpub/data/importer/classes/ClassFeatureContentNormalizer.java), [ClassExternalFeatureNode](../../../src/main/java/com/thedndpub/data/dto/classes/ClassExternalFeatureNode.java) | Retains label in `name` and marker in `required`; referenced canonical identity remains separate in `reference` |
| Public alternatives | [ClassPublicMapper.mapChoiceNodes](../../../src/main/java/com/thedndpub/data/dto/classes/ClassPublicMapper.java), [ClassChoiceReferenceDto](../../../src/main/java/com/thedndpub/data/dto/classes/ClassChoiceReferenceDto.java) | Emits kind, canonical target name, source and ID; does not emit label override or required marker |
| Public completeness | [ClassChoiceProjection](../../../src/main/java/com/thedndpub/data/dto/classes/ClassChoiceProjection.java) | Different source labels or any authored required marker prevent `COMPLETE`; both selected owners correctly report `PARTIAL` |

All 11 associated level values are already preserved separately as
`OptionalFeatureDefinition.prerequisites`. `OptionalFeatureMapper` projects the structured
prerequisite into `OptionalFeaturePrerequisiteDto.level`, with `level`, `characterClass` and
`subclass` components. `OptionalFeatureDto.from` exposes it through the existing
`GET /api/optional-features/{id}` contract. Clients can follow the existing Class option target ID
to that metadata. No label parsing, copied prerequisite model or new eligibility field is needed.
The required marker on the containing choice is not supplied by those target prerequisites.

## The Third Eye and Master Transmuter

| Owner | Actual source option node | Structured content | Current public result |
|---|---|---|---|
| The Third Eye, PHB page 116, Wizard / Divination, level 10 | `class-wizard.json#/subclassFeature/27/entries/1` | Four ordered named `entries` blocks; no authored count/style | `WITHHELD`; no public alternatives or `mechanics` placeholder |
| Master Transmuter, PHB page 119, Wizard / Transmutation, level 14 | `class-wizard.json#/subclassFeature/58/entries/1` | Four ordered named `entries` blocks; no authored count/style | `WITHHELD`; no public alternatives or `mechanics` placeholder |

Their child blocks each contain one text entry. The typed `ClassBlockEntryDte` and normalized
`ClassFeatureSection` preserve names/hierarchy; `ClassFeatureText` preserves the entry body.
The public mapper recurses through sections looking for supported reference alternatives. None
exists in these two choice lists. Batch 9A reports their typed choice presence explicitly, so
`WITHHELD` accurately communicates the current projection. No source alternative becomes a new
definition or target ID.

The block names are authored option names, not a formally declared controlled vocabulary. For
example, a familiar word inside one block name does not authorize publishing the whole source
selection list. The evidence file retains all eight block names internally; the assessment
recommends no new public names or descriptions.

Potential non-prose structure includes one option-node count per feature, four alternatives per
node, and the absence of a source-authored selection count. Those are observable structural facts,
but none currently has a public field or field-specific publication authorization. In particular,
four alternatives must not become `count=4`, a grant quantity, or a claim of how many selections
the player may make. Additional structural counts are not necessary for clients to distinguish
absence from withholding; the existing status already does that. Publishing counts alone would
leave both statuses `WITHHELD`, because no alternatives would be exposed.

## Exact XGE source-resolution trace

Both references occur under Fighting Style in College of Swords: Bard / PHB parent, subclass
source XGE, feature source XGE, level 3, page 15. The existing feature ID is
`subclass-feature:xge:phb:bard:xge:swords:3:fighting-style`.

| Authored field in `class-bard.json` | Exact authored token | Existing resolved target |
|---|---|---|
| `/subclassFeature/38/entries/1/entries/0/optionalfeature` | `Dueling` | `optional-feature:phb:dueling` |
| `/subclassFeature/38/entries/1/entries/1/optionalfeature` | `Two-Weapon Fighting` | `optional-feature:phb:two-weapon-fighting` |

Neither token contains a source qualifier. The trace is:

1. `ClassEntryDeserializer` preserves the token in `ClassReferenceEntryDte.optionalfeature`.
2. `ClassFeatureContentNormalizer.reference` requests kind `OPTIONAL_FEATURE` from
   [ClassExternalReferenceResolver.resolve](../../../src/main/java/com/thedndpub/data/importer/classes/ClassExternalReferenceResolver.java).
3. The resolver splits the UID on pipes. With no second segment, it sets local source to PHB
   for this kind. It does not use the containing feature's XGE source.
4. [SourceRegistry.find](../../../src/main/java/com/thedndpub/data/services/SourceRegistry.java)
   validates PHB as a known source. Registry entries PHB, TCE, XGE and XPHB are separate codes
   with no aliases between them. Registry membership supplies neither a target identity nor rights.
5. `OptionalFeatureCatalog.findByName` returns exact case-insensitive name matches; the class
   resolver filters their **primary** `definition.source` to PHB. Both filtered match sets contain
   exactly one record. `OptionalFeatureMapper` forms the IDs shown above. Status is `RESOLVED`.
6. The public Class choice projection uses the resulting PHB source and target ID. The original
   unqualified token is still available in importer provenance, not in the public choice DTO.

### Independent source corroboration

| Optional Feature record | Primary source/page | Additional source corroboration | Same-name Optional Feature candidates |
|---|---|---|---|
| `optionalfeatures.json#/optionalfeature/53`, Dueling | PHB 72 | `otherSources` includes XGE 15; feature type includes authored `FS:B` | PHB only |
| `optionalfeatures.json#/optionalfeature/208`, Two-Weapon Fighting | PHB 72 | `otherSources` includes XGE 15; feature type includes authored `FS:B` | PHB only |

Thus both targets are unambiguous **within the pinned Optional Feature domain**, and their
additional-source metadata explicitly agrees with the containing XGE feature's page. The current
resolution is supported by source evidence beyond the default itself. This does not make PHB an
explicitly authored qualifier on either reference, nor prove the general default rule correct for
every possible future dataset.

Same-name XPHB definitions do exist in another domain:
`feats.json#/feat/92` is Dueling (XPHB page 209), and `/feat/288` is Two-Weapon Fighting (XPHB page 210).
The PHB Optional Feature records explicitly have `reprintedAs` entries with `tag=feat` and those
XPHB UIDs. These are source/edition/domain distinctions, not competing Optional Feature targets
for `refOptionalfeature`. The class resolver must not substitute a Feat based only on the name.

| Hypothetical explicit qualifier, with the existing target kind unchanged | Dueling result | Two-Weapon Fighting result |
|---|---|---|
| `PHB` | Same PHB Optional Feature ID | Same PHB Optional Feature ID |
| `XGE` | `UNRESOLVED`, no primary-source XGE Optional Feature match | `UNRESOLVED`, no primary-source XGE Optional Feature match |
| `XPHB` | `UNRESOLVED`, the same-name XPHB record is a Feat | `UNRESOLVED`, the same-name XPHB record is a Feat |

These are read-only reproductions of the current lookup predicate, not changes to tokens or
reference outcomes. `otherSources` is provenance, not a primary-source lookup alias. Inferring XGE
from the owner would make these lookups fail despite the explicit XGE additional-source evidence.

### Broader source-default question

The bounded inventory covers all 60 unqualified Optional Feature reference occurrences in the
40 typed Class option nodes: 50 distinct names, 58 occurrences under PHB owners and these two
under the XGE owner. All 60 currently find a PHB record. Of them, **27 occurrences / 27 distinct
names** also match an XPHB Optional Feature by name. They comprise 16 maneuver names, eight
Metamagic names and three Pact Boon names. The remaining 33 occurrences have only one Optional
Feature primary-source candidate each. The full occurrence/candidate-source list is recorded
in `source-evidence.json`; these counts are not a survey of every reference dialect in every catalog.

The two XGE cases are not demonstrated resolution defects. They are examples of the broader
difference between authored source qualification and an adapter-supplied default. For the 27
multi-source names, the existing PHB fallback materially selects one edition. Their owners are
PHB, which is contextual support, but that is not an explicitly qualified source token.

There is also a concrete provenance limit in
[SourceAuthoredReferenceExtractor.addClassExternalReference](../../../src/main/java/com/thedndpub/data/references/SourceAuthoredReferenceExtractor.java):
it consumes normalized `ClassExternalReference.source`, forms `uid(name, source)`, and labels the
occurrence as a normalized typed class-domain reference. Consequently, the shared resolver sees
`name|PHB` for an originally unqualified Class token. Its resolution outcome cannot by itself
prove that PHB was authored on the original reference. The raw DTE/provenance and the evidence
in this report retain that distinction. A later internal provenance review could record
`authored` versus `defaulted` qualification without changing any target, count or resolver rule.
No public diagnostics or Step 8 reference expansion is proposed here.

## Publication and compatibility classification

The governing repository rules separate import completeness from publication. In particular,
[LICENSING-BOUNDARIES](../../remaining-json/LICENSING-BOUNDARIES.md) requires review of exact
field/source even for numeric mechanics; the Entry assessment publication document makes no new
authorization. Batch 9A approved only the status enum and explicitly excluded labels, the required
marker and new counts. This review identifies engineering classifications, not legal permission.

| Candidate | Content type and current permission | Classification | Compatibility, attribution and status consequence |
|---|---|---|---|
| Keep/document existing `choiceProjectionStatus` | Application-controlled vocabulary, already approved and implemented | `SAFE_TO_IMPLEMENT_UNDER_EXISTING_POLICY` | No new field needed; statuses remain 2 PARTIAL / 2 WITHHELD |
| Use/document existing Optional Feature target links and structured prerequisites | Already published records and metadata | `SAFE_TO_IMPLEMENT_UNDER_EXISTING_POLICY` | No new content/contract; preserves target source/page; not an eligibility calculation; Class status stays PARTIAL |
| Add the 11 full source display labels | Authored names plus literal mechanical qualifier text, not schema-controlled labels | `REQUIRES_PUBLICATION_APPROVAL` | Separate additive `displayLabel` would preserve existing canonical `name`; retain PHB/TCE provenance and determine applicable attribution/rights; required marker still missing from Elemental Disciplines |
| Add the one required-option Boolean | Structural source/reference marker with selection-related meaning; no explicit field publication decision | `REQUIRES_PUBLICATION_APPROVAL` | Additive nullable field must preserve absence versus false; specify source-marker semantics only; labels still omitted, so Elemental Disciplines stays PARTIAL |
| Parse new numeric minimum levels from source labels | Derived mechanical interpretation; equivalent structured target data already exists | `DEFER` | Avoid duplicate or inferred eligibility fields; do not change the status just because numeric data is copied |
| Add named alternatives from the two Wizard section lists | Source-authored names/arrangement, not a controlled vocabulary declaration | `REQUIRES_PUBLICATION_APPROVAL` | No names/IDs may be fabricated; public labels alone still omit bodies/section structure, so do not assume COMPLETE |
| Add option-node/alternative/withheld counts | Non-prose structural metadata, but newly discloses source arrangement and was excluded from 9A | `REQUIRES_PUBLICATION_APPROVAL` | New fields would be additive; precise node versus alternative semantics required; no selection-count inference; WITHHELD remains accurate |
| Add the withheld descriptive bodies to obtain COMPLETE | Expressive instructions/rules content | `DEFER` | No product need established; no permission supplied by extracting structure or by an existing target name |
| Record authored/defaulted qualification separately, or change source fallback behavior | Internal provenance/resolution policy | `REQUIRES_SOURCE_RESOLUTION_REVIEW` | Audit affected occurrences first; no automatic source rewrite, target-ID change, public diagnostic or status change |

Keys such as `name`, `data` and `isRequiredOption` are schema field names. PHB/TCE/XGE/XPHB are
registered source identifiers. The status values are application-controlled vocabulary. These
categories differ from source-authored option names, label suffixes and descriptive prose.
Shortness, numeric form, a familiar rules word, or a source `srd` flag is not a publication decision.

The two PHB fighting-style target records carry `srd=true`; the 11 label target records do not
carry affirmative `srd`/`srd52` flags in this source inspection. Neither observation establishes
field-level rights for newly publishing Class labels. Exact licensed-document scope and any
attribution/change notices would need to be established in a publication review. Source Registry
code/page metadata should be retained, but it cannot substitute for that review. No new content
receives licensing clearance from this assessment.

For any later approved DTO addition, preserve canonical `ClassChoiceReferenceDto.name` and IDs;
use a new optional field rather than replacing a label's meaning. Java record extension affects
canonical constructors, generated equality/hash codes/toString and reflection; preserve previous
constructor signatures using overloads, and default unknown values to null rather than inferred
false. JSON consumers must accept additive fields. A completeness classifier must be reviewed
alongside any actual publication change: publishing labels could make Rune Carver COMPLETE,
while Elemental Disciplines additionally requires its mandatory marker; publishing the marker
alone would not resolve the missing labels. No state changes in this analysis.

## Minimal recommendation and Batch 9C gate

1. Retain the current statuses and withholding. They accurately communicate current completeness.
2. Document that existing target IDs lead to structured Optional Feature level prerequisites.
   This avoids adding a parser or republishing labels simply to convey a level value.
3. Obtain a specific product/publication decision only if clients need full labels or the required
   marker. A bounded future batch could add separately approved optional metadata and review
   completeness classification/tests together. Counts or Wizard prose are not prerequisites.
4. Review the 60 Class defaulted references' provenance, starting with the 27 multi-source names,
   before proposing source-policy changes. Preserve the two corroborated XGE resolutions.

There is **no immediate production Batch 9C correctness fix** supported by this review. Batch 9A
already tells clients when option content is partial/withheld, existing target APIs supply the
11 relevant structured prerequisites, and no erroneous XGE target was found. A future 9C would
need explicit new-field publication authorization or a separately evidenced provenance defect;
it should not be scheduled merely to turn every status into COMPLETE.

## Open questions

- Do clients need source display labels or the exact required-option marker beyond status plus
  existing target prerequisite data?
- What exact source-marker semantics should an approved required-option field communicate,
  without treating it as a selected/granted benefit or interpreting source count arithmetic?
- Which licensed material, if any, covers each proposed PHB/TCE field and its source arrangement,
  and what attribution would apply?
- Should future internal references record both original source qualification and adapter defaults
  explicitly? How can that be done while retaining the existing 6,246 occurrence identities/outcomes?
- What pinned upstream grammar evidence should establish source-default policy for the broader
  Class dialect? The two corroborated cases do not answer every multi-source occurrence.

## Verification

`audit-source.mjs` reproduces 11 labels, one true required-option marker, two withheld groups,
two XGE tokens, 60 unqualified references/50 names, and 27 multi-source occurrences. It checks
all 175 pinned JSON resource hashes and aggregate fingerprints of all production/resource/test
files plus the previous audit and assessment trees. The protected bytes, including the Batch 9A
working-tree changes present at the start, are unchanged. Resolver code and prior evidence are
unchanged. [`verification.json`](verification.json) records the check results.

`git diff --check` passes; new analysis files have no trailing whitespace. Maven was not run for
this analysis-only task; the reported baseline remains 504 passing tests and 6,246 resolver
occurrences. Only files under this new analysis directory were added. No DTO, endpoint, source
JSON, production implementation, test or resolver modification was made.
