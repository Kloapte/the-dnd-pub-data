# Batch 11: Item charge syntax and publication assessment

## Decision

A narrow **internal authored-field syntax adapter is justified**, but a charge-resource engine or automatic
prose extraction is not. The 220-root finding is correct: **219 recovery-field dice tags and one count-field
dice tag**, each occupying its entire source string. It does not describe 220 equivalent gameplay resources.

There is also a newly confirmed, smaller preservation gap: **28 standalone `recharge` declarations survive
source mapping and catalog provenance but are absent from normalized ItemDefinition/public charge metadata**.
These describe several kinds of limited-use behavior. Do not fix the omission by automatically calling them
charge pools or publishing them through the shared ItemChargeDto. Recommend a separate internal preservation
batch first, then the syntax adapter if evidence navigation is desired. Nothing is implemented in this task.

Public charge text already forwards raw tags on **224 effective Items** after four inherited tagged values.
Changing that contract requires a separate publication and consumer migration decision. No new publication
permission follows from parsing the tags.

## Scope, evidence and reconciliation with earlier work

This analysis follows the existing [summary](../entry-assessment/ASSESSMENT-SUMMARY.md),
[taxonomy](../entry-assessment/MECHANICS-TAXONOMY.md),
[model gaps](../entry-assessment/MODEL-GAP-ANALYSIS.md),
[reliability rules](../entry-assessment/EXTRACTION-RELIABILITY.md),
[relationship gaps](../entry-assessment/RELATIONSHIP-GAPS.md),
[shared model candidates](../entry-assessment/SHARED-MODEL-CANDIDATES.md),
[publication boundary](../entry-assessment/PUBLICATION-BOUNDARIES.md),
[Batch 5](../batch-5-syntax-foundation/IMPLEMENTATION.md),
[Batch 10A](../batch-10a-object-membership/IMPLEMENTATION.md) and
[implementation order](../IMPLEMENTATION-ORDER.md). Earlier evidence is unchanged.

The Item domain has **2,512 Item roots and 230 Base Item roots**, yielding the same **2,742 effective
definitions**. Resolving 67 copies enriches existing roots; it does not create 67 extra identities. No Item
version declarations occur. Magic Variant templates and their possible matches remain a separate catalog;
this assessment does not materialize them as Items or include their fields in the Item denominators.

The earlier 220 figure is a publication/syntax boundary, not a missing normalized formula or complete resource
model. The broader C16 probe's 331 structured owners/852 authored occurrences includes 16 Base Item `reload`
fields alongside the 315 Item owners/836 dedicated charge-field occurrences below. Ammunition reload is not a
charge pool. The 852 effective dedicated-field total below is a separate denominator, coincidentally equal.
Completed Item focus/signed-bonus and other Step 7 fixes are not reopened.

### Artifacts and count boundaries

- [source-evidence.json](source-evidence.json): complete 315-root dedicated-field inventory; exact 220-owner
  tagged subset; original field paths/types/values; entry marker locations; all 28 omissions; inherited scalar
  origins; all 67 copy declarations and 143 authored charge spell schedules.
- [runtime-evidence.json](runtime-evidence.json): existing Java runtime results for all 2,512 effective Items,
  field origins, normalized/public charge values and original shared-parser evidence. Base Item absence is
  checked independently in the source scanner; the runtime catalog validates all 2,742 definitions.
- [review-cases.json](review-cases.json): manual source-specific classifications of all 35 marker-bearing
  roots without dedicated fields and 40 proposed review labels with explicit eligibility scopes.
- [audit-source.mjs](audit-source.mjs): parsed-source inventory, source/model comparison, span checks and
  protection of every existing source/code/test/report file.
- [ItemChargeProbe.java](ItemChargeProbe.java): analysis-only driver invoking existing catalog, copy resolver,
  public mapper and shared syntax components. It introduces no production annotation model or extractor.
- [review-cases.mjs](review-cases.mjs): reproducible reviewed manifests and proposed gold expansion.
- [verify.mjs](verify.mjs), [verification.json](verification.json), [determinism.json](determinism.json):
  two independent JVM runs, deterministic outputs and file integrity checks.

The shared parser checks **7,926 authored Item tags**, including copy payloads; all spans reconstruct.
The report retains **2,090 candidate tag observations**: formulas, charge-marker text tags and dedicated-field
tags. This selected retention count is not a charge-effect count. No malformed syntax or span failures occur.
The Java selection filter uses charge/charges/recharge words, plus all formulas and dedicated fields. The
broader source scanner also includes charged/charging for negative-case review. Every tag is still parsed;
the selected syntax report is not claimed to contain every broad lexical marker occurrence.
Base Items contain no dedicated charge fields or charge-marker matches; their 16 numeric reload fields remain
separate. Effective entry annotations are **not** materialized or counted as authored syntax.

## 1. Exhaustive dedicated-field inventory

All paths in this section are relative to `src/main/resources/data/items.json`, collection `/item`.
The inventory includes owner name, existing ID, primary source/page, exact root pointer, each field pointer,
source value/type, entry marker locators and tags. `taggedOwnerPointers` enumerates every one of the 220 roots.

| Source field | Authored roots/occurrences | Observed JSON shapes | Effective occurrences | Inherited occurrences |
|---|---:|---|---:|---:|
| charges | 287 | 286 integers; one tagged string | 291 | 4 |
| recharge | 289 | Strings only, five literal codes | 295 | 6 |
| rechargeAmount | 260 | 40 integers; 219 tagged strings; one plain string | 266 | 6 |
| Union of owners | **315** | Three distinct field roles | **319** | Six owners inherit at least one field |
| Field occurrences | **836** | Count each authored field once | **852** | **16** |

No explicit null, empty string, Boolean, array, object, zero or negative scalar value occurs in these fields.
Absence is common and remains absence. Source integers are positive, with charges ranging from 1 to 66.
The typed amount deserializer accepts a wider scalar input surface than these observations; this is not
evidence for Boolean or arbitrary-object mechanics. No unsupported source variant is invented here.

### The 220 tagged roots

- Every tagged root has an authored `charges` field. **219** have a tagged `rechargeAmount`; the remaining
  root, Stonemaker War Pick BMT, has the tagged `charges` value and no recharge amount.
- **217** have an authored recharge code. Robe of Stars DMG/XDMG have recovery amounts but no recharge code;
  their dusk timing is prose. Stonemaker has no recharge code or recovery field.
- There are **220 tags**, one per field/root. Every one is a single `dice` tag, one argument, no nested tag,
  no surrounding text, and a bounded Batch 5 DICE_NOTATION expression. No damage/scaledice/scaledamage,
  modifier, symbolic or reference tags occur in these dedicated fields.
- The 220 roots also contain **399 entry formula observations**, which must not be conflated with their
  dedicated-field observations. Examples include damage, result selection and last-charge destruction rolls.

All 18 recovery expression spellings and their authored frequencies are:

| Token inside dice tag | Count | Token inside dice tag | Count |
|---|---:|---|---:|
| 1d3 | 71 | 1d6 | 32 |
| 1d6 + 4 | 32 | 1d6 + 1 | 29 |
| 1d4 | 13 | 1d4 + 1 | 13 |
| 1d4 + 3 | 6 | 1d4 + 4 | 5 |
| 1d10 | 3 | 2d8 + 4 | 3 |
| 1d20 | 2 | 1d8 | 2 |
| 1d4 + 2 | 2 | 4d6 + 2 | 2 |
| 1d6 + 3 | 1 | 1d4 - 1 | 1 |
| 6d6 | 1 | 1d8 + 2 | 1 |

These sum to 219. Stonemaker's count expression is also `1d6 + 1`, in a different field role. A negative term
is present in `1d4 - 1`; no evaluation, clamping, minimum or probability is derived. Key Card BMT
`/item/1175/rechargeAmount` is the plain string **"5"**, not a dice tag or a JSON integer. Its existing
number-versus-text distinction must remain intact.

Recharge codes are `dawn` 279, `dusk` five, `midnight` two, `special` two and `restLong` one.
Codes are authored labels, not executable event definitions or complete recharge conditions.

Primary-source distribution of the 220 tagged owners, **39 sources**:

AI 2; AU 9; AUD 2; BGG 2; BMT 10; CoA 1; CoS 2; CRCotN 1; DMG 45; EFA 2; EGW 4; ERLW 2;
FRAiF 1; FTD 1; GGR 12; HAT-LMI 1; HotDQ 2; IDRotF 1; KftGV 5; LMoP 2; MCV2DC 1; MOT 2;
NF 2; OotA 2; PaBTSO 4; PotA 2; QftIS 1; SCC 5; SKT 2; TCE 13; TftYP 2; ToA 2; UtHftLH 1;
VEoR 1; WBtW 1; WDH 3; WDMM 1; XDMG 63; XGE 5. All primary codes match the existing Source Registry.
No registry or publication metadata change was necessary.

## 2. Entry content and nonuniform resource meanings

The scanner walks parsed string leaves, preserving source pointers and string hashes. Its bounded marker
`charg(e/es/ed/ing)` or `recharg...` identifies **633 leaves on 316 owners**, including metadata names and
copy payloads. Among them, 478 leaves contain an expenditure word, 328 a recovery word and 280 a conditional
word. These are overlapping **lexical review leads**, not measured mechanical rules or automatic associations.

Within the 220 tagged roots, 218 have an expenditure word in a charge-marker entry leaf, 217 have a recovery
word, and 141 have a conditional word. These counts are not sufficient to infer expenditure-to-ability links,
reset conditions or an absence of conditions on the other roots. The two Robe of Stars records illustrate
why word matching is incomplete: they consume/replenish stars. A reset can be described without the word
charge or recharge, and a conditional sentence can concern destruction rather than recovery.

### Complete review of fieldless marker owners

All **35** marker-bearing roots without any of the three dedicated fields were reviewed and classified in
review-cases.json. This is exhaustive for that lexical subset, not a claim that every possible prose-only
use/rest mechanic can be found by these words.

| Reviewed meaning | Roots | Exact root indexes |
|---|---:|---|
| Own charge quantities/use described in prose | 7 | 1532, 1664, 1819, 1820, 2086, 2163, 2379 |
| Own recovery delegated/described via another Item function | 1 | 2445 |
| Other uses/cooldown/creature recharge/restoration | 10 | 86, 103, 157, 212, 252, 920, 1378, 1733, 2446, 2462 |
| Another Item's charges inspected or recovered | 3 | 102, 564, 2507 |
| Copy inherits actual charge fields | 1 | 410 |
| Charged-with-magic/crystals wording, no charge pool established | 13 | 1276, 1277, 1282, 1283, 1286, 1287, 1690, 2317, 2318, 2319, 2320, 2325, 2326 |

The seven own-pool cases are Planar Key AitFR-THP, Powered Armor LLK, Ring of Three Wishes DMG/XDMG,
Soul Coin BGDIA, Staff of Charming DMG and Vial of Thought Capture AZfyT. Waythe TftYP is the delegated
case. These are manual descriptions of source evidence, **not** newly extracted normalized fields.
Charred Wand WDH is not entry-only effectively: it inherits structured fields from its parent.

### Positive/negative and conditional examples

| Source owner and exact path | Evidence and consequence |
|---|---|
| Stonemaker War Pick BMT, `/item/2224/charges` | Tagged variable count; preserve COUNT_FIELD role. Do not call every formula recovery, maximum or current charges |
| Wand of Magic Missiles DMG, `/item/2408/rechargeAmount` | Entire dedicated recovery string is one dice tag; exact formula preservation is deterministic |
| Robe of Stars DMG/XDMG, `/item/1840/entries/1`, `/item/1841/entries/1` | Six removable stars and dusk reappearance; root charges/rechargeAmount encode stars, while timing remains prose |
| Key Card BMT, `/item/1175/entries/0` and root fields | Five uses and dawn recovery; `charges` does not guarantee that descriptive text calls the resource charges |
| Helm of Devil Command BGDIA, `/item/978/recharge`, `/entries/2` | Code special; prose ties recovery to 24 hours after the last expenditure. No structured condition/duration field |
| Staff of Fate CM, `/item/2168/entries/1/entries/1` | Last-charge destruction roll and conditional recovery formula share a leaf. No root rechargeAmount; do not pair nearest dice with recharge automatically |
| Staff of Charming DMG, `/item/2163/entries/2` | Recovery and last-charge destruction dice coexist; formula membership is exact, their roles require text interpretation |
| Planar Key AitFR-THP, `/item/1532/entries/1` | Random initial count with explicit no-regain prose. A dice tag is not a recharge declaration |
| Arcane Battery NF, `/item/102/entries/0` | Formula restores another magic Item, not an owned battery charge pool |
| Bag of Tricks Gray DMG, `/item/145/entries/1`, `/entries/4/colLabels/0` | Outcome-selection dice, not recharge amount; source recharge code is separately authored |
| Powered Armor LLK, `/item/1664/entries/4/items/0`, `/entries/5` | Ability formula/cost alternatives and energy-cell count occur in different contexts. No automatic armor-pool ownership or expenditure model |
| Manual of Bodily Health DMG, `/item/1276/entries/0` | Charged-with-magic language is not a charge resource. Do not convert the word into a count/recovery fact |

There are no symbolic formula tokens in dedicated charge fields. In charge-marker text leaves, 363 formula
tags occur (333 dice, 30 damage). The bounded classifier recognizes 310 dice-notation tokens and retains 53
opaque tokens, all observed spellings `d20`, `d4`, `d6` or `d100`. OPAQUE_OR_SYMBOLIC is a lexical category;
these omitted-count dice spellings are not evidence of an unknown character-dependent charge quantity.
Expressions such as all expended charges or cost alternatives remain prose and are not evaluated.

## 3. Source → DTE → normalized → public trace

| Source | Existing typed representation | Normalized representation | Public behavior / coverage |
|---|---|---|---|
| charges number/string | ItemSourceDte.charges: ItemNumberTextDte(number,text) | ItemChargeDto.count/countText in ItemDefinition.charges | Forwarded by ItemDto.from; 287 authored/291 effective owners |
| recharge string | ItemSourceDte.recharge: String | ItemChargeDto.recharge only when charge DTO is constructed | 28 standalone declarations omitted; the other 261 authored declarations survive, plus six inherited |
| rechargeAmount number/string | ItemSourceDte.rechargeAmount: ItemNumberTextDte | ItemChargeDto.rechargeAmount/rechargeAmountText | Existing raw text, including dice markup and plain "5", forwarded exactly |
| attachedSpells.charges | ItemAttachedSpellsDte → ItemSpellGrantDte(direct,byUses) | ItemSpellScheduleDto → ItemSpellGrantDto | Literal bucket/list structures already public; not selected spells or automatic grants |
| entries/additionalEntries and copy modifications | Shared typed EntryDte and Item copy DTEs | Retained in source/provenance and resolved entry lists, not ItemDefinition charge mechanics | Main descriptions not included in ItemDto; no authoritative expenditure/condition extraction |
| Base Item reload | ItemSourceDte.reload: Integer | ItemDefinition.reload | Already public as ammunition/equipment metadata; separate from magical charges |

Java trace anchors:

- [ItemSourceDte](../../../src/main/java/com/thedndpub/data/dte/item/ItemSourceDte.java), fields around lines 38–40;
  [ItemNumberTextDeserializer](../../../src/main/java/com/thedndpub/data/dte/item/ItemNumberTextDeserializer.java)
  preserves observed numeric versus text input. No tag parsing occurs there.
- [ItemMapper](../../../src/main/java/com/thedndpub/data/dto/item/ItemMapper.java), `map` around lines 33–34 and
  66–71, and `value` around line 430: nearest non-null scalar source/ancestor wins independently by field.
- [ItemDefinition](../../../src/main/java/com/thedndpub/data/dto/item/ItemDefinition.java), `charges`;
  [ItemChargeDto](../../../src/main/java/com/thedndpub/data/dto/item/ItemChargeDto.java), the five-component
  public/shared record; [ItemDto](../../../src/main/java/com/thedndpub/data/dto/item/ItemDto.java), `from` forwards
  `item.charges()` unchanged.
- [ItemAttachedSpellsDeserializer](../../../src/main/java/com/thedndpub/data/dte/item/ItemAttachedSpellsDeserializer.java)
  and ItemMapper `attachedSpells`/`spellGrant` preserve literal schedule keys and arrays. No filter expansion.
- [ItemCatalog](../../../src/main/java/com/thedndpub/data/services/ItemCatalog.java), constructor retains
  private ItemImportProvenance/source/ResolvedItemDte and auxiliary data. ItemDefinition has no main entries
  field; retained importer content is not a normalized resource model.
- [ItemPipelineTests](../../../src/test/java/com/thedndpub/data/item/ItemPipelineTests.java), around lines 134–137,
  already asserts the raw Wand recovery tag and a literal charge spell bucket. These contracts are unchanged.

### Confirmed normalization omission

ItemMapper only constructs ItemChargeDto when `chargeCount != null || rechargeAmount != null`. An authored
recharge code alone is therefore dropped from ItemDefinition. The runtime audit confirms all **28** cases:
six Bag of Tricks color/edition records, 21 BMT cards and Ventilating Lungs ERLW. Their complete names/IDs,
source pointers and original codes are in `lostRecharge` in source-evidence.json. All 28 are local authored
declarations, not inherited or ambiguous source matches; none has a public charge DTO.

This is **PARTIAL normalized preservation**, not loss from strict source deserialization. There is no evidence
that the code is intentionally withheld for licensing; the guard is based on amount presence. However, a
public charges object may deliberately be inappropriate for these other use/reset concepts. The smallest safe
follow-up is internal preservation of the exact standalone code with its source context, without changing
ItemChargeDto or manufacturing counts. No source-derived eligibility/reset engine is required.

There are no authored root `rechargeConditions`, `uses`, `maxCharges` or `consumes` fields in this family.
Timing/conditions beyond the recharge code, charge destruction, expenditure-to-ability relationships and many
uses are EntryDte prose. Do not invent missing properties to fill an expected universal charge schema.

### Structured spell schedules and references

`attachedSpells.charges` occurs on **143 authored roots**, with **312 literal buckets** and **431 spell-string
occurrences**. The observed charge schedule shape is an object of string arrays, with keys
`0,1,2,3,4,5,6,7,8,12`. Four authored buckets use literal key `0`; preserve it. The DTE supports broader schedule
variants elsewhere, but no array-valued charge schedule was observed here.

After inheritance, **147** effective Items have charge schedules with **438** spell-string occurrences.
Literal bucket-to-spell membership is directly structured and already normalized; do not duplicate it as
new grants or assume a bucket defines a complete casting rule. Public ItemDto already forwards this structure.
Its strings are not automatically authoritative target IDs.

[SourceAuthoredReferenceExtractor](../../../src/main/java/com/thedndpub/data/references/SourceAuthoredReferenceExtractor.java)
currently extracts Item declarations through `extractBaseItemReferences`, using authoredBaseItemReferences.
It does not create new Item charge-spell occurrences from these schedules or resolve dice tags. ItemsService
adds the existing PublicReferenceProjection separately. **431 spell strings are not 431 new resolver
occurrences.** Any future reference expansion belongs to separately authorized reference work.

## 4. Reliability and relationship matrix

| Candidate | Reliability | What may be retained | Required abstention |
|---|---|---|---|
| Item → exact charges field | DIRECT_STRUCTURE | Existing value/variant, source field role and owner | Maximum/current/initial interpretation not uniformly explicit |
| Item → rechargeAmount field | DIRECT_STRUCTURE | Existing amount or text, source field role | Evaluation, cap and actual applied recovery |
| Item → recharge code | DIRECT_STRUCTURE | Literal code including special/restLong | Complete reset condition, time arithmetic or event execution |
| Dedicated field → exact dice tag | TAG_SYNTAX | Exact token, arguments, UTF-16 span, one of 18 spellings | Numerical result, modifier application or probability |
| Entry/table/list → formula tag | TAG_SYNTAX | Original section/leaf membership only | Formula role inferred from containing Item having charges |
| Nearby formula → recharge/expenditure | LOCAL_CONTEXT at best | Proposed reviewed association only | No automatic nearest-tag/keyword pairing |
| Last-charge roll → recovery/destruction branch | PROSE_INFERENCE | Keep original source evidence | Branch/outcome/cost/execution model |
| Recharge condition → duration/trigger | PROSE_INFERENCE | Exact source/prose retained internally | Invented structured reset schedule |
| Charge bucket → literal spell string | DIRECT_STRUCTURE, already modeled | Existing key, order/list membership and qualifiers | Spell grant, eligibility, source default or targetId inference |
| Expenditure prose → named ability/spell | PROSE_INFERENCE | Source tags as mentions, if separately indexed | Guaranteed effect or consumption relationship |
| Battery/mythallar formula → owner's own pool | UNSUPPORTED | Correct authored owner and text evidence | Owner is not automatically resource recipient |

Parsing a dedicated recovery field establishes its **authored field role**, stronger than a generic formula in
description text. It still does not define the resource name, maximum, cap, exceptions, timing or a complete
recharge process. Numeric/string variants already preserved in ItemChargeDto must not be re-extracted as new
authoritative mechanics.

## 5. Copy/version and provenance

ItemCopyResolver resolves 67 copies without new Item identities; no `_versions` occur. Scalar inheritance is
independent by field. Copy modifications in this corpus affect `entries` and/or `property`, not the three
charge scalars. Entry operations observed are insertArr, appendArr and replaceArr. The full copy manifest
includes operation and target owner; no unsupported transformation is silently evaluated by this analysis.

| Effective owner | Scalar source origin | Inherited fields |
|---|---|---|
| Charred Wand of Magic Missiles WDH `/item/410` | Wand of Magic Missiles DMG `/item/2408` | charges, recharge, rechargeAmount |
| Devlin's Staff of Striking ToA `/item/585` | Staff of Striking DMG `/item/2180` | All three |
| Jakarion's Staff of Frost CoS `/item/1146` | Staff of Frost DMG `/item/2173` | All three |
| Wyllow's Staff of Flowers WDMM `/item/2494` | Staff of Flowers XGE `/item/2172` | All three |
| Jewel of Three Prayers Awakened CRCotN `/item/1156` | Dormant `/item/1157` | recharge, rechargeAmount; count overridden locally |
| Jewel of Three Prayers Exalted CRCotN `/item/1158` | Dormant `/item/1157` through Awakened | recharge, rechargeAmount; count overridden locally |

This accounts for **16 inherited fields on six owners**, including four inherited tagged recovery strings.
Root-field origins are reproducible: the probe follows the actual resolved chain to the nearest non-null
typed field, then independently matches its original JSON value and pointer. Optional future effective links
could retain this original parent evidence and distinct effective owner. They must never assign an inherited
string a nonexistent child-local authored span.

The authored copy modification under `/item/410/_copy/_mod/entries/items` adds a conditional charge-related
rule. Entry modifications can append/insert/replace nodes and change effective positions; raw modification
payloads are AUTHORED_TRANSFORM_INSTRUCTION evidence, not effective entry annotations. The probe records
effective entry counts/hashes, but does not claim complete entry-node origin maps or effective formula totals.
Starting with authored field observations avoids pretending that copied/modified prose originated at new
child pointers. No new transformed text/lineage is fabricated.

## 6. Publication and compatibility assessment

Current public ItemChargeDto exposes count, countText, recharge, rechargeAmount and rechargeAmountText under
`charges`, with NON_EMPTY behavior. ItemDto forwards it directly; the normalized definition and public DTO
share that type. **287 authored-equivalent charge DTOs become 291 effective DTOs**, of which **224 expose raw
tagged text**. The 28 standalone reset codes have no DTO and remain absent publicly. Existing integer/text
values are not recalculated; the plain "5" remains rechargeAmountText.

Two semantic cautions are established, not an assertion that all public data is wrong:

1. A `charges` object can represent stars or uses. `count` is not documented source evidence for a universal
   maximum/current value. Clients treating every record as one consumable charge-pool mechanic can be misled.
2. Recharge codes/amounts are incomplete for conditional rules, and can be absent while descriptive timing
   exists. Their presence is not a complete schedule; their absence is not proof of no reset rule.

Raw markup exposure is a previously identified boundary, not authorization to remove fields here. The repository
test explicitly expects at least one raw recovery tag. No inventory of external clients establishes that
renaming/removing fields is safe. Adding components to this shared record changes both public JSON and direct
Java constructor/binary contracts; do not put internal observations into ItemChargeDto. Use a separate internal
index if approved. An additive public alternative would still need a reviewed migration/version policy and
tests preserving existing fields, meaningful false/zero and omission behavior.

| Candidate | Classification | Reason |
|---|---|---|
| Authored field/tag evidence index with exact internal spans | INTERNAL_ONLY | No public projection; separate from existing authoritative scalar models |
| Preserve the 28 standalone recharge codes in a separate internal representation | INTERNAL_ONLY | Confirmed source distinction; no permission to publish new Item resource metadata |
| Keep existing reviewed field shapes/serialization unchanged | SAFE_UNDER_EXISTING_POLICY for compatibility continuation | No new content exposure or new rights claim; existing raw-text concern remains unresolved |
| Public formula-only alternative, resource discriminator, completeness/field-presence marker | REQUIRES_PUBLICATION_APPROVAL | Additive is technically possible but field/source/consumer permission is not established |
| Replace/remove raw countText/rechargeAmountText | REQUIRES_PUBLICATION_APPROVAL plus explicit compatibility migration | Cannot silently alter existing JSON or Java consumers |
| Public reset conditions, expenditure-to-spell rules, resource execution | DEFER | Prose interpretation and publication approval both missing |

The existing project publication boundary applies: code licensing does not license the dataset; source
registry/book metadata and SRD flags do not clear every field automatically. This assessment performs no new
legal clearance, SRD equivalence audit or attribution determination. Parsed/derived numeric facts do not
automatically become publishable. Source locators, raw tags, diagnostics and source prose remain internal
for any proposed adapter. No policy permits automatically publishing all short charge expressions.

## 7. Proposed bounded gold set and acceptance

[review-cases.json](review-cases.json) proposes **40 labels**: **33 ACCEPT, six ABSTAIN, one REJECT**.
Each has exact valid source endpoints, spans for tags, relationship, reliability, reason and scope. It is
reviewable evidence, not independently adjudicated semantic ground truth or measured extractor accuracy.

- 19 authored-field syntax positives: one representative of each of 18 formula spellings, plus the separate
  Stonemaker count-field role. The complete 220-observation inventory is the exhaustive future coverage oracle.
- Six existing structured baseline labels cover literal code/amount variants and the standalone-field gap.
- Six entry-syntax controls retain actual formula membership; they are outside the initial dedicated-field
  adapter scope. Each paired semantic role claim abstains (six labels), including recovery versus destruction
  dice, external Item restoration and selection dice.
- One optional effective-provenance positive points from a copy owner to verified parent evidence; one literal
  zero spell-bucket baseline remains unchanged. One negative rejects claiming the inherited formula is locally
  authored. Both endpoints are real source evidence; no false child pointer is presented as original evidence.

Eligibility scopes are explicit so future tests do not count entry controls or optional effective links as
false negatives of a smaller authored-field adapter. No 40-label semantic success percentage is proposed.
Before any LOCAL_CONTEXT extraction, independently review/adjudicate the exact patterns with additional
counterexamples; leave current semantic candidates as abstentions.

Future adapter acceptance:

1. Consider all 2,512 Item roots and confirm Base Item exclusion; exactly 220 authored dedicated-field
   observations across 220 owners, including the one count-field observation and 219 recovery observations.
2. Match source fingerprints, existing owner IDs, original JSON Pointers, hierarchy, complete original tags,
   argument order and UTF-16 spans. Parse every supported occurrence; retain/report unsupported grammar and
   fail/report malformed input rather than silently repairing or dropping it.
3. Keep field roles separate and preserve number/text/absence distinctions already represented elsewhere.
   No evaluation of dice, symbolic substitutions, maxima, timing, cap or actual charge state.
4. Do not materialize the six semantic candidates or promote any entry-syntax control to a charge rule.
   Use exact-location set comparison for eligible TP/FP/FN and report excluded controls/abstentions separately.
5. Synthetic tests may cover multiple/nested tags, Unicode, repeated locations and malformed syntax, but must
   not claim those are observed dedicated-field variants. Current fields are single flat complete tags.
6. If effective provenance is separately authorized, reconcile four inherited tags and preserve both effective
   ownership and original parent evidence. Do not count 224 as authored coverage or create effective entry spans.
7. Require immutable deterministic output, existing public JSON/identities/mechanics and full resolver
   fingerprints unchanged, all 175 pinned hashes, focused/full tests and packaged-JAR loading for production work.

## 8. Recommended implementation order

### First: internal standalone recharge preservation

Separately authorize preservation of the **28** currently omitted literal codes in a narrow internal Item
representation. Do not attach it to the shared public ItemChargeDto or reconstruct counts/uses from prose.
Keep existing charge DTO behavior/public JSON unchanged. Tests should exhaust all 28 roots, all 2,742 effective
identities, absence, existing copy behavior and field-source provenance. This is a correctness/preservation task;
it need not implement a syntax adapter or resource model.

### Then: internal authored charge-field syntax adapter

Use Batch 5 SyntaxSourceOwner, SyntaxContext, SyntaxTextLocation, SyntaxEvidence, SyntaxSpan, MarkupTag,
FormulaSyntaxObservation and existing parser/classifier. A small Item-specific observation needs only existing
Item ID, explicit source-field role (CHARGES_FIELD / RECHARGE_AMOUNT_FIELD), deterministic ordinal and shared
observation/evidence. Numeric values/codes stay in existing typed fields or the separately preserved reset
metadata; do not duplicate them as effects. A separate immutable startup index is appropriate.

Initial scope is the **220 authored flat tags**, not arbitrary entry formulas, attached spell interpretation,
copy-expanded descriptions or public fields. Supported relations are original owner→field and field→formula
membership, not maximum/recovery event/spell expenditure. Effective parent links are optional later work,
not a requirement to fabricate complete effective annotation coverage.

### Separate review: public charge contract

Decide source-specific publication eligibility and external-client migration before adding safe-expression or
completeness/resource-kind metadata, changing legacy text fields, or publishing standalone reset declarations.
No production Batch is implemented or automatically started by this assessment.

## 9. Verification and reproduction

The snapshot in protected-files.json fingerprints **every existing src file and every previous analysis file**
before work. The scanner verifies exact old src/analysis file sets (excluding this new directory), not merely
selected file hashes. It independently checks all 175 JSON hashes against the prior pinned manifest. Existing
dirty Batch 9A/10A changes were preserved as found; this task does not claim a clean Git checkout.

Reproduce with Node and Java 21+ from the repository root, using the complete compiled/dependency baseline
already verified for Batch 10A:

```powershell
node analysis/mechanics/batch-11-item-charges/verify.mjs "C:/Program Files/Java/jdk-21.0.12.1/bin/java.exe"
git diff --check
```

verify.mjs runs the analysis-only Java probe and both source/review scripts twice. It uses target/classes and
the dependencies extracted by the existing Batch 10A packaged probe. The analysis mapper copy sorts map entries
to remove JVM immutable-map iteration variation; this changes no production serialization setting. The four
generated evidence files are byte-identical across independent runs. No source/prose is written outside this
new analysis directory; execution logs/build artifacts are not source changes.

Results: all **175** pinned source hashes unchanged; production Java, public DTOs, endpoints, tests, resolver
code and previous analysis evidence unchanged; all 7,926 parsed Item tag spans reconstruct; 2,090 retained
candidate spans also independently resolve in Node; `git diff --check` passes. Maven was not run. **518 tests
and 6,246 resolver occurrences remain the reported baseline**, not a fresh full-suite/result comparison.

## 10. Open questions

1. What is the intended internal home for standalone reset codes without calling uses/stars/ability resets
   charge pools? Preserve source role first; a universal ResourceUsage record is not justified.
2. Which clients rely on raw charge markup or interpret count as maximum/current? Repository tests establish
   compatibility requirements but cannot answer external consumer usage.
3. Which source/field subsets have publication approval, including inherited content and source-specific
   variants? This assessment provides provenance, not rights clearance.
4. Should a future authored syntax index include inherited scalar links? They are feasible for six copy owners,
   but must not imply complete effective entry provenance.
5. Would an entry-level recharge/expenditure pilot add value after field syntax? It would need independent
   semantic adjudication, boundary/branch/cost counterexamples and deliberate abstention. No such inference is
   approved here, including the seven prose-only charge-pool records.
