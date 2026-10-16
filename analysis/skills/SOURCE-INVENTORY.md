# Skills source inventory

## Corpus

- File: `src/main/resources/data/skills.json`.
- Root: one `skill` array; no other root collections or metadata records.
- Records: 36 definitions, 36 unique case-insensitive `(name, source)` identities.
- Primary source codes: PHB (18 definitions) and XPHB (18 definitions).
- Source Registry coverage: both codes exist; no source identity additions are needed.
- Reprints: 18 PHB definitions point to the corresponding XPHB definition using `Name|Source`; XPHB definitions have no `reprintedAs` field. The pairs have the same name, but remain distinct source-qualified definitions.
- No copy, inheritance, or version-generation instructions are present.

The 18 names in each edition are Acrobatics, Animal Handling, Arcana, Athletics, Deception, History, Insight, Intimidation, Investigation, Medicine, Nature, Perception, Performance, Persuasion, Religion, Sleight of Hand, Stealth, and Survival. This is a report of this corpus, not an assumed universal list.

## Fields and coverage

| JSON field | Type | Coverage | Interpretation / destination |
|---|---|---:|---|
| `name` | string | 36 | Required definition name; normalized and public. |
| `source` | string | 36 | Required source code; shared `SourceIdentity`. |
| `page` | integer | 36 | Source occurrence locator; shared `SourceReference`, public. |
| `ability` | string | 36 | Explicit values `dex`, `wis`, `int`, `str`, `cha`; typed ability association. |
| `entries` | array | 36 | Description/explanation; retained in import provenance, excluded from public DTO. |
| `reprintedAs` | string array | 18 | Pipe-delimited name/source reprint targets; retained in provenance and registry-validated. |
| `srd` | boolean | 18 | 2014 rules availability flag; import provenance only. |
| `basicRules` | boolean | 18 | 2014 Basic Rules availability flag; import provenance only. |
| `srd52` | boolean | 18 | 2024 SRD availability flag; import provenance only. |

No explicit nulls were observed. Optional fields are absent rather than null. All 36 records have one or more `entries` elements. The corpus has 38 entry elements: 37 strings and one object. The sole object is an entry of `type: list` with an `items` string array, nested in PHB Athletics. No other nested entry fields or polymorphic variants occur. The shared typed `EntryDte` hierarchy supports this shape and validates its fields.

## Mechanics and cross-entity references

The source explicitly provides the associated ability for every skill. Values and counts are: DEX 6, WIS 10, INT 10, STR 2, CHA 8; CON does not occur. These source fields are sufficient to expose a typed ability association. This is not inferred from skill names or customary rules.

The records define reusable skills and contain no character-specific proficiency, expertise, ability modifier, check bonus, or other numeric mechanic. Descriptive entries mention checks and examples, but do not define a reusable check operation or bonus formula. They are retained internally because they are part of the imported source, and excluded from public JSON pending publication review.

`reprintedAs` links skill definitions across PHB and XPHB. These are references between source-qualified definitions, not instructions to merge definitions. Monster data already contains `{@skill ...}` markup and skill bonus structures; those describe references/creature statistics and do not turn this catalog's definitions into proficiency state or check calculations. Background, class, feat, and character proficiency relationships are not present in `skills.json`.

## Edge cases and handling

- PHB Athletics has both a prose string and a structured list entry. The typed shared entry model preserves both.
- PHB definitions include `srd`, `basicRules`, and `reprintedAs`; XPHB definitions instead include `srd52`. Availability flags are source-edition-specific and are not collapsed into one public classification.
- Page values differ by source; XPHB skills use page 14 while PHB entries span pages 175–179.
- The ability vocabulary is open in the source format, but this corpus has only the five listed values. Unsupported future values fail normalization explicitly rather than silently becoming a guessed ability.
- Every primary and reprint-target source is already in the shared Source Registry. No aliases or ambiguous source relationships were discovered.
