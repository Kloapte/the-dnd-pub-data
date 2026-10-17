# Cross-Catalog Resolver Test Strategy

## Unit tests

- Exact qualified matches return the existing catalog ID and preserve original source/name.
- Different case follows the adapter's documented lookup behavior without changing authored values.
- Parent-qualified subclass/feature identities resolve only with exact parent context.
- Item/Base Item and rule/generated item target kinds do not cross-resolve.
- PHB/XPHB duplicates return all candidates for unqualified lookup; a sole current candidate remains deferred.
- Missing qualified targets remain `UNRESOLVED`; unknown source codes are separate registry failures.
- Choices, selectors, groups, and markup never collapse to one target.
- Candidate order and diagnostic output are deterministic and immutable.
- Duplicate target IDs and impossible multi-match exact keys fail validation.

## Integration and regression tests

Build the composed immutable snapshot once, validate all required adapters, verify no circular Spring dependency, and test classpath/JAR startup. Assert no query triggers corpus re-normalization. Compare public JSON before/after any future projection change. The existing class feature graph tests remain the authority for its 2,725 effective references; the shared layer must not duplicate its edges.

## Corpus validation

For each occurrence capture owner catalog/ID/field locator, category, original token/source, semantic class, outcome, and candidate IDs. Report occurrence counts and distinct authored values separately. Baseline counts are checked against domain reports and versioned source snapshots. Legitimate deferred expressions are not failures. Qualified misses, malformed references, duplicate IDs, and source registry violations are separately counted and assigned policy severity.
