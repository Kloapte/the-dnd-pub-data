# Dependency Matrix

This matrix identifies likely explicit relationships to examine during each future source-discovery stage. It is not a statement that every mention is a resolvable definition reference. Reuse the existing Step 5 resolver for exact source-qualified references after typed extraction; do not add adapters in this planning task.

| New family | Existing target domains | Likely semantics to distinguish | Resolver feasibility / boundary |
|---|---|---|---|
| Adventures index | Books / Source Registry | source identity, publication metadata, contents headers | Primarily provenance and navigation; not a content link to an adventure-body catalog (bodies absent). |
| Bastion facilities | Items, Monsters, Classes, Feats, Optional Features, Skills | hirelings, material/equipment, prerequisites and option filters | Resolve only typed UIDs; class or skill labels may be unqualified/filter text. |
| Character creation options | Classes, Races, Backgrounds, Feats, Optional Features, Skills, Languages, Spells | choice categories, prerequisites, explicit grants | Keep choice/filter semantics and preserve owner level/context. |
| Cults | Monsters, Conditions, Actions, Spells, Deities | statblock-like powers, deity markup, conditions | Do not convert prose markup into grants/effects. Exact UID links only. |
| Boons | Feats, Spells, Conditions, Classes | prerequisite or grant references | Preserve boons as definitions; distinguish prerequisites from grants. |
| Deities | Books/Sources, Items, Spells, Monsters, Actions | pantheon, reprints, deity markup, named equipment | Five identity collisions require pantheon context. Markup is not automatically an effect or grant. |
| Encounters | Monsters, Items, Adventures, Actions, Conditions | table outputs, creature/item references, narrative results | Exact source-qualified IDs may resolve; display names and encounter text remain non-binding. |
| Crochet patterns | Items, possibly recipes | named materials, measurement and instruction text | Do not map material names to Item definitions absent explicit source identity. Likely no RPG resolver integration. |
| Treasure tables | Items, Magic Variants, Monsters, Sources | item UIDs, item groups, random table result, loot categories | Treat groups/ranges as selection expressions, not concrete item grants. No generated items. |
| Name tables | Races, Backgrounds, Sources | ancestry/culture labels and name sets | Most associations are presentation categories, not references to race definitions. No source inference. |
| Objects | Items, Monsters, Actions, Conditions | statted objects, attack markup, explicit spell/item links, object category | Implemented. Spell/item links use existing adapters; unsupported targets remain source-preserved. Object and Item identities stay separate. |
| Recipes | Items, Sources | ingredient labels, quantities, recipe yields | Ingredient names are not item references unless UIDs are explicitly authored. Content restrictions are material. |
| Rewards | Feats, Items, Spells, Classes, Conditions | reward grant, choice, prerequisite, scaling | Preserve typed grants and alternatives; avoid application/calculation. |
| Traps | Conditions, Monsters, Actions, Items, Skills | effects, countermeasures, trigger and detection | Condition/action UIDs may be exact; descriptive mechanics remain text. |
| Hazards | Conditions, Monsters, Actions, Items, Skills | environmental effects, saves, damage, countermeasures | Separate from trap identity; no generic effects engine. |
| Variant Rules | Classes, Skills, Conditions, Items, Spells, Sources | rule topic links, variants and presentation | Definitions are explanatory rule references; markup may link to existing catalogs but not imply runtime binding. |
| Vehicles | Items, Monsters, Actions, Conditions, Sources | vehicle equipment, crew, vehicle actions, upgrades | Exact reference candidates need typed fields. Vehicle stat blocks remain their own domain. |
| Vehicle upgrades | Vehicles, Items, Objects | compatibility restrictions and stat adjustments | Applicability/filter is not a concrete vehicle/item grant. Keep upgrade identity separate. |

## Existing resolver reuse

The Step 5 snapshot supports SPELL, ITEM, BASE_ITEM, FEAT, OPTIONAL_FEATURE, SKILL, and LANGUAGE. It can handle exact UIDs after an owner-specific typed extractor confirms the field's semantics. It does not currently target MONSTER, ACTION, CONDITION, DISEASE, DEITY, VEHICLE, OBJECT, or source/book entities. Those additions should be proposed only with evidence of exact structured references and an owner use case. The existing class feature resolver remains domain-owned.

An implementation should report occurrence counts, distinct authored identities, selector/filter counts, and resolution outcomes separately. A table containing `{@item ...}` in explanatory text is not automatically a reference-bearing structured field.
