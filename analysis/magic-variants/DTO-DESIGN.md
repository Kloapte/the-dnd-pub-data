# Magic Variants DTO Design

`MagicVariantSummaryDto` supplies ID, name, source, kind, optional type, and eligible-item count for collection results. `MagicVariantDto` adds structured applicability requirements/exclusions, transformation data, explicit base-item relationship, and reprint relationships.

`MagicVariantRequirementDto` retains each required alternative as an independent record and preserves list-valued name/property alternatives. `MagicVariantTransformationDto` exposes only normalized structured changes, including rarity, attunement, bonuses, charges/recharge, formulas, spell references, grants, and other typed properties. Formula values are not evaluated. Source data's meaningful false booleans remain present. Empty nested structures are omitted.

All public records use `@JsonInclude(NON_EMPTY)`. Raw entries, 5eTools markup, copy instructions, import provenance, diagnostics, availability flags, and unresolved prose are not public. Item-specific transformations use `MagicVariantBaseItemReferenceDto`; unresolved anchors have name and source but no fabricated item ID.

IDs are stable readable source-aware slugs (`magic-variant:{source}:{slug}`). Source references reuse the established source DTO. The source code is preserved; no display names are invented for open codes.
