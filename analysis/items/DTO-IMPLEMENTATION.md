# Items DTO implementation

## Coverage and normalization

Both complete resources deserialize through strict Jackson readers that reject unknown fields. `ItemSourceDte` covers the source properties shared by the `item`, `itemGroup`, and `baseitem` collections. Auxiliary collection records have dedicated DTEs. Entry prose/markup is retained as typed `EntryDte` trees in separate internal import provenance, never copied to public DTOs. The generated normalized field inventory lists every root and nested JSON field, types, counts, and scalar examples.

| Source area | Typed DTE | Normalized definition | Public detail | Treatment |
|---|---|---|---|---|
| Identity, page, source | Yes | Yes | Yes | Source-aware ID; primary source/page retained |
| Type codes, alternate type, group, tags | Yes | Type codes, alternate type, groups and classifications | Yes | Extensible, source-qualified codes; names joined from authoritative `itemType` records where present |
| Rarity, value/value rarity, weight | Yes, including explicit null | Yes | Yes | Value is preserved in source integer units; explicit null does not inherit through a copy |
| Base-item reference | Yes | Yes | Yes | Exact source-qualified UID retained and validated against `baseitem` |
| Weapon damage, range, reload, ammo, property, mastery | Yes | Yes | Yes | Structured values and UIDs; no prose interpretation |
| Armor AC, strength, dexterity cap, stealth | Yes, including explicit null | Yes | Yes | Explicit false and null distinctions are preserved internally |
| Attunement and restrictions | Yes, bool/string plus typed tag fields | Required flag plus structured restrictions | Yes | Raw source prose is not exposed; structured restrictions remain source-faithful |
| Charges and recharge | Yes, number/string unions | Yes | Yes | Formula markup stays as source value text; no resource tracking |
| Attached spells | Yes, direct list or schedules and count maps | Yes | Yes | Spell UID identity retained; no Spell Catalog dependency |
| Ability grants, bonuses, protections, lights, speed changes | Yes | Yes | Yes | Explicit structured properties only |
| Capacity, pack contents, vehicle/equipment values | Yes, including string/object alternatives | Capacity and pack contents plus selected vehicle values | Yes | No carrying or inventory calculations |
| Secondary/additional/reprint references | Yes | Distinct source-reference lists and reprint relations | Yes | Every source identity validated |
| Description entries, additional entries | Yes | Internal import provenance, including resolved copy entries | No | No raw prose or 5eTools markup in responses |
| Source flags and presentation metadata | Yes | Import provenance only | No | Availability/presentation data is not public |
| Item groups and base-file reference collections | Dedicated DTEs | Internal auxiliary data | No separate endpoint | Retained but not mistaken for equipment definitions |

The path-level inventory is authoritative for every nested field. This matrix makes the disposition of all observed top-level fields explicit; fields not repeated in the normalized/public columns remain typed in the source DTE and original-record provenance.

| Source record | Fully typed root fields (excluding common identity/source/page) | Normalized definition / public DTO | Typed source/provenance only |
|---|---|---|---|
| `items.json:item` | All listed fields are typed, including copy, entry trees, list/object/scalar unions, source relations, and vehicle/stat fields | `type`, `typeAlt`, `rarity`, `valueRarity`, `weight`, `value`, `baseItem`, weapon and armor stats, `property`, `mastery`, `reqAttune*`, `charges`, `recharge*`, `attachedSpells`, `ability`, `containerCapacity`, `packContents`, `modifySpeed`, `light`, source/reprint fields, `group`, `miscTags`, damage/condition protection lists, `focus`, explicit item booleans, `scfType`, `age`, `tier`, `detail1/2`, spell scroll/critical values, vehicle/carrying/speed values, and explicit bonus fields | `weightNote`, `alias`, `items`, `lootTables`, `shippingCost`, `travelCost`, `edition`, `entries`, `additionalEntries`, source availability/fluff flags, and unprojected mechanical tag fields remain in source provenance; none are interpreted as mechanics |
| `items.json:itemGroup` | Common item fields plus `items`, `itemsHidden`, grouping selectors, and all item-like structured properties are typed using the shared `ItemSourceDte` | Group source content is retained as `ItemAuxiliaryData`; member strings are not promoted to item definitions | Group prose, selector/member strings, availability flags, and group-only metadata remain auxiliary/provenance data |
| `items-base.json:baseitem` | All listed fields are typed, including the source’s explicit weapon family booleans and per-record `edition` | Common weapon/armor/equipment fields, type/property/mastery, value/weight, source/reprint data, and structured pack contents normalize through the same definition mapper | Weapon family marker booleans (`arrow`, `axe`, `bolt`, `bulletFirearm`, `bulletSling`, `bow`, `cellEnergy`, `club`, `crossbow`, `dagger`, `hammer`, `lance`, `mace`, `needleBlowgun`, `net`, `polearm`, `rapier`, `spear`, `sword`), `alias`, `group`, `edition`, entries, and availability/fluff flags remain typed source provenance |
| `items-base.json:itemProperty` / `itemMastery` | Identity, abbreviation, template/entry, page, source, reprint, SRD flags | Retained in `ItemAuxiliaryData`; item property/mastery UIDs are projected as structured references on items | Full descriptions/templates and availability flags remain auxiliary source data |
| `items-base.json:itemType` / `itemTypeAdditionalEntries` | Identity, abbreviation, entries, page, source, `_copy`, and `appliesTo` | Resolved type code/name/source mapping is projected when an item’s code has a matching definition | Type descriptions, supplemental entries, and copy directives remain auxiliary source data |
| `items-base.json:itemEntry` | Identity and `entriesTemplate` | Retained in `ItemAuxiliaryData`; not an item definition | Templates remain unexpanded source data |

The source has three explicit-null cases among normalized scalars (`value` twice, `strength` once, and `dexterityMax` once); typed nullable wrappers distinguish a missing property from a present null so copy resolution does not replace explicit nulls with parent values. Numeric zero and meaningful booleans remain present in public JSON.

## Copies and auxiliary records

The catalog retains 2,512 `item` roots and 230 `baseitem` roots, for 2,742 public definitions. It resolves all 67 item copy roots and both auxiliary item-type copy records; copies do not create additional identities beyond their root records. The item copy resolver applies the observed `entries` array operations (`appendArr`, `insertArr`, `replaceArr`) and property operation (`appendIfNotExistsArr`) before normalization. Root explicit values override inherited values, except `_preserve` source metadata follows the source's preserve declaration. Unsupported operation names/targets/indexes fail startup. Each item definition keeps immutable `ItemImportProvenance` with the original typed source record, resolved copy result, and copy target; auxiliary item groups, property/type lookups, templates, and mastery data remain in separate internal `ItemAuxiliaryData`.

There are no `_versions` declarations. The 13 `itemEntry.entriesTemplate` records are templates and are not expanded into standalone item definitions. The 119 `itemGroup` records represent grouped selectors/sets and do not create additional catalog items.

## Public DTOs and REST

`ItemSummaryDto` exposes ID, name, primary source/page, definition kind, type codes, rarity, weight, and copper-unit value. `ItemDto` adds source type definitions, base-item UID, weapon/armor properties, attunement restrictions, resource charges, spell grant schedules, ability grants, explicit bonuses, resistances/immunities, light, speed modifications, capacity, pack contents, group/classification tags, secondary sources, and reprint links. Every public DTO uses `@JsonInclude(NON_EMPTY)`; raw prose, copy directives, availability flags, and import provenance are excluded.

- `GET /api/items?name=&source=&type=&rarity=` returns sorted summaries.
- `GET /api/items/{id}` returns one detail DTO.
- `GET /api/items/by-name/{name}?source=` returns all exact-name matches, optionally source filtered.

The catalog is immutable and startup loaded from both packaged classpath resources. The small static endpoint follows the existing catalog convention and is not paginated.

## Source validation and remaining limits

All 134 observed source identities are validated; three confirmed missing codes were added to the shared Source Registry without publication metadata. Base-item UIDs, item-property/mastery UIDs, spell UIDs, ammunition UIDs, secondary sources, and reprint targets are validated when source-qualified. Markup references in entries remain prose rather than generated links. There is no `magicvariants.json` ingestion. Item group membership is retained as auxiliary source data rather than expanded into independently addressable items. No character inventory, grants, progression, combat calculations, or generic effects engine is implemented.

The focused Items test class contains 15 tests covering all source collections, root counts, copy resolution and rejection, source-aware IDs, base relationships, equipment and magic-item mechanics, filtering, lookup, DTO projection, malformed input, packaged resources, and REST behavior. The full Maven suite passes: 140 tests, 0 failures, 0 errors, 0 skipped (baseline 125; 15 Items tests added). The registry test also reflects the three confirmed identity-only source additions.
