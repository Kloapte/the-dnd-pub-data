# Classes DTO design checkpoint

Status: design entry point. The internal definitions are implemented in [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md), and public DTOs/catalogs/routes are implemented in [STAGE-C-IMPLEMENTATION.md](STAGE-C-IMPLEMENTATION.md).

The concrete design is split across:

- [NORMALIZED-MODEL-DESIGN.md](NORMALIZED-MODEL-DESIGN.md): source DTE boundary, four independent normalized definition kinds, proposed components and sealed variants, field destinations, catalogs, shared Source Registry integration, and unresolved design decisions.
- [DTO-API-DESIGN.md](DTO-API-DESIGN.md): public records/components, reference and choice representations, publication exclusions, serialization requirements, REST routes, queries, and future acceptance criteria.
- [IDENTITY-ANALYSIS.md](IDENTITY-ANALYSIS.md): complete source identity tuples, collision findings, proposed readable IDs, and UID defaults.

Stage B implemented strict typed DTEs, copy resolution, graph resolution and immutable internal normalized definitions. Stage C adds the public catalogs, DTOs, services, and read-only controllers. Character state and rule calculations remain out of scope. See [DTO-IMPLEMENTATION.md](DTO-IMPLEMENTATION.md) for the internal implementation entry point and [STAGE-C-IMPLEMENTATION.md](STAGE-C-IMPLEMENTATION.md) for the public API.
