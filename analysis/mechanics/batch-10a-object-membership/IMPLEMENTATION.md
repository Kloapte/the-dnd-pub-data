# Batch 10A: internal Object evidence membership index

Implemented and verified. The [design checkpoint](DTO-DESIGN.md) preceded production implementation.
The unchanged [Batch 10 assessment](../batch-10-object-relationships/ASSESSMENT.md) and its 80-label gold set
define the boundary. This is an internal, startup-loaded evidence index; no gameplay effect, Object definition,
public DTO, endpoint or resolver behavior was added or changed.

## Model and integration

Production components:

| Type | Contract |
|---|---|
| [ObjectEvidenceAnchor](../../../src/main/java/com/thedndpub/data/dto/object/evidence/ObjectEvidenceAnchor.java) | Existing SyntaxTextLocation plus optional SyntaxSpan. Structural nodes/arrays/scalars and complete string members have no span. Tag observations alone have original UTF-16 spans |
| [ObjectEvidenceMembership](../../../src/main/java/com/thedndpub/data/dto/object/evidence/ObjectEvidenceMembership.java) | Existing Object ID, per-owner occurrenceIndex, relationship kind, parent and child anchors, optional exact attackType value, optional existing SyntaxObservation |
| [ObjectMembershipKind](../../../src/main/java/com/thedndpub/data/dto/object/evidence/ObjectMembershipKind.java) | Seven source-membership kinds, listed below; no interpreted effects or eligibility relations |
| [ObjectEvidenceMembershipIndex](../../../src/main/java/com/thedndpub/data/dto/object/evidence/ObjectEvidenceMembershipIndex.java) | Immutable ordered list and per-Object immutable lookup, including empty results for Objects without typed attacks |
| [ObjectEvidenceMembershipAdapter](../../../src/main/java/com/thedndpub/data/importer/object/ObjectEvidenceMembershipAdapter.java) | Strict ObjectFileDte validation, exact reconciliation with existing definitions, original source traversal and Batch 5 parsing/classification |
| [ObjectEvidenceMembershipCatalog](../../../src/main/java/com/thedndpub/data/services/ObjectEvidenceMembershipCatalog.java) | Internal Spring bean built once after ObjectCatalog; no controller/service projection dependency |

SyntaxTextLocation already validates source-owner membership and ancestor pointers without requiring a text value.
It is reused for original structural locations too; a structural anchor must not be interpreted as a text span.
Its SyntaxSourceOwner preserves resource `data/objects.json`, exact-byte SHA-256, `object` collection and authored
root pointer. Ordered SyntaxContext ancestors retain their original pointers/type labels. Origin is AUTHORED.
Only original Object roots are supported: strict source DTEs reject copy/version/transform fields, and no
effective lineage is inferred. Objects in this pinned corpus have no copy/version expansion.

Span semantics remain Batch 5's decoded-Java-String UTF-16 `[startInclusive,endExclusive)`, not JSON file bytes
or Unicode code-point offsets. Existing SyntaxEvidence is retained exactly on each SyntaxObservation. There are
no invented offsets for an attack object, section array, metadata scalar or complete entry node. Exact full
strings remain in existing source/definition content; the index does not duplicate source paragraphs.

Membership construction rejects cross-owner/cross-section boundaries, non-authored origins, span/observation
mismatches, inappropriate observation types, and inappropriate scalar values. The index validates unique known
Object IDs and sequential per-owner ordering; all returned collections are immutable.

## Relationship kinds and counts

| Kind | Pinned count | Source claim |
|---|---:|---|
| HAS_ATTACK_SECTION | 7 | Exact typed attack node owns its attackEntries array |
| HAS_HIT_SECTION | 7 | Exact typed attack node owns its hitEntries array |
| HAS_AUTHORED_ATTACK_TYPE | 7 | Exact typed attack node owns its attackType scalar/code |
| SECTION_CONTAINS_MODIFIER_TAG | 7 | Actual section string contains a shared ModifierSyntaxObservation |
| SECTION_CONTAINS_FORMULA_TAG | 7 | Actual section string contains a shared FormulaSyntaxObservation |
| SECTION_CONTAINS_ENTRY | 14 | Actual section array contains its ordered text member |
| ENTRY_CONTAINS_SYNTAX | 14 | Original text member contains a shared syntax observation at an exact span |
| Total | **63** | 35 gold-positive links plus 28 explicit intermediate links |

The last two kinds expose the intermediate membership requested for entry-level navigation. Their 28 edges are
not additional gameplay facts or additional gold positives. A section-to-formula edge and an entry-to-the-same-tag
edge deliberately have different parents; they are not two formula occurrences. There are **14 distinct tag
occurrences** on the seven typed attacks, not 28 or 63.

Parent/header relations are emitted first (attack section, hit section, metadata), followed by attack-section
members and then hit-section members. Member order follows original arrays; shared parser observations follow
preorder, preserving nested spans and repeated occurrences. occurrenceIndex starts at zero for each Object.
Root order follows the original file, independently of the existing catalog's public sort order.

## Complete source coverage

All **37** authored/effective Objects are reconciled by exact name and primary source against existing
authoritative Object definitions. Unknown/missing/duplicate associations or action mismatches fail loading.
No ID is synthesized by the adapter. The original strict Object DTE validates all fields before evidence
processing; original JSON is used only to recover exact authored paths/hierarchy, not as an untyped domain model.

| Existing Object ID | Original attack pointer, within objects.json | Memberships |
|---|---|---:|
| object:WDMM:animated-ghaunadaur-statue | /object/0/actionEntries/0/entries/0 | 9 |
| object:DMG:ballista | /object/2/actionEntries/0/entries/0 | 9 |
| object:DMG:cannon | /object/5/actionEntries/0/entries/0 | 9 |
| object:RMBRE:captain-a-hole | /object/7/actionEntries/0/entries/0 | 9 |
| object:DMG:mangonel | /object/23/actionEntries/0/entries/0 | 9 |
| object:DMG:ram | /object/25/actionEntries/0/entries/0 | 9 |
| object:DMG:trebuchet | /object/34/actionEntries/0/entries/0 | 9 |

The other 30 roots have empty membership lists. There are meaningful text-only action and nested list/item
structures elsewhere, including Automatic Ballista and Eldritch Cannon. They remain preserved by existing
Object definitions but are outside this adapter's typed-attack scope. No source keyword or inline `h` label
promotes their text into a structured attack. Batch 10 exhaustively found no other `type=attack` nodes across
the 175 source files; its evidence and scripts are unchanged.

The adapter parses only strings belonging to the typed attack sections. It reuses AuthoredSyntaxTextWalker,
BalancedMarkupParser and SyntaxObservationClassifier without changing them or adding another parser. Opaque tags
within an authorized section remain ENTRY_CONTAINS_SYNTAX observations; only existing formula/modifier variants
receive their matching section-to-syntax kind. Nested markup stays in the shared immutable tag tree. A parse or
schema error fails the build of the index; no partial result is published.

## Gold-set reconciliation

The acceptance oracle is the unchanged original [gold-set.json](../batch-10-object-relationships/gold-set.json).
[gold-verification.json](gold-verification.json) records an assertion result for **every one of the 80 IDs**.
Eligibility is determined by the reviewed container scope and gold decision, not by assuming that reliability,
control status and abstention are interchangeable categories.

| Evaluation dimension | Result |
|---|---:|
| Eligible positive labels (A1–A7, ACCEPT) | 35 |
| True positives | 35 |
| False positives in the five-kind eligible output subset | 0 |
| False negatives | 0 |
| Rejected structural claims / true negatives | 9 / 9 |
| Required abstentions / no matching output | 33 / 33 |
| Positive controls intentionally outside adapter scope | 3 |
| Boundary violations | 0 |
| Precision, TP/(TP+FP) | 35/35 = 1.0 |
| Recall, TP/(TP+FN) | 35/35 = 1.0 |

The three positive controls C01/C04/C05 describe real text/list syntax membership, but not one of the seven
authorized typed attack containers. They are verified absent and excluded from eligible recall; this does not
relabel them as incorrect gold facts. All nine negative candidates are verified absent. All eight LOCAL_CONTEXT,
17 PROSE_INFERENCE and eight UNSUPPORTED candidates abstain, with their original decisions preserved in the
reconciliation report. Abstentions are not positive extractions or extra true negatives.

The output's five gold-relation kinds must equal the full set of eligible positive labels, preventing extra
unmatched output from being ignored. The remaining 28 intermediate links are separately tested against source
hierarchy, exact pointers, spans and relation counts. Precision/recall describe only this finite evidence
membership contract, not semantic accuracy over Object prose. No independent semantic adjudication is claimed.

There is no runtime registry of proposed prose effects or gold-set candidates. Abstention means those relation
kinds are not generated; validation retains all 33 explicit no-output decisions. Original source content is
unchanged and remains available internally for a future separately reviewed investigation.

## Tests

**14 new tests** in [ObjectEvidenceMembershipTests.java](../../../src/test/java/com/thedndpub/data/object/ObjectEvidenceMembershipTests.java),
with no pre-existing tests modified:

- All seven containers, seven relationship kinds, 63 records and empty lookups for nonqualifying Objects.
- All 80 gold labels, exact endpoint/resource/span/type matching, eligible output equality, nine boundary
  negatives, 33 abstentions and three excluded positive controls.
- Every source owner, source hash, JSON Pointer, ancestor type, authored origin and UTF-16 span reconstructed
  against the original source; no metadata-node spans.
- Exact MW/RW metadata preservation without interpreting codes or deriving attack statistics.
- Deterministic traversal and sequential per-Object occurrence ordering across repeated builds.
- Immutable lists, indexes and evidence hierarchies, including defensive copying of supplied lists.
- Synthetic multiple formulas in one section and multiple members; repeated tokens remain separate occurrences.
- Synthetic nested note/formula/unknown tags and Unicode offsets; no section promotion or effect inference.
- Empty sections, repeated corpus formulas on different owners and distinct original locations.
- Malformed markup, unsupported structured section members, unknown fields and source/catalog mismatch fail.
- Cross-section/cross-owner membership construction and invalid index owner/sequence are rejected.
- Public projection omits actions/annotations and existing definitions remain byte-equivalent after adaptation.

Multiple formulas in a typed hit section and nested tags there are synthetic robustness fixtures, not claims
about pinned-corpus coverage: the seven actual attacks each contain one hit modifier and one damage formula.
Recursive entry objects in those string arrays remain unsupported and strictly rejected.

## Compatibility, publication and verification

Final full Maven run: **518 tests, zero failures, errors or skipped tests**, from 504 baseline + 14 new tests.
The package goal also succeeded. Current classpath and an isolated packaged-JAR application were both started
and probed; the latter uses BOOT-INF/classes/resources plus libraries from that JAR, with no target/classes
fallback. Both initialize the membership catalog with 63 records and produce identical membership fingerprints.

The [runtime probe](ObjectMembershipRuntimeProbe.java) captured the existing compiled baseline before the new
implementation was compiled. [baseline-runtime.json](baseline-runtime.json), [current-runtime.json](current-runtime.json)
and [packaged-runtime.json](packaged-runtime.json) compare exact digests of all 37 Object identities, complete
normalized Object definitions, public service JSON and all 6,246 resolver result records. Every baseline field
is identical after implementation and in the packaged application.

Resolver outcomes remain 2,517 RESOLVED, 2,054 AMBIGUOUS, 181 UNRESOLVED and 1,494 DEFERRED. The whole result
fingerprint remains `ddefcf233c3279575ce0fbe5fabc9f0f70ede0b2a371f9ad2854cab88ea3c08a`.
Membership fingerprint in both runtime modes is
`42eaee2693ba4a8f6d7cbe3a2d288ac7dfe249c97ef488676d9b842c9721d2a0`.

All **175 source hashes** match both the pre-task snapshot and the earlier pinned manifest. All pre-existing
production, DTO, endpoint and test files retain their pre-task bytes; only new internal classes and a new test
class were added. All pre-existing analysis, including the original Batch 10 assessment/scripts/gold set, is
unchanged except the authorized implementation-order update. The already dirty Batch 9A/9B working state
was preserved. `git diff --check` passes; new files were also checked for trailing whitespace/readability.

The initial baseline probe used an old analysis dependency classpath that lacked Spring Data; rerunning it with
the complete previously packaged dependencies captured the valid baseline. This was a probe setup issue, not
a changed application contract. The final full suite and packaged verification have no outstanding failures.

Reproduction (Java 21+, existing local Maven cache):

```powershell
mvn -o -Dmaven.repo.local=target/m2 package
```

Compile the two analysis probe classes into `target/batch-10a-probe` using application classes and complete
runtime dependencies. Run ObjectMembershipRuntimeProbe with a report path for the classpath result; run
ObjectMembershipJarProbe with the packaged JAR, probe-class directory and report path for the isolated result.
The [JAR launcher](ObjectMembershipJarProbe.java) records no new production code. Finally run:

```powershell
node analysis/mechanics/batch-10a-object-membership/verify.mjs
git diff --check
```

[verify.mjs](verify.mjs) compares all protected old-file hashes, both pinned source manifests, every baseline
runtime fingerprint, packaged equality, gold metrics and the full-suite log. It handles PowerShell UTF-16 log
encoding explicitly. Machine-readable results are in [verification.json](verification.json).

## Exclusions and remaining limitations

No damage type, receiver, target, range, save outcome, condition, trigger, duration, probability, branch,
resource use or applied effect is inferred. No new reference, target kind, source default or compatibility
relationship is generated. No ObjectDefinition field or public API/JSON field changes. Exact tags and source
locators stay internal; syntax reliability does not authorize publication of imported prose or formulas.

This index covers authored Object membership only, not text-only actions, nested list-item promotion, effective
Monster origins or a universal evidence graph. The small gold set is exhaustive for the seven typed attacks
and cannot establish general prose extraction accuracy. No semantic candidate from Batch 10 is newly approved.

Recommended next Step 7 task: the already planned **Item charge syntax/publication assessment**, with a fresh
source/consumer review before implementation. Do not expand this pilot into Object effects or start another
batch automatically.
