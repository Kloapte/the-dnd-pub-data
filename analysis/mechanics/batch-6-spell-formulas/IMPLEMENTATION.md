# Step 7 Batch 6: internal Spell formula adapter

Implemented as a separate startup-loaded internal syntax index. The adapter reuses the shared
Batch 5 evidence and syntax package. It does not change Spell normalization, public DTOs, REST
routes or reference resolution.

## Adapter and index

`SpellFormulaAnnotationAdapter` walks the original `data/spells/*.json` resources using
`AuthoredSyntaxTextWalker`, then calls `BalancedMarkupParser` and
`SyntaxObservationClassifier`. It validates every authored source root against the existing
normalized Spell ID set before indexing an observation. It reads the 18 resources during startup;
source JSON is not parsed on request.

`SpellFormulaAnnotation` stores the existing source-aware `SpellId`, a zero-based per-Spell
formula occurrence index, a source section and the shared `SyntaxObservation`. The section comes
from the exact JSON Pointer path and is one of `ENTRIES`, `ENTRIES_HIGHER_LEVEL` or
`OTHER_SOURCE_FIELD`. The observation preserves its `SyntaxSourceOwner`, resource SHA-256,
original JSON Pointer, entry hierarchy/type labels, `SyntaxOrigin`, UTF-16 span, exact tag text,
tag name, raw arguments, argument segments and nested tag boundaries.

`SpellFormulaAnnotationIndex` retains all 969 source Spell IDs, including an empty formula list
for each Spell without a formula tag. Its ordered global list follows sorted source resource
order, source array order, source text traversal order and parser preorder. Per-Spell indexes and
nested lists are immutable. `SpellFormulaAnnotationCatalog` exposes the internal index to
backend code; no public controller or DTO uses it.

The shared classifier also finds 18 directly authored Spell modifier/label tags. These are
preserved in a separate `SpellSyntaxAnnotation` collection: five `hit` modifier observations,
two `d20` modifier observations and eleven `dc` label observations. The adapter does not associate
them with nearby formula observations.

## Source coverage

The source audit and full-corpus tests reconcile the existing Stage D denominator:

| Formula tag | Occurrences | Spell owners |
|---|---:|---:|
| `damage` | 642 | 362 |
| `dice` | 126 | 94 |
| `scaledamage` | 148 | 143 |
| `scaledice` | 14 | 14 |
| **Total / union of owners** | **930** | **422** |

The formula tags occur in **12 of 18** Spell source files. `damage` has 82 occurrences in
higher-level entries and `dice` has six. Every `scaledamage` and `scaledice` tag occurs in a
higher-level entry, 148 and 14 respectively. The remaining 680 formula occurrences are under
`entries`; 31 formula tags occur in table content (19 `damage`, 12 `dice`). These formula tag
families have no nested markup children in this corpus. Entry nodes themselves may be nested;
their full pointer and hierarchy are retained.

The whole-root adapter observed 969 source roots and reconciled each to one existing Spell ID.
Formula counts in authored transform payloads: **zero**. Formula tags outside `entries` and
`entriesHigherLevel`: **zero**. Every observed formula is classified by the shared vocabulary as
a `FormulaSyntaxObservation`; modifier/label occurrences remain separate. Stage D and the new
adapter agree on all four formula family and owner counts. The Batch 5 scanner's broader source
scope also confirms the original locations and spans; it found no discrepancy for these Spell
formula families.

The existing normalized Spell catalog contains **969 definitions**. This adapter does not
expand formula annotations through copy/version resolution and does not materialize an effective
annotation index. The effective-definition formula occurrence total is therefore **not measured
here**. It is not reported as zero or inferred to equal the authored 930.

The existing 41 normalized `scalingLevelDice` definitions and 78 explicit non-point numeric
range geometries remain unchanged and separately authoritative.

## Context, origin and examples

The exact JSON Pointer separates main entries from higher-level entries and retains paths through
named entry blocks, nested lists and tables. Source order is preserved; identical formula text at
distinct source paths remains two observations. Source IDs retain edition distinctions, including
PHB/XPHB Fireball, Healing Word, False Life and Magic Missile. Regression coverage also includes
PHB Scorching Ray and TCE Booming Blade.

The required examples remain syntax-only:

- Healing Word retains the authored `1d4` formula token; its spellcasting-ability modifier is
  prose, not an evaluated numeric value.
- Magic Missile retains `1d4 + 1`; the adapter creates no projectile count, target distribution
  or total damage.
- Fireball retains its formula tag; no target, damage type, save outcome or complete damage
  effect is constructed.
- Booming Blade formulas retain their distinct source entry pointers; conditional descriptions
  are not merged into a single effect.
- Shield remains indexed with an empty formula list, demonstrating that formulas are not
  synthesized from prose.

`SyntaxOrigin` is carried from the shared walker. Authored formula occurrences are 930;
transform-instruction formula occurrences are zero; unavailable-origin formula occurrences are
zero. No transformed or effective lineage is fabricated. Formula payloads, including unsupported
or symbolic expression shapes if encountered, retain the shared observation's original tag and
arguments. Expressions are not evaluated.

## Compatibility and verification

No existing `SpellDefinition`, `SpellMechanics`, `SpellScaling`, DTE, normalizer, public mapper,
controller or resolver code changed. The new Spring component depends on the existing `SpellCatalog`
and validates stable IDs; public mapping never receives the syntax index. Existing Spell metadata
and serialized responses remain governed by their current tests.

Six focused corpus tests cover the 969 roots, exact 930 formula occurrences and 422 owners, all
four tag families, auxiliary labels/modifiers, source identity and SHA-256, pointer/span
reconstruction, higher-level and table contexts, repeated formulas at different locations,
immutable indexes, no-formula Spells, edition distinctions, no inferred effects and the 41/78
structured metadata counts.

Verification:

- Focused adapter tests: **6 passed**.
- Full Maven package: **470 tests passed**, zero failures/errors/skips (464 baseline plus six).
- Packaged executable JAR starts with the new startup index and existing catalog smoke checks
  pass.
- Batch 5's full-corpus test verified unchanged hashes for all 175 JSON resources and exact
  source span reconstruction.
- Existing tests retain all **6,246** resolver outcomes and Spell public API contracts.
- `git diff --check` passes.

## Limitations

The index is source-authored, not a copy-expanded effective annotation model. Any future
copy/version transformation that introduces or relocates formula text requires explicit lineage
and coverage analysis before materializing effective observations. Only the four formula
families plus the observed shared modifier/label syntax are indexed; other references and
formatting tags stay in existing source entries. The index does not associate a formula with
damage, healing, a target, an attack, a save branch, a condition, a duration or execution
behavior.

This completes the separate Spell syntax adapter. Legendary-group preservation remains the next
assessment investigation; Monster annotations and gameplay effect models are not part of this
batch.
