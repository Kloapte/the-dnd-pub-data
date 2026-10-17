# Cross-Catalog Reference Discovery — Stage A

## Scope and conclusion

This stage reviewed the existing normalized catalog implementations and their source-derived reference analyses. It makes no production-code, DTO, API, or normalization changes. The evidence shows that the application already has several correct domain-local resolvers, but no single cross-catalog reference contract. A shared layer is justified for exact lookup, candidate enumeration, and diagnostics, provided it wraps catalog-owned identities rather than recreating them.

The inventory is not a count of every name-like string. It includes only typed references, explicit UIDs, structured selectors, and recognized markup links reported by the existing full-corpus analyses. Descriptive mentions and presentation links are kept separate from grants. Some catalog reports count reference occurrences while others count owner records or distinct UIDs; those metrics are labeled separately rather than added into a misleading grand total.

## Key findings

- Class graphs already resolve 2,339 authored feature edges exactly; copy inheritance yields 2,725 effective feature edges. Parent class/subclass links and class-owned Feat, Optional Feature, Item, and Monster references use explicit source-aware resolution where supported.
- The Classes corpus contains 1,068 direct spell reference occurrences: 446 source-qualified exact matches, 620 unqualified references with multiple current candidates, and two unqualified `branding smite` references with one current candidate. Three additional explicit choice candidates resolve exactly. No unqualified source should be inferred.
- Background equipment analysis reports 718 exact item matches, eight unresolved occurrences across three authored UIDs, zero ambiguous matches, and 204 text/equipment-type descriptors. These are not equivalent to 730 concrete item grants.
- Class starting equipment has three deferred item-group references. They identify groups, not missing concrete item definitions.
- Five named Magic Variant transformations refer to two absent XDMG base-item identities. They remain variant applicability/transformation records, not generated Item definitions.
- Spell definitions contain 389 validated reprint edges. These are same-catalog source relationships and must not be treated as cross-catalog grants.
- IDs are catalog-specific. Most are readable source/name IDs; subclasses and class features require parent/level context. Monster IDs still use a Base64-derived representation. The resolver must ask catalog adapters for IDs and never synthesize IDs itself.
- Existing source-domain analyses report their primary and explicit reference sources checked against the shared Source Registry. This discovery stage identified no evidence requiring a registry edit; source validity and entity-target existence remain separate checks.

## Documents and evidence

1. [Reference inventory](REFERENCE-INVENTORY.md)
2. [Resolution semantics](RESOLUTION-SEMANTICS.md)
3. [Identity analysis](IDENTITY-ANALYSIS.md)
4. [Spell references](SPELL-REFERENCE-ANALYSIS.md)
5. [Item references](ITEM-REFERENCE-ANALYSIS.md)
6. [Feature references](FEATURE-REFERENCE-ANALYSIS.md)
7. [Architecture](ARCHITECTURE-DESIGN.md)
8. [API proposal](API-DESIGN.md)
9. [Test strategy](TEST-STRATEGY.md)

Machine-readable checked evidence is in [reference-evidence.json](reference-evidence.json). `scan-reference-evidence.mjs` verifies the Class evidence file's occurrence totals and category/status counts. It intentionally does not pretend that heterogeneous owner-record counts from separate catalog reports are directly summable.

## Verification

No production contracts or source data were changed. The complete Maven suite passes **271 tests, 0 failures, 0 errors, and 0 skips**. Existing domain reports and tests remain the authorities for their stated catalog-specific counts.
