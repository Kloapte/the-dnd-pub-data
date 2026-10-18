# Batch 9A — Public Class Choice Completeness

## Design checkpoint

Add `choiceProjectionStatus` to Class Feature and Subclass Feature detail DTOs, including details
returned by name. Collection summaries and parent Class/Subclass DTOs retain their current shapes.
The field is one enum value, avoiding redundant presence booleans and counts. It describes all
typed option nodes in the effective normalized feature content, not all choices mentioned in prose,
character eligibility, selected options, or reference resolution success.

| Status | Contract |
|---|---|
| `NONE` | No typed authored option node exists in this effective feature's content. |
| `COMPLETE` | Every typed option node and its relevant alternative structure/metadata survives the existing public choice projection. This does not certify completeness of feature descriptions or game rules. |
| `PARTIAL` | Some alternatives are publicly represented, but an option node, grouping, label, or required-option metadata is omitted. |
| `WITHHELD` | Typed option nodes exist, but none of their alternatives is publicly represented. |

The aggregate status covers all nested option nodes. A mixture of complete and withheld choices
is `PARTIAL`. Section wrappers within choices are conservatively incomplete because their
structure/labels are flattened by the existing mapper. Descriptive sections surrounding a choice
do not themselves make the choice incomplete. Missing reference target IDs do not imply that an
authored alternative is missing: resolution and choice projection remain separate concerns.
Importer `preserveConsumes`/`preservePrerequisite` switches are not public choice constraints.

No counts are added: `count` in the existing choice DTO remains the authored selection count,
not the number of choice nodes or alternatives. Source absence remains absence. The new field
does not need to disclose how many alternatives were withheld.

Append the enum component to both detail records. Retain their previous constructor signatures
as delegating overloads, leaving status null/omitted when callers provide no source content.
Do not guess `NONE` for those legacy Java callers. Mapper-built responses always set a status;
`withCrossCatalogReferences` preserves it. Existing record component accessors and constructor
descriptors remain available; reflective component counts/record equality necessarily change.
JSON clients must accept additive fields. No global Jackson settings change.

The existing `mechanics`, `choices`, `options`, `groups`, reference IDs and ordering remain exact.
In particular, withheld choices do not create placeholder alternatives or an empty `mechanics`
object. `@JsonInclude(NON_EMPTY)` remains in place. The only newly approved public information is
choice presence/completeness. Labels, required markers, entry bodies, ability declarations,
resolver diagnostics and source fallback behavior remain outside this batch.

## Implemented projection

**Status: IMPLEMENTED and verified.** The only modified existing production files are
`ClassFeatureDto`, `SubclassFeatureDto`, and `ClassPublicMapper`. New `ClassChoiceProjectionStatus`
and package-private `ClassChoiceProjection` types implement the bounded assessment. Existing
normalizers, reference projectors, choice DTOs and controllers are unchanged.

`ClassPublicMapper.detail` assesses normalized feature content and appends the enum to the detail
DTO. A read-only traversal collects typed option nodes through feature sections and nested
choices. It checks whether the unchanged public mapper represents alternatives, and whether
any labels, required-option values, child sections, or other choice content would be omitted.
This uses typed normalized nodes directly and performs no source parsing, resolution or gameplay
inference. It does not use a fixed list of exceptional feature names.

The following evidence reconciles every authored option node to its effective feature owner and
the packaged public projection. Tests also traverse all 2,176 Class/Subclass Feature definitions:
2,136 have no typed options, 36 have complete reference lists, two are partial, and two have
withheld alternatives. The 40 nodes each occur on one distinct feature; none is inherited by
a copy. Multiple/nested choice aggregation is tested synthetically, without claiming those
shapes occur in the pinned 40-node corpus.

## Complete source reconciliation

The source column is relative to `src/main/resources/data/classes/`. These are option-node
locators, not new catalog identities. [`verification.json`](verification.json) records the
corresponding stable feature IDs and unchanged public reference-alternative totals.

| # | Source option node | Feature | Status | Existing public reference alternatives |
|---:|---|---|---|---:|
| 1 | `class-barbarian.json#/subclassFeature/18/entries/2` | Totem Spirit (PHB) | `COMPLETE` | 5 |
| 2 | `class-barbarian.json#/subclassFeature/24/entries/1` | Aspect of the Beast (PHB) | `COMPLETE` | 5 |
| 3 | `class-barbarian.json#/subclassFeature/31/entries/1` | Totemic Attunement (PHB) | `COMPLETE` | 5 |
| 4 | `class-barbarian.json#/subclassFeature/58/entries/3` | Storm Aura (XGE) | `COMPLETE` | 3 |
| 5 | `class-barbarian.json#/subclassFeature/62/entries/1` | Storm Soul (XGE) | `COMPLETE` | 3 |
| 6 | `class-barbarian.json#/subclassFeature/67/entries/1` | Raging Storm (XGE) | `COMPLETE` | 3 |
| 7 | `class-bard.json#/subclassFeature/36/entries/2` | Blade Flourish (XGE) | `COMPLETE` | 3 |
| 8 | `class-bard.json#/subclassFeature/38/entries/1` | Fighting Style (XGE) | `COMPLETE` | 2 |
| 9 | `class-cleric.json#/classFeature/24/entries/1/entries/0` | Divine Order (XPHB) | `COMPLETE` | 2 |
| 10 | `class-cleric.json#/classFeature/35/entries/1/entries/0` | Blessed Strikes (XPHB) | `COMPLETE` | 2 |
| 11 | `class-druid.json#/classFeature/21/entries/1/entries/0` | Primal Order (XPHB) | `COMPLETE` | 2 |
| 12 | `class-druid.json#/classFeature/30/entries/1/entries/0` | Elemental Fury (XPHB) | `COMPLETE` | 2 |
| 13 | `class-fighter.json#/classFeature/0/entries/1` | Fighting Style (PHB) | `COMPLETE` | 11 |
| 14 | `class-fighter.json#/subclassFeature/9/entries/2/entries/0` | Maneuver Options (TCE) | `COMPLETE` | 7 |
| 15 | `class-fighter.json#/subclassFeature/10/entries/1` | Maneuvers (PHB) | `COMPLETE` | 16 |
| 16 | `class-fighter.json#/subclassFeature/45/entries/5` | Psionic Power (TCE) | `COMPLETE` | 3 |
| 17 | `class-fighter.json#/subclassFeature/48/entries/2` | Telekinetic Adept (TCE) | `COMPLETE` | 2 |
| 18 | `class-fighter.json#/subclassFeature/55/entries/5` | Rune Carver (TCE) | `PARTIAL` | 6 |
| 19 | `class-fighter.json#/subclassFeature/66/entries/3` | Arcane Shot Options (XGE) | `COMPLETE` | 8 |
| 20 | `class-fighter.json#/subclassFeature/92/entries/1` | Arcane Shot Options (AU) | `COMPLETE` | 8 |
| 21 | `class-fighter.json#/subclassFeature/107/entries/1` | Maneuver Options (XPHB) | `COMPLETE` | 20 |
| 22 | `class-monk.json#/subclassFeature/8/entries/1` | Elemental Disciplines (PHB) | `PARTIAL` | 17 |
| 23 | `class-monk.json#/subclassFeature/45/entries/1` | Tipsy Sway (XGE) | `COMPLETE` | 2 |
| 24 | `class-monk.json#/subclassFeature/51/entries/2` | Path of the Kensei (XGE) | `COMPLETE` | 2 |
| 25 | `class-paladin.json#/classFeature/3/entries/1` | Fighting Style (PHB) | `COMPLETE` | 7 |
| 26 | `class-ranger.json#/classFeature/4/entries/1` | Fighting Style (PHB) | `COMPLETE` | 7 |
| 27 | `class-ranger.json#/subclassFeature/14/entries/1` | Hunter's Prey (PHB) | `COMPLETE` | 3 |
| 28 | `class-ranger.json#/subclassFeature/17/entries/1` | Defensive Tactics (PHB) | `COMPLETE` | 3 |
| 29 | `class-ranger.json#/subclassFeature/21/entries/1` | Multiattack (PHB) | `COMPLETE` | 2 |
| 30 | `class-ranger.json#/subclassFeature/26/entries/1` | Superior Hunter's Defense (PHB) | `COMPLETE` | 3 |
| 31 | `class-rogue.json#/subclassFeature/27/entries/5` | Psionic Power (TCE) | `COMPLETE` | 2 |
| 32 | `class-rogue.json#/subclassFeature/31/entries/2` | Soul Blades (TCE) | `COMPLETE` | 2 |
| 33 | `class-sorcerer.json#/classFeature/5/entries/2` | Metamagic (PHB) | `COMPLETE` | 8 |
| 34 | `class-sorcerer.json#/classFeature/6/entries/2` | Metamagic Options (TCE) | `COMPLETE` | 2 |
| 35 | `class-sorcerer.json#/classFeature/24/entries/1` | Metamagic Options (XPHB) | `COMPLETE` | 10 |
| 36 | `class-warlock.json#/classFeature/3/entries/1` | Pact Boon (PHB) | `COMPLETE` | 4 |
| 37 | `class-warlock.json#/classFeature/18/entries/1` | Eldritch Invocation Options (XPHB) | `COMPLETE` | 28 |
| 38 | `class-warlock.json#/subclassFeature/30/entries/4` | Genie's Vessel (TCE) | `COMPLETE` | 2 |
| 39 | `class-wizard.json#/subclassFeature/27/entries/1` | The Third Eye (PHB) | `WITHHELD` | 0 |
| 40 | `class-wizard.json#/subclassFeature/58/entries/1` | Master Transmuter (PHB) | `WITHHELD` | 0 |

## Public examples and compatibility

The following are excerpts from feature detail responses; other response properties are retained:

```json
{"name":"Spellcasting","choiceProjectionStatus":"NONE"}
{"name":"Totem Spirit","choiceProjectionStatus":"COMPLETE"}
{"name":"Rune Carver","choiceProjectionStatus":"PARTIAL"}
{"name":"Elemental Disciplines","choiceProjectionStatus":"PARTIAL"}
{"name":"The Third Eye","choiceProjectionStatus":"WITHHELD"}
{"name":"Master Transmuter","choiceProjectionStatus":"WITHHELD"}
```

The Third Eye and Master Transmuter continue to omit `mechanics`: no placeholders, section
labels, new option entries, prose or alternatives are added. Their status now communicates that
typed choices exist. Rune Carver and Elemental Disciplines retain exactly their existing reference
lists, counts, ordering, names and IDs, and now disclose that their choice projections are partial.
The 11 withheld reference labels and one required-option marker remain withheld.

`NONE` means absence of typed option nodes, not that prose cannot mention choices. `COMPLETE`
means completeness of these typed choice nodes in the existing public representation, not that
the feature is an executable rule or that every prerequisite in surrounding prose is published.
The status is independent of reference resolution. The two XGE-owned unqualified Fighting Style
references retain their existing PHB fallback and target IDs.

The retained overloads preserve previous direct Java constructor signatures, including the
previous serialized shape for manually constructed DTOs whose status is unknown. Legacy JSON
without the field deserializes with a null status. All mapper-generated feature details contain a
non-null enum, including detail-by-name and service reference-decorated responses. Record
component introspection, generated equality/hash codes and toString now include the new component;
those aspects cannot be identical after a record extension. Strict JSON consumers must accept
the authorized additive property. Existing summary and parent Class/Subclass responses do not
receive the new field.

## Publication boundary and remaining questions

This batch publishes only a four-valued completeness assessment. It does not publish choice
counts, newly normalized ability declarations, labels, a required flag, entry text, importer
flags, candidate lists or diagnostics. The existing 222 choice references remain byte-for-byte
equivalent in legacy public JSON, including 60 Optional Feature UIDs using the established PHB
default. No reference occurrences or target kinds are introduced.

Separate review is still required for the Rune Carver and Elemental Disciplines labels/required
marker and for source provenance of the XGE Fighting Style fallback. Resolving those questions
is not necessary to report `PARTIAL` honestly. No Batch 9B work was started.

## Tests and verification

`ClassChoiceCompletenessTests` adds 11 tests covering all 40 source option nodes and all 2,176
effective feature definitions. It checks the four public statuses, both withheld Wizard features
through ID and by-name routes, both partial metadata cases, a complete choice's selection count
versus alternatives, all 222 existing reference entries and their order/IDs, the two XGE source
fallbacks, typed ability nodes without choices, multiple/nested choices, flattened groups,
explicit false required metadata, same-name labels, unresolved alternative identity, legacy
constructors/JSON, immutable lists, deterministic serialization, and meaningful false/zero values.

The baseline was captured before production edits using the packaged application.
`ClassChoiceRuntimeProbe.java` hashes all 2,536 Class-domain public details through `ClassesService`,
including existing cross-catalog projections, after removing only `choiceProjectionStatus`.
The same hash is verified by a regression test and the rebuilt packaged application. Separate
hashes cover normalized Class-domain values (excluding importer provenance sets), stable IDs,
and every resolver result. The previous Class-domain normalization/identity/graph fingerprint
tests also continue to pass, including provenance. Baseline and packaged reports are retained as
[`baseline-runtime.json`](baseline-runtime.json) and [`packaged-runtime.json`](packaged-runtime.json).

Full Maven `package`: **504 tests, zero failures/errors/skips**, from the 493-test baseline plus
11 new tests. The isolated `ClassChoiceJarProbe.java` loads the probe and packaged classes/resources
using a platform-parent classloader with no source directory or `target/classes` fallback; Class
JSON resources resolve with a `jar:` URL. Its public, normalized, identity and resolver hashes
match the baseline. All **175 source JSON hashes** and the original Batch 9/Entry assessment
evidence match the pre-implementation manifest. Existing src files changed only in the three
approved public projection files. Catalog loaders, normalizers, source files and existing tests
retain their pre-implementation hashes; other catalog identities/mechanics are unaffected.

`git diff --check` and new-file whitespace checks pass. Verification commands, run at repository
root (the local Maven cache path may be adapted):

```powershell
mvn -o -Dmaven.repo.local=target/m2 package
$probeClasspath = (Resolve-Path target/classes).Path + ';' + (Get-Content target/analysis-classpath.txt -Raw).Trim()
javac -proc:none -cp $probeClasspath -d target/batch-9a-probe analysis/mechanics/batch-9a-class-choice-completeness/ClassChoiceRuntimeProbe.java
java analysis/mechanics/batch-9a-class-choice-completeness/ClassChoiceJarProbe.java target/dnd-pub-data-0.0.1-SNAPSHOT.jar target/batch-9a-probe analysis/mechanics/batch-9a-class-choice-completeness/packaged-runtime.json
node analysis/mechanics/batch-9a-class-choice-completeness/verify.mjs
git diff --check
```

The probe folder must exist before compiling. `target/analysis-classpath.txt` contains the build
dependency classpath; it can be regenerated with Maven's dependency classpath tooling when needed.
The verifier compares the retained baseline fingerprints and immutable evidence rather than
rewriting the original Batch 9 scanner output.
