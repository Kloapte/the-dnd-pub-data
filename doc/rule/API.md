# Rules API

The Rules API reads the normalized dataset at `normalized/core-rules/rules.json` into typed Rule DTOs. Rule mechanics are deserialized according to their `kind` and `subtype` discriminators, including their nested typed variants. The repository keeps the loaded dataset in memory for the lifetime of the application; the service performs exact identifier and name searches.

| Method | Path | Behavior |
| --- | --- | --- |
| `GET` | `/api/rules` | Returns all Rules in normalized dataset order. |
| `GET` | `/api/rules/{id}` | Returns the Rule with the exact identifier, or `404` if it does not exist. |
| `GET` | `/api/rules/name/{name}` | Returns all Rules with the exact name. An unmatched name returns an empty array; duplicate names can return multiple Rules. |

The response DTO follows the approved [Rule schema](../../src/main/resources/schemas/rule/schema.json). `id`, `name`, and `sourceMetadata` identify the Rule. Optional `ruleType`, `mechanics`, `references`, and `composition` fields are included when present. Mechanics preserve the schema's discriminators and typed nested structures.

The API does not interpret source text or execute mechanics. It serves the approved normalized Rule facts and deterministic searches over those records.
