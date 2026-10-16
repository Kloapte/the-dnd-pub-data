# Language DTO design

## Identity and source model

Each definition is identified by a deterministic, source-aware ID:
`language:<lowercase-source>:<lowercase-name-slug>`. The source-qualified key
distinguishes reprints and editions. The currently complete corpus has no
duplicate `(name, source)` pair. Catalog construction rejects collisions rather
than appending unstable numeric suffixes. Names remain exact source strings;
lookup is case-insensitive on both name and source.

## Layers

- `LanguageDte` and `LanguageScriptDte` model the source records. The root
  `LanguagesFileDte` models both root collections. Source prose uses the
  existing recursive `EntryDte` sealed hierarchy; observed language entries
  are strings and inset objects.
- `NormalizedLanguageDefinition` is the internal application definition. It
  contains only identity and public classification fields. Collections are
  defensively copied.
- `LanguageDto` and `LanguageSummaryDto` are public projections. They include
  ID, name, source, observed type, script, and dialect labels. They currently
  share the same safe fields; the separate types leave room for detail-only
  additions after publication review. `origin` is excluded pending approval.
- `LanguageImportProvenance` retains page, prose, source-locator fields,
  availability flags, and asset paths separately from normalized definitions.
- `LanguageCatalog` loads `/data/languages.json` once and indexes by ID and
  normalized name/source. It rejects unknown source properties. Script records
  are loaded and validated but remain internal because their only additional
  field is font asset paths.

## Classification and relationships

`type` is preserved as a nullable source string. Observed values are
`standard`, `exotic`, `rare`, and `secret`; their meanings differ across source
editions, so no inferred family or normalized rarity enum is introduced. The
source contains no language-family field. `script` is a source-provided label,
not proof of a reusable mechanical definition. `dialects` are retained as
labels rather than resolved language references. `reprintedAs` is retained as
the original `Name|Source` string and is not treated as a resolved definition
reference. Future entity records can reference the stable `(name, source)` ID.

## Public content and publication

The API omits page locators, source import flags, reprint/source links, speaker
tags, prose entries, image flags, fonts, and import provenance. In particular,
font paths are internal assets and prose may be copyrighted. `origin` is also
kept internal until separately approved. Mechanical effects are not present in
the dataset; language proficiency mechanics are not invented. No generic
mechanical effect framework is added.

## REST representation

- `GET /api/languages` returns all summary records in stable name/source/ID
  order. At 201 records this small fixed catalog does not need pagination;
  optional case-insensitive `name` substring and `source` exact filters apply.
- `GET /api/languages/{id}` returns the detail projection.
- `GET /api/languages/by-name/{name}?source=...` resolves exact name/source.

Missing definitions return 404; blank/invalid query inputs return 400 via the
existing API exception conventions. Public records use Jackson
`NON_EMPTY`, preserving false and zero values should future public fields need
them.
