# Book catalog implementation status

The publication catalog is implemented for all 66 records in `books.json`.
Analysis found that the dataset does not cover the source codes used throughout
existing Language and Monster definitions, so their migrations are intentionally
deferred under the user's stop condition. Existing entity IDs and APIs remain
unchanged.

The catalog uses `source` as the stable publication identity and retains
alternate `id` values as aliases. DTEs strictly deserialize known source
properties and preserve polymorphic TOC headers and numeric/string ordinals.
Internal provenance retains TOC and cover metadata, while public DTOs expose
catalog metadata only. `findBySourceCode` returns no match for unresolved codes;
callers must retain the original code. The catalog does not invent a record.

Validation: 66 publications map; unique `id` and `source` keys; alias lookup
for the six differing ID/source codes; deterministic ordering; ambiguous alias
title lookup; strict unknown-property handling; public serialization excludes
TOC and cover paths; resource loading works from a JAR classpath. The full Maven
suite passed: 65 tests, 0 failures, 0 errors, 0 skipped.

The source-code audit covered 201 languages, 6 language scripts, and 4,560
base monsters in 113 bestiary files. Because many references remain unresolved,
the API is a catalog of known records rather than an authoritative resolver for
the current entity corpora. Entity migrations remain outstanding.
