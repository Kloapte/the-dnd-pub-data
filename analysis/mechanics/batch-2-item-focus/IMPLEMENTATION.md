# Step 7 Batch 2: Preserve Item Focus Variants

Implemented and verified on 2026-10-17 against the pinned 5eTools v2.37.0 corpus.
This batch changes internal normalization only.

## Original gap and source audit

`ItemStringListDte` already distinguishes a Boolean from an ordered string array.
`ItemMapper.focusEnabled()` reads only its Boolean component. There was no distinct normalized
focus representation for class-label arrays.

Direct mapper inspection refines the original G2 finding: array labels were already appended to
the mixed `classifications` list, including inherited labels. They were not wholly discarded.
The lost distinction was their authored focus role and variant, separate from other classification
labels. This implementation keeps both legacy projections unchanged and adds a typed internal
focus value. It does not reconstruct focus information from either legacy projection.

The [design checkpoint](DTO-DESIGN.md) was recorded before production implementation.
[audit-focus.mjs](audit-focus.mjs) parses both source files, inventories every focus field, follows
copy chains and fingerprints the inputs. [focus-evidence.json](focus-evidence.json) records every
authored/effective focus owner, exact locator, value and origin. Original Step 7 scanner evidence
is unchanged.

### Raw versus effective coverage

| Population | Records | Array focus | Boolean true | Absent focus |
|---|---:|---:|---:|---:|
| Authored `items.json/item` | 2,512 | 52 | 2 | 2,458 |
| Authored `items-base.json/baseitem` | 230 | 0 | 0 | 230 |
| Effective Item/Base Item definitions | **2,742** | **55** | **2** | **2,685** |
| Auxiliary `items.json/itemGroup` selectors | 119 | 7 | 0 | 112 |

There are **54 authored concrete Items with focus** and **57 effective definitions with focus**.
The seven additional arrays on group selectors are not part of the 52 concrete Item arrays.
Those groups remain typed internal auxiliary source records; they are not promoted to Item
definitions and their focus values are not applied to member Items.

No focus field in either file contains Boolean false, explicit null, an empty array, a scalar
string, a number or an object. Array values are authored, ordered class labels; no label includes
a source qualification. All observed arrays are retained without sorting, case conversion,
deduplication or Class ID assignment.

| Concrete Item array value, in authored order | Raw count | Effective count |
|---|---:|---:|
| `Artificer` | 3 | 3 |
| `Wizard` | 18 | 18 |
| `Sorcerer` | 17 | 17 |
| `Druid, Ranger` | 5 | 5 |
| `Druid, Warlock` | 1 | 1 |
| `Sorcerer, Warlock, Wizard` | 4 | 4 |
| `Warlock` | 2 | 2 |
| `Artificer, Bard, Cleric, Druid, Paladin, Ranger, Sorcerer, Warlock, Wizard` | 2 | 5 |
| **Total arrays** | **52** | **55** |

The two Boolean roots are Ruby of the War Mage, XDMG and XGE, at
`$.item[1880].focus` and `$.item[1881].focus`; both author true.
Representative array locators include +1 All-Purpose Tool TCE at `$.item[0].focus`,
+1 Moon Sickle TCE at `$.item[6].focus`, Bell Branch TCE at `$.item[191].focus`, and
Slumbering Dragon-Touched Focus FTD at `$.item[2070].focus`.

Primary-source counts among the 54 authored concrete focus owners are TCE 37, XDMG 6, XGE 3,
AU 2, FTD 2, AUD 1, DMG 1, GGR 1 and WDMM 1. Effective FTD coverage becomes five; other
source counts are unchanged. Source codes and registry validation are unchanged.

## Final normalized model

Three small types in the existing internal Item model package:

- `ItemFocus`: sealed interface permitting the two observed representation kinds.
- `ItemBooleanFocus(boolean enabled)`: preserves explicit Boolean true and false.
- `ItemClassFocus(List<String> classLabels)`: preserves an exact ordered list, using a defensive
  immutable copy.

`ItemDefinition` gains nullable `ItemFocus focus`. Null represents absence of an effective
authored value. The existing `Boolean focusEnabled` component remains present with its established
meaning. No public DTO is extended.

The mapper obtains `ItemStringListDte` through its existing copy-aware `value()` lookup, once for
the two focus projections. A Boolean maps to `ItemBooleanFocus`; an array maps directly to
`ItemClassFocus`. Neither mapping reads a flattened classification nor the legacy Boolean.

| Resolved typed source | New normalized `focus` | Existing `focusEnabled` |
|---|---|---|
| No value | null | null |
| true | `ItemBooleanFocus(true)` | true |
| false, supported synthetic case | `ItemBooleanFocus(false)` | false |
| Ordered label array | `ItemClassFocus(labels)` | null |
| Empty array, supported synthetic case | `ItemClassFocus([])` | null |

The new records and existing compatibility projections are immutable. Array labels remain in
legacy `classifications` exactly as before. An empty array remains distinct from absence and
Boolean false in the new normalized value. No focus is inferred from `scfType`, item type,
attunement, names or prose.

### Strict source mapping

The existing field deserializer could coerce numeric array elements into strings through
`getValueAsString()`. Its array loop now requires `START_ARRAY`, then `VALUE_STRING` elements
until `END_ARRAY`; unsupported elements fail with a mapping exception. Boolean handling is
unchanged. All real Item and group arrays still deserialize. Unknown-field rejection remains
enabled; no unknown field is ignored and no source DTE schema is broadened.

## Copy and version handling

All 67 Item copy roots continue to resolve through the unchanged `ItemCopyResolver` and mapper
lookup. Three Items inherit focus, all from Slumbering Dragon-Touched Focus FTD:

| Effective owner | Source record | Copy depth to authored focus |
|---|---|---:|
| Stirring Dragon-Touched Focus FTD | `$.item[2214]` | 1 |
| Wakened Dragon-Touched Focus FTD | `$.item[2389]` | 2 |
| Ascendant Dragon-Touched Focus FTD | `$.item[116]` | 3 |

Every inherited label and its ordering are preserved. The new value does not generate additional
definitions or change identities, source metadata, copy lineage, entries or properties.

Synthetic tests establish the existing supported override behavior: a non-null child array,
including an empty array, overrides its parent; false overrides a parent's array and remains
false when inherited by a grandchild. Label overrides preserve duplicates, ordering, case and
whitespace exactly. These are representation tests, not invented corpus variants.

There are **zero Item `_versions` declarations** in either file. No version engine is introduced;
an unsupported version field remains a strict mapping error. Explicit-null focus is likewise
absent from the corpus. Existing DTE behavior treats explicit null like absence, including during
copy lookup; this behavior remains unchanged and is tested rather than introducing an unneeded
null-presence model.

## Public API and publication boundary

`ItemDto`, `ItemSummaryDto`, REST controllers, services, routes and reference projection are
unchanged. `ItemDto.from()` still projects its explicit existing fields and does not include
`ItemDefinition.focus`. Array-bearing Items still have no `focusEnabled` property in public
JSON; Boolean true remains true. A supported false Boolean still serializes as false.

Existing public `classifications` already contain some class labels. That established JSON is
preserved; this batch introduces no new focus-array property or other publication. It does not
authorize future publication of the normalized focus value, prose, markup or eligibility rules.
The public eligibility representation remains a separately approved future decision.

There are no new Class IDs, inferred source codes, target adapters, references or eligibility
calculations. The new field is not used by Magic Variant matching and does not confer a benefit.

## Tests and verification

Nine tests in `ItemFocusNormalizationTests` cover:

1. Pre-change fingerprints of all 2,742 IDs, every existing normalized field, complete service
   detail JSON including references, all summaries, Magic Variant matching input, the complete
   per-rule eligible Item matrix, all 230 Magic Variant definitions/statistics and resolver results.
2. Every one of the 52 authored arrays, all eight exact ordered array variants, both true
   Boolean roots, seven auxiliary selectors and the absence of focus on all 230 Base Items.
3. Every effective Item/Base Item against resolved typed source, including all three inherited
   focus chains, all 67 copy roots and exact absent/array/Boolean totals.
4. Supported synthetic false/empty/absent/null cases and the unchanged legacy/public flag.
5. Copy inheritance and child overrides, preserving false, empty arrays and exact labels.
6. Defensive copying and immutable normalized lists, including empty lists.
7. Strict rejection of numbers, scalar strings, objects, nested arrays, Boolean/null/non-string
   array members, unknown properties and unsupported version fields.
8. Existing Item list/detail/by-name endpoint contracts, including copied Items and Base Items,
   with no new public focus field.
9. Loading both complete resources from an isolated JAR classloader, rebuilding the entire
   catalog and comparing all 2,742 normalized definitions including their new focus values.

No existing test is weakened or modified. Object keys are canonicalized for compatibility
fingerprints; array order is preserved. For the existing normalized-field comparison only, the
new root `focus` component is removed before comparison. The public JSON comparison removes
**no fields**.

| Verification | Result |
|---|---|
| Focused Item focus / Item pipeline / Magic Variant tests | All passed |
| Full Maven suite, including `mvn package` | **418 tests; zero failures, errors or skips** (409 baseline + 9) |
| Stable Item/Base Item IDs | All **2,742** match the pre-change fingerprint |
| Existing normalized fields | Complete fingerprint unchanged |
| Public Item detail and summary JSON | Complete fingerprints unchanged |
| Magic Variant behavior | Matching inputs, per-rule eligible Item IDs, definitions and all statistics unchanged |
| Resolver results | Complete **6,246** result fingerprint unchanged; zero new occurrences |
| Resolver outcomes | RESOLVED **2,517**, AMBIGUOUS **2,054**, UNRESOLVED **181**, DEFERRED **1,494** |
| Source JSON | All **175** hashes match the original analysis baseline |
| Focus audit | Deterministic rerun produces identical evidence |
| Executable JAR | Contains all three focus types and both resources; startup/HTTP checks pass |
| Whitespace | `git diff --check` and new-file checks pass |

Batch 2 production changes are limited to `ItemDefinition`, `ItemMapper`,
`ItemStringListDeserializer` and the three new focus types. Existing public DTOs, copy resolver,
catalog/service/controller implementation, Magic Variant implementation and reference code
retain their original hashes. Previously completed Batch 1 work is preserved.

`verify-packaged.ps1` launches the executable JAR from `target` on loopback with a temporary
port. It checks all 2,742 Item summaries, 230 Magic Variant summaries, source-qualified copy
lookups, both Boolean focus Items, legacy classifications and absence of the new public field.
It stops only its own process. Results and process logs remain under `target`.

Reproduction from the repository root, using the configured Maven cache where needed:

```powershell
node analysis/mechanics/batch-2-item-focus/audit-focus.mjs
mvn test
mvn package
& ./analysis/mechanics/batch-2-item-focus/verify-packaged.ps1
git diff --check
```

## Remaining limitations

- Class labels remain unqualified authored strings, with no Class identity or eligibility inference.
- Public focus-array projection is excluded; legacy classifications remain unchanged.
- Explicit-null versus absent focus is not distinguished by the existing DTE. No corpus record
  requires that distinction; future source changes would need a separate presence assessment.
- Group-selector data remains auxiliary, without member inheritance or new Item definitions.
- No Item version expansion, bonus parsing, other mechanics batch, Step 8 work or frontend change
  is included.
