# Architecture, source identity and provenance

This is a current navigation guide to existing code and conventions. Follow the
[roadmap](../THE_DND_PUB_BACKEND_ROADMAP.md) for authorized scope and [AGENTS.md](../../AGENTS.md)
for implementation standards.

## Data boundaries

```mermaid
flowchart LR
    A[Pinned source JSON] --> B[Typed source DTEs]
    B --> C[Copy and version resolution where supported]
    C --> D[Immutable normalized definitions]
    D --> E[Immutable catalogs and indexes]
    E --> F[Dedicated public DTOs]
    F --> G[Thin REST controllers]
```

Each source family has its own identity and copy dialect. Reuse infrastructure when semantics match;
do not force every family through Monster inheritance. Existing field-coverage and design reports
are retained in the [catalog archive index](../../analysis/archive/README.md#catalogs-and-normalization).
Strict DTE deserialization preserves unknown-field failures and authored alternatives. Prose and
markup retained internally are not automatically eligible for a REST projection.

## Identity and publication provenance

- [SourceIdentity](../../src/main/java/com/thedndpub/data/dto/source/SourceIdentity.java) represents a
  source code. [SourceReference](../../src/main/java/com/thedndpub/data/dto/source/SourceReference.java)
  adds optional page context.
- [SourceRegistry](../../src/main/java/com/thedndpub/data/services/SourceRegistry.java) validates known
  codes and aliases. Existing Book/publication records supply publication metadata where available;
  unknown publication metadata does not justify fabricating it.
- Keep original source codes and edition distinctions. PHB/XPHB are different owners. Names alone
  are not unique identities; individual families may need parent, pantheon or other authored context.
- Original JSON Pointers, source-owner locators and resource fingerprints locate authored evidence.
  Unchanged inheritance, transformed content and unavailable origin are different cases.
  Never manufacture an authored span for generated text.
- Source upgrades are a future roadmap step. A resource fingerprint change must not silently inherit
  interpretation verification or publication approval.

The retained [source registry design](../../analysis/sources/SOURCE-REGISTRY-DESIGN.md) and
[implementation](../../analysis/sources/SOURCE-REGISTRY-IMPLEMENTATION.md) provide historical rationale.
The [domain discoveries](../../analysis/domain/DOMAIN-DISCOVERIES.md) and
[source coverage matrix](../../analysis/remaining-json/SOURCE-COVERAGE-MATRIX.md) remain active references.

## References and mechanics are separate

The existing reference infrastructure records resolution without turning a mention or optional choice
into a guaranteed grant. Preserve ambiguous, unresolved and deferred cases; do not infer target IDs
from names or neighboring prose. See the retained
[reference semantics](../../analysis/references/RESOLUTION-SEMANTICS.md).

Syntax evidence does not itself establish a gameplay effect. The
[canonical mechanics model](MECHANICS-MODEL.md) adds proposed typed claims with evidence, review and
rights metadata. The [inspection contract](../api/SPELL-INSPECTION.md) projects those claims separately
from the existing Spell `mechanics` classifications and formula publication status.

## Runtime and repository assets

Classpath resources live under `src/main/resources/`; the default Maven layout packages them into
the Spring Boot JAR. Some tests additionally read analysis inventories and gold sets from repository
paths. Reproducibility scripts also rely on original folders, filenames and fingerprints.

Use the [artifact dependency inventory](../development/DOCUMENTATION-CONSOLIDATION.md) before moving
anything. Do not move source data, P1D resources or test evidence to make the documentation tree neater.
