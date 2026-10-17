# Batch 2 Internal Focus Design Checkpoint

Recorded before production changes, 2026-10-17. Continuation is explicitly authorized by the
Batch 2 implementation request.

## Source evidence and domain boundary

`items.json` contains 2,512 concrete Items, with 52 ordered string-array `focus` values and two
Boolean values, both true (Ruby of the War Mage, XGE and XDMG). The 230 Base Items contain no
focus fields. Three concrete Dragon-Touched Focus copies inherit the Slumbering definition's
array; effective coverage is 55 arrays and two Booleans among 2,742 definitions. There are 67
copy roots and no Item version declarations. No source focus field is null, false or an empty
array. Absence remains meaningful.

Seven additional arrays belong to `itemGroup` selectors, outside the 2,742 definition corpus.
Those selectors remain typed internal auxiliary source data. They are not new Item definitions,
and their focus values are not automatically applied to their member Items.

The existing mapper does retain array labels in the mixed `classifications` list. Thus the gap
is loss of the distinct focus variant/role, rather than total loss of every label. The existing
Boolean `focusEnabled` is null for arrays and absent focus. Preserve both legacy projections.

## Chosen normalized representation

- `ItemFocus`: small sealed internal interface.
- `ItemBooleanFocus(boolean enabled)`: an explicitly authored Boolean, including false.
- `ItemClassFocus(List<String> classLabels)`: ordered, exact authored labels, defensively copied.
- `ItemDefinition.focus`: nullable `ItemFocus`; null means no effective authored focus value.

No closed Class enum, Class ID, inferred source, eligibility expression, public DTO or new
catalog is introduced. An empty array, if supplied, becomes `ItemClassFocus([])` and remains
distinct from null and Boolean false. The corpus contains neither empty nor false values;
synthetic tests cover the representations already supported by the source DTE.

Resolve the existing typed `ItemStringListDte` with the mapper's current nearest-non-null copy
lookup, then map it directly. Non-null child arrays (including empty arrays) and false Booleans
override parents. Absent child values inherit through existing copy chains. No copy algorithm
or version expansion is added. Existing DTE behavior treats explicit null like absence; there
are no explicit-null corpus focus fields, so a new null-presence model is unnecessary here.

Retain the existing `ItemDefinition.focusEnabled` component and its exact meaning: authored or
inherited Boolean value, otherwise null. Both fields are immutable and derive directly from
the same resolved DTE; arrays never imply true. Retain `classifications`, including existing
focus labels and their order. Public `ItemDto`, `ItemSummaryDto`, services, routes, references
and Magic Variant matching input remain unchanged. `ItemDto.from()` continues its explicit
projection and does not include the new focus value.

## Validation and publication

Before implementation, capture fingerprints for all IDs, existing normalized fields, complete
public detail/summary JSON, Magic Variant matching input/results/statistics and resolver results.
Then verify every authored/effective focus value and all existing contracts, including copies.

Review the source field parser with synthetic invalid cases. Require actual string array tokens;
numeric, Boolean, null and nested array elements must not be coerced into class labels. Any
necessary correction is restricted to `ItemStringListDeserializer`, with full-corpus validation.

No new field is publicly published. This internal lossless normalization does not establish
publication permission or interpret class eligibility. Original Step 7 scanner evidence remains
unchanged; this batch records its own reproducible audit and implementation evidence.
