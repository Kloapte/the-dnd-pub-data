# Classes and subclasses: Stage C implementation

Status: public read APIs and immutable catalogs are implemented over the Stage B normalized model. No character state, mechanics evaluator, persistence layer, or spell catalog was added.

## Runtime architecture

`ClassDomainSnapshot` invokes the existing `ClassesSourceLoader`, `ClassDefinitionNormalizer`, and `ClassExternalReferenceResolver` once during Spring startup. `ClassDomainIndex` validates source identities, the four expected definition counts, parent edges, root feature references, and every effective graph edge. The four catalog beans are immutable views over that single index; they do not independently normalize source data.

| Catalog | Definitions |
| --- | ---: |
| `ClassCatalog` | 30 |
| `SubclassCatalog` | 330 |
| `ClassFeatureCatalog` | 677 |
| `SubclassFeatureCatalog` | 1,499 |
| Total | 2,536 |

All IDs are the Stage B source-aware IDs. Startup rejects duplicate IDs and broken parent/feature references. Catalog reads are deterministic and collections are immutable. Source references are checked through the shared Source Registry during normalization and catalog index construction.

## Public DTO projection

`ClassPublicMapper` maps Stage B definitions to the eight requested DTOs: `ClassDto`, `ClassSummaryDto`, `SubclassDto`, `SubclassSummaryDto`, `ClassFeatureDto`, `ClassFeatureSummaryDto`, `SubclassFeatureDto`, and `SubclassFeatureSummaryDto`. Public records use `NON_EMPTY`, defensively copy collections, and retain boxed booleans and numeric zero values.

Class and subclass DTOs expose parent/source identities, edition labels where authored, source relationships, starting and multiclass options, lightweight feature references, structured choices, spellcasting metadata, and normalized progressions. Classes contain subclass IDs rather than nested subclass definitions. Subclasses retain their exact Stage B parent class ID. Feature DTOs are independently addressable and expose parent IDs, level, direct progression order when that order is explicitly present, variant classification, consumption metadata, and typed feature/external references and choice candidates. No replacement target is exposed: Stage A found variant flags but no explicit replacement-target relationships.

The public projection omits normalized descriptive entry trees, import provenance, copy instructions, availability flags, raw media paths, and feature presentation content. Class-owned table rows preserve level associations and non-presentation cell kinds (`NUMBER`, `BONUS`, `DICE`, `SPEED_BONUS`); `PRESENTATION_TEXT` cells are omitted. Markup-bearing column labels become positional `Column N` labels without publishing renderer instructions. The currently normalized feature-entry tables and prose are not projected because their headings/values are presentation content. This is a documented public projection limit; the source-normalized structures remain available internally. Proficiency prose restrictions are omitted while structured grants and optional flags remain. `false` and `0` are retained.

The DTOs reuse Stage B typed values for mechanics whose meaning is identical, including spellcasting, equipment groups, proficiency choices, and typed progression cells. They do not serialize Stage B definitions, provenance, or source entry nodes directly.

### Step 7 Batch 1: additive progression column positions

Progression rows now expose `positionedCells` alongside the unchanged compact `cells` array.
Each positioned value contains its original zero-based `columnIndex`, `kind`, and the same safe
`number`, `dice`, and `rollable` properties where present. Use `positionedCells` to associate values
with the existing `columns`; compact list indexes do not identify source columns when text was
withheld. This applies to Class and Subclass `tables`, and `spellcasting.slotTables` and
`spellcasting.pactTables`.

For PHB Barbarian's first row, the existing `cells` remains `[{"kind":"BONUS","number":2}]`;
the additive `positionedCells` is `[{"columnIndex":1,"kind":"BONUS","number":2}]`. The source
text at column zero remains withheld. Fully withheld and empty rows omit both empty arrays.
Index zero, numeric zero and Boolean false remain serialized. No route or summary contract changes.
Public Java carriers are now `ClassProgressionTableDto`, `ClassProgressionTableRowDto`, and
`ClassSpellcastingDto`; internal normalized table and spellcasting types are unchanged.

See [Batch 1 implementation](../mechanics/batch-1-class-table-positions/IMPLEMENTATION.md) for
the six affected roots, compatibility decision, corpus regression checks and verification.

## Graph and references

All 2,536 definitions are independently available by the same IDs used by owner references and feature-detail endpoints. The Stage B graph contains 2,725 effective feature-reference occurrences, all of which resolve. Direct class/subclass progression relationships retain source order, level, and occurrence flags. Some features are reached only through other features and therefore have no direct class/subclass progression order; their public `order` is omitted instead of invented.

Parent context preserves PHB/XPHB separation. Name lookup returns every exact case-insensitive match, optionally narrowed by source, and never chooses one ambiguous definition arbitrarily. List filters use case-insensitive name substring and exact case-insensitive source matching. Parent relationship filters require the complete class/subclass ID.

All 1,068 authored direct spell references and three explicit choice candidates are retained by Stage B. The public effective definition set includes repeated spell grants inherited by copy-derived subclass definitions; the API therefore contains 1,694 direct spell-reference occurrences across independently addressable definitions, plus the three explicit candidates. Unqualified spells remain unqualified; no Spell Catalog identity is guessed. The three authored item-group references remain typed `ITEM_GROUP` choices without fabricated concrete Item IDs.

## Routes

| Route | Response / filters |
| --- | --- |
| `GET /api/classes` | Class summaries; optional `name`, `source` |
| `GET /api/classes/{id}` | Class detail |
| `GET /api/classes/by-name/{name}?source=` | Exact-name class details; ambiguous matches are all returned |
| `GET /api/classes/{id}/subclasses` | Subclass summaries for the exact parent class |
| `GET /api/classes/{id}/features` | Ordered class feature summaries |
| `GET /api/subclasses` | Subclass summaries; optional `name`, `source`, `classId` |
| `GET /api/subclasses/{id}` | Subclass detail |
| `GET /api/subclasses/by-name/{name}?source=` | Exact-name subclass details; ambiguous matches are all returned |
| `GET /api/subclasses/{id}/features` | Ordered subclass feature summaries |
| `GET /api/class-features` | Class feature summaries; optional `name`, `source`, `classId`, `level` |
| `GET /api/class-features/{id}` | Class feature detail |
| `GET /api/class-features/by-name/{name}?source=` | Exact-name class feature details |
| `GET /api/subclass-features` | Subclass feature summaries; optional `name`, `source`, `classId`, `subclassId`, `level` |
| `GET /api/subclass-features/{id}` | Subclass feature detail |
| `GET /api/subclass-features/by-name/{name}?source=` | Exact-name subclass feature details |

Unknown detail IDs and empty by-name results return 404. Invalid numeric query parameters use Spring's 400 response. These fixed source catalogs return deterministic lists without paging, following the existing API conventions.

## Validation and remaining limits

Seven focused Stage C tests cover catalog counts, ID uniqueness, all 2,725 graph edges, parent and edition identity, filters and ambiguous names, progression ordering, complete public serialization checks, false/zero behavior, preserved spell/item-group references, route behavior, and missing-resource handling. Stage B's classpath JAR test continues to cover recursive packaged resource loading.

The public API does not publish descriptive feature rules, raw markup, or presentation-only table text. Feature replacement effects, character-level calculations, spell slot calculations, spell catalog binding, and concrete item-group expansion remain outside Stage C.

Full Maven verification: **`mvn test` — 248 tests, zero failures/errors/skips, BUILD SUCCESS**. This is the 241-test Stage B baseline plus seven dedicated Stage C public API tests.
