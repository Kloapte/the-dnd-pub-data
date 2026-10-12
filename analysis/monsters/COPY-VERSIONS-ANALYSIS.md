# Monster `_copy` and `_versions` analysis

## Executive summary

The audit reads the original `monster` arrays in all 111 files listed for that collection in `analysis/coverage-manifest.json`. It found 4,560 source records, 1,144 records with `_copy`, 85 records with non-empty `_versions`, and one record using both. `_copy` children are already included in the 4,560 source records; they must be resolved in place, not counted as additional catalog entries.

There are 287 `_versions` elements. Of these, 286 are ordinary named version records. The remaining element is an abstract definition with five implementations. Treating each ordinary version as one output and each implementation as one output yields **291 additional variants**, for an estimated **4,851 independently usable records** after expansion. The estimate does not count `_templates` as independent monsters.

All 1,144 parent references include both name and source and resolve to exactly one record in this collection. There are no missing or ambiguous parents and no cycles. Thirteen copied records point to a parent that is itself copied; the maximum chain is two copy edges. Upstream copy, modifier, template, and version semantics are now analyzed in [`COPY-RESOLUTION-SPEC.md`](COPY-RESOLUTION-SPEC.md); template asset coverage and modifier counts are in [`template-inventory.json`](template-inventory.json).

The analyzer is reproducible with `node analysis/monsters/analyze_copy_versions.mjs`. It regenerates the three deterministic JSON reports in this directory. It reads source files only and does not modify them or Java code.

## `_copy` structures

`_copy` is an object with these observed properties and counts:

| Property | Records | Meaning observed in source |
| --- | ---: | --- |
| `name` | 1,144 | Parent monster name |
| `source` | 1,144 | Parent source identifier |
| `_mod` | 914 | Modification instructions |
| `_templates` | 187 | One or more additional template references |
| `_preserve` | 8 | Flags/properties whose inherited values receive special preservation treatment |

There are 187 template-reference occurrences and 42 distinct `(name, source)` template references. These are not monster parent edges: examples include `Skeleton|DMG`, `Tiefling|PHB`, and `Dracolich|MM`. They refer outside the 4,560-record monster collection. All 42 pairs resolve uniquely against the pinned v2.37.0 template asset. Three template definitions also have internal `_copy` links, which are included in that asset's preprocessing and audited separately from monster parent edges.

All copied records have at least one sibling property besides `name`, `source`, and `_copy`; in many records those are direct stat, feature, or display/source overrides. For example, `Knight of the Order` in `monsters/bestiary-cos.json`, `monster[40]`, copies `Revenant|MM`, supplies a direct `ac`, and also has `_mod` actions. A resolver must account for both direct child properties and `_mod`. The source data alone does not define a generic overlay order for every property or array.

`_preserve` is observed with `legendaryGroup` (6 records) and, once each, `variant`, `page`, `hasFluff`, and `hasFluffImages`. Preserve semantics should be confirmed upstream rather than treated as ordinary overrides.

### Parent references and graph

The lookup key `(name, source)` is unique for every reference and every source record in this collection. No reference omits source, and every requested parent is present. Thirteen parent edges point to another copied record:

| Child | Parent |
| --- | --- |
| Ogruhl | Dragon Tortoise |
| Arrigal | Vistana Assassin |
| Bray Martikov | Young Wereraven |
| Brom Martikov | Young Wereraven |
| Sir Godfrey Gwilym | Knight of the Order |
| Brother Broumane | Nightsea Chil-liren |
| Paolo Maykapal | Dragon Hunter |
| Zisatta | Nightsea Chil-liren |
| Kinyel Druu'giir | Drow Assassin |
| Y | Derro Savant |
| Zegdar | Emberhorn Minotaur |
| Shatterskull Giant | Boulderfoot Giant |
| Woodcrasher Baloth | Baloth |

The graph has 3,416 roots (no `_copy` parent), 1,131 depth-one copied records, and 13 depth-two copied records. Maximum depth is two edges; cycles: zero. The machine report includes each full edge and source location.

Representative source excerpt, from `monsters/bestiary-bgdia.json`, `monster[23]`:

```json
{"name":"Hellwasp Grub","source":"BGDIA","_copy":{"name":"Giant Centipede","source":"MM"}}
```

`Knight of the Order` (`monsters/bestiary-cos.json`, `monster[40]`) demonstrates parent source identity, direct child override, text replacement and array operations:

```json
{"name":"Knight of the Order","source":"CoS","_copy":{"name":"Revenant","source":"MM","_mod":{"*":{"mode":"replaceTxt","replace":"revenant","with":"knight"},"action":[{"mode":"replaceArr","replace":"Multiattack","items":{"name":"Multiattack","entries":["The knight makes two longsword attacks or two fist attacks."]}},{"mode":"insertArr","index":1,"items":{"name":"Longsword","entries":["{@atk mw} ..."]}}]}},"ac":[{"ac":13,"from":["broken chainmail"]}]}
```

`Sylvira Savikas` (`monsters/bestiary-bgdia.json`, `monster[45]`) demonstrates a template reference and the `_`-scoped `addSkills` operation. `Cat Skeleton` (`monsters/bestiary-cos.json`, `monster[17]`) demonstrates `setProp` with `null` values for `vulnerable` and `languages`; whether null means removal or literal null must be verified upstream.

## `_mod` operation inventory

Counts below are instruction occurrences across both `_copy._mod` and version `_mod` locations, including the one abstract version modifier. The target fields are the source `_mod` keys. `*` is a global text operation; `_` is a general operation bucket. The complete shapes and representative examples are in `copy-mod-inventory.json`.

| Mode | Count | Fields modified | Example |
| --- | ---: | --- | --- |
| `replaceTxt` | 805 | `*` | Bitter Breath, BGDIA, `bestiary-bgdia.json`, `_copy._mod.*` |
| `replaceArr` | 275 | `action` 199, `trait` 67, `spellcasting` 6, `bonus` 3 | Vestige Companion, AU, `bestiary-au.json`, version `_mod.action` |
| `appendArr` | 224 | `trait` 148, `action` 38, `spellcasting` 20, `bonus` 13, `reaction` 2, `resist` 2, `conditionImmune` 1 | Burney the Barber, BGDIA, `_copy._mod.trait` |
| `removeArr` | 122 | `trait` 57, `action` 56, `bonus` 6, `conditionImmune` 2, `resist` 1 | Battle Familiar, AU, version `_mod.trait` |
| `renameArr` | 73 | `trait` 33, `action` 29, `bonus` 8, `reaction` 3 | Battle Familiar, AU, version `_mod.action` |
| `prependArr` | 51 | `trait` 43, `action` 8 | Giant White Moray Eel, GoS, `_copy._mod.trait` |
| `addSpells` | 31 | `_` | Zikran, CM, `_copy._mod._` |
| `replaceSpells` | 14 | `_` | Traxigor, BGDIA, `_copy._mod._` |
| `addSkills` | 6 | `_` | Sylvira Savikas, BGDIA, `_copy._mod._` |
| `removeProperty` (scalar `remove`) | 4 | `action` 2, `variant` 2 | Arabelle, CoS, `_copy._mod.action` |
| `appendIfNotExistsArr` | 7 | `languages` | Blurg, OotA, `_copy._mod.languages` |
| `insertArr` | 1 | `action` | Knight of the Order, CoS, `_copy._mod.action` |
| `removeSpells` | 1 | `_` | Jijibisha Manivarshi, JttRC, `_copy._mod._` |
| `setProp` | 2 | `_` | Cat Skeleton, CoS, `_copy._mod._` |

The scalar `remove` form is represented by `action: "remove"` or `variant: "remove"`, not an object containing a `mode`. It is kept distinct in the report as `removeProperty` so it is not confused with `removeArr`.

Observed payload patterns include:

- Array operations use `items`, `names`, `replace`, `renames`, and, for `insertArr`, `index`. Payloads can be one entry or a list. `appendIfNotExistsArr.items` is either a string or array.
- `replaceTxt` uses `replace`, `with`, and optional `flags` (including `i`). The source does not establish whether matching is literal, regex-based, global, or applied to every recursive string.
- `addSkills` uses a dynamic skill-name object with numeric values; whether these values are bonuses, replacements, or arithmetic adjustments needs upstream confirmation.
- Spell modes carry dynamic usage keys and spell-level structures. `addSpells` and `replaceSpells` use keyed spell data; `removeSpells` also has a `will` shape. Preserve the original keys and nested values when resolving.
- `setProp` carries a property name and value. The observed null values make deletion-versus-null behavior an open question.
- `replaceArr` examples select by entry name, while `removeArr` uses names. Position, duplicate-name, no-match, and order behavior are not established solely by the examples.
- Direct child/version properties also override stats such as AC, CR, speed, HP, senses, skills, and defenses; these assignments are separate from `_mod`. Within `_mod`, `appendArr` is observed on `resist` (2) and `conditionImmune` (1), but no modifier targets `immune` or `vulnerable`. Attack and damage edits appear as replacement/appended action-entry content, not as a separate numeric attack/damage operation. The only observed numeric operation mode is `addSkills` (6); do not infer its arithmetic from the name.

Do not implement these operations based only on their names. Verify target selection, no-match behavior, ordering, recursive text scope, numeric behavior, and null semantics against the upstream 5eTools implementation and tests.

## `_versions` inventory

Eighty-five base records define `_versions`; their arrays contain 287 elements. There are 286 direct version records with explicit `name` and `source`, plus one abstract definition without either at the top level. Version fields observed include names/source, `_mod`, AC, HP, speed, alignment, CR, creature type, ability scores, saves, skills, senses/passive, defenses, actions, traits, bonus/reaction actions, spellcasting, tags, level, token scale, proficiency note, and variant labels. Exact key counts are in `version-inventory.json`.

Among the 286 direct versions, 244 have `_mod` and 42 use direct fields without `_mod`. The abstract element has `_abstract._mod` and five `_implementations`, each with its own `_variables` object. It does not specify a concrete version name/source on the outer element and does not contain `_copy`. This is a parameterized template expanded five ways, not one concrete variant plus five more.

Representative direct-version example: `Vestige Companion` (`monsters/bestiary-au.json`, `monster[22]`) defines three named, same-source versions (Celestial, Fiend, Undead), each with distinct field overrides and action/bonus `replaceArr` modifications. They inherit from that base monster; the version records do not identify another parent.

```json
{"name":"Vestige Companion","source":"AU","_versions":[{"name":"Vestige Companion (Celestial)","source":"AU","_mod":{"action":{"mode":"replaceArr","replace":"Vestige's Strike","items":{"name":"Vestige's Strike","entries":["... Radiant damage."]}}},"type":"celestial","resist":["radiant"]},{"name":"Vestige Companion (Fiend)","source":"AU","_mod":{"action":{"mode":"replaceArr","replace":"Vestige's Strike","items":{"name":"Vestige's Strike","entries":["... Fire damage."]}}},"type":"fiend","resist":["fire"]}]}
```

Representative abstract example: `Draconic Spirit` (`monsters/bestiary-xphb.json`, `monster[8]`) has an abstract name `Draconic Spirit ({{resist}})` and five implementation variable values: Acid, Cold, Fire, Lightning, and Poison. Its abstract modifier contains `{{resist}}` in trait/action entry content. Variable substitution and ordering relative to `_mod` text operations require upstream verification.

`Skaab` (`monsters/bestiary-ps-i.json`, `monster[13]`) is the single record with both mechanisms. It copies `Flesh Golem|MM`, then defines four named `_versions` with their own direct fields and `_mod` operations. Those version outputs therefore depend on resolving the copied Skaab base first; they are not independent copies of `Flesh Golem` that can skip the intermediate child transformation.

```json
{"name":"Skaab","source":"PSI","_copy":{"name":"Flesh Golem","source":"MM","_mod":{"*":{"mode":"replaceTxt","replace":"golem","with":"skaab","flags":"i"}}},"_versions":[{"name":"Skaab: Armor Plating","source":"PSI","_mod":{"action":{"mode":"replaceArr","replace":"Multiattack","items":{"name":"Multiattack","entries":["The skaab makes three slam attacks."]}}},"ac":[{"ac":14,"from":["armor plating"]}],"cr":"7"}]}
```

The abstract `Draconic Spirit` wrapper has no concrete top-level name/source; its five implementation values pair with the abstract name template to form five candidate display names. The audit's straightforward placeholder substitution finds no collisions with canonical monster name/source pairs; confirm exact interpolation rules upstream. No `_versions` element contains `_copy`; version inheritance is from its containing monster, including Skaab, whose base first inherits from Flesh Golem.

## Identity, names, and collisions

All 4,560 source monsters have source identity; the dataset does not provide a separate `edition` property for these records. There are **714 case-insensitive name buckets containing 1,464 source records** with repeated names across the collection. There are **zero duplicate `(name, source)` pairs** among source records. A copied record shares its parent's name in 27 cases, but the child's own source distinguishes it. The one unnamed abstract version uses its base display name and source.

Use a stable canonical identity based on source plus the original canonical name, and retain the raw source file/entity locator for traceability. For generated outputs, add a separate variant identifier. A version identifier should incorporate the parent identity and a stable discriminator for the version definition; for abstract implementations it should also include canonicalized implementation variables. Do not rename the canonical parent to make its variants unique. Preserve the original parent reference, source record, version name/source, and display name separately. `(name, source)` is presently unambiguous in the source corpus, but retaining the file/index locator protects provenance if future data introduces duplicates.

## Expansion and resolution estimate

| Measure | Count |
| --- | ---: |
| Original source monsters | 4,560 |
| Copied monsters already included above | 1,144 |
| Monsters defining versions | 85 |
| Version definitions/array elements | 287 |
| Concrete additional variants under observed expansion | 291 |
| Estimated independent catalog after expansion | 4,851 |
| Missing `_copy` parents | 0 |
| Ambiguous `_copy` parents | 0 |
| Cycles | 0 |

There are no reference-resolution blockers in the monster parent graph or against the selected template asset, so 4,560 existing records plus the estimated 291 version outputs are structurally resolvable. Modifier semantics and the template dependency are analyzed in [`COPY-RESOLUTION-SPEC.md`](COPY-RESOLUTION-SPEC.md). The monster dataset does not contain a source revision marker, so the template release is a validated compatible pairing rather than a proven original release pairing.

## Open questions and recommended resolver sequence

The original JSON establishes graph edges and payload shapes. Upstream execution semantics are documented in [`COPY-RESOLUTION-SPEC.md`](COPY-RESOLUTION-SPEC.md); resolver work should pin the upstream release, preserve the known `removeSpells` behavior choice, and record the absent source-dataset revision as a provenance limitation.

Recommended future implementation sequence:

1. Define canonical source identity and a separate generated-variant identity with provenance and parent links.
2. Build the complete `(name, source)` copy graph; reject missing/ambiguous links and cycles before materialization.
3. Verify upstream semantics and encode each `_mod` operation as a focused transformation with fixture cases drawn from these reports.
4. Resolve parent copies in topological order, applying direct child properties and modifiers in the verified order; keep the imported `MonsterDte` unchanged.
5. Resolve `_templates` using the pinned local asset and verified merge rules, including its three internal template copies.
6. Expand ordinary `_versions` and abstract implementations from their containing resolved base, preserving the source version and implementation data.
7. Validate every result for unique identity, retained source/parent provenance, deterministic output, and parity with representative upstream resolved examples.

## Machine-readable artifacts

- `copy-mod-inventory.json`: all copy records, copy-property counts, operation counts/shapes/fields, and representative examples.
- `version-inventory.json`: all version definitions, keys, shapes, modifier presence, implementation data, and generated-count estimate.
- `inheritance-graph.json`: every parent edge with source locators, missing/ambiguous references, depth, copied-parent edges, cycles, name collisions, template references, and version relationships.
- `template-inventory.json`: exact upstream asset revision/hash, all 187 references and 42 resolved pairs, template copy edges, effective modifier counts, and referencing monsters.
- `analyze_copy_versions.mjs`: deterministic source-data audit used to generate the reports.
- `analyze_template_dependencies.mjs`: deterministic local template/reference audit.
