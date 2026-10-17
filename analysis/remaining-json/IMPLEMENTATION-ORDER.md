# Recommended Implementation Order

Each stage below begins with its own full source audit and design checkpoint. The order minimizes schema rework by starting with bounded metadata and narrow value domains, then progressing to variant-heavy gameplay records. Do not interpret these steps as authorization to implement now.

## 0. Shared per-catalog contract

For every catalog: (1) complete source file and root-field inventory, including nested JSON types and null/empty variants; (2) typed Jackson DTEs for all root/support structures with strict unknown-field behavior; (3) full-corpus deserialization and source/UID validation; (4) evidence-based copy/version handling or an explicit no-copy finding; (5) immutable normalized definitions and stable readable source-aware IDs; (6) deterministic ordering and duplicate detection with required parent/category context; (7) summary/detail DTO design with prose/media excluded by default; (8) immutable classpath-loaded catalog and source-aware lookup/filter services; (9) thin REST routes only for independently useful concepts; (10) typed reference extraction and Step 5 resolver reuse for exact supported target kinds; (11) full-corpus, serialization, deterministic, and packaged-JAR tests; (12) documentation, field coverage, limitations, licensing notes, and Maven verification.

Exceptions must be explicit: Adventure Index, Name Tables, and Treasure Tables may be better served as specialized indexes/table APIs than conventional definition catalogs; Recipes are implemented as a metadata-only catalog after source-purpose and publication review; Homecraft is OUT OF SCOPE by product decision; Bastion Facilities are not a full Bastion rules model; Variant Rules may be metadata/reference-only if safe prose is unavailable.

## 1. Adventure publication index and name tables — low complexity, separate decisions

Audit `adventure` contents and source identity; model only publication/index metadata. Separately inspect `name` tables and dice/option schemas. Do not merge the two just because both are lookup-oriented. Decide whether names are suitable for an API before modeling all strings as character-name definitions.

## 2. Character creation options — medium

Use existing Class, Race, Background, Feat, Optional Feature, Skill, Language, and Spell IDs only for explicit structured references. Preserve filters and choices. This catalog offers high product value but has meaningful dependency coverage, so complete typed extraction before resolving links.

## 3. Cults and boons — medium

Implement as two independently typed root kinds. Cults may reuse narrow monster-statblock entry values after shape comparison; never reuse a full Monster model by resemblance. Boons should preserve grants/prerequisites without applying them.

## 4. Objects — medium

Establish identity and a precise boundary from Items before DTE mapping. Reuse only exact geometry/stat values. Resolve condition/action/monster references only when explicit and typed.

## 5. Bastion facilities — medium

Implement facility definitions with level, space, cost, hireling and prerequisite structures. Treat source file scope as facilities only; keep turn procedures/economy out until a source family supplies them.

## 6. Encounters — medium

Model encounter records and result tables; separate authored table content from runtime encounter generation. Use resolver integration for explicitly authored creature/item UIDs only.

## 7. Rewards — high

Define reward categories and typed grants before implementation. This is a strong candidate for cross-catalog references and choices, so coordinate exact resolver adapters after root normalization is stable.

## 8. Traps and hazards — high

Share safe entry primitives only after confirming semantics; maintain separate `TrapDefinition` and `HazardDefinition` identities. Preserve source-authored statistics and text, but do not add effect execution.

## 9. Vehicles and upgrades — high

Model vehicles and upgrades separately, then introduce explicit typed compatibility references. Avoid merging vehicle base stats into Items or Objects. No automatic upgrade application.

## 10. Variant rules — high

Implement a reference catalog preserving rule type, source and entry structure internally. Public fields should favor safe metadata and exact typed references; do not implement a configurable rules engine.

## 11. Deities — high

First resolve the five observed PHB `(name,source)` collisions with pantheon as required identity context. Analyze six `_copy` declarations and reprints. Keep descriptive lore and symbols/media out of public responses pending rights review.

## 12. Treasure tables — high

Treat the seven loot collections as distinct typed table families. Preserve dice expressions and outputs but do not generate loot or turn table choices into item grants. Integrate existing Item/Magic Variant identities only where exact UIDs exist.

## 13. Recipes — implemented as a metadata-only catalog; Homecraft excluded

`recipes.json` contains 316 food and cocktail recipes rather than gameplay crafting rules. A narrow read-only metadata catalog is implemented; recipe instructions, ingredients, yields, and related expressive content remain internal pending publication authorization. `homecrafts.json` contains 20 crochet patterns and is OUT OF SCOPE by product decision. See `recipes/DOMAIN-DESIGN.md` and `recipes/IMPLEMENTATION.md`.

## Per-stage exit criteria

No stage is complete on record counts alone. Require exact source coverage, field accounting, strict DTE loading, immutable IDs/indexes, supported transformation/reference validation, API safety review, tests for representative and exceptional records, packaged-JAR loading, and documented unresolved source semantics. Keep each stage additive and avoid migrations or sync behavior until the persistence roadmap steps.
