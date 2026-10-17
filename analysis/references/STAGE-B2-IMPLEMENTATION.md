# Stage B2 — Feat, Optional Feature, Skill, and Language Integration

## Implementation

Stage B2 extends the existing `com.thedndpub.data.references` pipeline. The B1 resolver, outcome model, immutable adapter registry, coordinator, snapshot, and diagnostics remain in place. Four catalog adapters now index Feat, Optional Feature, Skill, and Language definitions using the IDs owned by their catalogs. Each adapter validates every indexed source against the shared Source Registry and inherits B1's immutable, deterministically sorted name and source indexes.

The catalogs expose immutable definition snapshots for adapter construction. The Spring coordinator takes all seven target kinds (Spell, Item, Base Item, Feat, Optional Feature, Skill, and Language), extracts supported references once, resolves once, and publishes one immutable internal snapshot. No public DTO or REST contract changed. The existing `ClassReferenceResolver` and the class feature graph were not changed.

## Extracted references

The extractor uses typed normalized definitions and their retained typed import records only where a normalized definition does not carry the source grant structure (Feat skill/language grants). It does not scan descriptive entries or markup.

Supported fields include:

- Background feat, skill, and language grants, plus explicit alternatives and category selectors.
- Race and Subrace feat, skill, language, combined proficiency choices, and alternatives, read from the authored root source records. Expanded race versions are not counted again.
- Class/subclass feat and Optional Feature progression references and category filters; class starting and multiclass skill choices; and typed external references nested in class and subclass feature content.
- Feat proficiency grants and choices, feat prerequisite feat/feature references, and Optional Feature progression selectors.
- Optional Feature prerequisite references to other Optional Features.

The B1 class spell, equipment, and Item `baseItem` extractors are unchanged. Multi-pipe feat prerequisite values (for example, a feat UID plus a configured display/choice segment) are retained as `UNSUPPORTED`/`DEFERRED`, with their recognized source segment validated; they are never truncated into a false exact identity. Combined skill/tool/language selectors use the explicit `PROFICIENCY` target kind and stay deferred because they do not identify one target catalog. Category tokens such as `any` and `anyStandard` are filters, not missing definitions.

## Corpus results

The Stage B2 extractor contributes **1,483 reference occurrences**. The complete startup snapshot now contains **3,875 occurrences**, including the same 2,392 B1-selected occurrences.

| Target kind | B2 occurrences |
|---|---:|
| Feat | 160 |
| Optional Feature | 210 |
| Skill | 830 |
| Language | 273 |
| Combined proficiency selector (no target adapter) | 10 |
| **B2 total** | **1,483** |

Occurrences by owning catalog are Background 499, Class 283, Class Feature 79, Subclass 17, Subclass Feature 84, Feat 155, Optional Feature 10, Race 302, and Subrace 54. These are occurrence counts, not unique definition counts; the snapshot also retains detailed owner/field/target/form/outcome/cardinality aggregates.

| Outcome | B2 occurrences | Full snapshot (B1 + B2) |
|---|---:|---:|
| RESOLVED | 127 | 1,697 |
| AMBIGUOUS | 488 | 1,108 |
| UNRESOLVED | 22 | 30 |
| DEFERRED | 846 | 1,040 |
| **Total** | **1,483** | **3,875** |

The B1 subset remains exactly: 1,570 RESOLVED, 620 AMBIGUOUS, 8 UNRESOLVED, and 194 DEFERRED. Stage A's 84 Background roots with feat data is a root count, while this stage records 90 feat-field occurrences (86 exact grants and four category filters). Stage A's class-only Optional Feature and Feat figures use a narrower owner/field boundary; B2 additionally includes feature definitions, progression selectors, and prerequisite references. These counts should not be added to root counts or distinct authored UID counts.

Current B2 unresolved references are preserved as diagnostics: 13 Feat prerequisite references to Optional Features and nine unqualified Language names without a catalog candidate. They do not cause startup failure. The eight B1 unresolved Background item UIDs remain unchanged. Candidate-only unique unqualified names remain DEFERRED; multi-candidate names remain AMBIGUOUS. PHB and XPHB candidates retain distinct catalog IDs.

## Validation and limitations

Unknown explicit source identities and malformed recognized UIDs still fail through the B1 resolver. Duplicate target IDs fail adapter construction. Source-authored missing targets remain diagnostics according to the B1 policy; selection expressions, prerequisites, categories, and combined proficiency choices are not treated as grants. Candidate lists and all snapshot structures remain immutable and sorted.

The existing class feature graph remains owned by `ClassReferenceResolver` (2,339 authored and 2,725 effective feature edges). B2 does not re-resolve those edges. Spell eligibility, Monster spellcasting, Magic Variant applicability, tool/weapon/armor vocabularies, and arbitrary prose/markup references remain outside this integration. The `PROFICIENCY` target kind is intentionally adapterless and represents combined-domain choices without guessing a skill/language/tool category.

`CrossCatalogResolutionTests` covers all target adapter counts, catalog-owned exact ID lookup, B1 count preservation, corpus-level B2 target/outcome totals, choice and prerequisite distinctions, missing target preservation, stable unique field locators, deterministic ordering, and snapshot/index immutability. The complete Maven suite passes **279 tests, with zero failures or errors** (277-test baseline plus two Stage B2 tests).
