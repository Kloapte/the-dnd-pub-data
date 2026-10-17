# Revised Step 7 implementation roadmap

This is a plan, not authorization to implement another batch. Batches 1, 2, 3A, 3B and 4 are complete. Their original reports/evidence and the existing implementation-order document remain unchanged. This assessment supersedes the remaining annotation proposals for planning purposes.

## Ranked work

| Priority | Candidate / scope | Source coverage | Reliability and reuse | Complexity / regression / publication | API value and dependency |
|---|---|---|---|---|---|
| P0 review | Existing publication/choice completeness assessment | Monster rich-content contract; 220 Item roots; seven Optional Feature roots/14 strings; 40 Class option nodes | Direct mapper trace; no semantic extraction | Medium review; high compatibility/rights uncertainty | Prevent misleading/inadvertent expansion; can run alongside internal work |
| 1 | Shared evidence/span and lossless syntax foundation | 90,294 definition-root tags, 91,994 including support, 73 tag names | SYNTAX; broad reuse, no effect claims | Medium; low public regression if strictly internal; source retention rights still apply | Foundation for searchable/auditable observations; no new API |
| 2 | Spell formula adapter | 930 formula tags on 422/969 authored spell owners | SYNTAX, high testability; existing structured fields unchanged | Low/medium adapter scope; no public addition | Useful internal index; depends on foundation/pinned grammar |
| 3 | Legendary-group preservation design | 188 support roots; 115 lair/158 regional/three mythic body fields; 16 copies/21 versions | DIRECT containers; source model missing | Medium/high because distinct copy/version source semantics; rights unreviewed | Internal completeness before lair annotations; no new Monster/resolver identity |
| 4 | Monster syntax adapter | 4,560 raw roots / 4,851 effective catalog definitions; counts must be independently reconciled | SYNTAX with action-kind/name/spellcasting context | Medium/high effective-origin risk; public MonsterDto makes accidental exposure especially risky | Internal action audit; depends on foundation, not a combined Spell/Monster effect engine |
| 5 | Class ability/choice completeness review | 29 typed declarations, 40 option nodes | DIRECT existing model audit; no new extraction | Low/medium; public changes need separate approval | Confirm withheld mechanics/completeness representation; do not publish option prose |
| 6 | Object attack/hit relationship pilot | Seven explicit nodes on seven roots | DIRECT sections; LOCAL_CONTEXT effect relations require gold set | Small review set; high semantic restraint | Best first bounded association investigation after syntax foundation |
| 7 | Item charge/quantity syntax and publication design | 220 tagged Item roots; 62 Treasure coin fields on 20 definitions | SYNTAX plus DIRECT field role | Medium grammar/compatibility review; no common execution model | Potential later typed expression metadata; separate families/batches |
| 8 | Class then Subclass Feature syntax adapters | 677 / 1,499 authored feature owners; copy-effective totals need measurement | SYNTAX with level/options/consumption context | Medium; requires origin and option-scope preservation | Internal research/search; do not duplicate already structured declarations/grants |
| 9 | Vehicle summary/detail consistency | Eight raw roots/12 top-level AC/HP fields | DIRECT; catalog-specific, little shared-model need | Low/medium; additive public review required | Bounded metadata consistency; independent of effect interpretation |
| Defer | Healing/save/condition/trigger associations outside bounded pilots; Multiattack graphs; facility/upgrade/Variant Rule engines | Representative cases; no full semantic denominator | PROSE_INFERENCE or UNSUPPORTED | High semantic/rights uncertainty | No automatic authoritative production effects without a separate strategy |

The generic syntax totals are an audit universe, not a requirement to implement all 73 tag families at once. The first production parser should declare a bounded vocabulary and explicitly retain/report unsupported families.

## Recommended first implementation: internal syntax foundation plus bounded validation

**Exact scope:** evidence locations, lossless balanced tag spans/arguments and a small formula/label observation vocabulary. Use existing shared entry and Class/Monster/dedicated-content adapters only to retain context. Preserve all source and stable identity contracts. The first batch should define the library/index contract and validate it on full authored inputs; defer source-effective annotation materialization to separately measured adapters.

**Potential structures:** small internal evidence/span and formula/label observations, not effects. The design names in SHARED-MODEL-CANDIDATES are illustrative. Unknown tag grammar must be retained or explicitly rejected within the chosen parser contract.

**Public changes:** none. **Cross-catalog implications:** no resolver calls, target kinds, source inference or new reference occurrences. Existing occurrence IDs can be context only if already authoritative.

**Tests in that future authorized implementation:** nested tags, pipe arguments, empty/extra arguments, Unicode/UTF-16 offsets, malformed/unbalanced syntax, formatting/reference negative controls, signed integer versus dice differences, source locators, immutable collections and deterministic output. Corpus reconciliation must separate authored, transform, support and effective counts.

**Acceptance:** every tag in the declared vocabulary is accounted for; all original strings remain round-trippable through source span/hash; no subject/target/branch/default arithmetic inferred; no public serialization or 6,246-reference fingerprint change. A parser success rate is not semantic accuracy.

**Exclusions:** public formula endpoints, automatic condition applications, expression evaluation, Item charges as an applied resource engine, Step 8, persistence, synchronization, frontend, Homecraft and Recipe gameplay.

## Redesigned Spell and Monster batches

The earlier proposed SpellFormulaAnnotation and MonsterActionAnnotation batches should **share evidence/syntax primitives but remain separate domain adapters**. Combining the whole domains would mix source-effective origin tracking, public boundaries and effect semantics without a benefit. Two unrelated parser implementations would duplicate grammar and diverge on nesting/offsets.

### Spell adapter

Preserve entry/higher-level/table context and all 930 formula observations across 422 owners. Keep existing 969 SpellDefinition metadata/classifications/scaling unchanged. Test PHB/XPHB contrasts, Healing Word, False Life, Fireball, Magic Missile, Scorching Ray and Booming Blade. Assert no healing modifier, count, target, secondary effect, damage type or slot arithmetic is invented. No new public DTO/reference fields.

### Monster adapter

Preserve action category, feature name (including recharge), entry hierarchy, spellcasting context and copy/version origin. Count all supported syntax on raw and effective owners separately. Keep statistics, conditional defenses, spell usage and identity rules intact. Test zero/one/two-argument recharge, qualified save/result tags, symbolic companion-relative values, legendary/mythic/reaction/bonus contexts and modified/expanded text. Do not pair nearest labels, build Multiattack graphs or automatically annotate unimported legendary-group bodies as if already loaded.

### Legendary-group preservation investigation

Determine intended current support contract, strict full schema, source codes, group identity, internal copies/version semantics, template applicability and links before introducing a typed body model. Keep this within Monster support; do not count group roots as additional Monster definitions or expand the shared resolver. Acceptance must distinguish raw body preservation from semantic extraction and protect existing IDs/public output.

## LOCAL_CONTEXT pilot requirements

The seven-Object pilot should first publish an internal review set of attack/hit container boundaries and abstentions. Only a separately approved implementation may attach narrow effect relations. Require gold labels, negative cases, independent review, precision/recall/abstention reporting and publication decisions; do not set arbitrary accuracy percentages now.

## Work intentionally deferred

No automatic complete effects for Conditions/Diseases, arbitrary character-option benefits, ongoing effects, rest reset prose, summoning/transformation rules, environmental simulation, Facility production, Vehicle upgrades, Variant Rule activation or campaign conflicts. These require semantic interpretation and potentially a future rules engine; no such engine is needed for lossless internal observations.
