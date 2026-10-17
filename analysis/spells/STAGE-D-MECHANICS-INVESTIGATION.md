# Stage D — Spell Mechanics Investigation

## Scope and method

The investigation scanned all 18 spell JSON files and all 969 root spell records. The reproducible scanner is [investigate-spell-mechanics.mjs](investigate-spell-mechanics.mjs); its machine-readable output is [mechanics-investigation-evidence.json](mechanics-investigation-evidence.json). It walks `entries` and `entriesHigherLevel`, including nested arrays, objects, and table cells, parses balanced inline tags and their top-level pipe-separated arguments, and records source paths and surrounding snippets. The scanner reports zero malformed markup and asserts that every requested concrete example is present.

Some counts below are exact syntactic counts; heuristic measures are explicitly marked. A short-text keyword window cannot establish game-rule semantics, so conditional and “prose mechanic” counts are leads for review, not correctness claims.

## Corpus findings

| Finding | Count | Interpretation |
|---|---:|---|
| Spell roots | 969 | Complete 18-file corpus |
| `{@damage}` occurrences / spells | 642 / 362 | Formula-like damage annotations, not semantic primary-effect labels |
| `{@scaledamage}` occurrences / spells | 148 / 143 | Every occurrence has three top-level arguments |
| `{@dice}` occurrences / spells | 126 / 94 | 116 one-argument, 10 two-argument occurrences |
| `{@scaledice}` occurrences / spells | 14 / 14 | Every occurrence has three top-level arguments |
| Healing-related dice candidates | 32 | Lexical context heuristic; no dedicated healing tag was observed |
| Multiple damage tag occurrences | 223 | Multiple annotations do not necessarily mean unrelated formulas |
| Multiple distinct damage arguments | 86 | Distinct formulas still require context/meaning review |
| `areaTags` roots without a separate area-dimension field | 643 / 643 | Tags classify area; no radius/length geometry is encoded there |
| Structured damage type roots without a public formula | 361 / 361 | Damage types are public today; formulas are not |
| Roots with higher-level entries | 354 | Includes prose and structured markup |
| Higher-level entries without `scalingLevelDice` | 336 | Such entries cannot be treated as numeric scaling automatically |
| Higher-level entries without `scalingLevelDice`, `{@scaledamage}`, or `{@scaledice}` | 179 | No source-structured scaling field or scaling tag was found |
| Formula contexts flagged by conditional/nested/multi-argument heuristic | 349 | Over-approximation, not a semantic classification |
| Prose mechanic candidates without root mechanic tags | 157 | Keyword heuristic only; not an extraction target list |
| Public damage/healing formula fields | 0 / 0 | Stage C intentionally excludes descriptive entries |

The existing root-level structured classifications occur on 361 spells for `damageInflict`, 406 for `savingThrow`, 65 for `spellAttack`, 184 for `conditionInflict`, 17 for `damageImmune`, 30 for `damageResist`, 2 for `damageVulnerable`, 12 for `conditionImmune`, and 643 for `areaTags`. `scalingLevelDice` is present on 5 roots. These are source-authored categories; they do not encode full effect resolution. The scanner records these counts in `structuredFieldSpellCounts`.

No dedicated public structure presently represents damage/healing formulas, spellcasting ability modifier arithmetic, save-result branches, AC bonuses, temporary hit-point grant rules, target-count formulas, movement changes, or repeated-effect schedules. Some corresponding concepts have classification tags or appear in text, but that is not equivalent to a typed outcome model. Damage resistance/immunity classifications are present on the counts above; spell-specific interactions still require effect semantics.

The scanner does not promote area shape text or dimensions from prose to mechanics. Dedicated fields such as `damageInflict`, `savingThrow`, `spellAttack`, `conditionInflict`, `areaTags`, and `scalingLevelDice` remain the safer current structured classifications.

### Dedicated fields versus entries

Spell roots encode useful classifications and casting/range/component/duration metadata in dedicated fields. Spell prose and higher-level entries carry many formulas, conditional outcomes, target-count rules, modifier references, movement and defense effects, and their relationships. Markup tags add syntax and references, but generally do not encode whether a value is the primary effect, an on-hit branch, a secondary rider, a save branch, a repeated effect, or an incidental formula.

For example, a `{@damage ...}` tag identifies a damage-formatted expression. It does not by itself identify its target, timing, save branch, damage type, number of applications, or whether it is one of several independent effects. `{@scaledamage ...}` has a regular three-argument syntax in this corpus, which is suitable for typed parsing as an annotation, but not enough to attach the progression safely to a particular verified public effect.

## Required examples

The reproducible evidence JSON stores each target spell's dedicated fields, tag arguments, and entry paths. Source text is left in the source corpus; the evidence output does not copy descriptive entries. The following are corpus observations, not imported game-rule assumptions:

| Spell | Dedicated source fields / current public data | Markup or prose evidence | Safe conclusion |
|---|---|---|---|
| PHB Fireball | Damage type `fire`, Dexterity save, point range 150 ft; public API exposes those classifications | `{@damage 8d6}`; higher-level `{@scaledamage 8d6|3-9|1d6}`; “20-foot-radius” occurs in surrounding prose | Scaling tag can be parsed with all three arguments; radius is not a dedicated dimension and must not be emitted as one |
| XPHB Fireball | Damage type and save are structured; API exposes them | Same damage and scaling tag shapes; radius remains in prose | Separate source identity from PHB; do not derive radius |
| PHB Healing Word | Level, range, casting, and duration are structured | `{@dice 1d4}` plus spellcasting ability modifier; `{@scaledice 1d4|1-9|1d4}` in higher-level entry | Dice annotation can be retained; modifier and healing semantics need contextual verification |
| XPHB Healing Word | Same categories of dedicated metadata | `{@dice 2d4}` and `{@scaledice 2d4|1-9|2d4}` | Do not collapse edition-specific formulas |
| PHB Shield | Self range and reaction casting are structured | +5 AC and duration are prose; a source-qualified `{@spell Magic Missile|XPHB}` reference appears | No formula tag is needed to represent the prose effect; do not infer an AC effect model |
| XPHB Shield | Self range and reaction casting are structured | AC effect remains prose; source-qualified Magic Missile tag | Preserve its structured reference only; do not convert prose to a bonus mechanic |
| TCE Booming Blade | Existing source model includes structured scaling-level-dice schedules | Multiple `{@damage}` tags occur across hit, movement, and level-dependent clauses | Tag location and clause context matter; a flat formula list would lose relationships |
| XPHB Cure Wounds | Structured classification and casting metadata | `{@dice 2d8}`, spellcasting ability modifier, and `{@scaledice}` in higher-level text | Do not infer the modifier value or healing result without a verified semantic model |
| XPHB Magic Missile | Force damage classification and range are structured | `{@damage 1d4 + 1}`; dart count and added darts are prose | Formula does not encode number of darts or total spell output |
| XPHB Scorching Ray | Fire classification and ranged spell attack are structured | `{@damage 2d6}`; ray count and additional-ray rule are prose | Formula does not encode attack count |
| XPHB Counterspell | Casting metadata is structured | `{@variantrule ...}` references occur; success/failure resolution remains prose | Rule-reference tags are not structured outcome rules |

## What is safe to expose now

The Stage C public representation should continue to expose only normalized dedicated fields: school, level, casting-time alternatives, range, component flags and safe cost/consumption metadata, duration/concentration, typed damage/save/attack/condition classifications, scaling-level-dice structures that Stage B already modeled, and source relationships. It should not expose entries, snippets, or raw tags.

Parsing inline markup into typed source annotations is feasible for exact syntax and location. Semantic public mechanics—damage/healing effect definitions, save branches, target counts, area dimensions, AC bonuses, movement, resistances, and repeated effects—need either a rigorously verified semantic extraction model or explicit curation. Character-dependent calculations remain a separate future layer.

## School metadata implementation

The production API now returns school as `{ "code": "V", "name": "Evocation" }` for both spell detail and summary responses. Eight observed codes map through one strict backend enum. The JSON type change is breaking for clients that read `school` as a string; IDs, routes, and school filtering remain compatible, with filters accepting either code or full name. See [SCHOOL-METADATA-IMPLEMENTATION.md](SCHOOL-METADATA-IMPLEMENTATION.md).

## Recommended next step

Implement an internal typed markup-annotation parser that records tag kind, exact arguments, entry path, higher-level/table context, and nearby source-node identity. Keep it out of the public DTOs. Use separate semantic verification/curation records to associate a formula with an effect, target, branch, and scaling rule before exposing mechanics. This prevents syntactic markup from being mistaken for a complete spell effect.
