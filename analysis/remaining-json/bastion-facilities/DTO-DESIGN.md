# Bastion Facility DTO Design Checkpoint

## Source audit

The pinned `data/bastions.json` file contains one root collection, `facility`, with 70 records. Every record has a unique `(source, name)` pair. Sources are AU (9), EFA (10), FRHoF (8), RHW (8), and XDMG (35). Each source is present in the shared Source Registry.

Every record has `name`, `source`, `page`, `facilityType`, `space`, and `entries`. `facilityType` is `basic` or `special`. `level`, `hirelings`, and `orders` occur in 64 records; `prerequisite` occurs in 34. Fluff flags occur in 16 records (`hasFluffImages` in 15, `hasFluff` in 1). No explicit nulls occur.

Facility source data has no authored cost/currency, dimensions, armor class, hit points, damage threshold/defenses, ability scores, speed, or structured action/benefit/duration fields. Those concepts must not be inferred from descriptive entries. Space is a list of source strings; hireling entries use `exact` or `min` plus optional `space`; orders are source-authored strings.

Prerequisites contain `membership`, `spellcastingFocus`, `otherSummary`, `expertise`, or `proficiency`. `spellcastingFocus` is polymorphic: boolean or string array. `expertise` has a boolean `skill`; `proficiency` has a string-array `skill`; membership is a list of strings; `otherSummary` has `entry` and `entrySummary` strings. These are structured prerequisites, not executable eligibility logic.

Top-level entries include 79 strings, 112 `entries` objects, and one table. The recursive entry schema also includes nested entries, table rows/cells, captions, labels/styles, columns, footnotes, and item records. Existing strict typed entry DTEs cover the observed entry forms; import loading remains strict and full-corpus-tested. Source markup includes spell, item, skill, action, creature, facility, reward, condition, sense, status, damage, variantrule, table, filter, book, dice, chance, DC, and bold tags. Markup is preserved internally as typed entry text and not exposed publicly.

There are no `_copy`, `_versions`, `reprintedAs`, `otherSources`, or record-level media path fields. No relationships are synthesized from matching names or prose. Inline tags are eligible for reference inventory only where an established resolver adapter supports the target kind.

## Identity and normalized model

Stable identity is `facility:<source>:<slug(name)>`, with source copied exactly and a deterministic NFC/lowercase ASCII slug. The complete corpus has no candidate collisions. `(source,name)` is checked independently during catalog construction so future collisions fail startup. Reprints/editions remain distinct through the authored source component.

The internal immutable `FacilityDefinition` retains identity, provenance (source and page), classification, level, prerequisite structures, space, hirelings, orders, and complete typed entries. Prerequisite focus uses an explicit boolean-or-string-list DTE union. No item, monster, or bastion-ownership model is reused.

## Public projections

The summary projection exposes stable ID, name, source, facility type, level, and space. Detail additionally exposes structured prerequisites, hireling requirements, and authored order labels. It omits page/import diagnostics, fluff flags, entries and all prose/markup, and media. No empty generic mechanics object is emitted. Cross-catalog references, if added, are lightweight resolver projections only and never include candidate diagnostics or target definitions.

## API and behavior boundary

The catalog is loaded from the classpath at startup, validates each source, creates immutable ID/name indexes and sorted summary projections, and does no source parsing or reference resolution per request. Read-only routes are `/api/bastion-facilities`, `/api/bastion-facilities/{id}`, and `/api/bastion-facilities/by-name/{name}`. Source, type, and level filters are supported from explicit fields. Ownership, construction, turns, orders, production, hirelings, upgrades, event generation, and eligibility remain future application/rules-engine concerns.

## Publication boundary

Entries contain third-party descriptive prose and 5eTools markup. They remain internal to the import model and are excluded from public DTOs. Only source-authored structured catalog metadata listed above is projected; this does not make narrative source text publishable.
