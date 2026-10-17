# Proposed public Spell API

Use a summary/detail split with explicit mappers from normalized definitions:

- `SpellSummaryDto`: stable ID, name, source, page when present, level, school, and safe casting summary fields supported by structured source. School is presented as a typed `{code,name}` value.
- `SpellDto`: summary identity plus structured casting-time alternatives, range/area, components/material requirements, durations/concentration, ritual, scaling values, safe mechanic classifications, secondary/reprint source relationships, and explicitly represented spell references where present.
- Nested public records should be separate immutable DTO types for casting time, distance/range, components/material, duration, scaling, source reference, and spell identity/reference.

All public records use `@JsonInclude(JsonInclude.Include.NON_EMPTY)`. Preserve numeric `0` for cantrips and false values when source-authored. Keep absent distinct from false internally where needed. Empty arrays/strings should not produce empty nested objects.

Potential routes are `GET /api/spells`, `GET /api/spells/{id}`, and `GET /api/spells/by-name/{name}?source=`. Name lookup must return/preserve multiple source matches; it must never select arbitrarily. Candidate filters include exact source, level, school, ritual, concentration, casting-time unit, and structured damage classification. Class/subclass eligibility filters are not recommended until an authoritative eligibility aggregation model exists; a class grant selector is not itself a full spell list.

School presentation uses the eight source codes observed across all 969 spell roots: `A` Abjuration, `C` Conjuration, `D` Divination, `E` Enchantment, `I` Illusion, `N` Necromancy, `T` Transmutation, and `V` Evocation. The normalized definition retains the source code. The public DTO represents school as `{ "code": "V", "name": "Evocation" }`. This changes the existing JSON property type from string to object and is a deliberate breaking response change; filters accept either code or full name. See `SCHOOL-METADATA-IMPLEMENTATION.md`.

Do not expose recursive entries, descriptive rules prose, raw 5eTools markup, renderer/media paths, copy instructions, import diagnostics, or internal provenance. A safe structured projection is not a publication decision for descriptive text. Do not publish mechanics inferred from prose. Include source relationships only as reviewed safe identity metadata.

## Stage C implementation

Implemented public records live under `com.thedndpub.data.dto.spell.api`: `SpellSummaryDto`, `SpellDto`, and typed records for casting time, range/distance, components/material, duration, mechanics, scaling, and source/reprint relationships. `SpellPublicMapper` projects only the structured Stage B model. It omits material descriptions and textual consumption notes, and represents casting condition/note text only with presence flags because those strings may contain markup or descriptive rules text. It does not expose edition as a separate field; source identity carries PHB/XPHB distinction.

`SpellCatalog`, `SpellsService`, and `SpellsController` implement the immutable 969-definition catalog and routes `GET /api/spells`, `GET /api/spells/{id}`, and `GET /api/spells/by-name/{name}?source=`. Collection filters are name, source, level, school, ritual, concentration, and structured damage type. Results are deterministic and preserve multiple exact-name source matches. No class/subclass eligibility filters are exposed because spell definitions contain no authoritative eligibility data. See [STAGE-C-IMPLEMENTATION.md](STAGE-C-IMPLEMENTATION.md) for test coverage and projection limits.

## Stage D implementation and investigation

Stage D retains the Stage C public fields and changes only the public `school` representation to a typed `{code,name}` object. The backend `SpellSchool` enum is authoritative and rejects unknown codes. School filtering accepts codes or full names. Descriptive prose and markup remain excluded; no generic formula extraction was added. Full-corpus markup and mechanics evidence is recorded in `STAGE-D-MECHANICS-INVESTIGATION.md`, `INLINE-MARKUP-INVENTORY.md`, and `MECHANICS-EXTRACTION-DESIGN.md`.
