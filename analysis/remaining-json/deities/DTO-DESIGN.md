# Deities DTO Design Checkpoint

## Discovery basis

The pinned `src/main/resources/data/deities.json` contains one `deity` collection with 563 authored
records. Every record has a `name`, `source`, `page`, and `pantheon`. There are 13 primary source
codes, all registered. The full recursive type/path inventory is in `source-audit.json`.

The corpus has five repeated `(source, name)` identities, all five in PHB. Each pair has distinct,
authored pantheon values. The candidate ID format `deity:<source>:<normalized-pantheon>:<normalized-name>`
is complete and collision-free across all 563 records. Pantheon is present and nonblank on every
record, including the six VGM records with `_copy` metadata. No fallback identity is required.

## Proposed internal model

- `DeitiesFileDte`: strict root containing `deity` and typed `_meta`.
- `DeityDte`: exact source fields, including open string codes, recursive typed entries, nested
  additional-source references, symbol-image metadata, and typed `_copy` identity.
- `DeityCopyResolver`: resolves the six authored copy directives against exact `(name, source,
  pantheon)` roots. It inherits missing structured alignment, domains, and symbol fields, keeps
  child values authoritative, and records the parent stable ID and inherited-field provenance.
  The source is complete and all six parents exist; this is source-defined inheritance rather than
  a prose-derived relationship. It does not merge entry prose.
- `DeityDefinition`: immutable normalized identity, source/page, pantheon, authored and effective
  structured fields, category/plane/province/title/aliases, source references, typed lineage,
  publication flags, and recursive entries retained internally.

Alignment remains an ordered list of authored string codes; domains remain an ordered list of
authored strings. Neither is a closed enum: the source uses an open set of values. `symbol` is a
plain string except for 12 ERLW values containing inline formatting markup. The `symbolImg` object
is typed, preserved internally, and never published. The 92 observed recursive paths include
`list`, `item`, `entries`, `section`, `image`, `insetReadaloud`, `table`, `inset`, and `quote`
nodes. Existing entry types are reused when their shapes match; `insetReadaloud` needs a narrow
typed entry variant. `internal` occurs as the value of `href.type` for image references, not as an
entry node.

## Public projection

`DeitySummaryDto` exposes stable ID, name, source/page, pantheon, category, alignment, and domains.
`DeityDetailDto` adds source-safe authored aliases and exact supported cross-catalog references.
Titles, symbols, worshiper descriptions, plane/province descriptions, prose entries, media paths,
copy diagnostics, publication flags, and import provenance remain internal under the current
licensing boundary. This prevents raw markup and unreviewed setting lore/art descriptions from
leaking through the API. Domain and alignment codes are exposed as authored structured metadata;
they do not grant features or impose eligibility.

## References and API behavior

Existing target adapters support 17 explicit Spell, 15 Item, and 1 Skill markup occurrence in
deity entries. `@creature`, `@reward`, and other unsupported targets remain typed source content
with no target ID. Book tags are publication citations rather than catalog definition links.

The list endpoint filters by source, pantheon, alignment, and domain. Name lookup is exact and
source-aware; if a name/source pair resolves to more than one deity it returns the established
ambiguity response. An optional pantheon parameter disambiguates the five known PHB name pairs.
Nested headings and deity relationships are not promoted to independent definitions.

No worship, eligibility, domain assignment, spell granting, piety, divine intervention, or campaign
religion behavior belongs in this catalog.
