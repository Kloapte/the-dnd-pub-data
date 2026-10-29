# Step 1 — Source Registry: Phase 6 Backend Integration

**Status:** Implementation complete and awaiting review. Phase 6 is not approved. No later roadmap step has begun.

## Existing backend conventions inspected

The application uses Java 21, Spring Boot 4.0.5, Jackson 3 through Spring Boot's web MVC starter, Springdoc OpenAPI annotations, and JUnit 5 with Spring Boot tests. The checked-in Java application previously contained only the application entry point and a security configuration; there were no Source domain records, Source services, controllers, centralized exception handlers, or shared HTTP error-body convention to extend. Existing routes follow `/api/...`, and API paths are permitted by the current security filter chain. The integration uses Spring's standard `ResponseStatusException` handling for 404 responses.

The current POM explicitly uses `spring-boot-starter-webmvc`; no new runtime dependency was added.

## Typed Source model

The DTOs in `com.thedndpub.data.dto.source` mirror the approved schema:

- `Source`: canonical code, source-code aliases, and publication references.
- `PublicationReference`: kind, distinct publication ID, name, published/revised dates, group, title aliases, and raw parent token.
- `PublicationKind` and `PublicationGroup`: closed enums mapped to the exact JSON strings, including hyphenated group values.

Records defensively copy lists, omit absent/empty optional fields during JSON serialization, and reject invalid required tokens, empty explicit arrays, and duplicate aliases. Jackson rejects unknown enum values; startup indexing checks duplicate/case-colliding codes, code/alias collisions, alias collisions, and duplicate publication identity keys. No workflow fields or parent-resolution behavior were added.

## Startup loading and service behavior

`SourceRegistry` loads `classpath:normalized/sources/sources.json` during bean construction. It deserializes the entire dataset before exposing the service, retains dataset order for list results, and builds immutable exact-code and explicit-alias indexes. Missing or malformed data and identity violations stop application startup with an `IllegalStateException` identifying the dataset and cause.

The service provides list, canonical-code lookup, alias lookup, and combined code-or-alias lookup. Alias resolution returns the same Source record with its canonical code. Lookup is exact and does not infer or lowercase aliases.

## REST API

| Endpoint | Behavior |
| --- | --- |
| `GET /api/sources` | Returns all Source records in canonical dataset order. |
| `GET /api/sources/{code}` | Looks up an exact canonical code or declared code alias; returns canonical identity. |

Unknown codes and aliases return HTTP 404. Both operations are read-only and have Springdoc `@Operation`, response, and tag annotations. No database, pagination, or mutation endpoints were introduced.

## Verification

`SourceRegistryIntegrationTests` contains six JUnit 5 tests covering:

- Loading all 181 Sources and all 167 publication references.
- Optional values and enum/date deserialization for `PHB`, `EEPC`, `MOT`, `PSZ`, and `AAG`.
- Canonical code and declared alias lookup, including canonical identity after alias access.
- Duplicate Source code, code/alias collision, and duplicate alias rejection.
- Missing and malformed dataset failures at service initialization.
- REST list and detail response structure, omission of empty optional arrays, raw parent token output, and HTTP 404.

The Phase 6 test class passed: **6 tests, 0 failures, 0 errors**. Main and test sources compiled, and the Spring Boot test context started with the runtime dataset. Verification was run in an isolated copy under `target/phase6-backend-check` because the existing `target/test-classes` contains stale classes that shadow current source types.

The full checked-in test source set cannot compile in a clean isolated build: existing `DndPubDataApplicationTests` imports `com.thedndpub.data.dto.monster.MonsterSummaryDto`, `MonsterCatalog`, `MonsterNotFoundException`, and `MonstersService`, none of which exist under `src/main/java` in the repository. That unrelated test file was left unchanged. A package build of the isolated project completed and produced `dnd-pub-data-0.0.1-SNAPSHOT.jar`; the unrelated legacy test compilation issue remains the reason the complete checked-in test suite cannot run cleanly.

## Documentation and data boundary

The runtime dataset remains `src/main/resources/normalized/sources/sources.json`. The original `src/main/resources/data/` files, canonical dataset, and approved schema were not modified. Runtime data classes contain only canonical domain fields; provenance remains in its separate manifest.

The [Source Registry documentation](../../../doc/SOURCE_REGISTRY.md) describes startup loading, lookup, endpoints, future approved dataset updates, and remaining reference limitations. The [documentation index](../../../doc/INDEX.md) links to it. The dataset retains 18 unresolved and 2 ambiguous parent references; the service returns raw tokens and does not attempt to resolve them.

## Review status

Phase 6 implementation is complete and awaiting review. The roadmap records this status without marking the phase approved. Approval is required before any subsequent roadmap domain begins.
