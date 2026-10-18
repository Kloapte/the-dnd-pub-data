# Canonical mechanics and independent review/rights metadata

Current implementation: [mechanics model package](../../src/main/java/com/thedndpub/data/mechanics/model/),
[review package](../../src/main/java/com/thedndpub/data/mechanics/review/).
The [approved 7.2.1 design](../../analysis/mechanics/step-7/7.2.1-mechanics-model-and-api-contract.md)
remains the detailed architectural baseline. The implementation reports record concrete refinements:
[7.2.2](../../analysis/mechanics/step-7/7.2.2-canonical-mechanics-model-implementation.md) and
[7.2.3](../../analysis/mechanics/step-7/7.2.3-spell-inspection-api-implementation.md).

## Current model

`MechanicsDocument` binds an authoritative owner, source revisions, processing, interpreted claims,
observations, unsupported constructs, evidence, coverage and a computed review summary.
`MechanicRecord` binds a stable claim ID and revision to a typed payload/context, evidence associations,
derivation, assessment, review state and independent licensing classification.

The seven initial payload families are AC modifier, reaction activation, effect duration, Spell-specific
damage prevention, damage, healing and saving-throw requirement. Supporting values preserve signed,
dice, symbolic, summed and explicitly unknown amounts; trigger grouping; source-qualified mentions;
duration references and exact context bindings. These values do not evaluate rules or establish
an interpretation merely by being constructed.

The bounded [Shield pilot](../../analysis/mechanics/step-7/7.3.2-shield-extraction-implementation.md)
produces five source-backed claims per PHB/XPHB edition, with `PROCESSED` / `PARTIAL` coverage.
The [7.3.3 expansion](../../analysis/mechanics/step-7/7.3.3-spell-extraction-expansion.md) adds two
partial PHB Fireball claims (save and failed-save damage) and one partial PHB Healing Word sum.
Unsupported targeting, successful-save half damage and scaling remain explicit. The other 965
Spell documents remain `NOT_ATTEMPTED` / `NOT_ASSESSED`. Model-construction fixtures
are not extracted mechanics or independently reviewed benchmarks; all six benchmark fixtures still
await human review.

## Independent dimensions

| Dimension | Meaning |
| --- | --- |
| Processing | Whether an adapter has attempted interpretation; failure is not a successful empty assessment |
| Completeness and coverage | Bounded claim/scope completeness, including partial, unknown and unsupported information |
| Support and findings | Deterministic evidence/relationship diagnostics; no confidence percentage certifies correctness |
| Mechanics review | `PENDING_REVIEW`, `NEEDS_MECHANICS_REVIEW`, `VERIFIED`, `REJECTED` |
| Rights | `NEEDS_REVIEW` by default; documented claim-scoped `SRD`, `LICENSED`, or `OPEN` where established |
| Publication | Separate authorization/projection decisions; neither review nor rights grants publication |

Only a validated human decision bound to the exact claim revision can produce effective VERIFIED.
The recorded decision remains distinct from effective status. Essential unknowns, missing/stale
evidence, unresolved dependencies, contradictions and failed benchmarks require mechanics review.
A benchmark pass alone never verifies a claim. Review summaries retain individual-state counts.

Rights coverage must be scoped to the actual interpreted claim. A formula-token license does not
automatically cover its interpreted effect/context. Numeric content or an SRD flag does not establish
OPEN or SRD status. Stale rights decisions require renewed review while retaining history.

## Evidence and revisions

Text evidence reuses Batch 5 locations and UTF-16 `[start,end)` spans. Structured-field evidence has
a JSON Pointer, not a fake string span. Transformed evidence retains pinned inputs/instructions;
unavailable origin is explicit. Observations, evidence and claims remain separate objects.

Mechanic IDs use an owner-scoped stable slot, not filtered positions, payload values or review states.
Claim revisions bind payload, context, evidence, derivation, source revisions and assessment through
deterministic hashes. Document revisions separately bind the joined snapshot. Future persistence
must preserve old observations, proposals and decisions rather than overwriting history.

## P1D and inspection

The [P1D infrastructure report](../../analysis/mechanics/public-v1/spells/P1D-IMPLEMENTATION.md)
describes the existing occurrence-level publication gate: 250 pending candidates and zero authorized
formulas. It neither interprets mechanics nor certifies correctness. The private inspection catalog
does not depend on its authorized set and does not filter pending/rejected/rights-unreviewed claims.

See the [Spell inspection API](../api/SPELL-INSPECTION.md) for the current projection. No extraction,
rules engine, database review workflow or external publication policy is introduced by these guides.
