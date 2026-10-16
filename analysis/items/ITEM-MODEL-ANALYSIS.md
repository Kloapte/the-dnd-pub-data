# Relationship between Items and Base Items

## Decision

Use one unified Item Catalog with a `definitionKind` discriminator (`ITEM` or `BASE_ITEM`). Both collections are independently usable, source-specific definitions. A base item is a reusable mundane/equipment definition, while a root `item` may be a concrete magic item, special equipment definition, or copy-derived variant. A separate endpoint/catalog is unnecessary because the identities and query semantics are compatible.

## Evidence

- `items.json:item`: 2,512 concrete records; each has a unique `(name, source)` identity.
- `items-base.json:baseitem`: 230 base equipment records; each has a unique `(name, source)` identity.
- There are 241 explicit `baseItem` references on item records. They use `name|source` UIDs, with case variation in source codes. These are references to base item identity, not invitations to merge the definitions.
- There are no identity collisions between `item` and `baseitem`.
- `itemGroup` has 119 records with an `items` member list. These are group definitions/selectors, not independent item instances; their members refer to individual item definitions or grouped name/source identifiers. They are retained as auxiliary import data and not counted as item catalog definitions.
- Other `items-base.json` collections define properties, types, type-specific supplemental entries, item entry templates, and mastery rules. They enrich classification/reference data but are not independently retrievable equipment.
- Fifteen identity overlaps occur between item groups and auxiliary `itemEntry`/`itemType` records. These are different collection semantics, not definition collisions.

Base-item UIDs are validated against the baseitem set when source-qualified identity is present. Name/source matching is exact after case-normalizing the source code; no fuzzy name matching is used. References are preserved even when unresolved, with diagnostics causing startup failure for unsupported required references.

The item catalog does not import `magicvariants.json`. Its definition model retains base-item identity separately from source data and can later accept explicit variant-resolution references without treating a magic variant as an item prematurely.
