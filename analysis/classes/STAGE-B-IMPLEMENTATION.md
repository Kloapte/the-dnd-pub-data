# Classes and subclasses: Stage B implementation

Status: the internal pipeline is implemented. Stage C public DTOs, catalogs/services, and REST routes are implemented in [STAGE-C-IMPLEMENTATION.md](STAGE-C-IMPLEMENTATION.md).

## Production components

| Package / component | Responsibility |
| --- | --- |
| `dte.classes` | Strict typed source roots and owned structures, class-specific sealed rich entries, polymorphic scalar/object/array deserializers, defensive collection utilities |
| `ClassesSourceLoader` | Recursive packaged classpath resource discovery, strict parsing, source locations, root field/null presence and immutable source documents |
| `ClassCopyResolver` | Complete source identity indexing, dependency resolution, class-specific metadata defaults and immutable effective records/provenance |
| `ClassIdentities` | Complete identity tuples, source UID grammars/defaults and stable readable IDs |
| `ClassReferenceResolver` | Exact parent/feature/reprint indexes, authored/effective reference validation, cycle and orphan checks |
| `ClassSourceValidator` | Shared Source Registry validation, explicit UID/markup/filter source slots, unresolved placeholder rejection |
| `ClassMechanicsNormalizer` | Hit-dice/proficiency/requirement/equipment/progression/table value conversion without character calculations |
| `ClassSpellcastingNormalizer` | Caster metadata, progressions, formulas, typed gates/usages, spell references and selector clauses |
| `ClassFeatureContentNormalizer` | Internal typed descriptions, feature references, options, required/external references, tables, ability declarations and statblocks |
| `ClassExternalReferenceResolver` | Exact bindings to existing Items, Optional Features, Feats and Monsters; typed deferred groups and unqualified identities |
| `ClassDefinitionNormalizer` | Stateless validation/copy/graph/normalization orchestration returning an immutable import result |
| `dto.classes` | Internal immutable definitions, IDs, source relationships, choices, spell/progression values, content graphs, provenance, validation and file metadata |

These are ordinary explicit import objects, not new Spring catalog/service components. `ClassNormalizationResult` returns the four definition lists, effective graph, external occurrences, validation summary and all 13 file metadata records. It owns no mutable cache or persistence. Stage C can load it once and build immutable query indexes later.

The production entry point is explicit:

```java
ClassSourceCorpus source = new ClassesSourceLoader(objectMapper).load();
ClassExternalReferenceResolver references = ClassExternalReferenceResolver.fromExistingCatalogs(
        objectMapper,
        sourceRegistry,
        itemCatalog,
        optionalFeatureCatalog,
        featCatalog,
        monsterCatalog);
ClassNormalizationResult result = new ClassDefinitionNormalizer(sourceRegistry, references).normalize(source);
```

## Actual counts and graph validation

| Item | Implemented result |
| --- | ---: |
| Resource files | 15 |
| Class definitions | 30 |
| Subclass definitions | 330 |
| Class feature definitions | 677 |
| Subclass feature definitions | 1,499 |
| Total independently addressable definitions | 2,536 |
| Auxiliary file metadata objects / internal-copy values | 13 / 18 |
| Subclass copies / subclass feature copies | 124 / 75 |
| Additional version/template definitions | 0 |
| Authored structural feature reference occurrences | 2,339, all resolved |
| Effective reference occurrences after copy inheritance | 2,725, all resolved |
| Unresolved/ambiguous own feature or parent references | 0 |
| Orphan feature definitions / dependency cycles | 0 / 0 |
| Duplicate full identities / readable IDs | 0 / 0 |
| Inadequate name/source identity collision groups | 296 |
| Registered source identities used | 26, including 18 primary identities |

The additional 386 effective edges are inherited occurrences, not extra feature definitions. Authored edges are validated independently before counting effective relationships. The same feature retains one definition/ID when referenced from several owners or choice groups.

## DTE coverage and source fidelity

The source layer covers all **729 Stage A property paths**. The full-corpus test independently reconstructs each original JSON tree from the typed DTEs and asserts complete equality for all 15 files, including metadata, arrays, choice shapes, rich text, source flags, tables and copy instructions. This checks values and structure rather than relying solely on record counts.

String-versus-object source forms are explicitly retained for proficiency entries, feature unlocks and equipment entries, including an item-only object versus a bare item UID. Dense and sparse progressions, direct spell arrays and usage objects, string filters and explicit spell choices remain distinct. Source records retain absent lists instead of replacing them with empty lists before copy application. Root field presence separately tracks explicit nulls; null/false/empty override behavior is tested.

Unknown fields, unsupported entry/cell types, invalid equipment branch keys, unsupported transformations, malformed UIDs, scalar coercion, fractional integers, duplicate JSON properties, trailing JSON documents and non-object source roots are rejected. Trees exist only inside parsing boundaries, not model components. Known models use typed records/lists/maps rather than `Object`, generic JSON nodes or `Map<String,Object>`.

Class-specific entries implement the observed forms without changing the shared entry hierarchy. Tests demonstrate the shared entry consumer's previous behavior and class-specific extensions separately; the existing shared-entry and catalog regression suites remain applicable.

## Copy semantics and provenance

The normative implemented behavior is in [COPY-RESOLUTION-SPEC.md](COPY-RESOLUTION-SPEC.md). Bases resolve before descendants, outer properties override atomically, absent ordinary fields inherit, and protected publication/source/edition/media metadata requires an observed preservation flag. Parent source/level changes remain concrete destination identity, while borrowed UIDs are not rewritten.

Stage B corrected the Stage A prose omission concerning six feature `srd` and three `basicRules` preservation flags; all were already visible in the field inventory and are now mapped and tested. No version/template structure was discovered during implementation.

Every definition retains separate typed authored/effective source material, location, copy lineage, inherited fields and suppressed metadata. Source records are not mutated. Raw descriptions and import instructions remain internal and do not create a public response contract.

## Progression and spellcasting

Feature lists keep exact level and source order, occurrence flags and target identity. Numeric progressions become explicit level/count values; sparse milestones are not expanded or summed. Optional Feature and Feat category progression remain distinct; required references bind to existing definitions. Saving throws, primary ability groups, starting/multiclass proficiency packages and grouped AND/OR prerequisites remain separate facts.

All owned source table groups retain title, column identity, row association, subclass selectors and typed cells. Numbers, strings, bonuses, dice and speed bonuses remain different. Feature roll/presentation table rows have no invented class-level association. Slot matrices are distinguished by their source field; Pact Magic retains its ordinary table representation. No proficiency bonus, resource pool, slot, recharge, level or multiclass calculation is performed.

Spellcasting retains `full`, `1/2`, `artificer`, `pact` and subclass `1/3` source codes without class-name inference. Cantrip/known/prepared/fixed progressions, fixed-by-level grants, preparation timing and validated symbolic preparation expressions remain distinct. PHB/XPHB facts are never merged.

All **1,068 direct authored structured spell UIDs** are retained: 446 qualified and 622 unqualified. Stage B additionally covers **three explicitly enumerated candidates** inside FRHoF Scion of the Three's `choose.from`, which the Stage A reference counter excluded. Thus the complete authored spell UID count including explicit choice candidates is **1,071** (449 qualified, 622 unqualified). No Spell Catalog IDs or unqualified sources are invented.

Typed grant categories, class/spell-level gates, unspecified/daily/rest/resource/ritual usage, numeric/ability/per-spell count tokens, choose/all selectors, unrestricted empty filters and configuration suffixes are preserved. Ritual/focus/recharge rules that occur only as descriptive entries remain typed internal text; absence of a structured field does not imply false.

## Starting options and external references

Equipment retains independent groups, unconditional bundles, lettered alternatives, explicit quantities, fixed copper values, category alternatives and unbound special names. Legacy display/rolled-gold text remains internal source material and is not evaluated. `equipmentTypes` is supported explicitly rather than forced through Background equipment records.

The three XPHB starting-equipment references to `holy symbol` (two) and `druidic focus` (one) become `ITEM_GROUP` references with `DEFERRED_ITEM_GROUP`, original source spelling and no concrete Item ID. Group membership is discovered from the existing typed Item source, not hardcoded names. Concrete item references bind to existing Item Catalog IDs.

Optional Feature, Feat and named item/monster statblock references bind by exact source identity to existing catalogs. Unqualified skill choices retain names/counts without a selected source or fabricated ID. Spell references remain `DEFERRED_SPELL`. Descriptive markup is retained and its sources are validated; it does not become a grant or replacement effect.

All 26 source identities resolve through the shared Source Registry. `UATheMysticClass` remains its existing identity-only registration; no Stage B source registry/publication changes were required.

## Tests and verification

Dedicated classes cover deserialization/source reconstruction, copy resolution, feature graph identities/failures, spellcasting structures, full normalization/mechanics and packaged resource loading. Tests use the complete real corpus plus focused synthetic cases for metadata overrides, unknown shapes/sources, missing/ambiguous targets, ID collisions, cycles, invalid paths/operations, choices, false/zero values and immutability.

Packaged tests load all 15 resources from an isolated JAR, discover an additional nested fixture without a manifest, and reject duplicate classpath resources or no resources. Resource discovery and input connections disable URL/JAR caches so temporary or replaced JARs are not left locked on Windows. Normalization equality is checked across repeated runs and reversed input document order.

Final verification: **`mvn test` — 241 tests, zero failures/errors/skips, BUILD SUCCESS**, including 65 dedicated Stage B tests and all 176 baseline tests. No existing API contract or shared entry implementation was changed. See [VERIFICATION.md](VERIFICATION.md).

| Dedicated test class | Tests |
| --- | ---: |
| `ClassesDeserializationTests` | 11 |
| `ClassCopyResolverTests` | 8 |
| `ClassFeatureGraphTests` | 9 |
| `ClassesNormalizationTests` | 23 |
| `ClassSpellcastingTests` | 11 |
| `ClassesResourceLoadingTests` | 3 |

## Stage C boundary

The immutable public catalogs, query services, summary/detail projections, serialization tests, and routes are implemented as documented in [STAGE-C-IMPLEMENTATION.md](STAGE-C-IMPLEMENTATION.md). The source-derived API design remains available in [DTO-API-DESIGN.md](DTO-API-DESIGN.md).

Keep provenance and descriptive rules/markup out of public JSON. Preserve the deferred item-group and spell references until their catalogs support binding. Presentation strings, prose-only replacement rules, resource recharge, sidekick specializations and spell-focus rules must not be turned into calculated mechanics. Metadata inheritance follows the documented application policy rather than claiming undocumented upstream defaults. No character creation state, automatic feature application, rules engine or persistence is included.
