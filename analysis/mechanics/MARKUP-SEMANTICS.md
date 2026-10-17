# Markup Semantics and Limits

## What was observed

The scanner parses JSON first, then visits each string with its owner, field path, entry/table/
higher-level/transform context and UTF-16 character offsets. A balanced-brace parser separates
nested tags and top-level pipe arguments. It finds **91,994 occurrences**, including **90,294**
on definition roots, with **zero unbalanced tags**. These totals include formatting, references,
transform instructions and non-mechanical content. They are not counts of complete effects.

All frequencies and observed argument-count/nesting variants are in
[TAG-FREQUENCIES.md](TAG-FREQUENCIES.md). `corpus-evidence.json` adds per-catalog counts, affected
root counts, argument-shape histograms, code-token values and source examples for each variant.
Tag names are case-folded for aggregation; exact case/arguments remain in source and token hashes.

The upstream [Renderer Demo](https://5e.tools/renderdemo.html) corroborates that the tags combine
presentation, dice and references. Its live contents are not a pinned v2.37.0 specification.
The [v2.37.0 release page](https://github.com/5etools-mirror-3/5etools-src/releases/tag/v2.37.0)
identifies the upstream release, but a matching renderer implementation was not retrieved in this
analysis. Formal renderer defaults and all token-code meanings therefore remain a validation
dependency for a production parser. Corpus syntax is the authority for counts here.

## Relevant syntax families

Counts in the following table include support and transform contexts. Refer to the complete
frequency table for the definition-root-only denominator.

| Tags / occurrences | Observed form | Self-contained fact | Context still missing | Proposed handling |
|---|---|---|---|---|
| `damage` 11,528 | One argument 11,522 including nine nested tags; two arguments six | Authored damage-formatted expression and optional extra argument | Receiver, type, timing, application count, save/hit branch, whether incidental | Internal formula annotation; no public effect automatically |
| `dice` 4,611 | One argument 4,569 including 11 nested; two arguments 28; three arguments 14 | Dice/display annotation syntax | Whether to roll, healing versus quantity/example, modifier, subject | Internal annotation, preserve all arguments |
| `scaledamage` 148; `scaledice` 14 | Exactly three arguments throughout | Base/level-span/increment-like syntax slots | Scaling axis and association to an effect or clause | Preserve original strings; no inferred per-slot damage or healing output |
| `dc` 6,650 | One argument | Authored DC-formatted value | Check versus save, target, consequence, ability if not elsewhere explicit | Value annotation; bind only through independently verified structure |
| `hit` 6,040; `d20` two | One argument | Attack/check-roll formatting token | Complete attack, reach/range, target or modifier source | Keep tag category and expression, never evaluate |
| `atk` 4,562; `atkr` 995 | One argument; compact code/combinations | Explicit attack-category/roll label | Full weapon/attack/effect structure and targets | Keep exact code; do not impose an unverified common enum |
| `h` 5,576; `hom` 35 | Zero arguments | Explicit result-label boundary | Scope of following clauses and associated action/effect | Preserve as a label annotation; no branch application |
| `recharge` 939 | Zero arguments 166; one 770; two three | Recharge-format marker plus authored token(s), when present | Default threshold, timing, resource pool and reset semantics | Preserve zero-argument form and optional formatting argument; no default/calculation yet |
| `hitYourSpellAttack` 73; `dcYourSpellSave` two | Hit tag has zero args 27 or one display argument 46; save tag zero args | Explicit character-relative label | Character identity, ability, proficiency and actual value | Symbolic annotation only; no numeric substitution |
| `actSave` 529 | One argument | Explicit ability code in a save label | DC association, target, effect, scope | Save-label annotation; 525 Monster and four Object occurrences |
| `actSaveFail` 548 | Zero args 516; one argument 32 | Failure label and optional authored qualifier | Which save owns it, duration and consequences | Keep qualifier and order; no universal branch pairing |
| `actSaveFailBy` one | One argument, `5` | Failure-margin marker | Associated save/effects | Internal threshold token plus locator |
| `actSaveSuccess` 252; `actSaveSuccessOrFail` 84 | Zero arguments | Outcome label | Associated effect/target/application count | Preserve labels without inventing half-damage behavior |
| `actTrigger` 76; `actResponse` 76 | Trigger zero args; response zero args 65, one arg 11 | Trigger/response label boundary | Interpretation of trigger/response prose | Internal contextual annotation, no event engine |
| `chance` 126 | One arg 82; three 24; five 20 | Authored chance/display/result-text syntax | Mechanic versus example, trigger/subject, rights for result text | Keep source evidence internally; no automatic probability/effect API |
| `skillCheck` six | One argument; e.g. `acrobatics 6` | Explicit skill/check annotation | Roll role and outcome context | Narrow syntax review; do not infer a shared Skill target source |
| `condition` 8,210; `status` 616; `disease` eight | References with optional source/display parts | Mention of a named definition | Imposed/removed/immune/example status, target, timing | Reference annotation only; no new resolver kinds |
| `spell` 15,966; `item` 4,962; `feat` 187; `optfeature` five | Authored identity/source/display/configuration variants | Explicit mention/reference syntax | Grant, prerequisite, option, spell-bearing item or incidental text | Reuse selected existing resolver results only; no generic promotion to a grant |
| `filter` 848 | Display/category/filter expressions with pipes | Selection/filter text | Exact candidate list and whether the option is selected | Preserve selector; never evaluate it into granted definitions |
| `skill` 2,481; `language` 57; `sense` 679 | Tooltip/reference-like names and optional parts | Named skill/language/sense mention | Proficiency, requirement or grant context | Keep source qualification as authored; no edition defaults |
| `unit` 96; `recipe` eight | Culinary formatting/recipe links | Recipe content annotation | No gameplay mechanic established | Existing metadata-only Recipe boundary |

Source-specific `action`, `class`, feature, creature, object, trap, hazard, vehicle, upgrade, deity,
rule, table, card and deck references follow the same limitation: a named link is not a mechanical
dependency or effect. Formatting/navigation tags (`b`, `i`, `note`, `book`, `5etools`, links, colors,
tips and media-related fields) must not become mechanics simply because they contain numbers or
references. The scanner hashes long argument samples instead of reproducing source prose.

## Reproducible cases

| Owner / file / locator | Observation | Conclusion |
|---|---|---|
| Fireball PHB, `spells/spells-phb.json`, use its name/source record's `entries` and `entriesHigherLevel` | `damage 8d6` and `scaledamage 8d6|3-9|1d6` | Exact syntax; not proof of target count, separate effect radius or save branch |
| Fiendish Icon ABH, `monsters/bestiary-abh.json`, `$.monster[0].action[2].entries[0]` | `actSave dex` at offset zero, failure label later in the same string | Ability/label ordering is explicit; a complete typed save outcome is not |
| Homunculus XMM, `monsters/bestiary-xmm.json`, `$.monster[249].action[0].entries[0]` | `actSaveFailBy 5` | Rare threshold marker must be preserved; a zero-argument-only parser would lose it |
| Animated Ghaunadaur Statue WDMM, `objects.json`, `$.object[0].actionEntries[0].entries[0]` | Object `type=attack` with separate attack/hit entry arrays | Structural association is stronger than sentence adjacency, but effect details remain text |
| Abracadabrus IDRotF, `items.json`, `$.item[43].rechargeAmount` | `dice 1d20` in a dedicated recharge field | Owner field establishes recharge-amount role; it does not define when recharge happens |
| Stabilize a Creature PHB, `actions.json`, `$.action[40].entries[1]` | `dice 1d4` in action instructions | Dice syntax does not imply damage or an immediate roll instruction |
| Encounter roots, `encounters.json`, `tables[].table[].result` | 1,588 dice tags | Many are authored result quantities; no random encounter generation or Monster target inference |

Full per-tag examples include exact owner index and offsets. Fireball's precise index is recorded
in the existing [Stage 3 evidence](../spells/mechanics-investigation-evidence.json); this report
does not rely on names alone where parent/source context is required.

## Parse confidence versus effect confidence

Balanced tags and top-level argument boundaries can be parsed deterministically across all observed
syntax. That is a syntax claim only. The scanner does not validate every formula grammar, compact
code, renderer default, escaped/display argument or tag-specific semantic contract. Its argument
shape labels are lexical (`integer-token`, `numeric-or-dice-characters`, `other-text-or-expression`),
not a formula parser's success rate.

Even explicit save-result labels do not define a universal action AST. Counts are asymmetric
(529 save labels, 548 failure labels, 252 success labels), and branches can appear in different
strings, sections, repeated effects or examples. Do not pair nearest tags or treat every success
as half damage. Preserve exact paths, order and boundaries, then require an approved constrained
association or manual review.

The existing source renderer may visually assume reference defaults. That is not authoritative
source identity under the backend's resolver contract. In particular, an unqualified spell/item/
skill name must not acquire a source or `targetId` through enrichment. Existing semantic roles
and the 6,246 occurrence outcomes remain owned by the current startup resolver.
