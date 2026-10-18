# P1C — Spell formula approval manifest and attribution design

## 1. Preparation outcome and authority boundary

**Review artifacts only. No expression is authorized or published.**

[candidate-manifest.json](p1c/candidate-manifest.json) contains all **250** P1B verified licensed
matches across **195** Spell owners, exactly once. Every entry is **PENDING_REVIEW**. Every human
decision, reviewer identity/authority, rationale and timestamp placeholder is **null**. The
[decision journal](p1c/decision-journal.json) has **zero events** and a null head. Attribution
delivery is also pending review. Neither schema validity nor a verified license match grants
publication authorization.

P1C does not change source JSON, Java, DTOs, endpoints, tests, normalized models, references,
original assessments or P1B classifications/evidence. The only edited pre-existing document is
the explicitly requested [Public Mechanics roadmap](../ROADMAP.md). The previously verified
baseline remains **539 tests / 6,246 resolver occurrences**; Maven was not rerun.

This phase replaces the earlier roadmap's tentative use of “P1C” for scaling/opaque syntax.
Those topics remain deferred; the current P1C prepares human review of the bounded literal subset.

## 2. Complete candidate reconciliation

| Scope | Observations | Unique owners | Disposition |
|---|---:|---:|---|
| P1B `VERIFIED_LICENSED_MATCH` | 250 | 195 | In candidate manifest; all PENDING_REVIEW |
| P1B `AMBIGUOUS_MATCH` | 147 | 69 | Outside approval set; classification unchanged |
| P1B `NO_VERIFIABLE_EVIDENCE` | 311 | 168 | Outside approval set; classification unchanged |
| **Original bounded candidate set** | **708** | **402** | Every occurrence reconciled once |

Owner sets overlap across classifications and must not be summed. The 458 excluded observations
are individually enumerated in [excluded-candidates.json](p1c/excluded-candidates.json), with
hash-bound pointers back to their original P1B records. `OUTSIDE_APPROVAL_SET_NOT_A_HUMAN_REJECTION`
is a scope disposition, not an inferred human rejection or legal conclusion.

The selected licensed evidence is **130 PHB observations → SRD 5.1** and **120 XPHB observations
→ SRD 5.2.1**. The latter also matched 5.2.0 during P1B; 5.2.1 is selected explicitly for review.
No older supplemental occurrence is promoted by a name, reprint, SRD flag or identical formula.
The 222 formula observations outside the original 708-candidate subset remain outside this review
as well. The existing full index still has **930 observations / 422 owners / 969 indexed owners**.

## 3. Review manifest schema and exact bindings

The contract is [candidate-manifest.schema.json](p1c/candidate-manifest.schema.json), JSON Schema
2020-12, version **1.0.0**. Objects reject unknown properties. The initial-snapshot schema permits
only `PENDING_REVIEW` and null decision fields; it cannot be made an authorization by changing a
boolean or adding an unrecognized “approved” property. Its artifact kind explicitly says
`SPELL_FORMULA_REVIEW_CANDIDATES_NOT_RUNTIME_AUTHORIZATION` and publication authorization is
`NOT_GRANTED`.

| Manifest field | Preserved information |
|---|---|
| `manifestId`, `datasetVersion`, `inputArtifacts` | Review revision, pinned v2.37.0 and exact input-artifact hashes; paths here are internal review locators |
| `publicationScope` | Proposed public field paths, notation subset and semantic exclusions; this is proposed scope, not permission |
| `entries[].occurrenceId` | Existing Spell ID plus original formula occurrence index, scoped to this pinned review |
| `spellId`, `sourceCode`, `formulaOccurrenceIndex` | Source-aware ownership and original index; never a name-only join or filtered-array index |
| `sourceEvidence` | Resource SHA-256, owner/content JSON Pointers, hierarchy/section, authored origin, source text/tag fingerprints and original UTF-16 span |
| `expression` | Exact authored expression, SHA-256, original notation and existing lexical kind; no evaluation or effect role |
| `licensedMatch` | Selected SRD document/version/URL/fingerprint, published spell heading/descriptor, license ID/URL and P1B match evidence |
| `licensedMatch.matchReference` | Exact P1B artifact hash, JSON Pointer to the selected verified match and canonical value hash |
| `licensedMatch.documentReference` | Exact official-document evidence reference, including the selected document version/fingerprint |
| `attribution` | Notice ID, exact required-statement hash, original notice reference, attribution-proposal hash and change-disclosure hash |
| `eligibility` | Unchanged P1B classification, technical reliability, evidence rationale and original row reference; **not** a human decision |
| `bindingSha256` | Digest of the entry's bound evidence, scope and attribution, excluding the digest itself and the empty `review` placeholders |
| `review` | PENDING_REVIEW and null human-decision placeholders |

Every evidence reference contains `artifactId`, `artifactSha256`, `jsonPointer` and `valueSha256`.
It can therefore be checked against both the exact evidence file and selected object, rather than
an unversioned path. Source spans use Java String UTF-16 `[start,end)` offsets. The licensed PDF
evidence uses P1B's separately declared normalized extracted-text offsets plus physical PDF
page/column; these coordinate systems must not be mixed.

`SHA256_SORTED_KEY_JSON_V1` sorts object keys by JavaScript UTF-16 lexical order, preserves array
order and exact strings, serializes JSON scalars with JSON string escaping, and hashes UTF-8 bytes
without a trailing newline. It performs no Unicode, case, whitespace or expression normalization.
Full artifact fingerprints instead hash their actual file bytes. This is a documented review hash
algorithm, not a claim of certified canonicalization or cryptographic proof of reviewer identity.
A future implementation must reproduce its fixtures or adopt a separately reviewed migration.

Every entry has this initial review block:

```json
{
  "status": "PENDING_REVIEW",
  "decision": null,
  "reviewerId": null,
  "reviewerAuthorityRef": null,
  "decisionId": null,
  "rationale": null,
  "reviewedAt": null,
  "releaseScopeId": null,
  "decisionRecordRef": null
}
```

No generation time is substituted for review time. No project owner, account name or approval date
is invented. The original technical/license rationale remains separately labeled in `eligibility`.

## 4. Human review workflow and audit trail

The candidate manifest is the immutable **initial review snapshot**. Record subsequent decisions
in a separately versioned decision journal; do not edit this snapshot into a runtime allowlist.
[decision-journal.schema.json](p1c/decision-journal.schema.json) defines the proposed event shape;
the supplied journal is deliberately empty.

1. **Assign real authority.** The product/publication owner identifies who can decide this exact
   payload scope and which protected review/approval record proves that authority. That decision
   is outstanding; arbitrary identity strings in JSON are not authentication.
2. **Review an occurrence.** Open its exact source binding, selected P1B match, primary SRD evidence
   and required notice. Check expression, notation, source edition, proposed public field scope
   and attribution delivery independently. Approve or reject an occurrence, not a whole source
   book, Spell, tag family or confidence class.
3. **Record an actual decision.** A later journal event must identify the candidate-manifest hash,
   occurrence ID, binding hash, scope ID, attribution-proposal hash, real reviewer/authority
   reference, rationale and actual UTC RFC3339 review timestamp. No event is generated here.
4. **Use explicit actions.** `AUTHORIZE_EXACT_SCOPE`, `REJECT`, or `WITHDRAW` are proposed human
   decision actions. They are not public projection statuses. Each event has a unique event ID,
   sequence, predecessor hash and superseded-event ID where applicable. No wildcard grants.
5. **Approve delivery and release separately.** A real, versioned signoff must cover the exact
   attribution proposal and public contract. The release owner then approves a concrete selected
   occurrence set, its decision-journal head and attribution artifacts. Candidate review alone
   does not authorize deploying new DTOs/endpoints.
6. **Compile only after separate implementation authorization.** A future release builder may
   turn valid active human decisions into an immutable packaged release resource. The initial
   candidate manifest and journal must never be deserialized directly as runtime authorization.

Event sequencing must be contiguous, with the first predecessor null; each `eventSha256` hashes
the canonical event without its own digest. `headEventSha256` identifies the final event. A later
decision for the same occurrence must explicitly supersede its current event; timestamps alone
do not select a winner. Only one active decision may exist per occurrence/scope. Concurrent or
duplicate active approvals are invalid until explicitly reconciled.

Hash chains detect changed content only relative to a trusted head. Protected repository review,
release controls and verifiable reviewer authority are still required; merely filling in an
“authorize” string does not establish human approval. Reviewer identity and internal audit data
remain private. Bulk review may enumerate an exact signed-off list, but each occurrence/binding
must still receive an explicit decision; approval never expands to other observations automatically.

### Rejection, withdrawal and later changes

- Missing decision, pending decision, rejection and withdrawal all mean **withheld**.
- A rejection does not overwrite P1B's licensing evidence or imply that the expression is unlicensed.
- Withdrawal retains the original approval and reason/history, supersedes the active authorization,
  and prevents inclusion in later releases. It must identify the prior decision; do not delete it.
- A reversal requires a fresh human decision with explicit supersession, not removal of the rejection.
- Candidate/evidence/scope/notice changes create new review bindings and a new manifest revision.
  Earlier decisions remain auditable but cannot authorize a changed binding.
- Deployment approval must bind the accepted current decision head/release revision, preventing
  silent rollback to an artifact that predates withdrawal. An old isolated JAR cannot learn about
  later decisions without a controlled redeployment; this is an operational requirement, not a new
  mutable runtime registry or polling service proposed here.
- Removing future API publication does not revoke recipients' existing CC license rights. Preserve
  historical notices needed for already distributed releases/exports.

The generator refuses to overwrite populated decision metadata or a nonempty journal. Once review
begins, preserve the P1C seed and create subsequent journal/review revisions rather than resetting it.
The current validator intentionally validates the **initial pending snapshot**, not later approvals.

## 5. Attribution delivery proposal

[attribution-proposal.json](p1c/attribution-proposal.json) and its
[schema](p1c/attribution-proposal.schema.json) bind the exact two required notices to their P1B
source references. The notice texts are copied unchanged from the verified P1B statements.
Its own review block is also entirely pending/null.

The applicable primary notices are page 1 of
[SRD 5.1](https://media.dndbeyond.com/compendium-images/srd/5.1/SRD_CC_v5.1.pdf) and
[SRD 5.2.1](https://media.dndbeyond.com/compendium-images/srd/5.2/SRD_CC_v5.2.1.pdf).
They specify CC BY 4.0 and their version-specific attribution statements. The
[CC BY 4.0 legal code](https://creativecommons.org/licenses/by/4.0/legalcode.en), Sections 2 and 3,
addresses supplied notices, source/license links, changes, downstream restrictions and no endorsement.
This design does not turn that license into a blanket license for a PHB/XPHB response.

### Minimal proposed delivery

- Bundle a versioned `THIRD-PARTY-NOTICES.md` and matching notice JSON with each release/export.
  Include the exact requested statements, relevant licensed-document URLs/versions, the CC BY 4.0
  link and applicable supplied disclaimer reference. Keep application-code MIT notices separate.
- Propose read-only **GET `/api/attributions/spell-formulas-v1`**, served from immutable packaged
  data through the existing thin-controller/service pattern. Public entries would contain a notice
  ID, document title/version/URL, license ID/link, required statement, disclaimer reference and
  approved changes disclosure. They would not contain source pointers, hashes or reviewer details.
- Each future formula observation carries its `attributionId`. The two IDs are
  `wizards-srd-5.1` and `wizards-srd-5.2.1`. This distinguishes the licensed derivation source
  from the existing Spell `source` field, which remains PHB or XPHB.
- Formula-bearing detail/by-name responses should expose an additive HTTP `Link` with
  `rel="describedby"` pointing to that scoped notice resource. Document the link and notice IDs
  in API documentation. Browser clients need an explicitly reviewed CORS header-exposure setting
  if cross-origin access to `Link` is intended; do not silently change global configuration.
- Do **not** add a blanket `rel="license"` or top-level CC license to the complete mixed-source
  Spell JSON. The notice resource states that its coverage is only the approved formula subset.
- Multiple approved SRD versions use distinct notice IDs. The API returns the selected versions'
  exact statements; adding/replacing a version requires notice and occurrence review. 5.2.0's
  comparison evidence remains in P1B but supplies no separate selected notice in this release plan.

The proposed change disclosure is P1B's existing statement: selected formulas are extracted and
reformatted, source markup/surrounding text are omitted, and no gameplay effect/execution is asserted.
Its exact text/hash is bound to every candidate. The publisher's request for its specified
attribution and the CC conditions still require review in the intended delivery medium.

Source Registry remains authoritative for original source identities/publications. It does not
become an approval database; no invented SRD source code replaces PHB/XPHB and no license is inferred
from Book metadata or `srd` flags. Notice IDs are application notice identifiers, not resolver kinds.

API discovery alone does not establish compliance for every downstream redistribution. Clients
that cache, export, reformat or republish expressions must carry appropriate notices/change
indications in their own medium and preserve applicable prior modification information. Review
cannot promise that an API link alone satisfies every such use. No notice endpoint/header is
implemented or approved by this preparation task.

## 6. Future validation and fail-closed requirements

| Gate | Required future behavior |
|---|---|
| Decision vocabulary | Unknown/missing decision values never authorize; malformed claimed approvals reject release/startup |
| Occurrence identity | One active approval per exact Spell ID + occurrence index + pinned binding; no name-based joins |
| Index existence | Every proposed emitted occurrence exists in the current startup Batch 6 index, with complete 969-owner reconciliation retained |
| Expression/notation | Exact expression, lexical shape and tag notation match; no whitespace/operator rewriting or nearby-value substitution |
| Source identity/integrity | Exact source code, pinned resource hash, owner pointer, content pointer, span and token/text hashes agree |
| Licensed evidence | Selected document/version/fingerprint, match evidence ID and exact expression evidence exist and match the reviewed release |
| Scope | Public field set and attribution proposal equal the approved scope hashes; approval cannot expand to prose, scaling or effects |
| Attribution | Required notice exists with exact approved statement, document/license links and change disclosure; delivery is configured and tested |
| Human authority | Actual decision evidence is verifiable through the project's approved review process; strings/dates/hashes alone are insufficient |
| Journal/history | Unique event IDs, valid sequence/hash chain, explicit supersession, current release head and no duplicate active grants |
| Negative state | Missing/pending/rejected/withdrawn decisions withhold; a verified licensed match alone withholds |
| Revision | Changed source/document/scope/notice bindings require renewed review; same name, ID or formula never inherits approval |

Distinguish expected absence from corruption: no approved release/entry means withhold, preserving
P1A behavior. A supplied release artifact that claims approval but fails a binding, notice,
authority or journal check should fail startup. Do not silently skip its errors, guess a source,
or downgrade a missing owner into `NONE_OBSERVED`.

### No runtime dependency on this analysis directory

Review paths and JSON Pointers in this manifest are private audit tooling inputs. A future
reviewed release builder should package a self-contained set such as:

```text
classpath:publication/spell-formulas/v1/release.json
classpath:publication/spell-formulas/v1/evidence.json
classpath:publication/spell-formulas/v1/notices.json
```

These are proposed application release resources, separate from the 175 pinned `data/` sources.
The packaged evidence registry contains stable opaque evidence IDs and the reviewed document/value
bindings needed for startup checks, not `analysis/...` file paths. The application joins existing
startup observations and notice records by ID/hash, with no source rescanning per request, live
SRD download, mutable approval cache or dependency on a checkout. Build/deployment review binds
the exact packaged release, authority evidence and current decision head. No builder/runtime loader
or release manifest is implemented in P1C.

## 7. Future public DTO integration proposal

After separate human publication approval **and** implementation authorization, the smallest
candidate payload remains an additive immutable list on Spell detail only:

```text
SpellDto.formulaObservations: List<SpellFormulaObservationDto>
SpellFormulaObservationDto: occurrenceIndex, notation, expression, attributionId
MechanicalExpressionDto: authoredToken, lexicalKind
```

Here `expression.authoredToken` means the exact expression such as a dice token, **not** its raw
5eTools tag. `DAMAGE_TAG` and `DICE_TAG` are notation labels, not DamageEffect/HealingEffect types.
No public section/caption/name/prose, source span or inferred type/save/target/scaling relationship
is included in this proposed scope. Existing structured Spell mechanics remain independently
authoritative and unchanged. Summary DTOs/routes/filters remain unchanged.

Future records must use defensive copies and `@JsonInclude(NON_EMPTY)`, preserve original
occurrence indexes (including meaningful zero), and order emitted values by their original index.
Gaps remain gaps; do not renumber after filtering. Preserve existing detail-property values/order
and the earlier Java constructor signatures with delegating overloads. Record component reflection,
patterns and equality still require compatibility review; do not change global Jackson settings.

For each owner, let **I** be its complete indexed formula list and **E** the actual approved,
validated and emitted observation list:

| Condition | Public status |
|---|---|
| I empty and owner/index join valid | `NONE_OBSERVED` |
| I nonempty and E empty | `WITHHELD` |
| E is a nonempty proper subset of I | `PARTIAL` |
| E represents all of I under the approved contract | `COMPLETE` |

Neither the 250 candidates, the 708 bounded candidates, nor the number of approval events is the
completeness denominator. Every emitted observation must belong to I. Missing/mismatched owner
joins fail validation; they cannot imply absence. Status must describe the actual serialized
projection, not planned payloads. `COMPLETE` means indexed syntax completeness only, not full
mechanics or gameplay effects.

Current E is empty for every owner: **547 NONE_OBSERVED / 422 WITHHELD / 0 PARTIAL / 0 COMPLETE**.
Only as a counterfactual, if all 250 were later approved and emitted, the existing P1B calculation
would yield **547 / 227 / 113 / 82** respectively. This task changes none of those public values.

## 8. Source revision handling

Preserve old candidate manifests, decision journals, notice artifacts and release records by
immutable revision/hash. A changed source fingerprint invalidates reuse even when expression
spelling and Spell ID happen to remain equal. Changed expressions, pointers, offsets, notation,
copy/version origins, document versions or attribution disclosures likewise require a new binding.
For a new pin, generate a new pending set and present a diff for review; never copy approval
fields, silently retarget an old occurrence index or treat matching hashes elsewhere as permission.

Source-aware catalog identities do not change to encode an approval. Hashes bind evidence and
scope, not permanent catalog identity. Rights under an irrevocable public license are distinct
from this project's conservative integrity/release policy.

## 9. Artifacts, reproducibility and validation results

All new artifacts are in `p1c/`, plus this design document:

- `candidate-manifest.json` / `.schema.json`: all 250 pending occurrence bindings.
- `decision-journal.json` / `.schema.json`: empty journal and proposed future event contract.
- `attribution-proposal.json` / `.schema.json`: two exact notices, scoped discovery/delivery plan
  and null review metadata.
- `excluded-candidates.json`: all 458 excluded occurrences and original P1B references.
- `review-artifacts.mjs`, `generate-manifest.mjs`, `generate-schemas.mjs`: deterministic analysis
  generation with guards against overwriting human review data.
- `validate-review.mjs`, `validation-cases.json`, `verification.json`: initial-snapshot validation
  and bounded invalid-artifact probes, not production tests or an authorization engine.
- `protected-input-hashes.json`: pre-P1C snapshot of 2,175 existing source/test/analysis files,
  including prior dirty work. Only the requested roadmap edit is exempted from equality checks.

Reproduce from the repository root. Schema tools are pinned and installed only under `target/`;
no Maven/production or frontend dependency changes are made:

```powershell
npm.cmd install --prefix target/p1c-schema-tools --cache target/p1c-npm-cache `
    --ignore-scripts --no-audit --no-fund ajv@8.17.1 ajv-formats@3.0.1
node analysis/mechanics/public-v1/spells/p1c/generate-schemas.mjs --check
node analysis/mechanics/public-v1/spells/p1c/generate-manifest.mjs --check
node analysis/mechanics/public-v1/spells/p1c/validate-review.mjs
git diff --check
```

The `--check` commands build artifacts in memory and compare their bytes without overwriting
review files. Fresh generation without `--check` is only for the pending seed and refuses any
populated review fields/nonempty journal. Do not regenerate the protected input snapshot or
P1B evidence to make verification pass. P1B's original verifier predates these new artifacts and
the authorized roadmap edit; P1C independently protects the unchanged P1B files instead of
weakening or regenerating that historical verifier/result.

Validation checks all 250 occurrence/owner/index/source/evidence bindings, all null decision
fields, two exact version-specific notices and the 250+458=708 partition. Three closed schemas
validate successfully. **27** bounded corruption probes reject duplicate/missing occurrences,
changed expression/notation/source/locator/fingerprint/version, missing/unknown evidence, altered
license/notice, nonpending/unknown decisions, scope widening and excluded-candidate promotion.
These probes use temporary in-memory copies only; no approvals or fake reviewer records are
persisted and no gameplay accuracy is claimed. They do not prove a future production approval
loader or reviewer-authentication process, neither of which exists yet.

The generators reproduce identical bytes. All **175 source JSON hashes** and **2,174 protected
pre-existing files** match; the only permitted existing-file change is the roadmap. P1B evidence,
production Java, DTOs, endpoints, resolver and tests remain unchanged. `git diff --check` passes,
and new untracked artifacts are also checked for trailing whitespace. Maven is not run.

## 10. Remaining human decisions and exact subsequent scope

Outstanding decisions:

1. Identify authorized reviewers and the protected records establishing their actual authority.
2. Review/authorize or reject individual occurrences and the exact proposed public field scope.
3. Approve the two notice statements' delivery, change disclosure, discovery and downstream guidance.
4. Select a concrete release subset, current decision head and immutable release revision.
5. Separately authorize production implementation and its compatibility/operational checks.

The recommended next implementation, **only after those decisions**, is a bounded Spell-only
allowlist-backed formula projection plus scoped attribution delivery. Its acceptance requirements:

- Self-contained, strict immutable release/evidence/notice resources; no analysis-path dependency.
- Startup joins to the existing 969-owner/930-observation index and validated source fingerprints.
- Only explicitly authorized exact occurrences emitted; negative/pending/withdrawn/mismatched
  cases withheld or startup-rejected according to the policy above.
- Original indexes/order and exact expressions preserved; actual-emission-derived P1A statuses.
- Two approved notice versions and tested discovery/delivery; no blanket whole-response licensing.
- Constructor/JSON/summary/route compatibility and full existing identity/reference stability.
- Focused acceptance tests, complete Maven verification, packaged-JAR loading, unchanged 175
  source hashes and `git diff --check`.
- No raw markup, prose, scaling tokens, new effect semantics, inferred relationships, resolver
  expansion, Monster/Item API changes, frontend work or Step 8.

If human review has not authorized a subset and attribution delivery, production formula
publication remains blocked. Preparing schemas, pending records or a valid hash does not remove
that gate. This task stops with the review artifacts and report.
