# Optional Features DTO implementation

## Field coverage matrix

| Source field | Strongly typed DTE | Normalized definition | Public detail | Handling |
|---|---:|---:|---:|---|
| `name` | Yes | Yes | Yes | Required identity text |
| `source`, `page` | Yes | Yes (`SourceReference`) | Yes | Source validated against Source Registry; page retained |
| `featureType` | Yes | Yes | Yes | Original codes retained as a list; multiple memberships supported |
| `prerequisite` | Yes | Yes | Yes | Each source group retained as a list; contained requirements remain combined; class/subclass and numeric level distinctions preserved |
| `entries` | Yes (`EntryDte`) | Import-only | No | Descriptive entries/markup excluded from normalized/public output |
| `consumes` | Yes | Yes | Yes | Resource label and observed integer `amount`, `amountMin`, `amountMax`; no recharge field exists in source |
| `additionalSpells` | Yes | Yes | Yes | Ability, known/prepared selection count and query, innate UIDs, daily/resource keys and spells, ritual UIDs/choices, resource name |
| `skillProficiencies` | Yes | Yes | Yes | Observed deception/persuasion boolean flags; no names resolved |
| `senses` | Yes | Yes | Yes | Numeric blindsight/darkvision/truesight values |
| `featProgression` | Yes | Yes | Yes | Name, category codes, and numeric progression map |
| `optionalfeatureProgression` | Yes | Yes | Yes | Name, feature type codes, and numeric progression map |
| `otherSources` | Yes | Yes (`SourceReference` list) | Yes | Source and page retained; source validated |
| `reprintedAs` | Yes | Yes | Yes | Both string UID and object `{uid, tag}` forms retain their target identity/tag; target source validated |
| `srd`, `srd52` | Yes | No | No | Availability metadata excluded |
| `isClassFeatureVariant` | Yes | No | No | Does not establish selection availability/class |
| `hasFluffImages` | Yes | No | No | Presentation metadata excluded |

Nested variants are typed as follows: prerequisite `spell` values preserve plain spell UIDs and structured `{choose, entry, entrySummary}` values; `level` preserves integer level and optional class/subclass `{name, source, visibleStats}` references; prerequisite `item`, `pact`, `optionalfeature`, and `otherSummary` are kept in their observed forms. Innate spell `_` supports either a spell UID list or an object with `daily`, `resource`, and `ritual`; usage maps retain source keys and UID arrays, while rituals preserve string UIDs or `{choose, count}` options. Known/prepared grants retain `{choose, count}` options. Source DTE deserializers reject unknown fields and invalid union variants.

There are no prerequisite alternatives in this corpus: all 95 prerequisite arrays contain one group. The list model still preserves the source grouping shape. Group fields are not flattened. The source provides no recharge data for `consumes`, no resource formula beyond the observed integer bounds, and no feature type display-name mapping. These are documented source absences, not guessed values.

## Public representation

`OptionalFeatureDto` projects all normalized structured fields listed above except entries and source availability/presentation flags. Nested public records use `@JsonInclude(NON_EMPTY)`, preserving meaningful false and zero values. `OptionalFeatureSummaryDto` remains concise: ID, name, source/page, and feature type codes. Public detail never contains raw markup, descriptive prose, import diagnostics, or inferred class availability.

Routes remain `GET /api/optional-features` (optional name/source/featureType filters), `GET /api/optional-features/{id}`, and `GET /api/optional-features/by-name/{name}?source=`. The static catalog returns the complete 221-record summary list without pagination. Lookup sorting is deterministic.

## Validation

The catalog has 221 roots, 0 expanded definitions, and 221 final definitions. The source includes no `_copy`, `_versions`, `_mod`, or template declarations. All primary, secondary, explicit class/subclass, and suffix-qualified UID source references are registry-validated. All 13 feature type codes and all nine multiple-type records are tested. No authoritative labels for those codes are supplied by this source, so public JSON retains codes without invented names.

Dedicated Optional Features tests: 14 tests, 0 failures, 0 errors. They cover full strict-corpus deserialization, record and identity counts, source registry references, source-qualified IDs, primary/secondary/reprint locators, feature types, grouped prerequisites, spell grants, consumption, proficiency/sense grants, progressions, deterministic sorting/filtering/lookups, DTO serialization, malformed structures, HTTP endpoints, and a JAR-backed classpath resource. The complete Maven suite passes 125 tests with 0 failures and 0 errors (baseline 111 plus these 14 tests).
