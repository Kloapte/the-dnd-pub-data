# Monster copy resolver implementation

## Scope and compatibility reference

The resolution pipeline first resolves each source monster's `_copy` inheritance, template roots, and copy/template modifiers, then expands source `_versions` into independent catalog entries. It does not normalize content into DTOs.

The resolver targets upstream 5eTools revision `8c026b807fac21862a309379a0c3228a0683198b` (v2.37.0), as documented in [COPY-RESOLUTION-SPEC.md](COPY-RESOLUTION-SPEC.md). The local monster source does not identify an upstream revision. The pinned template asset was validated against all local template references and retains its original JSON.

## Implementation structure

- `MonsterIdentity` is the normalized `(name, source)` key used for parent and template lookup. Original spelling and source remain in the source JSON content.
- `MonsterSourceLocation` records the file and zero-based collection index.
- `MonsterSourceRecord` couples the already typed `MonsterDte` with a defensive copy of the complete original JSON object. The tree is required because source properties outside the current DTE projection must survive resolution.
- `MonsterCopyGraph` indexes the complete input before resolving any entity and rejects duplicate identities, missing parents, and copy cycles. Parent copies resolve recursively and their resolved content is cached.
- `MonsterTemplateRepository` loads the pinned `template.json`, resolves the three metadata internal-copy links, validates definition/apply fields, and returns defensive copies. It is immutable after construction.
- `DefaultMonsterCopyResolver` performs shallow root inheritance, explicit-null deletion, preserve-gated metadata handling, template root precedence, then modifier execution. Working parent and child nodes are always independent.
- `ResolvedMonster` holds the identity, source location, parent/template provenance, resolved content, and executed mode names. The consumed `_copy` instructions are retained in provenance, not resolved content.
- `MonsterVersionExpander` accepts only copy-resolved monsters. It preserves every original identity, moves original `_versions` arrays into `ResolutionProvenance.sourceVersionDefinitions`, and creates generated definitions with identity, location, and ancestry in `MonsterVersionProvenance`.
- Each direct version becomes an independent synthetic copy against the already resolved containing monster. The expander delegates shallow root inheritance, template roots, and modifier execution to `MonsterCopyResolver`, so the existing mode handlers remain the single implementation.
- Abstract definitions are cloned for each implementation. String values throughout the abstract JSON tree are substituted from that implementation's `_variables`, then implementation root fields are shallow-assigned over the abstract fields. An explicit variables object makes unresolved `{{...}}` placeholders an error; without a variables object, literal placeholders remain unchanged.
- Generated identities use their declared `(name, source)` and collide with neither source records nor other generated records. Provenance keeps the containing monster, source filename and index, version and implementation indexes, variable values, source definition, copy ancestry, template ancestry, and executed mode names.
- Output order is deterministic: original records by canonical identity/location, then generated variants by containing identity and definition/implementation index. Source DTEs, resolved bases, and cached templates are not mutated.

## Modifier handling

The resolver dispatches the active copy/template instructions through mode-specific handlers. The full corpus exercises the active modes `replaceTxt`, `replaceArr`, `appendArr`, `removeArr`, `prependArr`, `addSpells`, `replaceSpells`, `addSkills`, scalar `remove`, `appendIfNotExistsArr`, `insertArr`, `removeSpells`, `setProp`, `addSenses`, `maxSize`, `prefixSuffixStringProp`, `scalarAddDc`, `scalarAddHit`, `scalarAddProp`, `scalarMultProp`, and `scalarMultXp`. `renameArr` occurs only in `_versions` instructions in this corpus and has a separate synthetic copy test. Together these cover the 22 inventory labels in the compatibility report (`removeProperty` is implemented as the scalar `remove` operation).

Modifier targets are processed deterministically: ordinary root properties in name order, then `_`, then `*`; instructions for each target retain child-before-template order and their source ordering. `*` visits the pinned entry-target property set. Text replacement uses Java regular expressions, case/multiline/dotall flags, JavaScript replacement tokens, recursive entry text, and inline-tag protection unless `tagInsensitive` is set. Array operations preserve first-match and source ordering behavior. Spell lists retain distinct usage keys such as `1` and `1e`.

The known pinned upstream `removeSpells` bug is preserved: removals from `will`, `constant`, and `ritual` do not take effect because upstream discards the filtered arrays. A focused regression test covers this behavior. Daily-use removal is tested as an effective removal.

Unknown modifier modes, instruction properties, template definition properties, malformed template `_copy` references, and missing targets required by a handler raise `MonsterResolutionException` with monster identity, source location, and modifier path.

## Version expansion

The expander follows pinned v2.37.0 behavior: version `_mod` and `_templates` are passed through as copy instructions, and `_preserve` defaults to `{ "*": true }` when absent. Explicit version root values override inherited root values; explicit JSON null deletes inherited properties. Version modifiers use the existing copy/template handlers and ordering.

The synthetic base clone drops `_versions`, `hasToken`, `hasFluff`, and `hasFluffImages`. It also filters inherited `additionalSources`, `otherSources`, and `referenceSources` entries whose object `source` equals the generated version source; other entries, including non-object metadata, remain. Final content is checked for active `_copy`, `_versions`, `_mod`, `_templates`, and `_preserve` instructions. Original version definitions remain available in source provenance.

The upstream internal `_versionBase_*` annotations are represented through typed provenance instead of adding internal properties to game-facing content. Ancestry information is retained, while content stays free of those internal markers.

## Validation

`MonsterCopyResolverTests.loadsAndResolvesCompleteMonsterCorpus` discovers the bestiary files from the resource directory and validates all 4,560 monsters in 111 source files. It resolves twice and compares output deterministically. The test confirms:

- 4,560 resolved original records, all with unique canonical identities;
- 1,144 copy edges resolved, no remaining active `_copy` instructions;
- 187 template reference occurrences applied through 66 indexed template definitions;
- all active observed mode names executed; `renameArr` is covered with a focused fixture because its source occurrence is version-only;
- source objects remain unchanged, and `Skaab` retains but does not expand `_versions`;
- representative copy/template outcomes for Cat Skeleton, Knight of the Order, Zikran, and Aarakocra Spelljammer.

`MonsterVersionExpanderTests` additionally covers direct overrides, explicit null, ordered modifiers, version templates, abstract substitutions and implementation precedence, malformed definitions, identity collisions, immutability, deterministic output, and the full dataset.

The full corpus expands to **4,851 entries**: 4,560 original definitions, 85 originals with versions, 287 source version definition objects, and 291 generated variants (the abstract family expands to five). All identities are unique and no final content retains active copy/version/modifier/template instructions. Integration assertions cover Skaab, Battle Familiar, and all five XPHB Draconic Spirit forms, including action and resistance text; the test also confirms every mode in generated version instructions was executed.

The latest targeted version-expander run passed all 4 tests, including full-corpus validation. The previous full Maven suite had one unrelated Spring application-context failure because `MonsterController` references the absent `com.thedndpub.data.services.MonstersService`; data import and resolver tests run independently of Spring.

## Boundaries for the next milestone

The version expander does not normalize gameplay content into `MonsterDto`, add REST endpoints, add persistence, or implement executable gameplay mechanics. `appliedModifierModes` is a set of mode names, so it records coverage rather than instruction sequence; ordering is verified through the resulting content fields and the shared ordered dispatcher.

The resolver does not implement gameplay calculations. It only applies source instructions, including CR-derived proficiency in `addSkills` and scalar adjustments recorded by templates.
