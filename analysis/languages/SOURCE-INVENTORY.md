# Language source inventory

Source: `src/main/resources/data/languages.json` (62,764 bytes)

The root is an object containing `language` (201 definition objects) and
`languageScript` (6 script/font objects). The source is one consolidated 5etools
file, not a per-book directory. Definitions cover 23 source codes. The source
has 201 unique case-insensitive `(name, source)` identities and no copies or
version-generation instructions.

## Field inventory

| Collection | Field | Type | Present | Meaning / destination |
|---|---|---:|---:|---|
| language | name | string | 201 | Definition name; public |
| language | source | string | 201 | Edition/source code; public |
| language | page | integer | 201 | Source locator; internal provenance |
| language | reprintedAs | string[] | 46 | `Name\|Source` references; retain internally |
| language | typicalSpeakers | string[] | 159 | Descriptive/tagged speaker text; internal pending publication decision |
| language | type | string | 105 | Observed `standard`, `exotic`, `rare`, `secret`; source classification |
| language | script | string | 99 | Script name; reference by name only, no inferred mechanics |
| language | srd | boolean | 18 | Legacy SRD flag; source metadata |
| language | basicRules | boolean | 18 | Legacy Basic Rules flag; source metadata |
| language | srd52 | boolean | 19 | 2024 SRD flag; source metadata |
| language | basicRules2024 | boolean | 19 | 2024 Basic Rules flag; source metadata |
| language | origin | string | 36 | Descriptive origin; internal pending publication decision |
| language | entries | (string \| inset object)[] | 40 | Narrative/option prose; internal only |
| language | hasFluffImages | boolean | 26 | Presentation flag; internal only |
| language | fonts | string[] | 1 | Internal asset paths; excluded from public API |
| language | additionalSources | `{source,page}`[] | 10 | Source locator list; internal provenance |
| language | dialects | string[] | 3 | Source-defined dialect names; public definition labels, not resolved references |
| language | otherSources | `{source,page}`[] | 2 | Additional source locators; internal provenance |
| languageScript | name | string | 6 | Script definition name |
| languageScript | source | string | 6 | Source edition |
| languageScript | fonts | string[] | 6 | Internal asset paths; never public |

All observed nested values are strings, integer page numbers, booleans, arrays,
or objects of the listed shapes. No explicit nulls were found. Optional fields
are absent rather than null in this corpus. There are 21 object entries, all
`type: inset`, each with a name and string children; the remaining entries are
strings. The reusable entry DTE hierarchy is compatible with these shapes.

## Coverage and edge cases

- The 201 names include same-name entries across sources (for example Abyssal
  in GGR, PHB, and XPHB); `(name, source)` is unique in this corpus.
- Legacy and 2024 classifications coexist and are not interchangeable. `type`
  has different values (`standard`/`exotic` vs `rare`/`secret`) across editions.
- `typicalSpeakers` can contain 5etools inline creature/race/filter tags.
- `reprintedAs` elements use a pipe-delimited source reference.
- Source prose in `entries`, `typicalSpeakers`, and `origin` may have publication
  restrictions. It is retained internally but excluded from API projection.
- `languageScript.fonts` and `language.fonts` contain third-party asset paths.
- No copy inheritance, generated variants, or explicit references to another
  language definition were found. `script`, `dialects`, and `reprintedAs` are
  names/references, but the source does not provide a foreign-key contract.

## Representative forms

- Minimal record: `name`, `source`, `page`.
- Legacy classification: `type`, optional `script`, speakers, and SRD/basic-rule flags.
- 2024 classification: `type`, `origin`, and 2024 SRD/basic-rule flags.
- Rich record: inset `entries`, reprint references, extra source locations,
  dialect labels, and an image availability flag.
- Script record: name/source plus one or more font paths.

## Cross-entity reference inventory

Speaker strings contain presentation tags referencing creatures or races.
These are not normalized into foreign keys because they may be filter queries,
include aliases, and lack a consistent source-aware identifier. A language
definition can later be referenced as `(name, source)` by races, backgrounds,
monsters, and other entities. Reprint links remain source strings pending a
cross-definition identity policy.
