# Vehicles and Vehicle Upgrades DTO Design Checkpoint

## Source audit

The pinned source is `src/main/resources/data/vehicles.json` (SHA-256
`7362a2d1348cbeed73119590276ca402f406a09001a6d9519d22af8ea2e00d45`). It has two distinct root arrays:

- `vehicle`: 39 definitions from 11 source codes.
- `vehicleUpgrade`: 31 definitions from BGDIA and GoS.

No `(source, name)` or generated ID collision occurs within either array. Source codes are present in the Source
Registry. There are no `_copy` or `_versions` declarations. One vehicle has a `reprintedAs` UID from DMG to XDMG;
vehicles also have two `otherSources` records. These codes are registry validated and provenance is retained.

Vehicle fields include shared names that do not share shape: `ac` is a number or an array containing a structured
AC record; `hp` is a number or a hit-point object; `speed` is a number or a structured movement value; `pace` is a
number or an object with mode values that themselves can be numbers or strings. `size` is a string or a string array.
`action` is a string or structured named/list action. `capCargo` is a number or string, and four numeric values are
fractional (`0.5` or `0.25`), so numeric capacities use `BigDecimal`. These differences use explicit sealed
variants and custom strict deserializers, not generic values.

Nested source structures include hull, control, movement, weapon, action station, station, trait, and recursive typed
entries. Weapon costs are records with numeric cost and optional note. Action thresholds use a fixed four-position
object, modeled as a typed record. Vehicle upgrades contain only name, source, page, `upgradeType` (string array), and
typed descriptive `entries`; observed entry variants are text, named entries, and tables. No structured cost,
compatibility restriction, prerequisite, benefit, or installed-upgrade relationship appears in the source.

Source markup includes tags for DCs, skills, damage, hits, conditions, actions, variant rules, attacks, objects,
creatures, feats, quick references, status, recharge, dice, spells, and adventures. Markup in prose remains internal;
only explicit references supported by the established extractor are considered for resolver integration. Numeric or
mechanical details appearing only in text are not normalized.

## Public DTO decision

Keep `VehicleDefinition` and `VehicleUpgradeDefinition` separate and retain complete typed source structures
internally. Summary DTOs expose identity, source, authored vehicle type or upgrade type, size and safe AC/HP/speed
metadata where source representation supports a direct structured projection. Vehicle details may additionally expose
terrain, capacities, dimensions, pace, ability scores, hull, movement, control, weapon, station, and action-statistic metadata after reviewing
each nested field for source-authored structure. Descriptive entries and their raw markup remain private. Upgrade DTOs
expose identity, source, and authored upgrade categories only: the source's upgrade descriptions are prose and do not
support fabricated costs, prerequisites, compatibility rules, or benefit fields.

All public records use `@JsonInclude(NON_EMPTY)`, immutable collections, and the existing lightweight cross-catalog
reference projection. No media, internal import diagnostics, or full target definitions are exposed.

## Catalog/API design

Two immutable startup catalogs share one classpath parse of `vehicles.json`, but keep independent indexes and public
types. IDs are `vehicle:<source>:<normalized-name-slug>` and
`vehicle-upgrade:<source>:<normalized-name-slug>`. The full corpus check found no collision, so no additional
discriminator is needed. Deterministic collection order is name, source, then ID; source-aware name lookup preserves
duplicate names across sources. Endpoints are read-only collection, detail, and by-name routes under
`/api/vehicles` and `/api/vehicle-upgrades` with source and authored type/category filters.

The catalogs preserve mechanics but do not execute movement, actions, weapons, capacities, repairs, crew assignments,
or upgrades. Vehicle upgrades are not Items, and vehicles remain distinct from Monsters and Objects.

## Reference checkpoint

The source contains explicit markup tags in entries, but no structured UID property or authored vehicle-to-upgrade
link. The implementation will extract only supported markup target kinds via the existing reference pipeline, retain
owner locators and roles, and expose target IDs only for authoritative exact resolutions. Unsupported tag kinds remain
source content and are documented; names in ordinary entry prose do not become relationships.
