# Proposed Spell Mechanics Extraction Design

## Boundary

Do not expose a tag occurrence as a verified spell mechanic. Introduce an **internal annotation extraction** stage after typed source deserialization and before any future semantic projection:

`Typed entries → markup annotations with locations → optional semantic verification/curation → public structured mechanics`

The first stage is syntax-preserving and does not decide what the spell does. A later reviewed semantic layer may associate annotations with named effect clauses, targets, timing, conditional branches, damage type, or scaling. Character-specific calculations remain outside both layers.

## Suggested immutable records

Use one public top-level type per file and typed sealed variants where appropriate:

- `SpellMarkupLocation`: root spell ID, entry path, higher-level-entry flag, table/row/cell coordinates, and stable text-fragment ordinal.
- `SpellMarkupAnnotation`: location plus a sealed `SpellMarkupValue` and the exact tag name.
- `TaggedFormulaAnnotation`: formula-token representation plus original argument string and tag kind (`DICE`, `DAMAGE`, `SCALEDICE`, `SCALEDAMAGE`).
- `TaggedScalingAnnotation`: base expression, authored level/range argument, increment expression, and original argument sequence. Keep all arguments even if future parsers recognize a subset.
- `TaggedReferenceAnnotation`: typed reference kind and authored label/source arguments for condition, spell, action, rule term, item, and other references.
- `SemanticAssociation`: optional reviewed association to a spell effect clause, target role, outcome branch, and confidence state (`UNREVIEWED`, `AMBIGUOUS`, `VERIFIED`). This record belongs to curated semantic metadata, never inferred solely from a tag name.

Formula token parsing may recognize integer, dice-count, die-size, sign, and modifier tokens. It must preserve the original expression and reject malformed syntax. It should not evaluate formulas or infer their purpose.

## Context retention

Store source paths, parent entry titles where available, higher-level section, table position, fragment order, neighboring source-node references, and the fact that a tag was nested in another tag. Avoid storing a lossy flattened prose string as the only context. Internal extraction may point back to typed entry nodes; it must not cause those nodes to enter public DTOs.

The scanner found 223 spells with multiple damage tag occurrences and 86 with multiple distinct damage formula arguments. TCE Booming Blade demonstrates that formulas can belong to on-hit, movement, and level-dependent clauses. A flat `List<String>` of formulas would misrepresent their association.

## Safe and unsafe projection

The extraction layer should produce **generic source annotations only**. Do not expose formulas publicly until a semantic association is verified. Safe future public mechanics should be a separate curated/verified projection with explicit effect role and target/branch relationships. The current Stage C structured classifications remain valid and independent.

Do not use arbitrary keyword windows to identify “healing,” “damage,” or “conditional.” The scanner's 32 healing-related dice candidates, 349 potentially conditional/nested/multiargument formula contexts, and 157 prose-only mechanic candidates are discovery leads, not labels.

## Curation integration boundary

If generic annotations are insufficient, add an optional classpath-curated overlay keyed by stable spell ID plus source-entry location. It should only add semantic relationships that are reviewed against source evidence; source text remains the authority for the annotation, and the overlay must not mutate Stage B definitions or collapse editions. Validate that curated locations exist and that formulas match the referenced markup. Keep overlay provenance internal.

No mechanics extraction or overlay is implemented in Stage D. This document proposes a design for review.
