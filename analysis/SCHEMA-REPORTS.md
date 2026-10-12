# JSON schema analysis reports

Run the scanner independently of Spring with:

```sh
mvn exec:java -Dexec.args="src/main/resources/data analysis"
```

To scan only spells or monsters, pass that directory as the first argument. Each immediate source directory is treated as a domain; nested directory names remain part of the domain path. Four files are written per discovered domain. Reports are advisory and do not filter source properties.

For a focused domain scan, direct the output to the domain report folder, for example:

```sh
mvn exec:java -Dexec.args="src/main/resources/data/spells analysis/spells"
mvn exec:java -Dexec.args="src/main/resources/data/monsters analysis/monsters"
```

Example tuning options:

```sh
mvn exec:java -Dschema.example.maxChars=300 -Dschema.example.maxDepth=6 \
  -Dschema.example.maxFields=30 -Dschema.values.maxDistinct=20 \
  -Dexec.args="src/main/resources/data analysis"
```

## Reports

- `schema-summary.json` lists source files, collection/entity counts, every discovered normalized property path, JSON types and occurrence/missing/null counts, string length ranges, bounded low-cardinality values, structural variants, suggested Java types, and scan status.
- `schema-conflicts.json` contains paths with incompatible JSON types, occurrence counts, up to three bounded actual JSON examples per type, source filenames, entity indices, and advisory type suggestions. Integer and decimal observations are treated as compatible numeric representations.
- `schema-dynamic-keys.json` conservatively identifies objects whose keys all match numeric spell-level/recharge notation. It includes keys, value types, bounded object examples, affected object counts, and confidence. Other dynamic key patterns are not inferred automatically.
- `schema-gameplay.json` classifies all paths as `GAMEPLAY_CORE`, `GAMEPLAY_DESCRIPTION`, `PRESENTATION`, `SOURCE_METADATA`, or `UNKNOWN`. Rules use path-name hints, retain nested `entries` and inline-tag paths, and never remove data. Review classifications before using them to shape records.
- `coverage-manifest.json` lists each `(domain, top-level collection)` independently, with contributing source files, entity count, property-path count, report locations, and files that could not be associated with a collection. Each domain summary also repeats its own collection-level entries. The shared domain reports retain collection identity through top-level path prefixes (for example, `monster[]` and `legendaryGroup[]`); use `propertyPathPrefix` in the manifest to filter them.

Structural labels can overlap: `OPTIONAL` is based on missing occurrences among objects at the normalized parent path; `POLYMORPHIC` means incompatible JSON value types; `HETEROGENEOUS_ARRAY` means array elements have multiple types or sufficiently different object shapes; `DYNAMIC_KEYS` means the conservative numeric-key heuristic matched; and `STRUCTURAL_VARIANT` means repeated objects have field sets differing by at least two names. One-field differences are treated as likely optionality to avoid warning noise. Recursive `.entries[]` paths are capped at two repeated levels and aggregate deeper instances at that recurring pattern.

Examples preserve actual JSON values and structure up to the configured depth, field, and string limits. They include the source-relative filename and the entity's `name` or `id` where available, plus a stable entity index. Omitted deep or broad values are marked in the sample; they remain fully traversed for statistics. Free-text values are not enumerated. Known low-cardinality fields and booleans keep at most the configured number of distinct values, with truncation explicitly reported.

Gameplay classification matches exact, case-insensitive path segments. Metadata takes precedence over gameplay, followed by core mechanics, rule-bearing descriptions, and presentation. The default token lists are included in both summary and gameplay reports and can be overridden with `-Dschema.classification.metadata=...`, `-Dschema.classification.gameplayCore=...`, `-Dschema.classification.gameplayDescription=...`, or `-Dschema.classification.presentation=...`; values are comma-separated. `UNKNOWN` is the fallback.

An output's `scanComplete` is false if a source file in that domain failed. Failures include filename, approximate location, JSON path, and parser error; the process returns nonzero if any input failed. A streaming parser processes one file at a time. Aggregated path statistics, bounded examples, object-shape samples, and key summaries remain in memory.
