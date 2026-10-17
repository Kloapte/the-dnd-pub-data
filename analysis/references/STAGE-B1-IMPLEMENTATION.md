# Cross-Catalog Reference Resolver — Stage B1

## Implemented components

Internal implementation is under `com.thedndpub.data.references`:

- Typed target kinds, semantic forms, authored identities, parsed qualified identities, owners, and field locators.
- Typed outcomes: `RESOLVED`, `AMBIGUOUS`, `UNRESOLVED`, and `DEFERRED`; candidate cardinality is independent of outcome.
- `CatalogTargetAdapter`, immutable `AdapterRegistry`, deterministic indexed Spell and Item adapters, and `ReferenceResolver`.
- `SourceAuthoredReferenceExtractor` and Spring `CrossCatalogResolutionCoordinator`.
- Immutable `ResolutionSnapshot`, occurrence results, diagnostics, aggregate counts, and severity.

`ItemCatalog` now exposes an immutable list of its source-authored baseItem declarations. It returns stable owner IDs and original UIDs, not import provenance. Item lookup for equipment searches both Item and Base Item definitions and records the actual matched definition kind. A separate Base Item adapter handles explicit `baseItem` declarations. The resolver never reconstructs IDs.

The coordinator resolves once during Spring startup after its target catalogs and owner snapshots exist. Those catalogs do not depend on the coordinator, so the bean graph remains acyclic. No REST request path was changed.

## Resolution semantics

- Qualified definition + one exact catalog match → `RESOLVED`, with catalog-owned ID and actual target definition kind.
- Qualified definition + no exact match → `UNRESOLVED`.
- Unqualified definition + no candidates → `UNRESOLVED`.
- Unqualified definition + one candidate → `DEFERRED`, cardinality `ONE`, candidate ID retained.
- Unqualified definition + multiple candidates → `AMBIGUOUS`, with sorted candidate identities.
- Choice, filter, and group forms remain `DEFERRED`, even when they carry a source-qualified candidate.
- Unknown explicit sources and malformed UIDs fail initialization. Source-authored missing targets do not fail globally.

Aliases are validated and canonicalized through the shared Source Registry for lookup; original tokens and source codes remain in the result and diagnostic. Candidate order is stable-ID order. Diagnostics are internal only.

## Supported source-authored fields

| Owner and field | Target | Handling |
|---|---|---|
| Class/subclass `additionalSpells` direct selections | Spell | Qualified resolves; unqualified is ambiguous/deferred. |
| Class/subclass `additionalSpells` `choose.from` | Spell | Retained as `CHOICE`; never promoted to a grant. |
| Class/subclass `all` and spell filters | Spell | Retained as a deferred structured selector; not expanded. |
| Class `startingOptions.equipment` concrete UIDs | Item or Base Item | Exact lookup; alternatives retain separate locators. |
| Class equipment item groups | Item Group | Deferred; no concrete item is fabricated. |
| Background `startingEquipment` item UIDs | Item or Base Item | Exact lookup; missing targets remain diagnostics. |
| Item source-authored `baseItem` | Base Item | Exact lookup using an authored-field index; inherited copy duplicates excluded. |

Class spell extraction reads the authored typed root record rather than effective copied spellcasting. This preserves the source-authored occurrence boundary and avoids duplicating inherited declarations. Class feature edges remain with `ClassReferenceResolver`.

## Corpus results

The integration test validates **2,392 selected reference occurrences**:

| Owner field group | Resolved | Ambiguous | Unresolved | Deferred | Occurrences |
|---|---:|---:|---:|---:|---:|
| Class/subclass direct authored spell declarations | 446 | 620 | 0 | 2 | 1,068 |
| Explicit spell-choice candidates | 0 | 0 | 0 | 3 | 3 |
| Spell filters and `all` selectors | 0 | 0 | 0 | 186 | 186 |
| Background starting equipment | 718 | 0 | 8 | 0 | 726 |
| Class starting equipment | 165 | 0 | 0 | 3 | 168 |
| Item `baseItem` declarations | 241 | 0 | 0 | 0 | 241 |
| **Total** | **1,570** | **620** | **8** | **194** | **2,392** |

Counts are occurrences, not distinct authored tokens. Every result and diagnostic retains owner ID, stable field locator, authored token/source, outcome, cardinality, candidates, and reason/severity.

### Stage A count reconciliation

- Stage A's 169 class item references included 168 equipment UIDs plus one Psychic Blade feature statblock link. B1 implements the equipment field only: 165 exact concrete targets and three item groups. The separate statblock reference remains for later integration. These 165 equipment targets plus the statblock target reconcile Stage A's 166 exact concrete item matches.
- Background equipment agrees with its catalog statistics: 718 resolved and 8 unresolved across three UIDs. The eight remain warning diagnostics; no definitions are invented.
- The expanded Item Catalog contains 263 effective `baseItem` values due to inherited copies. B1 counts 241 source-authored declarations only.
- The class spell totals match the authored boundary: 446 source-qualified exact matches, 620 unqualified multi-candidate names, two unqualified unique-current-candidate names, three separate choice candidates, and 186 selectors.

Qualified class spell, class equipment, and Item `baseItem` references are required exact matches. Known Background misses are tolerated warnings. Choices, filters, groups, and unqualified references are informational deferrals or ambiguity diagnostics. All explicit sources are validated before target lookup.

## Tests and compatibility

`CrossCatalogResolutionTests` covers the full selected corpus counts, PHB/XPHB separation, unique-but-unqualified Branding Smite results, source-qualified resolution, ambiguity, missing targets, unknown sources, malformed UIDs, choice/filter/group preservation, Item/Base Item distinction, stable ordering, and snapshot immutability. The Spring context test verifies acyclic initialization. Existing classpath/JAR loader tests cover the target catalog resources.

No public DTO, REST route, source JSON, or class feature graph behavior changed. The complete Maven suite passes **277 tests, 0 failures, 0 errors, and 0 skips** (271-test baseline plus 6 Stage B1 tests).

## Subsequent stage

Stage B2 adds Feat, Optional Feature, Skill, and Language adapters and the selected source-authored grant, choice, filter, and prerequisite extractors. See `analysis/references/STAGE-B2-IMPLEMENTATION.md` for occurrence totals and scope boundaries. Monster spellcasting, Magic Variant applicability, remaining grant fields, and public resolution projections remain future integration work. Spell grants remain separate from eligibility filters; combined proficiency vocabularies remain separate from definition catalogs.
