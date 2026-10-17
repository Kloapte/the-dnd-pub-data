# Shared model candidates

These are design candidates only. No Java types, normalized fields or endpoints are introduced. Reuse follows shared source semantics, not similar names. Existing narrow model contracts should remain in place unless an independently scoped migration establishes a benefit.

## Candidate decisions

| Candidate | Recommendation | Why |
|---|---|---|
| Evidence location and syntax span | Shared foundation | Every catalog needs the same source/snapshot/offset distinction; no effect semantics implied |
| Lossless formula/tag observations | Small shared syntax vocabulary with catalog adapters | Same markup appears across Spells, Monsters, Items, Objects and feature content; roles differ |
| Attack/save/trigger label observations | Shared syntax parser, distinct narrow observation kinds | Explicit labels can be retained without assembling an effect tree |
| Resource usage | Preserve existing catalog models; share only proven matching leaves | Amount, frequency bucket, pool and reset are different source structures |
| Proficiency/defense choice leaves | Reuse existing matching values case by case | Batch 3A already reuses matching Race/Feat values; conditional Monster defense records differ |
| Choice context | Shared evidence boundary, not a universal grant expression | Counts, option identity and selecting semantics are source-specific |
| Scaling | Evidence envelope plus domain-specific schedules | Spell slot spans, Class row levels and Feat literal wildcard progression are not interchangeable |
| Duration/targeting/effects | Keep catalog-specific structures; defer generic effect types | Only some catalogs author complete structured fields; generic joining would invent meaning |
| Universal Effect / Prerequisite AST | Do not introduce | Corpus does not establish one complete formal semantic grammar |

## 1. Evidence location / syntax span

**Consumers:** all 36 domains where evidence exists, including dedicated table results and support content. Existing identities are supplied by catalog owners; no new table-row/heading IDs.

**Source justification:** Spell entries/higher-level entries, Monster action names and entries, Class options, Object attack/hit arrays, Item recharge strings and Treasure amount fields all require an exact owner and field locator. Their mechanical meanings differ but source-location semantics match.

**Preserve:** existing effective owner ID when available, authored file/root/index locator, source/page, file/node/token hash, optional string offsets, and separate authored origin versus effective copy/version owner. Record container context (section, table row/column, option, action kind) without interpreting it. A locator is snapshot evidence, not a stable persistence ID.

**Exclude:** source snippets in public JSON, reference resolution, selected options, inferred subjects and a source-sync engine. No prose inference is required. False meaning risk is low only if unknown context stays absent; an omitted target must not default to self.

**Safer scope:** one internal evidence carrier used by annotation indexes, without embedding the full source DTE into each observation. The missing evidence is a reviewed origin-mapping contract for modified/generated effective content; raw locators alone do not solve it.

## 2. Formula observations

**Consumers:** initial Spells then Monsters; later bounded Item/Object/feature/Treasure adapters. Recipes stay outside gameplay use despite shared parser syntax.

**Source justification:** common `damage`, `dice`, `scaledamage`, `scaledice` tags, with actual arity/nesting variations in the existing [TAG-FREQUENCIES.md](../TAG-FREQUENCIES.md). Items also have already-normalized signed-integer bonuses, which are a separate grammar and need no reprocessing.

**Shared semantics:** exact authored tag name, ordered arguments and source span. Formula syntax may include display/extra arguments and symbolic content. Preserve unknown expressions rather than evaluating them.

**Differences:** damage formatting versus generic dice; higher-slot scaling versus level schedules; quantity versus healing; dedicated recharge role versus an incidental example. A common parser cannot assign those roles without source evidence.

**Preserve:** tag spelling/category, exact token/hash, every argument, nesting, owner/container and independent parse/semantic/publication dispositions. **Exclude:** damage type association, receiver, timing, application count, computed dice totals, uniform formula grammar, and source-qualified entity IDs.

No prose interpretation is needed for syntax observations. False meaning arises if the type is named DamageEffect when only `@damage` has been seen. A narrow FormulaObservation is safer than a shared Damage model for the first batch. Before production parsing, verify pinned renderer semantics, escaping, nesting and all accepted/unknown argument variants; the analysis scanner is not automatically production-ready.

## 3. Check/attack/outcome/usage labels

**Consumers:** Monsters, Objects, later Class features and Spells where explicit tags occur. **Evidence:** `actSave`, `actSaveFail`, `actSaveSuccess`, `actSaveSuccessOrFail`, `actSaveFailBy`, `atk`, `atkr`, `hit`, `dc`, `recharge`, `actTrigger`, `actResponse`.

**Shared semantics:** a source-authored label/code/threshold token at an exact span. **Differences:** a DC can describe a check, a save or an example; recharge can occur in the feature name and have zero/one/two arguments. Character-relative spell attack/save markers are symbolic labels, not missing numbers to calculate.

**Preserve:** label category, exact arguments, action/name/entry ownership, source order and unknown code status. **Exclude:** nearest-label pairing, default thresholds, automatic half damage, resolved actors, resource resets or event execution.

No prose interpretation for labels; complete save/trigger effects require LOCAL_CONTEXT or PROSE_INFERENCE validation. Separate label observations are safer than a single save-effect record. Missing evidence: an adjudicated scope contract for multi-string/multi-clause outcomes.

## 4. Usage and resource leaves

**Consumers:** Optional Features, Class/Subclass Features, Items, Monster spellcasting and Rewards. Actual types include [OptionalFeatureConsumptionDto](../../../src/main/java/com/thedndpub/data/dto/optionalfeature/OptionalFeatureConsumptionDto.java), [ClassFeatureConsumption](../../../src/main/java/com/thedndpub/data/dto/classes/ClassFeatureConsumption.java), [ItemChargeDto](../../../src/main/java/com/thedndpub/data/dto/item/ItemChargeDto.java) and [MonsterSpellcastingDte](../../../src/main/java/com/thedndpub/data/dte/monster/MonsterSpellcastingDte.java).

**Shared semantics:** only a verified amount/expression leaf and evidence may match. **Differences:** resource name/min/max, daily/rest/charges buckets, slot counts, item charge maximum and recharge amount have different ownership and time semantics.

**Preserve:** absent amount versus zero; original bucket keys; named pool when authored; distinct consumption/reset expressions; choice context. **Exclude:** a generic maximum/reset default, character resources, consumption execution and attack-resource associations inferred from prose.

A universal ResourceUsage record would encourage false equivalence. Prefer adapting one source family at a time and sharing a formula observation only after exact grammar agreement. Missing evidence: a complete field-variant audit for charges/recharge and semantics of each source bucket.

## 5. Choice, prerequisites, defenses and progression

**Consumers:** existing character-option catalogs, not every list in the corpus. Source examples: Feat fixed/choice proficiencies, Race resistance choices, Class options, AdditionalSpells selectors and Treasure choose-from variants.

**Shared semantics:** an authored group/option boundary or identical named/choice leaf can be reused. **Differences:** OR prerequisite groups versus AND fields; available option versus automatic grant; expertise versus proficiency; added sense range versus base range; literal `*` progression versus numeric levels; treasure alternatives versus character benefits.

**Preserve:** exact category keys/filters, count presence, ordering, nesting, source qualification and role. **Exclude:** eligibility evaluation, selected grants, default counts not authored, normalized Class IDs for labels, filter materialization and calculated level outcomes.

Batch 3A's matching [RaceGrantGroupDto](../../../src/main/java/com/thedndpub/data/dto/race/RaceGrantGroupDto.java) and [RaceResistanceDto](../../../src/main/java/com/thedndpub/data/dto/race/RaceResistanceDto.java) reuse is evidence for leaf reuse, not justification to migrate all grant models. No prose inference is needed to preserve source choices. False meaning risk is high when a generic API calls all options grants. A small source-context envelope is safer; no new prerequisite language is needed.

## 6. Domain effect models: later, narrow and optional

Candidate HealingFormula, SaveOutcomeAssociation or ConditionApplication structures must remain proposals until source role, subject/target, trigger, duration, scaling and choice can be established. For now, keep evidence links with unknown fields absent. Existing [SpellDuration](../../../src/main/java/com/thedndpub/data/dto/spell/SpellDuration.java) does not automatically match Trap duration labels or Vehicle movement instructions.

A seven-Object attack/hit pilot is a better first relationship investigation than a cross-catalog effect engine. Future domain models should record reviewed facts and explicitly incomplete associations; they should not execute attacks, saves, grants, damage or timing.
