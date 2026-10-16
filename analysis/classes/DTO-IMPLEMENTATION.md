# Internal class definition implementation

Stage B implements the internal normalized definitions and mappers described in [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md). Source models are under `dte.classes`, immutable internal definition/value types under `dto.classes`, and import/resolution/normalization components under `importer.classes`.

There are four independently addressable internal definition kinds with source/parent/level-aware IDs. Import provenance, descriptions and file metadata remain internal. Public DTOs, catalogs, services, REST controllers and public serialization contracts are deferred to Stage C; the skill's public API phases have not been executed.
