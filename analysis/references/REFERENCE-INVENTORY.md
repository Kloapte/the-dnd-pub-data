# Reference Inventory

## Counting rules

Counts below retain each source analysis's stated unit: structured occurrences, distinct UIDs, owner records, markup occurrences, or candidate matches. They are not summed across unlike categories. A future scanner should emit both occurrence and distinct-authored-identity totals with a stable source locator.

| Owner catalog | Observed cross-catalog reference categories | Evidence and current counts |
|---|---|---|
| Classes, subclasses, class/subclass features | Features, optional features, feats, skills, items/item groups, spells/selectors, creatures; markup links to actions, conditions, senses, languages, items, spells, etc. | 2,339 authored internal feature edges; 162 Optional Feature refs; 2 Feat refs; 256 unqualified skill tokens; 169 item refs (166 concrete, 3 groups); 1,068 direct spell refs; 186 spell selector occurrences; 1 creature ref. Markup includes 5,684 links across tags. See `analysis/classes/CROSS-CATALOG-ANALYSIS.md`. |
| Backgrounds | Items, feats, skills, tools, languages, spell grants | Equipment: 718 resolved item occurrences, 8 unresolved occurrences across 3 exact UIDs, 0 ambiguous, 204 descriptors. Feat references occur on 84 roots (61 distinct qualified UIDs). Skills on 146 roots, tools on 124, languages on 56, additional spell groups on 15. These latter figures count roots, not individual grants. |
| Races/species | Feature relationships, skills, languages, tools, spells, senses and other trait effects | Structured grants are retained by the race pipeline; each category has source-specific choice structures. This Stage A does not assert a consolidated cross-catalog occurrence total because the existing race report counts typed grant records and alternatives differently. Resolve only explicit identities; choices remain choices. |
| Feats | Feats/optional features, spells, skills, ability choices, conditions and markup links | `additionalSpells` exists on 86 roots; structured grants include choices and schedule forms. Do not count chooser expressions as individual spell definitions. Use `analysis/feats/` field and DTO evidence. |
| Optional Features | Feats, spells, class/subclass prerequisites, proficiency categories | `additionalSpells` occurs on 54 roots; `prerequisite.spell` on 10. Feature category codes are filters, while explicit UIDs are definition references. |
| Items and Base Items | Base items, spells, ammunition, properties, mastery, item groups, variants | 241 explicit item→baseItem references; Item Catalog has 2,512 item + 230 base-item definitions. `itemGroup` has 119 auxiliary collections, not independently addressable items. `attachedSpells` supports direct lists and schedules. |
| Magic Variants | Items/base items, spell grants, properties and applicability selectors | 225 generic rules generate 12,663 potential rule/item matches. Five item-specific transformations have two absent XDMG base-item anchors. These are not concrete generated items. |
| Monsters | Spells, conditions, actions, senses, damage types, items and creature references | Monster statblocks retain spellcasting blocks and structured references, but names in spellcasting prose/blocks are not necessarily source-qualified spell definitions. Resolve only explicit UIDs. Existing monster normalization resolves its internal copied creature relationships locally. |
| Spells | Reprints and spell-list metadata | 389 explicit reprint references resolve within Spell Catalog. Spell roots do not author a complete cross-catalog eligibility graph; do not treat lists in other catalogs as spell definitions. |
| Actions | Conditions, damage types, senses, saves and other named rule concepts | Action-specific effect and reference structures remain source-specific. A condition/damage type name is a typed rules-vocabulary reference only where represented as such, not every occurrence in action prose. |
| Skills, Languages, Senses | Source/reprint relationships and definitions | These are mostly target catalogs, not reference owners. Exact identity is source-aware. References to a skill/language/sense from other owners may be unqualified and edition-ambiguous. |
| Conditions and Diseases | Reprints and related condition/disease references | Same-catalog and cross-kind links must retain target category. A word in descriptive condition text is not a machine reference. |

## Classes machine evidence

`analysis/classes/cross-catalog-references.json` is the full occurrence-level reference graph for class import, including source path/locator, kind, authored UID/selector, and resolution status. The checked aggregation reports 5,684 markup occurrences, 3,997 structured occurrences, 186 selector occurrences, and 1,658 foreign-resolution records. The detailed class report explains that this file intentionally contains repeated categories and alternative discovery paths; do not add its top-level arrays together as unique definitions.

## Reference category distinctions

- **Definition link:** an exact UID/source-bearing node.
- **Choice/filter:** a set or predicate; it is not an occurrence of every possible target.
- **Group/type:** a category such as item group, weapon type, spell list, or proficiency category.
- **Markup link:** a link target embedded in authored entry content; semantic role depends on its owning field.
- **Presentation text:** a displayed name or phrase without source identity.
- **Source metadata:** identifies publication provenance and does not identify a target entity.

The detailed category proposals are in [resolution semantics](RESOLUTION-SEMANTICS.md).
