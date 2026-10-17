# Feature and Proficiency Reference Analysis

## Existing exact graph

The class pipeline already resolves its internal graph independently: 645 class progression feature refs, 1,115 subclass progression feature refs, 32 `refClassFeature` links, and 547 `refSubclassFeature` links total 2,339 authored edges; copy inheritance yields 2,725 effective occurrences. There are 677 class features and 1,499 subclass features, with zero unresolved/ambiguous refs, cycles, or orphan definitions. Parent class and subclass identities are contextual and already exact. A shared resolver should not replace or rescan this graph; an adapter may expose these verified edges to a common diagnostic/report format.

The same class analysis resolves 162 Optional Feature references, two explicit XPHB Feat references, and one creature reference. Class skills (256) are unqualified choices, not exact skill identities. There are no structured class→Background or class→Race definition links. Markup links to feats/actions/conditions/senses/languages are not equivalent to the structured reference counts.

## Other grants and prerequisites

Backgrounds contain qualified Feat grants on 84 roots (61 distinct UIDs), plus skill/tool/language grants and choices. Optional Feature/Feat category codes describe selection filters rather than exact definitions. Race/background/class proficiency names often omit source or represent a choice. A catalog's existence and a unique current name match do not establish authored identity.

Weapon/armor/tool categories may belong to vocabularies rather than a definition catalog. Conditions can be exact only where a typed source reference or known condition identity is supplied. Do not extract them from prose or raw markup into mechanical grants.

The shared component should accept typed `TargetKind` adapters and preserve category distinctions, while leaving the class feature graph's proven domain resolver intact.
