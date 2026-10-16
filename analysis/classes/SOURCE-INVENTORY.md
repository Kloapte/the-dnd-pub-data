# Classes and subclasses: Stage A source inventory

Status: Stage A source snapshot, reviewed against the local corpus on 2026-10-16. Stage B now implements the internal pipeline with exact typed reconstruction of all 15 source files and all 729 paths; see [STAGE-B-IMPLEMENTATION.md](STAGE-B-IMPLEMENTATION.md).

## Scope and reproducibility

The actual resource root is `src/main/resources/data/classes/`, packaged as `data/classes/`. Recursive filesystem discovery found **15 files, all JSON**, in one directory. There are no subdirectories, index files, manifests, standalone subclass/feature files, shared feature files, or auxiliary table collections in this directory. Do not introduce an assumed `index.json` dependency.

Run from the repository root:

```sh
node analysis/classes/discover-classes.mjs
node analysis/classes/write-discovery-tables.mjs
```

The scripts read the complete corpus without modifying it. Every file has a byte count, SHA-256, and root collection inventory in [source-evidence.json](source-evidence.json). Parse failures terminate discovery; this snapshot has zero. Every JSON object and array is visited without a recursive-entry depth cap. Existing `schema-*.json` reports and the project's `SchemaDiscovery` tooling were inspected and retained; the additional scanner supplies exact nested paths and relationship analysis that the earlier advisory reports do not provide.

## Root accounting

| Collection | Root records | Without `_copy` | With `_copy` | Unique full identities |
| --- | ---: | ---: | ---: | ---: |
| class | 30 | 30 | 0 | 30 |
| subclass | 330 | 206 | 124 | 330 |
| classFeature | 677 | 677 | 0 | 677 |
| subclassFeature | 1,499 | 1,424 | 75 | 1,499 |
| Total definitions | 2,536 | 2,337 | 199 | 2,536 |

Thirteen files also contain an `_meta` object. Its only field is `internalCopies`, an array of root collection names (18 array values altogether). These are metadata objects, not definitions. Counting those objects gives 2,549 root values; the application definition candidate count remains 2,536.

The four named collections are physically colocated in class-family files. Their records are separate definitions, linked through source-qualified UIDs rather than embedded feature definitions inside class records. Embedded `entries` objects are content, options, or reference nodes, not additional catalog roots. Tables live inside roots or entries. Optional features are external definitions in `data/optionalfeatures.json`.

See [DISCOVERY-TABLES.md](DISCOVERY-TABLES.md) for the complete per-file counts, primary source distribution, every root field, class identities, and all 59 progression table groups. No file is counted through both a manifest and filesystem traversal: only physical files are scanned, exactly once.

## Sources and editions

Classes: PHB 12, XPHB 12, TCE 4, EFA 1, UATheMysticClass 1. The four TCE classes are Artificer and three sidekick classes. Subclasses use 18 distinct primary source codes. All root definitions together use 18 primary codes; secondary, reprint, filter, statblock, and markup references increase the set to 26 case-insensitive source identities (31 written spellings).

All 30 classes declare `edition`: 17 `classic`, 13 `one`. Of 330 subclasses, 130 declare `classic`, 70 declare `one`, and 130 omit the field. Feature roots do not declare editions. Source, parent source, and declared edition must remain distinct facts.

## Representative shapes

```json
{
  "name": "Fighter",
  "source": "PHB",
  "edition": "classic",
  "hd": {"number": 1, "faces": 10},
  "classFeatures": [
    "Fighting Style|Fighter||1",
    {"classFeature": "Martial Archetype|Fighter||3", "gainSubclassFeature": true}
  ]
}
```

This is an abbreviated selection of actual fields and reference forms, not a complete class record. Ordinary subclass roots carry `name`, `shortName`, `source`, `className`, `classSource`, and `subclassFeatures`. Copy roots may supply only identity plus `_copy`; 67 subclasses omit `subclassFeatures` and require inheritance analysis.

Class feature identity uses `name`, `className`, `classSource`, `level`, and `source`. Subclass features additionally use `subclassShortName` and `subclassSource`. The 75 copied subclass features omit `entries` and `page`, supplied by their bases. All other feature roots contain entries.

## Counting boundaries

- **Physical files:** 15.
- **Root definitions:** 2,536, including 199 authored copy declarations.
- **Unique authored full identities:** 2,536; zero duplicate identities across files or within files.
- **Referenced features:** 2,176 unique roots, through 2,339 structured reference occurrences. References do not add definitions.
- **Copy-derived definitions:** 124 subclasses and 75 subclass features are already included in root counts. They are not additional generated candidates.
- **Version expansions:** zero declarations and zero additional candidates; no expansion was performed.

## Coverage and limitations

[normalized-field-inventory.json](normalized-field-inventory.json) inventories all **729 paths**, all JSON types and object field sets, occurrences, parent-based missing counts, explicit nulls, empty arrays/objects, bounded examples, owning primary sources, and declared editions. Array element paths have no property optionality denominator. There are zero explicit null values in this snapshot. Category labels distinguish references, mechanics/configuration, identity/discriminators, description/presentation, and import metadata; they do not authorize automatic public exposure.

Rich descriptive entries and fluff are retained in source; discovery samples are internal analysis material. No prose-derived mechanics, identities, or references are inferred. See the design documents for preservation and public projection decisions.
