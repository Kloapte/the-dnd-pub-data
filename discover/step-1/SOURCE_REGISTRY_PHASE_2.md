# Step 1 — Source Registry: Phase 2 Contract Proposal

**Status:** Phase 2 reviewed and approved; the model and schema are closed as the accepted Source contract. Phase 3 representative conversion was reviewed and approved. See the [Phase 3 conversion report](../../reports/step-1/SOURCE_REGISTRY_REPRESENTATIVE_CONVERSION.md). Phase 4 validation was approved, and the full dataset has since been generated under Phase 5; see the [Phase 5 expansion report](../../reports/step-1/SOURCE_REGISTRY_PHASE_5_EXPANSION.md).

## 1. Approved direction applied

This proposal uses one primary `Source` model. Its required identity is the case-preserved registry code. Optional `publications` values preserve book/adventure metadata without creating separate Book and Adventure domain models. Each contained value records its classification (`kind`) and its own optional publication ID, name, dates, grouping, title aliases, and raw parent token.

### Why the contained publication list is needed

The evidence has a concrete one-to-many identity relationship. Source code `MOT` occurs on a book record whose ID is `MOT` and an adventure record whose ID is `MOT-NSS`; `FRAiF` similarly occurs on a book and on adventure `FRAiF-TLLoL`. A flat `Source` with a single `kind`, `publicationId`, name, or date would overwrite or discard one of those distinct records.

The smallest adjustment is an optional list of embedded publication metadata references on the one Source model. The entries are values owned by the Source entity, not independent publication domain entities or subclasses. A `kind` value per entry preserves the observed book/adventure distinction. The Phase 2 proposal asks approval for this bounded composition.

## 2. Canonical model and rationale

The proposed Source shape is:

```text
Source
  code: string                         required; stable primary identity
  codeAliases?: string[]               source-code aliases only
  publications?: PublicationReference[]
    kind: "book" | "adventure"        required for each supplied record
    publicationId?: string             preserve distinct source `id`
    name?: string
    published?: date
    revised?: date
    group?: observed source grouping
    titleAliases?: string[]            human-readable aliases only
    parentSource?: string              raw supplied token; target not implied
```

The exact Draft 2020-12 contract is [source.schema.json](../../schemas/source/v1/source.schema.json). It requires only `code` at the Source level. Code-only identities such as `EEPC` need no fabricated name or publication record. Optional empty arrays are omitted. Unknown properties are rejected.

### Alternatives considered

| Alternative | Benefit | Cost or data loss | Decision |
| --- | --- | --- | --- |
| Flat Source with one set of publication fields | Smallest object. | Cannot preserve both the book and adventure metadata carried by `MOT` or `FRAiF`; would need overwriting, duplicate Source codes, or silent loss. | Rejected. |
| Abstract Source with Book and Adventure subclasses | Types make the two classifications explicit. | Adds inheritance and separate domain submodels without distinct Source identity behavior; does not alone solve one source code with records from both collections. | Rejected as unnecessary. |
| Source with embedded `publications` metadata values | One primary Source identity; preserves each record, kind, ID, and metadata; supports zero-publication code-only sources. | Requires a nested array and an integrity check for publication-key collisions. | Recommended smallest faithful adjustment. |
| Separate registry and independent Book/Adventure entities | Preserves input file shape. | Violates the approved one-model direction and duplicates source references. | Rejected. |

The embedded `PublicationReference` name in the schema is a reusable structural definition only. It is not a new standalone model or dataset.

## 3. Identity, aliases, and reference behavior

- `code` is the Source primary identifier and must preserve its supplied spelling and case. No synthetic identifier is added.
- `codeAliases` maps only aliases from the source registry. For example, `PSZ` has code alias `PS-Z`.
- `publicationId` maps the metadata record's `id` where it differs from `source` or where needed to distinguish records. `PS-Z` is both the supplied alias of code `PSZ` and the distinct publication ID for the Plane Shift book; the fields retain these separate roles.
- `titleAliases` maps human-readable publication title aliases. The Planescape title alias shared by two books and one adventure is not an identity key.
- `kind` preserves whether a record came from book or adventure metadata. `group` preserves the observed source grouping value without inferring rules meaning or edition identity.
- `parentSource` preserves the raw metadata token. It is not a canonical reference object and does not encode review/resolution status. A separate integrity pass can resolve a token only when the evidence identifies its role and target.

### Unresolved parent identity

The tokens `SAiS` (3 records), `PAitM` (3), `AitFR` (5), and `NRH` (7) account for 18 parent values outside the registry. The source records do not establish whether these tokens are omitted registry codes, publication IDs, or source-specific shorthand. They remain strings in `parentSource`; no Source target is created.

Even registered parent tokens can have target-granularity questions. Adventure `MOT-NSS` has `parentSource: "MOT"`; `MOT` is also the current Source code and the book publication ID. The raw value can be preserved, but the schema does not assert a parent publication relationship. Resolve the source context and publication target only with explicit evidence.

## 4. Field mapping and exclusions

| Input | Canonical destination | Handling |
| --- | --- | --- |
| Registry `code` | `Source.code` | Required, unchanged. |
| Registry `aliases` | `Source.codeAliases` | Copy non-empty aliases; omit an empty list. |
| Book/adventure record collection | `publications[].kind` | `book` or `adventure`. |
| Book/adventure `source` | owning `Source.code` | Join to registry identity; not copied as a second ID. |
| Book/adventure `id` | `publications[].publicationId` | Preserve when different from code or needed for disambiguation. Do not treat as a code alias. |
| Book/adventure `name` | `publications[].name` | Preserve supplied value. |
| Book/adventure `published` | `publications[].published` | Preserve ISO date. |
| Book `revised` | `publications[].revised` | Preserve when supplied; observed twice. |
| Book/adventure `group` | `publications[].group` | Preserve known source value; enum is the ten values in the schema. |
| Book/adventure `alias` | `publications[].titleAliases` | Preserve non-empty values; distinct from code aliases. |
| Book/adventure `parentSource` | `publications[].parentSource` | Copy raw token; do not invent a target. |

The schema excludes `contents`, adventure `storyline`, adventure `level`, authors, cover credits, and `publishedOrder`. The first four publication/content fields named by the user remain outside Source identity; `publishedOrder` is not needed to identify a source. No story or rules content was modeled.

## 5. Provenance and versioning

Provenance remains outside the Source JSON so the same typed Source object can be used by the application without workflow metadata. The normalization manifest should link each Source code to its registry entry and each embedded publication reference to the corresponding book/adventure record. It should record original file and JSON location, source revision/version when available, content hash, canonical Source code, publication ID where applicable, and the schema ID/version. The exact record pointers must be captured during conversion; no unavailable pointer or hash is fabricated in this proposal.

The schema path and identifier are versioned (`schemas/source/v1/source.schema.json`, `urn:thedndpub:schemas:source:v1`). A future incompatible change requires a new reviewed schema version and migration review. Deterministic source comparisons should preserve previously approved identities and flag changes to code, aliases, IDs, dates, names, grouping, and parent tokens for review.

## 6. Representative examples

The five canonical JSON examples are stored beside the schema:

| Example | Coverage |
| --- | --- |
| [source-phb.json](../../schemas/source/v1/examples/source-phb.json) | Supplied book name, publication date, and group. |
| [source-code-only-eepc.json](../../schemas/source/v1/examples/source-code-only-eepc.json) | Source identity without publication metadata. |
| [source-mot.json](../../schemas/source/v1/examples/source-mot.json) | Two metadata records under one Source code and distinct adventure ID `MOT-NSS`. |
| [source-psz-aliases.json](../../schemas/source/v1/examples/source-psz-aliases.json) | Separate source-code alias and publication ID roles. |
| [source-aag-unresolved-parent.json](../../schemas/source/v1/examples/source-aag-unresolved-parent.json) | Human title alias and unresolved `SAiS` parent token. |

## 7. Validation

Validation used Ajv 8.17.1's Draft 2020-12 implementation and `ajv-formats` 3.0.1. Detailed outcomes and limits are in the [schema validation summary](../../reports/step-1/SOURCE_REGISTRY_SCHEMA_VALIDATION.md). Results:

- The schema passed Draft 2020-12 meta-schema validation and strict schema compilation.
- All five representative examples passed schema validation, including the two-publication `MOT` Source and code-only Source.
- Five negative checks were rejected as expected: unknown workflow property, invalid calendar date, unsupported kind, empty optional array, and missing required code.
- JSON files were parsed as valid JSON during the validator run.

These checks establish structural validity. They do not resolve parent references, prove titles or dates against source content, or validate registry-wide uniqueness. Conversion will still require separate integrity checks for one Source per code, publication identity-key uniqueness within each Source, alias collisions, registry membership, and provenance completeness. The distinct-ID convention (include `publicationId` when it differs from `code`) also needs a cross-field integrity check because standard JSON Schema cannot compare these two dynamic strings.

## 8. Review decisions and limits

The contract is ready for review with these decisions visible:

1. Approve the one-Source model with an embedded list of book/adventure metadata values to preserve the observed `MOT` and `FRAiF` one-to-many cases.
2. Confirm `group` should be retained as a closed ten-value enum; new upstream values would require a versioned contract review.
3. Confirm parent tokens remain raw `parentSource` values until source-code versus publication-ID target roles are established. This includes the 18 unregistered tokens and `MOT-NSS`'s `MOT` token.
4. Keep the isolated `WGE` alternate-art value unresolved for the later monster-domain analysis; it is not added as a Source identity by this contract.

No source data, normalized dataset, Java code, or roadmap content was changed during Phase 2. The [Source Registry documentation](../../doc/SOURCE_REGISTRY.md) records the accepted contract and actionable incomplete-reference cases; [terminology](../../doc/TERMINOLOGY.md) and the [documentation index](../../doc/INDEX.md) make the normalization vocabulary and documents discoverable.
