# Monster copy, modifier, template, and version resolution

## Executive summary

This investigation compares the local 4,560-monster corpus (111 files) with upstream 5eTools release `v2.37.0`, pinned at immutable commit [`8c026b807fac21862a309379a0c3228a0683198b`](https://github.com/5etools-mirror-3/5etools-src/tree/8c026b807fac21862a309379a0c3228a0683198b). The local monster JSON and coverage manifest contain no upstream commit, release, build timestamp, or revision marker; their `_meta` keys are only `dependencies`, `internalCopies`, and `otherSources`. This release is therefore a compatible template snapshot by direct reference validation, not a provably co-versioned source snapshot.

The corpus contains 1,144 `_copy` children, 85 records with `_versions`, 187 `_templates` reference occurrences (42 distinct name/source pairs), and 287 version-definition array elements. One monster (`Skaab`, PSI) has both `_copy` and `_versions`. All copy parents resolve uniquely in this corpus; the previously audited graph has no missing parents or cycles and reaches depth two. Expanding the 286 direct versions and five abstract implementations adds 291 usable variants to the 4,560 source records, for an estimated 4,851 outputs. Copy children are already part of those 4,560.

The project now vendors the upstream template asset at [`src/main/resources/data/monsters/template.json`](../../src/main/resources/data/monsters/template.json), unchanged from the pinned revision (SHA-256 `F3675FA8AB755D520A4A22D7837B84B474E0E50CACD1294DB50F6FC3FFAE2902`). All 187 monster references and all 42 distinct name/source pairs resolve uniquely. The complete corpus plus referenced templates uses 22 modifier modes: 14 in monster copy/version instructions and eight additional modes introduced by templates. Three templates also use internal `_copy` instructions, which upstream resolves before applying those templates.

## Evidence and scope

Primary sources consulted:

- Pinned upstream [`js/utils.js`](https://github.com/5etools-mirror-3/5etools-src/blob/8c026b807fac21862a309379a0c3228a0683198b/js/utils.js): metadata copy loading around [lines 5088-5122](https://github.com/5etools-mirror-3/5etools-src/blob/8c026b807fac21862a309379a0c3228a0683198b/js/utils.js#L5088-L5122), copy recursion and template loading around [lines 5504-5555](https://github.com/5etools-mirror-3/5etools-src/blob/8c026b807fac21862a309379a0c3228a0683198b/js/utils.js#L5504-L5555), modifier handlers/dispatch around [lines 5630-6121](https://github.com/5etools-mirror-3/5etools-src/blob/8c026b807fac21862a309379a0c3228a0683198b/js/utils.js#L5630-L6121), copy application around [lines 6132-6234](https://github.com/5etools-mirror-3/5etools-src/blob/8c026b807fac21862a309379a0c3228a0683198b/js/utils.js#L6132-L6234), and version expansion around [lines 6428-6538](https://github.com/5etools-mirror-3/5etools-src/blob/8c026b807fac21862a309379a0c3228a0683198b/js/utils.js#L6428-L6538).
- Pinned upstream [`data/bestiary/template.json`](https://github.com/5etools-mirror-3/5etools-src/blob/8c026b807fac21862a309379a0c3228a0683198b/data/bestiary/template.json), with 66 total `monsterTemplate` definitions; 42 are referenced by this corpus.
- Local inventories: [`COPY-VERSIONS-ANALYSIS.md`](COPY-VERSIONS-ANALYSIS.md), [`copy-mod-inventory.json`](copy-mod-inventory.json), [`version-inventory.json`](version-inventory.json), [`inheritance-graph.json`](inheritance-graph.json), and the new [`template-inventory.json`](template-inventory.json), reproducible with `node analysis/monsters/analyze_template_dependencies.mjs`.

This is a source-semantics analysis, not a claim that an independently implemented Java resolver has been parity-tested. The exact monster corpus revision cannot be reconstructed from the corpus itself. The selected template revision is immutable and has full reference coverage, but a future resolver should record this explicit pairing and revalidate it whenever either dataset changes.

## Copy inheritance and execution order

`_copy.name` and `_copy.source` identify a monster in the current bestiary collection. Upstream derives a hash for that reference, looks it up in the entity list/cache, rejects self-reference, and recursively resolves a parent that itself has `_copy` before applying the child. Upstream throws on a missing parent when running the normal page load. Our local corpus makes every reference unique, but the resolver should still fail explicitly on missing, ambiguous, and cyclic links.

`getCopy` starts from a working clone of the child and records the set of the child's original root keys. It then visits each parent root property:

1. If the child explicitly has the property with JSON `null`, that property is deleted from the child result. It does not inherit the parent's value.
2. If the child property is absent, the parent property is copied as a whole root value, except for the preserve-gated metadata fields described below.
3. If the child property exists and is non-null, that entire child value wins. Nested objects and arrays are **not recursively merged** by this root pass. For example, a child `speed` object replaces the parent `speed` object as a unit.

Thus the direct child fields are already in place before `_mod`, and they override parent values by root key. A child's nested partial object is not a patch unless an explicit modifier implements that patch behavior. Explicit null and absence are different states.

Upstream's preserve-gated base fields are `page`, `otherSources`, `referenceSources`, `srd`, `srd52`, `basicRules`, `basicRules2024`, `reprintedAs`, `hasFluff`, `hasFluffImages`, `hasToken`, `tokenCredit`, `tokenCustom`, `foundryTokenScale`, `altArt`, and `_versions`. For ordinary copies these are inherited only if `_preserve` has `"*": true` or the property key set to `true`. This is a whitelist over fields that normally should not flow from the base; it does not mean “preserve the child value” for every arbitrary property. Version construction supplies a special preserve-all default, discussed below.

The executable order in `getCopy` is:

1. Normalize child modifier payloads to instruction arrays.
2. Resolve the referenced templates. For each template, append its `_mod` instructions after any child instructions for the same modifier target; template root changes are retained for later.
3. Shallowly fill missing root values from the already-resolved parent, while honoring explicit null deletion and `_preserve`.
4. Apply each template's `_root` values only when that root property was not present on the original child. Because the key set was captured before parent inheritance, a template root value **overwrites an inherited parent value** when the child omitted that key. An explicit child value (including explicit null) blocks the template root assignment.
5. Resolve 5eTools variable tokens in modifier payloads and execute modifiers. Root-level modifier targets are sorted before `_` and `*` targets; operations inside one target run in their normalized array order. `*` applies to the fixed entry-property set (action, bonus, reaction, trait, legendary, mythic, variant, spellcasting, plus corresponding headers), not every string on the monster.
6. Remove `_copy` and set an internal `_isCopy` marker in the upstream working result. A Java import pipeline should retain equivalent provenance in a separate wrapper instead of relying on that transient flag.

This distinction means template `_root` assignments occur after inheritance and take precedence over inherited parent roots, while explicit child roots take precedence over template roots. Template `_mod` operations occur after child `_mod` instructions. Templates are applied as part of the referenced child's copy operation, not as a new monster-parent edge.

### `_preserve`

The current source shows `_preserve` as a per-key boolean map or wildcard. It controls copying of the preserve-gated properties above. In addition, versions default `_preserve` to wildcard true. The local corpus has eight explicit `_preserve` uses; the earlier inventory names `legendaryGroup` (six), and one each of `variant`, `page`, `hasFluff`, and `hasFluffImages`. The discrepancy between the preserve-gated field list and some observed keys is relevant: keys outside the base preserve-gated set are already inherited under the normal shallow copy, so the flag is harmless for those properties in this path. It should not be generalized into deep-merge behavior.

## Modifier compatibility matrix

The table below retains the detailed behavior for monster-defined modes. Counts are source instruction occurrences from monster `_copy._mod` and `_versions._mod` locations.

| Mode | Count | Confirmed behavior | Important failure/edge behavior | Dataset example |
| --- | ---: | --- | --- | --- |
| `replaceTxt` | 805 | Builds a JavaScript global regular expression from `replace`; appends configured flags (such as `i`). By default walks the target entry property and its `entries`, `headerEntries`, and `footerEntries` content recursively. It splits inline tags and does not replace inside `{@...}` tags unless `tagInsensitive` is enabled. `props` narrows the searched properties. | Regex syntax is active, so replacement strings must not be treated as literals. Scope is constrained by target property and recursive entry fields, not the entire monster. Preserve inline tags exactly. | Bitter Breath (BGDIA), `_copy._mod.*`; Skaab (PSI) replaces “golem” case-insensitively. |
| `replaceArr` | 275 | Replaces the first matching array element. Selector can be an exact name string, `{regex, flags}`, or `{index}`. It splices one element with one or multiple supplied `items`, preserving surrounding order. | Missing target array or match throws unless an alternate nonthrowing variant is used. Duplicate names select the first match. | Knight of the Order (CoS), action `Multiattack`, followed by insertion of `Longsword`; Vestige Companion (AU) version actions. |
| `appendArr` | 224 | Appends normalized item(s) to the target array. | A missing target array is created from the new items; existing arrays retain their original order before appended items. | Burney the Barber (BGDIA), trait; several templates append traits/actions. |
| `removeArr` | 122 | Removes the first entry with matching `.name` for `names`, or strict-equal requested values for `items`. | Missing matches throw unless `force: true`; name removal is first-match only. | Battle Familiar (AU), version traits; Dracolich template uses `force: true` to remove Amphibious if present. |
| `renameArr` | 73 | For each `{rename, with}`, finds the first named entry and changes its `name` in place; keeps its position and content. | Missing source name fails. Does not replace the entry's nested text. | Battle Familiar (AU), version action/trait changes. |
| `prependArr` | 51 | Places new item(s) before the existing array contents. | Missing array becomes the new list. | Giant White Moray Eel (GoS), traits. |
| `addSpells` | 31 | Selects the named spellcasting object when `name` is supplied, otherwise the first one. Adds missing spell levels; merges array categories and case-insensitively sorts; incoming scalar categories replace. Also appends supported `will`, `constant`, and `ritual` arrays and usage buckets (`recharge`, `legendary`, `charges`, `rest`, `restLong`, `daily`, `weekly`, `monthly`, `yearly`). | Requires spellcasting; nested non-array category data may throw. Valid usage keys include numeric keys and `Ne` forms; do not normalize `1` and `1e` as equivalent. | Zikran (CM), `_copy._mod._`. |
| `replaceSpells` | 14 | Uses the first spellcasting block. Replaces the exact spell string in supported `spells` levels or `daily` buckets with one or more strings, then sorts case-insensitively. | Missing exact spell match throws. Upstream code notes limits for non-slot based spellcaster shapes. | Traxigor (BGDIA), `_copy._mod._`. |
| `addSkills` | 6 | Each requested skill value is a mode: 1 adds proficiency bonus plus ability modifier; 2 adds twice proficiency bonus plus ability modifier. The new total is stored as a signed bonus string only when greater than the existing total, so this operation does not lower a skill. Proficiency bonus derives from CR. | Depends on CR and ability-score lookup; unsupported skill key or malformed CR should be surfaced. This is not “add the integer to the existing bonus.” | Sylvira Savikas (BGDIA), `_copy._mod._`. |
| Scalar `remove` (inventory label `removeProperty`) | 4 | A string value `"remove"` for a property deletes that root property; the same handler supports a dotted property path in applicable modifier targets. | Distinct from array `removeArr`. | Arabelle (CoS), action; observed also for `variant`. |
| `appendIfNotExistsArr` | 7 | Appends only values not deeply equal to any existing value. | It does not deduplicate duplicates inside the incoming list against each other. | Blurg (OotA), languages. |
| `insertArr` | 1 | Splices new item(s) at the requested index; without an index it appends. JavaScript `splice` index semantics apply, including negative indices. | Missing target array throws. | Knight of the Order (CoS), inserts Longsword at index 1. |
| `removeSpells` | 1 | Removes exact values from spell levels and supported usage buckets. | Upstream's `constant`, `will`, and `ritual` branch calls `filter` without assigning its result, so those removals have no effect in the inspected implementation. Missing bucket arrays can also fail. Implement parity intentionally and add a regression test before deciding whether to correct the upstream defect. | Jijibisha Manivarshi (JttRC), removes daily uses under `3e` and `1e`. |
| `setProp` | 2 | Deep-clones `value` into `prop`, including preserving JSON null as an actual null value. It does not interpret null as deletion. | A null assignment is different from a scalar `remove` instruction. Path creation/behavior follows upstream property setter utilities. | Cat Skeleton (CoS), sets `vulnerable` and `languages` to null. |

### Combined monster and template mode count crosswalk

Referenced template definitions are resolved through the asset's `_meta.internalCopies` before use. “Template definitions” counts instructions in the effective 42 distinct referenced templates, including inherited instructions. “Template applications” weights those effective instructions by the 187 references in the monster corpus. Template internal `_copy` modifiers execute three times while loading the template asset; they are shown separately. The machine-readable source for this matrix is `template-inventory.json`.

| Mode | Monster instructions | Effective referenced-template definitions | Per-reference template applications | Template internal-copy operations |
| --- | ---: | ---: | ---: | ---: |
| `replaceTxt` | 805 | 5 | 14 | 0 |
| `replaceArr` | 275 | 0 | 0 | 0 |
| `appendArr` | 224 | 39 | 160 | 0 |
| `removeArr` | 122 | 1 | 2 | 0 |
| `renameArr` | 73 | 0 | 0 | 0 |
| `prependArr` | 51 | 1 | 26 | 0 |
| `addSpells` | 31 | 0 | 0 | 0 |
| `replaceSpells` | 14 | 0 | 0 | 0 |
| `addSkills` | 6 | 7 | 37 | 0 |
| scalar `remove` / `removeProperty` | 4 | 0 | 0 | 0 |
| `appendIfNotExistsArr` | 7 | 59 | 224 | 0 |
| `insertArr` | 1 | 0 | 0 | 0 |
| `removeSpells` | 1 | 0 | 0 | 0 |
| `setProp` | 2 | 0 | 0 | 3 |
| `addSenses` | 0 | 22 | 113 | 0 |
| `maxSize` | 0 | 1 | 26 | 0 |
| `prefixSuffixStringProp` | 0 | 1 | 26 | 0 |
| `scalarAddDc` | 0 | 6 | 156 | 0 |
| `scalarAddHit` | 0 | 6 | 156 | 0 |
| `scalarAddProp` | 0 | 2 | 52 | 0 |
| `scalarMultProp` | 0 | 1 | 26 | 0 |
| `scalarMultXp` | 0 | 1 | 26 | 0 |
| **Total instructions/applications** | **1,616** | **152** | **1,044** | **3** |

There are 14 distinct modes in effective referenced templates; six overlap monster modes. `setProp` also occurs in the three internal template-copy definitions. Eight modes are new compared with monster instructions: `addSenses`, `maxSize`, `prefixSuffixStringProp`, `scalarAddDc`, `scalarAddHit`, `scalarAddProp`, `scalarMultProp`, and `scalarMultXp`. Thus a resolver covering this corpus and its templates must implement 22 distinct operational modes; internal template copy adds three more `setProp` operations but no new mode name.

### Template-introduced mode semantics

These behaviors come from the pinned upstream handlers in `js/utils.js`; counts and actual referencing monsters are in `template-inventory.json`.

| Mode | Confirmed behavior and important edge | Representative template and use |
| --- | --- | --- |
| `addSenses` | Ensures `senses` is an array. For each `{type, range}`, it finds the first existing string matching `<type> <integer>` case-insensitively; it replaces that value only when the current range is lower. If no matching sense is found, it appends `<type> <range> ft.`. | `Deep Gnome|DMG` adds darkvision; `Large or Smaller Half-Blue Dragon|MM` adds blindsight and darkvision. |
| `maxSize` | Sorts the monster's size codes, retains all alternatives whose size rank is at or below `max`, and if none qualify uses the maximum itself. Unknown size codes throw. | `Reduced Threat|TftYP`, referenced by 26 reduced-threat monsters, caps size at `L`. |
| `prefixSuffixStringProp` | Resolves `prop` relative to the modifier target path; if the value is a string, wraps it with optional `prefix` and `suffix`; missing/non-string values are unchanged. | `Reduced Threat|TftYP` wraps `hp.formula` as a floored half formula. |
| `scalarAddProp` | Adds `scalar` numerically to the selected property, or every property when `prop: "*"`; string-valued results are formatted with an explicit plus sign when nonnegative. A missing target object is ignored. | `Reduced Threat|TftYP` applies `-2` to every save and skill value. |
| `scalarMultProp` | Multiplies the selected property (or every property for `"*"`), optionally floors, and preserves string-vs-number output formatting. Missing target objects are ignored. | `Reduced Threat|TftYP` halves and floors `hp.average`. |
| `scalarMultXp` | Scales existing `cr.xp`, or derives XP from CR if absent; optionally floors. When `cr` is scalar, converts it to `{cr: ..., xp: ...}`. | `Reduced Threat|TftYP` multiplies XP by 0.5 and floors. |
| `scalarAddHit` | Recursively walks the target content and adds `scalar` to every `{@hit signedInteger}` tag. Other text is untouched. | `Reduced Threat|TftYP` applies `-2` across actions, reactions, legendary actions, traits, variants, and spellcasting. |
| `scalarAddDc` | Recursively walks target content and adds `scalar` to `{@dc integer}` tags. The implementation's regex accepts optional pipe metadata but rebuilds the tag as `{@dc n}`, dropping that metadata if present. | `Reduced Threat|TftYP` applies `-2` across the same rules-content targets as `scalarAddHit`. |

The new arithmetic and tag operations are material gameplay transformations. They require focused regression cases for negative output, missing properties, nested entries, floor behavior, and inline-tag preservation. `scalarAddDc`'s treatment of pipe metadata should be tested against the source examples before porting because the upstream handler intentionally reconstructs the tag.

### Modifier ordering, normalization, and unsupported modes

An object instruction and an array of instructions are normalized to the same sequential list. For entry-list properties, the data can also encode a property object whose keys are modes. Multiple instructions for one target run in their source order after normalization. Template instructions are concatenated after child instructions for that target. Different targets are ordered with ordinary root properties first, then `_`, then `*`; this matters when a skill operation consumes a CR/ability field or a text replacement sees newly inserted content.

The local monster inventory's 14 modes plus its 42 referenced templates use 22 distinct modes, all dispatched by the pinned upstream implementation. Unknown modes throw an “Unhandled mode” error rather than being ignored. Other upstream handlers not observed in either source monsters or their referenced templates include `appendStr`, `replaceName`, `replaceOrAppendArr`, `calculateProp`, `addSaves`, `addAllSaves`, and `addAllSkills`; keep them unsupported until source data requires them, and fail with source path and mode name if encountered.

### Notable examples from the corpus

- `Knight of the Order`, `monsters/bestiary-cos.json`, `monster[40]`: copies Revenant|MM; provides a direct AC override; applies global text replacement; replaces the named Multiattack action; inserts Longsword at index 1. This demonstrates child root assignment plus ordered mods.
- `Cat Skeleton`, `monsters/bestiary-cos.json`, `monster[17]`: copies Skeleton|MM and references the `Skeleton|DMG` template; its `setProp` writes null to defense/language properties. This distinguishes literal null from inherited-value deletion.
- `Skaab`, `monsters/bestiary-ps-i.json`, `monster[13]`: copies Flesh Golem|MM with case-insensitive text replacement and then defines four versions. It is the representative chain for copy-before-version expansion.
- `Draconic Spirit`, `monsters/bestiary-xphb.json`, `monster[8]`: abstract version name contains `{{resist}}`; five implementation variable maps parameterize it.

## Template findings

### Source revision and local asset

The upstream release page identifies `v2.37.0` at commit `8c026b807fac21862a309379a0c3228a0683198b`; the original `data/bestiary/template.json` from that commit is vendored at `src/main/resources/data/monsters/template.json`. Its SHA-256 is `F3675FA8AB755D520A4A22D7837B84B474E0E50CACD1294DB50F6FC3FFAE2902`. It contains 66 `monsterTemplate` definitions and retains the original `_meta.internalCopies` marker.

The 4,560 local monster records and `coverage-manifest.json` have no upstream source revision marker. The pinned asset is demonstrably compatible with the local references because every normalized `(name, source)` pair resolves once, but an exact historical source/template pairing cannot be established. Record the chosen pairing in future import metadata and rerun the inventory when either side changes.

### Reference and structural validation

The local scan traverses all 111 manifest monster files and finds 187 `_templates` reference occurrences across 42 distinct normalized keys. Against the pinned asset: 42 resolved, 0 missing, 0 ambiguous. The audit covers reference-to-definition lookup and template internal dependencies; it does not perform monster copy transformations.

The 42 referenced definitions have 152 effective `_mod` instructions and 1,044 template-modifier applications when weighted by the number of monsters referencing each template. Definitions expose `apply._root` and/or `apply._mod`; additional keys across the referenced definitions include `name`, `source`, `page`, `ref`, `alias`, `prerequisite`, `crMin`, and `_copy`. Root properties applied by these definitions include `type`, `size`, `speed`, `int`, and `legendaryGroup`. Root objects are template values, not new monster records.

`Aarakocra Spelljammer` (`monsters/bestiary-lox.json`, `monster[0]`) copies `Mage|MM` and references `Aarakocra|DMG`. That template supplies root `type`, `size`, and `speed`; its `type` value takes precedence over the inherited Mage `type` because the child did not explicitly provide `type`.

Three referenced template definitions have their own `_copy` instructions:

| Child template | Parent template | Internal operation |
| --- | --- | --- |
| `Hill Dwarf|PHB` | `Mountain Dwarf|PHB` | `setProp` changes `apply._root.type` to the Hill Dwarf tag. |
| `Shield Dwarf|PHB` | `Mountain Dwarf|PHB` | `setProp` changes `apply._root.type` to the Shield Dwarf tag. |
| `Strongheart Halfling|PHB` | `Stout Halfling|PHB` | `setProp` changes `apply._root.type` to the Strongheart tag. |

Upstream `DataUtil._pDoMetaMerge` sees `_meta.internalCopies: ["monsterTemplate"]` and runs the `monsterTemplate` copy strategy over definitions inside the template asset before `getCopy` applies a referenced template to a monster. Therefore template resolution is recursive in two distinct stages: internal template-to-template copies are resolved while loading the asset; then monster `_copy._templates` choose the already-resolved template definition. The internal copy instructions execute `setProp` three times and introduce no new operation name. This stage must be represented in the Java pipeline; simply reading `apply` from each raw definition would leave the three derived templates without their inherited `apply` content.

For a monster copy, upstream matches each requested template by case-insensitive, trimmed name and source. Missing keys are accumulated and cause the copy to fail. It normalizes each template `apply._mod`; for matching targets it concatenates child mods first and template mods afterward. After shallow parent inheritance, template `apply._root` values are assigned only for root keys absent on the original child; those values overwrite inherited parent roots, while explicit child roots block the assignment. Finally all combined mods are executed. `apply` is the runtime operational content; `page`, `ref`, aliases, and prerequisites are template identity/display/source context. Keep the raw asset as an internal resolver dependency. A separate public Monster DTE hierarchy is not required for templates; an internal validated template representation or `JsonNode` working view is sufficient, provided unknown operational keys/modes fail and the original vendored asset remains unchanged.

The existing [`MonsterCopyModifierDte`](../../src/main/java/com/thedndpub/data/dte/monster/MonsterCopyModifierDte.java) has payload fields for the monster-defined modes but does not currently model several template payload shapes, including `max`, `scalar`, `floor`, `prefix`, `suffix`, and `senses`. Feeding the template asset through that deserializer would therefore lose or reject needed fields. Keep template parsing as a separate resolver-internal concern, or deliberately extend reusable modifier payload records when implementation begins; no new public `MonsterDte` field or template collection mapping is required.

The asset and complete per-template reference inventory are reproducible with `node analysis/monsters/analyze_template_dependencies.mjs`; see [`template-inventory.json`](template-inventory.json) for each referencing monster, every resolved definition, its effective parent, modifier targets/modes, and any validation issue.

## Version expansion rules

The upstream version expander treats versions as instructions to create independent entities, rather than mutating the parent entity in place.

### Direct versions

For an ordinary `_versions` item, upstream deep-clones the item and rewrites its `_mod`, `_templates`, and `_preserve` into `_copy` instructions. If the version omitted `_preserve`, it sets `_preserve: {"*": true}`. It then copies from a clone of the base monster with `_versions` and token/fluff flags removed. So direct version fields override base roots; missing roots inherit; explicit null deletes inherited roots; version templates and modifiers execute through the same pipeline. The version's own name/source identify the generated record. A direct version is based on the containing monster, not an arbitrary `_copy` named by the version.

Because `_pMergeCopy` resolves a source monster's `_copy` recursively first, the effective base is the resolved containing monster. `Skaab` therefore resolves Flesh Golem → Skaab → each Skaab version.

### Abstract versions and implementations

If a version element has `_abstract` and `_implementations`, upstream deep-clones the abstract object, then produces one candidate per implementation. It substitutes `{{key}}` placeholders in every string in the abstract clone from that implementation's `_variables`, removes the implementation `_variables`, and overlays the implementation object onto the abstract object (`Object.assign`, so implementation root properties win). It then sends the resulting candidate through the same version-to-copy conversion and `_getVersion` flow. The abstract copy receives the wildcard preserve default unless explicitly set. Variable substitution here is a simple placeholder replacement in strings, not full template-language evaluation; unrelated 5eTools `$...$` computed tags have separate rendering semantics and are not resolved by this step.

The current dataset's example is Draconic Spirit: abstract name `Draconic Spirit ({{resist}})` and five implementations for Acid, Cold, Fire, Lightning, and Poison. The expected outputs are five separately named variants. The input representation and implementation variables should remain in provenance even after expansion.

### Version identity and metadata

Upstream keeps `_versionBase_*` provenance on expanded page entities and uses version name/source for the visible entity identity/hash. The local dataset has 286 explicit version name/source pairs and one abstract family with five generated names. The previous local analysis found no collision between the generated `(name, source)` pairs and canonical records under the observed substitution. It also found 714 case-insensitive repeated-name groups among source monsters but no duplicate `(name, source)` source keys.

For application identity, use a canonical source-record key plus a separate generated-variant key. Recommended fields: original source record ID (including source file and array index for stable provenance), canonical name/source, display name/source, containing base ID, version definition index or explicit version name/source, implementation index and canonicalized variables when abstract, and resolved copy-parent ID when the base is a copy. Avoid changing canonical names to manufacture uniqueness. Treat any generated key collision as a validation error; do not select an identity by iteration order.

## Graph results and catalog size

| Metric | Result |
| --- | ---: |
| Source records in `monster` | 4,560 |
| Files | 111 |
| Records with `_copy` | 1,144 |
| Records with `_versions` | 85 |
| Records with both | 1 (`Skaab`, PSI) |
| Version array elements | 287 |
| Concrete outputs represented by those elements | 291 |
| New independent outputs | 291 |
| Estimated expanded catalog | 4,851 |
| Copy graph roots | 3,416 |
| Depth-one copied records | 1,131 |
| Depth-two copied records | 13 |
| Maximum copy depth | 2 |
| Missing / ambiguous copy parent refs | 0 / 0 |
| Cycles | 0 |
| Duplicate case-insensitive source `(name, source)` identities | 0 |
| Template references / distinct pairs | 187 / 42 |

Copy records are not an extra 1,144 additions; they are already in the 4,560. Only expanded version entities raise the source-record count to 4,851. Templates augment a target record and do not generate standalone monsters. No external/unavailable `_copy` parent reference occurs in the local graph. All 187 template references are under monster `_copy._templates`; no version-level template references occur. Their 42 distinct pairs resolve against the pinned local template asset.

## Recommended Java architecture

The proposed pipeline is sound with two refinements: make template resolution an explicit dependency of the copy resolver, and carry provenance alongside every normalized result.

```text
MonsterDte (immutable source DTE)
  -> MonsterCopyResolver (copy graph + templates + ordered modifiers)
  -> MonsterVersionExpander (copies resolved base; emits independent variants)
  -> ResolvedMonster (typed normalized record + provenance)
  -> MonsterMapper
  -> MonsterDto
```

`MonsterDte` should preserve the source instructions untouched. A temporary Jackson `ObjectNode` working copy is appropriate inside resolution: upstream rules are defined as JSON root/path operations, and a mutable tree distinguishes missing from explicit null while supporting array splices and recursive text walks. Never mutate a deserialized DTE or source node. Convert the resolved tree into a strict normalized typed record immediately after resolution; reject leftover `_copy`, `_templates`, `_mod`, or `_versions` fields there. `ObjectNode` is an internal transformation substrate, not a field in the final DTE or DTO.

Recommended boundaries:

1. `MonsterIdentity` and `SourceLocation`: canonical `(source, name)` plus file/index provenance; normalize comparisons only according to upstream hash rules and retain original casing.
2. `MonsterCopyGraph`: index records, validate exactly one parent, detect cycles, and produce topological order before applying mutations.
3. `MonsterTemplateRepository`: load a versioned `monsterTemplate` asset keyed by `(name, source)`; fail with the requesting entity and `_copy._templates[i]` path on missing/ambiguous keys.
4. `MonsterCopyResolver` / `MonsterCopyApplier`: clone, recurse, apply root merge, template root operations, and mode handlers in the order above. Give every handler strict shape validation, path-aware exceptions, and no catch-and-ignore fallback.
5. `MonsterVersionExpander`: expand only after resolving the containing base; preserve raw definition/implementation provenance and construct a stable variant key.
6. `ResolvedMonster` and `MonsterMapper`: represent normalized complete game-facing content without source instructions; map to DTOs after structural validation.

Error objects should include source filename, entity name/source, source array index, JSON path, parent/template/version identity, mode name, and the reason (missing target, malformed payload, unknown mode, collision). Preserve a causal Jackson/operation exception.

Deterministic validation should pin the upstream code/data revision and include: exact inventory snapshot counts; all 1,144 copy edges; all 42 template keys and three internal template-copy edges; all 22 modes across monster and effective template instructions; ordered multi-mod targets; explicit-null versus absent cases; shallow nested replacement; forced and non-forced missing array targets; duplicate names selecting the first; regex/tag behavior; skill arithmetic and lower-existing-skill case; all spell keys including `1e`; the known `removeSpells` constant/will/ritual behavior; direct and abstract versions; Skaab copy-before-version; collision and cycle rejection; immutable-source assertion; and stable sorted normalized output for all 4,560 originals plus 291 variants.

## Representative fixture plan from this dataset

| Fixture | Behavior to assert |
| --- | --- |
| Knight of the Order (`bestiary-cos.json`, monster[40]) | Direct AC wins over Revenant; regex replacement and action replace/insert preserve operation order and action positions. |
| Aarakocra Spelljammer (`bestiary-lox.json`, monster[0]) | `Aarakocra|DMG` template root `type` replaces the inherited Mage `type`; a second focused case should prove an explicit child `type` blocks template root assignment. |
| Cat Skeleton (`bestiary-cos.json`, monster[17]) | Skeleton parent + Skeleton template; template fills eligible root values; `setProp` null remains null after modification. |
| Bitter Breath (`bestiary-bgdia.json`) | `replaceTxt` regex flags, nested strings, and inline-tag protection. |
| Battle Familiar (`bestiary-au.json`, monster[2] and its versions) | Rename/remove actions and traits; version operations apply after base resolution. |
| Sylvira Savikas (`bestiary-bgdia.json`, monster[45]) | Tiefling template and `addSkills` computed from CR/ability; prove a better existing skill is retained. |
| Zikran (`bestiary-cm.json`, monster[40]) | `addSpells` appends/sorts within exact usage categories. |
| Traxigor (`bestiary-bgdia.json`) | Exact spell replacement and missing-target failure. |
| Jijibisha Manivarshi (`bestiary-jttrc.json`) | Daily spell-use removal for keys `3e`/`1e`; separate fixture for upstream constant/will/ritual implementation behavior. |
| Draconic Spirit (`bestiary-xphb.json`, monster[8]) | Five abstract implementations, variable substitution in names/content, and unique variant keys. |
| Skaab (`bestiary-ps-i.json`, monster[13]) | Resolve Flesh Golem parent and child mods before expanding all four versions. |

## Remaining unknowns and compatibility cautions

- The monster dataset itself supplies no upstream revision. Commit `8c026b807fac21862a309379a0c3228a0683198b` is immutable and all template references resolve against it, but it cannot be proven to be the exact template release paired with the source monsters.
- A pinned asset inventory verifies every input key and exposed modifier. A resolver implementation still needs executable parity tests for the 22 modes and the three internal template-copy `setProp` operations.
- Upstream's text replacement regex construction can throw for malformed regex syntax. The local inventory is shape-complete, but a compatibility suite should exercise every pattern/flag and preserve tag boundaries.
- Some handler behavior is utility-dependent (dotted paths, deep equality, regex capture replacement, negative splice indexing). Port the observed behavior or explicitly document an intentional deviation.
- The inspected `removeSpells` path appears to have a no-op bug for `constant`, `will`, and `ritual`; copying it exactly preserves upstream compatibility but may preserve a data-authoring bug. Decide this explicitly and test it.
- Upstream's `addSpells`/`replaceSpells` support is shape-specific and the source itself marks limits for some spellcasting forms. The Java implementation should reject unsupported shapes with exact paths until examples and upstream behavior establish the desired rule.
- The corpus contains no missing/ambiguous monster parents and no cycles, and its 42 template keys are unique in the selected asset; future data can change either condition. Never turn validation into arbitrary “first match” resolution.
- The 4,851 estimate is structural expansion count. It does not establish successful game-facing mapping until every modifier output is validated against expected normalized forms.

## Readiness for resolver implementation

Template dependency analysis is complete for the selected immutable asset: 187/187 references resolve, the 42 distinct keys are unique, all three internal template copy parents resolve, and all observed monster/template modifier instructions are classified. There is no missing template or unknown modifier mode blocking implementation against this pinned pair.

Before normalized output can be accepted, the resolver must parse the template payload fields listed above; the current modifier DTE does not include them. Two compatibility decisions also need recording: the local monster dataset has no upstream revision, so the v2.37.0 template pairing is evidence-based rather than historically proven; and `removeSpells` has an upstream no-op for `constant`, `will`, and `ritual` that must be followed or deliberately corrected. These do not block starting implementation against the pinned pair, but they must be covered in fixtures and documented before outputs are accepted.
