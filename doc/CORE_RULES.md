# Core Rules Foundation contracts

**Scope:** Step 2 canonical model and schema proposal for Action, Rule, Skill, Condition, Disease, Status, Sense, Language, and LanguageScript.

**Phase 1:** Completed and approved.

**Phase 2:** Model and Draft 2020-12 schemas are complete and awaiting review. No source records have been converted. Phase 3 requires approval.

## Model structure

Each record is a source-qualified entity with a stable identifier in the form `<type>:<source>:<exact source name>`, for example `skill:PHB:Acrobatics`. The identifier preserves the source code, name spelling, capitalization, and edition-specific record. Records with the same name from different sources remain separate. `reprintedAs` records a supplied relationship; it does not select a preferred edition or authorize merging.

The nine models remain independent, cohesive domain types. The source file `conditionsdiseases.json` contains three arrays but does not make conditions, diseases, and statuses one entity. Likewise, languages and language-script font metadata remain distinct. The design uses composition for shared source flags, citations, rich-text nodes, and explicit model references rather than an inheritance hierarchy.

| Model | Required data | Optional observed data |
| --- | --- | --- |
| Action | `id`, `source`, `name`, `page`, `entries` | source flags, typed `timing`, reprint links, `seeAlsoAction`, `fromVariant`, explicit references |
| Rule | `id`, `source`, `name`, `page`, `entries` | source flags, raw `ruleType` code, separate `type`, reprint links, additional-source citations, explicit references |
| Skill | `id`, `source`, `name`, `page`, `ability`, `entries` | source flags, reprint links, explicit references |
| Condition | `id`, `source`, `name`, `page`, `entries` | source flags, other-source citations, reprint links, `hasFluffImages`, explicit references |
| Disease | `id`, `source`, `name`, `page`, `entries` | source flags, supplied disease `type`, reprint links, explicit references |
| Status | `id`, `source`, `name`, `page`, `entries` | source flags, reprint links, explicit references |
| Sense | `id`, `source`, `name`, `page`, `entries` | source flags, reprint links, explicit references |
| Language | `id`, `source`, `name`, `page` | source flags, reprint links, speaker descriptions, language category, script name, origin, entries, image flag, font paths, source citations, dialect names, explicit references |
| LanguageScript | `id`, `source`, `name`, `fonts` | explicit references |

`sourceFlags` is a composed object containing only supplied `srd`, `srd52`, `basicRules`, and `basicRules2024` booleans. It may contain any subset and must be omitted when there are no flags. False is meaningful and must be preserved.

## Rich-text entries and references

`entries` is a recursive typed array. It accepts strings and observed object nodes: `entries`, `list`, `table`, `inset`, `section`, `item`, `inline`, `link`, `image`, and `quote`. Tables preserve string, number, or rich-text cells, optional footnotes, and the observed generated-table inclusion metadata. `item` requires exactly one of a singular `entry` string or nested `entries` array. Internal link/image paths are retained as navigation metadata, not resolved domain references.

Inline 5eTools tags remain unchanged inside source-authored text. A separate optional `references` array can carry explicitly identified entity references as `targetType`, exact `name`, optional source code, optional stable `id`, and optional raw tag token. An `id` requires a source code. No target is inferred from a similar name; absence of source or ID does not assert resolution. Non-entity tags such as dice, DC, filter, and 5eTools page navigation remain text/navigation, not entity references.

`reprintedAs` preserves the source's supplied representation: string UIDs such as `Attack|XPHB`, and the observed object form with `uid` and `tag` (used for reprint targets typed as hazards). This avoids guessing that every reprint target has the same domain type. Later conversion may add typed `references` when the target type and identity are established, while preserving the reprint association.

`additionalSources` and `otherSources` remain arrays of `{source, page}` citations. They reference Source Registry codes and publication locations; they are not entity relationships.

## Typed action timing

The source `time` array maps to optional `timing`. Each value is one of:

- `{ "kind": "fixed", "number": 1, "unit": "action" }` for an explicit numeric action, reaction, or bonus unit.
- `{ "kind": "free" }` for source text `Free`.
- `{ "kind": "varies" }` for source text `Varies`.

When source timing is absent, omit `timing`; do not substitute a default. This preserves the three observed forms and the missing `Mark` timing. This model does not encode all prose about when an action can be used.

## Rule classification and edition identity

`ruleType` is restricted to the source codes `C`, `O`, `V`, and `VO`, retained without expanding their meanings. It is optional because 40 of 231 source rules omit it. The independent source `type` field accepts `section` or `entries`; it is never inferred from `ruleType`.

The `Rule` type is not synonymous with optional or variant rules. The four source codes do not establish activation, a universal rules toggle, or complete semantics. Source flags and edition codes remain source facts. No latest-edition selection is encoded.

## Descriptive content and deterministic mechanics

The contracts keep descriptive `entries` separate from typed facts already available in source data, such as action timing and a skill's ability association. They do not introduce a universal mechanics engine or encode extracted condition effects yet. The analysis found recurring effects but not enough approved, representative interpretations to define a shared executable effect contract without premature assumptions. Future semantic conversion must use approved typed mechanic structures where supported and retain explanatory entries and exceptions. Ambiguous, deferred, and unsupported interpretations belong in project tracking, not in canonical entity fields.

## Source mappings

| Source JSON | Canonical property or treatment |
| --- | --- |
| `name`, `source`, `page` | Preserve name and exact Source code. Build `id` deterministically as `<type>:<source>:<exact name>`; preserve page as an integer. `LanguageScript` has no source page. |
| `srd`, `srd52`, `basicRules`, `basicRules2024` | Group present booleans under `sourceFlags` without changing values; omit the object if none are present. |
| `entries` | Preserve text and recursively map supported typed nodes under the same property. Inline tags remain literal. |
| `action.time` | Map each source value to a typed `timing` alternative; absent `time` means absent `timing`. |
| `skill.ability` | Preserve the supplied lowercase ability code (`str`, `dex`, `con`, `int`, `wis`, `cha`). |
| `variantrule.ruleType` and `variantrule.type` | Preserve separately as `ruleType` and `type`; keep their source values. |
| `reprintedAs` | Preserve string UID values or the observed `{uid, tag}` object; do not merge or choose a target edition. |
| `seeAlsoAction`, `fromVariant` | Preserve supplied values in their own fields; do not invent missing source qualifiers. |
| `additionalSources`, `otherSources` | Preserve each field as its own array of typed Source-code/page citations. |
| `condition.hasFluffImages`, `language.hasFluffImages` | Preserve the supplied boolean. |
| `language.type`, `script`, `origin`, `typicalSpeakers`, `dialects`, `fonts` | Preserve as typed categories or strings/arrays. Keep script a string; do not bind it to the incomplete font catalog. |
| `languageScript.fonts` | Preserve supplied asset paths on the separate LanguageScript entity. |
| Explicit entity tags in content | Preserve literal tag in `entries`; optionally add a `references` value when its entity type/name/source are parsed from explicit evidence. A later-domain target need not exist yet. |
| Provenance and interpretation/review tracking | Keep in an external manifest, discovery document, or report. Never put workflow state in canonical JSON. |

All other source properties observed in the six datasets are represented by these mappings. The schemas reject additional properties so an upstream field change prompts analysis rather than silent loss.

## Schemas and examples

All contracts use JSON Schema Draft 2020-12 and version `v1`:

- [Shared definitions](../schemas/core-rules/v1/common.schema.json)
- [Action](../schemas/core-rules/v1/action.schema.json)
- [Rule](../schemas/core-rules/v1/rule.schema.json)
- [Skill](../schemas/core-rules/v1/skill.schema.json)
- [Condition](../schemas/core-rules/v1/condition.schema.json)
- [Disease](../schemas/core-rules/v1/disease.schema.json)
- [Status](../schemas/core-rules/v1/status.schema.json)
- [Sense](../schemas/core-rules/v1/sense.schema.json)
- [Language](../schemas/core-rules/v1/language.schema.json)
- [LanguageScript](../schemas/core-rules/v1/language-script.schema.json)
- [Rich-text helper schema](../schemas/core-rules/v1/rich-text.schema.json)

The example JSON files demonstrate each model, all action timing alternatives (including absent timing), and the observed rich-text node types. They are contract examples, not a normalized batch or a generated dataset. Validation findings are in the [Phase 2 schema validation report](../reports/step-2/CORE_RULES_FOUNDATION_PHASE_2_SCHEMA_VALIDATION.md).

## Provenance, limitations, and open review points

Use the approved Source Registry through the exact `source` code. Keep source file path, source revision/hash, raw entry locator, schema version, and interpretation review history in an external provenance manifest. A structural schema pass does not establish mechanics correctness or cross-document target validity.

The current contract preserves 594 source records' observed field and rich-text variations, but no canonical records have been generated. The schema does not force all explicit inline tags into `references`; reference extraction and target integrity require later conversion checks. `Language.script` values are not links to LanguageScript because the catalog is incomplete and source contexts differ. Sense ranges remain dependent on creature records. Disease progression and complex variant rules remain prose unless and until their mechanics can be interpreted reliably. Existing incomplete-reference tracking under `/doc` remains applicable.

Review these decisions before Phase 3: confirm the `id` component encoding (especially punctuation in future names/source codes); whether raw string/object `reprintedAs` remains the canonical representation or becomes a typed relation plus provenance; the policy for populating optional `references` from inline tags; and whether representative semantic cases justify any narrow reusable effect model or schema refinement. No full data expansion or backend integration is authorized by these proposals.
