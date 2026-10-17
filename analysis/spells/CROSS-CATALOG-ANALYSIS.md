# Cross-catalog spell reference feasibility

## Reference classes

Keep four forms separate:

1. **Source-qualified spell UID:** exact `(name, source)` reference. Candidate for exact resolution only.
2. **Unqualified spell name:** preserve the name without assigning a source; cannot always be unique across editions.
3. **Selector/filter:** a query over classes, spell levels, school, or source; not an authored individual spell reference.
4. **Markup or prose:** inline links and descriptive mentions; not a grant or eligibility relationship unless another typed field says so.

## Existing catalog findings

| Owner | Source-derived evidence | Resolution feasibility |
| --- | --- | --- |
| Classes, subclasses, and features | 1,068 direct structured spell UID occurrences: 446 source-qualified and 622 unqualified; three extra explicit choice candidates. 186 selector occurrences, 102 expressions. | Qualified candidates can be exact-checked by source/name; Stage A did not bind them. Unqualified references remain ambiguous where names repeat. Selectors are not enumerated. |
| Feats | 86 roots have structured `additionalSpells`; Magic Initiate has three source-specific version grants. | Grants contain spell names and selection/filter structures; preserve query and variants without assuming a source identity. |
| Optional Features | `additionalSpells` occurs on 54 roots; `prerequisite.spell` occurs on 10. | Exact UIDs may retain explicit source; chooser objects and schedules remain choice/filter structures. No fuzzy binding. |
| Backgrounds | 15 additional-spell grant groups. | Preserve `expanded` source schedules and source-qualified/unqualified references as authored. |
| Races | Structured spell grants in `additionalSpells`, including known, innate, prepared, expanded, ability choice, level gates, and uses. | Keep source grant and schedule; do not turn it into a full class spell list. |
| Items and Base Items | `attachedSpells` supports lists or schedules. | Preserve typed spell references/configuration; no inferred source when absent. |
| Monsters | Creature spellcasting blocks contain names, levels, uses, and inline text. | Creature spell access is not class eligibility. Resolve only explicit UIDs if present; otherwise retain source text/name. |

The class counts come from [class cross-catalog analysis](../classes/CROSS-CATALOG-ANALYSIS.md) and intentionally keep its direct-reference counting scope separate from the three choice candidates. Exact comparison of the 1,068 structured class UIDs against the 969 spell identities finds all **446 non-empty source-qualified occurrences** resolve by exact name/source. Among the 622 source-less or empty-source occurrences, 620 point to names with multiple source candidates and two `branding smite` occurrences currently have one candidate; no source is filled in. The three explicit choice candidates are source-qualified XPHB spells and each exact-matches one root. Configuration suffixes such as `#c` are not part of the identity. Other owners' `additionalSpells` group counts are not individual spell UID occurrence counts. This report does not perform catalog binding or claim aggregate unique spell-reference counts for all domains.

Exact ID matching is feasible only for explicitly qualified values whose syntax identifies a spell. For unqualified name references, the source corpus contains 386 repeated-name groups, including 359 PHB/XPHB pairs; global name lookup must preserve ambiguity. Class/school/level/source selectors can define filters but do not enumerate all eligible definitions. Markup tags and prose mentions stay outside normalized grant relationships.
