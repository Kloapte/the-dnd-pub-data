# Step 7 Batch 8: Internal Monster Syntax Adapter

## Scope and design

Implemented as an internal, startup-built index over original `monster` roots in
`data/monsters/bestiary-*.json`. The adapter reuses Batch 5's `AuthoredSyntaxTextWalker`,
`BalancedMarkupParser`, and `SyntaxObservationClassifier`; it introduces no parser or gameplay
semantics of its own.

`MonsterSyntaxAnnotation` associates one existing `MonsterId` and canonical `MonsterIdentity` with
an occurrence index, exact source-container field, typed context kind, optional authored feature
name and its original pointer, and the existing `SyntaxObservation`. The observation retains its
`SyntaxEvidence`, original tag and arguments, nested tags, source file SHA-256, owner and leaf JSON
Pointers, hierarchy, and UTF-16 source span. Occurrence indexes follow source-file order, source
array order, text-leaf order, then the parser's parent-before-child order.

`MonsterSyntaxContextKind` preserves `trait`, `action`, `bonus`, `reaction`, `legendary`, `mythic`,
`variant`, and `spellcasting` as distinct source contexts. Other tagged fields are marked `OTHER`
and retain their exact root field in `sourceContainer`. Feature names are copied only from the
explicit `name` field of an authored array member in one of these source containers; the original
pointer is retained and tested against the source tree.

`MonsterSyntaxAnnotationIndex` is immutable and has entries only for the 4,560 authored root IDs.
It verifies that each source owner maps to an already existing catalog Monster ID, while recording
the separately measured 4,851 effective Monster count. `findByAuthoredMonsterId` is explicitly an
authored-root lookup; generated effective versions are not represented as empty authored results.

## Source and transformation coverage

The source audit reconciles the task baseline to the pinned bytes:

| Measurement | Result |
|---|---:|
| Bestiary source files | 111 |
| Authored `monster` roots | 4,560 |
| Effective Monster definitions | 4,851 |
| Authored roots with a `_copy` declaration | 1,144 |
| Authored `_versions` definitions | 287 |
| Abstract version declarations | 1 |
| Abstract implementation entries | 5 |
| Roots with `_copy` or `_versions` | 1,228 |
| Effective definitions beyond raw roots | 291 |

The 291 additional effective definitions correspond to 286 concrete version declarations plus
the five authored abstract implementation entries. Copies change existing effective definitions
and do not create new IDs.

The authored-source walker classified **55,026** observations as `AUTHORED` and **2,508** as
`AUTHORED_TRANSFORM_INSTRUCTION`; there were zero `AUTHORED_ORIGIN_UNAVAILABLE` observations.
Those transformation observations are syntax in source `_copy`, `_mod`, `_versions`, or related
instruction payloads. They are not counted as effective bodies. There are 3,539 Monster owners
with an authored observation, 284 owners with a transform-instruction observation, and 90 owners
in both sets, for 3,733 distinct observed owners.

MonsterCopyResolver merges parent content and local modifications; MonsterVersionExpander also
creates source content for 291 effective versions, including template and interpolation paths.
The current provenance tracks identity ancestry, templates, modifier modes and version declarations,
but not per-text span transformation mappings. Consequently this batch indexes **authored source
only**. It does not copy parent observations onto children, attribute generated text to a child
source pointer, or claim a measured effective syntax total. The effective denominator is recorded
only for identity reconciliation.

## Syntax inventory

All 57,534 tags in the 4,560 bestiary roots parsed and classified. Nested tags count as independent
observations. Counts below include transform-instruction observations and are syntax totals, not
semantic effects.

### Shared observation types

| Observation class | Count |
|---|---:|
| `FormulaSyntaxObservation` | 10,423 |
| `ModifierSyntaxObservation` | 5,870 |
| `LabelSyntaxObservation` | 18,636 |
| `OpaqueSyntaxObservation` | 22,605 |

Opaque categories: 20,698 reference tags, 75 formatting tags, and 1,832 unsupported tag-family
observations. Unknown families remain exact opaque syntax. Malformed tags: zero; parsing is strict
and would reject startup with the resource and original JSON pointer rather than repair the source.

### Tag families

| Family | Count | Family | Count | Family | Count |
|---|---:|---|---:|---|---:|
| `action` | 437 | `actresponse` | 76 | `actsave` | 525 |
| `actsavefail` | 544 | `actsavefailby` | 1 | `actsavesuccess` | 248 |
| `actsavesuccessorfail` | 84 | `acttrigger` | 76 | `adventure` | 19 |
| `atk` | 4,500 | `atkr` | 982 | `b` | 3 |
| `book` | 7 | `chance` | 41 | `condition` | 5,767 |
| `creature` | 323 | `damage` | 9,574 | `dc` | 5,085 |
| `dcyourspellsave` | 1 | `dice` | 849 | `disease` | 5 |
| `feat` | 1 | `filter` | 11 | `footnote` | 1 |
| `h` | 5,499 | `hazard` | 11 | `hit` | 5,870 |
| `hityourspellattack` | 73 | `hom` | 35 | `i` | 6 |
| `item` | 1,077 | `note` | 66 | `quickref` | 179 |
| `recharge` | 907 | `sense` | 457 | `skill` | 889 |
| `skillcheck` | 6 | `spell` | 11,431 | `status` | 311 |
| `table` | 2 | `variantrule` | 1,555 | | |

Tag counts reconcile to the four observation classes above. The declared Batch 5 vocabulary
recognizes formulas (`damage`, `dice`, `scaledamage`, `scaledice`), modifiers (`hit`, `d20`),
and label families (including attack, save, trigger, response, and recharge labels). This Monster
source slice contains `damage`, `dice`, `hit`, `atk`, `atkr`, save/DC labels, and recharge among
those families. The corpus contains no `scaledamage` or `scaledice` in Monster roots. Other tags
remain opaque according to the shared classifier, even when they are references.

### Context distribution

| Source context | Observations |
|---|---:|
| `trait` | 4,618 |
| `action` | 33,775 |
| `bonus` | 1,292 |
| `reaction` | 609 |
| `legendary` | 1,356 |
| `mythic` | 45 |
| `variant` | 1,749 |
| `spellcasting` | 11,751 |
| Other exact source fields | 2,339 |

Recharge has 907 observations: 165 with zero arguments, 740 with one, and two with two. Exact
arguments and feature/container context are retained. The adapter does not calculate recharge
probabilities, make resource counters, or infer usage limits. Spell and innate-spellcasting text is
traversed in its source context; existing structured spell-usage declarations are not replaced or
duplicated as syntax observations unless they contain authored markup.

## Legendary-group boundary

The source pattern is limited to `bestiary-*.json`; it does not scan `legendarygroups.json`.
Legendary-group support remains independently identified: 188 authored group roots, 209 effective
support definitions, and 307 existing effective Monster associations. No support-body text is
duplicated into Monster observations, no support tags contribute to the 57,534 total, and no
association or resolver occurrence is changed.

## Compatibility and safety

This index is internal and startup-built. It does not modify `MonsterDefinition`/`MonsterDto`,
Monster statistics, action projections, spellcasting projections, endpoints, or reference
projections. No new reference occurrences or target kinds are created. A tag such as `damage` is
only a formula syntax observation; nearby DC, hit, condition, or spell tags remain independent.
No actor, target, damage type, success branch, timing, effect, attack sequence, or Multiattack graph
is inferred. No Monster or legendary-group prose is added to public output.

## Tests and verification

`MonsterSyntaxAnnotationCatalogTests` checks the pinned counts above, every family and context
histogram, origin split, recharge argument arities, parser observation classes, all 4,560 authored
owners, effective identity reconciliation, source file fingerprints, original pointers and UTF-16
span reconstruction, feature-name source pointers, immutability, and the existing 209 support
definitions / 307 associations. It also confirms that no effective annotations are materialized.

The full Maven package suite passes **493 tests** (484 baseline plus nine Batch 8 tests), with zero
failures, errors or skips. The isolated packaged-archive probe starts the app without a source
directory or `target/classes` fallback and reads 4,560 authored owners, 4,851 effective Monsters,
57,534 observations, 111 Monster resources, 209 legendary groups, and 307 associations directly
from the packaged classes/resources. The existing packaged application smoke also passes. Hash
comparison against the pre-Batch 8 snapshot confirms all **175 source JSON files** are unchanged.
Existing Monster identity/public serialization tests and the 6,246 resolver outcome regressions
pass. `git diff --check` and new-file whitespace checks pass.

The earlier Step 7 evidence scanners used concept-specific owner/category filters. This adapter
parses every tag in every string leaf of all authored Monster roots, including opaque
reference/formatting tags and source transformation instructions. Those selected-category totals
are therefore not interchangeable with this 57,534-tag syntax denominator; the Batch 8 regression
test pins its own scanner contract.

## Remaining limitations

- Per-text copy/version transformation provenance is not sufficient for an effective annotation
  index. Authored and transformation-instruction observations remain separate.
- Syntax recognition is not gameplay semantics. No nearest-tag relationships are created.
- Monster-to-Spell syntax is not added to the cross-catalog resolver, and no target IDs are inferred.
- Legendary-group body annotations remain separate future work.
- Internal retention does not authorize broader publication of source text or mechanics.
