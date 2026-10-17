# Cross-Catalog Resolver Architecture

## Proposed boundary

Create a read-only internal reference module after review. It should not own or normalize source records and should not mutate authored mechanics. Catalog-specific adapters expose: target kind, exact qualified lookup, unqualified candidate enumeration, stable ID, source/name identity, and immutable snapshot iteration where required. An immutable registry composes adapters at application startup.

Typed source reference records retain original authored value, parsed name/source when valid, target kind, and semantic form (`DEFINITION`, `CHOICE`, `FILTER`, `GROUP`, `MARKUP`, `PRESENTATION`, `EXTERNAL`). Typed result records carry outcome, exact target ID if authoritative, candidate IDs when ambiguous, and a diagnostic reason. Avoid `Object` maps, reflection dispatch, fuzzy matching, or a global mutable registry.

## Lifecycle recommendation

1. Source-specific normalization preserves typed references and validates syntax/source registry; it does not depend on other Spring catalogs.
2. After all immutable catalog snapshots are constructed, a startup validation coordinator invokes explicit adapters once and produces an immutable resolution/diagnostic snapshot.
3. DTO projection may expose stable target IDs only from that snapshot; it must not perform lookup or infer semantics per request.
4. Query requests use the existing target catalog services and do not rerun resolution.

This startup phase avoids circular Spring dependencies: adapters receive immutable snapshot values (or are built from a single application-level snapshot), and the coordinator depends on adapters, not on each other's services. If two catalogs need each other during normalization, this is a design defect; defer their cross-link until the composed snapshot exists.

The class feature graph remains owned by `ClassReferenceResolver`; its result may be adapted after its independent validation. Reprints within a domain remain owned by that domain. A shared layer adds value for cross-domain outcomes, uniform diagnostics, and future catalogs; it should not wrap every same-domain relationship gratuitously.

## Startup policy

Fail startup for duplicate stable IDs, malformed explicit UID syntax, unknown source codes where source identity is required, and qualified references that project policy declares mandatory. Report source-authored missing targets as immutable diagnostics rather than fabricating definitions. Choices, filters, groups, unsupported categories, and unqualified names are legitimate `DEFERRED` records. Severity should be policy per owner/reference field, not a universal assumption.

## Determinism and growth

Build immutable indexes once: exact `(kind, source, canonical authored name)` to one catalog-owned ID and unqualified `(kind, canonical name)` to a sorted candidate list. Canonicalization is an adapter contract; keep original strings intact. Use stable ID ordering for candidates. A catalog addition may change candidate cardinality, so an unqualified unique candidate remains non-authoritative and is recomputed only at startup.
