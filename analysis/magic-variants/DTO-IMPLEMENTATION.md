# Magic Variants DTO Implementation

Implemented typed DTEs and strict custom deserialization for the complete 230-record corpus, including string-or-array condition values, attached-spell shapes, and the observed copy operations. Normalization resolves the five source copy chains in source order, validates primary/secondary/reprint and structured referenced source codes against Source Registry, and separates source records/operations from public definitions.

The immutable startup-loaded Magic Variant Catalog contains 230 rules/transformations and auxiliary loot-table metadata. It indexes by ID/name and filters case-insensitively by name, source, and source type. Matching against the existing immutable Item Catalog is exact and does not mutate it. Results are deterministic. Statistics expose inventory and potential generation measurements, not generated item records.

REST endpoints:

- `GET /api/magic-variants` with optional `name`, `source`, and `type` filters.
- `GET /api/magic-variants/{id}` for detail.
- `GET /api/magic-variants/by-name/{name}?source=` for all or source-filtered matches.

Focused coverage is in `MagicVariantPipelineTests`: complete strict deserialization, identities/counts, alternatives and exclusions, transformations, copy operations, source coverage, filtering, serialization, errors, REST behavior, resolver cycle/duplicate rejection, and JAR resource loading. The focused suite passed (12 tests). The full Maven suite passed with 152 tests, zero failures, zero errors, and zero skipped.

Known limitation: the five item-specific transformations ultimately refer to two XDMG item anchors absent from the current Item Catalog. They remain typed transformations with unresolved name/source links; no concrete item is emitted. Rule stacking and formula evaluation are intentionally undefined.
