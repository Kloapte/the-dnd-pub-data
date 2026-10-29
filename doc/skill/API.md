# Skills API

The backend loads 36 canonical Skill records from `normalized/core-rules/skills.json` into immutable, schema-shaped
DTOs. A Skill contains its canonical identity, source metadata, associated ability, and optional references to checks
that use the Skill. Ability and reference target values use schema-defined enums; optional reference fields are omitted
when absent.

## Read endpoints

| Method and path | Result |
| --- | --- |
| `GET /api/skills` | All Skills in normalized dataset order. |
| `GET /api/skills/{id}` | The Skill with the exact canonical ID; returns 404 when absent. |
| `GET /api/skills/name/{name}` | All exact name matches. |

## Data flow and coverage

`SkillRepository` deserializes the canonical JSON with Jackson and rejects an empty dataset or duplicate IDs.
`SkillService` supplies all, exact-ID, and exact-name queries. The controller exposes only read operations.

Integration tests round-trip all 36 canonical records, cover the optional check-reference field, reject invalid ability
values and empty reference lists, and exercise all three endpoints.
