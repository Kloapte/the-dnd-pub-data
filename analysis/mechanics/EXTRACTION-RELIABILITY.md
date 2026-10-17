# Extraction Reliability

## Independent confidence dimensions

| Level | Meaning | Evidence required |
|---|---|---|
| `EXACT_STRUCTURED` | Directly authored value/relationship with explicit source semantics | Typed field plus catalog-specific semantics; no interpretation |
| `DETERMINISTIC_CONTEXTUAL` | Formal enclosing structure and a validated syntax contract establish the limited association | Explicit parent/role boundary; all observed variants accounted for; fail or retain unknowns |
| `HEURISTIC` | Natural language, adjacency or a probabilistic rule is needed | Record uncertainty and review requirement; do not make authoritative claims |
| `UNSUPPORTED` | Current source/validation cannot establish the requested fact | Preserve evidence; do not fabricate a value |

Each finding needs both **parse confidence** and **semantic confidence**. A damage annotation may
have exact parsed arguments while its target, application time and branch remain unsupported.
Publication eligibility is a third independent decision; neither confidence axis grants rights.

## Candidate assessment and coverage

Counts describe authored source coverage, not proven extraction of complete effects. Counts may
overlap. All predicates and affected owners are reproducible in `candidate-evidence.json`.

| Candidate | Coverage | Parse / semantic confidence | False positives, false negatives and review |
|---|---|---|---|
| Class table source positions | 97 rows, six roots (four classes/two subclasses) | EXACT_STRUCTURED / EXACT_STRUCTURED for position and retained cell | No prose interpretation; copy-effective impact not counted yet. Review API compatibility, preserve withheld positions |
| Item focus Boolean/array variants | 54 roots: 52 arrays, two Booleans | EXACT_STRUCTURED / EXACT_STRUCTURED for authored restriction representation | No invented Class IDs or eligibility calculation. Future mapping must cover inherited values |
| Feat omitted typed grant/defense fields | 74 fields across 59 roots | EXACT_STRUCTURED / EXACT_STRUCTURED at source field granularity | Choices/categories must remain choices. New shared types require shape comparison; all 314 effective Feats need future coverage |
| Feat prerequisite proficiency objects | 12 roots | EXACT_STRUCTURED / EXACT_STRUCTURED for category keys | Current strings are not enough to reconstruct original keys; read DTE directly. No prerequisite evaluation |
| Item signed bonus values | 410 expressions across 339 roots | Deterministic numeric-token parse / EXACT_STRUCTURED property role | All observed tokens match signed-integer syntax; no claim about stacking, recipients or conditional application. Preserve original token |
| Spell scaling fields | 41 roots, 36 object/five array | EXACT_STRUCTURED / existing domain semantics | Already normalized/public. No additional extraction proposed; fix analysis counts only |
| Explicit spell geometric ranges | 78 roots | EXACT_STRUCTURED / existing range semantics | Already normalized/public. Range geometry is not necessarily the whole effect area; do not generalize to point-range spells |
| Generic markup annotation boundaries | 91,994 tags, zero unbalanced | Deterministic boundary parsing / no effect claim | All current strings scanned, including copies/support. Zero unbalanced tags is not full grammar validation. Future parser needs negative cases and variant tests |
| Spell formula annotations | 642 damage, 126 dice, 148 scaledamage, 14 scaledice tags on 969 roots | Deterministic syntax after grammar validation / effect role usually HEURISTIC or UNSUPPORTED | Healing modifiers, target count, repeated damage, secondary effects and tables need context; manual associations required for effects |
| Monster attack/save/recharge annotations | 4,500 atk; 982 atkr; 5,870 hit; 9,574 damage; 5,085 DC; 907 recharge tags | Exact tag/owner preservation / constrained role only | These include authored transforms. Separate action kinds/names, versions, spellcasting entries and conditional riders. Full effect assembly needs review |
| Explicit save/response labels | 529 actSave; 548 failure; 252 success; 84 success-or-fail; 76 trigger/response each | Deterministic label syntax / outcome association only if formally validated | Asymmetric counts rule out universal one-to-one pairing. Failure qualifiers and margin-by-five case must be preserved |
| Object attack/hit containers | Seven roots | EXACT_STRUCTURED boundary / DETERMINISTIC_CONTEXTUAL section association | Stronger than prose proximity; does not identify complete damage/target/effect. Manual review before any effect projection |
| Item recharge field annotations | 220 roots with tagged root charge expressions | Deterministic syntax / role anchored by field | Formula can describe a recharge amount without reset timing. Existing public raw markup needs contract review, not automatic conversion to effects |
| Existing typed additionalSpells | 465 field-presence roots: 464 arrays, one explicit null | EXACT_STRUCTURED / source-specific grants, options, filters, usage schedules | Already retained by owners where documented. A null is not a grant. Preserve absent versus explicit null through inheritance |
| Feature resource consumption | 24 Class Feature and 115 Subclass Feature roots | EXACT_STRUCTURED / authored resource consumption only | Already normalized/public. Do not infer resource maximum/recharge or character resource instance |
| Treasure currency expressions | 62 fields on 20 definitions | Deterministic expression syntax after validation / authored coin-amount role | Already typed strings internally; no assumed common roll die or award. Publication of expressions/full tables remains separately reviewed |
| Vehicle component movement | Eight roots have movement containers | EXACT_STRUCTURED for mode labels / numeric speed UNSUPPORTED from these fields | `VehicleMovementSpeedDte` contains mode and descriptive entries, not numeric speed leaves. Top-level structured speed is already public; never derive component speeds from instructions |
| Trap/Hazard effects, conditions, upgrade benefits, Variant Rules | 37/73/30 Condition/31 Upgrade/231 Rule roots as review denominators | Tags parse; full mechanics HEURISTIC | Keyword matches miss paraphrases and include examples/negation. Manual review; no automatic effect extraction coverage promised |
| Multiattack graph | 2,223 headings on 2,221 raw Monster roots | Exact heading recognition / graph HEURISTIC or UNSUPPORTED | Matching names are not stable action references; optional attacks, replacements and attack counts require interpretation |
| Recipe, lore, name or narrative-result effects | No approved gameplay target | Syntax may parse / UNSUPPORTED as a game effect | Numeric quantities and conditional wording create false positives; exclude from gameplay enrichment |

## How to measure reliability in a future implementation

For structured correctness work, the acceptance denominator is the complete affected field set plus
all effective definitions, with exact preservation/serialization assertions. No precision estimate
is necessary for directly authored position or category keys.

For syntax annotation work, report recognized tags, supported variants, rejected/retained unknowns,
correct offsets and exact round-trip tokens. Test nested tags, extra/empty arguments, display text,
malformed braces and unsupported codes. Do not call a parser's success percentage effect accuracy.

For contextual associations, construct a reviewed gold set across sources/editions and negative
cases: examples, negation, immunity/removal, options, repeated/secondary effects, tables, references,
multiple branches, companion-relative modifiers and cross-string sections. Report precision,
recall and abstentions separately per narrow association. This analysis creates no gold set and
therefore claims no numeric semantic accuracy or automation rate.

## Scanner limitations

- Parses authored JSON, not copy/template/version-effective records; transformations are separately
  labeled where their locator establishes them. Future effective annotations must keep lineage.
- Marker regular expressions run on parsed string nodes, never raw JSON text, but are still
  lexical. They include headings and metadata; Recipe conditional matches demonstrate this.
- `type=table` counts coexist with other physical table shapes. Neither kind proves mechanical
  significance, roll semantics or publication eligibility.
- No dice evaluation, probability normalization, effect-target inference, source-default inference,
  filter expansion, reference resolution or entity promotion occurs.
- Samples are bounded and deterministic, not random/representative statistical samples. Exact
  counts use the full corpus; rare arity/nesting variants have their own saved examples.
- Filename hashes establish the scanned bytes, not their licensing status. A stable array index is
  a locator within that fingerprinted snapshot, not a new persistent entity ID.
