# Spells pipeline: Stage C implementation

## Public components

The internal Stage B `SpellDefinition` remains the catalog input. `SpellCatalog` is a Spring singleton that loads and normalizes the spell corpus once during startup, then builds immutable indexes by ID, case-folded name, and source. It validates the expected 969 definitions and every reprint target. Source validation is performed by Stage B normalization against the shared Source Registry. The catalog and loader work with packaged JAR resources.

`SpellPublicMapper` creates a lightweight `SpellSummaryDto` and a `SpellDto` detail view. Public value records represent casting time, range/distance, components/material presence, duration, mechanics classifications, scaling formulas, secondary sources, and reprint targets. All public records use `@JsonInclude(NON_EMPTY)` and defensive list copies. The projection does not expose internal DTEs, entries, import metadata, source availability flags, or provenance.

Material component prose and textual consume notes are not projected. The public API exposes whether material is required, structured cost, and an explicit boolean consume value where authored. Casting-time condition and note strings are also omitted because full-corpus values contain 5eTools markup and descriptive trigger text; `hasCondition` and `hasNote` indicate authored presence without publishing the source strings. Spell edition is not a distinct normalized field, so clients use the preserved source identity (for example, PHB or XPHB).

## Catalog and query behavior

The immutable catalog contains **969 spells**. IDs remain the Stage B readable source-aware IDs; matching names in different sources are separate records. Exact by-name lookup returns every matching definition unless a source filter narrows it. Filters are case-insensitive for source, name, school, and damage type. Name filtering on collection queries is substring-based.

Supported collection filters are `name`, `source`, `level`, `school`, `ritual`, `concentration`, and `damageType`. Filters combine conjunctively. Negative levels are rejected as invalid input. Unknown IDs and missing exact names return 404 responses. The full fixed catalog is returned as summaries without pagination, consistent with existing class catalog endpoints.

Routes:

- `GET /api/spells` — summaries, with the supported filters above.
- `GET /api/spells/{id}` — detail by stable ID; IDs with colons are supported.
- `GET /api/spells/by-name/{name}?source=` — all exact case-insensitive name matches, optionally narrowed by source.

No `classId` or `subclassId` filters are exposed. Stage A/B established that spell definitions do not contain authoritative class/subclass eligibility lists. Selectors and grants remain owned by their class/feat/background/race/feature domains; cross-catalog references are still unbound, including the 620 ambiguous unqualified class references and the two single-candidate `branding smite` occurrences.

## Structured coverage and limits

Public details preserve source-supported level, school, casting-time amount/unit alternatives, range type and distance, V/S/R component flags, material presence/cost/boolean consumption, duration alternatives, concentration, all structured classification lists, scaling labels and level/formula values, and primary/additional/other/reference/reprint source relationships. Reprint links use target spell IDs and retain target name/source; all 389 links resolve. Scaling formulas are passed through as source-authored formula strings and are not evaluated. The source provides numeric material cost without a separate currency field, so no unit is invented. Range geometry/distance is exposed where present; there is no independent structured area-dimension model in the normalized definitions.

There is no public description, raw markup, prose-derived damage/healing, save DC, or generic spell effect model. Damage and condition properties expose the Stage B authored classification tags only. Higher-level prose remains internal. The source does not justify a structured numeric healing/damage schedule or complete spell-list eligibility model, so those are omitted.

## Verification

`SpellPublicApiTests` verifies all 969 IDs retrieve, all 389 reprint relationships resolve to catalog targets, PHB/XPHB separation and name ambiguity, deterministic combined filters, immutable catalog indexes, REST responses/errors, false and zero serialization, and exclusion of entries, markup, and internal fields in the serialized detail of every spell. It also loads the full 18-file corpus from a temporary packaged JAR.

Focused Stage C tests: **9 passed**. Full Maven verification: `mvn test` reports **270 tests, 0 failures, 0 errors, 0 skipped** (`BUILD SUCCESS`), up from the 261-test Stage B baseline.
