# Variant Rules Catalog Implementation

## Source and identity

The pinned `src/main/resources/data/variantrules.json` file has SHA-256 `fbbaed4bb8002076fac0e38a59ed65bac34e261ac753602f3a02b0bdfb1a6485` and contains one root collection, `variantrule`, with 231 definitions. Primary source distribution: XPHB 115, DMG 44, XGE 22, PHB 13, AI 10, GoS 9, AAG 4, TCE 3, VRGR 3, MM 3, and one each from CoS, ESK, PSI, SCAG, and VGM.

IDs use `variant-rule:<exact-source>:<normalized-name-slug>`. No duplicate `(source,name)` pair or ID slug collision was found. Seven names recur across sources; they remain distinct and source-aware (`Downtime Activity: Carousing`, `Downtime Activity: Training`, `Familiars`, `Healing`, `Inspiration`, `Sidekicks`, and `Simultaneous Effects`). All 17 primary, additional-source, and qualified-reprint codes validate against the Source Registry; no registry edit was needed.

## Typed import and internal model

`VariantRulesFileDte` strictly loads the root, and `VariantRuleDte` models every root property. `ruleType` is preserved as the authored code C/O/V/VO; it is absent on 40 records and never inferred. Root `type` is absent on 186 records and has `entries` or `section` otherwise. Nested sections stay under their parent rule and do not get separate catalog IDs.

The 119-path recursive source audit is in [`FIELD-COVERAGE.md`](FIELD-COVERAGE.md). It found 188 tables with 1,138 rows, 5 nested section nodes (plus 28 roots marked as sections), and heterogeneous strings, numbers, and structured table cells. Common `EntryDte` variants are reused where the source shapes match. The shared strict typed entry model was extended with authored list headings, inline wrappers, link nodes, image nodes and typed hrefs, and the `isNameGenerator` table flag. No `Object`, `JsonNode`, generic map, or silently ignored field is used in the Variant Rule source model. Reprint strings and tagged UID objects use explicit variants. `additionalSources` retains source/page citations.

`VariantRuleDefinition` is immutable and retains all source flags, root metadata, lineage, source citations, and full typed recursive entries internally. It computes nested section and table counts from actual entry structure; the root entry type is reported separately. Root parent/child relationships, prerequisites, choices, modifiers, activation values, and other structured mechanics are absent. Dice, DC, damage, effects, and instructions occur in descriptive strings or table content only; they are preserved internally and not interpreted. Meaningful source flags are retained, while no explicit false or null root values occur in this snapshot.

## Public API and licensing

Immutable summaries expose ID, name, source, page, ruleType, root entry type, and section/table counts. Detail adds supported lightweight references. Both omit source text, examples, table rows/cells, raw markup, image/href metadata, attribution, SRD/basic-rules import flags, reprint/import metadata, resolver outcomes, diagnostics, and candidates. The source's structured tables and prose are not assumed redistributable merely because they are structured. Prose-only and interpretation-dependent rules remain deferred to Step 7.

Read-only endpoints:

- `GET /api/variant-rules?name=&source=&ruleType=`
- `GET /api/variant-rules/{id}`
- `GET /api/variant-rules/by-name/{name}?source=`

The 231-record static catalog loads and indexes once at startup. Collection responses use deterministic ordering and source/type/name filtering; pagination is not added, consistent with the established small static-catalog convention. No campaign settings, rule execution, mechanics calculations, eligibility, conflict resolution, or writes are implemented.

## Cross-catalog references

Existing adapters resolve 361 explicit Spell, Item, Skill, and Item-filter occurrences. Outcomes: 102 RESOLVED, 214 AMBIGUOUS, 21 UNRESOLVED, and 24 DEFERRED. Only exact resolved target IDs are projected. See [`REFERENCE-INVENTORY.md`](REFERENCE-INVENTORY.md) for per-target outcomes and all markup tags.

The existing 4,520 resolver occurrences retain outcomes 1,918/1,487/44/1,071. No adapter was added for Variant Rule, Action, Condition, Monster, Object, Hazard, Trap, Vehicle, Vehicle Upgrade, Class, or other unsupported targets. Their typed entry source remains intact; no target IDs are inferred from prose or names.

## Verification

- Full-corpus strict deserialization: all 231 definitions load.
- Source Registry validation, unique stable IDs, source-aware duplicate-name lookup, typed entry variants, table rows, reprint forms, unknown-field rejection, safe JSON projection, REST behavior, resolver baseline stability, and packaged-JAR loading are covered in `VariantRuleCatalogTests`.
- Complete Maven suite: 370 tests, 0 failures, 0 errors (362 prior tests plus 8 Variant Rules tests).
- `mvn -DskipTests package`: passed. The packaged-JAR classpath test loads all 231 definitions from `data/variantrules.json`.
- Existing resolver snapshot: all 4,520 prior occurrences retain their recorded outcomes; the new 361 Variant Rule occurrences are counted separately by catalog tests.
- `git diff --check`: passed. Git reported only expected LF-to-CRLF working-tree notices for text files.
