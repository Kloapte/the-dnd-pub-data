# Batch 3A Internal Mechanics Design Checkpoint

Recorded before production implementation, 2026-10-17. The user explicitly authorizes continuing
through implementation and verification.

## Confirmed source

The parsed full-corpus audit reproduces 14 fields, 74 occurrences and 59 affected roots among
305 authored Feats. Nine versions produce 314 effective definitions. Neither version parent
(Magic Initiate XPHB and Strike of the Giants BGG) has these fields, so effective coverage remains
74 occurrences / 59 owners. There are no ordinary `_copy` declarations. Existing expansion
already inherits all 14 source fields; no expansion change is needed.

Every observed field is a nonempty array. Nested grant objects contain Boolean fixed flags,
integer category/count selectors, or explicit choices with `from`, optional `count`, and the
Weapon Master PHB `fromFilter`. Language groups can have multiple ordered alternatives.
Combined choices have an ordered `choose` list. Resistances are named strings or choice objects;
damage and condition immunities are separate string arrays. Senses are dynamic integer maps;
`senses` and `bonusSenses` remain distinct. Progressions have names, feature type lists and maps
whose only observed key is `*`; no concrete level is inferred.

## Internal model and reuse decision

Add a nullable `FeatMechanics mechanics` component to `FeatDefinition`. It contains:

- `FeatProficiencies`: separate ordered lists for skills, expertise, languages, tools, weapons,
  armor, saving throws and combined skill/tool/language choices.
- `FeatDefenses`: separate condition immunities, damage immunities and resistances.
- Separate ordered `FeatSenseValues` lists for ordinary and bonus senses.
- An ordered `FeatOptionalFeatureProgression` list retaining names, exact feature type strings,
  and authored progression keys/values.

Reuse existing immutable `RaceGrantGroupDto`, `RaceGrantDto`, `RaceGrantChoiceDto` for named
Boolean/count/choice values, and `RaceResistanceDto` for fixed/choice damage resistance. Their
historical Race names do not encode a Race owner or reference; the value semantics match these
Feat representations. Fixed flags, category/count keys (`any`, `anyProficientSkill`, etc.), choice
keys, nullable counts, ordered options and filters map directly. Expertise remains a separate
owner field, not ordinary skill proficiency. Categories unsupported by the Feat DTE are empty;
no new categories or references are inferred.

The shared choice model represents missing `from` as an empty option list. This means no explicit
enumerated options, not an empty selected result. In particular a `fromFilter` choice keeps the
exact filter and optional count. The corpus has no explicit-null or empty `from` variants whose
semantics require another representation. Exact source shape remains in existing import records.

Do not reuse `RaceSkillToolLanguageChoiceDto` for combined Feat choices: it cannot retain the
Feat choice DTE's filter field. Use `FeatCombinedGrant(List<RaceGrantChoiceDto> choose)` instead,
reusing the same general choice value without dropping a known typed field.

Background grant types are importer DTEs rather than a dedicated normalized value; their choice
shape also has categories absent from Feat source. Class proficiency grants contain optional and
descriptive restrictions, with different packages/skills semantics; they are unsuitable here.
Monster/Object defenses include conditional/note structures absent from Feat defense fields.
Optional Feature senses have three closed keys, unlike the Feat sense-key maps. Existing Optional
Feature progression DTOs add categories and sort map keys; Class milestones require integer
levels and cannot preserve `*`. Small Feat sense/progression values preserve the actual shapes
and insertion order without changing those other models.

All new internal records defensively copy collections. Nullable outer lists preserve missing
versus present-empty fields where supplied; null and absent both follow the existing DTE's null
contract. Empty groups/choices, zero counts/ranges and false flags are preserved, without selecting
options. Dynamic maps are restricted to actual sense keys and progression keys. No universal
effect model is introduced.

## Mapper and publication boundary

A dedicated `FeatMechanicsMapper` consumes the already expanded typed source fields. The existing
`FeatMapper` calls it; no display string or entry parsing occurs. Empty owner bundles are absent
unless a source field is present (including an explicit empty list). Existing ability, prerequisite,
spell, entry, provenance and identity behavior remains intact. Batch 3B is excluded.

No public DTO, service, controller or reference projection changes. `FeatDto.from()` continues its
explicit projection and does not include mechanics. This is internal normalization, with no new
publication permission or public grant/defense fields. Capture pre-change fingerprints for all
IDs, existing normalized fields, import lineage, complete public JSON and all resolver results;
then verify corpus coverage, nine versions, immutable values, negative cases and packaged loading.
