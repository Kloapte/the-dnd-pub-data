# Public Mechanics v1 roadmap

Design and publication review precede each implementation authorization. Preserve the existing
catalog identities, source distinctions, normalized mechanics, and reference outcomes. A syntax
observation is evidence; application of an effect remains a separate problem.

| Phase | Review and bounded delivery | Gate / exclusions |
|---|---|---|
| 1. Spell public mechanics | P1A implemented; P1B crosswalk and P1C preparation complete; P1D internal gate implemented and verified | [P1D implementation](spells/P1D-IMPLEMENTATION.md): 250 pending candidates, zero authorized expressions. Existing 547 NONE_OBSERVED / 422 WITHHELD retained; 561 passing tests. Human review and separate public projection authorization remain outstanding |
| 2. Monster public mechanics | Audit current statistical/action/spellcasting exposure and separately design any syntax projection | Review existing prose exposure and authoritative legendary-group ownership before adding content. Reuse lexical syntax only where representations match; no Multiattack graph or combat execution |
| 3. Item public mechanics | Review existing public charge strings and consumer compatibility; assess typed charge/focus/bonus projections independently | Batch 11B is internal-only. Charge-resource meaning and source rights need independent review; no recharge execution, stacking, inventory, or attunement calculation |
| 4. Feat, Class and feature mechanics | Select already structured grant/prerequisite/progression subsets with explicit choice completeness | Preserve alternatives, grouping, optionality and withheld metadata. Existing Class choice status does not approve source labels/prose or turn options into selected grants |
| 5. Cross-catalog consistency and validation | Compare the approved contracts, serialization, ordering, provenance gates and consumer expectations | Reuse only proven common value types. Verify identities, references, licensing decisions and packaged loading. This is consistency work, not Step 8 resolver-target expansion |

**P1A is complete:** only `formulaProjectionStatus` is added to detail responses, using a
startup-validated 969-owner join to the existing formula index. Legacy Java construction leaves
the field null/omitted; summaries and existing detail values/order are preserved. `PARTIAL` and
`COMPLETE` remain reserved and are not emitted.

**P1B is complete:** the [publication crosswalk](spells/P1B-PUBLICATION-CROSSWALK.md) reconciles
708 bounded candidates. It identifies 250 verified licensed matches across 195 owners, with
147 ambiguous and 311 unverified occurrences outside the current approval set. Evidence is not
publication authorization; all original classifications and evidence remain unchanged.

**P1C preparation is complete:** the [candidate manifest and attribution design](spells/P1C-APPROVAL-DESIGN.md)
prepare all 250 verified occurrences as PENDING_REVIEW, an empty decision journal, two proposed
version-specific notices and fail-closed validation requirements. Human occurrence/scope and
attribution review remain outstanding. No formula payload is authorized or implemented.
This supersedes the earlier tentative P1C label for scaling/opaque syntax; those topics remain
separately deferred. Any production projection requires explicit review decisions and a separate
implementation request with the relevant regression and packaged-loading checks.

**P1D is implemented and verified:** the [internal publication gate](spells/P1D-IMPLEMENTATION.md)
loads a fingerprint-pinned runtime projection independently of analysis paths and validates all
250 candidates against the existing Spell formula index. The optional decision artifact is absent;
there are no reviewer assignments, attribution approvals or release approvals. Zero observations
are authorized. Invalid decisions fail startup; valid future decisions additionally require exact
scope/source/evidence bindings, approved attribution and a protected release attestation. Public
DTOs, routes and status values remain unchanged. The full suite passes 561 tests and packaged-JAR
startup succeeds. The next gate is genuine human review, followed only on separate authorization
by a bounded public formula/attribution implementation; no formula publication occurs in P1D.

Keep this sequence adjustable when rights decisions, consumer needs, source variants, or verified
model defects justify a smaller batch or a different order. No phase authorizes frontend work,
persistence, synchronization, new resolver kinds, or gameplay execution. Homecraft remains excluded;
Recipes remain food/cocktail metadata.
