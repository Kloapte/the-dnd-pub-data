# Step 7 Batch 3A: Structured Feat Mechanics

Implemented and verified on 2026-10-17 against the pinned 5eTools v2.37.0 corpus.
This batch normalizes existing typed fields internally; it changes no public contract.

## Evidence and original gap

The full `data/feats.json` audit confirms **305 authored roots**, **59 affected roots**,
**74 field occurrences**, **14 field families**, and **nine versions / 314 effective definitions**.
All fourteen fields were already typed by `FeatDte` and retained in `FeatImportRecord.sourceRecord`,
but were absent from `FeatDefinition`. This was a normalized-domain omission, not discarded input
or an opportunity requiring prose extraction.

[audit-feat-mechanics.mjs](audit-feat-mechanics.mjs) parses source structures and records counts,
every field/type path, exact values and locators, all affected owners, version metadata and the
source SHA-256. [field-evidence.json](field-evidence.json) is deterministic and independently
reproduces G3. Original Step 7 scanner code and machine evidence are unchanged.

### Complete field inventory and normalized destinations

Every top-level occurrence is a nonempty array. The following destinations are relative to the
new `FeatDefinition.mechanics` component. Every row was previously import-only. Every field is
already inherited by the existing version expander; the nine actual versions inherit none of
these fields because their parents have none.

| Exact source field | Authored / effective occurrences | Observed nested shape and semantics | Existing DTE | Normalized destination |
|---|---:|---|---|---|
| `skillProficiencies` | 12 / 12 | Object groups: fixed Boolean skill keys, `any` count, or `choose.from` with optional count | `List<FeatGrantDte>` | `proficiencies.skills` |
| `expertise` | 7 / 7 | Fixed Perception/Intimidation flags or `anyProficientSkill` count | `List<FeatGrantDte>` | `proficiencies.expertise` |
| `languageProficiencies` | 7 / 7 | Fixed language flags or `any` counts; ordered multi-group alternatives | `List<FeatGrantDte>` | `proficiencies.languages` |
| `toolProficiencies` | 10 / 10 | Fixed flags, `any`, `anyArtisansTool`, `anyMusicalInstrument` counts, or explicit options/count | `List<FeatGrantDte>` | `proficiencies.tools` |
| `weaponProficiencies` | 4 / 4 | Fixed firearms/martial/improvised flags or choice with exact `fromFilter` and count | `List<FeatGrantDte>` | `proficiencies.weapons` |
| `armorProficiencies` | 6 / 6 | Fixed heavy/light/medium/shield Boolean keys, including combined fixed keys in one group | `List<FeatGrantDte>` | `proficiencies.armor` |
| `savingThrowProficiencies` | 2 / 2 | Choice among ordered ability-code strings; count omitted | `List<FeatGrantDte>` | `proficiencies.savingThrows` |
| `skillToolLanguageProficiencies` | 2 / 2 | Group object with ordered `choose` array; options `anySkill`, `anyTool`, count 3 | `List<FeatSkillToolLanguageChoiceDte>` | `proficiencies.combined` |
| `conditionImmune` | 2 / 2 | String arrays: poisoned / frightened | `List<String>` | `defenses.conditionImmunities` |
| `immune` | 2 / 2 | String arrays: radiant / poison damage | `List<String>` | `defenses.damageImmunities` |
| `resist` | 11 / 11 | Six roots with named strings; five roots with `choose.from` and optional count | `List<FeatResistanceDte>` | `defenses.resistances` |
| `senses` | 4 / 4 | Objects mapping sense names to integer values: blindsight 10/15, truesight 60 | `List<FeatSenseGrantDte>` | `senses` |
| `bonusSenses` | 1 / 1 | Separate bonus sense map: darkvision 60 | `List<FeatSenseGrantDte>` | `bonusSenses` |
| `optionalfeatureProgression` | 4 / 4 | Objects with name, ordered feature type strings and integer-valued progression map | `List<FeatOptionalFeatureProgressionDte>` | `optionalFeatureProgressions` |
| **Total** | **74 / 74** | **59 authored / effective owners** | | |

No present field or descendant value in these 74 occurrences is null, an empty array, Boolean
false or numeric zero. Those representations are tested synthetically where supported by the
existing typed schema. Optional choice counts and filtered-choice option arrays can be absent.
No immunity choices, conditional defense objects, source-qualified grant UIDs, or concrete
progression level keys occur. No extra fields are added for these confirmed absences.

Source grant keys are genuinely dynamic names/categories. Sense names and progression keys are
the only new map-valued normalized fields, with `Integer` values. They are not generic property
bags. All progression maps have the literal key `*`, with value 1 or 2; no level is invented.

### Reproducible examples

- Aberrant Anatomy RHW: `$.feat[0].skillProficiencies`, `.expertise`, `.senses` retain fixed
  Perception proficiency, distinct Perception expertise and blindsight 15.
- Artificer Initiate TCE: `$.feat[22].toolProficiencies` keeps `anyArtisansTool: 1` as a category
  selector, without selecting a tool.
- Cult of the Dragon Initiate FRHoF: `$.feat[78].languageProficiencies` retains two ordered groups,
  Draconic and an `any` count. They are not flattened into one guaranteed grant set.
- Crafter XPHB: `$.feat[73].toolProficiencies` retains ordered tool options and count 3.
- Weapon Master PHB: `$.feat[300].weaponProficiencies` retains count 4 and the literal filter
  `type=martial weapon;mundane weapon|miscellaneous=mundane`, without evaluating it.
- Skilled PHB/XPHB: `$.feat[250/251].skillToolLanguageProficiencies` retains the combined choice
  group and count; it does not become three selected skill/tool grants.
- Boon of Poison Mastery FRHoF: `$.feat[48].conditionImmune` and `.immune` keep poisoned-condition
  immunity distinct from poison-damage immunity.
- Keenness of the Stone Giant BGG: `$.feat[158].bonusSenses` remains a bonus field, separate from
  ordinary sense ranges and without computing a resulting character range.
- Eldritch Adept TCE, Fighting Initiate TCE, Martial Adept PHB and Metamagic Adept TCE:
  `$.feat[98/114/189/193].optionalfeatureProgression` preserve names, `EI` / `FS:F` / `MV:B` / `MM`
  feature type codes and literal wildcard counts. These codes do not become feature IDs.

The compact multi-index notation above identifies separate records; every individual exact JSON
locator is recorded in the machine evidence.

## Final internal model and mapping

The [design checkpoint](DTO-DESIGN.md) was written before production changes.

`FeatDefinition` gains nullable `FeatMechanics mechanics`, with six new immutable value records:

- `FeatMechanics`: proficiency bundle, defense bundle, distinct ordinary/bonus sense lists, and
  optional-feature progression list.
- `FeatProficiencies`: eight separate ordered grant/group lists, including expertise and combined
  choices in their own fields.
- `FeatDefenses`: separate condition immunities, damage immunities and fixed/choice resistances.
- `FeatCombinedGrant`: ordered `choose` values, including all fields of the source choice DTE.
- `FeatSenseValues`: immutable sense-name-to-integer map.
- `FeatOptionalFeatureProgression`: name, ordered feature types and immutable progression map.

The stateless `FeatMechanicsMapper` consumes already expanded `FeatDte` fields directly and is
called from `FeatMapper`. No display string or descriptive entry is parsed. No new Spring bean,
cache, catalog, resolver dependency or per-request normalization is introduced.

All new collection carriers defensively copy their values. Sense and progression maps use
unmodifiable insertion-preserving copies. Progression keys remain strings and are neither sorted
into interpreted levels nor expanded. Ordered groups, choices and options remain ordered.

An absent field stays null in its new owner bundle; an explicitly present empty list remains an
empty list. A mechanics bundle is omitted internally only when all fourteen DTE fields are null.
The existing DTE does not distinguish explicit-null from absent root fields; both remain null.
Zero counts/ranges and false grant flags are copied without evaluation.

### Reuse assessment

Existing `RaceGrantGroupDto`, `RaceGrantDto` and `RaceGrantChoiceDto` encode the same named
Boolean/count/choice semantics, without a Race owner or target identity. They are reused as values
inside Feat-specific bundles. Grant keys, optional counts, ordered options and exact filters are
preserved. The owner field supplies the proficiency versus expertise role.

`RaceResistanceDto` is also reused: the existing Race mapper already consumes the same
`FeatNamedResistanceDte` / `FeatChoiceResistanceDte` source types with identical damage-resistance
semantics. Named and choice values occupy separate, mutually exclusive fields.

The shared choice value represents missing `from` as an empty enumeration, keeping `fromFilter`
and nullable count separately. This is no selected result or inferred grant. The actual corpus
has no explicit-null/empty `from` distinction to preserve, and all 74 authored source shapes
round-trip exactly from normalized values. Existing import records retain exact source shape.
The additional shared `categories` property is empty because Feat choice DTEs have no such field.

Other inspected structures were unsuitable:

- Background grants are source DTEs with an additional category-bearing choice shape; they are
  not reused as new application-facing Feat import models.
- Class/Subclass proficiency structures include descriptive restrictions, optional flags and
  package/skill-specific structures; those are different source semantics.
- `RaceSkillToolLanguageChoiceDto` lacks the filter field present in `FeatGrantChoiceDte`.
  `FeatCombinedGrant` reuses the general choice value so no known typed field is discarded.
- Monster/Object defense records carry note/conditional structures absent from these Feat fields.
- Optional Feature senses have three closed fields, unlike the dynamic Feat sense-key map.
- Existing Optional Feature progression DTOs add categories and sort map keys. Class progression
  milestones require integer levels. The narrow Feat progression record retains the literal `*`
  and authored map ordering without modifying either other model.

## Version inheritance and preserved existing data

There are **zero ordinary Feat `_copy` declarations**. Existing version expansion produces
three Magic Initiate XPHB variants and six Strike of the Giants BGG variants. Their parents
author none of the fourteen fields, so all nine correctly have null mechanics. Effective
coverage remains **59 definitions and 74 occurrences**, with **255** definitions having no
new mechanics bundle.

The unchanged `FeatVersionExpander` already passes all fourteen parent fields to expanded
definitions. A synthetic fixture combines actual corpus field shapes and confirms all fourteen
survive version inheritance without selecting benefits, changing parent data or altering lineage.
No new copy/version semantics, version overrides or expansion operations are implemented.

Pre-change fingerprints confirm that every existing normalized field remains unchanged across
all 314 definitions: ability increases, prerequisites (including the existing category flattening),
spells, entries, repeatability, reprints, source data, IDs and parent relationships. Import source
records and expansion declaration/index metadata also retain their full pre-change fingerprint.
Batch 3B prerequisite normalization is explicitly excluded.

## Public and licensing boundary

No Feat public DTO, summary, endpoint, service or reference projection changes. `FeatDto.from()`
keeps its explicit existing field list and omits `mechanics`. Complete public detail JSON including
references, and all summaries, match pre-change fingerprints without removing any public field.

No new grant, defense, sense, progression, source entry, markup, full referenced definition or
diagnostic field is published. Reusing an existing DTO value class internally does not authorize
new public exposure. Publication review remains separate from normalization correctness.
There are no new reference occurrences, target kinds, inferred target IDs, evaluated filters,
character grants, selected alternatives, applied defenses or calculated senses/progressions.

## Tests and verification

Twelve tests in `FeatStructuredMechanicsTests` cover:

1. Pre-change fingerprints for all 314 IDs, all existing normalized fields, import lineage,
   complete public details/summaries, and every resolver result.
2. Exact reconstruction of all 74 authored JSON fields from normalized values, independently
   comparing every one of the 59 affected roots and all fourteen field-family totals.
3. All 314 effective definitions, all nine actual variants, lineage and effective coverage counts.
4. Fixed grants, count/category selectors, expertise, optional counts, ordered alternatives,
   combined choices and the actual Weapon Master filter.
5. Named/choice resistance, condition versus damage immunity, and ordinary versus bonus senses.
6. All four actual progressions, wildcard keys, exact feature type codes and ordered groups.
7. Supported synthetic empty/null/zero/false values, without default counts or selected options.
8. Synthetic inheritance of all fourteen actual field shapes through the unchanged version expander.
9. Immutable normalized collections, defensive copies and detachment from mutable source lists/maps.
10. Strict rejection of unsupported shapes and unknown root/nested fields; null grant values fail
    explicitly rather than being guessed or silently discarded.
11. Existing list/detail/by-name REST JSON, including representative variants, without new fields.
12. Full normalization from an isolated resource JAR classloader, comparing all 314 definitions
    including the new mechanics and nine versions.

No existing test is modified or weakened. Fingerprints canonicalize object keys and preserve
array order. Only the new internal `mechanics` component is excluded from the old normalized-field
comparison; no field is excluded from the public JSON or resolver comparisons.

| Verification | Result |
|---|---|
| Focused new/existing Feat tests | **24 passed** |
| Complete Maven suite via `mvn package` | **430 tests; zero failures, errors or skips** (418 baseline + 12) |
| Stable identities | All **314** match the pre-change fingerprint |
| Authored/effective coverage | **59 / 59** affected owners; **74 / 74** field occurrences |
| Versions | All **nine** and their lineage unchanged |
| Existing normalized fields / import lineage | Complete fingerprints unchanged |
| Public Feat detail and summary JSON | Complete fingerprints unchanged |
| Resolver results | All **6,246** complete results unchanged; zero new occurrences |
| Resolver outcomes | RESOLVED **2,517**; AMBIGUOUS **2,054**; UNRESOLVED **181**; DEFERRED **1,494** |
| Source JSON | All **175** hashes match the original analysis baseline |
| Source audit | Deterministic rerun produces identical evidence |
| Packaged application | All seven new classes packaged; startup and scoped HTTP checks pass |
| Whitespace | `git diff --check` and new-file whitespace/conflict checks pass |

Only two existing production files change: `FeatDefinition` and the call in `FeatMapper`.
The six new value records and stateless mapper are internal additions. Existing source DTEs,
deserializers, version expander, public DTOs, services, controllers, shared grant models and
reference code retain their hashes. Prior Batch 1 and Batch 2 work is preserved.

`verify-packaged.ps1` starts the executable JAR from `target` on loopback and a temporary port.
It validates 314 summaries, detail lookup for all 59 affected roots and nine versions, ordinary
source-aware by-name lookup and version-parent lookup, without publishing new content. It stops
only its own process. Runtime results and logs remain under `target`.

### Separately observed existing HTTP limitation

The executable server returns **403** for an encoded semicolon in
`/api/feats/by-name/Magic%20Initiate%3B%20Cleric?source=XPHB`. Existing security, routing and
controller files are unchanged; the application uses the existing HTTP security configuration.
Standalone controller tests do not exercise that security boundary. All nine variant **ID**
lookups pass, and ordinary by-name requests pass. The smoke script records the encoded-name
status explicitly. No security relaxation or routing fix is included in this internal batch.

Reproduction from the repository root, using the configured Maven cache where required:

```powershell
node analysis/mechanics/batch-3a-feat-structured-mechanics/audit-feat-mechanics.mjs
mvn package
& ./analysis/mechanics/batch-3a-feat-structured-mechanics/verify-packaged.ps1
git diff --check
```

## Remaining limitations

- New mechanics remain internal and require a separate publication decision before projection.
- Optional counts stay unspecified when absent; filters and category selectors are not evaluated.
- There are no authored concrete progression levels or immunity choices to normalize here.
- The shared choice value uses an empty enumeration for missing `from`; root null/absence follows
  the existing DTE contract. Source import records retain authored distinctions.
- Prerequisite category flattening, prose-only effects, EntryDte assessment, other Step 7 batches,
  Step 8, character state and frontend work remain outside this implementation.
