# Step 7 Batch 11A: Preserve Standalone Item Recharge Declarations

Implemented and verified against the pinned 5eTools v2.37.0 corpus. This batch adds internal
normalization for recharge fields that were present in source but did not belong to the existing
normalized `ItemChargeDto` shape.

## Source declaration inventory

The complete audit in [Batch 11](../batch-11-item-charges/ASSESSMENT.md) identified **28** authored
Item roots where `recharge` exists while both `charges` and `rechargeAmount` are absent. All values
are JSON strings with the exact value `dawn`. No Base Item has this field family. The declarations
are source field values, not an assertion that each Item uses a universal charge pool.

| Source records | Count | Source field |
|---|---:|---|
| Bag of Tricks, Gray/Rust/Tan (DMG and XDMG editions) | 6 | `/item/{index}/recharge` |
| Balance, Comet, Donjon, Euryale, Fates, Flames, Fool, Gem, Jester, Knight, Moon, Puzzle, Rogue, Ruin, Sage, Skull, Star, Sun, Talons, Throne and Void Cards (BMT) | 21 | `/item/{index}/recharge` |
| Ventilating Lungs (ERLW) | 1 | `/item/2369/recharge` |
| **Total** | **28** | `recharge` string |

The six Bag of Tricks records are the Gray, Rust and Tan variants from DMG and XDMG. Every
declaration has a distinct source-aware Item identity. The source inventory records the exact
pointer, name, source and value for each record.

The field occurs on 289 authored roots overall. The other recharge values already accompany a
normalized count or amount. The 28 declarations add **28 authored field occurrences** that were
missing from the normalized model. All 28 owners are concrete roots; none is inherited into one of
these roots. No Item `_versions` are present.

## Existing model gap

`ItemSourceDte.recharge` was a string. `ItemMapper` only constructed `ItemChargeDto` when a charge
count or recharge amount existed. That guard intentionally avoided an empty charge object, but it
also discarded a standalone recharge string from `ItemDefinition`.

The DTE now uses `ItemRechargeDte` and `ItemRechargeDeserializer`. The DTE preserves an absent
field, an explicit null and an authored string as separate states. It rejects numbers, Booleans,
arrays and objects. The observed corpus contains no explicit null recharge values. Synthetic tests
confirm that absence, null and present strings remain distinct, including an empty string. The
complete source corpus uses only non-empty `dawn` strings for this specific omission.

## Normalized model and mapping

`ItemDefinition` now has nullable `standaloneRecharge: ItemRechargeDeclaration`. Null represents
an absent effective field or a recharge already preserved by the existing charge model. When a
standalone field is present, the immutable record retains:

- `authoredValue`: the exact source string, with no conversion to a reset rule.
- `authoredOwnerName`: the Item whose source record supplied the string.
- `authoredSource`: the exact source code and authored page when present.

An explicit-null standalone field is represented by a non-null declaration with a null
`authoredValue`; this variant does not occur in the pinned source.

The mapper populates this component only when the effective Item has a recharge string and has no
effective `charges` or `rechargeAmount` field. Existing copy-aware field lookup determines the
effective value. A separate walk of the resolved copy chain identifies the record that authored
that value; the effective child is never represented as the authored source owner unless it
actually supplies the field.

When a recharge declaration already belongs in `ItemChargeDto`, `standaloneRecharge` remains null.
This avoids duplicating existing normalized charge data. The mapper does not derive count,
recovery amount, duration, resource availability or gameplay behavior from the recharge code.

## Copy and version behavior

The 28 source declarations are all local and all remain local in the effective catalog. Full
Item/Base Item loading produces **28 effective standalone declarations** on the same 28 stable
IDs. Existing copies that inherit complete charge structures continue to populate `ItemChargeDto`
and do not gain a duplicate standalone declaration.

The Item copy resolver's supported scalar behavior is nearest non-null source field inheritance;
its `_mod` dialect changes entries and properties, not scalar recharge fields. No source-authored
scalar recharge replacement was found. Tests cover inheritance from a copied parent and a local
child recharge value taking precedence. This is ordinary source-field precedence, not a newly
implemented `_mod` operation. Unsupported scalar copy modifications remain rejected by the
existing strict copy dialect.

## Public API and compatibility

No public Item DTO or endpoint changed. `ItemDto.from` does not project `standaloneRecharge`, and
all existing `ItemChargeDto` values retain their previous construction and JSON shape. No source
prose, tagged formula, resolver data or new reference is exposed.

The existing normalized-Item fingerprint test now excludes the separately asserted additive
internal `standaloneRecharge` component. The complete public detail/summary JSON fingerprints,
Magic Variant projections and all 6,246 resolver outcomes remain part of the unchanged baseline
assertions.

## Tests and verification

`ItemStandaloneRechargeNormalizationTests` covers:

- All 28 source roots, exact stable IDs, exact string values and authored source/page provenance.
- Exactly 28 effective declarations among all 2,742 Item/Base Item definitions.
- Missing, present and empty string values; null behavior; strict rejection of unsupported JSON types.
- Copy inheritance and local child override provenance.
- Existing charge-bearing Wand normalization and omission from public `ItemDto` JSON.

| Verification | Result |
|---|---|
| Focused Item regression tests | **35 passed** across standalone recharge, Item pipeline, focus and signed-bonus suites |
| Full Maven `package` | **522 tests passed**, zero failures/errors/skips (518 baseline + 4) |
| Authored standalone declarations | **28 / 28** preserved |
| Effective standalone declarations | **28 / 28** preserved; no new definitions |
| Item/Base Item identities | All **2,742** definitions remain loaded under their existing IDs |
| Public Item JSON | Existing Item detail/summary fingerprints and Item endpoint tests pass unchanged |
| Resolver | Existing **6,246** occurrences and outcome fingerprint pass unchanged |
| Packaged executable JAR | Application startup passed with the packaged catalog and source resources |
| Pinned source JSON | All **175** hashes match the Batch 10A pre-implementation snapshot |
| Whitespace | `git diff --check` passes |

The first focused run exposed an existing normalized-model fingerprint that needed to exclude the
new internal component, as the earlier Item focus and signed-bonus batches do. After updating that
comparison to remove only the new field, the unchanged-field and public JSON assertions passed.

## Remaining limitations

- The new value preserves the authored recharge code only. It does not establish whether the
  associated rule means a charge pool, uses, stars or another limited-use concept.
- Recharge conditions and timing beyond the literal code remain in descriptive entries.
- No source-authored scalar recharge `_mod` operations or Item versions exist in this corpus.
- Public exposure remains excluded pending a separate publication and compatibility review.
- The separate Item charge syntax adapter remains unimplemented.
