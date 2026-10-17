# Rewards DTO Design Checkpoint

## Source findings

The pinned source is `src/main/resources/data/rewards.json`, with a single `reward` root collection containing 278 records from 26 source codes. The source registry recognizes all primary and secondary source codes. There are no `_copy` or `_versions` declarations, no explicit null or empty values, and no duplicate `(source, name)` pairs. There are 25 reprint declarations (14 strings and 11 tagged `{tag, uid}` records), one secondary source reference, and 29 `seeAlsoFacility` references.

Root fields are `name`, `source`, `type`, `entries` (all 278); `page` (275); `additionalSpells` (89); `seeAlsoFacility` (29); `reprintedAs` (25); `rarity` (8); and `otherSources` (1). Reward types include Blessing, Boon, Charm, Curse, Draconic Gift, Fragment of Suffering, Inhabitation, Other, and Piety Trait. Exact recursive path/type counts and samples are in `source-audit.json` and will be mirrored as a readable field-coverage report.

`entries` contains source text and typed `entries`, `list`, and `statblockInline` variants. Three inline statblocks have an authored `object` statblock payload with name, source, page, size, object/creature type, HP, walk speed, immunities, condition immunities, and an `isNpc` flag. These are retained in the internal source model and are not treated as Monster definitions. No reward-level cost, charge, recharge, duration, ability-score, proficiency, resistance, or prerequisite fields occur outside entries. Such mechanics will not be derived from prose.

`additionalSpells` occurs 89 times and represents the only structured grant-like information. Its nested `innate` and `known` shapes vary. Frequency buckets (`daily`, `limited`, and `rest`) have dynamic authored keys such as `1`, `1e`, `3`, `3e`, `9`, `cha`, `int`, and `wis`; they map to spell UID arrays. The special `innate._` field is either a string array or an object with frequency buckets and ritual spell IDs. Ability is either a string or `{choose:[...]}`. These are reward-specific structures; similarly named spell structures in Feat and Optional Feature sources are not reused without semantic equivalence.

## Proposed source types

- `RewardsFileDte` owns `List<RewardDte> reward`.
- `RewardDte` models every observed root field, including source/page provenance, exact type/rarity strings, typed entries, `additionalSpells`, facility references, reprint variants, and secondary sources.
- Reward-specific sealed entry DTEs model text, named entries, lists/list items, and inline object statblocks. Unknown entry `type` values fail deserialization.
- Reward-specific spell-grant DTEs represent ability string/choice variants, known spell IDs, innate spell strings or frequency maps, ritual IDs, and `resourceName`. Narrow dynamic maps are used only for frequency/resource keys whose names are authored and varied. Values remain typed spell UID lists.
- Reprint is an explicit string-or-tagged-UID variant. No copy/version expansion is required by corpus evidence.

## Normalized catalog model

`RewardDefinition` carries `RewardId`, name, `SourceReference` (including primary page), type, optional rarity, normalized structured spell-grant groups, facility cross-reference tokens, reprint lineage, secondary-source references, and internal authored entries. Spell grant groups retain known/innate/frequency/ritual distinctions and ability selection. Stable identity is `reward:<exact-source>:<normalized-name-slug>`; the full corpus has no collisions under this scheme.

The catalog loads the resource once, validates all source identities, maps all 278 records, and builds immutable ID and exact-name/source indexes. Results are deterministically sorted by source then name then ID. Query filters are source and reward type. Lookup follows existing source-aware duplicate-name behavior.

## Public projection

Summary fields: stable ID, name, source code/page, type, and rarity when present.

Detail fields add typed spell grant groups and lightweight public Spell references for explicit spell UIDs where the resolver can support them, plus the authored `seeAlsoFacility` tokens as typed unresolved facility references only if the public reference contract permits unresolved links. The implementation will keep unsupported facility targets internal if that contract would imply a resolved definition. Authored entry text, markup, inline statblock payloads, importer provenance, and media are not exposed. Reprint metadata is retained internally and can be shown as lightweight lineage only when it can be represented without leaking source markup or resolver diagnostics.

No public prerequisite, duration, charge, recharge, score change, resistance, or proficiency DTO is planned because those fields are absent as structured root data. Mechanics stated only in entries remain for later Step 7 interpretation and publication review.

## Reference policy

The corpus contains explicit spell UIDs in `additionalSpells` and inline markup, plus supported item/skill markup and one `filter` token among authored entries. These supported references are extracted from exact source field locators and resolved through existing adapters. Spell UID paths retain known/innate/frequency/ritual context and are not flattened into character mechanics. Ability-choice data selects an ability score and is not a spell choice. The 30 `seeAlsoFacility` tokens and inline tags for unsupported target kinds remain preserved source information and do not receive inferred target IDs. Creature names in prose or the three inline statblocks are not promoted to Monster links.

## REST representation

- `GET /api/rewards` returns ordered summaries and supports source/type filters.
- `GET /api/rewards/{id}` returns one safe detail.
- `GET /api/rewards/by-name/{name}?source=` uses exact source-aware lookup.

The collection is only 278 records, so it follows small-catalog conventions without pagination unless existing controller conventions require it. No reward execution or character mutation routes are included.

## Licensing and limitations

Entries may contain protected expressive text. Public DTOs exclude all authored entries and raw markup, even when entries describe mechanical effects. Structured spell UID metadata is kept separate from descriptions and projected only through the existing lightweight reference policy. Inline statblock data is preserved internally but not published as a creature definition. This checkpoint does not authorize public redistribution of source prose or unapproved media.
