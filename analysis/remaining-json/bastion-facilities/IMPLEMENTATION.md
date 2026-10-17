# Bastion Facilities Catalog Implementation

## Corpus and identity

Imported all 70 roots from the pinned v2.37.0 `bastions.json` `facility` collection. Source distribution: AU 9, EFA 10, FRHoF 8, RHW 8, XDMG 35. Every source code is present in the shared Source Registry; no source registration or publication metadata was added.

Stable IDs are `facility:<source>:<slug(name)>`, preserving the authored source code and reprint/edition distinction. The complete source has no repeated `(source,name)` identities or slug ID collisions. Catalog startup validates both identities and all authored primary source codes. There is no copy, version, or reprint lineage in this file.

## Typed import and normalized model

Added strict `FacilityFileDte`, `FacilityDte`, typed hirelings and prerequisite records, and an explicit sealed boolean-or-string-array representation for `spellcastingFocus`. Facility entries use the existing strict typed entry hierarchy because their observed structures match its source semantics. Unknown source fields and unsupported focus shapes fail import; no generic object/map fields were introduced.

The immutable `FacilityDefinition` preserves source/page provenance, facility type, optional level, space labels, hirelings, orders, typed prerequisite variants, complete internal entries, and authored fluff availability flags. `otherSummary` and descriptive entries remain internal source data. Full corpus loads and source/type counts are verified at startup/tests.

Structured source mechanics are limited to classification, level, space, hireling counts, order labels, and prerequisite choices. The source has no structured cost/currency, dimensions, combat statistics, damage defenses, movement, abilities, numeric durations, or mechanical benefit fields. No such values are derived from prose. Prose-only mechanics are deferred to roadmap Step 7. This catalog is not an Item, Monster, Bastion instance, or executable Bastion rules engine.

## Public API

Immutable summary DTOs expose ID, name, source/page reference, facility type, level, and space. Detail DTOs additionally expose hireling requirements, order labels, and safe structured prerequisites, along with lightweight supported cross-catalog references. Descriptive `otherSummary.entry`/`entrySummary`, entries, markup, fluff flags, media paths, importer state, and resolver diagnostics are omitted. Public prerequisite projections do not include text-bearing `otherSummary` values.

Routes:

- `GET /api/bastion-facilities?source=&type=&level=`
- `GET /api/bastion-facilities/{id}`
- `GET /api/bastion-facilities/by-name/{name}?source=`

Catalog loading, validation, ID/name indexes, reference resolution, and summary projection happen at startup. Results are deterministic and collections immutable. The dataset has no duplicate names, while synthetic coverage verifies distinct source identities remain separately retrievable by name.

## References and boundaries

The facility entry markup produced 110 new resolver occurrences: 89 RESOLVED, 0 AMBIGUOUS, 6 UNRESOLVED, and 15 DEFERRED (26 spell links; 29 skill links; 48 item definition links; 7 item filters). The existing 3,954 outcomes remain unchanged at 1,704/1,178/30/1,042. See [REFERENCE-INVENTORY.md](REFERENCE-INVENTORY.md). Other inline tag kinds remain typed internal entry content without speculative target adapters.

No ownership, construction, turn processing, order execution, production, hireling management, upgrades, event generation, or eligibility calculation is included. Descriptive mechanics and tagged prose are excluded from public responses. This source contains prose subject to the repository's publication/licensing boundary; structured projection does not authorize prose or media publication.

## Verification

- Focused Bastion Facilities pipeline tests: 10 tests, passing.
- Full Maven suite: 335 tests, 0 failures, 0 errors.
- Packaged-JAR resource loading: covered by the focused test.
- Existing resolver baseline and additive facility reference outcomes: asserted in tests.
- `git diff --check`: passed. Git emitted only its repository line-ending conversion warnings; no whitespace errors were reported.

## Recommended next Step 6 catalog

Continue with Encounters, following the existing [implementation order](../IMPLEMENTATION-ORDER.md). Keep authored encounter tables distinct from runtime encounter generation, and only bind explicit creature/item UIDs supported by target adapters.
