# Race and Species Edition Analysis

The corpus contains source-specific records from many books and both legacy PHB and XPHB. The source explicitly supplies `edition` on 18 race roots, while many other records have no edition property. Edition must therefore not be inferred for every record from publication date. PHB and XPHB names remain distinct identities.

Legacy records commonly encode fixed `ability` values, language grants, and named traits in entries; some structured fields such as size, speed, darkvision, and resistances are also present. XPHB records include `creatureTypes`, `sizeEntry`, and `creatureTypeTags`, and may encode 2024 mechanical distinctions in typed fields. The corpus also retains legacy and updated versions side by side, e.g. Aasimar and Dragonborn from MPMM/PHB versus XPHB. These are not merged.

The `ability` field appears on both older and newer source records and can contain fixed increases or a `choose` structure; absence is meaningful. Languages are often expressed by the legacy `languageProficiencies` structure; newer records may describe language mechanics in entries rather than an explicit proficiency field. No mechanics are inferred from prose.

The normalized model consequently uses one source-aware definition model with optional typed mechanics and an explicit source edition value only when the source provides it. Publication metadata and naming conventions are not used to backfill missing edition data.
