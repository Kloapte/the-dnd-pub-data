# Background Inheritance Analysis

There are 171 root records, 26 `_copy` declarations, zero `_versions`, and zero root `_mod` declarations. Each copy target resolves by exact `(name, source)` to a background root; copy chains are checked for cycles. The 26 children remain independently cataloged under their own source identity, while parent identity and copy operations are retained as internal provenance.

The only observed modification target is `entries`. Of the 26 copies, 21 declare entry modifications containing 23 operations: 13 `insertArr` and 10 `replaceArr`. Payloads are one Entry or arrays of Entries. Replacements target either an array index (3) or a named entry (7). The resolver applies operations in declared order. No other modification operation is supported or silently ignored. Fields absent on the child inherit from the parent; child identity, source/page, source-specific flags, and explicitly supplied fields take precedence. `reprintedAs` and secondary source references are preserved separately from copy lineage.

Expected normalized catalog count is **171**: copies are already source roots, not additional version expansions. Import provenance tracks each root, its parent, and operations. No version-generated definitions are present.
