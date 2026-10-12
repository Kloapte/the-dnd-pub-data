# Monster DTO implementation

## Scope and pipeline

The normalized public definition is implemented in `com.thedndpub.data.dto.monster`. `MonsterMapper` consumes `ResolvedMonster.content()` directly; it never reconstructs the post-resolution object from `MonsterDte`. The mapping chain is:

`MonsterDte → MonsterCopyResolver → MonsterVersionExpander → ResolvedMonster → MonsterMapper → MonsterDto`

The mapper reads a defensive copy of the resolved tree and does not mutate it. It rejects unexpected root properties and any active `_copy`, `_versions`, `_mod`, or `_templates` instruction. Jackson trees are used only inside mapping code. The public DTO API is composed of records, typed maps, and sealed recursive content/cell/value interfaces; it has no `Object`, `Map<String,Object>`, or `JsonNode` fields.

## Record groups

`MonsterDto` groups its normalized fields into `Identity`, `Statistics`, `Defenses`, `Features`, `Combat`, spellcasting blocks, `Classification`, and `Catalog`. The nested records model identity/source links, polymorphic creature type and alignment, AC/HP/movement/CR, skill alternatives, recursive defense clauses, feature groups, spell levels and use buckets, tags, equipment/media references, and rules availability. Ability and feature categories use closed enums only for stable vocabularies; open source vocabularies remain strings.

`MonsterContentDto` is sealed and includes plain text, named sections, lists/items, tables, quotes, insets, and embedded spellcasting. `TableCellDto` is sealed for text, rolls, and structured content. The typed `ContentValue` tree preserves additional source properties found inside rich content while avoiding raw Jackson values; `Section`, list/table/cell variants and spellcasting blocks keep those extensions alongside their familiar typed fields. This extension is needed for nested version/variant entries that contain spell lists and header/footer material.

`MonsterImportProvenance` and `NormalizedMonsterRecord` keep import lineage outside the public monster response. The provenance records source file and index, original identity, copy/version parent IDs, version/implementation indexes, applied template IDs, and abstract-version variables. Public IDs use a stable URL-safe encoding of the case-folded source/name pair. No source-to-edition registry has been reviewed, so edition IDs are currently empty; source availability flags remain distinct.

`MonsterSummaryDto.from` projects catalog fields from a normalized detail DTO. It includes stable ID, name/source, known edition IDs, CR, size/type/alignment, representative AC/HP, and environment. This keeps the summary aligned with the detail model without adding REST endpoints.

## Mapping and coverage

The complete-catalog test dynamically discovers the `bestiary-*.json` source files, parses the original 4,560 monsters, runs copy/template resolution and version expansion, then maps all 4,851 resulting definitions. It checks unique IDs, deterministic results under reversed input order, unchanged resolved source content, retained generated ancestry, no source-resolution instructions in public records, and the expected Cat Skeleton, Skaab, Battle Familiar, Draconic Spirit, Zikran, nested defenses, yearly bucket, inline markup, and null/absent examples.

The mapper's explicit root allowlist is compared to the approved field inventory: 92 roots match, and all 535 inventoried paths are marked with a normalized destination. The full corpus test provides the structural shape check: no source record raised a mapping exception. Root fields outside the allowlist fail with their JSON path. Rich-content extras are retained in the sealed `ContentValue` representation. `languages` and `vulnerable` distinguish explicit JSON null (`NullableList.explicitNull`) from absence (`null`) and present values.

## Intentional mapping choices and limitations

- AC, HP, speed, CR, passive perception, creature type, alignment, skill alternatives, and damage/condition defenses retain their observed polymorphic or conditional forms.
- CR strings preserve fractions. HP averages use `BigDecimal`. No proficiency bonus or edition is inferred.
- Spell slots retain literal level keys; daily/rest/recharge/legendary/charges/yearly and other observed usage keys remain distinct. `constant`, `will`, and `ritual` are retained separately. Spell text keeps inline references and is not resolved against the spell collection.
- Feature prose stays complete structured content; the mapper does not infer executable mechanics from prose.
- No encounter state is represented.
- The source `ContentValue` extension is a typed recursive value tree for additional properties within rich content, not a public arbitrary JSON node. If a future source revision adds a new semantic entry type, the extension preserves its properties while the canonical variant dispatch should be reviewed and extended.
- Some metadata reference forms are normalized to a common name/source or type/path record. Original displayed strings and nested content are preserved where relevant; edition interpretation and external media resolution are separate work.

## Validation results

Focused command:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.12.1'
.\mvnw.cmd -s target/maven-settings.xml -q -Dtest=MonsterMapperTests test
```

Result: **5 focused tests passed**, including the complete 4,851-record integration mapping, polymorphic-statistics and recursive-content fixture, and the 92-root/535-path inventory check.

Full Maven suite: **35 tests run, 34 passed, 1 errored**. The sole error is the existing unrelated Spring application-context failure: `MonsterController` references the absent `com.thedndpub.data.services.MonstersService`. The DTO and mapper tests, plus existing copy resolver, version expander, DTE, and entry validation tests, passed. This unrelated application-context issue is not addressed by this DTO mapping work.

## Next decisions

The DTO and mapper are ready for API-boundary review. Before presenting edition filters as authoritative, add a reviewed source-to-edition registry. Spell reference linking and any optional prose-to-mechanics extraction should be separate reviewed stages. No REST, frontend, persistence, or encounter-instance implementation is included.
