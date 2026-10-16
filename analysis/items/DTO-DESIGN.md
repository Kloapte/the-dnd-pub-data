# Item DTO design

## Definition model

`ItemDefinition` is the immutable catalog representation for both source item kinds. It carries a source-aware `item:<source-slug>:<name-slug>` ID, `SourceReference`, `ItemDefinitionKind`, original type/category codes, explicit base-item reference, rarity, attunement representation, weight/value, structured weapon/armor fields, source references, and selected explicit structured item mechanics. Copy resolution is completed before normalization. Internal `ItemImportProvenance` keeps the original typed source DTE, resolved copy result, and copy lineage separately from public fields.

Types are strings rather than enums because the source category/type namespace is open and edition-qualified. `ItemTypeDto` provides the source-authored name and source code where an authoritative `itemType` record exists; otherwise the original code remains available without an invented label. Descriptive entry text is not exposed.

## Structured properties

Dedicated records preserve damage dice/type, range/reload/ammunition, weapon property and mastery UIDs, armor AC/strength/stealth fields, charge/recharge values, attached spell schedules, pack quantities, container capacities, ability modifiers, and vehicle statistics. Union fields preserve both alternatives: textual/boolean attunement, numeric/text charge fields, structured/string/number values where seen, and list/object spell schedules. Dynamic schedule keys are string keys with typed list/value payloads. Source-qualified type codes and alternate type codes are retained; `ItemTypeDto` adds an authoritative name/source mapping only when the auxiliary source data provides one.

`ItemDto` is the detail representation and includes the useful structured properties. `ItemSummaryDto` contains ID, name, source/page, kind, type codes, rarity, weight, and value for selection/listing use. No raw entry text, markup, copy instructions, import metadata, or availability flags are projected.

## Relationships

`baseItem` remains a source UID reference to a distinct BASE_ITEM definition. `property`, `mastery`, `attachedSpells`, `reprintedAs`, and secondary source fields retain explicit source identities. Text mentions in entry markup remain descriptive content and do not create links. `itemGroup` members remain auxiliary references rather than generated catalog records.

## API

- `GET /api/items` returns sorted summaries and supports `name`, `source`, `type`, and `rarity` filters.
- `GET /api/items/{id}` returns one detail DTO.
- `GET /api/items/by-name/{name}?source=` returns all matching source-specific detail DTOs, or every exact-name match when source is omitted.

No separate base-item route is needed; `definitionKind` distinguishes base definitions.
