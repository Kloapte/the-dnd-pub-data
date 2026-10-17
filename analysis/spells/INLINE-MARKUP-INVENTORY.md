# Inline Spell Markup Inventory

Counts are from the full-corpus scanner and include the recursive `entries` and `entriesHigherLevel` text trees. Occurrence counts are exact for the 5eTools tag syntax; “spell count” is the number of distinct root spells containing that tag. Arguments are split only at top-level `|` separators. For nested markup, the scanner balances braces before splitting.

| Tag | Occurrences | Spells | Observed argument counts | Classification |
|---|---:|---:|---|---|
| `action` | 117 | 97 | 1:22, 2:93, 3:2 | Reference/presentation; action name is not an effect |
| `adventure` | 1 | 1 | 3:1 | Reference only |
| `b` | 23 | 4 | 1:23 | Formatting only |
| `book` | 7 | 6 | 2:3, 4:4 | Reference only |
| `chance` | 19 | 19 | 5:19 | Presentation/odds; semantics require domain-specific interpretation |
| `classfeature` | 1 | 1 | 4:1 | Reference only |
| `condition` | 580 | 266 | 1:299, 2:276, 3:5 | Condition reference/label; parseable, but not proof the spell applies it mechanically |
| `creature` | 131 | 59 | 1:57, 2:47, 3:27 | Creature reference/label |
| `d20` | 2 | 1 | 1:2 | Rules reference/presentation |
| `damage` | 642 | 362 | 1:642 | Formula annotation; parseable, semantically ambiguous |
| `dc` | 11 | 11 | 1:11 | DC syntax; the tag does not in itself define how the DC is calculated |
| `dice` | 126 | 94 | 1:116, 2:10 | Formula annotation; may be healing, duration, quantity, or another value |
| `filter` | 44 | 23 | 3:2, 4:15, 5:26, 6:1 | Structured filter/reference syntax; not necessarily spell effect |
| `hazard` | 8 | 8 | 2:8 | Reference only |
| `hit` | 5 | 1 | 1:5 | Formatting/presentation marker |
| `i` | 33 | 4 | 1:33 | Formatting only |
| `item` | 13 | 9 | 2:10, 3:3 | Item reference |
| `note` | 1 | 1 | 1:1 | Presentation note |
| `quickref` | 49 | 41 | 3:35, 5:14 | Rules reference only |
| `race` | 24 | 2 | 1:5, 2:10, 3:9 | Race reference |
| `scaledamage` | 148 | 143 | 3:148 | Scaling formula annotation; syntax parseable, effect association ambiguous |
| `scaledice` | 14 | 14 | 3:14 | Scaling formula annotation; effect association ambiguous |
| `sense` | 29 | 27 | 1:16, 2:13 | Sense reference |
| `skill` | 67 | 58 | 1:35, 2:32 | Skill reference |
| `spell` | 159 | 86 | 1:91, 2:68 | Spell reference; arguments may omit source |
| `status` | 57 | 44 | 1:28, 2:21, 3:8 | Status reference/label |
| `variantrule` | 720 | 294 | 2:457, 3:263 | Rule-term reference, not structured mechanic |

The complete per-tag argument samples, source paths, higher-level/table occurrence counts, and target tag paths are retained in `mechanics-investigation-evidence.json`. The generated evidence omits descriptive entry prose.

## Mechanical-tag details

- `damage`: all 642 tags have one argument. The argument is a formula string such as `8d6` or `1d4 + 1`; it can be lexically parsed without claiming what entity/effect it describes.
- `scaledamage`: all 148 tags have three arguments. XPHB Fireball uses `8d6|3-9|1d6`. The parser can preserve the base formula, authored level range, and increment formula as strings/typed formula tokens. The tag alone does not establish which effect or branch it scales.
- `dice`: 10 of 126 occurrences have two arguments. The tag is generic and contexts include quantities and healing; do not classify all as damage/healing.
- `scaledice`: all 14 have three arguments; XPHB Healing Word uses `2d4|1-9|2d4` in higher-level prose. Preserve source context.
- `condition`: arguments vary 1–3. A tag can name a condition in a possible effect, an existing state, or a rules discussion. Existing root `conditionInflict` is more reliable for public classification.
- `variantrule`: 263 occurrences have three arguments and 457 have two. These are references to terminology such as Hit Points, Advantage, Sphere/area labels, and Concentration. They do not encode the associated rule values.

Context placement is also captured: `damage` has 82 occurrences in higher-level entries and 19 in tables; `scaledamage` has 148 in higher-level entries; `dice` has 6 in higher-level entries and 12 in tables; `scaledice` has 14 in higher-level entries; `condition` has 18 in tables; `variantrule` has 15 in higher-level entries and 4 in tables. The script's text-window heuristic flagged 505 `damage` occurrences and 71 `scaledamage` occurrences near conditional words. These are deliberately not treated as condition parsing or effect classification. Of 354 spells with higher-level entries, 179 have neither the dedicated `scalingLevelDice` field nor a `scaledamage`/`scaledice` tag. A single spell can contain many formulas and outcomes; 223 roots have multiple damage-tag occurrences, 86 have different damage argument strings. No nested `damage`, `scaledamage`, or `dice` tags were found; one nested tag of another kind (`filter`) was detected.

One nested tag was detected (`filter`); no malformed tag strings were found. The conditional-context flag is based on a local text-window heuristic and must not be read as a rules parse. Tags occur in nested entry trees and table rows; some tags occur in `entriesHigherLevel`.
