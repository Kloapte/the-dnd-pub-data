# Batch 5 internal design checkpoint

The user authorizes implementation after this design checkpoint. This is an internal library, not a new catalog/public DTO or a materialized Spell/Monster annotation layer.

## Evidence

Use resource path, optional exact-byte SHA-256, root collection and an owner RFC 6901 JSON Pointer. The owner pointer identifies an authored source occurrence, not a new catalog identity. A text location adds its exact leaf pointer, ordered ancestor contexts (pointer and authored `type`, if any) and an origin classification. Repeated identical strings at different pointers remain different occurrences.

Origins distinguish ordinary authored source, authored transformation instructions and unavailable authored origin. The authored walker never claims effective lineage. A caller supplying already transformed text must explicitly use unavailable origin unless separately verified source evidence exists.

Spans use Java String UTF-16 offsets: start inclusive, end exclusive, measured in the JSON-decoded original string. A tag carries the exact substring and location/span. No code-point conversion or source-file byte-offset claim is made.

## Parser

A strict, stateless balanced parser returns ordered top-level tags with immutable nested children, plus a deterministic preorder flattening. It balances all braces within a tag, splits only top-level pipes and retains exact tag spelling, exact argument suffix and all argument segments (including empty/trailing segments). Only separator whitespace before the payload is excluded from segments; the exact suffix and complete original token retain it.

Tag names use ASCII letters/digits/underscore/hyphen. A leading separator is whitespace or a pipe. Invalid names, missing closing braces and nesting above the explicit safety limit fail with a location/span-bearing exception; no partial successful result or repair is returned. Unmatched ordinary braces outside a tag remain ordinary text. Unknown balanced tags parse normally.

The source audit finds zero backslashes inside tags, zero whitespace-only argument suffixes, zero directly adjoining pipe suffixes and zero non-ASCII separators. JSON escaping is decoded by Jackson before parsing. The parser does not assign renderer escape semantics to backslashes: braces and pipes remain delimiters. Synthetic tests document this boundary rather than asserting unobserved renderer behavior.

## Observation vocabulary

Generic parsing is separate from classification. Initially recognize 21 tag names:

- Formula: `dice`, `damage`, `scaledice`, `scaledamage`.
- Modifier token: `hit`, `d20`.
- Explicit label: `atk`, `atkr`, `h`, `hom`, `dc`, `recharge`, `actSave`, `actSaveFail`, `actSaveSuccess`, `actSaveSuccessOrFail`, `actSaveFailBy`, `actTrigger`, `actResponse`, `hitYourSpellAttack`, `dcYourSpellSave`.

Classification is case-insensitive; authored spelling is preserved. Formula/modifier values are characterized syntactically as signed integer, unsigned integer, bounded dice notation or opaque/symbolic expression. Integer tokens are not evaluated as modifiers; no dice evaluation or arithmetic simplification occurs. Every extra argument stays on the parsed tag.

Other tags become opaque reference, formatting or unsupported observations. Those categories do not parse UIDs, resolve targets or imply mechanics. Known-family classification remains syntax only: save labels do not impose saves; damage tags do not create DamageEffect; recharge has no inferred default.

## Traversal

Use a generic authored-JSON text-leaf walker accepting an explicitly supplied owner and a callback. It preserves original object/array hierarchy, source type labels, field/array positions and RFC 6901 escaping. It accounts for shared/Class/Monster/dedicated content through the original source representation, without serializing DTE wrappers back into fabricated source paths.

The walker is not a schema importer or catalog loader. `JsonNode` is restricted to this generic infrastructure input; evidence/observation records have typed fields. No mutable global registry, Spring bean, catalog mutation or per-request work is introduced. Existing reference extraction stays untouched.

## Compatibility and validation

New Java types live in `com.thedndpub.data.mechanics.syntax`; none are reachable from public DTOs. Existing normalized definitions and APIs remain untouched. Full-corpus tests independently compare all per-tag counts and exact source spans with the assessment denominators, separating definition/support role and transformation instructions. Homecraft stays excluded; effective coverage is explicitly unmeasured.

Focused tests cover immutable carriers, malformed input, Unicode offsets, arguments/nesting, unknown syntax, expressions and generic traversal. Existing regression fingerprints and the full Maven suite protect previous normalization/public/reference contracts. Packaged-JAR verification checks classpath startup and isolated loading of the new parser classes.
