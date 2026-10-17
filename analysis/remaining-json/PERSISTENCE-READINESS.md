# Persistence and Source Synchronization Readiness

This is a retention plan for future roadmap Steps 10 and 11; it introduces no database model, repository, migration, or sync job. Static classpath catalogs should remain the runtime source until those steps are explicitly undertaken.

## Identity/provenance fields to retain

| Information | Why future persistence needs it | Capture policy now |
|---|---|---|
| Stable authored identity: catalog kind, exact source code, authored name, source UID and edition context | Enables deterministic upsert, reprint separation and links | Preserve original source strings; do not normalize away case or punctuation in stored provenance |
| Parent/context identity | Deity pantheon, vehicle upgrade compatibility, table kind and other identities are not always name/source | Store context as typed identity components, not reconstructed display strings |
| Snapshot/revision identity | Distinguishes changing upstream releases | Record pinned source version `v2.37.0`, repository commit/hash or manifest when known; current inventory includes per-file SHA-256 fingerprints |
| Source provenance and page locator | Attribution, auditing and identifying altered records | Retain primary and secondary source codes, page, reprint UIDs and source metadata separately |
| Copy/version lineage | Explain derived records and reproduce expansion | Preserve raw copy/version declaration and resolved base identity in import provenance; no new family gets a guessed expansion contract |
| Deterministic ordering | Stable APIs, repeatable diffs, predictable migration output | Preserve authored array order when semantically meaningful and define stable tie-breakers for indexes |
| Content fingerprint | Detect edits independent of runtime identity | Hash exact file bytes now; later optionally hash canonical parsed source record with explicit canonicalization version |
| Collection/file provenance | Records share files and some fields are collection-specific | Retain relative file path, root collection key, and record locator (array index plus source identity) |
| Publication/license scope | Prevent a data sync from automatically publishing newly imported prose/media | Store source/license assertions and DTO publication policy separately from import correctness |

## Per-family requirements

| Family | Required persistence context |
|---|---|
| Adventure index | `source`, ID/group, snapshot fingerprint; contents ordering is an index not chapter body identity |
| Bastion facility | source/name/level or facility type where needed; preserve prerequisites and source field locator |
| Character option | source/name plus option type and any parent/class/level context |
| Cult / boon | separate kind + source/name; preserve reprint and copy lineage if observed |
| Deity | source/name/**pantheon**; copy base, reprints, and source data locator |
| Encounter | source/name + collection/table kind; preserve result ordering and authored ranges |
| Crochet pattern | source/name and designer/attribution metadata; access policy for instruction content |
| Treasure tables | source + table collection kind + authored name; preserve dice range/order and output category |
| Name table | source/name + table options; treat values and ordering as authored content |
| Object | source/name + object category and any parent/source context |
| Recipe | source/name + attribution; ingredient/step order and content license metadata |
| Reward | source/name/category; typed grant references and prerequisite context |
| Trap/hazard | separate kind + source/name; preserve rating/context and source order |
| Variant rule | source/name/ruleType and reprint relationships |
| Vehicle | source/name/type; parent identity for upgrades if source specifies one |
| Vehicle upgrade | source/name + compatibility filter; keep as a distinct record from resulting vehicle |

## Fingerprints and determinism

The scanner stores SHA-256 of each JSON file's exact bytes in `per-file-classification.json`. This is reproducible for the checked-out snapshot and will reveal file-level changes. A record-level content hash should not be designed until each family specifies canonicalization for key ordering, array order, omitted fields, explicit nulls, and transformation expansion. Copy-derived effective records should retain both source-authored base/override provenance and a deterministic normalized fingerprint.

No persistence readiness finding authorizes changing current source JSON or introducing database coupling into catalog startup.
