# P1A — Spell formula projection status

## Contract and publication checkpoint

This batch implements the metadata-only contract approved in [ASSESSMENT.md](ASSESSMENT.md).
It appends nullable `SpellFormulaProjectionStatus formulaProjectionStatus` to the public
`SpellDto` detail record. `SpellSummaryDto`, routes, filters, normalized definitions and the
existing mechanics/scaling projections retain their contracts.

| Enum value | Meaning | P1A detail responses |
|---|---|---:|
| `NONE_OBSERVED` | No formula observations exist for this owner in the bounded authored-source Batch 6 index | 547 |
| `WITHHELD` | Indexed formula observations exist, but no observation payload is published | 422 |
| `PARTIAL` | Reserved for a separately approved, partially published indexed observation payload | 0 |
| `COMPLETE` | Reserved for a separately approved, completely published indexed observation payload | 0 |

These statuses describe indexed formula publication, not completeness of a Spell's mechanics,
effects or description. In particular, Shield has mechanics and retained descriptive entries
despite its `NONE_OBSERVED` status. The index is bounded to `damage`, `dice`, `scaledamage` and
`scaledice` formula observations; auxiliary modifiers/labels do not change this status.

The only newly published information is application-authored projection metadata. No formula
payload, token, inline scaling argument, source pointer/span, entry prose, raw markup, internal
diagnostic or additional mechanical relationship is authorized or projected. Existing public
structured mechanics and scaling are unaffected. Publication permission for P1B remains separate.

## Implementation and startup join

Production types:

- `dto/spell/api/SpellFormulaProjectionStatus.java`: the four-value public enum.
- `dto/spell/api/SpellDto.java`: appended nullable component and delegating legacy constructor.
- `services/SpellFormulaProjectionCatalog.java`: immutable, startup-validated status index.
- `dto/spell/api/SpellPublicMapper.java`: injected status lookup on detail mapping only.

`SpellFormulaProjectionCatalog` receives the existing `SpellCatalog` and
`SpellFormulaAnnotationCatalog`. It consumes the already-built Batch 6 annotation index, without
another resource read, parser, traversal or reference resolver. At startup it checks that the
complete set of normalized `SpellId` owners exactly matches `index.bySpellId().keySet()`.
Duplicate normalized IDs, missing annotation owners and extra annotation owners reject startup.
A same-count, same-name PHB/XPHB mismatch also fails: names never form the join key.

Only after validating each owner does it derive an enum from the owner's explicit observation
list. The result is an unmodifiable snapshot in catalog order. Empty lists are distinguished
from missing map entries; the existing annotation convenience lookup's empty-list fallback is
deliberately not used. An unknown subsequent status lookup throws rather than reporting
`NONE_OBSERVED`. Spring injects the validated component into the public mapper, so both ID and
by-name detail responses use the same status. Request handling performs only a map lookup.

The constructor accepting definitions and an existing annotation index supports focused join
validation without reparsing sources. The mapper's retained no-argument constructor represents
standalone legacy mapping with no index: it leaves status null, never guesses a status.

## Corpus reconciliation

All 969 authored Spell owners join their 969 existing normalized identities. The existing index
has an explicit list for every owner. Its 930 authored formula observations occur on 422 owners;
the remaining 547 owners have empty lists. No copied/versioned annotation expansion is added.
The approved assessment reports no Spell copy/version/transform declarations in this pin; these
statuses describe the authored index joined to definitions, not a new effective-origin model.

| Existing formula family | Unchanged authored occurrences |
|---|---:|
| `damage` | 642 |
| `dice` | 126 |
| `scaledamage` | 148 |
| `scaledice` | 14 |
| **Total** | **930** |

The 18 separate modifier/label observations do not become public payloads. Existing 41
`scalingLevelDice` roots and 78 explicit non-point numeric range geometries remain authoritative
and unchanged. Original assessment, coverage, runtime snapshots and gold-set files are preserved.

## JSON and Java compatibility

Existing detail properties retain their values and relative serialization order. The new
property is last. These excerpts illustrate only identity and the additive field; the existing
detail fields between them remain present as before:

```json
{"id":"spell:phb:fireball","formulaProjectionStatus":"WITHHELD"}
```

```json
{"id":"spell:phb:shield","formulaProjectionStatus":"NONE_OBSERVED"}
```

The original 14-argument `SpellDto` constructor remains as an overload delegating to the new
15-component canonical constructor with null status. `@JsonInclude(NON_EMPTY)` omits that null
from JSON; there is no empty placeholder, false value or fabricated `NONE_OBSERVED`. Direct
legacy mapper construction behaves the same way. Existing defensive collection copies and
meaningful false/zero serialization are preserved. No global Jackson configuration changes.

Ordinary existing Java constructor calls retain their descriptor. Adding a record component
still changes reflection/component lists, record patterns, generated equality/hashCode and
`toString`. A legacy DTO with null status is not equal to an otherwise identical indexed DTO
with a known status. JSON clients enforcing closed schemas must accept the additive field.
These compatibility limits follow the approved assessment and are not hidden by the overload.

## Regression coverage

`SpellFormulaProjectionStatusTests` adds ten focused tests:

1. Complete 969-owner join, unchanged per-owner formula counts, and exact 547/422 distribution.
2. All four enum values serialize exactly; zero/false survive and no payload is introduced.
3. PHB/XPHB Fireball and Shield, every proposed gold-set owner, and no inference of absent mechanics.
4. Detail/by-name route agreement, unchanged summary route and all-owner service consistency.
5. Every prior detail JSON SHA-256 matches the original assessment after removing only the new
   field; this checks property order as well as values for all 969 identities.
6. Previous Java constructor compatibility, legacy mapper equality and null-status omission.
7. Deterministic results, defensive snapshots and immutable DTO collections.
8. Rejection of missing, extra and duplicate startup owners and an unavailable index.
9. Rejection of same-name/same-count edition mismatches.
10. Explicit failure for unknown owners rather than an empty-index fallback.

The proposed gold set is reused for owner/status reconciliation only; no new semantic accuracy
claim or adjudication is made. Formula-specific source-span tests remain in the Batch 6 suite.

## Verification

**Implemented and verified.**

- Focused Maven run: **26 tests passed** (`SpellFormulaProjectionStatusTests`,
  `SpellPublicApiTests`, `SpellFormulaAnnotationCatalogTests`).
- Complete Maven `package`: **539 tests passed across 66 suites**, zero failures, errors or skips
  (529 baseline plus ten new tests). The existing resolver snapshot/fingerprint assertions pass:
  **6,246** occurrences, with 2,517 RESOLVED, 2,054 AMBIGUOUS, 181 UNRESOLVED and 1,494 DEFERRED.
- Every one of the **969** prior Spell detail snapshots retains its original SHA-256 after only
  the new property is removed. This verifies existing identities, field values and ordering.
- The unchanged Batch 6 tests verify all **930** formula observations and exact family counts,
  source locations/spans, 422 owners and existing structured scaling/range counts.
- The executable JAR starts successfully with the validated startup join. Live localhost
  requests confirm Fireball detail and by-name return `WITHHELD`, Shield returns
  `NONE_OBSERVED`, and the collection returns 969 summaries without the status field.
- All **175** pinned source JSON SHA-256 values match the existing pre-implementation baseline.
  Original assessment/evidence files and every pre-existing file outside the two Spell DTO/mapper
  changes and the approved roadmap edit retain their pre-P1A hashes. No normalized definition,
  importer, resolver, existing test, Monster API or Item API file changes in this batch.
- `git diff --check` passes. New files were reviewed for formatting and trailing whitespace.

Reproduction commands (use the workspace's Maven installation/cache configuration):

```text
mvn -Dtest=SpellFormulaProjectionStatusTests,SpellPublicApiTests,SpellFormulaAnnotationCatalogTests test
mvn package
node analysis/mechanics/batch-11b-item-charge-syntax/verify-source-hashes.mjs
java -jar target/dnd-pub-data-0.0.1-SNAPSHOT.jar --server.port=0
git diff --check
```

The source verifier is reused read-only; no original assessment evidence was regenerated.

## Remaining limitations

P1A cannot communicate formula payload completeness beyond withholding because no new payload
is published. It does not establish effect roles, damage types, targets, healing, save branches
or slot arithmetic. Reserved statuses must not be emitted by the application until an approved
payload supports them. Future source-owner or transformation changes must pass startup
reconciliation and a separate provenance review. No P1B implementation is authorized here.
