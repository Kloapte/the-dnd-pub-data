# Open Questions and Recommended Defaults

These are decisions for subsequent implementation review. They do not block this completed
analysis and are not requests to expand the current task.

| Question | Evidence / consequence | Recommended default / next evidence |
|---|---|---|
| How should public Class table positions be corrected compatibly? | Six authored roots / 97 rows lose column identity when text is removed; existing cell arrays have no index | Explicit source column indexes or withheld placeholders through a deliberate additive/versioned contract. Do not publish text or retain ambiguous positions silently |
| What existing authorization covers Monster entry content? | `MonsterDto` publishes rich Feature entries and spellcasting source content across its 4,851-definition contract | Check current rights/product authorization and consumers; keep any remediation independent from internal annotation work |
| Which Item charge and Optional Feature qualifier fields have a publication decision? | 220 Item raw roots carry charge markup; seven Optional Feature roots forward 14 text fields | Review the exact source/field subset and document an approved projection or withholding/migration plan |
| What exact SRD passages support each proposed new public value? | Source flags and Book metadata are not field-level rights mappings | Require explicit licensed document/version/locator or separate permission; default new enrichment to internal/unreviewed |
| Can Item focus labels reuse a Class reference type? | 52 arrays carry class labels without sources; two roots use Boolean focus | Preserve source labels and variants; do not infer sources/IDs or expand the resolver |
| Which Feat grant primitives genuinely match existing Race/Background models? | Fourteen field families occur on 59 roots; source alternatives differ by grant kind | Compare each typed shape and meaning before reuse; prefer small Feat structures over a universal grant map |
| How should numeric-looking Class string cells be treated? | PHB Barbarian has string `"2"` in a Rages column; current mapper calls every string presentation text | Preserve internally and withhold publicly during position repair. Consider a separate column-specific normalization only with proven grammar and publication decision |
| What is the authoritative v2.37.0 renderer grammar? | Corpus arities are known; live Renderer Demo is not a pinned specification and matching source was not retrieved | Validate the pinned implementation/documentation before committing code meanings/defaults. Preserve unknowns and zero-argument forms |
| Which save-label boundaries imply a complete outcome? | 529 save labels, 548 failures, 252 successes and qualified/margin variants are not one-to-one | Keep labels/offsets only until a constrained, reviewed association contract exists; no nearest-tag pairing |
| Can a formula's role be established without prose interpretation? | Dedicated Item recharge/coin fields provide a role; generic Spell/Monster tags often do not | Use exact owner fields/typed containers where available; leave target/trigger/branch absent elsewhere |
| How should copy-effective annotation provenance be represented? | 4,560 Monster roots become 4,851 definitions; Subclass copies and Feat/Race versions repeat/override content | Keep source origin and effective owner/lineage separately; count authored/effective occurrences independently |
| How stable are entry indexes across future snapshots? | Exact locators are reproducible only within file hashes | Treat indexes as snapshot locators; use hashes/review remapping later, with no persistence/sync implementation now |
| What semantic automation accuracy is acceptable? | No reviewed full-corpus semantic gold set exists; keyword counts demonstrably include non-mechanics | Claim no numeric effect accuracy. Define narrow task-specific precision/recall/abstention goals before semantic publication |
| Are source classification tags effect relationships? | Monster damage/condition classifications and Spell inflicted-condition tags lack clause/target linkage | Preserve classification semantics. Do not convert them into effect edges or automatic condition application |
| Should Vehicle detail include its summary AC/HP? | Four AC and eight HP fields across eight roots are normalized/public in summaries but absent from detail DTOs | Consider a small additive consistency batch using existing projections; do not derive component speeds from entry strings |
| Are complete table contents or generated paraphrases publishable? | Current APIs deliberately withhold result prose/collections; transformed content is not automatically cleared | Keep tables/prose and generated descriptions withheld without a separate rights decision |

## Highest-risk ambiguities

- A syntax parser can be completely deterministic while misidentifying the actor, target, branch,
  quantity, timing or modifier source. This is especially risky for damage/healing, save outcomes,
  Multiattack and repeated effects.
- A mention can mean an option, prerequisite, immunity, removal, example or reference. Existing
  resolver role/choice context must not be replaced with a generic grant/effect edge.
- A source numeric value can be conditional or source-specific. Mixing Monster/Object/Vehicle
  statistics by similar field names can lose semantics.
- A positive SRD/basic-rules flag can be mistaken for an authorization record. Exact licensed
  content, attribution and source/version relationships still need validation.
- A public serialization cleanup can silently change a long-standing contract. Correctness and
  publication remediations need explicit compatibility scope and focused regression tests.

## Boundaries already decided

Homecraft is OUT OF SCOPE by product decision. Recipes are food/cocktail metadata catalogs.
Neither is reopened. No new resolver target kinds, Step 8 integration, database entities, source
synchronization, rules execution, character/campaign state or frontend work is proposed here.
