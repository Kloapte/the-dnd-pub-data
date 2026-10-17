# Step 7 Batch 4: Item Signed Bonuses

## Source audit

The pinned source has **2,512 Item roots** and **230 Base Item roots**. Ten signed-bonus fields
occur on **339 authored Item roots**, for **410 authored field occurrences**. No Base Item root
contains any of the ten fields. Each source field is already a nullable `String` on
`ItemSourceDte`; no DTE changes were needed.

| Source field | Authored occurrences | Effective occurrences | Observed tokens |
|---|---:|---:|---|
| `bonusAc` | 85 | 88 | `+1`, `+2`, `+3`, `+5` |
| `bonusWeapon` | 200 | 201 | `+1`, `+2`, `+3` |
| `bonusWeaponAttack` | 3 | 3 | `+1`, `+9` |
| `bonusWeaponDamage` | 2 | 2 | `+2` |
| `bonusSpellAttack` | 53 | 59 | `+1`, `+2`, `+3` |
| `bonusSpellSaveDc` | 32 | 35 | `+1`, `+2`, `+3` |
| `bonusSavingThrow` | 29 | 29 | `-2`, `+1`, `+2`, `+4` |
| `bonusSavingThrowConcentration` | 1 | 1 | `+2` |
| `bonusAbilityCheck` | 3 | 3 | `-2`, `+1` |
| `bonusProficiencyBonus` | 2 | 2 | `+1` |
| **Total** | **410** | **423** | |

All 410 authored values are JSON strings matching the strict grammar `[+-][0-9]+`. The ten DTE
properties use a field-specific Jackson deserializer to reject JSON numbers instead of allowing
scalar coercion to turn them into strings. There are
408 positive values and two negative `-2` values (both on Stone of Ill Luck, TftYP). There are no
zero values, unsigned numbers, JSON numeric values, expressions, markup or other tokens. `+9`
is retained and parsed as nine in `bonusWeaponAttack`; it is not constrained to the common +1 to
+3 values. The grammar also preserves a leading sign for synthetic `+0` / `-0` values.

## Effective values and copy inheritance

The complete effective Item/Base Item catalog contains **2,742 definitions**. Copy-aware mapping
produces **349 definitions with signed bonuses** and **423 effective occurrences**. This is 13
additional occurrences inherited by ten copied Items. The field counts show the inherited values:

| Field | Inherited occurrences |
|---|---:|
| `bonusAc` | 3 |
| `bonusWeapon` | 1 |
| `bonusSpellAttack` | 6 |
| `bonusSpellSaveDc` | 3 |

Other bonus fields have no inherited occurrences. The ten inheriting copies remain resolved by the
existing `ItemCopyResolver`; inherited values use the same copy-aware `value(...)` lookup as the
legacy `ItemBonusesDto`. No copy behavior, identity, or lineage logic changed. There are no
Base Item bonus values to inherit or normalize.

## Normalized representation and parsing

`ItemDefinition` gains nullable internal `signedBonuses`, separate from the existing string-valued
`bonuses` field:

- `ItemSignedBonuses` has one named component for each authored source field, retaining the
  field's role.
- `ItemSignedBonus` retains both the exact `authoredToken` and parsed `int numericValue`.
- `ItemSignedBonusParser` accepts only a leading `+` or `-` followed by decimal digits. The DTE
  deserializer first enforces that the source token is a JSON string. The parser rejects
  whitespace, unsigned tokens, formulas, markup, and values outside the Java `int` range with an
  explicit exception.

Absent source values remain null, and an Item with no bonus values has `signedBonuses == null`.
Zero is preserved as integer zero while its authored sign remains in the token. The mapper reads
resolved typed source fields directly, including inherited copy values; it never reparses a
display string. Existing `ItemBonusesDto` strings retain their original meaning and are checked
against the token projection in tests.

The numeric value describes only the authored field. The mapper does not stack bonuses, apply
them, or calculate Armor Class, attacks, damage, saves, checks, proficiency or character values.

## Compatibility and publication

No Item or Base Item public DTO, summary, REST route, service projection, Magic Variant matcher,
or reference adapter changes. `ItemDto` continues to expose its existing string-valued `bonuses`
field and does not expose `signedBonuses` or `ItemSignedBonusParser`. The Batch 2 Item focus model
is unchanged.

## Tests and verification

`ItemSignedBonusNormalizationTests` covers all ten raw fields and all 410 occurrences, exact raw
tokens, signs and parsed integers, all 2,742 effective definitions, all 349 affected effective
Items, all 13 inherited fields across ten copies, parity with legacy bonus strings, strict parser
rejections, zero token representation, focus compatibility and public JSON exclusion.
`ItemFocusNormalizationTests` continues to fingerprint every existing normalized Item field,
public detail and summary JSON, Magic Variant input and eligibility results, and all 6,246 resolver
results. Its legacy normalized-field fingerprint excludes only the separately asserted new
internal `signedBonuses` field; public JSON fingerprints exclude nothing.

| Verification | Result |
|---|---|
| Focused Item, focus, pipeline and Magic Variant suite | **43 tests passed** |
| Full Maven package/test suite | **443 tests; zero failures, errors or skips** (436 baseline + 7) |
| Item/Base Item identities | All **2,742** definitions load with unique unchanged IDs |
| Effective signed bonus coverage | **349 definitions / 423 occurrences**, including all 13 inherited values |
| Existing normalized Item fields | Full pre-change fingerprint unchanged, excluding only new internal `signedBonuses` |
| Public Item JSON | Full detail and summary fingerprints unchanged; signed bonuses are absent |
| Magic Variant behavior | Existing matching and full catalog fingerprint tests pass unchanged |
| Resolver results | All **6,246** results/outcomes remain unchanged; no new occurrences |
| Item source JSON | `items.json`: `3af773b48cb874682aae36c14f2e89b89ae1380dd4ae877f7b1e545ec4d27968`; `items-base.json`: `77f843ad3738684eb3d0ef567021f865120fc827a271b5f2d8ca19aa1aa5b067` (both match Batch 2 evidence) |
| All source JSON | No source JSON differs from the checked-in pinned baseline |
| Packaged executable JAR | Verification script passed; all **2,742** Items/Base Items and **230** Magic Variants load; existing copy lookups pass |
| Whitespace | `git diff --check` passes |

## Remaining limitations

The parser intentionally accepts only the corpus-observed explicitly signed integer grammar. If a
future pinned source introduces unsigned numbers, formulas, localized digits or a larger integer,
catalog loading fails with the field name rather than guessing. Public exposure requires a separate
compatibility and publication review. No effect application or character-stat calculation is
included.
