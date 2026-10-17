# Item Reference Analysis

## Verified counts

- Background starting equipment: 718 exact-resolved item occurrences, 8 unresolved occurrences over three exact UIDs (`holy symbol|phb`, `holy symbol|xphb`, `arcane focus|xphb`), zero ambiguous, and 204 text/equipment-type descriptors. They remain authored references; the unresolved UIDs are not safe to guess from similarly named entries.
- Class starting equipment: 169 authored item references in the full class cross-reference analysis: 166 concrete exact item/base-item targets and three item-group targets. The group targets are two `holy symbol|xphb` and one `druidic focus|xphb` in the class report; these are selection groups, not missing Item definitions.
- Item catalog: 2,512 Items and 230 Base Items; 241 item-to-baseItem UIDs were exact-validated. `itemGroup` has 119 auxiliary collections and does not create addressable item definitions.
- Magic Variants: 225 generic rules produce 12,663 potential rule/item matches. Five named item-specific transformations anchor through two absent XDMG item identities. These are variant applicability records, not generated items.

## Classification rules

Concrete `item|source` and `baseItem|source` UIDs are exact definition links. Item group references remain group/category selection data. Equipment packages retain their alternatives, quantities, and free-text descriptors. A generic Magic Variant rule's eligible item predicate is a filter over Item definitions, not a generated item or a direct reference to each candidate. Do not resolve prose such as “a weapon” to an item.

The differing `holy symbol`/focus outcomes between Background and Class corpora demonstrate why owner source must not be injected and why a single global name-only resolver would misstate source intent.
