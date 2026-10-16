# Source Registry coverage

Status: corpus-wide source validation completed against the shared registry.

Stage B now validates these source codes from typed roots, copy targets, source relationships, explicit UID slots, filters and descriptive markup, with unknown-source rejection tests. No further registry or publication changes were needed; see [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md).

## Distinct identities

The corpus uses **31 written source spellings representing 26 case-insensitive source identities**:

AU, BGG, CoS, DMG, DSotDQ, EFA, EGW, ERLW, FRHoF, FTD, GGR, MM, MOT, PHB, PSA, PSK, RHW, SCAG, SKT, TCE, UATheMysticClass, VRGR, XDMG, XGE, XMM, XPHB.

Lowercase variants include `mm`, `phb`, `scag`, `tce`, and `xphb`. Preserve these original spellings on source occurrences. Lookup is case-insensitive; no extra registry aliases are needed for case differences.

Primary definitions use 18 identities. The other eight appear through secondary/reprint/content/reference paths. Collection and per-code counts, reference categories, and representative pointers are retained in [source-registry-coverage.json](source-registry-coverage.json), with a human-readable code table in [DISCOVERY-TABLES.md](DISCOVERY-TABLES.md).

## Confirmed missing source and scoped change

Before Stage A, `UATheMysticClass` was absent from `data/sources/registry.json`. It is explicitly authored on one Mystic class, six subclasses, 23 class features, and 36 subclass features, with parent and UID references as well. This is confirmed identity evidence rather than inferred publication metadata.

Following the skill's existing Source Registry convention, Stage A adds only `{code:"UATheMysticClass", aliases:[]}`. No title, date, edition classification, publication, or alias is invented. Registry identities increase **178 → 179**. Existing Book Catalog associations remain **66**; identities without publication metadata increase **112 → 113**. The existing registry test's three count expectations were adjusted accordingly; no class pipeline production code or new tests were introduced.

After the change, all 26 identities resolve. There are **zero missing identities, invalid explicit source values, alias conflicts, or case-insensitive registry identity conflicts**. Unknown or ambiguous entity targets are a different concern: the three item-group catalog gaps remain even though XPHB is known.

## Source semantics

Use existing `SourceIdentity` and `SourceReference`. Primary and `otherSources` locators retain their optional integer pages. Parent class/subclass sources, feature sources, and copy targets remain distinct fields. Reprint UIDs use kind-specific source slots; subclass `shortName|className|classSource|subclassSource` is not `name|source`.

Class markup uses separate display and subclass-routing slots. Deity markup has a pantheon slot before source. Filter source clauses are selectors. None of those non-source positions is registered as a source identity.

The Book Catalog remains authoritative for known publication metadata. Catalog initialization must validate source references through the immutable registry and must not silently register new codes. Metadata availability is independent of source identity validity.

## Validation

The complete discovery scan reports all explicit source occurrences; every known typed UID grammar is parsed without discarding empty positions. No prose-based publication identification or online source metadata lookup is needed or used. Stage A passed all **176 tests**. Stage B passes the complete **241-test** suite, including Source Registry lookup, alias validation, serialization, JAR classpath loading, and the class pipeline's unknown-source checks.
