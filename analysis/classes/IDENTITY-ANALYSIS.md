# Class, subclass, and feature identity

Status: identity contract implemented and tested in Stage B by `ClassIdentities` and `ClassReferenceResolver`. Counts below describe authored roots. No Stage C catalog has been implemented.

## Source identity tuples

Lookup compares case-insensitively using exact tokens, without fuzzy matching. Retain original names and source spellings alongside lookup keys. Neither source aliases nor reprint relationships merge definitions.

| Kind | Full identity components |
| --- | --- |
| Class | name, source |
| Subclass | name, className, classSource, source |
| Class feature | name, className, classSource, level, source |
| Subclass feature | name, className, classSource, subclassShortName, subclassSource, level, source |

All required identity fields are present on every corresponding root. All 330 subclasses also declare `shortName`. Within a full parent/source context, short names provide a unique alternate lookup for feature-parent and subclass reprint UIDs. They are not global subclass identities. No root `alias` fields or additional alternate-name collections occur.

## Recommended readable IDs

Use lower-case source/name slugs, Unicode decomposition and diacritic removal, nonalphanumeric runs replaced by `-`, and trimmed separators. Preserve the original source code in shared `SourceIdentity`/`SourceReference` values. Validate both tuple and slug uniqueness, failing clearly on future collisions; never append encounter-order numbers or encode Base64.

| Kind | Format | Example |
| --- | --- | --- |
| Class | `class:{source}:{name}` | `class:phb:fighter` |
| Subclass | `subclass:{source}:{parentSource}:{parentName}:{name}` | `subclass:phb:phb:fighter:champion` |
| Class feature | `class-feature:{source}:{classSource}:{className}:{level}:{name}` | `class-feature:phb:phb:fighter:1:fighting-style` |
| Subclass feature | `subclass-feature:{source}:{classSource}:{className}:{subclassSource}:{subclassShortName}:{level}:{name}` | `subclass-feature:phb:phb:fighter:phb:champion:3:improved-critical` |

Repeated source segments are intentional: feature source, subclass source, and parent class source can differ. `subclass:tce:tce:artificer:alchemist` and `subclass:tce:efa:artificer:alchemist` must remain different IDs. PHB/XPHB Fighters become `class:phb:fighter` and `class:xphb:fighter`.

## Actual collision results

The complete scan finds zero duplicate full identities and zero proposed ID collisions over 2,536 roots. It also finds zero duplicate subclass short-name identities when parent class name/source and subclass source are included.

Using only `(name, source)` instead produces **296 collision groups**: 124 subclass groups, 55 class feature groups, and 117 subclass feature groups. These are groups, not a count of colliding record pairs. Class identities have no collisions. Complete groups and locators are retained in [source-evidence.json](source-evidence.json).

The 124 subclass groups correspond to parent-context copies: four TCE Artificer subclasses also belong to EFA Artificer, and 120 PHB-parent subclasses also belong to XPHB parents. Feature collisions additionally include repeated level-specific Ability Score Improvements, similarly named features across classes/subclasses, and adapted features with different parent sources. Example: PHB and XPHB-parent copies of Knowledge Domain share a feature source of PHB.

## UID grammars and defaults

Preserve empty pipe-delimited positions. These interpretations resolve every structured feature reference in the corpus:

```text
classFeature:
  name | className | classSource | level | featureSource
  classSource defaults to PHB
  featureSource defaults to classSource

subclassFeature:
  name | className | classSource | subclassShortName | subclassSource | level | featureSource
  classSource defaults to PHB
  subclassSource defaults to PHB independently of classSource
  featureSource defaults to subclassSource

subclass reprintedAs and {@subclass ...}:
  shortName | className | classSource | subclassSource

class reprintedAs:
  name | source
```

This is corpus-validated interpretation, not a license to add defaults to other UID domains. In particular, `Knowledge Domain|Cleric|XPHB|Knowledge||3` identifies a PHB subclass/source feature attached to XPHB Cleric. Defaulting that empty subclass source to XPHB creates false unresolved references. Class markup also accepts display text and subclass routing slots; it is not a subclass UID.

## Reprints and source handling

Thirteen class reprint UIDs and 77 subclass reprint UIDs all resolve to unique authored definitions. Features have no root `reprintedAs`; one embedded generated-table reprint is presentation metadata. Reprints are relationships, not copy instructions or deduplication rules. Copy-derived roots preserve their own identity even when entries or progression reference definitions in the original parent context.
