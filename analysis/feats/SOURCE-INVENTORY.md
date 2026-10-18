# Feats source inventory

Analyzed all of `src/main/resources/data/feats.json`. The root contains one collection, `feat`, with **305 records and 305 unique `(name, source)` identities**. The [normalized field inventory](normalized-field-inventory.json) records every observed property path and JSON value type, including nested entry, prerequisite, spell-grant, proficiency, and version structures.

## Sources and identity

The 20 primary source codes are RHW, EFA, ERLW, XPHB, AU, PHB, DSotDQ, SatO, TCE, ABH, FRHoF, XGE, BMT, LFL, BGG, FTD, PSK, SCC, MTF, and PSX. Primary counts are in the inventory. EEPC and SCAG appear only in reprint references. MTF also appears in `additionalSources`; across all primary, secondary, and reprint references there are **22 distinct codes**, all covered by Source Registry identities or aliases. No registry additions are needed. One feat (`Svirfneblin Magic`, MTF) has two `additionalSources` references. There are 50 explicit reprint targets across 50 records, primarily PHB/TCE/etc. definitions pointing to XPHB, and one Aberrant Dragonmark target in EFA. Reprint targets remain source-qualified and are not merged. Two base definitions contain `_versions`: Magic Initiate (three variants) and Strike of the Giants (six variants). No `_copy` property occurs.

## Classifications and availability

`category` is a source string on 197 records. Observed codes and counts: G (95), O (35), EB (32), D (13), FS (11), DG (9), FS:P (1), and FS:R (1). These codes are preserved as source classifications; the importer does not map them to an assumed universal taxonomy. Some sources (notably XPHB) provide categories while many older or other-source records do not. Availability metadata includes `srd` (1), `srd52` (17), and `basicRules2024` (17); `hasFluffImages` occurs on 44 records. These are excluded from public DTOs.

## Prerequisites, ability increases, and choices

There are 224 feats with `prerequisite`, represented as arrays of requirement objects: 208 records have one object, 13 have two, and 3 have three. Objects combine applicable conditions; the array is retained intact so alternatives are not flattened. Observed keys include level (number or a structured level/class object), ability thresholds, feat and feat-category references, class feature, race/species, background, proficiency, spellcasting flags, campaign, exclusive category, and `other`/`otherSummary` requirements. These remain declarative; no prerequisite evaluator is introduced.

Structured `ability` increases occur on 178 records. They include fixed ability increments, `choose.from` with `amount`/`count`/`entry`, optional maximum, and a hidden flag. Five feats have `repeatable: true`; `repeatableHidden` is source presentation metadata. Structured selection mechanisms also appear in proficiency and spell grants. They describe available choices, not a character's selected values.

## Other structured properties

The source explicitly structures skill proficiencies (12 records), expertise (7), language proficiencies (7), tool proficiencies (10), weapon proficiencies (4), armor proficiencies (6), saving-throw proficiencies (2), combined skill/tool/language choices (2), senses (4), bonus senses (1), condition immunities (2), damage immunity (2), damage resistance/choice (11), optional-feature progression (4), and additional spell grants (86). These are source grants/configuration, not resolved character state. Choice objects and dynamic named proficiency keys are retained using typed values.

Spell grant structures distinguish known, prepared, innate, and expanded spells and can contain filter-based spell choices, class/level query strings, ability choices, and dynamic schedule/level keys. The Spell catalog is not required and no spell names are fuzzy-resolved. `_versions` include typed source modifications (`replaceArr` and `removeArr`) with variant names/sources and version-specific spell grants; the importer preserves them and does not apply those edits to the parent definition.

## Entries and cross-entity markup

All records have typed `entries`; supported entry forms are inherited from the shared entry DTEs, and the complete inventory enumerates the observed nested shapes. Descriptive entries contain inline tags including `skill`, `condition`, `sense`, `spell`, `feat`, `action`, `language`, `background`, `class`, `item`, `itemProperty`, `variantrule`, `status`, `damage`, `filter`, and others. These tags are not equivalent to typed source-field references. Ordinary mentions also occur. Public APIs omit entries and raw markup; typed entries stay in internal import provenance due publication restrictions.


## Validation

The full corpus deserializes strictly and all 305 definitions normalize. `normalized-field-inventory.json` inventories all observed JSON paths and value types. The complete Maven test suite passes with 107 tests (baseline 99 plus 8 Feat tests); the Feat suite includes packaged-JAR resource loading.


