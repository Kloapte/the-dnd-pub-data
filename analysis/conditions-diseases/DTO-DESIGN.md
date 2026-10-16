# Conditions and diseases DTO design

## Architecture decision

Use shared import infrastructure with distinct Condition and Disease domain models and catalogs (option C). A condition is a reusable named rules concept; a disease is a source-specific disease entry whose transmission, symptoms, and recovery rules are often prose. Their source schema already differs: conditions have secondary sources and Basic Rules flags, while diseases may have a source `type` classification. Combining them into one public definition with a discriminator would obscure these differences. Separate implementations also preserve the requested `/conditions` and `/diseases` contracts. Shared source identity/reference types and common entry DTEs remain reusable.

Rejected: one shared definition catalog would imply equivalent semantics and require nullable fields for unlike source metadata. Fully separate import infrastructure would duplicate source identity, reference validation, and entry handling without benefit. The `status` root collection (5 records) is inventoried and typed as part of source deserialization, but is not a condition or disease and is not exposed as an unrelated third API/catalog in this task.

## Models

- Source DTEs: `ConditionsDiseasesFileDte`, `ConditionDte`, `DiseaseDte`, and `StatusDte`; recursively typed shared `EntryDte` content; typed secondary source occurrence references.
- Normalized internal definitions: `ConditionDefinition` and `DiseaseDefinition`, each with source-aware ID, name, primary `SourceReference` (including page), explicit reprint references, and secondary source occurrences where present. `DiseaseDefinition` alone includes the optional source classification.
- Import records keep entry text/markup, source availability flags, image availability, and provenance out of the public definition.
- Public API: separate `ConditionDto`/`ConditionSummaryDto` and `DiseaseDto`/`DiseaseSummaryDto`. Detail DTOs expose ID, name, source/page, explicit reprint targets, and condition secondary sources; disease detail exposes optional source classification. Summaries expose stable ID, name, and source.

IDs use `condition:<source-slug>:<name-slug>` and `disease:<source-slug>:<name-slug>`. Same-name PHB/XPHB definitions remain independent. Reprint targets preserve target name and source; they do not merge definitions.

## Mechanical boundary

The source has no dedicated structured condition mechanic fields. Exhaustion's legacy six-level table and newer cumulative level rules are entry content; do not infer a generalized level/effect type. Disease progression and mechanical details are prose-only. Disease `type` is carried as an optional source classification, not treated as a canonical taxonomy. Inline condition tags are not parsed into public relationships.

No active condition, active disease, creature-specific configuration, or effects engine is represented. Those would be separate future domain models. Public DTOs contain no raw entries, prose, markup, availability flags, image flags, or import diagnostics.

