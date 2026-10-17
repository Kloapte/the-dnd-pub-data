# Objects DTO Design

## Identity and domain boundary

Objects are standalone destructible or interactable world entities. They remain distinct from equipment
`ItemDefinition` and creature `MonsterDefinition`, even when they have armor class, hit points, ability scores,
or attack-like authored text. Stable IDs use `object:<source>:<normalized-name-slug>`. The corpus has no duplicate
`(source, name)` identity or generated variants. Reprint relationships remain source-authored lineage and do not
merge definitions.

## Internal model

`ObjectDefinition` retains the normalized identity and authored source page, category, NPC marker, size, AC, HP,
speed, six ability scores, senses, damage and condition defenses, typed action structures, source entries,
reprint UIDs, token/alternate-art source references, and SRD flag. `ObjectStatisticDte`, `ObjectSpeedDte`, and
`ObjectDefenseDte` are source-shaped sealed polymorphic values. Actions have typed text, attack, and list forms.
Full descriptive entry and action text stays internal.

Importer-only fields such as token credit/customization and image availability stay available through the catalog's
source provenance record and are not domain or API fields. There are no copy/template declarations, dimensions,
damage thresholds, or authored movement beyond scalar speed / walk and burrow values.

## Public projections

Summary fields: `id`, `name`, `source`, `category`, `size`, `armorClass`, and `hitPoints`.

Public statistics expose a numeric `value` or a `specialValuePresent` marker, never authored special text. Defense
values expose either a source code/name or a special-value marker; source prose is not copied into the API.

Detail adds the source NPC marker, speed, six ability scores, structured damage/condition defenses, SRD marker, and
supported exact cross-catalog references. It omits actions and entries because their authored text includes prose and
5eTools markup. Token paths, credits, alternate art, and importer flags are also omitted. Numeric zero and boolean
false values remain meaningful under `NON_EMPTY` serialization.

Routes are `GET /api/objects`, `GET /api/objects/{id}`, and
`GET /api/objects/by-name/{name}?source=<source>`. List lookup supports `name`, `source`, and authored object
category (`type`) filters. Exact duplicate names across sources return all matches unless a source is supplied.

## References and publication

The corpus contains two spell references and two item tags. Both spell tags and the source-qualified item tag use
existing resolver adapters and receive a public `targetId` only for authoritative exact resolutions. One item tag
uses extended display/configuration markup and remains deferred with no target ID. Other observed markup target
families lack adapters and are retained inside internal text. Reprint UIDs are lineage, not resolver edges. No
mechanic is derived from prose.
The public API does not expose source descriptions, authored action text, token/media paths, or attribution strings.
Structured values remain source-attributed; the repository license does not establish publication rights for all
source material.
