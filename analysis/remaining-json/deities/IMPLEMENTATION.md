# Deities Catalog Implementation

## Source and identity

`src/main/resources/data/deities.json` is the pinned source (SHA-256:
`6666acc24818ed5c9fd963cdf1d3030b845dea5437be48dea54923dccaf7c5b8`). It has one `deity` root
collection and 563 definitions. Primary sources: PHB 195, SCAG 99, MTF 80, FRHoF 42, EGW 32, XDMG
27, ERLW 21, DSotDQ 21, DMG 20, MOT 15, VGM 9, VRGR 1, and GoS 1. All primary and secondary source
codes validate against the shared Source Registry; no registry changes were needed.

All 563 records have an authored pantheon; distribution is Dwarven 33, Norse 20, Elven 41,
Faerûnian 90, Egyptian 14, Greek 19, Eberron 42, Celtic 14, Exandria 32, Halfling 13, Dawn War
20, Theros 15, Forgotten Realms 37, Gnome 13, Gnomish 9, Nonhuman 24, Orc 12, Greyhawk 52,
Dragonlance 42, Duergar 2, Yuan-ti 3, Drow 14, and Unknown 2. Pantheon is an authored, stable
identity component. IDs use
`deity:<exact-source>:<normalized-pantheon>:<normalized-name>`. Normalized source/name/pantheon
identities and generated IDs are unique across the full corpus. The five PHB same-name collision
pairs remain separate; see `IDENTITY-DESIGN.md` for their contexts and IDs.

## Typed source and normalized models

Strict Jackson DTEs model the file metadata, all 21 observed deity root fields, typed source/page
references, typed symbol-image metadata, and the exact `_copy` identity. Recursive entries reuse
matching shared `EntryDte` variants and add a typed `insetReadaloud` entry. All 92 recursive paths
are listed in `FIELD-COVERAGE.md`; there are no observed null or empty source values. Optional list
absence remains distinguishable in the source DTE.

`DeityCopyResolver` resolves six VGM `_copy` records to exact SCAG name/source/pantheon definitions.
It inherits only missing alignment, domains, and symbol values; preserves child source/page/title/
entries and records parent IDs plus inherited-field provenance. It validates the matching authored
`customExtensionOf` UID. The copies are not collapsed into their SCAG definitions and their VGM IDs
remain distinct.

Immutable `DeityDefinition` preserves exact source identity/page, pantheon, category, alignment,
domains, title, aliases, province, plane, worshipers, symbol text, symbol media metadata, publication
flags, additional sources, reprint alias, copy lineage, and full typed entries. Alignment and domain
values remain ordered open strings, not enums or class references. No deity relationships are
inferred.

## Structured information and publication boundary

The catalog normalizes pantheon, category, alignment, domains, alternate names, source references,
and exact copy lineage. `symbol`, title, worshiper/province/plane descriptions, and recursive entry
content are retained internally but not published. All symbol artwork paths/credits, raw markup,
source publication flags, copy diagnostics, and internal provenance are excluded from public JSON.
Public summaries expose ID, name, source/page, pantheon, category, alignment, and domains. Details
add aliases, secondary sources, and supported lightweight references.

The API exposes source-authored domain and alignment metadata only. It does not grant spells,
assign a cleric domain or paladin oath, evaluate worship eligibility, implement piety, or execute
religious mechanics. Lore-only content remains deferred to Step 7 and licensing review.

## References and API

Existing adapters add 33 supported occurrences: SPELL 17 (17 resolved), ITEM 15 (1 resolved, 8
unresolved, 6 deferred), SKILL 1 (1 resolved). Aggregate: 19 resolved, 0 ambiguous, 8 unresolved,
6 deferred. Unsupported creature, reward, condition, publication prose, and deity relationships
remain internal. The existing 4,881 resolver outcomes are unchanged.

Read-only endpoints:

- `GET /api/deities?name=&source=&pantheon=&alignment=&domain=`
- `GET /api/deities/{id}`
- `GET /api/deities/by-name/{name}?source=&pantheon=`

Name lookup returns a list to preserve established duplicate-name behavior. Filtering by source and
pantheon can make a name unique; the five same-source PHB collisions are directly tested with
pantheon disambiguation. There are 563 fixed records, so the catalog follows the existing
non-paginated static-catalog convention.

## Verification

- Full corpus: 563 definitions deserialize through strict DTEs and validate all source references.
- Identity: 563 unique IDs, including the five PHB duplicate-name groups and six copy-derived IDs.
- Entry variants, copy lineage, public JSON exclusions, supported references, REST filters,
  source-aware duplicate lookup, immutability, and packaged-JAR resource loading are covered by
  `DeityCatalogTests`.
- Focused Deities tests and the full Maven suite pass: 380 tests, 0 failures, 0 errors, 0 skipped.
- `mvn -DskipTests package` succeeds; the packaged artifact is
  `target/dnd-pub-data-0.0.1-SNAPSHOT.jar`.
- The packaged-resource test loads all 563 definitions from a JAR classpath.
- `git diff --check` passes; Git reports only existing LF-to-CRLF working-copy warnings.
