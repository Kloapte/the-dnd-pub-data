# Identity Analysis

## Catalog-owned identity rules

There is no safe universal `(name, source) → ID` formula. The proposed resolver must use catalog adapters that expose exact source-aware and candidate lookups and return the catalog's own ID.

| Domain | Identity evidence / required context |
|---|---|
| Spell, feat, action, skill, language, sense, condition, disease, background, optional feature | Generally readable type/source/name slug; PHB and XPHB remain distinct. Preserve existing catalog ID exactly. |
| Item and Base Item | Name/source plus definition kind; group members are not Item IDs. Base-item links are explicitly qualified UIDs. |
| Race/subrace | Race uses source/name; subrace identity includes parent context. Do not merge lineage/edition variants. |
| Class/subclass/features | Class uses source/name; subclass includes parent class identity; feature identity includes owner context and level/ordering where required. Existing graph IDs are authoritative. |
| Monster | Current `MonsterMapper` IDs use `5etools:` plus a Base64 URL-safe canonical key. This is inconsistent with readable IDs but must be returned unchanged by an adapter. Do not regenerate or “correct” it in this discovery stage. |
| Magic Variant | Identity denotes a rule/transformation, not a generated concrete item. Applicability matching does not establish an Item identity. |

Source normalization is catalog-specific. Reports show case-insensitive name/source indexes in several catalogs, source slugs in IDs, aliases only where the Source Registry explicitly records them, and multiple identity dimensions for copy-expanded/parented records. A generic lowercased key can support adapter lookup but must not replace original source values or stable IDs.

## Actual collision and ambiguity findings

- Spell catalog: zero duplicate `(name, source)` identities; 386 repeated-name groups across sources and 359 PHB/XPHB pairs. These are cross-source name ambiguities, not ID collisions.
- Class graph: zero proposed ID collisions after parent class/subclass/level context; all 2,339 authored internal feature refs resolve uniquely.
- Item and Base Item: 2,742 definitions without cross-kind `(name, source)` collision in the analyzed catalog. Item groups and generated variant candidates are not definitions.
- Race/subrace: parent context is required to avoid subrace collisions; the existing race analysis resolves its own IDs.
- No evidence supports applying an owner’s source to a bare spell/skill/tool name.
- The five Magic Variant anchors to absent XDMG items are unresolved targets, not identity collisions.

For other catalog-specific collision totals, use each domain's discovery report; do not infer a cross-catalog collision from matching names because categories are distinct. A future adapter conformance test must assert that each reported stable ID round-trips to one definition.
