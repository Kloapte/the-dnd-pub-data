# Vehicles and Vehicle Upgrades Implementation

## Scope and source

`src/main/resources/data/vehicles.json` contains two distinct definition roots:

| Catalog | Count | Authored sources |
|---|---:|---|
| Vehicles | 39 | AAG 16, GoS 6, BGDIA 4, MTF 3, EFA 3, AI 2, DMG 1, XDMG 1, DD 1, DitLCoT 1, VEoR 1 |
| Vehicle Upgrades | 31 | GoS 22, BGDIA 9 |

The exact source fingerprint is `7362a2d1348cbeed73119590276ca402f406a09001a6d9519d22af8ea2e00d45`. Complete paths, types,
counts, and presence evidence are captured in [`source-audit.json`](source-audit.json); the readable field inventory is
[`FIELD-COVERAGE.md`](FIELD-COVERAGE.md). The design checkpoint is [`DTO-DESIGN.md`](DTO-DESIGN.md).

## Identity and provenance

IDs use `vehicle:<source>:<normalized-name-slug>` and
`vehicle-upgrade:<source>:<normalized-name-slug>`. Full-corpus `(source, name)` and generated ID validation found no
collisions. Names repeated across sources remain separately addressable and source-aware lookup returns every match.
All primary, secondary, and reprint source codes validate against the Source Registry; no registry changes were needed.
One DMG vehicle has a reprint UID targeting XDMG, and two vehicles have `otherSources` citations. Both are retained.
Neither collection contains `_copy` or `_versions`; Vehicle Upgrades have no reprint or secondary-source fields.

## Typed import and normalized definitions

`VehicleFileDte` reads both roots once with unknown-property failure enabled. Vehicle source types preserve scalar/list
size, numeric/detailed AC, numeric/structured HP, numeric/detailed speed, numeric/object pace, numeric/text cargo
capacity, and text/object action forms with explicit sealed variants and strict deserializers. Typed nested records cover
hull, control, movement, movement-speed descriptors, weapon mounts and costs, actions, stations, traits, reactions,
action thresholds, source references, and recursive common entry variants. Vehicle Upgrade records keep their own
typed classification and entry structure.

`VehicleDefinition` and `VehicleUpgradeDefinition` are separate immutable catalog records. Full authored source
structures remain inside the normalized vehicle record; typed descriptive entries remain internal for both catalogs.
All 70 definitions load. Numeric cargo capacity uses `BigDecimal`: the corpus contains `0.5` and `0.25`, so integer
coercion would have lost source data. Meaningful ability-score zeroes are retained. No explicit false or null values
occur in the source snapshot. Action thresholds preserve authored zero positions as numeric map keys.

The vehicle source supplies types OBJECT, SHIP, SPELLJAMMER, INFWAR, ELEMENTAL_AIRSHIP, and CREATURE; these are
vehicle classifications, not Item/Object/Monster identity. Vehicle Upgrade type strings remain authored values such as
`SHP:W` and `IWM:A`; no category enum or compatibility meaning is inferred.

Structured vehicle data includes:

- Size, terrain, dimensions, crew/passenger/creature/cargo capacities, weight, and numeric cost.
- Top-level and hull AC/HP/damage-threshold values, component AC/HP, and action-threshold positions.
- Ability scores, speed and pace forms, movement component metadata, hull metadata, and damage/condition immunity lists.
- Weapon mount AC/HP/damage threshold, crew/count/size, and structured numeric costs.
- Action, station, control, trait, and reaction records retained internally as typed data.

Only `immune` and `conditionImmune` defense properties occur. No vulnerability/resistance property exists. `cost` has
no currency field in this source and the API does not label it with a currency. Vehicle Upgrade definitions contain no
structured cost, prerequisite, compatibility restriction, or benefit; all authored upgrade rules appear in entries.
Those prose-only effects and operating instructions are deferred to roadmap Step 7 and are not parsed as mechanics.

## Public projections and API

Summary/detail DTOs expose stable ID, name, `SourceReference`, vehicle classification, size, safe AC/HP/speed metadata,
and selected structured detail metadata. Detail fields include terrain, capacities, dimensions, speed/pace, hull,
movement/control/station component statistics, weapon mount statistics and numeric costs, action thresholds, ability
scores, weight, cost, immunity lists, and supported lightweight references. Prose, markup, component instructions,
weapon notes, action entries, media flags, token metadata, and importer details are not projected.

Vehicle Upgrade summary/detail DTOs expose ID, name, source, authored upgrade type codes, and supported lightweight
references. Their descriptive `entries` are retained internally but not published, and no fabricated mechanical block
is emitted.

Read-only routes are:

- `GET /api/vehicles?name=&source=&type=`
- `GET /api/vehicles/{id}`
- `GET /api/vehicles/by-name/{name}?source=`
- `GET /api/vehicle-upgrades?name=&source=&type=`
- `GET /api/vehicle-upgrades/{id}`
- `GET /api/vehicle-upgrades/by-name/{name}?source=`

Each catalog is indexed once at startup, uses deterministic ordering, source-aware name lookup, immutable collections,
and in-memory projections. Both catalogs share one source loader and one parse of the file. No ownership, construction,
installation, movement, combat, repair, cargo, or upgrade-execution service is implemented.

## References and publication boundaries

The four explicit markup references supported by existing adapters are two `{@spell}` and two `{@skill}` occurrences.
They resolve as: 1 RESOLVED (source-qualified exact match), 2 AMBIGUOUS, 0 UNRESOLVED, and 1 DEFERRED. Only the exact
resolution exposes a `targetId`. The prior resolver snapshot remains 4,516 occurrences with the same outcome counts;
the four vehicle references are counted separately. No Vehicle Upgrade reference and no structured vehicle-to-upgrade
relationship occurs.

Markup for objects, creatures, feats, actions, conditions, variant rules, adventures, damage, checks, and formatting tags
remains within typed descriptive source entries. The source content is preserved internally, but these tags are not
treated as authoritative references unless an existing typed target adapter supports them. No new resolver target kind
was added. Reference details and tag counts are in [`REFERENCE-INVENTORY.md`](REFERENCE-INVENTORY.md).

Public DTOs follow the existing licensing boundary: source-authored mechanics and short classification metadata only;
no descriptive vehicle/upgrade prose, raw markup, source media paths, resolver diagnostics, candidate lists, or target
definitions are published. Structured source organization alone is not treated as permission to redistribute authored
text.

## Verification

- Complete strict source deserialization: 39 Vehicles and 31 Vehicle Upgrades.
- Source registry checks, duplicate identity checks, polymorphic values, fractional capacities, strict unknown-field
  rejection, immutability, sorting, public serialization, references, REST filters, and packaged-JAR resource loading
  are covered in `VehiclesCatalogTests`.
- Resolver baseline assertions filter the new owner kinds and verify the supplied pre-implementation count and outcome
  distribution; the new references are asserted independently.
- Full Maven suite: 362 tests passed (355 baseline plus 7 Vehicles/Upgrades tests).
- `mvn package` completed successfully; the packaged JAR includes the classpath source and the test suite's
  packaged-JAR loading check passed.
- `git diff --check` passed. Git reported only line-ending normalization warnings for existing Windows checkout files.
