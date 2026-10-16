# Shared publication and source-reference design

## Analysis gate and recommendation

The registry's `source` codes are unique and match some existing entity
references; they are the appropriate stable public catalog IDs for records
that exist. The six cases where
`book.id` differs from `book.source` are aliases, not grounds for generated
IDs. However, `books.json` is not an authoritative registry for current
Languages and Monsters source codes: it lacks 7/24 primary and 2/6 secondary
Language codes, and 66/110 primary and 21/26 secondary Monster codes. No other
source registry is present in the repository. The catalog can safely expose
the 66 records it contains, but cannot claim completeness or silently resolve
an unknown code.

Per the request's explicit stop condition, Languages and Monsters migrations
are deferred until missing source metadata is supplied or a source-registry
policy is approved. Existing entity IDs and APIs remain untouched. The
independent catalog/API is implemented for the known publication records, with
the limitations documented here.

## Catalog types

Use `Publication` / `PublicationDto` terminology for the actual book catalog
record. `books.json` contains core rulebooks, adventures/settings, supplements,
organized play, screens, recipes, card references, and homecraft publications;
calling every record a book or a source code alone would lose that semantic
distinction. Keep the requested URL namespace `/api/books` for compatibility.

- `BookDte`: one source record with `name`, `id`, `source`, `group`, cover
  metadata, dates, author, contents, optional aliases, and optional
  `parentSource`.
- Internal `PublicationDefinition`: stable `id` equal to `source`, display
  name, dates, author, group, aliases, and the raw `parentSource` code.
  Preserve raw `id` separately as a source alias when it differs. Parent
  resolution must remain explicitly unresolved if it is not in the catalog.
- `PublicationSummaryDto`: ID, title, publication date, and group when the
  group meaning is explained as an editorial grouping.
- `PublicationDto`: summary fields plus author, revised date, aliases, and
  parent source code. Do not expose TOC data or cover internals by default.
- `PublicationImportProvenance`: original alternate `id`, TOC structure,
  internal cover paths, cover credits, and import diagnostics.

The current corpus has six mismatched `id`/`source` values. `source` remains
the canonical identity because existing language/monster occurrences cite
`PSZ`, `PSI`, `PSK`, `PSA`, `PSX`, and `PSD`. Preserve the respective `id`
values (`PS-Z`, etc.) as aliases. Both columns are individually unique today;
catalog startup should reject canonical source collisions and alias conflicts.

`group` is repository curation, not a consistently defined source publication
kind. Do not call it `type` or use it as an authoritative publication-type
filter. The schema provides publication and revision dates but no general
edition field. Do not infer rules edition from code prefixes. Explicit title
years may be retained in titles; an edition enum requires an explicit source
field or approved mapping.

## Shared source reference

Use a small `PublicationReference` value object with `sourceCode` and optional
`page`. It represents the publication occurrence of an entity, so page stays
on the reference and not on the publication. A source reference stores the
original code even if unresolved. Resolution is an in-memory catalog concern,
never an HTTP call.

When an API needs the result, expose `PublicationReferenceDto` with
`bookId` (the canonical source code when resolved, otherwise the unchanged
unresolved source code), optional `page`, and a meaningful `resolved` boolean.
This keeps missing references visible without embedding a full
`PublicationDto`. The source code stays available for filtering. Avoid
general-purpose reference inheritance or a graph framework.

For Languages, primary source remains an occurrence reference (current page
is internal provenance); `additionalSources` and `otherSources` remain
distinct reference lists. Existing language identity remains
`language:<source>:<name-slug>`; a book relation must not change the ID or
merge edition-specific definitions.

For Monsters, primary source is the source occurrence with its page. Existing
`otherSources` and `referenceSources` remain distinct secondary/reference
lists; reprint targets retain their source code. Monster IDs remain as
currently generated from source and name. Existing source filters continue
to use the unchanged source code.

The same reference value can support both entities, but implementation should
only follow when the catalog can report missing references reliably. Current
counts show extensive unresolved source usage, particularly adventures and
short releases.

## API projection and content policy

Requested endpoints are `/api/books`, `/api/books/{id}`, and
`/api/books/by-name/{name}`. List filtering can support case-insensitive title
substring and exact source-code lookup. Title lookup may return multiple
summaries because aliases and parent products can share titles; return a list
and never choose arbitrarily. The fixed 66-record catalog does not require
pagination. There is no valid publication-type filter until `group` semantics
are formalized.

The summary/detail DTO records use `NON_EMPTY`. Do not expose `contents`
headers, cover paths, cover credits, or raw TOC titles until publication rights
are reviewed. Author, dates, title, and editorial group still require the
project's publication decision. Empty optional nested cover DTOs should not
be constructed.

## Unresolved decisions

1. Supply metadata or an explicit allowlisted source registry for the missing
   7 Language-primary, 2 Language-secondary, 66 Monster-primary, 21
   Monster-secondary, and 1 Monster-reprint codes.
2. Decide whether source codes outside `books.json` are legitimate publications
   (adventures, organized play, older releases, or other products) or invalid.
3. Decide whether a separately curated rules-edition mapping is in scope.
4. Approve which publication metadata can be returned publicly.
