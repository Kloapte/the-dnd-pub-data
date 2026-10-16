# Action DTO design

## Domain boundary

Implement reusable, source-specific Action Definitions in their own catalog. Do not model an action granted to a creature, an action performed in combat, turn tracking, or monster stat-block features. The source includes one action collection; it does not claim to enumerate every possible D&D action.

The action source explicitly gives a `time` property as an array of either `{number, unit}` values or strings (`Free`, `Varies`). Keep this as timing data without introducing an `ActionCost` enum or inferring action economy from prose. The normalized `ActionTiming` uses optional number/unit/special components and occurs in a list to preserve alternatives. Structured units remain their exact source strings. Public API may expose these evidenced values.

## Proposed types

- `ActionsFileDte` and `ActionDte` model the root and source record, including all optional flags, `fromVariant`, `seeAlsoAction`, entries, and times.
- `ActionTimeDte` is a sealed source union: `ActionTimeUnitDte(number, unit)` for object values and `ActionTimeSpecialDte(value)` for textual values, with strict custom deserialization.
- `ActionReprintDte` is a typed union for string UIDs and `{uid, tag}` objects. The normalized `ActionReference` retains target name, shared `SourceIdentity`, and optional source tag. A `variantrule`-tagged UID is retained as such rather than asserted to be another Action Definition.
- `ActionDefinition` is immutable and contains `ActionId`, name, primary `SourceReference`, timings, and explicit reprint references.
- `ActionImportRecord` pairs the definition with `ActionImportProvenance`: typed entries, `seeAlsoAction` strings, `fromVariant`, and availability flags.
- Public API has `ActionDto` and `ActionSummaryDto`. Detail exposes ID, name, primary source/page, structured timing alternatives, and explicit reprint references. Summary exposes ID, name, and source.

IDs use `action:<source-slug>:<name-slug>`, keeping PHB and XPHB definitions separate. `seeAlsoAction` remains provenance because source is not consistently explicit. References in prose are not converted into mechanics or cross-entity DTOs.

## Exclusions and open mechanics

Raw entry prose, markup, availability flags, source lineage, related-action text, and import diagnostics are excluded from public output. Skill, condition, status, item, spell, book, variant-rule, table, and quick-reference markup remain unparsed entry text. Prerequisites, checks, targeting, range, duration, effects, and action availability lack consistent structured properties. Monster `actions`, `bonusActions`, `reactions`, and legendary features remain monster-specific features and are not linked by matching names.

