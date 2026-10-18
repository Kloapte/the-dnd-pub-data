# Spell mechanics inspection contract

Implementation reference:
[7.2.3 report](../../analysis/mechanics/step-7/7.2.3-spell-inspection-api-implementation.md).
Scope and next work are governed by the [roadmap](../THE_DND_PUB_BACKEND_ROADMAP.md).

## Routes

```http
GET /api/spells/spell:phb:shield
GET /api/spells/spell:phb:shield?include=interpretedMechanics
GET /api/spells/spell:xphb:shield?include=interpretedMechanics
GET /api/spells/by-name/Shield?source=PHB&include=interpretedMechanics
```

The exact include value is `interpretedMechanics`. Unknown/blank values on detail/by-name use the
existing HTTP 400 convention. Unknown owners retain 404 behavior. By-name remains a list and can
return multiple source-distinct definitions. Collection routes retain their existing filters and
summary DTOs; this include does not add inspection fields to summaries.

Without include, `interpretedMechanics` is null and omitted. Existing `mechanics`, scaling,
`formulaProjectionStatus`, fields and ordering remain unchanged. Both older `SpellDto` constructor
signatures remain available. Record reflection, component count, canonical construction, equality
shape and patterns nevertheless changed when the new nullable component was added.

## Current response semantics

PHB and XPHB Shield now return five source-backed claims each, with PROCESSED / PARTIAL status;
see the [7.3.2 implementation](../../analysis/mechanics/step-7/7.3.2-shield-extraction-implementation.md).
PHB Fireball now returns a partial save requirement and failed-save damage claim; PHB Healing Word
returns a partial healing sum. See the [7.3.3 report](../../analysis/mechanics/step-7/7.3.3-spell-extraction-expansion.md)
for unsupported save-success, targeting and scaling portions. The other 965 Spells retain the
unattempted envelope below. This example shows selected envelope
properties; observation references and unchanged surrounding Spell fields are not repeated here.
The resource fingerprint is the actual pinned PHB value.

```json
{
  "interpretedMechanics": {
    "schemaVersion": 1,
    "projection": "PRIVATE_INSPECTION",
    "availability": "AVAILABLE",
    "processing": { "state": "NOT_ATTEMPTED" },
    "sourceRevisions": [
      {
        "id": "data/spells/spells-phb.json",
        "datasetVersion": "5eTools v2.37.0",
        "resourcePath": "data/spells/spells-phb.json",
        "resourceSha256": "c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a"
      }
    ],
    "coverage": { "status": "NOT_ASSESSED", "unsupportedCount": 0 },
    "reviewSummary": {
      "total": 0,
      "pendingReview": 0,
      "needsMechanicsReview": 0,
      "verified": 0,
      "rejected": 0
    }
  }
}
```

This means interpretation has not been attempted, **not** that an unattempted Spell lacks mechanics. Empty
interpretations, unsupported constructs and claim-evidence lists are omitted under NON_EMPTY.
Meaningful zero and false values are preserved. No human verification or rights clearance is invented
for a nonexistent claim.

There are 930 existing formula observation references across 422 Spells. The other 547 have none in
the bounded Batch 6 index. Where present, `observations` contains source-aware occurrence references
and bounded JSON Pointer/UTF-16 locators, not formula expressions or applied effects. Current
`formulaProjectionStatus` remains 547 NONE_OBSERVED / 422 WITHHELD.

## Typed claims within the same contract

The dedicated envelope supports canonical typed payload/context, evidence associations, derivation,
assessment, independent review/rights metadata, coverage, unsupported findings and an evidence pool.
The bounded interpreters run once during startup. All 13 claims have recorded PENDING_REVIEW and
licensing NEEDS_REVIEW; unresolved mentions, unknown subjects/DCs
and their dependencies require effective mechanics review. No claim has human verification or
publication authorization. A failed canonical processing result projects
UNAVAILABLE / FAILED and a controlled diagnostic; it is not a successful empty result. Catalog
integrity failures stop startup rather than silently returning missing inspection.

Public DTOs omit private reviewer identities, audit journals and complete source entries. The
[inspection mapper](../../src/main/java/com/thedndpub/data/mechanics/inspection/SpellMechanicsInspectionMapper.java)
is the explicit boundary from canonical documents, and the
[catalog](../../src/main/java/com/thedndpub/data/services/SpellMechanicsInspectionCatalog.java)
validates the complete owner/index/source join at startup. Requests do not scan or reparse source files.

## Deployment boundary

This inspection API assumes the owner's private network. Include is a data selector, not a privilege
credential. Current API security behavior is unchanged; no development profile, Keycloak or frontend
filter grants protection. Before external exposure, implement an appropriate server-side access and
publication policy. P1D remains separate and currently authorizes zero formula observations.

The [mechanics model guide](../architecture/MECHANICS-MODEL.md) explains review/rights independence.
No new interpretation or publication authorization follows from this contract documentation.
