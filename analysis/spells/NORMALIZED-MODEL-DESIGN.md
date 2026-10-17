# Normalized Spell model design and Stage B result

Keep a Spell as a reusable, source-aware definition. Build Stage B around immutable typed values, with distinct source metadata and authored mechanic values. Preserve all root source records and descriptive entry trees internally; do not use a generic effect engine.

## Definition and references

`SpellDefinition` should contain stable ID, original name, primary source, optional page/level/school, typed casting time alternatives, range, components, duration, optional ritual metadata, source classifications, structured mechanic tags, scaling, additional/higher-level entry content, and source/reprint relationships. Use dedicated `SpellIdentity` and `SpellReference` records. An explicit UID carries the source when authored; an unqualified name remains source-less. Keep aliases separate from canonical identity.

Source references should distinguish primary source, additional/other source page locators, reference-source codes, and reprint edges. There are no spell copies or versions in the pinned source; provenance should not imply a parent where none exists.

## Typed values supported by evidence

- Casting time is an ordered immutable list of values with numeric amount, unit code, optional condition, and optional note.
- Range has geometry/type plus an optional distance with open type code and optional numeric amount.
- Components preserve verbal/somatic/royal flags independently, and material as text or an object containing text, optional cost, and boolean/string consume variant.
- Duration is an ordered list of type, optional amount/unit/up-to, optional ends, and optional concentration.
- Scaling preserves object/list forms and string formulas with original level keys.
- Structured attack/save/damage/condition/creature-type tags remain classifications, not fully executable effects.
- Spell-list grants/selectors remain in their owner domain and refer to a `SpellReference` only when an individual source reference exists.

Use immutable lists and explicit unions; never represent known source forms as `Object`, `JsonNode`, or `Map<String,Object>`. Keep extension/failure policy strict for new shapes. Do not invent defaults for absent ritual, concentration, components, or source.

## Not normalized as mechanics

No numeric healing/damage/save DC model is justified by root fields. Preserve the recursive entries and higher-level descriptions internally, but do not derive mechanical effects from prose or raw tags. Do not calculate scaling, spell slots, targets, duration expiry, eligibility, or class preparation. No database or character state belongs in this pipeline.

Stage B implemented this design in immutable `SpellDefinition` and typed value records under `com.thedndpub.data.dto.spell`. `SpellSourceLoader` and `SpellDefinitionNormalizer` load and normalize all 969 roots. Full implementation details and validation evidence are in [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md). Public projections and catalogs remain Stage C work.
