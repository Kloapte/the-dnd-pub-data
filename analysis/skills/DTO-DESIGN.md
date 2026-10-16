# Skills DTO and domain design

## Source-backed model

The dataset contains 36 `(name, source)` definitions, split between PHB and XPHB. Each source record explicitly provides a page and ability code. The ability code is normalized to a closed `SkillAbility` enum for the currently observed vocabulary (`STR`, `DEX`, `INT`, `WIS`, `CHA`), and the public value serializes in source-style lowercase. Unknown future values fail mapping with a field-specific error instead of being inferred.

Definitions use IDs in the existing source-aware slug convention: `skill:<lowercase source slug>:<lowercase name slug>`, e.g. `skill:phb:acrobatics`. Same-named PHB and XPHB definitions remain separate. `SourceIdentity` represents the source identity and `SourceReference` adds the page locator. Both use the shared Source Registry model.

## Typed import and provenance

- `SkillsFileDte` contains the root `skill` collection.
- `SkillDte` models the required identity, page, ability, and entry fields, and optional source availability/reprint fields.
- `entries` uses the existing sealed `EntryDte` hierarchy. The corpus has only strings plus one `list` object, but using the shared typed hierarchy preserves the unusual list shape and gives strict validation of supported entry types.
- `SkillMapper` produces `SkillDefinition` and `SkillImportProvenance` separately. Provenance retains narrative `entries`, reprint targets, and the edition-specific `srd`, `basicRules`, and `srd52` flags. These source fields do not become skill mechanics.

## Normalized definitions and public projection

`SkillDefinition` contains `SkillId`, display name, shared `SourceReference`, and explicit `SkillAbility`. It is independent of any character or creature's state.

- `SkillDto` is the detail projection: ID, name, source reference with page, and the source-defined ability association.
- `SkillSummaryDto` exposes the same stable identity and compact source/ability data for list results.
- Public records use `@JsonInclude(NON_EMPTY)`. Descriptions, raw markup, availability flags, and import provenance stay internal.

The descriptions may contain publication-controlled prose, so they are not exposed pending a publication decision. Reprint references are kept internally and validated against source identities and the normalized `(name, source)` catalog. The API does not merge or redirect reprinted definitions.

## Character mechanics and cross-entity boundaries

The source confirms a skill-to-ability relationship for every entry. It does not encode proficiency, expertise, ability modifiers, bonuses, or a skill-check operation. These remain separate future concepts: a skill definition is reusable; proficiency/expertise belongs to a character or creature; and a check is an event/configuration involving an ability, proficiency, and modifiers. No character mechanics engine or Ability Score catalog is warranted by this corpus.

Monster source contains `{@skill ...}` references and creature skill values. Those are not merged into skill definitions. Future background/class/feat relationships should reference the stable source-qualified skill identity and model granted proficiency separately.

## Catalog and API behavior

The catalog loads `data/skills.json` once from the classpath, validates all source references through `SourceRegistry`, validates reprint targets, and indexes unique IDs and case-insensitive `(name, source)` pairs. Search is deterministic. The name endpoint returns a list because the same name has distinct PHB and XPHB definitions; an optional source filter disambiguates it.

- `GET /api/skills?name=&source=` returns filtered summaries.
- `GET /api/skills/{id}` returns one detail DTO.
- `GET /api/skills/by-name/{name}?source=` returns matching detail DTOs; omitting source preserves all source-specific matches.

The fixed 36-record collection is small, so pagination is unnecessary. Collection results are always sorted by name, source, then ID.
