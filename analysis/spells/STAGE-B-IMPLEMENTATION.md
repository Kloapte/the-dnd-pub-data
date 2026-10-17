# Spells pipeline: Stage B implementation

## Scope

Stage B implements typed source loading and immutable internal spell normalization. It does not add public Spell DTOs, catalogs, services, or endpoints; those remain Stage C work. The Stage A findings remain the discovery record. Implementation checks the pinned corpus against those findings.

## Components

- Existing records in `com.thedndpub.data.dte.spell` were reused. `SpellDte` now retains the four root flags `basicRules`, `basicRules2024`, `hasFluff`, and `hasFluffImages` rather than ignoring them.
- `SpellFileDte` models the source file root. `SpellSourceLoader` discovers `data/spells/*.json` from the classpath, sorts files deterministically, enables strict unknown-property handling, and reads resources from directories or packaged JARs. `SpellSourceCorpus` and `SpellSourceDocument` retain file boundaries.
- Casting, range, components/material, duration, scaling, mechanics tags, identities, source relationships, and validation results are represented by immutable records in `dto.spell`.
- `SpellDefinitionNormalizer` validates identities and source codes with the shared `SourceRegistry`, makes source-aware IDs, normalizes structured values without deriving mechanics from prose, resolves reprint UIDs, and returns definitions sorted by ID.

The custom material and scaling deserializers inspect JSON tokens to handle the observed source unions. They reject unknown members and unsupported shapes. The scaling `Map<String,String>` was replaced by an ordered immutable list of `ScalingLevelDiceValueDte(levelKey, formula)` values, retaining the source's dynamic level keys without an untyped map.

## Corpus validation

| Check | Result |
| --- | ---: |
| Source JSON files loaded | 18 |
| Root spell records deserialized | 969 |
| Normalized definitions | 969 |
| Unique `(name, source)` identities | 969 |
| Readable ID collisions | 0 |
| Distinct validated primary/explicit source identities | 106 |
| Explicit reprint edges resolved | 389 / 389 |
| Copy/version/template expansions | 0 required |
| Discovered field paths compared to Stage A inventory | 148 / 148 |

IDs preserve the original name and primary source, with a normalized readable slug; for example, PHB and XPHB Shield are distinct `spell:phb:shield` and `spell:xphb:shield` definitions. Reprint edges point to independently addressable definitions and do not merge them. Source identity validation uses the existing registry; no registry entries or publication metadata were added.

Strict Jackson loading successfully deserializes every corpus file. A regression test compares the complete recursively observed path set against Stage A's machine-readable field inventory; strict unknown-field rejection checks that source members are modeled rather than silently skipped. The existing shared entry hierarchy retains descriptive entries internally. No source field is converted from prose into mechanics.

## Normalized structured data

- Casting time alternatives retain authored numeric amount, unit, condition, and note. Range retains the authored range type and typed distance amount/unit. Components retain V/S/R values and the material string/object union; material cost and boolean/string consume forms are preserved, including false and zero.
- Durations preserve ordered alternatives, duration amount/type/up-to, concentration, and end markers. Ritual remains nullable to distinguish absence from explicit false.
- Source-authored damage, save, attack, condition, area, creature-type, and other tags remain typed classification lists. They are not executable effects and no damage, healing, save DC, or condition mechanics are derived from descriptive entries.
- Scaling object/array alternatives are retained as typed scaling records with ordered `(levelKey, formula)` values. Higher-level descriptions remain in the internal typed entry tree; no progression or damage is calculated.
- Spell roots contain no canonical class/subclass spell-list membership structures. Eligibility selectors and spell grants remain in their owning domains and are not bound here. The 446 exact class references, ambiguous unqualified references, and three explicit candidates remain deferred to cross-catalog integration; the two currently single-candidate `branding smite` references are not bound.
- Internal import metadata retains source availability flags and SRD catalog markers for future import/publication decisions. It is not a public API projection.

## Tests and verification

Dedicated coverage includes complete source loading and strict mapping, all 148 field paths, the corpus normalization counts, source-aware ID uniqueness and PHB/XPHB separation, reprint resolution, representative casting/range/component/duration/scaling forms, structured tags without prose inference, immutability, deterministic normalization, unknown source and missing reprint rejection, malformed scaling rejection, and packaged-JAR resource loading.

Focused spell tests: **17 passed**. Full Maven verification: `mvn test` reports **261 tests, 0 failures, 0 errors, 0 skipped** (`BUILD SUCCESS`), up from the 248-test Stage A baseline.

## Stage C and known boundaries

Stage C still needs safe public `SpellSummaryDto` and `SpellDto` projections, immutable catalog/indexes, source-aware query services, and REST endpoints. It must exclude descriptive entries, raw markup, import metadata, and internal provenance from public responses. No Spell Catalog is needed to retain spell references in Stage B.

Spell roots do not provide structured numeric healing/damage effects or complete class eligibility lists. Some mechanical interpretation exists only in descriptive entry content and remains intentionally unmodeled. The class spell-reference binding problem is unchanged: 620 unqualified references have multiple candidates; Stage B does not invent source identities or bind references heuristically.
