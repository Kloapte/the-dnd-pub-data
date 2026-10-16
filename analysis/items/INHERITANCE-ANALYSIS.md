# Item inheritance and variants

## Counts and semantics

- Root concrete `item` records: 2,512.
- Root `baseitem` records: 230.
- `item._copy` declarations: 67. Each is a root import record that produces a separately usable definition, not merely lineage metadata.
- `itemType._copy` declarations: 2. These resolve auxiliary type records, not item definitions.
- `_versions`: 0 in both source files.
- `_mod`: 0 at root; 35 copy declarations include modifications.
- Resolved copy roots: 67. Each root is expanded in-place through its copy chain and modification operations; there are 0 synthetic definitions beyond the source roots.
- Final item catalog: 2,742 definitions (2,512 items + 230 base items), including the 67 resolved copy roots.

Every `_copy` target is resolved against an item record in the same import corpus; copy targets inherit source records, then root scalar/list/object properties override inherited values. `_preserve` retains only explicitly named target metadata where the child omits it. Definition `name` and `source` always come from the child/root identity. We do not expose copy directives or provenance publicly.

## Observed operations

The 35 `_mod` declarations contain 68 property operations: `entries.insertArr` (32), `entries.replaceArr` (1), `entries.appendArr` (21), and `property.appendIfNotExistsArr` (14). Supported array operations preserve order. `insertArr` uses the observed index convention, including `-1` as append. `replaceArr` requires an existing in-range index. `appendIfNotExistsArr` compares exact source values. Any unknown operation, property, malformed target, or invalid index fails import explicitly.

## Version and template behavior

No `_versions` declarations exist. `itemEntry.entriesTemplate` contains 13 auxiliary text templates used by grouped or parameterized entries; these are not concrete definitions and are not expanded without an associated explicit variant record. Item groups contain selection/member references and are not expanded into additional catalog records. The two copied `itemType` records resolve auxiliary category definitions only.

The 67 copy roots remain separate source identities and are returned as ordinary definitions after inheritance resolution. The final catalog has 2,742 definitions: 2,742 source definition roots, of which 67 require copy resolution, and 0 generated definitions. We do not import `magicvariants.json`, and this pipeline does not synthesize variants from it.
