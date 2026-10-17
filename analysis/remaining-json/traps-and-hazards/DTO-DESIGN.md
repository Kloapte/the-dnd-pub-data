# Traps and Hazards DTO Design Checkpoint

## Source findings

The pinned `data/trapshazards.json` file has two root arrays: 37 `trap` records and 73 `hazard` records. Every primary source code is in the Source Registry. Neither array contains `_copy` or `_versions`; there is no identity collision for `(source, name)`. The complete field and type inventory is in `FIELD-COVERAGE.md` and the machine-readable audit is `source-audit.json`.

Trap records have six authored `trapHazType` values (`CMPX`, `HAUNT`, `MAG`, `MECH`, `SMPL`, `TRP`), with one record carrying one or more `rating` objects. Traps additionally use `effect`, `trigger`, `countermeasures`, `duration`, and the complex-trap `initiative`, `initiativeNote`, `hauntBonus`, `eActive`, `eDynamic`, and `eConstant` structures. Hazards use five `trapHazType` values (`ENV`, `EST`, `GEN`, `WLD`, `WTH`); 33 have no type. Hazards have ratings, entries, source/page, reprint/SRD metadata, media availability flags, and two `otherSources` records. No structured detection/disarming checks, saves, damage blocks, character levels, or explicit mechanical reference fields occur at the root.

Nested descriptive entries are heterogeneous strings and typed objects. They include recursive named sections, tables and table cells. Complex-trap stage/countermeasure records have typed `type`, `style`, and named `items`, whose content is authored entry text. A small number of nested `entries` arrays contain table objects. All are retained internally through typed entry structures; their prose and markup are excluded from public output.

## Proposed internal types

- Separate `TrapDefinition` and `HazardDefinition`, IDs `trap:<source>:<slug>` and `hazard:<source>:<slug>`.
- Separate `TrapDte` and `HazardDte` root records, composed in one strict `TrapHazardsFileDte` to deserialize the file once.
- Shared narrow DTEs only for source-identical rating, source reference, and string-or-UID reprint values.
- Trap-only typed stage and countermeasure structures. Text and structured variants are explicit; nested entries use the existing sealed `EntryDte` hierarchy.
- Hazard classification and trap classification remain source codes represented as strings; their code sets and meanings differ.
- Both normalized definitions preserve the full authored entry tree, source/page, ratings, reprints, and media availability metadata internally. Hazard `otherSources` is retained. Trap mechanics (trigger/effect/countermeasure/duration and stages) remain structured, not executed.

## Identity and indexes

IDs are source-aware and readable. Full-corpus validation found no duplicate `(source, name)` pair in either catalog, so no additional context or array index is needed. Names can repeat across sources; name lookup remains a list and accepts an optional exact source filter. Stable ordering is name, source, then ID.

## Public DTOs

Separate summary/detail records will expose ID, name, source, authored classification, and safe rating metadata. Trap detail may expose authored initiative as a number and a safe duration type when present. The source's trigger, effect, countermeasure and stage content are instructions/rules text, so those values and all hazard/trap entries stay internal pending publication review. No public record will contain empty placeholder mechanics, raw markup, page prose, internal source flags, resolver diagnostics, or media paths.

## References and publication

There are no structured mechanical reference fields. Entry prose and trap instruction fields contain explicit markup tokens. Spell, item, skill, and filter markup use existing resolver adapters and are extracted with owner IDs, exact field locators, semantic roles, and source qualification; they remain references, not grants. Other markup remains preserved in typed source entries. The reference inventory documents this adapter boundary.

## HTTP representation

Use independent immutable catalogs and services with `/api/traps` and `/api/hazards`, each supporting collection summaries, ID detail, and source-aware `/by-name/{name}` lookup. Source and authored type filters are meaningful. No execution endpoints are part of this catalog.

## Confirmed limitations

Detection/disarming, saving throws, severity/level ranges, numerical damage, reset behavior, and encounter suitability are not authored as structured fields in these roots. If supplied only by descriptive text, they remain internal and are deferred to Step 7 interpretation/publication review.
