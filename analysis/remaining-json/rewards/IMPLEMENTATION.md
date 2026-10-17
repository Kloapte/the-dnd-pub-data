# Rewards Catalog Implementation

## Corpus and identity

`data/rewards.json` has one root collection, `reward`, with 278 definitions from 26 source codes. Source distribution: AU 1, BAM 2, BGDIA 7, BMT 26, CM 3, CRCotN 9, CoS 17, DIP 1, DMG 40, DSotDQ 3, DrDe 2, EFA 16, FTD 8, GGR 10, HFStCM 1, IDRotF 17, MOT 60, MTF 1, PaBTSO 2, RHW 5, ToA 19, ToFW 2, ToR 1, VNotEE 2, VRGR 4, XDMG 19. All 26 identities exist in the Source Registry; no registry changes were needed.

Types are Blessing 22, Boon 26, Charm 138, Curse 4, Draconic Gift 8, Fragment of Suffering 9, Inhabitation 9, Other 2, and Piety Trait 60. Rarity is present on eight definitions only. Identity is `reward:<exact-source>:<normalized-name-slug>`. Full-corpus `(source,name)` and generated ID validation found no collisions. Reprinted definitions in DMG/XDMG remain distinct source identities. No `_copy` or `_versions` declarations occur.

## Typed import and normalized model

`RewardsFileDte` and `RewardDte` strictly represent all root fields. Reward entry content uses the established typed recursive entry hierarchy with an added `statblockInline` variant, whose observed inline object statblock fields have a dedicated typed model. Named entries preserve optional authored IDs. Spell benefits have explicit ability-string/ability-choice variants and innate string-array/frequency variants; dynamic frequency names are typed maps to string spell UID lists. Reward reprints are explicitly string or `{tag,uid}` variants. Unknown properties and entry/spell variants fail loading.

`RewardDefinition` retains identity, name, `SourceReference`, classification, rarity, every structured spell block, facility links, reprint/secondary-source provenance, and all authored entries. Catalog loading validates all definitions and sources at startup. ID and source-aware name indexes and ordered summaries are immutable.

## Structured data and limits

The 89 `additionalSpells` groups include 113 explicit spell UID occurrences, ability selectors, known spells, innate spells, daily/limited/rest buckets, ritual spells, and one resource name. Frequency bucket keys and spell UIDs are preserved. No reward-level prerequisites, level requirements, charges, recharge, duration, ability-score changes, proficiencies, resistance/immunity, or explicit non-spell grant fields occur. Rules that appear only in entries remain descriptive source data; no mechanics are extracted from prose.

`seeAlsoFacility` has 30 authored tokens across 29 definitions. Tokens are retained but no `FACILITY` resolver target exists. The three inline statblocks remain embedded object context and are not Monster definitions. Rewards remain a separate catalog from Feats, Optional Features, Boons, Items, and Objects.

## Public API and publication boundary

Summary and detail responses expose ID, name, source/page, type, and optional rarity. Detail additionally includes typed spell benefit context and lightweight supported cross-catalog references. Usage/frequency groups remain distinct. Entries, raw markup, inline statblocks, importer metadata, resolver diagnostics/candidates, and media are omitted. No narrative or unreviewed authored benefit text is published. Spell names/UIDs are represented through the structured benefit fields and source-authored resolver references only.

Routes:

- `GET /api/rewards?source=&type=`
- `GET /api/rewards/{id}`
- `GET /api/rewards/by-name/{name}?source=`

The catalog has 278 entries and follows existing unpaginated small-catalog endpoint conventions. Missing IDs return 404. No reward granting, use tracking, eligibility, or character effect behavior is included.

## Validation

Focused Rewards tests pass (6 tests); the full Maven suite passes (349 tests, 0 failures/errors). `mvn -DskipTests package` and a packaged-JAR `GET /api/rewards?source=DMG` smoke check also pass (40 DMG records). `git diff --check` passes. Complete field/type evidence is in [FIELD-COVERAGE.md](FIELD-COVERAGE.md); explicit link counts and resolver outcomes are in [REFERENCE-INVENTORY.md](REFERENCE-INVENTORY.md). The source discovery artifact is `source-audit.json`.
