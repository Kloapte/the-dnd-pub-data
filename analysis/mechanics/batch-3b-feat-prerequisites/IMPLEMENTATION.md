# Step 7 Batch 3B: Structured Feat Proficiency Prerequisites

Implemented against the pinned 5eTools v2.37.0 corpus. This batch retains typed prerequisite
category data in the internal normalized Feat definition while preserving the existing public
prerequisite representation.

## Confirmed source coverage

The full `data/feats.json` audit found **12 authored roots** with a total of **12**
`prerequisite[].proficiency[]` objects. Every object contains exactly one category key with one
string value:

| Category key | Occurrences | Authored values |
|---|---:|---|
| `armor` | 9 | `medium` (4), `heavy` (2), `light` (2), `shield` (1) |
| `weapon` | 1 | `martial` |
| `weaponGroup` | 2 | `martial` (2) |
| **Total** | **12** | |

The authored roots are:

| Feat | Source | Source field path | Category/value |
|---|---|---|---|
| Fighting Initiate | TCE | `$.feat[114].prerequisite[0].proficiency[0]` | `weapon: martial` |
| Heavily Armored | PHB | `$.feat[146].prerequisite[0].proficiency[0]` | `armor: medium` |
| Heavily Armored | XPHB | `$.feat[147].prerequisite[0].proficiency[0]` | `armor: medium` |
| Heavy Armor Master | PHB | `$.feat[148].prerequisite[0].proficiency[0]` | `armor: heavy` |
| Heavy Armor Master | XPHB | `$.feat[149].prerequisite[0].proficiency[0]` | `armor: heavy` |
| Medium Armor Master | PHB | `$.feat[191].prerequisite[0].proficiency[0]` | `armor: medium` |
| Medium Armor Master | XPHB | `$.feat[192].prerequisite[0].proficiency[0]` | `armor: medium` |
| Moderately Armored | PHB | `$.feat[196].prerequisite[0].proficiency[0]` | `armor: light` |
| Moderately Armored | XPHB | `$.feat[197].prerequisite[0].proficiency[0]` | `armor: light` |
| Purple Dragon Commandant | FRHoF | `$.feat[220].prerequisite[1].proficiency[0]` | `weaponGroup: martial` |
| Shield Master | XPHB | `$.feat[247].prerequisite[0].proficiency[0]` | `armor: shield` |
| Strike of the Giants | BGG | `$.feat[268].prerequisite[0].proficiency[0]` | `weaponGroup: martial` |

There are no observed null or empty proficiency arrays, multiple proficiency entries in one
alternative, multi-key proficiency objects, source-qualified category values, numeric values, or
Boolean values. The source DTE already represents each category as an optional `String` field on
`FeatPrerequisiteProficiencyDte`; no DTE change was needed.

## Existing loss and group semantics

`FeatMapper.mapPrerequisites` previously joined non-null values from `armor`, `weapon` and
`weaponGroup` with semicolons into the existing `FeatPrerequisiteDto.proficiency` strings. That
loses the category key. It also makes the legacy string ambiguous if a future source object has
more than one category field.

The outer `prerequisite` array is represented as alternative groups. Fields within a group
remain conjunctive according to the existing prerequisite model. The proficiency array's order
is retained in the new list. In the observed corpus, each proficiency array has one element, and
each element has one category. No category alternatives are encoded inside those objects.

Two actual examples demonstrate why the outer group association matters:

- Purple Dragon Commandant's first alternative has level/Feat requirements; its proficiency is
  on alternative index **1**.
- Strike of the Giants has the weapon-group requirement at alternative index **0** and a
  background requirement at index **1**. The two groups remain alternatives rather than being
  combined into one requirement.

The existing DTE and normalized prerequisite DTO continue to preserve the complete original
group structure. The new representation retains the proficiency requirement's position within
that structure without extracting eligibility rules or parsing descriptions.

## Internal normalized representation

`FeatDefinition` now carries nullable `structuredProficiencyPrerequisites` alongside the existing
`prerequisites` field:

- `FeatPrerequisiteProficiencyGroup` identifies the original prerequisite alternative index and
  carries its ordered proficiency requirements.
- `FeatProficiencyPrerequisite` preserves the exact nullable `armor`, `weapon`, and `weaponGroup`
  values as distinct fields.

No group is emitted for an alternative without a `proficiency` field. A present empty source array
would remain an empty requirement list on its indexed group; none occurs in this corpus. Source
absence remains null at the DTE field. Category values are not normalized into enums or Class,
Item, or other catalog IDs.

Mapping reads the typed `FeatDte` directly. It does not reparse the legacy semicolon-joined
strings, inspect entries, resolve references, or evaluate whether a character satisfies a
requirement. Grant fields introduced in Batch 3A remain separate: for example, Crafter's tool
grant does not create a tool prerequisite, and Fighting Initiate's prerequisite remains distinct
from its optional-feature progression metadata.

## Effective definitions and copy/version handling

There are **no ordinary `_copy` declarations** in this source family. The 12 affected authored
roots expand to **18 effective definitions**: Strike of the Giants has six `_versions`, and each
inherits the parent's prerequisite alternatives through the existing version expander. The
other 11 roots have no versions. All nine Feat versions remain present overall; the six Strike of
the Giants versions are the affected variants.

All **314 effective Feat definitions** retain their existing stable IDs. The six affected
variants retain the same prerequisite structure and group indexes as their parent; no lineage or
expansion logic changed.

## Compatibility and publication

The existing flattened `FeatPrerequisiteDto.proficiency` strings remain exactly as before for
internal compatibility. `FeatDto`, its REST routes, public prerequisite fields and reference
projections were not changed. The new field exists only on the normalized definition and is not
included by the public `FeatDto` projection. No source prose, markup, target IDs, eligibility
results or grants are exposed.

This is a preservation fix, not a publication authorization. Any future public structured
prerequisite field requires a separate compatibility and licensing review.

## Tests and verification

`FeatStructuredPrerequisiteTests` covers every source root and category occurrence, exact category
keys/values, source alternative indexes, the Purple Dragon Commandant and Strike of the Giants
grouping cases, all 314 effective IDs, version inheritance, separation from grant mechanics,
immutability, and unchanged public JSON. The existing Batch 3A fingerprint test excludes this new
internal-only component from its old normalized-field fingerprint; its public DTO and resolver
fingerprints remain complete and unchanged.

| Check | Result |
|---|---|
| Authored affected roots / proficiency objects | **12 / 12** |
| Effective affected definitions | **18** |
| Effective catalog / Feat versions | **314 / 9** |
| Existing stable IDs and public prerequisite JSON | Unchanged |
| Resolver occurrences and outcomes | Unchanged: **6,246** total |
| Focused Feat regression tests | **30 passed** (including the 6 new tests) |
| Full Maven suite | **436 tests passed**, zero failures/errors/skips |
| Packaged executable JAR | Startup and existing Feat API projection verified |
| Source JSON | `feats.json` SHA-256 remains `381d51a5444cc5dac1e5f9af673a3fabb476662ee6229bda1a372ca49033a3b5` |
| Whitespace validation | `git diff --check` passes |

## Remaining limitations

Only values actually present in the source are preserved. Although the typed source record can
represent all three category fields together, no such combination occurs in the corpus. The
implementation does not define how multiple categories on one source object should be evaluated.
It preserves array order but does not assign additional AND/OR meaning inside a proficiency array
beyond the surrounding authored prerequisite group structure. Character eligibility, source
qualification, display text and public exposure remain out of scope.
