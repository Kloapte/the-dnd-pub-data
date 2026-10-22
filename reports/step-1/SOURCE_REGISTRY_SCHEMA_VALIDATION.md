# Source Registry schema validation

**Result:** Passed. This report records focused Phase 2 validation; it is not a full dataset conversion or reference-integrity certification.

## Validator

- JSON Schema implementation: Ajv 8.17.1, Draft 2020-12 mode.
- Format validation: `ajv-formats` 3.0.1.
- Schema options: strict compilation, all errors collected, schema meta-validation enabled.
- Schema: `schemas/source/v1/source.schema.json`.

## Results

| Check | Result |
| --- | --- |
| Draft 2020-12 meta-schema validation | Passed |
| Strict schema compilation | Passed |
| `source-phb.json` complete reference metadata example | Passed |
| `source-code-only-eepc.json` code-only example | Passed |
| `source-mot.json` shared Source code with multiple publication references | Passed |
| `source-psz-aliases.json` distinct code alias and publication ID | Passed |
| `source-aag-unresolved-parent.json` raw unresolved parent token | Passed |
| Unknown workflow property (`status`) | Rejected as expected |
| Invalid calendar date (`2024-02-30`) | Rejected as expected |
| Unsupported publication kind | Rejected as expected |
| Empty optional alias array | Rejected as expected |
| Missing required Source code | Rejected as expected |
| JSON syntax for schema and all examples | Passed |

## Limits

Schema validation does not prove that names or dates match upstream records, resolve parent tokens, or check uniqueness across a full Source dataset. Conversion must add integrity checks for one Source entry per code, registry membership, alias collisions, publication identity-key collisions within each Source, and traceable provenance. The conditional rule that `publicationId` is included when different from `code` also needs an integrity check because standard JSON Schema cannot compare these arbitrary values.
