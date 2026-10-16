# Magic Variants Domain Model

The source contains rule definitions, not an enumerated catalog of every resulting item. The domain therefore has two record kinds: `GENERIC_RULE` for 225 applicable variant rules and `ITEM_SPECIFIC_TRANSFORMATION` for five explicitly named copy-derived transformations. Neither kind is treated as an ordinary concrete item.

Rules preserve source-aware identity, source references, required and excluded conditions, typed transformation properties, and measured candidate counts. The five transformations additionally preserve a base-item reference (name/source) and ordered copy-operation provenance. Their base anchors are unresolved in Item Catalog, so no Item ID or generated Item is fabricated. `linkedLootTables` remains auxiliary catalog metadata.

The existing Item Catalog and its DTO contract are unchanged. No eager generated definitions, effects execution, inventory state, spell catalog dependency, or generic rules engine is introduced. Public API exposes safe structured fields only; descriptive entries, markup and importer instructions remain internal.
