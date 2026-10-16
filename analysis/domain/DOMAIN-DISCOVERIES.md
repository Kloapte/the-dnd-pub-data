# Domain discoveries

## Items and Base Items

- The source supports one unified immutable Item Catalog: `items.json:item` contains 2,512 concrete item records and `items-base.json:baseitem` contains 230 separately usable equipment definitions. They have compatible `(name, source)` identities and no collisions with each other. Definitions retain an `ITEM` or `BASE_ITEM` kind.
- The 241 `baseItem` fields are exact `name|source` references to base-item identities; they are not merge instructions. The catalog validates those references without merging definitions.
- `itemGroup` (119 records) is a selector/group structure. Auxiliary `itemProperty`, `itemType`, `itemTypeAdditionalEntries`, `itemEntry`, and `itemMastery` collections supply source-authored classification, reference, and template data, not additional equipment records.
- Item type identifiers are open source codes, sometimes qualified by source/edition. `itemType.abbreviation` is an authoritative code-to-name mapping. Names and source codes are preserved without an enum.
- 67 item roots use `_copy`; 35 include `_mod`, with operations limited to entries append/insert/replace and property append-if-absent. Two auxiliary type records use `_copy`. `_versions` is absent. Copy-derived source identities remain independent catalog definitions.
- Explicit cross-entity mechanics include attached spell UIDs, base item UIDs, ammunition UIDs, weapon property/mastery UIDs, structured pack-content item UIDs, and type codes. These are exact source references; no fuzzy matching is used. Markup references in entries remain narrative source text and are not normalized into API relationships.
- Secondary and reprint source references retain `SourceReference` page locators or source-qualified UIDs and are Source Registry validated. Source definitions from PHB/XPHB and DMG/XDMG remain separate.
- Item data has overlapping needs with Feat and Optional Feature models for source references and ability-score labels, but their mechanical grants are not semantically shared. This pipeline uses separate item-specific DTOs.
- Future `magicvariants.json` integration can connect through explicit item/base-item references. It must not generate variants until that source's identity and override semantics are separately analyzed.

## Existing discoveries

Prior source and domain findings remain unchanged. New item relationships are implemented locally rather than introducing unrelated shared effect or equipment models.

## Magic Variants

- `magicvariants.json` contains 225 generic rules, five copy-derived named transformations, and 127 auxiliary loot-table links. It contains no concrete generated-item enumeration. The variant catalog is kept distinct from the 2,742-definition Item Catalog.
- Applicability is explicit in `requires`/`excludes`: required clauses preserve OR alternatives with AND fields, and list-valued name/property terms remain alternatives. Matching uses exact normalized item facts; it does not parse narrative entries or fuzzy-match names.
- All 56 structured source codes are covered by Source Registry. Secondary and reprint references are preserved. Explicit attached spell references are source UIDs; other spell mentions in entries are not normalized.
- The five AU transformations copy through generic XDMG rules but terminate at two item anchors absent from Item Catalog. They are exposed as transformations with unresolved name/source references, not complete item records.
- Against current Items, generic rules produce 12,663 candidate rule/item matches. There are 3,361 repeated candidate generated identities, 213,179 same-item rule-pair overlaps, zero distinct same-item/same-output conflicts, and zero candidate identity collisions with existing Items. The data does not specify rule stacking, so no output combinations are materialized.

## Backgrounds

- Backgrounds are source-specific definitions: 171 roots normalize to 171 catalog records; 26 roots inherit from other backgrounds and are independently addressable. PHB and XPHB Acolyte remain distinct. Ability distributions are weighted choice structures, and proficiency/feat packages preserve their source grouping and alternatives.
- Starting equipment explicitly references Items through source-qualified UIDs. Current resolution yields 718 matches and eight unresolved occurrences (three exact UIDs), with no ambiguous UIDs; 204 descriptors are special text or equipment type. No Item is synthesized. Package choices preserve `_`, A/B, a/b and other option keys.
- Feat grants use source-qualified feat UIDs and explicit selection labels where present; category choice remains a category choice. Additional spell schedules preserve source-authored spell strings without a Spell Catalog dependency. Skill/language grants are source names or choice lists without source identity, so no edition-specific catalog IDs are invented.
- Feature prose and roleplaying tables remain typed internal entries. Public output exposes feature names and table labels/counts only. Source `data.isFeature` entry metadata, named section IDs and numeric table cells are retained by the shared entry DTE.
- Campaign prerequisites, additional-source references, other-source references, reprints and copy lineage are separate concepts. Copy parents resolve by exact name/source; entry modifications are applied in source order. No character creation state or grant application is modeled.

## Races and Species

- `races.json` contains 160 race roots and 98 subrace records. Five PHB subrace records have no name and are auxiliary relationship data; 93 named subrace roots have explicit parent race/source. `lineage` is a source flag on 50 races, not a general selectable-kind indicator.
- The corpus includes PHB and XPHB identities, which must remain independent. Edition is explicitly present on only 18 race roots, so it cannot be safely inferred for all records from book publication dates.
- Race/subrace mechanics have source-level typed candidates for movement, size, creature type, abilities, proficiency grants, senses, protections, spells, and traits. Entry prose remains distinct from those structured fields. Parent references alone do not authorize copying all parent mechanics into a subrace.
- There are 16 race and one subrace `_copy` declarations. Version lists contain 41 race and 14 subrace declarations; four race and three subrace declarations use abstract templates with 25 and 30 implementation rows. The race-specific expander applies the observed `appendArr`, `replaceArr`, `removeArr`, `prependArr`, and `replaceTxt` operations and substitutes typed `_variables` without reusing unrelated entity expansion contracts.
- All 35 primary/secondary/additional/reprint source codes are present in the Source Registry. Reprint records use both UIDs and structured UID/tag objects. No source registry changes were needed.
- Stage A and B implementation: strict typed DTEs deserialize the complete corpus; the immutable normalizer resolves 17 copy declarations and expands 103 direct/template version outputs. The classpath-loaded Race Catalog contains 356 definitions (160 race roots, 93 named subrace roots, 103 expanded versions); five nameless auxiliary subrace records remain typed import records. Parent relationships and the two parent-distinguished name/source collisions are preserved. Public DTOs expose structured mechanics, grant choices, movement markers, spell schedules, source references, and safe trait metadata without prose or copy/version instructions. `GET /api/races`, `GET /api/races/{id}`, and `GET /api/races/by-name/{name}?source=` are implemented. Full Maven suite: 176 tests, zero failures/errors.
