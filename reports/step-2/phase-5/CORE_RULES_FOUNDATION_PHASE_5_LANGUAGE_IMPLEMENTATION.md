# Core Rules Foundation Phase 5 Language Contract Implementation

## Status

LANG-1, LANG-3, and LANG-4 are implemented and validated for human review. Step 2 Phase 5 remains open and unapproved. Rule expansion, ACT-5/ACT-6, Phase 6, and Step 3 have not started.

## Implemented contract changes

Only `src/main/resources/schemas/language/schema.json` changed. The shared schema and all non-Language schemas remain unchanged. The Language schema ID remains v1; changes are additive to existing Language identities and fields.

### LANG-1 — Exact-tag speaker selectors

Creature filters now allow either the existing `creatureTypes` form or a typed `selector` form. The selector currently supports only `kind: TAG`, a non-empty exact `value`, and an optional non-empty exact `source`. Schema constraints prevent combining the tag selector with `creatureTypes`, the existing group/value `tags`, or outer `sources`, avoiding an accidental intersection or a second conflicting source scope. The `typicalSpeakers` description defines array members as alternatives.

Existing explicit creature references and creature-type filters continue to validate. No tag was converted to a creature type.

Ten exact tag expressions were migrated across nine Language records:

| Canonical identity | Source selector(s) preserved |
| --- | --- |
| `language:ggr:abyssal` | `demon` and `devil`, each scoped to GGR as separate alternatives |
| `language:phb:abyssal` | `demon`, with source scope absent |
| `language:ggr:celestial` | `angel`, scoped to GGR |
| `language:ggr:goblin` | Exact literal `any race`, scoped to GGR |
| `language:phb:goblin` | Exact literal `goblinoid`, with source scope absent |
| `language:phb:infernal` | `devil`, with source scope absent |
| `language:ggr:loxodon` | Exact literal `any race`, scoped to GGR |
| `language:ggr:merfolk` | Exact literal `any race;merfolk`, scoped to GGR; not split into group/value |
| `language:phb:orc` | `orc`, with source scope absent |

`language:psi:demonic` remains without canonical speaker selectors. Its two source selectors explicitly contain `source=` with an empty value. That state is distinct from absent source scope and a valid non-empty source. The canonical schema does not accept empty source identities; the complete source criteria remain documented as `SCHEMA_UNSUPPORTED`. No source identity was invented. `language:erlw:quori` and `language:mot:sylvan` search selectors were not migrated; their indexed fields and matching semantics remain unresolved under LANG-2.

### LANG-3 — Concealed-message capabilities

The schema adds a bounded `communicationCapabilities` array with a `CONCEALED_MESSAGE` discriminator and finite communication channels. It captures language-knowledge comprehension, Druidic recognition/detection and deciphering restrictions, and the PHB Thieves' Cant communication duration multiplier. `communicationModes` records the source-supported modes without claiming a general communication execution model.

- `language:phb:druidic` records `SPOKEN` and `LEFT_MESSAGE` modes. Its hidden-message profile makes recognition automatic for a language knower. Another observer can use a DC 15 Wisdom (Perception) check to detect presence only; deciphering by a non-knower requires magic. No Spell identity is invented.
- `language:phb:thieves-cant` records `ORDINARY_CONVERSATION`, comprehension requiring Thieves' Cant knowledge, and a communication duration multiplier of 4.
- `language:xphb:thieves-cant` is unchanged and has no PHB concealed-message behavior.

The schema fixes the Druidic check vocabulary/value and the Thieves' Cant multiplier for these source-defined structures. Rule remains responsible for D20 resolution; Language carries the declared check and outcome distinction.

### LANG-4 — Spoken mutual intelligibility

`intelligibility` now supports a typed relationship with `kind: MUTUAL_INTELLIGIBILITY`, `mode: SPOKEN`, `directionality: MUTUAL`, and an exact Language reference. `language:idrotf:loross` migrated its existing generic reference to `language:phb:elvish` into this structure. It has no duplicate generic edge. No written intelligibility was inferred from Loross using the Draconic alphabet.

Loross written translation remains omitted as an `EXTERNAL_DEPENDENCY`: the source names Cloistered Scholar and Sage backgrounds and the Eyes of the Rune Keeper Warlock invocation, but their approved canonical identities belong to future Character Building/Class work. The full source entry remains in the restricted omission register. This portion is not classified as a schema gap.

## Canonical records migrated

Twelve canonical records changed:

- Nine Phase 5 records received exact tag selectors: GGR/PHB Abyssal, GGR Celestial, GGR/PHB Goblin, PHB Infernal, GGR Loxodon, GGR Merfolk, and PHB Orc.
- Phase 5 Druidic received its concealed-message mechanics.
- Phase 5 Loross received its typed spoken-intelligibility relationship.
- Phase 3 PHB Thieves' Cant received its approved concealed-message mechanics.

PSI Demonic was reviewed but intentionally not migrated because the explicit empty source parameter cannot be represented faithfully. No source data, Action/Rule records, or unrelated domain records were modified by this Language implementation.

## Source accounting and coverage

The complete 201-record Language catalog remains represented by 201 canonical identities. Current classifications are:

| Coverage | Before refinement | After refinement |
| --- | ---: | ---: |
| Fully represented | 66 | **72** |
| Partially represented | 135 | **129** |
| Unsupported | 0 | **0** |

Within the 197-record Phase 5 Language batch, coverage moved from 65 full / 132 partial to **71 full / 126 partial**. Six records became full after their only unresolved field was represented: GGR and PHB Abyssal, GGR Celestial, PHB Infernal, GGR Loxodon, and GGR Merfolk. Other tag-affected records remain partial due to independent limitations such as entry content, presentation metadata, or PSI's empty source parameter. Druidic, Thieves' Cant, and Loross retain other partial-record dependencies.

The Phase 5 manifest now totals 325 canonical records: **179 full, 146 partial, and 3 unsupported**. Its entry classifications are **204 interpreted, 24 duplicate, and 190 omitted** across 276 roots. Its structured-field issue count is 154, including 148 Language issues (down from 157): 76 external non-creature speaker targets, three remaining unsupported/ambiguous selectors, six unresolved creature references, 35 unresolved origins, two unresolved dialect lists, and 26 excluded presentation flags. The remaining selector issues are PSI Demonic's explicit empty source and the Quori/Sylvan search selectors. Quori/Sylvan causes are `SOURCE_AMBIGUOUS`; PSI Demonic remains `SCHEMA_UNSUPPORTED`.

The PHB Druidic Phase 5 entry is now wholly `INTERPRETED`; it is removed from `doc/language/omitted.md`. The PHB Thieves' Cant Phase 3 source entry is now wholly `INTERPRETED`; its source-field speaker issue remains unresolved, and the entry is removed from the omission register. The Loross entry retains an omitted written-translation portion with cause `EXTERNAL_DEPENDENCY`. The Phase 3 disposition totals are now 62 interpreted / 11 duplicate / 33 omitted.

During validation, the Language registers did not contain complete source entries for a number of pre-existing manifest omissions/duplicates, and the Loross heading contained the wrong original source entry. To meet the permanent complete-entry assertion, the Language registers were reconciled from the manifest and immutable source: 38 omitted-entry sections and two duplicate-entry sections were restored, and the Loross section now contains the complete Loross source entry. The Druidic and Thieves' Cant omission sections were removed after their entries became fully interpreted. This did not change the remaining classification reasons or mechanics.

## Examples and permanent regression assertions

Four fixtures were added:

- `language-tag-speakers.json` demonstrates multiple source-tag alternatives.
- `language-druidic-messages.json` demonstrates Druidic recognition, detection, and deciphering.
- `language-thieves-cant-messages.json` demonstrates its separate channel and four-times duration.
- `language-loross-intelligibility.json` demonstrates spoken mutual intelligibility.

Permanent validation now checks exact tag values and scopes, absent scopes, alternative selectors, the literal Merfolk criterion, PSI empty-scope accounting, and rejection of empty/unknown selectors, search, inferred creature-type intersections, and missing tag values. It checks both PHB concealed-message profiles, including Druidic presence-only detection and the PHB Cant duration, and detects unsupported spell identities, unconditional comprehension, Druidic mechanics copied to Cant, and PHB Cant behavior assigned to XPHB. Loross assertions require the exact PHB Elvish Language reference, spoken mode, mutual direction, no generic duplicate edge, and no non-Language target.

## Validation results

`npm run validate:core-rules` passed:

- 9 Draft 2020-12 schemas compiled.
- 58 examples validated.
- 34 Phase 3/4 canonical records validated.
- 325 Phase 5 canonical records validated.
- 507 permanent checks passed.
- 14 Rule components and 27 Action records passed their integrity checks.

Structural validity and permanent assertions cover the changed shapes and enumerated source facts; they do not execute D&D communication or prove all 201 Language interpretations mechanically correct. `git diff --check` passed. Git emitted line-ending warnings for the repository's existing LF/CRLF configuration; there were no whitespace errors.

## Remaining limitations and deferred work

- LANG-2: Quori and Sylvan `search` matching semantics and exact result sets are unresolved.
- PSI Demonic's two explicitly empty source parameters remain unrepresentable without a distinct source-syntax state; they are omitted rather than normalized to absent scope.
- Seventy-six non-creature speaker fields, unresolved origins, and dialect identities still depend on exact references from other domains.
- LANG-5 Loross written translation awaits exact background/class-feature identities.
- LANG-6 Qualith remains partial; inscription creation/state, tactile interpretation, complexity-based checks, failure effects, degradation, and Spell alternative require other domain contracts and source-defined thresholds.
- Qualitative language limitations such as “depth” or “sophisticated conversation” remain non-executable without source-defined operational measures.
- LanguageScript remains excluded.

Phase 5 is still in progress and unapproved. No Rule expansion, ACT-5/ACT-6, Phase 6 backend integration, or Step 3 work has begun.
