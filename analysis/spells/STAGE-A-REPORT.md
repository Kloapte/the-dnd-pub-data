# Spells pipeline: Stage A report

## Findings

| Measure | Discovery result |
| --- | ---: |
| Recursive JSON files under `data/spells/` | 18 |
| Root collections | `spell`: 969 records; no other collections or `_meta` objects |
| Primary source identities | 18 |
| Distinct primary and explicit related source identities | 106; all covered by Source Registry |
| `_copy` roots | 0 |
| `_versions`/template roots | 0 |
| Unique recursively observed field paths | 148 |
| Explicit null values | 0 |
| Duplicate `(name, source)` identities / candidate readable-ID collisions | 0 / 0 |
| Repeated names across sources | 386 groups; 359 PHB/XPHB groups |
| Reprint references | 389; all exact targets resolve |
| Structured canonical class spell lists in spell roots | 0 |
| Class spell reference feasibility | 446/446 source-qualified direct UIDs exact-match; of 622 unqualified/empty-source references, 620 are ambiguous and two `branding smite` occurrences have one current candidate; 3 explicit XPHB choice candidates exact-match |

The full field and type evidence is in [normalized-field-inventory.json](normalized-field-inventory.json); file/source counts are in [SOURCE-INVENTORY.md](SOURCE-INVENTORY.md) and `discovery-summary.json`. [discover-spells.mjs](discover-spells.mjs) regenerates both machine-readable inventories from all JSON files and checks identity and registry coverage.

## Architecture recommendation

Use independent spell definitions with source-aware IDs and typed source references. Keep PHB/XPHB and all other primary sources separate. Model source-authored casting, range, components, duration, scaling, and tags as immutable typed values. Treat damage/healing/effects mostly as descriptive entry content unless the root carries an explicit structured tag/value; do not infer from prose. Keep spell grants and selectors on their owning class/feat/feature/background/race/item domains and link only explicit, reliable spell identities.

The proposed public surface is `SpellSummaryDto`, `SpellDto`, and separate safe value DTOs, served by list/detail/source-aware name endpoints. Public responses omit entry prose, raw markup, internal provenance, and import instructions. No full class spell-list corpus was found in the spell directory, so eligibility aggregation needs an explicit source decision.

## Existing code observed

The repository contains pre-existing `com.thedndpub.data.dte.spell` import records and earlier DTE mapping/schema artifacts. This Stage A task made no production spell DTE, normalized-definition, DTO, catalog, service, or controller changes. Findings below are based on recursive source analysis; existing importer artifacts do not replace the proposed Stage B/Stage C decisions.

## Risks and Stage B decisions

1. Represent source strings, source-qualified UIDs, reprint edges, and aliases as separate concepts; a name alone is not identity.
2. Keep source unions for material and consume, array/object scaling, entry/table polymorphism, and structured casting alternatives strict.
3. Preserve source formulas and dynamic level keys without evaluation; higher-level text is not a numeric progression.
4. There is no explicit false value for ritual or concentration in this corpus. Absence must not become false.
5. Spell root damage/healing/save/DC mechanics are incomplete. Do not derive mechanics from entries.
6. Eligibility selectors are not complete class lists. Unqualified references are ambiguous across PHB/XPHB and other sources.
7. No Source Registry additions are needed; no publication metadata should be invented.

## Documents and verification

- [Casting](CASTING-ANALYSIS.md), [mechanics](MECHANICS-ANALYSIS.md), [scaling](SCALING-ANALYSIS.md), [spell lists](SPELL-LIST-ANALYSIS.md), and [cross-catalog references](CROSS-CATALOG-ANALYSIS.md).
- [Identity](IDENTITY-ANALYSIS.md), [edition](EDITION-ANALYSIS.md), [copy/version](COPY-VERSION-ANALYSIS.md), and [Source Registry](SOURCE-REGISTRY-ANALYSIS.md).
- [Normalized model](NORMALIZED-MODEL-DESIGN.md), [DTO/API proposal](DTO-API-DESIGN.md), and reproducible [discovery summary](discovery-summary.json).

The complete existing Maven suite is run for Stage A verification. This report does not claim a spell catalog implementation.

Verification completed: `mvn test` reports **248 tests, 0 failures, 0 errors, 0 skipped** (`BUILD SUCCESS`). Discovery counts were regenerated from the pinned source corpus by running `node analysis/spells/discover-spells.mjs`.
