# Runtime and reproducibility artifacts

These assets are retained at their established paths. This index is not an instruction to move or
regenerate them. The [dependency inventory](../../docs/development/DOCUMENTATION-CONSOLIDATION.md)
records exact file consumers and conservative dynamic-directory dependencies.

| Asset | Location and role |
| --- | --- |
| Pinned source dataset | [data resources](../../src/main/resources/data/) — 175 files; unchanged |
| P1D packaged runtime inputs | [publication resources](../../src/main/resources/publication/spell-formulas/v1/) — startup input, zero authorizations |
| Approval candidates and journal | [P1C manifest](../mechanics/public-v1/spells/p1c/candidate-manifest.json), [decision journal](../mechanics/public-v1/spells/p1c/decision-journal.json) — pending/empty, unchanged |
| Approval schemas | [candidate schema](../mechanics/public-v1/spells/p1c/candidate-manifest.schema.json), [journal schema](../mechanics/public-v1/spells/p1c/decision-journal.schema.json), [attribution schema](../mechanics/public-v1/spells/p1c/attribution-proposal.schema.json) |
| Licensed-source crosswalk | [P1B evidence](../mechanics/public-v1/spells/p1b/occurrence-crosswalk.json) and [official-document metadata](../mechanics/public-v1/spells/p1b/official-documents.json) |
| Runtime exporter | [P1D exporter](../mechanics/public-v1/spells/p1d/export-runtime.mjs) — use `--check` to avoid writes |
| Mechanics corpus evidence | [corpus evidence](../mechanics/corpus-evidence.json), [EntryDte evidence](../mechanics/entry-assessment/entry-evidence.json) |
| Object gold set | [gold labels](../mechanics/batch-10-object-relationships/gold-set.json), [test-generated verification](../mechanics/batch-10a-object-membership/gold-verification.json) |
| Spell publication assessment gold set | [labels](../mechanics/public-v1/spells/gold-set.json) — existing tests read these paths |
| Item charge review cases | [review cases](../mechanics/batch-11-item-charges/review-cases.json) — retained assessment evidence |
| Hash verification | [source-hash verifier](../mechanics/batch-11b-item-charge-syntax/verify-source-hashes.mjs), [prior baseline](../mechanics/batch-10a-object-membership/pre-implementation-hashes.json) |
| Packaged inspection check | [7.2.3 HTTP script](../mechanics/step-7/7.2.3/verify-packaged.ps1) — localhost process; scratch outputs in `target/` |

Normalized field inventories under individual catalog folders are test inputs in several pipelines.
Do not remove them as “generated documentation.” Java tests also read analysis gold/evidence files.
No production Maven resource location was changed, and no analysis fixture was moved into a JAR.

Some historical verification scripts assert the entire workspace as it existed for their batch.
They require the matching historical revision or an explicitly reconstructed snapshot. Their old
baseline is not a reason to mutate current mechanics or rewrite a pending decision.
