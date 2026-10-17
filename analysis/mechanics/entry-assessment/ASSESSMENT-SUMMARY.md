# EntryDte mechanics and model-gap assessment

The next Step 7 work should establish a small shared **internal evidence/syntax foundation**, followed by separate Spell and Monster adapters. Retain catalog-specific mechanical models. Complete effect extraction from arbitrary prose is not justified by the source representation or current validation.

## Scope and evidence

All **175 JSON files**, **36 catalog domains**, **43 definition collections**, and **14,399 authored records** were considered. Existing catalog contracts contain **14,797 effective definitions**; this scanner does not expand them or claim effective entry/tag totals. The 812 supporting units are separately audited, including five Status roots, item rule support, Monster groups/templates and metadata. The 20 Homecraft records remain explicitly excluded.

The source uses **13 shared EntryDte variants**, **seven Class entry variants**, and **two Monster wrapper variants**. Including table cells and dedicated Object/Trap/Vehicle/Recipe content, **48 distinct Java carrier types** occur in **73 binding-family/type/shape combinations**. Wrappers, nested nodes and reused ingredient carriers must not be added as independent definitions.

Independent tag counting reproduces **90,294** definition-root occurrences and **91,994** including support, across **73** tag families. These include formatting, references, examples and transforms, not just mechanics. There are **973** definition-root type=table nodes: 824 shared entry tables, 144 Class entry tables, and five copy-payload tables outside the static binding adapter. Dedicated Encounter/Name/Treasure dialects are inventoried separately.

The matrix covers **31 mechanical concepts / 1,116 intersections**, with exact probe counts and source/model links. Keyword/reference-only leads remain UNCERTAIN. Probe ABSENT is an observed absence, not proof that unrestricted natural language contains no such rule.

## Important findings

1. **Structured mechanics are already extensive.** Statistics, Spell casting/range/components/duration/scaling, character-option grants, resource declarations and equipment properties need no blanket re-extraction. Batches 1–4 resolved progression positions, Item focus/signed bonuses and Feat grant/prerequisite preservation.
2. **Entries sometimes already encode mechanics directly.** Sixteen Class abilityDc and 13 abilityAttackMod nodes are normalized as ClassFeatureAbilityDeclaration. Forty typed Class option nodes preserve groups/counts internally. They are not mere prose and do not require extraction.
3. **The missing shared foundation is evidence plus syntax, not a universal effect engine.** Formula/label observations can preserve exact role-independent syntax. Damage type, receiver, count, branch, duration and trigger often require additional interpretation.
4. **Formula coverage is narrower than record coverage.** The 930 Spell formula tags occur across **422 of 969** authored spells. This is not 422 complete damage/healing models. Healing Word's symbolic modifier and Magic Missile's count/target distribution demonstrate the distinction.
5. **Public Class choices expose a subset.** Internal options are retained, but public mapping selects references and nested choices. Consumers must not assume an exposed option count/list is the entire authored universe. This needs a bounded compatibility audit, not publication of withheld text.
6. **Legendary-group bodies lack a discovered runtime model/loader.** The 188 support roots contain 115 lairActions, 158 regionalEffects and three mythicEncounter fields, with copies/versions. MonsterCatalog loads bestiaries; Monster identity retains group name/source only. Earlier “implemented support” wording overstates body coverage. This is a new support-model discrepancy; primary Monster counts stay unchanged.
7. **Stronger boundaries support smaller pilots.** Seven Object attack/hit containers, 14 Trap stage-group nodes on four roots, three Reward inline Object blocks and 160 roll cells on nine owners preserve useful relationships directly. They still do not encode complete effects.
8. **Publication remains independent.** Existing Monster content, Item charge markup and Optional Feature qualifiers require their previously identified rights/compatibility review. No new public field is approved by this assessment.

## Model coverage and proposed order

Existing narrow stat/metadata/grant contracts are COMPLETE where documented. Full damage/healing/save/condition/targeting/trigger relations are PARTIAL or MISSING; prose-dependent rule interpretation REQUIRES_REVIEW. Culinary and navigation content is NOT_APPLICABLE to gameplay effects.

Recommended order:

1. Shared internal evidence/span and bounded lossless syntax primitives; assess existing publication/completeness concerns alongside this work.
2. Spell formula adapter, using the shared primitives and all 930 occurrences.
3. Legendary-group support preservation design, with strict copy/version/source audit.
4. Monster adapter with separate raw/effective origins and counts; keep it separate from Spell semantics.
5. Class ability/choice completeness audit and seven-Object LOCAL_CONTEXT gold-set pilot.
6. Separately scoped Item charge, Treasure quantity and character-feature adapters; bounded Vehicle projection consistency review.

Defer arbitrary save-outcome assembly, condition effects, Multiattack graphs, rest recovery prose, summoning/transformation execution, Facility/upgrade benefits and Variant Rule engines until a separate semantic validation strategy exists.

## Relationship to prior investigations

The [Step 3 Spell investigation](../../spells/STAGE-D-MECHANICS-INVESTIGATION.md) and [extraction design](../../spells/MECHANICS-EXTRACTION-DESIGN.md) already separate annotations from effects. That distinction remains. The full Step 7 investigation corrected the earlier five-root scaling counter to **41** scalingLevelDice roots (36 object/five array), and confirmed **78** explicit non-point numeric range geometries already modeled. Those are established corrections, not new extraction opportunities here.

This assessment adds a typed-entry inventory, the **422-owner** denominator for the same 930 Spell formula tags, explicit Class ability/choice coverage, and the legendary-group runtime body discrepancy. It broadens the common evidence/syntax proposal across catalogs while retaining domain-specific semantics. It makes no new semantic accuracy claim from the earlier lexical counters.

## Deliverables and reproducibility

- [Entry structures](ENTRY-STRUCTURE-INVENTORY.md)
- [Mechanics taxonomy](MECHANICS-TAXONOMY.md)
- [Cross-catalog matrix](CROSS-CATALOG-MATRIX.md)
- [Model gaps](MODEL-GAP-ANALYSIS.md)
- [Reliability](EXTRACTION-RELIABILITY.md)
- [Relationship gaps](RELATIONSHIP-GAPS.md)
- [Shared candidates](SHARED-MODEL-CANDIDATES.md)
- [Publication](PUBLICATION-BOUNDARIES.md)
- [Roadmap](IMPLEMENTATION-ROADMAP.md)
- [Open questions](OPEN-QUESTIONS.md)

Scripts: `scan-entries.mjs`, `mechanic-concepts.mjs`, `model-review.mjs`, `build-report.mjs`, `validate-assessment.mjs`. Evidence: `entry-evidence.json`, `java-model-index.json`, `model-review.json`, `protected-files.json`, `prior-documentation.json`, `verification.json`.

```text
node analysis/mechanics/entry-assessment/scan-entries.mjs
node analysis/mechanics/entry-assessment/build-report.mjs
node analysis/mechanics/entry-assessment/validate-assessment.mjs
git diff --check
```

Capture flags create immutable initial manifests and refuse overwriting them. Normal reruns verify the current source/production/test and prior-documentation snapshots. Generated evidence is deterministic and contains paths/hashes/short metadata rather than copied paragraphs. Scanner limitations and unbound modification payloads are explicit.

## Validation boundary

Validation results are recorded in `verification.json`: protected-file equality, all 36/43/175 denominators, full tag-count parity, source-locator/token-hash checks, model/source links, deterministic outputs and whitespace. No production Java, DTOs, endpoints, source JSON, tests or resolver code is changed. Prior analysis evidence and batch reports remain unchanged.

Maven is not rerun for this analysis-only task. Existing Surefire reports retain the **443 passing tests** baseline. Reference resolution is not rerun or modified; the existing **6,246 occurrences** and outcomes remain the contract, supported by unchanged implementation/test/source hashes rather than claimed fresh runtime measurements.
