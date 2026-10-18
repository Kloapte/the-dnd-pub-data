# Batch 10: Object attack/hit relationship pilot

## Decision and scope

A small **internal structural relationship index** is technically justified as a separately authorized pilot.
There is no Object normalization defect to repair: the seven typed attacks already survive intact. The useful
addition would be reproducible links between existing source sections and existing syntax observations, not new
attack or damage mechanics. No extractor or production model is implemented by this assessment.

The exhaustive inventory is **seven attack containers on seven authored Objects**, among 37 Object roots.
All 175 pinned JSON files were parsed recursively for the exact `type: "attack"` discriminator; no additional
containers occur anywhere in that file set. This is a structural count, not a count of all attacks described by
Object text. Objects have no copies, versions or templates: their 37 authored definitions map to 37 effective
definitions without expansion. Other domains' effective counts are not remeasured here.

The review set has **80 candidate relationship labels**: 72 on the exhaustive seven-container inventory and eight
selected boundary controls from actual Object content. It contains 38 accepted structural/syntax links, nine
rejected boundary claims and 33 mandatory abstentions. These are counts of specified evidence relationships,
not a count of inferred gameplay effects or every relationship one could imagine.

| Reliability | ACCEPT | REJECT | ABSTAIN | Meaning of denominator |
|---|---:|---:|---:|---|
| DIRECT_STRUCTURE | 21 | 9 | 0 | Three accepted authored-field edges per attack; seven wrong-section and two control negatives |
| TAG_SYNTAX | 17 | 0 | 0 | 14 attack-section/tag links plus three control links |
| LOCAL_CONTEXT | 0 | 0 | 8 | Seven formula/type-word proposals plus one inline hit-label/formula proposal |
| PROSE_INFERENCE | 0 | 0 | 17 | 14 range/target proposals, two minimum-distance exceptions, one misleading tag/effect control |
| UNSUPPORTED | 0 | 0 | 8 | Seven owner-as-recipient proposals and one common-hit proposal |
| Total | 38 | 9 | 33 | 80 review labels |

The labels are a source-reviewed **proposed gold set**, not independently adjudicated semantic ground truth.
No extractor has run against them; precision and recall are therefore **not measured**. Independent review is
still required before extending automatic behavior into LOCAL_CONTEXT. DIRECT_STRUCTURE and TAG_SYNTAX positives
can be checked against exact field membership and the existing shared parser without interpreting gameplay.

## Evidence and prior findings

This confirms the seven-root finding in the original
[entry inventory](../entry-assessment/ENTRY-STRUCTURE-INVENTORY.md),
[relationship gaps](../entry-assessment/RELATIONSHIP-GAPS.md) and
[roadmap](../entry-assessment/IMPLEMENTATION-ROADMAP.md). It refines the roadmap's proposed pilot: preserving
membership does not require treating a hit section as an applied effect. No previously completed normalization
gap is reopened. The current [implementation order](../IMPLEMENTATION-ORDER.md) remains unchanged.

The analysis reuses the [Batch 5 syntax contract](../batch-5-syntax-foundation/IMPLEMENTATION.md) and respects
the authored/effective separation of [Batch 8](../batch-8-monster-syntax/IMPLEMENTATION.md). Neither Monster syntax
nor legendary-group content is included in Object totals. The resolver is never invoked by the analysis.

Files in this directory:

- [source-evidence.json](source-evidence.json): exhaustive seven-container inventory, identities, provenance,
  exact tag spans, hierarchy, classifications and aggregate label counts.
- [runtime-evidence.json](runtime-evidence.json): existing Java DTE/normalizer/public-projection checks for all
  37 Objects and the shared parser's 147 Object tag observations. It includes tags and fingerprints, not full
  source paragraphs.
- [gold-set.json](gold-set.json): all 80 labels, source endpoints, rules, decisions and reasons.
- [build-gold-set.mjs](build-gold-set.mjs): explicit reviewed case manifest and deterministic expansion into
  labels; not a semantic extraction framework.
- [audit-source.mjs](audit-source.mjs): structural inventory, original-pointer/span checks, gold endpoint and
  structural-rule checks, all-source hash validation, protected-tree verification.
- [verify.mjs](verify.mjs): runs the Java probe, gold expansion and audit twice, then checks byte-identical
  evidence output and writes the determinism report.
- [ObjectRelationshipProbe.java](ObjectRelationshipProbe.java): analysis driver using existing production
  DTEs, mapping and syntax components; no Spring startup, production annotation index or rules engine.
- [scope-baseline.json](scope-baseline.json), [verification.json](verification.json): pre-task protected-tree
  fingerprints and final verification results.

## 1. Exhaustive container inventory

Every pointer below is relative to `src/main/resources/data/objects.json`. All seven have exactly the same
four fields: `type` (string `attack`), `attackType` (string), `attackEntries` (one string), and `hitEntries`
(one string). The enclosing action has `type: "actions"`, an authored name, and one attack child. Each container
is nested under `/object/{index}/actionEntries/0/entries/0`; it is not an independent catalog definition.

| Key | Existing Object ID | Page | Object index | Action name | attackType | hit token | Formula token |
|---|---|---:|---:|---|---|---|---|
| A1 | `object:WDMM:animated-ghaunadaur-statue` | 250 | 0 | Claw | MW | +10 | 2d6 + 5 |
| A2 | `object:DMG:ballista` | 255 | 2 | Bolt | RW | +6 | 3d10 |
| A3 | `object:DMG:cannon` | 255 | 5 | Cannon Ball | RW | +6 | 8d10 |
| A4 | `object:RMBRE:captain-a-hole` | 21 | 7 | Cannon | RW | +5 | 1d6 |
| A5 | `object:DMG:mangonel` | 255 | 23 | Mangonel Stone | RW | +5 | 5d10 |
| A6 | `object:DMG:ram` | 255 | 25 | Ram | MW | +8 | 3d10 |
| A7 | `object:DMG:trebuchet` | 256 | 34 | Trebuchet Stone | RW | +5 | 8d10 |

Names are Animated Ghaunadaur Statue, Ballista, Cannon, Captain A-Hole, Mangonel, Ram and Trebuchet respectively.
Primary sources: **DMG five, WDMM one, RMBRE one**. `attackType` counts are MW two and RW five. Preserve these
codes without inventing display labels or importing Monster identity/attack conventions.

### Exact spans and content qualifications

Offsets refer to the **JSON-decoded string**, measured in Java String UTF-16 code units, `[start, end)`.
They are not file-byte offsets or code-point offsets. Append `/attackEntries/0` or `/hitEntries/0` to the
container pointer to resolve the appropriate string. Every span below is independently reconstructed in Java
and Node. Full text leaves have SHA-256 fingerprints in the evidence file.

| Key | `hit` tag span in attack string | `damage` tag span in hit string | Untagged damage word | Untagged range/reach | Target phrase |
|---|---|---|---|---|---|
| A1 | [0,10) | [4,21) | slashing | reach 5 ft. | one target |
| A2 | [0,9) | [4,18) | piercing | range 120/480 ft. | one target |
| A3 | [0,9) | [4,18) | bludgeoning | range 600/2,400 ft. | one target |
| A4 | [0,9) | [3,16) | bludgeoning | range 30 ft. | one target |
| A5 | [0,9) | [4,18) | bludgeoning | range 200/800 ft. | one target |
| A6 | [0,9) | [4,18) | bludgeoning | reach 5 ft. | one object |
| A7 | [0,9) | [4,18) | bludgeoning | range 300/1,200 ft. | one target |

The hit token is encoded as `{@hit ...}` and the formula as `{@damage ...}`. There is **no** numeric attack-bonus
field. The signed modifier comes from tag syntax. There is **no** typed damage-type, range, target or secondary
effect field in these containers. A5/A7 include a prose minimum-distance exception in their attack string.
The hit strings contain a leading displayed integer and a parenthesized formula, followed by an untagged
damage word. No arithmetic relationship between that integer and the formula is asserted here.

Confirmed absences **within the seven containers**: multiple formulas in one hit section; multiple string
members in either section; nested entry objects; nested tags; conditional hit branches; explicit condition tags;
structured secondary effects. All 14 section members are plain strings. Missing variants are not fabricated
to make the gold set look broader. The surrounding action/container hierarchy is present and preserved.

### Additional content is not another typed attack

All Object roots also contain text-only attacks, saving-throw descriptions and nested list items. Those cannot
be promoted into this seven-container domain by keyword matching. Examples used as boundary controls:

- Automatic Ballista HBTD: `/object/1/actionEntries/0/entries/0` is text with `atkr`, `hit`, `h` and `damage`
  tags. The shared parser recognizes those labels individually. It does not create `hitEntries` or determine
  which following text is governed by `h`.
- Eldritch Cannon EFA: `/object/10/actionEntries/0/entries/1` is a list with three items, not an attack.
  Items 0 and 1 each have a distinct `2d8` formula; item 2 has a `1d8` dice formula. These three occurrences
  share a wrapper but have separate item ancestry. Multiple formulas under one wrapper do not establish
  multiple damage components of a single hit.
- Eldritch Cannon, Protector TCE: `/object/13/actionEntries/0/entries/0` uses a `damage` formula tag in
  temporary-hit-point prose. This is a concrete counterexample to treating every damage tag as damage application.

These controls are selected examples, not an exhaustive semantic inventory of text-only Object actions.
The exhaustive structural scan counts seven `actions`, seven `attack`, 34 `entries`, two `list`, three `item`
and two `table` typed nodes across all Object fields. Those are node-discriminator counts, not action counts.

## 2. Source-to-model-to-public trace

All seven follow the same verified path; their full individual pointers and runtime IDs are in source-evidence.

| Stage | Java location | Preservation/publication behavior |
|---|---|---|
| Load | [ObjectCatalog.java](../../../src/main/java/com/thedndpub/data/services/ObjectCatalog.java), `load` and constructor | Reads `data/objects.json` once with strict unknown-field behavior; one mapped definition per source root; validates provenance; retains source DTEs by existing ID |
| Root DTE | [ObjectSourceDte.java](../../../src/main/java/com/thedndpub/data/dte/object/ObjectSourceDte.java), `actionEntries` | Ordered immutable list of ObjectActionDte; no flattening |
| Wrapper | [ObjectActionDte.java](../../../src/main/java/com/thedndpub/data/dte/object/ObjectActionDte.java) | Preserves type, authored action name and ordered content |
| Typed dispatch | [ObjectActionDeserializer.java](../../../src/main/java/com/thedndpub/data/dte/object/ObjectActionDeserializer.java), attack branch around line 32 | Allows exactly the four attack fields; requires textual attackType and string-array section members; rejects unknown fields and nontext section members |
| Attack body | [ObjectAttackDte.java](../../../src/main/java/com/thedndpub/data/dte/object/ObjectAttackDte.java) | Preserves type, attackType, attackEntries and hitEntries separately with defensive list copies |
| Normalization | [ObjectDefinitionMapper.java](../../../src/main/java/com/thedndpub/data/dto/object/ObjectDefinitionMapper.java), `map` | Supplies `source.actionEntries()` directly to definition actions |
| Internal definition | [ObjectDefinition.java](../../../src/main/java/com/thedndpub/data/dto/object/ObjectDefinition.java), `actions` | Keeps typed DTE action structures internally, defensively copied; no effect interpretation |
| Public detail | [ObjectDetailDto.java](../../../src/main/java/com/thedndpub/data/dto/object/ObjectDetailDto.java), `from` | Does not have action, attackEntries, hitEntries or descriptive entry fields |
| Service/API | [ObjectsService.java](../../../src/main/java/com/thedndpub/data/services/ObjectsService.java), `byId` / `byName` | Uses safe detail DTO plus pre-existing lightweight reference projection; no action text publication |

The analysis driver strictly deserializes all 37 roots and verifies normalized actions equal their original
typed DTE actions. It separately serializes **each of the seven ObjectAttackDte values back to a JSON tree**
and checks exact structural equality with its original source node. This catches missing/reordered section
members or altered codes, rather than merely observing that an actions list is non-null. All seven pass.
Public detail projection for all 37 Objects has no actions/entries/raw markup. This direct projection probe
does not rerun HTTP tests or the reference coordinator.

The existing [ObjectsPipelineTests.java](../../../src/test/java/com/thedndpub/data/object/ObjectsPipelineTests.java)
already checks strict full-corpus loading, identity, immutable indexes, public action omission, routes,
references and packaged resources. It does not establish the seven-container relationship gold set. No tests
were changed or added in this analysis.

Nested action lists use [ObjectActionListDte](../../../src/main/java/com/thedndpub/data/dte/object/ObjectActionListDte.java)
and [ObjectActionItemDte](../../../src/main/java/com/thedndpub/data/dte/object/ObjectActionItemDte.java), with shared
EntryDte values inside item entries. The seven ObjectAttackDte section arrays themselves use `List<String>`,
not recursive EntryDte values. A future adapter must respect that actual dialect.

## 3. Structural relationship matrix

| Proposed relationship | Evidence and count in the seven | Classification | First extractor decision |
|---|---|---|---|
| Attack → attack section | Explicit `attackEntries` field, seven | DIRECT_STRUCTURE | Emit membership |
| Attack → hit section | Explicit `hitEntries` field, seven | DIRECT_STRUCTURE | Emit membership |
| Attack → attackType | Explicit scalar field, seven | DIRECT_STRUCTURE | Retain code and pointer |
| Attack section → hit modifier observation | Exact tag in original array member, seven | TAG_SYNTAX | Emit syntax membership, not an applied numeric stat |
| Hit section → formula observation | Exact damage tag in original array member, seven | TAG_SYNTAX | Emit syntax membership, not damage application |
| Formula → adjacent damage word | Untagged word follows formula in seven strings | LOCAL_CONTEXT | Abstain; separately review a bounded text grammar |
| Attack → interpreted range/reach | Untagged range/reach phrase, seven | PROSE_INFERENCE | Abstain; preserve text location only |
| Attack → target rule | Untagged target phrase, seven; Ram differs from six others | PROSE_INFERENCE | Abstain |
| Attack → minimum-distance exception | Parenthetical prose on Mangonel and Trebuchet, two | PROSE_INFERENCE | Abstain |
| Formula → damage recipient | No explicit recipient field | UNSUPPORTED | Abstain; source owner is not a receiver |
| Hit → condition/secondary effect | None structured or tagged in these seven hit strings | UNSUPPORTED for such an inference | Emit none; no invented positive gold label |
| One hit → multiple formulas/branches | No such instance in seven | Unobserved, not measured | Do not claim coverage; require separate fixtures/review if source dialect changes |

The 21 accepted direct edges count **three specified field relations per container**. They do not enumerate
every possible ancestor/descendant edge. The 14 accepted syntax edges count one modifier and one formula
membership per container. Pure containment is directly known; identifying the endpoint as a formula/modifier
uses tag syntax, hence TAG_SYNTAX for that composite claim. No proximity-based link is upgraded to structure.

## 4. Complete reviewable gold set

[gold-set.json](gold-set.json) is the full label artifact. Every label carries an ID, source resource,
subject pointer, object pointer, exact UTF-16 span/token when textual, relation type, reliability, decision,
rule and rationale. A node anchor is a JSON Pointer; a text anchor is that pointer plus a span. Textual content
is limited to small anchors/tags, not full paragraphs. Source hash and owner identity are supplied by the
companion inventory. `build-gold-set.mjs` contains the explicit seven-case reviewed manifest.

The following label schema enumerates **all** seven-case labels. Substitute A1 through A7 using the inventory
above; the gold JSON provides every expanded pair and span. This table does not imply unreviewed pattern
generalization beyond those pinned cases.

| Suffix | Subject → object | Rule | Reliability | Decision | Labels |
|---|---|---|---|---|---:|
| -01 | attack node → its attackEntries array | S1 | DIRECT_STRUCTURE | ACCEPT | 7 |
| -02 | attack node → its hitEntries array | S1 | DIRECT_STRUCTURE | ACCEPT | 7 |
| -03 | attack node → its attackType scalar | S2 | DIRECT_STRUCTURE | ACCEPT | 7 |
| -04 | attackEntries array → hit tag | T1 | TAG_SYNTAX | ACCEPT | 7 |
| -05 | hitEntries array → damage tag | T1 | TAG_SYNTAX | ACCEPT | 7 |
| -06 | formula tag → adjacent damage word | A1 | LOCAL_CONTEXT | ABSTAIN | 7 |
| -07 | attack node → range/reach phrase as interpreted range | A2 | PROSE_INFERENCE | ABSTAIN | 7 |
| -08 | attack node → target phrase as target restriction | A2 | PROSE_INFERENCE | ABSTAIN | 7 |
| -09 | formula → owning Object as alleged damage recipient | A3 | UNSUPPORTED | ABSTAIN | 7 |
| -10 | attackEntries array → sibling hitEntries formula as alleged descendant | N1 | DIRECT_STRUCTURE | REJECT | 7 |
| -11 (A5/A7 only) | attack node → prose minimum-distance exception | A2 | PROSE_INFERENCE | ABSTAIN | 2 |

The remaining eight labels are actual-corpus controls:

| ID | Exact source endpoint(s), relative to objects.json | Candidate | Decision / reliability |
|---|---|---|---|
| C01 | `/object/1/actionEntries/0/entries/0`, h tag [38,42) | Text contains hit label | ACCEPT / TAG_SYNTAX |
| C02 | `/object/1/actionEntries/0` → its `entries/0` string | Wrapper has structured hit section | REJECT / DIRECT_STRUCTURE |
| C03 | Same Automatic Ballista string, h [38,42) → damage [46,60) | Label governs formula as a hit result | ABSTAIN / LOCAL_CONTEXT |
| C04 | `/object/10/actionEntries/0/entries/1/items/0` → `entries/0` damage [177,190) | Nested item contains formula | ACCEPT / TAG_SYNTAX |
| C05 | Same list, item 1 → `entries/0` damage [131,144) | Other nested item contains identical formula | ACCEPT / TAG_SYNTAX |
| C06 | Same list, item 0 → item 1's formula | Alleged item containment across siblings | REJECT / DIRECT_STRUCTURE |
| C07 | Item 0 formula → item 1 formula | Both belong to one applied hit | ABSTAIN / UNSUPPORTED |
| C08 | `/object/13/actionEntries/0/entries/0`, damage tag → temporary-hit-point phrase | Damage tag proves damage application | ABSTAIN / PROSE_INFERENCE |

ACCEPT means the specified evidence relation is present. REJECT means its structural claim is demonstrably
false; rejecting an edge is not failure to parse its formula. ABSTAIN means the proposed implementation must
make no semantic assertion either way. In particular, C08's prose is a counterexample for human review, not
an authorization to automatically produce a healing/temporary-hit-point effect instead.

Ambiguity is represented by abstention rather than invented ambiguous structure. Repeated `3d10` in Ballista
and Ram, repeated `8d10` in Cannon and Trebuchet, and repeated `2d8` in separate Eldritch Cannon list items all
retain different owners/pointers. Neither equal formula text nor common wrapper ancestry identifies a shared hit.

## 5. Supported rules and required abstention

For a first internal implementation, allow S1, S2 and T1 only within exact typed attack containers.
S1 checks the original `type=attack` node and named section field. S2 preserves the original code without
decoding it. T1 joins an existing Batch 5 tag observation to its actual section through the original text-leaf
pointer, preserving member order and span. Do not join to the nearest feature, tag or matching formula string.

Reject N1/N2 boundary violations. Text-only wrappers, list items and tables must not become ObjectAttackDte
instances. The nested control syntax can be recognized by Batch 5 without bringing it into the initial
typed-attack relationship scope. Preserve opaque/unsupported tags through the existing foundation if encountered;
do not invent a new parser or silently expand its vocabulary. Fail/report schema or parser errors rather than
repairing source or returning a partly correct relationship set without diagnostics.

Abstain on A1/A2/A3: damage-type association, hit-label extent, interpreted ranges, targets, exceptions,
recipient, success/failure/condition application, shared applied hit and damage execution. In the proposed
bounded contract these are deliberate omissions, not missed supported positives. Local lexical regularity
does not supply semantic validation. New nested/multiple-formula section shapes require review, not automatic
flattening or attachment to one effect. Current DTE deserialization already rejects nontext section members.

## 6. Suggested narrow internal model and implementation scope

No model is added in this task. If approved, prefer a read-only authored-source index using the existing
Object identity and Batch 5 evidence types:

- An attack-container anchor: existing Object ID plus SyntaxSourceOwner, original container pointer, wrapper
  hierarchy and original attackType scalar pointer/value. A pointer is an occurrence locator, not a new
  persistent Object or attack ID.
- Ordered section anchors: source field role (`attackEntries` or `hitEntries`), array pointer and member
  position. Preserve original boundaries; do not duplicate whole mutable Object definitions or concatenate text.
- Section-to-observation links: exact SyntaxEvidence and existing SyntaxObservation for each member's tags.
  Formula and modifier variants retain their exact tag arguments and lexical expression kinds.
- Narrow relation names describing membership only, such as HAS_HIT_SECTION and SECTION_CONTAINS_FORMULA_TAG.
  Reliability and unresolved semantics must remain explicit internal evidence, never public effect defaults.

This supports the **35 accepted relations on the seven attacks** at the gold set's defined granularity.
The three accepted control syntax links are validation controls outside that first production adapter scope.
No new cross-catalog target kind, occurrence generation, target resolution, DamageEffect, AttackEffect or
universal effect AST is needed. There is no justification for modifying ObjectDefinition's existing statistics
or action representation. Existing authored/effective ownership is one-to-one for Objects; do not extrapolate
that convenience to Monster copies/versions.

The model's value is navigable, reproducible evidence. It does not fix lost source information and is not
required merely to continue preserving Objects. A separately authorized **Batch 10A** would be reasonable for
testing the shared relationship approach at this small boundary; broad Object prose extraction is not justified.

## 7. Objective validation criteria for a future implementation

1. Match all seven original container pointers and their existing Object IDs; scan all 37 roots and account
   for all 175 file hashes. Do not create definitions for wrappers, sections, formulas or controls.
2. At the defined relation granularity, exactly reproduce the 21 direct and 14 syntax-positive pilot edges.
   Require field role, array/member ordering, source hash, decoded-string pointer and UTF-16 boundaries to match.
   A correct token with a wrong owner/section/span is a false positive and a missed expected edge.
3. Reject all nine structural-negative candidates. For controls outside adapter scope, emit no relationship
   that promotes text/list content into a typed attack. Shared-parser recognition of their tags remains valid.
4. Produce no semantic edge for all 33 abstention labels. Report abstentions separately from structural
   rejections, parse failures and unsupported syntax. Do not inflate recall by treating guessed semantics as
   extra supported positives.
5. Measure true positives, false positives and false negatives by exact endpoint/role identity. Precision is
   TP/(TP+FP); recall is TP/(TP+FN), with denominators and excluded controls explicit. The finite 35-edge
   structural contract should have **zero mismatches**: these fields/spans are explicit and exhaustively
   enumerated, so an error is a locator/implementation defect, not a tolerated semantic uncertainty. This is
   not an arbitrary percentage threshold or evidence of general prose extraction accuracy.
6. Repeated runs produce identical ordered evidence and hashes. Verify every substring independently against
   its original source leaf. Preserve same-text/different-location distinctions. Existing Batch 5 Unicode,
   malformed syntax and immutability tests remain applicable; future adapter tests should exercise its own
   boundaries without weakening strict DTE handling.
7. Keep every public DTO/endpoint, normalized field, Object/Monster/Item identity and all 6,246 resolver
   outcomes unchanged. Future production work must run its focused/full tests and packaged-resource checks;
   no such production verification is claimed by this analysis-only task.

Before supporting LOCAL_CONTEXT, require independent human review/adjudication of its labels and a bounded
pattern specification with false-positive controls. The seven very similar typed attacks cannot validate a
general formula-to-type, range, targeting, secondary-effect or branch extractor. Multiple formulas in a
single typed hit section and nested typed hit content have **zero actual examples here**; coverage cannot
be estimated for them. Any future synthetic robustness fixture must be labeled synthetic, not source evidence.

## 8. Publication and compatibility boundary

Apply the existing [publication policy assessment](../entry-assessment/PUBLICATION-BOUNDARIES.md).
Pointers, roles and containment links are structural metadata, but exact tagged tokens, action names and
source strings remain imported content. A reliable association is not permission to publish it. Even a
publicly familiar formula or SRD indicator does not establish field-by-field rights for this whole collection.
This assessment makes no new legal conclusion or publication approval.

All proposed observations/indexes stay internal and are not serialized by public DTOs. Do not publish Object
prose, additional raw tags, media, source locators or resolver diagnostics. Existing Object actions are
intentionally withheld; this is not a public completeness correction. There is no public contract or direct
Java DTO constructor change proposed. Recipe and Homecraft decisions remain unchanged.

## 9. Verification and reproducibility

Run from the repository root with Node and Java 21+, using the existing compiled application classes/dependency
classpath from the verified baseline. No new production compilation or Maven run is needed for this audit:

```powershell
$probeClasspath = (Join-Path (Get-Location) 'target/classes') + ';' +
    (Get-Content target/analysis-classpath.txt -Raw).Trim()
java --class-path $probeClasspath analysis/mechanics/batch-10-object-relationships/ObjectRelationshipProbe.java
node analysis/mechanics/batch-10-object-relationships/build-gold-set.mjs
node analysis/mechanics/batch-10-object-relationships/audit-source.mjs
git diff --check
```

For the complete two-run determinism check, use
`node analysis/mechanics/batch-10-object-relationships/verify.mjs` (optionally pass the Java 21+ executable
path as its first argument). This invokes the same three analysis steps twice and compares all four generated
JSON evidence files. It does not run Maven or modify existing classes.

`--snapshot` on audit-source was used once before the audit to record protected trees and refuses overwrite.
Do not regenerate that baseline to make a failing verification pass. The scanner compares the complete current
JSON file list and every file hash with the original 175-entry pinned manifest. It fingerprints **all of src**
and **all pre-existing analysis artifacts**; only this new directory is excluded. It preserves the already
dirty Batch 9A/9B working-tree state rather than claiming the repository started clean.

The Java source launcher initially could not resolve application classes inside the filesystem sandbox; the
same read-only probe succeeded with approved execution outside that sandbox. No source/classpath workaround
or application change was made. This environment detail is unrelated to mapping or parser correctness.

Verification results:

- All 175 source JSON hashes and file paths unchanged; exact discriminator scan found only the seven attacks.
- Strict Object DTE loading: 37 roots; normalized actions equal source actions for all 37; seven typed attack
  nodes round-trip to their original JSON structures; all 37 safe detail projections omit actions and entries.
- Existing shared walker/parser/classifier: **147 tags across all Object content**, **14 inside typed attacks**
  (seven `hit`, seven `damage`). All original spans reconstruct; zero malformed parses or span failures.
  The remaining 133 tags are outside the seven containers and are not extra pilot relationships.
- Every gold endpoint resolves, including all textual anchor spans; accepted and rejected structural claims
  are checked independently from prose labels. There are 160 validated endpoints on the 80 labels.
- Repeated probe, gold generation and audit outputs are byte-identical, recorded in the determinism report.
- All production Java, public DTOs/endpoints, tests, data, existing reports and original assessment evidence
  retain pre-task bytes. No reference generation/resolution code or prior outcomes were changed.
- `git diff --check` passes. Maven was not run; **504 passing tests and 6,246 reference occurrences remain the
  reported baseline**, not a fresh suite/resolver execution result.

See [verification.json](verification.json) and [determinism.json](determinism.json) for reproducible checks.

## 10. Remaining risks and open questions

1. Does the product need a navigable internal relationship index for only seven records, given that typed
   membership is already retained? The pilot is a small foundation experiment, not urgent correctness work.
2. Should a future adapter expose both section-level membership and string-member anchors? Keep its exact edge
   granularity explicit so duplicate transitive edges do not distort counts or evaluation.
3. Inline `h` labels and typed `hitEntries` are different representations. Extending the former needs a
   reviewed segment-boundary contract; this pilot supplies no renderer-wide boundary grammar.
4. Which bounded formula/type-word patterns merit independent review? The local corpus's simple layout is not
   sufficient evidence for damage-type inference across Objects, Monsters, Spells or tables.
5. Multiple-formula and nested typed hit variants are absent. They must not receive an invented source-coverage
   claim, and future source upgrades must trigger schema/gold review.
6. Internal exact-token evidence remains subject to existing content/access boundaries. Any public relationship
   metadata or derived mechanics needs a separate field/source publication and compatibility decision.

Recommendation: authorize a narrow internal structural adapter only if the evidence-navigation use case is
desired. Restrict it to S1/S2/T1 on the seven typed containers, retain all abstentions, and defer semantic
relationships. Do not begin another batch automatically.
