# P1D — Internal Spell publication infrastructure

## Design checkpoint

Implement a dedicated internal `publication.spell` package and startup component, independent of
public mappers. Required runtime resources under `publication/spell-formulas/v1/` contain a
fingerprint-pinned projection of the 250 P1C candidates, licensed evidence and proposed notices.
They contain no analysis paths. The optional decision resource is absent initially.

Separate candidate evidence, reviewer decisions, final authorization and notice configuration.
A trusted packaged policy pins the resource bytes, review/scope revisions and permitted reviewer
roles. Initially its authority list is empty and release approval is null; notice approval is
also null. Candidate initial review state remains PENDING_REVIEW. Verified matching is not a grant.

Validate all candidate/index joins even with no decisions. Strict typed Jackson input rejects
unknown fields, duplicate keys, unknown/numeric enum values, coercion and malformed documents.
Decision events use explicit supersession and a canonical hash chain. Active authorization also
requires reviewer authority, approved notice content and final release approval over the exact
policy/resource hashes. No missing record, SRD flag or source-name fallback grants permission.

The policy is the deployment trust root, not cryptographic proof of a person's identity. Its
authority assignments and approval attestations must originate in a protected human release
process. This batch supplies no such assignments or approvals. Test-only fixtures may exercise
that boundary. No public mapper consumes the resulting internal authorization index.

## 1. Runtime boundary and immutable types

P1D is implemented and verified. It grants **zero production authorizations** and publishes no
formula expressions. P1C's candidate manifest, review journal, schemas and evidence are unchanged.

The following resources are packaged independently of `analysis/` and the 175 pinned source files:

| Runtime resource | Purpose | Initial state |
|---|---|---|
| `publication/spell-formulas/v1/policy.json` | Deployment trust root: dataset/scope, review revisions, exact resource fingerprints, reviewer roles and release attestation | No reviewer authorities, no release approval, no decision fingerprint |
| `candidates.json` | Minimal projection of the frozen P1C review bindings, exact expressions, notation and shared `SyntaxEvidence` | 250 PENDING_REVIEW candidates on 195 Spell owners |
| `evidence.json` | Licensed-match references and versioned document/license metadata | 250 VERIFIED_LICENSED_MATCH records; SRD 5.1 and SRD 5.2.1 only; evidence does not grant permission |
| `notices.json` | Required notice text, change disclosure, disclaimer reference and document association | Two proposed notices, approval null |
| `decisions.json` (optional) | Explicit human decision event journal projected to a runtime envelope | **Absent**, not an empty synthetic approval file |

The resource filenames are fixed by `SpellPublicationSourceLoader`; none is supplied by a source
record or HTTP client. There is no filesystem or network lookup of analysis evidence at runtime.
Only resource identifiers, content digests and the necessary licensed document metadata cross
the review/runtime boundary. PDF text and broad review dossiers remain analysis-only.

Production implementation:

- `services.SpellPublicationCatalog`: eager Spring startup component; depends on `SpellCatalog`,
  `SpellFormulaAnnotationCatalog`, `SourceRegistry` and Jackson. No existing mapper depends on it.
- `publication.spell.SpellPublicationSourceLoader`: isolated strict Jackson mapping and actual
  resource-byte fingerprints. It does not change the application's ObjectMapper.
- `SpellPublicationValidator`: pure validation, explicit decision state transitions and final
  authorization derivation.
- `SpellPublicationHashes`: dedicated sorted-key canonical JSON hashing, independent of global
  JSON omission/formatting. Approval payload hashes exclude their own approval component;
  decision hashes cover the complete payload, excluding only the enclosing event digest.
- `SpellPublicationIndex`: defensive, immutable candidate list, effective state map and authorized
  observation list. Unknown or excluded occurrence IDs return `false` from `isAuthorized`.
- Typed input records: `SpellPublicationPolicyDte`, `SpellPublicationAuthorityDte`,
  `SpellPublicationApprovalDte`, `SpellPublicationCandidateDte`, `SpellPublicationCandidatesDte`,
  `SpellLicensedDocumentDte`, `SpellLicensedFormulaEvidenceDte`, `SpellPublicationEvidenceDte`,
  `SpellPublicationNoticeDte`, `SpellPublicationNoticesDte`, `SpellPublicationDecisionDte`,
  `SpellPublicationDecisionEventDte`, `SpellPublicationDecisionsDte` and
  `SpellPublicationResources`.
- Enums: `SpellPublicationNotation`, `SpellLicenseEvidenceClassification`,
  `SpellPublicationAction`, `SpellPublicationState`, `SpellPublicationRole`.
- `SpellPublicationAuthorization`: internal references to an existing immutable Batch 6
  annotation, licensed evidence, notice and decision. It is not a public DTO or a gameplay effect.

All collection-bearing records defensively copy their inputs. The result preserves candidate
ordering, independent of the order in which decision events were recorded. One public top-level
type is used per Java file; there is no generic JSON tree in production input models.

## 2. Candidate and licensed evidence validation

Validation runs even when there are no decisions:

1. Require schema version 1, pinned dataset `5eTools v2.37.0`, and the exact P1C literal-expression
   scope ID/fingerprint. The scope excludes prose, markup, contextual/scaling relationships and
   gameplay execution.
2. Compare actual runtime resource bytes with the policy's SHA-256 pins. Require exactly the
   policy's candidate count, no duplicate candidates and a pending initial review state.
3. Reconcile the complete Spell/index owner sets, using stable IDs; no name join or empty-list
   fallback is permitted. Each candidate must reference an existing occurrence index.
4. Verify exact source code against both the Source Registry's canonical identity and the Spell.
5. Match the candidate to the existing Batch 6 `FormulaSyntaxObservation`: exact expression and
   lexical kind, `damage`/`dice` notation, one argument, nonopaque literal syntax, complete shared
   evidence locator, authored origin and original tag fingerprint. Source-resource fingerprints,
   JSON Pointers, hierarchy and UTF-16 spans must equal the startup index's existing evidence.
6. Require a unique licensed-match record for every candidate, a VERIFIED_LICENSED_MATCH review
   classification, identical occurrence/expression/review binding, valid document/version/license
   metadata, exact match-reference digest and configured document-specific notice.
7. Require nonempty attribution and change disclosure with matching content fingerprints and a
   valid disclaimer reference. A configured notice is still **unapproved** until separately signed off.

The exporter also verifies P1C's row and licensed-match references against the original P1B
crosswalk before emitting runtime resources. Runtime enforcement trusts the reviewed resource
digests in the protected policy; it does not re-fetch PDFs or repeat P1B's legal/source research.
The original P1C binding digest remains opaque and immutable in this smaller projection, rather
than being recomputed from an incomplete subset of the review dossier.

The 458 excluded observations (147 ambiguous and 311 without verifiable evidence) are not
runtime candidates. A decision referring to any of them fails startup, even if its Spell and
formula occurrence exist in Batch 6. There is no fallback to SRD flags, source names, confidence
scores or the mere existence of licensing evidence.

## 3. Decision and authorization validation

Runtime decision events separate a typed `decision` payload from its `sha256`. The payload uses
the reviewed P1C event fields, including occurrence ID, manifest/binding/scope/attribution digests,
reviewer identity and authority reference, rationale, UTC timestamp, sequence, previous event hash
and explicit superseded event. The runtime envelope is deliberately not the analysis journal schema.

For each supplied event:

- Require a unique event ID, contiguous sequence starting at 1, valid payload hash, hash chain
  and final journal head. Timestamps cannot move backwards; time alone never chooses a winner.
- Require an existing candidate and exact manifest, occurrence binding, scope and attribution
  revision. No wildcard or source-wide authorization exists.
- Match reviewer ID and exact authority reference to the protected policy's
  `OCCURRENCE_REVIEW` role. Require a nonempty rationale and valid UTC review timestamp.
- A later event for an occurrence must explicitly supersede that occurrence's latest event.
  Duplicate active approvals fail, even if their event IDs differ.
- Apply only these state transitions:

| Current state | Permitted action | Result |
|---|---|---|
| PENDING_REVIEW | AUTHORIZE_EXACT_SCOPE / REJECT | AUTHORIZED / REJECTED |
| AUTHORIZED | WITHDRAW | WITHDRAWN |
| REJECTED | AUTHORIZE_EXACT_SCOPE with fresh explicit supersession | AUTHORIZED |
| WITHDRAWN | AUTHORIZE_EXACT_SCOPE / REJECT with fresh explicit supersession | AUTHORIZED / REJECTED |

Unknown states/actions, invalid transitions, stale bindings or corrupt histories fail startup.
There is no implicit reset to pending and no filtering of invalid records to continue startup.

An active authorized result additionally requires:

1. An attribution approval by a configured `ATTRIBUTION_APPROVAL` authority, tied to the exact
   canonical notice configuration, including document associations and proposed notices.
2. A release approval by a configured `RELEASE_APPROVAL` authority, tied to the exact canonical
   policy. That policy pins candidates, evidence, notices and the complete decision artifact bytes.
3. Both attestations carry rationale, UTC review timestamp and a protected approval-record digest.

Provided attestations are validated even if there are no final active approvals. Historical
authorizations ending in rejection/withdrawal never enter the final authorization list; their
history and configured evidence must still validate. A release approval without a decision
artifact is rejected. A missing decision artifact with a nonnull expected digest is also rejected.

### Trust and withdrawal limitations

The packaged policy is a **deployment trust root**, not reviewer authentication. Runtime checks
its role assignments and bound attestations; it cannot prove a person's identity from a string
or independently validate a private approval record from its digest. A future release must use
protected repository/release review to verify real authority and decision evidence. Signing or
external identity verification is not implemented here, and no such trust is assigned initially.

Changed source bytes invalidate the source/index join even if a formula string stays the same.
Changed expressions, evidence, scopes or notices require new review bindings/resources and fresh
release approval. Changed decision artifacts invalidate old release attestations. Deployment
controls must prevent rollback to a pre-withdrawal approved policy/JAR; an isolated older JAR
cannot discover later withdrawals. No mutable registry or synchronization service is introduced.

## 4. Zero-approval initial behavior and attribution preparation

- All 250 frozen candidate seeds remain PENDING_REVIEW, matching P1C exactly.
- No decision artifact, reviewer assignments, notice approval or release approval is packaged.
- Startup succeeds and returns **zero authorized observations**.
- No production path converts VERIFIED_LICENSED_MATCH into an authorization.
- Proposed SRD 5.1 / SRD 5.2.1 notices retain their exact P1C text and fingerprints, license URL,
  changes disclosure and disclaimer reference. Their presence is configuration, not legal approval.
- No attribution endpoint, response header or downstream compliance guarantee is implemented.

## 5. Tests and verification

`src/test/java/com/thedndpub/data/publication/spell/SpellPublicationCatalogTests.java` adds
**22 tests**. Synthetic reviewer assignments, approvals and decisions exist only in test memory.
There are no test approval resources in the production classpath or packaged JAR.

Coverage includes all 250 pending candidates and all 458 excluded IDs; a valid fixture requiring
all three approval layers; rejection/withdrawal and invalid transitions; duplicate/unknown events;
manifest, scope, reviewer, expression, notation, source fingerprint, pointer, span and evidence
mismatches; missing attribution; notice/release digest changes; corrupt hash chains; missing or
unreadable decision artifacts; strict unknown/duplicate/null/coerced input rejection; owner/index
mismatches; canonical hashing; immutable results and deterministic candidate-based ordering.

Verification results:

| Check | Result |
|---|---|
| Complete Maven suite (`package`) | **561 passed**, 0 failures, 0 errors, 0 skipped; baseline 539 + 22 |
| Existing Spell identities and formula index | 969 identities, 930 observations unchanged |
| Runtime and review reconciliation | 250 pending candidates / 195 owners; all 708 original candidates accounted for |
| Production authorization | 0; no packaged decision artifact or reviewer authorities |
| Public status distribution | 547 NONE_OBSERVED / 422 WITHHELD; no PARTIAL or COMPLETE |
| Existing Spell detail fields/order and summaries | Existing P1A full-corpus snapshot and route tests pass |
| Existing catalogs/normalization | Full suite passes; all pre-existing Java/test bytes unchanged |
| Existing resolver | 6,246; RESOLVED 2,517, AMBIGUOUS 2,054, UNRESOLVED 181, DEFERRED 1,494 |
| Pinned source hashes | All 175 unchanged |
| Prior evidence | Pre-task fingerprints unchanged; P1B/P1C and original assessment untouched |
| Export reproducibility | `export-runtime.mjs --check` reproduces all four resources exactly |
| Packaged JAR | Startup and internal validation pass; 969 summary records; detail/by-name status checks pass; no formulas or test fixtures published |
| Whitespace | `git diff --check` and new-file whitespace check pass |

The Maven build exposed no baseline discrepancy. During development one test compilation error
and one overly narrow exception assertion were corrected; the final complete suite passes.
The packaged-JAR script's PowerShell continuations were corrected before its successful run.

Reproduce from the repository root:

```powershell
node analysis/mechanics/public-v1/spells/p1d/export-runtime.mjs --check
node analysis/mechanics/batch-11b-item-charge-syntax/verify-source-hashes.mjs
mvn package
& ./analysis/mechanics/public-v1/spells/p1d/verify-packaged.ps1
node analysis/mechanics/public-v1/spells/p1d/verify-runtime.mjs
git -c core.safecrlf=false diff --check
```

The JAR script uses a hidden process with the JAR directory as its working directory, a temporary
port and local requests, records results under `target/`, and stops its own process in `finally`.
That working directory has no `analysis/` tree. Java/JAR paths are configurable parameters.
The runtime exporter refuses to overwrite reviewer assignments or a release approval; it is an
initial pending-seed exporter, not a human approval tool.
`p1d/verification.json` records the resource fingerprints and checked results; the verifier checks
them against the complete Maven reports, packaged-startup result and unchanged P1C input digests.

## 6. Public compatibility and a future approved release

No existing production Java file changed. `SpellDto`, `SpellSummaryDto`, public mappers,
controllers, definitions, routes, reference generation and resolver behavior remain unchanged.
No public mapper reads this authorization index. Even test-only authorized index results leave
every public Spell response unchanged. P1D adds no Java record components to existing contracts.

Before any future internal authorization can activate:

1. Assign genuine reviewer authorities through a protected approval process; verify the private
   audit records rather than copying the synthetic test fixture.
2. Obtain explicit occurrence/scope decisions, attribution approval and release signoff.
3. Project those decisions to the typed runtime journal, pin its exact bytes, bind the approved
   notice configuration and obtain release approval over the resulting policy hash.
4. Re-run full candidate/index/fingerprint, history, notice and release validation and package checks.

That still does **not** publish formulas in P1D. A separately authorized implementation must add
the reviewed public payload and attribution delivery, derive projection status from actual emitted
observations, and verify downstream documentation and compatibility. Original occurrence ordering
must remain intact; there is no permission to infer gameplay effects or publish additional prose.

**Recommended next task:** human occurrence, attribution and release review; then request a bounded
allowlist-backed public projection and attribution implementation for the explicitly approved subset.
No next batch is started by P1D.
