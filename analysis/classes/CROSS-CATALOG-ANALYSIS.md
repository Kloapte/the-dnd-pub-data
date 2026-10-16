# Cross-catalog reference analysis

Status: exact source reference discovery and Stage B typed bindings/deferred references. Existing item/optional-feature/feat/monster IDs are reused only for reliable identities; groups, spells and unqualified skills remain explicitly typed. No fuzzy matching, character selection, or prose extraction is included. See [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md).

The authored counts below retain Stage A's counting scope. Stage B additionally preserves three explicit spell candidates inside `choose.from`, giving 1,071 authored spell UIDs when those candidates are included. Copy inheritance adds reference occurrences without adding definitions.

## Classification

1. **Explicit source-qualified structured references:** pipe UIDs or named/source objects with an explicit source. Resolve by the target kind's exact identity.
2. **Explicit unqualified structured references:** skill tokens, spell names, tool labels, category selectors. Retain unqualified identities and source-authored counts/configuration; do not stamp owner source onto them.
3. **Markup-only references:** tags embedded in descriptive entries, display tables, or equipment prose. Retain internal links without interpreting them as grants, prerequisites, or replacement effects.
4. **Descriptive mentions:** text with no source reference syntax. Preserve as internal content; no entity relationship is inferred or counted from English names.

The categories are about representation and reliability. A markup tag may have explicit source slots and still remain markup-only. Source code validation does not imply that its target exists in a public catalog.

## Structured outcomes

| Target | Occurrences | Result |
| --- | ---: | --- |
| Class/subclass features | 2,339 | All exact-resolved; own feature graph |
| Optional Features | 162 | All exact-resolved, 161 reference nodes plus one required progression UID |
| Feats | 2 | Exact-resolved `refFeat` nodes, both explicit XPHB |
| Items/Base Items | 169 | 166 exact-resolved, three auxiliary group references lacking concrete catalog targets |
| Skills | 256 | Unqualified `choose.from` tokens; no edition/source invented |
| Spells | 1,068 | 446 explicit-source, 622 unqualified; all deferred without a Spell Catalog |
| Monsters | 1 | Exact-resolved AU Vestige Companion statblock link |

The 169 item references include 168 equipment UIDs and one feature statblock for XPHB Psychic Blade. The three non-concrete targets are two occurrences of `holy symbol|xphb` and one of `druidic focus|xphb`. Each resolves uniquely in source `itemGroup`, which is not an independently addressable Item Catalog definition. They are choices, not three missing source records. Preserve source group identities and report catalog binding as deferred.

Optional reference UIDs use explicit sources or the PHB default supported by existing Optional Features conventions and exact full-corpus target matches. Do not apply that default to bare skills, spells, or tools. `featureType` codes select Optional Feature categories; feat `category` codes select Feat categories. Neither is a direct list of grants.

Spell filter selectors add 186 occurrences (102 distinct expressions) but do not enumerate definitions. Class/school/level/source clauses remain typed selector constraints. Spell references retain qualifiers such as `#c`; no public target IDs are fabricated. `known`, `prepared`, `expanded`, and `innate` grants remain different.

## Other existing catalogs

- Skills: retain exact source skill tokens. Both PHB and XPHB definitions exist, so globally unique names are insufficient to select an edition.
- Languages: 11 markup links, no structured language grants in class/subclass roots.
- Backgrounds: `additionalFromBackground` is a procedure flag, not an explicit Background identity. It must not create a fake Background reference.
- Races: no structured race UID relationships were observed in the class corpus. Prose mentions do not become relationships.
- Actions: 212 markup links, no structured reusable action-grant root field.
- Senses: 28 markup links; structured vision/sense grants are not inferred.
- Conditions: 408 markup links; condition mentions do not automatically define effects or immunity.
- Feats: 70 additional markup links, independent from the two structured references and category-based progressions.
- Items: 487 markup links, independent from structured item options. Tool proficiency display strings may use these links without identifying the unqualified typed grant's source.
- Spells: 1,233 markup links, independent from the 1,068 structured grants.

There are also explicit links to creatures, rules, tables, deities, decks, hazards, objects, item mastery, item properties, and source books. Their complete tags, UIDs, and occurrence pointers are in [cross-catalog-references.json](cross-catalog-references.json); aggregate counts are in [DISCOVERY-TABLES.md](DISCOVERY-TABLES.md). Filters referencing `psionics` and other absent catalogs remain selectors, not unresolved manufactured definitions.

## Source Registry and public boundaries

All primary, secondary, parent, copy, reprint, named statblock, explicit UID, and recognized source-filter codes are validated against the shared registry. Empty UID source slots are grammatical defaults, not invalid source codes. Display text, subclass names, deity pantheons, category codes, and level tokens must not be misclassified as sources.

Internal references retain original UID, exact identity components, occurrence locator, and resolution status. Public references expose a safe name/source identity and an optional verified target ID; unresolved catalog binding is not replaced with a guessed ID. Import diagnostics and raw markup remain internal. Resolving descriptive links does not authorize publishing their surrounding rules text.
