# Optional Features source inventory

Source: `src/main/resources/data/optionalfeatures.json`; one root collection, `optionalfeature`, with 221 records. All 221 have name, source, page, featureType, and entries. There are 221 unique `(name, source)` identities.

Primary source counts: PHB 82, XPHB 58, TCE 47, XGE 22, AU 8, EFA 4. The field inventory records every observed nested path and JSON value type in `normalized-field-inventory.json`. Referenced source occurrences are XPHB 69, PHB 8, AU 8, ERLW 11, EFA 7, XGE 2, from both `otherSources` and `reprintedAs`. All these codes are present in the current Source Registry.

Feature type codes and occurrence counts: EI 82, MV:B 43, AI 16, AS 16, ED 17, MM 20, FS:F 11, FS:R 7, FS:P 7, FS:B 2, RP 4, RN 6, PB 4. Features may have multiple codes in the source array. The source supplies codes but no authoritative code-to-display-name dictionary in this file.

Prerequisites occur on 95 records. Observed keys are level (71), item (15), pact (15), optionalfeature (6), spell (10), and otherSummary (4). All 95 outer arrays contain exactly one group, so no alternatives are represented in this corpus. Within a group, fields are combined: observed combinations include level+spell (5), item+level (7), level+pact (8), and level+optionalfeature (6). `level` carries a numeric value plus class identity, and 11 also carry subclass identity. It remains a source level requirement; no character-level interpretation is assumed.

Structured fields include `consumes` (79), `additionalSpells` (54), skillProficiencies (1), senses (3), featProgression (1), and optionalfeatureProgression (1). `consumes` carries a resource name and optional numeric amount/min/max (no recharge field is present). Additional spells include ability, known/prepared choice declarations, innate spell UID lists, daily/resource maps keyed by source level/count codes, ritual spell strings or ritual choices, and resource names. The source does not encode spell catalog IDs and none are inferred. Proficiencies are boolean deception/persuasion grants; senses are numeric blindsight/darkvision/truesight grants. Progression values are numeric maps keyed by source-defined level keys. Entries are descriptive source markup and remain import-only. Flags include srd, srd52, isClassFeatureVariant, and hasFluffImages.

Nine records carry multiple feature type codes. No code-to-label dictionary is defined in this file or in the analyzed pipeline; all 13 codes remain unchanged. The source does not specify class availability from feature types.

Secondary sources occur on 17 records (21 occurrences). Reprint links occur on 84 records. The corpus includes no `_copy`, `_versions`, or `_mod` fields and no template declarations. `isClassFeatureVariant` occurs on 25 records; that flag does not itself establish class availability.
