# Background DTO Design

The normalized `BackgroundDefinition` keeps source identity, optional edition, source references, structured ability choices, proficiency grant packages, feat references/choices, equipment packages, currency values, spell references, prerequisites, feature metadata, characteristic-table metadata, and copy/reprint provenance separate. Collections are immutable. References retain source-qualified identities and resolution status where relevant.

`BackgroundSummaryDto` exposes stable ID, name, source/page, edition, skill names, feat references, and compact ability metadata. `BackgroundDto` adds all safe structured grants, equipment alternatives, currency, feature metadata, source relationships, and resolved/unresolved reference identities. Grant groups and equipment options retain their original grouping; no choices are evaluated.

Public projections use `@JsonInclude(NON_EMPTY)`. They omit source entries/prose, roleplaying table text, import lineage, source availability flags, and modification instructions. Relevant false and zero source values remain representable. IDs use readable normalized slugs, e.g. `background:xphb:acolyte`; source definitions are never merged.

The implementation additionally exposes structured additional spell UID lists and campaign prerequisites, and it keeps `additionalSources`, `otherSources`, and reprints distinct. Feat choice packages (including category alternatives) are preserved alongside source-aware feat links.
