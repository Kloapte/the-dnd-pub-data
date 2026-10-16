# Race Inheritance and Version Implementation

The source corpus has 160 race roots and 98 subrace records. It declares 16 race copies and one subrace copy; all 17 declarations resolve to source-qualified parents. Five nameless PHB subrace records remain import-only. Of the named subraces, 93 are normalized root definitions.

| Collection | Source roots | Copy declarations | Version declarations | Template implementation rows |
| --- | ---: | ---: | ---: | ---: |
| race | 160 | 16 | 41 | 25 |
| subrace | 98 | 1 | 14 | 30 |

There are 37 direct race versions and 11 direct subrace versions. Four race template declarations produce 25 implementations; three subrace templates produce 30. The normalizer emits 103 version definitions, for 356 definitions overall: 160 race roots, 93 named subrace roots, and 103 version outputs. The five unnamed auxiliary records do not receive definitions or fabricated IDs.

The normalizer handles the observed entry operations `appendArr`, `replaceArr`, `removeArr`, `prependArr`, and `replaceTxt`, preserving declaration order and rejecting unknown operations or malformed payloads. Version template variables are substituted in names, structured mechanics, and modification operands. Remaining placeholders cause explicit failure. Abstract templates themselves are not emitted as selectable definitions.

Subrace `overwrite` metadata is retained independently. A child does not inherit all parent mechanics merely because it has a parent reference. For version operations that explicitly target a parent entry absent from the local child, the referenced parent's entries provide the target structure. The nameless Dragonborn records are likewise used only as typed implementation templates with their explicit parent relation. No general parent/child character-builder merge is performed.

Expansion validates source registry coverage, parent references, copy cycles, duplicate IDs, and duplicate concrete identities. Two duplicate `(name, source)` subrace pairs occur: `Ixalan|PSX` and `Variant; Mark of Finding|ERLW`; subrace IDs include parent source and name to keep them distinct. Race identities remain `race:<source>:<name>` style IDs and edition sources remain independent.

The focused `RaceDteTests` class covers corpus and declaration counts, all observed operation names, representative transformations, template expansion, placeholder substitution, collision handling, source separation, and immutability. It currently passes all four tests. Full-suite results are recorded in `DTO-IMPLEMENTATION.md` after regression validation.
