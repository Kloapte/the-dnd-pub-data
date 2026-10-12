# Normalized Monster DTO design

## Scope and evidence

This is a design for the application model after the existing DTE import, copy/template resolution, and version expansion stages. It does not add DTO or mapper classes. A read-only traversal instantiated the existing Java pipeline against the complete source collection and inspected every `ResolvedMonster.content()` result.

| Measure | Observed |
| --- | ---: |
| Original source records | 4,560 |
| Expanded catalog records | 4,851 |
| Distinct resolved property paths | 535 |
| Distinct root properties | 92 |
| Resolved paths with no proposed destination | 0 |
| Root properties with explicit `null` values | `languages` (1), `vulnerable` (1) |

The reproducible, sorted observations are in [normalized-field-inventory.json](normalized-field-inventory.json). For each path, it records the number of distinct monsters containing the path, node/type/shape counts, up to three deterministic examples, proposed destination and Java type, semantic role, scanner classification when available, and normalization risk. Array members are represented with `[]`; spellcasting bucket/level keys remain literal path segments such as `daily.1e` and `spells.4`.

The scanner's gameplay labels are advisory. Eight resolved paths have no exact scanner classification (`action[].type`, `reaction[].type`, `spellcasting[].yearly`, its `1e` descendants, `trait[].page`, `trait[].source`, and a deeply nested variant-entry path); each is still mapped in the inventory. The inventory does not mistake an absent property for a present null property.

To regenerate from the project root in PowerShell, compile the project, build a temporary dependency classpath, run the analysis-only Java traversal, and annotate the result:

```powershell
.\mvnw.cmd -q -DskipTests compile dependency:build-classpath '-Dmdep.outputFile=target/monster-analysis-classpath.txt'
$monsterAnalysisCp = 'target/classes;' + (Get-Content -Raw target/monster-analysis-classpath.txt).Trim()
javac -proc:none -cp $monsterAnalysisCp -d target analysis/monsters/AnalyzeResolvedMonsterFields.java
java -cp "target;$monsterAnalysisCp" AnalyzeResolvedMonsterFields
node analysis/monsters/annotate_normalized_field_inventory.mjs
```

The source-file discovery, monster ordering, paths, shape keys, root mapping, and serialized property list are sorted for stable report diffs.

Selected presence counts from the complete resolved catalog: ability scores, AC, HP, and speed are present on 4,846 records each; CR on 4,605; action arrays on 4,811; traits on 3,989; spellcasting on 1,540. AC list items include 1,905 integers, 3,196 structured objects, and 112 special-only objects. CR is a string for 4,448 and an object for 157. HP is special-only for 150. Creature type is a string for 2,856 and structured for 1,995; the inner `type` is a choice object on nine, including generated Battle Familiar and Otherworldly Steed records. Passive Perception has two source-text values among 4,835 present records. Skills and saves occur on 3,404 and 2,202 records. Defense presence is 1,512 resist, 1,697 immune, 171 vulnerable, and 1,905 condition-immune; their arrays include both plain strings and conditional objects.

## Proposed public model

Use immutable Java records in a normalized domain/API package, separate from `dte`. Reuse the established recursive entry semantics, but expose domain-neutral `MonsterContentDto` variants rather than making API records depend on import package names. No record below has a generic `Object`, arbitrary map, or `JsonNode` property.

```java
public record MonsterDto(
    MonsterIdentityDto identity,
    MonsterStatisticsDto statistics,
    MonsterDefensesDto defenses,
    MonsterFeaturesDto features,
    MonsterCombatDto combat,
    MonsterSpellcastingDto spellcasting,
    MonsterClassificationDto classification,
    MonsterCatalogMetadataDto catalog
) {}

public record MonsterIdentityDto(
    MonsterId id, String name, List<String> aliases, SourceIdentityDto source,
    List<String> groups, List<String> sizes, CreatureTypeDto creatureType,
    List<AlignmentDto> alignments, String alignmentPrefix, ShortNameDto shortName,
    Boolean namedCreature, Boolean npc, String dragonAge, String dragonCastingColor,
    SummoningDto summoning, LegendaryGroupReferenceDto legendaryGroup
) {}

public record SourceIdentityDto(
    String source, Integer page, List<SourceReferenceDto> otherSources,
    List<SourceReferenceDto> referenceSources, List<String> reprintedAs,
    RulesAvailabilityDto availability
) {}

public record MonsterStatisticsDto(
    AbilityScoresDto abilities, InitiativeDto initiative, List<ArmorClassDto> armorClasses,
    HitPointsDto hitPoints, MovementDto movement, ChallengeRatingDto challengeRating,
    ProficiencyBonusDto proficiencyBonus, Map<Ability, String> savingThrows,
    Map<String, SkillValueDto> skills, PassivePerceptionDto passivePerception,
    List<String> senses, NullableListDto<String> languages, Integer level,
    List<String> forcedSaves, List<String> legendaryForcedSaves,
    List<String> spellForcedSaves, String proficiencyBonusNote
) {}

```

### Supporting records and enums

**Identity and source**

- `record MonsterId(String value)` is the canonical stable key. `enum Ability { STR, DEX, CON, INT, WIS, CHA }` is used only where the six ability codes are a closed set. `enum FeatureKind { TRAIT, ACTION, BONUS_ACTION, REACTION, LEGENDARY_ACTION, MYTHIC_ACTION, VARIANT }` comes from the containing source array. `enum SpellUsageKind` is listed below. Do not add closed enums for source vocabularies that can grow.
- `SourceReferenceDto(String source, Integer page)`; normalize an observed bare source string to `source` with a null page. Use separate source-reference variants if later evidence finds fields with different semantics.
- `LegendaryGroupReferenceDto(String name, String source)` remains a link to the separately imported legendary-group collection.
- `RulesAvailabilityDto(Boolean basicRules, Boolean basicRules2024, Boolean srd, Boolean srd52, Set<String> editionIds)` keeps the exact source flags separate from the source book's actual edition. `editionIds` is populated only through a reviewed source-to-edition registry.
- `CreatureTypeDto(String type, CreatureTypeChoiceDto typeChoice, List<CreatureTypeTagDto> tags, String swarmSize, Boolean sidekickHidden, List<String> sidekickTags, String sidekickType)` normalizes simple string source values to `type`; `type` itself can be the observed `{"choose":[...]}` object for nine monsters, including Battle Familiar and Otherworldly Steed. `CreatureTypeChoiceDto(List<String> choose)` and `CreatureTypeTagDto(String tag, String prefix, Boolean prefixHidden)` preserve choices and display qualifiers.
- `AlignmentDto(List<String> codes, Integer chance, String special, String note)` handles ordinary codes, grouped alignments, percentages, and textual special cases without forcing an incomplete alignment enum.
- `ShortNameDto(Boolean enabled, String value)` preserves both observed `shortName` booleans and string values.
- `SummoningDto(String summonedByClass, String summonedBySpell, Integer spellLevel)` holds the observed summon identity and scaling reference. The separate `level` value maps to statistics; do not infer it from a spell.
- `MonsterId` is a small value type around a stable key. Build it from the canonical, case-folded `(source, name)` pair, with unambiguous escaping or a versioned digest; retain original spelling in `identity.name/source`. The prior graph found no duplicate source identity among 4,560 originals, and the version stage enforced uniqueness across all 4,851 results.

**Statistics**

- `Ability` enum: `STR`, `DEX`, `CON`, `INT`, `WIS`, `CHA`. `AbilityScoresDto(Integer strength, Integer dexterity, Integer constitution, Integer intelligence, Integer wisdom, Integer charisma)` uses boxed fields because five expanded records have no `str`/`dex`/etc. properties.
- `InitiativeDto(Integer value, Integer proficiency, String advantageMode)` preserves the integer and object forms.
- `ArmorClassDto(Integer value, List<String> sources, String condition, Boolean braces, String special)` is a list member: the full dataset has both integer and object AC items and multiple AC entries per monster.
- `HitPointsDto(BigDecimal average, String formula, String special)` keeps the 26 observed non-integer JSON number averages, formulas, and special rules without rounding or recalculation.
- `MovementDto(MovementValueDto walk, MovementValueDto burrow, MovementValueDto climb, MovementValueDto fly, MovementValueDto swim, Boolean canHover, List<AlternateMovementDto> alternate, MovementChoiceDto choose, String note)`; `MovementValueDto(Integer number, String condition)`, `AlternateMovementDto(Integer number, String condition, String mode)`, and `MovementChoiceDto(Integer amount, List<String> from, String note)` preserve all observed conditions, choices, and alternate modes.
- `ChallengeRatingDto(String value, String covenValue, String lairValue, Integer experience, Integer lairExperience)` retains fractional CR strings (for example `1/8`) exactly and keeps the object-only coven/lair/XP information.
- `ProficiencyBonusDto(Integer value, String note, String derivationRuleset)` is derived only when the mapper has a confirmed ruleset and a reviewed CR-to-proficiency table. Do not infer the edition from a source abbreviation. Keep `pbNote`; no explicit root `pb` property was observed.
- `SkillValueDto` is sealed: `SkillBonusDto(String sourceBonus)` and `SkillAlternativesDto(List<SkillChoiceDto> choices)`. `SkillChoiceDto(Map<String,String> oneOf)` preserves source-defined sets of possible skill bonuses. Use a string-keyed map so unknown/future skill labels do not break import.
- `PassivePerceptionDto(Integer value, String sourceText)` preserves the 4,833 numeric values and two observed text values. Do not overwrite it with `10 + Wisdom modifier`.
- `NullableListDto<T>(boolean explicitNull, List<T> values)` distinguishes an absent field (DTO property absent/null by policy), explicit JSON null (`explicitNull=true`), and a present list (including empty). Use it for `languages` and `vulnerable`, the only root fields with explicit nulls in the resolved catalog.

**Defenses**

- `MonsterDefensesDto(NullableListDto<DefenseDto> resistances, NullableListDto<DefenseDto> immunities, NullableListDto<DefenseDto> vulnerabilities, NullableListDto<DefenseDto> conditionImmunities)` keeps the four categories separate.
- `DefenseDto(List<String> terms, Boolean conditional, String note, String preNote, String postNote, String special, List<DefenseDto> nested)` represents plain terms and recursive conditional objects. Retain source phrases such as “from nonmagical attacks” as text; do not flatten, interpret into a universal damage enum, or implement reduction rules.

**Features and combat**

- `MonsterFeaturesDto(List<MonsterFeatureDto> traits, List<MonsterFeatureDto> actions, List<MonsterFeatureDto> bonusActions, List<MonsterFeatureDto> reactions, List<MonsterFeatureDto> legendaryActions, List<MonsterFeatureDto> mythicActions, List<MonsterFeatureDto> variants, String actionNote, String reactionNote, List<String> legendaryHeader, List<String> mythicHeader, List<String> reactionHeader)` preserves each source container and its section text.
- `MonsterFeatureDto(String name, List<MonsterContentDto> content, FeatureKind kind, String sourceType, String source, Integer page, TokenReferenceDto token, VariantReferenceDto version, FeatureUsageDto usage, List<FeatureMechanicsDto> mechanics)` retains the feature's rare own `type` property and rich text. `kind` is `TRAIT`, `ACTION`, `BONUS_ACTION`, `REACTION`, `LEGENDARY_ACTION`, `MYTHIC_ACTION`, or `VARIANT` based on its parent collection.
- `FeatureUsageDto(Integer uses, String recharge, String rechargeText, Integer actionCost, String usageText)` and `FeatureMechanicsDto(List<AttackDto> attacks, SavingThrowRuleDto savingThrow, List<DamagePartDto> damage, List<String> conditions)` are optional structured annotations. `AttackDto(String attackType, Integer bonus, String target)`; `SavingThrowRuleDto(Ability ability, Integer dc, String successText, String failureText, String sourceText)`; `DamagePartDto(String diceOrAmount, String damageType, String sourceText)`. Populate only when a future extractor can identify the rule unambiguously; the complete content remains authoritative. Do not pretend every action has these fields.
- `MonsterCombatDto(Integer legendaryActionLimit, Integer lairLegendaryActionLimit)` represents definition-level caps, not remaining encounter actions.
- `VariantReferenceDto(String name, String addAs, String addHeadersAs)` preserves nested `variant[]._version` references. Those references are display/source data and are distinct from top-level `_versions` instructions.
- `TokenReferenceDto(String name, String source, Integer page)` retains the rare feature token link.

**Spellcasting**

- `MonsterSpellcastingDto(List<SpellcastingBlockDto> blocks)` contains a `SpellcastingBlockDto` for each source block: `String name`, `String type`, `Ability ability`, `String displayAs`, `List<MonsterContentDto> header`, `List<MonsterContentDto> footer`, `List<SpellReferenceDto> atWill`, `Map<String,SpellLevelDto> spellLevels`, `Map<SpellUsageKind,Map<String,List<SpellReferenceDto>>> limitedUses`, `List<SpellReferenceDto> rituals`, `Set<String> hidden`, and `String chargesItem`.
- `SpellLevelDto(Integer slots, Integer lower, List<SpellReferenceDto> spells)` uses string map keys for spell levels including `0` and `1` through `9`. `SpellReferenceDto(String sourceText, Boolean hidden, String resolvedSpellId)` preserves inline references and display text; `resolvedSpellId` is optional and requires a separate spell catalog link step.
- `SpellUsageKind` covers observed buckets: `DAILY`, `REST`, `LONG_REST`, `RECHARGE`, `LEGENDARY`, `CHARGES`, `YEARLY`, and `RITUAL`; retain original bucket strings if new kinds appear. The source model also has places for `WEEKLY`/`MONTHLY`; include them if present in a later dataset revision. The inner string keys are not interchangeable: `1`, `1e`, `2`, `2e`, etc. remain exact keys.
- Monster spell lists are not `SpellDte` lists. `SpellDte` models spell definitions; monster records model spell availability, levels, slots, uses, and inline text. Share only the rich-content DTOs and spell identity-link type where their meanings match.

**Rich description content**

Promote the shared recursive entry semantics into a domain-neutral DTO hierarchy, preserving the exact inline markup in text values:

- sealed `MonsterContentDto` with `PlainTextDto(String text)`, `SectionDto(String sourceType, String name, String entry, List<MonsterContentDto> entries, String source, Integer page)`, `ListContentDto(String style, List<MonsterContentDto> items)`, `ListItemDto(String name, String entry, List<MonsterContentDto> entries)`, `TableContentDto(String caption, List<String> columnLabels, List<String> columnStyles, List<List<TableCellDto>> rows, String source, Integer page)`, `QuoteContentDto(String attribution, List<MonsterContentDto> entries)`, `InsetContentDto(String name, List<MonsterContentDto> entries, String source, Integer page)`, and `SpellcastingContentDto(SpellcastingBlockDto spellcasting)`.
- sealed `TableCellDto` with `TextCellDto(String text)`, `RollCellDto(Integer exact, Integer minimum, Integer maximum, Boolean pad)`, and `StructuredCellDto(String sourceType, String name, List<MonsterContentDto> entries)`.
- Keep source `type` strings on content variants where 5eTools distinguishes structural entry forms. Do not flatten tables/lists to strings. Do not use `EntryDte` directly in public API records; map its complete semantics into these API-neutral DTOs.

**Classification and catalog metadata**

- `MonsterClassificationDto` contains typed sets for source-provided action, damage, legendary/spell damage, condition-infliction, language, miscellaneous, sense, spellcasting, and trait tags. Treat these as useful search facets; retain them rather than regenerating them from prose.
- `MonsterCatalogMetadataDto(Set<String> environments, List<GearReferenceDto> gear, List<String> attachedItems, Set<String> treasure, Boolean hasFluff, Boolean hasFluffImages, Boolean hasToken, Boolean customToken, String tokenCredit, Double foundryTokenScale, List<AlternateArtDto> alternateArt, SoundReferenceDto sound, RulesAvailabilityDto rulesAvailability, Boolean familiar, Boolean reprinted)` retains searchable and display metadata. `GearReferenceDto(String item, String displayName, Integer quantity)` normalizes string gear to `item`; `AlternateArtDto(String name, String source, Integer page)`; `SoundReferenceDto(String type, String path)` or a more specific observed record after inspecting the sound payload. Do not embed audio/image bytes.
- `RulesAvailabilityDto` keeps the exact booleans `basicRules`, `basicRules2024`, `srd`, and `srd52`; they describe eligibility/availability, not necessarily the source book's edition.

## Mapping and null rules

The machine report maps all 92 observed root properties and 535 nested paths. Its `normalizedDestination` gives a destination for each path; `rootPropertyJavaType` describes the root value's proposed type, while the nested record list above supplies the child types. The 92 root properties group as follows:

| DTO group | Root properties |
| --- | --- |
| Identity | `name`, `source`, `page`, `alias`, `otherSources`, `referenceSources`, `reprintedAs`, `group`, `legendaryGroup`, `size`, `type`, `alignment`, `alignmentPrefix`, `dragonAge`, `dragonCastingColor`, `shortName`, `isNamedCreature`, `isNpc`, `summonedByClass`, `summonedBySpell`, `summonedBySpellLevel` |
| Statistics | six abilities, `initiative`, `hp`, `cr`, `speed`, `ac`, `save`, `skill`, `passive`, `senses`, `languages`, `savingThrowForced`, `savingThrowForcedLegendary`, `savingThrowForcedSpell`, `level`, `pbNote` |
| Defenses | `resist`, `immune`, `vulnerable`, `conditionImmune` |
| Features | `trait`, `action`, `bonus`, `reaction`, `legendary`, `mythic`, `variant`, `actionNote`, `reactionNote`, `legendaryHeader`, `mythicHeader`, `reactionHeader` |
| Combat | `legendaryActions`, `legendaryActionsLair` |
| Spellcasting | `spellcasting` |
| Classification | `actionTags`, `damageTags`, `damageTagsLegendary`, `damageTagsSpell`, `conditionInflict`, `conditionInflictLegendary`, `conditionInflictSpell`, `languageTags`, `miscTags`, `senseTags`, `spellcastingTags`, `traitTags` |
| Catalog metadata | `environment`, `gear`, `attachedItems`, `treasure`, `familiar`, `basicRules`, `basicRules2024`, `srd`, `srd52`, `isReprinted`, `hasFluff`, `hasFluffImages`, `hasToken`, `tokenCredit`, `tokenCustom`, `foundryTokenScale`, `altArt`, `soundClip` |

The remaining identity roots `isNamedCreature`/`isNpc`/`dragonAge`/`dragonCastingColor` and `level`/summoning roots are included in the corresponding group lists above. Every source path is assigned exactly once in the JSON report; tags and metadata are preserved even when they are not direct mechanics.

Missing properties map to absent optional values. For the two explicit-null root cases, use `NullableListDto` so absent, null, and empty cannot collapse together. Preserve text and source flags byte-for-byte where a transformation is not necessary. Do not normalize inline `{@...}` markup inside `MonsterContentDto` strings.

### Required and optional fields

`MonsterDto`'s identity, statistics, defenses, features, combat, spellcasting, classification, and catalog group records should always be present, with empty collections where that gives a consistent API shape. `identity.id`, `identity.name`, and `identity.source.source` are required for every expanded record. All other source-derived fields are optional: the catalog has monsters without several ability scores, AC/HP/CR/speed fields, source pages, spellcasting, or particular feature sections. Within groups, scalar null means absent unless a dedicated nullable wrapper is specified; ordinary list fields use empty lists for absent and present-empty values where their distinction is not meaningful. Never turn a present source `null` for `languages` or `vulnerable` into an empty list.

No resolved content property currently lacks a safe typed destination, provided recursive descriptions, defense clauses, spell-use keys, special text, and external references remain intact. The inventory's `observedShapes` and samples call out variant detail at each path; no structural branch may be discarded just because a summary card does not display it.

The mapper's authoritative input should be `ResolvedMonster.content()`, not a reconstruction from the grouped `MonsterDte`. The resolved tree retains all source fields and the version/copy transformation output. The current `MonsterDteDeserializer` deliberately does not project `hasFluff`, `hasFluffImages`, or `soundClip`; those values remain present in the resolved content and are mapped to catalog metadata here. The inventory also observes four resolved `spellcasting[].yearly` structures, so the normalized mapper must cover that usage bucket even if a current DTE projection does not expose it as a record component.

## Edition, provenance, and public API boundary

Use a reviewed `SourceEditionRegistry` to associate source books with `DND_2014`, `DND_2024`, playtest, or other edition identifiers. Keep that inferred association separate from `basicRules`/`srd52` booleans. Until the registry is reviewed, edition is optional/unknown; do not derive it from names such as `XPHB` alone.

Keep full import lineage out of the public `MonsterDto`. Store it in a separate internal `MonsterImportProvenance` alongside the DTO:

```java
record MonsterImportProvenance(
    String sourceFile, int sourceArrayIndex, String canonicalName, String canonicalSource,
    MonsterId copyParentId, MonsterId versionParentId,
    Integer versionDefinitionIndex, Integer implementationIndex,
    List<MonsterId> appliedTemplateIds
) {}
```

The `variables` and raw source instruction trees remain in `ResolvedMonster` provenance for audit/debug use; do not copy those potentially large payloads into public responses. The generated monster's own ID comes from its generated name/source. Its version parent points to the containing resolved monster; the containing monster's copy parent remains a separate lineage edge. Keep import file/index out of normal frontend payloads unless an admin/debug endpoint needs them.

For REST, use separate `MonsterSummaryDto` and `MonsterDto` detail responses. The summary contains ID, name, source/edition, challenge rating, size/type, alignment, representative AC/HP, and perhaps environment. The detail response contains the complete groups and recursively expandable features/spellcasting. This limits list payload size without weakening the canonical model; the summary should be projected from `MonsterDto` so fields do not drift.

`MonsterDto` is a reusable definition only. Current HP, temporary HP, initiative result, active conditions, remaining slots, consumed uses, and remaining legendary actions belong to a separate future `MonsterInstanceDto` keyed to an encounter. Static max HP, spell slots, recharge definitions, and action limits remain in the monster definition.

## Resolved examples

The following outcomes are backed by the full-corpus resolver tests and demonstrate why the model keeps the rich fields:

- **Knight of the Order (CoS)** has a resolved AC of 13 and a source-modified Longsword action. Identity/source, one of potentially several AC values, and the full action text all remain available.
- **Cat Skeleton (CoS)** has explicit null `vulnerable` and `languages` after the template/copy pipeline. A normal absent optional list or empty list is not equivalent; preserve the null markers.
- **Skaab (PSI)** resolves its copy ancestry before emitting five definitions, including *Skaab: Armor Plating*. The generated definition retains its own identity while its stat block is independently usable.
- **Battle Familiar (AU)** generates Brute, Flyer, and Stalker definitions with different action/trait arrays and AC/HP formulas. Keep each version's entire description and formula text; don't merge into the base or assume its mechanics are all numeric.
- **Draconic Spirit (XPHB)** produces five named variants by substituting `acid`, `cold`, `fire`, `lightning`, and `poison` into nested feature content. Preserve both the structured feature tree and inline tags such as `{@damage 2d6}`.
- Spellcasting examples such as **Zikran (CM)** retain spell levels and inline spell references. Bucket keys like `1` and `1e` are separate use semantics and stay separate in the normalized model.

## Loss risks and decisions

1. **Text-to-mechanics parsing:** most action mechanics live in prose and inline tags. Keep complete rich content authoritative; any attack, save, damage, recharge, or cost fields are optional extracted indexes with their source text attached. Do not claim an action is fully executable from the index.
2. **Edition inference:** source records contain source codes and availability flags, but do not provide one dependable rules-edition field. A maintained source registry is needed before CR/proficiency interpretation or UI edition filters can be considered authoritative.
3. **Unknown/future vocabulary:** do not use closed enums for alignment strings, damage terms, conditions, skill labels, spell-use buckets, environment, or creature subtype. Use enums only for the six ability keys and the internal feature category; retain raw source values for extensible vocabularies.
4. **Null semantics:** the two explicit-null properties must stay distinguishable from absence and empty values. This behavior should be a mapper test invariant.
5. **Large description trees:** feature and variant content can include tables, nested lists, roll cells, embedded spellcasting, and source labels. DTO conversion must be recursive and tested for every shared entry/cell subtype.
6. **Proficiency bonus:** not a direct resolved property. Compute only under an explicit edition/ruleset policy and keep a note/provenance of derivation; never replace source notes.
7. **Media and cross-collection links:** tokens, art, sound, and legendary groups are references. Keep identifiers and attribution, resolve bytes or records in separate services.
8. **Eight scanner-unclassified nested paths:** the field inventory marks exact scanner classifications as absent for eight paths; their typed destinations have been assigned manually based on observed full-catalog structures. Review them again when datasets change.

`_copy`, `_versions`, `_mod`, `_templates`, and resolver working trees are not fields of `MonsterDto`. Version source instructions and copy/template details remain only in internal provenance. Nested `variant[]._version` references are ordinary variant data and map to `VariantReferenceDto`.

## Phased implementation plan

1. Create normalized identity/source, `MonsterId`, `MonsterImportProvenance`, and source edition registry types. Validate 4,851 unique IDs and lineage links.
2. Implement scalar and polymorphic statistics (size, type, alignment, AC, HP, movement, initiative, CR, saves, skills, passive) with explicit-null tests.
3. Implement recursive defense DTO conversion, including all nested clauses and conditional metadata.
4. Promote shared recursive entries/cells into API-neutral content DTOs and map all feature/variant containers. Compare every output subtree and inline string to resolved input.
5. Implement spellcasting blocks, slot levels, spell references, and each usage bucket while retaining literal map keys.
6. Add classification/catalog/source reference groups, edition availability, media and legendary-group links.
7. Add a deterministic full-catalog mapping test: 4,851 successes, zero unresolved paths, all input root properties accounted for, source text and null semantics preserved, provenance retained, and no source instructions exposed.
8. Project `MonsterSummaryDto` from `MonsterDto`; keep encounter runtime state in a future `MonsterInstanceDto`.

## Readiness

The field inventory covers all 4,851 resolved monsters and maps every observed path. The structural model is ready for DTO implementation, subject to two explicit decisions before the mapper treats derived values as authoritative: maintain a reviewed source-to-edition registry, and define whether/which prose mechanics extractors populate optional structured action summaries. Neither decision blocks implementing a loss-preserving typed DTO with those fields optional.
