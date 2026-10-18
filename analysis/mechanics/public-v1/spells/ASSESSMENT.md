# Public Mechanics v1: Spell contract and publication gate

Status: **design and publication review only**. No production code, tests, DTOs, routes, source data,
reference generation, or existing assessment evidence is changed. The reported baseline remains
529 Maven tests and 6,246 resolver occurrences; Maven was not run for this assessment.

## 1. Decision

Retain the current structured Spell API as the public mechanics foundation. It already publishes
every observed source mechanic-classification array and all structured casting, range, component,
duration, and level-scaling representations within its established field boundaries.

Propose one small implementation-ready addition to detail responses: `formulaProjectionStatus`.
It states whether the bounded authored formula index has no observations or has observations
whose content is withheld. This is application-authored projection metadata and reveals no formula
or descriptive content. It is consistent with the existing metadata-only projection approach in
[Batch 9A](../../batch-9a-class-choice-completeness/IMPLEMENTATION.md); that batch's approval of
choice metadata is not treated as approval of Spell expression content.

Design `formulaObservations` as a separate, later, approval-gated addition. No new formula token,
display argument, scaling argument, or derived effect receives publication clearance here.
Technical syntax coverage is sufficient for a useful bounded projection; the field/source rights
decision is still required. Do not substitute the existing SRD flags for that decision.

## 2. Evidence and reproducibility

New artifacts in this directory:

- [SpellMechanicsProbe.java](SpellMechanicsProbe.java) invokes the existing strict loader,
  normalizer, public mapper, and Batch 6 adapter. It introduces no production types or parser.
- [runtime-evidence.json](runtime-evidence.json) contains all 969 owner/DTO fingerprints, the
  930 exact formula observations, 18 auxiliary observations, source hashes, and current public
  response examples. It retains tags and locators rather than full descriptive paragraphs.
- [audit-spells.mjs](audit-spells.mjs) joins parsed source records to runtime results, independently
  reconstructs UTF-16 spans, checks all 175 pinned source hashes, and produces
  [coverage.json](coverage.json) and the proposed [gold-set.json](gold-set.json).
- [verification.json](verification.json) records determinism and protection checks.

From the repository root, with the existing compiled application classes and dependency classpath:

```powershell
$spellAuditClasspath = (Join-Path (Get-Location) 'target/classes') + ';' +
    (Get-Content target/analysis-classpath.txt -Raw).Trim()
javac -proc:none -cp $spellAuditClasspath -d target/scanner-classes `
    analysis/mechanics/public-v1/spells/SpellMechanicsProbe.java
java -cp ('target/scanner-classes;' + $spellAuditClasspath) SpellMechanicsProbe
node analysis/mechanics/public-v1/spells/audit-spells.mjs
```

For a determinism check, run `node analysis/mechanics/public-v1/spells/verify-analysis.mjs --capture`,
repeat the JVM probe and scanner, then run the same verifier without `--capture`.

The classpath file is an existing local build artifact, not a distributable dependency manifest.
An ordinary existing application build can supply an equivalent classpath. The probe verifies that
the classpath source-resource hashes equal the original files before accepting annotations.
Windows permissions on existing build/cache files required running this analysis JVM outside the
restricted Java sandbox. It wrote only new analysis evidence and temporary classes under `target`.

Counts below are exhaustive for the 18 pinned Spell files unless explicitly called a sample,
proposal, or unmeasured semantic question. No keyword scan is used to certify effect roles.

## 3. Existing source-to-public model coverage

Trace entry points:

1. [SpellSourceLoader](../../../../src/main/java/com/thedndpub/data/importer/spell/SpellSourceLoader.java),
   `load`: strict `SpellFileDte`/`SpellDte` deserialization.
2. [SpellDefinitionNormalizer](../../../../src/main/java/com/thedndpub/data/importer/spell/SpellDefinitionNormalizer.java),
   `normalize`, `mechanics`, `scaling`, `components`, and `range`.
3. [SpellDefinition](../../../../src/main/java/com/thedndpub/data/dto/spell/SpellDefinition.java)
   and the narrow immutable records in `dto/spell`.
4. [SpellPublicMapper](../../../../src/main/java/com/thedndpub/data/dto/spell/api/SpellPublicMapper.java),
   `summary`, `detail`, `mechanics`, `material`, and `consumed`.
5. [SpellDto](../../../../src/main/java/com/thedndpub/data/dto/spell/api/SpellDto.java),
   [SpellSummaryDto](../../../../src/main/java/com/thedndpub/data/dto/spell/api/SpellSummaryDto.java),
   [SpellsService](../../../../src/main/java/com/thedndpub/data/services/SpellsService.java), and
   [SpellsController](../../../../src/main/java/com/thedndpub/data/controllers/SpellsController.java).

The probe verifies **34,884 source/DTE/normalized/public array comparisons**: 969 owners times
12 mechanic-classification fields times three transitions. No value/order mismatch was found.
Absent arrays become immutable empty lists internally and are omitted publicly; this does not
invent a false value or a mechanical effect.

| Source field(s) | Typed DTE / normalized representation | Current detail projection | Corpus coverage / boundary |
|---|---|---|---|
| `name`, `source`, `page` | `SpellDte`; `SpellIdentity`, `SpellId`, `SourceReference` | `id`, `name`, `source: {source,page}` | 969; existing IDs/source spelling remain authoritative |
| `level`, `school` | Integer/code; `SpellDefinition.level/school` | `level`, `school: {code,name}` | 969; meaningful level 0 is already public |
| `alias` | `aliases` ordered list | `aliases` | Two owners |
| `time[]` | `SpellCastingTimeDte`; `SpellCastingTime(number,unit,condition,note)` | `castingTimes[].amount/unit/hasCondition/hasNote` | 969; two owners have alternatives; 27 have conditions, one a note. Exact qualifier text intentionally withheld |
| `range` | `SpellRangeDte`; `SpellRange`/`SpellDistance` | `range.type/distance.type/amount` | 969; 78 non-point numeric geometries already public. Range is not a target selector |
| `components.v/s/r` | Typed Booleans; `SpellComponents` | `components.verbal/somatic/royal` | 919/838/3 owners; source absence is not inferred false |
| `components.m` | `MaterialTextDte` or `MaterialDetailsDte`; `SpellMaterialText/Details` | `material.required/cost/consumed` | 523 owners: 342 strings, 181 objects. Cost public on 172; Boolean consumed on 82. Material prose and consume-note text withheld |
| `duration[]` | `SpellDurationDte`; `SpellDuration`/`SpellDurationAmount` | `durations[].type/concentration/duration/ends` | 969; 423 concentration-true owners. Durations retain alternatives and `upTo`, not application to a particular effect |
| `meta.ritual` | `SpellMetaDte`; `SpellDefinition.ritual` | `ritual` | 68 owners; missing is not converted to false |
| `scalingLevelDice` | Typed object/array deserializer; `SpellScaling(label,values)` | `scaling[].label/values[].levelKey/formula` | 41 owners, 177 values; 36 object/5 array source forms. Ordered exact keys/formulas already public |
| `entries`, `entriesHigherLevel` | Typed `EntryDte` hierarchy; retained on `SpellDefinition` | Not projected | 969 / 354 owners; intentionally retained internally |
| `srd`, `srd52`, Basic Rules/fluff flags | `SpellImportMetadata`, `SpellCatalogReference` | Not projected | Curation metadata, not a rights decision or edition identity |
| Additional/other/reference/reprint sources | Typed source records; `SpellSourceRelationships` | `sourceRelationships` | 537 owners have some relationships; 389 reprint edges. Reprints are not inheritance |

All twelve classification arrays are already public:

| Exact source field | `SpellMechanics` accessor | `SpellMechanicsDto` field | Owners |
|---|---|---|---:|
| `abilityCheck` | `abilityChecks` | `abilityChecks` | 57 |
| `affectsCreatureType` | `affectsCreatureTypes` | `affectsCreatureTypes` | 77 |
| `areaTags` | `areaTags` | `areaTags` | 643 |
| `conditionImmune` | `conditionImmunities` | `conditionImmunities` | 12 |
| `conditionInflict` | `conditionsInflicted` | `conditionsInflicted` | 184 |
| `damageImmune` | `damageImmunities` | `damageImmunities` | 17 |
| `damageInflict` | `damageInflicted` | `damageTypes` | 361 |
| `damageResist` | `damageResistances` | `damageResistances` | 30 |
| `damageVulnerable` | `damageVulnerabilities` | `damageVulnerabilities` | 2 |
| `miscTags` | `miscellaneousTags` | `miscellaneousTags` | 721 |
| `savingThrow` | `savingThrows` | `savingThrows` | 406 |
| `spellAttack` | `spellAttacks` | `spellAttacks` | 65 |

The `mechanics` object is nonempty for 910 owners and omitted for 59. These arrays are source
classifications. For example `damageTypes` and `savingThrows` do not pair a type with one formula,
identify a receiver, establish a save DC, or describe failure/success outcomes. Existing tags
such as `HL`, `SCL`, and `SGT` remain source classifications; this proposal does not reinterpret
them into healing, progression, or targeting engines.

Summary responses already include level/school, ritual/concentration, casting-time alternatives,
range, and `damageTypes`. Detail responses also include components, durations, the classification
object, scaling, and source relationships. Neither response currently consumes the formula index.
Routes remain `GET /api/spells`, `GET /api/spells/{id}`, and
`GET /api/spells/by-name/{name}?source=`. By-name returns a list and retains edition/source matches.

There is no separate authored edition field in `SpellDte` or `SpellIdentity`; PHB and XPHB remain
separate source-aware IDs. Do not introduce an inferred edition label or merge their mechanics.
No root target-count, effect-target, numeric save-DC, damage-amount, or healing-amount field exists
in this Spell schema. `affectsCreatureType`, range geometry, and classification arrays do not fill
those gaps.

## 4. Formula reconciliation and reliability

[Batch 6](../../batch-6-spell-formulas/IMPLEMENTATION.md) is reproduced by executing its unchanged
adapter and classifier. All 969 source owners map to the same 969 normalized identities. There are
zero copy/version/template declarations in the Spell corpus. A separately materialized effective
annotation index still does not exist; the results below describe authored annotations, not a new
effective-origin contract.

| Tag / safe description | Observations | Owners | Arguments | Reliability and limitation |
|---|---:|---:|---|---|
| `damage`: explicit damage **notation** | 642 | 362 | One | SYNTAX; 640 bounded dice tokens, two opaque omitted-count tokens. Not a complete DamageEffect |
| `dice`: dice/formula notation with no encoded effect role | 126 | 94 | 116 one-argument, ten two-argument | SYNTAX; role requires context. Ten numeric second arguments occur in the Str/Dex columns of PHB Animate Objects' statistics table, not as owning-Spell effect values |
| `scaledamage`: explicit scaling **syntax** | 148 | 143 | Three | SYNTAX; retain base/range/increment arguments. Seven base tokens contain semicolons |
| `scaledice`: explicit scaling syntax without effect role | 14 | 14 | Three | SYNTAX; includes XPHB Heal's numeric base `70` |
| **Formula total / owner union** | **930** | **422** | | Family owner counts overlap |
| Auxiliary `hit` / `d20` / `dc` | 5 / 2 / 11 | 13 total owners | Separate vocabulary | Modifier/label syntax, not part of the 930 formulas; no formula relationships are created |

All 930 are valid `FormulaSyntaxObservation` objects, with zero malformed spans, nested formula
tags, transform-origin formulas, or formulas outside entry sections. They occur in 12 of the
18 files: 680 in main entries and 250 in higher-level entries. Higher-level family counts are
82 `damage`, six `dice`, 148 `scaledamage`, and 14 `scaledice`.

The whole-ancestor table definition counts **35** formulas: 19 damage tags and 16 dice tags.
The earlier report's 31 is reproduced by restricting to row content; four additional dice tags are
column labels in PHB Confusion, PHB Reincarnate, XGE Chaos Bolt, and XGE Summon Lesser Demons.
This is a scope reconciliation, not four new formulas added to the 930. Original reports and
scanner evidence are unchanged.

The shared lexical classifier recognizes 872 bounded dice tokens, one unsigned integer, and
57 opaque tokens on 39 owners. The latter comprise 44 omitted-count die tokens, six tokens using
Unicode multiplication, and seven semicolon-containing scaling bases. `OPAQUE_OR_SYMBOLIC`
does not mean a semantic mystery: the classifier deliberately has a narrower grammar. Preserve
`d8`, `1d4 × 10`, and `4d8;2d8` exactly; do not insert a die count, rewrite operators, split
alternatives, or evaluate them. No signed-integer formula token occurs among these 930.

The 140 `dice`/`scaledice` tags on 95 owners do not identify an effect role. Damage-like notation
is identifiable for the other 790, but its applied role, target, condition, and type association
are still unestablished by the tag. All 422 formula-bearing owners require a separate semantic
review before offering complete executable effects. This is not a measured count of spells with
prose-only mechanics: those occur outside the formula-bearing subset as Shield demonstrates.

Existing structured scaling is distinct: 41 owners/177 already-public level-keyed values versus
162 inline scaling tags on 157 owners. These owner sets do not overlap in this pin. Do not replace
the existing `scaling` contract with inline-tag observations or generate a higher-slot schedule.

## 5. Proposed minimal public contract

### Implementation-ready metadata increment

Append **one nullable enum component** to `SpellDto`: `formulaProjectionStatus`.
Keep `SpellSummaryDto`, existing `mechanics`, existing `scaling`, routes, and queries unchanged.

| Status | Precise scope |
|---|---|
| `NONE_OBSERVED` | The indexed authored Spell root has no occurrence of the four formula tag families. This says nothing about effects, numerical prose, or auxiliary labels |
| `WITHHELD` | Indexed authored formula observations exist; no observation payload is published |
| `PARTIAL` | Some indexed formula occurrences are published, while others remain withheld. For the later approved payload only |
| `COMPLETE` | All indexed formula occurrences are represented by the approved observation contract, including their required syntax arguments. This certifies neither full Spell mechanics nor rules interpretation |

Initially mapper-built detail responses would use only `NONE_OBSERVED` (547) and `WITHHELD` (422).
Legacy Java callers without access to the index receive null/omission, not a fabricated status.
An index/owner mismatch must fail startup or leave the projection unavailable; it must not report
`NONE_OBSERVED`. The first batch needs no counts, placeholder observations, or new nested object.

### Payload design after separate publication approval

Append `List<SpellFormulaObservationDto> formulaObservations` only after its approved source/value
subset exists. Use `@JsonInclude(NON_EMPTY)` on every public record and defensive `List.copyOf`
on lists. The following are **proposed signatures, not implemented models**, each in its own file:

```java
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellFormulaObservationDto(
        int occurrenceIndex,
        SpellFormulaNotation notation,
        SpellFormulaSectionDto section,
        SpellFormulaContext context,
        MechanicalExpressionDto expression,
        SpellScalingSyntaxDto scalingSyntax) {}
```

```java
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicalExpressionDto(
        String authoredToken,
        MechanicalExpressionKind lexicalKind) {}
```

```java
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellScalingSyntaxDto(
        String authoredRangeToken,
        String authoredIncrementToken) {}
```

`SpellFormulaNotation` has `DAMAGE_TAG`, `DICE_TAG`, `SCALED_DAMAGE_TAG`, and `SCALED_DICE_TAG`.
These identify tag families, not gameplay effects. `SpellFormulaSectionDto` has `MAIN_ENTRIES`
and `HIGHER_LEVEL_ENTRIES`; unknown source sections abstain. `SpellFormulaContext` distinguishes
`ENTRY_TEXT`, `TABLE_ROW`, and `TABLE_COLUMN_LABEL` through explicit hierarchy/pointer boundaries.
This avoids presenting table-header or animated-object statistic syntax as an owning-Spell effect;
no authored table caption or column description is exposed. Unknown table contexts abstain.
`MechanicalExpressionKind` maps
the existing classifier's four lexical categories, including `OPAQUE_OR_SYMBOLIC`, without a
new parser. The optional scaling syntax carries all three explicitly authored arguments when
combined with the first expression token; it adds no inferred level axis or arithmetic.

The smallest subsequent token-release candidate is narrower: single-argument `damage`/`dice`
tags whose expression the existing classifier recognizes as bounded dice/numeric syntax.
That is **708 observations on 402 owners** (640 damage-tag and 68 dice-tag observations), subject
to rights review of every included source/value. Exclude all inline scaling tags, opaque tokens,
and the ten display-argument observations from that first token release. If that entire candidate
subset were approved, statuses would be 233 `COMPLETE`, 169 `PARTIAL`, 20 `WITHHELD`, and
547 `NONE_OBSERVED`. These are technical ceilings, not cleared publication counts.

Preserve `occurrenceIndex` from Batch 6, including gaps where observations are withheld. It is an
ordinal for distinguishing occurrences within the pinned authored root, not a new stable catalog
identity or a persistence key across dataset versions. Identical tokens at different pointers or
spans remain distinct observations. Do not deduplicate, renumber filtered entries, sort by formula,
or combine the main and higher-level formulas into a computed effect.

The original resource, source hash, JSON Pointer, entry hierarchy, exact tag/argument spelling,
UTF-16 span, and text hash remain in the internal Batch 6 index. The public outer Spell ID/source
and occurrence ordinal provide the join for internal review; source paths and offsets are not
API data. No new public identity scheme or reference target kind is needed.

Only the lexical expression DTO is a plausible shared foundation for Monster/Item work. It has
no target, trigger, modifier application, or resource semantics. Spell sections and inline scaling
syntax remain domain-specific until another catalog supplies matching evidence. Do not serialize
the shared internal `MarkupTag` or `SyntaxObservation` hierarchy directly.

## 6. Example response fragments

These are fragments of the existing detail response with proposed additive fields. Existing
fields are taken from `runtime-evidence.json`; omitted fields still retain their current values.
No example implies that an API change has been made.

**Metadata-only, implementation-ready proposal: PHB Fireball**

```json
{
  "id": "spell:phb:fireball",
  "source": {"source": "PHB", "page": 241},
  "level": 3,
  "school": {"code": "V", "name": "Evocation"},
  "mechanics": {
    "areaTags": ["S"],
    "damageTypes": ["fire"],
    "miscellaneousTags": ["BO", "OBJ"],
    "savingThrows": ["dexterity"]
  },
  "formulaProjectionStatus": "WITHHELD"
}
```

**Metadata-only, implementation-ready proposal: PHB Shield**

```json
{
  "id": "spell:phb:shield",
  "castingTimes": [{"amount": 1, "unit": "reaction", "hasCondition": true}],
  "durations": [{"type": "timed", "duration": {"type": "round", "amount": 1}}],
  "mechanics": {"miscellaneousTags": ["MAC"]},
  "formulaProjectionStatus": "NONE_OBSERVED"
}
```

Shield's absent formula array does not mean its AC benefit or reaction condition is modeled.
Existing `hasCondition` already communicates that a withheld condition exists.

**Conditional token-release design, requiring approval: PHB Fireball**

```json
{
  "id": "spell:phb:fireball",
  "formulaProjectionStatus": "PARTIAL",
  "formulaObservations": [{
    "occurrenceIndex": 0,
    "notation": "DAMAGE_TAG",
    "section": "MAIN_ENTRIES",
    "context": "ENTRY_TEXT",
    "expression": {"authoredToken": "8d6", "lexicalKind": "DICE_NOTATION"}
  }]
}
```

This is partial because occurrence 1 is a three-argument scaling tag. The existing public
`mechanics.damageTypes` and `savingThrows` arrays would remain separate; no formula/type/save
association is created. PHB Fireball's exact locations are
`data/spells/spells-phb.json#/spell/139/entries/0` and
`/spell/139/entriesHigherLevel/0/entries/0`.

**Conditional token-release design, requiring approval: PHB Healing Word and Magic Missile**

```json
[
  {
    "id": "spell:phb:healing-word",
    "formulaProjectionStatus": "PARTIAL",
    "formulaObservations": [{
      "occurrenceIndex": 0,
      "notation": "DICE_TAG",
      "section": "MAIN_ENTRIES",
      "context": "ENTRY_TEXT",
      "expression": {"authoredToken": "1d4", "lexicalKind": "DICE_NOTATION"}
    }]
  },
  {
    "id": "spell:phb:magic-missile",
    "formulaProjectionStatus": "COMPLETE",
    "formulaObservations": [{
      "occurrenceIndex": 0,
      "notation": "DAMAGE_TAG",
      "section": "MAIN_ENTRIES",
      "context": "ENTRY_TEXT",
      "expression": {"authoredToken": "1d4 + 1", "lexicalKind": "DICE_NOTATION"}
    }]
  }
]
```

Healing Word's spellcasting modifier remains prose; no modifier value is synthesized. Magic
Missile's `COMPLETE` would describe its one observed formula only, not projectile count, damage
application, target distribution, or complete spell effects. Locations are PHB `/spell/175/entries/0`
and `/spell/213/entries/0` respectively.

TCE Booming Blade already publishes two structured scaling groups with distinct authored labels.
Its seven entry formula occurrences remain seven observations in a later approved payload;
duplicate `2d8`/`3d8` tokens at different spans are retained. No third, merged damage effect is
introduced. PHB Acid Splash already publishes level keys `1`, `5`, `11`, `17`; this proposal adds
no duplicate level schedule.

## 7. Field-by-field publication gate

The repository's [publication boundaries](../../entry-assessment/PUBLICATION-BOUNDARIES.md) and
[licensing assessment](../../LICENSING-AND-PUBLICATION.md) require source/field-specific review;
they do not authorize new expression content merely because it is deterministic or numeric.

| Public candidate / existing field | Classification | Disposition |
|---|---|---|
| Existing `SpellDto`/`SpellSummaryDto` identity, aliases, level, school, source/page, and source relationships | `ALREADY_PUBLIC` | Retain contracts; this is an observed exposure classification, not retrospective legal clearance |
| Existing casting metadata, presence qualifiers, range/distance, components/material required/cost/Boolean consumed, durations/ends/concentration, ritual | `ALREADY_PUBLIC` | Retain exact structured subset; no prose expansion |
| All twelve existing `mechanics` arrays | `ALREADY_PUBLIC` | Retain classifications and source codes; do not convert them to effects |
| Existing `scaling.label/values.levelKey/formula` | `ALREADY_PUBLIC` | Retain already-published values; does not approve new inline formula sources |
| New `formulaProjectionStatus` | `SAFE_UNDER_EXISTING_POLICY` | Application-authored statement about bounded projection completeness; no source expressions, prose, or alternatives disclosed |
| Proposed occurrence ordinal, coarse section/table context, notation enum, lexical enum | `SAFE_UNDER_EXISTING_POLICY` as metadata | No source prose; defer emitting observation records until their payload is approved. Alone these are not useful additional mechanic fields |
| New `expression.authoredToken` | `REQUIRES_PUBLICATION_APPROVAL` | Exact source/value/edition scope must be reviewed, including the 708 simple candidates |
| New `scalingSyntax.authoredRangeToken/authoredIncrementToken` | `REQUIRES_PUBLICATION_APPROVAL` | Review exact arguments and interpretation separately; three-argument syntax is not a computed progression |
| Extra display arguments, whole tags, raw argument text, named entry labels, table cells and examples | `INTERNAL_ONLY` for this contract | Not needed for the proposed mechanics observation subset; no raw-tag endpoint |
| Resource paths, pointers, source/text hashes, UTF-16 spans, internal hierarchy and diagnostics | `INTERNAL_ONLY` | Retain source evidence privately; do not expose importer details |
| Material/consume-note prose, timing qualifiers, `entries`/`entriesHigherLevel`, media | `INTERNAL_ONLY` for this contract | Preserve current publication boundary; any future request requires separate review |
| SRD/Basic Rules flags and alternate-name metadata | `INTERNAL_ONLY` | Use as review leads, not a public approval Boolean |
| Inferred damage/healing/save/target/slot/effect relationships | `INTERNAL_ONLY` / excluded from v1 | Semantics unverified; permission alone cannot make them reliable |

Primary external references rechecked on 2026-10-18: Wizards publishes specific SRD documents
under Creative Commons; that does not cover every dataset source. See the
[official SRD page](https://www.dndbeyond.com/srd). The
[Creator FAQ](https://www.dndbeyond.com/creator-faq) distinguishes Basic Rules from licensed SRD
content. [CC BY 4.0 section 3](https://creativecommons.org/licenses/by/4.0/legalcode.en#s3) requires
applicable attribution, license information, and change indications for licensed material.
These references establish a review path, not legal clearance of individual 5eTools fields.

Source `srd` appears on 319 owners and `srd52` on 339, including 17 string-valued alternate-name
markers in each family. Among formula-bearing owners, 107 have `srd` and 129 have `srd52`;
those counts overlap and are not approved-owner totals. No exact SRD passage/edition/value
crosswalk or rights decision was completed in this phase. The application's MIT license and
Source Registry metadata do not supply that missing approval.

For a token release, record privately: the exact Spell/source and occurrence/value, pinned resource
fingerprint, licensed document/version/passage or separate permission, approved subset, required
attribution/change notice, and decision reference. A later sync must not grant approval to changed
tokens or new occurrences automatically. An annotation's extraction-confidence field must never
act as its publication decision.

## 8. Coverage interpretation

- **969** Spells already expose the complete inspected core metadata subset. This is sufficient
  for structured spellbook indexing/display, not a certification of complete character-sheet or
  execution behavior.
- **910** additionally expose one or more authored mechanic-classification arrays; **59** omit
  the empty object. No missing public projection of those twelve arrays was found.
- **41** already expose structured scaling; **78** already expose non-point numeric geometry.
  Do not propose extracting either from entry prose.
- **422** have additional reliable formula syntax, **547** have no formulas in the bounded index.
  All 930 tokens remain internal under the current gate.
- **140** generic dice/scaling observations on **95** owners lack a tag-encoded effect role;
  **57** tokens on **39** owners have an opaque lexical shape. These sets overlap with other
  categories and must not be summed into a count of mechanically incomplete Spells.
- The hypothetical 708-token subset could benefit **402** owners after publication approval;
  **zero** new tokens are implementation-ready under existing approval evidence.
- The number of Spells whose complete behavior can be derived from prose is **unmeasured**.
  The gold examples establish abstention needs, not an exhaustive semantic prevalence count.

## 9. Proposed gold set and future acceptance criteria

[gold-set.json](gold-set.json) contains 22 proposed manually reviewable labels with exact locations
and spans where a tag exists. These are reviewed source examples for a future test set, not an
independently adjudicated semantic benchmark. They cover Fireball PHB/XPHB, Healing Word, False
Life, Magic Missile, Scorching Ray, Booming Blade, Prismatic Spray, Animate Objects, Confusion, Lightning
Arrow, Control Weather, XPHB Heal, Acid Splash, Shield, and Bigby's Hand.

Positive labels establish tag family, source owner, source occurrence, numeric/opaque lexical
shape, or a meaningful auxiliary zero. Rejections cover an extra display argument interpreted as
an effect value, dice-scaling interpreted as damage, an SRD flag treated as approval, and formula
absence treated as absence of mechanics. Abstentions cover healing/temp-HP application, projectile
and attack counts, conditional effect merging, and selecting a result branch.

Future acceptance must include:

1. Exact 969-owner join; 930 formula occurrences and 18 separate auxiliary observations; unchanged
   IDs, source ordering, argument boundaries, and UTF-16 reconstruction. Independently verify
   main/higher-level and row/column-label boundaries.
2. Metadata-only initial counts of 547 `NONE_OBSERVED` / 422 `WITHHELD`; legacy constructor status
   remains null when unavailable. No “no mechanics” inference and no empty placeholder payloads.
3. Every future emitted payload passes both the syntax-scope rule and an explicit matching
   publication decision. Every denied case is withheld even if its grammar is familiar or SRD flag
   is true. Rights coverage cannot be estimated by the proposed gold set alone.
4. Preserve original ordinals and duplicate occurrences; never merge formulas with existing
   `damageTypes`, saving throws, scaling, source references, or nearby words.
5. `NON_EMPTY` serialization, meaningful ordinal 0, immutable lists, absent optional scaling
   records, and repeatable byte-equivalent output for the same pin. Preserve the old `SpellDto`
   constructor signature with a delegating overload; no fake status for direct Java clients.
6. Match every proposed positive/negative/abstention label after independent review. Report results
   by claim type; do not derive a gameplay accuracy percentage from syntax membership.
7. Existing API fields have identical serialized values/order. By-name and detail routes agree,
   summaries retain their contract, 6,246 resolver outcomes remain stable, and packaged loading
   works. Future implementation requires focused and full tests; this analysis changes no tests.

## 10. Compatibility and implementation sequence

**Proposed implementation batch P1A:** one additive `formulaProjectionStatus` field on Spell detail
DTOs, computed from the existing startup annotation index. Reuse the index; parse/resolve nothing
per request. Retain the previous Java canonical-constructor signature as an overload. Keep a
legacy mapper overload if index-aware mapping requires another input. No constructor or field
change to `SpellDefinition`, no new normalized mechanic, and no summary/REST route change.

Adding a record component changes record equality, component reflection, generated `toString`,
and potentially record-pattern users. The delegating constructor preserves ordinary existing
Java construction descriptors; it cannot make all reflective/record-pattern clients unchanged.
JSON clients that reject unknown fields need an additive-contract rollout decision. Do not change
global Jackson settings. No publication status should be encoded as false/0 or guessed when
annotations are unavailable.

**Proposed P1B:** source/field approval crosswalk and attribution decision for the 708-token
literal subset. A token mapper is not implementation-ready until that gate passes. Source-specific
approval may make actual coverage smaller than 402 owners. Do not silently publish the remaining
222 observations, auxiliary labels/modifiers, or display arguments.

**Proposed P1C:** independently review inline scaling and opaque expressions. Retain syntax first;
do not introduce slot arithmetic, formulas-to-damage-type links, effects, or a generic rules AST.
Monster and Item projections require their own contract/rights assessments, even if a small lexical
expression record is reused.

## 11. Open questions and blockers

1. Is the metadata-only P1A increment useful enough to ship before expression rights clearance,
   or should it ship together with the first approved token subset? Both preserve the same design.
2. Who owns the source/field publication decisions and attribution implementation? The current
   repository has provenance metadata but no approved occurrence-level formula allowlist.
3. Which SRD versions/passages and renamed spell identities correspond to each candidate value?
   Existing source flags alone cannot answer this.
4. Should the expression scope later admit omitted-count dice, Unicode operators, and semicolon
   bases as opaque tokens? This needs deliberate consumer and publication review, not parser repair.
5. Should display-argument observations remain entirely withheld or have a reviewed typed subset?
   PHB Animate Objects illustrates why the second argument must not become an effect value.
6. How should clients handle added status values and record components? Ordinary additive JSON
   compatibility is plausible; strict schemas and Java record-pattern users need explicit review.
7. Are future consumers seeking factual syntax display or executable mechanics? This contract
   supports the former; a new semantic validation strategy is needed for the latter.

## 12. Verification

The analysis runs the existing Java adapter/mapper without starting a gameplay service or changing
the test suite. Source/DTE/normal/public comparison and exact source-span assertions pass.
Two complete JVM/scanner runs produced byte-identical runtime, coverage, and gold-set evidence.
All 175 pinned source hashes and 2,151 pre-task hashes of existing source/test/analysis files match; see
[verification.json](verification.json). `git diff --check` passes. Production behavior and the
529-test / 6,246-reference baseline are retained, not newly certified by a Maven run in this task.
