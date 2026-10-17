# Cults and Boons implementation

## Source audit and design checkpoint

The pinned `data/cultsboons.json` contains two independently modeled root arrays: 30 `cult` definitions and 12 `boon` definitions. `analysis/remaining-json/cults-and-boons/discover-cults-boons.mjs` reproduces the root/source/type counts, all 58 full-document recursive field paths, markup-tag frequencies, source identities, duplicate identity checks, and transformation-field scan. Machine evidence is `discovery-evidence.json`; the generated field/type matrix is `FIELD-COVERAGE.md`.

Both collections author `name`, `source`, `page`, `type`, and `entries`. Their remaining fields differ and are not unified into a generic domain model:

- Cults: `goal.entry` (9), `cultists.entry` (10), `signatureSpells.entry` (10), `otherSources` (4), and `reprintedAs` (2).
- Boons: `abilityEntry` (8), `signatureSpells.entry` (8), and `reprintedAs` (8).

Every `entries` array uses the existing sealed `EntryDte` alternatives. Across direct root entries there are 79 Cult elements (37 strings, 42 objects) and 31 Boon elements (12 strings, 19 objects). Nested objects are `entries` and `section` entry shapes. The strict shared entry deserializer covers the full corpus. There are no explicit null values, media/image paths, `_copy`, `_versions`, templates, prerequisites, grant records, option choices, or dedicated numeric/statistic fields.

Identity is unique on authored `(name, source)` in each collection. IDs are `cult:<source-slug>:<name-slug>` and `boon:<source-slug>:<name-slug>`; no category context is needed. All 30/12 IDs are unique. Cult and Boon remain separate types even when their authored reprint UIDs point to matching Cult names.

Cult classifications are Diabolical 13, Demonic 8, Elder Evil 5, and Elemental 4. All 12 Boons are classified Demonic. The repeated Cult names `Cult of Geryon` and `Cult of Zariel` occur in MTF and MPMM and therefore remain distinct through their source-qualified IDs.

## Production components

- `dte.cultboon`: strict typed `CultsAndBoonsFileDte`, `CultDte`, `BoonDte`, typed text/source wrappers, and a sealed `CultBoonReprintDte` union for the observed UID-string and `{uid, tag}` shapes.
- `dto.cultboon`: independent immutable `CultDefinition` and `BoonDefinition`, source-aware ID values, internal import records/provenance, and an explicit mapper.
- `CultsAndBoonsSourceLoader`: strictly loads the classpath resource once into typed Cult and Boon arrays. `CultCatalog` and `BoonCatalog` are separate immutable startup catalogs with independent ID/name/import indexes; both validate primary/secondary/reprint/markup source identities and reject duplicate IDs and authored identities.
- `CultsService` / `BoonsService`: deterministic case-insensitive name/source/type filtering and ambiguity-preserving exact name lookup.
- `CultSummaryDto`, `CultDto`, `BoonSummaryDto`, and `BoonDto`: separate metadata-only projections. They expose ID, name, source/page, authored `type`, secondary sources for Cults, reprint name/source/tag, and explicit supported cross-catalog links. They do not expose the entry trees, Cult goal/cultist text, signature-spell text, Boon `abilityEntry`, markup, or importer provenance.
- `CultsController`, `BoonsController`, and a scoped not-found exception handler provide read-only API access. Collections are small and fixed (30 and 12), so list routes are unpaginated.

Public `type` is the authored classification string, not a generic mechanical type. `abilityEntry`, goal, cultist descriptions, spell schedules, and all `entries` are prose-bearing source content and remain internal pending publication review. No cult membership, boon application, character eligibility, or effects execution is implemented.

## Cross-catalog references

Reproducible details and source examples are in [REFERENCE-INVENTORY.md](REFERENCE-INVENTORY.md). The importer scans known typed entry strings and the typed root text fields for explicit `spell`, `item`, `skill`, and `filter` markup only. Markup locators identify the source entry and tag occurrence. The filter remains a filter. Direct markup links receive reference semantics, never grant semantics.

The 75 supported markup references add RESOLVED 5, AMBIGUOUS 69, UNRESOLVED 0, DEFERRED 1 to the immutable shared resolver snapshot. The prior 3,875 references retain their original RESOLVED 1,697 / AMBIGUOUS 1,108 / UNRESOLVED 30 / DEFERRED 1,040 results. The combined snapshot has 3,950 occurrences. Creature, deity, and condition markup stays in internal entries because the shared resolver has no corresponding target kinds. Ten explicit reprint references are preserved separately; nine `boon`-tagged UIDs happen to match MPMM Cult identities, so no cross-kind relationship is inferred.

## Source Registry and persistence

Primary sources are MTF (15 Cults; 12 Boons), MPMM (10 Cults), PSD (1 Cult), and PotA (4 Cults). Four Cult secondary-source records point to MPMM. All ten reprint sources point to MPMM. Inline source values are `PotA`, `scag`, `vgm`, and `phb`. Every one resolves through the existing Source Registry; no registry change or metadata invention was needed. Exact authored source spelling is retained.

IDs retain the stable authored source/name identity needed for future persistence. Page, type, other-source references, reprint UIDs/tags, and source file provenance are retained. Descriptive source entries remain in separate internal provenance. The source contains no copy/version lineage to persist.

## Endpoints

- `GET /api/cults?name=&source=&type=`
- `GET /api/cults/{id}`
- `GET /api/cults/by-name/{name}?source=`
- `GET /api/boons?name=&source=&type=`
- `GET /api/boons/{id}`
- `GET /api/boons/by-name/{name}?source=`

List `name` is case-insensitive substring matching; source and type comparisons are case-insensitive exact matches. By-name routes are exact and return all matches unless source narrows them. Detail routes use the catalog's stable IDs.

## Verification

Dedicated tests cover the complete corpus, source/category counts, stable IDs and uniqueness, internal entry preservation, both reprint shapes, Source Registry validation, strict unknown-field/type rejection, public serialization boundaries, filters, REST status/lookup behavior, all new resolver references/outcomes, unchanged historical resolver outcomes, and packaged-classpath JAR loading.

Full Maven suite: **317 tests passed, 0 failures, 0 errors, 0 skipped**. The 75 new Cult/Boon inline references were added to the startup snapshot; the pre-existing 3,875 occurrences retain their prior outcome counts.
