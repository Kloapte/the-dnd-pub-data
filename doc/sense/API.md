# Senses API

The backend loads the eight canonical Sense records from
`normalized/core-rules/senses.json` into immutable, schema-shaped DTOs. Each Sense retains its canonical identity,
source metadata, typed capabilities, and optional references. Capability variants are `SIGHT`,
`NONVISUAL_PERCEPTION`, and `CONDITION_INTERACTION`; nonvisual location precision distinguishes `VIBRATION_ORIGIN`
from `DETECTED_TARGETS`. Numeric ranges remain with creature records rather than the Sense definition.

## Read endpoints

| Method and path | Result |
| --- | --- |
| `GET /api/senses` | All Senses in normalized dataset order. |
| `GET /api/senses/{id}` | The Sense with the exact canonical ID; returns 404 when absent. |
| `GET /api/senses/name/{name}` | All exact name matches. |

## Data flow and coverage

`SenseRepository` deserializes the canonical JSON with Jackson and rejects an empty dataset or duplicate IDs.
`SenseService` supplies all, exact-ID, and exact-name queries. The controller exposes only read operations.

Integration tests round-trip all eight canonical records, cover every capability and location-precision variant, and
exercise valid and invalid union payloads and the three endpoints.
