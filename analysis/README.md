# Analysis index

Start with the **[backend roadmap](../docs/THE_DND_PUB_BACKEND_ROADMAP.md)** for current progress and
authorized next work. The [benchmark index](active/mechanics/benchmarks/README.md) locates the
7.3.1 fixtures and review gate. This directory contains evidence
and historical investigations, not additional authorization to implement their proposed next steps.

## Current guidance

| Topic | Entry point |
| --- | --- |
| Current architecture and conventions | [Documentation index](../docs/README.md) |
| Mechanics model and active implementation references | [Active mechanics index](active/mechanics/README.md) |
| Current Spell capability inventory | [7.4.4A–C inventory and comparisons](mechanics/step-7/7.4.4C-condition-extraction-comparison.md) |
| Latest bounded Spell mechanics implementation | [7.4.4X Chromatic Orb](mechanics/step-7/7.4.4X-chromatic-orb-damage-dice-conditional-chaining.md) |
| Generic-first interpretation architecture | [7.4.4 pattern audit](mechanics/step-7/7.4.4-generic-first-pattern-audit.md) |
| Generic-first catalogue dry run | [All-spell experimental results](mechanics/step-7/7.4.4-generic-first-full-catalogue-dry-run.md) and [per-owner artifact](mechanics/step-7/generic-first-dry-run-results.json) |
| Step 7.5 generic pipeline v2 baseline | [Implementation report](mechanics/step-7/7.4.4-generic-mechanics-pipeline-v2-implementation.md), [v2 catalogue summary](mechanics/step-7/7.4.4-generic-first-full-catalogue-dry-run-v2.md), and [per-owner results](mechanics/step-7/generic-first-dry-run-v2-results.json) |
| Step 7.5 condition extraction | [Condition strategy implementation and verification](mechanics/step-7/7.5-generic-condition-extraction-and-relationship-strategy-implementation.md), [v5 catalogue summary](mechanics/step-7/7.5-generic-condition-dry-run-v5.md), and [per-owner v5 results](mechanics/step-7/generic-condition-dry-run-v5-results.json) |
| Step 7.4.4 closure remediation | [F-02/F-03/F-04/F-06 results](mechanics/step-7/7.4.4-closure-remediation.md) |
| Step 7.4.4 closure remediation | [F-02/F-03/F-04/F-06 results](mechanics/step-7/7.4.4-closure-remediation.md) |
| Cross-catalog discoveries | [Domain discoveries](domain/DOMAIN-DISCOVERIES.md) |
| Completed source-family coverage | [Source coverage matrix](remaining-json/SOURCE-COVERAGE-MATRIX.md) |
| Source registry conventions | [Source registry design](sources/SOURCE-REGISTRY-DESIGN.md) |
| Publication gate and evidence | [Artifacts index](artifacts/README.md) |

## Historical archive and reproducibility

- [Archive index](archive/README.md) groups completed catalog, normalization, reference, mechanics
  and publication reports while preserving dependency-sensitive paths.
- [Complete retained historical report list](archive/HISTORICAL-ARTIFACTS.md) links every report
  classified HISTORICAL in the consolidation inventory.
- [Artifact index](artifacts/README.md) locates manifests, gold sets, evidence, schemas and scripts.
- [Maintenance report](../docs/development/DOCUMENTATION-CONSOLIDATION.md) explains original snapshots,
  the one link correction, case normalization and deferred cleanup decisions.

The inventory found 430 pre-existing analysis artifacts. All 224 HISTORICAL documents are named in
fingerprint evidence, and other files are directly read by tests or scripts. They remain in place.
An indexed archive separates their role without relocating their evidence or consumers. The one
Feat-inventory link correction has an exact pre-change snapshot for historical hash reconstruction.

Earlier `IMPLEMENTATION-ORDER.md`, `ROADMAP.md` and similar files retain the plans written for their
original scopes. They are historical inputs; the backend roadmap resolves progress and precedence.
Do not regenerate their evidence or rewrite their conclusions to match a later implementation.
