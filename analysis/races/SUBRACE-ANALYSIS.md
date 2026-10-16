# Subrace, Lineage, and Variant Analysis

There are 160 race records and 98 subrace records. All but five subrace records include both parent fields (`raceName` and `raceSource`); the five PHB records without names are relationship/auxiliary records rather than independently identifiable selectable options. Subrace names include ordinary subraces, lineage choices, and explicitly labeled variants. The race `lineage` flag is present on 50 records.

Subraces have their own source identity and may carry local mechanics, `overwrite` declarations, source references, copy or version declarations. This supports independently addressable subrace definitions with an explicit parent reference. A parent/subrace relationship is not itself sufficient evidence to copy all parent mechanics into the child. The implementation should retain parent and local mechanics separately; it must not duplicate inherited traits unless a specific source operation establishes that behavior.

The dataset has no reliable global `selectable` flag. Stage A normalizes named subraces as source-specific subrace definitions and leaves the five nameless relationship entries as import records. Direct and template-backed subrace versions expand into 41 additional definitions. The complete Stage A output is 356 definitions: 160 race roots, 93 named subrace roots, and 103 expanded versions. A future character builder can present parent/child choices using the relationship instead of requiring flattened definitions.

Two same-name/source subrace pairs have distinct parents (`Ixalan|PSX` and `Variant; Mark of Finding|ERLW`). Their IDs include the parent source and name. Parent mechanics remain separate; only explicit version operations targeting parent entries use parent entry data during expansion.
