# Spell School Metadata Implementation

## Backend mapping

`SpellSchool` is the single authoritative mapping from 5eTools source code to display name. The full corpus contains exactly these eight codes:

| Code | Name | Spell roots |
|---|---|---:|
| A | Abjuration | 118 |
| C | Conjuration | 176 |
| D | Divination | 74 |
| E | Enchantment | 99 |
| I | Illusion | 67 |
| N | Necromancy | 83 |
| T | Transmutation | 171 |
| V | Evocation | 181 |
| **Total** | | **969** |

The normalized source value remains the original one-character code. The catalog validates every school code at startup; the mapper projects it to `SpellSchoolDto(code, name)` for both summary and detail responses. Unknown codes fail explicitly rather than falling back to an arbitrary label.

## API compatibility

The `school` JSON property changes from a string (for example, `"V"`) to an object (for example, `{ "code": "V", "name": "Evocation" }`) in both `SpellSummaryDto` and `SpellDto`. This is a deliberate breaking response change for clients that deserialize `school` as a string. Spell IDs, source identities, routes, and all unrelated fields remain stable. No duplicate school mapping is needed in a frontend.

Filtering still accepts a source code and now also accepts a case-insensitive full school name. Unknown school filter values return the established invalid-parameter response; the backend does not silently reinterpret them.

## Verification

The focused API test compares all 969 normalized definitions with their summary and detail school projections, asserts the corpus distribution above, checks unknown-code rejection, filters by both `V` and `Evocation`, and checks REST detail serialization. Existing all-spell public serialization coverage continues to assert that details contain no prose, raw markup, or import internals.
