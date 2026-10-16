# Source Registry design

## Purpose

The shared registry gives language, monster, and future entity pipelines one stable lookup for source codes. A source identity is independent of publication availability. Codes retain the spelling found in source data, while indexes compare them case-insensitively.

## Model boundaries

1. **Source identity** — `SourceIdentity(code)` identifies the source code. It is not a publication ID and does not require a title.
2. **Source metadata** — `SourceMetadata(kind, title)` is optional descriptive classification. It remains absent when evidence does not establish it.
3. **Publication metadata** — `PublicationDefinition` remains owned by the existing Book Catalog and is an optional association on `SourceRecord`. The registry never creates a second publication record.
4. **Entity source reference** — `SourceReference(identity, page)` combines source identity with an optional occurrence locator. A reference does not imply a publication record.

`SourceRecord(identity, metadata, publication)` is the registry lookup result. Missing `metadata` is represented as null internally. Missing publication data is represented by a null `publication` plus `PublicationMetadataStatus.UNAVAILABLE`; available records carry `AVAILABLE`. Public entity projections continue to serialize their established source-code fields and do not embed full publication metadata.

## Registry keys and aliases

The registry resource contains 175 case-insensitively unique source identities and the six explicit book-ID aliases from the Book Catalog. Aliases resolve to the corresponding source identity and existing publication. Aliases never create source identities or publications. An alias that collides with another identity or maps to multiple identities fails catalog initialization.

The Book Catalog remains unchanged as the authority for publication records. Its source-code index is case-insensitive. Source identity is preserved independently, including the source spelling on entity references and existing entity IDs.

## Entity integration

- Language normalized records use `SourceIdentity`; API DTOs project its original `code` string, preserving the existing response shape and IDs.
- Language secondary/additional source provenance uses shared `SourceReference` values and retains pages.
- Monster primary `Source` and secondary/reference locators use shared source identity/reference values. The serialized source code remains a string, and page, secondary references, reprint strings, and availability remain where they were.
- Name/source lookups and source filters continue to compare codes case-insensitively. Source registry lookup does not merge entity definitions across sources.

Future entity types can use `SourceIdentity` and `SourceReference` without adding entity-specific source models. A new source does not require a database or a publication record.

## Evidence policy and public JSON

Only explicit `books.json` relationships populate publication metadata. Source codes absent from the Book Catalog remain valid registry entries with no publication association. The current imported data does not support assigning an individual source kind or title to those codes; no such values are inferred.

Public DTOs keep `@JsonInclude(NON_EMPTY)`. Source references serialize only identity and meaningful page information. Internal source metadata and publication records are not automatically exposed through language or monster endpoints. Existing response shapes are preserved.


