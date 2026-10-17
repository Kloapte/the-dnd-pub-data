# Variant Rules DTO Design Checkpoint

## Source-backed design

`variantrules.json` contains one root array, `variantrule`, with 231 records. The source fingerprint is `fbbaed4bb8002076fac0e38a59ed65bac34e261ac753602f3a02b0bdfb1a6485`. The full corpus has 119 distinct nested field paths and no explicit null or empty values. See [`source-audit.json`](source-audit.json) and [`FIELD-COVERAGE.md`](FIELD-COVERAGE.md).

Every record has `name`, `source`, `page`, and `entries`. Optional root metadata is `ruleType` (191 records), root entry `type` (45), `srd52` (115), `basicRules2024` (115), `srd` (1), `reprintedAs` (13), and `additionalSources` (3). No root parent/category/subcategory, rule prerequisites, modifier, choice/filter, or numeric mechanic fields occur.

## Identity

Use `variant-rule:<exact-source>:<normalized-name-slug>`. No duplicate `(source,name)` pair or slug collision exists across all 231 records. Names can repeat across source books, so source qualification is required. Preserve primary source spelling exactly. Reprints remain provenance links and do not collapse distinct edition definitions.

## Normalized model

- `VariantRuleDefinition`: stable `VariantRuleId`, name, `SourceReference` (source and page), optional authored `ruleType`, optional root entry type, source flags, typed reprint lineage, typed additional source citations, recursive authored `EntryDte` content, and derived section/table counts for safe metadata.
- `VariantRuleReprintDte`: explicit string UID and tagged UID forms. Preserve target tags and UIDs, validate qualified source codes, and do not treat reprints as resolver references.
- `VariantRuleAdditionalSourceDte`: source and page citation, validated against the shared Source Registry.
- `VariantRuleSummaryDto` and `VariantRuleDetailDto`: separate immutable public projections. Detail includes safe section/table counts and lightweight supported cross-catalog references; it never includes authored entry text or table rows.
- `VariantRuleCatalog` and service/controller: parse the classpath resource once, validate all 231 primary/provenance identities, enforce unique IDs, source-aware exact-name lookup, deterministic ordering, immutable indexes, and optional name/source/ruleType filters. Derived section counts count nested `section` entry nodes; the root entry type remains a separate field.

The `ruleType` codes `C`, `O`, `V`, and `VO` are retained as authored categorical values. Forty records have no `ruleType`; they remain unclassified rather than receiving an inferred default. The source has no explicit parent relationship. Nested section names remain entry headings and do not receive separate catalog IDs.

## Entry type extension checkpoint

The 5eTools v2.37.0 data uses typed recursive entry nodes and tables. The current shared `EntryDte` hierarchy handles most of the same structures, but the full Variant Rules corpus also contains `inline`, `link`, and `image` nodes, and 2 tables have `isNameGenerator`. These exact entry constructs will be added as strict typed shared variants/metadata, preserving their fields internally. Unknown entry/node fields continue to fail deserialization. `href` is a typed `{type,path,hash?}` reference, not a generic map.

No general rules AST or executable effect model will be introduced. No source-authored structured prerequisite, choice, modifier, activation requirement, or numeric effect exists at the root. Dice/DC/damage expressions occur only within descriptive entry strings or creative table content; they will not be parsed into mechanics.

## Cross-catalog references and licensing

The entry text contains markup for existing target types. Existing resolver adapters support Spell, Item, Skill, and selection filters; Variant Rule-to-Variant Rule, Action, Condition, Creature, Hazard, Vehicle, and other markup targets do not have supported adapters here. Supported explicit tokens will retain exact locators and semantic roles through the existing startup resolver. Unsupported tags remain typed internal entry text; no target kind or ID will be inferred.

Public DTOs expose identity, name, source/page, authored rule classification and root entry type, and section/table counts. They exclude entries, prose, examples, table cells, markup, image/href data, import flags, and resolver diagnostics. Structured JSON is not treated as permission to republish authored content.

## REST representation

- `GET /api/variant-rules?name=&source=&ruleType=` returns deterministic summary records.
- `GET /api/variant-rules/{id}` returns safe detail metadata and approved lightweight references.
- `GET /api/variant-rules/by-name/{name}?source=` returns source-aware exact-name matches.

This is a read-only reference catalog. It does not enable rules, execute mechanics, interpret prose, configure campaigns, or resolve conflicting rules.

## Proceeding decision

The requested implementation authorizes continuing past this checkpoint. Implementation will use the typed model above, reuse the existing immutable resolver only for supported markup targets, retain authored content internally, and preserve the preexisting 4,520 resolver outcomes.
