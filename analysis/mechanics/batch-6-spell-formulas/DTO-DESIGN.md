# Batch 6 Spell formula adapter design checkpoint

## Design checkpoint and implementation decision

Add an immutable, internal `SpellFormulaAnnotationIndex` keyed by the existing `SpellId`. Build
it from the pinned raw Spell source resources, and validate each authored `(name, source)` against
the already normalized Spell catalog. The index is supplemental: do not add annotations to
`SpellDefinition`, change its constructors or normalizer, or pass annotations to public mappers.
This keeps public JSON and established normalized mechanics independent from syntax evidence.

Each `SpellFormulaAnnotation` records its existing Spell ID, an ordinal scoped to that source
root, a small section classification (`ENTRIES`, `ENTRIES_HIGHER_LEVEL`, or
`OTHER_SOURCE_FIELD`), and the shared `FormulaSyntaxObservation`. The observation retains the
shared `MarkupTag`, `SyntaxEvidence`, raw arguments, complete tag text, absolute UTF-16 span,
source owner, exact JSON Pointer, entry hierarchy and `SyntaxOrigin`. No new catalog identity is
created. The original source path and exact-byte SHA-256 identify the evidence snapshot.

The adapter reads original JSON so Jackson DTE wrapper types cannot invent JSON paths. It reuses
`BalancedMarkupParser`, `AuthoredSyntaxTextWalker` and `SyntaxObservationClassifier`; it adds no
second parser or formula grammar. The walker traverses a Spell source root in authored property/
array order, and observations retain parser preorder. The immutable index retains empty lists for
Spells with no observed formula tags and an all-Spell source root count, so no-formula coverage is
explicit. A global ordered annotation list provides deterministic corpus ordering.

The source also has 18 explicit `hit`, `d20` and `dc` tags. The index preserves their existing
shared modifier/label observations in a separate auxiliary collection. They are not associated
with adjacent formula tags.

## Source coverage evidence to reconcile

The existing Stage D scan reports 969 Spell roots in 18 files; 422 roots contain 930 formula-tag
occurrences. Its four observed families are:

| Family | Authored occurrences | Authored Spell owners |
|---|---:|---:|
| `damage` | 642 | 362 |
| `dice` | 126 | 94 |
| `scaledamage` | 148 | 143 |
| `scaledice` | 14 | 14 |
| **Total** | **930** | **422** |

All `scaledamage` and `scaledice` observations occur in higher-level entries (148 and 14);
`damage` has 82 higher-level observations; `dice` has six. The previous entry scan found 19
formula tags in tables and no nested occurrences for these families. The implementation
reproduces the 930 formula occurrences and 422 owners using the whole-root walker. It found zero
formula-family tags in transform instruction paths, zero outside `entries` or
`entriesHigherLevel`, and zero formula tags nested inside other markup. The separate 18 label
and modifier observations are not included in the formula total. Effective formula annotations
are not materialized or counted by this adapter.

## Context and origin boundaries

Section classification comes only from the exact JSON Pointer path under the Spell owner. Entry
hierarchy and typed `type` labels are copied from the shared source walker. No nearby words are
used to name a condition, target, branch or effect. Source pointers preserve table/row/cell,
named-entry and nested-list context without copying any descriptive paragraph into evidence
reports.

Occurrences beneath `_copy`, `_mod`, `_versions`, `_abstract`, `_implementations` or `_templates`
retain the shared `AUTHORED_TRANSFORM_INSTRUCTION` origin. This describes the location of the
source syntax only; it does not claim how a transform resolves or which effective definitions
inherit it. Formula text in ordinary authored source is `AUTHORED`. No expansion is performed.

## Explicit exclusions

The adapter recognizes formula syntax and preserves source context. It does not make
`DamageEffect`, `HealingEffect` or scaling outcome objects. It does not infer damage type,
receiver, targets, attacks, projectile count, saving throw branch, condition, temporary hit points,
duration, automatic scaling, or spell execution. Existing Spell fields, including structured
scaling, range geometry, and all current metadata, remain authoritative and unchanged. Public
DTOs, endpoints and the shared reference resolver are not modified.
