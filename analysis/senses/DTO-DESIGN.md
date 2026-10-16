# Sense DTO Design

## Concepts

`SenseDefinition` is a reusable source-specific rules definition. Its identity is `SenseId` (`sense:<source-slug>:<name-slug>`), with display name and a shared `SourceReference` carrying the source identity and page. Definitions from different sources remain separate, even when their names match.

`Configured Sense` is a future entity occurrence, such as a creature's sense with a range or qualifier. The source file does not provide structured configuration data, so no configured-sense type is introduced here. In particular, prose mentioning a “specific range” does not establish a value or unit.

## Mechanics and source content

No explicit structured mechanical properties are present. `entries` are descriptive/explanatory rules text, including 5eTools markup and a structured Truesight list. They are retained as typed source entries in `SenseImportProvenance` for import fidelity, but not exposed via public APIs. Source availability flags and reprint strings also remain provenance. Reprint links are validated by the catalog.

The public `SenseDto` and `SenseSummaryDto` contain only stable ID, name, and source reference (including page). No source prose, availability flags, or importer details are serialized.

## Reuse and future relationships

Future monsters, species/races, characters, spells, feats, magic items, and conditions may refer to a `SenseId`. A creature's configured sense should remain distinct from the reusable definition and may eventually carry source-supported range/qualifier values. Character proficiency and ability-check mechanics do not apply to this concept. Monster source data currently exposes configured sense text as strings; migrating that to stable references is outside scope.

## API

- `GET /api/senses` returns sorted summaries and accepts optional case-insensitive `name` and `source` filters.
- `GET /api/senses/{id}` returns one source-specific definition.
- `GET /api/senses/by-name/{name}?source=...` returns all matching definitions or filters to one source; missing matches use the existing 404 convention.
