# Statuses API

The backend loads five canonical Status records from `normalized/core-rules/statuses.json` into immutable,
schema-shaped DTOs. Status mechanics are represented by typed hit-point thresholds, turn restrictions, roll modifiers,
maintenance saves, and ending conditions. Maintenance-save DCs retain their formula and conditional damage fraction;
edition-specific mechanics remain separate Status records.

## Read endpoints

| Method and path | Result |
| --- | --- |
| `GET /api/statuses` | All Statuses in normalized dataset order. |
| `GET /api/statuses/{id}` | The Status with the exact canonical ID; returns 404 when absent. |
| `GET /api/statuses/name/{name}` | All exact name matches. |

## Data flow and coverage

`StatusRepository` deserializes the canonical JSON with Jackson and rejects an empty dataset or duplicate IDs.
`StatusService` supplies all, exact-ID, and exact-name queries. The controller exposes only read operations.

Integration tests round-trip all five canonical records, cover each mechanic variant and both maintenance-save DC
formulas, reject invalid and conditional payloads, and exercise all three endpoints.
