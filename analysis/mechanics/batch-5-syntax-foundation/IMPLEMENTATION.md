# Step 7 Batch 5: shared internal evidence and syntax foundation

Implemented and verified against the repository-pinned v2.37.0 bytes. The [design checkpoint](DTO-DESIGN.md) preceded implementation. This is an unwired internal library in `com.thedndpub.data.mechanics.syntax`: it introduces no catalog, service bean, REST field, domain annotation index or rule execution.

## Existing implementation review

SourceAuthoredReferenceExtractor, ClassSourceValidator and several source validators contain narrow regex tag readers; ReferenceUidParser parses selected identities. Copy resolution has additional renderer-specific replacement regexes, and MagicVariantMapper recognizes a narrow dice expression. None provides a shared balanced tag tree with exact spans/nesting/arguments. They remain untouched to preserve resolver and normalization contracts. The analysis scanners inform this implementation but are not imported as runtime dependencies.

The complete assessment directory is fingerprinted in [source-audit.json](source-audit.json), including all eleven documents, scanners and evidence files. The audit separately checked all 175 original source hashes and unusual tag syntax before the Java design. Original assessment artifacts are unchanged.

## Evidence contract

| Internal type | Meaning |
|---|---|
| `SyntaxSourceOwner` | Resource path, optional exact-byte SHA-256, original collection and owner RFC 6901 JSON Pointer. It identifies a reproducible source occurrence, not a new catalog identity |
| `SyntaxContext` | Original ancestor pointer and its uninterpreted authored type label, if any |
| `SyntaxTextLocation` | Supplied owner, exact text-leaf pointer, ordered immutable hierarchy and explicit origin |
| `SyntaxOrigin` | AUTHORED, AUTHORED_TRANSFORM_INSTRUCTION, or AUTHORED_ORIGIN_UNAVAILABLE |
| `SyntaxText` | Location plus exact JSON-decoded original string; internal content only |
| `SyntaxSpan` | Inclusive start / exclusive end measured in Java String UTF-16 units |
| `SyntaxEvidence` | Text location plus span; it carries no inferred subject/target/effect |

Evidence types are in the [syntax package](../../../src/main/java/com/thedndpub/data/mechanics/syntax/). Collection components defensively copy inputs. Pointer validation retains correct `~0`/`~1` escaping, verifies owner membership and checks ancestor order. File fingerprints are caller-supplied and optional; the full-corpus validator supplies/validates actual fingerprints. No source publication identity is inferred or registered.

For a decoded string beginning with an emoji followed by a space, the next tag starts at offset **3**, because the emoji occupies two UTF-16 units. Offsets are neither Unicode code-point positions nor raw JSON/file byte positions. The raw substring reconstructs exactly through `SyntaxSpan.extract(originalString)`.

Repeated identical text in two source fields has two different text-leaf pointers. A path is stable within its fingerprinted source snapshot, not a new persistence ID. Effective copy/version origin mapping is explicitly unavailable in this batch. The generic walker accepts authored input only; callers with transformed text must supply evidence with unavailable origin unless independently verified. No raw locator is silently assigned as the origin of generated content.

## Balanced parser

[BalancedMarkupParser](../../../src/main/java/com/thedndpub/data/mechanics/syntax/BalancedMarkupParser.java) is stateless and returns `MarkupParseResult`. Each immutable `MarkupTag` retains:

- Exact original span text and evidence.
- Exact case-preserved name.
- Exact argument suffix after the name, including separator whitespace.
- Ordered argument segments, including empty and trailing segments.
- Ordered direct nested tags and their original absolute string offsets.

Top-level tags remain ordered. `preorder()` returns a parent before its children and preserves sibling order. Nested tags are not also inserted as top-level siblings.

Names use ASCII letters/digits/underscore/hyphen. The parser balances braces within each tag and splits pipes only at brace depth zero. Leading separator whitespace is excluded only from the segment view; raw suffix/token retain it exactly. A directly adjacent pipe is preserved as a leading empty segment. A whitespace-only suffix remains distinguishable from no suffix through rawArgumentText, although both yield zero segments.

Balanced unknown tags parse normally. Empty/invalid names, invalid separators, missing closing braces and nesting above **64 tag levels** throw `MarkupSyntaxException` with source location/span. No partial parse is returned and no malformed source is repaired. Ordinary braces outside tags are ordinary text.

JSON escapes are decoded before parsing. The audit found **zero backslashes inside corpus tags**, zero whitespace-only suffixes, zero directly adjacent pipe suffixes and zero non-ASCII separators. Consequently no renderer escape convention is inferred. In a synthetic backslash-before-pipe case, the slash remains literal and the pipe still delimits; this behavior is tested and documented. Quoted text introduces no additional delimiter-escaping rule. The parser recognizes syntax without implementing renderer defaults.

## Observation vocabulary

`SyntaxObservationClassifier` is separate from generic parsing and produces four sealed variants:

| Observation | Initial names | Preserved interpretation |
|---|---|---|
| FormulaSyntaxObservation | dice, damage, scaledice, scaledamage | Primary expression plus complete original tag/arguments; not an applied formula or effect |
| ModifierSyntaxObservation | hit, d20 | Authored token shape; no modifier application |
| LabelSyntaxObservation | atk, atkr, h, hom, dc, recharge, actSave, actSaveFail, actSaveSuccess, actSaveSuccessOrFail, actSaveFailBy, actTrigger, actResponse, hitYourSpellAttack, dcYourSpellSave | Explicit label and exact arguments; no default code/threshold/branch/actor interpretation |
| OpaqueSyntaxObservation | Every other family, or formula/modifier without a payload | Complete syntax retained, with REFERENCE, FORMATTING or UNSUPPORTED category |

There are **21** recognized names. Classification is case-insensitive while tag spelling remains exact. Opaque reference labels do not parse UIDs, infer sources or call the shared resolver. Formatting/reference categories are lexical categories, not semantic adapters or publication decisions.

`SyntaxExpression` characterizes exact primary tokens as SIGNED_INTEGER, UNSIGNED_INTEGER, DICE_NOTATION or OPAQUE_OR_SYMBOLIC. The dice recognizer deliberately covers only a bounded lexical pattern (explicit count/die size with optional numeric/dice additions, subtraction or multiplication). Other expressions are retained opaquely, including symbolic modifiers, nested markup, functions and syntax outside that pattern. This is not a complete dice grammar or a formula evaluator. No integer/dice arithmetic is performed. Extra/display/scaling arguments stay exact on the tag; they are not reduced to level schedules.

Full-corpus expression shapes across formula/modifier observations are **15,466** dice-notation tokens, **19** signed integers, **6,023** unsigned integers and **835** opaque/symbolic tokens. The latter are retained payloads inside recognized tag families, not malformed tags or lost occurrences. The audit specifically found one `hit` placeholder token, `<$to_hit__str$>`, alongside signed/unsigned integers and zero.

## Source traversal

[AuthoredSyntaxTextWalker](../../../src/main/java/com/thedndpub/data/mechanics/syntax/AuthoredSyntaxTextWalker.java) supplies every original JSON text leaf to a callback with its owner/hierarchy/pointer. Its generic JsonNode input is deliberate infrastructure for arbitrary source structures, not an application model containing generic property bags. Evidence and observations themselves use typed immutable components.

The walker handles original shared EntryDte content, Class-specific nodes, Monster wrappers/spellcasting, Object attack/hit arrays, Trap stages, Vehicle actions, Recipe instructions/quantities and dedicated table results through their original JSON locations. It does not serialize DTE wrapper names back into invented source paths. Text in names, headers, metadata or related fields is considered as well as entry bodies; numeric/Boolean JSON leaves are not coerced into textual mechanics.

Traversal preserves source object and array order. `_copy`, `_mod`, `_versions`, `_abstract`, `_implementations` and `_templates` paths mark authored transformation instructions. The callback receives no claimed effective-definition origin or catalog target ID. There is no source deserialization on HTTP requests, mutable effect registry or startup annotation materialization. Reading support data during syntax validation does not implement legendary-group body loading.

## Full-corpus validation

[corpus-verification.json](corpus-verification.json) records the complete Java validation and exact span digest. Counts independently match all original assessment tag frequencies **and every argument-count/nesting variant**.

| Measure | Result |
|---|---:|
| Source files scanned/fingerprinted | 175 |
| Authored primary owners / existing domain scope | 14,399 / 36 domains |
| Supporting units | 812 |
| Homecraft excluded owners | 20 |
| JSON text leaves scanned | 378,688 |
| Definition-root tag occurrences | 90,294 |
| All in-scope tag occurrences | 91,994 |
| Observed / initially recognized tag families | 73 / 21 |
| Recognized vocabulary occurrences / successfully classified | 42,741 / 42,741 |
| Other opaque occurrences / families | 49,253 / 52 |
| Malformed / span reconstruction failures | 0 / 0 |
| Source owners with any / recognized tags | 10,108 / 6,201 |
| Files with any / recognized tags | 166 / 157 |
| Effective definitions/annotations materialized | 0 |

The 42,741 recognized observations comprise **16,301 formula**, **6,042 modifier** and **20,398 label** observations. Every opaque observation is also successfully parsed syntactically. Unsupported semantic classification is distinct from parsing failure.

| Root role / origin | Tag occurrences |
|---|---:|
| Definition / ordinary authored | 87,405 |
| Definition / authored transformation instruction | 2,889 |
| Support / ordinary authored | 1,512 |
| Support / authored transformation instruction | 188 |
| Total | 91,994 |

These transform counts independently agree with the original scanner's transform contexts. They are not counts of generated/effective annotations. The existing **14,797 effective-definition** contract remains unchanged and is not an annotation coverage claim. Recipe syntax is counted for parser completeness without becoming gameplay crafting data; Homecraft remains excluded.

Every text pointer is resolved against the original parsed document. Every parsed tag span is compared with its original UTF-16 substring. Source-order/location/token evidence produces fingerprint `05fc77234cbac44d2b61013bdf7001ae860298d60abd40d76870701b44a574be`. Focused and full-suite runs produce identical complete reports. Reports contain aggregate counts and hashes, not copied source paragraphs.

## Tests and compatibility verification

**21 new tests**, with no existing tests modified:

- 15 BalancedMarkupParserTests: exact names/suffixes/spans, nesting/argument boundaries, adjacent/separate ordering, empty/extra arguments, Unicode offsets, malformed input and depth limit, ordinary braces, documented backslash behavior, opaque reference/formatting/unknown tags, lexical expression shapes, scaling/modifier/label preservation and immutable collections.
- Five AuthoredSyntaxTextWalkerTests: hierarchy and escaped pointers, repeated text occurrences, transformation origin, invalid evidence, defensive context copies and explicit unavailable-origin representation.
- One SyntaxCorpusTests full audit: all 175 files, source hashes, every owner/string/tag, exact tag/arity/nesting parity, span/pointer reconstruction and deterministic aggregate evidence.

| Verification | Result |
|---|---|
| Focused foundation/corpus suite | 21 tests passed |
| Full Maven package/test suite | **464 tests; zero failures/errors/skips** (443 baseline + 21) |
| Existing production Java, normalized models, public DTOs/endpoints and prior tests | All **1,680** pre-existing files under src retain their bytes |
| Definition counts / identities | Existing complete catalog tests/fingerprints pass; no source/model/identity logic modified |
| Prior Item focus/bonuses, Feat mechanics/prerequisites and Class positions | Existing regression tests pass unchanged |
| Existing public contracts | Existing serialization/API tests and Class/Item/Feat fingerprints pass unchanged; no foundation field is wired to DTOs |
| Resolver | All **6,246** complete results retain their fingerprint; 2,517 RESOLVED / 2,054 AMBIGUOUS / 181 UNRESOLVED / 1,494 DEFERRED |
| Source hashes | All **175** files match the pinned assessment hashes |
| Original assessment | All **23** files retain their original hashes |
| Packaged foundation classes | Isolated loader reads only BOOT-INF/classes from the executable JAR; parsing, classification and UTF-16 offsets pass |
| Executable-JAR classpath startup | Existing packaged smoke passes: 2,742 Items/Base Items, 230 Magic Variants, copy-effective lookups and prior focus/classification compatibility |
| Whitespace | git diff --check and direct checks of new Java/analysis files pass |

The isolated [JAR probe](SyntaxFoundationJarProbe.java) has no target/classes fallback. It loads the foundation classes from the actual packaged artifact using the platform class loader as parent. The existing executable-JAR smoke starts the application from target, thereby checking classpath resources rather than filesystem source paths.

[scope-verification.json](scope-verification.json) records source/assessment/pre-existing-file preservation and the complete list of new Java/test files. No public DTO, controller, normalized domain definition, resolver class, dependency or global Jackson setting changed in this batch.

## Reproduction

Run from the repository root, with the configured Maven repository where required:

```powershell
node analysis/mechanics/batch-5-syntax-foundation/audit-source.mjs
mvn test
mvn -DskipTests package
javac -encoding UTF-8 -d target/batch-5-probe-classes analysis/mechanics/batch-5-syntax-foundation/SyntaxFoundationJarProbe.java
java -cp target/batch-5-probe-classes SyntaxFoundationJarProbe target/dnd-pub-data-0.0.1-SNAPSHOT.jar
& ./analysis/mechanics/batch-2-item-focus/verify-packaged.ps1
node analysis/mechanics/batch-5-syntax-foundation/verify-scope.mjs
git diff --check
```

The focused report is retained as verification evidence and compared with subsequent complete-corpus output. Existing analysis scanners are not rerun or overwritten by this batch. The old assessment validator intentionally targets its analysis-only source snapshot and 443-test baseline; use this batch's scope verifier after these authorized additions.

## Remaining limitations and next adapter

This foundation recognizes tagged syntax, not complete effects or a full renderer/dice grammar. Strict failures are explicit; future unsupported syntax must remain opaque or receive an independently reviewed parser change. Effective copy/version origin mapping, domain ownership/selection rules and annotation indexes are not implemented. Generic public serialization of internal evidence/content is not approved.

The separate Spell adapter can now reuse the walker/parser/evidence/observations and independently account for its 930 formula tags across 422 authored spell owners. It still needs a separately approved immutable owner index and domain-specific context/coverage verification. No Spell or Monster adapter, legendary-group ingestion, Item charge semantics, save/condition outcome model, Multiattack graph or gameplay engine is implemented here.
