# Monster DTO implementation

## Scope and pipeline

The normalized public definition is implemented in `com.thedndpub.data.dto.monster`. `MonsterMapper` consumes `ResolvedMonster.content()` directly; it never reconstructs the post-resolution object from `MonsterDte`. The mapping chain is:

`MonsterDte → MonsterCopyResolver → MonsterVersionExpander → ResolvedMonster → MonsterMapper → MonsterDto`

The mapper reads a defensive copy of the resolved tree and does not mutate it. It rejects unexpected root properties and any active `_copy`, `_versions`, `_mod`, or `_templates` instruction. Jackson trees are used only inside mapping code. The public DTO API is composed of records, typed maps, and sealed recursive content/cell/value interfaces; it has no `Object`, `Map<String,Object>`, or `JsonNode` fields.

## Record groups

`MonsterDto` groups its normalized fields into `Identity`, `Statistics`, `Defenses`, `Features`, `Combat`, spellcasting blocks, and `Catalog`. The nested records model identity/source links, polymorphic creature type and alignment, AC/HP/movement/CR, skill alternatives, recursive defense clauses, feature groups, spell levels and use buckets, equipment references, and rules availability. Ability and feature categories use closed enums only for stable vocabularies; open source vocabularies remain strings. Source classification tags are retained separately as `NormalizedMonsterRecord.classification` and indexed by `MonsterCatalog` for backend filtering; they are not serialized in `MonsterDto`.

`MonsterContentDto` is sealed and includes plain text, named sections, lists/items, tables, quotes, insets, and embedded spellcasting. `TableCellDto` is sealed for text, rolls, and structured content. The typed `ContentValue` tree preserves additional source properties found inside rich content while avoiding raw Jackson values; `Section`, list/table/cell variants and spellcasting blocks keep those extensions alongside their familiar typed fields. This extension is needed for nested version/variant entries that contain spell lists and header/footer material.

`MonsterImportProvenance` and `NormalizedMonsterRecord` keep import lineage and search classification outside the public monster response. The provenance records source file and index, original identity, copy/version parent IDs, version/implementation indexes, applied template IDs, and abstract-version variables. Public IDs use a stable URL-safe encoding of the case-folded source/name pair. No source-to-edition registry has been reviewed, so edition IDs are currently empty; source availability flags remain distinct.

Public monster records use Jackson `NON_EMPTY` inclusion. Nulls, empty collections, and empty strings are omitted without suppressing meaningful `0` or `false` values. The mapper also returns `null` for optional grouping records when their source structure contains no meaningful fields. The required identity and statistics groups remain present. The catalog response retains environments, gear, attached items, treasure, familiar status, rules availability, and reprint information; fluff/token/media presentation metadata remains in resolved source content but is intentionally excluded from the public DTO.

`MonsterSummaryDto.from` projects catalog fields from a normalized detail DTO. It includes stable ID, name/source, known edition IDs, CR, size/type/alignment, representative AC/HP, and environment. This keeps the summary aligned with the detail model without adding REST endpoints.

## Mapping and coverage

The complete-catalog test dynamically discovers the `bestiary-*.json` source files, parses the original 4,560 monsters, runs copy/template resolution and version expansion, then maps all 4,851 resulting definitions. It checks unique IDs, deterministic results under reversed input order, unchanged resolved source content, retained generated ancestry, no source-resolution instructions in public records, and the expected Cat Skeleton, Skaab, Battle Familiar, Draconic Spirit, Zikran, nested defenses, yearly bucket, inline markup, and null/absent examples.

The mapper's explicit root allowlist is compared to the field inventory: 92 roots match, and all 535 inventoried paths have one of three explicit dispositions: public mapping, internal classification metadata, or excluded presentation-only metadata. The full corpus test provides the structural shape check: no source record raised a mapping exception. Root fields outside the allowlist fail with their JSON path. Rich-content extras are retained in the sealed `ContentValue` representation. `languages` and `vulnerable` distinguish explicit JSON null (`NullableList.explicitNull`) from absence (`null`) and present values internally, even when `NON_EMPTY` omits the wrapper's empty `values` list from API JSON.

## Intentional mapping choices and limitations

- AC, HP, speed, CR, passive perception, creature type, alignment, skill alternatives, and damage/condition defenses retain their observed polymorphic or conditional forms.
- CR strings preserve fractions. HP averages use `BigDecimal`. No proficiency bonus or edition is inferred.
- Spell slots retain literal level keys; daily/rest/recharge/legendary/charges/yearly and other observed usage keys remain distinct. `constant`, `will`, and `ritual` are retained separately. Spell text keeps inline references and is not resolved against the spell collection.
- Feature prose stays complete structured content; the mapper does not infer executable mechanics from prose.
- No encounter state is represented.
- The source `ContentValue` extension is a typed recursive value tree for additional properties within rich content, not a public arbitrary JSON node. If a future source revision adds a new semantic entry type, the extension preserves its properties while the canonical variant dispatch should be reviewed and extended.
- Some metadata reference forms are normalized to a common name/source or type/path record. Original displayed strings and nested content are preserved where relevant; edition interpretation and external media resolution are separate work.

## Validation results

The full Maven suite passes: **48 tests, 0 failures, 0 errors**. This includes the complete 4,851-record normalization check, all 4,560 source DTE imports, copy and version resolution, catalog initialization, public serialization tests, and the 92-root/535-path field inventory check. The catalog test confirms classification metadata is retained for all 4,851 records and remains available through its internal index.

## Next decisions

The DTO and mapper are ready for API-boundary review. Before presenting edition filters as authoritative, add a reviewed source-to-edition registry. Spell reference linking and any optional prose-to-mechanics extraction should be separate reviewed stages. No REST, frontend, persistence, or encounter-instance implementation is included.
