# Deity Identity Design

## Selected identity

Use:

`deity:<exact-source>:<normalized-pantheon>:<normalized-name>`

The primary source is preserved exactly. Pantheon and name slugs use Unicode normalization,
diacritic removal, lowercase ASCII, and hyphen separators. The identity components are all
source-authored. No array index or inferred source is used.

All 563 records have a nonblank pantheon and source. The complete corpus produces 563 unique IDs;
no pantheon/name slug collision exists. Pantheon is therefore sufficient to distinguish every
same-source, same-name group in this snapshot. It also preserves distinct source records that share
a name in different pantheons. Edition and reprint/source distinctions remain separated by exact
source code.

## Five PHB `(name, source)` collisions

| Name | First authored context | Second authored context | Resolution |
|---|---|---|---|
| Oghma | Celtic, p. 298; neutral good; Knowledge; “Unfurled scroll” | Forgotten Realms, p. 294; neutral; Knowledge; “Blank scroll”; `basicRules: true` | Pantheon is in each ID |
| Silvanus | Celtic, p. 298; neutral; Nature; “Summer oak tree” | Forgotten Realms, p. 294; neutral; Nature; “Oak leaf”; `basicRules: true` | Pantheon is in each ID |
| Surtur | Nonhuman, p. 296; lawful evil; Knowledge and War | Norse, p. 299; lawful evil; War | Pantheon is in each ID |
| Thrym | Nonhuman, p. 296; chaotic evil; War | Norse, p. 299; chaotic evil; War | Pantheon is in each ID |
| Tyr | Forgotten Realms, p. 294; lawful good; War and Order; `basicRules: true` | Norse, p. 299; lawful neutral; Knowledge, War, and Order | Pantheon is in each ID |

Titles and symbols also differ for some pairs, but identity does not depend on descriptive fields.
The records remain independent; no pantheon or deity equivalence is inferred.

## Copy lineage

Six VGM records declare `_copy` from an exact SCAG `(name, source, pantheon)` record and carry a
matching `customExtensionOf` UID. All six parents exist. VGM IDs remain VGM identities. Missing
structured alignment, domain, and symbol values inherit from the authored SCAG parent when the
catalog computes effective metadata; VGM-authored titles and entries take precedence and the
original `_copy` and `customExtensionOf` values remain preserved. The inherited fields and parent
ID are recorded separately from the child-authored values.

## Fallback policy

No fallback is needed for this source snapshot. A future record without a usable authored pantheon
must fail validation pending source investigation; the catalog will not invent a pantheon or fall
back to array position.
