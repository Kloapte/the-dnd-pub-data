# Spell Reference Analysis

## Findings

The Class source graph includes 1,068 direct spell UID occurrences: 446 are explicitly source-qualified and exact-match spell roots; 622 lack an authoritative source. Of the latter, 620 names have multiple candidates and two `branding smite` occurrences have one current candidate. Keep all 622 unqualified. Three additional explicit source-qualified candidates in a choice structure exact-match, but remain choice candidates rather than mandatory grants. The class report separately identifies 186 selector occurrences (102 distinct selector expressions); selectors are not spell references to enumerated definitions.

The Stage D spell corpus has 969 roots and 389 resolved `reprintedAs` relationships. Those relationships link spell definitions within the spell catalog and preserve edition distinctions. Spell roots do not establish the full class eligibility universe.

Other owner catalogs preserve spell grants/schedules in their own structured shapes: Feats (`additionalSpells` on 86 roots), Optional Features (`additionalSpells` on 54 roots and `prerequisite.spell` on 10), Backgrounds (15 additional-spell groups), Races (grant schedules such as known/innate/prepared/expanded), Items/Base Items (`attachedSpells` lists or schedules), and Monsters (spellcasting blocks). These owner-root counts are not individual spell UID counts. Their data must be scanned and reported by form before cross-binding; no total occurrence count is asserted here.

## Required distinction

1. Qualified individual spell UID: exact lookup can resolve.
2. Unqualified individual name: candidate list only; no source inference even for a unique current match.
3. Explicit choice candidates: retain candidates plus the choice relation; do not treat them as unconditional grants.
4. Selector/filter: preserve class, level, school, source, and operator constraints; do not enumerate and freeze the current catalog.
5. Spell-list eligibility: separate from a spell grant and from a creature's known spellcasting text.
6. Markup/display mention: not a grant unless the owning typed structure gives it that meaning.

Source-qualified matching must include source code exactly as authored (using the catalog's documented comparison semantics), preserve the original UID, and use the Spell Catalog's stable ID. Edition and reprint preferences are never inferred. Class cross-reference JSON and Stage A/B/C spell analyses are the reproducible evidence sources.
