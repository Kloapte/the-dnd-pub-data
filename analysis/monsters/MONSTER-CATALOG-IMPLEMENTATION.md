# Monster Catalog Implementation

## Initialization lifecycle

`MonsterCatalog` is a Spring singleton. During context initialization it discovers
`classpath*:data/monsters/bestiary-*.json` resources through the application context, sorts them by filename,
and streams each file's `monster` collection. Resource streams are used instead of filesystem paths so the same
loading path works from the development classpath and packaged JAR resources.

The catalog passes the source records through the existing `MonsterCopyResolver`, `MonsterVersionExpander`, and
`MonsterMapper` in that order. The pipeline runs once during startup. Any malformed source file, unresolved copy or
template, version expansion error, mapping error, duplicate canonical identity, or duplicate public ID fails context
initialization. Requests only read the fully initialized catalog.

## Components and indexes

- `MonsterResolutionConfiguration` supplies the Jackson mapper, template repository, copy resolver, version expander,
  and mapper as Spring beans. It reuses the existing resolution implementation.
- `MonsterCatalog` owns immutable indexes from stable monster ID and case-folded `(name, source)` to full
  `MonsterDto` records, plus an immutable summary list sorted by name, source, then ID.
- `MonstersService` filters, sorts, paginates, and looks up catalog records. It does not load source data or invoke
  resolution or normalization.
- `MonsterController` translates HTTP parameters into service requests and returns a stable `PagedResponse` instead
  of exposing Spring's `PageImpl` response shape.
- `MonsterApiExceptionHandler` returns `ProblemDetail` for invalid requests and missing monsters.

The public lookup requires both name and source. This keeps names shared by different books unambiguous. Canonical
identity comparisons and source filters ignore case; partial name filtering ignores case and surrounding whitespace.

## API

### `GET /api/monsters`

Returns a `PagedResponse<MonsterSummaryDto>` with `content`, `page`, `size`, `totalElements`, and `totalPages`.
Supported query parameters are:

- `page`: zero-based, defaults to `0`.
- `size`: defaults to `25`; accepted range is 1 through 100.
- `name`: optional case-insensitive partial match.
- `source`: optional case-insensitive exact book code.
- `sort`: optional repeated parameter in `property,asc|desc` form. Supported properties are `name` and `source`.
  Without a sort parameter the order is name ascending, then source ascending. An ID tie-breaker makes each ordering
  deterministic.

An out-of-range page returns an empty `content` list with the matching total count. Invalid page/size values and
unsupported sort properties return HTTP 400.

### `GET /api/monsters/{id}`

Returns the full `MonsterDto` for its `5etools:` stable ID. Unknown IDs return HTTP 404.

### `GET /api/monsters/by-name/{name}?source=MM`

Returns the full `MonsterDto` for a case-insensitive exact name and source match. The required `source` parameter
selects the intended book when names repeat. Missing source returns HTTP 400; an unknown pair returns HTTP 404.

The monster GET routes are public. Errors use Problem Details responses and do not include source file paths or stack
traces.

## Validation

The application context integration tests assert that startup produces exactly 4,851 records. They exercise unique
catalog indexing, combined name/source filters, pagination and page-size limits, sorting, ID and name/source lookup,
HTTP success and error responses, and concurrent reads. A separate resource test packages a bestiary resource in a
JAR and verifies Spring's classpath pattern resolver can discover and read it. The full Maven suite passed with 41
tests, no failures, and no errors after catalog/API integration. The executable Spring Boot JAR was also launched and
returned a page response with `totalElements` equal to 4,851.

The full Maven suite should be run with:

```text
mvnw -s target/maven-settings.xml test
```

The result and any existing unrelated failures are recorded in the change review; this document is kept independent
of machine-specific test output.

## Known limitations

- Catalog contents are rebuilt at each application startup and are not hot-reloaded.
- Search supports name substring and exact source filtering only; broader text search and category filters are not
  included.
- The service returns complete normalized monster definitions for detail requests and compact projections for list
  requests. No encounter runtime state, persistence, or REST write operations are included.
