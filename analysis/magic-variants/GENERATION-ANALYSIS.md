# Generation Analysis

## Matching model

The 225 generic rules express candidate matching in `requires` and `excludes`. Required clauses are OR alternatives; each populated field in one clause is ANDed. Exclusion values are alternatives within their field; a matching exclusion disqualifies the item. Matching uses exact case-insensitive source item name/source, type codes, properties, damage types, weapon category and explicit marker facts from the resolved Item Catalog. It does not use prose or fuzzy names.

The rules can change name, rarity, attunement, bonuses, charges, spell grants, resistances and other inherited item properties. Formula fields reference base-item values and are retained without calculation. The source does not define composition or stacking semantics for multiple matching rules.

## Measured catalog interaction

| Measurement | Result |
|---|---:|
| Generic rules | 225 |
| Potential generic rule/item matches | 12,663 |
| Generic rules with no eligible item | 0 |
| Candidate outputs sharing a generated `(name, source)` identity | 3,361 groups / occurrences |
| Candidate generated identities colliding with current Item Catalog definitions | 0 |
| Rule pairs that match at least one same item | 213,179 |
| Distinct same-item/same-output identity conflicts | 0 |
| Item-specific AU transformations | 5 |
| Potential matches for item-specific transformations | 2 |
| Unresolved base-item anchors | 5 |

The duplicate generated identity groups count repeated candidate results across eligible base items. They are evidence that eagerly materializing every rule/item pair as an ordinary item would be misleading. Rule overlap counts do not imply a mechanical conflict; the source specifies no stacking/composition behavior. The current items do not share a `(name, source)` identity with candidate outputs.

The five AU records are explicit named evolutions with `_copy` chains, but their two ultimate XDMG anchors (`Weapon of Warning`, `Armor of Psychic Resistance`) are not concrete definitions in the current Item Catalog. Copy instructions are applied to form the rule's typed transformation and retained separately as provenance. No incomplete item is generated or exposed.

## Decision

Do not generate concrete item definitions. Catalog the source rules and the five explicit transformations as Magic Variant records, preserving applicability and transformation data. The `linkedLootTables` map is auxiliary. No `magicvariants.json` record is an enumerated generated item. No variant is inserted into or allowed to overwrite the 2,742-definition Item Catalog.
